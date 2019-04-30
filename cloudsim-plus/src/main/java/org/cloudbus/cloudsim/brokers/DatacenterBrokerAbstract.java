/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.*;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.listeners.DatacenterBrokerEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.traces.google.GoogleTaskEventsTraceReader;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * An abstract class to be used as base for implementing a {@link DatacenterBroker}.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
public abstract class DatacenterBrokerAbstract extends CloudSimEntity implements DatacenterBroker {

    /**
     * A default {@link Function} which always returns {@link #DEF_VM_DESTRUCTION_DELAY} to indicate that any VM should not be
     * immediately destroyed after it becomes idle.
     * This way, using this Function the broker will destroy VMs only after:
     * <ul>
     * <li>all submitted Cloudlets from all its VMs are finished and there are no waiting Cloudlets;</li>
     * <li>or all running Cloudlets are finished and there are some of them waiting their VMs to be created.</li>
     * </ul>
     *
     * @see #setVmDestructionDelayFunction(Function)
     */
    private static final Function<Vm, Double> DEF_VM_DESTRUCTION_DELAY_FUNCTION = vm -> DEF_VM_DESTRUCTION_DELAY;

    /**
     * A map of registered event listeners for the onVmsCreatedListeners event
     * that the key is the Listener itself and the value indicates if it's a one
     * time listener (which is removed from the list after being notified for the first time).
     *
     * @see #addOnVmsCreatedListener(EventListener)
     */
    private final List<EventListener<DatacenterBrokerEventInfo>> onVmsCreatedListeners;

    /**
     * @see #getLastSelectedVm()
     */
    private Vm lastSelectedVm;

    /**
     * The last datacenter where a VM was created or tried to be created.
     */
    private Datacenter lastSelectedDc;

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

    private Supplier<Datacenter> datacenterSupplier;
    private Supplier<Datacenter> fallbackDatacenterSupplier;
    private Function<Cloudlet, Vm> vmMapper;

    private Comparator<Vm> vmComparator;
    private Comparator<Cloudlet> cloudletComparator;

    /**
     * @see #getVmCreationRequests()
     */
    private int vmCreationRequests;
    /**
     * @see #getVmCreationAcks()
     */
    private int vmCreationAcks;
    /**
     * @see #getDatacenterList()
     */
    private List<Datacenter> datacenterList;
    /**
     * @see #getDatacenterRequestedList()
     */
    private final Set<Datacenter> datacenterRequestedList;

    private Cloudlet lastSubmittedCloudlet;
    private Vm lastSubmittedVm;

    /**
     * @see #getVmDestructionDelayFunction()
     */
    private Function<Vm, Double> vmDestructionDelayFunction;

    /**
     * Creates a DatacenterBroker.
     *
     * @param simulation the CloudSim instance that represents the simulation the Entity is related to
     */
    public DatacenterBrokerAbstract(final CloudSim simulation) {
        this(simulation, "");
    }

    /**
     * Creates a DatacenterBroker giving a specific name.
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

        vmCreationRequests = 0;
        vmCreationAcks = 0;

        this.vmWaitingList = new ArrayList<>();
        this.vmExecList = new ArrayList<>();
        this.vmCreatedList = new ArrayList<>();
        this.cloudletWaitingList = new ArrayList<>();
        this.cloudletsFinishedList = new ArrayList<>();
        this.cloudletsCreatedList = new ArrayList<>();
        this.cloudletSubmittedList = new ArrayList<>();

        setDatacenterList(new TreeSet<>());
        datacenterRequestedList = new TreeSet<>();
        setDefaultPolicies();

        vmDestructionDelayFunction = DEF_VM_DESTRUCTION_DELAY_FUNCTION;
    }

    /**
     * Sets the default dummy policies for {@link #datacenterSupplier},
     * {@link #fallbackDatacenterSupplier} and {@link #vmMapper}.
     * The actual policies must be set by concrete DatacenterBroker classes.
     */
    private void setDefaultPolicies() {
        datacenterSupplier = () -> Datacenter.NULL;
        fallbackDatacenterSupplier = datacenterSupplier;
        vmMapper = (cloudlet) -> Vm.NULL;
    }

