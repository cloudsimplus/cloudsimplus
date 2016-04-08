/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

/**
 * HostPacket represents the packet that travels through the virtual network
 * within a Host. It contains information about cloudlets which are
 * communicating.
 *
 * <br/>Please refer to following publication for more details:<br/>
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
 * @since CloudSim Toolkit 1.0
 * @todo Attributes should be private
 */
public class HostPacket {

    /**
     * Id of the sender VM.
     */
    int senderVmId;

    /**
     * Id of the receiver VM.
     */
    int receiverVmId;

    /**
     * Id of the sender cloudlet.
     */
    int senderCloudletId;

    /**
     * Id of the receiver cloudlet.
     */
    int receiverCloudletId;

    /**
     * The length of the data being sent (in bytes).
     */
    double dataLength;

    /**
     * The time the packet was sent.
     */
    double sendTime;

    /**
     * The time the packet was received.
     */
    double receiveTime;

    public HostPacket(
            int senderVmId,
            int receiverVmId,
            double dataLength,
            double sendTime,
            double receiveTime,
            int senderCloudletId,
            int receiverCloudletId) {
        super();
        this.senderVmId = senderVmId;
        this.receiverVmId = receiverVmId;
        this.dataLength = dataLength;
        this.sendTime = sendTime;
        this.receiveTime = receiveTime;
        this.receiverCloudletId = receiverCloudletId;
        this.senderCloudletId = senderCloudletId;
    }
}
