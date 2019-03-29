.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core SimEntityNullBase

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.network HostPacket

.. java:import:: java.util Collections

.. java:import:: java.util List

SwitchNull
==========

.. java:package:: org.cloudbus.cloudsim.network.switches
   :noindex:

.. java:type:: final class SwitchNull implements Switch, SimEntityNullBase

   A class that implements the Null Object Design Pattern for \ :java:ref:`Switch`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Switch.NULL`

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEntity entity)
   :outertype: SwitchNull

downlinkTransferDelay
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double downlinkTransferDelay(HostPacket packet, int simultaneousPackets)
   :outertype: SwitchNull

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public NetworkDatacenter getDatacenter()
   :outertype: SwitchNull

getDownlinkBandwidth
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getDownlinkBandwidth()
   :outertype: SwitchNull

getDownlinkSwitches
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Switch> getDownlinkSwitches()
   :outertype: SwitchNull

getLevel
^^^^^^^^

.. java:method:: @Override public int getLevel()
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

uplinkTransferDelay
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double uplinkTransferDelay(HostPacket packet, int simultaneousPackets)
   :outertype: SwitchNull

