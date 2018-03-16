.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

VmAllocationPolicySimple
========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public class VmAllocationPolicySimple extends VmAllocationPolicyAbstract

   A VmAllocationPolicy implementation that chooses, as the host for a VM, that one with fewer PEs in use. It is therefore a Worst Fit policy, allocating VMs into the host with most available PEs.

   NOTE: This policy doesn't perform optimization of VM allocation (placement)
   by means of VM migration.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

Constructors
------------
VmAllocationPolicySimple
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicySimple()
   :outertype: VmAllocationPolicySimple

   Creates a new VmAllocationPolicySimple object.

Methods
-------
allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicySimple

   Allocates the host with less PEs in use for a given VM.

   :param vm: {@inheritDoc}
   :return: {@inheritDoc}

allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm, Host host)
   :outertype: VmAllocationPolicySimple

deallocateHostForVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicySimple

optimizeAllocation
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList)
   :outertype: VmAllocationPolicySimple

   The method in this VmAllocationPolicy doesn't perform any VM placement optimization and, in fact, has no effect.

   :param vmList: the list of VMs
   :return: an empty map to indicate that it never performs optimization

