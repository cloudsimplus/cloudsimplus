/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.cloudlet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.listeners.CloudletResourceAllocationFailEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.resources.Bandwidth;
import org.cloudsimplus.resources.Ram;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

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
 * scheduling policies performed by a {@link Vm} to run its
 * {@link Cloudlet Cloudlets}. Each VM must have its own instance of a CloudletScheduler.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
@Getter
public abstract non-sealed class CloudletSchedulerAbstract implements CloudletScheduler {
    @Serial
    private static final long serialVersionUID = -2314361120790372742L;

    private Vm vm;
    private CloudletTaskScheduler taskScheduler;

    /**
     * The current MIPS capacity from the VM that will be
     * made available to the scheduler. This MIPS share will be allocated
     * to Cloudlets as requested.
     */
    private MipsShare currentMipsShare;

    private double previousTime;

    private final List<CloudletExecution> cloudletFinishedList;

    /** {@return the list} of currently paused cloudlets. */
    private final List<CloudletExecution> cloudletPausedList;

    /** {@return the list} of cloudlets that failed executing. */
    private final List<CloudletExecution> cloudletFailedList;

    /** @see #getCloudletExecList() */
    private final List<CloudletExecution> cloudletExecList;

    /** @see #getCloudletWaitingList() */
    private final List<CloudletExecution> cloudletWaitingList;

    /** @see #getCloudletReturnedList() */
    private final Set<Cloudlet> cloudletReturnedList;

    /** @see #getCloudletSubmittedList() */
    private final List<Cloudlet> cloudletSubmittedList;

    private boolean cloudletSubmittedListEnabled;

    /** @see #addOnCloudletResourceAllocationFail(EventListener) */
    @Getter(AccessLevel.NONE)
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
    public final void setPreviousTime(final double previousTime) {
        this.previousTime = previousTime;
    }

    /**
     * Sets current MIPS share available for the VM using the scheduler.
     *
     * @param currentMipsShare the new current MIPS share
     * @see #getCurrentMipsShare()
     */
    protected void setCurrentMipsShare(@NonNull final MipsShare currentMipsShare) {
        if(currentMipsShare.pes() > vm.getPesNumber()){
            LOGGER.warn("Requested {} PEs but {} has just {}", currentMipsShare.pes(), vm, vm.getPesNumber());
            this.currentMipsShare = new MipsShare(vm.getPesNumber(), currentMipsShare.mips());
        }
        else this.currentMipsShare = currentMipsShare;
    }

    /**
     * Gets the amount of MIPS available (free) for each Processor PE,
     * considering the currently executing cloudlets in this processor
     * and the number of PEs these cloudlets require.
     * This is the amount of MIPS that each Cloudlet is allowed to use,
     * since the processor is shared among all executing cloudlets.
     *
     * <p>In the case of space shared schedulers,
     * there is no concurrency for PEs because some cloudlets
     * may wait in a queue until there are available PEs to be used
     * exclusively by them.</p>
     *
     * @return the amount of available MIPS for each Processor PE.
     */
    public double getAvailableMipsByPe(){
        /*
         * TODO  Splitting the capacity of a CPU core among different applications
         *       inside a VM is not in fact possible (unless containers are used).
         *       This was just an oversimplification
         *       performed by the CloudletSchedulerTimeShared that may affect
         *       other schedulers such as the CloudletSchedulerCompletelyFair,
         *       which in fact performs task preemption.
        */
        final long totalAllExecCloudletsPes = totalAllExecCloudletsPes();
        if(totalAllExecCloudletsPes > currentMipsShare.pes()) {
            return getTotalMipsShare() / totalAllExecCloudletsPes;
        }

        return getPeCapacity();
    }

    private Double getPeCapacity() {
        return currentMipsShare.mips();
    }

    /**
     * @return the total number of PEs from all cloudlets currently executing in this scheduler.
     */
    private long totalAllExecCloudletsPes() {
        return cloudletExecList.stream()
                               .map(CloudletExecution::getCloudlet)
                               .mapToLong(Cloudlet::getPesNumber).sum();
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
        final var msg = "{}: The list of submitted Cloudlets for {} is empty maybe because you didn't enabled it by calling enableCloudletSubmittedList().";
        if(cloudletSubmittedList.isEmpty() && !cloudletSubmittedListEnabled) {
            LOGGER.warn(msg, getClass().getSimpleName(), vm);
        }

        return (List<T>) cloudletSubmittedList;
    }

    @Override
    public CloudletScheduler enableCloudletSubmittedList() {
        this.cloudletSubmittedListEnabled = true;
        return this;
    }

    protected void addCloudletToWaitingList(final CloudletExecution cle) {
        if(requireNonNull(cle) == CloudletExecution.NULL){
            return;
        }

        if(cle.getCloudlet().getStatus() != Cloudlet.Status.FROZEN) {
            cle.setStatus(Cloudlet.Status.QUEUED);
        }
        cloudletWaitingList.add(cle);
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
        if(cloudletSubmittedListEnabled) {
            cloudletSubmittedList.add(cloudlet);
        }

        return cloudletSubmitInternal(new CloudletExecution(cloudlet), fileTransferTime);
    }

    /**
     * Receives the execution information of a Cloudlet to be executed in the VM managed by this scheduler.
     *
     * @param cle the submitted cloudlet
     * @param fileTransferTime time required to move the required files from the SAN to the VM (in seconds)
     * @return expected finish time of this cloudlet (considering the time to transfer required
     * files from the Datacenter to the Vm), or 0 if it is in a waiting queue
     * @see #cloudletSubmit(Cloudlet, double)
     */
    protected double cloudletSubmitInternal(final CloudletExecution cle, final double fileTransferTime) {
        if (canExecuteCloudlet(cle)) {
            cle.setStatus(Cloudlet.Status.INEXEC);
            cle.setFileTransferTime(fileTransferTime);
            addCloudletToExecList(cle);

            return getEstimatedFinishTime(cle.getCloudlet(), fileTransferTime);
        }

        // No enough free PEs, then add Cloudlet to the waiting queue
        addCloudletToWaitingList(cle);
        return 0.0;
    }

    /**
     * {@return the Cloudlet estimated finish time}
     * @param cloudlet the Cloudlet to check
     * @param fileTransferTime time required to move the required files from the SAN to the VM (in seconds)
     */
    private double getEstimatedFinishTime(final Cloudlet cloudlet, final double fileTransferTime) {
        // Estimated total time if no lifetime is set
        final double estimatedTotalTime = fileTransferTime + Math.abs(cloudlet.getLength() / getPeCapacity());
        return Math.min(cloudlet.getLifeTime(), estimatedTotalTime);
    }

    /**
     * Adds a Cloudlet to the list of cloudlets in execution.
     * @param cle the Cloudlet to be added
     */
    protected void addCloudletToExecList(final CloudletExecution cle) {
        cle.setStatus(Cloudlet.Status.INEXEC);
        cle.setLastProcessingTime(getVm().getSimulation().clock());
        cloudletExecList.add(cle);
        addUsedPes(cle.getPesNumber());
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

        // Gets all elements in each list and makes them a single full list,
        // returning the first Cloudlet with the given id
        return cloudletExecInfoListStream
            .flatMap(List::stream)
            .filter(cle -> cle.getId() == cloudletId)
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
            .filter(cle -> cle.getId() == cloudlet.getId())
            .findFirst();
    }

    /**
     * Processes a finished cloudlet.
     * @param cle the finished cloudlet to process
     */
    protected void cloudletFinish(final CloudletExecution cle) {
        cle.setStatus(Cloudlet.Status.SUCCESS);
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
        cloudlet.setStatus(Cloudlet.Status.READY);

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
        changeStatusOfCloudlet(cle, cle.getCloudlet().getStatus(), Cloudlet.Status.READY);
    }

    @Override
    public boolean cloudletPause(final Cloudlet cloudlet) {
        if (changeStatusOfCloudletIntoList(cloudletExecList, cloudlet, this::changeInExecToPaused)) {
            return true;
        }

        return changeStatusOfCloudletIntoList(cloudletWaitingList, cloudlet, this::changeReadyToPaused);
    }

    private void changeInExecToPaused(final CloudletExecution cle) {
        changeStatusOfCloudlet(cle, Cloudlet.Status.INEXEC, Cloudlet.Status.PAUSED);
        removeUsedPes(cle.getPesNumber());
    }

    private void changeReadyToPaused(final CloudletExecution cle) {
        changeStatusOfCloudlet(cle, Cloudlet.Status.READY, Cloudlet.Status.PAUSED);
    }

    @Override
    public Cloudlet cloudletFail(final Cloudlet cloudlet) {
        return stopCloudlet(cloudlet, Cloudlet.Status.FAILED);
    }

    @Override
    public Cloudlet cloudletCancel(final Cloudlet cloudlet) {
        return stopCloudlet(cloudlet, Cloudlet.Status.CANCELED);
    }

    /**
     * Sets a Cloudlet as {@link Cloudlet.Status#CANCELED} or {@link Cloudlet.Status#FAILED}.
     * @param cloudlet the Cloudlet to stop executing
     * @param stopStatus the stop status, either {@link Cloudlet.Status#CANCELED} or {@link Cloudlet.Status#FAILED}
     * @return the stopped cloudlet or {@link Cloudlet#NULL} if not found
     */
    private Cloudlet stopCloudlet(final Cloudlet cloudlet, final Cloudlet.Status stopStatus) {
        //Removes finished cloudlets from the list without changing its status
        boolean found = changeStatusOfCloudletIntoList(cloudletFinishedList, cloudlet, cle -> {});
        if (found) {
            return cloudlet;
        }

        found = changeStatusOfCloudletIntoList(
            cloudletExecList, cloudlet,
            cle -> changeStatusOfCloudlet(cle, Cloudlet.Status.INEXEC, stopStatus));
        if (found) {
            return cloudlet;
        }

        found = changeStatusOfCloudletIntoList(
            cloudletPausedList, cloudlet,
            cle -> changeStatusOfCloudlet(cle, Cloudlet.Status.PAUSED, stopStatus));
        if (found) {
            return cloudlet;
        }

        changeStatusOfCloudletIntoList(
            cloudletWaitingList, cloudlet,
            cle -> changeStatusOfCloudlet(cle, Cloudlet.Status.READY, stopStatus));
        if (found) {
            return cloudlet;
        }

        return Cloudlet.NULL;
    }

    /**
     * Changes the status of a given cloudlet.
     *
     * @param cle      Cloudlet to change its status
     * @param currentStatus the current cloudlet status
     * @param newStatus     the new status to set
     * TODO The parameter currentStatus only exists
     *      because apparently, the cloudlet status is not being accordingly changed
     *      along the simulation run.
     */
    private void changeStatusOfCloudlet(final CloudletExecution cle, final Cloudlet.Status currentStatus, final Cloudlet.Status newStatus) {
        if ((currentStatus == Cloudlet.Status.INEXEC || currentStatus == Cloudlet.Status.READY) && cle.getCloudlet().isFinished())
            cloudletFinish(cle);
        else cle.setStatus(newStatus);

        if (newStatus == Cloudlet.Status.PAUSED)
            cloudletPausedList.add(cle);
        else if (newStatus == Cloudlet.Status.READY)
            addCloudletToWaitingList(cle);
    }

    /**
     * Search for a cloudlet into a given list to change its status and
     * remove it from that list.
     *
     * @param cloudletList                the list where to search the cloudlet
     * @param cloudlet                    the id of the cloudlet to have its status changed
     * @param cloudletStatusUpdaterConsumer a {@link Consumer} that will apply
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
     * Deallocates the total used capacity from VM RAM and Bandwidth
     * so that the allocation can be updated when running Cloudlets are processed.
     */
    private void deallocateVmResources() {
        ((VmSimple)vm).getRam().deallocateAllResources();
        ((VmSimple)vm).getBw().deallocateAllResources();
    }

    /**
     * Updates the processing of all cloudlets from the Vm using this scheduler
     * that are in the {@link #getCloudletExecList() cloudlet execution list}.
     *
     * @param currentTime the current simulation time
     * @return the next time to update cloudlets processing
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private double updateCloudletsProcessing(final double currentTime) {
        double nextProcessing = Double.MAX_VALUE;
        long usedPes = 0;
        /* Uses an indexed loop to avoid ConcurrentModificationException,
         * e.g., in cases when the Cloudlet is canceled during simulation execution. */
        for (int i = 0; i < cloudletExecList.size(); i++) {
            final CloudletExecution cle = cloudletExecList.get(i);
            nextProcessing = Math.min(nextProcessing, updateCloudletProcessing(cle, currentTime));
            usedPes += cle.getCloudlet().getPesNumber();
        }

        ((VmSimple) vm).setFreePesNumber(vm.getPesNumber() - usedPes);

        return nextProcessing;
    }

    /**
     * Updates the processing of a specific cloudlet from the Vm using this scheduler.
     *
     * @param cle         the cloudlet to be its processing updated
     * @param currentTime current simulation time
     * @return the next time to update the cloudlet processing
     * (which is a relative delay from the current simulation time)
     * This will be the delay incurred in cloudlet processing caused by (i) vMem usage or (ii) BW reduction due to oversubscription.
     * If no delay is incurred, it returns the expected cloudlet completion time.
     */
    protected double updateCloudletProcessing(final CloudletExecution cle, final double currentTime) {
        if(cle.lastOverSubscriptionDelayNotPassed(currentTime)){
            return cle.remainingOverSubscriptionDelay(currentTime);
        }

        final long cloudletUsedMips = (long)getAllocatedMipsForCloudlet(cle, currentTime, true);

        /* The time the Cloudlet spent actually executing, since the last time Cloudlet update was
         * called by the scheduler. If it is zero, that indicates the Cloudlet didn't use
         * the CPU in this time span, because it is waiting for its required files
         * to be transferred from the Datacenter storage.
         */
        final double processingTimeSpan = cle.processingTimeSpan(currentTime);

        if(cle.hasLastOverSubscriptionDelay())
            cle.setLastOverSubscriptionDelay(0);

        final double vMemDelaySecs = getVirtualMemoryDelay(cle, processingTimeSpan);
        final double reducedBwDelaySecs = getBandwidthOverSubscriptionDelay(cle, processingTimeSpan);
        /* If delay is negative, resource was not allocated.
        If RAM and BW could not be allocated, just returns 0 to indicate no processing was performed
        due to lack of other resources. */
        if(vMemDelaySecs == Double.MIN_VALUE && reducedBwDelaySecs == Double.MIN_VALUE) {
            return 0;
        }

        final double maxDelay = getMaxOversubscriptionDelay(vMemDelaySecs, reducedBwDelaySecs);
        final long partialFinishedMI = (long)(cloudletUsedMips * processingTimeSpan);

        if(taskScheduler.isTimeToUpdateCloudletProcessing(cle.getCloudlet())){
            cle.updateProcessing(partialFinishedMI);
            updateVmResourceAbsoluteUtilization(cle, ((VmSimple)vm).getRam());
            updateVmResourceAbsoluteUtilization(cle, ((VmSimple)vm).getBw());
        }

        taskScheduler.processCloudletTasks(cle.getCloudlet(), partialFinishedMI);
        final double estimatedFinishTime = cloudletEstimatedFinishTime(cle, currentTime);
        cle.setLastOverSubscriptionDelay(maxDelay);
        return maxDelay == 0 ? estimatedFinishTime : Math.min(maxDelay, estimatedFinishTime);
    }

    /**
     * {@return the maximum delay caused by oversubscription of resources}
     * We have two scenarios: (i) Bandwidth and memory demand will happen at the same time or (ii) they happen separately.
     * Consider there is oversubscription on both resources. Since requests are sent
     * in parallel, the total time the Cloudlet will wait when there is BW and/or RAM oversubscription
     * is the max delay caused by either BW reduction or virtual memory utilization in
     * oversubscription scenarios.
     * @param vMemDelaySecs the delay caused by virtual memory utilization due to RAM oversubscription
     * @param reducedBwDelaySecs the delay caused by BW reduction due to BW oversubscription
     */
    private double getMaxOversubscriptionDelay(final double vMemDelaySecs, final double reducedBwDelaySecs) {
        return validateDelay(Math.max(vMemDelaySecs, reducedBwDelaySecs));
    }

    /**
     * Updates the VM utilization of a given resource, based on the current utilization of a
     * running Cloudlet, which depends on the Cloudlet's {@link UtilizationModel} for that resource.
     *
     * @param vmResource the kind of resource to update its utilization (usually {@link Ram} or {@link Bandwidth}).
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
            final String msg1 = available > 0 ? "just %d was available".formatted(available): "no amount is available.";
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
        // Uses reversed indexed loop to avoid ConcurrentModificationException if some Listener is deregistered during loop
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

    /// Gets the processing delay (in seconds) considering possible access to virtual memory (VMem / swap), if required
    /// when there is no enough available RAM due to over-subscription.
    /// Use of VMem is performed by swapping memory data, belonging to processes not currently using CPU,
    /// from RAM to disk to open up RAM space. Since the disk is way slower than the RAM, that causes
    /// the process to wait for that swap to complete.
    ///
    /// **IMPORTANT: This is a simplified implementation of memory swapping which:**
    ///
    /// - doesn't consider the time to get data from RAM (supposed inactive memory pages
    /// which aren't being used by some process) and write in the disk;
    /// - just considers that the time to read supposed data from the disk back to the RAM.
    /// - just considers that the first cloudlets (processes) to use virtual memory
    /// will always be the ones to have its performed impacted by delayed processing.
    /// In real operating systems, if a process Y requests VMem, inactive memory pages
    /// from a process X will be swapped out to disk. If process A further request
    /// those pages, inactive pages from process Y or any other one may be swapped out
    /// to disk. This way, different processes may be impacted by swapping overhead.
    /// But here, if a process X is the first to use CPU at any time interval,
    /// it will never be impacted by this overhead, but just the process that requests VMem.
    ///
    /// @param cle the cloudlet being processed
    /// @param processingTimeSpan the current cloudlet processing time span
    /// @return (i) the processing delay in seconds (considering vMem access);
    /// (ii) 0 if there is available physical RAM (no over-subscription), therefore no vMem is required;
    /// (iii) or [Double#MIN_VALUE] if the cloudlet is requesting more RAM than the total VM capacity.
    /// @link [Linux Kernel Swap Management](https://www.kernel.org/doc/gorman/html/understand/understand014.html)
    /// @see #getResourceOverSubscriptionDelay(CloudletExecution, double, ResourceManageable, BiPredicate, BiFunction)
    private double getVirtualMemoryDelay(final CloudletExecution cle, final double processingTimeSpan) {
        return getResourceOverSubscriptionDelay(
            cle, processingTimeSpan, ((VmSimple)vm).getRam(),

            /* Since using vMem requires some portion of the RAM to be swapped between the disk
             * to open up RAM space, the required RAM cannot be higher than the RAM capacity
             * neither than the available disk space. */
            (vmRam, requestedRam) -> requestedRam <= vmRam.getCapacity() && requestedRam <= vm.getStorage().getAvailableResource(),

            /* Amount of RAM that was not allocated to the Cloudlet due to lack of VM capacity.
             * This way, that extra amount will require virtual memory,
             * delaying cloudlet execution due to Host's drive read latency. */
            (notAllocatedRam, __) -> diskTransferTime(cle, notAllocatedRam));
    }

    /**
     * @return the time to transfer hard drive from the Host where the VM running a cloudlet is placed.
     * @param cle Cloudlet being processed
     * @param dataSize the size of the data to read from the VM disk (in MB),
     *                 considering the read speed of the underlying Host disk.
     */
    private double diskTransferTime(final CloudletExecution cle, final Double dataSize) {
        return cle.getCloudlet().getVm().getHost().getStorage().getTransferTime(dataSize.intValue());
    }

    /**
     * Gets the processing delay (in seconds) considering possible reduction BW allocation, if required
     * when there is no enough available BW due to over-subscription.
     * If a Cloudlet requests a given amount of BW and a smaller amount is allocated,
     * its processing will be delayed because the simulated data transfer
     * will be slowed.
     *
     * @param cle the cloudlet being processed
     * @param processingTimeSpan the current cloudlet processing time span
     * @return (i) the processing delay in seconds (considering reduction in BW allocation);
     * (ii) 0 if there is available BW (no over-subscription);
     * (iii) or {@link Double#MIN_VALUE} if the cloudlet is requesting more BW than the total VM capacity.
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
            to transfer the same 10 mbits it will take 0.25 second more. */
            (notAllocatedBw, requestedBw) -> requestedBw/(requestedBw-notAllocatedBw) - 1);
    }

    /**
     * Gets the cloudlet processing delay, including a delay when a given
     * resource is oversubscribed, which will cause overhead for cloudlet processing,
     * delaying its completion.
     * If the cloudlet is requesting RAM and that is oversubscribed, it will activate virtual memory (swap).
     * If the resource is bandwidth, less data is supposed to be transferred
     * (the bandwidth for cloudlets doesn't transfer actual data, just simulate bandwidth requirement),
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
        considering the total VM capacity, returns a negative value to indicate the allocation was not possible.
        */
        if(!suitableCapacityPredicate.test(vmResource, requestedResource)) {
            return Double.MIN_VALUE;
        }

        /* Amount of resource that was not allocated to the Cloudlet due to lack of VM capacity.
         * This way, that extra amount will cause delay in cloudlet execution,
         * since the cloudlet will wait for that non-allocated resource until the next processing time. */
        final double notAllocatedResource = Math.max(requestedResource - vmResource.getAvailableResource(), 0);
        if (notAllocatedResource > 0) {
            return delayFunction.apply(notAllocatedResource, requestedResource);
        }

        return 0;
    }

    /// Removes finished cloudlets from the
    /// [list of cloudlets to execute][#getCloudletExecList()]
    /// and adds them to the finished list.
    ///
    /// @return the number of finished cloudlets removed from the [execution list][#getCloudletExecList()]
    private int addCloudletsToFinishedList() {
        final List<CloudletExecution> finishedCloudlets
            = cloudletExecList.stream()
            .filter(cle -> cle.getCloudlet().isFinished())
            .toList();

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
        removeUsedPes(cle.getPesNumber());
        return cloudletExecList.remove(cle) ? cle : CloudletExecution.NULL;
    }

    /**
     * Sets the finish time of a cloudlet and adds it to the finished list.
     *
     * @param cle the cloudlet to set the finish time
     */
    private void setCloudletFinishTimeAndAddToFinishedList(final CloudletExecution cle) {
        final double clock = vm.getSimulation().clock();
        cloudletFinish(cle);
        cle.getCloudlet().setFinishTime(clock);
    }

    /**
     * Gets the estimated time when a given cloudlet is supposed to finish executing.
     * It considers the amount of Vm PES and the total of PEs required
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
         * In such a case, it gets the last allocated MIPS to compute that.
         * That value will be the current allocated MIPS if some MIPS were
         * actually allocated or the previous allocated MIPS otherwise.*/
        final double finishTimeForRemainingLen = cle.getRemainingCloudletLength() / cle.getLastAllocatedMips();

        final double estimatedFinishTime = Math.min(remainingLifeTime, finishTimeForRemainingLen);
        return Math.max(estimatedFinishTime, vm.getSimulation().getMinTimeBetweenEvents());
    }

    /// Selects the next Cloudlets in the waiting list to move to the execution
    /// list to start executing them. While there are enough free PEs,
    /// the method tries to find a suitable Cloudlet in the list, until it reaches
    /// the end of such a list.
    ///
    /// The method might also exchange some cloudlets in the execution list with
    /// some in the waiting list. Thus, some running cloudlets may be preempted
    /// to give opportunity to previously waiting cloudlets to run. This is a
    /// process called [context switch](https://en.wikipedia.org/wiki/Context_switch).
    /// However, each CloudletScheduler implementation decides how
    /// such a process is implemented. For instance, Space-Shared schedulers may
    /// perform context switch just after the currently running Cloudlets
    /// completely finish executing.
    ///
    ///
    /// This method is called internally by the
    /// [#updateProcessing(double,MipsShare)].
    /// @param currentTime current simulation time
    /// @return the next time to update the cloudlet processing
    /// (which is a relative delay from the current simulation time)
    /// This will be the delay incurred in cloudlet processing caused by (i) vMem usage or (ii) BW reduction due to oversubscription.
    /// If no delay is incurred, it returns the expected cloudlet completion time.
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
                .filter(cle -> cle.getCloudlet().getStatus() != Cloudlet.Status.FROZEN)
                .filter(this::canExecuteCloudlet)
                .findFirst();
    }

    /**
     * Checks if the number of PEs required by a given Cloudlet is free to use.
     *
     * @param cle the Cloudlet to get the number of required PEs
     * @return true if there is the number of free PEs, false otherwise
     */
    protected boolean isThereEnoughFreePesForCloudlet(final CloudletExecution cle) {
        return vm.getProcessor().getAvailableResource() >= cle.getPesNumber();
    }

    /**
     * Removes a Cloudlet from the waiting list and adds it to the exec list.
     * @param cle the cloudlet to add to the exec list
     * @return the given cloudlet
     */
    protected CloudletExecution addWaitingCloudletToExecList(final CloudletExecution cle) {
        /* If the Cloudlet is not found in the waiting List, there is no problem.
         * It just adds it to the exec List. */
        cloudletWaitingList.remove(cle);
        addCloudletToExecList(cle);
        return cle;
    }

    @Override
    public void setVm(@NonNull final Vm vm) {
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
     * @return true if the assigned Vm is different from the given one, false otherwise
     */
    private boolean isOtherVmAssigned(final Vm vm) {
        return this.vm != null && this.vm != Vm.NULL && !vm.equals(this.vm);
    }

    @Override
    public long getUsedPes() {
        return vm.getProcessor().getAllocatedResource();
    }

    /**
     * @return the number of PEs currently not being used.
     */
    @Override
    public long getFreePes() {
        return currentMipsShare.pes() - getUsedPes();
    }

    /**
     * Adds a given number of PEs to the number of currently used PEs.
     *
     * @param usedPesToAdd number of PEs to add to the number of used PEs
     */
    private void addUsedPes(final long usedPesToAdd) {
        vm.getProcessor().allocateResource(usedPesToAdd);
    }

    /**
     * Subtracts a given number of PEs from the number of currently used PEs.
     *
     * @param usedPesToRemove number of PEs to subtract from the number of used PEs
     */
    private void removeUsedPes(final long usedPesToRemove) {
        vm.getProcessor().deallocateResource(usedPesToRemove);
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

        return cloudletCpuUsageForOnePe * cloudlet.getPesNumber();
    }

    /**
     * Gets the current requested MIPS for a given cloudlet.
     *
     * @param cle the Cloudlet to get the requested MIPS
     * @param time the current simulation time
     * @return the current requested MIPS for the given cloudlet
     */
    protected double getRequestedMipsForCloudlet(final CloudletExecution cle, final double time) {
        final Cloudlet cloudlet = cle.getCloudlet();
        return getAbsoluteCloudletResourceUtilization(cloudlet, cloudlet.getUtilizationModelCpu(), time, vm.getMips(), "CPU", true);
    }

    /**
     * Gets the current allocated MIPS for a cloudlet.
     *
     * @param cle the cloudlet to get the allocated MIPS
     * @param time the current simulation time
     * @return the current allocated mips for cloudlet
     */
    public double getAllocatedMipsForCloudlet(final CloudletExecution cle, final double time) {
        return getAllocatedMipsForCloudlet(cle, time, false);
    }

    /**
     * Gets the current allocated MIPS for cloudlet.
     *
     * @param cle the cloudlet to get the allocated MIPS
     * @param time the current simulation time
     * @param log Indicate if a log should be issued when the requested resource is larger than the capacity
     * @return the current allocated mips for cloudlet
     */
    public double getAllocatedMipsForCloudlet(final CloudletExecution cle, final double time, final boolean log) {
        final var cl = cle.getCloudlet();
        final String resourceName = log ? "CPU" : "";
        return getAbsoluteCloudletResourceUtilization(cl, cl.getUtilizationModelCpu(), time, getAvailableMipsByPe(), resourceName, false);
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
     * at the current simulation time. That is based on the maximum amount of resource that the Cloudlet can use
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
     * for a given time. That is based on the maximum amount of resource that the Cloudlet can use
     * this time.
     *
     * @param cloudlet Cloudlet requesting the resource
     * @param model                   the {@link UtilizationModel} to get the absolute amount of resource used by the Cloudlet
     * @param time                    the current simulation time
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
     * {@return a <b>read-only</b> list of Cloudlets that finished executing and were returned to the broker}
     * A Cloudlet is returned to notify the broker about the end of its execution.
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
	 * that shares the CPU time between all running Cloudlets.
	 * It happens even when there are more Cloudlets than the number of CPUs.
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
        return cle.getCloudlet().getStatus().ordinal() < Cloudlet.Status.FROZEN.ordinal() && canExecuteCloudletInternal(cle);
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
