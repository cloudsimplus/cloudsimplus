.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

SimEntity
=========

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public interface SimEntity extends Nameable, Cloneable, Runnable, Comparable<SimEntity>

   An interface that represents a simulation entity. An entity handles events and can send events to other entities.

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

   **See also:** :java:ref:`CloudSimEntity`

Fields
------
NULL
^^^^

.. java:field::  SimEntity NULL
   :outertype: SimEntity

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`SimEntity`\  objects.

Methods
-------
getSimulation
^^^^^^^^^^^^^

.. java:method::  Simulation getSimulation()
   :outertype: SimEntity

   Gets the CloudSim instance that represents the simulation to each the Entity is related to.

getState
^^^^^^^^

.. java:method::  State getState()
   :outertype: SimEntity

   Gets the entity state.

   :return: the state

isAlive
^^^^^^^

.. java:method::  boolean isAlive()
   :outertype: SimEntity

   Checks if the entity is alive, i.e, it's not finished.

isFinished
^^^^^^^^^^

.. java:method::  boolean isFinished()
   :outertype: SimEntity

   Checks if the entity is finished or not.

isStarted
^^^^^^^^^

.. java:method::  boolean isStarted()
   :outertype: SimEntity

   Checks if the entity already was started or not.

processEvent
^^^^^^^^^^^^

.. java:method::  void processEvent(SimEvent evt)
   :outertype: SimEntity

   Processes events or services that are available for the entity. This method is invoked by the \ :java:ref:`CloudSim`\  class whenever there is an event in the deferred queue, which needs to be processed by the entity.

   :param evt: information about the event just happened

run
^^^

.. java:method:: @Override  void run()
   :outertype: SimEntity

   The run loop to process events fired during the simulation. The events that will be processed are defined in the \ :java:ref:`processEvent(SimEvent)`\  method.

   **See also:** :java:ref:`.processEvent(SimEvent)`

schedule
^^^^^^^^

.. java:method::  boolean schedule(SimEvent evt)
   :outertype: SimEntity

   Sends an event where all data required is defined inside the event instance.

   :param evt: the event to send
   :return: true if the event was sent, false if the simulation was not started yet

schedule
^^^^^^^^

.. java:method::  boolean schedule(double delay, int tag, Object data)
   :outertype: SimEntity

   Sends an event from the entity to itself.

   :param delay: How many seconds after the current simulation time the event should be sent
   :param tag: An user-defined number representing the type of event.
   :param data: The data to be sent with the event.
   :return: true if the event was sent, false if the simulation was not started yet

schedule
^^^^^^^^

.. java:method::  boolean schedule(SimEntity dest, double delay, int tag, Object data)
   :outertype: SimEntity

   Sends an event to another entity.

   :param dest: the destination entity
   :param delay: How many seconds after the current simulation time the event should be sent
   :param tag: An user-defined number representing the type of event.
   :param data: The data to be sent with the event.
   :return: true if the event was sent, false if the simulation was not started yet

schedule
^^^^^^^^

.. java:method::  boolean schedule(SimEntity dest, double delay, int tag)
   :outertype: SimEntity

   Sends an event to another entity with \ **no**\  attached data.

   :param dest: the destination entity
   :param delay: How many seconds after the current simulation time the event should be sent
   :param tag: An user-defined number representing the type of event.
   :return: true if the event was sent, false if the simulation was not started yet

setName
^^^^^^^

.. java:method::  SimEntity setName(String newName) throws IllegalArgumentException
   :outertype: SimEntity

   Sets the Entity name.

   :param newName: the new name
   :throws IllegalArgumentException: when the entity name is \ ``null``\  or empty

setSimulation
^^^^^^^^^^^^^

.. java:method::  SimEntity setSimulation(Simulation simulation)
   :outertype: SimEntity

   Sets the CloudSim instance that represents the simulation the Entity is related to.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to

setState
^^^^^^^^

.. java:method::  SimEntity setState(State state)
   :outertype: SimEntity

shutdownEntity
^^^^^^^^^^^^^^

.. java:method::  void shutdownEntity()
   :outertype: SimEntity

   Shuts down the entity. This method is invoked by the \ :java:ref:`CloudSim`\  before the simulation finishes. If you want to save data in log files this is the method in which the corresponding code would be placed.

start
^^^^^

.. java:method::  void start()
   :outertype: SimEntity

   Starts the entity during simulation start. This method is invoked by the \ :java:ref:`CloudSim`\  class when the simulation is started.

