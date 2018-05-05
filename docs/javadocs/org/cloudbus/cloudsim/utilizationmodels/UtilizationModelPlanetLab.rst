.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

UtilizationModelPlanetLab
=========================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public class UtilizationModelPlanetLab extends UtilizationModelAbstract

   Defines the resource utilization model based on a \ `PlanetLab <https://www.planet-lab.org>`_\  Datacenter workload (trace) file.

Constructors
------------
UtilizationModelPlanetLab
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelPlanetLab(String workloadFilePath, double schedulingInterval) throws NumberFormatException
   :outertype: UtilizationModelPlanetLab

   Instantiates a new PlanetLab resource utilization model from a trace file.

   :param workloadFilePath: The path of a PlanetLab Datacenter workload file.
   :param schedulingInterval: the scheduling interval that defines the time interval in which precise utilization is be got
   :throws NumberFormatException: the number format exception

   **See also:** :java:ref:`.getSchedulingInterval()`

UtilizationModelPlanetLab
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelPlanetLab(String workloadFilePath, double schedulingInterval, int dataSamples) throws NumberFormatException
   :outertype: UtilizationModelPlanetLab

   Instantiates a new PlanetLab resource utilization model with variable utilization samples from a workload file.

   :param workloadFilePath: The path of a PlanetLab Datacenter workload file.
   :param schedulingInterval: the scheduling interval that defines the time interval in which precise utilization is be got
   :param dataSamples: number of samples to read from the workload file
   :throws NumberFormatException: the number format exception

   **See also:** :java:ref:`.setSchedulingInterval(double)`

Methods
-------
getInstance
^^^^^^^^^^^

.. java:method:: public static UtilizationModelPlanetLab getInstance(String traceFilePath, double schedulingInterval)
   :outertype: UtilizationModelPlanetLab

   Instantiates a new PlanetLab resource utilization model from a trace file inside the \ **application's resource directory**\ .

   :param traceFilePath: The \ **relative path**\  of a PlanetLab Datacenter trace file.
   :param schedulingInterval: the scheduling interval that defines the time interval in which precise utilization is be got
   :throws NumberFormatException: the number format exception

   **See also:** :java:ref:`.getSchedulingInterval()`

getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getSchedulingInterval()
   :outertype: UtilizationModelPlanetLab

   Gets the time interval (in seconds) in which precise utilization can be got from the workload file.

   That means if the \ :java:ref:`getUtilization(double)`\  is called passing any time that is multiple of this scheduling interval, the utilization returned will be the value stored for that specific time. Otherwise, the value will be an arithmetic mean of the beginning and the ending of the interval in which the given time is.

   :return: the scheduling interval in seconds

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

