.. java:import:: java.util HashMap

.. java:import:: java.util Map

ExecutionTimeMeasurer
=====================

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public final class ExecutionTimeMeasurer

   Measurement of execution times of CloudSim's methods.

   :author: Anton Beloglazov

Methods
-------
end
^^^

.. java:method:: public static double end(String name)
   :outertype: ExecutionTimeMeasurer

   Finalizes measuring the execution time of a method/process.

   :param name: the name of the method/process being measured.
   :return: the time the method/process spent in execution (in seconds)

   **See also:** :java:ref:`.getExecutionStartTimes()`

getExecutionStartTime
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected static Long getExecutionStartTime(String name)
   :outertype: ExecutionTimeMeasurer

getExecutionStartTimes
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected static Map<String, Long> getExecutionStartTimes()
   :outertype: ExecutionTimeMeasurer

   Gets map the execution times.

   :return: the execution times map

   **See also:** :java:ref:`.executionStartTimes`

start
^^^^^

.. java:method:: public static void start(String name)
   :outertype: ExecutionTimeMeasurer

   Starts measuring the execution time of a method/process. Usually this method has to be called at the first line of the method that has to be its execution time measured.

   :param name: the name of the method/process being measured.

   **See also:** :java:ref:`.getExecutionStartTimes()`

