.. java:import:: java.util Arrays

.. java:import:: java.util List

.. java:import:: org.apache.commons.math3.stat.descriptive DescriptiveStatistics

.. java:import:: org.apache.commons.math3.stat.regression OLSMultipleLinearRegression

.. java:import:: org.apache.commons.math3.stat.regression SimpleRegression

MathUtil
========

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public final class MathUtil

   A class containing multiple convenient math functions.

   :author: Anton Beloglazov

Fields
------
HUNDRED_PERCENT
^^^^^^^^^^^^^^^

.. java:field:: public static final double HUNDRED_PERCENT
   :outertype: MathUtil

   100% represented in scale [0 .. 1].

Methods
-------
abs
^^^

.. java:method:: public static double[] abs(double... data)
   :outertype: MathUtil

   Gets the absolute values of an array of values

   :param data: the array of values
   :return: a new array with the absolute value of each element in the given array.

countNonZeroBeginning
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static int countNonZeroBeginning(double... data)
   :outertype: MathUtil

   Counts the number of values different of zero at the beginning of an array.

   :param data: the array of numbers
   :return: the number of values different of zero at the beginning of the array

countShortestRow
^^^^^^^^^^^^^^^^

.. java:method:: public static int countShortestRow(double[][] data)
   :outertype: MathUtil

   Gets the length of the shortest row in a given matrix

   :param data: the data matrix
   :return: the length of the shortest row int he matrix

createLinearRegression
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static SimpleRegression createLinearRegression(double[] x, double[] y)
   :outertype: MathUtil

createLinearRegression
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static OLSMultipleLinearRegression createLinearRegression(double[][] x, double[] y)
   :outertype: MathUtil

createWeigthedLinearRegression
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static SimpleRegression createWeigthedLinearRegression(double[] x, double[] y, double[] weigths)
   :outertype: MathUtil

getLoessParameterEstimates
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static double[] getLoessParameterEstimates(double... y)
   :outertype: MathUtil

   Gets the Local Regression (Loess) parameter estimates.

   :param y: the y array
   :return: the Loess parameter estimates

getRobustLoessParameterEstimates
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static double[] getRobustLoessParameterEstimates(double... y)
   :outertype: MathUtil

   Gets the robust loess parameter estimates.

   :param y: the y array
   :return: the robust loess parameter estimates

getStatistics
^^^^^^^^^^^^^

.. java:method:: public static DescriptiveStatistics getStatistics(List<Double> list)
   :outertype: MathUtil

   Gets an object to compute descriptive statistics for an list of numbers.

   :param list: the list of numbers. Must not be null.
   :return: descriptive statistics for the list of numbers.

getStatistics
^^^^^^^^^^^^^

.. java:method:: public static DescriptiveStatistics getStatistics(double... list)
   :outertype: MathUtil

   Gets an object to compute descriptive statistics for an array of numbers.

   :param list: the array of numbers. Must not be null.
   :return: descriptive statistics for the array of numbers.

getTricubeBisquareWeights
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static double[] getTricubeBisquareWeights(double... residuals)
   :outertype: MathUtil

   Gets the tricube bisquare weigths.

   :param residuals: the residuals array
   :return: the tricube bisquare weigths

getTricubeWeights
^^^^^^^^^^^^^^^^^

.. java:method:: public static double[] getTricubeWeights(int n)
   :outertype: MathUtil

   Gets the tricube weigths.

   :param n: the number of weights
   :return: an array of tricube weigths with n elements

iqr
^^^

.. java:method:: public static double iqr(double... data)
   :outertype: MathUtil

   Gets the Interquartile Range (IQR) from an array of numbers.

   :param data: the array of numbers
   :return: the IQR

listToArray
^^^^^^^^^^^

.. java:method:: public static double[] listToArray(List<? extends Number> list)
   :outertype: MathUtil

   Converts a List to array.

   :param list: the list of numbers
   :return: the double[]

mad
^^^

.. java:method:: public static double mad(double... data)
   :outertype: MathUtil

   Gets the Median absolute deviation (MAD) from a array of numbers.

   :param data: the array of numbers
   :return: the mad

mean
^^^^

.. java:method:: public static double mean(List<Double> list)
   :outertype: MathUtil

   Gets the average from a list of numbers. If the list is empty or contains just zeros, returns 0.

   :param list: the list of numbers
   :return: the average

median
^^^^^^

.. java:method:: public static double median(List<Double> list)
   :outertype: MathUtil

   Gets the median from a list of numbers.

   :param list: the list of numbers
   :return: the median

median
^^^^^^

.. java:method:: public static double median(double... list)
   :outertype: MathUtil

   Gets the median from an array of numbers.

   :param list: the array of numbers
   :return: the median

stDev
^^^^^

.. java:method:: public static double stDev(List<Double> list)
   :outertype: MathUtil

   Gets the standard deviation from a list of numbers.

   :param list: the list of numbers
   :return: the standard deviation

sum
^^^

.. java:method:: public static double sum(List<? extends Number> list)
   :outertype: MathUtil

   Sums a list of numbers.

   :param list: the list of numbers
   :return: the double

trimZeroTail
^^^^^^^^^^^^

.. java:method:: public static double[] trimZeroTail(double... data)
   :outertype: MathUtil

   Trims zeros at the end of an array.

   :param data: the data array
   :return: the trimmed array

variance
^^^^^^^^

.. java:method:: public static double variance(List<Double> list)
   :outertype: MathUtil

   Gets the Variance from a list of numbers.

   :param list: the list of numbers
   :return: the variance

