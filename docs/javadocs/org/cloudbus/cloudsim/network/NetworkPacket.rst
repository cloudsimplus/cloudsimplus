.. java:import:: org.cloudbus.cloudsim.core Identifiable

NetworkPacket
=============

.. java:package:: org.cloudbus.cloudsim.network
   :noindex:

.. java:type:: public interface NetworkPacket<T extends Identifiable>

   Defines the structure for a network packet.

   :author: Gokul Poduval, Chen-Khong Tham, National University of Singapore, Manoel Campos da Silva Filho
   :param <T>: the class of objects involved in the packet transmission, if they are Hosts, VMs, Switches, etc.

Methods
-------
getDestination
^^^^^^^^^^^^^^

.. java:method::  T getDestination()
   :outertype: NetworkPacket

   Gets the entity that the packet is going to.

getReceiveTime
^^^^^^^^^^^^^^

.. java:method::  double getReceiveTime()
   :outertype: NetworkPacket

   Gets the time when the packet was received.

getSendTime
^^^^^^^^^^^

.. java:method::  double getSendTime()
   :outertype: NetworkPacket

   Gets the time when the packet was sent.

getSize
^^^^^^^

.. java:method::  long getSize()
   :outertype: NetworkPacket

   Gets the size of the packet in bytes.

getSource
^^^^^^^^^

.. java:method::  T getSource()
   :outertype: NetworkPacket

   Gets the entity that this packet is coming from (the sender).

setDestination
^^^^^^^^^^^^^^

.. java:method::  void setDestination(T destination)
   :outertype: NetworkPacket

   Sets the entity that the packet is going to (the receiver).

   :param destination: the destination to set

setReceiveTime
^^^^^^^^^^^^^^

.. java:method::  void setReceiveTime(double time)
   :outertype: NetworkPacket

   Sets the time when the packet was received.

   :param time: the time to set

setSendTime
^^^^^^^^^^^

.. java:method::  void setSendTime(double time)
   :outertype: NetworkPacket

   Sets the time when the packet was sent.

   :param time: the time to set

setSource
^^^^^^^^^

.. java:method::  void setSource(T source)
   :outertype: NetworkPacket

   Sets the entity that this packet is coming from (the sender).

   :param source: the source ID to set

