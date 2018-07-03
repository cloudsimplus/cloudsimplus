.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Map.Entry

VmSchedulerTimeSharedOverSubscription
=====================================

.. java:package:: org.cloudbus.cloudsim.schedulers.vm
   :noindex:

.. java:type:: public class VmSchedulerTimeSharedOverSubscription extends VmSchedulerTimeShared

   A Time-Shared VM Scheduler which allows over-subscription. In other words, the scheduler still enables allocating into a Host, VMs which require more CPU MIPS than there is available. If the Host has at least the number of PEs a VM requires, the VM will be allowed to run into it.

   The scheduler doesn't in fact allocates more MIPS for Virtual PEs (vPEs) than there is in the physical PEs. It just reduces the allocated amount according to the available MIPS. This is an oversubscription, resulting in performance degradation because less MIPS may be allocated than the required by a VM.

   :author: Anton Beloglazov, Rodrigo N. Calheiros, Manoel Campos da Silva Filho

Constructors
------------
VmSchedulerTimeSharedOverSubscription
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSchedulerTimeSharedOverSubscription()
   :outertype: VmSchedulerTimeSharedOverSubscription

   Creates a time-shared over-subscription VM scheduler.

VmSchedulerTimeSharedOverSubscription
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSchedulerTimeSharedOverSubscription(double vmMigrationCpuOverhead)
   :outertype: VmSchedulerTimeSharedOverSubscription

   Creates a time-shared over-subscription VM scheduler, defining a CPU overhead for VM migration.

   :param vmMigrationCpuOverhead: the percentage of Host's CPU usage increase when a VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).

Methods
-------
allocateMipsShareForVm
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void allocateMipsShareForVm(Vm vm, List<Double> requestedMipsReduced)
   :outertype: VmSchedulerTimeSharedOverSubscription

isSuitableForVmInternal
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean isSuitableForVmInternal(Vm vm, List<Double> requestedMips, boolean showLog)
   :outertype: VmSchedulerTimeSharedOverSubscription

   Checks if a list of MIPS requested by a VM is allowed to be allocated or not. When there isn't the amount of requested MIPS available, this \ ``VmScheduler``\  allows to allocate what is available for the requesting VM, allocating less that is requested.

   This way, the only situation when it will not allow the allocation of MIPS for a VM is when the number of PEs required is greater than the total number of physical PEs. Even when there is not available MIPS at all, it allows the allocation of MIPS for the VM by reducing the allocation of other VMs.

   :param vm: {@inheritDoc}
   :param requestedMips: {@inheritDoc}
   :param showLog:
   :return: true if the requested MIPS List is allowed to be allocated to the VM, false otherwise

   **See also:** :java:ref:`.allocateMipsShareForVm(Vm,List)`

