.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: java.util List

.. java:import:: java.util Map

VmAllocationPolicyMigration
===========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.migration
   :noindex:

.. java:type:: public interface VmAllocationPolicyMigration extends VmAllocationPolicy

   An interface to be implemented by a VM allocation policy that detects \ :java:ref:`Host`\  under and over CPU utilization.

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  VmAllocationPolicyMigration NULL
   :outertype: VmAllocationPolicyMigration

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`VmAllocationPolicyMigration`\  objects.

Methods
-------
getMetricHistory
^^^^^^^^^^^^^^^^

.. java:method::  Map<Host, List<Double>> getMetricHistory()
   :outertype: VmAllocationPolicyMigration

   Gets a \ **read-only**\  map of metric history.

   :return: the metric history

getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getOverUtilizationThreshold(Host host)
   :outertype: VmAllocationPolicyMigration

   Gets the host CPU utilization threshold to detect over utilization. It is a percentage value from 0 to 1. Whether it is a static or dynamically defined threshold depends on each implementing class.

   :param host: the host to get the over utilization threshold
   :return: the over utilization threshold

getTimeHistory
^^^^^^^^^^^^^^

.. java:method::  Map<Host, List<Double>> getTimeHistory()
   :outertype: VmAllocationPolicyMigration

   Gets a \ **read-only**\  map of times when entries in each history list was added for each Host. All history lists are updated at the same time.

   :return: the time history

getUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getUnderUtilizationThreshold()
   :outertype: VmAllocationPolicyMigration

   Gets the percentage of total CPU utilization to indicate that a host is under used and its VMs have to be migrated.

   :return: the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Map<Host, List<Double>> getUtilizationHistory()
   :outertype: VmAllocationPolicyMigration

   Gets a \ **read-only**\  map of the utilization history for each Host.

   :return: the utilization history

isHostOverloaded
^^^^^^^^^^^^^^^^

.. java:method::  boolean isHostOverloaded(Host host)
   :outertype: VmAllocationPolicyMigration

   Checks if host is currently over utilized, according the the conditions defined by the Allocation Policy.

   :param host: the host to check
   :return: true, if the host is over utilized; false otherwise

isHostUnderloaded
^^^^^^^^^^^^^^^^^

.. java:method::  boolean isHostUnderloaded(Host host)
   :outertype: VmAllocationPolicyMigration

   Checks if host is currently under utilized, according the the conditions defined by the Allocation Policy.

   :param host: the host
   :return: true, if the host is under utilized; false otherwise

setUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setUnderUtilizationThreshold(double underUtilizationThreshold)
   :outertype: VmAllocationPolicyMigration

   Sets the percentage of total CPU utilization to indicate that a host is under used and its VMs have to be migrated.

   :param underUtilizationThreshold: the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)

