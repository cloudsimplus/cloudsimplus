/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms.network;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.VmPacket;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

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
public class NetworkVm extends VmSimple {
    public static final NetworkVm NULL = new NetworkVm();

    private List<NetworkCloudlet> cloudletList;
    private List<VmPacket> receivedPacketList;
    private boolean free;
    private double finishTime;

    /**
     * Creates a NetworkVm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
     *
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is not created inside a Host}, such values can be changed freely.
     *
     * @param id unique ID of the VM
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     */
    public NetworkVm(final int id, final long mipsCapacity, final int numberOfPes) {
        super(id, mipsCapacity, numberOfPes);
        cloudletList = new ArrayList<>();
    }

    /**
     * Creates an empty VM
     */
    private NetworkVm(){
        this(-1, 0, 1);
    }

    /**
     * Creates a NetworkVm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is not created inside a Host}, such values can be changed freely.
     *
     * <p>It is not defined an id for the Vm. The id is defined when the Vm is submitted to
     * a {@link DatacenterBroker}.</p>
     *
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     */
    public NetworkVm(final long mipsCapacity, final int numberOfPes) {
        super(mipsCapacity, numberOfPes);
        cloudletList = new ArrayList<>();
    }

    /** Indicates if the VM is free or not. */
    public boolean isFree() {
        return free;
    }

    /**
     * List of {@link NetworkCloudlet} of the VM.
     */
    public List<NetworkCloudlet> getCloudletList() {
        return cloudletList;
    }

    public void setCloudletList(final List<NetworkCloudlet> cloudletList) {
        this.cloudletList = cloudletList;
    }

    /**
     * List of packets received by the VM.
     */
    public List<VmPacket> getReceivedPacketList() {
        return receivedPacketList;
    }

    public void setReceivedPacketList(final List<VmPacket> receivedPacketList) {
        this.receivedPacketList = receivedPacketList;
    }

    public void setFree(final boolean free) {
        this.free = free;
    }

    /**
     * The time when the VM finishes processing its cloudlets.
     */
    public double getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(final double finishTime) {
        this.finishTime = finishTime;
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
