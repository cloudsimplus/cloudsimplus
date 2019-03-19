package org.cloudbus.cloudsim.network.switches;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.HostPacket;

import java.util.List;
import java.util.Map;

/**
 * Represents a Network Switch.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface Switch extends SimEntity {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Switch}
     * objects.
     */
    Switch NULL = new SwitchNull();

    /**
     * Gets the bandwidth this Switch has to communicate with Switches in the upper layer.
     * @return Bandwidth of uplink (in Megabits/s).
     * @see #getUplinkSwitches()
     */
    double getUplinkBandwidth();

    /**
     * Sets the bandwidth this Switch has to communicate with Switches in the upper layer.
     * @param uplinkBandwidth uplink bandwidth to set (in Megabits/s).
     * @see #getUplinkSwitches()
     */
    void setUplinkBandwidth(double uplinkBandwidth);

    /**
     * Gets the bandwidth this Switch has to communicate with Switches in the lower layer.
     * @return Bandwidth of downlink (in Megabits/s).
     * @see #getDownlinkSwitches()
     */
    double getDownlinkBandwidth();

    /**
     * Sets the bandwidth this Switch has to communicate with Switches in the lower layer.
     * @param downlinkBandwidth downlink bandwidth to set (in Megabits/s).
     * @see #getDownlinkSwitches()
     */
    void setDownlinkBandwidth(double downlinkBandwidth);

    /**
     * Gets the number of ports the switch has.
     * @return
     */
    int getPorts();

    /**
     * Sets the number of ports the switch has.
     * @param ports the number of ports to set
     */
    void setPorts(int ports);

    /**
     * Gets the latency time the switch spends to process a received packet. This time is
     * considered constant no matter how many packets the switch have to
     * process (in seconds).
     * @return the switching delay
     */
    double getSwitchingDelay();

    /**
     * Sets the latency time the switch spends to process a received packet. This time is
     * considered constant no matter how many packets the switch have to
     * process (in seconds).
     * @param switchingDelay the switching delay to set
     */
    void setSwitchingDelay(double switchingDelay);

    /**
     * Gets the list of Switches in the upper layer that this Switch is connected to.
     * @return
     */
    List<Switch> getUplinkSwitches();

    /**
     *
     * @return a <b>read-only</b> map of hosts and the list of packets
     * to be sent to each one.
     */
    Map<NetworkHost, List<HostPacket>> getPacketToHostMap();

    /**
     * Gets the list of Switches in the lower layer that this Switch is connected to.
     * @return
     */
    List<Switch> getDownlinkSwitches();

    /**
     * Considering a list of packets to be sent,
     * gets the amount of available downlink bandwidth for each packet,
     * assuming that such bandwidth is shared equally among
     * all packets, disregarding the packet size.
     *
     * @param packetList list of packets to be sent
     * @return the available downlink bandwidth for each packet in the list of packets to send (in Megabits/s)
     *         or the total downlink bandwidth capacity if the packet list has 0 or 1 element
     */
    double downlinkBandwidthByPacket(final List<HostPacket> packetList);

    /**
     * Considering a list of packets to be sent,
     * gets the amount of available uplink bandwidth for each packet,
     * assuming that such bandwidth is shared equally among
     * all packets, disregarding the packet size.
     *
     * @param packetList list of packets to be sent
     * @return the available uplink bandwidth for each packet in the list of packets to send (in Megabits/s)
     *         or the total uplink bandwidth capacity if the packet list has 0 or 1 element
     */
    double uplinkBandwidthByPacket(final List<HostPacket> packetList);

    /**
     * Gets the list of packets to be sent to a downlink switch.
     * @param downlinkSwitch the id of the switch to get the list of packets to send
     * @return the list of packets to be sent to the given switch.
     */
    List<HostPacket> getDownlinkSwitchPacketList(Switch downlinkSwitch);

    /**
     * Gets the list of packets to be sent to an uplink switch.
     * @param uplinkSwitch the switch to get the list of packets to send
     * @return the list of packets to be sent to the given switch.
     */
    List<HostPacket> getUplinkSwitchPacketList(Switch uplinkSwitch);

    /**
     * Gets the list of packets to be sent to a host.
     * @param host the host to get the list of packets to send
     * @return the list of packets to be sent to the given host.
     */
    List<HostPacket> getHostPacketList(NetworkHost host);

    /**
     *
     * @return a <b>read-only</b> map of the uplink Switches and list of packets
     * to be sent to each one.
     */
    Map<Switch, List<HostPacket>> getUplinkSwitchPacketMap();

    /**
     * Adds a packet that will be sent to a downlink {@link Switch}.
     * @param downlinkSwitch the target switch
     * @param packet the packet to be sent
     */
    void addPacketToSendToDownlinkSwitch(Switch downlinkSwitch, HostPacket packet);

    /**
     * Adds a packet that will be sent to a uplink {@link Switch}.
     * @param uplinkSwitch the target switch
     * @param packet the packet to be sent
     */
    void addPacketToSendToUplinkSwitch(Switch uplinkSwitch, HostPacket packet);

    /**
     * Adds a packet that will be sent to a {@link NetworkHost}.
     * @param host the target {@link NetworkHost}
     * @param packet the packet to be sent
     */
    void addPacketToSendToHost(NetworkHost host, HostPacket packet);

    /**
     * Gets the Datacenter where the switch is connected to.
     * @return
     */
    NetworkDatacenter getDatacenter();

    /**
     * Sets the Datacenter where the switch is connected to.
     * @param datacenter the Datacenter to set
     */
    void setDatacenter(NetworkDatacenter datacenter);

    /**
     * Gets the level (layer) of the Switch in the network topology,
     * depending if it is a root switch (layer 0), aggregate switch (layer 1)
     * or edge switch (layer 2)
     *
     * @return the switch network level
     */
    int getLevel();
}
