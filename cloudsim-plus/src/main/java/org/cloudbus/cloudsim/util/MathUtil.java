/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.util;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.*;

/**
 * A class containing multiple convenient math functions.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 * @see TimeUtil
 */
public final class MathUtil {
    /**
     * 100% represented in scale [0 .. 1].
     */
    public static final double HUNDRED_PERCENT = 0.1;

    /**
     * A private constructor to avoid class instantiation.
     */
    private MathUtil(){}

    /**
     * Sums a list of numbers.
     *
     * @param list the list of numbers
     * @return the double
     */
    public static double sum(final List<? extends Number> list) {
        double sum = 0.0;
        for (final Number number : list) {
            sum += number.doubleValue();
        }

        return sum;
    }

    /**
     * Gets the median from a list of numbers.
     *
     * @param list the list of numbers
     * @return the median
     */
    public static double median(final Collection<Double> list) {
        return getStatistics(list).getPercentile(50);
    }

    /**
     * Gets the median from an array of numbers.
     *
     * @param list the array of numbers
     * @return the median
     */
    public static double median(final double... list) {
        return getStatistics(list).getPercentile(50);
    }

    /**
     * Gets an object to compute descriptive statistics for an list of numbers.
     *
     * @param list the list of numbers. Must not be null.
     * @return descriptive statistics for the list of numbers.
     */
    public static DescriptiveStatistics getStatistics(final Collection<Double> list) {
        final DescriptiveStatistics stats = new DescriptiveStatistics();
        list.forEach(stats::addValue);
        return stats;
    }

    /**
     * Gets an object to compute descriptive statistics for an array of numbers.
     *
     * @param list the array of numbers. Must not be null.
     * @return descriptive statistics for the array of numbers.
     */
    public static DescriptiveStatistics getStatistics(final double... list) {
        return new DescriptiveStatistics(list);
    }

    /**
     * Gets the average from a list of numbers.
     * If the list is empty or contains just zeros, returns 0.
     *
     * @param list the list of numbers
     * @return the average
     */
    public static double mean(final List<Double> list) {
        if(list.isEmpty()){
            return 0;
        }

        return sum(list) / (double)list.size();
    }

    /**
     * Gets the Variance from a list of numbers.
     *
     * @param list the list of numbers
     * @return the variance
     */
    public static double variance(final List<Double> list) {
        long count = 0;
        double mean = mean(list);
        double deltaSum = 0.0;

        for(final double x : list) {
            count++;
            final double delta = x - mean;
            mean += delta / count;
            deltaSum += delta * (x - mean);
        }

        return deltaSum / (count - 1);
    }

    /**
     * Gets the Standard Deviation from a list of numbers.
     *
     * @param list the list of numbers
     * @return the standard deviation
     */
    public static double stDev(final List<Double> list) {
        return Math.sqrt(variance(list));
    }

    /**
     * Gets the <a href="https://en.wikipedia.org/wiki/Median_absolute_deviation">Median Absolute Deviation (MAD)</a> from a array of numbers.
     *
     * @param data the array of numbers
     * @return the mad
     */
    public static double mad(final double... data) {
        if (data.length == 0) {
            return 0;
        }

        final double median = median(data);
        final double[] deviationSum = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            deviationSum[i] = Math.abs(median - data[i]);
        }

