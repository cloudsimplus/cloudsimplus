package org.cloudsimplus.heuristics;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.IntStream;

/**
 * A base class for {@link Heuristic} implementations.
 *
 * @author Manoel Campos da Silva Filho
 * @param <S> the class of solutions the heuristic will deal with, starting with a initial
 *           solution (usually random, depending on sub-classes implementations)
 *           and executing the solution search in order
 *           to achieve a satisfying solution (defined by a stop criteria)
 */
public abstract class HeuristicAbstract<S extends HeuristicSolution<?>>  implements Heuristic<S> {
	/**
	 * Reference to the generic class that will be used to instantiate objects.
	 */
	protected final Class<S> solutionClass;

	private final ContinuousDistribution random;
	/**
	 * @see #getNumberOfNeighborhoodSearchesByIteration()
	 */
	protected int numberOfNeighborhoodSearchesByIteration;
	/**
	 * @see #getBestSolutionSoFar()
	 */
	protected S bestSolutionSoFar;
	/**
	 * @see #getNeighborSolution()
	 */
	protected S neighborSolution;

	/**
	 * @see #getSolveTime()
	 */
	private double solveTime;

	/**
	 * Creates a heuristic.
	 *
	 * @param random a random number generator
	 * @param solutionClass reference to the generic class that will be used to instantiate heuristic solutions
	 */
	public HeuristicAbstract(ContinuousDistribution random, Class<S> solutionClass){
		this.solutionClass = solutionClass;
		this.random = random;
		this.numberOfNeighborhoodSearchesByIteration = 1;
		setBestSolutionSoFar(newSolutionInstance());
		setNeighborSolution(bestSolutionSoFar);
	}

	@Override
	public double getSolveTime() {
		return solveTime;
	}

	/**
	 * Sets the time taken to solve the heuristic.
	 * @param solveTime the time to set (in seconds)
	 */
	protected void setSolveTime(double solveTime) {
		this.solveTime = solveTime;
	}

	/**
	 *
	 * @return a random number generator
	 */
	protected ContinuousDistribution getRandom(){
		return random;
	}

	private S newSolutionInstance() throws RuntimeException {
	    try {
	        Constructor<S> c = solutionClass.getConstructor(new Class[]{Heuristic.class});
	        return c.newInstance(this);
	    } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException ex) {
	        throw new RuntimeException(ex);
	    }
	}

	/**
	 * Updates the state of the system in order to keep looking
	 * for a suboptimal solution.
	 */
	protected abstract void updateSystemState();

	@Override
	public int getRandomValue(int maxValue){
		final double uniform = getRandom().sample();

        /*always get an index between [0 and size[,
        regardless if the random number generator returns
        values between [0 and 1[ or >= 1*/
		return (int)(uniform >= 1 ? uniform % maxValue : uniform * maxValue);
	}

	@Override
	public S solve() {
		long startTime = System.currentTimeMillis();
		setBestSolutionSoFar(getInitialSolution());
		while (!isToStopSearch()) {
			IntStream.range(0, getNumberOfNeighborhoodSearchesByIteration()).forEach(i -> {
				setNeighborSolution(createNeighbor(getBestSolutionSoFar()));
				if (getAcceptanceProbability() > getRandomValue(1)) {
					setBestSolutionSoFar(getNeighborSolution());
				}
			});

			updateSystemState();
		}
		setSolveTime((System.currentTimeMillis() - startTime)/1000.0);

		return getBestSolutionSoFar();
	}

	@Override
	public S getBestSolutionSoFar() {
	    return bestSolutionSoFar;
	}

	@Override
	public S getNeighborSolution() {
	    return neighborSolution;
	}

	/**
	 * Sets a solution as the current one.
	 * @param solution the solution to set as the current one.
	 */
	protected final void setBestSolutionSoFar(S solution) {
        this.bestSolutionSoFar = solution;
    }

	/**
	 * Sets a solution as the neighbor one.
	 * @param neighborSolution the solution to set as the neighbor one.
	 */
    protected final void setNeighborSolution(S neighborSolution) {
        this.neighborSolution = neighborSolution;
    }

	public int getNumberOfNeighborhoodSearchesByIteration() {
        return numberOfNeighborhoodSearchesByIteration;
    }

	public void setNumberOfNeighborhoodSearchesByIteration(int numberOfNeighborhoodSearches) {
        this.numberOfNeighborhoodSearchesByIteration = numberOfNeighborhoodSearches;
    }
}
