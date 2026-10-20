/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.distributions;

/// Interface to be implemented by a Pseudo-Random Number Generator (PRNG)
/// that follows a defined statistical continuous distribution.
///
/// Besides generating random numbers by [#sample()],
/// it provides some properties of the distribution.
/// The method names follow the ones from the
/// [Apache Commons Statistics](https://commons.apache.org/proper/commons-statistics/)
/// library, which is used by the implementing classes.
///
/// @author Marcos Dias de Assuncao
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
/// @see ContinuousDistributionAbstract
public non-sealed interface ContinuousDistribution extends StatisticalDistribution {
    /// An attribute that implements the Null Object Design Pattern for [ContinuousDistribution] objects.
    ContinuousDistribution NULL = new ContinuousDistributionNull();

    /// {@return the probability density function (PDF) of the distribution for a given value}
    /// It shows how likely values near x are.
    /// @param x the value to compute the density for
    /// @see <a href="https://en.wikipedia.org/wiki/Probability_density_function">Probability density function</a>
    double density(double x);

    /// {@return the cumulative distribution function (CDF) for a given value}
    /// That is the probability that a random value from the distribution is less than or equal to x.
    /// @param x the value to compute the CDF for
    /// @see <a href="https://en.wikipedia.org/wiki/Cumulative_distribution_function">Cumulative distribution function</a>
    double cumulativeProbability(double x);

    /// {@return the inverse of the cumulative distribution function (also known as quantile function)}
    /// It's the smallest value x for which the [CDF][#cumulativeProbability(double)] is greater than or equal to p.
    /// @param p the cumulative probability (between 0 and 1)
    /// @throws IllegalArgumentException when p is not between 0 and 1
    /// @see <a href="https://en.wikipedia.org/wiki/Quantile_function">Quantile function</a>
    double inverseCumulativeProbability(double p);

    /// {@return the mean of the distribution}
    /// It can be [Double#POSITIVE_INFINITY] or [Double#NaN] for some distributions and parameters.
    double getMean();

    /// {@return the variance of the distribution}
    /// It can be [Double#POSITIVE_INFINITY] or [Double#NaN] for some distributions and parameters.
    double getVariance();

    /// {@return the lowest value the distribution can generate}
    /// It can be [Double#NEGATIVE_INFINITY].
    double getSupportLowerBound();

    /// {@return the highest value the distribution can generate}
    /// It can be [Double#POSITIVE_INFINITY].
    double getSupportUpperBound();
}
