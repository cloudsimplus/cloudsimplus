.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: java.text DecimalFormat

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

IcmpPacket
==========

.. java:package:: org.cloudbus.cloudsim.network
   :noindex:

.. java:type:: public class IcmpPacket implements NetworkPacket<SimEntity>

   Represents a ping (ICMP protocol) packet that can be used to gather information from the network layer. An IcmpPacket traverses the network topology similar to a \ :java:ref:`HostPacket`\ , but it collects information like bandwidths, and Round Trip Time etc.

   You can set all the parameters to an IcmpPacket that can be applied to a HostPacket. So if you want to find out the kind of information that a particular type of HostPacket is experiencing, set the size and network class of an IcmpPacket to the same as the HostPacket, and send it to the same destination from the same source.

   :author: Gokul Poduval, Chen-Khong Tham, National University of Singapore

Constructors
------------
IcmpPacket
^^^^^^^^^^

.. java:constructor:: public IcmpPacket(String name, int packetID, long size, SimEntity source, SimEntity destination, int netServiceLevel)
   :outertype: IcmpPacket

   Constructs a new ICMP packet.

   :param name: Name of this packet
   :param packetID: the ID of this packet
   :param size: size of the packet
   :param source: the entity that sends out this packet
   :param destination: the entity to which this packet is destined
   :param netServiceLevel: the class of traffic this packet belongs to

Methods
-------
addBaudRate
^^^^^^^^^^^

.. java:method:: public void addBaudRate(double baudRate)
   :outertype: IcmpPacket

   Register the baud rate of the output link where the current entity that holds the IcmpPacket will send it next. Every entity that the IcmpPacket traverses should add the baud rate of the link on which this packet will be sent out next.

   :param baudRate: the entity's baud rate in bits/s

addEntryTime
^^^^^^^^^^^^

.. java:method:: public void addEntryTime(double time)
   :outertype: IcmpPacket

   Register the time the packet arrives at an entity such as a Router or CloudResource. This method should be called by routers and other entities when the IcmpPacket reaches them along with the current simulation time.

   :param time: current simulation time, use \ :java:ref:`org.cloudbus.cloudsim.core.CloudSim.clock()`\  to obtain this

addExitTime
^^^^^^^^^^^

.. java:method:: public void addExitTime(double time)
   :outertype: IcmpPacket

   Register the time the packet leaves an entity such as a Router or CloudResource. This method should be called by routers and other entities when the IcmpPacket is leaving them. It should also supply the current simulation time.

   :param time: current simulation time, use \ :java:ref:`org.cloudbus.cloudsim.core.CloudSim.clock()`\  to obtain this

addHop
^^^^^^

.. java:method:: public void addHop(SimEntity entity)
   :outertype: IcmpPacket

   Add an entity where the IcmpPacket traverses. This method should be called by network entities that count as hops, for instance Routers or CloudResources. It should not be called by links etc.

   :param entity: the id of the hop that this IcmpPacket is traversing

getBaudRate
^^^^^^^^^^^

.. java:method:: public double getBaudRate()
   :outertype: IcmpPacket

   Gets the bottleneck bandwidth between the source and the destination.

   :return: the bottleneck bandwidth

getDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public SimEntity getDestination()
   :outertype: IcmpPacket

getDetailBaudRate
^^^^^^^^^^^^^^^^^

.. java:method:: public List<Double> getDetailBaudRate()
   :outertype: IcmpPacket

   Gets a \ **read-only**\  list of all the bandwidths that this packet has traversed.

getDetailEntryTimes
^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Double> getDetailEntryTimes()
   :outertype: IcmpPacket

   Gets a \ **read-only**\  list of all entry times that the packet has traversed.

getDetailExitTimes
^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Double> getDetailExitTimes()
   :outertype: IcmpPacket

   Gets a \ **read-only**\  list of all exit times from all entities that the packet has traversed.