    @Override
    public void submitVmList(final List<? extends Vm> list, final double submissionDelay) {
        setDelayForEntitiesWithNoDelay(list, submissionDelay);
        submitVmList(list);
    }

    /**
     * {@inheritDoc}
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
     */
    @Override
    public void submitVmList(final List<? extends Vm> list) {
        sortVmsIfComparatorIsSet(list);
        setBrokerForEntities(list);
        lastSubmittedVm = setIdForEntitiesWithoutOne(list, lastSubmittedVm);
        vmWaitingList.addAll(list);

        if (isStarted() && !list.isEmpty()) {
            LOGGER.info(
                "{}: {}: List of {} VMs submitted to the broker during simulation execution. VMs creation request sent to Datacenter.",
                getSimulation().clock(), getName(), list.size());
            requestDatacenterToCreateWaitingVms();
        }
    }

    /**
     * Sets the broker for each {@link CustomerEntity} into a given list.
     *
     * @param customerEntities the List of {@link CustomerEntity} to set the broker.
     */
    private void setBrokerForEntities(final List<? extends CustomerEntity> customerEntities) {
        customerEntities.forEach(e -> e.setBroker(this));
    }

    /**
     * Defines IDs for a list of {@link CustomerEntity} entities that don't
     * have one already assigned. Such entities can be a {@link Cloudlet},
     * {@link Vm} or any object that implements {@link CustomerEntity}.
     *
     * @param list                list of objects to define an ID
     * @param lastSubmittedEntity the last Entity that was submitted to the broker
     * @return the last Entity in the given List of the lastSubmittedEntity if the List is empty
     */
    private <T extends CustomerEntity> T setIdForEntitiesWithoutOne(final List<? extends T> list, T lastSubmittedEntity) {
        return Simulation.setIdForEntitiesWithoutOne(list, lastSubmittedEntity) ? list.get(list.size() - 1) : lastSubmittedEntity;
    }

    private void sortVmsIfComparatorIsSet(final List<? extends Vm> list) {
        if (vmComparator != null) {
            list.sort(vmComparator);
        }
    }

    @Override
    public void submitVm(final Vm vm) {
        requireNonNull(vm);
        if (vm == Vm.NULL) {
            return;
        }

        final List<Vm> newList = new ArrayList<>(1);
        newList.add(vm);
        submitVmList(newList);
    }

    @Override
    public void submitCloudlet(final Cloudlet cloudlet) {
        requireNonNull(cloudlet);
        if (cloudlet == Cloudlet.NULL) {
            return;
        }

        final List<Cloudlet> newList = new ArrayList<>(1);
        newList.add(cloudlet);
        submitCloudletList(newList);
    }

    @Override
    public void submitCloudletList(final List<? extends Cloudlet> list, double submissionDelay) {
        submitCloudletList(list, Vm.NULL, submissionDelay);
    }

    @Override
    public void submitCloudletList(final List<? extends Cloudlet> list, Vm vm) {
        submitCloudletList(list, vm, -1);
    }

