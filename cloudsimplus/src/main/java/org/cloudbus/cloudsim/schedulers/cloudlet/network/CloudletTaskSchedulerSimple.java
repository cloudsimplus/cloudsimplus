/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet.network;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.network.*;
import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.network.VmPacket;
import org.cloudbus.cloudsim.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A scheduling policy performed by a
 * virtual machine to process {@link CloudletTask}s
 * of a {@link NetworkCloudlet}.
 *
 * <p>It also schedules the network communication between cloudlets,
 * managing the time a cloudlet stays blocked waiting
 * the response of a network package sent to another cloudlet.</p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Plus 1.0
 */
public class CloudletTaskSchedulerSimple implements CloudletTaskScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudletTaskSchedulerSimple.class.getSimpleName());

    /** @see #getVm() */
    private Vm vm;

    /** @see #getVmPacketsToSend() */
    private final List<VmPacket> vmPacketsToSend;

    /**
     * A map of {@link VmPacket}'s received, where each key is the
     * sender VM and each value is the list of packets sent by that VM
     * targeting the VM of this scheduler.
     */
    private final Map<Vm, List<VmPacket>> vmPacketsReceivedMap;

    /**
     * Creates a CloudletTaskSchedulerSimple.
     */
    public CloudletTaskSchedulerSimple() {
        super();
        vmPacketsToSend = new ArrayList<>();
        vmPacketsReceivedMap = new HashMap<>();
    }

    @Override
    public void processCloudletTasks(final Cloudlet cloudlet, final long partialFinishedMI) {
        if (cloudlet.isFinished() || isNotNetworkCloudlet(cloudlet)) {
            return;
        }

        final var netCloudlet = (NetworkCloudlet) cloudlet;
        if (!netCloudlet.isTasksStarted()) {
            scheduleNextTaskIfCurrentIsFinished(netCloudlet);
            return;
        }

        /*
         * @TODO author: manoelcampos It should be used polymorphism to avoid
         *       including these if's for each type of task.
         */
        if (isTimeToUpdateCloudletProcessing(netCloudlet))
            updateExecutionTask(netCloudlet, partialFinishedMI);
        else updateNetworkTasks(netCloudlet);
    }

    private void updateExecutionTask(final NetworkCloudlet cloudlet, final long partialFinishedMI) {
        /*
         * @TODO autor: manoelcampos It has to be checked if the task execution
         *       is considering only one cloudlet PE or all PEs.
         *       Each execution task is supposed to use just one PE.
         */
        final Optional<CloudletExecutionTask> optional = getCloudletCurrentTask(cloudlet);
        optional.ifPresent(task -> {
            task.process(partialFinishedMI);
            scheduleNextTaskIfCurrentIsFinished(cloudlet);
        });
    }

    private void updateNetworkTasks(final NetworkCloudlet cloudlet) {
        //TODO Needs to use polymorphism to avoid these ifs
        cloudlet.getCurrentTask().ifPresent(task -> {
            if (task instanceof CloudletSendTask sendTask)
               addPacketsToBeSentFromVm(cloudlet, sendTask);
            else if (task instanceof CloudletReceiveTask receiveTask)
                receivePackets(cloudlet, receiveTask);
        });
    }

    @Override
    public boolean isTimeToUpdateCloudletProcessing(final Cloudlet cloudlet) {
        Objects.requireNonNull(cloudlet);
        if(cloudlet.isFinished()){
            return false;
        }

        if(isNotNetworkCloudlet(cloudlet)) {
            return true;
        }

        return ((NetworkCloudlet)cloudlet).getCurrentTask().filter(CloudletTask::isExecutionTask).isPresent();
    }

    private boolean isNotNetworkCloudlet(final Cloudlet cloudlet) {
        return !(cloudlet instanceof NetworkCloudlet);
    }

    /**
     * Gets the list of packets to be sent from a given source
     * cloudlet and adds this list to the list of all packets to send
     * from the VM hosting that cloudlet.
     *
     * @param sourceCloudlet cloudlet to get the list of packets to send
     * @param task the network task that will send the packets
     */
    private void addPacketsToBeSentFromVm(final NetworkCloudlet sourceCloudlet, final CloudletSendTask task) {
        LOGGER.trace(
            "{}: {}: {} pkts added to be sent from {} in {}",
            sourceCloudlet.getSimulation().clockStr(), getClass().getSimpleName(),
            task.getPacketsToSend().size(), sourceCloudlet,
            sourceCloudlet.getVm());

        vmPacketsToSend.addAll(task.getPacketsToSend(sourceCloudlet.getSimulation().clock()));
        scheduleNextTaskIfCurrentIsFinished(sourceCloudlet);
    }

    /**
     * Process packets to be received by a given cloudlet and deliver them to it.
     *
     * @param destinationCloudlet a {@link NetworkCloudlet} that is waiting for packets,
     *                                    which is going to be checked if there are packets targeting it.
     * @param task the network task that will receive the packets
     */
    private void receivePackets(final NetworkCloudlet destinationCloudlet, final CloudletReceiveTask task) {
        final List<VmPacket> receivedPkts = getPacketsSentToCloudlet(task);
        // Assumption: packet will not arrive in the same cycle
        receivedPkts.forEach(task::receivePacket);
        receivedPkts.forEach(pkt -> logReceivedPacket(destinationCloudlet, pkt));

        /*Removes the received packets from the list of sent packets of the VM,
        to indicate they were in fact received and have to be removed
        from the list of the sender VM*/
        getListOfPacketsSentFromVm(task.getSourceVm()).removeAll(receivedPkts);

        /*
         * @TODO author: manoelcampos The task has to wait the reception
         *       of the expected packets up to a given timeout.
         *       After that, the task has to stop waiting and fail.
         */
        scheduleNextTaskIfCurrentIsFinished(destinationCloudlet);
    }

    private void logReceivedPacket(final NetworkCloudlet destinationCloudlet, final VmPacket pkt) {
        LOGGER.trace(
            "{}: {}: {} in {} received pkt with {} bytes from {} in {}",
            destinationCloudlet.getSimulation().clockStr(), getClass().getSimpleName(),
            pkt.getReceiverCloudlet(), pkt.getDestination(),
            pkt.getSize(), pkt.getSenderCloudlet(), pkt.getSource());
    }

    /**
     * Gets the current task by casting it to a generic type
     * @param cloudlet the Cloudlet to get its current task by casting it
     * @param <T> the generic type to cast the task to
     * @return the current task after casting it
     */
    private <T extends CloudletTask> Optional<T> getCloudletCurrentTask(final NetworkCloudlet cloudlet) {
        return cloudlet.getCurrentTask().map(task -> (T) task);
    }

    /**
     * Checks if there are packets sent to a given {@link NetworkCloudlet},
     * to be processed by a {@link CloudletReceiveTask}, and returns them to be
     * delivered for that Cloudlet.
     *
     * @param receiveTask the {@link CloudletReceiveTask} that is waiting for packets
     * @return the list of packets targeting the {@link NetworkCloudlet} or an empty list
     *         if there are no packets received that are targeting such a Cloudlet.
     */
    private List<VmPacket> getPacketsSentToCloudlet(final CloudletReceiveTask receiveTask) {
        final List<VmPacket> pktsFromExpectedSenderVm = getListOfPacketsSentFromVm(receiveTask.getSourceVm());

        return pktsFromExpectedSenderVm
                .stream()
                .filter(pkt -> pkt.getDestination().equals(receiveTask.getCloudlet().getVm()))
                .filter(pkt -> pkt.getReceiverCloudlet().equals(receiveTask.getCloudlet()))
                .collect(Collectors.toList());
    }

    /**
     * Schedules the execution of the next task of a given cloudlet.
     */
    private void scheduleNextTaskIfCurrentIsFinished(final NetworkCloudlet cloudlet) {
        if(!cloudlet.startNextTaskIfCurrentIsFinished(cloudlet.getSimulation().clock())){
            return;
        }

        final var dc = getVm().getHost().getDatacenter();
        dc.schedule(dc, dc.getSimulation().getMinTimeBetweenEvents(), CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING);
    }

    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public void setVm(final Vm vm) {
        this.vm = Objects.requireNonNull(vm);
    }

    @Override
    public void clearVmPacketsToSend() {
        vmPacketsToSend.clear();
    }

    @Override
    public List<VmPacket> getVmPacketsToSend() {
        return Collections.unmodifiableList(vmPacketsToSend);
    }

    /**
     * Gets the list of packets received that were sent from a given VM,
     * targeting the VM of this scheduler.
     *
     * @param sourceVm VM to get the list of packets sent from
     * @return the list of packets sent from the given VM
     */
    private List<VmPacket> getListOfPacketsSentFromVm(final Vm sourceVm){
        return vmPacketsReceivedMap.getOrDefault(sourceVm, new ArrayList<>());
    }

    @Override
    public boolean addPacketToListOfPacketsSentFromVm(final VmPacket pkt){
        final Vm vm = pkt.getSource();
        return vmPacketsReceivedMap.compute(vm, (k, v) -> v == null ? new ArrayList<>() : v).add(pkt);
    }
}
