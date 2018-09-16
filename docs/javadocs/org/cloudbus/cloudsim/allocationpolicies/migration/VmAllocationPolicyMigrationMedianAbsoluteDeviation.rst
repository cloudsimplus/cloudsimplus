.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: org.cloudbus.cloudsim.util MathUtil

VmAllocationPolicyMigrationMedianAbsoluteDeviation
==================================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.migration
   :noindex:

.. java:type:: public class VmAllocationPolicyMigrationMedianAbsoluteDeviation extends VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit

   A VM allocation policy that uses Median Absolute Deviation (MAD) to compute a dynamic threshold in order to detect host over utilization. \ **It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.**\

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
VmAllocationPolicyMigrationMedianAbsoluteDeviation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationMedianAbsoluteDeviation(PowerVmSelectionPolicy vmSelectionPolicy)
   :outertype: VmAllocationPolicyMigrationMedianAbsoluteDeviation

   Creates a VmAllocationPolicyMigrationMedianAbsoluteDeviation with a \ :java:ref:`safety parameter <getSafetyParameter()>`\  equals to 0 and no \ :java:ref:`fallback policy <getFallbackVmAllocationPolicy()>`\ .

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration

VmAllocationPolicyMigrationMedianAbsoluteDeviation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationMedianAbsoluteDeviation(PowerVmSelectionPolicy vmSelectionPolicy, double safetyParameter, VmAllocationPolicyMigration fallbackPolicy)
   :outertype: VmAllocationPolicyMigrationMedianAbsoluteDeviation

   Creates a VmAllocationPolicyMigrationMedianAbsoluteDeviation.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration
   :param safetyParameter: the safety parameter
   :param fallbackPolicy: the fallback VM allocation policy to be used when the over utilization host detection doesn't have data to be computed

Methods
-------
computeHostUtilizationMeasure
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double computeHostUtilizationMeasure(Host host) throws IllegalArgumentException
   :outertype: VmAllocationPolicyMigrationMedianAbsoluteDeviation

   Computes the host utilization MAD used for generating the host over utilization threshold.

   :param host: the host
   :throws {@inheritDoc}:
   :return: the host utilization MAD

