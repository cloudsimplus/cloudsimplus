/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.network.switches;

import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.network.HostPacket;
import org.cloudsimplus.util.BytesConversion;

/**
 * This class represents an Aggregate Switch in a Datacenter network.
 * It interacts with another Datacenter to exchange packets.
 *
 * <p>Please refer to the following publication for more details:
 * <ul>
 * <li>
 * <a href="https://doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </li>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 1.0
 */
public class AggregateSwitch extends AbstractSwitch {
    /**
     * The level (layer) of the Switch in the network topology.
     */
    public static final int LEVEL = 1;

    /**
     * Default delay of {@link AggregateSwitch} in milliseconds.
     */
    public static final double SWITCHING_DELAY = 0.00245;

    /**
     * Default downlink bandwidth of {@link AggregateSwitch} in Megabits/s.
     * It also represents the uplink bandwidth of the connected Edge Datacenter.
     */
    public static final long DOWNLINK_BW =  (long) BytesConversion.MEGA * 100 * 8;

    /**
     * Default number of aggregation switch ports that defines the number of
     * {@link EdgeSwitch} that can be connected to it.
     */
    public static final int PORTS = 1;

    /**
     * Instantiates an Aggregate Switch, specifying the Datacenter which is
     * connected to its downlink and uplink ports and corresponding bandwidths.
     *
     * @param simulation the CloudSimPlus instance that represents the simulation the Entity belongs
     * @param dc The Datacenter where the switch is connected to
     */
    public AggregateSwitch(final CloudSimPlus simulation, final NetworkDatacenter dc) {
        super(simulation, dc);
        setUplinkBandwidth(RootSwitch.DOWNLINK_BW);
        setDownlinkBandwidth(DOWNLINK_BW);
        setSwitchingDelay(SWITCHING_DELAY);
        setPorts(PORTS);
    }

    @Override
    protected void processPacketDown(final SimEvent evt) {
        /* packet is coming from root switch,
        so it needs to be sent to edge switch */
        super.processPacketDown(evt);
        final var netPkt = (HostPacket) evt.getData();
        final var downlinkSw = netPkt.getVmEdgeSwitch();
        addPacketToSendToDownlinkSwitch(downlinkSw, netPkt);
    }

    @Override
    protected void processPacketUp(final SimEvent evt) {
        // A packet is coming from edge router, so it needs to be sent to either root or another edge switch
        super.processPacketUp(evt);
        final var netPkt = (HostPacket) evt.getData();
        final var downlinkSw = netPkt.getVmEdgeSwitch();

        if (findConnectedEdgeSwitch(downlinkSw))
            addPacketToSendToDownlinkSwitch(downlinkSw, netPkt);
        else addPacketToBeSentToFirstUplinkSwitch(netPkt);
    }

    /**
     * Checks if the Aggregate Switch is connected to a given {@link EdgeSwitch}.
     * @param edgeSwitch the Edge Switch to check if the Aggregate Switch is connected to
     * @return true if the Edge Switch was found, false otherwise
     */
    private boolean findConnectedEdgeSwitch(final Switch edgeSwitch) {
        return getDownlinkSwitches().stream().anyMatch(edgeSwitch::equals);
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }
}
