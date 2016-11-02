/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.Cloudlet.Status;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.listeners.VmToCloudletEventInfo;
import org.cloudbus.cloudsim.resources.Pe;
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
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public abstract class CloudletSchedulerAbstract implements CloudletScheduler {
    /** @see #getProcessor()  */
    private Processor processor;

    /**
     * @see #getUsedPes()
     */
    private int usedPes;

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
    private final List<CloudletExecutionInfo> cloudletWaitingList;

    /**
     * @see #getCloudletPausedList()
     */
    private final List<CloudletExecutionInfo> cloudletPausedList;

    /**
     * @see #getCloudletFinishedList()
     */
    private final List<CloudletExecutionInfo> cloudletFinishedList;

    /**
     * @see #getCloudletFailedList()
     */
    private final List<CloudletExecutionInfo> cloudletFailedList;

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
        processor = new Processor();
        usedPes = 0;
	    vm = Vm.NULL;
        cloudletWaitingList  = new ArrayList<>();
        cloudletPausedList   = new ArrayList<>();
        cloudletFinishedList = new ArrayList<>();
        cloudletFailedList   = new ArrayList<>();
        currentMipsShare     = new ArrayList<>();
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

    @Override
    public List<CloudletExecutionInfo> getCloudletWaitingList() {
        return cloudletWaitingList;
    }

    @Override
    public List<CloudletExecutionInfo> getCloudletPausedList() {
        return cloudletPausedList;
    }

    @Override
    public List<CloudletExecutionInfo> getCloudletFinishedList() {
        return cloudletFinishedList;
    }

    @Override
    public List<CloudletExecutionInfo> getCloudletFailedList() {
        return cloudletFailedList;
    }

    @Override
    public double cloudletSubmit(Cloudlet cloudlet) {
        return cloudletSubmit(cloudlet, 0.0);
    }

	@Override
	public double cloudletSubmit(Cloudlet cl, double fileTransferTime) {
		CloudletExecutionInfo rcl = new CloudletExecutionInfo(cl);
		if(canAddCloudletToExecutionList(rcl)){
			rcl.setCloudletStatus(Cloudlet.Status.INEXEC);
			rcl.setFileTransferTime(fileTransferTime);
			rcl.setLastProcessingTime(CloudSim.clock());
			addCloudletToExecList(rcl);
			return fileTransferTime + (cl.getCloudletLength() / getProcessor().getCapacity());
		}

		// No enough free PEs, then add Cloudlet to the waiting queue
		moveCloudletToWaitingList(rcl);
		return 0.0;
	}
    
    /**
     * Adds a Cloudlet to the collection of cloudlets in execution.
     *
     * @param cloudlet the Cloudlet to be added
     */
    protected abstract void addCloudletToExecList(CloudletExecutionInfo cloudlet);
    

	/**
	 * Moves a paused cloudlet to the waiting list.
	 *
	 * @param c the cloudlet to be moved
	 */
	protected void moveCloudletToWaitingList(CloudletExecutionInfo c) {
		c.setCloudletStatus(Cloudlet.Status.QUEUED);
		getCloudletWaitingList().add(c);
	}

	@Override
    public double getTotalUtilizationOfCpu(double time) {
        return getCloudletExecList().stream()
                .mapToDouble(rcl -> rcl.getCloudlet().getUtilizationOfCpu(time))
                .sum();
    }

    @Override
    public boolean hasFinishedCloudlets() {
        return !getCloudletFinishedList().isEmpty();
    }

    @Override
    public int runningCloudletsNumber() {
        return getCloudletExecList().size();
    }

    @Override
    public Cloudlet getNextFinishedCloudlet() {
        if (getCloudletFinishedList().isEmpty()) {
	        return Cloudlet.NULL;
        }

	    return getCloudletFinishedList().remove(0).getCloudlet();
    }

    /**
     * Returns the first cloudlet in the execution list to migrate to another VM,
     * removing it from the list.
     *
     * @return the first executing cloudlet or {@link Cloudlet#NULL} if the executing list is empty
     * @pre $none
     * @post $none
     */
    @Override
    public Cloudlet getCloudletToMigrate() {
        Function<CloudletExecutionInfo, Cloudlet> finishMigratingCloudlet = rcl -> {
            removeCloudletFromExecListAndSetFinishTime(rcl);
            rcl.finalizeCloudlet();
            return rcl.getCloudlet();
        };
        

        return getCloudletExecList().stream()
                .findFirst()
                .map(finishMigratingCloudlet).orElse(Cloudlet.NULL);
    }

    @Override
    public int getCloudletStatus(int cloudletId) {
	    Optional<CloudletExecutionInfo> optional = findCloudletInAllLists(cloudletId);
        return optional.map(c->c.getCloudletStatus().ordinal()).orElse(-1);
    }

	/**
	 * Search for a Cloudlet into all Cloudlet lists.
	 *
	 * @param cloudletId the id of the Cloudlet to search for
	 * @return an {@link Optional} value that is able to indicate if the Cloudlet was found or not
	 */
	protected Optional<CloudletExecutionInfo> findCloudletInAllLists(double cloudletId) {
		Collection[] allLists =
			new Collection[]{getCloudletExecList(), getCloudletPausedList(), getCloudletWaitingList(),
				getCloudletFinishedList(), getCloudletFailedList()};

        for(Collection list: allLists){
			Optional<CloudletExecutionInfo> optional = findCloudletInList(cloudletId, list);
			if(optional.isPresent())
				return optional;
		}

		return Optional.empty();
	}

	/**
	 * Search for a Cloudlet into a given list.
	 *
	 * @param cloudletId the id of the Cloudlet to search for
	 * @param list the list to search the Cloudlet into
	 * @return an {@link Optional} value that is able to indicate if the Cloudlet was found or not
	 */
	protected Optional<CloudletExecutionInfo> findCloudletInList(double cloudletId, Collection<CloudletExecutionInfo> list) {
		return list.stream()
			.filter(rcl -> rcl.getCloudletId() == cloudletId)
			.findFirst();
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
     * Search for a cloudlet into a given list in order to change its status
     * and remove it from that list.
     *
     * @param cloudletList the list where to search the cloudlet
     * @param cloudletId the id of the cloudlet to have its status changed
     * @param cloudletStatusUpdaterConsumer the {@link Consumer} that will apply the change in the
     * status of the found cloudlet
     * @return the changed cloudlet or {@link Cloudlet#NULL} if not found
     * in the given list
     */
    private Cloudlet changeStatusOfCloudletIntoList(
	    Collection<CloudletExecutionInfo> cloudletList, int cloudletId,
	    Consumer<CloudletExecutionInfo> cloudletStatusUpdaterConsumer)
    {
        Function<CloudletExecutionInfo, Cloudlet> removeCloudletFromListAndUpdateItsStatus = c -> {
            cloudletList.remove(c);
            cloudletStatusUpdaterConsumer.accept(c);
            return c.getCloudlet();
        };

	    return cloudletList.stream()
		    .filter(c->c.getCloudlet().getId() == cloudletId)
		    .findFirst()
            .map(removeCloudletFromListAndUpdateItsStatus)
            .orElse(Cloudlet.NULL);
    }
    
    /**
     * Selects the next Cloudlets to start executing and adds them to the execution list.
     * The method might move the currently executing Cloudlets
     * to the waiting list in order to give other Cloudlets
     * the opportunity to use the PEs, what is called <a href="https://en.wikipedia.org/wiki/Context_switch">context switch</a>.
     * However, each CloudletScheduler implementation is in charge to decide how
     * this process is implemented. For instance, Space-Shared schedulers may just
     * perform context switch just after currently running Cloudlets completely
     * finish executing.
     * 
     * <p>This method is called internally by the {@link #updateVmProcessing(double, java.util.List)} one.</p>
     *
     * @param currentTime current simulation time
     * @param numberOfJustFinishedCloudlets the number of Cloudlets that are currently finished since
     * the last time the method was called. Accordingly, it isn't the overall amount of finished
     * Cloudlets so far, but the finished amount since the last time
     * @pre currentTime >= 0
     * @post $none
     */
    protected abstract void selectNextCloudletsToStartExecuting(double currentTime, int numberOfJustFinishedCloudlets);

	@Override
	public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
		setCurrentMipsShare(mipsShare);
		// no more cloudlets in this scheduler
		if (getCloudletExecList().isEmpty() && getCloudletWaitingList().isEmpty()) {
			setPreviousTime(currentTime);
			return Double.MAX_VALUE;
		}

		updateCloudletsProcessing(currentTime);        
		final int finishedCloudlets = removeFinishedCloudletsFromExecutionList();
        selectNextCloudletsToStartExecuting(currentTime, finishedCloudlets);

		double nextEvent = getEstimatedFinishTimeOfSoonerFinishingCloudlet(currentTime);
		setPreviousTime(currentTime);
		return nextEvent;
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
        long numberExecutedInstructions = cloudletExecutionTotalLengthForElapsedTime(rcl, currentTime);
        rcl.updateCloudletFinishedSoFar(numberExecutedInstructions);
	    if(numberExecutedInstructions > 0)
	    	rcl.setLastProcessingTime(currentTime);

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
     * <p>This method considers the delay for actually starting
     * the Cloudlet execution due to the time to transfer
     * {@link Cloudlet#getRequiredFiles() required Cloudlet files}
     * from the Datacenter storage (such as a SAN) to the Vm running
     * the Cloudlet.</p>
     *
     * <p>During this transfer time, the method will always return 0 to indicate
     * that the Cloudlet was not processed in fact, it is just waiting
     * the required files to be acquired. The required time
     * to transfer the files is stored in the {@link CloudletExecutionInfo#getFileTransferTime()}
     * attribute and is set when the Cloudlet is submitted to the scheduler.</p>
     *
     * @param rcl
     * @param currentTime current simulation time
     * @return the total length across all PEs, in number of Instructions (I),
     * since the last time cloudlet was processed.
     *
     * @see #updateCloudletsProcessing(double)
     *
     * @todo @author manoelcampos Shouldn't the processing update of a cloudlet
     * consider the cloudlet's UtilizationModel of CPU?
     * Commonly the utilization model used is the UtilizationModelFull,
     * that uses the CPU 100% all the available time.
     * However, if were have an utilization model that uses just 10% of
     * CPU, the cloudlet will last 10 times more to finish.
     * It has to be checked how the MigrationExample1 works,
     * once it uses the UtilizationModelArithmeticProgression
     * instead of the UtilizationModelFull.
     *
     * @todo @author manoelcampos This method is being called 2 times more
     * than required. Despite it is not causing any apparent issue, it has
     * to be investigated. For instance, for simulation time 2, with 2 cloudlets,
     * the method is being called 4 times instead of just 2 (1 for each cloudlet
     * for that time).
     */
    protected long cloudletExecutionTotalLengthForElapsedTime(CloudletExecutionInfo rcl, double currentTime) {
	    /* The time the Cloudlet spent executing in fact, since the last time Cloudlet update was
	     * called by the scheduler. If it is zero, indicates that the Cloudlet didn't use
	     * the CPU in this time span, because it is waiting for its required files
	     * to be achired from the Datacenter storage.
	     * */
	    double actualProcessingTime = 0;
	    if(hasCloudletFileTransferTimePassed(rcl, currentTime))
	        actualProcessingTime = timeSpan(currentTime);

	    double executedInstructions =
		    (processor.getAvailableMipsByPe() * rcl.getNumberOfPes() * 
            actualProcessingTime * Consts.MILLION);
	    Log.println(Log.Level.DEBUG, getClass(), currentTime,
		    "Cloudlet: %d Processing time: %.2f Last processed time: %.2f Actual process time: %.2f MI so far: %d",
		    rcl.getCloudletId(), currentTime, rcl.getLastProcessingTime(), 
            actualProcessingTime, rcl.getCloudlet().getCloudletFinishedSoFar());

	    return (long)executedInstructions;
    }

	/**
	 * Checks if the time to transfer the files required by a Cloudlet to execute
	 * has already passed, in order to start executing the Cloudlet in fact.
	 *
	 * @param rcl Cloudlet to check if the time to transfer the files has passed
	 * @param currentTime the current simulation time
	 * @return true if the time to transfer the files has passed, false otherwise
	 */
	private boolean hasCloudletFileTransferTimePassed(CloudletExecutionInfo rcl, double currentTime) {
		return rcl.getFileTransferTime() == 0 ||
               currentTime - rcl.getLastProcessingTime() > rcl.getFileTransferTime() || 
               rcl.getCloudlet().getCloudletFinishedSoFar() > 0;
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
     * Removes finished cloudlets from the
     * {@link #getCloudletExecList() list of cloudlets to execute}.
     *
     * @return the number of finished cloudlets removed from the
     * {@link #getCloudletExecList() execution list}
     */
    protected int removeFinishedCloudletsFromExecutionList() {
        List<CloudletExecutionInfo> finishedCloudlets =
	        getCloudletExecList().stream()
		        .filter(c->c.getCloudlet().isFinished())
		        .collect(Collectors.toList());

        for(CloudletExecutionInfo c: finishedCloudlets){
            removeCloudletFromExecListAndSetFinishTime(c);
        }

        return finishedCloudlets.size();
    }

    /**
     * Removes a Cloudlet from the list of cloudlets in execution
     * and sets its finish time.
     *
     * @param cloudlet the Cloudlet to be removed
     */
    protected void removeCloudletFromExecListAndSetFinishTime(CloudletExecutionInfo cloudlet) {
        setCloudletFinishTime(cloudlet);
        removeCloudletFromExecList(cloudlet);
    }

    /**
     * Removes a Cloudlet from the list of cloudlets in execution.
     *
     * @param cloudlet the Cloudlet to be removed
     * @return true if the Cloudlet was found and remove from the execution list. 
     */
    protected abstract boolean removeCloudletFromExecList(CloudletExecutionInfo cloudlet);

    /**
     * Sets the finish time of a cloudlet.
     *
     * @param rcl the cloudlet to set the finish time
     */
    protected void setCloudletFinishTime(CloudletExecutionInfo rcl) {
	    final double clock = CloudSim.clock();
        //Log.println(Log.Level.INFO, getClass(), clock, "Start Time: %f Transfer Time: %f", rcl.getExecStartTime(), rcl.getFileTransferTime());
        rcl.setFinishTime(clock);
        cloudletFinish(rcl);
    }

    /**
     * Gets the estimated finish time of the Cloudlet that is expected to
     * finish executing sooner than all other ones that are executing
     * on the VM using this scheduler.
     *
     * @param currentTime current simulation time
     * @return the estimated finish time of sooner finishing cloudlet
     */
    protected double getEstimatedFinishTimeOfSoonerFinishingCloudlet(double currentTime) {
        return getCloudletExecList()
                .stream()
                .mapToDouble(c->getEstimatedFinishTimeOfCloudlet(c, currentTime))
                .min().orElse(Double.MAX_VALUE);
    }

    /**
     * Gets the estimated time when a given cloudlet is supposed to finish executing.
     * It considers the amount of Vm PES and the sum of PEs required by all VMs running
     * inside the VM.
     *
     * @param rcl cloudlet to get the estimated finish time
     * @param currentTime current simulation time
     * @return the estimated finish time of the given cloudlet
     */
    protected double getEstimatedFinishTimeOfCloudlet(CloudletExecutionInfo rcl, double currentTime) {
        double estimatedFinishTime = currentTime +
                (rcl.getRemainingCloudletLength() /
                (processor.getAvailableMipsByPe() * rcl.getNumberOfPes()));

        if (estimatedFinishTime - currentTime < CloudSim.getMinTimeBetweenEvents()) {
            estimatedFinishTime = currentTime + CloudSim.getMinTimeBetweenEvents();
        }


	    return estimatedFinishTime;
        //return (rcl.getRemainingCloudletLength() == 0 ? estimatedFinishTime+rcl.getFileTransferTime() : estimatedFinishTime);
    }


	/**
	 * For each finished cloudlet, try to add a new one from the waiting list
	 * to the execution list.
	 *
	 * @param numberOfFinishedCloudlets number of Cloudlets that have just finished.
	 */
    protected void startNewCloudletsFromWaitingList(int numberOfFinishedCloudlets) {
        if (getCloudletWaitingList().isEmpty()) {
	        return;
        }

        Consumer<CloudletExecutionInfo> addCloudletToExecutionList = c -> {
            addCloudletToExecList(c);
            addUsedPes(c.getNumberOfPes());
            getCloudletWaitingList().remove(c);
        };

        for(int i = 0; i < numberOfFinishedCloudlets; i++){
            findSuitableWaitingCloudletToStartExecuting().ifPresent(addCloudletToExecutionList);
        }
    }

    /**
     * Checks if the amount of PEs required by a given Cloudlet is free
     * to used.
     *
     * @param c the Cloudlet to get the number of required PEs
     * @return true if there is the amount of free PEs, false otherwise
     */
    protected boolean isThereEnoughFreePesForCloudlet(CloudletExecutionInfo c){
        return processor.getNumberOfPes() - usedPes >= c.getNumberOfPes();
    }

	/**
	 * Try to find the first Cloudlet in the waiting list that the number of
	 * required PEs is not higher than the number of free PEs.
     * If a Cloudlet is found, sets its status to {@link Status#INEXEC}
     * and returns it.
	 *
	 * @return an {@link Optional} that indicates if a Cloudlet with the required
	 * conditions was found or not
	 */
	private Optional<CloudletExecutionInfo> findSuitableWaitingCloudletToStartExecuting() {
        //Receives the cloudlet, change its status and return the changed cloudlet.
        Function<CloudletExecutionInfo, CloudletExecutionInfo> changeCloudletStatusToExecAndReturnIt = c -> {
            c.setCloudletStatus(Status.INEXEC);
            return c;
        };

        return getCloudletWaitingList().stream()
            .filter(this::isThereEnoughFreePesForCloudlet)
            .findFirst()
            .map(changeCloudletStatusToExecAndReturnIt);
	}

	/**
     * Processor object created every time the processing of VMs is executed.
     * It represent the last CPU capacity assigned to the scheduler.
     *
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

    /**
     * Gets the number of currently used {@link Pe}'s.
     */
    public int getUsedPes() {
        return usedPes;
    }

    /**
     * Adds a given number of PEs to the amount of currently used PEs.
     *
     * @param usedPesToAdd number of PEs to add to the amount of used PEs
     */
    protected void addUsedPes(int usedPesToAdd) {
        this.usedPes += usedPesToAdd;
    }

    /**
     * Subtracts a given number of PEs from the amount of currently used PEs.
     *
     * @param usedPesToRemove number of PEs to subtract from the amount of used PEs
     */
    protected void removeUsedPes(int usedPesToRemove) {
        this.usedPes -= usedPesToRemove;
    }

}
