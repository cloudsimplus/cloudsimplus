/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.*;
import org.cloudbus.cloudsim.core.events.CloudSimEvent;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.TimeZoned;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.util.InvalidEventDataTypeException;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.listeners.DatacenterBrokerEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.traces.google.GoogleTaskEventsTraceReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * An abstract class for implementing {@link DatacenterBroker}s.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
public abstract class DatacenterBrokerAbstract extends CloudSimEntity implements DatacenterBroker {
    /**
     * A default {@link Function} which always returns {@link #DEF_VM_DESTRUCTION_DELAY}
     * to indicate that any VM should not be immediately destroyed after it becomes idle.
     * This way, using this Function the broker will destroy VMs only after:
     * <ul>
     *   <li>all submitted Cloudlets from all its VMs are finished and there are no waiting Cloudlets;</li>
     *   <li>or all running Cloudlets are finished and there are some of them waiting their VMs to be created.</li>
     * </ul>
     *
     * @see #setVmDestructionDelayFunction(Function)
     */
    private static final Function<Vm, Double> DEF_VM_DESTRUCTION_DELAY_FUNC = vm -> DEF_VM_DESTRUCTION_DELAY;

    private boolean selectClosestDatacenter;

    /**
     * A List of registered event listeners for the onVmsCreatedListeners event.
     *
     * @see #addOnVmsCreatedListener(EventListener)
     */
    private final List<EventListener<DatacenterBrokerEventInfo>> onVmsCreatedListeners;

    /**
     * Last Vm selected to run some Cloudlets.
     */
    private Vm lastSelectedVm;

    /**
     * The last datacenter where a VM was created or tried to be created.
     */
    private Datacenter lastSelectedDc;

    /** @see #setFailedVmsRetryDelay(double)  */
    private double failedVmsRetryDelay;

    /** @see #getVmFailedList() */
    private final List<Vm> vmFailedList;

    /** @see #getVmWaitingList() */
    private final List<Vm> vmWaitingList;

    /** @see #getVmExecList() */
    private final List<Vm> vmExecList;

    /** @see #getVmCreatedList() */
    private final List<Vm> vmCreatedList;

    /** @see #getCloudletWaitingList() */
    private final List<Cloudlet> cloudletWaitingList;

    /** @see #getCloudletSubmittedList() */
    private final List<Cloudlet> cloudletSubmittedList;

    /** @see #getCloudletFinishedList() */
    private final List<Cloudlet> cloudletsFinishedList;

    /** @see #getCloudletCreatedList() () */
    private final List<Cloudlet> cloudletsCreatedList;

    /**
     * Checks if the last time checked, there were waiting cloudlets or not.
     */
    private boolean wereThereWaitingCloudlets;

    /** @see #setDatacenterMapper(BiFunction) */
    private BiFunction<Datacenter, Vm, Datacenter> datacenterMapper;

    /** @see #setVmMapper(Function) */
    private Function<Cloudlet, Vm> vmMapper;

    /** @see #setVmComparator(Comparator) */
    private Comparator<Vm> vmComparator;

    /** @see #setCloudletComparator(Comparator) */
    private Comparator<Cloudlet> cloudletComparator;

    /** @see #getVmCreationRequests() */
    private int vmCreationRequests;

    /** @see #getDatacenterList() */
    private List<Datacenter> datacenterList;

    private Cloudlet lastSubmittedCloudlet;
    private Vm lastSubmittedVm;

    /** @see #getVmDestructionDelayFunction() */
    private Function<Vm, Double> vmDestructionDelayFunction;

    /**
     * Indicates if a shutdown request was already sent or not.
     */
    private boolean shutdownRequested;

    /** @see #isShutdownWhenIdle()  */
    private boolean shutdownWhenIdle;
    private boolean vmCreationRetrySent;

