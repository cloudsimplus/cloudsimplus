.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHostUtilizationHistory

.. java:import:: org.cloudbus.cloudsim.lists PeList

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.vms VmStateHistoryEntry

.. java:import:: java.util Collections

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: java.util.stream Collectors

HostDynamicWorkloadSimple
=========================

.. java:package:: org.cloudbus.cloudsim.hosts
   :noindex:

.. java:type:: public class HostDynamicWorkloadSimple extends HostSimple implements HostDynamicWorkload

   A host supporting dynamic workloads and performance degradation.

   :author: Anton Beloglazov

Constructors
------------
HostDynamicWorkloadSimple
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostDynamicWorkloadSimple(long ram, long bw, long storage, List<Pe> peList)
   :outertype: HostDynamicWorkloadSimple

   Creates a host.

   :param ram: the RAM capacity in Megabytes
   :param bw: the Bandwidth (BW) capacity in Megabits/s
   :param storage: the storage capacity in Megabytes
   :param peList: the host's \ :java:ref:`Pe`\  list

HostDynamicWorkloadSimple
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: @Deprecated public HostDynamicWorkloadSimple(int id, ResourceProvisioner ramProvisioner, ResourceProvisioner bwProvisioner, long storage, List<Pe> peList, VmScheduler vmScheduler)
   :outertype: HostDynamicWorkloadSimple

   Creates a host with the given parameters.

   :param id: the id
   :param ramProvisioner: the ram provisioner
   :param bwProvisioner: the bw provisioner
   :param storage: the storage capacity
   :param peList: the host's PEs list
   :param vmScheduler: the VM scheduler

Methods
-------
addStateHistoryEntry
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean isActive)
   :outertype: HostDynamicWorkloadSimple

getFinishedVms
^^^^^^^^^^^^^^

.. java:method:: @Override public List<Vm> getFinishedVms()
   :outertype: HostDynamicWorkloadSimple

getMaxUtilization
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxUtilization()
   :outertype: HostDynamicWorkloadSimple

getMaxUtilizationAmongVmsPes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxUtilizationAmongVmsPes(Vm vm)
   :outertype: HostDynamicWorkloadSimple

getPreviousUtilizationMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousUtilizationMips()
   :outertype: HostDynamicWorkloadSimple

getPreviousUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousUtilizationOfCpu()
   :outertype: HostDynamicWorkloadSimple

getStateHistory
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<HostStateHistoryEntry> getStateHistory()
   :outertype: HostDynamicWorkloadSimple

setPreviousUtilizationMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setPreviousUtilizationMips(double previousUtilizationMips)
   :outertype: HostDynamicWorkloadSimple

   Sets the previous utilization of CPU in mips.

   :param previousUtilizationMips: the new previous utilization of CPU in mips

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime)
   :outertype: HostDynamicWorkloadSimple

