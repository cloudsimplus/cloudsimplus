/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet.network;

import java.util.*;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.network.VmPacket;
import org.cloudbus.cloudsim.util.Log;

import org.cloudbus.cloudsim.cloudlets.network.CloudletExecutionTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletReceiveTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletSendTask;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * Implements a policy of scheduling performed by a
 * virtual machine to process network packets to be sent or received by its
 * {@link NetworkCloudlet}'s.
 *
 * It also schedules the network communication among the cloudlets,
 * managing the time a cloudlet stays blocked waiting
 * the response of a network package sent to another cloudlet.
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Plus 1.0
 */
public class PacketSchedulerSimple implements PacketScheduler {
    /**
     * @see #getVm()
     */
    private Vm vm;

    /**
     * @see #getVmPacketsToSend()
     */
    private final List<VmPacket> vmPacketsToSend;

    /**
     * A map of {@link VmPacket}'s received, where each key is the
     * sender VM and each value is the list of packets sent by that VM
     * targeting the VM of this scheduler.
     */
    private final Map<Vm, List<VmPacket>> vmPacketsReceivedMap;

    /**
     * Creates a PacketSchedulerSimple object.
     *
     * @pre $none
     * @post $none
     */
    public PacketSchedulerSimple() {
        super();
        vmPacketsToSend = new ArrayList<>();
        vmPacketsReceivedMap = new HashMap<>();
    }

    @Override
    public void processCloudletPackets(Cloudlet cloudlet, double currentTime) {
        if (cloudlet.isFinished() || isNotNetworkCloudlet(cloudlet)) {
            return;
        }

        final NetworkCloudlet netcl = (NetworkCloudlet) cloudlet;
        if (!netcl.isTasksStarted()) {
            scheduleNextTaskIfCurrentIsFinished(netcl);
            return;
        }


        /**
         * @todo @author manoelcampos It should be used polymorphism to avoid
         * including these if's for each type of task.
         */
        if (isTimeToUpdateCloudletProcessing(netcl)) {
            updateExecutionTask(netcl);
        }
        else if (netcl.getCurrentTask() instanceof CloudletSendTask) {
            addPacketsToBeSentFromVm(netcl);
        }
        else if (netcl.getCurrentTask() instanceof CloudletReceiveTask) {
            receivePackets(netcl);
        }
    }

    @Override
    public boolean isTimeToUpdateCloudletProcessing(Cloudlet cloudlet) {
        if(Objects.isNull(cloudlet) || cloudlet.isFinished()){
            return false;
        }

        if(isNotNetworkCloudlet(cloudlet)) {
            return true;
        }

        final NetworkCloudlet nc = (NetworkCloudlet)cloudlet;
        return nc.isTasksStarted() && nc.getCurrentTask() instanceof CloudletExecutionTask;
    }

    private boolean isNotNetworkCloudlet(Cloudlet cloudlet) {
        return !(cloudlet instanceof NetworkCloudlet);
    }

    /**
     * Gets the list of packets to be sent from a given source
     * cloudlet and adds this list to the list of all packets to send
     * from the VM hosting that cloudlet.
     *
     * @param sourceCloudlet cloudlet to get the list of packets to send
     */
    private void addPacketsToBeSentFromVm(NetworkCloudlet sourceCloudlet) {
        final CloudletSendTask dataTask = (CloudletSendTask)sourceCloudlet.getCurrentTask();
        Log.println(Log.Level.DEBUG, getClass(), sourceCloudlet.getSimulation().clock(),
                "%d pkts added to be sent from cloudlet %d in VM %d",
                dataTask.getPacketsToSend().size(), sourceCloudlet.getId(),
                sourceCloudlet.getVm().getId());

        vmPacketsToSend.addAll(dataTask.getPacketsToSend(sourceCloudlet.getSimulation().clock()));
        scheduleNextTaskIfCurrentIsFinished(sourceCloudlet);
    }

    /**
     * Check for packets to be received by a given cloudlet
     * and deliver them to it.
     *
     * @param sourceCloudlet cloudlet to check if there are packets to be received from.
     */
    private void receivePackets(NetworkCloudlet sourceCloudlet) {
        final CloudletReceiveTask task = (CloudletReceiveTask)sourceCloudlet.getCurrentTask();
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
    private List<VmPacket> getPacketsSentToGivenTask(CloudletReceiveTask destinationTask) {
        final List<VmPacket> pktsFromExpectedSenderVm =
                getListOfPacketsSentFromVm(destinationTask.getSourceVm());

        return pktsFromExpectedSenderVm
                .stream()
                .filter(pkt -> pkt.getDestination().getId() == destinationTask.getCloudlet().getVm().getId())
                .collect(Collectors.toList());
    }

    private void updateExecutionTask(NetworkCloudlet cloudlet) {
        /**
         * @todo @author manoelcampos It has to be checked if the task execution
         * is considering only one cloudlet PE our all PEs.
         * Each execution task is supposed to use just one PE.
         */
        final CloudletExecutionTask task = (CloudletExecutionTask)cloudlet.getCurrentTask();
        task.process(cloudlet.getFinishedLengthSoFar());

        scheduleNextTaskIfCurrentIsFinished(cloudlet);
    }

    /**
     * Schedules the execution of the next task of a given cloudlet.
     */
    private void scheduleNextTaskIfCurrentIsFinished(NetworkCloudlet cloudlet) {
        if(!cloudlet.startNextTaskIfCurrentIsFinished(cloudlet.getSimulation().clock())){
            return;
        }

        final Datacenter dc = getVm().getHost().getDatacenter();
        dc.schedule(dc.getId(), dc.getSimulation().getMinTimeBetweenEvents(), CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
    }

    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public void setVm(Vm vm) {
        this.vm = (Objects.isNull(vm) ? Vm.NULL : vm);
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
    private List<VmPacket> getListOfPacketsSentFromVm(Vm sourceVm){
        vmPacketsReceivedMap.putIfAbsent(sourceVm, new ArrayList<>());
        return vmPacketsReceivedMap.get(sourceVm);
    }

    @Override
    public boolean addPacketToListOfPacketsSentFromVm(VmPacket pkt){
        return getListOfPacketsSentFromVm(pkt.getSource()).add(pkt);
    }
}
