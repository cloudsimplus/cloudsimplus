/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * Represents a packet that travels from a {@link Vm} to another, through the virtual network
 * within a {@link Host}. It contains information about Cloudlets which are
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
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 1.0
 */
public class VmPacket implements NetworkPacket<Vm> {

    /**
     * @see NetworkPacket#getSource()
     */
    private Vm sourceVm;

    /**
     * @see #getDestination()
     */
    private Vm destinationVm;

    /**
     * @see #getSenderCloudlet()
     */
    private final Cloudlet senderCloudlet;

    /**
     * @see #getReceiverCloudlet()
     */
    private final Cloudlet receiverCloudlet;

    /**
     * @see #getSize()
     */
    private final long size;

    /**
     * The time the packet was sent.
     */
    private double sendTime;

    /**
     * The time the packet was received.
     */
    private double receiveTime;

    /**
     * Creates a packet to be sent to to a VM inside the
     * Host of the sender VM.
     * @param sourceVm id of the VM sending the packet
     * @param destinationVm id of the VM that has to receive the packet
     * @param size data length of the packet in bytes
     * @param senderCloudlet cloudlet sending the packet
     * @param receiverCloudlet cloudlet that has to receive the packet
     */
    public VmPacket(
        Vm sourceVm,
        Vm destinationVm,
        long size,
        Cloudlet senderCloudlet,
        Cloudlet receiverCloudlet) {
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
    public Vm getSource() {
        return sourceVm;
    }

    /**
     * Sets the id of the VM sending the packet.
     * This is the VM where the {@link #getSenderCloudlet() sending cloudlet}
     * is running.
     *
     * @param sourceVmId the source VM id to set
     */
    @Override
    public void setSource(Vm sourceVmId) {
        this.sourceVm = sourceVmId;
    }

    /**
     * Gets the id of the VM that has to receive the packet.
     * This is the VM where th {@link #getReceiverCloudlet() receiver cloudlet}
     * is running.
     *
     * @return
     */
    @Override
    public Vm getDestination() {
        return destinationVm;
    }

    /**
     * Sets the id of the VM that has to receive the packet.
     * This is the VM where th {@link #getReceiverCloudlet() receiver cloudlet}
     * is running.
     *
     * @param destinationVmId the destination VM id to set
     */
    @Override
    public void setDestination(Vm destinationVmId) {
        this.destinationVm = destinationVmId;
    }

    /**
     * Gets the cloudlet sending the packet.
     * @return
     */
    public Cloudlet getSenderCloudlet() {
        return senderCloudlet;
    }

    /**
     * Gets the cloudlet that has to receive the packet.
     * @return
     */
    public Cloudlet getReceiverCloudlet() {
        return receiverCloudlet;
    }

    @Override
    public long getSize() {
        return size;
    }
}
