.. java:import:: org.apache.commons.math3.stat.descriptive DescriptiveStatistics

.. java:import:: org.apache.commons.math3.stat.regression OLSMultipleLinearRegression

.. java:import:: org.apache.commons.math3.stat.regression SimpleRegression

.. java:import:: java.util Arrays

.. java:import:: java.util Collection

.. java:import:: java.util Comparator

.. java:import:: java.util List

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

createLinearRegression
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static SimpleRegression createLinearRegression(double[] x, double[] y)
   :outertype: MathUtil

createLinearRegression
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static OLSMultipleLinearRegression createLinearRegression(double[][] x, double[] y)
   :outertype: MathUtil

doubleToInt
^^^^^^^^^^^

.. java:method:: public static int doubleToInt(double value)
   :outertype: MathUtil

   Converts a double value to an int, using an appropriate rounding function. If the double is negative, it applies \ :java:ref:`Math.floor(double)`\  to round the number down. If it' a positive value, it applies \ :java:ref:`Math.ceil(double)`\  to round the number up. This way, a negative double will be converted to a negative int and a positive double will be converted to a positive int.

   It's different from using: \ :java:ref:`Math.round(double)`\  which always rounds to the next positive integer; \ :java:ref:`Math.floor(double)`\  which always rounds down; or \ :java:ref:`Math.ceil(double)`\  which always rounds up. It applies floor for negative values and ceil for positive ones.

   This method is useful to be used by \ :java:ref:`Comparator`\ s which rely on a double attribute to compare a list of objects. Since the \ :java:ref:`Comparator.compare(Object,Object)`\  method must return an int, the method being implemented here converts a double to an int value which can be used by a Comparator.

   :param value: the double value to convert
   :return: zero if the double value is zero, a negative int if the double is negative, or a positive int if the double is positive.

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

.. java:method:: public static DescriptiveStatistics getStatistics(Collection<Double> list)
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

.. java:method:: public static double[] getTricubeWeights(int weightsNumber)
   :outertype: MathUtil

   Gets the tricube weigths.

   :param weightsNumber: the number of weights
   :return: an array of tricube weigths with n elements

iqr
^^^

.. java:method:: public static double iqr(double... data)
   :outertype: MathUtil

   Gets the \ `Interquartile Range (IQR) <https://en.wikipedia.org/wiki/Interquartile_range>`_\  from an array of numbers.

   :param data: the array of numbers
   :return: the IQR

mad
^^^

.. java:method:: public static double mad(double... data)
   :outertype: MathUtil

   Gets the Median Absolute Deviation (MAD) from a array of numbers.

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

.. java:method:: public static double median(Collection<Double> list)
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

same
^^^^

.. java:method:: public static boolean same(double first, double second)
   :outertype: MathUtil

   Checks if two double numbers are equals, considering a precision error or 0.01. That is, if the different between the two numbers are lower or equal to 0.01, they are considered equal.

   :param first: the first number to check
   :param second: the second number to check
   :return: true if the numbers are equal considering the precision error

same
^^^^

.. java:method:: public static boolean same(double first, double second, double precisionError)
   :outertype: MathUtil

   Checks if two double numbers are equals, considering a given precision error. That is, if the different between the two numbers are lower or equal to the precision error, they are considered equal.

   :param first: the first number to check
   :param second: the second number to check
   :param precisionError: the precision error used to compare the numbers
   :return: true if the numbers are equal considering the precision error

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

variance
^^^^^^^^

.. java:method:: public static double variance(List<Double> list)
   :outertype: MathUtil

   Gets the Variance from a list of numbers.

   :param list: the list of numbers
   :return: the variance

