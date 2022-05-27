/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network;

import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.vms.network.NetworkVm;

/**
 * Represents a packet that travels from a {@link NetworkVm} to another, through the virtual network
 * within a {@link NetworkHost}. It contains information about {@link NetworkCloudlet}s which are
 * communicating.
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
 * @since CloudSim Toolkit 1.0
 */
public class VmPacket implements NetworkPacket<NetworkVm> {

    /** @see #getSource() */
    private NetworkVm sourceVm;

    /** @see #getDestination() */
    private NetworkVm destinationVm;

    /** @see #getSenderCloudlet() */
    private final NetworkCloudlet senderCloudlet;

    /** @see #getReceiverCloudlet() */
    private final NetworkCloudlet receiverCloudlet;

    /** @see #getSize() */
    private final long size;

    /**
     * The time (in seconds) the packet was sent.
     */
    private double sendTime;

    /**
     * The time (in seconds) the packet was received.
     */
    private double receiveTime;

    /**
     * Creates a packet to be sent to a VM inside the
     * Host of the sender VM.
     * @param sourceVm id of the VM sending the packet
     * @param destinationVm id of the VM that has to receive the packet
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
        this.sourceVm = sourceVm;
        this.destinationVm = destinationVm;
        this.size = size;
        this.receiverCloudlet = receiverCloudlet;
        this.senderCloudlet = senderCloudlet;
    }

    @Override
    public double getSendTime() {
        return sendTime;
    }

    @Override
    public void setSendTime(double sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public double getReceiveTime() {
        return receiveTime;
    }

    @Override
    public void setReceiveTime(double receiveTime) {
        this.receiveTime = receiveTime;
    }

    /**
     * Gets the VM sending the packet.
     * This is the VM where the {@link #getSenderCloudlet() sending cloudlet}
     * is running.
     *
     * @return
     */
    @Override
    public NetworkVm getSource() {
        return sourceVm;
    }

    /**
     * Sets the VM sending the packet.
     * This is the VM where the {@link #getSenderCloudlet() sending cloudlet}
     * is running.
     *
     * @param sourceVm the source VM to set
     */
    @Override
    public void setSource(final NetworkVm sourceVm) {
        this.sourceVm = sourceVm;
    }

    /**
     * Gets the VM that has to receive the packet.
     * This is the VM where th {@link #getReceiverCloudlet() receiver cloudlet}
     * is running.
     *
     * @return
     */
    @Override
    public NetworkVm getDestination() {
        return destinationVm;
    }

    /**
     * Sets the VM that has to receive the packet.
     * This is the VM where th {@link #getReceiverCloudlet() receiver cloudlet}
     * is running.
     *
     * @param destinationVm the destination VM to set
     */
    @Override
    public void setDestination(final NetworkVm destinationVm) {
        this.destinationVm = destinationVm;
    }

    public NetworkHost getDestinationHost() {
        return destinationVm.getHost();
    }

    /**
     * Gets the cloudlet sending the packet.
     * @return
     */
    public NetworkCloudlet getSenderCloudlet() {
        return senderCloudlet;
    }

    /**
     * Gets the cloudlet that has to receive the packet.
     * @return
     */
    public NetworkCloudlet getReceiverCloudlet() {
        return receiverCloudlet;
    }

    @Override
    public long getSize() {
        return size;
    }
}
