/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.Cloudlet.Status;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;

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
    private static final Logger logger = LoggerFactory.getLogger(CloudletSchedulerAbstract.class.getSimpleName());

    /**
     * @see #getCloudletPausedList()
     */
    private final List<CloudletExecution> cloudletPausedList;
    /**
     * @see #getCloudletFinishedList()
     */
    private final List<CloudletExecution> cloudletFinishedList;
    /**
     * @see #getCloudletFailedList()
     */
    private final List<CloudletExecution> cloudletFailedList;
    /**
     * @see #getTaskScheduler()
     */
    private CloudletTaskScheduler taskScheduler;
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
    private final List<CloudletExecution> cloudletExecList;
    /**
     * @see #getCloudletWaitingList()
     */
    private final List<CloudletExecution> cloudletWaitingList;

    /**
     * @see #getVm()
     */
    private Vm vm;

    /**
     * @see #getCloudletReturnedList()
     */
    private final Set<Cloudlet> cloudletReturnedList;

    /**
     * Creates a new CloudletScheduler object.
     */
    protected CloudletSchedulerAbstract() {
        setPreviousTime(0.0);
        vm = Vm.NULL;
        cloudletExecList = new ArrayList<>();
        cloudletPausedList = new ArrayList<>();
        cloudletFinishedList = new ArrayList<>();
        cloudletFailedList = new ArrayList<>();
        cloudletWaitingList = new ArrayList<>();
        cloudletReturnedList = new HashSet<>();
        currentMipsShare = new ArrayList<>();
        taskScheduler = CloudletTaskScheduler.NULL;
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
    protected final void setPreviousTime(final double previousTime) {
        this.previousTime = previousTime;
    }

    @Override
    public List<Double> getCurrentMipsShare() {
        return Collections.unmodifiableList(currentMipsShare);
    }

    /**
     * Sets the list of current mips share available for the VM using the
     * scheduler.
     *
     * @param currentMipsShare the new current mips share
     * @see #getCurrentMipsShare()
     */
    protected void setCurrentMipsShare(final List<Double> currentMipsShare) {
        if(currentMipsShare.size() > vm.getNumberOfPes()){
            logger.warn("Requested {} PEs but {} has just {}", currentMipsShare.size(), vm, vm.getNumberOfPes());
        }
        this.currentMipsShare = currentMipsShare;
    }

    /**
     * Gets the amount of MIPS available (free) for each Processor PE,
     * considering the currently executing cloudlets in this processor
     * and the number of PEs these cloudlets require.
     * This is the amount of MIPS that each Cloudlet is allowed to used,
     * considering that the processor is shared among all executing
     * cloudlets.
     *
     * <p>In the case of space shared schedulers,
     * there is no concurrency for PEs because some cloudlets
     * may wait in a queue until there is available PEs to be used
     * exclusively by them.</p>
     *
     * @return the amount of available MIPS for each Processor PE.
     *
     * @TODO Splitting the capacity of a CPU core among different applications
     * is not in fact possible. This was just an oversimplification
     * performed by the CloudletSchedulerTimeShared that may affect
     * other schedulers such as the CloudletSchedulerCompletelyFair,
     * which in fact performs task preemption.
     */
    public double getAvailableMipsByPe(){
        final long totalPesOfAllExecCloudlets = totalPesOfAllExecCloudlets();
        if(totalPesOfAllExecCloudlets > currentMipsShare.size()) {
            return getTotalMipsShare() / totalPesOfAllExecCloudlets;
        }

        return getPeCapacity();
    }

    private Double getPeCapacity() {
        return currentMipsShare.stream().findFirst().orElse(0.0);
    }

    /**
     * Gets the total number of PEs of all cloudlets currently executing in this processor.
     * @return
     */
    private long totalPesOfAllExecCloudlets() {
        return cloudletExecList.stream()
            .map(CloudletExecution::getCloudlet)
            .mapToLong(Cloudlet::getNumberOfPes).sum();
    }

    private double getTotalMipsShare(){
        return currentMipsShare.stream().mapToDouble(d -> d).sum();
    }

    @Override
    public List<CloudletExecution> getCloudletExecList() {
        return Collections.unmodifiableList(cloudletExecList);
    }

    protected void addCloudletToWaitingList(final CloudletExecution cloudlet) {
        Objects.requireNonNull(cloudlet);
        if(cloudlet == CloudletExecution.NULL){
            return;
        }

        cloudlet.setCloudletStatus(Cloudlet.Status.QUEUED);
        cloudletWaitingList.add(cloudlet);
    }


    /**
     * Gets the list of paused cloudlets.
     *
     * @return the cloudlet paused list
     */
    protected List<CloudletExecution> getCloudletPausedList() {
        return cloudletPausedList;
    }

    @Override
    public List<CloudletExecution> getCloudletFinishedList() {
        return cloudletFinishedList;
    }

    protected void addCloudletToFinishedList(final CloudletExecution cloudlet) {
        cloudletFinishedList.add(cloudlet);
    }

    /**
     * Gets the list of failed cloudlets.
     *
     * @return the cloudlet failed list.
     */
    protected List<CloudletExecution> getCloudletFailedList() {
        return cloudletFailedList;
    }

    @Override
    public List<CloudletExecution> getCloudletWaitingList() {
        return Collections.unmodifiableList(cloudletWaitingList);
    }

    /**
     * Sorts the {@link #cloudletWaitingList} using a given {@link Comparator}.
     * @param comparator the {@link Comparator} to sort the Waiting Cloudlets List
     */
    protected void sortCloudletWaitingList(final Comparator<CloudletExecution> comparator){
        cloudletWaitingList.sort(comparator);
    }

    @Override
    public final double cloudletSubmit(final Cloudlet cloudlet) {
        return cloudletSubmit(cloudlet, 0.0);
    }

    @Override
    public final double cloudletSubmit(final Cloudlet cl, final double fileTransferTime) {
        return cloudletSubmitInternal(new CloudletExecution(cl), fileTransferTime);
    }

    /**
     * @see #cloudletSubmit(Cloudlet, double)
     */
    protected double cloudletSubmitInternal(final CloudletExecution ce, final double fileTransferTime) {
        if (canAddCloudletToExecutionList(ce)) {
            ce.setCloudletStatus(Status.INEXEC);
            ce.setFileTransferTime(fileTransferTime);
            addCloudletToExecList(ce);
            return fileTransferTime + (ce.getCloudletLength() / getPeCapacity());
        }

        // No enough free PEs, then add Cloudlet to the waiting queue
        addCloudletToWaitingList(ce);
        return 0.0;
    }

    /**
     * Adds a Cloudlet to the list of cloudlets in execution.
     *
     * @param cloudlet the Cloudlet to be added
     */
    protected void addCloudletToExecList(final CloudletExecution cloudlet) {
        cloudlet.setCloudletStatus(Cloudlet.Status.INEXEC);
        cloudlet.setLastProcessingTime(getVm().getSimulation().clock());
        cloudletExecList.add(cloudlet);
        addUsedPes(cloudlet.getNumberOfPes());
    }

    @Override
    public boolean hasFinishedCloudlets() {
        return !cloudletFinishedList.isEmpty();
    }

    @Override
    public int runningCloudletsNumber() {
        return cloudletExecList.size();
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
        return cloudletExecList.stream()
            .findFirst()
            .map(this::finishCloudletMigration)
            .orElse(Cloudlet.NULL);
    }

    private Cloudlet finishCloudletMigration(final CloudletExecution ce) {
        removeCloudletFromExecListAndAddToFinishedList(ce);
        ce.finalizeCloudlet();
        return ce.getCloudlet();
    }

    @Override
    public int getCloudletStatus(final int cloudletId) {
        final Optional<CloudletExecution> optional = findCloudletInAllLists(cloudletId);
        return optional
            .map(CloudletExecution::getCloudlet)
            .map(Cloudlet::getStatus)
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
    protected Optional<CloudletExecution> findCloudletInAllLists(final double cloudletId) {
        //Concatenate all lists into a stream
        final Stream<List<CloudletExecution>> streamOfAllLists
            = Stream.of(cloudletExecList, cloudletPausedList, cloudletWaitingList,
            cloudletFinishedList, cloudletFailedList);

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
     * @param list       the list to search the Cloudlet into
     * @return an {@link Optional} value that is able to indicate if the
     * Cloudlet was found or not
     */
    protected Optional<CloudletExecution> findCloudletInList(final double cloudletId, final List<CloudletExecution> list) {
        return list.stream()
            .filter(ce -> ce.getCloudletId() == cloudletId)
            .findFirst();
    }

    @Override
    public void cloudletFinish(final CloudletExecution ce) {
        ce.setCloudletStatus(Cloudlet.Status.SUCCESS);
        ce.finalizeCloudlet();
        addCloudletToFinishedList(ce);
    }

    @Override
    public boolean cloudletPause(final int cloudletId) {
        if (changeStatusOfCloudletIntoList(cloudletExecList, cloudletId, this::changeInexecToPaused) != Cloudlet.NULL) {
            return true;
        }

        return changeStatusOfCloudletIntoList(cloudletWaitingList, cloudletId, this::changeReadyToPaused) != Cloudlet.NULL;
    }

    private void changeInexecToPaused(final CloudletExecution c) {
        changeStatusOfCloudlet(c, Status.INEXEC, Status.PAUSED);
    }

    private void changeReadyToPaused(final CloudletExecution c) {
        changeStatusOfCloudlet(c, Status.READY, Status.PAUSED);
    }

    @Override
    public Cloudlet cloudletCancel(final int cloudletId) {
        Cloudlet cloudlet;
        //Removes finished cloudlets from the list
        cloudlet = changeStatusOfCloudletIntoList(cloudletFinishedList, cloudletId, c -> {});
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        cloudlet = changeStatusOfCloudletIntoList(
            cloudletExecList, cloudletId,
            c -> changeStatusOfCloudlet(c, Status.INEXEC, Status.CANCELED));
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        cloudlet = changeStatusOfCloudletIntoList(
            cloudletPausedList, cloudletId,
            c -> changeStatusOfCloudlet(c, Status.PAUSED, Status.CANCELED));
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        cloudlet = changeStatusOfCloudletIntoList(
            cloudletWaitingList, cloudletId,
            c -> changeStatusOfCloudlet(c, Status.READY, Status.CANCELED));
        if (cloudlet != Cloudlet.NULL) {
            return cloudlet;
        }

        return Cloudlet.NULL;
    }

    /**
     * Changes the status of a given cloudlet.
     *
     * @param cloudlet      Cloudlet to set its status
     * @param currentStatus the current cloudlet status
     * @param newStatus     the new status to set
     * @todo @author manoelcampos The parameter currentStatus only exists
     * because apparently, the cloudlet status is not being accordingly changed
     * along the simulation run.
     */
    private void changeStatusOfCloudlet(final CloudletExecution cloudlet, final Status currentStatus, final Status newStatus) {
        if ((currentStatus == Status.INEXEC || currentStatus == Status.READY) && cloudlet.getCloudlet().isFinished()) {
            cloudletFinish(cloudlet);
        } else {
            cloudlet.setCloudletStatus(newStatus);
        }

        switch (newStatus) {
            case PAUSED:
                cloudletPausedList.add(cloudlet);
            break;
        }
    }

    /**
     * Search for a cloudlet into a given list in order to change its status and
     * remove it from that list.
     *
     * @param cloudletList                  the list where to search the cloudlet
     * @param cloudletId                    the id of the cloudlet to have its status changed
     * @param cloudletStatusUpdaterConsumer the {@link Consumer} that will apply
     *                                      the change in the status of the found cloudlet
     * @return the changed cloudlet or {@link Cloudlet#NULL} if not found in the
     * given list
     */
    private Cloudlet changeStatusOfCloudletIntoList(
        final List<CloudletExecution> cloudletList,
        final int cloudletId,
        final Consumer<CloudletExecution> cloudletStatusUpdaterConsumer)
    {
        final Function<CloudletExecution, Cloudlet> removeCloudletFromListAndUpdateItsStatus = c -> {
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
    public double updateProcessing(final double currentTime, final List<Double> mipsShare) {
        setCurrentMipsShare(mipsShare);

        if (isEmpty()) {
            setPreviousTime(currentTime);
            return Double.MAX_VALUE;
        }

        updateCloudletsProcessing(currentTime);
        updateVmRamAbsoluteUtilization();
        removeFinishedCloudletsFromExecutionListAndAddToFinishedList();
        moveNextCloudletsFromWaitingToExecList();

        final double nextSimulationTime = getEstimatedFinishTimeOfSoonerFinishingCloudlet(currentTime);
        setPreviousTime(currentTime);

        return nextSimulationTime;
    }

    /**
     * Updates the processing of all cloudlets of the Vm using this scheduler
     * that are in the {@link #getCloudletExecList() cloudlet execution list}.
     *
     * @param currentTime current simulation time
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void updateCloudletsProcessing(final double currentTime) {
        /* Uses a traditional for to avoid ConcurrentModificationException,
         * e.g., in cases when Cloudlet is cancelled during simulation execution. */
        for (int i = 0; i < cloudletExecList.size(); i++) {
            final CloudletExecution ce = cloudletExecList.get(i);
            updateCloudletProcessingAndPacketsDispatch(ce, currentTime);
        }
    }

    /**
     * Updates the processing of a specific cloudlet of the Vm using this
     * scheduler and packets that such a Cloudlet has to send or to receive
     * (if the CloudletScheduler has a {@link CloudletTaskScheduler} assigned to it).
     *
     * @param ce         The cloudlet to be its processing updated
     * @param currentTime current simulation time
     */
    private void updateCloudletProcessingAndPacketsDispatch(final CloudletExecution ce, final double currentTime) {
        long partialFinishedMI = 0;
        if (taskScheduler.isTimeToUpdateCloudletProcessing(ce.getCloudlet())) {
            partialFinishedMI = updateCloudletProcessing(ce, currentTime);
        }


        taskScheduler.processCloudletTasks(ce.getCloudlet(), partialFinishedMI);
    }

    /**
     * Updates the processing of a specific cloudlet of the Vm using this
     * scheduler.
     *
     * @param ce The cloudlet to be its processing updated
     * @param currentTime current simulation time
     * @return the executed length, in <b>Million Instructions (MI)</b>, since the last time cloudlet was processed.
     */
    protected long updateCloudletProcessing(final CloudletExecution ce, final double currentTime) {
        final long partialFinishedInstructions = cloudletExecutedInstructionsForTimeSpan(ce, currentTime);
        ce.updateProcessing(partialFinishedInstructions);
        return partialFinishedInstructions/Conversion.MILLION;
    }

    /**
     * Updates the VM usage of RAM, based on the current utilization of all
     * its running Cloudlets, that depends on the {@link Cloudlet#getUtilizationModelRam()}.
     *
     * <p>It deallocates all resources so that the VM's amount of allocated resource will be update
     * for each running Cloudlet. This way, each Cloudlet requests an amount that is allocated.
     * The request for the next Cloudlet may not be fulfilled due to lack of resources.
     * If a Cloudlet requests more resources than is available, just the available
     * amount is allocated to it.</p>
     */
    private void updateVmRamAbsoluteUtilization() {
        final ResourceManageable ram = vm.getResource(Ram.class);
        ram.deallocateAllResources();
        for (final CloudletExecution cloudletExecution : cloudletExecList) {
            final Cloudlet cloudlet = cloudletExecution.getCloudlet();
            final long requested = (long)getCloudletRamAbsoluteUtilization(cloudlet);
            if(requested > ram.getAvailableResource()){
                final String msg =
                        ram.getAvailableResource() > 0 ?
                        String.format("just %d was available and allocated to it.", ram.getAvailableResource()):
                        "no amount is available.";
                logger.warn(
                    "{}: {}: {} requested {} MB of RAM but {}",
                    vm.getSimulation().clock(), getClass().getSimpleName(), cloudlet, requested, msg);
            }
            ram.allocateResource(Math.min(requested, ram.getAvailableResource()));
        }
    }

    /**
     * Gets the absolute value of RAM utilization for a given Cloudlet
     *
     * @param cloudlet the Cloudlet to get the absolute value of RAM utilization
     * @return the Cloudlet RAM utilization in absolute value
     */
    private double getCloudletRamAbsoluteUtilization(final Cloudlet cloudlet) {
        final ResourceManageable ram = vm.getResource(Ram.class);
        final UtilizationModel um = cloudlet.getUtilizationModelRam();
        return um.getUnit() == Unit.ABSOLUTE ?
                Math.min(um.getUtilization(), vm.getRam().getCapacity()) :
                um.getUtilization() * ram.getCapacity();
    }

    /**
     * Computes the length of a given cloudlet, in number
     * of Instructions (I), which has been executed since the last time cloudlet
     * processing was updated.
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
     * stored in the {@link CloudletExecution#getFileTransferTime()}
     * attribute and is set when the Cloudlet is submitted to the scheduler.</p>
     *
     * @param cl the Cloudlet to compute the executed length
     * @param currentTime current simulation time
     * @return the executed length, in Number of Instructions (I), since the last time cloudlet was processed.
     * @TODO @author manoelcampos This method is being called 2 times more than required.
     * Despite it is not causing any apparent issue, it has to be
     * investigated. For instance, for simulation time 2, with 2 cloudlets, the
     * method is being called 4 times instead of just 2 (1 for each cloudlet for
     * that time).
     * @see #updateCloudletsProcessing(double)
     */
    private long cloudletExecutedInstructionsForTimeSpan(final CloudletExecution cl, final double currentTime) {
        /* The time the Cloudlet spent executing in fact, since the last time Cloudlet update was
         * called by the scheduler. If it is zero, indicates that the Cloudlet didn't use
         * the CPU in this time span, because it is waiting for its required files
         * to be transferred from the Datacenter storage.
         */
        final double actualProcessingTime = hasCloudletFileTransferTimePassed(cl, currentTime) ? timeSpan(cl, currentTime) : 0;
        final double cloudletUsedMips =
            getAbsoluteCloudletResourceUtilization(
                cl.getCloudlet().getUtilizationModelCpu(),
                currentTime, getAvailableMipsByPe());
        return (long) (cloudletUsedMips * actualProcessingTime * Conversion.MILLION);
    }

    /**
     * Checks if the time to transfer the files required by a Cloudlet to
     * execute has already passed, in order to start executing the Cloudlet in
     * fact.
     *
     * @param ce         Cloudlet to check if the time to transfer the files has passed
     * @param currentTime the current simulation time
     * @return true if the time to transfer the files has passed, false
     * otherwise
     */
    private boolean hasCloudletFileTransferTimePassed(final CloudletExecution ce, final double currentTime) {
        return ce.getFileTransferTime() == 0 ||
               currentTime - ce.getCloudletArrivalTime() > ce.getFileTransferTime() ||
               ce.getCloudlet().getFinishedLengthSoFar() > 0;
    }

    /**
     * Computes the time span between the current simulation time and the last
     * time the processing of a cloudlet was updated.
     *
     * @param cl the cloudlet to compute the execution time span
     * @param currentTime the current simulation time
     * @return
     */
    protected double timeSpan(final CloudletExecution cl, final double currentTime) {
        return currentTime - cl.getLastProcessingTime();
    }

    /**
     * Removes finished cloudlets from the
     * {@link #getCloudletExecList() list of cloudlets to execute}
     * and adds them to finished list.
     *
     * @return the number of finished cloudlets removed from the
     * {@link #getCloudletExecList() execution list}
     */
    private int removeFinishedCloudletsFromExecutionListAndAddToFinishedList() {
        final List<CloudletExecution> finishedCloudlets
            = cloudletExecList.stream()
            .filter(c -> c.getCloudlet().isFinished())
            .collect(toList());

        for (final CloudletExecution c : finishedCloudlets) {
            removeCloudletFromExecListAndAddToFinishedList(c);
        }

        return finishedCloudlets.size();
    }

    private void removeCloudletFromExecListAndAddToFinishedList(final CloudletExecution cloudlet) {
        setCloudletFinishTimeAndAddToFinishedList(cloudlet);
        removeCloudletFromExecList(cloudlet);
    }

    /**
     * Removes a Cloudlet from the list of cloudlets in execution.
     *
     * @param cloudlet the Cloudlet to be removed
     * @return the removed Cloudlet or {@link CloudletExecution#NULL} if not found
     */
    protected CloudletExecution removeCloudletFromExecList(final CloudletExecution cloudlet) {
        removeUsedPes(cloudlet.getNumberOfPes());
        return cloudletExecList.remove(cloudlet) ? cloudlet : CloudletExecution.NULL;
    }

    /**
     * Sets the finish time of a cloudlet and adds it to the
     * finished list.
     *
     * @param ce the cloudlet to set the finish time
     */
    private void setCloudletFinishTimeAndAddToFinishedList(final CloudletExecution ce) {
        final double clock = vm.getSimulation().clock();
        ce.setFinishTime(clock);
        cloudletFinish(ce);
    }

    /**
     * Gets the estimated time, considering the current time, that a next Cloudlet is expected to finish.
     *
     * @param currentTime current simulation time
     * @return the estimated finish time of sooner finishing cloudlet
     * (which is a relative delay from the current simulation time)
     */
    protected double getEstimatedFinishTimeOfSoonerFinishingCloudlet(final double currentTime) {
        return cloudletExecList
            .stream()
            .mapToDouble(c -> getEstimatedFinishTimeOfCloudlet(c, currentTime))
            .min().orElse(Double.MAX_VALUE);
    }

    /**
     * Gets the estimated time when a given cloudlet is supposed to finish
     * executing. It considers the amount of Vm PES and the sum of PEs required
     * by all VMs running inside the VM.
     *
     * @param ce         cloudlet to get the estimated finish time
     * @param currentTime current simulation time
     * @return the estimated finish time of the given cloudlet
     * (which is a relative delay from the current simulation time)
     */
    protected double getEstimatedFinishTimeOfCloudlet(final CloudletExecution ce, final double currentTime) {
        final double cloudletUsedMips =
            getAbsoluteCloudletResourceUtilization(ce.getCloudlet().getUtilizationModelCpu(),
                currentTime, getAvailableMipsByPe());
        double estimatedFinishTime = ce.getRemainingCloudletLength() / cloudletUsedMips;

        if (estimatedFinishTime < vm.getSimulation().getMinTimeBetweenEvents()) {
            estimatedFinishTime = vm.getSimulation().getMinTimeBetweenEvents();
        }

        return estimatedFinishTime;
    }

    /**
     * Selects the next Cloudlets in the waiting list to move to the execution
     * list in order to start executing them. While there is enough free PEs,
     * the method try to find a suitable Cloudlet in the list, until it reaches
     * the end of such a list.
     *
     * <p>
     * The method might also exchange some cloudlets in the execution list with
     * some in the waiting list. Thus, some running cloudlets may be preempted
     * to give opportunity to previously waiting cloudlets to run. This is a
     * process called
     * <a href="https://en.wikipedia.org/wiki/Context_switch">context switch</a>.
     * However, each CloudletScheduler implementation decides how
     * such a process is implemented. For instance, Space-Shared schedulers may
     * just perform context switch just after currently running Cloudlets
     * completely finish executing.
     * <p>
     * <p>
     * This method is called internally by the
     * {@link CloudletScheduler#updateProcessing(double, List)} one.</p>
     *
     * @pre currentTime >= 0
     * @post $none
     */
    protected void moveNextCloudletsFromWaitingToExecList() {
        Optional<CloudletExecution> optional = Optional.of(CloudletExecution.NULL);
        while (!cloudletWaitingList.isEmpty() && optional.isPresent() && getFreePes() > 0) {
            optional = findSuitableWaitingCloudlet();
            optional.ifPresent(this::addWaitingCloudletToExecList);
        }
    }

    /**
     * Try to find the first Cloudlet in the waiting list which the number of
     * required PEs is not higher than the number of free PEs.
     *
     * @return an {@link Optional} containing the found Cloudlet or an empty
     * Optional otherwise
     */
    protected Optional<CloudletExecution> findSuitableWaitingCloudlet() {
        return cloudletWaitingList.stream()
            .filter(this::isThereEnoughFreePesForCloudlet)
            .findFirst();
    }

    /**
     * Checks if the amount of PEs required by a given Cloudlet is free to use.
     *
     * @param c the Cloudlet to get the number of required PEs
     * @return true if there is the amount of free PEs, false otherwise
     */
    protected boolean isThereEnoughFreePesForCloudlet(final CloudletExecution c) {
        return vm.getProcessor().getAvailableResource() >= c.getNumberOfPes();
    }

    /**
     * Removes a Cloudlet from waiting list and adds it to the exec list.
     * @param cloudlet the cloudlet to add to to exec list
     * @return the given cloudlet
     */
    protected CloudletExecution addWaitingCloudletToExecList(final CloudletExecution cloudlet) {
        /*If the Cloudlet is not found in the waiting List, there is no problem.
        * Just add it to the exec List.*/
        cloudletWaitingList.remove(cloudlet);
        addCloudletToExecList(cloudlet);
        return cloudlet;
    }

    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public void setVm(final Vm vm) {
        Objects.requireNonNull(vm);

        if (isOtherVmAssigned(vm)) {
            throw new IllegalArgumentException(
                "CloudletScheduler already has a Vm assigned to it. Each Vm must have its own CloudletScheduler instance.");
        }

        this.vm = vm;
    }

    /**
     * Checks if the {@link CloudletScheduler} has a {@link Vm} assigned that is
     * different from the given one
     *
     * @param vm the Vm to check if assigned scheduler's Vm is different from
     * @return
     */
    private boolean isOtherVmAssigned(final Vm vm) {
        return this.vm != null && this.vm != Vm.NULL && !vm.equals(this.vm);
    }

    @Override
    public long getUsedPes() {
        return vm.getProcessor().getAllocatedResource();
    }

    /**
     * Gets the number of PEs currently not being used.
     *
     * @return
     */
    @Override
    public long getFreePes() {
        return currentMipsShare.size() - getUsedPes();
    }

    /**
     * Adds a given number of PEs to the amount of currently used PEs.
     *
     * @param usedPesToAdd number of PEs to add to the amount of used PEs
     */
    private void addUsedPes(final long usedPesToAdd) {
        vm.getProcessor().allocateResource(usedPesToAdd);
    }

    /**
     * Subtracts a given number of PEs from the amount of currently used PEs.
     *
     * @param usedPesToRemove number of PEs to subtract from the amount of used PEs
     */
    private void removeUsedPes(final long usedPesToRemove) {
        vm.getProcessor().deallocateResource(usedPesToRemove);
    }

    @Override
    public CloudletTaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    @Override
    public void setTaskScheduler(final CloudletTaskScheduler taskScheduler) {
        Objects.requireNonNull(taskScheduler);
        this.taskScheduler = taskScheduler;
        this.taskScheduler.setVm(vm);
    }

    @Override
    public boolean isTherePacketScheduler() {
        return taskScheduler != null && taskScheduler != CloudletTaskScheduler.NULL;
    }

    @Override
    public double getRequestedCpuPercentUtilization(final double time) {
        return cloudletExecList.stream()
            .map(CloudletExecution::getCloudlet)
            .mapToDouble(c -> getAbsoluteCloudletCpuUtilizationForAllPes(time, c))
            .sum() / vm.getTotalMipsCapacity();
    }

    /**
     * Gets the total CPU utilization in MIPS for a Cloudlet, considering
     * all the PEs in which it is running.
     *
     * @param time the simulation time
     * @param cloudlet the Cloudlet to get the total CPU utilization
     * @return the total Cloudlet CPU utilization (in MIPS) across all PEs it is using
     */
    private double getAbsoluteCloudletCpuUtilizationForAllPes(final double time, final Cloudlet cloudlet) {
        final double cloudletCpuUsageForOnePe =
            getAbsoluteCloudletResourceUtilization(
                cloudlet.getUtilizationModelCpu(), time, getAvailableMipsByPe());

        return cloudletCpuUsageForOnePe * cloudlet.getNumberOfPes();
    }

    @Override
    public double getRequestedMipsForCloudlet(final CloudletExecution ce, final double time) {
        return getAbsoluteCloudletResourceUtilization(ce.getCloudlet().getUtilizationModelCpu(), time, vm.getMips());
    }

    @Override
    public double getAllocatedMipsForCloudlet(final CloudletExecution ce, final double time) {
        return getAbsoluteCloudletResourceUtilization(ce.getCloudlet().getUtilizationModelCpu(), time, getAvailableMipsByPe());
    }

    @Override
    public double getCurrentRequestedBwPercentUtilization() {
        return cloudletExecList.stream()
            .map(CloudletExecution::getCloudlet)
            .map(Cloudlet::getUtilizationModelBw)
            .mapToDouble(um -> getAbsoluteCloudletResourceUtilization(um, vm.getBw().getCapacity()))
            .sum() / vm.getBw().getCapacity();
    }

    @Override
    public double getCurrentRequestedRamPercentUtilization() {
        return cloudletExecList.stream()
            .map(CloudletExecution::getCloudlet)
            .map(Cloudlet::getUtilizationModelRam)
            .mapToDouble(um -> getAbsoluteCloudletResourceUtilization(um, vm.getRam().getCapacity()))
            .sum() / vm.getRam().getCapacity();
    }

    /**
     * Computes the absolute amount of a resource used by a given Cloudlet
     * for the current simulation time, based on the maximum amount of resource that the Cloudlet can use
     * this time.
     *
     * @param um                      the {@link UtilizationModel} to get the absolute amount of resource used by the Cloudlet
     * @param maxResourceAllowedToUse the maximum absolute resource that the Cloudlet will be allowed to use
     * @return the absolute amount of resource that the Cloudlet will use
     */
    private double getAbsoluteCloudletResourceUtilization(final UtilizationModel um, final double maxResourceAllowedToUse) {
        return getAbsoluteCloudletResourceUtilization(um, vm.getSimulation().clock(), maxResourceAllowedToUse);
    }

    /**
     * Computes the absolute amount of a resource used by a given Cloudlet
     * for a given time, based on the maximum amount of resource that the Cloudlet can use
     * this time.
     *
     * @param um                      the {@link UtilizationModel} to get the absolute amount of resource used by the Cloudlet
     * @param time                    the simulation time
     * @param maxResourceAllowedToUse the maximum absolute resource that the Cloudlet will be allowed to use
     * @return the absolute amount of resource that the Cloudlet will use
     */
    private double getAbsoluteCloudletResourceUtilization(
        final UtilizationModel um,
        final double time,
        final double maxResourceAllowedToUse)
    {
        return um.getUnit() == Unit.ABSOLUTE ?
            Math.min(um.getUtilization(time), maxResourceAllowedToUse) :
            um.getUtilization() * maxResourceAllowedToUse;
    }

    @Override
    public Set<Cloudlet> getCloudletReturnedList() {
        return Collections.unmodifiableSet(cloudletReturnedList);
    }

    @Override
    public boolean isCloudletReturned(final Cloudlet cloudlet) {
        return cloudletReturnedList.contains(cloudlet);
    }

    @Override
    public void addCloudletToReturnedList(final Cloudlet cloudlet) {
        this.cloudletReturnedList.add(cloudlet);
    }

    @Override
    public void deallocatePesFromVm(int pesToRemove) {
        pesToRemove = Math.min(pesToRemove, currentMipsShare.size());
        removeUsedPes(pesToRemove);
        IntStream.range(0, pesToRemove).forEach(i -> currentMipsShare.remove(0));
    }

    @Override
    public List<Cloudlet> getCloudletList() {
        return Stream.concat(cloudletExecList.stream(), cloudletWaitingList.stream())
                     .map(CloudletExecution::getCloudlet)
                     .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    @Override
    public boolean isEmpty() {
        return cloudletExecList.isEmpty() && cloudletWaitingList.isEmpty();
    }
}
