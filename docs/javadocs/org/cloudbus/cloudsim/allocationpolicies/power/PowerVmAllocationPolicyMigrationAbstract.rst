.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Collectors

.. java:import:: java.util.stream Stream

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts HostDynamicWorkload

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.lists VmList

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHostUtilizationHistory

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.core Simulation

PowerVmAllocationPolicyMigrationAbstract
========================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: public abstract class PowerVmAllocationPolicyMigrationAbstract extends PowerVmAllocationPolicyAbstract implements PowerVmAllocationPolicyMigration

   An abstract power-aware VM allocation policy that dynamically optimizes the VM allocation (placement) using migration. \ **It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.**\  Such a behaviour can be overridden by sub-classes.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerVmAllocationPolicyMigrationAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmAllocationPolicyMigrationAbstract(PowerVmSelectionPolicy vmSelectionPolicy)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Creates a PowerVmAllocationPolicyMigrationAbstract.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration

Methods
-------
addHistoryEntryIfAbsent
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addHistoryEntryIfAbsent(PowerHost host, double metric)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Adds an entry for each history map of a host if it doesn't contain an entry for the current simulation time.

   :param host: the host to add metric history entries
   :param metric: the metric to be added to the metric history map

additionalHostFilters
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Stream<PowerHost> additionalHostFilters(Vm vm, Stream<PowerHost> hostStream)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Applies additional filters to select a Host to place a given VM. This implementation filters the stream of Hosts to get those ones that the placement of the VM impacts its power usage.

   This method can be overridden by sub-classes to change filtering.

   :param vm: the VM to find a Host to be placed into
   :param hostStream: a \ :java:ref:`Stream`\  containing the Hosts after passing the basic filtering
   :return: the Hosts \ :java:ref:`Stream`\  after applying the additional filters

extractHostListFromMigrationMap
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Host> extractHostListFromMigrationMap(Map<Vm, Host> migrationMap)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Extracts the host list from a migration map.

   :param migrationMap: the migration map
   :return: the list

findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public PowerHost findHostForVm(Vm vm)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

findHostForVm
^^^^^^^^^^^^^

.. java:method:: public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Finds a Host that has enough resources to place a given VM and that will not be overloaded after the placement. The selected Host will be that one with most efficient power usage for the given VM.

   This method performs the basic filtering and delegates additional ones and the final selection of the Host to other method.

   :param vm: the VM
   :param excludedHosts: the excluded hosts
   :return: the PM found to host the VM or \ :java:ref:`PowerHost.NULL`\  if not found

   **See also:** :java:ref:`.findHostForVmInternal(Vm,Stream)`

findHostForVm
^^^^^^^^^^^^^

.. java:method:: public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts, Predicate<PowerHost> predicate)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Finds a Host that has enough resources to place a given VM and that will not be overloaded after the placement. The selected Host will be that one with most efficient power usage for the given VM.

   This method performs the basic filtering and delegates additional ones and the final selection of the Host to other method.

   :param vm: the VM
   :param excludedHosts: the excluded hosts
   :param predicate: an additional \ :java:ref:`Predicate`\  to be used to filter the Host to place the VM
   :return: the PM found to host the VM or \ :java:ref:`PowerHost.NULL`\  if not found

   **See also:** :java:ref:`.findHostForVmInternal(Vm,Stream)`

findHostForVmInternal
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Optional<PowerHost> findHostForVmInternal(Vm vm, Stream<PowerHost> hostStream)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Applies additional filters to the Hosts Stream and performs the actual Host selection. This method is a Stream's final operation, that it, it closes the Stream and returns an \ :java:ref:`Optional`\  value.

   This method can be overridden by sub-classes to change the method used to select the Host for the given VM.

   :param vm: the VM to find a Host to be placed into
   :param hostStream: a \ :java:ref:`Stream`\  containing the Hosts after passing the basic filtering
   :return: an \ :java:ref:`Optional`\  that may or may not contain the Host to place the VM

   **See also:** :java:ref:`.findHostForVm(Vm,Set)`, :java:ref:`.additionalHostFilters(Vm,Stream)`

getMaxUtilizationAfterAllocation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getMaxUtilizationAfterAllocation(PowerHost host, Vm vm)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the max power consumption of a host after placement of a candidate VM. The VM is not in fact placed at the host. We assume that load is balanced between PEs. The only restriction is: VM's max MIPS < PE's MIPS

   :param host: the host
   :param vm: the vm
   :return: the power after allocation

getMetricHistory
^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getMetricHistory()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

