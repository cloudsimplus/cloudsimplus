/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cloudbus.cloudsim.Log;

import org.cloudbus.cloudsim.schedulers.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.cloudbus.cloudsim.core.CloudSim;

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
     * A map of {@link HostPacket}'s received, where each key is the Id 
     * of a sender VM and each value is the list of packets sent by that VM.
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
    public void updateCloudletProcessing(CloudletExecutionInfo rcl, double currentTime) {
        /**
         * @todo @author manoelcampos 
         * The error of not sending and receiving packets is in this method.
         * It is not advancing for the next task.
         * The Cloudlet.isFinished just considers the execution tasks,
         * without considering if all tasks were finished.
         * After all, a NetworkCloudlet may not have an
         * execution task, but just send or receive tasks.
         */
        
        NetworkCloudlet netcl = (NetworkCloudlet) rcl.getCloudlet();

        if (netcl.isFinished()) {
            return;
        }
        
        /**
         * @todo @author manoelcampos It should be used polymorphism to avoid
         * including these if's for each type of task.
         */
        if ((netcl.getCurrentTaskNum() == -1)) {
            scheduleNextTaskExecution(netcl);
        }
        else if (netcl.getCurrentTask() instanceof CloudletExecutionTask) {
            super.updateCloudletProcessing(rcl, currentTime);
            updateExecutionTask(rcl, currentTime);
        }
        else if (netcl.getCurrentTask() instanceof CloudletSendTask) {
            addPacketsToBeSentFromVm(netcl);
        }
        else if (netcl.getCurrentTask() instanceof CloudletReceiveTask) {
            receivePackets(netcl);
        }                
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
        final List<HostPacket> packetsToSendFromVmOfCloudlet = 
                getListOfPacketsToBeSentFromVm(sourceCloudlet.getVmId());
        
        Log.println(Log.Level.DEBUG, getClass(), CloudSim.clock(), 
                "%d pkts added to be sent from cloudlet %d in VM %d", 
                dataTask.getPacketsToSend().size(), sourceCloudlet.getId(),
                sourceCloudlet.getVmId());
        
        packetsToSendFromVmOfCloudlet.addAll(dataTask.getPacketsToSend(CloudSim.clock()));
        
        hostPacketsToSendMap.put(sourceCloudlet.getVmId(), packetsToSendFromVmOfCloudlet);
        scheduleNextTaskExecution(sourceCloudlet);
    }

    /**
     * Gets the list of packets to be sent from a given VM.
     * @param sourceVmId the source VM where the list of packets to send will
     * be obtained
     * @return 
     */
    protected List<HostPacket> getListOfPacketsToBeSentFromVm(int sourceVmId) {
        List<HostPacket> pktList = hostPacketsToSendMap.get(sourceVmId);
        if (pktList == null) {
            pktList = new ArrayList<>();
        }
        return pktList;
    }

    /**
     * Check for packets to be received by a given cloudlet
     * and deliver them to it.
     * 
     * @param netcl cloudlet to check if there is packets to be received.
     */
    protected void receivePackets(NetworkCloudlet netcl) {
        CloudletReceiveTask task = (CloudletReceiveTask)netcl.getCurrentTask();        
        
        final List<HostPacket> receivedPkts = getPacketsSentToGivenTask(task);
        // Asumption: packet will not arrive in the same cycle
        receivedPkts.forEach(pkt -> task.receivePacket(pkt));
        receivedPkts.stream().forEach(pkt -> 
            Log.println(
                Log.Level.DEBUG, getClass(), CloudSim.clock(),
                "Cloudlet %d in VM %d received pkt with %.0f bytes from Cloudlet %d in VM %d",
                pkt.getReceiverCloudlet().getId(),
                pkt.getReceiverVmId(),
                pkt.getDataLength(),
                pkt.getSenderCloudlet().getId(),
                pkt.getSenderVmId())
        );
        
        
        /*Removes the received packets from the list of sent packets of the VM,
        to indicate they were in fact received and have to be removed 
        from the list of the sender VM*/
        getListOfPacketsSentFromVm(task.getSourceVmId()).removeAll(receivedPkts);
        
        /**
         * @todo @author manoelcampos The task has to wait the reception
         * of the expected packets just after a given timeout.
         * After that, the task has to stop waiting and fail.
         */
        scheduleNextTaskExecution(netcl);
    }

    /**
     * Gets the list of packets sent to a given CloudletReceiveTask.
     * @param destinationTask The task that is waiting for packets
     * @return 
     */
    protected List<HostPacket> getPacketsSentToGivenTask(CloudletReceiveTask destinationTask) {
        List<HostPacket> packetsFromExpectedSenderVm = 
                getListOfPacketsSentFromVm(destinationTask.getSourceVmId());
        
        return packetsFromExpectedSenderVm
                .stream()
                .filter(pkt -> pkt.getReceiverVmId() == destinationTask.getNetworkCloudlet().getVmId())
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
        task.process(netcl.getCloudletFinishedSoFar());   
        
        scheduleNextTaskExecution(netcl);
    }

    /**
     * Schedules the execution of the next task of a given cloudlet.
     */
    private void scheduleNextTaskExecution(NetworkCloudlet cl) {
        cl.startNextTask(CloudSim.clock());
        //datacenter.schedule(datacenter.getId(), 0.0001, CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
    }
    
    /**
     * Gets the map of {@link HostPacket}'s to send, where each key is the id of the sending VM 
     * and each value is the list of packets to send.
     * 
     * @return a ready-only map of {@link HostPacket}'s to send
     */
    public Map<Integer, List<HostPacket>> getHostPacketsToSendMap() {
        return Collections.unmodifiableMap(hostPacketsToSendMap);
    }

    /**
     * Gets the list of packets received that were sent from a given VM.
     * 
     * @param sourceVmId id of VM to get the list of packets sent from
     * @return the list of packets sent from the given VM
     */
    public List<HostPacket> getListOfPacketsSentFromVm(int sourceVmId){
        List<HostPacket> list = hostPacketsReceivedMap.get(sourceVmId);
        if(list == null){
            list = new ArrayList<>();
            hostPacketsReceivedMap.put(sourceVmId, list);
        }
        
        return list;
    }
    
    /**
     * Adds a packet to the list of packets sent by a given VM.
     * The source VM is got from the packet.
     * 
     * @param pkt packet to be added to the list
     * @return true if the packet was added, false otherwise
     */
    public boolean addPacketToListOfPacketsSentFromVm(HostPacket pkt){
        return getListOfPacketsSentFromVm(pkt.getSenderVmId()).add(pkt);
    }
       

}
