.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: java.util Objects

VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit
========================================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.migration
   :noindex:

.. java:type:: public abstract class VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit extends VmAllocationPolicyMigrationAbstract implements VmAllocationPolicyMigrationDynamicUpperThreshold

   An abstract class that is the base for implementation of VM allocation policies which use a dynamic over utilization threshold. \ **It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.**\  Such a behaviour can be overridden by sub-classes.

   :author: Manoel Campos da Silva Filho

Constructors
------------
VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit(PowerVmSelectionPolicy vmSelectionPolicy)
   :outertype: VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit

   Creates a VmAllocationPolicyMigrationDynamicUpperThreshold with a \ :java:ref:`safety parameter <getSafetyParameter()>`\  equals to 0 and no \ :java:ref:`fallback policy <getFallbackVmAllocationPolicy()>`\ .

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration

VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit(PowerVmSelectionPolicy vmSelectionPolicy, double safetyParameter, VmAllocationPolicyMigration fallbackVmAllocationPolicy)
   :outertype: VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit

   Creates a VmAllocationPolicyMigrationDynamicUpperThreshold.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration
   :param safetyParameter: the safety parameter
   :param fallbackVmAllocationPolicy: the fallback VM allocation policy to be used when the over utilization host detection doesn't have data to be computed

Methods
-------
getFallbackVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VmAllocationPolicyMigration getFallbackVmAllocationPolicy()
   :outertype: VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit

getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getOverUtilizationThreshold(Host host)
   :outertype: VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit

   Gets a dynamically computed Host over utilization threshold based on the Host CPU utilization history.

   :param host: {@inheritDoc}
   :return: {@inheritDoc} or \ :java:ref:`Double.MAX_VALUE`\  if the threshold could not be computed (for instance, because the Host doesn't have enought history to use)

   **See also:** :java:ref:`VmAllocationPolicyMigrationDynamicUpperThreshold.computeHostUtilizationMeasure(Host)`

getSafetyParameter
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSafetyParameter()
   :outertype: VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit

isHostOverloaded
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostOverloaded(Host host)
   :outertype: VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit

   Checks if a host is over utilized based on the CPU over utilization threshold computed using the statistical method defined in \ :java:ref:`computeHostUtilizationMeasure(Host)`\ .

   :param host: {@inheritDoc}
   :return: {@inheritDoc}

setFallbackVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setFallbackVmAllocationPolicy(VmAllocationPolicyMigration fallbackPolicy)
   :outertype: VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit

setSafetyParameter
^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setSafetyParameter(double safetyParameter)
   :outertype: VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit

   Sets the safety parameter.

   :param safetyParameter: the new safety parameter

