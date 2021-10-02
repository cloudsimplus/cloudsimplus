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
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.HostPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an Edge Switch in a Datacenter network, which can be connected to {@link NetworkHost}s.
 * It interacts with other Datacenter in order to exchange packets.
 *
 * <br>Please refer to following publication for more details:<br>
 * <ul>
 * <li>
 * <a href="https://doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 3.0
 */
public class EdgeSwitch extends AbstractSwitch {
    /**
     * The level (layer) of the switch in the network topology.
     */
    public static final int LEVEL = 2;

    /**
     * Default number of ports that defines the number of
     * {@link Host} that can be connected to the switch.
     */
    public static final int PORTS = 4;

    /**
     * Default downlink bandwidth of EdgeSwitch in Megabits/s.
     * It also represents the uplink bandwidth of connected hosts.
     */
    private static final long DEF_DOWNLINK_BW = 100 * 8;

    /**
     * Default switching delay in milliseconds.
     */
    private static final double DEF_SWITCHING_DELAY = 0.00157;

    /**
     * List of hosts connected to the switch.
     */
    private final List<NetworkHost> hostList;

    /**
     * Instantiates a EdgeSwitch specifying Datacenter that are connected to its
     * downlink and uplink ports, and corresponding bandwidths. In this switch,
     * downlink ports aren't connected to other switch but to hosts.
     *
     * @param simulation the CloudSim instance that represents the simulation the Entity belongs
     * @param dc The Datacenter where the switch is connected to
     */
    public EdgeSwitch(final CloudSim simulation, final NetworkDatacenter dc) {
        super(simulation, dc);

        this.hostList = new ArrayList<>();
        setUplinkBandwidth(AggregateSwitch.DOWNLINK_BW);
        setDownlinkBandwidth(DEF_DOWNLINK_BW);
        setSwitchingDelay(DEF_SWITCHING_DELAY);
        setPorts(PORTS);
    }

    @Override
    protected void processPacketDown(final SimEvent evt) {
        super.processPacketDown(evt);

        // packet is to be received by host
        final HostPacket pkt = extractReceivedHostPacket(evt);
        addPacketToSendToHost(pkt.getDestination(), pkt);
    }

    private HostPacket extractReceivedHostPacket(final SimEvent evt) {
        final var pkt = (HostPacket) evt.getData();
        final var receiverVm = pkt.getVmPacket().getDestination();
        final var host = getVmHost(receiverVm);
        pkt.setDestination(host);
        return pkt;
    }

    @Override
    protected void processPacketUp(final SimEvent evt) {
        super.processPacketUp(evt);

        /* packet is received from host and to be sent to
        aggregate level or to another host in the same level */
        final HostPacket pkt = extractReceivedHostPacket(evt);

        // packet needs to go to a host which is connected directly to switch
        if (pkt.getDestination() != null && pkt.getDestination() != Host.NULL) {
            addPacketToSendToHost(pkt.getDestination(), pkt);
            return;
        }

        // otherwise, packet is to be sent to upper switch
        /*
         * ASSUMPTION: Each Edge is connected to one Aggregate Switch.
         * If there are more than one Aggregate Switch, the following code has to be modified.
        */
        addPacketToBeSentToFirstUplinkSwitch(pkt);
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    /**
     * Gets a <b>read-only</b> list of Hosts connected to the switch.
     * @return
     */
    public List<NetworkHost> getHostList() {
        return Collections.unmodifiableList(hostList);
    }

    /**
     * Connects a {@link NetworkHost} to the switch, by adding it to the
     * {@link #getHostList()}.
     * @param host the host to be connected to the switch
     */
    public void connectHost(final NetworkHost host) {
        hostList.add(Objects.requireNonNull(host));
        host.setEdgeSwitch(this);
    }

    /**
     * Disconnects a {@link NetworkHost} from the switch, by removing it from the
     * {@link #getHostList()}.
     * @param host the host to be disconnected from the switch
     * @return true if the Host was connected to the switch, false otherwise
     */
    public boolean disconnectHost(final NetworkHost host) {
        if(hostList.remove(host)){
            host.setEdgeSwitch(null);
            return true;
        }

        return false;
    }
}
