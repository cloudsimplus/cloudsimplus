.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util HashMap

.. java:import:: java.util Map

.. java:import:: java.util Objects

ResourceProvisionerAbstract
===========================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: public abstract class ResourceProvisionerAbstract implements ResourceProvisioner

   An abstract class that implements the basic features of a provisioning policy used by a \ :java:ref:`Host`\  to provide a given resource to its virtual machines.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

   **See also:** :java:ref:`ResourceProvisioner`

Constructors
------------
ResourceProvisionerAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected ResourceProvisionerAbstract()
   :outertype: ResourceProvisionerAbstract

   Creates a new ResourceManageable Provisioner for which the \ :java:ref:`resource <getResource()>`\  must be set further.

ResourceProvisionerAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ResourceProvisionerAbstract(ResourceManageable resource)
   :outertype: ResourceProvisionerAbstract

   Creates a new ResourceManageable Provisioner.

   :param resource: The resource to be managed by the provisioner

Methods
-------
deallocateResourceForAllVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateResourceForAllVms()
   :outertype: ResourceProvisionerAbstract

deallocateResourceForVmAndSetAllocationMapEntryToZero
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract long deallocateResourceForVmAndSetAllocationMapEntryToZero(Vm vm)
   :outertype: ResourceProvisionerAbstract

   Deallocate the resource for the given VM, without removing the VM fro the allocation map. The resource usage of the VM entry on the allocation map is just set to 0.

   :param vm: the VM to deallocate resource
   :return: the amount of allocated VM resource or zero if VM is not found

getAllocatedResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResourceForVm(Vm vm)
   :outertype: ResourceProvisionerAbstract

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: ResourceProvisionerAbstract

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: ResourceProvisionerAbstract

getResource
^^^^^^^^^^^

.. java:method:: @Override public ResourceManageable getResource()
   :outertype: ResourceProvisionerAbstract

getResourceAllocationMap
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Long> getResourceAllocationMap()
   :outertype: ResourceProvisionerAbstract

   Gets the VM resource allocation map, where each key is a VM and each value is the amount of resource allocated to that VM.

   :return: the resource allocation Map

getResourceClass
^^^^^^^^^^^^^^^^

.. java:method:: protected Class<? extends ResourceManageable> getResourceClass()
   :outertype: ResourceProvisionerAbstract

   Gets the class of the resource that this provisioner manages.

   :return: the resource class

getTotalAllocatedResource
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getTotalAllocatedResource()
   :outertype: ResourceProvisionerAbstract

isResourceAllocatedToVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAllocatedToVm(Vm vm)
   :outertype: ResourceProvisionerAbstract

setResource
^^^^^^^^^^^

.. java:method:: @Override public final void setResource(ResourceManageable resource)
   :outertype: ResourceProvisionerAbstract

