.. java:import:: org.cloudbus.cloudsim.allocationpolicies.migration VmAllocationPolicyMigrationAbstract

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Processor

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Optional

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.stream LongStream

VmAllocationPolicyAbstract
==========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public abstract class VmAllocationPolicyAbstract implements VmAllocationPolicy

   An abstract class that represents the policy used by a \ :java:ref:`Datacenter`\  to choose a \ :java:ref:`Host`\  to place or migrate a given \ :java:ref:`Vm`\ . It supports two-stage commit of reservation of hosts: first, we reserve the Host and, once committed by the customer, the VM is effectively allocated to that Host.

   Each \ :java:ref:`Datacenter`\  must to have its own instance of a \ :java:ref:`VmAllocationPolicy`\ .

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
VmAllocationPolicyAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyAbstract()
   :outertype: VmAllocationPolicyAbstract

   Creates a VmAllocationPolicy.

VmAllocationPolicyAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyAbstract(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicyAbstract

   Creates a VmAllocationPolicy, changing the \ :java:ref:`BiFunction`\  to select a Host for a Vm.

   :param findHostForVmFunction: a \ :java:ref:`BiFunction`\  to select a Host for a given Vm.

   **See also:** :java:ref:`VmAllocationPolicy.setFindHostForVmFunction(BiFunction)`

Methods
-------
allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicyAbstract

   Allocates the host with less PEs in use for a given VM.

   :param vm: {@inheritDoc}
   :return: {@inheritDoc}

allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateHostForVm(Vm vm, Host host)
   :outertype: VmAllocationPolicyAbstract

deallocateHostForVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicyAbstract

defaultFindHostForVm
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract Optional<Host> defaultFindHostForVm(Vm vm)
   :outertype: VmAllocationPolicyAbstract

   Provides the default implementation of the \ :java:ref:`VmAllocationPolicy`\  to find a suitable Host for a given VM.

   :param vm: the VM to find a suitable Host to
   :return: an \ :java:ref:`Optional`\  containing a suitable Host to place the VM or an empty \ :java:ref:`Optional`\  if no suitable Host was found

   **See also:** :java:ref:`.setFindHostForVmFunction(BiFunction)`

findHostForVm
^^^^^^^^^^^^^

.. java:method:: @Override public final Optional<Host> findHostForVm(Vm vm)
   :outertype: VmAllocationPolicyAbstract

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: VmAllocationPolicyAbstract

getHostCountForParallelSearch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getHostCountForParallelSearch()
   :outertype: VmAllocationPolicyAbstract

getHostList
^^^^^^^^^^^

.. java:method:: @Override public final <T extends Host> List<T> getHostList()
   :outertype: VmAllocationPolicyAbstract

getOptimizedAllocationMap
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList)
   :outertype: VmAllocationPolicyAbstract

   {@inheritDoc}

   This method implementation doesn't perform any
   VM placement optimization and, in fact, has no effect.
   The  class
   provides an actual implementation for this method that can be overridden
   by subclasses.

   :param vmList: {@inheritDoc}
   :return: {@inheritDoc}

scaleVmVertically
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean scaleVmVertically(VerticalVmScaling scaling)
   :outertype: VmAllocationPolicyAbstract

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public final void setDatacenter(Datacenter datacenter)
   :outertype: VmAllocationPolicyAbstract

   Sets the Datacenter associated to the Allocation Policy

   :param datacenter: the Datacenter to set

setFindHostForVmFunction
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setFindHostForVmFunction(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicyAbstract

   {@inheritDoc} The default implementation of such a Function is provided by the method \ :java:ref:`findHostForVm(Vm)`\ .

   :param findHostForVmFunction: {@inheritDoc}. Passing null makes the default method to find a Host for a VM to be used.

setHostCountForParallelSearch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setHostCountForParallelSearch(int hostCountForParallelSearch)
   :outertype: VmAllocationPolicyAbstract

