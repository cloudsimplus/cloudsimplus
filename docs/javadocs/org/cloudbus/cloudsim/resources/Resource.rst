Resource
========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public interface Resource extends ResourceCapacity

   An interface to represent a physical or virtual resource (like RAM, CPU or Bandwidth) that doesn't provide direct features to change allocated amount of resource. Objects that directly implement this interface are supposed to define the capacity and amount of allocated resource in their constructors.

   :author: Uros Cibej, Anthony Sulistio, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Resource NULL
   :outertype: Resource

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Resource`\  objects.

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

getPercentUtilization
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getPercentUtilization()
   :outertype: Resource

   Gets the current percentage of resource utilization in scale from 0 to 1. It is the percentage of the total resource capacity that is currently allocated.

   :return: current resource utilization (allocation) percentage in scale from 0 to 1

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method::  boolean isAmountAvailable(Resource resource)
   :outertype: Resource

   Checks if there the capacity required for the given resource is available (free) at this resource. This method is commonly used to check if there is a specific amount of resource free at a physical resource (this Resource instance) that is required by a virtualized resource (the given Resource).

   :param resource: the resource to check if its capacity is available at the current resource
   :return: true if the capacity required by the given Resource is free; false otherwise

   **See also:** :java:ref:`.isAmountAvailable(long)`

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method::  boolean isAmountAvailable(long amountToCheck)
   :outertype: Resource

   Checks if there is a specific amount of resource available (free).

   :param amountToCheck: the amount of resource to check if is free.
   :return: true if the specified amount is free; false otherwise

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method::  boolean isAmountAvailable(double amountToCheck)
   :outertype: Resource

   Checks if there is a specific amount of resource available (free), where such amount is a double value that will be converted to long.

   This method is just a shorthand to avoid explicitly converting a double to long.

   :param amountToCheck: the amount of resource to check if is free.
   :return: true if the specified amount is free; false otherwise

   **See also:** :java:ref:`.isAmountAvailable(long)`

isFull
^^^^^^

.. java:method::  boolean isFull()
   :outertype: Resource

   Checks if the resource is full or not.

   :return: \ ``true``\  if the storage is full, \ ``false``\  otherwise

isObjectSubClassOf
^^^^^^^^^^^^^^^^^^

.. java:method:: static boolean isObjectSubClassOf(Object object, Class classWanted)
   :outertype: Resource

   Checks if a given object is instance of a given class.

   :param object: the object to check
   :param classWanted: the class to verify if the object is instance of
   :return: true if the object is instance of the given class, false otherwise

isObjectSubClassOf
^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isObjectSubClassOf(Class classWanted)
   :outertype: Resource

   Checks if this object is instance of a given class.

   :param classWanted: the class to verify if the object is instance of
   :return: true if the object is instance of the given class, false otherwise

