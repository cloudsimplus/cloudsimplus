/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * A Pseudo-Random Number Generator which returns numbers
 * following a Poisson Distribution, modeling the probability of an event
 * to happen a number of times in a given time interval.
 *
 * @author Manoel Campos da Silva Filho
 * @todo There is the {@link org.apache.commons.math3.distribution.PoissonDistribution} implementation already.
 * This class should simply extend it, as the other class on this package.
 * @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution">Poisson Distribution</a>
 * @since CloudSim Plus 1.2.0
 */
public class PoissonDistr implements DiscreteDistribution {
    /** @see #isApplyAntitheticVariates() */
    private boolean applyAntitheticVariates;

    /**
     * A Uniform Pseudo-Random Number Generator used internally.
     */
    private final UniformDistr rand;

    /** @see #getLambda() */
    private double lambda;

    /** @see #getK() */
    private int k;

    /**
     * Creates a Poisson Pseudo-Random Number Generator to check the probability
     * of 1 event ({@link #getK() k = 1}) to happen at each time interval.
     *
     * @param lambda the average number of events that happen at each 1 time unit.
     *               If one considers the unit as minute, this value means the average number of arrivals
     *               at each minute.
     * @param seed   the seed to initialize the internal uniform Pseudo-Random Number Generator
     * @see #setK(int)
     * @see #setLambda(double)
     */
    public PoissonDistr(final double lambda, final long seed) {
        if (seed < 0) {
            throw new IllegalArgumentException("Seed cannot be negative");
        }
        this.rand = new UniformDistr(seed);
        this.k = 1;
        this.setLambda(lambda);
    }

    /**
     * Creates a Poisson Pseudo-Random Number Generator to check the probability
     * of 1 event ({@link #getK() k = 1}) to happen at each time interval.
     *
     * @param lambda average number of events by interval.
     *               For instance, if it was defined 1 event to be expected at
     *               each 2.5 minutes, it means that 0.4 event is expected
     *               at each minute (1/2.5).
     * @see #setK(int)
     */
    public PoissonDistr(final double lambda) {
        this(lambda, StatisticalDistribution.defaultSeed());
    }

    /**
     * Gets the average number of events (λ) that are expected to happen at each 1 time unit.
     * It is the expected number of events to happen each time,
     * also called the <b>event rate</b> or <b>rate parameter</b>.
     *
     * <p>If one considers the unit as minute, this value means the average number of arrivals
     * at each minute. It's the inverse of the {@link #getInterArrivalMeanTime()}.</p>
     *
     * @return
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * Sets the average number of events (λ) that are expected to happen at each 1 time unit.
     * It is the expected number of events to happen each time,
     * also called the <b>event rate</b> or <b>rate parameter</b>.
     *
     * <p>If one considers the unit as minute, this value means the average number of arrivals
     * at each minute. It's the inverse of the {@link #getInterArrivalMeanTime()}.</p>
     *
     * @param lambda the value to set
     */
    private void setLambda(final double lambda) {
        this.lambda = lambda;
    }

    /**
     * Gets the probability to arrive {@link #getK() k} events in the current time,
     * considering the mean arrival time {@link #getLambda() lambda (λ)},
     * which is represented as {@code Pr(k events in time period)}.
     * It computes the Probability Mass Function (PMF) of the Poisson distribution.
     *
     * @return the probability of a {@link #sample() random variable} to be equal to k
     * @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution#Definition">Poisson Probability Mass Function</a>
     */
    public double eventsArrivalProbability() {
        return (Math.pow(getLambda(), k) * Math.exp(-getLambda())) / CombinatoricsUtils.factorial(k);
    }

    /**
     * Checks if at the current time, {@link #getK() k} events have happened,
     * considering the {@link #eventsArrivalProbability() probability of these k events}
     * to happen in a time interval.
     *
     * @return true if k events have happened at the current time, false otherwise
     */
    public boolean eventsHappened() {
        return rand.sample() <= eventsArrivalProbability();
    }

    /**
     * Gets a random number that represents the next time (from current time or last generated event) that an event will happen,
     * considering the events arrival rate defined by {@link #getLambda() lambda (λ)}.
     * The time unit (if seconds, minutes, hours, etc) is the same
     * considered when setting a value to the {@link #getLambda() lambda} attribute.
     *
     * <p>
     * Calling this method for the first time returns the next event arrival time.
     * The retuning values for consecutive calls can be dealt in one of the following ways:
     * <ul>
     *   <li>
     *   If you are generating all random event arrivals at the beginning of the simulation,
     *   you need to add the previous time to the next event arrival time.
     *   This way, the arrival time of the previous event is added to the next one.
     *   For instance, if consecutive calls to this method return the values 60 and 25, from the current time, that means:
     *       (i) the first event will arrive in 60 seconds;
     *       (ii) the next event will arrive in 85 seconds, that is 25 seconds after the first one.
     *   </li>
     *   <li>
     *   If you are generating event arrivals during simulation runtime,
     *   you must NOT add the previous time to the generated event time,
     *   just use the returned value as the event arrival time.
     *   </li>
     * </ul>
     * </p>
     *
     * <p>Poisson inter-arrival times are independent and identically distributed exponential random variables with mean 1/λ.</p>
     *
     * @return
     * @see <a href="https://books.google.com.br/books?isbn=1420076191">Monte Carlo Methods and Models in Finance and Insurance.
     * Ralf Korn, Elke Korn, et al. 1st edition, 2010. Section 2.4.1: Exponential distribution. Page 33.</a>
     * @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution#Related_distributions">Related distributions</a>
     */
    @Override
    public double sample() {
        return -Math.log(1.0 - rand.sample()) / getLambda();
    }

