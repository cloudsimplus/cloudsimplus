.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

VmAllocationPolicySimple
========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public class VmAllocationPolicySimple extends VmAllocationPolicyAbstract

   A VmAllocationPolicy implementation that chooses, as the host for a VM, that one with fewer PEs in use. \ **It is therefore a Worst Fit policy**\ , allocating VMs into the host with most available PEs.

   NOTE: This policy doesn't perform optimization of VM allocation (placement)
   by means of VM migration.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
VmAllocationPolicySimple
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicySimple()
   :outertype: VmAllocationPolicySimple

   Creates a new VmAllocationPolicySimple object.

VmAllocationPolicySimple
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicySimple(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicySimple

   Creates a new VmAllocationPolicy, changing the \ :java:ref:`Function`\  to select a Host for a Vm.

   :param findHostForVmFunction: a \ :java:ref:`Function`\  to select a Host for a given Vm.

   **See also:** :java:ref:`VmAllocationPolicy.setFindHostForVmFunction(java.util.function.BiFunction)`

Methods
-------
allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm, Host host)
   :outertype: VmAllocationPolicySimple

deallocateHostForVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicySimple

findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public Optional<Host> findHostForVm(Vm vm)
   :outertype: VmAllocationPolicySimple

   Gets the first suitable host from the \ :java:ref:`getHostList()`\  that has fewer used PEs (i.e, higher free PEs).

   :return: an \ :java:ref:`Optional`\  containing a suitable Host to place the VM or an empty \ :java:ref:`Optional`\  if not found

getOptimizedAllocationMap
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList)
   :outertype: VmAllocationPolicySimple

   This implementation doesn't perform any VM placement optimization and, in fact, has no effect.

   :param vmList: the list of VMs
   :return: an empty map to indicate that it never performs optimization

