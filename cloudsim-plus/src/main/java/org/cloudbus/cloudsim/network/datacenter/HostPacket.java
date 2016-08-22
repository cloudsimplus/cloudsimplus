/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import org.cloudbus.cloudsim.Cloudlet;

/**
 * HostPacket represents a packet that travels through the virtual network
 * within a Host. It contains information about cloudlets which are
 * communicating.
 *
 * <br>Please refer to following publication for more details:<br>
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class HostPacket {

    /**
     * @see #getSenderVmId() 
     */
    private final int senderVmId;

    /**
     * @see #getReceiverVmId() 
     */
    private final int receiverVmId;

    /**
     * @see #getSenderCloudlet()   
     */
    private final Cloudlet senderCloudlet;

    /**
     * @see #getReceiverCloudlet() 
     */
    private final Cloudlet receiverCloudlet;

    /**
     * @see #getDataLength() 
     */
    private final double dataLength;

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
     * 
     * @param senderVmId id of the VM sending the packet
     * @param receiverVmId id of the VM that has to receive the packet
     * @param dataLength data length of the packet
     * @param senderCloudlet cloudlet sending the packet
     * @param receiverCloudlet cloudlet that has to receive the packet
     */
    public HostPacket(
            int senderVmId,
            int receiverVmId,
            double dataLength,
            Cloudlet senderCloudlet,
            Cloudlet receiverCloudlet) {
        super();
        this.senderVmId = senderVmId;
        this.receiverVmId = receiverVmId;
        this.dataLength = dataLength;
        this.receiverCloudlet = receiverCloudlet;
        this.senderCloudlet = senderCloudlet;
    }

    public double getSendTime() {
        return sendTime;
    }

    public void setSendTime(double sendTime) {
        this.sendTime = sendTime;
    }

    public double getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(double receiveTime) {
        this.receiveTime = receiveTime;
    }

    /**
     * 
     * @return id of the VM sending the packet.
     * This is the VM where the {@link #getSenderCloudlet() sending cloudlet}
     * is running.
     */
    public int getSenderVmId() {
        return senderVmId;
    }

    /**
     * 
     * @return id of the VM that has to receive the packet.
     * This is the VM whwere th {@link #getReceiverCloudlet() receiver cloudlet}
     * is running.
     */
    public int getReceiverVmId() {
        return receiverVmId;
    }

    /**
     * 
     * @return the cloudlet sending the packet.
     */
    public Cloudlet getSenderCloudlet() {
        return senderCloudlet;
    }
    
    /**
     * @return the cloudlet that has to receive the packet.
     */
    public Cloudlet getReceiverCloudlet() {
        return receiverCloudlet;
    }

    /**
     * 
     * @return the length of the data being sent,
     * that represents the size of the packet's payload (in bytes).
     */
    public double getDataLength() {
        return dataLength;
    }
}
