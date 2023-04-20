/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.vms.network;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.network.NetworkCloudlet;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;

/**
 * A Vm supporting simulation of network communication.
 * It executes actions related to management of packets
 * (sent and received).
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
 * </li>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 3.0
 */
@Accessors @Getter @Setter
public class NetworkVm extends VmSimple {
    public static final NetworkVm NULL = new NetworkVm();

    /**
     * List of {@link NetworkCloudlet} of the VM.
     */
    @NonNull
    private List<NetworkCloudlet> cloudletList;

    /**
     * List of packets received by the VM.
     */
    @NonNull
    private List<VmPacket> receivedPacketList;

    /** Indicates if the VM is free or not. */
    private boolean free;

    /**
     * Creates a NetworkVm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth
     * and 1024 MEGA of Storage Size.
     *
     * <p>To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is not created inside a Host}, such values can be changed freely.</p>
     *
     * @param id unique ID of the VM
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param pesNumber amount of {@link Pe} (CPU cores)
     */
    public NetworkVm(final int id, final long mipsCapacity, final int pesNumber) {
        super(id, mipsCapacity, pesNumber);
        cloudletList = new ArrayList<>();
    }

    /**
     * Creates a VM with no resources.
     */
    private NetworkVm(){
        this(-1, 0, 1);
    }

    /**
     * Creates a NetworkVm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth
     * and 1024 MEGA of Storage Size.
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is not created inside a Host}, such values can be changed freely.
     *
     * <p>It is not defined an id for the Vm. The id is defined when the Vm is submitted to
     * a {@link DatacenterBroker}.</p>
     *
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param pesNumber amount of {@link Pe} (CPU cores)
     */
    public NetworkVm(final long mipsCapacity, final int pesNumber) {
        super(mipsCapacity, pesNumber);
        cloudletList = new ArrayList<>();
    }

    @Override
    public NetworkHost getHost() {
        return (NetworkHost)super.getHost();
    }

    @Override
    public Vm setHost(final Host host) {
        if(host == Host.NULL)
            return super.setHost(NetworkHost.NULL);

        if(host instanceof NetworkHost)
            return super.setHost(host);

        throw new IllegalArgumentException("NetworkVm can only be run into a NetworkHost");
    }
}
