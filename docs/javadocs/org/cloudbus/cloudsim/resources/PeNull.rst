.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisionerSimple

PeNull
======

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: final class PeNull implements Pe

   A class that implements the Null Object Design Pattern for \ :java:ref:`Pe`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Pe.NULL`

Methods
-------
addCapacity
^^^^^^^^^^^

.. java:method:: @Override public boolean addCapacity(long capacityToAdd)
   :outertype: PeNull

allocateResource
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResource(long amountToAllocate)
   :outertype: PeNull

deallocateAllResources
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long deallocateAllResources()
   :outertype: PeNull

deallocateAndRemoveResource
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateAndRemoveResource(long amountToDeallocate)
   :outertype: PeNull

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResource(long amountToDeallocate)
   :outertype: PeNull

getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: PeNull

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: PeNull

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: PeNull

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: PeNull

getPeProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public PeProvisioner getPeProvisioner()
   :outertype: PeNull

getStatus
^^^^^^^^^

.. java:method:: @Override public Status getStatus()
   :outertype: PeNull

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isAmountAvailable(long amountToCheck)
   :outertype: PeNull

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isAmountAvailable(double amountToCheck)
   :outertype: PeNull

isBuzy
^^^^^^

.. java:method:: @Override public boolean isBuzy()
   :outertype: PeNull

isFailed
^^^^^^^^

.. java:method:: @Override public boolean isFailed()
   :outertype: PeNull

isFree
^^^^^^

.. java:method:: @Override public boolean isFree()
   :outertype: PeNull

isFull
^^^^^^

.. java:method:: @Override public boolean isFull()
   :outertype: PeNull

isResourceAmountBeingUsed
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountBeingUsed(long amountToCheck)
   :outertype: PeNull

isSuitable
^^^^^^^^^^

.. java:method:: @Override public boolean isSuitable(long newTotalAllocatedResource)
   :outertype: PeNull

isWorking
^^^^^^^^^

.. java:method:: @Override public boolean isWorking()
   :outertype: PeNull

removeCapacity
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeCapacity(long capacityToRemove)
   :outertype: PeNull

setAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean setAllocatedResource(long newTotalAllocatedResource)
   :outertype: PeNull

setCapacity
^^^^^^^^^^^

.. java:method:: @Override public boolean setCapacity(long mipsCapacity)
   :outertype: PeNull

setCapacity
^^^^^^^^^^^

.. java:method:: @Override public boolean setCapacity(double mipsCapacity)
   :outertype: PeNull

setId
^^^^^

.. java:method:: @Override public void setId(long id)
   :outertype: PeNull

setPeProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public Pe setPeProvisioner(PeProvisioner peProvisioner)
   :outertype: PeNull

setStatus
^^^^^^^^^

.. java:method:: @Override public boolean setStatus(Status status)
   :outertype: PeNull

