.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Optional

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

VmAllocationPolicyMigrationStaticThreshold
==========================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.migration
   :noindex:

.. java:type:: public class VmAllocationPolicyMigrationStaticThreshold extends VmAllocationPolicyMigrationAbstract

   A VM allocation policy that uses a static CPU utilization threshold to detect host over utilization. \ **It's a First Fit policy which selects the first found Host with most efficient power usage to place a given VM.**\

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
VmAllocationPolicyMigrationStaticThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationStaticThreshold(PowerVmSelectionPolicy vmSelectionPolicy, double overUtilizationThreshold)
   :outertype: VmAllocationPolicyMigrationStaticThreshold

   Creates a VmAllocationPolicyMigrationStaticThreshold.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration
   :param overUtilizationThreshold: the over utilization threshold

VmAllocationPolicyMigrationStaticThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationStaticThreshold(PowerVmSelectionPolicy vmSelectionPolicy, double overUtilizationThreshold, BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicyMigrationStaticThreshold

   Creates a new VmAllocationPolicy, changing the \ :java:ref:`Function`\  to select a Host for a Vm.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration
   :param overUtilizationThreshold: the over utilization threshold
   :param findHostForVmFunction: a \ :java:ref:`Function`\  to select a Host for a given Vm. Passing null makes the Function to be set as the default \ :java:ref:`findHostForVm(Vm)`\ .

   **See also:** :java:ref:`VmAllocationPolicy.setFindHostForVmFunction(java.util.function.BiFunction)`

Methods
-------
getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getOverUtilizationThreshold(Host host)
   :outertype: VmAllocationPolicyMigrationStaticThreshold

   Gets the static host CPU utilization threshold to detect over utilization. It is a percentage value from 0 to 1 that can be changed when creating an instance of the class.

   This implementation always returns the same over utilization threshold for any
   given host

   :param host: {@inheritDoc}
   :return: {@inheritDoc} (that is the same for any given host)

setOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final void setOverUtilizationThreshold(double overUtilizationThreshold)
   :outertype: VmAllocationPolicyMigrationStaticThreshold

   Sets the static host CPU utilization threshold to detect over utilization.

   :param overUtilizationThreshold: the overUtilizationThreshold to set

