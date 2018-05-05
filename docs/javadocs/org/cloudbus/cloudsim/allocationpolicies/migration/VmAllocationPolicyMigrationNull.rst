.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Optional

.. java:import:: java.util.function BiFunction

VmAllocationPolicyMigrationNull
===============================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.migration
   :noindex:

.. java:type:: final class VmAllocationPolicyMigrationNull implements VmAllocationPolicyMigration

   A class that implements the Null Object Design Pattern for \ :java:ref:`VmAllocationPolicyMigration`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`VmAllocationPolicyMigration.NULL`

Methods
-------
allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicyMigrationNull

allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm, Host host)
   :outertype: VmAllocationPolicyMigrationNull

deallocateHostForVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicyMigrationNull

findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public Optional<Host> findHostForVm(Vm vm)
   :outertype: VmAllocationPolicyMigrationNull

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: VmAllocationPolicyMigrationNull

getHostList
^^^^^^^^^^^

.. java:method:: @Override public <T extends Host> List<T> getHostList()
   :outertype: VmAllocationPolicyMigrationNull

getMetricHistory
^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getMetricHistory()
   :outertype: VmAllocationPolicyMigrationNull

getOptimizedAllocationMap
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList)
   :outertype: VmAllocationPolicyMigrationNull

getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getOverUtilizationThreshold(Host host)
   :outertype: VmAllocationPolicyMigrationNull

getTimeHistory
^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getTimeHistory()
   :outertype: VmAllocationPolicyMigrationNull

getUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUnderUtilizationThreshold()
   :outertype: VmAllocationPolicyMigrationNull

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getUtilizationHistory()
   :outertype: VmAllocationPolicyMigrationNull

isHostOverloaded
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostOverloaded(Host host)
   :outertype: VmAllocationPolicyMigrationNull

isHostUnderloaded
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostUnderloaded(Host host)
   :outertype: VmAllocationPolicyMigrationNull

scaleVmVertically
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean scaleVmVertically(VerticalVmScaling scaling)
   :outertype: VmAllocationPolicyMigrationNull

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenter(Datacenter datacenter)
   :outertype: VmAllocationPolicyMigrationNull

setFindHostForVmFunction
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setFindHostForVmFunction(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicyMigrationNull

setUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setUnderUtilizationThreshold(double underUtilizationThreshold)
   :outertype: VmAllocationPolicyMigrationNull

