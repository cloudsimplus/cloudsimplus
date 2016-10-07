package org.cloudsimplus.heuristics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

/**
 * <p>A base class for implementation of
 * <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated Annealing</a>
 * algorithms used to find a suboptimal solution for a problem defined by sub-classes of this one.</p>
 *
 * The Simulated Annealing is a heuristic that starts with a random solution
 * and iteratively generates a random neighbor solution that its fitness
 * is assessed in order to reach a sub-optimal result.
 * The algorithm try to avoid local maximums, randomly selecting
 * worse solutions to get away from being stuck in these locals.
 *
 * The algorithm basically works as follows:
 * <ol>
 *  <li>Starts generating a random solution as you wish;</li>
 *  <li>Computes its fitness using some function defined by the developer implementing the heuristic;</li>
 *  <li>Generates a neighbor random solution from the current solution and compute its fitness;</li>
 *  <li>Assess the neighbor and the current solution:
 *      <ul>
 *          <li>{@code if neighbor.getFitness() > current.getFitness()} then move the the new solution;</li>
 *          <li>{@code if neighbor.getFitness() < current.getFitness()} then randomly decide if move to the new solution;</li>
 *      </ul>
 *  </li>
 *  <li>Repeat steps 3 to 4 until an aceptable solution is found or some number
 * of iterations or time is reached. These conditions are defined by the developer
 * implementing the heuristic.</li>
 * </ol>
 *
 * @param <T> the class of solutions the heuristic will deal with, starting with a random solution
 *           and execute the solution search in order to achieve a satisfying solution (defined by a stop criteria)
 * @author Manoel Campos da Silva Filho
 * @see <a href="http://dx.doi.org/10.1109/101.17235">[1] R. A. Rutenbar,
 * “Simulated Annealing Algorithms: An overview,” IEEE Circuits Devices Mag., vol. 1, no. 5,
 * pp. 19–26, 1989.</a>
 */
public abstract class SimulatedAnnealing<T extends HeuristicSolution> extends HeuristicAbstract<T> {
    /**
     * @see #getColdTemperature()
     */
    private double coldTemperature;

    /**
     * @see #getCurrentTemperature()
     */
    private double currentTemperature;

    /**
     * @see #getCoolingRate()
     */
    private double coolingRate;

	/**
     * Instantiates a simulated annealing heuristic.
     *
	 * @param random a pseudo random number generator
     * @param solutionClass reference to the generic class that will be used to instantiate heuristic solutions
     */
    public SimulatedAnnealing(ContinuousDistribution random, Class<T> solutionClass){
        super(random, solutionClass);
    }

	/**
     * {@inheritDoc}
     * <p>It is used the Boltzmann distribution to define the probability
     * of a worse solution (considering its cost)
     * to be accepted or not in order to avoid local minima.
     * The Boltzmann factor computed also ensures that better solutions are always accepted.
     *
     * The Boltzmann Constant has different values depending of the used unit.
     * In this case, it was used the natural unit of information.</p>
     *
     * @return {@inheritDoc}
     *
     * @see <a href="http://www.wikiwand.com/en/Boltzmann_distribution">Boltzmann distribution</a>
     * @see <a href="http://en.wikipedia.org/wiki/Boltzmann_constant">Boltzmann constant</a>
     */
    @Override
    public double getAcceptanceProbability() {
        final double boltzmannConstant = 1.0;
        return Math.exp((getBestSolutionSoFar().getCost() - getNeighborSolution().getCost())
               / (boltzmannConstant * currentTemperature));
    }

    /**
     * {@inheritDoc}
     *
     * @return true if the system is cold enough and solution search can be
     * stopped, false otherwise
     */
    @Override
    public boolean isToStopSearch() {
        return currentTemperature <= coldTemperature;
    }

    /**
     * {@inheritDoc}
     *
     * Cools the system at a the defined {@link #getCoolingRate() cooling rate}.
     * @see #getCurrentTemperature() ()
     */
    @Override
    public void updateSystemState() {
	    currentTemperature *= (1 - coolingRate);
    }

    /**
     * Gets the current system temperature that
     * represents the system state at the time
     * of the method call.
     *
     * @return the current system temperature
     */
    public double getCurrentTemperature() {
        return currentTemperature;
    }

	/**
	 * Sets the current system temperature.
	 * @param currentTemperature the temperature to set
	 */
	protected void setCurrentTemperature(double currentTemperature) {
		this.currentTemperature = currentTemperature;
	}

	/**
     *
     * @return percentage rate in which the system will be cooled, in scale from [0 to 1[.
     */
    public double getCoolingRate() {
        return coolingRate;
    }

	/**
	 * Sets the percentage rate in which the system will be cooled, in scale from [0 to 1[.
	 * @param coolingRate the rate to set
	 */
	public void setCoolingRate(double coolingRate) {
        this.coolingRate = coolingRate;
    }

    /**
     *
     * @return the temperature that defines the system is cold enough
     * and solution search may be stopped.
     */
    public double getColdTemperature() {
        return coldTemperature;
    }

    /**
     * Sets the temperature that defines the system is cold enough
     * and solution search may be stopped.
     *
     * @param coldTemperature the cold temperature to set
     */
    public void setColdTemperature(double coldTemperature) {
        this.coldTemperature = coldTemperature;
    }

}
