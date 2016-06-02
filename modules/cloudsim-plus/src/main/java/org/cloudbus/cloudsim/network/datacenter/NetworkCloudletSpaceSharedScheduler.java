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
import org.cloudbus.cloudsim.resources.Processor;

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
     * @see #getPacketsToSendMap() 
     */
    private final Map<Integer, List<HostPacket>> packetsToSendMap;

    /**
     * @see #getPacketsReceivedMap() 
     */
    private final Map<Integer, List<HostPacket>> packetsReceivedMap;
    
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
        packetsToSendMap = new HashMap<>();
        packetsReceivedMap = new HashMap<>();
    }

    @Override
    protected void updateCloudletProcessing(ResCloudlet rcl, double currentTime, Processor p) {
        NetworkCloudlet netcl = (NetworkCloudlet) rcl.getCloudlet();

        if (netcl.isFinished()) {
            return;
        }
        
        Log.println(Log.Level.DEBUG, getClass(), currentTime, 
            "NetworkCloudlet %d current task: %d", 
            netcl.getId(), netcl.getCurrentTaskNum());
        
        /**
         * @todo @author manoelcampos It should be used polymorphism to avoid
         * using these if's for each task type.
         */
        if ((netcl.getCurrentTaskNum() == -1)) {
            startNextTask(netcl);
        }
        else if (netcl.getCurrentTask() instanceof CloudletExecutionTask) {
            super.updateCloudletProcessing(rcl, currentTime, p);
            updateExecutionTask(rcl, currentTime, p);
        }
        else if (netcl.getCurrentTask() instanceof CloudletSendTask) {
            addPacketsToBeSent(netcl);
        }
        else if (netcl.getCurrentTask() instanceof CloudletReceiveTask) {
            //Log.println(Log.Level.DEBUG, getClass(), currentTime, "updateCloudletProcessing - Update NetworkCloudlet %d WAIT_RECV task", netcl.getId());
            receivePackets(netcl);
        }
    }

    protected void addPacketsToBeSent(NetworkCloudlet netcl) {
        CloudletSendTask dataTask = (CloudletSendTask)netcl.getCurrentTask();
        final List<HostPacket> pktList = getVmPacketsToSendList(netcl);
        pktList.addAll(dataTask.getPacketsToSend(CloudSim.clock()));
        dataTask.getPacketsToSend().clear();
        
        packetsToSendMap.put(netcl.getVmId(), pktList);
        startNextTask(netcl);
    }

    protected List<HostPacket> getVmPacketsToSendList(NetworkCloudlet netcl) {
        List<HostPacket> pktList = packetsToSendMap.get(netcl.getVmId());
        if (pktList == null) {
            pktList = new ArrayList<>();
        }
        return pktList;
    }

    protected void receivePackets(NetworkCloudlet netcl) {
        CloudletReceiveTask task = (CloudletReceiveTask)netcl.getCurrentTask();        
        
        final List<HostPacket> pktToRemove = getPacketsSentToGivenTask(task);
        // Asumption: packet will not arrive in the same cycle
        pktToRemove.forEach(pkt -> pkt.receiveTime = CloudSim.clock());
        task.computeExecutionTime(CloudSim.clock());

        getListOfPacketsSentFromVm(task.getSourceVmId()).removeAll(pktToRemove);
        
        startNextTask(netcl);
    }

    protected List<HostPacket> getListOfPacketsSentFromVm(int sourceVmId) {
        List<HostPacket> list = packetsReceivedMap.get(sourceVmId);
        if (list == null){
            list = new ArrayList<>();
            packetsReceivedMap.put(sourceVmId, list);
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
                .filter(pkt -> pkt.receiverVmId == task.getNetworkCloudlet().getVmId())
                .collect(Collectors.toList());
    }

    protected void updateExecutionTask(ResCloudlet rcl, double currentTime, Processor p) {
        NetworkCloudlet netcl = (NetworkCloudlet)rcl.getCloudlet();
        /**
         * @todo @author manoelcampos updates the execution
         * length of the task, considering the NetworkCloudlet
         * has only one execution task.
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
     * Gets the map of packets to send, where each key is the Id of the sending VM and each
     * value is the list of packets to send.
     * 
     * @return 
     */
    public Map<Integer, List<HostPacket>> getPacketsToSendMap() {
        return packetsToSendMap;
    }

    /**
     * Gets the map of packets received, where each key is the Id of a sender VM and each
     * value is the list of packets sent by that VM.
     * 
     * @return 
     */
    public Map<Integer, List<HostPacket>> getPacketsReceivedMap() {
        return packetsReceivedMap;
    }

}
