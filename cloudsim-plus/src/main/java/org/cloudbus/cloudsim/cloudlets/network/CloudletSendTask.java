/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.network.VmPacket;

/**
 * Represents a task executed by a {@link NetworkCloudlet} that sends data to a
 * {@link CloudletReceiveTask}.
 *
 * <p>
 * Please refer to following publication for more details:
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
 *
 */
public class CloudletSendTask extends CloudletTask {
    private final List<VmPacket> packetsToSend;

    /**
     * Creates a new task.
     *
     * @param id task id
     */
    public CloudletSendTask(int id) {
        super(id);
        this.packetsToSend = new ArrayList<>();
    }

    /**
     * Creates and add a packet to the list of packets to be sent to a
     * {@link Cloudlet} that is inside a specific VM.
     *
     * @param destinationCloudlet destination cloudlet to send packets to
     * @param dataLength the number of data bytes of the packet to create
     * @return the created packet
     * @throws RuntimeException when a NetworkCloudlet was not assigned to the Task
     * @throws IllegalArgumentException when the source or destination Cloudlet doesn't have an assigned VM
     */
    public VmPacket addPacket(Cloudlet destinationCloudlet, long dataLength) {
        if(getCloudlet() == null) {
            throw new IllegalStateException("You must assign a NetworkCloudlet to this Task before adding packets.");
        }
        if(!getCloudlet().isBindToVm()) {
            throw new IllegalStateException("The source Cloudlet has to have an assigned VM.");
        }
        if(!destinationCloudlet.isBindToVm()) {
            throw new IllegalStateException("The destination Cloudlet has to have an assigned VM.");
        }

        final VmPacket packet = new VmPacket(
                getCloudlet().getVm(), destinationCloudlet.getVm(),
                dataLength, getCloudlet(), destinationCloudlet);
        packetsToSend.add(packet);
        return packet;
    }

    /**
     * @return a read-only list of packets to send
     */
    public List<VmPacket> getPacketsToSend() {
        return Collections.unmodifiableList(packetsToSend);
    }

    /**
     * Gets the list of packets to send,
     * updating the send time to the given time
     * and clearing the list of packets, marking the
     * task as finished.
     *
     * @param sendTime the send time to update all packets in the list
     * @return the packet list with the send time
     * updated to the given time
     */
    public List<VmPacket> getPacketsToSend(double sendTime) {
        packetsToSend.forEach(pkt ->  pkt.setSendTime(sendTime));

        if(isFinished())
            packetsToSend.clear();
        else setFinished(true);

        return packetsToSend;
    }

}
