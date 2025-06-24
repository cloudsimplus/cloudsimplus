/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.network;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.network.switches.EdgeSwitch;

/// Represents a packet which travels from one [Host] to another.
/// Each packet contains the:
///
/// - sender VM into the source Host and the receiver VM into the destination Host which are communicating;
/// - time at which it is sent and received;
/// - type and virtual IDs of tasks.
///
/// Please refer to the following publication for more details:
///
/// - [Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
///   Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
///   International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
///   Press, USA), Melbourne, Australia, December 5-7, 2011.](https://doi.org/10.1109/UCC.2011.24)
///
/// @author Saurabh Kumar Garg
/// @author Manoel Campos da Silva Filho
///
/// @since CloudSim Toolkit 1.0
@Getter @Setter
public final class HostPacket implements NetworkPacket<NetworkHost> {

    /**
     * Information about the virtual sender and receiver entities of the packet
     * (the sender and receiver Cloudlet and their respective VMs).
     */
    private final VmPacket vmPacket;

    /**
     * The {@link Host} that this packet is coming from (the sender).
     */
    private NetworkHost source;

    /**
     * The {@link Host} that the packet is going to (the receiver).
     */
    private NetworkHost destination;

    private double sendTime;
    private double receiveTime;

    /**
     * Creates a packet to be sent through the network between two hosts.
     *
     * @param sourceHost host sending the packet
     * @param vmPacket vm packet containing information of sender and receiver Cloudlets and their VMs.
     */
    public HostPacket(final NetworkHost sourceHost, @NonNull final VmPacket vmPacket) {
        this.vmPacket = vmPacket;
        this.sendTime = vmPacket.getSendTime();
        this.setSource(sourceHost);
    }

    /**
     * {@inheritDoc}
     * It is the size of the enclosing {@link VmPacket}.
     * @return {@inheritDoc}
     */
    @Override
    public long getSize() {
        return vmPacket.getSize();
    }

    /**
     * Gets the {@link EdgeSwitch} that the Host where the VM receiving a packet is connected to.
     * @return the Edge Switch connected to the Host where the targeting VM is placed
     */
    public EdgeSwitch getVmEdgeSwitch() {
        return vmPacket.getDestinationHost().getEdgeSwitch();
    }
}
