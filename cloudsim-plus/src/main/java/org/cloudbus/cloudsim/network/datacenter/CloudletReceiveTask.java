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
 * Represents a task executed by a {@link NetworkCloudlet} that 
 * receives data from a {@link CloudletSendTask}.
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
public class CloudletReceiveTask extends CloudletTask {

    private final List<HostPacket> packetsReceived;

    /**
     * @see #getSourceVmId() 
     */
    private final int sourceVmId;

    /**
     * Creates a new task.
     *
     * @param id task id
     * @param memory memory used by the task
     * @param sourceVm the Vm where it is expected to receive packets from
     * @param networkCloudlet the NetworkCloudlet that the task belongs to
     */
    public CloudletReceiveTask(int id, long memory, 
            int sourceVm, NetworkCloudlet networkCloudlet) {
        super(id, memory, networkCloudlet);
        this.packetsReceived = new ArrayList<>();
        this.sourceVmId = sourceVm;
    }
    
    /**
     * Creates a new task without assigning it to a {@link NetworkCloudlet}
     * (that has to be assigned further).    
     *
     * @param id task id
     * @param memory memory used by the task
     * @param sourceVm the Vm where it is expected to receive packets from
     * @see #setNetworkCloudlet(org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet) 
     */
    public CloudletReceiveTask(int id, long memory, int sourceVm) {
        this(id, memory, sourceVm, null);
    }

    /**
     * Receives a packet sent from a {@link CloudletSendTask}
     * and add it the the received packet list.
     *
     * @param packet the packet received
     */
    public void receivePacket(HostPacket packet) {
        this.packetsReceived.add(packet);
    }

    /**
     * Gets the list of packet received.
     * @return the received packet or null if none packet was received yet
     */
    public List<HostPacket> getPacketsReceived() {
        return packetsReceived;
    }

    /**
     * Gets the Vm where it is expected to receive packets from.
     * @return 
     */
    public int getSourceVmId() {
        return sourceVmId;
    }

}
