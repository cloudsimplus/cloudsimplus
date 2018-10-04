.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

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

.. java:method:: @Override public int compareTo(SimEvent evt)
   :outertype: SimEventNull

getData
^^^^^^^

.. java:method:: @Override public Object getData()
   :outertype: SimEventNull

getDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public SimEntity getDestination()
   :outertype: SimEventNull

getEndWaitingTime
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getEndWaitingTime()
   :outertype: SimEventNull

getListener
^^^^^^^^^^^

.. java:method:: @Override public EventListener<EventInfo> getListener()
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

.. java:method:: @Override public SimEntity getSource()
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

.. java:method:: @Override public SimEntity scheduledBy()
   :outertype: SimEventNull

setDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public SimEvent setDestination(SimEntity destination)
   :outertype: SimEventNull

setSerial
^^^^^^^^^

.. java:method:: @Override public void setSerial(long serial)
   :outertype: SimEventNull

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public SimEvent setSimulation(Simulation simulation)
   :outertype: SimEventNull

setSource
^^^^^^^^^

.. java:method:: @Override public SimEvent setSource(SimEntity source)
   :outertype: SimEventNull

