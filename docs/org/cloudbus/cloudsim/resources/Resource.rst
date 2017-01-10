Resource
========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public interface Resource extends ResourceCapacity

   An interface to represent a physical or virtual resource (like RAM, CPU or Bandwidth) that doesn't provide direct features to change allocated amount of resource. Objects that directly implement this interface are supposed to define the capacity and amount of allocated resource in their constructors.

   :author: Manoel Campos da Silva Filho

Methods
-------
getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getAllocatedResource()
   :outertype: Resource

   Gets the current total amount of allocated resource.

   :return: amount of allocated resource

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getAvailableResource()
   :outertype: Resource

   Gets the amount of the resource that is available (free).

   :return: the amount of available resource

isFull
^^^^^^

.. java:method::  boolean isFull()
   :outertype: Resource

   Checks if the storage is full or not.

   :return: \ ``true``\  if the storage is full, \ ``false``\  otherwise

isResourceAmountAvailable
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isResourceAmountAvailable(long amountToCheck)
   :outertype: Resource

   Checks if there is a specific amount of resource available (free).

   :param amountToCheck: the amount of resource to check if is free.
   :return: true if the specified amount is free; false otherwise