    @Override
    public long getSeed() {
        return rand.getSeed();
    }

    @Override
    public boolean isApplyAntitheticVariates() {
        return applyAntitheticVariates;
    }

    @Override
    public PoissonDistr setApplyAntitheticVariates(final boolean applyAntitheticVariates) {
        this.applyAntitheticVariates = applyAntitheticVariates;
        return this;
    }

    @Override
    public double originalSample() {
        return rand.sample();
    }

    /**
     * Gets the number of events to check the probability for them to happen
     * in a time interval (default 1).
     *
     * @return
     */
    public int getK() {
        return k;
    }

    /**
     * Sets the number of events to check the probability to happen
     * in a time interval.
     *
     * @param k the value to set
     */
    public void setK(final int k) {
        this.k = k;
    }

    /**
     * Gets the mean time between arrival of two events,
     * which is the inverse of {@link #getLambda() lambda (λ)}.
     * The time unit (if seconds, minutes, hours, etc) is the same
     * considered when setting a value to the {@link #getLambda() lambda} attribute.
     *
     * @return
     */
    public double getInterArrivalMeanTime() {
        return 1.0 / lambda;
    }

    /**
     * Tests the simulations of customers arrivals in a Poisson process.
     * All the code inside this method is just to try the class.
     * That is way it declares internal methods as Functional
     * objects, instead of declaring such methods
     * at the class level and just calling them.
     *
     * @param args
     */
    public static void main(final String[] args) {
        //@TODO This method should be moved to a meaningful example class that creates Cloudlets instead of customers

        /*
         * Mean number of customers that arrives per minute.
         * The value of 0.4 customers per minute means that 1 customer will arrive
         * at every 2.5 minutes.
         * It means that 1 minute / 0.4 customer per minute = 1 customer at every 2.5 minutes.
         * This is the inter-arrival time (in average).
         */
        final double MEAN_CUSTOMERS_ARRIVAL_MINUTE = 0.4;

        /*
         * Time length of each simulation in minutes.
         */
        final int SIMULATION_TIME_LENGTH = 25;

        //If the arrival of each customers must be shown.
        final boolean showCustomerArrivals = true;

        /*
         * Number of simulations to run.
         */
        final int NUMBER_OF_SIMULATIONS = 100;

        final BiConsumer<PoissonDistr, Integer> printArrivals = (poisson, minute) -> {
            if (showCustomerArrivals) {
                System.out.printf("%d customers arrived at minute %d%n", poisson.getK(), minute);
            }
        };

        /**
         * A {@link Function} to simulate the arrival of customers for a given time period.
         * This is just a method to test the implementation.
         *
         * @param poisson the PoissonDistr object that will compute the customer arrivals probabilities
         * @return the number of arrived customers
         */
        final Function<PoissonDistr, Integer> runSimulation = poisson -> {
            /*We want to check the probability of 1 customer to arrive at each
            single minute. The default k value is 1, so we don't need to set it.*/
            final int totalArrivedCustomers =
                IntStream.range(0, SIMULATION_TIME_LENGTH)
                         .filter(time -> poisson.eventsHappened())
                         .peek(time -> printArrivals.accept(poisson, time))
                         .map(time -> poisson.getK())
                         .sum();

            System.out.printf(
                "\t%d customers arrived in %d minutes%n", totalArrivedCustomers, SIMULATION_TIME_LENGTH);
            System.out.printf(
                "\tArrival rate: %.2f customers per minute. Customers inter-arrival time: %.2f minutes in average%n",
                poisson.getLambda(), poisson.getInterArrivalMeanTime());

            return totalArrivedCustomers;
        };

        double customersInAllSimulations = 0;
        PoissonDistr poisson = null;
        final long seed = System.currentTimeMillis();
        for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
            poisson = new PoissonDistr(MEAN_CUSTOMERS_ARRIVAL_MINUTE, seed + i);
            System.out.printf("Simulation number %d%n", i);
            customersInAllSimulations += runSimulation.apply(poisson);
        }

        final double mean = customersInAllSimulations / NUMBER_OF_SIMULATIONS;
        System.out.printf("%nArrived customers average after %d simulations: %.2f%n",
            NUMBER_OF_SIMULATIONS, mean);
        System.out.printf(
            "%.2f customers expected by each %d minutes of simulation with inter-arrival time of %.2f minutes%n",
            poisson.getLambda() * SIMULATION_TIME_LENGTH, SIMULATION_TIME_LENGTH,
            poisson.getInterArrivalMeanTime());
    }
}
