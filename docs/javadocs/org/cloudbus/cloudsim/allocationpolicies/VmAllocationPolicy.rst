.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Optional

.. java:import:: java.util.function BiFunction

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

VmAllocationPolicy
==================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public interface VmAllocationPolicy

   An interface to be implemented by each class that represents a policy used by a \ :java:ref:`Datacenter`\  to choose a \ :java:ref:`Host`\  to place or migrate a given \ :java:ref:`Vm`\ .

   The VmAllocationPolicy uses Java 8 Functional Programming to enable changing, at runtime, the policy used to select a Host for a given VM.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

   **See also:** :java:ref:`.setFindHostForVmFunction(BiFunction)`

Fields
------
NULL
^^^^

.. java:field::  VmAllocationPolicy NULL
   :outertype: VmAllocationPolicy

   A property that implements the Null Object Design Pattern for \ :java:ref:`VmAllocationPolicy`\  objects.

Methods
-------
allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method::  boolean allocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicy

   Allocates a host for a given VM.

   :param vm: the VM to allocate a host to
   :return: $true if the host could be allocated; $false otherwise

allocateHostForVm
^^^^^^^^^^^^^^^^^

.. java:method::  boolean allocateHostForVm(Vm vm, Host host)
   :outertype: VmAllocationPolicy

   Allocates a specified host for a given VM.

   :param vm: the VM to allocate a host to
   :param host: the host to allocate to the given VM
   :return: $true if the host could be allocated; $false otherwise

deallocateHostForVm
^^^^^^^^^^^^^^^^^^^

.. java:method::  void deallocateHostForVm(Vm vm)
   :outertype: VmAllocationPolicy

   Releases the host used by a VM.

   :param vm: the vm to get its host released

findHostForVm
^^^^^^^^^^^^^

.. java:method::  Optional<Host> findHostForVm(Vm vm)
   :outertype: VmAllocationPolicy

   Finds a host that has enough resources to place a given VM. \ **Classes must implement this method to define how to select a Host for a given VM.**\  They just have to provide a default implementation. However, this implementation can be dynamically changed by calling \ :java:ref:`setFindHostForVmFunction(BiFunction)`\ .

   :param vm: the vm to find a host for it
   :return: an \ :java:ref:`Optional`\  containing a suitable Host to place the VM or an empty \ :java:ref:`Optional`\  if no suitable Host was found

getDatacenter
^^^^^^^^^^^^^

.. java:method::  Datacenter getDatacenter()
   :outertype: VmAllocationPolicy

   Gets the \ :java:ref:`Datacenter`\  associated to the Allocation Policy.

getHostList
^^^^^^^^^^^

.. java:method::  <T extends Host> List<T> getHostList()
   :outertype: VmAllocationPolicy

   Gets the list of Hosts available in a \ :java:ref:`Datacenter`\ , that will be used by the Allocation Policy to place VMs.

   :param <T>: The generic type
   :return: the host list

getOptimizedAllocationMap
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList)
   :outertype: VmAllocationPolicy

   Gets a map of optimized allocation for VMs according to current utilization and Hosts under and overloaded conditions. The conditions that will make a new VM placement map to be proposed and returned is defined by each implementing class.

   :param vmList: the list of VMs to be reallocated
   :return: the new vm placement map, where each key is a VM and each value is the host where such a Vm has to be placed

scaleVmVertically
^^^^^^^^^^^^^^^^^

.. java:method::  boolean scaleVmVertically(VerticalVmScaling scaling)
   :outertype: VmAllocationPolicy

   Try to scale some Vm's resource vertically up or down, respectively if:

   ..

   * the Vm is overloaded and the Host where the Vm is placed has enough capacity
   * the Vm is underloaded

   The resource to be scaled is defined by the given \ :java:ref:`VerticalVmScaling`\  object.

   :param scaling: the \ :java:ref:`VerticalVmScaling`\  object with information of which resource is being requested to be scaled
   :return: true if the requested resource was scaled, false otherwise

setDatacenter
^^^^^^^^^^^^^

.. java:method::  void setDatacenter(Datacenter datacenter)
   :outertype: VmAllocationPolicy

   Sets the Datacenter associated to the Allocation Policy

   :param datacenter: the Datacenter to set

setFindHostForVmFunction
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setFindHostForVmFunction(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
   :outertype: VmAllocationPolicy

   Sets a \ :java:ref:`BiFunction`\  that selects a Host for a given Vm. This Function receives the current VmAllocationPolicy and the \ :java:ref:`Vm`\  requesting to be place. It then returns an \ :java:ref:`Optional`\  that may contain a suitable Host for that Vm or not.

   If not Function is set, the default VM selection method provided by implementing classes will be used.

   :param findHostForVmFunction: the \ :java:ref:`BiFunction`\  to set

