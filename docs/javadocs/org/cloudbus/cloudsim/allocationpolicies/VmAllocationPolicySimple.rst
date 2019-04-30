.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Comparator

.. java:import:: java.util Optional

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

.. java:import:: java.util.stream Stream

VmAllocationPolicySimple
========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public class VmAllocationPolicySimple extends VmAllocationPolicyAbstract

   A VmAllocationPolicy implementation that chooses, as the host for a VM, that one with the fewest PEs in use. \ **It is therefore a Worst Fit policy**\ , allocating each VM into the host with most available PEs.

   This is a really computationally complex policy since the worst-case complexity to allocate a Host for a VM is O(N), where N is the number of Hosts. Such an implementation is not appropriate for large scale scenarios.

   \ **NOTE: This policy doesn't perform optimization of VM allocation by means of VM migration.**\

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

   **See also:** :java:ref:`VmAllocationPolicyFirstFit`

Constructors
------------
VmAllocationPolicySimple
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicySimple()
   :outertype: VmAllocationPolicySimple

   Instantiates a VmAllocationPolicySimple.

VmAllocationPolicySimple
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicySimple(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicySimple

   Instantiates a VmAllocationPolicySimple, changing the \ :java:ref:`Function`\  to select a Host for a Vm in order to define a different policy.

   :param findHostForVmFunction: a \ :java:ref:`Function`\  to select a Host for a given Vm.

   **See also:** :java:ref:`VmAllocationPolicy.setFindHostForVmFunction(java.util.function.BiFunction)`

Methods
-------
defaultFindHostForVm
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected Optional<Host> defaultFindHostForVm(Vm vm)
   :outertype: VmAllocationPolicySimple

   Gets the first suitable host from the \ :java:ref:`getHostList()`\  that has the fewest number of used PEs (i.e, higher free PEs).

   :return: an \ :java:ref:`Optional`\  containing a suitable Host to place the VM or an empty \ :java:ref:`Optional`\  if not found

