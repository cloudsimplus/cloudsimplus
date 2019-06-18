/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet.network;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.network.CloudletExecutionTask;
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
 * Provides the features to enable a {@link CloudletScheduler} to
 * process internal {@link CloudletTask}s such as:
 * <ul>
 *  <li>processing of {@link CloudletExecutionTask}s;</li>
 *  <li>sending {@link VmPacket}s from the {@link Vm} of the scheduler to other ones;</li>
 *  <li>or receiving {@link VmPacket}s sent from other VMs to that {@link Vm}.</li>
 * </ul>
 *
 * The packet dispatching is performed by processing {@link CloudletTask}s
 * inside a {@link NetworkCloudlet}.
 *
 * <p>A researcher creating its own simulations using CloudSim Plus usually doesn't have to
 * care about this class, since even creating network-enabled simulations using objects
 * such as {@link NetworkDatacenter}, {@link NetworkHost}, {@link NetworkVm}
 * and {@link NetworkCloudlet}, the {@link NetworkHost} will automatically create instances of
 * the current interface and attach each one to the {@link CloudletScheduler}
 * that every Vm is using, doesn't matter what kind of scheduler it is.</p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface CloudletTaskScheduler {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link CloudletTaskScheduler}
     * objects.
     */
    CloudletTaskScheduler NULL = new CloudletTaskSchedulerNull();

    /**
     * Gets the Vm that the CloudletTaskScheduler will sent packets from or receive packets to.
     * @return
     */
    Vm getVm();

    /**
     * Sets the Vm that the CloudletTaskScheduler will sent packets from or receive packets to.
     * It is not required to manually set a Vm for the CloudletTaskScheduler,
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
     * Process Cloudlet's tasks, such as tasks to send packets from or received by a Cloudlet inside a VM.
     * @param cloudlet the Cloudlet to process packets
     * @param partialFinishedMI the partial executed length of this Cloudlet (in MI)
     */
    void processCloudletTasks(Cloudlet cloudlet, long partialFinishedMI);

    /**
     * Checks if it's time to update the execution of a given Cloudlet.
     * If the Cloudlet is waiting for packets to be sent or received,
     * then it isn't time to update its processing.
     *
     * @param cloudlet the Cloudlet to check if it is time to update its execution
     * @return true if it's time to update Cloudlet execution, false otherwise.
     */
    boolean isTimeToUpdateCloudletProcessing(Cloudlet cloudlet);
}
