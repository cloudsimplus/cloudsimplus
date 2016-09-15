package org.cloudsimplus.heuristics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

/**
 * Provides the methods for implementation of 
 * a <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated Annealing</a> 
 * algorithm in order to find a suboptimal solution for a problem.
 * 
 * @author Manoel Campos da Silva Filho
 * @param <T> the class of solutions the heuristic will deal with
 */
public abstract class SimulatedAnnealing<T extends HeuristicSolution> implements Heuristic<T> {
    /**
     * @see #getColdTemperature() 
     */
    private int coldTemperature;
    
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
    
    private T bestSolutionSoFar;
    private T neighborSolution;
    private T currentSolution;
    
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
        setCurrentSolution(newSolutionInstance());
        bestSolutionSoFar = currentSolution;
        neighborSolution = currentSolution;
    }

    private T newSolutionInstance() throws RuntimeException {
        try {
            Constructor<T> c = klass.getConstructor(new Class[]{Heuristic.class});
            return c.newInstance(this);
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected double boltzmannFactor() {
        /*
        The Boltzmann Constant has different values
        depending of the used unit.
        In this case, it was used the natural unit of information
        More info:
        http://en.wikipedia.org/wiki/Nat_(unit)
        http://en.wikipedia.org/wiki/Boltzmann_constant#Value_in_different_units
         */
        final double boltzmannConstant = 1.0;
        return Math.exp((bestSolutionSoFar().getFitness() - getNeighborSolution().getFitness()) / (boltzmannConstant * getCurrentState()));
    }

    @Override
    public boolean acceptNeighborSolution() {
        //If the new solution is better, accept it
        if (getNeighborSolution().compareTo(bestSolutionSoFar()) > 0) {
            return true;
        }
        
        //If the new solution is worse, randomly calculate the probability to it be accepted
        return boltzmannFactor() > random.sample();
    }

    /**
     * {@inheritDoc}
     * 
     * @return true if the system is cold enough and solution search can be
     * stopped, false otherwise
     */
    @Override
    public boolean stopSearch() {
        return getCurrentState() <= getColdTemperature();
    }

    /**
     * {@inheritDoc}
     * 
     * Cools the system at a the defined {@link #getCoolingRate() cooling rate}.
     * @see #getCurrentState()  
     */
    @Override
    public void updateSystemState() {
        setCurrentTemperature(getCurrentTemperature() * 1 - getCoolingRate());
    }

    @Override
    public void findNextSolution() {
        neighborSolution = (T)currentSolution.createNeighbor();
        if (acceptNeighborSolution()) {
            setCurrentSolution(neighborSolution);
        }
        if (currentSolution.compareTo(bestSolutionSoFar) > 0) {
            bestSolutionSoFar = currentSolution;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @return the current system temperature
     */
    @Override
    public double getCurrentState() {
        return getCurrentTemperature();
    }
    
    @Override
    public T bestSolutionSoFar() {
        return bestSolutionSoFar;
    }

    @Override
    public T getNeighborSolution() {
        return neighborSolution;
    }

    @Override
    public T getCurrentSolution() {
        return currentSolution;
    }

    @Override
    public ContinuousDistribution getRandom() {
        return random;
    }

    /**
     * 
     * @return percentage rate in which the system will be cooled (in scale from 0 to 1).
     */
    public double getCoolingRate() {
        return coolingRate;
    }

    public void setCoolingRate(double coolingRate) {
        this.coolingRate = coolingRate;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public final void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    /**
     * 
     * @return the currentTemperature that defines the system is cold enough
     * and solution search can be stopped.
     */
    public int getColdTemperature() {
        return coldTemperature;
    }

    public void setColdTemperature(int coldTemperature) {
        this.coldTemperature = coldTemperature;
    }

    @Override
    public final void setCurrentSolution(T solution) {
        this.currentSolution = solution;
    }    
    
}
