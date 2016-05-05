/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
     * The id of the datacenter where the switch is connected to.
     *
     * @todo It doesn't appear to be used
     */
    private int datacenterId;

    /**
     * Map of packets sent to switches on the uplink, where each key is a switch
     * id and the corresponding value is the packets sent to that switch.
     */
    private final Map<Integer, List<NetworkPacket>> uplinkSwitchPacketList;

    /**
     * Map of packets sent to switches on the downlink, where each key is a
     * switch id and the corresponding value is the packets sent to that switch.
     */
    private final Map<Integer, List<NetworkPacket>> downlinkSwitchPacketList;

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
     * host id and the corresponding value is the packets sent to that host.
     */
    private final Map<Integer, List<NetworkPacket>> packetToHost;

    /**
     * Bandwitdh of uplink.
     */
    private double uplinkBandwidth;

    /**
     * Bandwitdh of downlink.
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
     * The latency time the switch spends to process a received packet. This time is
     * considered constant no matter how many packets the switch have to
     * process.
     */
    private double switchingDelay;

    /**
     * A map of VMs connected to this switch.
     *
     * @todo The list doesn't appear to be updated (VMs added to it) anywhere.
     */
    private final Map<Integer, NetworkVm> vmlist = new HashMap<>();

    public Switch(String name, int level, NetworkDatacenter dc) {
        super(name);
        this.packetList = new ArrayList<>();
        this.hostList = new HashMap<>();
        this.packetToHost = new HashMap<>();
        this.uplinkSwitchPacketList = new HashMap<>();
        this.downlinkSwitchPacketList = new HashMap<>();
        this.downlinkSwitches = new ArrayList<>();
        this.uplinkSwitches = new ArrayList<>();        
        this.datacenter = dc;
    }

    @Override
    public void startEntity() {
        Log.printConcatLine(getName(), " is starting...");
        schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
    }

    @Override
    public void processEvent(SimEvent ev) {
        // Log.printLine(CloudSim.clock()+"[Broker]: event received:"+ev.getTag());
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
        NetworkPacket hspkt = (NetworkPacket) ev.getData();
        NetworkHost hs = hostList.get(hspkt.receiverHostId);
        hs.getPacketsReceived().add(hspkt);
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
        int recvVmId = hspkt.pkt.receiverVmId;
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
        // search for the host and packets..send to them
        if (downlinkSwitchPacketList != null) {
            for (Entry<Integer, List<NetworkPacket>> es : downlinkSwitchPacketList.entrySet()) {
                int tosend = es.getKey();
                List<NetworkPacket> hspktlist = es.getValue();
                if (!hspktlist.isEmpty()) {
                    double avband = downlinkBandwidth / hspktlist.size();
                    Iterator<NetworkPacket> it = hspktlist.iterator();
                    while (it.hasNext()) {
                        NetworkPacket hspkt = it.next();
                        double delay = 1000 * hspkt.pkt.dataLength / avband;

                        this.send(tosend, delay, CloudSimTags.NETWORK_EVENT_DOWN, hspkt);
                    }
                    hspktlist.clear();
                }
            }
        }
        if (uplinkSwitchPacketList != null) {
            for (Entry<Integer, List<NetworkPacket>> es : uplinkSwitchPacketList.entrySet()) {
                int tosend = es.getKey();
                List<NetworkPacket> hspktlist = es.getValue();
                if (!hspktlist.isEmpty()) {
                    double avband = uplinkBandwidth / hspktlist.size();
                    Iterator<NetworkPacket> it = hspktlist.iterator();
                    while (it.hasNext()) {
                        NetworkPacket hspkt = it.next();
                        double delay = 1000 * hspkt.pkt.dataLength / avband;

                        this.send(tosend, delay, CloudSimTags.NETWORK_EVENT_UP, hspkt);
                    }
                    hspktlist.clear();
                }
            }
        }
        if (packetToHost != null) {
            for (Entry<Integer, List<NetworkPacket>> es : packetToHost.entrySet()) {
                List<NetworkPacket> hspktlist = es.getValue();
                if (!hspktlist.isEmpty()) {
                    double avband = downlinkBandwidth / hspktlist.size();
                    Iterator<NetworkPacket> it = hspktlist.iterator();
                    while (it.hasNext()) {
                        NetworkPacket hspkt = it.next();
                        // hspkt.recieverhostid=tosend;
                        // hs.packetsReceived.add(hspkt);
                        this.send(getId(), hspkt.pkt.dataLength / avband, CloudSimTags.NETWORK_EVENT_HOST, hspkt);
                    }
                    hspktlist.clear();
                }
            }
        }

        // or to switch at next level.
        // clear the list
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
            if (vm != null) {
                return es.getValue();
            }
        }
        return null;
    }

    @Override
    public void shutdownEntity() {
        Log.printConcatLine(getName(), " is shutting down...");
    }

    public double getUplinkBandwidth() {
        return uplinkBandwidth;
    }

    public final void setUplinkBandwidth(double uplinkBandwidth) {
        this.uplinkBandwidth = uplinkBandwidth;
    }

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

    public Map<Integer, List<NetworkPacket>> getPacketToHost() {
        return packetToHost;
    }

    public List<Switch> getDownlinkSwitches() {
        return downlinkSwitches;
    }

    public Map<Integer, List<NetworkPacket>> getUplinkSwitchPacketList() {
        return uplinkSwitchPacketList;
    }

    public Map<Integer, List<NetworkPacket>> getDownlinkSwitchPacketList() {
        return downlinkSwitchPacketList;
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
