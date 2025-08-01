/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.network.switches;

import lombok.NonNull;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.network.HostPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an Edge Switch in a Datacenter network, which can be connected to {@link NetworkHost}s.
 * It interacts with another Datacenter to exchange packets.
 *
 * <br>Please refer to the following publication for more details:<br>
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
     * The level (layer) of the Switch in the network topology.
     */
    public static final int LEVEL = 2;

    /**
     * Default number of ports that defines the number of
     * {@link Host} that can be connected to the Switch.
     */
    public static final int PORTS = 4;

    /**
     * Default downlink bandwidth of {@link EdgeSwitch} in Megabits/s.
     * It also represents the uplink bandwidth of connected hosts.
     */
    private static final long DEF_DOWNLINK_BW = 100 * 8;

    /**
     * Default switching delay in milliseconds.
     */
    private static final double DEF_SWITCHING_DELAY = 0.00157;

    /**
     * List of hosts connected to the Switch.
     */
    private final List<NetworkHost> hostList;

    /**
     * Instantiates a {@link EdgeSwitch}, specifying the Datacenter which is connected to its
     * downlink and uplink ports and corresponding bandwidths. In this Switch,
     * downlink ports aren't connected to another Switch but to Hosts.
     *
     * @param simulation the CloudSimPlus instance that represents the simulation the Switch belongs to
     * @param dc the Datacenter where the Switch is connected to
     */
    public EdgeSwitch(final CloudSimPlus simulation, final NetworkDatacenter dc) {
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

        // packet to be received by the Host
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

        /* the packet received from the Host and to be sent to
        aggregate level or to another Host in the same level */
        final HostPacket pkt = extractReceivedHostPacket(evt);

        // the packet needs to go to a host which is connected directly to switch
        if (pkt.getDestination() != null && pkt.getDestination() != Host.NULL) {
            addPacketToSendToHost(pkt.getDestination(), pkt);
            return;
        }

        // otherwise, the packet is to be sent to upper switch
        /*
         * ASSUMPTION: Each Edge is connected to one Aggregate Switch.
         * If there is more than one Aggregate Switch, the following code has to be modified.
        */
        addPacketToBeSentToFirstUplinkSwitch(pkt);
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    /**
     * @return a <b>read-only</b> list of Hosts connected to the switch.
     */
    public List<NetworkHost> getHostList() {
        return Collections.unmodifiableList(hostList);
    }

    /**
     * Connects a {@link NetworkHost} to the Switch, by adding it to the
     * {@link #getHostList()}.
     * @param host the host to be connected to the switch
     */
    public void connectHost(@NonNull final NetworkHost host) {
        hostList.add(host);
        host.setEdgeSwitch(this);
    }

    /**
     * Disconnects a {@link NetworkHost} from the Switch, by removing it from the
     * {@link #getHostList()}.
     * @param host the host to be disconnected from the Switch
     * @return true if the Host was connected to the Switch, false otherwise
     */
    public boolean disconnectHost(@NonNull final NetworkHost host) {
        if(hostList.remove(host)){
            host.setEdgeSwitch(null);
            return true;
        }

        return false;
    }
}
