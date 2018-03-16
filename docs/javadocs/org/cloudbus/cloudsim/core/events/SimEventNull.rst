.. java:import:: org.cloudbus.cloudsim.core Simulation

SimEventNull
============

.. java:package:: org.cloudbus.cloudsim.core.events
   :noindex:

.. java:type:: final class SimEventNull implements SimEvent

   A class that implements the Null Object Design Pattern for \ :java:ref:`SimEvent`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`SimEvent.NULL`

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEvent o)
   :outertype: SimEventNull

endWaitingTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double endWaitingTime()
   :outertype: SimEventNull

eventTime
^^^^^^^^^

.. java:method:: @Override public double eventTime()
   :outertype: SimEventNull

getData
^^^^^^^

.. java:method:: @Override public Object getData()
   :outertype: SimEventNull

getDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public int getDestination()
   :outertype: SimEventNull

getSerial
^^^^^^^^^

.. java:method:: @Override public long getSerial()
   :outertype: SimEventNull

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: SimEventNull

getSource
^^^^^^^^^

.. java:method:: @Override public int getSource()
   :outertype: SimEventNull

getTag
^^^^^^

.. java:method:: @Override public int getTag()
   :outertype: SimEventNull

getTime
^^^^^^^

.. java:method:: @Override public double getTime()
   :outertype: SimEventNull

getType
^^^^^^^

.. java:method:: @Override public Type getType()
   :outertype: SimEventNull

scheduledBy
^^^^^^^^^^^

.. java:method:: @Override public int scheduledBy()
   :outertype: SimEventNull

setDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public SimEvent setDestination(int destination)
   :outertype: SimEventNull

setSerial
^^^^^^^^^

.. java:method:: @Override public void setSerial(long serial)
   :outertype: SimEventNull

setSource
^^^^^^^^^

.. java:method:: @Override public SimEvent setSource(int source)
   :outertype: SimEventNull

