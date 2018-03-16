.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

HostDynamicWorkload
===================

.. java:package:: org.cloudbus.cloudsim.hosts
   :noindex:

.. java:type:: public interface HostDynamicWorkload extends Host

   An interface to be implemented by Host classes that provide dynamic workloads.

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

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

   Gets the max utilization percentage among (between [0 and 1], where 1 is 100%) by all PEs.

   :return: the max utilization percentage (between [0 and 1])

getMaxUtilizationAmongVmsPes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getMaxUtilizationAmongVmsPes(Vm vm)
   :outertype: HostDynamicWorkload

   Gets the max utilization percentage (between [0 and 1]) among by all PEs allocated to a VM.

   :param vm: the vm
   :return: the max utilization percentage (between [0 and 1])

getPreviousUtilizationMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getPreviousUtilizationMips()
   :outertype: HostDynamicWorkload

   Gets the previous utilization of CPU in MIPS.

getPreviousUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getPreviousUtilizationOfCpu()
   :outertype: HostDynamicWorkload

   Gets the previous utilization of CPU in percentage (between [0 and 1]).

getStateHistory
^^^^^^^^^^^^^^^

.. java:method::  List<HostStateHistoryEntry> getStateHistory()
   :outertype: HostDynamicWorkload

   Gets a \ **read-only**\  host state history.

   :return: the state history