getMigrationMapFromOverloadedHosts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Host> getMigrationMapFromOverloadedHosts(Set<PowerHostUtilizationHistory> overloadedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets a new VM placement considering the list of VM to migrate from overloaded Hosts.

   :param overloadedHosts: the list of overloaded Hosts
   :return: the new VM placement map where each key is a VM and each value is the Host to place it.

getNewVmPlacementFromUnderloadedHost
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Host> getNewVmPlacementFromUnderloadedHost(List<? extends Vm> vmsToMigrate, Set<? extends Host> excludedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets a new placement for VMs from an underloaded host.

   :param vmsToMigrate: the list of VMs to migrate from the underloaded Host
   :param excludedHosts: the list of hosts that aren't selected as destination hosts
   :return: the new vm placement for the given VMs

getOverloadedHosts
^^^^^^^^^^^^^^^^^^

.. java:method:: protected Set<PowerHostUtilizationHistory> getOverloadedHosts()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the List of overloaded hosts. If a Host is overloaded but it has VMs migrating out, then it's not included in the returned List because the VMs to be migrated to move the Host from the overload state already are in migration.

   :return: the over utilized hosts

getPowerAfterAllocation
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getPowerAfterAllocation(PowerHost host, Vm vm)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the power consumption of a host after the supposed placement of a candidate VM. The VM is not in fact placed at the host.

   :param host: the host to check the power consumption
   :param vm: the candidate vm
   :return: the host power consumption after the supposed VM placement or 0 if the power consumption could not be determined

getPowerAfterAllocationDifference
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getPowerAfterAllocationDifference(PowerHost host, Vm vm)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the power consumption different after the supposed placement of a VM into a given Host and the original Host power consumption.

   :param host: the host to check the power consumption
   :param vm: the candidate vm
   :return: the host power consumption different after the supposed VM placement or 0 if the power consumption could not be determined

getSavedAllocation
^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Host> getSavedAllocation()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the saved allocation.

   :return: the saved allocation

getSwitchedOffHosts
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<PowerHost> getSwitchedOffHosts()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the switched off hosts.

   :return: the switched off hosts

getTimeHistory
^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getTimeHistory()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

getUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUnderUtilizationThreshold()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getUtilizationHistory()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

getUtilizationOfCpuMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getUtilizationOfCpuMips(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the utilization of the CPU in MIPS for the current potentially allocated VMs.

   :param host: the host
   :return: the utilization of the CPU in MIPS

getVmSelectionPolicy
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected PowerVmSelectionPolicy getVmSelectionPolicy()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the vm selection policy.

   :return: the vm selection policy

getVmsToMigrateFromOverloadedHosts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Vm> getVmsToMigrateFromOverloadedHosts(Set<PowerHostUtilizationHistory> overloadedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the VMs to migrate from Hosts.

   :param overloadedHosts: the List of overloaded Hosts
   :return: the VMs to migrate from hosts

getVmsToMigrateFromUnderUtilizedHost
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<? extends Vm> getVmsToMigrateFromUnderUtilizedHost(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the VMs to migrate from under utilized host.

   :param host: the host
   :return: the vms to migrate from under utilized host

isHostOverloaded
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostOverloaded(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   {@inheritDoc} It's based on current CPU usage.

   :param host: {@inheritDoc}
   :return: {@inheritDoc}

isHostUnderloaded
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostUnderloaded(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Checks if a host is under utilized, based on current CPU usage.

   :param host: the host
   :return: true, if the host is under utilized; false otherwise

isNotAllVmsMigratingOut
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isNotAllVmsMigratingOut(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Checks if all VMs of a Host are \ **NOT**\  migrating out. In this case, the given Host will not be selected as an underloaded Host at the current moment.

   :param host: the host to check

isNotHostOverloadedAfterAllocation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isNotHostOverloadedAfterAllocation(PowerHost host, Vm vm)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Checks if a host will be over utilized after placing of a candidate VM.

   :param host: the host to verify
   :param vm: the candidate vm
   :return: true, if the host will be over utilized after VM placement; false otherwise

optimizeAllocation
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

restoreAllocation
^^^^^^^^^^^^^^^^^

.. java:method:: protected void restoreAllocation()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Restore VM allocation from the allocation history.

   **See also:** :java:ref:`.savedAllocation`

saveAllocation
^^^^^^^^^^^^^^

.. java:method:: protected void saveAllocation()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Saves the current map between a VM and the host where it is place.

   **See also:** :java:ref:`.savedAllocation`

setUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setUnderUtilizationThreshold(double underUtilizationThreshold)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

setVmSelectionPolicy
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setVmSelectionPolicy(PowerVmSelectionPolicy vmSelectionPolicy)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Sets the vm selection policy.

   :param vmSelectionPolicy: the new vm selection policy