    /**
     * Creates a DatacenterBroker giving a specific name.
     * Subclasses usually should provide this constructor
     * and overloaded version that just requires the {@link CloudSim} parameter.
     *
     * @param simulation the CloudSim instance that represents the simulation the Entity is related to
     * @param name the DatacenterBroker name
     */
    public DatacenterBrokerAbstract(final CloudSim simulation, final String name) {
        super(simulation);
        if(!name.isEmpty()) {
            setName(name);
        }

        this.onVmsCreatedListeners = new ArrayList<>();
        this.lastSubmittedCloudlet = Cloudlet.NULL;
        this.lastSubmittedVm = Vm.NULL;
        this.lastSelectedVm = Vm.NULL;
        this.lastSelectedDc = Datacenter.NULL;
        this.shutdownWhenIdle = true;

        this.vmCreationRequests = 0;
        this.failedVmsRetryDelay = 5;
        this.vmFailedList = new ArrayList<>();
        this.vmWaitingList = new ArrayList<>();
        this.vmExecList = new ArrayList<>();
        this.vmCreatedList = new ArrayList<>();
        this.cloudletWaitingList = new ArrayList<>();
        this.cloudletsFinishedList = new ArrayList<>();
        this.cloudletsCreatedList = new ArrayList<>();
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
    public boolean isSelectClosestDatacenter() {
        return selectClosestDatacenter;
    }

    @Override
    public DatacenterBroker submitVmList(final List<? extends Vm> list, final double submissionDelay) {
        setDelayForEntitiesWithNoDelay(list, submissionDelay);
        return submitVmList(list);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The individual submission delay of VMs inside the group will be ignored.
     * Only the submission delay set for the {@link VmGroup} will be considered.</p>
     *
     * <p>If the entity already started (the simulation is running),
     * the creation of previously submitted VMs already was requested
     * by the {@link #start()} method that is called just once.
     * By this way, this method will immediately request the creation of these
     * just submitted VMs in order to allow VM creation after
     * the simulation has started. This avoid the developer to
     * dynamically create brokers just to create VMs or Cloudlets during
     * simulation execution.</p>
     *
     * @param list {@inheritDoc}
     * @see VmGroup
     * @return {@inheritDoc}
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
                lastSelectedDc = null;
                requestDatacenterToCreateWaitingVms(false, false);
            }
        }

        return this;
    }

    /**
     * Configures attributes for each {@link CustomerEntity} into a given list.
     *
     * @param customerEntities the List of {@link CustomerEntity} to configure.
     */
    private void configureEntities(final List<? extends CustomerEntity> customerEntities) {
        for (final var entity : customerEntities) {
            entity.setBroker(this);
            entity.setArrivedTime(getSimulation().clock());
            if(entity instanceof VmGroup vmGroup) {
                configureEntities(vmGroup.getVmList());
            }
        }
    }

