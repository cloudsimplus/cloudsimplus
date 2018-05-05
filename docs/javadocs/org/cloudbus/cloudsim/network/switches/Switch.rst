.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.hosts.network NetworkHost

.. java:import:: org.cloudbus.cloudsim.network HostPacket

.. java:import:: java.util List

.. java:import:: java.util Map

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
addPacketToBeSentToDownlinkSwitch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void addPacketToBeSentToDownlinkSwitch(Switch downlinkSwitch, HostPacket packet)
   :outertype: Switch

addPacketToBeSentToHost
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void addPacketToBeSentToHost(NetworkHost host, HostPacket packet)
   :outertype: Switch

addPacketToBeSentToUplinkSwitch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void addPacketToBeSentToUplinkSwitch(Switch uplinkSwitch, HostPacket packet)
   :outertype: Switch

connectHost
^^^^^^^^^^^

.. java:method::  void connectHost(NetworkHost host)
   :outertype: Switch

   Connects a \ :java:ref:`NetworkHost`\  to the switch, by adding it to the \ :java:ref:`getHostList()`\ .

   :param host: the host to be connected to the switch

disconnectHost
^^^^^^^^^^^^^^

.. java:method::  boolean disconnectHost(NetworkHost host)
   :outertype: Switch

   Disconnects a \ :java:ref:`NetworkHost`\  from the switch, by removing it from the \ :java:ref:`getHostList()`\ .

   :param host: the host to be disconnected from the switch
   :return: true if the Host was connected to the switch, false otherwise

getDatacenter
^^^^^^^^^^^^^

.. java:method::  NetworkDatacenter getDatacenter()
   :outertype: Switch

   Gets the Datacenter where the switch is connected to.

getDownlinkBandwidth
^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getDownlinkBandwidth()
   :outertype: Switch

   :return: Bandwitdh of downlink (in Megabits/s).

getDownlinkSwitchPacketList
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<HostPacket> getDownlinkSwitchPacketList(Switch downlinkSwitch)
   :outertype: Switch

   Gets the list of packets to be sent to a downlink switch.

   :param downlinkSwitch: the id of the switch to get the list of packets to send
   :return: the list of packets to be sent to the given switch.

getDownlinkSwitches
^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Switch> getDownlinkSwitches()
   :outertype: Switch

getHostList
^^^^^^^^^^^

.. java:method::  List<NetworkHost> getHostList()
   :outertype: Switch

   Gets a \ **read-only**\  list of Hosts connected to the switch.

getHostPacketList
^^^^^^^^^^^^^^^^^

.. java:method::  List<HostPacket> getHostPacketList(NetworkHost host)
   :outertype: Switch

   Gets the list of packets to be sent to a host.

   :param host: the host to get the list of packets to send
   :return: the list of packets to be sent to the given host.

getLevel
^^^^^^^^

.. java:method::  int getLevel()
   :outertype: Switch

   Gets the level (layer) of the AbstractSwitch in the network topology, depending if it is a root switch (layer 0), aggregate switch (layer 1) or edge switch (layer 2)

   :return: the switch network level

getPacketList
^^^^^^^^^^^^^

.. java:method::  List<HostPacket> getPacketList()
   :outertype: Switch

getPacketToHostMap
^^^^^^^^^^^^^^^^^^

.. java:method::  Map<NetworkHost, List<HostPacket>> getPacketToHostMap()
   :outertype: Switch

   :return: a read-only map of hosts and the list of packets to be sent to each one.

getPorts
^^^^^^^^

.. java:method::  int getPorts()
   :outertype: Switch

   Gets the number of ports the switch has.

getSwitchingDelay
^^^^^^^^^^^^^^^^^

.. java:method::  double getSwitchingDelay()
   :outertype: Switch

   :return: the latency time the switch spends to process a received packet. This time is considered constant no matter how many packets the switch have to process (in seconds).

getUplinkBandwidth
^^^^^^^^^^^^^^^^^^

.. java:method::  double getUplinkBandwidth()
   :outertype: Switch

   :return: Bandwitdh of uplink (in Megabits/s).

getUplinkSwitchPacketList
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<HostPacket> getUplinkSwitchPacketList(Switch uplinkSwitch)
   :outertype: Switch

   Gets the list of packets to be sent to an uplink switch.

   :param uplinkSwitch: the switch to get the list of packets to send
   :return: the list of packets to be sent to the given switch.

getUplinkSwitchPacketMap
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Map<Switch, List<HostPacket>> getUplinkSwitchPacketMap()
   :outertype: Switch

   :return: a read-only map of the uplink Switches and list of packets to be sent to each one.

getUplinkSwitches
^^^^^^^^^^^^^^^^^

.. java:method::  List<Switch> getUplinkSwitches()
   :outertype: Switch

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

setPorts
^^^^^^^^

.. java:method::  void setPorts(int ports)
   :outertype: Switch

setSwitchingDelay
^^^^^^^^^^^^^^^^^

.. java:method::  void setSwitchingDelay(double switchingDelay)
   :outertype: Switch

setUplinkBandwidth
^^^^^^^^^^^^^^^^^^

.. java:method::  void setUplinkBandwidth(double uplinkBandwidth)
   :outertype: Switch

