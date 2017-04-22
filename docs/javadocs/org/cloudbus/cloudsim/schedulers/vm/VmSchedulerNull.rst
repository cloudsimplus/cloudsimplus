.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Set

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
addVmMigratingIn
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addVmMigratingIn(Vm vm)
   :outertype: VmSchedulerNull

addVmMigratingOut
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addVmMigratingOut(Vm vm)
   :outertype: VmSchedulerNull

allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShare)
   :outertype: VmSchedulerNull

allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocatePesForVm(Vm vm)
   :outertype: VmSchedulerNull

deallocatePesForAllVms
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForAllVms()
   :outertype: VmSchedulerNull

deallocatePesForVm
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForVm(Vm vm)
   :outertype: VmSchedulerNull

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: VmSchedulerNull

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAvailableMips()
   :outertype: VmSchedulerNull

getCpuOverheadDueToVmMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCpuOverheadDueToVmMigration()
   :outertype: VmSchedulerNull

getHost
^^^^^^^

.. java:method:: @Override public Host getHost()
   :outertype: VmSchedulerNull

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxAvailableMips()
   :outertype: VmSchedulerNull

getPeCapacity
^^^^^^^^^^^^^

.. java:method:: @Override public long getPeCapacity()
   :outertype: VmSchedulerNull

getPeList
^^^^^^^^^

.. java:method:: @Override public <T extends Pe> List<T> getPeList()
   :outertype: VmSchedulerNull

getPeMap
^^^^^^^^

.. java:method:: @Override public Map<Vm, List<Pe>> getPeMap()
   :outertype: VmSchedulerNull

getPesAllocatedForVM
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Pe> getPesAllocatedForVM(Vm vm)
   :outertype: VmSchedulerNull

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: VmSchedulerNull

getVmsMigratingIn
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Vm> getVmsMigratingIn()
   :outertype: VmSchedulerNull

getVmsMigratingOut
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Vm> getVmsMigratingOut()
   :outertype: VmSchedulerNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm)
   :outertype: VmSchedulerNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(List<Double> vmMipsList)
   :outertype: VmSchedulerNull

removeVmMigratingIn
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeVmMigratingIn(Vm vm)
   :outertype: VmSchedulerNull

removeVmMigratingOut
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeVmMigratingOut(Vm vm)
   :outertype: VmSchedulerNull

setHost
^^^^^^^

.. java:method:: @Override public VmScheduler setHost(Host host)
   :outertype: VmSchedulerNull

