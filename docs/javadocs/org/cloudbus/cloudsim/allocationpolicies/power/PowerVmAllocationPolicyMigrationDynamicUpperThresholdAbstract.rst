.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHostUtilizationHistory

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: java.util Objects

PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract
=============================================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: public abstract class PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract extends PowerVmAllocationPolicyMigrationAbstract implements PowerVmAllocationPolicyMigrationDynamicUpperThreshold

   An abstract class that is the base for implementation of Power-aware VM allocation policies which use a dynamic over utilization threshold. \ **It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.**\  Such a behaviour can be overridden by sub-classes.

   :author: Manoel Campos da Silva Filho

Constructors
------------
PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract(PowerVmSelectionPolicy vmSelectionPolicy)
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract

   Creates a PowerVmAllocationPolicyMigrationDynamicUpperThreshold with a \ :java:ref:`safety parameter <getSafetyParameter()>`\  equals to 0 and no \ :java:ref:`fallback policy <getFallbackVmAllocationPolicy()>`\ .

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration

PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract(PowerVmSelectionPolicy vmSelectionPolicy, double safetyParameter, PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy)
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract

   Creates a PowerVmAllocationPolicyMigrationDynamicUpperThreshold.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration
   :param safetyParameter: the safety parameter
   :param fallbackVmAllocationPolicy: the fallback VM allocation policy to be used when the over utilization host detection doesn't have data to be computed

Methods
-------
getFallbackVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public PowerVmAllocationPolicyMigration getFallbackVmAllocationPolicy()
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract

getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getOverUtilizationThreshold(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract

   Gets a dynamically computed Host over utilization threshold based on the Host CPU utilization history.

   :param host: {@inheritDoc}
   :return: {@inheritDoc} or \ :java:ref:`Double.MAX_VALUE`\  if the threshold could not be computed (for instance, because the Host doesn't have enought history to use)

   **See also:** :java:ref:`.computeHostUtilizationMeasure(PowerHostUtilizationHistory)`

getSafetyParameter
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSafetyParameter()
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract

isHostOverloaded
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostOverloaded(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract

   Checks if a host is over utilized based on the CPU over utilization threshold computed using the statistical method defined in \ :java:ref:`computeHostUtilizationMeasure(PowerHostUtilizationHistory)`\ .

   :param host: {@inheritDoc}
   :return: {@inheritDoc}

setFallbackVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setFallbackVmAllocationPolicy(PowerVmAllocationPolicyMigration fallbackPolicy)
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract

setSafetyParameter
^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setSafetyParameter(double safetyParameter)
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract

   Sets the safety parameter.

   :param safetyParameter: the new safety parameter

