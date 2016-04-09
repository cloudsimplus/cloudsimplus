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
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.predicates.PredicateType;

/**
 * This class represents an Aggregate Switch in a Datacenter network. It
 * interacts with other switches in order to exchange packets.
 *
 * <br/>Please refer to following publication for more details:<br/>
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 *
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 1.0
 */
public class AggregateSwitch extends Switch {

    /**
     * The delay of {@link AggregateSwitch} in milliseconds.
     */
    public static double SwitchingDelay = 0.00245;
    /**
     * The downlink bandwidth of {@link AggregateSwitch} in Megabits/s.
     * It also represents the uplink bandwidth of connected edge switches.
     */
    public static long DownlinkBW = 100 * 1024 * 1024; // 100 Megabits
    /**
     * Number of aggregation switch ports that defines the number of
     * {@link EdgeSwitch} that can be connected to it.
     */
    public static int Ports = 1;

    /**
     * Instantiates a Aggregate Switch specifying the switches that are
     * connected to its downlink and uplink ports and corresponding bandwidths.
     *
     * @param name Name of the switch
     * @param dc The Datacenter where the switch is connected to
     */
    public AggregateSwitch(String name, NetworkDatacenter dc) {
        super(name, AGGREGATE_SWITCHES_LEVEL, dc);
        downlinkSwitchPacketList = new HashMap<>();
        uplinkSwitchPacketList = new HashMap<>();
        uplinkBandwidth = RootSwitch.DownlinkBW;
        downlinkBandwidth = DownlinkBW;
        latency = SwitchingDelay;
        numPort = Ports;
        uplinkSwitches = new ArrayList<>();
        downlinkSwitches = new ArrayList<>();
    }

    @Override
    protected void processPacketDown(SimEvent ev) {
        // packet coming from up level router.
        // has to send downward
        // check which switch to forward to
        // add packet in the switch list
        // add packet in the host list
        NetworkPacket hspkt = (NetworkPacket) ev.getData();
        int recvVMid = hspkt.pkt.receiverVmId;
        CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.Network_Event_send));
        schedule(getId(), latency, CloudSimTags.Network_Event_send);

        if (level == AGGREGATE_SWITCHES_LEVEL) {
            // packet is coming from root so need to be sent to edgelevel swich
            // find the id for edgelevel switch
            int switchid = dc.vmToSwitchMap.get(recvVMid);
            List<NetworkPacket> packetList = downlinkSwitchPacketList.get(switchid);
            if (packetList == null) {
                packetList = new ArrayList<>();
                downlinkSwitchPacketList.put(switchid, packetList);
            }
            packetList.add(hspkt);
            return;
        }

    }

    @Override
    protected void processPacketUp(SimEvent ev) {
        // packet coming from down level router.
        // has to send up
        // check which switch to forward to
        // add packet in the switch list
        //
        // int src=ev.getSource();
        NetworkPacket hspkt = (NetworkPacket) ev.getData();
        int recvVMid = hspkt.pkt.receiverVmId;
        CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.Network_Event_send));
        schedule(getId(), switchingDelay, CloudSimTags.Network_Event_send);

        if (level == AGGREGATE_SWITCHES_LEVEL) {
            // packet is coming from edge level router so need to be sent to
            // either root or another edge level swich
            // find the id for edgelevel switch
            int switchid = dc.vmToSwitchMap.get(recvVMid);
            boolean flagtoswtich = false;
            for (Switch sw : downlinkSwitches) {
                if (switchid == sw.getId()) {
                    flagtoswtich = true;
                }
            }
            if (flagtoswtich) {
                List<NetworkPacket> pktlist = downlinkSwitchPacketList.get(switchid);
                if (pktlist == null) {
                    pktlist = new ArrayList<NetworkPacket>();
                    downlinkSwitchPacketList.put(switchid, pktlist);
                }
                pktlist.add(hspkt);
            } else// send to up
            {
                Switch sw = uplinkSwitches.get(0);
                List<NetworkPacket> pktlist = uplinkSwitchPacketList.get(sw.getId());
                if (pktlist == null) {
                    pktlist = new ArrayList<NetworkPacket>();
                    uplinkSwitchPacketList.put(sw.getId(), pktlist);
                }
                pktlist.add(hspkt);
            }
        }
    }

}
