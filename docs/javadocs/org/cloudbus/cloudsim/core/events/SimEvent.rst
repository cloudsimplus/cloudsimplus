.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudsimplus.listeners EventInfo

SimEvent
========

.. java:package:: org.cloudbus.cloudsim.core.events
   :noindex:

.. java:type:: public interface SimEvent extends Comparable<SimEvent>, EventInfo

   Represents a simulation event which is passed between the entities in a specific \ :java:ref:`Simulation`\  instance.

   :author: Costas Simatos, Manoel Campos da Silva Filho

   **See also:** :java:ref:`CloudSimEvent`

Fields
------
NULL
^^^^

.. java:field::  SimEvent NULL
   :outertype: SimEvent

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`SimEvent`\  objects.

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override  int compareTo(SimEvent evt)
   :outertype: SimEvent

getData
^^^^^^^

.. java:method::  Object getData()
   :outertype: SimEvent

   Gets the data object passed in this event. The actual class of this data is defined by the entity that generates the event. The value defined for the \ :java:ref:`getTag()`\  is used by an entity receiving the event to know what is the class of the data associated to the event. After checking what is the event tag, te destination entity then can perform a typecast to convert the data to the expected class.

   :return: a reference to the data object

getDestination
^^^^^^^^^^^^^^

.. java:method::  SimEntity getDestination()
   :outertype: SimEvent

   Gets the entity which received this event.

getEndWaitingTime
^^^^^^^^^^^^^^^^^

.. java:method::  double getEndWaitingTime()
   :outertype: SimEvent

   Gets the simulation time that this event was removed from the queue for service.

getSerial
^^^^^^^^^

.. java:method::  long getSerial()
   :outertype: SimEvent

   Gets the serial number that defines the order of received events when multiple events are generated at the same time. If two events have the same \ :java:ref:`getTag()`\ , to know what event is greater than other (i.e. that happens after other), the \ :java:ref:`compareTo(SimEvent)`\  makes use of this field.

getSimulation
^^^^^^^^^^^^^

.. java:method::  Simulation getSimulation()
   :outertype: SimEvent

   Gets the CloudSim instance that represents the simulation for with the Entity is related to.

getSource
^^^^^^^^^

.. java:method::  SimEntity getSource()
   :outertype: SimEvent

   Gets the entity which scheduled this event.

getTag
^^^^^^

.. java:method::  int getTag()
   :outertype: SimEvent

   Gets the user-defined tag of this event. The meaning of such a tag depends on the entities that generate and receive the event. Usually it is defined from a constant value defined in \ :java:ref:`CloudSimTags`\ .

getType
^^^^^^^

.. java:method::  Type getType()
   :outertype: SimEvent

   Gets the internal type

scheduledBy
^^^^^^^^^^^

.. java:method::  SimEntity scheduledBy()
   :outertype: SimEvent

   Gets the entity which scheduled this event.

setDestination
^^^^^^^^^^^^^^

.. java:method::  SimEvent setDestination(SimEntity destination)
   :outertype: SimEvent

   Sets the destination entity of this event, that defines its destination.

   :param destination: the unique id number of the destination entity

setSerial
^^^^^^^^^

.. java:method::  void setSerial(long serial)
   :outertype: SimEvent

   Sets the serial number that defines the order of received events when multiple events are generated at the same time.

   :param serial: the serial value to set

setSimulation
^^^^^^^^^^^^^

.. java:method::  SimEvent setSimulation(Simulation simulation)
   :outertype: SimEvent

   Sets the simulation the event belongs to

   :param simulation: the simulation instance to set

setSource
^^^^^^^^^

.. java:method::  SimEvent setSource(SimEntity source)
   :outertype: SimEvent

   Sets the source entity of this event, that defines its sender.

   :param source: the unique id number of the source entity

