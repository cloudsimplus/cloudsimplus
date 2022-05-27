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
import org.cloudbus.cloudsim.util.BytesConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Allows simulating a Root switch which connects Datacenters to
 * an external network. It interacts with other Datacenter in order to exchange
 * packets.
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

    /**
     * The level (layer) of the switch in the network topology.
     */
    public static final int LEVEL = 0;

    /**
     * Default number of root switch ports that defines the number of
     * {@link AggregateSwitch} that can be connected to it.
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

    private static final Logger LOGGER = LoggerFactory.getLogger(RootSwitch.class.getSimpleName());

    /**
     * Instantiates a Root Switch specifying what other Datacenter are connected
     * to its downlink ports, and corresponding bandwidths.
     *
     * @param simulation the CloudSim instance that represents the simulation the Entity belongs
     * @param dc the Datacenter where the switch is connected to
     */
    public RootSwitch(final CloudSim simulation, final NetworkDatacenter dc) {
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
     * Finds which aggregate switch is connected to a given edge switch
     * @param edgeSwitch the id of the edge switch to find the aggregate switch
     * that it is connected to
     * @return an {@link Optional} with the aggregate switch that is connected to the given
     * edge switch; or an empty Optional if not found.
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