    /**
     * Defines IDs for a list of {@link CustomerEntity} entities that don't
     * have one already assigned. Such entities can be a {@link Cloudlet},
     * {@link Vm}, {@link VmGroup} or any object that implements {@link CustomerEntity}.
     *
     * @param list                list of objects to define an ID
     * @param lastSubmittedEntity the last Entity that was submitted to the broker
     * @return the last Entity in the given List of the lastSubmittedEntity if the List is empty
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
    public DatacenterBroker submitVm(final Vm vm) {
        requireNonNull(vm);
        if (Vm.NULL.equals(vm)) {
            return this;
        }

        final var newVmList = new ArrayList<Vm>(1);
        newVmList.add(vm);
        return submitVmList(newVmList);
    }

    @Override
    public DatacenterBroker submitCloudlet(final Cloudlet cloudlet) {
        requireNonNull(cloudlet);
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
     * <p>If the entity already started (the simulation is running),
     * the creation of previously submitted Cloudlets already was requested
     * by the {@link #start()} method that is called just once.
     * By this way, this method will immediately request the creation of these
     * just submitted Cloudlets if all submitted VMs were already created,
     * in order to allow Cloudlet creation after
     * the simulation has started. This avoid the developer to
     * dynamically create brokers just to create VMs or Cloudlets during
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
     * Binds a list of Cloudlets to a given {@link Vm}.
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
     * Sets the delay for a list of {@link CustomerEntity} entities that don't
     * have a delay already assigned. Such entities can be a {@link Cloudlet},
     * {@link Vm} or any object that implements {@link CustomerEntity}.
     *
     * <p>If the delay is defined as a negative number, objects' delay
     * won't be changed.</p>
     *
     * @param entities list of objects to set their delays
     * @param submissionDelay the submission delay to set
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
            case CLOUDLET_RETURN -> processCloudletReturn(evt);
            case CLOUDLET_READY -> processCloudletReady(evt);
            /* The data of such a kind of event is a Runnable that has all
             * the logic to update Cloudlet's attributes.
             * This way, it will be run to perform such an update.
             * Check the documentation of the tag below for details.*/
            case CLOUDLET_UPDATE_ATTRIBUTES -> executeRunnableEvent(evt);
            case CLOUDLET_PAUSE -> processCloudletPause(evt);
            case CLOUDLET_CANCEL -> processCloudletCancel(evt);
            case CLOUDLET_FINISH -> processCloudletFinish(evt);
            case CLOUDLET_FAIL -> processCloudletFail(evt);
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
            case VM_CREATE_RETRY -> {
                vmCreationRetrySent = false;
                yield requestDatacenterToCreateWaitingVms(false, true);
            }
            case VM_CREATE_ACK -> processVmCreateResponseFromDatacenter(evt);
            case VM_VERTICAL_SCALING -> requestVmVerticalScaling(evt);
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
     * Process the request to finish a Cloudlet with a indefinite length,
     * setting its length as the current number of processed MI.
     * @param evt the event data
     */
    private boolean processCloudletFinish(final SimEvent evt){
        final var cloudlet = (Cloudlet)evt.getData();
        logCloudletStatusChange(cloudlet, "finish running");
        /* If the executed length is zero, it means the cloudlet processing was not updated yet.
         * This way, calls the method to update the Cloudlet's processing.*/
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

        /* After defining the Cloudlet length, updates the Cloudlet processing again so that the Cloudlet status
         * is updated at this clock tick instead of the next one.*/
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
     * @param cloudlet
     */
    private void updateHostProcessing(final Cloudlet cloudlet) {
        cloudlet.getVm().getHost().updateProcessing(getSimulation().clock());
    }

    private void logCloudletStatusChange(final Cloudlet cloudlet, final String status) {
        final String msg = cloudlet.getJobId() > 0 ? String.format("(job %d) ", cloudlet.getJobId()) : "";
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
        if(evt.getData() instanceof List datacenterSet) {
            setDatacenterList(datacenterSet);
            LOGGER.info("{}: {}: List of {} datacenters(s) received.", getSimulation().clockStr(), getName(), datacenterList.size());
            requestDatacenterToCreateWaitingVms(false, false);
            return;
        }

        throw new InvalidEventDataTypeException(evt, "DC_LIST_REQUEST", "List<Datacenter>");
    }

    /**
     * Process the ack received from a Datacenter to a broker's request for
     * creation of a Vm in that Datacenter.
     *
     * @param evt a SimEvent object
     * @return true if the VM was created successfully, false otherwise
     */
    private boolean processVmCreateResponseFromDatacenter(final SimEvent evt) {
        final var vm = (Vm) evt.getData();

        //if the VM was successfully created in the requested Datacenter
        if (vm.isCreated()) {
            processSuccessVmCreationInDatacenter(vm);
            vm.notifyOnHostAllocationListeners();
        } else {
            vm.setFailed(true);
            if(!isRetryFailedVms()){
                vmWaitingList.remove(vm);
                vmFailedList.add(vm);
                LOGGER.warn("{}: {}: {} has been moved to the failed list because creation retry is not enabled.", getSimulation().clockStr(), getName(), vm);
            }

            vm.notifyOnCreationFailureListeners(lastSelectedDc);
        }

        //Decreases to indicate an ack for the request was received (either if the VM was created or not)
        vmCreationRequests--;

        if(vmCreationRequests == 0 && !vmWaitingList.isEmpty()) {
            requestCreationOfWaitingVmsToFallbackDatacenter();
        }

        if(allNonDelayedVmsCreated()) {
            requestDatacentersToCreateWaitingCloudlets();
        }

        return vm.isCreated();
    }

    /**
     * Checks if all VMs submitted with no delay were created.
     * Only after that, cloudlets creation is requested.
     * Otherwise, all waiting cloudlets would be sent to the
     * first created VM.
     * @return
     */
    private boolean allNonDelayedVmsCreated() {
        return vmWaitingList.stream().noneMatch(vm -> vm.getSubmissionDelay() == 0);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void notifyOnVmsCreatedListeners() {
        if(!vmWaitingList.isEmpty()) {
            return;
        }

        //Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onVmsCreatedListeners.size(); i++) {
            final var listener = onVmsCreatedListeners.get(i);
            listener.update(DatacenterBrokerEventInfo.of(listener, this));
        }
    }

    /**
     * After the response (ack) of all VM creation requests were received
     * but not all VMs could be created (what means some
     * acks informed about Vm creation failures), try to find
     * another Datacenter to request the creation of the VMs
     * in the waiting list.
     */
    private void requestCreationOfWaitingVmsToFallbackDatacenter() {
        this.lastSelectedDc = Datacenter.NULL;
        if (vmWaitingList.isEmpty() || requestDatacenterToCreateWaitingVms(false, true)) {
            return;
        }

        final var msg =
            "{}: {}: {} of the requested {} VMs couldn't be created because suitable Hosts weren't found in any available Datacenter."
            + (vmExecList.isEmpty() && !isRetryFailedVms() ? " Shutting broker down..." : "");
        LOGGER.error(msg, getSimulation().clockStr(), getName(), vmWaitingList.size(), getVmsNumber());

        /* If it gets here, it means that all datacenters were already queried and not all VMs could be created. */
        if (!vmWaitingList.isEmpty()) {
            processVmCreationFailure();
            return;
        }

        requestDatacentersToCreateWaitingCloudlets();
    }

    private void processVmCreationFailure() {
        if (isRetryFailedVms()) {
            lastSelectedDc = datacenterList.get(0);
            this.vmCreationRetrySent = true;
            schedule(failedVmsRetryDelay, CloudSimTag.VM_CREATE_RETRY);
        } else shutdown();
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
     *                             after the previous one was not able to create all waiting VMs,
     *                             false to indicate it will try the default datacenter.
     * @return true if some Datacenter was selected, false if all Datacenter were tried
     *         and not all VMs could be created
     * @see #submitVmList(java.util.List)
     */
    private boolean requestDatacenterToCreateWaitingVms(final boolean isFallbackDatacenter, final boolean creationRetry) {
        for (final Vm vm : vmWaitingList) {
            this.lastSelectedDc = isFallbackDatacenter && selectClosestDatacenter ?
                                        defaultDatacenterMapper(lastSelectedDc, vm) :
                                        datacenterMapper.apply(lastSelectedDc, vm);
            if(creationRetry) {
                vm.setLastTriedDatacenter(Datacenter.NULL);
            }
            this.vmCreationRequests += requestVmCreation(lastSelectedDc, isFallbackDatacenter, vm);
        }

        return lastSelectedDc != Datacenter.NULL;
    }

    @Override
    public int getVmsNumber() {
        return vmCreatedList.size() + vmWaitingList.size() + vmFailedList.size();
    }

    /**
     * Process a response from a Datacenter informing that it was able to
     * create the VM requested by the broker.
     *
     * @param vm id of the Vm that succeeded to be created inside the Datacenter
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
        cloudletsFinishedList.add(cloudlet);
        ((VmSimple) cloudlet.getVm()).addExpectedFreePesNumber(cloudlet.getNumberOfPes());
        final String lifeTime = cloudlet.getLifeTime() == -1 ? "" : " (after defined lifetime expired)";
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
        Idle VMs were destroyed above and here it requests the creation of waiting ones.
        When there are waiting Cloudlets, the destruction
        of idle VMs possibly free resources to start waiting VMs.
        This way, if a VM destruction delay function is not set,
        it defines one that always return 0 to indicate
        idle VMs must be destroyed immediately.
        */
        requestDatacenterToCreateWaitingVms(false, false);
    }

    @Override
    public DatacenterBroker requestIdleVmDestruction(final Vm vm) {
        if (vm.isCreated()) {
            if(isVmIdleEnough(vm) || isFinished()) {
                LOGGER.info("{}: {}: Requesting {} destruction.", getSimulation().clockStr(), getName(), vm);
                sendNow(getDatacenter(vm), CloudSimTag.VM_DESTROY, vm);
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

    @Override
    public List<Cloudlet> destroyVm(final Vm vm) {
        if(vm.isCreated()) {
            final var cloudletsAffectedList = new ArrayList<Cloudlet>();

            for (final var iterator = cloudletSubmittedList.iterator(); iterator.hasNext(); ) {
                final Cloudlet cloudlet = iterator.next();
                if(cloudlet.getVm().equals(vm) && !cloudlet.isFinished()) {
                    cloudlet.setVm(Vm.NULL);
                    cloudletsAffectedList.add(cloudlet.reset());
                    iterator.remove();
                }
            }

            vm.getHost().destroyVm(vm);
            vm.getCloudletScheduler().clear();
            return cloudletsAffectedList;
        }

        LOGGER.warn("Vm: " + vm.getId() + " does not belong to this broker! Broker: " + this);
        return new ArrayList<>();
    }

    /**
     * Checks if an event must be sent to verify if a VM became idle.
     * That will happen when the {@link #getVmDestructionDelayFunction() VM destruction delay}
     * is set and is not multiple of the {@link Datacenter#getSchedulingInterval()}
     *
     * <p>
     * In such situation, that means it is required to send additional events to check if a VM became idle.
     * No additional events are required when:
     * <ul>
     *   <li>the VM destruction delay was not set (VMs will be destroyed only when the broker is shutdown);</li>
     *   <li>the delay was set and it's multiple of the scheduling interval
     *   (VM idleness will be checked in the interval defined by the Datacenter scheduling).</li>
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
     * Checks if the broker is still alive and it's idle, so that it may be shutdown
     * @return
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
     * Try to request the creation of a VM into a given datacenter
     * @param datacenter the Datacenter to try creating the VM
     *                   (or {@link Datacenter#NULL} if not Datacenter is available)
     * @param isFallbackDatacenter indicate if the given Datacenter was selected when
     *                             a previous one don't have enough capacity to place the requested VM
     * @param vm the VM to be placed
     * @return 1 to indicate a VM creation request was sent to the datacenter,
     *         0 to indicate the request was not sent due to lack of available datacenter
     */
    private int requestVmCreation(final Datacenter datacenter, final boolean isFallbackDatacenter, final Vm vm) {
        if (datacenter == Datacenter.NULL || datacenter.equals(vm.getLastTriedDatacenter())) {
            return 0;
        }

        logVmCreationRequest(datacenter, isFallbackDatacenter, vm);
        send(datacenter, vm.getSubmissionDelay(), CloudSimTag.VM_CREATE_ACK, vm);
        vm.setLastTriedDatacenter(datacenter);
        return 1;
    }

    private void logVmCreationRequest(final Datacenter datacenter, final boolean isFallbackDatacenter, final Vm vm) {
        final var fallbackMsg = isFallbackDatacenter ? " (due to lack of a suitable Host in previous one)" : "";
        if(vm.getSubmissionDelay() == 0)
            LOGGER.info(
                "{}: {}: Trying to create {} in {}{}",
                getSimulation().clockStr(), getName(), vm, datacenter.getName(), fallbackMsg);
        else
            LOGGER.info(
                "{}: {}: Creation of {} in {}{} will be requested in {} seconds",
                getSimulation().clockStr(), getName(), vm, datacenter.getName(),
                fallbackMsg, vm.getSubmissionDelay());
    }

    /**
     * Request Datacenters to create the Cloudlets in the
     * {@link #getCloudletWaitingList() Cloudlets waiting list}.
     * If there aren't available VMs to host all cloudlets,
     * the creation of some ones will be postponed.
     *
     * <p>This method is called after all submitted VMs are created
     * in some Datacenter.</p>
     *
     * @see #submitCloudletList(java.util.List)
     */
    protected void requestDatacentersToCreateWaitingCloudlets() {
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

            ((VmSimple) lastSelectedVm).removeExpectedFreePesNumber(cloudlet.getNumberOfPes());

            logCloudletCreationRequest(cloudlet);
            cloudlet.setVm(lastSelectedVm);
            final Datacenter dc = getDatacenter(lastSelectedVm);
            send(dc, cloudlet.getSubmissionDelay(), CloudSimTag.CLOUDLET_SUBMIT, cloudlet);
            cloudlet.setLastTriedDatacenter(dc);
            cloudletsCreatedList.add(cloudlet);
            iterator.remove();
            createdCloudlets++;
        }

        allWaitingCloudletsSubmittedToVm(createdCloudlets);
    }

    private void logPostponingCloudletExecution(final Cloudlet cloudlet) {
        if(getSimulation().isAborted() || getSimulation().isAbortRequested())
            return;

        final Vm vm = cloudlet.getVm();
        final String vmMsg = Vm.NULL.equals(vm) ?
                                "it couldn't be mapped to any VM" :
                                String.format("bind Vm %d is not available", vm.getId());

        final String msg = String.format(
            "%s: %s: Postponing execution of Cloudlet %d because {}.",
            getSimulation().clockStr(), getName(), cloudlet.getId());

        if(vm.getSubmissionDelay() > 0) {
            final String secs = vm.getSubmissionDelay() > 1 ? "seconds" : "second";
            final var reason = String.format("bind Vm %d was requested to be created with %.2f %s delay", vm.getId(), vm.getSubmissionDelay(), secs);
            LOGGER.info(msg, reason);
        } else LOGGER.warn(msg, vmMsg);
    }

    private void logCloudletCreationRequest(final Cloudlet cloudlet) {
        final String delayMsg =
            cloudlet.getSubmissionDelay() > 0 ?
                String.format(" with a requested delay of %.0f seconds", cloudlet.getSubmissionDelay()) :
                "";

        LOGGER.info(
            "{}: {}: Sending Cloudlet {} to {} in {}{}.",
            getSimulation().clockStr(), getName(), cloudlet.getId(),
            lastSelectedVm, lastSelectedVm.getHost(), delayMsg);
    }

    /**
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
        LOGGER.info("{} is starting...", getName());
        schedule(getSimulation().getCloudInfoService(), 0, CloudSimTag.DC_LIST_REQUEST);
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
    public Vm getWaitingVm(final int index) {
        if (index >= 0 && index < vmWaitingList.size()) {
            return vmWaitingList.get(index);
        }

        return Vm.NULL;
    }

    @Override
    public List<Cloudlet> getCloudletCreatedList() {
        return cloudletsCreatedList;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletWaitingList() {
        return (List<T>) cloudletWaitingList;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletFinishedList() {
        return (List<T>) new ArrayList<>(cloudletsFinishedList);
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
     * Gets the number of VM creation requests.
     *
     * @return the number of VM creation requests
     */
    protected int getVmCreationRequests() {
        return vmCreationRequests;
    }

    /**
     * Gets the list of available datacenters.
     *
     * @return the dc list
     */
    protected List<Datacenter> getDatacenterList() {
        return datacenterList;
    }

    /**
     * Sets the list of available datacenters.
     *
     * @param datacenterList the new dc list
     */
    private void setDatacenterList(final List<Datacenter> datacenterList) {
        this.datacenterList = new ArrayList<>(datacenterList);
        if(selectClosestDatacenter){
            this.datacenterList.sort(Comparator.comparingDouble(Datacenter::getTimeZone));
        }
    }

    /**
     * Gets the Datacenter where a VM is placed.
     *
     * @param vm the VM to get its Datacenter
     * @return
     */
    protected Datacenter getDatacenter(final Vm vm) {
        return vm.getHost().getDatacenter();
    }

    @Override
    public final DatacenterBroker setDatacenterMapper(final BiFunction<Datacenter, Vm, Datacenter> datacenterMapper) {
        this.datacenterMapper = requireNonNull(datacenterMapper);
        return this;
    }

    @Override
    public final DatacenterBroker setVmMapper(final Function<Cloudlet, Vm> vmMapper) {
        this.vmMapper = requireNonNull(vmMapper);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p>If null is given, VMs won't be sorted and follow submission order.</p>
     * @param comparator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public DatacenterBroker setVmComparator(final Comparator<Vm> comparator) {
        this.vmComparator = comparator;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p>If null is given, Cloudlets won't be sorted and follow submission order.</p>
     * @param comparator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public void setCloudletComparator(final Comparator<Cloudlet> comparator) {
        this.cloudletComparator = comparator;
    }

    @Override
    public DatacenterBroker addOnVmsCreatedListener(final EventListener<DatacenterBrokerEventInfo> listener) {
        this.onVmsCreatedListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public DatacenterBroker removeOnVmsCreatedListener(final EventListener<? extends EventInfo> listener) {
        this.onVmsCreatedListeners.remove(requireNonNull(listener));
        return this;
    }

    @Override
    public String toString() {
        return "Broker " + getId();
    }

    @Override
    public Function<Vm, Double> getVmDestructionDelayFunction() {
        return vmDestructionDelayFunction;
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

    @Override
    public List<Cloudlet> getCloudletSubmittedList() {
        return cloudletSubmittedList;
    }

    /**
     * The policy used to select the closest Datacenter to run each {@link #getVmWaitingList() waiting VM},
     * according to their timezone offset.
     * This policy is just used if {@link #isSelectClosestDatacenter() selection of the closest datacenter} is enabled.
     *
     * @param lastDatacenter the last selected Datacenter
     * @param vm the VM trying to be created
     * @return the Datacenter selected to request the creating of waiting VMs
     *         or {@link Datacenter#NULL} if no suitable Datacenter was found
     * @see #defaultDatacenterMapper(Datacenter, Vm)
     * @see #setSelectClosestDatacenter(boolean)
     */
    protected Datacenter closestDatacenterMapper(final Datacenter lastDatacenter, final Vm vm) {
        return TimeZoned.closestDatacenter(vm, getDatacenterList());
    }

    /**
     * The default policy used to select a Datacenter to run {@link #getVmWaitingList() waiting VMs}.
     * @param lastDatacenter the last selected Datacenter
     * @param vm the VM trying to be created
     * @return the Datacenter selected to request the creating of waiting VMs
     *         or {@link Datacenter#NULL} if no suitable Datacenter was found
     * @see DatacenterBroker#setDatacenterMapper(BiFunction)
     * @see #closestDatacenterMapper(Datacenter, Vm)
     */
    protected abstract Datacenter defaultDatacenterMapper(Datacenter lastDatacenter, Vm vm);

    /**
     * The default policy used to select a VM to execute a given Cloudlet.
     * The method defines the default policy used to map VMs for Cloudlets
     * that are waiting to be created.
     *
     * <p>Since this policy can be dynamically changed
     * by calling {@link #setVmMapper(Function)},
     * this method will always return the default policy
     * provided by the subclass where the method is being called.</p>
     *
     * @param cloudlet the cloudlet that needs a VM to execute
     * @return the selected Vm for the cloudlet or {@link Vm#NULL} if
     * no suitable VM was found
     *
     * @see #setVmMapper(Function)
     */
    protected abstract Vm defaultVmMapper(Cloudlet cloudlet);

    @Override
    public <T extends Vm> List<T> getVmFailedList() {
        return  (List<T>) vmFailedList;
    }

    @Override
    public boolean isRetryFailedVms() {
        return failedVmsRetryDelay > 0;
    }

    @Override
    public double getFailedVmsRetryDelay() {
        return failedVmsRetryDelay;
    }

    @Override
    public void setFailedVmsRetryDelay(final double failedVmsRetryDelay) {
        this.failedVmsRetryDelay = failedVmsRetryDelay;
    }

    @Override
    public boolean isShutdownWhenIdle() {
        return shutdownWhenIdle;
    }

    @Override
    public DatacenterBroker setShutdownWhenIdle(final boolean shutdownWhenIdle) {
        this.shutdownWhenIdle = shutdownWhenIdle;
        return this;
    }
}
