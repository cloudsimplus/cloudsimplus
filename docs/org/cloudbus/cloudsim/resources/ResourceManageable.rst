ResourceManageable
==================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public interface ResourceManageable extends Resource

   An interface to represent a physical or virtual resource (like RAM, CPU or Bandwidth) with features to manage resource capacity and allocation.

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  ResourceManageable NULL
   :outertype: ResourceManageable

   A property that implements the Null Object Design Pattern for ResourceManageable<long> objects.

Methods
-------
allocateResource
^^^^^^^^^^^^^^^^

.. java:method::  boolean allocateResource(long amountToAllocate)
   :outertype: ResourceManageable

   Allocates a given amount of the resource, reducing that amount from the total available resource.

   :param amountToAllocate: the amount of resource to be allocated
   :return: true if amountToAllocate > 0 and there is enough resource to allocate, false otherwise

deallocateAllResources
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long deallocateAllResources()
   :outertype: ResourceManageable

   Deallocates all allocated resources, restoring the total available resource to the resource capacity.

   :return: the amount of resource freed

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method::  boolean deallocateResource(long amountToDeallocate)
   :outertype: ResourceManageable

   Deallocates a given amount of the resource, adding up that amount to the total available resource.

   :param amountToDeallocate: the amount of resource to be deallocated
   :return: true if amountToDeallocate > 0 and there is enough resource to deallocate, false otherwise

isResourceAmountBeingUsed
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isResourceAmountBeingUsed(long amountToCheck)
   :outertype: ResourceManageable

   Checks if there is a specific amount of resource being used.

   :param amountToCheck: the amount of resource to check if is used.
   :return: true if the specified amount is being used; false otherwise

isSuitable
^^^^^^^^^^

.. java:method::  boolean isSuitable(long newTotalAllocatedResource)
   :outertype: ResourceManageable

   Checks if it is possible to change the current allocated resource to a new amount, depending on the available resource remaining.

   :param newTotalAllocatedResource: the new total amount of resource to allocate.
   :return: true, if it is possible to allocate the new total resource; false otherwise

setAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean setAllocatedResource(long newTotalAllocatedResource)
   :outertype: ResourceManageable

   Sets the current total amount of allocated resource, changing it to the given value. It doesn't increase the current allocated resource by the given amount, instead, it changes the allocated resource to that specified amount.

   :param newTotalAllocatedResource: the new total amount of resource to allocate, changing the allocate resource to this new amount.
   :return: true if newTotalAllocatedResource is not negative and there is enough resource to allocate, false otherwise

setCapacity
^^^^^^^^^^^

.. java:method::  boolean setCapacity(long newCapacity)
   :outertype: ResourceManageable

   Sets the \ :java:ref:`resource capacity <getCapacity()>`\ .

   :param newCapacity: the new resource capacity
   :return: true if capacity > 0 and capacity >= current allocated resource, false otherwise

   **See also:** :java:ref:`.getAllocatedResource()`

