.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: java.util List

.. java:import:: java.util Map

PowerVmAllocationPolicyMigration
================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: public interface PowerVmAllocationPolicyMigration extends PowerVmAllocationPolicy

   An interface to be implemented by VM allocation policy for power-aware VMs that detects \ :java:ref:`PowerHost`\  under and over CPU utilization.

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  PowerVmAllocationPolicyMigration NULL
   :outertype: PowerVmAllocationPolicyMigration

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`PowerVmAllocationPolicyMigration`\  objects.

Methods
-------
getMetricHistory
^^^^^^^^^^^^^^^^

.. java:method::  Map<Host, List<Double>> getMetricHistory()
   :outertype: PowerVmAllocationPolicyMigration

   Gets a \ **read-only**\  map of metric history.

   :return: the metric history

getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getOverUtilizationThreshold(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigration

   Gets the host CPU utilization threshold to detect over utilization. It is a percentage value from 0 to 1. Whether it is a static or dynamically defined threshold depends on each implementing class.

   :param host: the host to get the over utilization threshold
   :return: the over utilization threshold

getTimeHistory
^^^^^^^^^^^^^^

.. java:method::  Map<Host, List<Double>> getTimeHistory()
   :outertype: PowerVmAllocationPolicyMigration

   Gets a \ **read-only**\  map of times when entries in each history list was added for each Host. All history lists are updated at the same time.

   :return: the time history

getUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getUnderUtilizationThreshold()
   :outertype: PowerVmAllocationPolicyMigration

   Gets the percentage of total CPU utilization to indicate that a host is under used and its VMs have to be migrated.

   :return: the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Map<Host, List<Double>> getUtilizationHistory()
   :outertype: PowerVmAllocationPolicyMigration

   Gets a \ **read-only**\  map of the utilization history for each Host.

   :return: the utilization history

isHostOverloaded
^^^^^^^^^^^^^^^^

.. java:method::  boolean isHostOverloaded(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigration

   Checks if host is over utilized.

   :param host: the host to check
   :return: true, if the host is over utilized; false otherwise

isHostUnderloaded
^^^^^^^^^^^^^^^^^

.. java:method::  boolean isHostUnderloaded(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigration

   Checks if host is under utilized.

   :param host: the host
   :return: true, if the host is under utilized; false otherwise

setUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setUnderUtilizationThreshold(double underUtilizationThreshold)
   :outertype: PowerVmAllocationPolicyMigration

   Sets the percentage of total CPU utilization to indicate that a host is under used and its VMs have to be migrated.

   :param underUtilizationThreshold: the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)

