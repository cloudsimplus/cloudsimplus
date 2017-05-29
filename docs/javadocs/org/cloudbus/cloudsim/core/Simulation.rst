.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: java.util.function Predicate

.. java:import:: org.cloudbus.cloudsim.core.predicates PredicateAny

.. java:import:: org.cloudbus.cloudsim.core.predicates PredicateNone

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.network.topologies NetworkTopology

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

Simulation
==========

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public interface Simulation

   An interface to be implemented by a class that manages simulation execution, controlling all the simulation life cycle.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

   **See also:** :java:ref:`CloudSim`

Fields
------
NULL
^^^^

.. java:field::  Simulation NULL
   :outertype: Simulation

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Simulation`\  objects.

SIM_ANY
^^^^^^^

.. java:field::  PredicateAny SIM_ANY
   :outertype: Simulation

   A standard predicate that matches any event.

SIM_NONE
^^^^^^^^

.. java:field::  PredicateNone SIM_NONE
   :outertype: Simulation

   A standard predicate that does not match any events.

Methods
-------
abort
^^^^^

.. java:method::  void abort()
   :outertype: Simulation

   Aborts the simulation without finishing the processing of entities in the \ :java:ref:`entities list <getEntityList()>`\ , what may give
   unexpected results.

   \ **Use this method just if you want to abandon the simulation an usually ignore the results.**\

addEntity
^^^^^^^^^

.. java:method::  void addEntity(CloudSimEntity e)
   :outertype: Simulation

   Adds a new entity to the simulation. Each \ :java:ref:`CloudSimEntity`\  object register itself when it is instantiated.

   :param e: The new entity

addOnClockTickListener
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Simulation addOnClockTickListener(EventListener<EventInfo> listener)
   :outertype: Simulation

   Adds a \ :java:ref:`EventListener`\  object that will be notified every time when the simulation clock advances. Notifications are sent in a second interval to avoid notification flood. Thus, if the clock changes, for instance, from 1.0, to 1.1, 2.0, 2.1, 2.2, 2.5 and then 3.2, notifications will just be sent for the times 1, 2 and 3 that represent the integer part of the simulation time.

   :param listener: the event listener to add

addOnEventProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Simulation addOnEventProcessingListener(EventListener<SimEvent> listener)
   :outertype: Simulation

   Adds a \ :java:ref:`EventListener`\  object that will be notified when any event is processed by CloudSim. When this Listener is notified, it will receive the \ :java:ref:`SimEvent`\  that was processed.

   :param listener: the event listener to add

addOnSimulationPausedListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Simulation addOnSimulationPausedListener(EventListener<EventInfo> listener)
   :outertype: Simulation

   Adds an \ :java:ref:`EventListener`\  object that will be notified when the simulation is paused. When this Listener is notified, it will receive an \ :java:ref:`EventInfo`\  informing the time the pause occurred.

   This object is just information about the event that happened. In fact, it isn't generated an actual {@limk SimEvent} for a pause event because there is not need for that.

   :param listener: the event listener to add

cancel
^^^^^^

.. java:method::  SimEvent cancel(int src, Predicate<SimEvent> p)
   :outertype: Simulation

   Cancels the first event from the future event queue that matches a given predicate and was sent by a given entity, then removes it from the queue.

   :param src: Id of entity that scheduled the event
   :param p: the event selection predicate
   :return: the removed event or \ :java:ref:`SimEvent.NULL`\  if not found

cancelAll
^^^^^^^^^

.. java:method::  boolean cancelAll(int src, Predicate<SimEvent> p)
   :outertype: Simulation

   Cancels all events from the future event queue that matches a given predicate and were sent by a given entity, then removes those ones from the queue.

   :param src: Id of entity that scheduled the event
   :param p: the event selection predicate
   :return: true if at least one event has been cancelled; false otherwise

clock
^^^^^

.. java:method::  double clock()
   :outertype: Simulation

   Gets the current simulation time in seconds.

   **See also:** :java:ref:`.isRunning()`

clockInHours
^^^^^^^^^^^^

.. java:method::  double clockInHours()
   :outertype: Simulation

   Gets the current simulation time in hours.

   **See also:** :java:ref:`.isRunning()`

clockInMinutes
^^^^^^^^^^^^^^

.. java:method::  double clockInMinutes()
   :outertype: Simulation

   Gets the current simulation time in minutes.

   **See also:** :java:ref:`.isRunning()`

findFirstDeferred
^^^^^^^^^^^^^^^^^

.. java:method::  SimEvent findFirstDeferred(int dest, Predicate<SimEvent> p)
   :outertype: Simulation

   Find first deferred event matching a predicate.

   :param dest: Id of entity that the event has to be sent to
   :param p: the event selection predicate
   :return: the first matched event or \ :java:ref:`SimEvent.NULL`\  if not found

getCalendar
^^^^^^^^^^^

.. java:method::  Calendar getCalendar()
   :outertype: Simulation

   Gets a new copy of initial simulation Calendar.

   :return: a new copy of Calendar object

getCloudInfoServiceEntityId
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  int getCloudInfoServiceEntityId()
   :outertype: Simulation

   Gets the entity ID of \ :java:ref:`CloudInformationService`\ .

   :return: the Entity ID or if it is not found

getDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method::  Set<Datacenter> getDatacenterList()
   :outertype: Simulation

   Sends a request to Cloud Information Service (CIS) entity to get the list of all Cloud Datacenter IDs.

   :return: a List containing Datacenter IDs

getEntitiesByName
^^^^^^^^^^^^^^^^^

.. java:method::  Map<String, SimEntity> getEntitiesByName()
   :outertype: Simulation

   Gets a \ **read-only**\  map where each key is the name of an \ :java:ref:`SimEntity`\  and each value is the actual \ :java:ref:`SimEntity`\ .

getEntity
^^^^^^^^^

.. java:method::  SimEntity getEntity(int id)
   :outertype: Simulation

   Get the entity with a given id.

   :param id: the entity's unique id number
   :return: The entity, or if it could not be found

getEntity
^^^^^^^^^

.. java:method::  SimEntity getEntity(String name)
   :outertype: Simulation

   Get the entity with a given name.

   :param name: The entity's name
   :return: The entity

getEntityId
^^^^^^^^^^^

.. java:method::  int getEntityId(String name)
   :outertype: Simulation

   Get the id of an entity with a given name.

   :param name: The entity's name
   :return: The entity's unique id number

getEntityList
^^^^^^^^^^^^^

.. java:method::  List<SimEntity> getEntityList()
   :outertype: Simulation

   Returns a read-only list of entities created for the simulation.

getEntityName
^^^^^^^^^^^^^

.. java:method::  String getEntityName(int entityId)
   :outertype: Simulation

   Gets name of the entity given its entity ID.

   :param entityId: the entity ID
   :return: the Entity name or if this object does not have one

getMinTimeBetweenEvents
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getMinTimeBetweenEvents()
   :outertype: Simulation

   Returns the minimum time between events. Events within shorter periods after the last event are discarded.

   :return: the minimum time between events.

getNetworkTopology
^^^^^^^^^^^^^^^^^^

.. java:method::  NetworkTopology getNetworkTopology()
   :outertype: Simulation

   Gets the network topology used for Network simulations.

getNumEntities
^^^^^^^^^^^^^^

.. java:method::  int getNumEntities()
   :outertype: Simulation

   Get the current number of entities in the simulation.

   :return: The number of entities

getNumberOfFutureEvents
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getNumberOfFutureEvents(Predicate<SimEvent> predicate)
   :outertype: Simulation

   Gets the number of events in the future queue which match a given predicate.

   :param predicate: the predicate to filter the list of future events.
   :return: the number of future events which match the predicate

holdEntity
^^^^^^^^^^

.. java:method::  void holdEntity(int src, long delay)
   :outertype: Simulation

   Holds an entity for some time.

   :param src: id of entity to be held
   :param delay: How many seconds after the current time the entity has to be held

isPaused
^^^^^^^^

.. java:method::  boolean isPaused()
   :outertype: Simulation

   Checks if the simulation is paused.

isRunning
^^^^^^^^^

.. java:method::  boolean isRunning()
   :outertype: Simulation

   Check if the simulation is still running. Even if the simulation \ :java:ref:`is paused <isPaused()>`\ , the method returns true to indicate that the simulation is in fact active yet.

   This method should be used by entities to check if they should continue executing.

pause
^^^^^

.. java:method::  boolean pause()
   :outertype: Simulation

   Requests the simulation to be paused as soon as possible.

   :return: true if the simulation was paused, false if it was already paused or has finished

pause
^^^^^

.. java:method::  boolean pause(double time)
   :outertype: Simulation

   Requests the simulation to be paused at a given time. The method schedules the pause request and then returns immediately.

   :param time: the time at which the simulation has to be paused
   :return: true if pause request was successfully received (the given time is greater than or equal to the current simulation time), false otherwise.

pauseEntity
^^^^^^^^^^^

.. java:method::  void pauseEntity(int src, double delay)
   :outertype: Simulation

   Pauses an entity for some time.

   :param src: id of entity to be paused
   :param delay: the time period for which the entity will be inactive

removeOnClockTickListener
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnClockTickListener(EventListener<EventInfo> listener)
   :outertype: Simulation

   Removes a listener from the onClockTickListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

removeOnEventProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnEventProcessingListener(EventListener<SimEvent> listener)
   :outertype: Simulation

   Removes a listener from the onEventProcessingListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

removeOnSimulationPausedListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnSimulationPausedListener(EventListener<EventInfo> listener)
   :outertype: Simulation

   Removes a listener from the onSimulationPausedListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

resume
^^^^^^

.. java:method::  boolean resume()
   :outertype: Simulation

   This method is called if one wants to resume the simulation that has previously been paused.

   :return: true if the simulation has been restarted or false if it wasn't paused.

select
^^^^^^

.. java:method::  SimEvent select(int dest, Predicate<SimEvent> p)
   :outertype: Simulation

   Selects the first deferred event that matches a given predicate and removes it from the queue.

   :param dest: Id of entity that the event has to be sent to
   :param p: the event selection predicate
   :return: the removed event or \ :java:ref:`SimEvent.NULL`\  if not found

send
^^^^

.. java:method::  void send(int src, int dest, double delay, int tag, Object data)
   :outertype: Simulation

   Sends an event from one entity to another.

   :param src: Id of entity that scheduled the event
   :param dest: Id of entity that the event will be sent to
   :param delay: How many seconds after the current simulation time the event should be sent
   :param tag: the \ :java:ref:`tag <SimEvent.getTag()>`\  that classifies the event
   :param data: the \ :java:ref:`data <SimEvent.getData()>`\  to be sent inside the event

sendFirst
^^^^^^^^^

.. java:method::  void sendFirst(int src, int dest, double delay, int tag, Object data)
   :outertype: Simulation

   Sends an event from one entity to another, adding it to the beginning of the queue in order to give priority to it.

   :param src: Id of entity that scheduled the event
   :param dest: Id of entity that the event will be sent to
   :param delay: How many seconds after the current simulation time the event should be sent
   :param tag: the \ :java:ref:`tag <SimEvent.getTag()>`\  that classifies the event
   :param data: the \ :java:ref:`data <SimEvent.getData()>`\  to be sent inside the event

sendNow
^^^^^^^

.. java:method::  void sendNow(int src, int dest, int tag, Object data)
   :outertype: Simulation

   Sends an event from one entity to another without delaying the message.

   :param src: Id of entity that scheduled the event
   :param dest: Id of entity that the event will be sent to
   :param tag: the \ :java:ref:`tag <SimEvent.getTag()>`\  that classifies the event
   :param data: the \ :java:ref:`data <SimEvent.getData()>`\  to be sent inside the event

setIdForEntitiesWithoutOne
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: static <T extends ChangeableId> boolean setIdForEntitiesWithoutOne(List<? extends T> list)
   :outertype: Simulation

   Defines IDs for a list of \ :java:ref:`ChangeableId`\  entities that don't have one already assigned. Such entities can be a \ :java:ref:`Cloudlet`\ , \ :java:ref:`Vm`\  or any object that implements \ :java:ref:`ChangeableId`\ .

   :param <T>: the type of entities to define an ID
   :param list: list of objects to define an ID
   :return: true if the List has any Entity, false if it's empty

setIdForEntitiesWithoutOne
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: static <T extends ChangeableId> boolean setIdForEntitiesWithoutOne(List<? extends T> list, T lastEntity)
   :outertype: Simulation

   Defines IDs for a list of \ :java:ref:`ChangeableId`\  entities that don't have one already assigned. Such entities can be a \ :java:ref:`Cloudlet`\ , \ :java:ref:`Vm`\  or any object that implements \ :java:ref:`ChangeableId`\ .

   :param <T>: the type of entities to define an ID
   :param list: list of objects to define an ID
   :param lastEntity: the last created Entity which its ID will be used as the base for the next IDs
   :return: true if the List has any Entity, false if it's empty

setNetworkTopology
^^^^^^^^^^^^^^^^^^

.. java:method::  void setNetworkTopology(NetworkTopology networkTopology)
   :outertype: Simulation

   Sets the network topology used for Network simulations.

   :param networkTopology: the network topology to set

start
^^^^^

.. java:method::  double start()
   :outertype: Simulation

   Starts the execution of CloudSim simulation and waits for complete
   execution of all entities, i.e. until all entities threads reach non-RUNNABLE state or there are no more events in the future event queue.

   \ **Note**\ : This method should be called just after all the entities have been setup and added.

   :throws RuntimeException: When the simulation already run once. If you paused the simulation and wants to resume it, you must use \ :java:ref:`resume()`\  instead of calling the current method.
   :return: the last clock time

terminate
^^^^^^^^^

.. java:method::  boolean terminate()
   :outertype: Simulation

   Forces the termination of the simulation before it ends.

   :return: true if the simulation was running and the termination request was accepted, false if the simulation was not started yet

terminateAt
^^^^^^^^^^^

.. java:method::  boolean terminateAt(double time)
   :outertype: Simulation

   Schedules the termination of the simulation for a given time before it has completely finished.

   :param time: the time at which the simulation has to be terminated
   :return: true if the time given is greater than the current simulation time, false otherwise

updateEntityName
^^^^^^^^^^^^^^^^

.. java:method::  boolean updateEntityName(String oldName)
   :outertype: Simulation

   Removes an entity with and old name from the \ :java:ref:`getEntitiesByName()`\  map and adds it again using its new name.

   :param oldName: the name the entity had before
   :return: true if the entity was found and changed into the list, false otherwise

wait
^^^^

.. java:method::  void wait(CloudSimEntity src, Predicate<SimEvent> p)
   :outertype: Simulation

   Sets the state of an entity to \ :java:ref:`SimEntity.State.WAITING`\ , making it to wait for events that satisfy a given predicate. Only such events will be passed to the entity. This is done to avoid unnecessary context Datacenter.

   :param src: entity that scheduled the event
   :param p: the event selection predicate

waiting
^^^^^^^

.. java:method::  long waiting(int dest, Predicate<SimEvent> p)
   :outertype: Simulation

   Gets the number of events in the deferred event queue that are targeted to a given entity and match a given predicate.

   :param dest: Id of entity that the event has to be sent to
   :param p: the event selection predicate

