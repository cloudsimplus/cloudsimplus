.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.datacenters DatacenterCharacteristics

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.hosts.network NetworkHost

.. java:import:: org.cloudbus.cloudsim.network HostPacket

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Map

SwitchNull
==========

.. java:package:: org.cloudbus.cloudsim.network.switches
   :noindex:

.. java:type:: final class SwitchNull implements Switch

   A class that implements the Null Object Design Pattern for \ :java:ref:`Switch`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Switch.NULL`

Methods
-------
addPacketToBeSentToDownlinkSwitch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addPacketToBeSentToDownlinkSwitch(Switch downlinkSwitch, HostPacket packet)
   :outertype: SwitchNull

addPacketToBeSentToHost
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addPacketToBeSentToHost(NetworkHost host, HostPacket packet)
   :outertype: SwitchNull

addPacketToBeSentToUplinkSwitch
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addPacketToBeSentToUplinkSwitch(Switch uplinkSwitch, HostPacket packet)
   :outertype: SwitchNull

connectHost
^^^^^^^^^^^

.. java:method:: @Override public void connectHost(NetworkHost host)
   :outertype: SwitchNull

disconnectHost
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean disconnectHost(NetworkHost host)
   :outertype: SwitchNull

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public NetworkDatacenter getDatacenter()
   :outertype: SwitchNull

getDownlinkBandwidth
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getDownlinkBandwidth()
   :outertype: SwitchNull

getDownlinkSwitchPacketList
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<HostPacket> getDownlinkSwitchPacketList(Switch s)
   :outertype: SwitchNull

getDownlinkSwitches
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Switch> getDownlinkSwitches()
   :outertype: SwitchNull

getHostList
^^^^^^^^^^^

.. java:method:: @Override public List<NetworkHost> getHostList()
   :outertype: SwitchNull

getHostPacketList
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<HostPacket> getHostPacketList(NetworkHost host)
   :outertype: SwitchNull

getId
^^^^^

.. java:method:: @Override public int getId()
   :outertype: SwitchNull

getLevel
^^^^^^^^

.. java:method:: @Override public int getLevel()
   :outertype: SwitchNull

getPacketList
^^^^^^^^^^^^^

.. java:method:: @Override public List<HostPacket> getPacketList()
   :outertype: SwitchNull

getPacketToHostMap
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<NetworkHost, List<HostPacket>> getPacketToHostMap()
   :outertype: SwitchNull

getPorts
^^^^^^^^

.. java:method:: @Override public int getPorts()
   :outertype: SwitchNull

getSwitchingDelay
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSwitchingDelay()
   :outertype: SwitchNull

getUplinkBandwidth
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUplinkBandwidth()
   :outertype: SwitchNull

getUplinkSwitchPacketList
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<HostPacket> getUplinkSwitchPacketList(Switch s)
   :outertype: SwitchNull

getUplinkSwitchPacketMap
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<Switch, List<HostPacket>> getUplinkSwitchPacketMap()
   :outertype: SwitchNull

getUplinkSwitches
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Switch> getUplinkSwitches()
   :outertype: SwitchNull

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenter(NetworkDatacenter datacenter)
   :outertype: SwitchNull

setDownlinkBandwidth
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setDownlinkBandwidth(double downlinkBandwidth)
   :outertype: SwitchNull

setPorts
^^^^^^^^

.. java:method:: @Override public void setPorts(int ports)
   :outertype: SwitchNull

setSwitchingDelay
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setSwitchingDelay(double switchingDelay)
   :outertype: SwitchNull

setUplinkBandwidth
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setUplinkBandwidth(double uplinkBandwidth)
   :outertype: SwitchNull

