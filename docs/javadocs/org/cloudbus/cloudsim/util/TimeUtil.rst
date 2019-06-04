TimeUtil
========

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public final class TimeUtil

   Utility class that provides some methods to deal with time units. It's not used the \ :java:ref:`java.time.Duration`\  and \ :java:ref:`java.time.Period`\  classes because they don't work with double type. Therefore, it's not possible for them to deal with time fractions, such as 2.5 hours.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`MathUtil`

Methods
-------
currentTimeSecs
^^^^^^^^^^^^^^^

.. java:method:: public static double currentTimeSecs()
   :outertype: TimeUtil

   Gets the computer actual time in seconds.

daysToSeconds
^^^^^^^^^^^^^

.. java:method:: public static double daysToSeconds(double days)
   :outertype: TimeUtil

   Converts a value in days to seconds.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param days: the value in days
   :return: the value in seconds

elapsedSeconds
^^^^^^^^^^^^^^

.. java:method:: public static double elapsedSeconds(double startTimeSeconds)
   :outertype: TimeUtil

   Gets the elapsed time from the given time in seconds.

   :param startTimeSeconds: the start time in seconds
   :return: the elapsed time in seconds

hoursToDays
^^^^^^^^^^^

.. java:method:: public static double hoursToDays(double hours)
   :outertype: TimeUtil

   Converts a value in hours to days.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param hours: the value in hours
   :return: the value in days

hoursToSeconds
^^^^^^^^^^^^^^

.. java:method:: public static double hoursToSeconds(double hours)
   :outertype: TimeUtil

   Converts a value in hours to seconds.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param hours: the value in hours
   :return: the value in seconds

microToMilli
^^^^^^^^^^^^

.. java:method:: public static double microToMilli(double micro)
   :outertype: TimeUtil

   Converts any value in micro (μ) to milli (m) scale, such as microseconds to milliseconds.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param micro: the value in micro (μ) scale
   :return: the value in milli (m) scale

microToSeconds
^^^^^^^^^^^^^^

.. java:method:: public static double microToSeconds(double micro)
   :outertype: TimeUtil

   Converts a value in microseconds (μ) to seconds.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param micro: the value in microseconds (μ)
   :return: the value in seconds

millisecsToMinutes
^^^^^^^^^^^^^^^^^^

.. java:method:: public static double millisecsToMinutes(long milli)
   :outertype: TimeUtil

   Converts a value in milliseconds to minutes.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param milli: the value in milliseconds
   :return: the value in minutes

minutesToSeconds
^^^^^^^^^^^^^^^^

.. java:method:: public static double minutesToSeconds(double minutes)
   :outertype: TimeUtil

   Converts a value in minutes to seconds.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param minutes: the value in minutes
   :return: the value in seconds

monthsToSeconds
^^^^^^^^^^^^^^^

.. java:method:: public static double monthsToSeconds(double months)
   :outertype: TimeUtil

   Converts a value in months to an \ **approximated**\  number of seconds, since it considers every month has 30 days.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\ , \ :java:ref:`java.time.Duration`\  and \ :java:ref:`java.time.Period`\  classes don't provide the double precision required here.

   :param months: the value in months
   :return: the value in seconds

secondsToDays
^^^^^^^^^^^^^

.. java:method:: public static double secondsToDays(double seconds)
   :outertype: TimeUtil

   Converts a value in seconds to days.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param seconds: the value in seconds
   :return: the value in days

secondsToHours
^^^^^^^^^^^^^^

.. java:method:: public static double secondsToHours(double seconds)
   :outertype: TimeUtil

   Converts a value in seconds to hours.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param seconds: the value in seconds
   :return: the value in hours

secondsToMinutes
^^^^^^^^^^^^^^^^

.. java:method:: public static double secondsToMinutes(double seconds)
   :outertype: TimeUtil

   Converts a value in seconds to minutes.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param seconds: the value in seconds
   :return: the value in minutes

secondsToStr
^^^^^^^^^^^^

.. java:method:: public static String secondsToStr(double seconds)
   :outertype: TimeUtil

   Converts a given amount of seconds to the most suitable unit, i.e., the highest unit that results in the lower converted value. For instance, if a value such as 80400 seconds is given, it will be converted to 1 day. It is not converted to hour, for instance, because it will return 24 (hours): a value which is higher than 1 (day).

   :param seconds: the number of seconds to convert to a suitable unit
   :return: a String containing the converted value followed by the name of the converted unit (e.g. "2.6 days")

