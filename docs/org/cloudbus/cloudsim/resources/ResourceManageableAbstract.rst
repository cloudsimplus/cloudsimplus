ResourceManageableAbstract
==========================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public abstract class ResourceManageableAbstract implements ResourceManageable

   A class that represents simple resources such as RAM, CPU, Bandwidth or Pe, storing, for instance, the resource capacity and amount of free available resource.

   The class is abstract just to ensure there will be an specific subclass for each kind of resource, allowing to differentiate, for instance, a RAM resource instance from a BW resource instance. The VM class also relies on this differentiation for generically getting a required resource.

   :author: Uros Cibej, Anthony Sulistio, Manoel Campos da Silva Filho

Constructors
------------
ResourceManageableAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ResourceManageableAbstract(long capacity)
   :outertype: ResourceManageableAbstract

Methods
-------
allocateResource
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResource(long amountToAllocate)
   :outertype: ResourceManageableAbstract

deallocateAllResources
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long deallocateAllResources()
   :outertype: ResourceManageableAbstract

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResource(long amountToDeallocate)
   :outertype: ResourceManageableAbstract

getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: ResourceManageableAbstract

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: ResourceManageableAbstract

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: ResourceManageableAbstract

isFull
^^^^^^

.. java:method:: @Override public boolean isFull()
   :outertype: ResourceManageableAbstract

isResourceAmountAvailable
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountAvailable(long amountToCheck)
   :outertype: ResourceManageableAbstract

isResourceAmountAvailable
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountAvailable(double amountToCheck)
   :outertype: ResourceManageableAbstract

isResourceAmountBeingUsed
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountBeingUsed(long amountToCheck)
   :outertype: ResourceManageableAbstract

isSuitable
^^^^^^^^^^

.. java:method:: @Override public boolean isSuitable(long newTotalAllocatedResource)
   :outertype: ResourceManageableAbstract

setAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean setAllocatedResource(long newTotalAllocatedResource)
   :outertype: ResourceManageableAbstract

setAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final boolean setAvailableResource(long newAvailableResource)
   :outertype: ResourceManageableAbstract

   Sets the given amount as available resource.

   :param newAvailableResource: the new amount of available resource to set
   :return: true if \ ``availableResource > 0 and availableResource <= capacity``\ , false otherwise

setCapacity
^^^^^^^^^^^

.. java:method:: @Override public final boolean setCapacity(long newCapacity)
   :outertype: ResourceManageableAbstract

sumAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean sumAvailableResource(long amountToSum)
   :outertype: ResourceManageableAbstract

   Sum a given amount (negative or positive) of available (free) resource to the total available resource.

   :param amountToSum: the amount to sum in the current total available resource. If given a positive number, increases the total available resource; otherwise, decreases the total available resource.
   :return: true if the total available resource was changed; false otherwise

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: ResourceManageableAbstract

