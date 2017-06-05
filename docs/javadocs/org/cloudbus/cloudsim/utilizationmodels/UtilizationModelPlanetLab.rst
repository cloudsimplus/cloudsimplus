.. java:import:: java.io BufferedReader

.. java:import:: java.io FileReader

.. java:import:: java.io IOException

UtilizationModelPlanetLab
=========================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public class UtilizationModelPlanetLab extends UtilizationModelAbstract

   Defines the resource utilization model based on a \ `PlanetLab <https://www.planet-lab.org>`_\  Datacenter trace file.

Constructors
------------
UtilizationModelPlanetLab
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelPlanetLab(String inputPath, double schedulingInterval) throws NumberFormatException, IOException
   :outertype: UtilizationModelPlanetLab

   Instantiates a new PlanetLab resource utilization model from a trace file.

   :param inputPath: The path of a PlanetLab Datacenter trace file.
   :param schedulingInterval: the scheduling interval that defines the time interval in which precise utilization is be got
   :throws NumberFormatException: the number format exception
   :throws IOException: Signals that an I/O exception has occurred

   **See also:** :java:ref:`.getSchedulingInterval()`

UtilizationModelPlanetLab
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelPlanetLab(String inputPath, double schedulingInterval, int dataSamples) throws NumberFormatException, IOException
   :outertype: UtilizationModelPlanetLab

   Instantiates a new PlanetLab resource utilization model with variable data samples from a trace file.

   :param inputPath: The path of a PlanetLab Datacenter trace file.
   :param schedulingInterval: the scheduling interval that defines the time interval in which precise utilization is be got
   :param dataSamples: number of samples to read from the workload file
   :throws NumberFormatException: the number format exception
   :throws IOException: Signals that an I/O exception has occurred.

   **See also:** :java:ref:`.setSchedulingInterval(double)`

Methods
-------
getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getSchedulingInterval()
   :outertype: UtilizationModelPlanetLab

   Gets the scheduling interval that defines the time interval in which precise utilization is to be got.

   That means if the \ :java:ref:`getUtilization(double)`\  is called passing any time that is multiple of this scheduling interval, the utilization returned will be the value stored for that specific time. Otherwise, the value will be an arithmetic mean of the beginning and the ending of the interval in which the given time is.

   :return: the scheduling interval

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

