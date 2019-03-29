.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.network HostPacket

.. java:import:: java.util List

Switch
======

.. java:package:: org.cloudbus.cloudsim.network.switches
   :noindex:

.. java:type:: public interface Switch extends SimEntity

   Represents a Network Switch.

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Switch NULL
   :outertype: Switch

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Switch`\  objects.

Methods
-------
downlinkTransferDelay
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double downlinkTransferDelay(HostPacket packet, int simultaneousPackets)
   :outertype: Switch

   Considering a list of packets to be sent simultaneously, computes the expected time to transfer each packet through the downlink, assuming that the bandwidth is shared equally between all packets.

   :param simultaneousPackets: number of packets to be simultaneously sent
   :return: the expected transmission time in seconds

getDatacenter
^^^^^^^^^^^^^

.. java:method::  NetworkDatacenter getDatacenter()
   :outertype: Switch

   Gets the Datacenter where the switch is connected to.

getDownlinkBandwidth
^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getDownlinkBandwidth()
   :outertype: Switch

   Gets the bandwidth this Switch has to communicate with Switches in the lower layer.

   :return: Bandwidth of downlink (in Megabits/s).

   **See also:** :java:ref:`.getDownlinkSwitches()`

getDownlinkSwitches
^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Switch> getDownlinkSwitches()
   :outertype: Switch

   Gets the list of Switches in the lower layer that this Switch is connected to.

getLevel
^^^^^^^^

.. java:method::  int getLevel()
   :outertype: Switch

   Gets the level (layer) of the Switch in the network topology, depending if it is a root switch (layer 0), aggregate switch (layer 1) or edge switch (layer 2)

   :return: the switch network level

getPorts
^^^^^^^^

.. java:method::  int getPorts()
   :outertype: Switch

   Gets the number of ports the switch has.

getSwitchingDelay
^^^^^^^^^^^^^^^^^

.. java:method::  double getSwitchingDelay()
   :outertype: Switch

   Gets the latency time the switch spends to process a received packet. This time is considered constant no matter how many packets the switch have to process (in seconds).

   :return: the switching delay

getUplinkBandwidth
^^^^^^^^^^^^^^^^^^

.. java:method::  double getUplinkBandwidth()
   :outertype: Switch

   Gets the bandwidth this Switch has to communicate with Switches in the upper layer.

   :return: Bandwidth of uplink (in Megabits/s).

   **See also:** :java:ref:`.getUplinkSwitches()`

getUplinkSwitches
^^^^^^^^^^^^^^^^^

.. java:method::  List<Switch> getUplinkSwitches()
   :outertype: Switch

   Gets the list of Switches in the upper layer that this Switch is connected to.

setDatacenter
^^^^^^^^^^^^^

.. java:method::  void setDatacenter(NetworkDatacenter datacenter)
   :outertype: Switch

   Sets the Datacenter where the switch is connected to.

   :param datacenter: the Datacenter to set

setDownlinkBandwidth
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setDownlinkBandwidth(double downlinkBandwidth)
   :outertype: Switch

   Sets the bandwidth this Switch has to communicate with Switches in the lower layer.

   :param downlinkBandwidth: downlink bandwidth to set (in Megabits/s).

   **See also:** :java:ref:`.getDownlinkSwitches()`

setPorts
^^^^^^^^

.. java:method::  void setPorts(int ports)
   :outertype: Switch

   Sets the number of ports the switch has.

   :param ports: the number of ports to set

setSwitchingDelay
^^^^^^^^^^^^^^^^^

.. java:method::  void setSwitchingDelay(double switchingDelay)
   :outertype: Switch

   Sets the latency time the switch spends to process a received packet. This time is considered constant no matter how many packets the switch have to process (in seconds).

   :param switchingDelay: the switching delay to set

setUplinkBandwidth
^^^^^^^^^^^^^^^^^^

.. java:method::  void setUplinkBandwidth(double uplinkBandwidth)
   :outertype: Switch

   Sets the bandwidth this Switch has to communicate with Switches in the upper layer.

   :param uplinkBandwidth: uplink bandwidth to set (in Megabits/s).

   **See also:** :java:ref:`.getUplinkSwitches()`

uplinkTransferDelay
^^^^^^^^^^^^^^^^^^^

.. java:method::  double uplinkTransferDelay(HostPacket packet, int simultaneousPackets)
   :outertype: Switch

   Considering a list of packets to be sent simultaneously, computes the expected time to transfer each packet through the uplink, assuming that the bandwidth is shared equally between all packets.

   :param simultaneousPackets: number of packets to be simultaneously sent
   :return: the expected transmission time in seconds

