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
import org.cloudbus.cloudsim.vms.Vm;

/**
 * This class represents an Edge AbstractSwitch in a Datacenter network. It interacts
 * with other Datacenter in order to exchange packets.
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
 *
 */
public class EdgeSwitch extends AbstractSwitch {
    /**
     * The level (layer) of the switch in the network topology.
     */
    public static final int LEVEL = 2;

    /**
     * Default downlink bandwidth of EdgeSwitch in Megabits/s.
     * It also represents the uplink bandwidth of connected hosts.
     */
    public static final long DOWNLINK_BW = 100 * 8;

    /**
     * Default number of ports that defines the number of
     * {@link Host} that can be connected to the switch.
     */
    public static final int PORTS = 4;

    /**
     * Default switching delay in milliseconds.
     */
    public static final double SWITCHING_DELAY = 0.00157;

    /**
     * Instantiates a EdgeSwitch specifying Datacenter that are connected to its
     * downlink and uplink ports, and corresponding bandwidths. In this switch,
     * downlink ports aren't connected to other switch but to hosts.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param dc The Datacenter where the switch is connected to
     */
    public EdgeSwitch(CloudSim simulation, NetworkDatacenter dc) {
        super(simulation, dc);

        setUplinkBandwidth(AggregateSwitch.DOWNLINK_BW);
        setDownlinkBandwidth(DOWNLINK_BW);
        setSwitchingDelay(SWITCHING_DELAY);
        setPorts(PORTS);
    }

    @Override
    protected void processPacketDown(SimEvent evt) {
        super.processPacketDown(evt);

        // packet is to be received by host
        final HostPacket pkt = extractReceivedHostPacket(evt);
        addPacketToBeSentToHost(pkt.getDestination(), pkt);
    }

    private HostPacket extractReceivedHostPacket(final SimEvent evt) {
        final HostPacket pkt = (HostPacket) evt.getData();
        final Vm receiverVm = pkt.getVmPacket().getDestination();
        final NetworkHost host = getVmHost(receiverVm);
        pkt.setDestination(host);
        return pkt;
    }

    @Override
    protected void processPacketUp(SimEvent evt) {
        super.processPacketUp(evt);

        // packet is received from host
        // packet is to be sent to aggregate level or to another host in the same level
        final HostPacket pkt = extractReceivedHostPacket(evt);

        // packet needs to go to a host which is connected directly to switch
        if (pkt.getDestination() != null && pkt.getDestination() != Host.NULL) {
            addPacketToBeSentToHost(pkt.getDestination(), pkt);
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

}
