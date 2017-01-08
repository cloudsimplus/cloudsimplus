.. java:import:: java.util HashMap

.. java:import:: java.util Map

.. java:import:: java.util Objects

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

AbstractResourceProvisioner
===========================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: public abstract class AbstractResourceProvisioner implements ResourceProvisioner

   An abstract class that implements the basic features of a provisioning policy used by a host to allocate a given resource to virtual machines inside it.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

   **See also:** :java:ref:`ResourceProvisioner`

Constructors
------------
AbstractResourceProvisioner
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractResourceProvisioner(ResourceManageable resource)
   :outertype: AbstractResourceProvisioner

   Creates a new ResourceManageable Provisioner.

   :param resource: The resource to be managed by the provisioner

Methods
-------
deallocateResourceForAllVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateResourceForAllVms()
   :outertype: AbstractResourceProvisioner

deallocateResourceForVmSettingAllocationMapEntryToZero
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract long deallocateResourceForVmSettingAllocationMapEntryToZero(Vm vm)
   :outertype: AbstractResourceProvisioner

   Deallocate the resource for the given VM, without removing the VM fro the allocation map. The resource usage of the VM entry on the allocation map is just set to 0.

   :param vm: the VM to deallocate resource
   :return: the amount of allocated VM resource or zero if VM is not found

getAllocatedResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResourceForVm(Vm vm)
   :outertype: AbstractResourceProvisioner

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: AbstractResourceProvisioner

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: AbstractResourceProvisioner

getResource
^^^^^^^^^^^

.. java:method:: protected ResourceManageable getResource()
   :outertype: AbstractResourceProvisioner

   :return: the resource

getResourceAllocationMap
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Long> getResourceAllocationMap()
   :outertype: AbstractResourceProvisioner

   :return: the resourceAllocationMap

getResourceClass
^^^^^^^^^^^^^^^^

.. java:method:: protected Class<? extends ResourceManageable> getResourceClass()
   :outertype: AbstractResourceProvisioner

   :return: the resourceClass

getTotalAllocatedResource
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getTotalAllocatedResource()
   :outertype: AbstractResourceProvisioner

