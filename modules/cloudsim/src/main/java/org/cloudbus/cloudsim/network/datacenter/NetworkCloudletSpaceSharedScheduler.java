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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
     * The map of packets to send, where each key is a destination VM and each
     * value is the list of packets to sent to that VM.
     */
    private final Map<Integer, List<HostPacket>> packetsToSend;

    /**
     * The map of packets received, where each key is the id of a sender VM and each
     * value is the list of packets sent by that VM.
     */
    private final Map<Integer, List<HostPacket>> packetsReceived;
    
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
        packetsToSend = new HashMap<>();
        packetsReceived = new HashMap<>();
    }

    @Override
    protected void updateCloudletProcessing(ResCloudlet rcl, double currentTime, Processor p) {
        NetworkCloudlet netcl = (NetworkCloudlet) rcl.getCloudlet();

        if (netcl.getCurrentTaskNum() >= netcl.getNumberOfTasks()) {
            return;
        }
        
        if ((netcl.getCurrentTaskNum() == -1)) {
            /**
             * @todo @author manoelcampos
             * It is assuming the execution state is the first one,
             * but not necessarily.
             * This situation has to be checked.
             */
            startExecutionTask(netcl);
        }
        else if (netcl.getCurrentTask().getStage() == Task.Stage.EXECUTION) {
            updateExecutionTask(rcl, currentTime, p);
        }
        else if (netcl.getCurrentTask().getStage() == Task.Stage.WAIT_RECV) {
            updateWaitReceiveTask(netcl);
        }
    }

    protected void updateWaitReceiveTask(NetworkCloudlet netcl) {
        Task task = netcl.getCurrentTask();
        List<HostPacket> pktList = packetsReceived.get(task.getVmId());
        List<HostPacket> pktToRemove = new ArrayList<>();
        if (pktList != null) {
            Iterator<HostPacket> it = pktList.iterator();
            if (it.hasNext()) {
                HostPacket pkt = it.next();
                // Asumption: packet will not arrive in the same cycle
                if (pkt.receiverVmId == netcl.getVmId()) {
                    pkt.receiveTime = CloudSim.clock();
                    task.setExecutionTime(CloudSim.clock() - pkt.sendTime);
                    changeToNextTask(netcl);
                    pktToRemove.add(pkt);
                }
            }
            pktList.removeAll(pktToRemove);
        }
    }

    protected void updateExecutionTask(ResCloudlet rcl, double currentTime, Processor p) {
        super.updateCloudletProcessing(rcl, currentTime, p);
        
        NetworkCloudlet netcl = (NetworkCloudlet)rcl.getCloudlet();
        /**
         * @todo @author manoelcampos
         * It has to be checked directly on the
         * task if it is finished or not,
         * once in next versions
         * it would be possible to have
         * multiple execution tasks in the same
         * NetworkCloudlet.
         */
        if (rcl.getCloudlet().isFinished()) {
            changeToNextTask(netcl);
        }
    }

    protected void startExecutionTask(NetworkCloudlet netcl) {
        Task task;
        task = netcl.setCurrentTaskNum(0);
        task.setStartTime(CloudSim.clock());
        if (task.getStage() == Task.Stage.EXECUTION) {
            /**
             * @todo @author manoelcampos
             * It musn't have to be used
             * the getExecutionTime to update the task processing.
             * The execution time in fact is being computed
             * to show the total time spend in in the task.
             * The execution of the task has to be updated
             * at the same way that a CloudletSimple is.
             */
            datacenter.schedule(datacenter.getId(), task.getExecutionTime(),
                    CloudSimTags.VM_DATACENTER_EVENT);
        } else {
            datacenter.schedule(
                    datacenter.getId(), 0.0001,
                    CloudSimTags.VM_DATACENTER_EVENT);
        }
    }
    
    /**
     * Changes a cloudlet to the next stage.
     *
     * @todo Method too long to understand what is its responsibility.
     */
    private void changeToNextTask(NetworkCloudlet cl) {
        Task currTask = cl.getCurrentTask();
        currTask.setExecutionTime(Math.round(CloudSim.clock() - currTask.getStartTime()));

        int currentTaskNum = cl.getCurrentTaskNum();
        if (currentTaskNum >= cl.getTasks().size() - 1) {
            cl.setCurrentTaskNum(cl.getTasks().size());
        } else {
            Task nextTask = cl.setCurrentTaskNum(currentTaskNum + 1);
            nextTask.setStartTime(CloudSim.clock());
            int i = 0;
            for (i = cl.getCurrentTaskNum(); i < cl.getTasks().size(); i++) {
                if (cl.getTasks().get(i).getStage() == Task.Stage.WAIT_SEND) {
                    HostPacket pkt = new HostPacket(
                            cl.getVmId(), cl.getTasks().get(i).getVmId(), 
                            cl.getTasks().get(i).getDataLenght(),
                            CloudSim.clock(), -1,
                            cl.getId(), cl.getTasks().get(i).getCloudletId());
                    List<HostPacket> pktlist = packetsToSend.get(cl.getVmId());
                    if (pktlist == null) {
                        pktlist = new ArrayList<>();
                    }
                    pktlist.add(pkt);
                    packetsToSend.put(cl.getVmId(), pktlist);
                } else {
                    break;
                }
            }
            
            datacenter.schedule(datacenter.getId(),
                    0.0001, CloudSimTags.VM_DATACENTER_EVENT);
            if (i == cl.getTasks().size()) {
                cl.setCurrentTaskNum(cl.getTasks().size());
            } else {
                cl.setCurrentTaskNum(i);
                if (cl.getTasks().get(i).getStage() == Task.Stage.EXECUTION) {
                    datacenter.schedule(datacenter.getId(),
                        cl.getTasks().get(i).getExecutionTime(),
                        CloudSimTags.VM_DATACENTER_EVENT);
                }
            }
        }
    }
    
    public Map<Integer, List<HostPacket>> getPacketsToSend() {
        return packetsToSend;
    }

    public Map<Integer, List<HostPacket>> getPacketsReceived() {
        return packetsReceived;
    }

}
