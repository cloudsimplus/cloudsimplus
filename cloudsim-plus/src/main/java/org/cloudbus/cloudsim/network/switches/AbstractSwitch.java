/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.switches;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimEntity;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.events.PredicateType;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.HostPacket;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * An base class for implementing Network Switch.
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 */
public abstract class AbstractSwitch extends CloudSimEntity implements Switch {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSwitch.class.getSimpleName());

    /**
     * Map of packets sent to Datacenter on the uplink, where each key is a switch
     * and the corresponding value is the list of packets to sent to that switch.
     */
    private final Map<Switch, List<HostPacket>> uplinkSwitchPacketMap;

    /**
     * Map of packets sent to Datacenter on the downlink, where each key is a
     * switch and the corresponding value is the list of packets to sent to that switch.
     */
    private final Map<Switch, List<HostPacket>> downlinkSwitchPacketMap;

    /**
     * List of hosts connected to the switch.
     */
    private final List<NetworkHost> hostList;

    /**
     * List of uplink Datacenter.
     */
    private final List<Switch> uplinkSwitches;

    /**
     * List of downlink Datacenter.
     */
    private final List<Switch> downlinkSwitches;

    /**
     * Map of packets sent to hosts connected in the switch, where each key is a
     * host and the corresponding value is the list of packets to sent to that host.
     */
    private final Map<NetworkHost, List<HostPacket>> packetToHostMap;

    /**
     * @see #getUplinkBandwidth()
     */
    private double uplinkBandwidth;

    /**
     * @see #getDownlinkBandwidth()
     */
    private double downlinkBandwidth;

    /**
     * @see #getPorts()
     */
    private int ports;

    /**
     * @see #getDatacenter()
     */
    private NetworkDatacenter datacenter;

    private final List<HostPacket> packetList;

    /**
     * @see #getSwitchingDelay()
     */
    private double switchingDelay;

    public AbstractSwitch(final CloudSim simulation, final NetworkDatacenter dc) {
        super(simulation);
        this.packetList = new ArrayList<>();
        this.hostList = new ArrayList<>();
        this.packetToHostMap = new HashMap<>();
        this.uplinkSwitchPacketMap = new HashMap<>();
        this.downlinkSwitchPacketMap = new HashMap<>();
        this.downlinkSwitches = new ArrayList<>();
        this.uplinkSwitches = new ArrayList<>();
        this.datacenter = dc;
    }

    @Override
    protected void startEntity() {
        LOGGER.info("{} is starting...", getName());
        schedule(this, 0, CloudSimTags.DATACENTER_LIST_REQUEST);
    }

    @Override
    public void processEvent(final SimEvent evt) {
        switch (evt.getTag()) {
            case CloudSimTags.NETWORK_EVENT_UP:
                // process the packet from down switch or host
                processPacketUp(evt);
            break;
            case CloudSimTags.NETWORK_EVENT_DOWN:
                // process the packet from uplink
                processPacketDown(evt);
            break;
            case CloudSimTags.NETWORK_EVENT_SEND:
                processPacketForward();
            break;
            case CloudSimTags.NETWORK_EVENT_HOST:
                processHostPacket(evt);
            break;
            case CloudSimTags.NETWORK_HOST_REGISTER:
                registerHost(evt);
            break;
        }
    }

    /**
     * Process a packet sent to a host.
     *
     * @param evt The packet sent.
     */
    protected void processHostPacket(final SimEvent evt) {
        final HostPacket pkt = (HostPacket) evt.getData();
        final NetworkHost host = pkt.getDestination();
        host.addReceivedNetworkPacket(pkt);
    }

    /**
     * Sends a packet to Datacenter connected through a downlink port.
     *
     * @param evt Event/packet to process
     */
    protected void processPacketDown(final SimEvent evt) {
        // packet coming from up level router
        // has to send downward.
        // check which switch to forward to
        // add packet in the switch list
        // add packet in the host list
        // int src=ev.getSource();
        getSimulation().cancelAll(this, new PredicateType(CloudSimTags.NETWORK_EVENT_SEND));
        schedule(this, getSwitchingDelay(), CloudSimTags.NETWORK_EVENT_SEND);
    }

    /**
     * Gets the Host where a VM is placed.
     * @param vm the VM to get its Host
     * @return the Host where the VM is placed
     */
    protected NetworkHost getVmHost(final Vm vm) {
        return (NetworkHost)vm.getHost();
    }

    /**
     * Sends a packet to Datacenter connected through a uplink port.
     *
     * @param evt Event/packet to process
     */
    protected void processPacketUp(final SimEvent evt) {
        // packet coming from down level router has to be sent up.
        // check which switch to forward to and add packet in the switch list
        getSimulation().cancelAll(this, new PredicateType(CloudSimTags.NETWORK_EVENT_SEND));
        schedule(this, switchingDelay, CloudSimTags.NETWORK_EVENT_SEND);
    }

    /**
     * Register a host that is connected to the switch.
     *
     * @param evt the event containing the host to be registered
     */
    private void registerHost(final SimEvent evt) {
        final NetworkHost host = (NetworkHost) evt.getData();
        hostList.add(host);
    }

    /**
     * Sends a packet to hosts connected to the switch
     *
     */
    private void processPacketForward() {
        forwardPacketsToDownlinkSwitches();
        forwardPacketsToUplinkSwitches();
        forwardPacketsToHosts();
    }

    /**
     * Gets the list of packets to be sent to each Downlink AbstractSwitch
     * and forward them.
     *
     * @see #downlinkSwitchPacketMap
     */
    private void forwardPacketsToDownlinkSwitches() {
        for (final Switch destinationSwitch: downlinkSwitchPacketMap.keySet()) {
            final List<HostPacket> packetList = getDownlinkSwitchPacketList(destinationSwitch);
            final double bandwidth = this.downlinkBandwidth;
            forwardPacketsToSwitch(destinationSwitch, packetList, bandwidth, CloudSimTags.NETWORK_EVENT_DOWN);
        }
    }

    private void forwardPacketsToSwitch(
        final Switch destinationSwitch, final List<HostPacket> packetList,
        final double bandwidth, final int cloudSimTag)
    {
        for (final HostPacket pkt : packetList) {
            final double delay = networkDelayForPacketTransmission(pkt, bandwidth, packetList);
            this.send(destinationSwitch, delay, cloudSimTag, pkt);
        }

        packetList.clear();
    }

    /**
     * Gets the list of packets to be sent to each Uplink Switch
     * and forward them.
     *
     * @see #uplinkSwitchPacketMap
     */
    private void forwardPacketsToUplinkSwitches() {
        for (final Switch destinationSwitch : uplinkSwitchPacketMap.keySet()) {
            final List<HostPacket> packetList = getUplinkSwitchPacketList(destinationSwitch);
            final double bandwidth = uplinkBandwidth;
            forwardPacketsToSwitch(destinationSwitch, packetList, bandwidth, CloudSimTags.NETWORK_EVENT_UP);
        }
    }

    /**
     * Gets the list of packets to be sent to each Host
     * and forward them.
     *
     * @see #packetToHostMap
     */
    private void forwardPacketsToHosts() {
        for (final NetworkHost host : packetToHostMap.keySet()) {
            final List<HostPacket> packetList = getHostPacketList(host);
            forwardPacketsToSwitch(this, packetList, downlinkBandwidth, CloudSimTags.NETWORK_EVENT_HOST);
        }
    }

    /**
     * Computes the network delay to send a packet through the network.
     *
     * @param netPkt     the packet to be sent
     * @param bwCapacity the total bandwidth capacity (in Megabits/s)
     * @param netPktList the list of packets waiting to be sent
     * @return the expected time to transfer the packet through the network (in seconds)
     */
    protected double networkDelayForPacketTransmission(final HostPacket netPkt, final double bwCapacity, final List<HostPacket> netPktList) {
        return Conversion.bytesToMegaBits(netPkt.getVmPacket().getSize()) / getAvailableBwForEachPacket(bwCapacity, netPktList);
    }

    /**
     * Considering a list of packets to be sent,
     * gets the amount of available bandwidth for each packet,
     * assuming that the bandwidth is shared equally among
     * all packets, disregarding the packet size.
     *
     * @param bwCapacity the total bandwidth capacity to be shared among
     *                   the packets to be sent (in Megabits/s)
     * @param netPktList list of packets to be sent
     * @return the available bandwidth for each packet in the list of packets to send (in Megabits/s)
     * or the total bandwidth capacity if the packet list has 0 or 1 element
     */
    private double getAvailableBwForEachPacket(final double bwCapacity, final List<HostPacket> netPktList) {
        return netPktList.isEmpty() ? bwCapacity : bwCapacity / netPktList.size();
    }

    @Override
    public void shutdownEntity() {
        super.shutdownEntity();
        LOGGER.info("{} is shutting down...", getName());
    }

    @Override
    public double getUplinkBandwidth() {
        return uplinkBandwidth;
    }

    @Override
    public final void setUplinkBandwidth(double uplinkBandwidth) {
        this.uplinkBandwidth = uplinkBandwidth;
    }

    @Override
    public double getDownlinkBandwidth() {
        return downlinkBandwidth;
    }

    @Override
    public final void setDownlinkBandwidth(double downlinkBandwidth) {
        this.downlinkBandwidth = downlinkBandwidth;
    }

    @Override
    public int getPorts() {
        return ports;
    }

    @Override
    public final void setPorts(final int ports) {
        this.ports = ports;
    }

    @Override
    public double getSwitchingDelay() {
        return switchingDelay;
    }

    @Override
    public final void setSwitchingDelay(final double switchingDelay) {
        this.switchingDelay = switchingDelay;
    }

    @Override
    public List<Switch> getUplinkSwitches() {
        return uplinkSwitches;
    }

    @Override
    public List<NetworkHost> getHostList() {
        return Collections.unmodifiableList(hostList);
    }

    @Override
    public void connectHost(final NetworkHost host) {
        hostList.add(host);
    }

    @Override
    public boolean disconnectHost(final NetworkHost host) {
        return hostList.remove(host);
    }

    @Override
    public Map<NetworkHost, List<HostPacket>> getPacketToHostMap() {
        return Collections.unmodifiableMap(packetToHostMap);
    }

    @Override
    public List<Switch> getDownlinkSwitches() {
        return downlinkSwitches;
    }

    @Override
    public List<HostPacket> getDownlinkSwitchPacketList(final Switch downlinkSwitch) {
        downlinkSwitchPacketMap.putIfAbsent(downlinkSwitch, new ArrayList<>());
        return downlinkSwitchPacketMap.get(downlinkSwitch);
    }

    @Override
    public List<HostPacket> getUplinkSwitchPacketList(final Switch uplinkSwitch) {
        uplinkSwitchPacketMap.putIfAbsent(uplinkSwitch, new ArrayList<>());
        return uplinkSwitchPacketMap.get(uplinkSwitch);
    }

    @Override
    public List<HostPacket> getHostPacketList(final NetworkHost host) {
        packetToHostMap.putIfAbsent(host, new ArrayList<>());
        return packetToHostMap.get(host);
    }

    @Override
    public Map<Switch, List<HostPacket>> getUplinkSwitchPacketMap() {
        return Collections.unmodifiableMap(uplinkSwitchPacketMap);
    }

    @Override
    public void addPacketToBeSentToDownlinkSwitch(final Switch downlinkSwitch, final HostPacket packet) {
        getDownlinkSwitchPacketList(downlinkSwitch).add(packet);
    }

    @Override
    public void addPacketToBeSentToUplinkSwitch(final Switch uplinkSwitch, final HostPacket packet) {
        getUplinkSwitchPacketList(uplinkSwitch).add(packet);
    }

    @Override
    public void addPacketToBeSentToHost(final NetworkHost host, final HostPacket packet) {
        getHostPacketList(host).add(packet);
    }

    @Override
    public NetworkDatacenter getDatacenter() {
        return datacenter;
    }

    @Override
    public void setDatacenter(final NetworkDatacenter datacenter) {
        this.datacenter = datacenter;
    }

    @Override
    public List<HostPacket> getPacketList() {
        return packetList;
    }

    /**
     * Gets the {@link EdgeSwitch} that the Host where the VM receiving a packet is connected to.
     * @param pkt the packet targeting some VM
     * @return the Edge Switch connected to the Host where the targeting VM is placed
     */
    protected EdgeSwitch getVmEdgeSwitch(final HostPacket pkt) {
        final Vm receiverVm = pkt.getVmPacket().getDestination();
        return ((NetworkHost)receiverVm.getHost()).getEdgeSwitch();
    }

    protected void addPacketToBeSentToFirstUplinkSwitch(HostPacket netPkt) {
        final Switch uplinkSw = getUplinkSwitches().get(0);
        addPacketToBeSentToUplinkSwitch(uplinkSw, netPkt);
    }
}
