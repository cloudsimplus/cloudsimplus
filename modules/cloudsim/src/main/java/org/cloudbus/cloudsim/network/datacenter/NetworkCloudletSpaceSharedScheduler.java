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

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerAbstract;
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
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 3.0
 * 
 * @todo @author manoelcampos Class have a lot of 
 * duplicated code from the super class.
 */
public class NetworkCloudletSpaceSharedScheduler extends CloudletSchedulerAbstract {
    /**
     * The current CPUs.
     */
    private int currentCpus;

    /**
     * The used PEs.
     */
    private int usedPes;

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
        cloudletWaitingList = new ArrayList<>();
        cloudletExecList = new ArrayList<>();
        cloudletPausedList = new ArrayList<>();
        cloudletFinishedList = new ArrayList<>();
        usedPes = 0;
        currentCpus = 0;
        packetsToSend = new HashMap<>();
        packetsReceived = new HashMap<>();
    }

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        /*@todo Method to long. Several "extract method" refactorings may be performed.*/
        setCurrentMipsShare(mipsShare);
        // update
        double capacity = 0.0;
        int cpus = 0;

        for (Double mips : mipsShare) { // count the CPUs available to the VMM
            capacity += mips;
            if (mips > 0) {
                cpus++;
            }
        }
        currentCpus = cpus;
        capacity /= cpus; // average capacity of each cpu

        for (ResCloudlet rcl : getCloudletExecList()) { // each machine in the
            // exec list has the
            // same amount of cpu

            NetworkCloudlet cl = (NetworkCloudlet) rcl.getCloudlet();

            // check status
            // if execution stage
            // update the cloudlet finishtime
            // CHECK WHETHER IT IS WAITING FOR THE PACKET
            // if packet received change the status of job and update the time.
            //
            if ((cl.getCurrentStageNum() != -1)) {
                if (cl.getCurrentStageNum() == TaskStage.Stage.FINISH.ordinal()) {
                    break;
                }
                TaskStage st = cl.getStages().get(cl.getCurrentStageNum());
                if (st.getStage() == TaskStage.Stage.EXECUTION) {

                    // update the time
                    cl.setTimeSpentInStage(Math.round(CloudSim.clock() - cl.getTimeToStartStage()));
                    if (cl.getTimeSpentInStage() >= st.getTime()) {
                        changeToNextStage(cl, st);
                        // change the stage
                    }
                }
                if (st.getStage() == TaskStage.Stage.WAIT_RECV) {
                    List<HostPacket> pktlist = packetsReceived.get(st.getVmId());
                    List<HostPacket> pkttoremove = new ArrayList<>();
                    if (pktlist != null) {
                        Iterator<HostPacket> it = pktlist.iterator();
                        HostPacket pkt;
                        if (it.hasNext()) {
                            pkt = it.next();
                            // Asumption packet will not arrive in the same cycle
                            if (pkt.receiverVmId == cl.getVmId()) {
                                pkt.receiveTime = CloudSim.clock();
                                st.setTime(CloudSim.clock() - pkt.sendTime);
                                changeToNextStage(cl, st);
                                pkttoremove.add(pkt);
                            }
                        }
                        pktlist.removeAll(pkttoremove);
                        // if(pkt!=null)
                        // else wait for recieving the packet
                    }
                }

            } else {
                cl.setCurrentStageNum(0);
                cl.setTimeToStartStage(CloudSim.clock());

                if (cl.getStages().get(0).getStage() == TaskStage.Stage.EXECUTION) {
                    datacenter.schedule(datacenter.getId(), cl.getStages().get(0).getTime(),
                            CloudSimTags.VM_DATACENTER_EVENT);
                } else {
                    datacenter.schedule(
                            datacenter.getId(), 0.0001,
                            CloudSimTags.VM_DATACENTER_EVENT);
                    // /sendstage///
                }
            }

        }

        if (getCloudletExecList().isEmpty() && getCloudletWaitingList().isEmpty()) { 
            // no more cloudlets in this scheduler
            setPreviousTime(currentTime);
            return 0.0;
        }

        // update each cloudlet
        int finished = 0;
        List<ResCloudlet> toRemove = new ArrayList<>();
        for (ResCloudlet rcl : getCloudletExecList()) {
            // rounding issue...
            if (((NetworkCloudlet) (rcl.getCloudlet())).getCurrentStageNum() == TaskStage.Stage.FINISH.ordinal()) {
                // stage is changed and packet to send
                ((NetworkCloudlet) (rcl.getCloudlet())).finishtime = CloudSim.clock();
                toRemove.add(rcl);
                cloudletFinish(rcl);
                finished++;
            }
        }
        getCloudletExecList().removeAll(toRemove);
        // add all the CloudletExecList in waitingList.
        // sort the waitinglist

        // for each finished cloudlet, add a new one from the waiting list
        if (!getCloudletWaitingList().isEmpty()) {
            for (int i = 0; i < finished; i++) {
                toRemove.clear();
                for (ResCloudlet rcl : getCloudletWaitingList()) {
                    if ((currentCpus - usedPes) >= rcl.getNumberOfPes()) {
                        rcl.setCloudletStatus(Cloudlet.Status.INEXEC);
                        for (int k = 0; k < rcl.getNumberOfPes(); k++) {
                            rcl.setMachineAndPeId(0, i);
                        }
                        getCloudletExecList().add(rcl);
                        usedPes += rcl.getNumberOfPes();
                        toRemove.add(rcl);
                        break;
                    }
                }
                getCloudletWaitingList().removeAll(toRemove);
            }// for(cont)
        }

        // estimate finish time of cloudlets in the execution queue
        double nextEvent = Double.MAX_VALUE;
        for (ResCloudlet rcl : getCloudletExecList()) {
            double remainingLength = rcl.getRemainingCloudletLength();
            double estimatedFinishTime = currentTime + (remainingLength / (capacity * rcl.getNumberOfPes()));
            if (estimatedFinishTime - currentTime < CloudSim.getMinTimeBetweenEvents()) {
                estimatedFinishTime = currentTime + CloudSim.getMinTimeBetweenEvents();
            }
            if (estimatedFinishTime < nextEvent) {
                nextEvent = estimatedFinishTime;
            }
        }
        setPreviousTime(currentTime);
        return nextEvent;
    }

    /**
     * Changes a cloudlet to the next stage.
     *
     * @todo Method too long to understand what is its responsibility.
     */
    private void changeToNextStage(NetworkCloudlet cl, TaskStage st) {
        cl.setTimeSpentInStage(0);
        cl.setTimeToStartStage(CloudSim.clock());
        int currentStage = cl.getCurrentStageNum();
        if (currentStage >= (cl.getStages().size() - 1)) {
            cl.setCurrentStageNum(TaskStage.Stage.FINISH.ordinal());
        } else {
            cl.setCurrentStageNum(currentStage + 1);
            int i = 0;
            for (i = cl.getCurrentStageNum(); i < cl.getStages().size(); i++) {
                if (cl.getStages().get(i).getStage() == TaskStage.Stage.WAIT_SEND) {
                    HostPacket pkt = new HostPacket(
                            cl.getVmId(), cl.getStages().get(i).getVmId(), 
                            cl.getStages().get(i).getDataLenght(),
                            CloudSim.clock(), -1,
                            cl.getId(), cl.getStages().get(i).getCloudletId());
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
            if (i == cl.getStages().size()) {
                cl.setCurrentStageNum(TaskStage.Stage.FINISH.ordinal());
            } else {
                cl.setCurrentStageNum(i);
                if (cl.getStages().get(i).getStage() == TaskStage.Stage.EXECUTION) {
                    datacenter.schedule(datacenter.getId(),
                        cl.getStages().get(i).getTime(),
                        CloudSimTags.VM_DATACENTER_EVENT);
                }
            }
        }
    }

    @Override
    public Cloudlet cloudletCancel(int cloudletId) {
        // First, looks in the finished queue
        for (ResCloudlet rcl : getCloudletFinishedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                getCloudletFinishedList().remove(rcl);
                return rcl.getCloudlet();
            }
        }

        // Then searches in the exec list
        for (ResCloudlet rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                getCloudletExecList().remove(rcl);
                if (rcl.getRemainingCloudletLength() == 0.0) {
                    cloudletFinish(rcl);
                } else {
                    rcl.setCloudletStatus(Cloudlet.Status.CANCELED);
                }
                return rcl.getCloudlet();
            }
        }

        // Now, looks in the paused queue
        for (ResCloudlet rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                getCloudletPausedList().remove(rcl);
                return rcl.getCloudlet();
            }
        }

        // Finally, looks in the waiting list
        for (ResCloudlet rcl : getCloudletWaitingList()) {
            if (rcl.getCloudletId() == cloudletId) {
                rcl.setCloudletStatus(Cloudlet.Status.CANCELED);
                getCloudletWaitingList().remove(rcl);
                return rcl.getCloudlet();
            }
        }

        return null;

    }

    @Override
    public boolean cloudletPause(int cloudletId) {
        boolean found = false;
        int position = 0;

        // first, looks for the cloudlet in the exec list
        for (ResCloudlet rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                break;
            }
            position++;
        }

        if (found) {
            // moves to the paused list
            ResCloudlet rgl = getCloudletExecList().remove(position);
            if (rgl.getRemainingCloudletLength() == 0.0) {
                cloudletFinish(rgl);
            } else {
                rgl.setCloudletStatus(Cloudlet.Status.PAUSED);
                getCloudletPausedList().add(rgl);
            }
            return true;

        }

        // now, look for the cloudlet in the waiting list
        position = 0;
        found = false;
        for (ResCloudlet rcl : getCloudletWaitingList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                break;
            }
            position++;
        }

        if (found) {
            // moves to the paused list
            ResCloudlet rgl = getCloudletWaitingList().remove(position);
            if (rgl.getRemainingCloudletLength() == 0.0) {
                cloudletFinish(rgl);
            } else {
                rgl.setCloudletStatus(Cloudlet.Status.PAUSED);
                getCloudletPausedList().add(rgl);
            }
            return true;

        }

        return false;
    }

    @Override
    public void cloudletFinish(ResCloudlet rcl) {
        rcl.setCloudletStatus(Cloudlet.Status.SUCCESS);
        rcl.finalizeCloudlet();
        getCloudletFinishedList().add(rcl);
        usedPes -= rcl.getNumberOfPes();
    }

    @Override
    public double cloudletResume(int cloudletId) {
        boolean found = false;
        int position = 0;

        // look for the cloudlet in the paused list
        for (ResCloudlet rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                found = true;
                break;
            }
            position++;
        }

        if (found) {
            ResCloudlet rcl = getCloudletPausedList().remove(position);

            // it can go to the exec list
            if ((currentCpus - usedPes) >= rcl.getNumberOfPes()) {
                rcl.setCloudletStatus(Cloudlet.Status.INEXEC);
                for (int i = 0; i < rcl.getNumberOfPes(); i++) {
                    rcl.setMachineAndPeId(0, i);
                }

                long size = rcl.getRemainingCloudletLength();
                size *= rcl.getNumberOfPes();
                rcl.getCloudlet().setCloudletLength(size);

                getCloudletExecList().add(rcl);
                usedPes += rcl.getNumberOfPes();

                // calculate the expected time for cloudlet completion
                double capacity = 0.0;
                int cpus = 0;
                for (Double mips : getCurrentMipsShare()) {
                    capacity += mips;
                    if (mips > 0) {
                        cpus++;
                    }
                }
                currentCpus = cpus;
                capacity /= cpus;

                long remainingLength = rcl.getRemainingCloudletLength();
                double estimatedFinishTime = CloudSim.clock()
                        + (remainingLength / (capacity * rcl.getNumberOfPes()));

                return estimatedFinishTime;
            } else {// no enough free PEs: go to the waiting queue
                rcl.setCloudletStatus(Cloudlet.Status.QUEUED);

                long size = rcl.getRemainingCloudletLength();
                size *= rcl.getNumberOfPes();
                rcl.getCloudlet().setCloudletLength(size);

                getCloudletWaitingList().add(rcl);
                return 0.0;
            }

        }

        // not found in the paused list: either it is in in the queue, executing
        // or not exist
        return 0.0;

    }

    @Override
    public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
        // it can go to the exec list
        if ((currentCpus - usedPes) >= cloudlet.getNumberOfPes()) {
            ResCloudlet rcl = new ResCloudlet(cloudlet);
            rcl.setCloudletStatus(Cloudlet.Status.INEXEC);
            for (int i = 0; i < cloudlet.getNumberOfPes(); i++) {
                rcl.setMachineAndPeId(0, i);
            }

            getCloudletExecList().add(rcl);
            usedPes += cloudlet.getNumberOfPes();
        } else {// no enough free PEs: go to the waiting queue
            ResCloudlet rcl = new ResCloudlet(cloudlet);
            rcl.setCloudletStatus(Cloudlet.Status.QUEUED);
            getCloudletWaitingList().add(rcl);
            return 0.0;
        }

        // calculate the expected time for cloudlet completion
        double capacity = 0.0;
        int cpus = 0;
        for (Double mips : getCurrentMipsShare()) {
            capacity += mips;
            if (mips > 0) {
                cpus++;
            }
        }

        currentCpus = cpus;
        capacity /= cpus;

        // use the current capacity to estimate the extra amount of
        // time to file transferring. It must be added to the cloudlet length
        double extraSize = capacity * fileTransferTime;
        long length = cloudlet.getCloudletLength();
        length += extraSize;
        cloudlet.setCloudletLength(length);
        return cloudlet.getCloudletLength() / capacity;
    }

    @Override
    public double cloudletSubmit(Cloudlet cloudlet) {
        cloudletSubmit(cloudlet, 0);
        return 0;
    }

    @Override
    public int getCloudletStatus(int cloudletId) {
        for (ResCloudlet rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus().ordinal();
            }
        }

        for (ResCloudlet rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus().ordinal();
            }
        }

        for (ResCloudlet rcl : getCloudletWaitingList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus().ordinal();
            }
        }

        return -1;
    }

    @Override
    public double getTotalUtilizationOfCpu(double time) {
        double totalUtilization = 0;
        for (ResCloudlet gl : getCloudletExecList()) {
            totalUtilization += gl.getCloudlet().getUtilizationOfCpu(time);
        }
        return totalUtilization;
    }

    @Override
    public boolean hasFinishedCloudlets() {
        return getCloudletFinishedList().size() > 0;
    }

    @Override
    public Cloudlet getNextFinishedCloudlet() {
        if (getCloudletFinishedList().size() > 0) {
            return getCloudletFinishedList().remove(0).getCloudlet();
        }
        return null;
    }

    @Override
    public int runningCloudletsNumber() {
        return getCloudletExecList().size();
    }

    @Override
    public Cloudlet migrateCloudlet() {
        ResCloudlet rcl = getCloudletExecList().remove(0);
        rcl.finalizeCloudlet();
        Cloudlet cl = rcl.getCloudlet();
        usedPes -= cl.getNumberOfPes();
        return cl;
    }

    @Override
    public List<Double> getCurrentRequestedMips() {
        List<Double> mipsShare = new ArrayList<>();
        if (getCurrentMipsShare() != null) {
            for (Double mips : getCurrentMipsShare()) {
                mipsShare.add(mips);
            }
        }
        return mipsShare;
    }

    @Override
    public double getTotalCurrentAvailableMipsForCloudlet(ResCloudlet rcl, List<Double> mipsShare) {
        /*@todo The param rcl is not being used.*/
        double capacity = 0.0;
        int cpus = 0;
        for (Double mips : mipsShare) { // count the cpus available to the vmm
            capacity += mips;
            if (mips > 0) {
                cpus++;
            }
        }
        currentCpus = cpus;
        capacity /= cpus; // average capacity of each cpu
        return capacity;
    }

    @Override
    public double getTotalCurrentAllocatedMipsForCloudlet(ResCloudlet rcl, double time) {
        //@todo The method doesn't appear to be implemented in fact
        return 0.0;
    }

    @Override
    public double getTotalCurrentRequestedMipsForCloudlet(ResCloudlet rcl, double time) {
        //@todo The method doesn't appear to be implemented in fact
        return 0.0;
    }

    @Override
    public double getCurrentRequestedUtilizationOfBw() {
        //@todo The method doesn't appear to be implemented in fact
        return 0;
    }

    @Override
    public double getCurrentRequestedUtilizationOfRam() {
        //@todo The method doesn't appear to be implemented in fact
        return 0;
    }

    public Map<Integer, List<HostPacket>> getPacketsToSend() {
        return packetsToSend;
    }

    public Map<Integer, List<HostPacket>> getPacketsReceived() {
        return packetsReceived;
    }

}
