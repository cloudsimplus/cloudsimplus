.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Resource

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

VmScheduler
===========

.. java:package:: org.cloudbus.cloudsim.schedulers.vm
   :noindex:

.. java:type:: public interface VmScheduler

   An interface that represents the policy used by a Virtual Machine Monitor (VMM) to share processing power of a PM among VMs running in a host. Each host has to use is own instance of a VmScheduler that will so schedule the allocation of host's PEs for VMs running on it.

   It also implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`VmScheduler.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`VmScheduler`\  variables.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  VmScheduler NULL
   :outertype: VmScheduler

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`VmScheduler`\  objects.

Methods
-------
allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method::  boolean allocatePesForVm(Vm vm, List<Double> requestedMips)
   :outertype: VmScheduler

   Requests the allocation of PEs for a VM.

   :param vm: the vm to allocate PEs to
   :param requestedMips: the list of MIPS share to be allocated to a VM
   :return: true if the PEs were allocated to the VM, false otherwise

allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method::  boolean allocatePesForVm(Vm vm)
   :outertype: VmScheduler

   Requests the allocation of PEs for a VM, according to the number of PEs and MIPS defined by VM attributes.

   :param vm: the vm to allocate PEs to
   :return: true if the PEs were allocated to the VM, false otherwise

deallocatePesForAllVms
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void deallocatePesForAllVms()
   :outertype: VmScheduler

   Releases PEs allocated to all the VMs of the host the VmScheduler is associated to. After that, all PEs will be available to be used on demand for requesting VMs.

deallocatePesFromVm
^^^^^^^^^^^^^^^^^^^

.. java:method::  void deallocatePesFromVm(Vm vm)
   :outertype: VmScheduler

   Releases all PEs allocated to a VM. After that, the PEs may be used on demand by other VMs.

   :param vm: the vm to deallocate PEs from

deallocatePesFromVm
^^^^^^^^^^^^^^^^^^^

.. java:method::  void deallocatePesFromVm(Vm vm, int pesToRemove)
   :outertype: VmScheduler

   Releases a given number of PEs from a VM. After that, the PEs may be used on demand by other VMs.

   :param vm: the vm to deallocate PEs from
   :param pesToRemove: number of PEs to deallocate

getAllocatedMips
^^^^^^^^^^^^^^^^

.. java:method::  List<Double> getAllocatedMips(Vm vm)
   :outertype: VmScheduler

   Gets the MIPS share of each host's Pe that is allocated to a given VM.

   :param vm: the vm to get the MIPS share

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method::  double getAvailableMips()
   :outertype: VmScheduler

   Gets the total amount of MIPS that is currently free. If there are VMs migrating into the Host, their requested MIPS will already be allocated, reducing the total available MIPS.

getHost
^^^^^^^

.. java:method::  Host getHost()
   :outertype: VmScheduler

   Gets the host that the VmScheduler get the list of PEs to allocate to VMs.

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method::  double getMaxAvailableMips()
   :outertype: VmScheduler

   Gets the maximum available MIPS among all the host's PEs.

getMaxCpuUsagePercentDuringOutMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getMaxCpuUsagePercentDuringOutMigration()
   :outertype: VmScheduler

   Gets the max percentage of CPU a VM migrating out of this Host can use. Since there may be an overhead associated to the migration process (if the \ :java:ref:`CPU overhead for VM migration <getVmMigrationCpuOverhead()>`\  is greater than 0), during the migration, the amount of MIPS the VM can use is reduced due to this overhead.

   :return: the max percentage of CPU usage during migration (in scale from [0 to 1], where 1 is 100%)

getPeCapacity
^^^^^^^^^^^^^

.. java:method::  long getPeCapacity()
   :outertype: VmScheduler

   Gets PE capacity in MIPS.

getRequestedMips
^^^^^^^^^^^^^^^^

.. java:method::  List<Double> getRequestedMips(Vm vm)
   :outertype: VmScheduler

   Gets a \ **copy**\  of the List of MIPS requested by a VM, avoiding the original list to be changed.

   :param vm: the VM to get the List of requested MIPS

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: VmScheduler

   Gets the actual total allocated MIPS for a VM along all its allocated PEs. If the VM is migrating into the Host, then just a fraction of the requested MIPS is actually allocated, representing the overhead of the migration process.

   The MIPS requested by the VM are just actually allocated after the migration is completed.

   :param vm: the VM to get the total allocated MIPS

   **See also:** :java:ref:`.getVmMigrationCpuOverhead()`

getVmMigrationCpuOverhead
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getVmMigrationCpuOverhead()
   :outertype: VmScheduler

   Defines the percentage of Host's CPU usage increase when a VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).

   :return: the Host's CPU migration overhead percentage.

getWorkingPeList
^^^^^^^^^^^^^^^^

.. java:method::  <T extends Pe> List<T> getWorkingPeList()
   :outertype: VmScheduler

   Gets the list of working PEs from the Host, \ **which excludes failed PEs**\ .

   :param <T>: the generic type

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForVm(Vm vm)
   :outertype: VmScheduler

   Checks if the PM using this scheduler has enough MIPS capacity to host a given VM.

   :param vm: the vm to check if there is enough available resource on the PM to host it
   :return: true, if it is possible to allocate the the VM into the host; false otherwise

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForVm(Vm vm, boolean showLog)
   :outertype: VmScheduler

   Checks if the PM using this scheduler has enough MIPS capacity to host a given VM.

   :param vm: the vm to check if there is enough available resource on the PM to host it
   :param showLog: if a log message should be printed when the Host isn't suitable for the given VM
   :return: true, if it is possible to allocate the the VM into the host; false otherwise

   **See also:** :java:ref:`.isSuitableForVm(Vm)`

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForVm(Vm vm, List<Double> requestedMips)
   :outertype: VmScheduler

   Checks if a list of MIPS requested by a VM is allowed to be allocated or not. Depending on the \ ``VmScheduler``\  implementation, the return value of this method may have different effects:

   ..

   * true: requested MIPS can be allocated, partial or totally;
   * false: requested MIPS cannot be allocated because there is no availability at all or there is just a partial amount of the requested MIPS available and the \ ``VmScheduler``\  implementation doesn't allow allocating less than the VM is requesting. If less than the required MIPS is allocated to a VM, it will cause performance degradation. Such situation defines an over-subscription situation which just specific \ ``VmSchedulers``\  accept.

   :param vm: the \ :java:ref:`Vm`\  to check if there are enough MIPS to allocate to
   :param requestedMips: a list of MIPS requested by a VM
   :return: true if the requested MIPS List is allowed to be allocated to the VM, false otherwise

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForVm(Vm vm, List<Double> requestedMips, boolean showLog)
   :outertype: VmScheduler

   Checks if a list of MIPS requested by a VM is allowed to be allocated or not.

   :param vm: the \ :java:ref:`Vm`\  to check if there are enough MIPS to allocate to
   :param requestedMips: a list of MIPS requested by a VM
   :param showLog: if a log message should be printed when the Host isn't suitable for the given VM
   :return: true if the requested MIPS List is allowed to be allocated to the VM, false otherwise

   **See also:** :java:ref:`.isSuitableForVm(Vm,List)`

setHost
^^^^^^^

.. java:method::  VmScheduler setHost(Host host)
   :outertype: VmScheduler

   Sets the host that the VmScheduler get the list of PEs to allocate to VMs. A host for the VmScheduler is set when the VmScheduler is set to a given host. Thus, the host is in charge to set itself to a VmScheduler.

   :param host: the host to be set
   :throws IllegalArgumentException: when the scheduler already is assigned to another Host, since each Host must have its own scheduler
   :throws NullPointerException: when the host parameter is null

