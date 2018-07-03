.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Collections

.. java:import:: java.util List

VmSchedulerNull
===============

.. java:package:: org.cloudbus.cloudsim.schedulers.vm
   :noindex:

.. java:type:: final class VmSchedulerNull implements VmScheduler

   A class that implements the Null Object Design Pattern for \ :java:ref:`VmScheduler`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`VmScheduler.NULL`

Methods
-------
allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocatePesForVm(Vm vm, List<Double> requestedMips)
   :outertype: VmSchedulerNull

allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocatePesForVm(Vm vm)
   :outertype: VmSchedulerNull

deallocatePesForAllVms
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForAllVms()
   :outertype: VmSchedulerNull

deallocatePesFromVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesFromVm(Vm vm)
   :outertype: VmSchedulerNull

deallocatePesFromVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesFromVm(Vm vm, int pesToRemove)
   :outertype: VmSchedulerNull

getAllocatedMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getAllocatedMips(Vm vm)
   :outertype: VmSchedulerNull

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAvailableMips()
   :outertype: VmSchedulerNull

getHost
^^^^^^^

.. java:method:: @Override public Host getHost()
   :outertype: VmSchedulerNull

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxAvailableMips()
   :outertype: VmSchedulerNull

getMaxCpuUsagePercentDuringOutMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxCpuUsagePercentDuringOutMigration()
   :outertype: VmSchedulerNull

getPeCapacity
^^^^^^^^^^^^^

.. java:method:: @Override public long getPeCapacity()
   :outertype: VmSchedulerNull

getRequestedMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getRequestedMips(Vm vm)
   :outertype: VmSchedulerNull

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: VmSchedulerNull

getVmMigrationCpuOverhead
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getVmMigrationCpuOverhead()
   :outertype: VmSchedulerNull

getWorkingPeList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Pe> List<T> getWorkingPeList()
   :outertype: VmSchedulerNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm)
   :outertype: VmSchedulerNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm, boolean showLog)
   :outertype: VmSchedulerNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm, List<Double> requestedMips)
   :outertype: VmSchedulerNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm, List<Double> requestedMips, boolean showLog)
   :outertype: VmSchedulerNull

setHost
^^^^^^^

.. java:method:: @Override public VmScheduler setHost(Host host)
   :outertype: VmSchedulerNull

