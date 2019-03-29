.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.selectionpolicies VmSelectionPolicy

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
getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getOverUtilizationThreshold(Host host)
   :outertype: VmAllocationPolicyMigration

   Gets the host CPU utilization threshold to detect over utilization. It is a percentage value from 0 to 1. Whether it is a static or dynamically defined threshold depends on each implementing class.

   :param host: the host to get the over utilization threshold
   :return: the over utilization threshold

getUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getUnderUtilizationThreshold()
   :outertype: VmAllocationPolicyMigration

   Gets the percentage of total CPU utilization to indicate that a host is under used and its VMs have to be migrated.

   :return: the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)

getVmSelectionPolicy
^^^^^^^^^^^^^^^^^^^^

.. java:method::  VmSelectionPolicy getVmSelectionPolicy()
   :outertype: VmAllocationPolicyMigration

   Gets the the policy that defines how VMs are selected for migration.

   :return: the \ :java:ref:`VmSelectionPolicy`\ .

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

setVmSelectionPolicy
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setVmSelectionPolicy(VmSelectionPolicy vmSelectionPolicy)
   :outertype: VmAllocationPolicyMigration

   Sets the the policy that defines how VMs are selected for migration.

   :param vmSelectionPolicy: the new vm selection policy

