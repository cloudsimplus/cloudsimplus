.. java:import:: org.apache.commons.lang3 StringUtils

.. java:import:: org.cloudbus.cloudsim.core.events CloudSimEvent

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util Objects

.. java:import:: java.util.function Predicate

CloudSimEntity
==============

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public abstract class CloudSimEntity implements SimEntity

   Represents a simulation entity. An entity handles events and can send events to other entities.

   :author: Marcos Dias de Assuncao

Constructors
------------
CloudSimEntity
^^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEntity(Simulation simulation)
   :outertype: CloudSimEntity

   Creates a new entity.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :throws IllegalArgumentException: when the entity name is invalid

Methods
-------
cancelEvent
^^^^^^^^^^^

.. java:method:: public SimEvent cancelEvent(Predicate<SimEvent> predicate)
   :outertype: CloudSimEntity

   Cancels the first event from the future event queue that matches a given predicate and that was submitted by this entity, then removes it from the queue.

   :param predicate: the event selection predicate
   :return: the removed event or \ :java:ref:`SimEvent.NULL`\  if not found

clone
^^^^^

.. java:method:: @Override protected final Object clone() throws CloneNotSupportedException
   :outertype: CloudSimEntity

   Gets a clone of the entity. This is used when independent replications have been specified as an output analysis method. Clones or backups of the entities are made in the beginning of the simulation in order to reset the entities for each subsequent replication. This method should not be called by the user.

   :throws CloneNotSupportedException: when the entity doesn't support cloning
   :return: A clone of the entity

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEntity entity)
   :outertype: CloudSimEntity

equals
^^^^^^

.. java:method:: @Override public boolean equals(Object object)
   :outertype: CloudSimEntity

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: CloudSimEntity

   Gets the unique id number assigned to this entity.

   :return: The id number

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: CloudSimEntity

   Gets the name of this entity.

   :return: The entity's name

getNextEvent
^^^^^^^^^^^^

.. java:method:: public SimEvent getNextEvent(Predicate<SimEvent> predicate)
   :outertype: CloudSimEntity

   Gets the first event matching a predicate from the deferred queue, or if none match, wait for a matching event to arrive.

   :param predicate: The predicate to match
   :return: the simulation event

getNextEvent
^^^^^^^^^^^^

.. java:method:: public SimEvent getNextEvent()
   :outertype: CloudSimEntity

   Gets the first event waiting in the entity's deferred queue, or if there are none, wait for an event to arrive.

   :return: the simulation event

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: CloudSimEntity

getState
^^^^^^^^

.. java:method:: @Override public State getState()
   :outertype: CloudSimEntity

hashCode
^^^^^^^^

.. java:method:: @Override public int hashCode()
   :outertype: CloudSimEntity

isAlive
^^^^^^^

.. java:method:: @Override public boolean isAlive()
   :outertype: CloudSimEntity

isFinished
^^^^^^^^^^

.. java:method:: @Override public boolean isFinished()
   :outertype: CloudSimEntity

isStarted
^^^^^^^^^

.. java:method:: @Override public boolean isStarted()
   :outertype: CloudSimEntity

numEventsWaiting
^^^^^^^^^^^^^^^^

.. java:method:: public long numEventsWaiting(Predicate<SimEvent> predicate)
   :outertype: CloudSimEntity

   Counts how many events matching a predicate are waiting in the entity's deferred queue.

   :param predicate: The event selection predicate
   :return: The count of matching events

numEventsWaiting
^^^^^^^^^^^^^^^^

.. java:method:: public long numEventsWaiting()
   :outertype: CloudSimEntity

   Counts how many events are waiting in the entity's deferred queue.

   :return: The count of events

pause
^^^^^

.. java:method:: public void pause(double delay)
   :outertype: CloudSimEntity

   Sets the entity to be inactive for a time period.

   :param delay: the time period for which the entity will be inactive

run
^^^

.. java:method:: @Override public void run()
   :outertype: CloudSimEntity

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEntity dest, double delay, int tag, Object data)
   :outertype: CloudSimEntity

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(double delay, int tag, Object data)
   :outertype: CloudSimEntity

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEntity dest, double delay, int tag)
   :outertype: CloudSimEntity

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEvent evt)
   :outertype: CloudSimEntity

scheduleFirst
^^^^^^^^^^^^^

.. java:method:: public void scheduleFirst(SimEntity dest, double delay, int tag)
   :outertype: CloudSimEntity

   Sends a high priority event to another entity and with \ **no**\  attached data.

   :param dest: the destination entity
   :param delay: How many seconds after the current simulation time the event should be sent
   :param tag: An user-defined number representing the type of event.

scheduleFirst
^^^^^^^^^^^^^

.. java:method:: public void scheduleFirst(SimEntity dest, double delay, int tag, Object data)
   :outertype: CloudSimEntity

   Sends a high priority event to another entity.

   :param dest: the destination entity
   :param delay: How many seconds after the current simulation time the event should be sent
   :param tag: An user-defined number representing the type of event.
   :param data: The data to be sent with the event.

scheduleFirstNow
^^^^^^^^^^^^^^^^

