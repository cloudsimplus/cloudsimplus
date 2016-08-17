/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

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
     * Id of the sender VM.
     */
    private final int senderVmId;

    /**
     * Id of the receiver VM.
     */
    private final int receiverVmId;

    /**
     * Id of the sender cloudlet.
     */
    private final int senderCloudletId;

    /**
     * Id of the receiver cloudlet.
     */
    private final int receiverCloudletId;

    /**
     * The length of the data being sent (in bytes).
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
     * @param senderVmId The id of the VM sending the packet
     * @param receiverVmId The id of the VM that has to receive the packet
     * @param dataLength The data length of the packet
     * @param senderCloudletId
     * @param receiverCloudletId 
     */
    public HostPacket(
            int senderVmId,
            int receiverVmId,
            double dataLength,
            int senderCloudletId,
            int receiverCloudletId) {
        super();
        this.senderVmId = senderVmId;
        this.receiverVmId = receiverVmId;
        this.dataLength = dataLength;
        this.receiverCloudletId = receiverCloudletId;
        this.senderCloudletId = senderCloudletId;
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

    public int getSenderVmId() {
        return senderVmId;
    }

    public int getReceiverVmId() {
        return receiverVmId;
    }

    public int getSenderCloudletId() {
        return senderCloudletId;
    }

    public int getReceiverCloudletId() {
        return receiverCloudletId;
    }

    public double getDataLength() {
        return dataLength;
    }
}
