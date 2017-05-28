.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Map

PowerVmAllocationPolicyMigrationNull
====================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: final class PowerVmAllocationPolicyMigrationNull implements PowerVmAllocationPolicyMigration

   A class that implements the Null Object Design Pattern for \ :java:ref:`PowerVmAllocationPolicyMigration`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`PowerVmAllocationPolicyMigration.NULL`

Methods
-------
allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm)
   :outertype: PowerVmAllocationPolicyMigrationNull

allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm, Host host)
   :outertype: PowerVmAllocationPolicyMigrationNull

deallocateHostForVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateHostForVm(Vm vm)
   :outertype: PowerVmAllocationPolicyMigrationNull

findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public PowerHost findHostForVm(Vm vm)
   :outertype: PowerVmAllocationPolicyMigrationNull

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: PowerVmAllocationPolicyMigrationNull

getHostList
^^^^^^^^^^^

.. java:method:: @Override public <T extends Host> List<T> getHostList()
   :outertype: PowerVmAllocationPolicyMigrationNull

getMetricHistory
^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getMetricHistory()
   :outertype: PowerVmAllocationPolicyMigrationNull

getOverUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getOverUtilizationThreshold(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationNull

getTimeHistory
^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getTimeHistory()
   :outertype: PowerVmAllocationPolicyMigrationNull

getUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUnderUtilizationThreshold()
   :outertype: PowerVmAllocationPolicyMigrationNull

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Host, List<Double>> getUtilizationHistory()
   :outertype: PowerVmAllocationPolicyMigrationNull

isHostOverloaded
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostOverloaded(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationNull

isHostUnderloaded
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isHostUnderloaded(PowerHost host)
   :outertype: PowerVmAllocationPolicyMigrationNull

optimizeAllocation
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList)
   :outertype: PowerVmAllocationPolicyMigrationNull

scaleVmVertically
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean scaleVmVertically(VerticalVmScaling scaling)
   :outertype: PowerVmAllocationPolicyMigrationNull

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenter(Datacenter datacenter)
   :outertype: PowerVmAllocationPolicyMigrationNull

setUnderUtilizationThreshold
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setUnderUtilizationThreshold(double underUtilizationThreshold)
   :outertype: PowerVmAllocationPolicyMigrationNull

