/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.network.VmPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a task executed by a {@link NetworkCloudlet} that sends data to a
 * {@link CloudletReceiveTask}.
 *
 * <p>
 * Please refer to the following publication for more details:
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
 *
 */
public non-sealed class CloudletSendTask extends CloudletTask {
    private final List<VmPacket> packetsToSend;

    /**
     * Creates a new task.
     *
     * @param id id to assign to the task
     */
    public CloudletSendTask(final int id) {
        super(id);
        this.packetsToSend = new ArrayList<>();
    }

    /**
     * Creates and adds a packet to the list of packets to be sent to a
     * {@link Cloudlet} that is inside a specific VM.
     *
     * @param destinationCloudlet destination cloudlet to send packets to
     * @param bytes the number of data bytes of the packet to create
     * @return the created packet
     * @throws RuntimeException when a NetworkCloudlet was not assigned to the Task
     * @throws IllegalArgumentException when the source or destination Cloudlet doesn't have an assigned VM
     */
    public VmPacket addPacket(final NetworkCloudlet destinationCloudlet, final long bytes) {
        Objects.requireNonNull(getCloudlet(), "You must assign a NetworkCloudlet to this Task before adding packets.");
        if(!getCloudlet().isBoundToVm()) {
            throw new IllegalStateException("The source Cloudlet has to have an assigned VM.");
        }
        if(!destinationCloudlet.isBoundToVm()) {
            throw new IllegalStateException("The destination Cloudlet has to have an assigned VM.");
        }

        final var packet = new VmPacket(
                getCloudlet().getVm(), destinationCloudlet.getVm(),
                bytes, getCloudlet(), destinationCloudlet);
        packetsToSend.add(packet);
        return packet;
    }

    /**
     * @return a <b>read-only</b> list of packets to send
     */
    public List<VmPacket> getPacketsToSend() {
        return Collections.unmodifiableList(packetsToSend);
    }

    /**
     * Gets the list of packets to send,
     * updating packets' send time to the given time
     * and clearing the list of packets, marking the
     * task as finished.
     *
     * @param sendTime packets' send time to set (in seconds)
     * @return the packet list with their send time updated to the given time
     */
    public List<VmPacket> getPacketsToSend(final double sendTime) {
        packetsToSend.forEach(pkt ->  pkt.setSendTime(sendTime));

        if(isFinished())
            packetsToSend.clear();
        else setFinished(true);

        return packetsToSend;
    }
}
