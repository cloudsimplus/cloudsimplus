.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

PowerVmAllocationPolicyMigrationStaticThreshold
===============================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: public class PowerVmAllocationPolicyMigrationStaticThreshold extends PowerVmAllocationPolicyMigrationAbstract

   A VM allocation policy that uses a static CPU utilization threshold to detect host over utilization. \ **It's a First Fit policy which selects the first found Host with most efficient power usage to place a given VM.**\

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerVmAllocationPolicyMigrationStaticThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmAllocationPolicyMigrationStaticThreshold(PowerVmSelectionPolicy vmSelectionPolicy, double overUtilizationThreshold)
   :outertype: PowerVmAllocationPolicyMigrationStaticThreshold

   Creates a PowerVmAllocationPolicyMigrationStaticThreshold.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration
   :param overUtilizationThreshold: the over utilization threshold

Methods
-------
getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getOverUtilizationThreshold(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationStaticThreshold

   Gets the static host CPU utilization threshold to detect over utilization. It is a percentage value from 0 to 1 that can be changed when creating an instance of the class.

   This method always return the same over utilization threshold for any
   given host

   :param host: {@inheritDoc}
   :return: {@inheritDoc} (that is the same for any given host)

setOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final void setOverUtilizationThreshold(double overUtilizationThreshold)
   :outertype: PowerVmAllocationPolicyMigrationStaticThreshold

   Sets the static host CPU utilization threshold to detect over utilization.

   :param overUtilizationThreshold: the overUtilizationThreshold to set

