/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a task executed by a {@link NetworkCloudlet} that sends data to a
 * {@link CloudletReceiveTask}.
 *
 * <p>
 * Please refer to following publication for more details:
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
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
    private final List<HostPacket> packetsToSend;

    /**
     * Creates a new task.
     *
     * @param id task id
     * @param memory memory used by the task
     * @param networkCloudlet the NetworkCloudlet that the task belongs to
     */
    public CloudletSendTask(int id, long memory, NetworkCloudlet networkCloudlet) {
        super(id, memory, networkCloudlet);
        this.packetsToSend = new ArrayList<>();
    }

    /**
     * Creates and add a packet to the list of packets to be sent.
     *
     * @param destinationVmId the Id of the destination VM to send data
     * @param destinationCloudletId the Id of the destination cloudlet to send packets
     * @param dataLength the number of data bytes of the packet to create
     * @return the created packet
     */
    public HostPacket addPacket(int destinationVmId, int destinationCloudletId, long dataLength) {
        HostPacket packet = new HostPacket(
                getNetworkCloudlet().getVmId(), destinationVmId,
                dataLength, -1, -1,
                getId(), destinationCloudletId);
        packetsToSend.add(packet);
        return packet;
    }

    /**
     * Gets the list of packets to send
     * @return 
     */
    public List<HostPacket> getPacketsToSend() {
        return packetsToSend;
    }

   
    /**
     * Gets the list of packets to send,
     * updating the sent time to the given time.
     * @param sendTime the send time to update all packets in the list
     * @return the packet list with the send time
     * updated to the given time
     */
    public List<HostPacket> getPacketsToSend(double sendTime) {
        packetsToSend.forEach(pkt ->  pkt.sendTime = sendTime);        
        return packetsToSend;
    }

}
