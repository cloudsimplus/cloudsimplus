.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.lists PeList

VmSchedulerAbstract
===================

.. java:package:: org.cloudbus.cloudsim.schedulers.vm
   :noindex:

.. java:type:: public abstract class VmSchedulerAbstract implements VmScheduler

   An abstract class for implementation of \ :java:ref:`VmScheduler`\ s.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

Constructors
------------
VmSchedulerAbstract
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSchedulerAbstract()
   :outertype: VmSchedulerAbstract

   Creates a VmScheduler.

Methods
-------
addVmMigratingIn
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addVmMigratingIn(Vm vm)
   :outertype: VmSchedulerAbstract

addVmMigratingOut
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addVmMigratingOut(Vm vm)
   :outertype: VmSchedulerAbstract

deallocatePesForAllVms
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForAllVms()
   :outertype: VmSchedulerAbstract

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: VmSchedulerAbstract

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

getMipsMapAllocated
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, List<Double>> getMipsMapAllocated()
   :outertype: VmSchedulerAbstract

   Gets the map of VMs to MIPS, were each key is a VM and each value is the currently allocated MIPS from the respective PE to that VM. The PEs where the MIPS capacity is get are defined in the \ :java:ref:`peMap`\ .

   :return: the mips map

getPeCapacity
^^^^^^^^^^^^^

.. java:method:: @Override public long getPeCapacity()
   :outertype: VmSchedulerAbstract

getPeList
^^^^^^^^^

.. java:method:: @Override public final List<Pe> getPeList()
   :outertype: VmSchedulerAbstract

getPeMap
^^^^^^^^

.. java:method:: @Override public Map<Vm, List<Pe>> getPeMap()
   :outertype: VmSchedulerAbstract

getPesAllocatedForVM
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Pe> getPesAllocatedForVM(Vm vm)
   :outertype: VmSchedulerAbstract

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: VmSchedulerAbstract

getVmsMigratingIn
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Vm> getVmsMigratingIn()
   :outertype: VmSchedulerAbstract

getVmsMigratingOut
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Vm> getVmsMigratingOut()
   :outertype: VmSchedulerAbstract

removeVmMigratingIn
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeVmMigratingIn(Vm vm)
   :outertype: VmSchedulerAbstract

removeVmMigratingOut
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeVmMigratingOut(Vm vm)
   :outertype: VmSchedulerAbstract

setAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: protected final void setAvailableMips(double availableMips)
   :outertype: VmSchedulerAbstract

   Sets the amount of mips that is free.

   :param availableMips: the new free mips amount

setHost
^^^^^^^

.. java:method:: @Override public VmScheduler setHost(Host host)
   :outertype: VmSchedulerAbstract

setMipsMapAllocated
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setMipsMapAllocated(Map<Vm, List<Double>> mipsMapAllocated)
   :outertype: VmSchedulerAbstract

   Sets the map of VMs to MIPS, were each key is a VM and each value is the currently allocated MIPS from the respective PE to that VM. The PEs where the MIPS capacity is get are defined in the \ :java:ref:`peMap`\ .

   :param mipsMapAllocated: the mips map

setPeMap
^^^^^^^^

.. java:method:: protected final void setPeMap(Map<Vm, List<Pe>> peMap)
   :outertype: VmSchedulerAbstract

   Sets the map of VMs to PEs, where each key is a VM and each value is a list of PEs allocated to that VM.

   :param peMap: the pe map

setVmsMigratingIn
^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setVmsMigratingIn(Set<Vm> vmsMigratingIn)
   :outertype: VmSchedulerAbstract

   Sets the vms migrating in.

   :param vmsMigratingIn: the new vms migrating in

setVmsMigratingOut
^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setVmsMigratingOut(Set<Vm> vmsMigratingOut)
   :outertype: VmSchedulerAbstract

   Sets the vms migrating out.

   :param vmsMigratingOut: the new vms migrating out

