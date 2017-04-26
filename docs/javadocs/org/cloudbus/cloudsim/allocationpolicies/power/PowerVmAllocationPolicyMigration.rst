.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

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
getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getOverUtilizationThreshold(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigration

   Gets the host CPU utilization threshold to detect over utilization. It is a percentage value from 0 to 1. Whether it is a static or dynamically defined threshold depends on each implementing class.

   :param host: the host to get the over utilization threshold
   :return: the over utilization threshold

getUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getUnderUtilizationThreshold()
   :outertype: PowerVmAllocationPolicyMigration

   Gets the percentage of total CPU utilization to indicate that a host is under used and its VMs have to be migrated.

   :return: the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)

isHostOverUtilized
^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isHostOverUtilized(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigration

   Checks if host is over utilized.

   :param host: the host
   :return: true, if the host is over utilized; false otherwise

isHostUnderUtilized
^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isHostUnderUtilized(PowerHost host)
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

