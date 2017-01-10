.. java:import:: java.util.stream Collectors

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts HostDynamicWorkload

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.lists VmList

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHostUtilizationHistory

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: org.cloudbus.cloudsim.vms.power PowerVm

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.util ExecutionTimeMeasurer

PowerVmAllocationPolicyMigrationAbstract
========================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: public abstract class PowerVmAllocationPolicyMigrationAbstract extends PowerVmAllocationPolicyAbstract implements PowerVmAllocationPolicyMigration

   An abstract power-aware VM allocation policy that dynamically optimizes the VM allocation (placement) using migration.

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

   Finds a PM that has enough resources to host a given VM and that will not be overloaded after placing the VM on it. The selected host will be that one with most efficient power usage for the given VM.

   :param vm: the VM
   :param excludedHosts: the excluded hosts
   :return: the PM found to host the VM or \ :java:ref:`PowerHost.NULL`\  if not found

getExecutionTimeHistoryHostSelection
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Double> getExecutionTimeHistoryHostSelection()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the execution time history host selection.

   :return: the execution time history host selection

getExecutionTimeHistoryTotal
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Double> getExecutionTimeHistoryTotal()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the execution time history total.

   :return: the execution time history total

getExecutionTimeHistoryVmReallocation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Double> getExecutionTimeHistoryVmReallocation()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the execution time history vm reallocation.

   :return: the execution time history vm reallocation

getExecutionTimeHistoryVmSelection
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Double> getExecutionTimeHistoryVmSelection()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the execution time history vm selection.

   :return: the execution time history vm selection

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

.. java:method:: public Map<Host, List<Double>> getMetricHistory()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the metric history.

   :return: the metric history

getMigrationMapFromUnderUtilizedHosts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Host> getMigrationMapFromUnderUtilizedHosts(List<PowerHostUtilizationHistory> overUtilizedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the migration map from under utilized hosts.

   :param overUtilizedHosts: the over utilized hosts
   :return: the migration map from under utilized hosts

getNewVmPlacement
^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Host> getNewVmPlacement(List<Vm> vmsToMigrate, Set<Host> excludedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets a new vm placement considering the list of VM to migrate.

   :param vmsToMigrate: the list of VMs to migrate
   :param excludedHosts: the list of hosts that aren't selected as destination hosts
   :return: the new vm placement map where each key is a Vm and each value is the host to place it.

getNewVmPlacementFromUnderUtilizedHost
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Host> getNewVmPlacementFromUnderUtilizedHost(List<? extends Vm> vmsToMigrate, Set<? extends Host> excludedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the new vm placement from under utilized host.

   :param vmsToMigrate: the list of VMs to migrate
   :param excludedHosts: the list of hosts that aren't selected as destination hosts
   :return: the new vm placement from under utilized host

getOverUtilizedHosts
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<PowerHostUtilizationHistory> getOverUtilizedHosts()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the over utilized hosts.

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

.. java:method:: public Map<Host, List<Double>> getTimeHistory()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the time history.

   :return: the time history

getUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUnderUtilizationThreshold()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

getUnderUtilizedHost
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected PowerHost getUnderUtilizedHost(Set<? extends Host> excludedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the most under utilized Host.

   :param excludedHosts: the Hosts that have to be disconsidering when looking for the under utilized Host
   :return: the most under utilized host or \ :java:ref:`PowerHost.NULL`\  if no Host was found

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Map<Host, List<Double>> getUtilizationHistory()
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the utilization history.

   :return: the utilization history

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

getVmsToMigrateFromHosts
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Vm> getVmsToMigrateFromHosts(List<PowerHostUtilizationHistory> overUtilizedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the VMs to migrate from hosts.

   :param overUtilizedHosts: the over utilized hosts
   :return: the VMs to migrate from hosts

getVmsToMigrateFromUnderUtilizedHost
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<? extends Vm> getVmsToMigrateFromUnderUtilizedHost(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Gets the VMs to migrate from under utilized host.

   :param host: the host
   :return: the vms to migrate from under utilized host

isHostNotOverusedAfterAllocation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isHostNotOverusedAfterAllocation(PowerHost host, Vm vm)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Checks if a host will be over utilized after placing of a candidate VM.

   :param host: the host to verify
   :param vm: the candidate vm
   :return: true, if the host will be over utilized after VM placement; false otherwise

isHostOverUtilized
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostOverUtilized(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Checks if a host is over utilized, based on current CPU usage.

   :param host: the host
   :return: true, if the host is over utilized; false otherwise

isHostUnderUtilized
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostUnderUtilized(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Checks if a host is under utilized, based on current CPU usage.

   :param host: the host
   :return: true, if the host is under utilized; false otherwise

isNotAllVmsMigratingOutNorVmsAreMigratingIn
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isNotAllVmsMigratingOutNorVmsAreMigratingIn(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Checks if all VMs of a Host are \ **NOT**\  migrating out nor there are VMs migrating in. If all VMs are migrating out or there is at least one VM migrating in, the given Host will not be selected as an underutilized Host at the current moment.

   :param host: the host to check

optimizeAllocation
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

printOverUtilizedHosts
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void printOverUtilizedHosts(List<PowerHostUtilizationHistory> overUtilizedHosts)
   :outertype: PowerVmAllocationPolicyMigrationAbstract

   Prints the over utilized hosts.

   :param overUtilizedHosts: the over utilized hosts

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

   Updates the list of maps between a VM and the host where it is place.

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

