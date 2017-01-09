.. java:import:: java.util Comparator

.. java:import:: java.util List

.. java:import:: java.util Set

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts HostDynamicWorkload

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHostSimple

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

PowerVmAllocationPolicyMigrationWorstFitStaticThreshold
=======================================================

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public class PowerVmAllocationPolicyMigrationWorstFitStaticThreshold extends PowerVmAllocationPolicyMigrationStaticThreshold

   A \ :java:ref:`VmAllocationPolicy`\  that uses a Static CPU utilization Threshold (THR) to detect host \ :java:ref:`under <getUnderUtilizationThreshold()>`\  and \ :java:ref:`getOverUtilizationThreshold(PowerHost)`\  over} utilization. It selects as the host to place a VM, that one having the least used amount of CPU MIPS (Worst Fit policy), \ **disregarding energy consumption**\ .

   :author: Manoel Campos da Silva Filho

Constructors
------------
PowerVmAllocationPolicyMigrationWorstFitStaticThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmAllocationPolicyMigrationWorstFitStaticThreshold(PowerVmSelectionPolicy vmSelectionPolicy, double overUtilizationThreshold)
   :outertype: PowerVmAllocationPolicyMigrationWorstFitStaticThreshold

Methods
-------
findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts)
   :outertype: PowerVmAllocationPolicyMigrationWorstFitStaticThreshold

   Gets the first PM that has enough resources to host a given VM, which has the most available capacity and will not be overloaded after the placement.

   :param vm: The VM to find a host to
   :param excludedHosts: A list of hosts to be ignored
   :return: a PM to host the given VM or null if there isn't any suitable one.

getHostList
^^^^^^^^^^^

.. java:method:: @Override public <T extends Host> List<T> getHostList()
   :outertype: PowerVmAllocationPolicyMigrationWorstFitStaticThreshold

   Gets an ascending sorted list of hosts based on CPU utilization, providing a Worst Fit host allocation policy for VMs.

   :param <T>: The generic type.
   :return: The sorted list of hosts.

   **See also:** :java:ref:`.findHostForVm(Vm,java.util.Set)`

getUnderUtilizedHost
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected PowerHost getUnderUtilizedHost(Set<? extends Host> excludedHosts)
   :outertype: PowerVmAllocationPolicyMigrationWorstFitStaticThreshold

   Gets the first under utilized host based on the \ :java:ref:`getUnderUtilizationThreshold()`\ .

   :param excludedHosts: the list of hosts to ignore
   :return: the first under utilized host or null if there isn't any one

