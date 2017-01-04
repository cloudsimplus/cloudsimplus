/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.network.VmPacket;
import org.cloudbus.cloudsim.util.Log;

import org.cloudbus.cloudsim.cloudlets.network.CloudletExecutionTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletReceiveTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletSendTask;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * NetworkCloudletSchedulerSpaceShared implements a policy of scheduling performed by a
 * virtual machine to run its {@link NetworkCloudlet}'s.
 * It also schedules the network communication among the cloudlets,
 * managing the time a cloudlet stays blocked waiting
 * the response of a network package sent to another cloudlet.
 * It consider that there will be only one cloudlet per VM. Other cloudlets will be in a waiting list.
 * We consider that file transfer from cloudlets waiting happens before cloudlet
 * execution. I.e., even though cloudlets must wait for CPU, data transfer
 * happens as soon as cloudlets are submitted.
 *
 * Each VM has to have its own instance of a CloudletScheduler.
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 * @todo @author manoelcampos The NetworkCloudletTimeSharedScheduler was delete because
 * it was just dupliating code from the super class. This scheduler in fact is dealing
 * with task scheduling but it not define that such tasks will be execute in a space-shared
 * manner. Just the Cloudlets are executed in this way. Thus, it doesn't make sense
 * to have these Cloudlets' tasks scheduling inside a specific space-shared scheduler.
 * And the tasks will just be processed if this specific scheduler is used,
 * what doesn't make sense and will just confuse users.
 * The tasks processing inside the {@link #updateCloudletProcessing(CloudletExecutionInfo, double)} maybe should be placed
 * elsewhere, such as inside the NetworkVm.updateVmProcessing() method.
 *
 * @since CloudSim Toolkit 3.0
 *
 */
public class NetworkCloudletSpaceSharedScheduler extends CloudletSchedulerSpaceShared {
    /**
     * @see #getHostPacketsToSendMap()
     */
    private final Map<Vm, List<VmPacket>> hostPacketsToSendMap;

    /**
     * A map of {@link VmPacket}'s received, where each key is the
     * sender VM and each value is the list of packets sent by that VM.
     */
    private final Map<Vm, List<VmPacket>> hostPacketsReceivedMap;

    /**
     * Creates a new CloudletSchedulerSpaceShared object. This method must be
     * invoked before starting the actual simulation.
     *
     * @pre $none
     * @post $none
     */
    public NetworkCloudletSpaceSharedScheduler() {
        super();
        hostPacketsToSendMap = new HashMap<>();
        hostPacketsReceivedMap = new HashMap<>();
    }

    @Override
    public void updateCloudletProcessing(CloudletExecutionInfo rcl, double currentTime) {
        NetworkCloudlet netcl = (NetworkCloudlet) rcl.getCloudlet();

        if (netcl.isFinished()) {
            return;
        }

        /**
         * @todo @author manoelcampos It should be used polymorphism to avoid
         * including these if's for each type of task.
         */
        if ((netcl.getCurrentTaskNum() == -1)) {
            scheduleNextTaskIfCurrentIsFinished(netcl);
            return;
        }

        if (netcl.getCurrentTask() instanceof CloudletExecutionTask) {
            super.updateCloudletProcessing(rcl, currentTime);
            updateExecutionTask(rcl, currentTime);
        }
        else if (netcl.getCurrentTask() instanceof CloudletSendTask) {
            addPacketsToBeSentFromVm(netcl);
        }
        else if (netcl.getCurrentTask() instanceof CloudletReceiveTask) {
            receivePackets(netcl);
        }

        setPreviousTime(currentTime);
    }

    /**
     * Gets the list of packets to be sent from a given source
     * cloudlet and adds this list to the list of all packets to send
     * from the VM hosting that cloudlet.
     *
     * @param sourceCloudlet cloudlet to get the list of packets to send
     */
    protected void addPacketsToBeSentFromVm(NetworkCloudlet sourceCloudlet) {
        CloudletSendTask dataTask = (CloudletSendTask)sourceCloudlet.getCurrentTask();
        final List<VmPacket> packetsToSendFromVmOfCloudlet =
                getListOfPacketsToBeSentFromVm(sourceCloudlet.getVm());

        Log.println(Log.Level.DEBUG, getClass(), sourceCloudlet.getSimulation().clock(),
                "%d pkts added to be sent from cloudlet %d in VM %d",
                dataTask.getPacketsToSend().size(), sourceCloudlet.getId(),
                sourceCloudlet.getVm().getId());

        packetsToSendFromVmOfCloudlet.addAll(dataTask.getPacketsToSend(sourceCloudlet.getSimulation().clock()));

        hostPacketsToSendMap.put(sourceCloudlet.getVm(), packetsToSendFromVmOfCloudlet);
        scheduleNextTaskIfCurrentIsFinished(sourceCloudlet);
    }

    /**
     * Gets the list of packets to be sent from a given VM.
     * @param sourceVm the source VM where the list of packets to send will
     * be obtained
     * @return
     */
    protected List<VmPacket> getListOfPacketsToBeSentFromVm(Vm sourceVm) {
        hostPacketsToSendMap.putIfAbsent(sourceVm, new ArrayList<>());
        return hostPacketsToSendMap.get(sourceVm);
    }

    /**
     * Check for packets to be received by a given cloudlet
     * and deliver them to it.
     *
     * @param sourceCloudlet cloudlet to check if there are packets to be received from.
     */
    protected void receivePackets(NetworkCloudlet sourceCloudlet) {
        CloudletReceiveTask task = (CloudletReceiveTask)sourceCloudlet.getCurrentTask();

        final List<VmPacket> receivedPkts = getPacketsSentToGivenTask(task);
        // Asumption: packet will not arrive in the same cycle
        receivedPkts.forEach(task::receivePacket);
        receivedPkts.forEach(pkt ->
            Log.println(
                Log.Level.DEBUG, getClass(), sourceCloudlet.getSimulation().clock(),
                "Cloudlet %d in VM %d received pkt with %d bytes from Cloudlet %d in VM %d",
                pkt.getReceiverCloudlet().getId(),
                pkt.getDestination().getId(),
                pkt.getSize(),
                pkt.getSenderCloudlet().getId(),
                pkt.getSource().getId())
        );


        /*Removes the received packets from the list of sent packets of the VM,
        to indicate they were in fact received and have to be removed
        from the list of the sender VM*/
        getListOfPacketsSentFromVm(task.getSourceVm()).removeAll(receivedPkts);

        /**
         * @todo @author manoelcampos The task has to wait the reception
         * of the expected packets up to a given timeout.
         * After that, the task has to stop waiting and fail.
         */
        scheduleNextTaskIfCurrentIsFinished(sourceCloudlet);
    }

    /**
     * Gets the list of packets sent to a given CloudletReceiveTask.
     * @param destinationTask The task that is waiting for packets
     * @return
     */
    protected List<VmPacket> getPacketsSentToGivenTask(CloudletReceiveTask destinationTask) {
        List<VmPacket> packetsFromExpectedSenderVm =
                getListOfPacketsSentFromVm(destinationTask.getSourceVm());

        return packetsFromExpectedSenderVm
                .stream()
                .filter(pkt -> pkt.getDestination().getId() == destinationTask.getCloudlet().getVm().getId())
                .collect(Collectors.toList());
    }

    protected void updateExecutionTask(CloudletExecutionInfo rcl, double currentTime) {
        NetworkCloudlet netcl = (NetworkCloudlet)rcl.getCloudlet();
        if(!(netcl.getCurrentTask() instanceof CloudletExecutionTask))
            throw new RuntimeException(
                "This method has to be called only when the current task of the NetworkCloudlet, inside the given ResCloudlet, is a CloudletExecutionTask");

        /**
         * @todo @author manoelcampos The method updates the execution
         * length of the task, considering the NetworkCloudlet
         * has only 1 execution task.
         *
         * @todo @author manoelcampos It has to be checked if the task execution
         * is considering only one cloudlet PE our all PEs.
         * Each execution task is supposed to use just one PE.
         */
        CloudletExecutionTask task = (CloudletExecutionTask)netcl.getCurrentTask();
        task.process(netcl.getFinishedLengthSoFar());

        scheduleNextTaskIfCurrentIsFinished(netcl);
    }

    /**
     * Schedules the execution of the next task of a given cloudlet.
     */
    private void scheduleNextTaskIfCurrentIsFinished(NetworkCloudlet cloudlet) {
        if(!cloudlet.startNextTaskIfCurrentIsFinished(cloudlet.getSimulation().clock())){
            return;
        }

        Datacenter dc = getVm().getHost().getDatacenter();
        dc.schedule(dc.getId(), dc.getSimulation().getMinTimeBetweenEvents(), CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
    }

    /**
     * Gets the map of {@link VmPacket}'s to send, where each key is the sending VM
     * and each value is the list of packets to send.
     *
     * @return a ready-only map of {@link VmPacket}'s to send
     */
    public Map<Vm, List<VmPacket>> getHostPacketsToSendMap() {
        return Collections.unmodifiableMap(hostPacketsToSendMap);
    }

    /**
     * Gets the list of packets received that were sent from a given VM.
     *
     * @param sourceVm VM to get the list of packets sent from
     * @return the list of packets sent from the given VM
     */
    public List<VmPacket> getListOfPacketsSentFromVm(Vm sourceVm){
        hostPacketsReceivedMap.putIfAbsent(sourceVm, new ArrayList<>());
        return hostPacketsReceivedMap.get(sourceVm);
    }

    /**
     * Adds a packet to the list of packets sent by a given VM.
     * The source VM is got from the packet.
     *
     * @param pkt packet to be added to the list
     * @return true if the packet was added, false otherwise
     */
    public boolean addPacketToListOfPacketsSentFromVm(VmPacket pkt){
        return getListOfPacketsSentFromVm(pkt.getSource()).add(pkt);
    }


}
