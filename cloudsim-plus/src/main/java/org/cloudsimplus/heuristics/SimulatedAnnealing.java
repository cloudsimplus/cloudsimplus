package org.cloudsimplus.heuristics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

/**
 * Provides the methods for implementation of 
 * a <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated Annealing</a> 
 * algorithm in order to find a suboptimal solution for a problem.
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
 * 
 * @author Manoel Campos da Silva Filho
 * @param <T> the class of solutions the heuristic will deal with
 */
public abstract class SimulatedAnnealing<T extends HeuristicSolution> implements Heuristic<T> {
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
     * Reference to the generic class that will be used to instantiate objects.
     */
    private final Class<T> klass;
    
    /**
     * @see #getNumberOfNeighborhoodSearchsByIteration() 
     */
    private int numberOfNeighborhoodSearchsByIteration;
    
    /**
     * @see #getBestSolutionSoFar() 
     */
    private T bestSolutionSoFar;
    /**
     * @see #getNeighborSolution() 
     */
    private T neighborSolution;
    
    private final ContinuousDistribution random;
    
    /**
     * Instantiates a simulated annealing heuristic.
     * 
     * @param klass Reference to the generic class that will be used to instantiate objects.
     * @param random a pseudo random number generator
     */
    public SimulatedAnnealing(Class<T> klass, ContinuousDistribution random){
        this.random = random;
        this.klass = klass;
        setBestSolutionSoFar(newSolutionInstance());
        neighborSolution = bestSolutionSoFar;
        this.numberOfNeighborhoodSearchsByIteration = 1;
    }

    private T newSolutionInstance() throws RuntimeException {
        try {
            Constructor<T> c = klass.getConstructor(new Class[]{Heuristic.class});
            return c.newInstance(this);
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
    

    /**
     * {@inheritDoc}
     * <p>It is used the Boltzmann factor to define if a worse solution
     * has to be accepted or not in order to avoid local maximums.
     * The factor also ensures that better solutions are always accepted.
     * 
     * The Boltzmann Constant has different values depending of the used unit.
     * In this case, it was used the natural unit of information.</p>
     * 
     * @return {@inheritDoc}
     * 
     * @see <a href="http://en.wikipedia.org/wiki/Boltzmann_constant">Boltzmann_constant</a>
     * @see <a href="http://en.wikipedia.org/wiki/Nat_(unit)">Natural unit of information</a>
     */
    @Override
    public double getAcceptanceProbability() {
        final double boltzmannConstant = 1.0;
        return Math.exp((getBestSolutionSoFar().getCost() - getNeighborSolution().getCost()) 
               / (boltzmannConstant * getCurrentState()));
    }

    /**
     * {@inheritDoc}
     * 
     * @return true if the system is cold enough and solution search can be
     * stopped, false otherwise
     */
    @Override
    public boolean isToStopSearch() {
        return getCurrentState()<= getColdTemperature();
    }

    /**
     * {@inheritDoc}
     * 
     * Cools the system at a the defined {@link #getCoolingRate() cooling rate}.
     * @see #getCurrentState()  
     */
    @Override
    public void updateSystemState() {
        setCurrentTemperature(getCurrentState()* 1 - getCoolingRate());
    }

    /**
     * {@inheritDoc}
     * <b>In this case, it returns the current system temperature.</b>
     * 
     * @return the current system temperature
     */
    @Override
    public double getCurrentState() {
        return currentTemperature;
    }
    
    @Override
    public T getBestSolutionSoFar() {
        return bestSolutionSoFar;
    }

    @Override
    public T getNeighborSolution() {
        return neighborSolution;
    }

    @Override
    public ContinuousDistribution getRandom() {
        return random;
    }

    /**
     * 
     * @return percentage rate in which the system will be cooled, in scale from [0 to 1[.
     */
    public double getCoolingRate() {
        return coolingRate;
    }

    public void setCoolingRate(double coolingRate) {
        this.coolingRate = coolingRate;
    }

    public final void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
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

    @Override
    public final void setBestSolutionSoFar(T solution) {
        this.bestSolutionSoFar = solution;
    }    

    @Override
    public void setNeighborSolution(T neighborSolution) {
        this.neighborSolution = neighborSolution;
    }

    @Override
    public int getNumberOfNeighborhoodSearchsByIteration() {
        return numberOfNeighborhoodSearchsByIteration;
    }

    @Override
    public void setNumberOfNeighborhoodSearchsByIteration(int numberOfNeighborhoodSearches) {
        this.numberOfNeighborhoodSearchsByIteration = numberOfNeighborhoodSearches;
    }
    
}
