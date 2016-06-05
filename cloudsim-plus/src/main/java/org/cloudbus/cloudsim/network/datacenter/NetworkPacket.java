/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

/**
 * NewtorkPacket represents the packet which travel from one server to another.
 * Each packet contains IDs of the senderVmId and receiver VM which are
 * communicating, time at which it is sent and received, type and virtual IDs of
 * tasks.
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
public class NetworkPacket {

    /**
     * Information about the virtual sender and receiver entities of the packet
     * (the sender and receiver Cloudlet and their respective VMs).
     */
    HostPacket pkt;

    /**
     * Id of the sender host.
     */
    int senderHostId;

    /**
     * Id of the receiver host.
     */
    int receiverHostId;

    /**
     * Id of the senderVmId VM.
     *
     * @todo Isn't this data at {@link #pkt}?
     */
    int senderVmId;

    /**
     * Id of the receiver VM.
     *
     * @todo Isn't this data at {@link #pkt}?
     */
    int receiverVmId;

    /**
     * Id of the sender cloudlet.
     *
     * @todo This field is not needed, since its value is being get from a
     * {@link HostPacket} instance at {@link NetworkHost#sendpackets()}. So,
     * such a data can be got form the {@link #pkt} attribute.
     */
    int senderCloudletId;

    /**
     * Time when the packet was sent.
     */
    double sendTime;

    /**
     * Time when the packet was received.
     */
    double receiveTime;

    public NetworkPacket(int senderHostId, HostPacket pkt, int senderVmId, int senderCloudletId) {
        this.pkt = pkt;
        this.senderVmId = senderVmId;
        this.senderCloudletId = senderCloudletId;
        this.senderHostId = senderHostId;

        this.sendTime = pkt.sendTime;
        this.receiverVmId = pkt.receiverVmId;
    }
}
