/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet.network;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.network.CloudletTask;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.VmPacket;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.network.NetworkVm;

import java.util.List;

/**
 * Provides the functionalities to enable a {@link CloudletScheduler} to
 * send {@link VmPacket}s from the {@link Vm} of the scheduler to other ones or
 * to receive {@link VmPacket}s sent from other VMs to that {@link Vm}.
 * The packet dispatching is performed by processing {@link CloudletTask}s
 * inside a {@link NetworkCloudlet}.
 *
 * <p>A researcher creating its own simulations using CloudSim Plus usually doesn't have to
 * care about this class, since even creating network-enabled simulations using objects
 * such as {@link NetworkDatacenter}, {@link NetworkHost}, {@link NetworkVm}
 * and {@link NetworkCloudlet}, the {@link NetworkHost} will automatically create and instance of
 * the current interface and attach them to the {@link CloudletScheduler}
 * that every Vm is using, doesn't matter what kind of scheduler it is.</p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface PacketScheduler {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link PacketScheduler}
     * objects.
     */
    PacketScheduler NULL = new PacketSchedulerNull();

    /**
     * Gets the Vm that the PacketScheduler will sent packets from or receive packets to.
     * @return
     */
    Vm getVm();

    /**
     * Sets the Vm that the PacketScheduler will sent packets from or receive packets to.
     * It is not required to manually set a Vm for the PacketScheduler,
     * since the {@link NetworkHost} does it when it creates a Vm.
     *
     * @param vm the Vm to set
     */
    void setVm(Vm vm);

    /**
     * Clears the list of {@link VmPacket}'s to send from the Vm of this scheduler
     * to other VMs.
     *
     */
    void clearVmPacketsToSend();

    /**
     * Gets a <b>read-only</b> list of {@link VmPacket}'s to send from the Vm of this scheduler
     * to other VMs.
     *
     * @return a  <b>read-only</b> {@link VmPacket} list
     *
     */
    List<VmPacket> getVmPacketsToSend();

    /**
     * Adds a packet to the list of packets sent by a given VM,
     * targeting the VM of this scheduler.
     * The source VM is got from the packet.
     *
     * @param pkt packet to be added to the list
     * @return true if the packet was added, false otherwise
     */
    boolean addPacketToListOfPacketsSentFromVm(VmPacket pkt);

    /**
     * Process the packets to be sent from or received by a Cloudlet inside the vm.
     * @param cloudlet the Cloudlet to process packets
     * @param currentTime current simulation time
     */
    void processCloudletPackets(Cloudlet cloudlet, double currentTime);

    /**
     * Checks if is time to update the execution of a given Cloudlet.
     * If the Cloudlet is waiting for packets to be sent or received,
     * then it is not time updated its processing.
     *
     * @param cloudlet the Cloudlet to check if it is time to update its execution
     * @return true if its timie to update Cloudlet execution, false otherwise.
     */
    boolean isTimeToUpdateCloudletProcessing(Cloudlet cloudlet);
}
