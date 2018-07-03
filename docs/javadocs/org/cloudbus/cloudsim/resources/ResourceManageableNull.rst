ResourceManageableNull
======================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: final class ResourceManageableNull implements ResourceManageable

   A class that implements the Null Object Design Pattern for \ :java:ref:`ResourceManageable`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`ResourceManageable.NULL`

Methods
-------
addCapacity
^^^^^^^^^^^

.. java:method:: @Override public boolean addCapacity(long capacityToAdd)
   :outertype: ResourceManageableNull

allocateResource
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResource(long amountToAllocate)
   :outertype: ResourceManageableNull

deallocateAllResources
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long deallocateAllResources()
   :outertype: ResourceManageableNull

deallocateAndRemoveResource
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateAndRemoveResource(long amountToDeallocate)
   :outertype: ResourceManageableNull

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResource(long amountToDeallocate)
   :outertype: ResourceManageableNull

getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: ResourceManageableNull

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: ResourceManageableNull

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: ResourceManageableNull

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isAmountAvailable(long amountToCheck)
   :outertype: ResourceManageableNull

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isAmountAvailable(double amountToCheck)
   :outertype: ResourceManageableNull

isFull
^^^^^^

.. java:method:: @Override public boolean isFull()
   :outertype: ResourceManageableNull

isResourceAmountBeingUsed
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountBeingUsed(long amountToCheck)
   :outertype: ResourceManageableNull

isSuitable
^^^^^^^^^^

.. java:method:: @Override public boolean isSuitable(long newTotalAllocatedResource)
   :outertype: ResourceManageableNull

removeCapacity
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeCapacity(long capacityToRemove)
   :outertype: ResourceManageableNull

setAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean setAllocatedResource(long newTotalAllocatedResource)
   :outertype: ResourceManageableNull

setCapacity
^^^^^^^^^^^

.. java:method:: @Override public boolean setCapacity(long newCapacity)
   :outertype: ResourceManageableNull

