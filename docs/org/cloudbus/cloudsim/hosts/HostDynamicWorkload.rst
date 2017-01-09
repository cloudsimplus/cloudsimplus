.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

HostDynamicWorkload
===================

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public interface HostDynamicWorkload extends Host

   An interface to be implemented by Host classes that provide dynamic workloads.

   :author: Manoel Campos da Silva Filho

Methods
-------
addStateHistoryEntry
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void addStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean isActive)
   :outertype: HostDynamicWorkload

   Adds a host state history entry.

   :param time: the time
   :param allocatedMips: the allocated mips
   :param requestedMips: the requested mips
   :param isActive: the is active

getFinishedVms
^^^^^^^^^^^^^^

.. java:method::  List<Vm> getFinishedVms()
   :outertype: HostDynamicWorkload

   Gets the list of VMs that finished executing.

getMaxUtilization
^^^^^^^^^^^^^^^^^

.. java:method::  double getMaxUtilization()
   :outertype: HostDynamicWorkload

   Gets the max utilization percentage among by all PEs.

getMaxUtilizationAmongVmsPes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getMaxUtilizationAmongVmsPes(Vm vm)
   :outertype: HostDynamicWorkload

   Gets the max utilization percentage among by all PEs allocated to a VM.

   :param vm: the vm

getPreviousUtilizationMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getPreviousUtilizationMips()
   :outertype: HostDynamicWorkload

   Gets the previous utilization of CPU in mips.

getPreviousUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getPreviousUtilizationOfCpu()
   :outertype: HostDynamicWorkload

   Gets the previous utilization of CPU in percentage.

getStateHistory
^^^^^^^^^^^^^^^

.. java:method::  List<HostStateHistoryEntry> getStateHistory()
   :outertype: HostDynamicWorkload

   Gets a \ **read-only**\  host state history.

   :return: the state history

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method::  long getUtilizationOfBw()
   :outertype: HostDynamicWorkload

   Gets the current utilization of bw (in absolute values).

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method::  double getUtilizationOfCpu()
   :outertype: HostDynamicWorkload

   Gets current utilization of CPU in percentage.

getUtilizationOfCpuMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getUtilizationOfCpuMips()
   :outertype: HostDynamicWorkload

   Gets the current utilization of CPU in MIPS.

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method::  long getUtilizationOfRam()
   :outertype: HostDynamicWorkload

   Gets the current utilization of memory (in absolute values).

