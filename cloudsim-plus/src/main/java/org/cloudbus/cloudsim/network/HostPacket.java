/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;

/**
 * Represents a packet which travels from one {@link Host} to another.
 * Each packet contains: IDs of the sender VM into the source Host and receiver VM into the destination Host which are
 * communicating; the time at which it is sent and received; type and virtual IDs of tasks.
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
public class HostPacket implements NetworkPacket<NetworkHost> {

    /**
     * Information about the virtual sender and receiver entities of the packet
     * (the sender and receiver Cloudlet and their respective VMs).
     */
    private final VmPacket vmPacket;

    /**
     * Id of the sender host.
     */
    private NetworkHost senderHost;

    /**
     * Id of the receiver host.
     */
    private NetworkHost receiverHost;

    /**
     * @see #getSendTime()
     */
    private double sendTime;

    /**
     * @see #getReceiveTime()
     */
    private double receiveTime;

    /**
     * Creates a new packet to be sent through the network between two hosts.
     *
     * @param senderHost The id of the host sending the packet
     * @param vmPacket The vm packet containing information of sender and receiver Cloudlets and their VMs.
     */
    public HostPacket(NetworkHost senderHost, VmPacket vmPacket) {
        this.vmPacket = vmPacket;
        this.sendTime = vmPacket.getSendTime();
        this.senderHost = senderHost;
    }

    @Override
    public long getSize() {
        return vmPacket.getSize();
    }

    /**
     * Gets the ID of the {@link Host} that this packet is coming from (the sender).
     * @return
     */
    @Override
    public NetworkHost getSource() {
        return senderHost;
    }

    /**
     * Sets the ID of the {@link Host} that this packet is coming from (the sender).
     * @param senderHost the source Host id to set
     */
    @Override
    public void setSource(NetworkHost senderHost) {
        this.senderHost = senderHost;
    }

    /**
     * Gets the ID of the {@link Host} that the packet is going to.
     * @return
     */
    @Override
    public NetworkHost getDestination() {
        return receiverHost;
    }

    /**
     * Sets the ID of the {@link Host} that the packet is going to.
     * @param receiverHost the receiver Host id to set
     */
    @Override
    public void setDestination(NetworkHost receiverHost) {
        this.receiverHost = receiverHost;
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

    public VmPacket getVmPacket() {
        return vmPacket;
    }
}
