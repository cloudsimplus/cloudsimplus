.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Set

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.resources Resource

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

   A property that implements the Null Object Design Pattern for \ :java:ref:`VmScheduler`\  objects.

Methods
-------
addVmMigratingIn
^^^^^^^^^^^^^^^^

.. java:method::  boolean addVmMigratingIn(Vm vm)
   :outertype: VmScheduler

   Adds a \ :java:ref:`Vm`\  to the list of VMs migrating in.

   :param vm: the vm to be added
   :return: true if the VM wasn't into the list and was added, false otherwise

addVmMigratingOut
^^^^^^^^^^^^^^^^^

.. java:method::  boolean addVmMigratingOut(Vm vm)
   :outertype: VmScheduler

   Adds a \ :java:ref:`Vm`\  to the list of VMs migrating out.

   :param vm: the vm to be added
   :return: true if the VM wasn't into the list and was added, false otherwise

allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method::  boolean allocatePesForVm(Vm vm, List<Double> mipsShareRequested)
   :outertype: VmScheduler

   Requests the allocation of PEs for a VM.

   :param vm: the vm
   :param mipsShareRequested: the list of MIPS share to be allocated to a VM
   :return: $true if this policy allows a new VM in the host, $false otherwise

deallocatePesForAllVms
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void deallocatePesForAllVms()
   :outertype: VmScheduler

   Releases PEs allocated to all the VMs of the host the VmScheduler is associated to. After that, all PEs will be available to be used on demand for requesting VMs.

deallocatePesForVm
^^^^^^^^^^^^^^^^^^

.. java:method::  void deallocatePesForVm(Vm vm)
   :outertype: VmScheduler

   Releases PEs allocated to a VM. After that, the PEs may be used on demand by other VMs.

   :param vm: the vm

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: VmScheduler

   Gets the MIPS share of each host's Pe that is allocated to a given VM.

   :param vm: the vm to get the MIPS share

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method::  double getAvailableMips()
   :outertype: VmScheduler

   Gets the amount of MIPS that is free.

getCpuOverheadDueToVmMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getCpuOverheadDueToVmMigration()
   :outertype: VmScheduler

   Defines the percentage of Host's CPU usage increase when a VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).

   :return: the Host's CPU migration overhead percentage.

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

getPeCapacity
^^^^^^^^^^^^^

.. java:method::  long getPeCapacity()
   :outertype: VmScheduler

   Gets PE capacity in MIPS.

getPeList
^^^^^^^^^

.. java:method::  <T extends Pe> List<T> getPeList()
   :outertype: VmScheduler

   Gets the list of PEs from the Host.

   :param <T>: the generic type

getPeMap
^^^^^^^^

.. java:method::  Map<Vm, List<Pe>> getPeMap()
   :outertype: VmScheduler

   Gets the map of VMs to PEs, where each key is a VM UID and each value is a list of PEs allocated to that VM.

getPesAllocatedForVM
^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Pe> getPesAllocatedForVM(Vm vm)
   :outertype: VmScheduler

   Gets the list of PEs allocated for a VM.

   :param vm: the VM to get the allocated PEs

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: VmScheduler

   Gets the total allocated MIPS for a VM along all its allocated PEs.

   :param vm: the VM to get the total allocated MIPS

getVmsMigratingIn
^^^^^^^^^^^^^^^^^

.. java:method::  Set<Vm> getVmsMigratingIn()
   :outertype: VmScheduler

   Gets a \ **read-only**\  list of VMs migrating in.

getVmsMigratingOut
^^^^^^^^^^^^^^^^^^

.. java:method::  Set<Vm> getVmsMigratingOut()
   :outertype: VmScheduler

   Gets a \ **read-only**\  list of VMs migrating out.

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForVm(Vm vm)
   :outertype: VmScheduler

   Checks if the PM using this scheduler has enough MIPS capacity to host a given VM.

   :param vm: the vm to check if there is enough available resource on the PM to host it
   :return: true, if it is possible to allocate the the VM into the host; false otherwise

removeVmMigratingIn
^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeVmMigratingIn(Vm vm)
   :outertype: VmScheduler

   Adds a \ :java:ref:`Vm`\  to the list of VMs migrating in.

   :param vm: the vm to be added

removeVmMigratingOut
^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeVmMigratingOut(Vm vm)
   :outertype: VmScheduler

   Adds a \ :java:ref:`Vm`\  to the list of VMs migrating out.

   :param vm: the vm to be added

setHost
^^^^^^^

.. java:method::  VmScheduler setHost(Host host)
   :outertype: VmScheduler

   Sets the host that the VmScheduler get the list of PEs to allocate to VMs. A host for the VmScheduler is set when the VmScheduler is set to a given host. Thus, the host is in charge to set itself to a VmScheduler.

   :param host: the host to be set
   :throws NullPointerException: when the host parameter is null
   :throws IllegalArgumentException: when the scheduler already is assigned to another Host, since each Host must have its own scheduler

