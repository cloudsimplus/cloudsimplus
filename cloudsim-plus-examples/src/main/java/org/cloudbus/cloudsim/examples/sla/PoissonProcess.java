package org.cloudbus.cloudsim.examples.sla;

import java.util.Random;
import org.apache.commons.math3.util.CombinatoricsUtils;

/**
 * Represents a Poisson Process that models the probability of an event happens
 * a number of times in a given time interval.
 *
 * @see https://en.wikipedia.org/wiki/Poisson_distribution
 * @see https://en.wikipedia.org/wiki/Poisson_point_process
 *
 * @author Manoel Campos da Silva Filho
 */
public class PoissonProcess {

    /**
     * @see #getLambda()
     */
    private double lambda;

    /**
     * @see #getK()
     */
    private int k;

    /**
     * Uniform random number generator.
     */
    private final Random rand;

    /**
     * Creates a new Poisson process that considers you want to check the
     * probability of 1 event ({@link #getK() k}) to happen at each time.
     *
     * @param lambda average number of events by interval
     * @param seed the seed to initialize the uniform random number generator
     * @see #setK(int)
     */
    public PoissonProcess(double lambda, long seed) {
        this.k = 1;
        this.setLambda(lambda);
        this.rand = new Random(seed);
    }

    /**
     *
     * @return the average number of events that happen at each single time
     * unit. It is also called the event rate or rate parameter.
     *
     * If the unit is minute, it means the average number of arrivals at each
     * minute.
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * Sets the average number of events that happen at each single time unit.
     *
     * @param lambda the value to set
     */
    private void setLambda(double lambda) {
        this.lambda = lambda;
    }

    /**
     *
     * @return the probability to arrive {@link #getK() K} events in the current
     * time.
     * @see https://en.wikipedia.org/wiki/Poisson_distribution
     */
    public double probabilityToArriveNextKEvents() {
        //computes the Probability Mass Function (PMF) of the Poisson distribution
        return (Math.pow(getLambda(), k) * Math.exp(-getLambda())) / CombinatoricsUtils.factorial(k);
    }

    /**
     * Indicates if at the current time, {@link #getK() K} events have happened,
     * considering the probability of these K events to happen in a time
     * interval.
     *
     * @return true if the K events have happened at current time, false
     * otherwise
     * @see #probabilityOfKEventsToHappen()
     */
    public boolean haveKEventsHappened() {
        return rand.nextDouble() >= probabilityToArriveNextKEvents();
    }

    /**
     *
     * @return the number of events to check the probability to happen in a time
     * interval.
     */
    public int getK() {
        return k;
    }

    /**
     * Sets the number of events to check the probability to happen in a time
     * time.
     *
     * @param k the value to set
     */
    public void setK(int k) {
        this.k = k;
    }

    /**
     *
     * @return the mean time between arrival of two events.
     */
    public double getInterarrivalMeanTime() {
        final double oneMinute = 1.0;
        return oneMinute / lambda;
    }

}
