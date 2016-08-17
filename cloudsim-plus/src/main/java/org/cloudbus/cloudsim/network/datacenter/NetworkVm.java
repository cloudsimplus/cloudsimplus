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

import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;

/**
 * NetworkVm class extends {@link VmSimple} to support simulation of networked
 * datacenters. It executes actions related to management of packets (sent and
 * received).
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
 * @since CloudSim Toolkit 3.0
 * @todo Attributes should be private
 */
public class NetworkVm extends VmSimple implements Comparable<Object> {

    /**
     * List of {@link NetworkCloudlet} of the VM.
     */
    public List<NetworkCloudlet> cloudletList;

    /**
     * List of packets received by the VM.
     */
    public List<HostPacket> receivedPacketList;

    /**
     * @todo It doesn't appear to be used.
     */
    public double memory;

    /** Indicates if the VM is free or not. */
    public boolean free;

    /**
     * The time when the VM finished to process its cloudlets.
     */
    public double finishTime;

    public NetworkVm(
            int id,
            int userId,
            double mips,
            int pesNumber,
            int ram,
            long bw,
            long size,
            String vmm,
            CloudletScheduler cloudletScheduler) {
        super(id, userId, mips, pesNumber, ram, bw, size, vmm, cloudletScheduler);

        cloudletList = new ArrayList<>();
    }

    public boolean isFree() {
        return free;
    }

    @Override
    public int compareTo(Object arg0) {
        NetworkVm hs = (NetworkVm) arg0;
        if (hs.finishTime > finishTime) {
            return -1;
        }
        if (hs.finishTime < finishTime) {
            return 1;
        }
        return 0;
    }
}
