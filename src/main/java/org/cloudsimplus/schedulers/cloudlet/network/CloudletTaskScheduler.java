/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.cloudlet.network;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.network.CloudletExecutionTask;
import org.cloudsimplus.cloudlets.network.CloudletTask;
import org.cloudsimplus.cloudlets.network.NetworkCloudlet;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.network.NetworkVm;

import java.util.List;

/// Provides the features to enable a [CloudletScheduler] to
/// process internal [CloudletTask]s such as:
///
/// - processing of [CloudletExecutionTask]s;
/// - sending [VmPacket]s from the [Vm] of the scheduler to other ones;
/// - or receiving [VmPacket]s sent from other VMs to that [Vm].
///
/// The packet dispatching is performed by processing [CloudletTask]s
/// inside a [NetworkCloudlet].
///
/// A researcher creating its own simulations using CloudSim Plus, usually doesn't have to
/// care about this class. Even creating network-enabled simulations, using objects
/// such as [NetworkDatacenter], [NetworkHost], [NetworkVm] and [NetworkCloudlet],
/// the [NetworkHost] will automatically create instances of
/// the current interface and attach each one to the [CloudletScheduler]
/// that every Vm is using, no matter what kind of scheduler it is.
///
/// @author Saurabh Kumar Garg
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public interface CloudletTaskScheduler {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link CloudletTaskScheduler} objects.
     */
    CloudletTaskScheduler NULL = new CloudletTaskSchedulerNull();

    /**
     * @return the Vm that the CloudletTaskScheduler will receive packets from or send packets to.
     */
    Vm getVm();

    /**
     * Sets the Vm that the CloudletTaskScheduler will receive packets from or send packets to.
     * It is not required to manually set a Vm for the CloudletTaskScheduler,
     * since the {@link NetworkHost} does it when it creates a Vm.
     *
     * @param vm the Vm to set
     */
    CloudletTaskScheduler setVm(Vm vm);

    /**
     * Clears the list of {@link VmPacket}'s to send from the Vm of this scheduler to other VMs.
     */
    void clearVmPacketsToSend();

    /**
     * @return <b>read-only</b> list of {@link VmPacket}'s to send from the Vm of this scheduler to other VMs.
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
