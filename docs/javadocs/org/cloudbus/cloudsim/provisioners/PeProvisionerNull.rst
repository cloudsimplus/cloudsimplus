.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.vms Vm

PeProvisionerNull
=================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: final class PeProvisionerNull implements PeProvisioner

   A class that implements the Null Object Design Pattern for \ :java:ref:`PeProvisioner`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`PeProvisioner.NULL`

Methods
-------
allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity)
   :outertype: PeProvisionerNull

allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResourceForVm(Vm vm, double newTotalVmResource)
   :outertype: PeProvisionerNull

deallocateResourceForAllVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateResourceForAllVms()
   :outertype: PeProvisionerNull

deallocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResourceForVm(Vm vm)
   :outertype: PeProvisionerNull

getAllocatedResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResourceForVm(Vm vm)
   :outertype: PeProvisionerNull

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: PeProvisionerNull

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: PeProvisionerNull

getResource
^^^^^^^^^^^

.. java:method:: @Override public ResourceManageable getResource()
   :outertype: PeProvisionerNull

getTotalAllocatedResource
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getTotalAllocatedResource()
   :outertype: PeProvisionerNull

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization()
   :outertype: PeProvisionerNull

isResourceAllocatedToVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAllocatedToVm(Vm vm)
   :outertype: PeProvisionerNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource)
   :outertype: PeProvisionerNull

setPe
^^^^^

.. java:method:: @Override public void setPe(Pe pe)
   :outertype: PeProvisionerNull

setResource
^^^^^^^^^^^

.. java:method:: @Override public void setResource(ResourceManageable resource)
   :outertype: PeProvisionerNull

