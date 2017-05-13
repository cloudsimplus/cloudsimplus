.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.vms Vm

ResourceProvisionerNull
=======================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: final class ResourceProvisionerNull implements ResourceProvisioner

   A class that implements the Null Object Design Pattern for \ :java:ref:`ResourceProvisioner`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`ResourceProvisioner.NULL`

Methods
-------
allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity)
   :outertype: ResourceProvisionerNull

deallocateResourceForAllVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateResourceForAllVms()
   :outertype: ResourceProvisionerNull

deallocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResourceForVm(Vm vm)
   :outertype: ResourceProvisionerNull

getAllocatedResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResourceForVm(Vm vm)
   :outertype: ResourceProvisionerNull

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: ResourceProvisionerNull

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: ResourceProvisionerNull

getResource
^^^^^^^^^^^

.. java:method:: @Override public ResourceManageable getResource()
   :outertype: ResourceProvisionerNull

getTotalAllocatedResource
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getTotalAllocatedResource()
   :outertype: ResourceProvisionerNull

isResourceAllocatedToVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAllocatedToVm(Vm vm)
   :outertype: ResourceProvisionerNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource)
   :outertype: ResourceProvisionerNull

setResource
^^^^^^^^^^^

.. java:method:: @Override public void setResource(ResourceManageable resource)
   :outertype: ResourceProvisionerNull

