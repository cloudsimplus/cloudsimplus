.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicyAbstract

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.selectionpolicies.power PowerVmSelectionPolicy

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Collectors

.. java:import:: java.util.stream Stream

VmAllocationPolicyMigrationAbstract
===================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.migration
   :noindex:

.. java:type:: public abstract class VmAllocationPolicyMigrationAbstract extends VmAllocationPolicyAbstract implements VmAllocationPolicyMigration

   An abstract VM allocation policy that dynamically optimizes the VM allocation (placement) using migration. \ **It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.**\  Such a behaviour can be overridden by sub-classes.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
VmAllocationPolicyMigrationAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationAbstract(PowerVmSelectionPolicy vmSelectionPolicy)
   :outertype: VmAllocationPolicyMigrationAbstract

   Creates a VmAllocationPolicyMigrationAbstract.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration

VmAllocationPolicyMigrationAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyMigrationAbstract(PowerVmSelectionPolicy vmSelectionPolicy, BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicyMigrationAbstract

   Creates a new VmAllocationPolicy, changing the \ :java:ref:`Function`\  to select a Host for a Vm.

   :param vmSelectionPolicy: the policy that defines how VMs are selected for migration
   :param findHostForVmFunction: a \ :java:ref:`Function`\  to select a Host for a given Vm. Passing null makes the Function to be set as the default \ :java:ref:`findHostForVm(Vm)`\ .

   **See also:** :java:ref:`VmAllocationPolicy.setFindHostForVmFunction(java.util.function.BiFunction)`

Methods
-------
addHistoryEntryIfAbsent
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addHistoryEntryIfAbsent(Host host, double metric)
   :outertype: VmAllocationPolicyMigrationAbstract

   Adds an entry for each history map of a host if it doesn't contain an entry for the current simulation time.

   :param host: the host to add metric history entries
   :param metric: the metric to be added to the metric history map

findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public Optional<Host> findHostForVm(Vm vm)
   :outertype: VmAllocationPolicyMigrationAbstract

findHostForVm
^^^^^^^^^^^^^

.. java:method:: public Optional<Host> findHostForVm(Vm vm, Set<? extends Host> excludedHosts)
   :outertype: VmAllocationPolicyMigrationAbstract

   Finds a Host that has enough resources to place a given VM and that will not be overloaded after the placement. The selected Host will be that one with most efficient power usage for the given VM.

   This method performs the basic filtering and delegates additional ones and the final selection of the Host to other method.

   :param vm: the VM
   :param excludedHosts: the excluded hosts
   :return: an \ :java:ref:`Optional`\  containing a suitable Host to place the VM or an empty \ :java:ref:`Optional`\  if not found

   **See also:** :java:ref:`.findHostForVmInternal(Vm,Stream)`

findHostForVm
^^^^^^^^^^^^^

.. java:method:: public Optional<Host> findHostForVm(Vm vm, Set<? extends Host> excludedHosts, Predicate<Host> predicate)
   :outertype: VmAllocationPolicyMigrationAbstract

   Finds a Host that has enough resources to place a given VM and that will not be overloaded after the placement. The selected Host will be that one with most efficient power usage for the given VM.

   This method performs the basic filtering and delegates additional ones and the final selection of the Host to other method.

   :param vm: the VM
   :param excludedHosts: the excluded hosts
   :param predicate: an additional \ :java:ref:`Predicate`\  to be used to filter the Host to place the VM
   :return: an \ :java:ref:`Optional`\  containing a suitable Host to place the VM or an empty \ :java:ref:`Optional`\  if not found

   **See also:** :java:ref:`.findHostForVmInternal(Vm,Stream)`

findHostForVmInternal
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Optional<Host> findHostForVmInternal(Vm vm, Stream<Host> hostStream)
   :outertype: VmAllocationPolicyMigrationAbstract

   Applies additional filters to the Hosts Stream and performs the actual Host selection. This method is a Stream's final operation, that it, it closes the Stream and returns an \ :java:ref:`Optional`\  value.

   This method can be overridden by sub-classes to change the method used to select the Host for the given VM.

   :param vm: the VM to find a Host to be placed into
   :param hostStream: a \ :java:ref:`Stream`\  containing the Hosts after passing the basic filtering
   :return: an \ :java:ref:`Optional`\  containing a suitable Host to place the VM or an empty \ :java:ref:`Optional`\  if not found

   **See also:** :java:ref:`.findHostForVm(Vm,Set)`, :java:ref:`.additionalHostFilters(Vm,Stream)`

getMaxUtilizationAfterAllocation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getMaxUtilizationAfterAllocation(Host host, Vm vm)
   :outertype: VmAllocationPolicyMigrationAbstract

   Gets the max power consumption of a host after placement of a candidate VM. The VM is not in fact placed at the host. We assume that load is balanced between PEs. The only restriction is: VM's max MIPS < PE's MIPS

   :param host: the host
   :param vm: the vm
   :return: the power after allocation

getMetricHistory
^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getMetricHistory()
   :outertype: VmAllocationPolicyMigrationAbstract

getOptimizedAllocationMap
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList)
   :outertype: VmAllocationPolicyMigrationAbstract

