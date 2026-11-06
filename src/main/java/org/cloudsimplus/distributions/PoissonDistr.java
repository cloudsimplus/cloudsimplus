/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.distributions;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.statistics.distribution.PoissonDistribution;

/// A Pseudo-Random Number Generator which returns numbers
/// following a Poisson Distribution, modeling the probability of an event
/// to happen a number of times in a given time interval.
///
/// @author Manoel Campos da Silva Filho
/// @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution">Poisson Distribution</a>
/// @since CloudSim Plus 1.2.0
public class PoissonDistr implements DiscreteDistribution {
    /// @see #isApplyAntitheticVariates()
    private boolean applyAntitheticVariates;

    /// A Uniform Pseudo-Random Number Generator used internally.
    private final UniformDistr rand;

    /// The average number of events (λ) that are expected to happen at each 1 time unit.
    /// It is the expected number of events to happen each time,
    /// also called the **event rate** or **rate parameter**.
    ///
    /// If one considers the unit as minute, this value means the average number of arrivals
    /// at each minute. It's the inverse of the [#getInterArrivalMeanTime()].
    @Getter
    private double lambda;

    /// The Poisson distribution with the given [#lambda],
    /// used to compute the [#eventsArrivalProbability()].
    private PoissonDistribution distribution;

    /// The number of events to check the probability for them to happen
    /// in a time interval (default 1).
    @Getter @Setter
    private int k;

    /// Creates a Poisson Pseudo-Random Number Generator to check the probability
    /// of 1 event ([k = 1][#getK()]) to happen at each time interval.
    ///
    /// @param lambda the average number of events that happen at each 1 time unit.
    /// If one considers the unit as minute, this value means the average number of arrivals
    /// at each minute.
    /// @param seed   the seed to initialize the internal uniform Pseudo-Random Number Generator
    /// @throws IllegalArgumentException when lambda is not greater than zero or the seed is negative
    /// @see #setK(int)
    /// @see #setLambda(double)
    public PoissonDistr(final double lambda, final long seed) {
        if (seed < 0) {
            throw new IllegalArgumentException("Seed cannot be negative");
        }
        this.rand = new UniformDistr(seed);
        this.k = 1;
        this.setLambda(lambda);
    }

    /// Creates a Poisson Pseudo-Random Number Generator to check the probability
    /// of 1 event ([k = 1][#getK()]) to happen at each time interval.
    ///
    /// @param lambda average number of events by interval.
    /// For instance, if it was defined 1 event to be expected at
    /// each 2.5 minutes, it means that 0.4 event is expected
    /// at each minute (1/2.5).
    /// @throws IllegalArgumentException when lambda is not greater than zero
    /// @see #setK(int)
    public PoissonDistr(final double lambda) {
        this(lambda, StatisticalDistribution.defaultSeed());
    }

    /// Sets the average number of events (λ) that are expected to happen at each 1 time unit.
    /// It is the expected number of events to happen each time,
    /// also called the **event rate** or **rate parameter**.
    ///
    /// If one considers the unit as minute, this value means the average number of arrivals
    /// at each minute. It's the inverse of the [#getInterArrivalMeanTime()].
    ///
    /// @param lambda the value to set
    /// @throws IllegalArgumentException when lambda is not greater than zero
    private void setLambda(final double lambda) {
        this.distribution = PoissonDistribution.of(lambda);
        this.lambda = lambda;
    }

    /// Gets the probability (Pr) to arrive [k][#getK()] events in the current time,
    /// considering the mean arrival time [lambda (λ)][#getLambda()],
    /// which is represented as `Pr(k events in time period)`.
    /// It computes the Probability Mass Function (PMF) of the Poisson distribution.
    ///
    /// @return the probability of a [random variable][#sample()] to be equal to k
    /// @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution#Definition">Poisson Probability Mass Function</a>
    public double eventsArrivalProbability() {
        return distribution.probability(k);
    }

    /// Checks if at the current time, [k][#getK()] events have happened,
    /// considering the [probability of these k events][#eventsArrivalProbability()]
    /// to happen in a time interval.
    ///
    /// @return true if k events have happened at the current time, false otherwise
    public boolean eventsHappened() {
        return rand.sample() <= eventsArrivalProbability();
    }

    /// {@return a random number that represents the next time (from current time or last generated event)
    /// that an event will happen}
    /// That time is computed based on events arrival rate defined by [lambda (λ)][#getLambda()].
    /// The time unit (if seconds, minutes, hours, etc.) is the same
    /// you considered when setting a value to the [lambda][#getLambda()] attribute.
    ///
    /// Calling this method for the first time returns the next event arrival time.
    /// The return values for consecutive calls can be handled in one of the following ways:
    ///
    /// -
    ///   If you are generating all random event arrivals at the beginning of the simulation,
    ///   you need to add the previous time to the next event arrival time.
    ///   This way, the arrival time of the previous event is added to the next one.
    ///   For instance, if consecutive calls to this method return the values 60 and 25,
    ///   from the current time, that means:
    ///   (i) the first event will arrive in 60 seconds;
    ///   (ii) the next event will arrive in 85 seconds, that is 25 seconds after the first one.
    /// -
    ///   If you are generating event arrivals during simulation runtime,
    ///   you must NOT add the previous time to the generated event time,
    ///   just use the returned value as the event arrival time.
    ///
    /// Poisson inter-arrival times are independent and identically distributed
    /// exponential random variables with mean 1/λ.
    ///
    /// @see <a href="https://books.google.com.br/books?isbn=1420076191">Monte Carlo Methods and Models in Finance and Insurance.
    /// Ralf Korn, Elke Korn, et al. 1st edition, 2010. Section 2.4.1: Exponential distribution. Page 33.</a>
    /// @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution#Related_distributions">Related distributions</a>
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

    /// {@return the mean time between arrival of two events}
    /// It is the inverse of [lambda (λ)][#getLambda()].
    /// The time unit (if seconds, minutes, hours, etc.) is the same
    /// considered when setting a value to the [lambda][#getLambda()] attribute.
    public double getInterArrivalMeanTime() {
        return 1.0 / lambda;
    }
}
