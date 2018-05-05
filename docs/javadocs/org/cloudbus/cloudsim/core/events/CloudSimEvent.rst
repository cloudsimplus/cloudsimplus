.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

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

.. java:constructor:: public CloudSimEvent(CloudSim simulation, Type type, double time, SimEntity src, SimEntity dest, int tag, Object data)
   :outertype: CloudSimEvent

   Creates a CloudSimEvent.

   :param src: the event to clone

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(CloudSim simulation, Type type, double time, Object data)
   :outertype: CloudSimEvent

   Creates a CloudSimEvent.

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(SimEvent src)
   :outertype: CloudSimEvent

   Creates a CloudSimEvent cloning another given one.

   :param src: the event to clone

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(CloudSim simulation, Type type, double time, SimEntity src)
   :outertype: CloudSimEvent

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEvent event)
   :outertype: CloudSimEvent

eventTime
^^^^^^^^^

.. java:method:: @Override public double eventTime()
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

.. java:method:: @Override public SimEvent setDestination(SimEntity destination)
   :outertype: CloudSimEvent

setSerial
^^^^^^^^^

.. java:method:: @Override public void setSerial(long serial)
   :outertype: CloudSimEvent

setSource
^^^^^^^^^

.. java:method:: @Override public SimEvent setSource(SimEntity source)
   :outertype: CloudSimEvent

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: CloudSimEvent

