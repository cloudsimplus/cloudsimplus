/**
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.apache.commons.math3.util.CombinatoricsUtils;

/**
 * Represents a Poisson Process which models the probability of an event
 * to happen a number of times in a given time interval.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution">Poisson Distribution</a>
 * @see <a href="https://en.wikipedia.org/wiki/Poisson_point_process">Poisson Point Process</a>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public class PoissonProcess {
    /**
     * A random number generator that returns values between [0 and 1[.
     */
    private ContinuousDistribution rand;
    
    /**
     * @see #getLambda()
     */
    private double lambda;

    /**
     * @see #getK()
     */
    private int k;

    /**
     * Creates a new Poisson process to check
     * the probability of 1 event ({@link #getK() k}) to happen at each time
     * interval.
     *
     * @param lambda the average number of events that happen at each 1 time unit.
     * @param seed the seed to initialize the uniform random number generator
     * @see #setK(int)
     * @see #setLambda(double) 
     */
    public PoissonProcess(double lambda, long seed){
        this.rand = new UniformDistr(seed);
        this.k = 1;
        this.setLambda(lambda);
    }
    
    /**
     * Creates a new Poisson process that considers you want to check
     * the probability of 1 event ({@link #getK() k}) to happen at each time.
     *
     * @param lambda average number of events by interval
     * 
     * @see #setK(int)
     */
    public PoissonProcess(double lambda){
        this(lambda, -1);
    }    

    /**
     * Gets the average number of events that are expected to happen at each 1 time unit.
     * It is the expected number of events to happen each time, 
     * also called the <b>event rate</b> or <b>rate parameter</b>.
     *
     * <p>If the unit is minute, this value means the average number of arrivals
     * at each minute. It's the inverse of the {@link #getInterarrivalMeanTime()}.</p>
     * @return 
     */
    public double getLambda(){
        return lambda;
    }

    /**
     * Sets the average number of events that are expected to happen at each 1 time unit.
     * It is the expected number of events to happen each time, 
     * also called the <b>event rate</b> or <b>rate parameter</b>.
     *
     * <p>If the unit is minute, this value means the average number of arrivals
     * at each minute. It's the inverse of the {@link #getInterarrivalMeanTime()}.</p>
     * @param lambda the value to set
     */
    private void setLambda(double lambda) {
        this.lambda = lambda;
    }

    /**
     * Gets the probability to arrive {@link #getK() K} events in the current time,
     * considering the expected average arrival time {@link #getLambda() lambda}.
     * @return 
     * @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution">Poisson distribution</a>
     */
    public double eventsArrivalProbability(){
        //computes the Probability Mass Function (PMF) of the Poisson distribution
        return (Math.pow(getLambda(), k) * Math.exp(-getLambda())) / CombinatoricsUtils.factorial(k);
    }

    /**
     * Checks if at the current time, {@link #getK() K} events have happened,
     * considering the probability of these K events to happen
     * in a time interval.
     *
     * @return true if the K events have happened at current time, false otherwise
     * @see #eventsArrivalProbability()
     */
    public boolean eventsHappened(){
        return rand.sample() >= eventsArrivalProbability();
    }

    /**
     * Gets the number of events to check the probability to happen
     * in a time interval (default 1).
     * @return 
     */
    public int getK() {
        return k;
    }

    /**
     * Sets the number of events to check the probability to happen
     * in a time time.
     * @param k the value to set
     */
    public void setK(int k) {
        this.k = k;
    }

    /**
     * Gets the mean time between arrival of two events.
     * @return 
     */
    public double getInterarrivalMeanTime(){
        final double oneMinute = 1.0;
        return oneMinute/lambda;
    }

    /**
     * Tests the simulations of customers arrivals in a Poisson process.
     *
     * @param args
     */
    public static void main(String args[]){
        /*
         * Average number of customers that arrives per minute.
         * The value of 0.4 customers per minute means that 1 customer will arrive
         * at every 2.5 minutes.
         * It means that 1 minute / 0.4 customer per minute = 1 customer at every 2.5 minutes.
         * This is the interarrival time (in average).
         */
        final double MEAN_CUSTOMERS_ARRIVAL_PER_MINUTE=0.4;

        /*
         * Time length of each simulation in minutes.
        */
        final int SIMULATION_TIME_LENGHT = 25;
        
        //If the arrival of each customers must be shown.
        final boolean showCustomerArrivals = true;

        /*
         * Number of simulations to run.
         */
        int NUMBER_OF_SIMULATIONS = 100;
        
        BiConsumer<PoissonProcess, Integer> printArrivals = (poisson, minute) -> {
            if(showCustomerArrivals)
                System.out.printf("%d customers arrived at minute %d\n", poisson.getK(), minute);
        };

        /*
         * A {@link Function} to simulate the arrival of customers for a given time period.
         * This is just a method to test the implementation.
         *
         * @param poisson the PoissonProcess object that will compute the customer arrivals probabilities
         * @return the number of arrived customers
         */
        Function<PoissonProcess, Integer> runSimulation = poisson -> {
            /*We want to check the probability of 1 customer to arrive at each
            single minute. The default k value is 1, so we dont need to set it.*/
            final int totalArrivedCustomers = 
                IntStream.range(0, SIMULATION_TIME_LENGHT)
                    .filter(i -> poisson.eventsHappened())
                    .peek(i -> printArrivals.accept(poisson, i))
                    .map(i -> poisson.getK())
                    .sum();

            System.out.printf(
                    "\t%d customers arrived in %d minutes\n", totalArrivedCustomers, SIMULATION_TIME_LENGHT);
            System.out.printf("\tArrival rate: %.2f customers per minute. Customers interarrival time: %.2f minutes in average\n",
                    poisson.getLambda(), poisson.getInterarrivalMeanTime());

            return totalArrivedCustomers;
        };      

        int customersArrivedInAllSimulations = 0;
        PoissonProcess poisson = null;
        for(int i = 0; i < NUMBER_OF_SIMULATIONS; i++){
            long seed=System.currentTimeMillis();
            poisson = new PoissonProcess(MEAN_CUSTOMERS_ARRIVAL_PER_MINUTE, seed);
            System.out.printf("Simulation number %d\n", i);
            customersArrivedInAllSimulations += runSimulation.apply(poisson);
        }

        double mean = customersArrivedInAllSimulations/(double)NUMBER_OF_SIMULATIONS;
        System.out.printf("\nArrived customers average after %d simulations: %.2f\n",
                NUMBER_OF_SIMULATIONS, mean);
        if(poisson != null){
            System.out.printf(
                "%.2f customers expected by each %d minutes of simulation with interarrival time of %.2f minutes\n",
                 poisson.getLambda()*SIMULATION_TIME_LENGHT, SIMULATION_TIME_LENGHT,
                 poisson.getInterarrivalMeanTime());
        }
    }
}
