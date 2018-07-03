.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

ResourceProvisioner
===================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: public interface ResourceProvisioner

   An interface that represents the provisioning policy used by a \ :java:ref:`Host`\  to provide a given physical resource to its \ :java:ref:`Vm`\ s. Each host must have its own instance of a ResourceProvisioner for each \ :java:ref:`Resource`\  it owns, such as \ :java:ref:`Ram`\ , \ :java:ref:`Bandwidth`\  (BW) and \ :java:ref:`Pe`\  (CPU).

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  ResourceProvisioner NULL
   :outertype: ResourceProvisioner

   An attribute that implements the Null Object Design Pattern for ResourceProvisioner objects.

Methods
-------
allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity)
   :outertype: ResourceProvisioner

   Allocates an amount of the physical resource for a VM, changing the current capacity of the virtual resource to the given amount.

   :param vm: the virtual machine for which the resource is being allocated
   :param newTotalVmResourceCapacity: the new total amount of resource to allocate to the VM, changing the allocate resource to this new amount. It doesn't increase the current allocated VM resource by the given amount, instead, it changes the VM allocated resource to that specific amount
   :return: $true if the resource could be allocated; $false otherwise

allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean allocateResourceForVm(Vm vm, double newTotalVmResource)
   :outertype: ResourceProvisioner

   Allocates an amount of the physical resource for a VM, changing the current capacity of the virtual resource to the given amount.

   This method is just a shorthand to avoid explicitly converting a double to long.

   :param vm: the virtual machine for which the resource is being allocated
   :param newTotalVmResource: the new total amount of resource to allocate to the VM, changing the allocate resource to this new amount. It doesn't increase the current allocated VM resource by the given amount, instead, it changes the VM allocated resource to that specific amount
   :return: $true if the resource could be allocated; $false otherwise

   **See also:** :java:ref:`.allocateResourceForVm(Vm,long)`

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

   Gets the amount of resource allocated to a given VM from the physical resource

   :param vm: the VM
   :return: the allocated resource for the VM

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getAvailableResource()
   :outertype: ResourceProvisioner

   Gets the amount of free available physical resource from the host that the provisioner can allocate to VMs.

   :return: the amount of free available physical resource

getCapacity
^^^^^^^^^^^

.. java:method::  long getCapacity()
   :outertype: ResourceProvisioner

   Gets the total capacity of the physical resource from the Host that the provisioner manages.

   :return: the total physical resource capacity

getResource
^^^^^^^^^^^

.. java:method::  ResourceManageable getResource()
   :outertype: ResourceProvisioner

   Gets the resource being managed by the provisioner, such as \ :java:ref:`Ram`\ , \ :java:ref:`Pe`\ , \ :java:ref:`Bandwidth`\ , etc.

   :return: the resource managed by this provisioner

getTotalAllocatedResource
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getTotalAllocatedResource()
   :outertype: ResourceProvisioner

   Gets the total amount of resource allocated to all VMs from the physical resource

   :return: the total allocated resource among all VMs

isResourceAllocatedToVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isResourceAllocatedToVm(Vm vm)
   :outertype: ResourceProvisioner

   Checks if the resource the provisioner manages is allocated to a given Vm.

   :param vm: the VM to check if the resource is allocated to
   :return: true if the resource is allocated to the VM, false otherwise

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource)
   :outertype: ResourceProvisioner

   Checks if it is possible to change the current allocated resource for a given VM to a new amount, depending on the available physical resource remaining.

   :param vm: the vm to check if there is enough available resource on the host to change the allocated amount for the VM
   :param newVmTotalAllocatedResource: the new total amount of resource to allocate for the VM.
   :return: true, if it is possible to allocate the new total VM resource; false otherwise

setResource
^^^^^^^^^^^

.. java:method::  void setResource(ResourceManageable resource)
   :outertype: ResourceProvisioner

   Sets the resource to be managed by the provisioner, such as \ :java:ref:`Ram`\ , \ :java:ref:`Pe`\ , \ :java:ref:`Bandwidth`\ , etc.

   :param resource: the resource managed by this provisioner

