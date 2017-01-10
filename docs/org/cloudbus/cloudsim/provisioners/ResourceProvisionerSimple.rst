.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

ResourceProvisionerSimple
=========================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: public class ResourceProvisionerSimple extends AbstractResourceProvisioner

   ResourceProvisionerSimple is an extension of \ :java:ref:`AbstractResourceProvisioner`\  which uses a best-effort policy to allocate a resource to VMs: if there is available amount of the resource on the host, it allocates; otherwise, it fails.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
ResourceProvisionerSimple
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ResourceProvisionerSimple(ResourceManageable resource)
   :outertype: ResourceProvisionerSimple

   Creates a new ResourceManageable Provisioner.

   :param resource: The resource to be managed by the provisioner

Methods
-------
allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResourceForVm(Vm vm, long newTotalVmResource)
   :outertype: ResourceProvisionerSimple

deallocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResourceForVm(Vm vm)
   :outertype: ResourceProvisionerSimple

deallocateResourceForVmSettingAllocationMapEntryToZero
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected long deallocateResourceForVmSettingAllocationMapEntryToZero(Vm vm)
   :outertype: ResourceProvisionerSimple

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource)
   :outertype: ResourceProvisionerSimple

