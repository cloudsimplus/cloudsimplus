.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

.. java:import:: java.util Optional

VmAllocationPolicyFirstFit
==========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public class VmAllocationPolicyFirstFit extends VmAllocationPolicyAbstract implements VmAllocationPolicy

   An \ **First Fit VM allocation policy**\  which finds the first Host having suitable resources to place a given VM. This is a very time efficient policy with a best-case complexity O(1) and a worst-case complexity O(N), where N is the number of Hosts.

   \ **NOTES:**\

   ..

   * This policy doesn't perform optimization of VM allocation by means of VM migration.
   * It has a low computational complexity but may return and inactive Host that will be activated, while there may be active Hosts suitable for the VM.

   :author: Manoel Campos da Silva Filho

Methods
-------
defaultFindHostForVm
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected Optional<Host> defaultFindHostForVm(Vm vm)
   :outertype: VmAllocationPolicyFirstFit

