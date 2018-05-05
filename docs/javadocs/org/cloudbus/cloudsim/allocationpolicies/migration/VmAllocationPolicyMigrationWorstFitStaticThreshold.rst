.. java:import:: java.util Comparator

.. java:import:: java.util Optional

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

.. java:import:: java.util.stream Stream

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

VmAllocationPolicyMigrationWorstFitStaticThreshold
==================================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.migration
   :noindex:

.. java:type:: public class VmAllocationPolicyMigrationWorstFitStaticThreshold extends VmAllocationPolicyMigrationStaticThreshold

   A \ :java:ref:`VmAllocationPolicy`\  that uses a Static CPU utilization Threshold (THR) to detect host \ :java:ref:`under <getUnderUtilizationThreshold()>`\  and \ :java:ref:`getOverUtilizationThreshold(Host)`\  over} utilization.

   It's a \ **Worst Fit policy**\  which selects the Host having the least used amount of CPU MIPS to place a given VM, \ **disregarding energy consumption**\ .

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
VmAllocationPolicyMigrationWorstFitStaticThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationWorstFitStaticThreshold(PowerVmSelectionPolicy vmSelectionPolicy, double overUtilizationThreshold)
   :outertype: VmAllocationPolicyMigrationWorstFitStaticThreshold

VmAllocationPolicyMigrationWorstFitStaticThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationWorstFitStaticThreshold(PowerVmSelectionPolicy vmSelectionPolicy, double overUtilizationThreshold, BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicyMigrationWorstFitStaticThreshold

   Creates a new VmAllocationPolicy, changing the \ :java:ref:`Function`\  to select a Host for a Vm.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration
   :param overUtilizationThreshold: the over utilization threshold
   :param findHostForVmFunction: a \ :java:ref:`Function`\  to select a Host for a given Vm. Passing null makes the Function to be set as the default \ :java:ref:`findHostForVm(Vm)`\ .

   **See also:** :java:ref:`VmAllocationPolicy.setFindHostForVmFunction(java.util.function.BiFunction)`

Methods
-------
findHostForVmInternal
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected Optional<Host> findHostForVmInternal(Vm vm, Stream<Host> hostStream)
   :outertype: VmAllocationPolicyMigrationWorstFitStaticThreshold

   Gets the Host having the most available MIPS capacity (min used MIPS).

   This method is ignoring the additional filtering performed by the super class. This way, Host selection is performed ignoring energy consumption. However, all the basic filters defined in the super class are ensured, since this method is called just after they are applied.

   :param vm: {@inheritDoc}
   :param hostStream: {@inheritDoc}
   :return: {@inheritDoc}

