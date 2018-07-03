.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Comparator

.. java:import:: java.util Map

.. java:import:: java.util Optional

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

VmAllocationPolicyBestFit
=========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public class VmAllocationPolicyBestFit extends VmAllocationPolicyAbstract

   A VmAllocationPolicy implementation that chooses, as the host for a VM, that one with the most PEs in use. \ **It is therefore a Best Fit policy**\ , allocating each VM into the host with the least available PEs that are enough for the VM.

   \ **NOTE: This policy doesn't perform optimization of VM allocation by means of VM migration.**\

   :author: Manoel Campos da Silva Filho

Constructors
------------
VmAllocationPolicyBestFit
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyBestFit()
   :outertype: VmAllocationPolicyBestFit

   Instantiates a VmAllocationPolicyBestFit.

VmAllocationPolicyBestFit
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyBestFit(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicyBestFit

   Instantiates a VmAllocationPolicyBestFit, changing the \ :java:ref:`Function`\  to select a Host for a Vm in order to define a different policy.

   :param findHostForVmFunction: a \ :java:ref:`Function`\  to select a Host for a given Vm.

   **See also:** :java:ref:`VmAllocationPolicy.setFindHostForVmFunction(BiFunction)`

Methods
-------
findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public Optional<Host> findHostForVm(Vm vm)
   :outertype: VmAllocationPolicyBestFit

   Gets the first suitable host from the \ :java:ref:`getHostList()`\  that has the most number of used PEs (i.e, lower free PEs).

   :return: an \ :java:ref:`Optional`\  containing a suitable Host to place the VM or an empty \ :java:ref:`Optional`\  if not found

