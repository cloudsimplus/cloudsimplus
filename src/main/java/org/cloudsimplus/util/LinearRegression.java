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
package org.cloudsimplus.util;

import lombok.NonNull;

import java.util.Arrays;

/// A [linear regression](https://en.wikipedia.org/wiki/Linear_regression) model
/// `y = β0 + β1·x1 + β2·x2 + ... + βk·xk`, fitted by
/// [Ordinary Least Squares (OLS)](https://en.wikipedia.org/wiki/Ordinary_least_squares).
///
/// - `y` is the dependent variable (the value to predict);
/// - `x1, ..., xk` are the independent variables (the values used for the prediction);
/// - `β0` is the intercept (the value of `y` when all `x` are zero);
/// - `β1, ..., βk` are the coefficients (how much `y` changes when each `x` increases by 1).
///
/// When there is a single independent variable (`k = 1`), it's a *simple* linear regression
/// `y = β0 + β1·x`, where `β1` is the slope of the line.
/// Otherwise, it's a *multiple* linear regression.
///
/// OLS finds the parameters `β` that minimize the sum of squared differences between
/// each observed `y` and the predicted one.
/// The parameters are computed using a [QRDecomposition] of the data,
/// which keeps good accuracy even when the independent variables are highly correlated
/// (see the [QRDecomposition] documentation for details).
///
/// Objects of this class are immutable: all values are computed when the object is created.
///
/// ## Why this class exists
///
/// CloudSim Plus used to compute regressions with the `SimpleRegression` and `OLSMultipleLinearRegression`
/// classes from [Apache Commons Math 3](https://commons.apache.org/proper/commons-math/).
/// That library was replaced because it has had no release since 2016 and has no Java module support
/// (it has neither a `module-info` nor an `Automatic-Module-Name`).
///
/// The Commons Math team moved most of its code to smaller libraries that support Java modules,
/// such as [Commons Statistics](https://commons.apache.org/proper/commons-statistics/) and
/// [Commons RNG](https://commons.apache.org/proper/commons-rng/), which CloudSim Plus now uses.
/// However, none of them provides linear regression.
/// It's only available in Commons Math 4, whose last release is a beta from 2022 (`4.0-beta1`).
///
/// Since CloudSim Plus only needs a small part of those classes (parameter estimates, predictions and R²),
/// this class implements it, using the same algorithm as Commons Math (a [QRDecomposition])
/// to keep the same numerical accuracy.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see Regression
public final class LinearRegression {
    /// The estimated parameters `[β0, β1, ..., βk]`.
    private final double[] parameters;

    /// @see #getRSquared()
    private final double rSquared;

    /// Fits a simple linear regression model `y = β0 + β1·x`.
    ///
    /// @param x the values of the independent variable
    /// @param y the values of the dependent variable (one for each `x` value)
    /// @return the fitted model
    /// @throws IllegalArgumentException when `x` and `y` have different lengths,
    ///         or there are fewer than 2 observations
    public static LinearRegression of(@NonNull final double[] x, @NonNull final double[] y) {
        final double[][] observations = Arrays.stream(x).mapToObj(value -> new double[]{value}).toArray(double[][]::new);
        return new LinearRegression(observations, y);
    }

    /// Fits a multiple linear regression model `y = β0 + β1·x1 + ... + βk·xk`.
    ///
    /// @param x the values of the independent variables, where each row is an observation
    ///          and each column is an independent variable. For instance, `x[i][j]` is the value of `xj+1`
    ///          for the `i`-th observation.
    /// @param y the values of the dependent variable (one for each observation, that is, each row of `x`)
    /// @return the fitted model
    /// @throws IllegalArgumentException when the number of rows of `x` is different from the length of `y`,
    ///         the rows of `x` have different lengths, or there are fewer observations
    ///         than [parameters][#getParameterEstimates()] to estimate
    public static LinearRegression of(@NonNull final double[][] x, @NonNull final double[] y) {
        return new LinearRegression(x, y);
    }

    private LinearRegression(final double[][] x, final double[] y) {
        validate(x, y);
        final double[][] design = addInterceptColumn(x);
        final var qr = new QRDecomposition(design);
        this.parameters = qr.isFullRank() ? qr.solve(y) : nanArray(design[0].length);
        this.rSquared = computeRSquared(x, y);
    }

    private static void validate(final double[][] x, final double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException(
                "x and y must have the same number of observations, but they have %d and %d".formatted(x.length, y.length));
        }

