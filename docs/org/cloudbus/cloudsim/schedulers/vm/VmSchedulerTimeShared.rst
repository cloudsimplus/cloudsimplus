.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.lists PeList

VmSchedulerTimeShared
=====================

.. java:package:: org.cloudbus.cloudsim.schedulers.vm
   :noindex:

.. java:type:: public class VmSchedulerTimeShared extends VmSchedulerAbstract

   VmSchedulerTimeShared is a Virtual Machine Monitor (VMM), also called Hypervisor, that defines a policy to allocate one or more PEs from a PM to a VM, and allows sharing of PEs by multiple VMs. This class also implements 10% performance degradation due
   to VM migration. It does not support over-subscription.

   Each host has to use is own instance of a VmScheduler that will so schedule the allocation of host's PEs for VMs running on it.

   It does not perform a preemption process in order to move running VMs to the waiting list in order to make room for other already waiting VMs to run. It just imposes there is not waiting VMs, \ **oversimplifying**\  the scheduling, considering that for a given simulation second \ ``t``\ , the total processing capacity of the processor cores (in MIPS) is equally divided by the VMs that are using them.

   In processors enabled with \ `Hyper-threading technology (HT) <https://en.wikipedia.org/wiki/Hyper-threading>`_\ , it is possible to run up to 2 processes at the same physical CPU core. However, this scheduler implementation oversimplifies a possible HT feature by allowing several VMs to use a fraction of the MIPS capacity from physical PEs, until that the total capacity of the virtual PE is allocated. Consider that a virtual PE is requiring 1000 MIPS but there is no physical PE with such a capacity. The scheduler will allocate these 1000 MIPS across several physical PEs, for instance, by allocating 500 MIPS from PE 0, 300 from PE 1 and 200 from PE 2, totaling the 1000 MIPS required by the virtual PE.

   In a real hypervisor in a Host that has Hyper-threading CPU cores, two virtual PEs can be allocated to the same physical PE, but a single virtual PE must be allocated to just one physical PE.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

Constructors
------------
VmSchedulerTimeShared
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSchedulerTimeShared()
   :outertype: VmSchedulerTimeShared

   Creates a vm time-shared scheduler.

Methods
-------
allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShareRequested)
   :outertype: VmSchedulerTimeShared

deallocatePesForAllVms
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForAllVms()
   :outertype: VmSchedulerTimeShared

   Releases PEs allocated to all the VMs.

deallocatePesForVm
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForVm(Vm vm)
   :outertype: VmSchedulerTimeShared

getCpuOverheadDueToVmMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCpuOverheadDueToVmMigration()
   :outertype: VmSchedulerTimeShared

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxAvailableMips()
   :outertype: VmSchedulerTimeShared

   Returns maximum available MIPS among all the PEs. For the time shared policy it is just all the avaiable MIPS.

   :return: max mips

getMipsMapRequested
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, List<Double>> getMipsMapRequested()
   :outertype: VmSchedulerTimeShared

   Gets the map of mips requested by each VM, where each key is a VM and each value is a list of MIPS requested by that VM.

getPesInUse
^^^^^^^^^^^

.. java:method:: protected int getPesInUse()
   :outertype: VmSchedulerTimeShared

   Gets the number of PEs in use.

   :return: the pes in use

getTotalCapacityToBeAllocatedToVm
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getTotalCapacityToBeAllocatedToVm(List<Double> vmRequestedMipsShare)
   :outertype: VmSchedulerTimeShared

   Checks if the requested amount of MIPS is available to be allocated to a VM

   :param vmRequestedMipsShare: a VM's list of requested MIPS
   :return: the sum of total requested mips if there is enough capacity to be allocated to the VM, 0 otherwise.

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm)
   :outertype: VmSchedulerTimeShared

setMipsMapRequested
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setMipsMapRequested(Map<Vm, List<Double>> mipsMapRequested)
   :outertype: VmSchedulerTimeShared

   Sets the mips map requested.

   :param mipsMapRequested: the mips map requested

setPesInUse
^^^^^^^^^^^

.. java:method:: protected void setPesInUse(int pesInUse)
   :outertype: VmSchedulerTimeShared

   Sets the number of PEs in use.

   :param pesInUse: the new pes in use

updateMapOfRequestedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean updateMapOfRequestedMipsForVm(Vm vm, List<Double> mipsShareRequested)
   :outertype: VmSchedulerTimeShared

   Update the \ :java:ref:`getMipsMapRequested()`\  with the list of MIPS requested by a given VM.

   :param vm: the VM
   :param mipsShareRequested: the list of mips share requested by the vm
   :return: true if successful, false otherwise

