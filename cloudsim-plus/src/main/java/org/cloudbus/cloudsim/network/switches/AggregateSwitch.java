/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.switches;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.network.HostPacket;
import org.cloudbus.cloudsim.util.Conversion;

/**
 * This class represents an Aggregate AbstractSwitch in a Datacenter network. It
 * interacts with other Datacenter in order to exchange packets.
 *
 * <p>Please refer to following publication for more details:
 * <ul>
 * <li>
 * <a href="https://doi.org/10.1109/UCC.2011.24">
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
public class AggregateSwitch extends AbstractSwitch {
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
     * It also represents the uplink bandwidth of connected edge Datacenter.
     */
    public static final long DOWNLINK_BW =  (long) Conversion.MEGA * 100 * 8;

    /**
     * Default number of aggregation switch ports that defines the number of
     * {@link EdgeSwitch} that can be connected to it.
     */
    public static final int PORTS = 1;

    /**
     * Instantiates a Aggregate AbstractSwitch specifying the Datacenter that are
     * connected to its downlink and uplink ports and corresponding bandwidths.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param dc The Datacenter where the switch is connected to
     */
    public AggregateSwitch(CloudSim simulation, NetworkDatacenter dc) {
        super(simulation, dc);
        setUplinkBandwidth(RootSwitch.DOWNLINK_BW);
        setDownlinkBandwidth(DOWNLINK_BW);
        setSwitchingDelay(SWITCHING_DELAY);
        setPorts(PORTS);

    }

    @Override
    protected void processPacketDown(SimEvent evt) {
        // packet is coming from root so need to be sent to edgelevel swich
        // find the id for edgelevel switch
        super.processPacketDown(evt);
        final HostPacket netPkt = (HostPacket) evt.getData();
        final Switch downlinkSw = getVmEdgeSwitch(netPkt);
        addPacketToBeSentToDownlinkSwitch(downlinkSw, netPkt);
    }

    @Override
    protected void processPacketUp(SimEvent evt) {
        // packet is coming from edge level router so need to be sent to
        // either root or another edge level swich
        // find the id for edge level switch
        super.processPacketUp(evt);
        final HostPacket netPkt = (HostPacket) evt.getData();
        final Switch downlinkSw = getVmEdgeSwitch(netPkt);

        if (findConnectedEdgeSwitch(downlinkSw)) {
            addPacketToBeSentToDownlinkSwitch(downlinkSw, netPkt);
        } else { // send to up
            addPacketToBeSentToFirstUplinkSwitch(netPkt);
        }
    }

    /**
     * Checks if the Aggregate switch is connected to a given Edge switch.
     * @param edgeSwitch the id of the edge switch to check if the aggregate switch
     * is connected to
     * @return true if the edge switch was found, false othersise
     */
    private boolean findConnectedEdgeSwitch(Switch edgeSwitch) {
        return getDownlinkSwitches().stream().anyMatch(edgeSwitch::equals);
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

}
