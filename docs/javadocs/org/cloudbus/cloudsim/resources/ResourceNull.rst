ResourceNull
============

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: final class ResourceNull implements Resource

   A class that implements the Null Object Design Pattern for \ :java:ref:`Resource`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Resource.NULL`

Methods
-------
getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: ResourceNull

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: ResourceNull

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: ResourceNull

isAmountAvailable
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isAmountAvailable(long amountToCheck)
   :outertype: ResourceNull

isFull
^^^^^^

.. java:method:: @Override public boolean isFull()
   :outertype: ResourceNull