        if (x.length == 0 || x[0].length == 0) {
            throw new IllegalArgumentException("There must be at least one observation and one independent variable");
        }

        final int parametersNumber = x[0].length + 1;
        if (x.length < parametersNumber) {
            throw new IllegalArgumentException(
                "There must be at least %d observations to estimate %d parameters, but there are %d"
                    .formatted(parametersNumber, parametersNumber, x.length));
        }
    }

    /// Creates the *design matrix* of the regression, which is the matrix `x`
    /// with an additional first column filled with 1s.
    /// That column is multiplied by the intercept `β0`, enabling the regression line
    /// not to pass through the origin.
    ///
    /// @param x the values of the independent variables (each row is an observation)
    /// @return the design matrix
    /// @throws IllegalArgumentException when the rows of `x` have different lengths
    private static double[][] addInterceptColumn(final double[][] x) {
        final int variables = x[0].length;
        final double[][] design = new double[x.length][variables + 1];
        for (int i = 0; i < x.length; i++) {
            if (x[i].length != variables) {
                throw new IllegalArgumentException("All observations must have the same number of independent variables");
            }

            design[i][0] = 1;
            System.arraycopy(x[i], 0, design[i], 1, variables);
        }

        return design;
    }

    private static double[] nanArray(final int length) {
        final double[] array = new double[length];
        Arrays.fill(array, Double.NaN);
        return array;
    }

    /// Computes the [coefficient of determination (R²)](https://en.wikipedia.org/wiki/Coefficient_of_determination)
    /// as `1 - SSR/SST`, where:
    ///
    /// - `SSR` is the residual sum of squares: `Σ(yi - ŷi)²`, with `ŷi` being the predicted value for `yi`;
    /// - `SST` is the total sum of squares: `Σ(yi - ȳ)²`, with `ȳ` being the mean of `y`.
    ///
    /// @param x the values of the independent variables (each row is an observation)
    /// @param y the values of the dependent variable
    /// @return the R²
    private double computeRSquared(final double[][] x, final double[] y) {
        final double mean = Arrays.stream(y).average().orElse(Double.NaN);
        double residualSumOfSquares = 0;
        double totalSumOfSquares = 0;
        for (int i = 0; i < y.length; i++) {
            residualSumOfSquares += square(y[i] - predict(x[i]));
            totalSumOfSquares += square(y[i] - mean);
        }

        // When all y values are equal, there is no variation to explain
        return totalSumOfSquares == 0 ? Double.NaN : 1 - residualSumOfSquares / totalSumOfSquares;
    }

    private static double square(final double value) {
        return value * value;
    }

    /// Predicts the value of `y` for the given values of the independent variables,
    /// computing `β0 + β1·x1 + ... + βk·xk`.
    ///
    /// @param x the values of the independent variables `x1, ..., xk`
    ///          (a single value for a simple linear regression)
    /// @return the predicted `y` value
    /// @throws IllegalArgumentException when the number of given values is different
    ///         from the number of independent variables of the model
    public double predict(@NonNull final double... x) {
        if (x.length != parameters.length - 1) {
            throw new IllegalArgumentException(
                "The model has %d independent variables, but %d values were given".formatted(parameters.length - 1, x.length));
        }

        double prediction = parameters[0];
        for (int j = 0; j < x.length; j++) {
            prediction += parameters[j + 1] * x[j];
        }

        return prediction;
    }

    /// {@return a copy of the estimated parameters `[β0, β1, ..., βk]`}
    /// For a simple linear regression, that is `[intercept, slope]`.
    ///
    /// If the independent variables are linearly dependent (for instance, a variable that has
    /// the same value in all observations, or that is a multiple of another one),
    /// there is no unique solution. In such a case, all parameters are [Double#NaN].
    public double[] getParameterEstimates() {
        return parameters.clone();
    }

    /// {@return the [coefficient of determination (R²)](https://en.wikipedia.org/wiki/Coefficient_of_determination)}
    /// It shows how well the model fits the data: it's the fraction of the variation of `y`
    /// explained by the independent variables.
    /// It's 1 when the model fits all observations perfectly, and it's close to 0 when
    /// the model doesn't explain `y` any better than just using its mean.
    ///
    /// It's [Double#NaN] when the [parameters][#getParameterEstimates()] can't be estimated
    /// or when all values of `y` are equal.
    public double getRSquared() {
        return rSquared;
    }
}
