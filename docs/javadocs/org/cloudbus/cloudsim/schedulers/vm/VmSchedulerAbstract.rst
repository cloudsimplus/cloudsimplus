.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util.stream IntStream

.. java:import:: java.util.stream LongStream

VmSchedulerAbstract
===================

.. java:package:: org.cloudbus.cloudsim.schedulers.vm
   :noindex:

.. java:type:: public abstract class VmSchedulerAbstract implements VmScheduler

   An abstract class for implementation of \ :java:ref:`VmScheduler`\ s.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
DEFAULT_VM_MIGRATION_CPU_OVERHEAD
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final double DEFAULT_VM_MIGRATION_CPU_OVERHEAD
   :outertype: VmSchedulerAbstract

   The default percentage to define the CPU overhead of VM migration if one is not explicitly set.

   **See also:** :java:ref:`.getVmMigrationCpuOverhead()`

Constructors
------------
VmSchedulerAbstract
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSchedulerAbstract(double vmMigrationCpuOverhead)
   :outertype: VmSchedulerAbstract

   Creates a VmScheduler, defining a CPU overhead for VM migration.

   :param vmMigrationCpuOverhead: the percentage of Host's CPU usage increase when a VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).

Methods
-------
allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final boolean allocatePesForVm(Vm vm)
   :outertype: VmSchedulerAbstract

allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final boolean allocatePesForVm(Vm vm, List<Double> requestedMips)
   :outertype: VmSchedulerAbstract

allocatePesForVmInternal
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract boolean allocatePesForVmInternal(Vm vm, List<Double> mipsShareRequested)
   :outertype: VmSchedulerAbstract

deallocatePesForAllVms
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForAllVms()
   :outertype: VmSchedulerAbstract

deallocatePesFromVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesFromVm(Vm vm)
   :outertype: VmSchedulerAbstract

deallocatePesFromVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesFromVm(Vm vm, int pesToRemove)
   :outertype: VmSchedulerAbstract

deallocatePesFromVmInternal
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void deallocatePesFromVmInternal(Vm vm, int pesToRemove)
   :outertype: VmSchedulerAbstract

getAllocatedMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getAllocatedMips(Vm vm)
   :outertype: VmSchedulerAbstract

getAllocatedMipsMap
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, List<Double>> getAllocatedMipsMap()
   :outertype: VmSchedulerAbstract

   Gets a map of MIPS allocated to each VM, were each key is a VM and each value is the List of currently allocated MIPS from the respective physical PEs which are being used by such a VM.

   When VM is in migration, the allocated MIPS in the source Host is reduced due to migration overhead, according to the \ :java:ref:`getVmMigrationCpuOverhead()`\ . This is a situation that the allocated MIPS will be lower than the requested MIPS.

   :return: the allocated MIPS map

   **See also:** :java:ref:`.getAllocatedMips(Vm)`, :java:ref:`.getRequestedMipsMap()`

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAvailableMips()
   :outertype: VmSchedulerAbstract

getHost
^^^^^^^

.. java:method:: @Override public Host getHost()
   :outertype: VmSchedulerAbstract

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxAvailableMips()
   :outertype: VmSchedulerAbstract

getMaxCpuUsagePercentDuringOutMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxCpuUsagePercentDuringOutMigration()
   :outertype: VmSchedulerAbstract

getMipsShareRequestedReduced
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Double> getMipsShareRequestedReduced(Vm vm, List<Double> mipsShareRequested)
   :outertype: VmSchedulerAbstract

   Gets an adjusted List of MIPS requested by a VM, reducing every MIPS which is higher than the \ :java:ref:`capacity of each physical PE <getPeCapacity()>`\  to that value.

   :param vm: the VM to get the MIPS requested
   :param mipsShareRequested: the VM requested MIPS List
   :return: the VM requested MIPS List without MIPS higher than the PE capacity.

getPeCapacity
^^^^^^^^^^^^^

.. java:method:: @Override public long getPeCapacity()
   :outertype: VmSchedulerAbstract

getRequestedMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getRequestedMips(Vm vm)
   :outertype: VmSchedulerAbstract

getRequestedMipsMap
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, List<Double>> getRequestedMipsMap()
   :outertype: VmSchedulerAbstract

   Gets a map of MIPS requested by each VM, where each key is a VM and each value is a list of MIPS requested by that VM. When a VM is going to be placed into a Host, its requested MIPS is a list where each element is the MIPS capacity of each VM \ :java:ref:`Pe`\  and the list size is the number of PEs.

   :return: the requested MIPS map

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: VmSchedulerAbstract

getVmMigrationCpuOverhead
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getVmMigrationCpuOverhead()
   :outertype: VmSchedulerAbstract

getWorkingPeList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final List<Pe> getWorkingPeList()
   :outertype: VmSchedulerAbstract

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public final boolean isSuitableForVm(Vm vm, boolean showLog)
   :outertype: VmSchedulerAbstract

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm, List<Double> requestedMips, boolean showLog)
   :outertype: VmSchedulerAbstract

isSuitableForVmInternal
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract boolean isSuitableForVmInternal(Vm vm, List<Double> requestedMips, boolean showLog)
   :outertype: VmSchedulerAbstract

percentOfMipsToRequest
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double percentOfMipsToRequest(Vm vm)
   :outertype: VmSchedulerAbstract

   Gets the percentage of the MIPS requested by a VM that will be in fact requested to the Host, according to the VM migration status:

   ..

   * VM is migrating out of this Host: the MIPS requested by VM will be reduced according to the \ :java:ref:`CPU migration overhead <getVmMigrationCpuOverhead()>`\ . The number of MIPS corresponding to the CPU overhead is used by the Host to perform the migration;
   * VM is migrating into this Host: only a fraction of its requested MIPS will be in fact requested to the Host. This amount is computed by reducing the \ :java:ref:`CPU migration overhead <getVmMigrationCpuOverhead()>`\ ;
   * VM is not in migration: 100% of its requested MIPS will be in fact requested to the Host

   :param vm: the VM that is requesting MIPS from the Host
   :return: the percentage of MIPS requested by the VM that will be in fact requested to the Host (in scale from [0 to 1], where is 100%)

removePesFromMap
^^^^^^^^^^^^^^^^

.. java:method:: protected <T> int removePesFromMap(Vm vm, Map<Vm, List<T>> map, int pesToRemove)
   :outertype: VmSchedulerAbstract

   Remove a given number of PEs from a given \ ``Vm -> List<PE>``\  Map, where each PE in the List associated to each Vm may be an actual \ :java:ref:`Pe`\  object or just its capacity in MIPS (Double).

   In other words, the map can be \ ``Map<Vm, List<Double>>``\  or \ ``Map<Vm, List<Pe>>``\ .

   :param <T>: the type of the elements into the List associated to each map key, which can be a MIPS number (Double) or an actual \ :java:ref:`Pe`\  object.
   :param vm: the VM to remove PEs from
   :param map: the map where the PEs will be removed
   :param pesToRemove: the number of PEs to remove from the List of PEs associated to the Vm
   :return: the number of removed PEs

setHost
^^^^^^^

.. java:method:: @Override public final VmScheduler setHost(Host host)
   :outertype: VmSchedulerAbstract

