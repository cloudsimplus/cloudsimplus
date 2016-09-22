/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.predicates.PredicateType;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * Represents a Network Switch.
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 * 
 */
public abstract class Switch extends SimEntity {
    /**
     * The value of 1 Kilobyte in bytes.
     */
    public static final int KILOBYTE = 1024;

    /**
     * The id of the datacenter where the switch is connected to.
     *
     * @todo It doesn't appear to be used
     */
    private int datacenterId;

    /**
     * Map of packets sent to switches on the uplink, where each key is a switch
     * id and the corresponding value is the list of packets to sent to that switch.
     */
    private final Map<Integer, List<NetworkPacket>> uplinkSwitchPacketMap;

    /**
     * Map of packets sent to switches on the downlink, where each key is a
     * switch id and the corresponding value is the list of packets to sent to that switch.
     */
    private final Map<Integer, List<NetworkPacket>> downlinkSwitchPacketMap;

    /**
     * Map of hosts connected to the switch, where each key is the host ID and
     * the corresponding value is the host itself.
     */
    private final Map<Integer, NetworkHost> hostList;

    /**
     * List of uplink switches.
     */
    private final List<Switch> uplinkSwitches;

    /**
     * List of downlink switches.
     */
    private final List<Switch> downlinkSwitches;

    /**
     * Map of packets sent to hosts connected in the switch, where each key is a
     * host id and the corresponding value is the list of packets to sent to that host.
     */
    private final Map<Integer, List<NetworkPacket>> packetToHostMap;

    /**
     * @see #getUplinkBandwidth() 
     */
    private double uplinkBandwidth;

    /**
     * @see #getDownlinkBandwidth() 
     */
    private double downlinkBandwidth;

    /** @see #getPorts() */
    private int ports;

    /**
     * The datacenter where the switch is connected to.
     *
     * @todo It doesn't appear to be used
     */
    private NetworkDatacenter datacenter;

    private final List<NetworkPacket> packetList;

    /**
     * @see #getSwitchingDelay() 
     */
    private double switchingDelay;

    public Switch(String name, NetworkDatacenter dc) {
        super(name);
        this.packetList = new ArrayList<>();
        this.hostList = new HashMap<>();
        this.packetToHostMap = new HashMap<>();
        this.uplinkSwitchPacketMap = new HashMap<>();
        this.downlinkSwitchPacketMap = new HashMap<>();
        this.downlinkSwitches = new ArrayList<>();
        this.uplinkSwitches = new ArrayList<>();        
        this.datacenter = dc;
    }

