ResourceAbstract
================

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public abstract class ResourceAbstract implements Resource

   An abstract implementation of a \ :java:ref:`Resource`\ .

   :author: Manoel Campos da Silva Filho

Fields
------
capacity
^^^^^^^^

.. java:field:: protected long capacity
   :outertype: ResourceAbstract

   **See also:** :java:ref:`.getCapacity()`

Constructors
------------
ResourceAbstract
^^^^^^^^^^^^^^^^

.. java:constructor:: public ResourceAbstract(long capacity)
   :outertype: ResourceAbstract

Methods
-------
getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: ResourceAbstract

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: ResourceAbstract

isResourceAmountAvailable
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountAvailable(long amountToCheck)
   :outertype: ResourceAbstract

isResourceAmountAvailable
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isResourceAmountAvailable(double amountToCheck)
   :outertype: ResourceAbstract

isResourceAmountBeingUsed
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isResourceAmountBeingUsed(long amountToCheck)
   :outertype: ResourceAbstract

isSuitable
^^^^^^^^^^

.. java:method:: public boolean isSuitable(long newTotalAllocatedResource)
   :outertype: ResourceAbstract

