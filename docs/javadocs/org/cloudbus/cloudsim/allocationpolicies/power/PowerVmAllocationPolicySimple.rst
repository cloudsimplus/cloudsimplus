.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Map

PowerVmAllocationPolicySimple
=============================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: public class PowerVmAllocationPolicySimple extends PowerVmAllocationPolicyAbstract

   A simple VM allocation policy that does \ **not**\  perform any optimization on VM allocation. \ **It's a First Fit policy which finds the first Host having suitable resources to place a given VM.**\

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerVmAllocationPolicySimple
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PowerVmAllocationPolicySimple()
   :outertype: PowerVmAllocationPolicySimple

   Instantiates a new PowerVmAllocationPolicySimple.

Methods
-------
optimizeAllocation
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList)
   :outertype: PowerVmAllocationPolicySimple

   The method in this VmAllocationPolicy doesn't perform any VM placement optimization and, in fact, has no effect.

   :param vmList: the list of VMs
   :return: an empty map to indicate that it never performs optimization

