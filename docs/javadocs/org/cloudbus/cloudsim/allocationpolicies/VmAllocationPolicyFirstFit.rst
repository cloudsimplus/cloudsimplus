.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Optional

VmAllocationPolicyFirstFit
==========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public class VmAllocationPolicyFirstFit extends VmAllocationPolicyAbstract implements VmAllocationPolicy

   An \ **First Fit VM allocation policy**\  which finds the first Host having suitable resources to place a given VM.

   NOTE: This policy doesn't perform optimization of VM allocation (placement)
   by means of VM migration.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Methods
-------
findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public Optional<Host> findHostForVm(Vm vm)
   :outertype: VmAllocationPolicyFirstFit

getOptimizedAllocationMap
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList)
   :outertype: VmAllocationPolicyFirstFit

   This implementation doesn't perform any VM placement optimization and, in fact, has no effect.

   :param vmList: the list of VMs
   :return: an empty map to indicate that it never performs optimization

