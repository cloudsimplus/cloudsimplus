package org.cloudsimplus.heuristics;

import java.util.stream.IntStream;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

/**
 * <p>Provides the methods to be used for implementation of heuristics
 * to find solution for complex problems where the solution space
 * to search is large. These problems are usually NP-Hard ones
 * which the time to find a solution increases,
 * for instance, in exponential time. Such problems can be, for instance,
 * mapping a set of VMs to existing Hosts or mapping a set of Cloudlets
 * to VMs.
 *
 * A heuristic implementation thus provides an approximation of
 * an optimal solution (a suboptimal solution).
 * </p>
 *
 * <p>Different heuristic can be implemented, such as
 * <a href="https://en.wikipedia.org/wiki/Tabu_search">Tabu search</a>,
 * <a href="https://en.wikipedia.org/wiki/Simulated_annealing">Simulated annealing</a>,
 * <a href="https://en.wikipedia.org/wiki/Hill_climbing">Hill climbing</a> or
 * <a href="https://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms">Ant colony optimization</a>,
 * to name a few.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @param <T> the class of solutions the heuristic will deal with
 */
public interface Heuristic<T extends HeuristicSolution> {
    /**
     * Computes the acceptance probability to define if a neighbor solution
     * has to be accepted or not, compared to the {@link #getBestSolutionSoFar()}.
     *
     * @return the acceptance probability, in scale from [0 to 1] where
     * 0 is to maintain the {@link #getBestSolutionSoFar() current solution},
     * 1 is to accept the neighbor solution, while intermediate
     * values defines the probability that the neighbor solution
     * will be randomly accepted.
     */
    public double getAcceptanceProbability();

	/**
	 * Gets a random number between 0 (inclusive) and maxValue (exclusive).
	 *
	 * @param maxValue the max value to get a random number (exclusive)
	 * @return the random number
	 */
	public int getRandomValue(int maxValue);

    /**
     * Checks if the solution search can be stopped.
     *
     * @return true if the solution search can be stopped, false otherwise.
     */
    public boolean isToStopSearch();

    /**
     * Gets the initial solution that the heuristic will start from
     * in order to try to improve it. If not initial solution was
     * generated yet, one should be randomly generated.
     * @return the initial randomly generated solution
     */
    public T getInitialSolution();

    /**
     *
     * @return latest neighbor solution created
     * @see HeuristicSolution#createNeighbor()
     */
    public T getNeighborSolution();

    /**
     *
     * @return best solution that has found up to now
     */
    public T getBestSolutionSoFar();

    /**
     *
     * @return the number of times a neighbor solution will be searched
     * at each iteration of the {@link #solve() solution find}.
     */
    public int getNumberOfNeighborhoodSearchesByIteration();

    /**
     * Sets the number of times a neighbor solution will be searched
     * at each iteration of the {@link #solve() solution find}.
     *
     * @param numberOfNeighborhoodSearches number of neighbor searches to perform
     * at each iteration
     */
    public void setNumberOfNeighborhoodSearchesByIteration(int numberOfNeighborhoodSearches);

	/**
	 * Starts the heuristic to find a suboptimal solution.
	 * After the method finishes, call the {@link #getBestSolutionSoFar()}
	 * to get the final solution.
	 *
	 * @return the final solution
	 * @see #getBestSolutionSoFar()
	 * @todo @author manoelcampos Try to parallelize the solution finding in order
	 * to reduce search time. Maybe a Map-Reduce approach can be useful.
	 */
	public T solve();

	/**
	 *
	 * @return the time taken to finish the solution search (in seconds).
	 * @see #solve()
	 */
	public double getSolveTime();

    /**
     * A property that implements the Null Object Design Pattern for {@link Heuristic}
     * objects.
     */
    public static final Heuristic NULL = new HeuristicNull();
}

/**
 * A class to allow the implementation of Null Object Design Pattern
 * for this interface and extensions of it.
 */
class HeuristicNull<T extends HeuristicSolution> implements Heuristic<T> {
    @Override public double getAcceptanceProbability() { return 0.0; }
	@Override  public int getRandomValue(int maxValue) { return 0; }
	@Override public boolean isToStopSearch() { return false; }
    @Override public T getInitialSolution() { return (T)HeuristicSolution.NULL; }
    @Override public T getNeighborSolution() { return (T)HeuristicSolution.NULL; }
	@Override  public T solve() { return (T)HeuristicSolution.NULL; }
	@Override public T getBestSolutionSoFar() { return (T)HeuristicSolution.NULL; }
	@Override public int getNumberOfNeighborhoodSearchesByIteration() { return 0; }
	@Override public void setNumberOfNeighborhoodSearchesByIteration(int numberOfNeighborhoodSearches) {}
	@Override public double getSolveTime() { return 0; }
}