    @Override
    public void startEntity() {
        Log.printConcatLine(getName(), " is starting...");
        schedule(getId(), 0, CloudSimTags.DATACENTER_CHARACTERISTICS_REQUEST);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            // Resource characteristics request
            case CloudSimTags.NETWORK_EVENT_UP:
                // process the packet from down switch or host
                processPacketUp(ev);
                break;
            case CloudSimTags.NETWORK_EVENT_DOWN:
                // process the packet from uplink
                processPacketDown(ev);
                break;
            case CloudSimTags.NETWORK_EVENT_SEND:
                processPacketForward(ev);
                break;

            case CloudSimTags.NETWORK_EVENT_HOST:
                processHostPacket(ev);
                break;
            // Resource characteristics answer
            case CloudSimTags.NETWORK_HOST_REGISTER:
                registerHost(ev);
                break;
            // other unknown tags are processed by this method
            default:
                processOtherEvent(ev);
                break;
        }
    }

    /**
     * Process a packet sent to a host.
     *
     * @param ev The packet sent.
     */
    protected void processHostPacket(SimEvent ev) {
        // Send packet to host
        NetworkPacket netPkt = (NetworkPacket) ev.getData();
        NetworkHost host = hostList.get(netPkt.getReceiverHostId());
        host.addReceivedNetworkPacket(netPkt);
    }

    /**
     * Sends a packet to switches connected through a downlink port.
     *
     * @param ev Event/packet to process
     */
    protected void processPacketDown(SimEvent ev) {
        // packet coming from up level router
        // has to send downward.
        // check which switch to forward to
        // add packet in the switch list
        // add packet in the host list
        // int src=ev.getSource();
        CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.NETWORK_EVENT_SEND));
        schedule(getId(), getSwitchingDelay(), CloudSimTags.NETWORK_EVENT_SEND);
    }

    /**
     * Sends a packet to switches connected through a uplink port.
     *
     * @param ev Event/packet to process
     */
    protected void processPacketUp(SimEvent ev) {
        // packet coming from down level router.
        // has to be sent up.
        // check which switch to forward to
        // add packet in the switch list
        //
        // int src=ev.getSource();
        NetworkPacket hspkt = (NetworkPacket) ev.getData();
        int recvVmId = hspkt.getHostPacket().getReceiverVmId();
        CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.NETWORK_EVENT_SEND));
        schedule(getId(), switchingDelay, CloudSimTags.NETWORK_EVENT_SEND);
    }

    /**
     * Register a host that is connected to the switch.
     *
     * @param ev
     */
    private void registerHost(SimEvent ev) {
        NetworkHost hs = (NetworkHost) ev.getData();
        hostList.put(hs.getId(), hs);
    }

    /**
     * Process a received packet.
     *
     * @param ev The packet received.
     */
    protected void processPacket(SimEvent ev) {
        // send packet to itself with switching delay (discarding other)
        CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.NETWORK_EVENT_UP));
        schedule(getId(), switchingDelay, CloudSimTags.NETWORK_EVENT_UP);
        packetList.add((NetworkPacket) ev.getData());

        // add the packet in the list
    }

    /**
     * Process non-default received events that aren't processed by the
     * {@link #processEvent(org.cloudbus.cloudsim.core.SimEvent)} method. This
     * method should be overridden by subclasses in other to process new defined
     * events.
     *
     * @param ev
     */
    protected void processOtherEvent(SimEvent ev) {}

    /**
     * Sends a packet to hosts connected to the switch
     *
     * @param ev Event/packet to process
     */
    protected void processPacketForward(SimEvent ev) {
        forwardPacketsToDownlinkSwitches();
        forwardPacketsToUplinkSwitches();
        forwardPacketsToHosts();
    }

    /**
     * Gets the list of packets to be sent to each Host
     * and forward them.
     * @see #packetToHostMap
     */
    private void forwardPacketsToHosts() {
        if (packetToHostMap != null) {
            for (Entry<Integer, List<NetworkPacket>> es : packetToHostMap.entrySet()) {
                List<NetworkPacket> netPktList = es.getValue();
                for(NetworkPacket netPkt: netPktList) {
                    double delay = networkDelayForPacketTransmission(netPkt, downlinkBandwidth, netPktList);
                    this.send(getId(), delay, CloudSimTags.NETWORK_EVENT_HOST, netPkt);
                }
                netPktList.clear();
            }
        }
    }

    /**
     * Gets the list of packets to be sent to each Uplink Switch
     * and forward them.
     * @see #uplinkSwitchPacketMap
     */
    private void forwardPacketsToUplinkSwitches() {
        if (uplinkSwitchPacketMap != null) {
            for (Entry<Integer, List<NetworkPacket>> es : uplinkSwitchPacketMap.entrySet()) {
                int destinationSwitchId = es.getKey();
                List<NetworkPacket> netPktList = es.getValue();
                for(NetworkPacket netPkt: netPktList) {
                    double delay = networkDelayForPacketTransmission(netPkt, uplinkBandwidth, netPktList);
                    this.send(destinationSwitchId, delay, CloudSimTags.NETWORK_EVENT_UP, netPkt);
                }
                netPktList.clear();
            }
        }
    }

    /**
     * Gets the list of packets to be sent to each Downlink Switch
     * and forward them.
     * @see #downlinkSwitchPacketMap
     */
    private void forwardPacketsToDownlinkSwitches() {
        if (downlinkSwitchPacketMap != null) {
            for (Entry<Integer, List<NetworkPacket>> es : downlinkSwitchPacketMap.entrySet()) {
                int destinationSwitchId = es.getKey();
                List<NetworkPacket> netPktList = es.getValue();
                for (NetworkPacket netPkt: netPktList) {
                    double delay = networkDelayForPacketTransmission(netPkt, downlinkBandwidth, netPktList);
                    this.send(destinationSwitchId, delay, CloudSimTags.NETWORK_EVENT_DOWN, netPkt);
                }
                netPktList.clear();
            }
        }
    }

    /**
     * Computes the network delay to send a packet through the network.
     * 
     * @param netPkt the packet to be sent
     * @param bwCapacity the total bandwidth capacity (in Megabits/s)
     * @param netPktList the list of packets waiting to be sent
     * @return the expected time to transfer the packet through the network (in seconds)
     */
    protected double networkDelayForPacketTransmission(NetworkPacket netPkt, double bwCapacity, List<NetworkPacket> netPktList) {
        return bytesToMegabits(netPkt.getHostPacket().getDataLength()) / 
                      getAvailableBwForEachPacket(bwCapacity, netPktList);
    }
    
    public static final double bytesToMegabits(double bytes){
        return bytesToBits(bytesToMegabytes(bytes));
    }

    public static final double bytesToMegabytes(double bytes){
        return bytes / KILOBYTE / KILOBYTE;
    }
    
    /**
     * Converts any value in bytes to bits,
     * doesn't matter if the unit is Kilobytes (KB), Megabytes (MB), Gigabytes (GB), etc.
     * 
     * @param bytes the value in byte (KB, MB, GB , etc)
     * @return the value in bits, following the same unit
     * of the input param. For instance, if it is given
     * a value in Megabytes it will be converted to Megabits,
     * if in Gigabytes it will be converted to Gigabits.
     */
    public static final double bytesToBits(double bytes){
        return bytes * 8;
    }
    
    /**
     * Considering a list of packets to be sent,
     * gets the amount of available bandwidth for each packet,
     * assuming that the bandwidth is shared equally among 
     * all packets, disregarding the packet size.
     * 
     * @param bwCapacity the total bandwidth capacity to be shared among 
     * the packets to be sent (in Megabits/s)
     * @param netPktList list of packets to be sent 
     * @return the available bandwidth for each packet in the list of packets to send (in Megabits/s)
     */
    private double getAvailableBwForEachPacket(double bwCapacity, List<NetworkPacket> netPktList) {
        return (netPktList.isEmpty() ? bwCapacity : bwCapacity / netPktList.size());
    }

    /**
     * Gets the host of a given VM.
     *
     * @param vmid The id of the VM
     * @return the host of the VM
     */
    protected NetworkHost getHostWithVm(int vmid) {
        for (Entry<Integer, NetworkHost> es : hostList.entrySet()) {
            Vm vm = VmList.getById(es.getValue().getVmList(), vmid);
            if (vm != Vm.NULL) {
                return es.getValue();
            }
        }
        return null;
    }

    @Override
    public void shutdownEntity() {
        Log.printConcatLine(getName(), " is shutting down...");
    }

    /**
     * 
     * @return Bandwitdh of uplink (in Megabits/s).
     */
    public double getUplinkBandwidth() {
        return uplinkBandwidth;
    }

    public final void setUplinkBandwidth(double uplinkBandwidth) {
        this.uplinkBandwidth = uplinkBandwidth;
    }

    /**
     * 
     * @return Bandwitdh of downlink (in Megabits/s).
     */
    public double getDownlinkBandwidth() {
        return downlinkBandwidth;
    }

    public final void setDownlinkBandwidth(double downlinkBandwidth) {
        this.downlinkBandwidth = downlinkBandwidth;
    }

    /**
     * Gets the number of ports the switch has.
     * @return 
     */
    public int getPorts() {
        return ports;
    }

    public final void setPorts(int ports) {
        this.ports = ports;
    }

    /**
     * 
     * @return the latency time the switch spends to process a received packet. This time is
     * considered constant no matter how many packets the switch have to
     * process (in seconds).
     */
    public double getSwitchingDelay() {
        return switchingDelay;
    }

    public final void setSwitchingDelay(double switchingDelay) {
        this.switchingDelay = switchingDelay;
    }

    public List<Switch> getUplinkSwitches() {
        return uplinkSwitches;
    }

    public int getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(int datacenterId) {
        this.datacenterId = datacenterId;
    }

    public Map<Integer, NetworkHost> getHostList() {
        return hostList;
    }

    /**
     * 
     * @return a read-only map of hosts and the list of packets
     * to be sent to each one.
     */
    public Map<Integer, List<NetworkPacket>> getPacketToHostMap() {
        return Collections.unmodifiableMap(packetToHostMap);
    }
    
    public List<Switch> getDownlinkSwitches() {
        return downlinkSwitches;
    }

    /**
     * Gets the list of packets to be sent to a downlink switch.
     * @param downlinkSwitchId the id of the switch to get the list of packets to send
     * @return the list of packets to be sent to the given switch.
     */
    public List<NetworkPacket> getDownlinkSwitchPacketList(int downlinkSwitchId) {
        return getListOfPackets(downlinkSwitchPacketMap, downlinkSwitchId);
    }
    
    /**
     * Gets the list of packets to be sent to an uplink switch.
     * @param uplinkSwitchId the id of the switch to get the list of packets to send
     * @return the list of packets to be sent to the given switch.
     */
    public List<NetworkPacket> getUplinkSwitchPacketList(int uplinkSwitchId) {
        return getListOfPackets(uplinkSwitchPacketMap, uplinkSwitchId);
    }
    
    /**
     * Gets the list of packets to be sent to a host.
     * @param hostId the id of the host to get the list of packets to send
     * @return the list of packets to be sent to the given host.
     */
    public List<NetworkPacket> getHostPacketList(int hostId) {
        return getListOfPackets(packetToHostMap, hostId);
    }

    /**
     * Gets a list of packets from a given map of packet lists.
     * @param map map where to get the list of packets
     * @param key the map entry key where the list has to be got
     * @return the list of packets from the map entry with the given key
     */
    private List<NetworkPacket> getListOfPackets(Map<Integer, List<NetworkPacket>> map, int key) {
        List<NetworkPacket> list = map.get(key);
        if(list == null){
            list = new ArrayList<>();
            map.put(key, list);
        }
        
        return list;
    }    
    
    /**
     * 
     * @return a read-only map of the uplink Switches and list of packets
     * to be sent to each one.
     */
    public Map<Integer, List<NetworkPacket>> getUplinkSwitchPacketMap(){
        return Collections.unmodifiableMap(uplinkSwitchPacketMap);
    }
    
    public void addPacketToBeSentToDownlinkSwitch(int downlinkSwitchId, NetworkPacket packet){
        getDownlinkSwitchPacketList(downlinkSwitchId).add(packet);
    }

    public void addPacketToBeSentToUplinkSwitch(int uplinkSwitchId, NetworkPacket packet){
        getUplinkSwitchPacketList(uplinkSwitchId).add(packet);
    }

    public void addPacketToBeSentToHost(int hostId, NetworkPacket packet){
        getHostPacketList(hostId).add(packet);
    }

    public NetworkDatacenter getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(NetworkDatacenter datacenter) {
        this.datacenter = datacenter;
    }

    public List<NetworkPacket> getPacketList() {
        return packetList;
    }

    /**
     * Gets the level (layer) of the Switch in the network topology,
     * depending if it is a root switch (layer 0), aggregate switch (layer 1)
     * or edge switch (layer 2)
     * 
     * @return the switch network level
     */
    public abstract int getLevel();
    
}
