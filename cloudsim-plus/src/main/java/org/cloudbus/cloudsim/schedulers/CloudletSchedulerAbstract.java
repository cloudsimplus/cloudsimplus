/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Cloudlet.Status;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.listeners.VmToCloudletEventInfo;
import org.cloudbus.cloudsim.resources.Processor;

/**
 * Implements the basic features of a {@link CloudletScheduler}, representing
 * the policy of scheduling performed by a virtual machine to run its
 * {@link Cloudlet Cloudlets}. So, classes extending this must execute
 * Cloudlets. The interface for cloudlet management is also implemented in this
 * class. Each VM has to have its own instance of a CloudletScheduler.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public abstract class CloudletSchedulerAbstract implements CloudletScheduler {
    /** @see #getProcessor()  */
    private Processor processor;

    /**
     * The number of used PEs.
     */
    protected int usedPes;

    /**
     * @see #getPreviousTime()
     */
    private double previousTime;

    /**
     * @see #getCurrentMipsShare()
     */
    private List<Double> currentMipsShare;

    /**
     * @see #getCloudletWaitingList()
     */
    protected List<? extends CloudletExecutionInfo> cloudletWaitingList;

    /**
     * @see #getCloudletExecList()
     */
    protected List<? extends CloudletExecutionInfo> cloudletExecList;

    /**
     * @see #getCloudletPausedList()
     */
    protected List<? extends CloudletExecutionInfo> cloudletPausedList;

    /**
     * @see #getCloudletFinishedList()
     */
    protected List<? extends CloudletExecutionInfo> cloudletFinishedList;

    /**
     * @see #getCloudletFailedList()
     */
    protected List<? extends CloudletExecutionInfo> cloudletFailedList;
    
    /**
     * @see #getVm() 
     */
    private Vm vm;

    /**
     * Creates a new CloudletScheduler object. A CloudletScheduler must be
     * created before starting the actual simulation.
     *
     * @pre $none
     * @post $none
     */
    public CloudletSchedulerAbstract() {
        setPreviousTime(0.0);
        cloudletWaitingList = new ArrayList<>();
        cloudletExecList = new ArrayList<>();
        cloudletPausedList = new ArrayList<>();
        cloudletFinishedList = new ArrayList<>();
        cloudletFailedList = new ArrayList<>();
    }

    @Override
    public double getPreviousTime() {
        return previousTime;
    }

    /**
     * Sets the previous time when the scheduler updated the processing of
     * cloudlets it is managing.
     *
     * @param previousTime the new previous time
     */
    protected final void setPreviousTime(double previousTime) {
        this.previousTime = previousTime;
    }

    /**
     * Sets the list of current mips share available for the VM using the
     * scheduler.
     *
     * @param currentMipsShare the new current mips share
     * @see #getCurrentMipsShare()
     */
    protected void setCurrentMipsShare(List<Double> currentMipsShare) {
        this.currentMipsShare = currentMipsShare;
        processor = Processor.fromMipsList(currentMipsShare, getCloudletExecList());
    }

    @Override
    public List<Double> getCurrentMipsShare() {
        return currentMipsShare;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CloudletExecutionInfo> List<T> getCloudletWaitingList() {
        return (List<T>) cloudletWaitingList;
    }

    /**
     * Sets the list of cloudlet waiting to be executed on the VM.
     *
     * @param <T> the generic type
     * @param cloudletWaitingList the cloudlet waiting list
     */
    protected <T extends CloudletExecutionInfo> void setCloudletWaitingList(List<T> cloudletWaitingList) {
        this.cloudletWaitingList = cloudletWaitingList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CloudletExecutionInfo> List<T> getCloudletExecList() {
        return (List<T>) cloudletExecList;
    }

    /**
     * Sets the list of cloudlets being executed on the VM.
     *
     * @param <T> the generic type
     * @param cloudletExecList the new cloudlet exec list
     */
    protected <T extends CloudletExecutionInfo> void setCloudletExecList(List<T> cloudletExecList) {
        this.cloudletExecList = cloudletExecList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CloudletExecutionInfo> List<T> getCloudletPausedList() {
        return (List<T>) cloudletPausedList;
    }

    /**
     * Sets the list of paused cloudlets.
     *
     * @param <T> the generic type
     * @param cloudletPausedList the new cloudlet paused list
     */
    protected <T extends CloudletExecutionInfo> void setCloudletPausedList(List<T> cloudletPausedList) {
        this.cloudletPausedList = cloudletPausedList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CloudletExecutionInfo> List<T> getCloudletFinishedList() {
        return (List<T>) cloudletFinishedList;
    }

    /**
     * Sets the list of finished cloudlets.
     *
     * @param <T> the generic type
     * @param cloudletFinishedList the new cloudlet finished list
     */
    protected <T extends CloudletExecutionInfo> void setCloudletFinishedList(List<T> cloudletFinishedList) {
        this.cloudletFinishedList = cloudletFinishedList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CloudletExecutionInfo> List<T> getCloudletFailedList() {
        return (List<T>) cloudletFailedList;
    }

    /**
     * Sets the list of failed cloudlets.
     *
     * @param <T> the generic type
     * @param cloudletFailedList the new cloudlet failed list.
     */
    protected <T extends CloudletExecutionInfo> void setCloudletFailedList(List<T> cloudletFailedList) {
        this.cloudletFailedList = cloudletFailedList;
    }
    
    @Override
    public double cloudletSubmit(Cloudlet cloudlet) {
        return cloudletSubmit(cloudlet, 0.0);
    }    

    @Override
    public double getTotalUtilizationOfCpu(double time) {
        return getCloudletExecList().stream()
                .mapToDouble(rcl -> rcl.getCloudlet().getUtilizationOfCpu(time))
                .sum();
    }

    @Override
    public boolean hasFinishedCloudlets() {
        return getCloudletFinishedList().size() > 0;
    }
    
    @Override
    public int runningCloudletsNumber() {
        return getCloudletExecList().size();
    }

    @Override
    public Cloudlet getNextFinishedCloudlet() {
        if (getCloudletFinishedList().size() > 0) {
            return getCloudletFinishedList().remove(0).getCloudlet();
        }
        return null;
    }
    
    /**
     * Returns the first cloudlet to migrate to another VM.
     *
     * @return the first running cloudlet
     * @pre $none
     * @post $none
     */
    @Override
    public Cloudlet migrateCloudlet() {
        if (getCloudletExecList().isEmpty()) {
            return Cloudlet.NULL;
        }

        CloudletExecutionInfo rcl = getCloudletExecList().remove(0);
        rcl.finalizeCloudlet();
        Cloudlet cl = rcl.getCloudlet();
        return cl;
    }
    
    @Override
    public int getCloudletStatus(int cloudletId) {
        for (CloudletExecutionInfo rcl : getCloudletExecList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus().ordinal();
            }
        }

        for (CloudletExecutionInfo rcl : getCloudletPausedList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus().ordinal();
            }
        }

        for (CloudletExecutionInfo rcl : getCloudletWaitingList()) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl.getCloudletStatus().ordinal();
            }
        }

        return -1;
    }
    
    @Override
    public void cloudletFinish(CloudletExecutionInfo rcl) {
        rcl.setCloudletStatus(Cloudlet.Status.SUCCESS);
        rcl.finalizeCloudlet();
        getCloudletFinishedList().add(rcl);
    }    
    
    @Override
    public boolean cloudletPause(int cloudletId) {
        if(changeStatusOfCloudletIntoList(
                getCloudletExecList(), cloudletId, 
                c -> changeStatusOfCloudlet(c, Status.INEXEC, Status.PAUSED)) != Cloudlet.NULL){
            return true;
        }
        
        if(changeStatusOfCloudletIntoList(
                getCloudletWaitingList(), cloudletId, 
                c -> changeStatusOfCloudlet(c, Status.READY, Status.PAUSED)) != Cloudlet.NULL){
            return true;
        }

        return false;
    }    
    
    @Override
    public Cloudlet cloudletCancel(int cloudletId) {
        Cloudlet cloudlet;
        cloudlet = changeStatusOfCloudletIntoList(getCloudletFinishedList(), cloudletId, (c)->{});
        if(cloudlet != Cloudlet.NULL)
            return cloudlet;
        
        cloudlet = changeStatusOfCloudletIntoList(
                getCloudletExecList(), cloudletId, 
                c -> changeStatusOfCloudlet(c, Status.INEXEC, Status.CANCELED));
        if(cloudlet != Cloudlet.NULL)
            return cloudlet;

        cloudlet = changeStatusOfCloudletIntoList(
                getCloudletPausedList(), cloudletId, 
                c -> changeStatusOfCloudlet(c, Status.PAUSED, Status.CANCELED));
        if(cloudlet != Cloudlet.NULL)
            return cloudlet;

        cloudlet = changeStatusOfCloudletIntoList(
                getCloudletWaitingList(), cloudletId, 
                c -> changeStatusOfCloudlet(c, Status.READY, Status.CANCELED));
        if(cloudlet != Cloudlet.NULL)
            return cloudlet;

        return null;
    }
    
    /**
     * Changes the status of a given cloudlet.
     * 
     * @param cloudlet
     * @param currentStatus
     * @param statusToSet 
     * 
     * @todo @author manoelcampos The parameter currentStatus only exists because apparently,
     * the cloudlet status is not being accordingly changed along
     * the simulation run.
     */
    private void changeStatusOfCloudlet(CloudletExecutionInfo cloudlet, Status currentStatus, Status statusToSet){
        if((currentStatus == Status.INEXEC || currentStatus == Status.READY) && cloudlet.getCloudlet().isFinished())
            cloudletFinish(cloudlet);
        else cloudlet.setCloudletStatus(statusToSet);

        switch(statusToSet){
            case PAUSED: getCloudletPausedList().add(cloudlet); break;
        }
    }
    
    /**
     * Search for a cloudlet into a given list in order to change its status.
     * @param cloudletList the list where to search the cloudlet
     * @param cloudletId the id of the cloudlet to have its status changed
     * @param consumer The consumer that will apply the change in the 
     * status of the found cloudlet
     * @return the changed cloudlet or {@link Cloudlet#NULL} if not found
     * in the given list
     */
    private Cloudlet changeStatusOfCloudletIntoList(
            List<CloudletExecutionInfo> cloudletList, int cloudletId,
            Consumer<CloudletExecutionInfo> consumer) {
        for (int i = 0; i < cloudletList.size(); i++) {
            CloudletExecutionInfo rcl = cloudletList.get(i);
            if (rcl.getCloudletId() == cloudletId) {
                cloudletList.remove(rcl);
                if(consumer != null){
                    consumer.accept(rcl);
                }
                return rcl.getCloudlet();
            }
        }
        
        return Cloudlet.NULL;
    }      
    
    /**
     * Updates the processing of all cloudlets 
     * of the Vm using this scheduler that are in the 
     * {@link #getCloudletExecList() cloudlet execution list}.
     * @param currentTime current simulation time
     */
    private void updateCloudletsProcessing(double currentTime) {
        getCloudletExecList().forEach(rcl -> updateCloudletProcessing(rcl, currentTime));
    }
    
    @Override
    public void updateCloudletProcessing(CloudletExecutionInfo rcl, double currentTime) {
        long numberOfInstructions = cloudletExecutionTotalLengthForElapsedTime(rcl, currentTime);
        rcl.updateCloudletFinishedSoFar(numberOfInstructions);
        
        Cloudlet cloudlet = rcl.getCloudlet();
        VmToCloudletEventInfo evt = new VmToCloudletEventInfo(currentTime, vm, cloudlet);
        cloudlet.getOnUpdateCloudletProcessingListener().update(evt);
    }

    /**
     * Computes the total length of a given cloudlet (across all PEs), 
     * in number of Instructions (I), that has been executed since the 
     * last time cloudlet processing was updated.
     * This length is considered as the sum of executed length in each Cloudlet PE.
     * 
     * @param rcl
     * @param currentTime current simulation time
     * @return the total length across all PEs, in number of Instructions (I),
     * since the last time cloudlet was processed.
     * 
     * @see #updateCloudletsProcessing(double, java.util.List) 
     * 
     * @todo @author manoelcampos Shouldn't the processing update of a cloudlet 
     * consider the cloudlet's UtilizationModel of CPU?
     * Usually the utilization model used is the UtilizationModelFull,
     * that uses the CPU 100% all the available time.
     * However, if were have an utilization model that uses just 10% of
     * CPU, the cloudlet will last 10 times more to finish.
     * It has to be checked how the MigrationExample1 works,
     * once it uses the UtilizationModelArithmeticProgression
     * instead of the UtilizationModelFull.
     */
    protected long cloudletExecutionTotalLengthForElapsedTime(CloudletExecutionInfo rcl, double currentTime) {
        return (long)(processor.getAvailableMipsByPe() * rcl.getNumberOfPes() * timeSpan(currentTime) * Consts.MILLION);
    }

    /**
     * Computes the time span between the current simulation time
     * and the last time the scheduler updated the processing
     * of it's managed cloudlets.
     * The method manages to correct precision issues
     * of double values math operations.
     * @param currentTime the current simulation time
     * @return 
     */
    protected double timeSpan(double currentTime) {
        return Math.floor(currentTime) - Math.floor(getPreviousTime());
    }

     /**
     * Gets a {@link Processor} object from a list of MIPS capacity available 
     * for the scheduler.
     *
     * @param mipsShare list with MIPS share of each PE available to the scheduler
     * @return a {@link Processor} object
     */
    /*protected Processor getProcessor(List<Double> mipsShare) {
        return Processor.fromMipsList(mipsShare);
    }*/ 
    
    /**
     * Removes finished cloudlets from the 
     * {@link #getCloudletExecList() list of cloudlets to execute}.
     * 
     * @return the number of finished cloudlets removed from the 
     * {@link #getCloudletExecList() execution list}
     */
    protected int removeFinishedCloudletsFromExecutionList() {
        List<CloudletExecutionInfo> toRemove = new ArrayList<>();
        getCloudletExecList().forEach(rcl -> {
            if(checkFinishedCloudlet(rcl))
                toRemove.add(rcl);
        });
        getCloudletExecList().removeAll(toRemove);
        
        return toRemove.size();
    }    

    /**
     * Checks if a specific finished cloudlet from the 
     * {@link #getCloudletExecList() list of cloudlets to execute}
     * has finished.
     * 
     * @param rcl the cloudlet to check if has finished
     * @return true if the cloudlet has finished, false otherwise
     */
    protected boolean checkFinishedCloudlet(CloudletExecutionInfo rcl) {
        if (rcl.getCloudlet().isFinished()) {
            rcl.setFinishTime(CloudSim.clock());
            cloudletFinish(rcl);
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets the estimated finish time of the cloudlet that is expected to 
     * finish executing sooner than all other ones that are executing
     * on the VM using this scheduler.
     * 
     * @param currentTime
     * @return 
     */
    protected double getEstimatedFinishTimeOfSoonerFinishingCloudlet(double currentTime) {
        double nextEvent = Double.MAX_VALUE;
        for (CloudletExecutionInfo rcl : getCloudletExecList()) {
            double estimatedFinishTime = 
                    getEstimatedFinishTimeOfCloudlet(rcl, currentTime);

            if (estimatedFinishTime < nextEvent) {
                nextEvent = estimatedFinishTime;
            }
        }
        return nextEvent;
    }
    
    /**
     * Gets the estimated time when a given cloudlet is supposed to finish executing.
     * @param rcl
     * @param currentTime
     * @return 
     */
    protected double getEstimatedFinishTimeOfCloudlet(CloudletExecutionInfo rcl, double currentTime) {
        double estimatedFinishTime = currentTime
                + (rcl.getRemainingCloudletLength() / 
                (processor.getAvailableMipsByPe() * rcl.getNumberOfPes()));
        if (estimatedFinishTime - currentTime < CloudSim.getMinTimeBetweenEvents()) {
            estimatedFinishTime = currentTime + CloudSim.getMinTimeBetweenEvents();
        }
        return estimatedFinishTime;
    }

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        setCurrentMipsShare(mipsShare);
        // no more cloudlets in this scheduler
        if (getCloudletExecList().isEmpty() && getCloudletWaitingList().isEmpty()) {
            setPreviousTime(currentTime);
            return Double.MAX_VALUE;
        }

        updateCloudletsProcessing(currentTime);

        final int finished = removeFinishedCloudletsFromExecutionList();
        startNewCloudletsFromWaitingList(finished);

        double nextEvent = getEstimatedFinishTimeOfSoonerFinishingCloudlet(currentTime);
        setPreviousTime(currentTime);
        return nextEvent;
    }

    private void startNewCloudletsFromWaitingList(int numberOfFinishedCloudlets) {
        // for each finished cloudlet, add a new one from the waiting list
        if (!getCloudletWaitingList().isEmpty()) {
            for (int i = 0; i < numberOfFinishedCloudlets; i++) {
                List<CloudletExecutionInfo> toRemove = new ArrayList<>();
                for (CloudletExecutionInfo rcl : getCloudletWaitingList()) {
                    if ((processor.getNumberOfPes() - usedPes) >= rcl.getNumberOfPes()) {
                        rcl.setCloudletStatus(Cloudlet.Status.INEXEC);
                        getCloudletExecList().add(rcl);
                        usedPes += rcl.getNumberOfPes();
                        toRemove.add(rcl);
                        break;
                    }
                }
                getCloudletWaitingList().removeAll(toRemove);
            }
        }
    }    

    /**
     * Processor object created every time the processing of VMs is executed.
     * It represent the last CPU capacity assigned to the scheduler.
     * @return 
     * @see #updateVmProcessing(double, java.util.List) 
     */
    protected Processor getProcessor() {
        return processor;
    }

    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public void setVm(Vm vm) {
        this.vm = vm;
    }
}