        return median(deviationSum);
    }

    /**
     * Gets the <a href="https://en.wikipedia.org/wiki/Interquartile_range">Inter-quartile Range (IQR)</a>
     * from an array of numbers.
     *
     * @param data the array of numbers
     * @return the IQR
     */
    public static double iqr(final double... data) {
        Arrays.sort(data);
        final int quartile1 = (int) Math.round(0.25 * (data.length + 1)) - 1;
        final int quartile3 = (int) Math.round(0.75 * (data.length + 1)) - 1;
        return data[quartile3] - data[quartile1];
    }

    /**
     * Counts the number of values different of zero at the beginning of
     * an array.
     *
     * @param data the array of numbers
     * @return the number of values different of zero at the beginning of the array
     */
    public static int countNonZeroBeginning(final double... data) {
        int i = data.length - 1;
        while (i >= 0) {
            if (data[i--] != 0) {
                break;
            }
        }
        return i + 2;
    }

    /**
     * Gets the Local Regression (Loess) parameter estimates.
     *
     * @param y the dependent variable
     * @return the Loess parameter estimates
     */
    public static double[] getLoessParameterEstimates(final double... y) {
        final double[] x = createIndependentArray(y.length);
        return createWeightedLinearRegression(x, y, getTricubeWeights(y.length))
                .regress().getParameterEstimates();
    }

    /**
     * Creates an array representing the independent variable for
     * computing a linear regression.
     *
     * @param length the length of the array to create
     * @return
     */
    private static double[] createIndependentArray(final int length) {
        final double[] x = new double[length];
        for (int i = 0; i < length; i++) {
            x[i] = i + 1;
        }
        return x;
    }

    /**
     * Creates a a simple linear regression.
     * @param x the independent variable
     * @param y the dependent variable
     * @return
     */
    public static SimpleRegression createLinearRegression(final double[] x, final double[] y) {
        final SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < x.length; i++) {
            regression.addData(x[i], y[i]);
        }
        return regression;
    }

    /**
     * Creates a a multiple linear regression.
     * @param x the independent variable
     * @param y the dependent variable
     * @return
     */
    public static OLSMultipleLinearRegression createLinearRegression(final double[][] x, final double[] y)
    {
        final OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(y, x);
        return regression;
    }

    /**
     * Creates a a weighted linear regression.
     * @param x the independent variable
     * @param y the dependent variable
     * @param weights the weights to apply to x and y
     * @return
     */
    private static SimpleRegression createWeightedLinearRegression(
        final double[] x, final double[] y, final double[] weights)
    {
        final double[] weightedX = new double[x.length];
        final double[] weightedY = new double[y.length];

        final long numZeroWeights = Arrays.stream(weights).filter(weight -> weight <= 0).count();

        for (int i = 0; i < x.length; i++) {
            if (numZeroWeights >= 0.4 * weights.length) {
                // See: http://www.ncsu.edu/crsc/events/ugw07/Presentations/Crooks_Qiao/Crooks_Qiao_Alt_Presentation.pdf
                weightedX[i] = Math.sqrt(weights[i]) * x[i];
                weightedY[i] = Math.sqrt(weights[i]) * y[i];
            } else {
                weightedX[i] = x[i];
                weightedY[i] = y[i];
            }
        }

        return createLinearRegression(weightedX, weightedY);
    }

    /**
     * Gets the robust loess parameter estimates.
     *
     * @param y the dependent variable
     * @return the robust loess parameter estimates
     */
    public static double[] getRobustLoessParameterEstimates(final double... y) {
        final double[] x = createIndependentArray(y.length);
        final SimpleRegression tricubeRegression =
                createWeightedLinearRegression(x, y, getTricubeWeights(y.length));
        final double[] residuals = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            residuals[i] = y[i] - tricubeRegression.predict(x[i]);
        }
        final SimpleRegression tricubeBySqrRegression =
                createWeightedLinearRegression(x, y, getTricubeBisquareWeights(residuals));

        final double[] estimates = tricubeBySqrRegression.regress().getParameterEstimates();
        if (Double.isNaN(estimates[0]) || Double.isNaN(estimates[1])) {
            return tricubeRegression.regress().getParameterEstimates();
        }
        return estimates;
    }

    /**
     * Gets the tricube weigths.
     *
     * @param weightsNumber the number of weights
     * @return an array of tricube weigths with n elements
     */
    public static double[] getTricubeWeights(final int weightsNumber) {
        final double[] weights = new double[weightsNumber];
        final double top = weightsNumber - 1; //spread
        for (int i = 2; i < weightsNumber; i++) {
            final double k = Math.pow(1 - Math.pow((top - i) / top, 3), 3);
            weights[i] = k > 0 ? 1 / k : Double.MAX_VALUE;
        }

        weights[0] = weights[1] = weights[2];
        return weights;
    }

    /**
     * Gets the tricube bisquare weigths.
     *
     * @param residuals the residuals array
     * @return the tricube bisquare weigths
     */
    public static double[] getTricubeBisquareWeights(final double... residuals) {
        final double[] weights = getTricubeWeights(residuals.length);
        final double[] weights2 = new double[residuals.length];
        final double s6 = median(abs(residuals)) * 6;
        for (int i = 2; i < residuals.length; i++) {
            final double k = Math.pow(1 - Math.pow(residuals[i] / s6, 2), 2);
            weights2[i] = k > 0 ? (1 / k) * weights[i] : Double.MAX_VALUE;
        }

        weights2[0] = weights2[1] = weights2[2];
        return weights2;
    }

    /**
     * Gets the absolute values of an array of values
     *
     * @param data the array of values
     * @return a new array with the absolute value of each element in the given array.
     */
    public static double[] abs(final double... data) {
        final double[] result = new double[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Math.abs(data[i]);
        }
        return result;
    }

    /**
     * Converts a double value to an int, using an appropriate
     * rounding function.
     * If the double is negative, it applies {@link Math#floor(double)}
     * to round the number down. If it' a positive value, it
     * applies {@link Math#ceil(double)} to round the number up.
     * This way, a negative double will be converted to a negative int
     * and a positive double will be converted to a positive int.
     *
     * <p>It's different from using: {@link Math#round(double)} which always
     * rounds to the next positive integer; {@link Math#floor(double)} which
     * always rounds down; or {@link Math#ceil(double)} which always
     * rounds up. It applies floor for negative values and ceil
     * for positive ones.</p>
     *
     * <p>This method is useful to be used by {@link Comparator}s which
     * rely on a double attribute to compare a list of objects.
     * Since the {@link Comparator#compare(Object, Object)} method
     * must return an int, the method being implemented here
     * converts a double to an int value which can be used by
     * a Comparator.</p>
     *
     * @param value the double value to convert
     * @return zero if the double value is zero, a negative int if the double is negative,
     * or a positive int if the double is positive.
     */
    public static int doubleToInt(final double value){
        return (int)(value < 0 ? Math.floor(value) : Math.ceil(value));
    }

    /**
     * Try to convert a String to an int value.
     * If the conversion is not possible, returns a default value.
     * @param value the value to try converting
     * @param defaultValue the default value to return in case of error
     * @return the converted value or the default one in case of error
     */
    public static int parseInt(final String value, final int defaultValue){
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException e){
            return defaultValue;
        }
    }

    /**
     * Checks if two double numbers are equals, considering a precision error or 0.01.
     * That is, if the different between the two numbers are lower or equal to 0.01, they are considered equal.
     * @param first the first number to check
     * @param second the second number to check
     * @return true if the numbers are equal considering the precision error
     */
    public static boolean same(final double first, final double second){
        return same(first,second, 0.01);
    }

    /**
     * Checks if two double numbers are equals, considering a given precision error.
     * That is, if the different between the two numbers are lower or equal to the precision error, they are considered equal.
     * @param first the first number to check
     * @param second the second number to check
     * @param precisionError the precision error used to compare the numbers
     * @return true if the numbers are equal considering the precision error
     */
    public static boolean same(final double first, final double second, final double precisionError){
        return Math.abs(first-second) <= precisionError;
    }

    /**
     * Computes correlation coefficients for a set of data.
     *
     * @param data the data to compute the correlation coefficients
     * @return the correlation coefficients
     */
    public static List<Double> correlationCoefficients(final double[][] data) {
        final int rows = data.length;
        final int cols = data[0].length;
        final List<Double> correlationCoefficients = new LinkedList<>();
        for (int i = 0; i < rows; i++) {
            final double[][] x = new double[rows - 1][cols];
            int k = 0;
            for (int j = 0; j < rows; j++) {
                if (j != i) {
                    x[k++] = data[j];
                }
            }

            // Transpose the matrix so that it fits the linear model
            final double[][] xT = new Array2DRowRealMatrix(x).transpose().getData();

            // RSquare is the "coefficient of determination"
            correlationCoefficients.add(createLinearRegression(xT, data[i]).calculateRSquared());
        }

        return correlationCoefficients;
    }
}
