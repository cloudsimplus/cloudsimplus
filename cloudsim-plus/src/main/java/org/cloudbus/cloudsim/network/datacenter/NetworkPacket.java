/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

/**
 * NewtorkPacket represents a packet which travels from one server to another.
 * Each packet contains IDs of the sender VM and receiver VM which are
 * communicating, time at which it is sent and received, type and virtual IDs of
 * tasks.
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
public class NetworkPacket {

    /**
     * Information about the virtual sender and receiver entities of the packet
     * (the sender and receiver Cloudlet and their respective VMs).
     */
    private final HostPacket hostPacket;

    /**
     * Id of the sender host.
     */
    private int senderHostId;

    /**
     * Id of the receiver host.
     */
    private int receiverHostId;

    /**
     * Time when the packet was sent.
     */
    private double sendTime;

    /**
     * Time when the packet was received.
     */
    private double receiveTime;

    /**
     * Creates a new packet to be sent through the network between two hosts.
     * 
     * @param senderHostId The id of the host sending the packet
     * @param pkt The host packet containing information of sender and
     * receiver Cloudlets and their VMs.
     */
    public NetworkPacket(int senderHostId, HostPacket pkt) {
        this.hostPacket = pkt;
        this.sendTime = pkt.getSendTime();
        this.senderHostId = senderHostId;
    }

    public int getSenderHostId() {
        return senderHostId;
    }

    public int getReceiverHostId() {
        return receiverHostId;
    }

    public double getSendTime() {
        return sendTime;
    }

    public double getReceiveTime() {
        return receiveTime;
    }

    public HostPacket getHostPacket() {
        return hostPacket;
    }

    public void setSendTime(double sendTime) {
        this.sendTime = sendTime;
    }

    public void setSenderHostId(int senderHostId) {
        this.senderHostId = senderHostId;
    }

    public void setReceiverHostId(int receiverHostId) {
        this.receiverHostId = receiverHostId;
    }
}
