ResourceManageable
==================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public interface ResourceManageable extends Resource

   An interface to represent a physical or virtual resource (like RAM, CPU or Bandwidth) with features to manage resource capacity and allocation.

   :author: Uros Cibej, Anthony Sulistio, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  ResourceManageable NULL
   :outertype: ResourceManageable

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`ResourceManageable`\  objects.

Methods
-------
addCapacity
^^^^^^^^^^^

.. java:method::  boolean addCapacity(long capacityToAdd)
   :outertype: ResourceManageable

   Try to add a given amount to the \ :java:ref:`resource capacity <getCapacity()>`\ .

   :param capacityToAdd: the amount to add
   :return: true if capacityToAdd > 0, false otherwise

   **See also:** :java:ref:`.getAllocatedResource()`

allocateResource
^^^^^^^^^^^^^^^^

.. java:method::  boolean allocateResource(long amountToAllocate)
   :outertype: ResourceManageable

   Try to allocate a given amount of the resource, reducing that amount from the total available resource.

   :param amountToAllocate: the amount of resource to be allocated
   :return: true if amountToAllocate > 0 and there is enough resource to allocate, false otherwise

allocateResource
^^^^^^^^^^^^^^^^

.. java:method::  boolean allocateResource(Resource resource)
   :outertype: ResourceManageable

   Try to allocate in this resource, the amount of resource specified by the capacity of the given resource. This method is commonly used to allocate a specific amount from a physical resource (this Resource instance) to a virtualized resource (the given Resource).

   :param resource: the resource to try to allocate its capacity from the current resource
   :return: true if required capacity from the given resource > 0 and there is enough resource to allocate, false otherwise

   **See also:** :java:ref:`.allocateResource(long)`

deallocateAllResources
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long deallocateAllResources()
   :outertype: ResourceManageable

   Deallocates all allocated resources, restoring the total available resource to the resource capacity.

   :return: the amount of resource freed

deallocateAndRemoveResource
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean deallocateAndRemoveResource(long amountToDeallocate)
   :outertype: ResourceManageable

   Try to deallocate a given amount of the resource and then remove such amount from the total capacity. If the given amount is greater than the total allocated resource, all the resource will be deallocated and that amount will be removed from the total capacity.

   :param amountToDeallocate: the amount of resource to be deallocated and then removed from the total capacity
   :return: true if amountToDeallocate > 0 and there is enough resource to deallocate, false otherwise

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method::  boolean deallocateResource(Resource resource)
   :outertype: ResourceManageable

   Try to deallocate all the capacity of the given resource from this resource. This method is commonly used to deallocate a specific amount of a physical resource (this Resource instance) that was being used by a virtualized resource (the given Resource).

   :param resource: the resource that its capacity will be deallocated
   :return: true if capacity of the given resource > 0 and there is enough resource to deallocate, false otherwise

   **See also:** :java:ref:`.deallocateResource(long)`

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method::  boolean deallocateResource(long amountToDeallocate)
   :outertype: ResourceManageable

   Try to deallocate a given amount of the resource.

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

removeCapacity
^^^^^^^^^^^^^^

.. java:method::  boolean removeCapacity(long capacityToRemove)
   :outertype: ResourceManageable

   Try to remove a given amount to the \ :java:ref:`resource capacity <getCapacity()>`\ .

   :param capacityToRemove: the amount to remove
   :return: true if capacityToRemove > 0, the current allocated resource is less or equal to the expected new capacity and the capacity to remove is not higher than the current capacity; false otherwise

   **See also:** :java:ref:`.getAllocatedResource()`

setAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean setAllocatedResource(long newTotalAllocatedResource)
   :outertype: ResourceManageable

   Try to set the current total amount of allocated resource, changing it to the given value. It doesn't increase the current allocated resource by the given amount, instead, it changes the allocated resource to that specified amount.

   :param newTotalAllocatedResource: the new total amount of resource to allocate, changing the allocate resource to this new amount.
   :return: true if newTotalAllocatedResource is not negative and there is enough resource to allocate, false otherwise

setAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean setAllocatedResource(double newTotalAllocatedResource)
   :outertype: ResourceManageable

   Try to set the current total amount of allocated resource, changing it to the given value. It doesn't increase the current allocated resource by the given amount, instead, it changes the allocated resource to that specified amount.

   This method is just a shorthand to avoid explicitly converting a double to long.

   :param newTotalAllocatedResource: the new total amount of resource to allocate, changing the allocate resource to this new amount.
   :return: true if newTotalAllocatedResource is not negative and there is enough resource to allocate, false otherwise

setCapacity
^^^^^^^^^^^

.. java:method::  boolean setCapacity(long newCapacity)
   :outertype: ResourceManageable

   Try to set the \ :java:ref:`resource capacity <getCapacity()>`\ .

   :param newCapacity: the new resource capacity
   :return: true if capacity >= 0 and capacity >= current allocated resource, false otherwise

   **See also:** :java:ref:`.getAllocatedResource()`

