.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.network.topologies NetworkTopology

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: java.util Calendar

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util Set

.. java:import:: java.util.function Predicate

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
ANY_EVT
^^^^^^^

.. java:field::  Predicate<SimEvent> ANY_EVT
   :outertype: Simulation

   A standard predicate that matches any event.

NULL
^^^^

.. java:field::  Simulation NULL
   :outertype: Simulation

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Simulation`\  objects.

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

addOnSimulationPauseListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Simulation addOnSimulationPauseListener(EventListener<EventInfo> listener)
   :outertype: Simulation

   Adds an \ :java:ref:`EventListener`\  object that will be notified when the simulation is paused. When this Listener is notified, it will receive an \ :java:ref:`EventInfo`\  informing the time the pause occurred.

   This object is just information about the event that happened. In fact, it isn't generated an actual {@limk SimEvent} for a pause event because there is not need for that.

   :param listener: the event listener to add

addOnSimulationStartListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Simulation addOnSimulationStartListener(EventListener<EventInfo> listener)
   :outertype: Simulation

cancel
^^^^^^

.. java:method::  SimEvent cancel(SimEntity src, Predicate<SimEvent> p)
   :outertype: Simulation

   Cancels the first event from the future event queue that matches a given predicate and was sent by a given entity, then removes it from the queue.

   :param src: Id of entity that scheduled the event
   :param p: the event selection predicate
   :return: the removed event or \ :java:ref:`SimEvent.NULL`\  if not found

cancelAll
^^^^^^^^^

.. java:method::  boolean cancelAll(SimEntity src, Predicate<SimEvent> p)
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

.. java:method::  SimEvent findFirstDeferred(SimEntity dest, Predicate<SimEvent> p)
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

getCloudInfoService
^^^^^^^^^^^^^^^^^^^

.. java:method::  CloudInformationService getCloudInfoService()
   :outertype: Simulation

   Gets the \ :java:ref:`CloudInformationService`\ .

   :return: the Entity

getDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method::  Set<Datacenter> getDatacenterList()
   :outertype: Simulation

   Sends a request to Cloud Information Service (CIS) entity to get the list of all Cloud Datacenter IDs.

   :return: a List containing Datacenter IDs

getEntityList
^^^^^^^^^^^^^

.. java:method::  List<SimEntity> getEntityList()
   :outertype: Simulation

   Returns a read-only list of entities created for the simulation.

getMinTimeBetweenEvents
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getMinTimeBetweenEvents()
   :outertype: Simulation

   Returns the minimum time between events (in seconds). Events within shorter periods after the last event are discarded.

   :return: the minimum time between events (in seconds).

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

.. java:method::  void holdEntity(SimEntity src, long delay)
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

isTerminationTimeSet
^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isTerminationTimeSet()
   :outertype: Simulation

isTimeToTerminateSimulationUnderRequest
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isTimeToTerminateSimulationUnderRequest()
   :outertype: Simulation

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

.. java:method::  void pauseEntity(SimEntity src, double delay)
   :outertype: Simulation

   Pauses an entity for some time.

   :param src: id of entity to be paused
   :param delay: the time period for which the entity will be inactive

removeOnClockTickListener
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnClockTickListener(EventListener<? extends EventInfo> listener)
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

removeOnSimulationPauseListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnSimulationPauseListener(EventListener<EventInfo> listener)
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

.. java:method::  SimEvent select(SimEntity dest, Predicate<SimEvent> p)
   :outertype: Simulation

   Selects the first deferred event that matches a given predicate and removes it from the queue.

   :param dest: entity that the event has to be sent to
   :param p: the event selection predicate
   :return: the removed event or \ :java:ref:`SimEvent.NULL`\  if not found

send
^^^^

.. java:method::  void send(SimEvent evt)
   :outertype: Simulation

   Sends an event where all data required is defined inside the event instance.

   :param evt: the event to send

send
^^^^

.. java:method::  void send(SimEntity src, SimEntity dest, double delay, int tag, Object data)
   :outertype: Simulation

   Sends an event from one entity to another.

   :param src: entity that scheduled the event
   :param dest: entity that the event will be sent to
   :param delay: How many seconds after the current simulation time the event should be sent
   :param tag: the \ :java:ref:`tag <SimEvent.getTag()>`\  that classifies the event
   :param data: the \ :java:ref:`data <SimEvent.getData()>`\  to be sent inside the event

sendFirst
^^^^^^^^^

.. java:method::  void sendFirst(SimEvent evt)
   :outertype: Simulation

   Sends an event where all data required is defined inside the event instance, adding it to the beginning of the queue in order to give priority to it.

   :param evt: the event to send

sendFirst
^^^^^^^^^

.. java:method::  void sendFirst(SimEntity src, SimEntity dest, double delay, int tag, Object data)
   :outertype: Simulation

   Sends an event from one entity to another, adding it to the beginning of the queue in order to give priority to it.

   :param src: entity that scheduled the event
   :param dest: entity that the event will be sent to
   :param delay: How many seconds after the current simulation time the event should be sent
   :param tag: the \ :java:ref:`tag <SimEvent.getTag()>`\  that classifies the event
   :param data: the \ :java:ref:`data <SimEvent.getData()>`\  to be sent inside the event

sendNow
^^^^^^^

.. java:method::  void sendNow(SimEntity src, SimEntity dest, int tag, Object data)
   :outertype: Simulation

   Sends an event from one entity to another without delaying the message.

   :param src: entity that scheduled the event
   :param dest: entity that the event will be sent to
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

   Starts simulation execution and waits for
   all entities to finish, i.e. until all entities threads reach non-RUNNABLE state or there are no more events in the future event queue.

   \ **Note**\ : This method should be called just after all the entities have been setup and added. The method blocks until the simulation is ended.

   :throws UnsupportedOperationException: When the simulation has already run once. If you paused the simulation and wants to resume it, you must use \ :java:ref:`resume()`\  instead of calling the current method.
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

   Schedules the termination of the simulation for a given time (in seconds).

   If a termination time is set, the simulation stays running even if there is no event to process. It keeps waiting for new dynamic events, such as the creation of Cloudlets and VMs at runtime. If no event happens, the clock is increased to simulate time passing. The clock increment is defined according to: (i) the lower \ :java:ref:`Datacenter.getSchedulingInterval()`\  between existing Datacenters; or (ii) \ :java:ref:`getMinTimeBetweenEvents()`\  in case no \ :java:ref:`Datacenter`\  has its schedulingInterval set.

   :param time: the time at which the simulation has to be terminated (in seconds)
   :return: true if the time given is greater than the current simulation time, false otherwise

wait
^^^^

.. java:method::  void wait(CloudSimEntity src, Predicate<SimEvent> p)
   :outertype: Simulation

   Sets the state of an entity to \ :java:ref:`SimEntity.State.WAITING`\ , making it to wait for events that satisfy a given predicate. Only such events will be passed to the entity. This is done to avoid unnecessary context Datacenter.

   :param src: entity that scheduled the event
   :param p: the event selection predicate

waiting
^^^^^^^

.. java:method::  long waiting(SimEntity dest, Predicate<SimEvent> p)
   :outertype: Simulation

   Gets the number of events in the deferred event queue that are targeted to a given entity and match a given predicate.

   :param dest: Id of entity that the event has to be sent to
   :param p: the event selection predicate

