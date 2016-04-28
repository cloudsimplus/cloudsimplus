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
import org.cloudbus.cloudsim.Log;

import org.cloudbus.cloudsim.core.SimEvent;

/**
 * This class allows to simulate Root switch which connects Datacenters to
 * external network. It interacts with other switches in order to exchange
 * packets.
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
 * @since CloudSim Toolkit 3.0
 */
public class RootSwitch extends Switch {
    /**
     * The level (layer) of the switch in the network topology.
     */
    public static final int LEVEL = 0;

    /**
     * Default number of root switch ports that defines the number of
     * {@link AggregateSwitch} that can be connected to it.
     */
    public static int PORTS = 1;
    
    /**
     * Default switching delay in milliseconds.
     */
    public static double SWITCHING_DELAY = 0.00285;
    
    /**
     * The downlink bandwidth of RootSwitch in Megabits/s.
     * It also represents the uplink bandwidth of connected aggregation switches.
     */
    public static final long DOWNLINK_BW = 40 * 1024 * 1024 * 1024; // 40000 Megabits (40 Gigabits)

    /**
     * Instantiates a Root Switch specifying what other switches are connected
     * to its downlink ports, and corresponding bandwidths.
     *
     * @param name Name of the root switch
     * @param dc The Datacenter where the switch is connected to
     */
    public RootSwitch(String name, NetworkDatacenter dc) {
        super(name, LEVEL, dc);
        setDownlinkBandwidth(DOWNLINK_BW);
        setSwitchingDelay(SWITCHING_DELAY);
        setPorts(PORTS);
    }

    @Override
    protected void processPacketUp(SimEvent ev) {
        super.processPacketUp(ev);
        
        NetworkPacket hspkt = (NetworkPacket) ev.getData();
        int recvVmId = hspkt.pkt.receiverVmId;

        // get id of edge switch
        int edgeSwitchId = getDatacenter().vmToSwitchMap.get(recvVmId);
        // search which aggregate switch has it
        int aggSwitchId = -1;

        for (Switch sw : getDownlinkSwitches()) {
            for (Switch edge : sw.getDownlinkSwitches()) {
                if (edge.getId() == edgeSwitchId) {
                    aggSwitchId = sw.getId();
                    break;
                }
            }
        }
        
        if (aggSwitchId < 0) {
            Log.printLine("No destination switch for this packet");
        } else {
            List<NetworkPacket> packetList = getDownlinkSwitchPacketList().get(aggSwitchId);
            if (packetList == null) {
                packetList = new ArrayList<>();
                getDownlinkSwitchPacketList().put(aggSwitchId, packetList);
            }
            packetList.add(hspkt);
        }
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }
}
