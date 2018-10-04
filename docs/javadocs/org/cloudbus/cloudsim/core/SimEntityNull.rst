.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

SimEntityNull
=============

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: final class SimEntityNull implements SimEntity

   A class that implements the Null Object Design Pattern for \ :java:ref:`SimEntity`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`SimEntity.NULL`

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEntity entity)
   :outertype: SimEntityNull

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: SimEntityNull

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: SimEntityNull

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: SimEntityNull

getState
^^^^^^^^

.. java:method:: @Override public State getState()
   :outertype: SimEntityNull

isAlive
^^^^^^^

.. java:method:: @Override public boolean isAlive()
   :outertype: SimEntityNull

isFinished
^^^^^^^^^^

.. java:method:: @Override public boolean isFinished()
   :outertype: SimEntityNull

isStarted
^^^^^^^^^

.. java:method:: @Override public boolean isStarted()
   :outertype: SimEntityNull

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent evt)
   :outertype: SimEntityNull

run
^^^

.. java:method:: @Override public void run()
   :outertype: SimEntityNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEvent evt)
   :outertype: SimEntityNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEntity dest, double delay, int tag, Object data)
   :outertype: SimEntityNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(double delay, int tag, Object data)
   :outertype: SimEntityNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEntity dest, double delay, int tag)
   :outertype: SimEntityNull

setName
^^^^^^^

.. java:method:: @Override public SimEntity setName(String newName) throws IllegalArgumentException
   :outertype: SimEntityNull

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public SimEntity setSimulation(Simulation simulation)
   :outertype: SimEntityNull

setState
^^^^^^^^

.. java:method:: @Override public SimEntity setState(State state)
   :outertype: SimEntityNull

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override public void shutdownEntity()
   :outertype: SimEntityNull

start
^^^^^

.. java:method:: @Override public void start()
   :outertype: SimEntityNull

