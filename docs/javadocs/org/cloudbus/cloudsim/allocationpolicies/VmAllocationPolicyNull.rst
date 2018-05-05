.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Optional

.. java:import:: java.util.function BiFunction

VmAllocationPolicyNull
======================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: final class VmAllocationPolicyNull implements VmAllocationPolicy

   A class that implements the Null Object Design Pattern for the \ :java:ref:`VmAllocationPolicy`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`VmAllocationPolicy.NULL`

Methods
-------
allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicyNull

allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm, Host host)
   :outertype: VmAllocationPolicyNull

deallocateHostForVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicyNull

findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public Optional<Host> findHostForVm(Vm vm)
   :outertype: VmAllocationPolicyNull

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: VmAllocationPolicyNull

getHostList
^^^^^^^^^^^

.. java:method:: @Override public List<Host> getHostList()
   :outertype: VmAllocationPolicyNull

getOptimizedAllocationMap
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList)
   :outertype: VmAllocationPolicyNull

scaleVmVertically
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean scaleVmVertically(VerticalVmScaling scaling)
   :outertype: VmAllocationPolicyNull

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenter(Datacenter datacenter)
   :outertype: VmAllocationPolicyNull

setFindHostForVmFunction
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setFindHostForVmFunction(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicyNull

