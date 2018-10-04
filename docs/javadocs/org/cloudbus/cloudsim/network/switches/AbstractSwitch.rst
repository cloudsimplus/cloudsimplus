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

AbstractSwitch
==============

.. java:package:: org.cloudbus.cloudsim.network.switches
   :noindex:

.. java:type:: public abstract class AbstractSwitch extends CloudSimEntity implements Switch

   An base class for implementing Network Switch.

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
AbstractSwitch
^^^^^^^^^^^^^^

.. java:constructor:: public AbstractSwitch(CloudSim simulation, NetworkDatacenter dc)
   :outertype: AbstractSwitch

Methods
-------
addPacketToBeSentToDownlinkSwitch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addPacketToBeSentToDownlinkSwitch(Switch downlinkSwitch, HostPacket packet)
   :outertype: AbstractSwitch

addPacketToBeSentToHost
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addPacketToBeSentToHost(NetworkHost host, HostPacket packet)
   :outertype: AbstractSwitch

addPacketToBeSentToUplinkSwitch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addPacketToBeSentToUplinkSwitch(Switch uplinkSwitch, HostPacket packet)
   :outertype: AbstractSwitch

connectHost
^^^^^^^^^^^

.. java:method:: @Override public void connectHost(NetworkHost host)
   :outertype: AbstractSwitch

disconnectHost
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean disconnectHost(NetworkHost host)
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

.. java:method:: @Override public List<HostPacket> getDownlinkSwitchPacketList(Switch downlinkSwitch)
   :outertype: AbstractSwitch

getDownlinkSwitches
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Switch> getDownlinkSwitches()
   :outertype: AbstractSwitch

getHostList
^^^^^^^^^^^

.. java:method:: @Override public List<NetworkHost> getHostList()
   :outertype: AbstractSwitch

getHostPacketList
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<HostPacket> getHostPacketList(NetworkHost host)
   :outertype: AbstractSwitch

getPacketList
^^^^^^^^^^^^^

.. java:method:: @Override public List<HostPacket> getPacketList()
   :outertype: AbstractSwitch

getPacketToHostMap
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<NetworkHost, List<HostPacket>> getPacketToHostMap()
   :outertype: AbstractSwitch

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

.. java:method:: @Override public List<HostPacket> getUplinkSwitchPacketList(Switch uplinkSwitch)
   :outertype: AbstractSwitch

getUplinkSwitchPacketMap
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Switch, List<HostPacket>> getUplinkSwitchPacketMap()
   :outertype: AbstractSwitch

getUplinkSwitches
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Switch> getUplinkSwitches()
   :outertype: AbstractSwitch

getVmEdgeSwitch
^^^^^^^^^^^^^^^

.. java:method:: protected EdgeSwitch getVmEdgeSwitch(Vm vm)
   :outertype: AbstractSwitch

   Gets the \ :java:ref:`EdgeSwitch`\  that the Host where the VM is placed is connected to.

   :param vm: the VM to get the Edge Switch
   :return: the connected Edge Switch

getVmHost
^^^^^^^^^

.. java:method:: protected NetworkHost getVmHost(Vm vm)
   :outertype: AbstractSwitch

   Gets the Host where a VM is placed.

   :param vm: the VM to get its Host
   :return: the Host where the VM is placed

networkDelayForPacketTransmission
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double networkDelayForPacketTransmission(HostPacket netPkt, double bwCapacity, List<HostPacket> netPktList)
   :outertype: AbstractSwitch

   Computes the network delay to send a packet through the network.

   :param netPkt: the packet to be sent
   :param bwCapacity: the total bandwidth capacity (in Megabits/s)
   :param netPktList: the list of packets waiting to be sent
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

