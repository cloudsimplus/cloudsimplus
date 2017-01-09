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

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public class HostDynamicWorkloadSimple extends HostSimple implements HostDynamicWorkload

   A host supporting dynamic workloads and performance degradation.

   :author: Anton Beloglazov

Constructors
------------
HostDynamicWorkloadSimple
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostDynamicWorkloadSimple(int id, long storage, List<Pe> peList)
   :outertype: HostDynamicWorkloadSimple

   Creates a host.

   :param id: the id
   :param storage: the storage capacity
   :param peList: the host's PEs list

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

   Adds a host state history entry.

   :param time: the time
   :param allocatedMips: the allocated mips
   :param requestedMips: the requested mips
   :param isActive: the is active

getFinishedVms
^^^^^^^^^^^^^^

.. java:method:: @Override public List<Vm> getFinishedVms()
   :outertype: HostDynamicWorkloadSimple

getMaxUtilization
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxUtilization()
   :outertype: HostDynamicWorkloadSimple

   Gets the max utilization percentage among by all PEs.

   :return: the maximum utilization percentage

getMaxUtilizationAmongVmsPes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxUtilizationAmongVmsPes(Vm vm)
   :outertype: HostDynamicWorkloadSimple

   Gets the max utilization percentage among by all PEs allocated to a VM.

   :param vm: the vm
   :return: the max utilization percentage of the VM

getPreviousUtilizationMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousUtilizationMips()
   :outertype: HostDynamicWorkloadSimple

   Gets the previous utilization of CPU in mips.

   :return: the previous utilization of CPU in mips

getPreviousUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousUtilizationOfCpu()
   :outertype: HostDynamicWorkloadSimple

   Gets the previous utilization of CPU in percentage.

   :return: the previous utilization of cpu in percents

getStateHistory
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<HostStateHistoryEntry> getStateHistory()
   :outertype: HostDynamicWorkloadSimple

   Gets the host state history.

   :return: the state history

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getUtilizationOfBw()
   :outertype: HostDynamicWorkloadSimple

   Gets the utilization of bw (in absolute values).

   :return: the utilization of bw

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpu()
   :outertype: HostDynamicWorkloadSimple

   Get current utilization of CPU in percentage.

   :return: current utilization of CPU in percents

getUtilizationOfCpuMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpuMips()
   :outertype: HostDynamicWorkloadSimple

   Get current utilization of CPU in MIPS.

   :return: current utilization of CPU in MIPS

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getUtilizationOfRam()
   :outertype: HostDynamicWorkloadSimple

   Gets the utilization of memory (in absolute values).

   :return: the utilization of memory

setPreviousUtilizationMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setPreviousUtilizationMips(double previousUtilizationMips)
   :outertype: HostDynamicWorkloadSimple

   Sets the previous utilization of CPU in mips.

   :param previousUtilizationMips: the new previous utilization of CPU in mips

setUtilizationMips
^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setUtilizationMips(double utilizationMips)
   :outertype: HostDynamicWorkloadSimple

   Sets the utilization mips.

   :param utilizationMips: the new utilization mips

updateVmsProcessing
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateVmsProcessing(double currentTime)
   :outertype: HostDynamicWorkloadSimple

