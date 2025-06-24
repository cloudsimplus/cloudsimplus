/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.util;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * An utility class containing multiple convenient math functions.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 * @see TimeUtil
 * @see BytesConversion
 */
public final class MathUtil {
    /**
     * A private constructor to avoid class instantiation.
     */
    private MathUtil(){/**/}

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
     * Gets an object to compute descriptive statistics for a list of numbers.
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

    /// Gets the [Median Absolute Deviation (MAD)](https://en.wikipedia.org/wiki/Median_absolute_deviation)
    /// from an array of numbers.
    ///
    /// @param data the array of numbers
    /// @return the mad
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
     * Gets the [Inter-quartile Range (IQR)](https://en.wikipedia.org/wiki/Interquartile_range
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
     * Counts the number of values different of zero at the beginning of an array.
     *
     * @param data the array of numbers
     * @return the number of values different of zero at the beginning of the array
     */
    public static int countNonZeroBeginning(final double... data) {
        int index = data.length - 1;
        while (index >= 0) {
            if (data[index--] != 0) {
                break;
            }
        }
        return index + 2;
    }

    /**
     * Gets the absolute values from an array of values
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
     * This way, a negative double will be converted to a negative int,
     * and a positive double will be converted to a positive int.
     *
     * <p>It's different from using: {@link Math#round(double)} which always
     * rounds to the next positive integer; {@link Math#floor(double)} which
     * always rounds down; or {@link Math#ceil(double)} which always
     * rounds up. It applies the floor for negative values and ceil
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
     * @return true if the numbers are equal considering the precision error, false otherwise
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
     * @return true if the numbers are equal considering the precision error, false otherwise
     */
    public static boolean same(final double first, final double second, final double precisionError){
        return Math.abs(first-second) <= precisionError;
    }

    /**
     * Computes the percentage of a current value related to a total value.
     * @param partial the partial value to compute the percentage
     * @param total the total value that represents 100%
     * @return the percentage of the current value in scale from 0 to 100%
     */
    public static double percentValue(final double partial, final double total){
        return (partial/total)*100.0;
    }

    /**
     * Checks if the given number is a percentage between [0 and 1].
     * @param value the value to check
     * @param fieldName the name of the field to validate (used in a possible validation error message)
     * @return the given value
     * @throws IllegalArgumentException if the value is not between [0 and 1]
     */
    public static double percentage(final double value, final String fieldName) {
        if(value < 0 || value > 1)
            throw new IllegalArgumentException(fieldName + "  must be between [0 and 1].");

        return value;
    }

    /**
     * {@return the first positive long value} given.
     * @param first the first value to check
     * @param second the first value to check
     */
    public static long positive(final long first, final long second){
        //Overloaded methods just change the types to avoid boxing
        return first > 0 ? first : second;
    }

    /**
     * {@return the first positive double value} given.
     * @param first the first value to check
     * @param second the first value to check
     */
    public static double positive(final double first, final double second){
        // Overloaded methods just change the types to avoid boxing
        return first > 0 ? first : second;
    }

    /**
     * Checks if the given int value is not negative.
     * @param value the value to check
     * @param fieldName the name of the field to validate (used in a possible validation error message)
     * @return the given value
     * @throws IllegalArgumentException if the value is negative
     */
    public static int nonNegative(final int value, final String fieldName) {
        nonNegative((double)value, fieldName);
        return value;
    }

    /**
     * Checks if the given long value is not negative.
     * @param value the value to check
     * @param fieldName the name of the field to validate (used in a possible validation error message)
     * @return the given value
     * @throws IllegalArgumentException if the value is negative
     */
    public static long nonNegative(final long value, final String fieldName) {
        nonNegative((double)value, fieldName);
        return value;
    }

    /**
     * Checks if the given double value is not negative.
     * @param value the value to check
     * @param fieldName the name of the field to validate (used in a possible validation error message)
     * @return the given value
     * @throws IllegalArgumentException if the value is negative
     */
    public static double nonNegative(final double value, final String fieldName) {
        if (value < 0)
            throw new IllegalArgumentException(fieldName + " cannot be negative.");

        return value;
    }
}
