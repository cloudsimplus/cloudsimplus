.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: java.io BufferedReader

.. java:import:: java.io IOException

.. java:import:: java.io InputStreamReader

.. java:import:: java.io UncheckedIOException

.. java:import:: java.util Objects

UtilizationModelPlanetLab
=========================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public class UtilizationModelPlanetLab extends UtilizationModelAbstract

   Defines a resource utilization model based on a \ `PlanetLab <https://www.planet-lab.org>`_\  Datacenter workload (trace) file.

   Each PlanetLab trace file available contains CPU utilization measured at every 5 minutes (300 seconds) inside PlanetLab VMs. This value in seconds is commonly used for the \ :java:ref:`scheduling interval <getSchedulingInterval()>`\  attribute when instantiating an object of this class.

Constructors
------------
UtilizationModelPlanetLab
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelPlanetLab(String workloadFilePath, double schedulingInterval) throws NumberFormatException
   :outertype: UtilizationModelPlanetLab

   Instantiates a new PlanetLab resource utilization model from a trace file.

   :param workloadFilePath: the path of a PlanetLab Datacenter workload file.
   :param schedulingInterval: the time interval in which precise utilization can be got from the file
   :throws NumberFormatException: the number format exception

   **See also:** :java:ref:`.getSchedulingInterval()`

UtilizationModelPlanetLab
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelPlanetLab(String workloadFilePath, double schedulingInterval, int dataSamples) throws NumberFormatException
   :outertype: UtilizationModelPlanetLab

   Instantiates a new PlanetLab resource utilization model with variable utilization samples from a workload file.

   :param workloadFilePath: the path of a PlanetLab Datacenter workload file.
   :param schedulingInterval: the time interval in which precise utilization can be got from the file
   :param dataSamples: number of samples to read from the workload file
   :throws NumberFormatException: the number format exception

   **See also:** :java:ref:`.getSchedulingInterval()`

Methods
-------
getInstance
^^^^^^^^^^^

.. java:method:: public static UtilizationModelPlanetLab getInstance(String traceFilePath, double schedulingInterval)
   :outertype: UtilizationModelPlanetLab

   Instantiates a new PlanetLab resource utilization model from a trace file inside the \ **application's resource directory**\ .

   :param traceFilePath: the \ **relative path**\  of a PlanetLab Datacenter trace file.
   :param schedulingInterval: the time interval in which precise utilization can be got from the file
   :throws NumberFormatException: the number format exception

   **See also:** :java:ref:`.getSchedulingInterval()`

getIntervalSize
^^^^^^^^^^^^^^^

.. java:method:: protected final int getIntervalSize(int startIndex, int endIndex)
   :outertype: UtilizationModelPlanetLab

   Gets the number of \ :java:ref:`utilization`\  samples between two indexes.

   Since the utilization array is implemented as a circular list, when the last index is read, it restarts from the first index again. Accordingly, we can have situations where the end index is the last array element and the start index is the first or some subsequent index. This way, computing the difference between the two indexes would return a negative value. The method ensures that a positive value is returned, correctly computing the size of the interval between the two indexes.

   Consider that the trace file has 288 lines, indexed from line 0 to 287. Think of the trace as a circular list with indexes 0, 1, 2, 3 ...... 286, 287, 0, 1, 2, 3 ... If the start index is 286 and the end index 2, then the interval size is 4 (the number of indexes between 286 and 2).

   :param startIndex: the start index in the interval
   :param endIndex: the end index in the interval
   :return: the number of samples inside such indexes interval

getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getSchedulingInterval()
   :outertype: UtilizationModelPlanetLab

   Gets the time interval (in seconds) in which precise utilization can be got from the workload file.

   That means if the \ :java:ref:`getUtilization(double)`\  is called passing any time that is multiple of this scheduling interval, the utilization returned will be the value stored for that specific time. Otherwise, the value will be an arithmetic mean of the beginning and the ending of the interval in which the given time is.

   :return: the scheduling interval in seconds

getSecondsInsideInterval
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final double getSecondsInsideInterval(int prevIndex, int nextIndex)
   :outertype: UtilizationModelPlanetLab

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization(double time)
   :outertype: UtilizationModelPlanetLab

setSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final void setSchedulingInterval(double schedulingInterval)
   :outertype: UtilizationModelPlanetLab

   Sets the scheduling interval.

   :param schedulingInterval: the scheduling interval to set

   **See also:** :java:ref:`.getSchedulingInterval()`

