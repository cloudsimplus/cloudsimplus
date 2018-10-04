.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: java.util Objects

CloudSimEvent
=============

.. java:package:: org.cloudbus.cloudsim.core.events
   :noindex:

.. java:type:: public final class CloudSimEvent implements SimEvent

   This class represents a simulation event which is passed between the entities in the simulation.

   :author: Costas Simatos

   **See also:** :java:ref:`CloudSim`, :java:ref:`SimEntity`

Constructors
------------
CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(double delay, SimEntity src, SimEntity dest, int tag, Object data)
   :outertype: CloudSimEvent

   Creates a \ :java:ref:`Type.SEND`\  CloudSimEvent.

   :param delay: how many seconds after the current simulation time the event should be scheduled
   :param src: the source entity which is sending the message
   :param dest: the source entity which has to receive the message
   :param tag: the tag that identifies the type of the message (which is used by the destination entity to perform operations based on the message type)
   :param data: the data attached to the message, that depends on the message tag

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(double delay, SimEntity dest, int tag, Object data)
   :outertype: CloudSimEvent

   Creates a \ :java:ref:`Type.SEND`\  CloudSimEvent where the sender and destination are the same entity.

   :param delay: how many seconds after the current simulation time the event should be scheduled
   :param dest: the source entity which has to receive the message
   :param tag: the tag that identifies the type of the message (which is used by the destination entity to perform operations based on the message type)
   :param data: the data attached to the message, that depends on the message tag

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(SimEntity dest, int tag)
   :outertype: CloudSimEvent

   Creates a \ :java:ref:`Type.SEND`\  CloudSimEvent where the sender and destination are the same entity, the message has no delay and no data.

   :param dest: the source entity which has to receive the message
   :param tag: the tag that identifies the type of the message (which is used by the destination entity to perform operations based on the message type)

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(Type type, double delay, SimEntity src)
   :outertype: CloudSimEvent

   Creates a CloudSimEvent where the destination entity and tag are not set yet. Furthermore, there will be not data associated to the event.

   :param delay: how many seconds after the current simulation time the event should be scheduled

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(SimEvent src)
   :outertype: CloudSimEvent

   Creates a CloudSimEvent cloning another given one.

   :param src: the event to clone

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(Type type, double delay, SimEntity src, SimEntity dest, int tag, Object data)
   :outertype: CloudSimEvent

   Creates a CloudSimEvent.

   :param type: the internal type of the event
   :param delay: how many seconds after the current simulation time the event should be scheduled
   :param src: the source entity which is sending the message
   :param dest: the source entity which has to receive the message
   :param tag: the tag that identifies the type of the message (which is used by the destination entity to perform operations based on the message type)
   :param data: the data attached to the message, that depends on the message tag

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEvent evt)
   :outertype: CloudSimEvent

getData
^^^^^^^

.. java:method:: @Override public Object getData()
   :outertype: CloudSimEvent

getDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public SimEntity getDestination()
   :outertype: CloudSimEvent

getEndWaitingTime
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getEndWaitingTime()
   :outertype: CloudSimEvent

getListener
^^^^^^^^^^^

.. java:method:: @Override public EventListener<? extends EventInfo> getListener()
   :outertype: CloudSimEvent

getSerial
^^^^^^^^^

.. java:method:: @Override public long getSerial()
   :outertype: CloudSimEvent

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: CloudSimEvent

getSource
^^^^^^^^^

.. java:method:: @Override public SimEntity getSource()
   :outertype: CloudSimEvent

getTag
^^^^^^

.. java:method:: @Override public int getTag()
   :outertype: CloudSimEvent

getTime
^^^^^^^

.. java:method:: @Override public double getTime()
   :outertype: CloudSimEvent

getType
^^^^^^^

.. java:method:: @Override public Type getType()
   :outertype: CloudSimEvent

scheduledBy
^^^^^^^^^^^

.. java:method:: @Override public SimEntity scheduledBy()
   :outertype: CloudSimEvent

setDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public final SimEvent setDestination(SimEntity destination)
   :outertype: CloudSimEvent

setSerial
^^^^^^^^^

.. java:method:: @Override public void setSerial(long serial)
   :outertype: CloudSimEvent

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public final SimEvent setSimulation(Simulation simulation)
   :outertype: CloudSimEvent

setSource
^^^^^^^^^

.. java:method:: @Override public final SimEvent setSource(SimEntity source)
   :outertype: CloudSimEvent

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: CloudSimEvent

