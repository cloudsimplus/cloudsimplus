.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core Simulation

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

.. java:constructor:: public CloudSimEvent(CloudSim simulation)
   :outertype: CloudSimEvent

   Creates a blank event.

   :param simulation: the simulation to which the event belongs to

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(SimEvent eventToClone)
   :outertype: CloudSimEvent

   Creates an CloudSimEvent cloning another given one.

   :param eventToClone: the event to clone

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(CloudSim simulation, Type type, double time, int src, int dest, int tag, Object data)
   :outertype: CloudSimEvent

CloudSimEvent
^^^^^^^^^^^^^

.. java:constructor:: public CloudSimEvent(CloudSim simulation, Type type, double time, int src)
   :outertype: CloudSimEvent

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEvent event)
   :outertype: CloudSimEvent

endWaitingTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double endWaitingTime()
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

.. java:method:: @Override public int getDestination()
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

.. java:method:: @Override public int getSource()
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

.. java:method:: @Override public int scheduledBy()
   :outertype: CloudSimEvent

setDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public SimEvent setDestination(int destination)
   :outertype: CloudSimEvent

setSerial
^^^^^^^^^

.. java:method:: @Override public void setSerial(long serial)
   :outertype: CloudSimEvent

setSource
^^^^^^^^^

.. java:method:: @Override public SimEvent setSource(int source)
   :outertype: CloudSimEvent

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: CloudSimEvent

