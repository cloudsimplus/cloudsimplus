/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.Cloudlet.Status;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.util.Consts;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.VmToCloudletEventInfo;
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

    /**
     * @see #getProcessor()
     */
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
     * @see #getCloudletExecList()
     */
    private List<CloudletExecutionInfo> cloudletExecList;

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
     * @see #getCloudletWaitingList()
     */
    private List<CloudletExecutionInfo> cloudletWaitingList;

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
        cloudletExecList = new ArrayList<>();
        cloudletPausedList = new ArrayList<>();
        cloudletFinishedList = new ArrayList<>();
        cloudletFailedList = new ArrayList<>();
        cloudletWaitingList = new ArrayList<>();
        currentMipsShare = new ArrayList<>();
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

    /**
     * Gets a List of cloudlets being executed on the VM.
     *
     * @return the cloudlet execution list
     * @see #addCloudletToExecList(CloudletExecutionInfo)
     * @see #removeCloudletFromExecListAndAddToFinishedList(CloudletExecutionInfo)
     */
    protected List<CloudletExecutionInfo> getCloudletExecList() {
        return cloudletExecList;
    }

    protected final void setCloudletWaitingList(List<CloudletExecutionInfo> cloudletWaitingList) {
        this.cloudletWaitingList = cloudletWaitingList;
    }

    protected final void setCloudletExecList(List<CloudletExecutionInfo> cloudletExecList) {
        this.cloudletExecList = cloudletExecList;
    }

    protected void addCloudletToWaitingList(CloudletExecutionInfo cloudlet) {
        cloudlet.setCloudletStatus(Cloudlet.Status.QUEUED);
        cloudletWaitingList.add(cloudlet);
    }

    protected boolean removeCloudletFromWaitingList(CloudletExecutionInfo cloudlet) {
        return cloudletWaitingList.remove(cloudlet);
    }

    /**
     * Gets the list of paused cloudlets.
     *
     * @return the cloudlet paused list
     */
    protected List<CloudletExecutionInfo> getCloudletPausedList() {
        return cloudletPausedList;
    }

    @Override
    public List<CloudletExecutionInfo> getCloudletFinishedList() {
        return cloudletFinishedList;
    }

    protected void addCloudletToFinishedList(CloudletExecutionInfo cloudlet) {
        cloudletFinishedList.add(cloudlet);
    }

    /**
     * Gets the list of failed cloudlets.
     *
     * @return the cloudlet failed list.
     */
    protected List<CloudletExecutionInfo> getCloudletFailedList() {
        return cloudletFailedList;
    }

    /**
     * Gets a List of cloudlet waiting to be executed on the VM.
     *
     * @return the cloudlet waiting list
     */
    protected List<CloudletExecutionInfo> getCloudletWaitingList() {
        return cloudletWaitingList;
    }

    @Override
    public double cloudletSubmit(Cloudlet cloudlet) {
        return cloudletSubmit(cloudlet, 0.0);
    }

    @Override
    public double cloudletSubmit(Cloudlet cl, double fileTransferTime) {
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(cl);
        return processCloudletSubmit(rcl, fileTransferTime);
    }

    /**
     * Process a Cloudlet after it is received by the
     * {@link #cloudletSubmit(Cloudlet, double)} method, that creates a
     * {@link CloudletExecutionInfo} object to encapsulate the submitted
     * Cloudlet and record execution information.
     *
     * @param rcl the CloudletExecutionInfo that encapsulates the Cloudlet
     * object
     * @param fileTransferTime time required to move the required files from the
     * SAN to the VM
     * @return expected finish time of this cloudlet (considering the time to
     * transfer required files from the Datacenter to the Vm), or 0 if it is in
     * a waiting queue
     */
    protected double processCloudletSubmit(CloudletExecutionInfo rcl, double fileTransferTime) {
        if (canAddCloudletToExecutionList(rcl)) {
            rcl.setCloudletStatus(Status.INEXEC);
            rcl.setFileTransferTime(fileTransferTime);
            addCloudletToExecList(rcl);
            return fileTransferTime + (rcl.getCloudletLength() / getProcessor().getCapacity());
        }

        // No enough free PEs, then add Cloudlet to the waiting queue
        addCloudletToWaitingList(rcl);
        return 0.0;
    }

    /**
     * Adds a Cloudlet to the list of cloudlets in execution.
     *
     * @param cloudlet the Cloudlet to be added
     */
    protected void addCloudletToExecList(CloudletExecutionInfo cloudlet) {
        cloudlet.setCloudletStatus(Cloudlet.Status.INEXEC);
        cloudlet.setLastProcessingTime(getVm().getSimulation().clock());
        cloudletExecList.add(cloudlet);
        addUsedPes(cloudlet.getCloudlet().getNumberOfPes());
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
    public Cloudlet removeNextFinishedCloudlet() {
        if (getCloudletFinishedList().isEmpty()) {
            return Cloudlet.NULL;
        }

        return getCloudletFinishedList().remove(0).getCloudlet();
    }

    /**
     * Returns the first cloudlet in the execution list to migrate to another
     * VM, removing it from the list.
     *
     * @return the first executing cloudlet or {@link Cloudlet#NULL} if the
     * executing list is empty
     * @pre $none
     * @post $none
     */
    @Override
    public Cloudlet getCloudletToMigrate() {
        Function<CloudletExecutionInfo, Cloudlet> finishMigratingCloudlet = rcl -> {
            removeCloudletFromExecListAndAddToFinishedList(rcl);
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
        return optional
                    .map(CloudletExecutionInfo::getCloudletStatus)
                    .map(Status::ordinal)
                    .orElse(-1);
    }

    /**
     * Search for a Cloudlet into all Cloudlet lists.
     *
     * @param cloudletId the id of the Cloudlet to search for
     * @return an {@link Optional} value that is able to indicate if the
     * Cloudlet was found or not
     */
    protected Optional<CloudletExecutionInfo> findCloudletInAllLists(double cloudletId) {
        //Concatenate all lists into a strem
        Stream<List<CloudletExecutionInfo>> streamOfAllLists
                = Stream.of(getCloudletExecList(), getCloudletPausedList(), getCloudletWaitingList(),
                        getCloudletFinishedList(), getCloudletFailedList());

        //Gets all elements in each list and makes them a single full list,
        //returning the first Cloudlet with the given id
        return streamOfAllLists
                .flatMap(List::stream)
                .filter(c -> c.getCloudletId() == cloudletId)
                .findFirst();
    }

    /**
     * Search for a Cloudlet into a given list.
     *
     * @param cloudletId the id of the Cloudlet to search for
     * @param list the list to search the Cloudlet into
     * @return an {@link Optional} value that is able to indicate if the
     * Cloudlet was found or not
     */
    protected Optional<CloudletExecutionInfo> findCloudletInList(double cloudletId, List<CloudletExecutionInfo> list) {
        return list.stream()
                .filter(rcl -> rcl.getCloudletId() == cloudletId)
                .findFirst();
    }

    @Override
    public void cloudletFinish(CloudletExecutionInfo rcl) {
        rcl.setCloudletStatus(Cloudlet.Status.SUCCESS);
        rcl.finalizeCloudlet();
        addCloudletToFinishedList(rcl);
    }

    @Override
    public boolean cloudletPause(int cloudletId) {
        if (changeStatusOfCloudletIntoList(
                getCloudletExecList(), cloudletId,
                c -> changeStatusOfCloudlet(c, Status.INEXEC, Status.PAUSED)) != Cloudlet.NULL) {
            return true;
        }

        return changeStatusOfCloudletIntoList(
            getCloudletWaitingList(), cloudletId,
            c -> changeStatusOfCloudlet(c, Status.READY, Status.PAUSED)) != Cloudlet.NULL;

    }

    @Override
    public Cloudlet cloudletCancel(int cloudletId) {
        Cloudlet cloudlet;
        cloudlet = changeStatusOfCloudletIntoList(getCloudletFinishedList(), cloudletId, (c) -> {});
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        cloudlet = changeStatusOfCloudletIntoList(
                getCloudletExecList(), cloudletId,
                c -> changeStatusOfCloudlet(c, Status.INEXEC, Status.CANCELED));
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        cloudlet = changeStatusOfCloudletIntoList(
                getCloudletPausedList(), cloudletId,
                c -> changeStatusOfCloudlet(c, Status.PAUSED, Status.CANCELED));
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        cloudlet = changeStatusOfCloudletIntoList(
                getCloudletWaitingList(), cloudletId,
                c -> changeStatusOfCloudlet(c, Status.READY, Status.CANCELED));
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        return Cloudlet.NULL;
    }

    /**
     * Changes the status of a given cloudlet.
     *
     * @param cloudlet Cloudlet to set its status
     * @param currentStatus the current cloudlet status
     * @param newStatus the new status to set
     *
     * @todo @author manoelcampos The parameter currentStatus only exists
     * because apparently, the cloudlet status is not being accordingly changed
     * along the simulation run.
     */
    private void changeStatusOfCloudlet(CloudletExecutionInfo cloudlet, Status currentStatus, Status newStatus) {
        if ((currentStatus == Status.INEXEC || currentStatus == Status.READY) && cloudlet.getCloudlet().isFinished()) {
            cloudletFinish(cloudlet);
        } else {
            cloudlet.setCloudletStatus(newStatus);
        }

        switch (newStatus) {
            case PAUSED:
                getCloudletPausedList().add(cloudlet);
                break;
        }
    }

    /**
     * Search for a cloudlet into a given list in order to change its status and
     * remove it from that list.
     *
     * @param cloudletList the list where to search the cloudlet
     * @param cloudletId the id of the cloudlet to have its status changed
     * @param cloudletStatusUpdaterConsumer the {@link Consumer} that will apply
     * the change in the status of the found cloudlet
     * @return the changed cloudlet or {@link Cloudlet#NULL} if not found in the
     * given list
     */
    private Cloudlet changeStatusOfCloudletIntoList(
            List<CloudletExecutionInfo> cloudletList, int cloudletId,
            Consumer<CloudletExecutionInfo> cloudletStatusUpdaterConsumer) {
        Function<CloudletExecutionInfo, Cloudlet> removeCloudletFromListAndUpdateItsStatus = c -> {
            cloudletList.remove(c);
            cloudletStatusUpdaterConsumer.accept(c);
            return c.getCloudlet();
        };

        return cloudletList.stream()
                .filter(c -> c.getCloudlet().getId() == cloudletId)
                .findFirst()
                .map(removeCloudletFromListAndUpdateItsStatus)
                .orElse(Cloudlet.NULL);
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
        removeFinishedCloudletsFromExecutionListAndAddToFinishedList();
        moveNextCloudletsFromWaitingToExecList();

        double nextEvent = getEstimatedFinishTimeOfSoonerFinishingCloudlet(currentTime);
        setPreviousTime(currentTime);
        return nextEvent;
    }

    /**
     * Updates the processing of all cloudlets of the Vm using this scheduler
     * that are in the {@link #getCloudletExecList() cloudlet execution list}.
     *
     * @param currentTime current simulation time
     */
    private void updateCloudletsProcessing(double currentTime) {
        getCloudletExecList().forEach(rcl -> updateCloudletProcessing(rcl, currentTime));
    }

    /**
     * Updates the processing of a specific cloudlet of the Vm using this
     * scheduler.
     *
     * @param rcl The cloudlet to be its processing updated
     * @param currentTime current simulation time
     *
     */
    protected void updateCloudletProcessing(CloudletExecutionInfo rcl, double currentTime) {
        long numberExecutedInstructions = cloudletExecutionTotalLengthForElapsedTime(rcl, currentTime);
        rcl.updateCloudletFinishedSoFar(numberExecutedInstructions);
        if (numberExecutedInstructions > 0) {
            rcl.setLastProcessingTime(currentTime);
        }

        Cloudlet cloudlet = rcl.getCloudlet();
        VmToCloudletEventInfo evt = new VmToCloudletEventInfo(currentTime, vm, cloudlet);
        cloudlet.getOnUpdateCloudletProcessingListener().update(evt);
    }

    /**
     * Computes the total length of a given cloudlet (across all PEs), in number
     * of Instructions (I), that has been executed since the last time cloudlet
     * processing was updated. This length is considered as the sum of executed
     * length in each Cloudlet PE.
     *
     * <p>
     * This method considers the delay for actually starting the Cloudlet
     * execution due to the time to transfer
     * {@link Cloudlet#getRequiredFiles() required Cloudlet files} from the
     * Datacenter storage (such as a SAN) to the Vm running the Cloudlet.</p>
     *
     * <p>
     * During this transfer time, the method will always return 0 to indicate
     * that the Cloudlet was not processed in fact, it is just waiting the
     * required files to be acquired. The required time to transfer the files is
     * stored in the {@link CloudletExecutionInfo#getFileTransferTime()}
     * attribute and is set when the Cloudlet is submitted to the scheduler.</p>
     *
     * @param rcl the Cloudlet to compute the total length
     * @param currentTime current simulation time
     * @return the total length across all PEs, in number of Instructions (I),
     * since the last time cloudlet was processed.
     *
     * @see #updateCloudletsProcessing(double)
     *
     * @TODO @author manoelcampos Shouldn't the processing update of a cloudlet
     * consider the cloudlet's UtilizationModel of CPU? Commonly the utilization
     * model used is the UtilizationModelFull, that uses the CPU 100% all the
     * available time. However, if we have an utilization model that uses just
     * 10% of CPU, the cloudlet will last 10 times more to finish. It has to be
     * checked how the MigrationExample1 works, once it uses the
     * UtilizationModelArithmeticProgression instead of the
     * UtilizationModelFull.
     *
     * @TODO @author manoelcampos This method is being called 2 times more than
     * required. Despite it is not causing any apparent issue, it has to be
     * investigated. For instance, for simulation time 2, with 2 cloudlets, the
     * method is being called 4 times instead of just 2 (1 for each cloudlet for
     * that time).
     */
    protected long cloudletExecutionTotalLengthForElapsedTime(CloudletExecutionInfo rcl, double currentTime) {
        /* The time the Cloudlet spent executing in fact, since the last time Cloudlet update was
         * called by the scheduler. If it is zero, indicates that the Cloudlet didn't use
         * the CPU in this time span, because it is waiting for its required files
         * to be achired from the Datacenter storage.
         * */
        double actualProcessingTime = 0;
        if (hasCloudletFileTransferTimePassed(rcl, currentTime)) {
            actualProcessingTime = timeSpan(currentTime);
        }

        double executedInstructions
                = (processor.getAvailableMipsByPe() * rcl.getCloudlet().getNumberOfPes()
                * actualProcessingTime * Consts.MILLION);
        //Log.println(Log.Level.DEBUG, getClass(), currentTime, "Cloudlet: %d Processing time: %.2f Last processed time: %.2f Actual process time: %.2f MI so far: %d",  rcl.getCloudletId(), currentTime, rcl.getLastProcessingTime(),  actualProcessingTime, rcl.getCloudlet().getCloudletFinishedSoFar());

        return (long) executedInstructions;
    }

    /**
     * Checks if the time to transfer the files required by a Cloudlet to
     * execute has already passed, in order to start executing the Cloudlet in
     * fact.
     *
     * @param rcl Cloudlet to check if the time to transfer the files has passed
     * @param currentTime the current simulation time
     * @return true if the time to transfer the files has passed, false
     * otherwise
     */
    private boolean hasCloudletFileTransferTimePassed(CloudletExecutionInfo rcl, double currentTime) {
        return rcl.getFileTransferTime() == 0
                || currentTime - rcl.getLastProcessingTime() > rcl.getFileTransferTime()
                || rcl.getCloudlet().getCloudletFinishedSoFar() > 0;
    }

    /**
     * Computes the time span between the current simulation time and the last
     * time the scheduler updated the processing of it's managed cloudlets. The
     * method manages to correct precision issues of double values math
     * operations.
     *
     * @param currentTime the current simulation time
     * @return
     */
    protected double timeSpan(double currentTime) {
        return Math.floor(currentTime) - Math.floor(getPreviousTime());
    }

    /**
     * Removes finished cloudlets from the
     * {@link #getCloudletExecList() list of cloudlets to execute}
     * and adds them to finished list.
     *
     * @return the number of finished cloudlets removed from the
     * {@link #getCloudletExecList() execution list}
     */
    protected int removeFinishedCloudletsFromExecutionListAndAddToFinishedList() {
        List<CloudletExecutionInfo> finishedCloudlets
                = getCloudletExecList().stream()
                .filter(c -> c.getCloudlet().isFinished())
                .collect(Collectors.toList());

        for (CloudletExecutionInfo c : finishedCloudlets) {
            removeCloudletFromExecListAndAddToFinishedList(c);
        }

        return finishedCloudlets.size();
    }

    protected void removeCloudletFromExecListAndAddToFinishedList(CloudletExecutionInfo cloudlet) {
        setCloudletFinishTimeAndAddToFinishedList(cloudlet);
        removeCloudletFromExecList(cloudlet);
    }

    /**
     * Removes a Cloudlet from the list of cloudlets in execution.
     *
     * @param cloudlet the Cloudlet to be removed
     * @return true if the Cloudlet was found and remove from the execution
     * list.
     */
    protected boolean removeCloudletFromExecList(CloudletExecutionInfo cloudlet) {
        removeUsedPes(cloudlet.getCloudlet().getNumberOfPes());
        return cloudletExecList.remove(cloudlet);
    }

    /**
     * Sets the finish time of a cloudlet and adds it to the
     * finished list.
     *
     * @param rcl the cloudlet to set the finish time
     */
    protected void setCloudletFinishTimeAndAddToFinishedList(CloudletExecutionInfo rcl) {
        final double clock = getVm().getSimulation().clock();
        rcl.setFinishTime(clock);
        cloudletFinish(rcl);
    }

    /**
     * Gets the estimated finish time of the Cloudlet that is expected to finish
     * executing sooner than all other ones that are executing on the VM using
     * this scheduler.
     *
     * @param currentTime current simulation time
     * @return the estimated finish time of sooner finishing cloudlet
     */
    protected double getEstimatedFinishTimeOfSoonerFinishingCloudlet(double currentTime) {
        return getCloudletExecList()
                .stream()
                .mapToDouble(c -> getEstimatedFinishTimeOfCloudlet(c, currentTime))
                .min().orElse(Double.MAX_VALUE);
    }

    /**
     * Gets the estimated time when a given cloudlet is supposed to finish
     * executing. It considers the amount of Vm PES and the sum of PEs required
     * by all VMs running inside the VM.
     *
     * @param rcl cloudlet to get the estimated finish time
     * @param currentTime current simulation time
     * @return the estimated finish time of the given cloudlet
     */
    protected double getEstimatedFinishTimeOfCloudlet(CloudletExecutionInfo rcl, double currentTime) {
        double estimatedFinishTime = currentTime
                + (rcl.getRemainingCloudletLength()
                / (processor.getAvailableMipsByPe() * rcl.getCloudlet().getNumberOfPes()));

        if (estimatedFinishTime - currentTime < getVm().getSimulation().getMinTimeBetweenEvents()) {
            estimatedFinishTime = currentTime + getVm().getSimulation().getMinTimeBetweenEvents();
        }

        return estimatedFinishTime;
        //return (rcl.getRemainingCloudletLength() == 0 ? estimatedFinishTime+rcl.getFileTransferTime() : estimatedFinishTime);
    }

    /**
     * /**
     * Selects the next Cloudlets in the waiting list to move to the execution
     * list in order to start executing them. While there is enough free PEs,
     * the method try to find a suitable Cloudlet in the list, until it reaches
     * the end of such a list.
     *
     * The method might also exchange some cloudlets in the execution list with
     * some in the waiting list. Thus, some running cloudlets may be preempted
     * to give opportunity to previously waiting cloudlets to run. This is a
     * process called
     * <a href="https://en.wikipedia.org/wiki/Context_switch">context
     * switch</a>. However, each CloudletScheduler implementation decides how
     * such a process is implemented. For instance, Space-Shared schedulers may
     * just perform context switch just after currently running Cloudlets
     * completely finish executing.
     *
     * <p>
     * This method is called internally by the
     * {@link #updateVmProcessing(double, java.util.List)} one.</p>
     *
     * @pre currentTime >= 0
     * @post $none
     */
    protected void moveNextCloudletsFromWaitingToExecList() {
        for (int i = 0; i < cloudletWaitingList.size() && getFreePes() > 0; i++) {
            findSuitableWaitingCloudletToStartExecutingAndRemoveIt().ifPresent(this::addCloudletToExecList);
        }
    }

    /**
     * Checks if the amount of PEs required by a given Cloudlet is free to use.
     *
     * @param c the Cloudlet to get the number of required PEs
     * @return true if there is the amount of free PEs, false otherwise
     */
    protected boolean isThereEnoughFreePesForCloudlet(CloudletExecutionInfo c) {
        return processor.getNumberOfPes() - usedPes >= c.getCloudlet().getNumberOfPes();
    }

    /**
     * Try to find the first Cloudlet in the waiting list that the number of
     * required PEs is not higher than the number of free PEs. If a Cloudlet is
     * found, sets its status to {@link Status#INEXEC} and returns it, removing
     * such Cloudlet from the waiting list.
     *
     * @return an {@link Optional} containing the found Cloudlet or an empty
     * Optional otherwise
     */
    protected Optional<CloudletExecutionInfo> findSuitableWaitingCloudletToStartExecutingAndRemoveIt() {
        //Receives the cloudlet, change its status and return the changed cloudlet.
        Function<CloudletExecutionInfo, CloudletExecutionInfo> changeCloudletStatusToExecAndReturnIt = c -> {
            c.setCloudletStatus(Status.INEXEC);
            return c;
        };

        Optional<CloudletExecutionInfo> optional = getCloudletWaitingList().stream()
                .filter(this::isThereEnoughFreePesForCloudlet)
                .findFirst()
                .map(changeCloudletStatusToExecAndReturnIt);

        optional.ifPresent(this::removeCloudletFromWaitingList);

        return optional;
    }

    /**
     * Processor object created every time the processing of VMs is executed. It
     * represent the last CPU capacity assigned to the scheduler.
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

    @Override
    public int getUsedPes() {
        /**
         *
         * @todo The number of free and used PEs should be inside the Processor
         * class. However, a new instance of the class is created every time the
         * updateVmProcessing is called, what will make the information about
         * the number of usedPes to be lost.
         */
        return usedPes;
    }

    /**
     * Gets the number of PEs currently not being used.
     *
     * @return
     */
    @Override
    public int getFreePes() {
        return getProcessor().getNumberOfPes() - getUsedPes();
    }

    /**
     * Adds a given number of PEs to the amount of currently used PEs.
     *
     * @param usedPesToAdd number of PEs to add to the amount of used PEs
     */
    private void addUsedPes(int usedPesToAdd) {
        this.usedPes += usedPesToAdd;
    }

    /**
     * Subtracts a given number of PEs from the amount of currently used PEs.
     *
     * @param usedPesToRemove number of PEs to subtract from the amount of used
     * PEs
     */
    private void removeUsedPes(int usedPesToRemove) {
        this.usedPes -= usedPesToRemove;
    }

}
