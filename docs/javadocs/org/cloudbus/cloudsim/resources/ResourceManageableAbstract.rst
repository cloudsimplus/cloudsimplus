ResourceManageableAbstract
==========================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public abstract class ResourceManageableAbstract extends ResourceAbstract implements ResourceManageable

   A class that represents simple resources such as RAM, CPU, Bandwidth or Pe. It stores, for instance, the resource capacity and amount of free available resource.

   The class is abstract just to ensure there will be an specific subclass for each kind of resource, allowing to differentiate, for example, a RAM Resource from a BW Resource. The VM class also relies on this differentiation for generically getting a required resource.

   :author: Uros Cibej, Anthony Sulistio, Manoel Campos da Silva Filho

Constructors
------------
ResourceManageableAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ResourceManageableAbstract(long capacity)
   :outertype: ResourceManageableAbstract

Methods
-------
addCapacity
^^^^^^^^^^^

.. java:method:: @Override public boolean addCapacity(long capacityToAdd)
   :outertype: ResourceManageableAbstract

allocateResource
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResource(long amountToAllocate)
   :outertype: ResourceManageableAbstract

deallocateAllResources
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long deallocateAllResources()
   :outertype: ResourceManageableAbstract

deallocateAndRemoveResource
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateAndRemoveResource(long amountToDeallocate)
   :outertype: ResourceManageableAbstract

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResource(long amountToDeallocate)
   :outertype: ResourceManageableAbstract

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: ResourceManageableAbstract

removeCapacity
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeCapacity(long capacityToRemove)
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

.. java:method:: @Override public boolean setCapacity(long newCapacity)
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

