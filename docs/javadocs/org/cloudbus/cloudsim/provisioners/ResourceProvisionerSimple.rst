.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Objects

ResourceProvisionerSimple
=========================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: public class ResourceProvisionerSimple extends ResourceProvisionerAbstract

   A best-effort \ :java:ref:`ResourceProvisioner`\  policy used by a \ :java:ref:`Host`\  to provide a resource to VMs:

   ..

   * if there is available amount of the resource on the host, it provides;
   * otherwise, it fails.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
ResourceProvisionerSimple
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ResourceProvisionerSimple()
   :outertype: ResourceProvisionerSimple

   Creates a new ResourceProvisionerSimple which the \ :java:ref:`ResourceManageable`\  it will manage have to be set further.

   **See also:** :java:ref:`.setResource(ResourceManageable)`

ResourceProvisionerSimple
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected ResourceProvisionerSimple(ResourceManageable resource)
   :outertype: ResourceProvisionerSimple

   Creates a new ResourceProvisionerSimple.

   :param resource: the resource to be managed by the provisioner

Methods
-------
allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity)
   :outertype: ResourceProvisionerSimple

allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResourceForVm(Vm vm, double newTotalVmResource)
   :outertype: ResourceProvisionerSimple

deallocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResourceForVm(Vm vm)
   :outertype: ResourceProvisionerSimple

deallocateResourceForVmAndSetAllocationMapEntryToZero
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected long deallocateResourceForVmAndSetAllocationMapEntryToZero(Vm vm)
   :outertype: ResourceProvisionerSimple

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource)
   :outertype: ResourceProvisionerSimple