getPowerAfterAllocation
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getPowerAfterAllocation(Host host, Vm vm)
   :outertype: VmAllocationPolicyMigrationAbstract

   Gets the power consumption of a host after the supposed placement of a candidate VM. The VM is not in fact placed at the host.

   :param host: the host to check the power consumption
   :param vm: the candidate vm
   :return: the host power consumption after the supposed VM placement or 0 if the power consumption could not be determined

getPowerAfterAllocationDifference
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getPowerAfterAllocationDifference(Host host, Vm vm)
   :outertype: VmAllocationPolicyMigrationAbstract

   Gets the power consumption different after the supposed placement of a VM into a given Host and the original Host power consumption.

   :param host: the host to check the power consumption
   :param vm: the candidate vm
   :return: the host power consumption different after the supposed VM placement or 0 if the power consumption could not be determined

getSwitchedOffHosts
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Host> getSwitchedOffHosts()
   :outertype: VmAllocationPolicyMigrationAbstract

   Gets the switched off hosts.

   :return: the switched off hosts

getTimeHistory
^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getTimeHistory()
   :outertype: VmAllocationPolicyMigrationAbstract

getUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUnderUtilizationThreshold()
   :outertype: VmAllocationPolicyMigrationAbstract

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getUtilizationHistory()
   :outertype: VmAllocationPolicyMigrationAbstract

getUtilizationOfCpuMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getUtilizationOfCpuMips(Host host)
   :outertype: VmAllocationPolicyMigrationAbstract

   Gets the utilization of the CPU in MIPS for the current potentially allocated VMs.

   :param host: the host
   :return: the utilization of the CPU in MIPS

getVmSelectionPolicy
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected PowerVmSelectionPolicy getVmSelectionPolicy()
   :outertype: VmAllocationPolicyMigrationAbstract

   Gets the vm selection policy.

   :return: the vm selection policy

getVmsToMigrateFromUnderUtilizedHost
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<? extends Vm> getVmsToMigrateFromUnderUtilizedHost(Host host)
   :outertype: VmAllocationPolicyMigrationAbstract

   Gets the VMs to migrate from under utilized host.

   :param host: the host
   :return: the vms to migrate from under utilized host

isHostOverloaded
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostOverloaded(Host host)
   :outertype: VmAllocationPolicyMigrationAbstract

   {@inheritDoc} It's based on current CPU usage.

   :param host: {@inheritDoc}
   :return: {@inheritDoc}

isHostUnderloaded
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostUnderloaded(Host host)
   :outertype: VmAllocationPolicyMigrationAbstract

   Checks if a host is under utilized, based on current CPU usage.

   :param host: the host
   :return: true, if the host is under utilized; false otherwise

notAllVmsAreMigratingOut
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean notAllVmsAreMigratingOut(Host host)
   :outertype: VmAllocationPolicyMigrationAbstract

   Checks if all VMs of a Host are \ **NOT**\  migrating out. In this case, the given Host will not be selected as an underloaded Host at the current moment. That is: not all VMs are migrating out if at least one VM isn't in migration process.

   :param host: the host to check
   :return: true if at least one VM isn't migrating, false if all VMs are migrating

setUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setUnderUtilizationThreshold(double underUtilizationThreshold)
   :outertype: VmAllocationPolicyMigrationAbstract

setVmSelectionPolicy
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setVmSelectionPolicy(PowerVmSelectionPolicy vmSelectionPolicy)
   :outertype: VmAllocationPolicyMigrationAbstract

   Sets the vm selection policy.

   :param vmSelectionPolicy: the new vm selection policy

