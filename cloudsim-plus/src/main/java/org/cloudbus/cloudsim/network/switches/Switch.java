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
     *
     * @return Bandwitdh of uplink (in Megabits/s).
     */
    double getUplinkBandwidth();

    void setUplinkBandwidth(double uplinkBandwidth);

    /**
     *
     * @return Bandwitdh of downlink (in Megabits/s).
     */
    double getDownlinkBandwidth();

    void setDownlinkBandwidth(double downlinkBandwidth);

    /**
     * Gets the number of ports the switch has.
     * @return
     */
    int getPorts();

    void setPorts(int ports);

    /**
     *
     * @return the latency time the switch spends to process a received packet. This time is
     * considered constant no matter how many packets the switch have to
     * process (in seconds).
     */
    double getSwitchingDelay();

    void setSwitchingDelay(double switchingDelay);

    List<Switch> getUplinkSwitches();

    /**
     * Gets a <b>read-only</b> list of Hosts connected to the switch.
     * @return
     */
    List<NetworkHost> getHostList();

    /**
     * Connects a {@link NetworkHost} to the switch, by adding it to the
     * {@link #getHostList()}.
     * @param host the host to be connected to the switch
     */
    void connectHost(NetworkHost host);

    /**
     * Disconnects a {@link NetworkHost} from the switch, by removing it from the
     * {@link #getHostList()}.
     * @param host the host to be disconnected from the switch
     * @return true if the Host was connected to the switch, false otherwise
     */
    boolean disconnectHost(NetworkHost host);

    /**
     *
     * @return a read-only map of hosts and the list of packets
     * to be sent to each one.
     */
    Map<NetworkHost, List<HostPacket>> getPacketToHostMap();

    List<Switch> getDownlinkSwitches();

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
     * @return a read-only map of the uplink Switches and list of packets
     * to be sent to each one.
     */
    Map<Switch, List<HostPacket>> getUplinkSwitchPacketMap();

    void addPacketToBeSentToDownlinkSwitch(Switch downlinkSwitch, HostPacket packet);

    void addPacketToBeSentToUplinkSwitch(Switch uplinkSwitch, HostPacket packet);

    void addPacketToBeSentToHost(NetworkHost host, HostPacket packet);

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

    List<HostPacket> getPacketList();

    /**
     * Gets the level (layer) of the AbstractSwitch in the network topology,
     * depending if it is a root switch (layer 0), aggregate switch (layer 1)
     * or edge switch (layer 2)
     *
     * @return the switch network level
     */
    int getLevel();
}
