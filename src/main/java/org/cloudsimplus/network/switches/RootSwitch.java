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
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.network.HostPacket;
import org.cloudsimplus.util.BytesConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Allows simulating a Root switch that connects {@link Datacenter}s to
 * an external network. It interacts with another Datacenter to exchange
 * packets.
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
 * @since CloudSim Toolkit 3.0
 */
public class RootSwitch extends AbstractSwitch {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootSwitch.class.getSimpleName());

    /**
     * The level (layer) of the switch in the network topology.
     */
    public static final int LEVEL = 0;

    /**
     * Default number of root switch ports that defines the number of
     * {@link AggregateSwitch}es that can be connected to it.
     */
    public static final int PORTS = 1;

    /**
     * Default switching delay in milliseconds.
     */
    public static final double SWITCHING_DELAY = 0.00285;

    /**
     * The downlink bandwidth of RootSwitch in Megabits/s.
     * It also represents the uplink bandwidth of connected aggregation Datacenter.
     */
    public static final long DOWNLINK_BW = (long) BytesConversion.gigaToMega(40 * 8); // 40000 Megabits (40 Gigabits)

    /**
     * Instantiates a Root Switch, specifying which another Datacenter is connected
     * to its downlink ports and corresponding bandwidths.
     *
     * @param simulation the CloudSimPlus instance that represents the simulation the Switch belongs to
     * @param dc the Datacenter where the Switch is connected to
     */
    public RootSwitch(final CloudSimPlus simulation, final NetworkDatacenter dc) {
        super(simulation, dc);
        setDownlinkBandwidth(DOWNLINK_BW);
        setSwitchingDelay(SWITCHING_DELAY);
        setPorts(PORTS);
    }

    @Override
    protected void processPacketUp(final SimEvent evt) {
        super.processPacketUp(evt);
        final var netPkt = (HostPacket) evt.getData();
        final var edgeSwitch = netPkt.getVmEdgeSwitch();

        final var optionalAggrSw = findAggregateConnectedToEdgeSwitch(edgeSwitch);
        optionalAggrSw.ifPresentOrElse(
            aggSw -> addPacketToSendToDownlinkSwitch(aggSw, netPkt),
            () -> LOGGER.error("No destination switch for this packet")
        );
    }

    /**
     * Finds which {@link AggregateSwitch} is connected to a given {@link EdgeSwitch}
     * @param edgeSwitch the edge switch to find the AggregateSwitch that it is connected to
     * @return an {@link Optional} with the AggregateSwitch that is connected to the given
     * EdgeSwitch; or an empty Optional if not found.
     */
    private Optional<Switch> findAggregateConnectedToEdgeSwitch(final Switch edgeSwitch) {
        //List of Aggregate Switches connected to this Root Switch
        final var aggregateSwitchList = getDownlinkSwitches();
        return aggregateSwitchList
                .stream()
                .filter(aggregateSw -> isEdgeConnectedToAggregatedSwitch(edgeSwitch, aggregateSw))
                .findFirst();
    }

    private boolean isEdgeConnectedToAggregatedSwitch(final Switch edgeSwitch, final Switch aggregateSw) {
        //List of Edge Switches connected to the given Aggregate Switch
        final var edgeSwitchList = aggregateSw.getDownlinkSwitches();
        return edgeSwitchList.stream().anyMatch(edgeSwitch::equals);
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }
}
