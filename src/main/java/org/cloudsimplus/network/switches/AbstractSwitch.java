/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.network.switches;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.core.CloudSimEntity;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.events.PredicateType;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.network.HostPacket;
import org.cloudsimplus.util.BytesConversion;
import org.cloudsimplus.util.MathUtil;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * An abstract class for implementing Network {@link Switch}es.
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 */
@Accessors
public abstract non-sealed class AbstractSwitch extends CloudSimEntity implements Switch {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSwitch.class.getSimpleName());

    @Getter @Setter @NonNull
    private NetworkDatacenter datacenter;

    @Getter
    private double uplinkBandwidth;

    @Getter
    private double downlinkBandwidth;

    @Getter
    private int ports;

    @Getter
    private double switchingDelay;

    @Getter
    private final List<Switch> uplinkSwitches;

    @Getter
    private final List<Switch> downlinkSwitches;

    /**
     * Map of packets sent to Datacenter on the uplink, where each key is a Switch
     * and the corresponding value is the list of packets to be sent to that Switch.
     */
    private final Map<Switch, List<HostPacket>> uplinkSwitchPacketMap;

    /**
     * Map of packets sent to Datacenter on the downlink, where each key is a
     * switch and the corresponding value is the list of packets to be sent to that switch.
     */
    private final Map<Switch, List<HostPacket>> downlinkSwitchPacketMap;

    /**
     * Map of packets sent to hosts connected in the switch, where each key is a
     * host and the corresponding value is the list of packets to be sent to that host.
     */
    private final Map<NetworkHost, List<HostPacket>> packetToHostMap;

    public AbstractSwitch(final CloudSimPlus simulation, final NetworkDatacenter dc) {
        super(simulation);
        this.packetToHostMap = new HashMap<>();
        this.uplinkSwitchPacketMap = new HashMap<>();
        this.downlinkSwitchPacketMap = new HashMap<>();
        this.downlinkSwitches = new ArrayList<>();
        this.uplinkSwitches = new ArrayList<>();
        this.datacenter = Objects.requireNonNull(dc);
    }

    @Override
    protected void startInternal() {
        LOGGER.info("{} is starting...", this);
        schedule(CloudSimTag.DC_LIST_REQUEST);
    }

    @Override
    public void processEvent(final SimEvent evt) {
        switch (evt.getTag()) {
            case CloudSimTag.NETWORK_EVENT_UP -> processPacketUp(evt);
            case CloudSimTag.NETWORK_EVENT_DOWN -> processPacketDown(evt);
            case CloudSimTag.NETWORK_EVENT_SEND -> processPacketForward();
            case CloudSimTag.NETWORK_EVENT_HOST -> processHostPacket(evt);
            default -> LOGGER.trace("{}: {}: Unknown event {} received.", getSimulation().clockStr(), this, evt.getTag());
        }
    }

    /**
     * Process a packet sent to a host.
     * @param evt the packet sent
     */
    protected void processHostPacket(final SimEvent evt) {
        if(evt.getData() instanceof HostPacket pkt) {
            final NetworkHost host = pkt.getDestination();
            host.addReceivedNetworkPacket(pkt);
        }
        else throw new IllegalStateException("NETWORK_EVENT_HOST SimEvent data must be a HostPacket");
    }

    /**
     * Sends a packet from uplink to Datacenter connected through a downlink port.
     * @param evt event/packet to process
     */
    protected void processPacketDown(final SimEvent evt) {
        // A packet coming from an up-level router has to be sent downward.
        getSimulation().cancelAll(this, new PredicateType(CloudSimTag.NETWORK_EVENT_SEND));
        schedule(this, getSwitchingDelay(), CloudSimTag.NETWORK_EVENT_SEND);
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
     * Sends a packet from a down switch or host to the Datacenter connected through an uplink port.
     * @param evt Event/packet to process
     */
    protected void processPacketUp(final SimEvent evt) {
        // A packet coming from a down-level router has to be sent up.
        getSimulation().cancelAll(this, new PredicateType(CloudSimTag.NETWORK_EVENT_SEND));
        schedule(this, switchingDelay, CloudSimTag.NETWORK_EVENT_SEND);
    }

    /**
     * Sends a packet to hosts connected to the Switch.
     */
    private void processPacketForward() {
        forwardPacketsToDownlinkSwitches();
        forwardPacketsToUplinkSwitches();
        forwardPacketsToHosts();
    }

    /**
     * Gets the packets to be sent to each downlink Switch and forwards them.
     *
     * @see #downlinkSwitchPacketMap
     */
    private void forwardPacketsToDownlinkSwitches() {
        for (final var targetSwitch: downlinkSwitchPacketMap.keySet()) {
            final var hostPktList = getDownlinkSwitchPacketList(targetSwitch);
            final double bw = this.downlinkBandwidth;
            forwardPacketsToSwitch(targetSwitch, hostPktList, bw, CloudSimTag.NETWORK_EVENT_DOWN);
        }
    }

    /**
     * Gets the packets to be sent to each uplink Switch and forwards them.
     *
     * @see #uplinkSwitchPacketMap
     */
    private void forwardPacketsToUplinkSwitches() {
        for (final var targetSwitch : uplinkSwitchPacketMap.keySet()) {
            final var hostPktList = getUplinkSwitchPacketList(targetSwitch);
            final double bw = uplinkBandwidth;
            forwardPacketsToSwitch(targetSwitch, hostPktList, bw, CloudSimTag.NETWORK_EVENT_UP);
        }
    }

    private void forwardPacketsToSwitch(
        final Switch destinationSwitch, final List<HostPacket> packetList,
        final double bandwidth, final int tag)
    {
        for (final HostPacket pkt : packetList) {
            final double delay = packetTransferDelay(pkt, bandwidth, packetList.size());
            send(destinationSwitch, delay, tag, pkt);
        }

        packetList.clear();
    }

    /**
     * Gets the packets to be sent to each Host and forwards them.
     *
     * @see #packetToHostMap
     */
    private void forwardPacketsToHosts() {
        for (final NetworkHost host : packetToHostMap.keySet()) {
            final var hostPktList = getHostPacketList(host);
            forwardPacketsToSwitch(this, hostPktList, downlinkBandwidth, CloudSimTag.NETWORK_EVENT_HOST);
        }
    }

    @Override
    public double downlinkTransferDelay(final HostPacket packet, final int simultaneousPackets) {
        return packetTransferDelay(packet, downlinkBandwidth, simultaneousPackets);
    }

    @Override
    public double uplinkTransferDelay(final HostPacket packet, final int simultaneousPackets) {
        return packetTransferDelay(packet, uplinkBandwidth, simultaneousPackets);
    }

    /**
     * Computes the network delay for sending a packet through the network,
     * considering that a list of packets will be sent simultaneously.
     *
     * @param netPkt     the packet to be sent
     * @param bwCapacity the total bandwidth capacity (in Megabits/s)
     * @param simultaneousPackets number of packets to be simultaneously sent
     * @return the expected time to transfer the packet through the network (in seconds)
     */
    protected double packetTransferDelay(
        final HostPacket netPkt, final double bwCapacity, final int simultaneousPackets)
    {
        return BytesConversion.bytesToMegaBits(netPkt.getSize()) / bandwidthByPacket(bwCapacity, simultaneousPackets);
    }

    /**
     * Considering a list of packets to be sent,
     * gets the amount of available bandwidth for each packet,
     * assuming that the bandwidth is shared equally among all packets.
     *
     * @param bwCapacity the total bandwidth capacity to share among
     *                   the packets to be sent (in Megabits/s)
     * @param simultaneousPackets number of packets to be simultaneously sent
     * @return the available bandwidth for each packet in the list of packets to send (in Megabits/s)
     *         or the total bandwidth capacity if the packet list has 0 or 1 element
     */
    protected double bandwidthByPacket(final double bwCapacity, final int simultaneousPackets) {
        return simultaneousPackets == 0 ? bwCapacity : bwCapacity / (double)simultaneousPackets;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        LOGGER.info("{} is shutting down...", getName());
    }

    @Override
    public final void setUplinkBandwidth(final double uplinkBandwidth) {
        this.uplinkBandwidth = MathUtil.nonNegative(uplinkBandwidth, "uplinkBandwidth");
    }

    @Override
    public final void setDownlinkBandwidth(final double downlinkBandwidth) {
        this.downlinkBandwidth = MathUtil.nonNegative(downlinkBandwidth, "downlinkBandwidth");
    }

    @Override
    public final void setPorts(final int ports) {
        this.ports = MathUtil.nonNegative(ports, "ports");
    }

    @Override
    public final void setSwitchingDelay(final double switchingDelay) {
        this.switchingDelay = MathUtil.nonNegative(switchingDelay, "switchingDelay");
    }

    /**
     * Gets the list of packets to be sent to a downlink Switch.
     * @param downlinkSwitch the Switch to get the list of packets to send
     * @return the list of packets to be sent to the given Switch.
     */
    protected List<HostPacket> getDownlinkSwitchPacketList(final Switch downlinkSwitch) {
        return downlinkSwitchPacketMap.getOrDefault(downlinkSwitch, new ArrayList<>());
    }

    /**
     * Gets the list of packets to be sent to an uplink Switch.
     * @param uplinkSwitch the Switch to get the list of packets to send
     * @return the list of packets to be sent to the given Switch.
     */
    protected List<HostPacket> getUplinkSwitchPacketList(final Switch uplinkSwitch) {
        return uplinkSwitchPacketMap.getOrDefault(uplinkSwitch, new ArrayList<>());
    }

    /**
     * Gets the list of packets to be sent to a Host.
     * @param host the host to get the list of packets to send
     * @return the list of packets to be sent to the given Host.
     */
    protected List<HostPacket> getHostPacketList(final NetworkHost host) {
        return packetToHostMap.getOrDefault(host, new ArrayList<>());
    }

    /**
     * Adds a packet that will be sent to a downlink {@link Switch}.
     * @param downlinkSwitch the target Switch
     * @param packet the packet to be sent
     */
    protected void addPacketToSendToDownlinkSwitch(final Switch downlinkSwitch, final HostPacket packet) {
        computeMapValue(downlinkSwitchPacketMap, downlinkSwitch, packet);
    }

    protected void addPacketToBeSentToFirstUplinkSwitch(final HostPacket netPkt) {
        final Switch uplinkSw = getUplinkSwitches().get(0);
        addPacketToSendToUplinkSwitch(uplinkSw, netPkt);
    }

    /**
     * Adds a packet that will be sent to an uplink {@link Switch}.
     * @param uplinkSwitch the target Switch
     * @param packet the packet to be sent
     */
    protected void addPacketToSendToUplinkSwitch(final Switch uplinkSwitch, final HostPacket packet) {
        computeMapValue(uplinkSwitchPacketMap, uplinkSwitch, packet);
    }

    /**
     * Adds a packet that will be sent to a {@link NetworkHost}.
     * @param host the target {@link NetworkHost}
     * @param packet the packet to be sent
     */
    protected void addPacketToSendToHost(final NetworkHost host, final HostPacket packet) {
        computeMapValue(packetToHostMap, host, packet);
    }

    /**
     * Computes a value for a multimap: a map where each value is a List.
     * @param map the map to compute a value (to add a value to a List mapped to a key)
     * @param key the key to access the mapped List
     * @param valueToAdd the value to add to the List
     * @param <K> type of the map key
     * @param <V> type of the map value
     */
    private <K, V> void computeMapValue(final Map<K, List<V>> map, final K key, final V valueToAdd) {
        map.compute(key, (k, list) -> list == null ? new ArrayList<>() : list).add(valueToAdd);
    }
}
