/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cloudbus.cloudsim.Log;

import org.cloudbus.cloudsim.schedulers.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

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
 * 
 * @since CloudSim Toolkit 3.0
 * 
 */
public class NetworkCloudletSpaceSharedScheduler extends CloudletSchedulerSpaceShared {
    /**
     * @see #getHostPacketsToSendMap() 
     */
    private final Map<Integer, List<HostPacket>> hostPacketsToSendMap;

    /**
     * @see #getHostPacketsReceivedMap() 
     */
    private final Map<Integer, List<HostPacket>> hostPacketsReceivedMap;
    
    /**
     * The datacenter where the VM using this scheduler runs.
     */
    private final NetworkDatacenter datacenter;

    /**
     * Creates a new CloudletSchedulerSpaceShared object. This method must be
     * invoked before starting the actual simulation.
     *
     * @param datacenter the datacenter where the VM using this scheduler runs
     * @pre $none
     * @post $none
     */
    public NetworkCloudletSpaceSharedScheduler(NetworkDatacenter datacenter) {
        super();
        this.datacenter = datacenter;
        hostPacketsToSendMap = new HashMap<>();
        hostPacketsReceivedMap = new HashMap<>();
    }

    @Override
    public void updateCloudletProcessing(ResCloudlet rcl, double currentTime) {
        NetworkCloudlet netcl = (NetworkCloudlet) rcl.getCloudlet();

        if (netcl.isFinished()) {
            return;
        }
        
        Log.println(Log.Level.DEBUG, getClass(), currentTime, 
            "NetworkCloudlet %d current task: %d", 
            netcl.getId(), netcl.getCurrentTaskNum());
        
        /**
         * @todo @author manoelcampos It should be used polymorphism to avoid
         * including these if's for each type of task.
         */
        if ((netcl.getCurrentTaskNum() == -1)) {
            startNextTask(netcl);
        }
        else if (netcl.getCurrentTask() instanceof CloudletExecutionTask) {
            super.updateCloudletProcessing(rcl, currentTime);
            updateExecutionTask(rcl, currentTime);
        }
        else if (netcl.getCurrentTask() instanceof CloudletSendTask) {
            addPacketsToBeSent(netcl);
        }
        else if (netcl.getCurrentTask() instanceof CloudletReceiveTask) {
            receivePackets(netcl);
        }
    }

    protected void addPacketsToBeSent(NetworkCloudlet netcl) {
        CloudletSendTask dataTask = (CloudletSendTask)netcl.getCurrentTask();
        final List<HostPacket> pktList = getVmPacketsToSendList(netcl);
        pktList.addAll(dataTask.getPacketsToSend(CloudSim.clock()));
        dataTask.getPacketsToSend().clear();
        
        hostPacketsToSendMap.put(netcl.getVmId(), pktList);
        startNextTask(netcl);
    }

    protected List<HostPacket> getVmPacketsToSendList(NetworkCloudlet netcl) {
        List<HostPacket> pktList = hostPacketsToSendMap.get(netcl.getVmId());
        if (pktList == null) {
            pktList = new ArrayList<>();
        }
        return pktList;
    }

    protected void receivePackets(NetworkCloudlet netcl) {
        CloudletReceiveTask task = (CloudletReceiveTask)netcl.getCurrentTask();        
        
        final List<HostPacket> pktToRemove = getPacketsSentToGivenTask(task);
        // Asumption: packet will not arrive in the same cycle
        pktToRemove.forEach(pkt -> pkt.setReceiveTime(CloudSim.clock()));
        task.computeExecutionTime(CloudSim.clock());

        getListOfPacketsSentFromVm(task.getSourceVmId()).removeAll(pktToRemove);
        
        startNextTask(netcl);
    }

    protected List<HostPacket> getListOfPacketsSentFromVm(int sourceVmId) {
        List<HostPacket> list = hostPacketsReceivedMap.get(sourceVmId);
        if (list == null){
            list = new ArrayList<>();
            hostPacketsReceivedMap.put(sourceVmId, list);
        }
        
        return list;
    }

    /**
     * Gets the list of packets sent to a given CloudletReceiveTask.
     * @param task The task that is waiting for packets
     * @return 
     */
    protected List<HostPacket> getPacketsSentToGivenTask(CloudletReceiveTask task) {
        List<HostPacket> packetsFromExpectedSenderVm = getListOfPacketsSentFromVm(task.getSourceVmId());
        return packetsFromExpectedSenderVm
                .stream()
                .filter(pkt -> pkt.getReceiverVmId() == task.getNetworkCloudlet().getVmId())
                .collect(Collectors.toList());
    }

    protected void updateExecutionTask(ResCloudlet rcl, double currentTime) {
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
        task.process(netcl.getCloudletFinishedSoFar());   
        if (task.isFinished()) {
            netcl.getCurrentTask().computeExecutionTime(currentTime);
            startNextTask(netcl);
        }            
    }

    /**
     * Changes a cloudlet to the next task.
     */
    private void startNextTask(NetworkCloudlet cl) {
        if (!cl.isTheLastTask()) {
            cl.startNextTask(CloudSim.clock());
            datacenter.schedule(datacenter.getId(), 0.0001, 
                    CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }
    }
    
    /**
     * Gets the map of {@link HostPacket}'s to send, where each key is the Id of the sending VM and each
     * value is the list of packets to send.
     * 
     * @return 
     */
    public Map<Integer, List<HostPacket>> getHostPacketsToSendMap() {
        return hostPacketsToSendMap;
    }

    /**
     * Gets the map of {@link HostPacket}'s received, where each key is the Id of a sender VM and each
     * value is the list of packets sent by that VM.
     * 
     * @return 
     */
    public Map<Integer, List<HostPacket>> getHostPacketsReceivedMap() {
        return hostPacketsReceivedMap;
    }

}
