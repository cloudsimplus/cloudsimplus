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
package org.cloudsimplus.testbeds;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.Optional;

/**
 * Computes the confidence interval for any arbitrary metric
 * from results got from multiple simulation runs.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.5.3
 */
@Getter
public final class ConfidenceInterval {
    /**
     * The confidence level for computing the CI {@link #value} (in % from 0 to 1).
     */
    public static final double CONFIDENCE_LEVEL = 0.95;

    /**
     * The name of the metric for which the Confidence Interval is computed.
     */
    private final String metricName;

    /**
     * The Confidence Interval value,
     * which is the mean value for an arbitrary metric
     * from multiple simulation runs.
     * This value is usually referred to as just CI.
     *
     * @see #CONFIDENCE_LEVEL
     */
    private final double value;

    /**
     * The Standard Deviation.
     */
    private final double stdDev;

    /**
     * The number of samples used to compute the Confidence Interval.
     */
    private final long samples;

    /**
     * The computed t-Distribution critical value.
     * @see #computeCriticalValue(long)
     */
    private final double criticalValue;

    /**
     * The CI error margin (±), which defines the size of the interval in which
     * results may lay between.
     * The interval is between {@link #getLowerLimit()} and {@link #getUpperLimit()}.
     */
    private final double errorMargin;

    /**
     * The lower limit of the Confidence Interval,
     * based on the {@link #getErrorMargin()}.
     */
    private final double lowerLimit;

    /**
     * The upper limit of the Confidence Interval,
     * based on the {@link #getErrorMargin()}.
     */
    private final double upperLimit;

    /**
     * Creates a ConfidenceInterval (CI) object with 95% confidence level.
     * @param stats the object containing the statistics for the arbitrary metric collected
     * @param metricName the name of the metric for which the CI is computed.
     */
    public ConfidenceInterval(final SummaryStatistics stats, @NonNull final String metricName) {
        this.metricName = metricName;
        final Optional<Double> optionalErrorMargin = errorMargin(stats);
        this.stdDev = stats.getStandardDeviation();
        this.samples = stats.getN();
        this.value = stats.getMean();

        if(optionalErrorMargin.isPresent()) {
            this.criticalValue = computeCriticalValue(samples);
            this.errorMargin = optionalErrorMargin.get();
            this.lowerLimit = stats.getMean() - errorMargin;
            this.upperLimit = stats.getMean() + errorMargin;
            //System.out.printf("95%% Confidence Interval: %.4f ∓ %.4f, that is [%.4f to %.4f]%n", ci, intervalSize, lowerLimit, upperLimit);
            return;
        }

        /* When the number of simulation runs is not greater than 1,
         * there is no way to compute the Confidence Interval.
         * Therefore, it will just store the mean value for a given metric
         * as the `ci` and all other attributes will be zero,
         * indicating there is no Confidence Interval in fact. */
        this.criticalValue = 0;
        this.errorMargin = 0;
        this.lowerLimit = 0;
        this.upperLimit = 0;
     }

    /// Computes the Confidence Interval (CI) error margin for a given set of samples.
    /// That enables finding the interval lower and upper bound around a
    /// mean value.
    ///
    /// To reduce the confidence interval by half, you have to execute the
    /// experiments 4 more times. This is called the "Replication Method" and
    /// just works when the samples are i.i.d. (independent and identically
    /// distributed). Thus, if you have a correlation between samples of each
    /// simulation run, a different method such as a bias compensation,
    /// batch means or regenerative method has to be used.
    ///
    /// **NOTE:** How to compute the error margin is a little confusing.
    /// The Harry Perros' book states that if less than 30 samples are collected,
    /// the t-Distribution has to be used to that purpose.
    ///
    /// However, this [Wikipedia article](https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps)
    /// says that if the standard deviation of the real population is known, it
    /// has to be used the z-value from the Standard Normal Distribution.
    /// Otherwise, it has to be used the t-value from the t-Distribution to
    /// calculate the critical value for defining the error margin (also called
    /// standard error). The book "Numeric Computation and Statistical Data
    /// Analysis on the Java Platform" confirms the last statement, and such
    /// an approach was followed.
    ///
    /// @param stats the statistic object with the values to compute the error
    ///              margin of the confidence interval
    /// @return the error margin to compute the lower and upper bound of the confidence interval
    ///
    /// @link [Critical Values of the Student's t Distribution](http://www.itl.nist.gov/div898/handbook/eda/section3/eda3672.htm)
    /// @link [t-Distribution](https://en.wikipedia.org/wiki/Student%27s_t-distribution)
    /// @link [Harry Perros, "Computer Simulation Techniques: The definitive introduction!," 2009](http://www4.ncsu.edu/~hp/files/simulation.pdf)
    /// @link [Numeric Computation and Statistical Data Analysis on the Java Platform](http://www.springer.com/gp/book/9783319285290)
    public static Optional<Double> errorMargin(@NonNull final SummaryStatistics stats) {
        final long samples = stats.getN();
        if(samples <= 1) {
            return Optional.empty();
        }

        try {
            final double criticalValue = computeCriticalValue(samples);
            return Optional.of(criticalValue * stats.getStandardDeviation() / Math.sqrt(samples));
        } catch (final MathIllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Computes the Confidence Interval (CI) critical value for a given
     * number of samples and confidence level.
     * @param samples number of collected samples
     * @return the Confidence Interval (CI) critical value
     */
    private static double computeCriticalValue(final long samples) {
        /* Creates a T-Distribution with N-1 degrees of freedom
         * since we are computing the sample's confidence interval
         * instead of using the entire population. */
        final double freedomDegrees = samples - 1;

        /* The t-Distribution is used to determine the probability that
        the real population mean lies in a given interval. */
        final var tDist = new TDistribution(freedomDegrees);
        final double significance = 1.0 - CONFIDENCE_LEVEL;
        return tDist.inverseCumulativeProbability(1.0 - significance / 2.0);
    }

    /// Checks if the CI was actually computed (when the number of samples is greater than 1).
    ///
    /// Otherwise, the CI [value][#getValue()] is just the mean for the experiment metric,
    /// not the CI in fact.
    /// In that case, [criticalValue][#getCriticalValue()],
    /// [errorMargin][#getErrorMargin()],
    /// [lowerLimit][#getLowerLimit()] and [upperLimit][#getUpperLimit()]
    /// will be zero (corroborating there is no CI).
    /// @return true if the CI was computed, false otherwise
    public boolean isComputed(){
        return samples > 1;
    }
}
