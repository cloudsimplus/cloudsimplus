.. java:import:: org.cloudbus.cloudsim.vms Vm

ResourceProvisioner
===================

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public interface ResourceProvisioner

   An interface that represents the provisioning policy used by a host to allocate a given resource to virtual machines inside it. Each host has to have its own instance of a ResourceProvisioner for each resource it owns, such as RAM, Bandwidth (BW) and CPU.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  ResourceProvisioner NULL
   :outertype: ResourceProvisioner

   A property that implements the Null Object Design Pattern for ResourceProvisioner objects.

Methods
-------
allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean allocateResourceForVm(Vm vm, long newTotalVmResource)
   :outertype: ResourceProvisioner

   Allocates an amount of resource for a given VM (if the resource was never been allocated before) or change the current allocation. If the VM already has any amount of the resource allocated, deallocate if first and allocate the newTotalVmResource amount.

   :param vm: the virtual machine for which the resource is being allocated
   :param newTotalVmResource: the new total amount of resource to allocate to the VM, changing the allocate resource to this new amount. It doesn't increase the current allocated VM resource by the given amount, instead, it changes the VM allocated resource to that specific amount
   :return: $true if the resource could be allocated; $false otherwise

deallocateResourceForAllVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void deallocateResourceForAllVms()
   :outertype: ResourceProvisioner

   Releases all the allocated amount of the resource used by all VMs.

deallocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean deallocateResourceForVm(Vm vm)
   :outertype: ResourceProvisioner

   Releases all the allocated amount of the resource used by a VM.

   :param vm: the vm
   :return: true if the resource was deallocated; false if the related resource has never been allocated to the given VM.

getAllocatedResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getAllocatedResourceForVm(Vm vm)
   :outertype: ResourceProvisioner

   Gets the amount of allocated resource for a given VM

   :param vm: the VM
   :return: the allocated resource for the VM

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getAvailableResource()
   :outertype: ResourceProvisioner

   Gets the amount of free available resource from the host that the provisioner can allocate to VMs.

   :return: the amount of free available resource

getCapacity
^^^^^^^^^^^

.. java:method::  long getCapacity()
   :outertype: ResourceProvisioner

   Gets the total capacity of the resource from the host that the provisioner manages.

   :return: the total resource capacity

getTotalAllocatedResource
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getTotalAllocatedResource()
   :outertype: ResourceProvisioner

   Gets the total allocated resource among all VMs

   :return: the total allocated resource among all VMs

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource)
   :outertype: ResourceProvisioner

   Checks if it is possible to change the current allocated resource for a given VM to a new amount, depending on the available resource remaining.

   :param vm: the vm to check if there is enough available resource on the host to change the allocated amount for the VM
   :param newVmTotalAllocatedResource: the new total amount of resource to allocate for the VM.
   :return: true, if it is possible to allocate the new total VM resource; false otherwise

