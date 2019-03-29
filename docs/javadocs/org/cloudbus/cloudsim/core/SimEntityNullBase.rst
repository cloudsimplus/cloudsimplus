.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

SimEntityNullBase
=================

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public interface SimEntityNullBase extends SimEntity

   A base interface used internally to implement the Null Object Design Pattern for interfaces extending \ :java:ref:`SimEntity`\ . It's just used to avoid the boilerplate code in such Null Object implementations.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`SimEntity.NULL`

Methods
-------
getId
^^^^^

.. java:method:: @Override  long getId()
   :outertype: SimEntityNullBase

getName
^^^^^^^

.. java:method:: @Override  String getName()
   :outertype: SimEntityNullBase

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override  Simulation getSimulation()
   :outertype: SimEntityNullBase

getState
^^^^^^^^

.. java:method:: @Override  State getState()
   :outertype: SimEntityNullBase

isAlive
^^^^^^^

.. java:method:: @Override  boolean isAlive()
   :outertype: SimEntityNullBase

isFinished
^^^^^^^^^^

.. java:method:: @Override  boolean isFinished()
   :outertype: SimEntityNullBase

isStarted
^^^^^^^^^

.. java:method:: @Override  boolean isStarted()
   :outertype: SimEntityNullBase

processEvent
^^^^^^^^^^^^

.. java:method:: @Override  void processEvent(SimEvent evt)
   :outertype: SimEntityNullBase

run
^^^

.. java:method:: @Override  void run()
   :outertype: SimEntityNullBase

schedule
^^^^^^^^

.. java:method:: @Override  boolean schedule(SimEvent evt)
   :outertype: SimEntityNullBase

schedule
^^^^^^^^

.. java:method:: @Override  boolean schedule(SimEntity dest, double delay, int tag, Object data)
   :outertype: SimEntityNullBase

schedule
^^^^^^^^

.. java:method:: @Override  boolean schedule(double delay, int tag, Object data)
   :outertype: SimEntityNullBase

schedule
^^^^^^^^

.. java:method:: @Override  boolean schedule(SimEntity dest, double delay, int tag)
   :outertype: SimEntityNullBase

schedule
^^^^^^^^

.. java:method:: @Override  boolean schedule(int tag, Object data)
   :outertype: SimEntityNullBase

setName
^^^^^^^

.. java:method:: @Override  SimEntity setName(String newName) throws IllegalArgumentException
   :outertype: SimEntityNullBase

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override  SimEntity setSimulation(Simulation simulation)
   :outertype: SimEntityNullBase

setState
^^^^^^^^

.. java:method:: @Override  SimEntity setState(State state)
   :outertype: SimEntityNullBase

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override  void shutdownEntity()
   :outertype: SimEntityNullBase

start
^^^^^

.. java:method:: @Override  void start()
   :outertype: SimEntityNullBase

