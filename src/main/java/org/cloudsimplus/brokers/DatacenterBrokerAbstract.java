/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.brokers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.*;
import org.cloudsimplus.core.events.CloudSimEvent;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.TimeZoned;
import org.cloudsimplus.listeners.DatacenterBrokerEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.traces.google.GoogleTaskEventsTraceReader;
import org.cloudsimplus.util.InvalidEventDataTypeException;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmAbstract;
import org.cloudsimplus.vms.VmGroup;
import org.cloudsimplus.vms.VmSimple;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An abstract class for implementing {@link DatacenterBroker}s.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
@Accessors @Getter @Setter
public non-sealed abstract class DatacenterBrokerAbstract extends CloudSimEntity implements DatacenterBroker {
    /**
     * A default {@link Function} which always returns {@link #DEF_VM_DESTRUCTION_DELAY}
     * to indicate that any VM should not be immediately destroyed after it becomes idle.
     * This way, using this Function, the broker will destroy VMs only after:
     * <ul>
     *   <li>all submitted Cloudlets from all its VMs are finished and there are no waiting Cloudlets;</li>
     *   <li>or all running Cloudlets are finished and there are some of them waiting their VMs to be created.</li>
     * </ul>
     *
     * @see #setVmDestructionDelayFunction(Function)
     */
    private static final Function<Vm, Double> DEF_VM_DESTRUCTION_DELAY_FUNC = vm -> DEF_VM_DESTRUCTION_DELAY;

    private boolean selectClosestDatacenter;

    private boolean batchVmCreation;

    /**
     * A List of registered event listeners for the onVmsCreatedListeners event.
     *
     * @see #addOnVmsCreatedListener(EventListener)
     */
    @Getter(AccessLevel.NONE)
    private final List<EventListener<DatacenterBrokerEventInfo>> onVmsCreatedListeners;

    private final VmCreation vmCreation;

    private final List<Vm> vmFailedList;

    private final List<Vm> vmWaitingList;

    private final List<Vm> vmExecList;

    private final List<Vm> vmCreatedList;

    private final List<Cloudlet> cloudletWaitingList;

    private final List<Cloudlet> cloudletSubmittedList;

    private final List<Cloudlet> cloudletFinishedList;

    private final List<Cloudlet> cloudletCreatedList;

    @NonNull
    private Datacenter lastSelectedDc;

    /**
     * Last Vm selected to run some Cloudlets.
     */
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Vm lastSelectedVm;

    /** @see #setDatacenterMapper(BiFunction) */
    @NonNull
    private BiFunction<Datacenter, Vm, Datacenter> datacenterMapper;

    /** @see #setVmMapper(Function) */
    @NonNull
    @Getter(AccessLevel.NONE)
    private Function<Cloudlet, Vm> vmMapper;

    @Getter(AccessLevel.NONE)
    private Function<Vm, Double> vmDestructionDelayFunction;

    /**
     * {@inheritDoc}
     * <p>If null is given, VMs won't be sorted and follow submission order.</p>
     * @see #setVmComparator(Comparator) */
    @Getter(AccessLevel.NONE)
    @Nullable
    private Comparator<Vm> vmComparator;

    /**
     * {@inheritDoc}
     * <p>If null is given, Cloudlets won't be sorted and follow submission order.</p>
     */
    @Getter(AccessLevel.NONE)
    @Nullable
    private Comparator<Cloudlet> cloudletComparator;

    /** @see #getDatacenterList() */
    @Getter(AccessLevel.PROTECTED)
    private List<Datacenter> datacenterList;

    /**
     * Indicates if the last time checked, there were waiting cloudlets or not.
     */
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private boolean wereThereWaitingCloudlets;

    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Cloudlet lastSubmittedCloudlet;

    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Vm lastSubmittedVm;

    /**
     * Indicates if a shutdown request was already sent or not.
     */
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private boolean shutdownRequested;

    /** @see #isShutdownWhenIdle()  */
    private boolean shutdownWhenIdle;

    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private boolean vmCreationRetrySent;

    /**
     * Creates a DatacenterBroker giving a specific name.
     *
     * @param simulation the {@link CloudSimPlus} instance that represents the simulation the Entity is related to
     * @param name the DatacenterBroker name
     */
    public DatacenterBrokerAbstract(final CloudSimPlus simulation, final String name) {
        super(simulation);
        if(name != null && !name.isEmpty()) {
            setName(name);
        }

        this.onVmsCreatedListeners = new ArrayList<>();
        this.lastSubmittedCloudlet = Cloudlet.NULL;
        this.lastSubmittedVm = Vm.NULL;
        this.lastSelectedVm = Vm.NULL;
        this.lastSelectedDc = Datacenter.NULL;
        this.shutdownWhenIdle = true;

        this.vmCreation = new VmCreation();
        this.vmFailedList = new ArrayList<>();
        this.vmWaitingList = new ArrayList<>();
        this.vmExecList = new ArrayList<>();
        this.vmCreatedList = new ArrayList<>();
        this.cloudletWaitingList = new ArrayList<>();
        this.cloudletFinishedList = new ArrayList<>();
        this.cloudletCreatedList = new ArrayList<>();
        this.cloudletSubmittedList = new ArrayList<>();
        setDatacenterList(new ArrayList<>());

        setDatacenterMapper(this::defaultDatacenterMapper);
        setVmMapper(this::defaultVmMapper);
        vmDestructionDelayFunction = DEF_VM_DESTRUCTION_DELAY_FUNC;
    }

    @Override
    public final DatacenterBroker setSelectClosestDatacenter(final boolean select) {
        this.selectClosestDatacenter = select;
        if(select){
            setDatacenterMapper(this::closestDatacenterMapper);
        }

        return this;
    }

    @Override
    public DatacenterBroker submitVmList(final List<? extends Vm> list, final double submissionDelay) {
        setDelayForEntitiesWithNoDelay(list, submissionDelay);
        return submitVmList(list);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The individual submission delay of VMs inside a group will be ignored.
     * Only the submission delay directly set for the {@link VmGroup} will be considered.</p>
     *
     * <p>If the entity already started (the simulation is running),
     * the creation of previously submitted VMs was already requested
     * by the {@link #start()} method, which is called just once.
     * This way, this method will immediately request the creation of these
     * submitted VMs in order to allow VM creation after
     * the simulation has started. This avoids the developer to
     * dynamically create brokers just to submit VMs or Cloudlets during
     * simulation execution.</p>
     *
     * @param list {@inheritDoc}
     * @return {@inheritDoc}
     * @see VmGroup
     */
    @Override
    public DatacenterBroker submitVmList(final List<? extends Vm> list) {
        sortVmsIfComparatorIsSet(list);
        configureEntities(list);
        lastSubmittedVm = setIdForEntitiesWithoutOne(list, lastSubmittedVm);
        vmWaitingList.addAll(list);

        if (isStarted() && !list.isEmpty()) {
            LOGGER.info(
                "{}: {}: List of {} VMs submitted to the broker during simulation execution. VMs creation request sent to Datacenter.",
                getSimulation().clockStr(), getName(), list.size());
            requestDatacentersToCreateWaitingCloudlets();
            if(!vmCreationRetrySent) {
                lastSelectedDc = Datacenter.NULL;
                requestDatacenterToCreateWaitingVms(false, false);
            }
        }

        return this;
    }

    /**
     * Configures attributes for each {@link CustomerEntity} into a given list.
     *
     * @param customerEntities the List of {@link CustomerEntity} to configure.
     *                         Such entities can be a {@link Cloudlet}, {@link Vm}, {@link VmGroup}
     *                         or any object that implements {@link CustomerEntity}.
     */
    private void configureEntities(final List<? extends CustomerEntity> customerEntities) {
        for (final var entity : customerEntities) {
            entity.setBroker(this);
            entity.setBrokerArrivalTime(getSimulation().clock());
            /* If the finished entity is submitted again, clear the last tried datacenter
            * to indicate that, this time, it wasn't tried any datacenter yet. */
            entity.setLastTriedDatacenter(Datacenter.NULL);
            if(entity instanceof VmGroup vmGroup) {
                configureEntities(vmGroup.getVmList());
            }
        }
    }

    /**
     * Defines IDs for a list of {@link CustomerEntity}s that don't
     * have one assigned already. Such entities can be a {@link Cloudlet},
     * {@link Vm}, {@link VmGroup} or any object that implements {@link CustomerEntity}.
     *
     * @param list                list of Customer Entities to define an ID
     * @param lastSubmittedEntity the last Customer Entity that was submitted to the broker
     * @return the last Customer Entity in the given List, or the lastSubmittedEntity if the List is empty
     */
    private <T extends CustomerEntity> T setIdForEntitiesWithoutOne(final List<? extends T> list, T lastSubmittedEntity) {
        return Simulation.setIdForEntitiesWithoutOne(list, lastSubmittedEntity);
    }

    private void sortVmsIfComparatorIsSet(final List<? extends Vm> list) {
        if (vmComparator != null) {
            list.sort(vmComparator);
        }
    }

    @Override
    public DatacenterBroker submitVm(@NonNull final Vm vm) {
        if (Vm.NULL.equals(vm)) {
            return this;
        }

        final var newVmList = new ArrayList<Vm>(1);
        newVmList.add(vm);
        return submitVmList(newVmList);
    }

    @Override
    public DatacenterBroker submitCloudlet(@NonNull final Cloudlet cloudlet) {
        if (cloudlet == Cloudlet.NULL) {
            return this;
        }

        final var newCloudletList = new ArrayList<Cloudlet>(1);
        newCloudletList.add(cloudlet);
        return submitCloudletList(newCloudletList);
    }

    @Override
    public DatacenterBroker submitCloudletList(final List<? extends Cloudlet> list, double submissionDelay) {
        return submitCloudletList(list, Vm.NULL, submissionDelay);
    }

    @Override
    public DatacenterBroker submitCloudletList(final List<? extends Cloudlet> list, Vm vm) {
        return submitCloudletList(list, vm, -1);
    }

    @Override
    public DatacenterBroker submitCloudletList(final List<? extends Cloudlet> list, Vm vm, double submissionDelay) {
        setDelayForEntitiesWithNoDelay(list, submissionDelay);
        bindCloudletsToVm(list, vm);
        return submitCloudletList(list);
    }

    /**
     * {@inheritDoc}
     *
     * <p>If the entity already started (the simulation is running),
     * the creation of previously submitted Cloudlets was already requested
     * by the {@link #start()} method, which is called just once.
     * This way, this method will immediately request the creation of these
     * submitted Cloudlets if all submitted VMs were already created,
     * in order to allow Cloudlet creation after
     * the simulation has started. This avoids the developer to
     * dynamically create brokers just to submit VMs or Cloudlets during
     * simulation execution.</p>
     *
     * @param list {@inheritDoc}
     * @see #submitCloudletList(List, double)
     * @return {@inheritDoc}
     */
    @Override
    public DatacenterBroker submitCloudletList(final List<? extends Cloudlet> list) {
        if (list.isEmpty()) {
            return this;
        }

        sortCloudletsIfComparatorIsSet(list);
        configureEntities(list);
        lastSubmittedCloudlet = setIdForEntitiesWithoutOne(list, lastSubmittedCloudlet);
        cloudletSubmittedList.addAll(list);
        setSimulationForCloudletUtilizationModels(list);
        cloudletWaitingList.addAll(list);
        wereThereWaitingCloudlets = true;

        if (!isStarted()) {
            return this;
        }

        LOGGER.info(
            "{}: {}: List of {} Cloudlets submitted to the broker during simulation execution.",
            getSimulation().clockStr(), getName(), list.size());

        LOGGER.info("Cloudlets creation request sent to Datacenter.");
        requestDatacentersToCreateWaitingCloudlets();

        return this;
    }

    /**
     * Binds a list of Cloudlets to a given {@link Vm}, so that those Cloudlets will execute on that Vm.
     * If the {@link Vm} is {@link Vm#NULL}, the Cloudlets will not be bound.
     *
     * @param cloudlets the List of Cloudlets to be bound to a VM
     * @param vm        the VM to bind the Cloudlets to
     */
    private void bindCloudletsToVm(final List<? extends Cloudlet> cloudlets, Vm vm) {
        if (Vm.NULL.equals(vm)) {
            return;
        }

        cloudlets.forEach(c -> c.setVm(vm));
    }

    private void sortCloudletsIfComparatorIsSet(final List<? extends Cloudlet> cloudlets) {
        if (cloudletComparator != null) {
            cloudlets.sort(cloudletComparator);
        }
    }

    private void setSimulationForCloudletUtilizationModels(final List<? extends Cloudlet> cloudletList) {
        for (final var cloudlet : cloudletList) {
            setSimulationForUtilizationModel(cloudlet.getUtilizationModelCpu());
            setSimulationForUtilizationModel(cloudlet.getUtilizationModelBw());
            setSimulationForUtilizationModel(cloudlet.getUtilizationModelRam());
        }
    }

    private void setSimulationForUtilizationModel(final UtilizationModel cloudletUtilizationModel) {
        if (cloudletUtilizationModel.getSimulation() == null || cloudletUtilizationModel.getSimulation() == Simulation.NULL) {
            cloudletUtilizationModel.setSimulation(getSimulation());
        }
    }

    /**
     * Sets the delay for a list of {@link CustomerEntity}s that don't
     * have a delay assigned already. Such entities can be a {@link Cloudlet},
     * {@link Vm} or any object that implements {@link CustomerEntity}.
     *
     * <p>If the delay is defined as a negative number, objects' delay won't be changed.</p>
     *
     * @param entities list of Customer Entities to set their delays
     * @param submissionDelay the submission delay to set (in seconds)
     */
    private void setDelayForEntitiesWithNoDelay(final List<? extends CustomerEntity> entities, final double submissionDelay) {
        if (submissionDelay < 0) {
            return;
        }

        entities.stream()
                .filter(entity -> entity.getSubmissionDelay() <= 0)
                .forEach(entity -> entity.setSubmissionDelay(submissionDelay));
    }

    @Override
    public boolean bindCloudletToVm(final Cloudlet cloudlet, final Vm vm) {
        if (!this.equals(cloudlet.getBroker()) && !DatacenterBroker.NULL.equals(cloudlet.getBroker())) {
            return false;
        }

        cloudlet.setVm(vm);
        return true;
    }

    @Override
    public void processEvent(final SimEvent evt) {
        if (processCloudletEvents(evt) || processVmEvents(evt) || processGeneralEvents(evt)) {
            return;
        }

        LOGGER.trace("{}: {}: Unknown event {} received.", getSimulation().clockStr(), this, evt.getTag());
    }

    private boolean processCloudletEvents(final SimEvent evt) {
        return switch (evt.getTag()) {
            case CloudSimTag.CLOUDLET_CREATION -> createWaitingCloudlets();
            case CloudSimTag.CLOUDLET_RETURN -> processCloudletReturn(evt);
            case CloudSimTag.CLOUDLET_READY -> processCloudletReady(evt);
            /* The data of such a kind of event is a Runnable that has all
             * the logic to update Cloudlet's attributes.
             * This way, it will be run to perform such an update.
             * Check the documentation of the tag below for details.*/
            case CloudSimTag.CLOUDLET_UPDATE_ATTRIBUTES -> executeRunnableEvent(evt);
            case CloudSimTag.CLOUDLET_PAUSE -> processCloudletPause(evt);
            case CloudSimTag.CLOUDLET_CANCEL -> processCloudletCancel(evt);
            case CloudSimTag.CLOUDLET_FINISH -> processCloudletFinish(evt);
            case CloudSimTag.CLOUDLET_FAIL -> processCloudletFail(evt);
            default -> false;
        };
    }

    private boolean executeRunnableEvent(final SimEvent evt){
        if(evt.getData() instanceof Runnable runnable) {
            runnable.run();
            return true;
        }

        throw new InvalidEventDataTypeException(evt, "CLOUDLET_UPDATE_ATTRIBUTES", "Runnable");
    }

    private boolean processVmEvents(final SimEvent evt) {
        return switch (evt.getTag()) {
            case CloudSimTag.VM_CREATE_RETRY -> {
                vmCreationRetrySent = false;
                yield requestDatacenterToCreateWaitingVms(false, true);
            }
            case CloudSimTag.VM_CREATE_ACK -> processVmCreateResponseFromDatacenter(evt);
            case CloudSimTag.VM_VERTICAL_SCALING -> requestVmVerticalScaling(evt);
            default -> false;
        };
    }

    private boolean processGeneralEvents(final SimEvent evt) {
        if (evt.getTag() == CloudSimTag.DC_LIST_REQUEST) {
            processDatacenterListRequest(evt);
            return true;
        }

        if (evt.getTag() == CloudSimTag.ENTITY_SHUTDOWN || evt.getTag() == CloudSimTag.SIMULATION_END) {
            shutdown();
            return true;
        }

        return false;
    }

    /**
     * Sets the status of a received Cloudlet to {@link Cloudlet.Status#READY}
     * so that the Cloudlet can be selected to start running as soon as possible
     * by a {@link CloudletScheduler}.
     *
     * <p>This tag is commonly used when Cloudlets are created
     * from a trace file such as a {@link GoogleTaskEventsTraceReader Google Cluster Trace}.</p>
     *
     * @param evt the event to process
     */
    private boolean processCloudletReady(final SimEvent evt){
        final var cloudlet = (Cloudlet)evt.getData();
        if(cloudlet.getStatus() == Cloudlet.Status.PAUSED)
             logCloudletStatusChange(cloudlet, "resume execution of");
        else logCloudletStatusChange(cloudlet, "start executing");

        cloudlet.getVm().getCloudletScheduler().cloudletReady(cloudlet);
        return true;
    }

    private boolean processCloudletPause(final SimEvent evt){
        final var cloudlet = (Cloudlet)evt.getData();
        logCloudletStatusChange(cloudlet, "de-schedule (pause)");
        cloudlet.getVm().getCloudletScheduler().cloudletPause(cloudlet);
        return true;
    }

    private boolean processCloudletCancel(final SimEvent evt){
        final var cloudlet = (Cloudlet)evt.getData();
        logCloudletStatusChange(cloudlet, "cancel execution of");
        cloudlet.getVm().getCloudletScheduler().cloudletCancel(cloudlet);
        return true;
    }

    /**
     * Process the request to finish a Cloudlet with an indefinite length,
     * setting its length as the current number of processed MI.
     * @param evt the event data
     */
    private boolean processCloudletFinish(final SimEvent evt){
        final var cloudlet = (Cloudlet)evt.getData();
        logCloudletStatusChange(cloudlet, "finish running");
        /* If the executed length is zero, it means the cloudlet processing was not updated yet.
         * This way, calls the method to update the Cloudlet processing.*/
        if(cloudlet.getFinishedLengthSoFar() == 0){
            updateHostProcessing(cloudlet);
        }

        /* If after updating the host processing, the cloudlet executed length is still zero,
         * it means the Cloudlet has never started. This happens, for instance, due
         * to lack of PEs to run the Cloudlet (usually when you're using a CloudletSchedulerSpaceShared).
         * This way, sets the Cloudlet as failed. */
        if(cloudlet.getFinishedLengthSoFar() == 0) {
            cloudlet.getVm().getCloudletScheduler().cloudletFail(cloudlet);
            return true;
        }

        final long prevLength = cloudlet.getLength();
        cloudlet.setLength(cloudlet.getFinishedLengthSoFar());

        /* After defining the Cloudlet length, updates the Cloudlet processing again,
         * so that the Cloudlet status is updated at this clock tick instead of the next one. */
        updateHostProcessing(cloudlet);

        /* If the Cloudlet length was negative, after finishing it,
         * a VM update event is sent to ensure the broker is notified the Cloudlet has finished.
         * A negative length makes the Cloudlet to keep running until a finish message is
         * sent to the broker. */
        if(prevLength < 0){
            final double delay = cloudlet.getSimulation().getMinTimeBetweenEvents();
            final Datacenter dc = cloudlet.getVm().getHost().getDatacenter();
            dc.schedule(delay, CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING);
        }

        return true;
    }

    /**
     * Updates the processing of the Host where a Cloudlet's VM is running.
     * @param cloudlet the cloudlet to get the Host from its VM
     */
    private void updateHostProcessing(final Cloudlet cloudlet) {
        cloudlet.getVm().getHost().updateProcessing(getSimulation().clock());
    }

    private void logCloudletStatusChange(final Cloudlet cloudlet, final String status) {
        final String msg = cloudlet.getJobId() > 0 ? "(job %d) ".formatted(cloudlet.getJobId()) : "";
        LOGGER.info("{}: {}: Request to {} {} {}received.", getSimulation().clockStr(), getName(), status, cloudlet, msg);
    }

    private boolean processCloudletFail(final SimEvent evt){
        final var cloudlet = (Cloudlet)evt.getData();
        cloudlet.getVm().getCloudletScheduler().cloudletFail(cloudlet);
        return true;
    }

    private boolean requestVmVerticalScaling(final SimEvent evt) {
        if (evt.getData() instanceof VerticalVmScaling scaling) {
            getSimulation().sendNow(
                evt.getSource(), scaling.getVm().getHost().getDatacenter(),
                CloudSimTag.VM_VERTICAL_SCALING, scaling);
            return true;
        }

        throw new InvalidEventDataTypeException(evt, "VM_VERTICAL_SCALING", "VerticalVmScaling");
    }

    /**
     * Process a request to get the list of all Datacenters registered in the
     * Cloud Information Service (CIS) of the {@link #getSimulation() simulation}.
     *
     * @param evt a CloudSimEvent object
     */
    private void processDatacenterListRequest(final SimEvent evt) {
        if(evt.getData() instanceof List dcList) {
            setDatacenterList(dcList);
            LOGGER.info("{}: {}: List of {} datacenters(s) received.", getSimulation().clockStr(), getName(), this.datacenterList.size());
            requestDatacenterToCreateWaitingVms(false, false);
            return;
        }

        throw new InvalidEventDataTypeException(evt, "DC_LIST_REQUEST", "List<Datacenter>");
    }

    /**
     * Process the ack received from a Datacenter to a broker's request for
     * creation of a Vm in that Datacenter.
     *
     * <p>Uses regressive index loop to avoid ConcurrentModificationException.
     * The issue happens because the broker sends its vmWaitingList to the Datacenter,
     * which returns back the same list in this event.
     * This way, the local vmList points to the vmWaitingList.
     * Since we are iterating over vmList and removing Vms from vmWaitingList,
     * the issue happens.
     * A solution is to make the broker send a copy of the vmWaitingList to the Datacenter,
     * but that imposes a performance and memory consumption penalty.
     * Using a forward indexed loop causes some VM to be missed when a VM is removed from
     * the vmWaitingList.
     * </p>
     *
     * @param evt a SimEvent object
     * @return true if the VM was created successfully, false otherwise
     */
    private boolean processVmCreateResponseFromDatacenter(final SimEvent evt) {
        //Data can be a single Vm or List<Vm> (in the former situation, gets the single Vm as a List)
        final var vmList = VmAbstract.getList(evt.getData());

        int createdVms = 0;
        for (int i = vmList.size()-1; i >= 0; i--) {
            var vm = vmList.get(i);
            //if the VM was successfully created in the requested Datacenter
            if (vm.isCreated()) {
                createdVms++;
                processSuccessVmCreationInDatacenter(vm);
                vm.notifyOnHostAllocationListeners();
            } else {
                vm.setFailed(true);
                if (!vmCreation.isRetryFailedVms()) {
                    vmWaitingList.remove(vm);
                    vmFailedList.add(vm);
                    LOGGER.warn(
                        "{}: {}: {} has been moved to the failed list because creation retry is not enabled.",
                        getSimulation().clockStr(), getName(), vm);
                }

                vm.notifyOnCreationFailureListeners(lastSelectedDc);
            }
        }

        //Decreases to indicate an ack for the request was received (either if the VM was created or not)
        vmCreation.incCreationRequests(-1);

        if(vmCreation.getCreationRequests() == 0 && !vmWaitingList.isEmpty()) {
            requestCreationOfWaitingVmsToFallbackDatacenter();
        }

        if(allNonDelayedVmsCreated()) {
            requestDatacentersToCreateWaitingCloudlets();
        }

        return createdVms > 0;
    }

    /**
     * {@return true if all VMs submitted with no delay were created, false otherwise}
     * Only after those VMs are created, cloudlets creation is requested.
     * Otherwise, all waiting cloudlets would be sent to the
     * first created VM.
     */
    private boolean allNonDelayedVmsCreated() {
        return vmWaitingList.stream().noneMatch(vm -> vm.getSubmissionDelay() == 0);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void notifyOnVmsCreatedListeners() {
        if(!vmWaitingList.isEmpty()) {
            return;
        }

        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onVmsCreatedListeners.size(); i++) {
            final var listener = onVmsCreatedListeners.get(i);
            listener.update(DatacenterBrokerEventInfo.of(listener, this));
        }

        if (vmWaitingList.isEmpty()) {
            vmCreation.resetCurrentRetries();
        }
    }

    /**
     * After the response (ack) of all VM creation requests were received,
     * but not all VMs could be created
     * (which means some acks informed about Vm creation failures),
     * try to find another Datacenter to request the creation of the VMs in the waiting list.
     */
    private void requestCreationOfWaitingVmsToFallbackDatacenter() {
        this.lastSelectedDc = Datacenter.NULL;
        if (vmWaitingList.isEmpty() || requestDatacenterToCreateWaitingVms(false, true)) {
            return;
        }

        final var msg =
            "{}: {}: {} of the requested {} VMs couldn't be created because suitable Hosts weren't found in any available Datacenter."
            + (vmExecList.isEmpty() && !vmCreation.isRetryFailedVms() ? " Shutting broker down..." : "");
        LOGGER.error(msg, getSimulation().clockStr(), getName(), vmWaitingList.size(), getVmsNumber());

        /* If it gets here, it means that all datacenters were already queried and not all VMs could be created. */
        if (!vmWaitingList.isEmpty()) {
            processVmCreationFailure();
            return;
        }

        requestDatacentersToCreateWaitingCloudlets();
    }

    private void processVmCreationFailure() {
        if (vmCreation.isRetryFailedVms()) {
            lastSelectedDc = datacenterList.get(0);
            this.vmCreationRetrySent = true;
            schedule(vmCreation.getRetryDelay(), CloudSimTag.VM_CREATE_RETRY);
            vmCreation.incCurrentRetries();
        }
        else shutdown();
    }

    /**
     * Request the creation of {@link #getVmWaitingList() waiting VMs} in some Datacenter.
     *
     * <p>If it's trying a fallback datacenter and the {@link #selectClosestDatacenter} is enabled,
     * that means the function assigned to the {@link #datacenterMapper} is the
     * {@link #closestDatacenterMapper(Datacenter, Vm)}
     * which has failed to find a suitable Datacenter for the VM.
     * This way, it uses the {@link #defaultDatacenterMapper(Datacenter, Vm)} instead.
     * </p>
     *
     * @param isFallbackDatacenter true to indicate that a fallback Datacenter will be tried,
     *                             after the previous one was not able to create all waiting VMs;
     *                             false to indicate it will try the default datacenter.
     * @return true if some Datacenter was selected; false if all Datacenter were tried
     *         and not all VMs could be created
     * @see #submitVmList(java.util.List)
     */
    private boolean requestDatacenterToCreateWaitingVms(final boolean isFallbackDatacenter, final boolean creationRetry) {
        if(vmWaitingList.isEmpty())
            return false;

        for (final var vm : vmWaitingList) {
            if(creationRetry) {
                vm.setLastTriedDatacenter(Datacenter.NULL);
            }

            if(!batchVmCreation) {
                this.lastSelectedDc = getLastSelectedDc(isFallbackDatacenter, vm);
                vmCreation.incCreationRequests(requestSingleVmCreation(lastSelectedDc, isFallbackDatacenter, vm));
            }
        }

        //Sends a single VM creation request to the first selected DC for a List of Vms
        if(batchVmCreation) {
            //In batch VM creation, sends all VMs to the same DC selected for the first VM
            this.lastSelectedDc = getLastSelectedDc(isFallbackDatacenter, vmWaitingList.get(0));
            vmCreation.incCreationRequests(requestVmCreation(lastSelectedDc, isFallbackDatacenter, vmWaitingList));
        }

        return lastSelectedDc != Datacenter.NULL;
    }

    private Datacenter getLastSelectedDc(final boolean isFallbackDatacenter, final Vm vm) {
        return isFallbackDatacenter && selectClosestDatacenter ?
                    defaultDatacenterMapper(lastSelectedDc, vm) :
                    datacenterMapper.apply(lastSelectedDc, vm);
    }

    /**
     * Try to request the creation of a single VM into a given datacenter
     * @param dc the target Datacenter to try creating the VM (or {@link Datacenter#NULL} if no DC is available)
     * @param isFallbackDatacenter indicate if the given Datacenter was selected when
     *                             a previous one doesn't have enough capacity to place the requested VM
     * @param vm the VM to be placed
     * @return 1 to indicate a VM creation request was sent to the datacenter;
     *         0 to indicate the request was not sent due to lack of an available datacenter
     * @see #requestVmCreation(Datacenter, boolean, Object)
     */
    private int requestSingleVmCreation(final Datacenter dc, final boolean isFallbackDatacenter, final Vm vm) {
        /* It was not passed List.of(vm) to the calling method,
           after declaring the last parameter as List<Vm>,
           to avoid creation of a new object and possible pressure on memory consumption
           and performance degradation. */
        return requestVmCreation(dc, isFallbackDatacenter, vm);
    }

    /**
     * Try to request the creation of a single VM or a {@code List<Vm>} into the datacenter of the first Vm.
     * @param dc the target Datacenter to try creating the VM (or {@link Datacenter#NULL} if no DC is available)
     * @param isFallbackDatacenter
     * @param vmOrList a single Vm object or a {@code List<Vm>}
     * @return 1 to indicate a VM creation request was sent to the datacenter;
     *         0 to indicate the request was not sent due to lack of an available datacenter
     */
    private <T> int requestVmCreation(final Datacenter dc, final boolean isFallbackDatacenter, final T vmOrList) {
        if(vmOrList instanceof List<?> list && list.isEmpty() )
            return 0;

        final var vm = VmAbstract.getFirstVm(vmOrList);

        if (dc == Datacenter.NULL || dc.equals(vm.getLastTriedDatacenter())) {
            return 0;
        }

        logVmCreationRequest(dc, isFallbackDatacenter, vmOrList);
        send(dc, vm.getSubmissionDelay(), CloudSimTag.VM_CREATE_ACK, vmOrList);
        vm.setLastTriedDatacenter(dc);
        return 1;
    }

    /**
     *
     * @param dc
     * @param isFallbackDatacenter
     * @param vmOrList a single Vm object or a {@code List<Vm>}
     */
    private <T> void logVmCreationRequest(final Datacenter dc, final boolean isFallbackDatacenter, final T vmOrList) {
        final var fallbackMsg = isFallbackDatacenter ? " (due to lack of a suitable Host in previous one)" : "";
        final var vm = VmAbstract.getFirstVm(vmOrList);
        final var vmMsg = VmAbstract.isVmList(vmOrList) ? "%d Vms in batch".formatted(VmAbstract.getVmCount(vmOrList)) : vm;
        if(vm.getSubmissionDelay() == 0)
            LOGGER.info(
                "{}: {}: Trying to create {} inside {}{}",
                getSimulation().clockStr(), getName(), vmMsg, dc, fallbackMsg);
        else
            LOGGER.info(
                "{}: {}: Creation of {} inside {}{} will be requested in {} seconds",
                getSimulation().clockStr(), getName(), vmMsg, dc,
                fallbackMsg, vm.getSubmissionDelay());
    }

    @Override
    public int getVmsNumber() {
        return vmCreatedList.size() + vmWaitingList.size() + vmFailedList.size();
    }

    /**
     * Process a response from a Datacenter informing that it was able to
     * create the VM requested by the broker.
     *
     * @param vm Vm that succeeded to be created inside the Datacenter
     */
    private void processSuccessVmCreationInDatacenter(final Vm vm) {
        if(vm instanceof VmGroup vmGroup){
            int createdVms = 0;
            for (final Vm nextVm : vmGroup.getVmList()) {
                if (nextVm.isCreated()) {
                    processSuccessVmCreationInDatacenter(nextVm);
                    createdVms++;
                }
            }

            if(createdVms == vmGroup.size()){
                vmWaitingList.remove(vmGroup);
            }

            return;
        }

        vmWaitingList.remove(vm);
        vmExecList.add(vm);
        vmCreatedList.add(vm);
        notifyOnVmsCreatedListeners();
    }

    /**
     * Processes the end of execution of a given cloudlet inside a Vm.
     *
     * @param evt a SimEvent object containing the cloudlet that has just finished executing and returned to the broker
     */
    private boolean processCloudletReturn(final SimEvent evt) {
        final var cloudlet = (Cloudlet) evt.getData();
        cloudletFinishedList.add(cloudlet);
        ((VmSimple) cloudlet.getVm()).addExpectedFreePesNumber(cloudlet.getPesNumber());
        final String lifeTime = cloudlet.getLifeTime() == Double.MAX_VALUE ? "" : " (after defined lifetime expired)";
        LOGGER.info(
            "{}: {}: {} finished{} in {} and returned to broker.",
            getSimulation().clockStr(), getName(), cloudlet, lifeTime, cloudlet.getVm());

        if (cloudlet.getVm().getCloudletScheduler().isEmpty()) {
            requestIdleVmDestruction(cloudlet.getVm());
            return true;
        }

        requestVmDestructionAfterAllCloudletsFinished();
        return true;
    }

    /**
     * Request the destruction of VMs after all running cloudlets have finished and returned to the broker.
     * If there is no waiting Cloudlet, request all VMs to be destroyed.
     */
    private void requestVmDestructionAfterAllCloudletsFinished() {
        for (int i = vmExecList.size() - 1; i >= 0; i--) {
            requestIdleVmDestruction(vmExecList.get(i));
        }

        if (cloudletWaitingList.isEmpty()) {
            return;
        }

        /*
        There are some cloudlets waiting their VMs to be created.
        Idle VMs were destroyed above and here it requests the creation of the waiting ones.
        When there are waiting Cloudlets, the destruction
        of idle VMs possibly free resources to start the waiting VMs.
        This way, if a VM destruction delay function is not set,
        it defines one that always return 0 to indicate
        idle VMs must be destroyed immediately.
        */
        requestDatacenterToCreateWaitingVms(false, false);
    }

    @Override
    public DatacenterBroker requestIdleVmDestruction(final Vm vm) {
        if (vm.isCreated()) {
            if(this.isFinished() || vm.isLifeTimeReached() || isVmIdleEnough(vm)) {
                vm.shutdown();
            }

            if(isVmIdlenessVerificationRequired((VmSimple)vm)) {
                getSimulation().send(
                    new CloudSimEvent(vmDestructionDelayFunction.apply(vm),
                        vm.getHost().getDatacenter(),
                        CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING));
                return this;
            }
        }

        requestShutdownWhenIdle();
        return this;
    }

    private boolean isVmIdleEnough(final Vm vm) {
        final double delay = vmDestructionDelayFunction.apply(vm);
        return delay > DEF_VM_DESTRUCTION_DELAY && vm.isIdleEnough(delay);
    }

    @Override
    public void requestShutdownWhenIdle() {
        if (!shutdownRequested && isTimeToShutdownBroker()) {
            schedule(CloudSimTag.ENTITY_SHUTDOWN);
            shutdownRequested = true;
        }
    }

    /**
     * Checks if an event must be sent to verify if a VM became idle.
     * That will happen when the {@link #setVmDestructionDelayFunction(Function) VM destruction delay}
     * is set and is not multiple of the {@link Datacenter#getSchedulingInterval()}
     *
     * <p>
     * In such a situation, that means it is required to send additional events to check if a VM became idle.
     * No additional events are required when:
     * <ul>
     *   <li>the VM destruction delay was not set (VMs will be destroyed only when the broker is shutdown);</li>
     *   <li>the delay was set, and it's multiple of the scheduling interval
     *   (VM idleness will be checked in the interval defined by the {@link Datacenter#getSchedulingInterval()}).</li>
     * </ul>
     *
     * Avoiding additional messages improves performance of large scale simulations.
     * </p>
     * @param vm the Vm to check
     * @return true if a message to check VM idleness has to be sent, false otherwise
     */
    private boolean isVmIdlenessVerificationRequired(final VmSimple vm) {
        if(vm.hasStartedSomeCloudlet() && vm.getCloudletScheduler().isEmpty()){
            final int schedulingInterval = (int)vm.getHost().getDatacenter().getSchedulingInterval();
            final int delay = vmDestructionDelayFunction.apply(vm).intValue();
            return delay > DEF_VM_DESTRUCTION_DELAY && (schedulingInterval <= 0 || delay % schedulingInterval != 0);
        }

        return false;
    }

    /**
     * {@return true if the broker is still alive and idle, so that it may be shutdown, false otherwise}
     */
    private boolean isTimeToShutdownBroker() {
        return isAlive() && isTimeToTerminateSimulation() && shutdownWhenIdle && isBrokerIdle();
    }

    private boolean isTimeToTerminateSimulation() {
        return !getSimulation().isTerminationTimeSet() || getSimulation().isTimeToTerminateSimulationUnderRequest();
    }

    private boolean isBrokerIdle() {
        return cloudletWaitingList.isEmpty() && vmWaitingList.isEmpty() && vmExecList.isEmpty();
    }

    /**
     * Request Datacenters to create the Cloudlets in the
     * {@link #getCloudletWaitingList() waiting list}.
     * If there aren't available VMs to host all cloudlets,
     * the creation of some of them will be postponed.
     *
     * <p>This method is called after all submitted VMs are created in some Datacenter.</p>
     *
     * @see #submitCloudletList(java.util.List)
     */
    protected void requestDatacentersToCreateWaitingCloudlets() {
        schedule(CloudSimTag.CLOUDLET_CREATION);
    }

    private boolean createWaitingCloudlets() {
        /* Uses Iterator to remove Cloudlets from the waiting list
         * while iterating over that List. This avoids the collection of successfully
         * created Cloudlets into a separate list.
         * Cloudlets in such new list were removed just after the loop,
         * degrading performance in large scale simulations. */
        int createdCloudlets = 0;
        for (final var iterator = cloudletWaitingList.iterator(); iterator.hasNext(); ) {
            final CloudletSimple cloudlet = (CloudletSimple)iterator.next();
            if (!cloudlet.getLastTriedDatacenter().equals(Datacenter.NULL)) {
                continue;
            }

            //selects a VM for the given Cloudlet
            lastSelectedVm = vmMapper.apply(cloudlet);
            if (!lastSelectedVm.isCreated()) {
                logPostponingCloudletExecution(cloudlet);
                continue;
            }

            ((VmSimple) lastSelectedVm).removeExpectedFreePesNumber(cloudlet.getPesNumber());

            cloudlet.setVm(lastSelectedVm);
            logCloudletCreationRequest(cloudlet);
            final Datacenter dc = getDatacenter(lastSelectedVm);
            final double totalDelay = cloudlet.getSubmissionDelay() + getVmStartupDelay(cloudlet);
            send(dc, totalDelay, CloudSimTag.CLOUDLET_SUBMIT, cloudlet);
            cloudlet.setLastTriedDatacenter(dc);
            cloudletCreatedList.add(cloudlet);
            iterator.remove();
            createdCloudlets++;
        }

        allWaitingCloudletsSubmittedToVm(createdCloudlets);
        return createdCloudlets > 0;
    }

    /**
     * {@return the startup delay (in seconds) of a VM running a given Cloudlet if the VM is booting up, or 0 if it's already running}
     * @param cloudlet the cloudlet to get the VM startup delay
     */
    private static double getVmStartupDelay(final Cloudlet cloudlet) {
        return cloudlet.getVm().isStartingUp() ? cloudlet.getVm().getStartupDelay() : 0;
    }

    private void logPostponingCloudletExecution(final Cloudlet cloudlet) {
        if(getSimulation().isAborted() || getSimulation().isAbortRequested())
            return;

        final Vm vm = cloudlet.getVm();
        final String vmMsg = Vm.NULL.equals(vm) ?
                                "it couldn't be mapped to any VM" :
                                "bind Vm %d is not available".formatted(vm.getId());

        final String msg =
            "%s: %s: Postponing execution of Cloudlet %d because {}."
            .formatted(getSimulation().clockStr(), getName(), cloudlet.getId());

        if (vm.getSubmissionDelay() <= 0) {
            LOGGER.warn(msg, vmMsg);
            return;
        }

        final String secs = vm.getSubmissionDelay() > 1 ? "seconds" : "second";
        final var reason =
            "bind Vm %d was requested to be created with %.2f %s delay"
            .formatted(vm.getId(), vm.getSubmissionDelay(), secs);
        LOGGER.info(msg, reason);
    }

    private void logCloudletCreationRequest(final Cloudlet cloudlet) {
        final double submission = cloudlet.getSubmissionDelay();
        final double startup = getVmStartupDelay(cloudlet);
        final double totalDelay = submission + startup;

        final var submissionStr = submission > 0 ? "requested submission delay" : "";
        final var connector = submission > 0 && startup > 0 ? " + " : "";
        final var startupStr = startup > 0 ? "VM boot up time" : "";

        final var delayMsg = totalDelay == 0 ? "" : " in %.2f seconds (%s%s%s)".formatted(totalDelay, submissionStr, connector, startupStr);

        LOGGER.info(
            "{}: {}: Sending Cloudlet {} to {} inside {}{}.",
            getSimulation().clockStr(), getName(), cloudlet.getId(),
            lastSelectedVm, lastSelectedVm.getHost(), delayMsg);
    }

    /**
     * {@return true if all waiting Cloudlets were submitted to some VM, false otherwise}
     * @param createdCloudlets number of Cloudlets previously waiting that have been just created
     */
    private boolean allWaitingCloudletsSubmittedToVm(final int createdCloudlets) {
        if (!cloudletWaitingList.isEmpty()) {
            return false;
        }

        //avoid duplicated notifications
        if (wereThereWaitingCloudlets) {
            LOGGER.info(
                "{}: {}: All {} waiting Cloudlets submitted to some VM.",
                getSimulation().clockStr(), getName(), createdCloudlets);
            wereThereWaitingCloudlets = false;
        }

        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        LOGGER.info("{}: {} is shutting down...", getSimulation().clockStr(), getName());
        requestVmDestructionAfterAllCloudletsFinished();
    }

    @Override
    public void startInternal() {
        LOGGER.info("{} is starting...", this);
        schedule(getSimulation().getCis(), 0, CloudSimTag.DC_LIST_REQUEST);
    }

    @Override
    public <T extends Vm> List<T> getVmCreatedList() {
        return (List<T>) vmCreatedList;
    }

    @Override
    public <T extends Vm> List<T> getVmExecList() {
        return (List<T>) vmExecList;
    }

    @Override
    public <T extends Vm> List<T> getVmWaitingList() {
        return (List<T>) vmWaitingList;
    }

    @Override
    public <T extends Vm> List<T> getVmFailedList() {
        return  (List<T>) vmFailedList;
    }

    @Override
    public Vm getWaitingVm(final int index) {
        if (index >= 0 && index < vmWaitingList.size()) {
            return vmWaitingList.get(index);
        }

        return Vm.NULL;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletWaitingList() {
        return (List<T>) cloudletWaitingList;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletFinishedList() {
        return (List<T>) new ArrayList<>(cloudletFinishedList);
    }

    /**
     * Gets a Vm at a given index from the {@link #getVmExecList() list of created VMs}.
     *
     * @param vmIndex the index where a VM has to be got from the created VM list
     * @return the VM at the given index or {@link Vm#NULL} if the index is invalid
     */
    protected Vm getVmFromCreatedList(final int vmIndex) {
        return vmIndex >= 0 && vmIndex < vmExecList.size() ? vmExecList.get(vmIndex) : Vm.NULL;
    }

    /**
     * Sets the list of available datacenters.
     *
     * @param datacenterList the new datacenter list
     */
    private void setDatacenterList(final List<Datacenter> datacenterList) {
        this.datacenterList = new ArrayList<>(datacenterList);
        if(selectClosestDatacenter){
            this.datacenterList.sort(Comparator.comparingDouble(Datacenter::getTimeZone));
        }
    }

    /**
     * {@return the Datacenter where a VM is placed}
     * @param vm the VM to get its Datacenter
     */
    protected Datacenter getDatacenter(final Vm vm) {
        return vm.getHost().getDatacenter();
    }

    @Override
    public DatacenterBroker addOnVmsCreatedListener(@NonNull final EventListener<DatacenterBrokerEventInfo> listener) {
        this.onVmsCreatedListeners.add(listener);
        return this;
    }

    @Override
    public DatacenterBroker removeOnVmsCreatedListener(@NonNull final EventListener<? extends EventInfo> listener) {
        this.onVmsCreatedListeners.remove(listener);
        return this;
    }

    @Override
    public String toString() {
        return "Broker " + getId();
    }

    @Override
    public DatacenterBroker setVmDestructionDelay(final double delay) {
        if(delay <= getSimulation().getMinTimeBetweenEvents() && delay != DEF_VM_DESTRUCTION_DELAY){
            final var msg = "The delay should be larger then the simulation minTimeBetweenEvents to ensure VMs are gracefully shutdown.";
            throw new IllegalArgumentException(msg);
        }

        setVmDestructionDelayFunction(vm -> delay);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p>If null is given, the default VM destruction delay function will be used.</p>
     * @param function {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public DatacenterBroker setVmDestructionDelayFunction(final Function<Vm, Double> function) {
        this.vmDestructionDelayFunction = function == null ? DEF_VM_DESTRUCTION_DELAY_FUNC : function;
        return this;
    }

    /**
     * Selects a Datacenter to request the creating of waiting VMs.
     * This is the default policy used to select a Datacenter to run {@link #getVmWaitingList() waiting VMs},
     * if the {@link #isSelectClosestDatacenter() selection of the closest datacenter} is disabled.
     * @param lastDatacenter the last selected Datacenter
     * @param vm the VM trying to be created
     * @return the selected datacenter or {@link Datacenter#NULL} if no suitable Datacenter was found
     * @see DatacenterBroker#setDatacenterMapper(BiFunction)
     * @see #closestDatacenterMapper(Datacenter, Vm)
     */
    protected abstract Datacenter defaultDatacenterMapper(Datacenter lastDatacenter, Vm vm);

    /**
     * Selects the closest Datacenter to request the creating of waiting VMs, according to their timezone offset.
     * This policy is just used if the {@link #isSelectClosestDatacenter() selection of the closest datacenter} is enabled.
     *
     * @param lastDatacenter the last selected Datacenter
     * @param vm the VM trying to be created
     * @return the selected datacenter or {@link Datacenter#NULL} if no suitable Datacenter was found
     * @see #defaultDatacenterMapper(Datacenter, Vm)
     * @see #setSelectClosestDatacenter(boolean)
     */
    protected Datacenter closestDatacenterMapper(final Datacenter lastDatacenter, final Vm vm) {
        return TimeZoned.closestDatacenter(vm, getDatacenterList());
    }

    /**
     * Selects a VM to execute a given Cloudlet.
     * The method defines the default policy used to map VMs for Cloudlets
     * that are waiting to be created.
     *
     * <p>Since this policy can be dynamically changed
     * by calling {@link #setVmMapper(Function)},
     * this method will always return the default policy
     * provided by the subclass where the method is being called.</p>
     *
     * @param cloudlet the cloudlet that needs a VM to execute
     * @return the selected Vm for the cloudlet or {@link Vm#NULL} if no suitable VM was found
     *
     * @see #setVmMapper(Function)
     */
    protected abstract Vm defaultVmMapper(Cloudlet cloudlet);
}
