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
import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.MipsShare;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.listeners.CloudletResourceAllocationFailEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.io.Serial;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.cloudsimplus.listeners.CloudletResourceAllocationFailEventInfo.of;

/**
 * An abstract class for implementing {@link CloudletScheduler}s representing
 * scheduling policies performed by a virtual machine to run its
 * {@link Cloudlet Cloudlets}. Each VM must have its own instance of a CloudletScheduler.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public abstract class CloudletSchedulerAbstract implements CloudletScheduler {
    @Serial
    private static final long serialVersionUID = -2314361120790372742L;

    /** @see #getCloudletPausedList() */
    private final List<CloudletExecution> cloudletPausedList;

    /** @see #getCloudletFinishedList() */
    private final List<CloudletExecution> cloudletFinishedList;

    /** @see #getCloudletFailedList() */
    private final List<CloudletExecution> cloudletFailedList;

    /** @see #getTaskScheduler() */
    private CloudletTaskScheduler taskScheduler;

    /** @see #getPreviousTime() */
    private double previousTime;

    /** @see #getCurrentMipsShare() */
    private MipsShare currentMipsShare;

    /** @see #getCloudletExecList() */
    private final List<CloudletExecution> cloudletExecList;

    /** @see #enableCloudletSubmittedList() */
    private boolean enableCloudletSubmittedList;

    /** @see #getCloudletSubmittedList() */
    private final List<Cloudlet> cloudletSubmittedList;

    /**
     * @see #getCloudletWaitingList()
     */
    private final List<CloudletExecution> cloudletWaitingList;

    /** @see #getVm() */
    private Vm vm;

    /** @see #getCloudletReturnedList() */
    private final Set<Cloudlet> cloudletReturnedList;

    /** @see #addOnCloudletResourceAllocationFail(EventListener) */
    private final List<EventListener<CloudletResourceAllocationFailEventInfo>> resourceAllocationFailListeners;

    /**
     * Creates a CloudletScheduler.
     */
    protected CloudletSchedulerAbstract() {
        setPreviousTime(0.0);
        vm = Vm.NULL;
        cloudletSubmittedList = new ArrayList<>();
        cloudletExecList = new ArrayList<>();
        cloudletPausedList = new ArrayList<>();
        cloudletFinishedList = new ArrayList<>();
        cloudletFailedList = new ArrayList<>();
        cloudletWaitingList = new ArrayList<>();
        cloudletReturnedList = new HashSet<>();
        currentMipsShare = new MipsShare();
        taskScheduler = CloudletTaskScheduler.NULL;
        resourceAllocationFailListeners = new ArrayList<>();
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

    /**
     * Gets current MIPS capacity from the VM that will be
     * made available to the scheduler. This MIPS share will be allocated
     * to Cloudlets as requested.
     *
     * @return the current MIPS share, where each item represents
     * the MIPS capacity of a {@link Pe} which is available to the scheduler.
     *
     */
    public MipsShare getCurrentMipsShare() {
        return currentMipsShare;
    }

    /**
     * Sets current MIPS share available for the VM using the
     * scheduler.
     *
     * @param currentMipsShare the new current MIPS share
     * @see #getCurrentMipsShare()
     */
    protected void setCurrentMipsShare(final MipsShare currentMipsShare) {
        if(currentMipsShare.pes() > vm.getNumberOfPes()){
            LOGGER.warn("Requested {} PEs but {} has just {}", currentMipsShare.pes(), vm, vm.getNumberOfPes());
            this.currentMipsShare = new MipsShare(vm.getNumberOfPes(), currentMipsShare.mips());
        }
        else this.currentMipsShare = currentMipsShare;
    }

    /**
     * Gets the amount of MIPS available (free) for each Processor PE,
     * considering the currently executing cloudlets in this processor
     * and the number of PEs these cloudlets require.
     * This is the amount of MIPS that each Cloudlet is allowed to used,
     * since the processor is shared among all executing
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
     *       inside a VM is not in fact possible (unless containers are used).
     *       This was just an oversimplification
     *       performed by the CloudletSchedulerTimeShared that may affect
     *       other schedulers such as the CloudletSchedulerCompletelyFair,
     *       which in fact performs task preemption.
     */
    public double getAvailableMipsByPe(){
        final long totalPesOfAllExecCloudlets = totalPesOfAllExecCloudlets();
        if(totalPesOfAllExecCloudlets > currentMipsShare.pes()) {
            return getTotalMipsShare() / totalPesOfAllExecCloudlets;
        }

        return getPeCapacity();
    }

    private Double getPeCapacity() {
        return currentMipsShare.mips();
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
        return currentMipsShare.totalMips();
    }

    @Override
    public List<CloudletExecution> getCloudletExecList() {
        return Collections.unmodifiableList(cloudletExecList);
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletSubmittedList() {
        if(cloudletSubmittedList.isEmpty() && !enableCloudletSubmittedList) {
            LOGGER.warn("{}: The list of submitted Cloudlets for {} is empty maybe because you didn't enabled it by calling enableCloudletSubmittedList().", getClass().getSimpleName(), vm);
        }

        return (List<T>) cloudletSubmittedList;
    }

    @Override
    public CloudletScheduler enableCloudletSubmittedList() {
        this.enableCloudletSubmittedList = true;
        return this;
    }

    protected void addCloudletToWaitingList(final CloudletExecution cle) {
        if(requireNonNull(cle) == CloudletExecution.NULL){
            return;
        }

        if(cle.getCloudlet().getStatus() != Status.FROZEN) {
            cle.setStatus(Status.QUEUED);
        }
        cloudletWaitingList.add(cle);
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
    public final double cloudletSubmit(final Cloudlet cloudlet, final double fileTransferTime) {
        if(enableCloudletSubmittedList) {
            cloudletSubmittedList.add(cloudlet);
        }

        return cloudletSubmitInternal(new CloudletExecution(cloudlet), fileTransferTime);
    }

    /**
     * Receives the execution information of a Cloudlet to be executed in the VM managed by this scheduler.
     *
     * @param cle the submitted cloudlet
     * @param fileTransferTime time required to move the required files from the SAN to the VM
     * @return expected finish time of this cloudlet (considering the time to transfer required
     * files from the Datacenter to the Vm), or 0 if it is in a waiting queue
     * @see #cloudletSubmit(Cloudlet, double)
     */
    protected double cloudletSubmitInternal(final CloudletExecution cle, final double fileTransferTime) {
        if (canExecuteCloudlet(cle)) {
            cle.setStatus(Status.INEXEC);
            cle.setFileTransferTime(fileTransferTime);
            addCloudletToExecList(cle);
            return fileTransferTime + Math.abs(cle.getCloudletLength()/getPeCapacity()) ;
        }

        // No enough free PEs, then add Cloudlet to the waiting queue
        addCloudletToWaitingList(cle);
        return 0.0;
    }

    /**
     * Adds a Cloudlet to the list of cloudlets in execution.
     *
     * @param cle the Cloudlet to be added
     */
    protected void addCloudletToExecList(final CloudletExecution cle) {
        cle.setStatus(Cloudlet.Status.INEXEC);
        cle.setLastProcessingTime(getVm().getSimulation().clock());
        cloudletExecList.add(cle);
        addUsedPes(cle.getNumberOfPes());
    }

    @Override
    public boolean hasFinishedCloudlets() {
        return !cloudletFinishedList.isEmpty();
    }

    /**
     * Search for a Cloudlet into all Cloudlet lists.
     *
     * @param cloudletId the id of the Cloudlet to search for
     * @return an {@link Optional} value that is able to indicate if the
     * Cloudlet was found or not
     */
    protected Optional<CloudletExecution> findCloudletInAllLists(final double cloudletId) {
        final var cloudletExecInfoListStream = Stream.of(
            cloudletExecList, cloudletPausedList, cloudletWaitingList,
            cloudletFinishedList, cloudletFailedList
        );

        //Gets all elements in each list and makes them a single full list,
        //returning the first Cloudlet with the given id
        return cloudletExecInfoListStream
            .flatMap(List::stream)
            .filter(cle -> cle.getCloudletId() == cloudletId)
            .findFirst();
    }

    /**
     * Search for a Cloudlet into a given list.
     *
     * @param cloudlet the Cloudlet to search for
     * @param list       the list to search the Cloudlet into
     * @return an {@link Optional} value that is able to indicate if the
     * Cloudlet was found or not
     */
    protected Optional<CloudletExecution> findCloudletInList(final Cloudlet cloudlet, final List<CloudletExecution> list) {
        return list.stream()
            .filter(cle -> cle.getCloudletId() == cloudlet.getId())
            .findFirst();
    }

    /**
     * Processes a finished cloudlet.
     *
     * @param cle finished cloudlet
     */
    protected void cloudletFinish(final CloudletExecution cle) {
        cle.setStatus(Status.SUCCESS);
        cle.finalizeCloudlet();
        cloudletFinishedList.add(cle);
    }

    @Override
    public boolean cloudletReady(final Cloudlet cloudlet) {
        if (changeStatusOfCloudletIntoList(cloudletPausedList, cloudlet, this::changeToReady)) {
            return true;
        }

        /*
         If the Cloudlet was not found in the paused list, it hasn't started executing yet.
         It may have been frozen waiting for a READY message.
         This way, just changes its status to ready so
         that it can be scheduled naturally to start executing.
         */
        cloudlet.setStatus(Status.READY);

        /*
         Requests a cloudlet processing update to ensure the cloudlet will be moved to the
         exec list as soon as possible.
         Without such a request, the Cloudlet may start executing only
         after a new and possibly unrelated message is processed by the simulator.
         Since the next message to be received may take a long time,
         the processing update is requested right away.
         */
        final Datacenter dc = vm.getHost().getDatacenter();
        dc.schedule(CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING);
        return true;
    }

    private void changeToReady(final CloudletExecution cle) {
        changeStatusOfCloudlet(cle, cle.getCloudlet().getStatus(), Status.READY);
    }

    @Override
    public boolean cloudletPause(final Cloudlet cloudlet) {
        if (changeStatusOfCloudletIntoList(cloudletExecList, cloudlet, this::changeInExecToPaused)) {
            return true;
        }

        return changeStatusOfCloudletIntoList(cloudletWaitingList, cloudlet, this::changeReadyToPaused);
    }

    private void changeInExecToPaused(final CloudletExecution cle) {
        changeStatusOfCloudlet(cle, Status.INEXEC, Status.PAUSED);
        removeUsedPes(cle.getNumberOfPes());
    }

    private void changeReadyToPaused(final CloudletExecution cle) {
        changeStatusOfCloudlet(cle, Status.READY, Status.PAUSED);
    }

    @Override
    public Cloudlet cloudletFail(final Cloudlet cloudlet) {
        return stopCloudlet(cloudlet, Status.FAILED);
    }

    @Override
    public Cloudlet cloudletCancel(final Cloudlet cloudlet) {
        return stopCloudlet(cloudlet, Status.CANCELED);
    }

    /**
     * Sets a Cloudlet as {@link Status#CANCELED} or {@link Status#FAILED}.
     * @param cloudlet the Cloudlet to stop executing
     * @param stopStatus the stop status, either {@link Status#CANCELED} or {@link Status#FAILED}
     * @return the stopped cloudlet or {@link Cloudlet#NULL} if not found
     */
    private Cloudlet stopCloudlet(final Cloudlet cloudlet, final Status stopStatus) {
        //Removes finished cloudlets from the list without changing its status
        boolean found = changeStatusOfCloudletIntoList(cloudletFinishedList, cloudlet, cle -> {});
        if (found) {
            return cloudlet;
        }

        found = changeStatusOfCloudletIntoList(
            cloudletExecList, cloudlet,
            cle -> changeStatusOfCloudlet(cle, Status.INEXEC, stopStatus));
        if (found) {
            return cloudlet;
        }

        found = changeStatusOfCloudletIntoList(
            cloudletPausedList, cloudlet,
            cle -> changeStatusOfCloudlet(cle, Status.PAUSED, stopStatus));
        if (found) {
            return cloudlet;
        }

        changeStatusOfCloudletIntoList(
            cloudletWaitingList, cloudlet,
            cle -> changeStatusOfCloudlet(cle, Status.READY, stopStatus));
        if (found) {
            return cloudlet;
        }

        return Cloudlet.NULL;
    }

    /**
     * Changes the status of a given cloudlet.
     *
     * @param cle      Cloudlet to set its status
     * @param currentStatus the current cloudlet status
     * @param newStatus     the new status to set
     * TODO The parameter currentStatus only exists
     *      because apparently, the cloudlet status is not being accordingly changed
     *      along the simulation run.
     */
    private void changeStatusOfCloudlet(final CloudletExecution cle, final Status currentStatus, final Status newStatus) {
        if ((currentStatus == Status.INEXEC || currentStatus == Status.READY) && cle.getCloudlet().isFinished())
            cloudletFinish(cle);
        else cle.setStatus(newStatus);

        if (newStatus == Status.PAUSED)
            cloudletPausedList.add(cle);
        else if (newStatus == Status.READY)
            addCloudletToWaitingList(cle);
    }

    /**
     * Search for a cloudlet into a given list in order to change its status and
     * remove it from that list.
     *
     * @param cloudletList                  the list where to search the cloudlet
     * @param cloudlet                    the id of the cloudlet to have its status changed
     * @param cloudletStatusUpdaterConsumer the {@link Consumer} that will apply
     *                                      the change in the status of the found cloudlet
     * @return true if the Cloudlet was found, false otherwise
     */
    private boolean changeStatusOfCloudletIntoList(
        final List<CloudletExecution> cloudletList,
        final Cloudlet cloudlet,
        final Consumer<CloudletExecution> cloudletStatusUpdaterConsumer)
    {
        final Function<CloudletExecution, Cloudlet> removeCloudletAndUpdateStatus = cle -> {
            cloudletList.remove(cle);
            cloudletStatusUpdaterConsumer.accept(cle);
            return cle.getCloudlet();
        };

        return findCloudletInList(cloudlet, cloudletList)
            .map(removeCloudletAndUpdateStatus)
            .isPresent();
    }

    @Override
    public double updateProcessing(final double currentTime, final MipsShare mipsShare) {
        setCurrentMipsShare(mipsShare);

        if (isEmpty()) {
            setPreviousTime(currentTime);
            return Double.MAX_VALUE;
        }

        deallocateVmResources();

        double nextSimulationDelay = updateCloudletsProcessing(currentTime);
        nextSimulationDelay = Math.min(nextSimulationDelay, moveNextCloudletsFromWaitingToExecList(currentTime));
        addCloudletsToFinishedList();

        setPreviousTime(currentTime);
        vm.getSimulation().setLastCloudletProcessingUpdate(currentTime);

        return nextSimulationDelay;
    }

    /**
     * Deallocates total used capacity from VM RAM and Bandwidth
     * so that the allocation can be updated when running Cloudlets are processed.
     */
    private void deallocateVmResources() {
        ((VmSimple)vm).getRam().deallocateAllResources();
        ((VmSimple)vm).getBw().deallocateAllResources();
    }

    /**
     * Updates the processing of all cloudlets of the Vm using this scheduler
     * that are in the {@link #getCloudletExecList() cloudlet execution list}.
     *
     * @param currentTime current simulation time
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private double updateCloudletsProcessing(final double currentTime) {
        double nextCloudletFinishTime = Double.MAX_VALUE;
        long usedPes = 0;
        /* Uses an indexed for to avoid ConcurrentModificationException,
         * e.g., in cases when Cloudlet is cancelled during simulation execution. */
        for (int i = 0; i < cloudletExecList.size(); i++) {
            final CloudletExecution cle = cloudletExecList.get(i);
            updateCloudletProcessingAndPacketsDispatch(cle, currentTime);
            nextCloudletFinishTime = Math.min(nextCloudletFinishTime, cloudletEstimatedFinishTime(cle, currentTime));
            usedPes += cle.getCloudlet().getNumberOfPes();
        }

        ((VmSimple) vm).setFreePesNumber(vm.getNumberOfPes() - usedPes);

        return nextCloudletFinishTime;
    }

    /**
     * Updates the processing of a specific cloudlet of the Vm using this
     * scheduler. Then process tasks such a Cloudlet may have
     * (if the CloudletScheduler has a {@link CloudletTaskScheduler} assigned to it).
     *
     * @param cle         The cloudlet to be its processing updated
     * @param currentTime current simulation time
     */
    private void updateCloudletProcessingAndPacketsDispatch(final CloudletExecution cle, final double currentTime) {
        long partialFinishedMI = 0;
        if (taskScheduler.isTimeToUpdateCloudletProcessing(cle.getCloudlet())) {
            partialFinishedMI = updateCloudletProcessing(cle, currentTime);
        }

        taskScheduler.processCloudletTasks(cle.getCloudlet(), partialFinishedMI);
    }

    /**
     * Updates the processing of a specific cloudlet of the Vm using this
     * scheduler.
     *
     * @param cle The cloudlet to be its processing updated
     * @param currentTime current simulation time
     * @return the executed length, in <b>Million Instructions (MI)</b>, since the last time cloudlet was processed.
     */
    protected long updateCloudletProcessing(final CloudletExecution cle, final double currentTime) {
        final double partialFinishedInstructions = cloudletExecutedInstructionsForTimeSpan(cle, currentTime);
        cle.updateProcessing(partialFinishedInstructions);
        updateVmResourceAbsoluteUtilization(cle, ((VmSimple)vm).getRam());
        updateVmResourceAbsoluteUtilization(cle, ((VmSimple)vm).getBw());

        return (long)(partialFinishedInstructions/ Conversion.MILLION);
    }

    /**
     * Updates the VM utilization of given resource, based on the current utilization of a
     * running Cloudlet, that depends on the Cloudlet's {@link UtilizationModel} for that resource.
     *
     * @param vmResource the kind of resource to updates its utilization (usually {@link Ram} or {@link Bandwidth}).
     */
    private void updateVmResourceAbsoluteUtilization(final CloudletExecution cle, final ResourceManageable vmResource) {
        final var cloudlet = cle.getCloudlet();
        final long requested = (long) getCloudletResourceAbsoluteUtilization(cloudlet, vmResource);
        if(requested > vmResource.getCapacity()){
            LOGGER.warn(
                "{}: {}: {} requested {} {} of {} but that is >= the VM capacity ({})",
                vm.getSimulation().clockStr(), getClass().getSimpleName(), cloudlet,
                requested, vmResource.getUnit(), vmResource.getClass().getSimpleName(), vmResource.getCapacity());
            return;
        }

        final long available = vmResource.getAvailableResource();
        if(requested > available){
            final String msg1 =
                    available > 0 ?
                    String.format("just %d was available", available):
                    "no amount is available.";
            final String msg2 = vmResource.getClass() == Ram.class ? ". Using Virtual Memory," : ",";
            LOGGER.warn(
                "{}: {}: {} requested {} {} of {} but {}{} which delays Cloudlet processing.",
                vm.getSimulation().clockStr(), getClass().getSimpleName(), cloudlet,
                requested, vmResource.getUnit(), vmResource.getClass().getSimpleName(), msg1, msg2);

            updateOnResourceAllocationFailListeners(vmResource, cloudlet, requested, available);
        }

        vmResource.allocateResource(Math.min(requested, available));
    }

    private void updateOnResourceAllocationFailListeners(
        final ResourceManageable resource, final Cloudlet cloudlet,
        final long requested, final long available)
    {
        //Uses reversed indexed for to avoid ConcurrentModificationException if some Listener is de-registered during loop
        for (int i = resourceAllocationFailListeners.size()-1; i >= 0; i--) {
            final var listener = resourceAllocationFailListeners.get(i);
            listener.update(of(listener, cloudlet, resource.getClass(), requested, available, vm.getSimulation().clock()));
        }
    }

    @Override
    public CloudletScheduler addOnCloudletResourceAllocationFail(final EventListener<CloudletResourceAllocationFailEventInfo> listener) {
        if(EventListener.NULL.equals(listener)){
            return this;
        }

        resourceAllocationFailListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnCloudletResourceAllocationFail(final EventListener<CloudletResourceAllocationFailEventInfo> listener) {
        return resourceAllocationFailListeners.remove(listener);
    }

    /**
     * Gets the absolute utilization of a given Cloudlet's resource
     *
     * @param cloudlet the Cloudlet to get the absolute value of RAM utilization
     * @param vmResource the VM resource the cloudlet is requesting
     * @return the current utilization of the requested Cloudlet's resource in absolute value
     */
    private double getCloudletResourceAbsoluteUtilization(
        final Cloudlet cloudlet,
        final ResourceManageable vmResource)
    {
        final UtilizationModel um = cloudlet.getUtilizationModel(vmResource.getClass());
        return um.getUnit() == UtilizationModel.Unit.ABSOLUTE ?
                Math.min(um.getUtilization(), vmResource.getCapacity()) :
                um.getUtilization() * vmResource.getCapacity();
    }

    /**
     * Computes the length of a given cloudlet, in number
     * of Instructions (I), which has been executed since the last time cloudlet
     * processing was updated. The number of executed instructions also takes in consideration
     * if the cloudlet is using virtual memory (VMem / swap).
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
     * @param cle the Cloudlet to compute the executed length
     * @param currentTime current simulation time
     * @return the executed length, in Number of Instructions (I), since the last time cloudlet was processed.
     * @see #updateCloudletsProcessing(double)
     *
     * TODO This method is being called 2 times more than required.
     *      Despite it is not causing any apparent issue, it has to be
     *      investigated. For instance, for simulation time 2, with 2 cloudlets, the
     *      method is being called 4 times instead of just 2 (1 for each cloudlet for
     *      that time).
     */
    private double cloudletExecutedInstructionsForTimeSpan(final CloudletExecution cle, final double currentTime) {
        /* The time the Cloudlet spent executing in fact, since the last time Cloudlet update was
         * called by the scheduler. If it is zero, indicates that the Cloudlet didn't use
         * the CPU in this time span, because it is waiting for its required files
         * to be transferred from the Datacenter storage.
         */
        final double processingTimeSpan = hasCloudletFileTransferTimePassed(cle, currentTime) ? timeSpan(cle, currentTime) : 0;

        final double vMemDelay = getVirtualMemoryDelay(cle, processingTimeSpan);
        final double reducedBwDelay = getBandwidthOverSubscriptionDelay(cle, processingTimeSpan);
        /*If delay is negative, resource was not allocated.
        If RAM and BW could not be allocated, just returns 0 to indicate no processing was performed
        due to lack of other resources.*/
        if(vMemDelay == Double.MIN_VALUE && reducedBwDelay == Double.MIN_VALUE) {
            return 0;
        }

        final double cloudletUsedMips = getAllocatedMipsForCloudlet(cle, currentTime, true);
        final double actualProcessingTime = processingTimeSpan - (validateDelay(vMemDelay) + validateDelay(reducedBwDelay));
        return cloudletUsedMips * actualProcessingTime * Conversion.MILLION;
    }

    /**
     * Validates some delay value. The delay can be related to virtual memory swap
     * or reduction in allocated BW due to over-subscription.
     * When the delay is {@link Double#MIN_VALUE}, it means there is no delay.
     * @param delay the delay to validate
     * @return the given delay when it's valid or 0 to indicate there is no delay
     * when the given value is {@link Double#MIN_VALUE}
     */
    private double validateDelay(final double delay) {
        return delay == Double.MIN_VALUE ? 0 : delay;
    }

    /**
     * Gets the processing delay considering possible access to virtual memory (VMem / swap), if required
     * when there is no enough available RAM due to over-subscription.
     * Use of VMem is performed by swapping memory data, belonging to processes not currently using CPU,
     * from RAM to disk to open up RAM space. Since the disk is way slower than the RAM, that causes
     * the process to wait for that swap to complete.
     *
     * <p>
     *     <b>IMPORTANT - This is a simplified implementation of memory swapping which:</b>
     *     <ul>
     *         <li>doesn't consider the time to get data from RAM (supposed inactive memory pages
     *         which aren't being used by some process) and write in the disk;</li>
     *         <li>just considers the time to read supposed data from the disk back to the RAM.</li>
     *         <li>just considers the first cloudlets (processes) to use virtual memory
     *         will always be the ones to have its performed impacted by delayed processing.
     *         In real operating systems, if a process B requests VMem, inactive memory pages
     *         from a process A will be swapped out to disk. If process A further request
     *         those pages, inactive pages from process B or any other one may be swapped out
     *         to disk. This way, different process may be impacted by swapping overhead.
     *         But here, if process A is the first to use CPU at any time interval,
     *         it will never be impacted by this overhead, but just the process that requests VMem.</li>
     *     </ul>
     * </p>
     *
     * @param cle the cloudlet being processed
     * @param processingTimeSpan the current cloudlet processing time span
     * @return (i) the processing delay in seconds (considering VMem access);
     * (ii) 0 if there is available physical RAM (no over-subscription);
     * (iii) or {@link Double#MIN_VALUE} if the cloudlet is requesting more RAM then the total VM capacity.
     * @see <a href="https://www.kernel.org/doc/gorman/html/understand/understand014.html">Linux Kernel Swap Management</a>
     * @see #getResourceOverSubscriptionDelay(CloudletExecution, double, ResourceManageable, BiPredicate, BiFunction)
     */
    private double getVirtualMemoryDelay(final CloudletExecution cle, final double processingTimeSpan) {
        return getResourceOverSubscriptionDelay(
            cle, processingTimeSpan, ((VmSimple)vm).getRam(),

            /*
             * Since using VMem requires some portion of the RAM to be swapped between the disk
             * to open up RAM space, the required RAM cannot be higher than the RAM capacity
             * neither than the available disk space.
             */
            (vmRam, requestedRam) -> requestedRam <= vmRam.getCapacity() && requestedRam <= vm.getStorage().getAvailableResource(),

            /* Amount of RAM that was not allocated to the Cloudlet due to lack of VM capacity.
             * This way, that extra amount will require virtual memory,
             * delaying cloudlet execution due to Host's drive read latency. */
            (notAllocatedRam, __) -> diskTransferTime(cle, notAllocatedRam));
    }

    /**
     * Gets the time to transfer hard drive from the Host where the VM running a cloudlet is placed.
     * @param cle Cloudlet being processed
     * @param dataSize the size of the data to read from the VM disk (in MB),
     *                 considering the read speed of the underlying Host disk.
     * @return
     */
    private double diskTransferTime(final CloudletExecution cle, final Double dataSize) {
        return cle.getCloudlet().getVm().getHost().getStorage().getTransferTime(dataSize.intValue());
    }

    /**
     * Gets the processing delay considering possible reduction BW allocation, if required
     * when there is no enough available BW due to over-subscription.
     * If a Cloudlet requests a given amount of BW and a smaller amount is allocated,
     * its processing will be delayed because the simulated data transfer
     * will be slowed.
     *
     * @param cle the cloudlet being processed
     * @param processingTimeSpan the current cloudlet processing time span
     * @return (i) the processing delay in seconds (considering reduction in BW allocation);
     * (ii) 0 if there is available BW (no over-subscription);
     * (iii) or {@link Double#MIN_VALUE} if the cloudlet is requesting more BW then the total VM capacity.
     * @see #getResourceOverSubscriptionDelay(CloudletExecution, double, ResourceManageable, BiPredicate, BiFunction)
     */
    private double getBandwidthOverSubscriptionDelay(final CloudletExecution cle, final double processingTimeSpan) {
        return getResourceOverSubscriptionDelay(
            cle, processingTimeSpan, ((VmSimple)vm).getBw(),
            (vmBw, requestedBw) -> requestedBw <= vmBw.getCapacity(),

            /* When some BW cannot be allocated to the Cloudlet (due to over-subscription),
            the delay is computed based on the time needed to use
            the required bandwidth after the reduced allocation.
            For instance, if the required bandwidth is 10mbps, that means
            the cloudlet is willing to transfer 10 mbits in one second.
            If just 8 mbps is allocated to the cloudlet,
            to transfer the same 10 mbits it will take 0,25 second more. */
            (notAllocatedBw, requestedBw) -> requestedBw/(requestedBw-notAllocatedBw) - 1);
    }

    /**
     * Gets the cloudlet processing delay, including a delay when a given
     * resource is oversubscribed, which will cause overhead for cloudlet processing,
     * delaying its completion.
     * If the resource the cloudlet is request is RAM and it's oversubscribed, that
     * will activate virtual memory (swap).
     * If the resource is bandwidth, less data is supposed to be transferred
     * (the bandwidth for cloudlets doesn't transfer actual data, just simulate bandwidth requirements)
     * and the cloudlet execution is delayed too, since the data transfer will take longer.
     *
     * @param cle the Cloudlet being processed
     * @param processingTimeSpan the current cloudlet processing time span
     * @param vmResource the VM resource the cloudlet is requesting (that will be checked if it's oversubscribed)
     * @param suitableCapacityPredicate a {@link BiPredicate} that receives the VM resource being requested and
     *                                  the amount of requested resources,
     *                                  then indicates if the requested capacity can be allocated or not
     *                                  (even total or partially). If the predicate returns false,
     *                                  that means the requested amount exceeds the total resource capacity.
     * @param delayFunction a {@link BiFunction} that receives the amount of resources that couldn't be allocated to the cloudlet
     *                      and the amount requested,
     *                      then returning the additional delay in cloudlet processing caused by that
     * @return (i) the processing delay in seconds;
     * (ii) 0 if there is available physical resource (no over-subscription);
     * (iii) or {@link Double#MIN_VALUE} if the cloudlet is requesting more resource then the total VM capacity.
     * @see #getVirtualMemoryDelay(CloudletExecution, double)
     * @see #getBandwidthOverSubscriptionDelay(CloudletExecution, double)
     */
    private double getResourceOverSubscriptionDelay(
        final CloudletExecution cle, final double processingTimeSpan,
        final ResourceManageable vmResource,
        final BiPredicate<ResourceManageable, Double> suitableCapacityPredicate,
        final BiFunction<Double, Double, Double> delayFunction)
    {
        final double requestedResource = getCloudletResourceAbsoluteUtilization(cle.getCloudlet(), vmResource);

        /* If the requested resource is not suitable (even for over-subscription),
        considering the total VM capacity.
        Returns a negative value to indicate the allocation was not possible*/
        if(!suitableCapacityPredicate.test(vmResource, requestedResource)) {
            cle.incOverSubscriptionDelay(processingTimeSpan);
            return Double.MIN_VALUE;
        }

        /* Amount of resource that was not allocated to the Cloudlet due to lack of VM capacity.
         * This way, that extra amount will cause delay in cloudlet execution,
         * since the cloudlet will wait for that non-allocated resource until the next processing time. */
        final double notAllocatedResource = Math.max(requestedResource - vmResource.getAvailableResource(), 0);
        if (notAllocatedResource > 0) {
            final double delay = delayFunction.apply(notAllocatedResource, requestedResource);
            cle.incOverSubscriptionDelay(delay);
            return delay;
        }

        return 0;
    }

    /**
     * Checks if the time to transfer the files required by a Cloudlet to
     * execute has already passed, in order to start executing the Cloudlet in
     * fact.
     *
     * @param cle         Cloudlet to check if the time to transfer the files has passed
     * @param currentTime the current simulation time
     * @return true if the time to transfer the files has passed, false
     * otherwise
     */
    private boolean hasCloudletFileTransferTimePassed(final CloudletExecution cle, final double currentTime) {
        return cle.getFileTransferTime() == 0 ||
               currentTime - cle.getCloudletArrivalTime() > cle.getFileTransferTime() ||
               cle.getCloudlet().getFinishedLengthSoFar() > 0;
    }

    /**
     * Computes the time span between the current simulation time and the last
     * time the processing of a cloudlet was updated.
     *
     * @param cle the cloudlet to compute the execution time span
     * @param currentTime the current simulation time
     * @return
     */
    protected double timeSpan(final CloudletExecution cle, final double currentTime) {
        return currentTime - cle.getLastProcessingTime();
    }

    /**
     * Removes finished cloudlets from the
     * {@link #getCloudletExecList() list of cloudlets to execute}
     * and adds them to finished list.
     *
     * @return the number of finished cloudlets removed from the
     * {@link #getCloudletExecList() execution list}
     */
    private int addCloudletsToFinishedList() {
        final List<CloudletExecution> finishedCloudlets
            = cloudletExecList.stream()
            .filter(cle -> cle.getCloudlet().isFinished())
            .collect(toList());

        for (final CloudletExecution c : finishedCloudlets) {
            addCloudletToFinishedList(c);
        }

        return finishedCloudlets.size();
    }

    private void addCloudletToFinishedList(final CloudletExecution cle) {
        setCloudletFinishTimeAndAddToFinishedList(cle);
        removeCloudletFromExecList(cle);
    }

    /**
     * Removes a Cloudlet from the list of cloudlets in execution.
     *
     * @param cle the Cloudlet to be removed
     * @return the removed Cloudlet or {@link CloudletExecution#NULL} if not found
     */
    protected CloudletExecution removeCloudletFromExecList(final CloudletExecution cle) {
        removeUsedPes(cle.getNumberOfPes());
        return cloudletExecList.remove(cle) ? cle : CloudletExecution.NULL;
    }

    /**
     * Sets the finish time of a cloudlet and adds it to the
     * finished list.
     *
     * @param cle the cloudlet to set the finish time
     */
    private void setCloudletFinishTimeAndAddToFinishedList(final CloudletExecution cle) {
        final double clock = vm.getSimulation().clock();
        cloudletFinish(cle);
        cle.setFinishTime(clock);
    }

    /**
     * Gets the estimated time when a given cloudlet is supposed to finish
     * executing. It considers the amount of Vm PES and the sum of PEs required
     * by all VMs running inside the VM.
     *
     * @param cle         cloudlet to get the estimated finish time
     * @param currentTime current simulation time
     * @return the estimated finish time of the given cloudlet
     * (which is a relative delay from the current simulation time)
     */
    protected double cloudletEstimatedFinishTime(final CloudletExecution cle, final double currentTime) {
        final double cloudletAllocatedMips = getAllocatedMipsForCloudlet(cle, currentTime);
        cle.setLastAllocatedMips(cloudletAllocatedMips);
        final double remainingLifeTime = cle.getRemainingLifeTime();

        /* If no MIPS were currently allocated for the Cloudlet,
         * it would cause a division by zero when trying to compute the estimated finish time.
         * In such a case, gets the last allocated MIPS to compute that.
         * That value will be the current allocated MIPS if some MIPS were
         * actually allocated or the previous allocated MIPS otherwise.*/
        final double finishTimeForRemainingLen = cle.getRemainingCloudletLength() / cle.getLastAllocatedMips();

        final double estimatedFinishTime = Math.min(remainingLifeTime, finishTimeForRemainingLen);
        return Math.max(estimatedFinishTime, vm.getSimulation().getMinTimeBetweenEvents());
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
     * perform context switch just after the currently running Cloudlets
     * completely finish executing.
     * <p>
     * <p>
     * This method is called internally by the
     * {@link CloudletScheduler#updateProcessing(double, MipsShare)}.</p>
     * @param currentTime current simulation time
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     */
    protected double moveNextCloudletsFromWaitingToExecList(final double currentTime) {
        Optional<CloudletExecution> optional = Optional.of(CloudletExecution.NULL);
        double nextCloudletFinishTime = Double.MAX_VALUE;
        while (!cloudletWaitingList.isEmpty() && optional.isPresent()) {
            optional = findSuitableWaitingCloudlet();
            final double estimatedFinishTime =
                optional
                    .map(this::addWaitingCloudletToExecList)
                    .map(cle -> cloudletEstimatedFinishTime(cle, currentTime))
                    .orElse(Double.MAX_VALUE);
            nextCloudletFinishTime = Math.min(nextCloudletFinishTime, estimatedFinishTime);
        }

        return nextCloudletFinishTime;
    }

    /**
     * Try to find the first Cloudlet in the waiting list that the number of
     * required PEs is not higher than the number of free PEs.
     *
     * @return an {@link Optional} containing the found Cloudlet or an empty
     * Optional otherwise
     */
    protected Optional<CloudletExecution> findSuitableWaitingCloudlet() {
        return cloudletWaitingList
                .stream()
                .filter(cle -> cle.getCloudlet().getStatus() != Status.FROZEN)
                .filter(this::canExecuteCloudlet)
                .findFirst();
    }

    /**
     * Checks if the amount of PEs required by a given Cloudlet is free to use.
     *
     * @param cle the Cloudlet to get the number of required PEs
     * @return true if there is the amount of free PEs, false otherwise
     */
    protected boolean isThereEnoughFreePesForCloudlet(final CloudletExecution cle) {
        return vm.getProcessor().getAvailableResource() >= cle.getNumberOfPes();
    }

    /**
     * Removes a Cloudlet from waiting list and adds it to the exec list.
     * @param cle the cloudlet to add to to exec list
     * @return the given cloudlet
     */
    protected CloudletExecution addWaitingCloudletToExecList(final CloudletExecution cle) {
        /*If the Cloudlet is not found in the waiting List, there is no problem.
        * Just add it to the exec List.*/
        cloudletWaitingList.remove(cle);
        addCloudletToExecList(cle);
        return cle;
    }

    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public void setVm(final Vm vm) {
        if (isOtherVmAssigned(requireNonNull(vm))) {
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
        return currentMipsShare.pes() - getUsedPes();
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
        this.taskScheduler = requireNonNull(taskScheduler);
        this.taskScheduler.setVm(vm);
    }

    @Override
    public boolean isThereTaskScheduler() {
        return taskScheduler != null && taskScheduler != CloudletTaskScheduler.NULL;
    }

    @Override
    public double getRequestedCpuPercent(final double time) {
        return getRequestedOrAllocatedCpuPercentUtilization(time, true);
    }

    @Override
    public double getAllocatedCpuPercent(final double time) {
        return getRequestedOrAllocatedCpuPercentUtilization(time, false);
    }

    private double getRequestedOrAllocatedCpuPercentUtilization(final double time, final boolean requestedUtilization) {
        return cloudletExecList.stream()
            .map(CloudletExecution::getCloudlet)
            .mapToDouble(cloudlet -> getAbsoluteCloudletCpuUtilizationForAllPes(time, cloudlet, requestedUtilization))
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
    private double getAbsoluteCloudletCpuUtilizationForAllPes(final double time, final Cloudlet cloudlet, final boolean requestedUtilization) {
        final double cloudletCpuUsageForOnePe =
            getAbsoluteCloudletResourceUtilization(
                cloudlet, cloudlet.getUtilizationModelCpu(), time, getAvailableMipsByPe(), "CPU", requestedUtilization);

        return cloudletCpuUsageForOnePe * cloudlet.getNumberOfPes();
    }

    /**
     * Gets the current requested MIPS for a given cloudlet.
     *
     * @param cle the ce
     * @param time the time
     * @return the current requested mips for the given cloudlet
     */
    protected double getRequestedMipsForCloudlet(final CloudletExecution cle, final double time) {
        final Cloudlet cloudlet = cle.getCloudlet();
        return getAbsoluteCloudletResourceUtilization(cloudlet, cloudlet.getUtilizationModelCpu(), time, vm.getMips(), "CPU", true);
    }

    /**
     * Gets the current allocated MIPS for cloudlet.
     *
     * @param cle the ce
     * @param time the time
     * @return the current allocated mips for cloudlet
     */
    public double getAllocatedMipsForCloudlet(final CloudletExecution cle, final double time) {
        return getAllocatedMipsForCloudlet(cle, time, false);
    }

    /**
     * Gets the current allocated MIPS for cloudlet.
     *
     * @param cle the ce
     * @param time the time
     * @param log Indicate if a log should be issued when the requested resource is larger than the capacity
     * @return the current allocated mips for cloudlet
     */
    public double getAllocatedMipsForCloudlet(final CloudletExecution cle, final double time, final boolean log) {
        final Cloudlet cloudlet = cle.getCloudlet();
        final String resourceName = log ? "CPU" : "";
        return getAbsoluteCloudletResourceUtilization(cloudlet, cloudlet.getUtilizationModelCpu(), time, getAvailableMipsByPe(), resourceName, false);
    }

    @Override
    public double getCurrentRequestedBwPercentUtilization() {
        return cloudletExecList.stream()
            .map(CloudletExecution::getCloudlet)
            .mapToDouble(cl -> getAbsoluteCloudletResourceUtilization(cl, cl.getUtilizationModelBw(), vm.getBw().getCapacity(), "BW"))
            .sum() / vm.getBw().getCapacity();
    }

    @Override
    public double getCurrentRequestedRamPercentUtilization() {
        return cloudletExecList.stream()
            .map(CloudletExecution::getCloudlet)
            .mapToDouble(cl -> getAbsoluteCloudletResourceUtilization(cl, cl.getUtilizationModelRam(), vm.getRam().getCapacity(), "RAM"))
            .sum() / vm.getRam().getCapacity();
    }

    /**
     * Computes the absolute amount of a resource used by a given Cloudlet
     * for the current simulation time, based on the maximum amount of resource that the Cloudlet can use
     * this time.
     *
     * @param cloudlet Cloudlet requesting the resource
     * @param model                   the {@link UtilizationModel} to get the absolute amount of resource used by the Cloudlet
     * @param maxResourceAllowedToUse the maximum absolute resource that the Cloudlet will be allowed to use
     * @param resource name of the resource being requested
     * @return the absolute amount of resource that the Cloudlet will use
     */
    private double getAbsoluteCloudletResourceUtilization(
        final Cloudlet cloudlet, final UtilizationModel model,
        final double maxResourceAllowedToUse, final String resource)
    {
        return getAbsoluteCloudletResourceUtilization(cloudlet, model, vm.getSimulation().clock(), maxResourceAllowedToUse, resource, true);
    }

    /**
     * Computes the absolute amount of a resource used or requested by a given Cloudlet
     * for a given time, based on the maximum amount of resource that the Cloudlet can use
     * this time.
     *
     * @param cloudlet Cloudlet requesting the resource
     * @param model                   the {@link UtilizationModel} to get the absolute amount of resource used by the Cloudlet
     * @param time                    the simulation time
     * @param maxResourceAllowedToUse the maximum absolute resource that the Cloudlet will be allowed to use
     * @param resourceName name of the resource being requested. If an empty string is given, no
     *                     warning is issued if the requested amount of resource is larger than its capacity
     * @param requestedUtilization indicate if the actual requested resource utilization should be returned
     *                             instead of just the allocated utilization.
     *                             Consider the cloudlet is requesting more than 100% of the resource capacity, then if:
     *                             i) this param is true, the total requested capacity is returned;
     *                             ii) this param is false, the used capacity is returned.
     * @return the absolute amount of resource that the Cloudlet is using or has requested
     */
    private double getAbsoluteCloudletResourceUtilization(
        final Cloudlet cloudlet,
        final UtilizationModel model,
        final double time,
        final double maxResourceAllowedToUse,
        final String resourceName,
        final boolean requestedUtilization)
    {
        if (model.getUnit() == UtilizationModel.Unit.ABSOLUTE) {
            return Math.min(model.getUtilization(time), maxResourceAllowedToUse);
        }

        final double requestedPercent = model.getUtilization();
        final double allocatedPercent = requestedUtilization ? requestedPercent : Math.min(requestedPercent, 1);

        //Shows the log when the method is called to return the actual allocated resource amount (not the requested one)
        if(requestedPercent > 1 && !requestedUtilization && !resourceName.isEmpty()) {
            LOGGER.warn(
                "{}: {}: {} is requesting {}% of the total {} capacity which cannot be allocated. Allocating {}%.",
                vm.getSimulation().clockStr(), getClass().getSimpleName(), cloudlet,
                requestedPercent*100, resourceName, allocatedPercent*100);
        }

        return allocatedPercent * maxResourceAllowedToUse;
    }

    /**
     * Gets a <b>read-only</b> list of Cloudlets that finished executing and were returned the their broker.
     * A Cloudlet is returned to notify the broker about the end of its execution.
     * @return
     */
    protected Set<Cloudlet> getCloudletReturnedList() {
        return Collections.unmodifiableSet(cloudletReturnedList);
    }

    @Override
    public void addCloudletToReturnedList(final Cloudlet cloudlet) {
        this.cloudletReturnedList.add(cloudlet);
    }

    @Override
    public void deallocatePesFromVm(final long pesToRemove) {
        final long removedPes = currentMipsShare.remove(pesToRemove);
        removeUsedPes(removedPes);
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

    /**
	 * Checks if a Cloudlet can be added to the execution list or not.
	 * Each CloudletScheduler can define a different policy to
	 * indicate if a Cloudlet can be added to that list at the moment this method is called.
	 *
	 * <p>For instance, time-shared implementations can put all
	 * Cloudlets in the execution list, once it uses a preemptive policy
	 * that shares the CPU time between all running Cloudlets,
	 * even there are more Cloudlets than the number of CPUs.
	 * That is, it might always add new Cloudlets to the execution list.
	 * </p>
	 *
	 * <p>On the other hand, space-shared schedulers do not share
	 * the same CPUs between different Cloudlets. In this type of
	 * scheduler, a CPU is only allocated to a Cloudlet when the previous
	 * Cloudlet finished its entire execution.
	 * That is, it might not always add new Cloudlets to the execution list.</p>
	 *
	 * @param cle Cloudlet to check if it can be added to the execution list
	 * @return true if the Cloudlet can be added to the execution list, false otherwise
	 */
    private boolean canExecuteCloudlet(final CloudletExecution cle){
        return cle.getCloudlet().getStatus().ordinal() < Status.FROZEN.ordinal() && canExecuteCloudletInternal(cle);
    }

    /**
     * @see #canExecuteCloudlet(CloudletExecution)
     */
    protected abstract boolean canExecuteCloudletInternal(CloudletExecution cle);

    @Override
    public void clear() {
        this.cloudletWaitingList.clear();
        this.cloudletExecList.clear();
    }
}
