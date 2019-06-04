.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

.. java:import:: java.util Optional

VmAllocationPolicyRoundRobin
============================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public class VmAllocationPolicyRoundRobin extends VmAllocationPolicyAbstract implements VmAllocationPolicy

   A \ **Round-Robin VM allocation policy**\  which finds the next Host having suitable resources to place a given VM in a circular way. That means when it selects a suitable Host to place a VM, it moves to the next suitable Host when a new VM has to be placed. This is a high time-efficient policy with a best-case complexity O(1) and a worst-case complexity O(N), where N is the number of Hosts.

   \ **NOTES:**\

   ..

   * This policy doesn't perform optimization of VM allocation by means of VM migration.
   * It has a low computational complexity (high time-efficient) but may return and inactive Host that will be activated, while there may be active Hosts suitable for the VM.
   * Despite the low computational complexity, such a policy will increase the number of active Hosts, that increases power consumption.

   :author: Manoel Campos da Silva Filho

Methods
-------
defaultFindHostForVm
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected Optional<Host> defaultFindHostForVm(Vm vm)
   :outertype: VmAllocationPolicyRoundRobin