.. java:method:: public void scheduleFirstNow(SimEntity dest, int tag, Object data)
   :outertype: CloudSimEntity

   Sends a high priority event to another entity with no delay.

   :param dest: the destination entity
   :param tag: An user-defined number representing the type of event.
   :param data: The data to be sent with the event.

scheduleFirstNow
^^^^^^^^^^^^^^^^

.. java:method:: public void scheduleFirstNow(SimEntity dest, int tag)
   :outertype: CloudSimEntity

   Sends a high priority event to another entity with \ **no**\  attached data and no delay.

   :param dest: the destination entity
   :param tag: An user-defined number representing the type of event.

scheduleNow
^^^^^^^^^^^

.. java:method:: public void scheduleNow(SimEntity dest, int tag, Object data)
   :outertype: CloudSimEntity

   Sends an event to another entity with no delay.

   :param dest: the destination entity
   :param tag: An user-defined number representing the type of event.
   :param data: The data to be sent with the event.

scheduleNow
^^^^^^^^^^^

.. java:method:: public void scheduleNow(SimEntity dest, int tag)
   :outertype: CloudSimEntity

   Sends an event to another entity with \ **no**\  attached data and no delay.

   :param dest: the destination entity
   :param tag: An user-defined number representing the type of event.

selectEvent
^^^^^^^^^^^

.. java:method:: public SimEvent selectEvent(Predicate<SimEvent> predicate)
   :outertype: CloudSimEntity

   Extracts the first event matching a predicate waiting in the entity's deferred queue.

   :param predicate: The event selection predicate
   :return: the simulation event

send
^^^^

.. java:method:: protected void send(SimEntity dest, double delay, int cloudSimTag, Object data)
   :outertype: CloudSimEntity

   Sends an event/message to another entity by \ ``delaying``\  the simulation time from the current time, with a tag representing the event type.

   :param dest: the destination entity
   :param delay: How many seconds after the current simulation time the event should be sent. If delay is a negative number, then it will be changed to 0
   :param cloudSimTag: an user-defined number representing the type of an event/message
   :param data: A reference to data to be sent with the event

send
^^^^

.. java:method:: protected void send(SimEntity dest, double delay, int cloudSimTag)
   :outertype: CloudSimEntity

   Sends an event/message to another entity by \ ``delaying``\  the simulation time from the current time, with a tag representing the event type.

   :param dest: the destination entity
   :param delay: How many seconds after the current simulation time the event should be sent. If delay is a negative number, then it will be changed to 0
   :param cloudSimTag: an user-defined number representing the type of an event/message

sendNow
^^^^^^^

.. java:method:: protected void sendNow(SimEntity dest, int cloudSimTag, Object data)
   :outertype: CloudSimEntity

   Sends an event/message to another entity, with a tag representing the event type.

   :param dest: the destination entity
   :param cloudSimTag: an user-defined number representing the type of an event/message
   :param data: A reference to data to be sent with the event

sendNow
^^^^^^^

.. java:method:: protected void sendNow(SimEntity dest, int cloudSimTag)
   :outertype: CloudSimEntity

   Sends an event/message to another entity, with a tag representing the event type.

   :param dest: the destination entity
   :param cloudSimTag: an user-defined number representing the type of an event/message

setEventBuffer
^^^^^^^^^^^^^^

.. java:method:: protected void setEventBuffer(SimEvent evt)
   :outertype: CloudSimEntity

   Sets the event buffer.

   :param evt: the new event buffer

setId
^^^^^

.. java:method:: protected final void setId(int id)
   :outertype: CloudSimEntity

   Sets the entity id and defines its name based on such ID.

   :param id: the new id

setName
^^^^^^^

.. java:method:: @Override public SimEntity setName(String name) throws IllegalArgumentException
   :outertype: CloudSimEntity

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public final SimEntity setSimulation(Simulation simulation)
   :outertype: CloudSimEntity

setStarted
^^^^^^^^^^

.. java:method:: protected void setStarted(boolean started)
   :outertype: CloudSimEntity

   Defines if the entity has already started or not.

   :param started: the start state to set

setState
^^^^^^^^

.. java:method:: @Override public SimEntity setState(State state)
   :outertype: CloudSimEntity

   Sets the entity state.

   :param state: the new state

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override public void shutdownEntity()
   :outertype: CloudSimEntity

start
^^^^^

.. java:method:: @Override public void start()
   :outertype: CloudSimEntity

   {@inheritDoc}. It performs general initialization tasks that are common for every entity and executes the specific entity startup code by calling \ :java:ref:`startEntity()`\ .

   **See also:** :java:ref:`.startEntity()`

startEntity
^^^^^^^^^^^

.. java:method:: protected abstract void startEntity()
   :outertype: CloudSimEntity

   Defines the logic to be performed by the entity when the simulation starts.

waitForEvent
^^^^^^^^^^^^

.. java:method:: public void waitForEvent(Predicate<SimEvent> predicate)
   :outertype: CloudSimEntity

   Waits for an event matching a specific predicate. This method does not check the entity's deferred queue.

   :param predicate: The predicate to match

