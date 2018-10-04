.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util ArrayList

.. java:import:: java.util Iterator

.. java:import:: java.util List

VmSchedulerSpaceShared
======================

.. java:package:: org.cloudbus.cloudsim.schedulers.vm
   :noindex:

.. java:type:: public class VmSchedulerSpaceShared extends VmSchedulerAbstract

   VmSchedulerSpaceShared is a VMM allocation policy that allocates one or more PEs from a host to a Virtual Machine Monitor (VMM), and doesn't allow sharing of PEs. The allocated PEs will be used until the VM finishes running. If there is no enough free PEs as required by a VM, or whether the available PEs doesn't have enough capacity, the allocation fails. In the case of fail, no PE is allocated to the requesting VM.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
VmSchedulerSpaceShared
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSchedulerSpaceShared()
   :outertype: VmSchedulerSpaceShared

   Creates a space-shared VM scheduler.

VmSchedulerSpaceShared
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmSchedulerSpaceShared(double vmMigrationCpuOverhead)
   :outertype: VmSchedulerSpaceShared

   Creates a space-shared VM scheduler, defining a CPU overhead for VM migration.

   :param vmMigrationCpuOverhead: the percentage of Host's CPU usage increase when a VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).

Methods
-------
allocatePesForVmInternal
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocatePesForVmInternal(Vm vm, List<Double> requestedMips)
   :outertype: VmSchedulerSpaceShared

deallocatePesFromVmInternal
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void deallocatePesFromVmInternal(Vm vm, int pesToRemove)
   :outertype: VmSchedulerSpaceShared

isSuitableForVmInternal
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean isSuitableForVmInternal(Vm vm, List<Double> requestedMips, boolean showLog)
   :outertype: VmSchedulerSpaceShared

