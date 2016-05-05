/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.core.SimEvent;

/**
 * This class represents an Aggregate Switch in a Datacenter network. It
 * interacts with other switches in order to exchange packets.
 *
 * <p>Please refer to following publication for more details:
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 * 
 * @since CloudSim Toolkit 1.0
 */
public class AggregateSwitch extends Switch {
    /**
     * The level (layer) of the switch in the network topology.
     */
    public static final int LEVEL = 1;

    /**
     * Default delay of {@link AggregateSwitch} in milliseconds.
     */
    public static final double SWITCHING_DELAY = 0.00245;
    
    /**
     * Default downlink bandwidth of {@link AggregateSwitch} in Megabits/s.
     * It also represents the uplink bandwidth of connected edge switches.
     */
    public static final long DOWNLINK_BW = 100 * 1024 * 1024; // 100 Megabits
    /**
     * Default number of aggregation switch ports that defines the number of
     * {@link EdgeSwitch} that can be connected to it.
     */
    public static final int PORTS = 1;

    /**
     * Instantiates a Aggregate Switch specifying the switches that are
     * connected to its downlink and uplink ports and corresponding bandwidths.
     *
     * @param name Name of the switch
     * @param dc The Datacenter where the switch is connected to
     */
    public AggregateSwitch(String name, NetworkDatacenter dc) {
        super(name, LEVEL, dc);
        setUplinkBandwidth(RootSwitch.DOWNLINK_BW);
        setDownlinkBandwidth(DOWNLINK_BW);
        setSwitchingDelay(SWITCHING_DELAY);
        setPorts(PORTS);
        
    }

    @Override
    protected void processPacketDown(SimEvent ev) {
        super.processPacketDown(ev);
        
        NetworkPacket hspkt = (NetworkPacket) ev.getData();
        int recvVmId = hspkt.pkt.receiverVmId;
        
        // packet is coming from root so need to be sent to edgelevel swich
        // find the id for edgelevel switch
        int switchid = getDatacenter().vmToSwitchMap.get(recvVmId);
        List<NetworkPacket> packetList = getDownlinkSwitchPacketList().get(switchid);
        if (packetList == null) {
            packetList = new ArrayList<>();
            getDownlinkSwitchPacketList().put(switchid, packetList);
        }
        packetList.add(hspkt);
    }

    @Override
    protected void processPacketUp(SimEvent ev) {
        super.processPacketUp(ev);
        
        NetworkPacket hspkt = (NetworkPacket) ev.getData();
        int recvVmId = hspkt.pkt.receiverVmId;

        // packet is coming from edge level router so need to be sent to
        // either root or another edge level swich
        // find the id for edgelevel switch
        int switchid = getDatacenter().vmToSwitchMap.get(recvVmId);
        boolean flagtoswtich = false;
        for (Switch sw : getDownlinkSwitches()) {
            if (switchid == sw.getId()) {
                flagtoswtich = true;
                break;
            }
        }
        
        if (flagtoswtich) {
            List<NetworkPacket> pktlist = getDownlinkSwitchPacketList().get(switchid);
            if (pktlist == null) {
                pktlist = new ArrayList<>();
                getDownlinkSwitchPacketList().put(switchid, pktlist);
            }
            pktlist.add(hspkt);
        } else { // send to up
            Switch sw = getUplinkSwitches().get(0);
            List<NetworkPacket> pktlist = getUplinkSwitchPacketList().get(sw.getId());
            if (pktlist == null) {
                pktlist = new ArrayList<>();
                getUplinkSwitchPacketList().put(sw.getId(), pktlist);
            }
            pktlist.add(hspkt);
        }
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

}
