.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core CloudSimEntity

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core.events PredicateType

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.hosts.network NetworkHost

.. java:import:: org.cloudbus.cloudsim.network HostPacket

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

AbstractSwitch
==============

.. java:package:: org.cloudbus.cloudsim.network.switches
   :noindex:

.. java:type:: public abstract class AbstractSwitch extends CloudSimEntity implements Switch

   A base class for implementing Network Switch.

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
AbstractSwitch
^^^^^^^^^^^^^^

.. java:constructor:: public AbstractSwitch(CloudSim simulation, NetworkDatacenter dc)
   :outertype: AbstractSwitch

Methods
-------
addPacketToBeSentToFirstUplinkSwitch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addPacketToBeSentToFirstUplinkSwitch(HostPacket netPkt)
   :outertype: AbstractSwitch

addPacketToSendToDownlinkSwitch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addPacketToSendToDownlinkSwitch(Switch downlinkSwitch, HostPacket packet)
   :outertype: AbstractSwitch

   Adds a packet that will be sent to a downlink \ :java:ref:`Switch`\ .

   :param downlinkSwitch: the target switch
   :param packet: the packet to be sent

addPacketToSendToHost
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addPacketToSendToHost(NetworkHost host, HostPacket packet)
   :outertype: AbstractSwitch

   Adds a packet that will be sent to a \ :java:ref:`NetworkHost`\ .

   :param host: the target \ :java:ref:`NetworkHost`\
   :param packet: the packet to be sent

addPacketToSendToUplinkSwitch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addPacketToSendToUplinkSwitch(Switch uplinkSwitch, HostPacket packet)
   :outertype: AbstractSwitch

   Adds a packet that will be sent to a uplink \ :java:ref:`Switch`\ .

   :param uplinkSwitch: the target switch
   :param packet: the packet to be sent

bandwidthByPacket
^^^^^^^^^^^^^^^^^

.. java:method:: protected double bandwidthByPacket(double bwCapacity, int simultaneousPackets)
   :outertype: AbstractSwitch

   Considering a list of packets to be sent, gets the amount of available bandwidth for each packet, assuming that the bandwidth is shared equally among all packets.

   :param bwCapacity: the total bandwidth capacity to share among the packets to be sent (in Megabits/s)
   :param simultaneousPackets: number of packets to be simultaneously sent
   :return: the available bandwidth for each packet in the list of packets to send (in Megabits/s) or the total bandwidth capacity if the packet list has 0 or 1 element

downlinkTransferDelay
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double downlinkTransferDelay(HostPacket packet, int simultaneousPackets)
   :outertype: AbstractSwitch

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public NetworkDatacenter getDatacenter()
   :outertype: AbstractSwitch

getDownlinkBandwidth
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getDownlinkBandwidth()
   :outertype: AbstractSwitch

getDownlinkSwitchPacketList
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<HostPacket> getDownlinkSwitchPacketList(Switch downlinkSwitch)
   :outertype: AbstractSwitch

   Gets the list of packets to be sent to a downlink switch.

   :param downlinkSwitch: the id of the switch to get the list of packets to send
   :return: the list of packets to be sent to the given switch.

getDownlinkSwitches
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Switch> getDownlinkSwitches()
   :outertype: AbstractSwitch

getHostPacketList
^^^^^^^^^^^^^^^^^

.. java:method:: protected List<HostPacket> getHostPacketList(NetworkHost host)
   :outertype: AbstractSwitch

   Gets the list of packets to be sent to a host.

   :param host: the host to get the list of packets to send
   :return: the list of packets to be sent to the given host.

getPorts
^^^^^^^^

.. java:method:: @Override public int getPorts()
   :outertype: AbstractSwitch

getSwitchingDelay
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSwitchingDelay()
   :outertype: AbstractSwitch

getUplinkBandwidth
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUplinkBandwidth()
   :outertype: AbstractSwitch

getUplinkSwitchPacketList
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<HostPacket> getUplinkSwitchPacketList(Switch uplinkSwitch)
   :outertype: AbstractSwitch

   Gets the list of packets to be sent to an uplink switch.

   :param uplinkSwitch: the switch to get the list of packets to send
   :return: the list of packets to be sent to the given switch.

getUplinkSwitches
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Switch> getUplinkSwitches()
   :outertype: AbstractSwitch

getVmEdgeSwitch
^^^^^^^^^^^^^^^

.. java:method:: protected EdgeSwitch getVmEdgeSwitch(HostPacket pkt)
   :outertype: AbstractSwitch

   Gets the \ :java:ref:`EdgeSwitch`\  that the Host where the VM receiving a packet is connected to.

   :param pkt: the packet targeting some VM
   :return: the Edge Switch connected to the Host where the targeting VM is placed

getVmHost
^^^^^^^^^

.. java:method:: protected NetworkHost getVmHost(Vm vm)
   :outertype: AbstractSwitch

   Gets the Host where a VM is placed.

   :param vm: the VM to get its Host
   :return: the Host where the VM is placed

packetTransferDelay
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double packetTransferDelay(HostPacket netPkt, double bwCapacity, int simultaneousPackets)
   :outertype: AbstractSwitch

   Computes the network delay to send a packet through the network, considering that a list of packets will be sent simultaneously.

   :param netPkt: the packet to be sent
   :param bwCapacity: the total bandwidth capacity (in Megabits/s)
   :param simultaneousPackets: number of packets to be simultaneously sent
   :return: the expected time to transfer the packet through the network (in seconds)

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent evt)
   :outertype: AbstractSwitch

processHostPacket
^^^^^^^^^^^^^^^^^

.. java:method:: protected void processHostPacket(SimEvent evt)
   :outertype: AbstractSwitch

   Process a packet sent to a host.

   :param evt: The packet sent.

processPacketDown
^^^^^^^^^^^^^^^^^

.. java:method:: protected void processPacketDown(SimEvent evt)
   :outertype: AbstractSwitch

   Sends a packet to Datacenter connected through a downlink port.

   :param evt: Event/packet to process

processPacketUp
^^^^^^^^^^^^^^^

.. java:method:: protected void processPacketUp(SimEvent evt)
   :outertype: AbstractSwitch

   Sends a packet to Datacenter connected through a uplink port.

   :param evt: Event/packet to process

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenter(NetworkDatacenter datacenter)
   :outertype: AbstractSwitch

setDownlinkBandwidth
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setDownlinkBandwidth(double downlinkBandwidth)
   :outertype: AbstractSwitch

setPorts
^^^^^^^^

.. java:method:: @Override public final void setPorts(int ports)
   :outertype: AbstractSwitch

setSwitchingDelay
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setSwitchingDelay(double switchingDelay)
   :outertype: AbstractSwitch

setUplinkBandwidth
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setUplinkBandwidth(double uplinkBandwidth)
   :outertype: AbstractSwitch

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override public void shutdownEntity()
   :outertype: AbstractSwitch

startEntity
^^^^^^^^^^^

.. java:method:: @Override protected void startEntity()
   :outertype: AbstractSwitch

uplinkTransferDelay
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double uplinkTransferDelay(HostPacket packet, int simultaneousPackets)
   :outertype: AbstractSwitch

