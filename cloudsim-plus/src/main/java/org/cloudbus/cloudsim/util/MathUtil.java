/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.util;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A class containing multiple convenient math functions.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
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
        return list.stream().mapToDouble(Number::doubleValue).sum();
    }

    /**
     * Gets the median from a list of numbers.
     *
     * @param list the list of numbers
     * @return the median
     */
    public static double median(final List<Double> list) {
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
    public static DescriptiveStatistics getStatistics(final List<Double> list) {
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
        return list.stream().mapToDouble(n->n).average().orElse(0);
    }

    /**
     * Gets the Variance from a list of numbers.
     *
     * @param list the list of numbers
     * @return the variance
     */
    public static double variance(final List<Double> list) {
        long n = 0;
        double mean = mean(list);
        double s = 0.0;

        for(final double x : list) {
            n++;
            final double delta = x - mean;
            mean += delta / n;
            s += delta * (x - mean);
        }
        // if you want to calculate std deviation
        // of a sample change this to (s/(n-1))
        return s / (n - 1);
    }

    /**
     * Gets the standard deviation from a list of numbers.
     *
     * @param list the list of numbers
     * @return the standard deviation
     */
    public static double stDev(final List<Double> list) {
        return Math.sqrt(variance(list));
    }

    /**
     * Gets the Median absolute deviation (MAD) from a array of numbers.
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
     * Gets the Interquartile Range (IQR) from an array of numbers.
     *
     * @param data the array of numbers
     * @return the IQR
     */
    public static double iqr(final double... data) {
        Arrays.sort(data);
        final int q1 = (int) Math.round(0.25 * (data.length + 1)) - 1;
        final int q3 = (int) Math.round(0.75 * (data.length + 1)) - 1;
        return data[q3] - data[q1];
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
     * @param y the y array
     * @return the Loess parameter estimates
     */
    public static double[] getLoessParameterEstimates(final double... y) {
        final double[] x = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            x[i] = i + 1;
        }

        return createWeigthedLinearRegression(x, y, getTricubeWeights(y.length))
                .regress().getParameterEstimates();
    }

    public static SimpleRegression createLinearRegression(final double[] x, final double[] y) {
        final SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < x.length; i++) {
            regression.addData(x[i], y[i]);
        }
        return regression;
    }

    public static OLSMultipleLinearRegression createLinearRegression(
        final double[][] x, final double[] y)
    {
        final OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(y, x);
        return regression;
    }

    private static SimpleRegression createWeigthedLinearRegression(
        final double[] x, final double[] y, final double[] weigths)
    {
        final double[] xW = new double[x.length];
        final double[] yW = new double[y.length];

        final long numZeroWeigths = Arrays.stream(weigths).filter(weigth -> weigth <= 0).count();

        for (int i = 0; i < x.length; i++) {
            if (numZeroWeigths >= 0.4 * weigths.length) {
                // See: http://www.ncsu.edu/crsc/events/ugw07/Presentations/Crooks_Qiao/Crooks_Qiao_Alt_Presentation.pdf
                xW[i] = Math.sqrt(weigths[i]) * x[i];
                yW[i] = Math.sqrt(weigths[i]) * y[i];
            } else {
                xW[i] = x[i];
                yW[i] = y[i];
            }
        }

        return createLinearRegression(xW, yW);
    }

    /**
     * Gets the robust loess parameter estimates.
     *
     * @param y the y array
     * @return the robust loess parameter estimates
     */
    public static double[] getRobustLoessParameterEstimates(final double... y) {
        final int n = y.length;
        final double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = i + 1;
        }
        final SimpleRegression tricubeRegression = createWeigthedLinearRegression(x,
            y, getTricubeWeights(n));
        final double[] residuals = new double[n];
        for (int i = 0; i < n; i++) {
            residuals[i] = y[i] - tricubeRegression.predict(x[i]);
        }
        final SimpleRegression tricubeBySqrRegression = createWeigthedLinearRegression(
            x, y, getTricubeBisquareWeights(residuals));

        final double[] estimates = tricubeBySqrRegression.regress()
            .getParameterEstimates();
        if (Double.isNaN(estimates[0]) || Double.isNaN(estimates[1])) {
            return tricubeRegression.regress().getParameterEstimates();
        }
        return estimates;
    }

    /**
     * Gets the tricube weigths.
     *
     * @param n the number of weights
     * @return an array of tricube weigths with n elements
     */
    public static double[] getTricubeWeights(final int n) {
        final double[] weights = new double[n];
        final double top = n - 1; //spread
        for (int i = 2; i < n; i++) {
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
        final int n = residuals.length;
        final double[] weights = getTricubeWeights(n);
        final double[] weights2 = new double[n];
        final double s6 = median(abs(residuals)) * 6;
        for (int i = 2; i < n; i++) {
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
     * @param d the double value to convert
     * @return zero if the double value is zero, a negative int if the double is negative,
     * or a positive int if the double is positive.
     */
    public static int doubleToInt(final double d){
        return (int)(d < 0 ? Math.floor(d) : Math.ceil(d));
    }

    /**
     * Checks if two double numbers are equals, considering a precision error or 0.01.
     * That is, if the different between the two numbers are lower or equal to 0.01, they are considered equal.
     * @param a the first number to check
     * @param b the second number to check
     * @return true if the numbers are equal considering the precision error
     */
    public static boolean same(final double a, final double b){
        return same(a,b, 0.01);
    }

    /**
     * Checks if two double numbers are equals, considering a given precision error.
     * That is, if the different between the two numbers are lower or equal to the precision error, they are considered equal.
     * @param a the first number to check
     * @param b the second number to check
     * @param precisionError the precision error used to compare the numbers
     * @return true if the numbers are equal considering the precision error
     */
    public static boolean same(final double a, final double b, final double precisionError){
        return Math.abs(a-b) <= precisionError;
    }

}
