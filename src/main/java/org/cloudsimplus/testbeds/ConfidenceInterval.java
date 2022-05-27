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

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.Objects;
import java.util.Optional;

/**
 * Computes the confidence interval for any arbitrary metric
 * from results got from multiple simulation runs.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.5.3
 */
public final class ConfidenceInterval {
    /**
     * The confidence level for computing the CI {@link #value} (in % from 0 to 1).
     */
    public static final double CONFIDENCE_LEVEL = 0.95;

    private final String metricName;
    private final double value;
    private final double stdDev;
    private final long samples;

    private final double criticalValue;
    private final double errorMargin;
    private final double lowerLimit;
    private final double upperLimit;

    /**
     * Creates a ConfidenceInterval object with 95% confidence level.
     * @param stats the object containing the statistics for the arbitrary metric collected
     * @param metricName
     */
    public ConfidenceInterval(final SummaryStatistics stats, final String metricName) {
        this.metricName = Objects.requireNonNull(metricName);
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

        /* When the number of simulations runs is not greater than 1,
         * there is no way to compute the confidence interval.
         * Therefore, it will just store the mean value for a given metric
         * as the ci and all other attributes will be zero,
         * indicating there is no confidence interval in fact. */
        this.criticalValue = 0;
        this.errorMargin = 0;
        this.lowerLimit = 0;
        this.upperLimit = 0;
     }

    /**
     * Computes the Confidence Interval error margin for a given set of samples
     * in order to enable finding the interval lower and upper bound around a
     * mean value.
     *
     * <p>
     * To reduce the confidence interval by half, one have to execute the
     * experiments 4 more times. This is called the "Replication Method" and
     * just works when the samples are i.i.d. (independent and identically
     * distributed). Thus, if you have correlation between samples of each
     * simulation run, a different method such as a bias compensation,
     * batch means or regenerative method has to be used. </p>
     *
     * <b>NOTE:</b> How to compute the error margin is a little confusing.
     * The Harry Perros' book states that if less than 30 samples are collected,
     * the t-Distribution has to be used to that purpose.
     *
     * However, this
     * <a href="https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps">Wikipedia
     * article</a>
     * says that if the standard deviation of the real population is known, it
     * has to be used the z-value from the Standard Normal Distribution.
     * Otherwise, it has to be used the t-value from the t-Distribution to
     * calculate the critical value for defining the error margin (also called
     * standard error). The book "Numeric Computation and Statistical Data
     * Analysis on the Java Platform" confirms the last statement and such
     * approach was followed.
     *
     * @param stats the statistic object with the values to compute the error
     * margin of the confidence interval
     * @return the error margin to compute the lower and upper bound of the
     * confidence interval
     *
     * @see
     * <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda3672.htm">Critical
     * Values of the Student's t Distribution</a>
     * @see
     * <a href="https://en.wikipedia.org/wiki/Student%27s_t-distribution">t-Distribution</a>
     * @see <a href="http://www4.ncsu.edu/~hp/files/simulation.pdf">Harry
     * Perros, "Computer Simulation Techniques: The definitive introduction!,"
     * 2009</a>
     * @see <a href="http://www.springer.com/gp/book/9783319285290">Numeric
     * Computation and Statistical Data Analysis on the Java Platform</a>
     */
    public static Optional<Double> errorMargin(final SummaryStatistics stats) {
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
     * Computes the Confidence Interval critical value for a given
     * number of samples and confidence level.
     * @param samples number of collected samples
     * @return
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

    /**
     * Gets the Confidence Interval value,
     * which is the mean value for an arbitrary metric
     * from multiple simulation runs.
     * This value is usually referred as just CI.
     *
     * @see #CONFIDENCE_LEVEL
     */
    public double getValue() {
        return value;
    }

    public double getStdDev() {
        return stdDev;
    }

    /**
     * Gets the number of samples used to compute the Confidence Interval.
     */
    public long getSamples() {
        return samples;
    }

    /**
     * Gets the t-Distribution critical value.
     */
    public double getCriticalValue() {
        return criticalValue;
    }

    /**
     * Gets the CI error margin, which defines the size of the interval in which
     * results may lay between.
     * The interval is between {@link #getLowerLimit()} and {@link #getUpperLimit()}.
     */
    public double getErrorMargin() {
        return errorMargin;
    }

    /**
     * Gets the lower limit of the Confidence Interval,
     * based on the {@link #getErrorMargin()}.
     */
    public double getLowerLimit() {
        return lowerLimit;
    }

    /**
     * Gets the upper limit of the Confidence Interval,
     * based on the {@link #getErrorMargin()}.
     */
    public double getUpperLimit() {
        return upperLimit;
    }

    /**
     * Gets the name of the metric for which the Confidence Interval is computed.
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * Check if the CI was actually computed, if the number of samples is greater than 1.
     * Otherwise, the CI {@link #value} is just the mean for the experiment metric,
     * not the CI in fact.
     * In that case, {@link #criticalValue}, {@link #errorMargin},
     * {@link #lowerLimit} and {@link #upperLimit}
     * will be zero (corroborating there is no CI).
     *
     * @return
     */
    public boolean isComputed(){
        return samples > 1;
    }
}
