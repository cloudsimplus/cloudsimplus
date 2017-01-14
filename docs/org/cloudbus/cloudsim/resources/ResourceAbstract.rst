.. java:import:: org.cloudbus.cloudsim.vms Vm

ResourceAbstract
================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public abstract class ResourceAbstract implements ResourceManageable

   A class that represent simple resources such as RAM, CPU or Bandwidth, storing, for instance, its capacity and amount of free available resource. The class is abstract just to ensure there will be an specific subclass for each kind of resource, allowing to differentiate, for instance, a RAM resource instance from a BW resource instance. The VM class also relies on this differentiation for generically getting a required resource (see \ :java:ref:`Vm.getResource(java.lang.Class)`\ ).

   :author: Manoel Campos da Silva Filho

Constructors
------------
ResourceAbstract
^^^^^^^^^^^^^^^^

.. java:constructor:: public ResourceAbstract(long capacity)
   :outertype: ResourceAbstract

Methods
-------
allocateResource
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResource(long amountToAllocate)
   :outertype: ResourceAbstract

deallocateAllResources
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long deallocateAllResources()
   :outertype: ResourceAbstract

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResource(long amountToDeallocate)
   :outertype: ResourceAbstract

getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: ResourceAbstract

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: ResourceAbstract

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: ResourceAbstract

isFull
^^^^^^

.. java:method:: @Override public boolean isFull()
   :outertype: ResourceAbstract

isResourceAmountAvailable
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountAvailable(long amountToCheck)
   :outertype: ResourceAbstract

isResourceAmountBeingUsed
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountBeingUsed(long amountToCheck)
   :outertype: ResourceAbstract

isSuitable
^^^^^^^^^^

.. java:method:: @Override public boolean isSuitable(long newTotalAllocatedResource)
   :outertype: ResourceAbstract

setAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean setAllocatedResource(long newTotalAllocatedResource)
   :outertype: ResourceAbstract

setAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final boolean setAvailableResource(long newAvailableResource)
   :outertype: ResourceAbstract

   Sets the given amount as available resource.

   :param newAvailableResource: the new amount of available resource to set
   :return: true if \ ``availableResource > 0 and availableResource <= capacity``\ , false otherwise

setCapacity
^^^^^^^^^^^

.. java:method:: @Override public final boolean setCapacity(long newCapacity)
   :outertype: ResourceAbstract

sumAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean sumAvailableResource(long amountToSum)
   :outertype: ResourceAbstract

   Sum a given amount (negative or positive) of available (free) resource to the total available resource.

   :param amountToSum: the amount to sum in the current total available resource. If given a positive number, increases the total available resource; otherwise, decreases the total available resource.
   :return: true if the total available resource was changed; false otherwise

