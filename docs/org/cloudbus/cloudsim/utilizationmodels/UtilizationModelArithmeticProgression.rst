UtilizationModelArithmeticProgression
=====================================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public class UtilizationModelArithmeticProgression implements UtilizationModel

   An Cloudlet \ :java:ref:`UtilizationModel`\  that uses Arithmetic Progression to increases the utilization of the related resource along the simulation time.

   :author: Manoel Campos da Silva Filho

Fields
------
HUNDRED_PERCENT
^^^^^^^^^^^^^^^

.. java:field:: public static final double HUNDRED_PERCENT
   :outertype: UtilizationModelArithmeticProgression

   The value that represents 100%, taking a scale from 0 to 1.

ONE_PERCENT
^^^^^^^^^^^

.. java:field:: public static final double ONE_PERCENT
   :outertype: UtilizationModelArithmeticProgression

   The value that represents 1%, taking a scale from 0 to 1, where 1 is 100%.

Constructors
------------
UtilizationModelArithmeticProgression
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelArithmeticProgression()
   :outertype: UtilizationModelArithmeticProgression

UtilizationModelArithmeticProgression
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelArithmeticProgression(double utilizationPercentageIncrementPerSecond)
   :outertype: UtilizationModelArithmeticProgression

UtilizationModelArithmeticProgression
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelArithmeticProgression(double utilizationPercentageIncrementPerSecond, double currentUtilization)
   :outertype: UtilizationModelArithmeticProgression

   Instantiates a UtilizationModelProgressive that sets the \ :java:ref:`utilization increment <setUtilizationPercentageIncrementPerSecond(double)>`\  and the \ :java:ref:`initial utilization <setInitialUtilization(double)>`\

   :param utilizationPercentageIncrementPerSecond:
   :param currentUtilization:

Methods
-------
getInitialUtilization
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getInitialUtilization()
   :outertype: UtilizationModelArithmeticProgression

   Gets the initial percentage of resource that cloudlets using this UtilizationModel will require when they start to execute.

   :return: the initial utilization percentage (in scale is from 0 to 1, where 1 is 100%)

getMaxResourceUsagePercentage
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMaxResourceUsagePercentage()
   :outertype: UtilizationModelArithmeticProgression

   Gets the maximum percentage of resource of resource that will be used.

   :return: the maximum resource usage percentage (in scale from [0 to 1], where 1 is equals 100%)

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization(double time)
   :outertype: UtilizationModelArithmeticProgression

getUtilizationPercentageIncrementPerSecond
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getUtilizationPercentageIncrementPerSecond()
   :outertype: UtilizationModelArithmeticProgression

   Gets the utilization percentage to be incremented at the total utilization returned by \ :java:ref:`getUtilization(double)`\  at every simulation second.

   :return: the utilization percentage increment

   **See also:** :java:ref:`.setUtilizationPercentageIncrementPerSecond(double)`

setMaxResourceUsagePercentage
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setMaxResourceUsagePercentage(double maxResourceUsagePercentage)
   :outertype: UtilizationModelArithmeticProgression

   Sets the maximum percentage of resource of resource that will be used.

   :param maxResourceUsagePercentage: the maximum resource usage percentage (in scale from ]0 to 1], where 1 is equals 100%)

