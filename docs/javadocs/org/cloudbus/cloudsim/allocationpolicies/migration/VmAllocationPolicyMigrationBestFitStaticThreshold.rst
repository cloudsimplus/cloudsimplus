.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Comparator

.. java:import:: java.util Optional

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

.. java:import:: java.util.stream Stream

VmAllocationPolicyMigrationBestFitStaticThreshold
=================================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.migration
   :noindex:

.. java:type:: public class VmAllocationPolicyMigrationBestFitStaticThreshold extends VmAllocationPolicyMigrationStaticThreshold

   A \ :java:ref:`VmAllocationPolicy`\  that uses a Static CPU utilization Threshold (THR) to detect host \ :java:ref:`under <getUnderUtilizationThreshold()>`\  and \ :java:ref:`getOverUtilizationThreshold(Host)`\  over} utilization.

   It's a \ **Best Fit policy**\  which selects the Host having the most used amount of CPU MIPS to place a given VM, \ **disregarding energy consumption**\ .

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
VmAllocationPolicyMigrationBestFitStaticThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationBestFitStaticThreshold(PowerVmSelectionPolicy vmSelectionPolicy, double overUtilizationThreshold)
   :outertype: VmAllocationPolicyMigrationBestFitStaticThreshold

VmAllocationPolicyMigrationBestFitStaticThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationBestFitStaticThreshold(PowerVmSelectionPolicy vmSelectionPolicy, double overUtilizationThreshold, BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicyMigrationBestFitStaticThreshold

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
   :outertype: VmAllocationPolicyMigrationBestFitStaticThreshold

   Gets the Host having the least available MIPS capacity (max used MIPS).

   This method is ignoring the additional filtering performed by the super class. This way, Host selection is performed ignoring energy consumption. However, all the basic filters defined in the super class are ensured, since this method is called just after they are applied.

   :param vm: {@inheritDoc}
   :param hostStream: {@inheritDoc}
   :return: {@inheritDoc}

