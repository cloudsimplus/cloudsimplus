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
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;

import java.util.Objects;

/**
 * Represents a packet which travels from one {@link Host} to another.
 * Each packet contains: IDs of the sender VM into the source Host and receiver VM into the destination Host which are
 * communicating; the time at which it is sent and received; type and virtual IDs of tasks.
 *
 * <p>Please refer to following publication for more details:
 * <ul>
 *   <li>
 *   <a href="https://doi.org/10.1109/UCC.2011.24">
 *   Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 *   Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 *    International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 *    Press, USA), Melbourne, Australia, December 5-7, 2011.
 *    </a>
 *    </li>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 1.0
 */
public class HostPacket implements NetworkPacket<NetworkHost> {

    /** @see #getVmPacket() */
    private final VmPacket vmPacket;

    /** @see #getSource() */
    private NetworkHost sourceHost;

    /** @see #getDestination() */
    private NetworkHost destinationHost;

    /** @see #getSendTime() */
    private double sendTime;

    /** @see #getReceiveTime() */
    private double receiveTime;

    /**
     * Creates a packet to be sent through the network between two hosts.
     *
     * @param sourceHost host sending the packet
     * @param vmPacket vm packet containing information of sender and receiver Cloudlets and their VMs.
     */
    public HostPacket(final NetworkHost sourceHost, final VmPacket vmPacket) {
        this.vmPacket = Objects.requireNonNull(vmPacket);
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
     * Gets the {@link Host} that this packet is coming from (the sender).
     * @return
     */
    @Override
    public NetworkHost getSource() {
        return sourceHost;
    }

    /**
     * Sets the {@link Host} that this packet is coming from (the sender).
     * @param sourceHost the source Host id to set
     */
    @Override
    public final void setSource(final NetworkHost sourceHost) {
        this.sourceHost = Objects.requireNonNull(sourceHost);
    }

    /**
     * Gets the {@link Host} that the packet is going to.
     * @return
     */
    @Override
    public NetworkHost getDestination() {
        return destinationHost;
    }

    /**
     * Sets the {@link Host} that the packet is going to (the receiver).
     * @param destinationHost the receiver Host id to set
     */
    @Override
    public void setDestination(final NetworkHost destinationHost) {
        this.destinationHost = Objects.requireNonNull(destinationHost);
    }

    @Override
    public double getSendTime() {
        return sendTime;
    }

    @Override
    public void setSendTime(final double sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public double getReceiveTime() {
        return receiveTime;
    }

    @Override
    public void setReceiveTime(final double receiveTime) {
        this.receiveTime = receiveTime;
    }

    /**
     * Gets information about the virtual sender and receiver entities of the packet
     * (the sender and receiver Cloudlet and their respective VMs).
     * @return
     */
    public VmPacket getVmPacket() {
        return vmPacket;
    }

    /**
     * Gets the {@link EdgeSwitch} that the Host where the VM receiving a packet is connected to.
     * @return the Edge Switch connected to the Host where the targeting VM is placed
     */
    public EdgeSwitch getVmEdgeSwitch() {
        return vmPacket.getDestinationHost().getEdgeSwitch();
    }
}
