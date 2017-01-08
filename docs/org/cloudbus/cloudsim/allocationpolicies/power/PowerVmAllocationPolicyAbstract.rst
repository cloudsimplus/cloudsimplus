.. java:import:: java.util HashMap

.. java:import:: java.util Map

.. java:import:: java.util Objects

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicyAbstract

.. java:import:: org.cloudbus.cloudsim.core Simulation

PowerVmAllocationPolicyAbstract
===============================

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public abstract class PowerVmAllocationPolicyAbstract extends VmAllocationPolicyAbstract implements PowerVmAllocationPolicy

   An abstract power-aware VM allocation policy.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerVmAllocationPolicyAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmAllocationPolicyAbstract()
   :outertype: PowerVmAllocationPolicyAbstract

   Creates a PowerVmAllocationPolicy.

Methods
-------
allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm)
   :outertype: PowerVmAllocationPolicyAbstract

allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm, Host host)
   :outertype: PowerVmAllocationPolicyAbstract

deallocateHostForVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateHostForVm(Vm vm)
   :outertype: PowerVmAllocationPolicyAbstract

findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public PowerHost findHostForVm(Vm vm)
   :outertype: PowerVmAllocationPolicyAbstract

getVmHostMap
^^^^^^^^^^^^

.. java:method:: public Map<Vm, Host> getVmHostMap()
   :outertype: PowerVmAllocationPolicyAbstract

   Gets the map where each key is a VM UID and each value is the host where the VM is placed.

