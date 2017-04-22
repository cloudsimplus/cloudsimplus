.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Map.Entry

.. java:import:: org.cloudbus.cloudsim.lists PeList

.. java:import:: org.cloudbus.cloudsim.vms Vm

VmSchedulerTimeSharedOverSubscription
=====================================

.. java:package:: org.cloudbus.cloudsim.schedulers.vm
   :noindex:

.. java:type:: public class VmSchedulerTimeSharedOverSubscription extends VmSchedulerTimeShared

   This is a Time-Shared VM Scheduler, which allows over-subscription. In other words, the scheduler still allows the allocation of VMs that require more CPU capacity than is available. Oversubscription results in performance degradation.

   :author: Anton Beloglazov, Rodrigo N. Calheiros

Methods
-------
redistributeMipsDueToOverSubscription
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void redistributeMipsDueToOverSubscription()
   :outertype: VmSchedulerTimeSharedOverSubscription

   Recalculates distribution of MIPs among VMs, considering eventual shortage of MIPS compared to the amount requested by VMs.

updateMapOfRequestedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean updateMapOfRequestedMipsForVm(Vm vm, List<Double> mipsShareRequested)
   :outertype: VmSchedulerTimeSharedOverSubscription

   Allocates PEs for vm. The policy allows over-subscription. In other words, the policy still allows the allocation of VMs that require more CPU capacity than is available. Oversubscription results in performance degradation. It cannot be allocated more CPU capacity for each virtual PE than the MIPS capacity of a single physical PE.

   :param vm: the vm
   :param mipsShareRequested: the list of mips share requested
   :return: true, if successful

