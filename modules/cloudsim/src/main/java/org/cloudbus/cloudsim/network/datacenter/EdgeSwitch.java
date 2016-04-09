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
import java.util.Map.Entry;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.predicates.PredicateType;

/**
 * This class represents an Edge Switch in a Datacenter network. It interacts
 * with other switches in order to exchange packets.
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
 * @since CloudSim Toolkit 3.0
 */
public class EdgeSwitch extends Switch {

    /**
     * The downlink bandwidth of EdgeSwitch in Megabits/s.
     * It also represents the uplink bandwidth of connected hosts.
     */
    public static long DownlinkBW = 100 * 1024 * 1024;
    
    /**
     * Number of ports that defines the number of
     * {@link org.cloudbus.cloudsim.Host} that can be connected to the switch.
     */
    public static int Ports = 4;
    /**
     * The delay of in milliseconds.
     */
    public static double SwitchingDelay = 0.00157;

    /**
     * Instantiates a EdgeSwitch specifying switches that are connected to its
     * downlink and uplink ports, and corresponding bandwidths. In this switch,
     * downlink ports aren't connected to other switch but to hosts.
     *
     * @param name Name of the switch
     * @param dc The Datacenter where the switch is connected to
     */
    public EdgeSwitch(String name, NetworkDatacenter dc) {
        super(name, EDGE_SWITCHES_LEVEL, dc);
        hostList = new HashMap<>();
        uplinkSwitchPacketList = new HashMap<>();
        packetToHost = new HashMap<>();
        uplinkBandwidth = AggregateSwitch.DownlinkBW;
        downlinkBandwidth = DownlinkBW;
        switchingDelay = SwitchingDelay;
        numPort = Ports;
        uplinkSwitches = new ArrayList<>();
    }

    @Override
    protected void processPacketUp(SimEvent ev) {
        // packet coming from down level router/host.
        // has to send up
        // check which switch to forward to
        // add packet in the switch list
        //
        // int src=ev.getSource();
        NetworkPacket hspkt = (NetworkPacket) ev.getData();
        int recvVMid = hspkt.pkt.receiverVmId;
        CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.Network_Event_send));
        schedule(getId(), switchingDelay, CloudSimTags.Network_Event_send);

        // packet is recieved from host
        // packet is to be sent to aggregate level or to another host in the same level
        int hostid = dc.vmToHostMap.get(recvVMid);
        NetworkHost hs = hostList.get(hostid);
        hspkt.receiverHostId = hostid;

        // packet needs to go to a host which is connected directly to switch
        if (hs != null) {
            // packet to be sent to host connected to the switch
            List<NetworkPacket> pktlist = packetToHost.get(hostid);
            if (pktlist == null) {
                pktlist = new ArrayList<>();
                packetToHost.put(hostid, pktlist);
            }
            pktlist.add(hspkt);
            return;

        }
        // otherwise
        // packet is to be sent to upper switch
        // ASSUMPTION EACH EDGE is Connected to one aggregate level switch
        // if there are more than one Aggregate level switch one need to modify following code

        Switch sw = uplinkSwitches.get(0);
        List<NetworkPacket> pktlist = uplinkSwitchPacketList.get(sw.getId());
        if (pktlist == null) {
            pktlist = new ArrayList<>();
            uplinkSwitchPacketList.put(sw.getId(), pktlist);
        }
        pktlist.add(hspkt);
        return;

    }

    @Override
    protected void processPacketForward(SimEvent ev) {
        // search for the host and packets..send to them

        if (uplinkSwitchPacketList != null) {
            for (Entry<Integer, List<NetworkPacket>> es : uplinkSwitchPacketList.entrySet()) {
                int tosend = es.getKey();
                List<NetworkPacket> hspktlist = es.getValue();
                if (!hspktlist.isEmpty()) {
                    // sharing bandwidth between packets
                    double avband = uplinkBandwidth / hspktlist.size();
                    Iterator<NetworkPacket> it = hspktlist.iterator();
                    while (it.hasNext()) {
                        NetworkPacket hspkt = it.next();
                        double delay = 1000 * hspkt.pkt.dataLength / avband;

                        this.send(tosend, delay, CloudSimTags.Network_Event_UP, hspkt);
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
                        // hs.packetrecieved.add(hspkt);
                        this.send(getId(), hspkt.pkt.dataLength / avband, CloudSimTags.Network_Event_Host, hspkt);
                    }
                    hspktlist.clear();
                }
            }
        }

        // or to switch at next level.
        // clear the list
    }

}