getHopsList
^^^^^^^^^^^

.. java:method:: public List<SimEntity> getHopsList()
   :outertype: IcmpPacket

   Gets a \ **read-only**\  list of all entities that this packet has traversed, that defines the hops it has made.

getId
^^^^^

.. java:method:: public int getId()
   :outertype: IcmpPacket

   Returns the ID of this packet

   :return: packet ID

getLastHop
^^^^^^^^^^

.. java:method:: public SimEntity getLastHop()
   :outertype: IcmpPacket

   Gets the entity that was the last hop where this packet has traversed.

getNetServiceLevel
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getNetServiceLevel()
   :outertype: IcmpPacket

   Gets the network service type of this packet

   :return: the network service type

getNumberOfHops
^^^^^^^^^^^^^^^

.. java:method:: public int getNumberOfHops()
   :outertype: IcmpPacket

   Gets the number of hops that the packet has traversed. Since the packet takes a round trip, the same router may have been traversed twice.

getReceiveTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double getReceiveTime()
   :outertype: IcmpPacket

getSendTime
^^^^^^^^^^^

.. java:method:: @Override public double getSendTime()
   :outertype: IcmpPacket

getSize
^^^^^^^

.. java:method:: @Override public long getSize()
   :outertype: IcmpPacket

getSource
^^^^^^^^^

.. java:method:: @Override public SimEntity getSource()
   :outertype: IcmpPacket

getTag
^^^^^^

.. java:method:: public int getTag()
   :outertype: IcmpPacket

   Gets the packet direction that indicates if it is going or returning. The direction can be \ :java:ref:`CloudSimTags.ICMP_PKT_SUBMIT`\  or \ :java:ref:`CloudSimTags.ICMP_PKT_RETURN`\ .

getTotalResponseTime
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getTotalResponseTime()
   :outertype: IcmpPacket

   Gets the total time that the packet has spent in the network. This is basically the Round-Trip Time (RTT). Dividing this by half should be the approximate latency.

   RTT is taken as the "final entry time" - "first exit time".

   :return: total round-trip time

setDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public void setDestination(SimEntity destination)
   :outertype: IcmpPacket

setLastHop
^^^^^^^^^^

.. java:method:: public void setLastHop(SimEntity entity)
   :outertype: IcmpPacket

   Sets the entity that was the last hop where this packet has traversed.

   :param entity: the entity to set as the last hop

setNetServiceLevel
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setNetServiceLevel(int netServiceLevel)
   :outertype: IcmpPacket

   Sets the network service type of this packet.

   By default, the service type is 0 (zero). It is depends on the packet scheduler to determine the priority of this service level.

   :param netServiceLevel: the service level to set

setReceiveTime
^^^^^^^^^^^^^^

.. java:method:: @Override public void setReceiveTime(double time)
   :outertype: IcmpPacket

setSendTime
^^^^^^^^^^^

.. java:method:: @Override public void setSendTime(double time)
   :outertype: IcmpPacket

setSize
^^^^^^^

.. java:method:: public boolean setSize(long size)
   :outertype: IcmpPacket

   Sets the size of the packet.

   :param size: the size to set
   :return: \ ``true``\  if a positive value was given, \ ``false``\  otherwise

setSource
^^^^^^^^^

.. java:method:: @Override public void setSource(SimEntity source)
   :outertype: IcmpPacket

setTag
^^^^^^

.. java:method:: public boolean setTag(int tag)
   :outertype: IcmpPacket

   Sets the packet direction that indicates if it is going or returning. The direction can be \ :java:ref:`CloudSimTags.ICMP_PKT_SUBMIT`\  or \ :java:ref:`CloudSimTags.ICMP_PKT_RETURN`\ .

   :param tag: the direction to set
   :return: true if the tag is valid, false otherwise

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: IcmpPacket

   Returns a human-readable information of this packet.

   :return: description of this packet