    @Override
    public void submitCloudletList(final List<? extends Cloudlet> list, Vm vm, double submissionDelay) {
        setDelayForEntitiesWithNoDelay(list, submissionDelay);
        bindCloudletsToVm(list, vm);
        submitCloudletList(list);
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
     */
    @Override
    public void submitCloudletList(final List<? extends Cloudlet> list) {
        if (list.isEmpty()) {
            return;
        }
        sortCloudletsIfComparatorIsSet(list);
        setBrokerForEntities(list);
        lastSubmittedCloudlet = setIdForEntitiesWithoutOne(list, lastSubmittedCloudlet);
        cloudletSubmittedList.addAll(list);
        setSimulationForCloudletUtilizationModels(list);
        cloudletWaitingList.addAll(list);
        wereThereWaitingCloudlets = true;

        if (!isStarted()) {
            return;
        }

        LOGGER.info(
            "{}: {}: List of {} Cloudlets submitted to the broker during simulation execution.",
            getSimulation().clock(), getName(), list.size());

        if (allNonDelayedVmsCreated()) {
            LOGGER.info("Cloudlets creation request sent to Datacenter.");
            requestDatacentersToCreateWaitingCloudlets();
            notifyOnVmsCreatedListeners();
        } else
            LOGGER.info("Waiting creation of {} VMs to send Cloudlets creation request to Datacenter.", vmWaitingList.size());
    }

    /**
     * Checks if all VMs submitted with no delay were created.
     * @return
     */
    private boolean allNonDelayedVmsCreated() {
        return vmWaitingList.stream().noneMatch(vm -> vm.getSubmissionDelay() == 0);
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

    private void setSimulationForCloudletUtilizationModels(final List<? extends Cloudlet> cloudlets) {
        for (final Cloudlet cloudlet : cloudlets) {
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
        if (!this.equals(cloudlet.getBroker())) {
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

        LOGGER.trace("{}: {}: Unknown event {} received.", getSimulation().clock(), this, evt.getTag());
    }

    private boolean processCloudletEvents(final SimEvent evt) {
        switch (evt.getTag()) {
            case CloudSimTags.CLOUDLET_RETURN:
                processCloudletReturn(evt);
                return true;
            case CloudSimTags.CLOUDLET_READY:
                processCloudletReady(evt);
                return true;
            /* The data of such a kind of event is a Runnable that has all
             * the logic to update the Cloudlet's attributes.
             * This way, it will be run to perform such an update.
             * Check the documentation of the tag below for details.*/
            case CloudSimTags.CLOUDLET_UPDATE_ATTRIBUTES:
                ((Runnable) evt.getData()).run();
                return true;
            case CloudSimTags.CLOUDLET_PAUSE:
                processCloudletPause(evt);
                return true;
            case CloudSimTags.CLOUDLET_CANCEL:
                processCloudletCancel(evt);
                return true;
            case CloudSimTags.CLOUDLET_FINISH:
                processCloudletFinish(evt);
                return true;
            case CloudSimTags.CLOUDLET_FAIL:
                processCloudletFail(evt);
                return true;
        }

        return false;
    }

    private boolean processVmEvents(final SimEvent evt) {
        switch (evt.getTag()) {
            case CloudSimTags.VM_CREATE_ACK:
                processVmCreateResponseFromDatacenter(evt);
                return true;
            case CloudSimTags.VM_DESTROY:
                requestIdleVmDestruction((Vm) evt.getData());
                return true;
            case CloudSimTags.VM_VERTICAL_SCALING:
                requestVmVerticalScaling(evt);
                return true;
        }

        return false;
    }

    private boolean processGeneralEvents(final SimEvent evt) {
        if (evt.getTag() == CloudSimTags.DATACENTER_LIST_REQUEST) {
            processDatacenterListRequest(evt);
            return true;
        }

        if (evt.getTag() == CloudSimTags.END_OF_SIMULATION) {
            shutdownEntity();
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
     * @param evt the event data
     */
    private void processCloudletReady(final SimEvent evt){
        final Cloudlet cloudlet = (Cloudlet)evt.getData();
        if(cloudlet.getStatus() == Cloudlet.Status.PAUSED)
             logCloudletStatusChange(cloudlet, "resume execution of");
        else logCloudletStatusChange(cloudlet, "start executing");

        cloudlet.getVm().getCloudletScheduler().cloudletReady(cloudlet);
    }

    private void processCloudletPause(final SimEvent evt){
        final Cloudlet cloudlet = (Cloudlet)evt.getData();
        logCloudletStatusChange(cloudlet, "deschedule (pause)");
        cloudlet.getVm().getCloudletScheduler().cloudletPause(cloudlet);
    }

    private void processCloudletCancel(final SimEvent evt){
        final Cloudlet cloudlet = (Cloudlet)evt.getData();
        logCloudletStatusChange(cloudlet, "cancel execution of");
        cloudlet.getVm().getCloudletScheduler().cloudletCancel(cloudlet);
    }

    /**
     * Process the request to finish a Cloudlet with a indefinite length,
     * setting its length as the current number of processed MI.
     * @param evt the event data
     */
    private void processCloudletFinish(final SimEvent evt){
        final Cloudlet cloudlet = (Cloudlet)evt.getData();
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
            return;
        }

        final long prevLength = cloudlet.getLength();
        cloudlet.setLength(cloudlet.getFinishedLengthSoFar());

        /* After defining the Cloudlet length, updates the Cloudlet processing again so that the Cloudlet status
         * is updated at this clock tick instead of the next one.*/
        updateHostProcessing(cloudlet);

        /* If the Cloudlet length was negative,
         * after finishing it a VM update event is sent to ensure the broker is notified
         * the Cloudlet has finished.
         * A negative length makes the Cloudlet to keep running until a finish message is
         * sent to the broker. */
        if(prevLength < 0){
            final double delay = cloudlet.getSimulation().getMinTimeBetweenEvents();
            final Datacenter dc = cloudlet.getVm().getHost().getDatacenter();
            dc.schedule(delay, CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING, null);
        }
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
        LOGGER.info("{}: {}: Request to {} {} {}received.", getSimulation().clock(), this, status, cloudlet, msg);
    }

    private void processCloudletFail(final SimEvent evt){
        final Cloudlet cloudlet = (Cloudlet)evt.getData();
        cloudlet.getVm().getCloudletScheduler().cloudletFail(cloudlet);
    }

    private void requestVmVerticalScaling(final SimEvent evt) {
        if (!(evt.getData() instanceof VerticalVmScaling)) {
            return;
        }

        final VerticalVmScaling scaling = (VerticalVmScaling) evt.getData();
        getSimulation().sendNow(
            evt.getSource(), scaling.getVm().getHost().getDatacenter(),
            CloudSimTags.VM_VERTICAL_SCALING, scaling);
    }

    /**
     * Process a request to get the list of all Datacenters registered in the
     * Cloud Information Service (CIS) of the {@link #getSimulation() simulation}.
     *
     * @param evt a CloudSimEvent object
     */
    private void processDatacenterListRequest(final SimEvent evt) {
        setDatacenterList((Set<Datacenter>) evt.getData());
        LOGGER.info("{}: {}: List of {} datacenters(s) received.", getSimulation().clock(), getName(), datacenterList.size());
        requestDatacenterToCreateWaitingVms();
    }

    /**
     * Process the ack received from a Datacenter to a broker's request for
     * creation of a Vm in that Datacenter.
     *
     * @param evt a CloudSimEvent object
     * @return true if the VM was created successfully, false otherwise
     */
    private boolean processVmCreateResponseFromDatacenter(final SimEvent evt) {
        final Vm vm = (Vm) evt.getData();
        boolean vmCreated = false;
        vmCreationAcks++;

        //if the VM was successfully created in the requested Datacenter
        if (vm.isCreated()) {
            processSuccessVmCreationInDatacenter(vm, vm.getHost().getDatacenter());
            vmCreated = true;
        } else {
            processFailedVmCreationInDatacenter(vm, lastSelectedDc);
        }

        if (allNonDelayedVmsCreated()) {
            requestDatacentersToCreateWaitingCloudlets();
            notifyOnVmsCreatedListeners();
        } else if (getVmCreationRequests() == getVmCreationAcks()) {
            requestCreationOfWaitingVmsToFallbackDatacenter();
        }

        return vmCreated;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void notifyOnVmsCreatedListeners() {
        //Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onVmsCreatedListeners.size(); i++) {
            EventListener<DatacenterBrokerEventInfo> listener = onVmsCreatedListeners.get(i);
            listener.update(DatacenterBrokerEventInfo.of(listener, this));
        }
    }

    /**
     * After the response (ack) of all VM creation request were received
     * but not all VMs could be created (what means some
     * acks informed about Vm creation failures), try to find
     * another Datacenter to request the creation of the VMs
     * in the waiting list.
     */
    private void requestCreationOfWaitingVmsToFallbackDatacenter() {
        final Datacenter nextDatacenter = fallbackDatacenterSupplier.get();
        lastSelectedDc = nextDatacenter;
        if (nextDatacenter != Datacenter.NULL) {
            requestDatacenterToCreateWaitingVms(nextDatacenter, true);
            return;
        }

        /* If it gets here, it means that all datacenters were already queried
         * and not all VMs could be created. */
        if (vmExecList.isEmpty()) {
            LOGGER.error(
                "{}: {}: None of the requested {} VMs could be created because suitable Hosts weren't found in any available Datacenter. Shutting broker down...",
                getSimulation().clock(), getName(), vmWaitingList.size());
            shutdownEntity();
            return;
        }

        LOGGER.error(
            "{}: {}: {} of the requested {} VMs couldn't be created because suitable Hosts weren't found in any available Datacenter.",
            getSimulation().clock(), getName(), vmWaitingList.size(), getVmsNumber());


        requestDatacentersToCreateWaitingCloudlets();
    }

    /**
     * Gets the total number of broker's VMs, including created and waiting ones.
     *
     * @return
     */
    private int getVmsNumber() {
        return vmCreatedList.size() + vmWaitingList.size();
    }

    /**
     * Process a response from a Datacenter informing that it was able to
     * create the VM requested by the broker.
     *
     * @param vm         id of the Vm that succeeded to be created inside the Datacenter
     * @param datacenter id of the Datacenter where the request to create
     *                   the Vm succeeded
     */
    private void processSuccessVmCreationInDatacenter(final Vm vm, final Datacenter datacenter) {
        vmWaitingList.remove(vm);
        vmExecList.add(vm);
        vmCreatedList.add(vm);
    }

    /**
     * Process a response from a Datacenter informing that it was NOT able to
     * create the VM requested by the broker.
     *
     * @param vm         id of the Vm that failed to be created inside the Datacenter
     * @param datacenter id of the Datacenter where the request to create
     */
    private void processFailedVmCreationInDatacenter(final Vm vm, final Datacenter datacenter) {
        vm.notifyOnCreationFailureListeners(datacenter);
    }

    /**
     * Processes the end of execution of a given cloudlet inside a Vm.
     *
     * @param evt the cloudlet that has just finished to execute and was returned to the broker
     */
    private void processCloudletReturn(final SimEvent evt) {
        final Cloudlet cloudlet = (Cloudlet) evt.getData();
        cloudletsFinishedList.add(cloudlet);
        LOGGER.info("{}: {}: {} finished and returned to broker.", getSimulation().clock(), getName(), cloudlet);

        if (cloudlet.getVm().getCloudletScheduler().isEmpty()) {
            requestIdleVmDestruction(cloudlet.getVm());
            return;
        }

        requestVmDestructionAfterAllCloudletsFinished();
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
        When there is waiting Cloudlets, the destruction
        of idle VMs possibly free resources to start waiting VMs.
        This way, if a VM destruction delay function is not set,
        it defines one that always return 0 to indicate
        idle VMs must be destroyed immediately.
        */
        requestDatacenterToCreateWaitingVms();
    }

    /**
     * Request an idle VM to be destroyed at the time defined by a delay {@link Function}.
     * The request will be sent if the given delay function returns a value
     * greater than {@link #DEF_VM_DESTRUCTION_DELAY}.
     * Otherwise, it doesn't send the request, meaning the VM should not be destroyed according to a specific delay.
     *
     * @param vm the VM to destroy
     * @see #getVmDestructionDelayFunction()
     */
    private void requestIdleVmDestruction(final Vm vm) {
        final double delay = vmDestructionDelayFunction.apply(vm);

        boolean vmAlive = vm.isCreated();
        if (vmAlive && ((delay > DEF_VM_DESTRUCTION_DELAY && vm.isIdleEnough(delay)) || isFinished())) {
            LOGGER.info("{}: {}: Requesting Vm {} destruction.", getSimulation().clock(), getName(), vm.getId());
            sendNow(getDatacenter(vm), CloudSimTags.VM_DESTROY, vm);
            vmExecList.remove(vm);
            vmAlive = false;
        }

        if (isTimeToShutdownBroker() && isBrokerIdle()) {
            shutdownEntity();
            return;
        }

        if (vmAlive && delay > DEF_VM_DESTRUCTION_DELAY) {
            send(this, getDelayToCheckVmIdleness(vm), CloudSimTags.VM_DESTROY, vm);
        }
    }

    /**
     * Gets the time to wait to check again if a VM became idle,
     * based on the {@link #vmDestructionDelayFunction}.
     *
     * @param vm the VM to get the delay to check it idleness again
     * @return the delay to check the VM idleness again
     */
    private double getDelayToCheckVmIdleness(final Vm vm) {
        /*
         * If the delay or the schedulingInterval is not set
         * or is zero, gets the minTimeBetweenEvents, since
         * the delay to check VM idleness again has to be greater than zero.
         */
        final double schedulingInterval = vm.getHost().getDatacenter().getSchedulingInterval();
        final double delay = vmDestructionDelayFunction.apply(vm);

        if (delay <= 0 && schedulingInterval <= 0) {
            return getSimulation().getMinTimeBetweenEvents();
        } else if (delay <= 0) { // if just the delay is not a positive number
            return schedulingInterval;
        }

        return Math.min(Math.abs(delay), Math.abs(schedulingInterval));
    }

    private boolean isTimeToShutdownBroker() {
        return isAlive() &&
            (!getSimulation().isTerminationTimeSet() || getSimulation().isTimeToTerminateSimulationUnderRequest());
    }

    private boolean isBrokerIdle() {
        return cloudletWaitingList.isEmpty() &&
               vmWaitingList.isEmpty() &&
               vmExecList.isEmpty();
    }

    /**
     * Request the creation of VMs in the
     * {@link #getVmWaitingList() VM waiting list}
     * inside some Datacenter.
     *
     * @see #submitVmList(java.util.List)
     */
    protected void requestDatacenterToCreateWaitingVms() {
        lastSelectedDc = Datacenter.NULL.equals(lastSelectedDc) ? datacenterSupplier.get() : lastSelectedDc;
        requestDatacenterToCreateWaitingVms(lastSelectedDc);
    }

    /**
     * Request a specific Datacenter to create the VM in the
     * {@link #getVmWaitingList() VM waiting list}.
     *
     * @param datacenter id of the Datacenter to request the VMs creation
     * @see #submitVmList(java.util.List)
     */
    protected void requestDatacenterToCreateWaitingVms(final Datacenter datacenter) {
        requestDatacenterToCreateWaitingVms(datacenter, false);
    }

    /**
     * Request a specific Datacenter to create the VM in the
     * {@link #getVmWaitingList() VM waiting list}.
     *
     * @param datacenter           id of the Datacenter to request the VMs creation
     * @param isFallbackDatacenter true to indicate that the given Datacenter is a fallback one,
     *                             i.e., it's a next Datacenter where the creation of VMs is being tried
     *                             (after some VMs could not be created into the previous Datacenter);
     *                             false to indicate that this is a regular Datacenter where
     *                             VM creation has to be tried.
     * @see #submitVmList(java.util.List)
     */
    protected void requestDatacenterToCreateWaitingVms(final Datacenter datacenter, final boolean isFallbackDatacenter) {
        int requestedVms = 0;
        for (final Vm vm : vmWaitingList) {
            final CustomerEntityAbstract entity = (CustomerEntityAbstract) vm;
            if (!datacenter.equals(entity.getLastTriedDatacenter())) {
                logVmCreationRequest(datacenter, isFallbackDatacenter, vm);
                send(datacenter, vm.getSubmissionDelay(), CloudSimTags.VM_CREATE_ACK, vm);
                entity.setLastTriedDatacenter(datacenter);
                requestedVms++;
            }
        }
        datacenterRequestedList.add(datacenter);
        this.vmCreationRequests += requestedVms;
    }

    private void logVmCreationRequest(final Datacenter datacenter, final boolean isFallbackDatacenter, final Vm vm) {
        final String fallbackMsg = isFallbackDatacenter ? " (due to lack of a suitable Host in previous one)" : "";
        if(vm.getSubmissionDelay() == 0)
            LOGGER.info(
                "{}: {}: Trying to create Vm {} in {}{}",
                getSimulation().clock(), getName(), vm.getId(), datacenter.getName(), fallbackMsg);
        else
            LOGGER.info(
                "{}: {}: Creation of Vm {} in {}{} will be requested in {} seconds",
                getSimulation().clock(), getName(), vm.getId(), datacenter.getName(),
                fallbackMsg, vm.getSubmissionDelay());
    }

    /**
     * <p>Request Datacenters to create the Cloudlets in the
     * {@link #getCloudletWaitingList() Cloudlets waiting list}.
     * If there aren't available VMs to host all cloudlets,
     * the creation of some ones will be postponed.</p>
     *
     * <p>This method is called after all submitted VMs are created
     * in some Datacenter.</p>
     *
     * @see #submitCloudletList(java.util.List)
     */
    protected void requestDatacentersToCreateWaitingCloudlets() {
        /* @TODO autor: manoelcampos Where is checked if the Vm where
         *       a cloudlet was submitted to has the required resources?
         *       See https://github.com/manoelcampos/cloudsim-plus/issues/126
         */

        final List<Cloudlet> successfullySubmitted = new ArrayList<>();
        for (final Cloudlet cloudlet : cloudletWaitingList) {
            final CustomerEntityAbstract entity = (CustomerEntityAbstract) cloudlet;
            if (!entity.getLastTriedDatacenter().equals(Datacenter.NULL)) {
                continue;
            }

            //selects a VM for the given Cloudlet
            lastSelectedVm = vmMapper.apply(cloudlet);
            if (lastSelectedVm == Vm.NULL) {
                logPostponingCloudletExecution(cloudlet);
                continue;
            }

            logCloudletCreationRequest(cloudlet);
            cloudlet.setVm(lastSelectedVm);
            send(getDatacenter(lastSelectedVm),
                cloudlet.getSubmissionDelay(), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            entity.setLastTriedDatacenter(getDatacenter(lastSelectedVm));
            cloudletsCreatedList.add(cloudlet);
            successfullySubmitted.add(cloudlet);
        }

        cloudletWaitingList.removeAll(successfullySubmitted);
        allWaitingCloudletsSubmittedToVm();
    }

    private void logPostponingCloudletExecution(final Cloudlet cloudlet) {
        final String msg = String.format(
            "%.2f: %s: Postponing execution of Cloudlet %d. Bind Vm %d {}.",
            getSimulation().clock(), getName(), cloudlet.getId(), cloudlet.getVm().getId());

        if(cloudlet.getVm().getSubmissionDelay() > 0)
            LOGGER.info(msg, "was requested to be created with some delay");
        else LOGGER.warn(msg, "is not available");
    }

    private void logCloudletCreationRequest(final Cloudlet cloudlet) {
        final String delayMsg =
            cloudlet.getSubmissionDelay() > 0 ?
                String.format(" with a requested delay of %.0f seconds", cloudlet.getSubmissionDelay()) :
                "";

        LOGGER.info(
            "{}: {}: Sending Cloudlet {} to Vm {} in {}{}.",
            getSimulation().clock(), getName(), cloudlet.getId(),
            lastSelectedVm.getId(), lastSelectedVm.getHost(), delayMsg);
    }

    private boolean allWaitingCloudletsSubmittedToVm() {
        if (!cloudletWaitingList.isEmpty()) {
            return false;
        }

        //avoid duplicated notifications
        if (wereThereWaitingCloudlets) {
            LOGGER.info(
                "{}: {}: All waiting Cloudlets submitted to some VM.",
                getSimulation().clock(), getName());
            wereThereWaitingCloudlets = false;
        }

        return true;
    }

    @Override
    public void shutdownEntity() {
        super.shutdownEntity();
        LOGGER.info("{}: {} is shutting down...", getSimulation().clock(), getName());
        requestVmDestructionAfterAllCloudletsFinished();
    }

    @Override
    public void startEntity() {
        LOGGER.info("{} is starting...", getName());
        schedule(getSimulation().getCloudInfoService(), 0, CloudSimTags.DATACENTER_LIST_REQUEST);
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
     * Gets the number of acknowledges (ACKs) received from Datacenters
     * in response to requests to create VMs.
     * The number of acks doesn't mean the number of created VMs,
     * once Datacenters can respond informing that a Vm could not be created.
     *
     * @return the number vm creation acks
     */
    protected int getVmCreationAcks() {
        return vmCreationAcks;
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
    protected final void setDatacenterList(Set<Datacenter> datacenterList) {
        this.datacenterList = new ArrayList<>(datacenterList);
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

    /**
     * Gets the list of datacenters where was requested to place VMs.
     *
     * @return
     */
    protected Set<Datacenter> getDatacenterRequestedList() {
        return datacenterRequestedList;
    }

    /**
     * @return latest VM selected to run a cloudlet.
     */
    protected Vm getLastSelectedVm() {
        return lastSelectedVm;
    }

    @Override
    public final void setDatacenterSupplier(final Supplier<Datacenter> datacenterSupplier) {
        this.datacenterSupplier = requireNonNull(datacenterSupplier);
    }

    @Override
    public final void setFallbackDatacenterSupplier(final Supplier<Datacenter> fallbackDatacenterSupplier) {
        this.fallbackDatacenterSupplier = requireNonNull(fallbackDatacenterSupplier);
    }

    @Override
    public Function<Cloudlet, Vm> getDefaultVmMapper() {
        return this::defaultVmMapper;
    }

    @Override
    public Function<Cloudlet, Vm> getVmMapper() {
        return vmMapper;
    }

    @Override
    public final void setVmMapper(final Function<Cloudlet, Vm> vmMapper) {
        this.vmMapper = requireNonNull(vmMapper);
    }

    @Override
    public void setVmComparator(final Comparator<Vm> comparator) {
        this.vmComparator = comparator;
    }

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
        return getName();
    }

    @Override
    public Function<Vm, Double> getVmDestructionDelayFunction() {
        return vmDestructionDelayFunction;
    }

    @Override
    public DatacenterBroker setVmDestructionDelay(final double delay) {
        setVmDestructionDelayFunction(vm -> delay);
        return this;
    }

    @Override
    public DatacenterBroker setVmDestructionDelayFunction(final Function<Vm, Double> function) {
        this.vmDestructionDelayFunction = function == null ? DEF_VM_DESTRUCTION_DELAY_FUNCTION : function;
        return this;
    }

    /**
     * Indicates if there are more cloudlets waiting to
     * be executed yet.
     *
     * @return true if there are waiting cloudlets, false otherwise
     */
    protected boolean isThereWaitingCloudlets() {
        return !cloudletWaitingList.isEmpty();
    }

    @Override
    public List<Cloudlet> getCloudletSubmittedList() {
        return cloudletSubmittedList;
    }

    /**
     * Defines the default policy used to select a Vm to host a Cloudlet
     * that is waiting to be created.
     * <br>It applies a Round-Robin policy to cyclically select
     * the next Vm from the list of waiting VMs.
     *
     * @param cloudlet the cloudlet that needs a VM to be placed into
     * @return the selected Vm for the cloudlet or {@link Vm#NULL} if
     * no suitable VM was found
     */
    protected abstract Vm defaultVmMapper(Cloudlet cloudlet);

}
