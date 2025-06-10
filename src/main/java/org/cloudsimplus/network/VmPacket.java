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
import lombok.experimental.Accessors;
import org.cloudsimplus.cloudlets.network.NetworkCloudlet;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.vms.network.NetworkVm;

/// Represents a packet that travels from a [NetworkVm] to another, through the virtual network
/// within a [NetworkHost]. It contains information about [NetworkCloudlet]s that are communicating.
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
@Accessors @Getter @Setter
public class VmPacket implements NetworkPacket<NetworkVm> {

    /**
     * The VM sending the packet.
     * This is the VM where the {@link #getSenderCloudlet() sending cloudlet} is running.
     */
    @NonNull
    private NetworkVm source;

    /**
     * The VM that has to receive the packet.
     * This is the VM where the {@link #getReceiverCloudlet() receiver cloudlet} is running.
     */
    @NonNull
    private NetworkVm destination;

    /**
     * The cloudlet sending the packet.
     */
    @NonNull
    private final NetworkCloudlet senderCloudlet;

    /**
     * The cloudlet that has to receive the packet.
     */
    @NonNull
    private final NetworkCloudlet receiverCloudlet;

    private final long size;

    private double sendTime;
    private double receiveTime;

    /**
     * Creates a packet to be sent to a VM inside the Host of the sender VM.
     *
     * @param sourceVm the VM sending the packet
     * @param destinationVm the VM that has to receive the packet
     * @param size data length of the packet in bytes
     * @param senderCloudlet cloudlet sending the packet
     * @param receiverCloudlet cloudlet that has to receive the packet
     */
    public VmPacket(
        final NetworkVm sourceVm,
        final NetworkVm destinationVm,
        final long size,
        final NetworkCloudlet senderCloudlet,
        final NetworkCloudlet receiverCloudlet)
    {
        super();
        this.source = sourceVm;
        this.destination = destinationVm;
        this.size = size;
        this.receiverCloudlet = receiverCloudlet;
        this.senderCloudlet = senderCloudlet;
    }

    public NetworkHost getDestinationHost() {
        return destination.getHost();
    }
}
