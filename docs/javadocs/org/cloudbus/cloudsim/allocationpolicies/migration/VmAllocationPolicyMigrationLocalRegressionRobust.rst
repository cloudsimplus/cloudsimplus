.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: org.cloudbus.cloudsim.util MathUtil

VmAllocationPolicyMigrationLocalRegressionRobust
================================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.migration
   :noindex:

.. java:type:: public class VmAllocationPolicyMigrationLocalRegressionRobust extends VmAllocationPolicyMigrationLocalRegression

   A VM allocation policy that uses Local Regression Robust (LRR) to predict host utilization (load) and define if a host is overloaded or not. \ **It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.**\

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
VmAllocationPolicyMigrationLocalRegressionRobust
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationLocalRegressionRobust(PowerVmSelectionPolicy vmSelectionPolicy)
   :outertype: VmAllocationPolicyMigrationLocalRegressionRobust

   Creates a VmAllocationPolicyMigrationLocalRegressionRobust with a \ :java:ref:`safety parameter <getSafetyParameter()>`\  equals to 0 and no \ :java:ref:`fallback policy <getFallbackVmAllocationPolicy()>`\ .

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration

VmAllocationPolicyMigrationLocalRegressionRobust
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationLocalRegressionRobust(PowerVmSelectionPolicy vmSelectionPolicy, double safetyParameter, VmAllocationPolicyMigration fallbackVmAllocationPolicy)
   :outertype: VmAllocationPolicyMigrationLocalRegressionRobust

   Creates a VmAllocationPolicyMigrationLocalRegressionRobust.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration
   :param safetyParameter: the safety parameter
   :param fallbackVmAllocationPolicy: the fallback VM allocation policy to be used when the over utilization host detection doesn't have data to be computed

Methods
-------
getParameterEstimates
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected double[] getParameterEstimates(double[] reversedUsageHistory)
   :outertype: VmAllocationPolicyMigrationLocalRegressionRobust

   Gets the utilization estimates.

   :param reversedUsageHistory: the utilization history in reverse order
   :return: the utilization estimates

