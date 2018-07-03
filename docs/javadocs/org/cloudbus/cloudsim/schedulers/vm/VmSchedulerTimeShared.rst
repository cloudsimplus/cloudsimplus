.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Map

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

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
VmSchedulerTimeShared
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSchedulerTimeShared()
   :outertype: VmSchedulerTimeShared

   Creates a time-shared VM scheduler.

VmSchedulerTimeShared
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSchedulerTimeShared(double vmMigrationCpuOverhead)
   :outertype: VmSchedulerTimeShared

   Creates a time-shared VM scheduler, defining a CPU overhead for VM migration.

   :param vmMigrationCpuOverhead: the percentage of Host's CPU usage increase when a VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).

Methods
-------
allocateMipsShareForVm
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void allocateMipsShareForVm(Vm vm, List<Double> requestedMipsReduced)
   :outertype: VmSchedulerTimeShared

   Performs the allocation of a MIPS List to a given VM. The actual MIPS to be allocated to the VM may be reduced if the VM is in migration, due to migration overhead.

   :param vm: the VM to allocate MIPS to
   :param requestedMipsReduced: the list of MIPS to allocate to the VM, after it being adjusted by the \ :java:ref:`getMipsShareRequestedReduced(Vm,List)`\  method.

   **See also:** :java:ref:`.getMipsShareRequestedReduced(Vm,List)`

allocatePesForVmInternal
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocatePesForVmInternal(Vm vm, List<Double> requestedMips)
   :outertype: VmSchedulerTimeShared

deallocatePesForAllVms
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForAllVms()
   :outertype: VmSchedulerTimeShared

   Releases PEs allocated to all the VMs.

deallocatePesFromVmInternal
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void deallocatePesFromVmInternal(Vm vm, int pesToRemove)
   :outertype: VmSchedulerTimeShared

getMipsShareToAllocate
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Double> getMipsShareToAllocate(Vm vm, List<Double> requestedMips)
   :outertype: VmSchedulerTimeShared

   Gets the actual MIPS that will be allocated to each vPE (Virtual PE), considering the VM migration status. If the VM is in migration, this will cause overhead, reducing the amount of MIPS allocated to the VM.

   :param vm: the VM requesting allocation of MIPS
   :param requestedMips: the list of MIPS requested for each vPE
   :return: the List of MIPS allocated to the VM

getMipsShareToAllocate
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Double> getMipsShareToAllocate(List<Double> requestedMips, double scalingFactor)
   :outertype: VmSchedulerTimeShared

   Gets the actual MIPS that will be allocated to each vPE (Virtual PE), considering the VM migration status. If the VM is in migration, this will cause overhead, reducing the amount of MIPS allocated to the VM.

   :param requestedMips: the list of MIPS requested for each vPE
   :param scalingFactor: the factor that will be used to reduce the amount of MIPS allocated to each vPE (which is a percentage value between [0 .. 1]) in case the VM is in migration
   :return: the List of MIPS allocated to the VM

isSuitableForVmInternal
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean isSuitableForVmInternal(Vm vm, List<Double> requestedMips, boolean showLog)
   :outertype: VmSchedulerTimeShared

