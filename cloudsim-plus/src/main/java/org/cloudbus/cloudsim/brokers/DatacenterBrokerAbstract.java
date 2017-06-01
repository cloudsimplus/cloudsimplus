/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.*;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.listeners.*;
import org.cloudsimplus.listeners.EventListener;

/**
 * An abstract class to be used as base for implementing a {@link DatacenterBroker}.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
public abstract class DatacenterBrokerAbstract extends CloudSimEntity implements DatacenterBroker {
    /**
     * A map of registered event listeners for the onCreationOfWaitingVmsFinish event
     * that the key is the Listener itself and the value indicates if it's a one
     * time listener (which is removed from the list after being notified for the first time).
     *
     * @see #addOnVmsCreatedListener(EventListener)
     * @see #addOneTimeOnVmsCreatedListener(EventListener)
     */
    private Map<EventListener<DatacenterBrokerEventInfo>, Boolean> onVmsCreatedListeners;

    /**
     * @see #getLastSelectedVm()
     */
    private Vm lastSelectedVm;

    /**
     * The last datacenter where a VM was created or tried to be created.
     */
    private Datacenter lastSelectedDc;

    /**
     * @see #getVmsWaitingList()
     */
    private final List<Vm> vmsWaitingList;

    /**
     * A map of requests for VM creation sent to Datacenters.
     * The key is a VM and the value is a Datacenter to where
     * a request to create that VM was sent.
     * If the value is null or the VM isn't in the map,
     * it wasn't requested to be created yet.
     */
    private final Map<Vm, Datacenter> vmCreationRequestsMap;

    /**
     * @see #getVmsCreatedList()
     */
    private final List<Vm> vmsCreatedList;
    /**
     * @see #getCloudletsWaitingList()
     */
    private final List<Cloudlet> cloudletsWaitingList;

    /**
     * A map of requests for Cloudlet creation sent to Datacenters.
     * The key is a Cloudlet and the value is a Datacenter to where
     * a request to create that Cloudlet was sent.
     * If the value is null or the Cloudlet isn't in the map,
     * it wasn't requested to be created yet.
     */
    private final Map<Cloudlet, Datacenter> cloudletCreationRequestsMap;
    private Supplier<Datacenter> datacenterSupplier;
    private Supplier<Datacenter> fallbackDatacenterSupplier;
    private Function<Cloudlet, Vm> vmMapper;

    private Comparator<Vm> vmComparator;
    private Comparator<Cloudlet> cloudletComparator;

    /**
     * @see #getCloudletsFinishedList()
     */
    private final List<Cloudlet> cloudletsFinishedList;

    /**
     * @see #getCloudletsCreatedList()
     */
    private int cloudletsCreated;
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
    /**
     * @see #getVmsToDatacentersMap()
     */
    private final Map<Vm, Datacenter> vmsToDatacentersMap;
    private Cloudlet lastSubmittedCloudlet;
    private Vm lastSubmittedVm;

    /**
     * Creates a new DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @post $none
     */
    public DatacenterBrokerAbstract(CloudSim simulation) {
        super(simulation);
        this.onVmsCreatedListeners = new HashMap<>();
        this.lastSubmittedCloudlet = Cloudlet.NULL;
        this.lastSubmittedVm = Vm.NULL;
        this.lastSelectedVm = Vm.NULL;
        this.lastSelectedDc = Datacenter.NULL;

        cloudletsCreated = 0;
        vmCreationRequests = 0;
        vmCreationAcks = 0;

        this.vmsWaitingList = new ArrayList<>();
        this.vmsCreatedList = new ArrayList<>();
        this.cloudletsWaitingList = new ArrayList<>();
        this.cloudletsFinishedList = new ArrayList<>();

        setDatacenterList(new TreeSet<>());
        datacenterRequestedList = new TreeSet<>();
        vmCreationRequestsMap = new HashMap<>();
        cloudletCreationRequestsMap = new HashMap<>();
        vmsToDatacentersMap = new HashMap<>();

        setDefaultPolicies();
    }

    /**
     * Sets the default dummy policies for {@link #datacenterSupplier},
     * {@link #fallbackDatacenterSupplier} and
     * {@link #vmMapper}. The actual policies must be set by
     * concrete DatacenterBroker classes.
     */
    private void setDefaultPolicies() {
        datacenterSupplier = () -> Datacenter.NULL;
        fallbackDatacenterSupplier = datacenterSupplier;
        vmMapper = (cloudlet) -> Vm.NULL;
    }

    @Override
    public void submitVmList(List<? extends Vm> list, double submissionDelay) {
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
    public void submitVmList(List<? extends Vm> list) {
        sortVmsIfComparatorIsSet(list);
        setBrokerForEntities(list);
        lastSubmittedVm = setIdForEntitiesWithoutOne(list, lastSubmittedVm);
        vmsWaitingList.addAll(list);

        if (isStarted() && !list.isEmpty()) {
            println(String.format(
                "%.2f: %s: List of %d VMs submitted to the broker during simulation execution.\n\t VMs creation request sent to Datacenter.",
                getSimulation().clock(), getName(), list.size()));
            requestDatacenterToCreateWaitingVms();
        }
    }

    /**
     * Sets the broker for each {@link CustomerEntity} into a given list.
     *
     * @param customerEntities the List of {@link CustomerEntity} to set the broker.
     */
    private void setBrokerForEntities(List<? extends CustomerEntity> customerEntities) {
        customerEntities.forEach(e -> e.setBroker(this));
    }

    /**
     * Defines IDs for a list of {@link ChangeableId} entities that don't
     * have one already assigned. Such entities can be a {@link Cloudlet},
     * {@link Vm} or any object that implements {@link ChangeableId}.
     *
     * @param list list of objects to define an ID
     * @param lastSubmittedEntity the last Entity that was submitted to the broker
     * @return the last Entity in the given List of the lastSubmittedEntity if the List is empty
     */
    private <T extends ChangeableId> T setIdForEntitiesWithoutOne(List<? extends T> list, T lastSubmittedEntity){
        return Simulation.setIdForEntitiesWithoutOne(list, lastSubmittedEntity) ? list.get(list.size()-1) : lastSubmittedEntity;
    }

    private void sortVmsIfComparatorIsSet(List<? extends Vm> list) {
        if(!Objects.isNull(vmComparator)) {
            list.sort(vmComparator);
        }
    }

    @Override
    public void submitVm(Vm vm) {
        final List<Vm> newList = new ArrayList<>(1);
        newList.add(vm);
        submitVmList(newList);
    }

    @Override
    public void submitCloudlet(Cloudlet cloudlet) {
        final List<Cloudlet> newList = new ArrayList<>(1);
        newList.add(cloudlet);
        submitCloudletList(newList);
    }

    @Override
    public void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay) {
        submitCloudletList(list, Vm.NULL, submissionDelay);
    }

    @Override
    public void submitCloudletList(List<? extends Cloudlet> list, Vm vm) {
        submitCloudletList(list, vm, -1);
    }

    @Override
    public void submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay) {
        setDelayForEntitiesWithNoDelay(list, submissionDelay);
        bindCloudletsToVm(list, vm);
        submitCloudletList(list);
    }

    /**
     * Binds a list of Cloudlets to a given {@link Vm}.
     * If the {@link Vm} is {@link Vm#NULL}, the Cloudlets will not be bound.
     *
     * @param cloudlets the List of Cloudlets to be bound to a VM
     * @param vm the VM to bind the Cloudlets to
     */
    private void bindCloudletsToVm(List<? extends Cloudlet> cloudlets, Vm vm) {
        if(Vm.NULL.equals(vm)){
            return;
        }

        cloudlets.forEach(c -> c.setVm(vm));
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
     * @param list {@inheritDoc}
     * @see #submitCloudletList(List, double)
     */
    @Override
    public void submitCloudletList(List<? extends Cloudlet> list) {
        sortCloudletsIfComparatorIsSet(list);
        setBrokerForEntities(list);
        lastSubmittedCloudlet =  setIdForEntitiesWithoutOne(list, lastSubmittedCloudlet);
        if(list.isEmpty()) {
            return;
        }
        setSimulationForCloudletUtilizationModels(list);
        getCloudletsWaitingList().addAll(list);

        if (!isStarted()) {
            return;
        }

        println(String.format(
            "%.2f: %s: List of %d Cloudlets submitted to the broker during simulation execution.",
            getSimulation().clock(), getName(), list.size()));

        //If there aren't more VMs to be created, then request Cloudlets creation
        if(getVmsWaitingList().isEmpty()){
            println(" Cloudlets creation request sent to Datacenter.");
            requestDatacentersToCreateWaitingCloudlets();
            notifyOnCreationOfWaitingVmsFinishListeners();
        } else
            println(String.format(
                    " Waiting creation of %d VMs to send Cloudlets creation request to Datacenter.",
                    getVmsWaitingList().size()));
    }

    private void sortCloudletsIfComparatorIsSet(List<? extends Cloudlet> list) {
        if(!Objects.isNull(cloudletComparator)) {
            list.sort(cloudletComparator);
        }
    }

    private void setSimulationForCloudletUtilizationModels(List<? extends Cloudlet> list) {
        for(final Cloudlet c: list){
            setSimulationForUtilizationModelIfNotSet(c.getUtilizationModelCpu());
            setSimulationForUtilizationModelIfNotSet(c.getUtilizationModelBw());
            setSimulationForUtilizationModelIfNotSet(c.getUtilizationModelRam());
        }
    }

    private void setSimulationForUtilizationModelIfNotSet(UtilizationModel cloudletUtilizationModel) {
        if(Objects.isNull(cloudletUtilizationModel.getSimulation()) || cloudletUtilizationModel.getSimulation() == Simulation.NULL){
            cloudletUtilizationModel.setSimulation(getSimulation());
        }
    }

    /**
     * Sets the delay for a list of {@link Delayable} entities that don't
     * have a delay already assigned. Such entities can be a {@link Cloudlet},
     * {@link Vm} or any object that implements {@link Delayable}.
     *
     * <p>If the delay is defined as a negative number, objects' delay
     * won't be changed.</p>
     *
     * @param list            list of objects to set their delays
     * @param submissionDelay the submission delay to set
     */
    private void setDelayForEntitiesWithNoDelay(List<? extends Delayable> list, double submissionDelay) {
        if(submissionDelay < 0){
            return;
        }

        list.stream()
            .filter(e -> e.getSubmissionDelay() <= 0)
            .forEach(e -> e.setSubmissionDelay(submissionDelay));
    }

    @Override
    public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm) {
        if (!getCloudletsWaitingList().contains(cloudlet)) {
            return false;
        }

        cloudlet.setVm(vm);
        return true;
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.DATACENTER_LIST_REQUEST:
                processDatacenterListRequest(ev);
                break;
            case CloudSimTags.VM_CREATE_ACK:
                processVmCreateResponseFromDatacenter(ev);
                break;
            case CloudSimTags.VM_VERTICAL_SCALING:
                requestVmVerticalScaling(ev);
                break;
            case CloudSimTags.CLOUDLET_RETURN:
                processCloudletReturn(ev);
                break;
            case CloudSimTags.END_OF_SIMULATION:
                shutdownEntity();
                break;
            default:
                processOtherEvent(ev);
                break;
        }
    }

    private void requestVmVerticalScaling(SimEvent ev) {
        if (!(ev.getData() instanceof VerticalVmScaling)) {
            return;
        }

        final VerticalVmScaling scaling = (VerticalVmScaling) ev.getData();
        getSimulation().sendNow(
            ev.getSource(), scaling.getVm().getHost().getDatacenter().getId(),
            CloudSimTags.VM_VERTICAL_SCALING, scaling);
    }

    /**
     * Process a request for the list of all Datacenters registered in the
     * Cloud Information Service (CIS) of the {@link #getSimulation() simulation}.
     *
     * @param ev a CloudSimEvent object
     * @pre ev != $null
     * @post $none
     */
    protected void processDatacenterListRequest(SimEvent ev) {
        setDatacenterList((Set<Datacenter>) ev.getData());
        println(String.format(
            "%.2f: %s: List of Datacenters received with %d datacenters(s).",
            getSimulation().clock(), getName(), getDatacenterList().size()));
        requestDatacenterToCreateWaitingVms();
    }

    /**
     * Process the ack received from a Datacenter to a broker's request for
     * creation of a Vm in that Datacenter.
     *
     * @param ev a CloudSimEvent object
     * @return true if the VM was created successfully, false otherwise
     * @pre ev != null
     * @post $none
     */
    protected boolean processVmCreateResponseFromDatacenter(SimEvent ev) {
        final Vm vm = (Vm) ev.getData();
        boolean vmCreated = false;
        vmCreationAcks++;

        //if the VM was successfully created in the requested Datacenter
        if (vm.isCreated()) {
            processSuccessVmCreationInDatacenter(vm, vm.getHost().getDatacenter());
            vmCreated = true;
        } else {
            processFailedVmCreationInDatacenter(vm, lastSelectedDc);
        }

        // all the requested VMs have been created
        if (getVmsWaitingList().isEmpty()) {
            requestDatacentersToCreateWaitingCloudlets();
            notifyOnCreationOfWaitingVmsFinishListeners();
        } else if (getVmCreationRequests() == getVmCreationAcks()) {
            requestCreationOfWaitingVmsToFallbackDatacenter();
        }

        return vmCreated;
    }

    private void notifyOnCreationOfWaitingVmsFinishListeners(){
        onVmsCreatedListeners.entrySet().forEach(entry -> entry.getKey().update(DatacenterBrokerEventInfo.of(this)));
        onVmsCreatedListeners.entrySet().removeIf(this::isOneTimeListener);
    }

    /**
     * Checks if an EventListener from the {@link #onVmsCreatedListeners}
     * is a one-time listener, that after being notified for the first time,
     * must be removed from the map of registered listeners.
     *
     * @param eventListenerBooleanEntry the entry for the listener to check
     * @return true if it is a one-time listener, false otherwise
     */
    private boolean isOneTimeListener(Map.Entry<EventListener<DatacenterBrokerEventInfo>, Boolean> eventListenerBooleanEntry) {
        return eventListenerBooleanEntry.getValue();
    }

    /**
     * After the response (ack) of all VM creation request were received
     * but not all VMs could be created (what means some
     * acks informed about Vm creation failures), try to find
     * another Datacenter to request the creation of the VMs
     * in the waiting list.
     */
    protected void requestCreationOfWaitingVmsToFallbackDatacenter() {
        final Datacenter nextDatacenter = fallbackDatacenterSupplier.get();
        lastSelectedDc = nextDatacenter;
        if (nextDatacenter != Datacenter.NULL) {
            clearVmCreationRequestsMapToTryNextDatacenter();
            requestDatacenterToCreateWaitingVms(nextDatacenter);
            return;
        }

        /* If it gets here, it means that all datacenters were already queried
         * and not all VMs could be created, but some of them could. */
        if (getVmsCreatedList().isEmpty()) {
            println(String.format("%.2f: %s: %s", getSimulation().clock(), getName(),
                "none of the required VMs could be created. Aborting"));
            finishExecution();
            return;
        }

        requestDatacentersToCreateWaitingCloudlets();
    }

    /**
     * After trying to create the waiting VMs at a given Datacenter
     * and not all VMs could be created, removes the VMs yet waiting
     * in order to allow requesting their creation in another datacenter.
     * If a waiting VM is inside the {@link #vmCreationRequestsMap},
     * it means that it was already sent a request to create it.
     * Removing it from such a map, will allow
     * to trying creating the VM at another Datacenter.
     */
    private void clearVmCreationRequestsMapToTryNextDatacenter() {
        for (final Vm vm : vmsWaitingList) {
            vmCreationRequestsMap.remove(vm);
        }
    }

    /**
     * Process a response from a Datacenter informing that it was able to
     * create the VM requested by the broker.
     *
     * @param vm         id of the Vm that succeeded to be created inside the Datacenter
     * @param datacenter id of the Datacenter where the request to create
     *                   the Vm succeeded
     */
    protected void processSuccessVmCreationInDatacenter(Vm vm, Datacenter datacenter) {
        getVmsToDatacentersMap().put(vm, datacenter);
        vmsWaitingList.remove(vm);
        getVmsCreatedList().add(vm);
        println(String.format(
            "%.2f: %s: %s has been created in %s.",
            getSimulation().clock(), getName(), vm, vm.getHost()));
    }

    /**
     * Process a response from a Datacenter informing that it was NOT able to
     * create the VM requested by the broker.
     *
     * @param vm         id of the Vm that failed to be created inside the Datacenter
     * @param datacenter id of the Datacenter where the request to create
     */
    protected void processFailedVmCreationInDatacenter(Vm vm, Datacenter datacenter) {
        vm.notifyOnCreationFailureListeners(datacenter);
        println(String.format(
            "%.2f: %s: Creation of %s failed in Datacenter #%s",
            getSimulation().clock(), getName(), vm, datacenter.getId()));
    }

    /**
     * Processes the end of execution of a given cloudlet inside a Vm.
     *
     * @param ev The cloudlet that has just finished to execute
     * @pre ev != $null
     * @post $none
     */
    protected void processCloudletReturn(SimEvent ev) {
        final Cloudlet cloudlet = (Cloudlet) ev.getData();
        cloudletsFinishedList.add(cloudlet);
        println(String.format("%.2f: %s: %s %d finished and returned to broker.",
            getSimulation().clock(), getName(), cloudlet.getClass().getSimpleName(), cloudlet.getId()));
        cloudletsCreated--;
        if (getCloudletsWaitingList().isEmpty() && cloudletsCreated == 0) {
            // all cloudlets executed
            println(String.format(
                "%.2f: %s: All Cloudlets executed. Finishing...",
                getSimulation().clock(), getName()));
            destroyVms();
            finishExecution();
        } else if (hasMoreCloudletsToBeExecuted()) {
            /* All the cloudlets sent have finished. It means that some bind
            cloudlets are waiting their VMs to be created.*/
            destroyVms();
            requestDatacenterToCreateWaitingVms();
        }
    }

    @Override
    public boolean hasMoreCloudletsToBeExecuted() {
        return !getCloudletsWaitingList().isEmpty() && cloudletsCreated == 0;
    }

    /**
     * Process non-default received events that aren't processed by the {@link #processEvent(SimEvent)} method.
     * This method should be overridden by subclasses if they really want to process new defined
     * events.
     *
     * @param ev a CloudSimEvent object
     * @pre ev != null
     * @post $none
     */
    protected void processOtherEvent(SimEvent ev) {
        if (Objects.isNull(ev)) {
            println(String.format("%s.processOtherEvent(): Error - an event is null.", getName()));
            return;
        }
        println(String.format("%s.processOtherEvent(): Error - event unknown by this DatacenterBroker.", getName()));
    }

    /**
     * Request the creation of VMs in the
     * {@link #getVmsWaitingList() VM waiting list}
     * inside some Datacenter.
     *
     * @pre $none
     * @post $none
     * @see #submitVmList(java.util.List)
     */
    protected void requestDatacenterToCreateWaitingVms() {
        lastSelectedDc = Datacenter.NULL.equals(lastSelectedDc) ? datacenterSupplier.get() : lastSelectedDc;
        requestDatacenterToCreateWaitingVms(lastSelectedDc);
    }

    /**
     * Request a specific Datacenter to create the VM in the
     * {@link #getVmsWaitingList() VM waiting list}.
     *
     * @param datacenter id of the Datacenter to request the VMs creation
     * @pre $none
     * @post $none
     * @see #submitVmList(java.util.List)
     */
    protected void requestDatacenterToCreateWaitingVms(Datacenter datacenter) {
        int requestedVms = 0;
        for (final Vm vm : getVmsWaitingList()) {
            if (!vmsToDatacentersMap.containsKey(vm) && !vmCreationRequestsMap.containsKey(vm)) {
                println(String.format(
                    "%.2f: %s: Trying to Create %s in %s",
                    getSimulation().clock(), getName(), vm, datacenter.getName()));
                sendNow(datacenter.getId(), CloudSimTags.VM_CREATE_ACK, vm);
                vmCreationRequestsMap.put(vm, datacenter);
                requestedVms++;
            }
        }
        getDatacenterRequestedList().add(datacenter);
        this.vmCreationRequests += requestedVms;
    }

    /**
     * <p>Request Datacenters to create the Cloudlets in the
     * {@link #getCloudletsWaitingList() Cloudlets waiting list}.
     * If there aren't available VMs to host all cloudlets,
     * the creation of some ones will be postponed.</p>
     * <p>
     * <p>This method is called after all submitted VMs are created
     * in some Datacenter.</p>
     *
     * @pre $none
     * @post $none
     * @todo @author manoelcampos Where is checked if the Vm to where
     * a cloudlet was submitted has the required resources?
     * @see #submitCloudletList(java.util.List)
     */
    protected void requestDatacentersToCreateWaitingCloudlets() {
        final List<Cloudlet> successfullySubmitted = new ArrayList<>();
        for (final Cloudlet cloudlet : getCloudletsWaitingList()) {
            if (cloudletCreationRequestsMap.containsKey(cloudlet)) {
                continue;
            }

            //selects a VM for the given Cloudlet
            lastSelectedVm = vmMapper.apply(cloudlet);
            if (lastSelectedVm == Vm.NULL) {
                // vm was not created
                println(String.format(
                    "%.2f: %s: : Postponing execution of cloudlet %d: bind VM not available.",
                    getSimulation().clock(), getName(), cloudlet.getId()));
                continue;
            }
            println(String.format(
                "%.2f: %s: Sending %s %d to %s in %s.",
                getSimulation().clock(), getName(), cloudlet.getClass().getSimpleName(), cloudlet.getId(),
                lastSelectedVm, lastSelectedVm.getHost()));
            cloudlet.setVm(lastSelectedVm);
            send(getVmDatacenter(lastSelectedVm).getId(),
                cloudlet.getSubmissionDelay(), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            cloudletCreationRequestsMap.put(cloudlet, getVmDatacenter(lastSelectedVm));
            cloudletsCreated++;
            successfullySubmitted.add(cloudlet);
        }
        // remove created cloudlets from waiting list
        getCloudletsWaitingList().removeAll(successfullySubmitted);
    }

    /**
     * Destroy all created broker's VMs.
     *
     * @pre $none
     * @post $none
     */
    protected void destroyVms() {
        for (final Vm vm : getVmsCreatedList()) {
            println(String.format("%.2f: %s: Destroying %s", getSimulation().clock(), getName(), vm));
            sendNow(getVmDatacenter(vm).getId(), CloudSimTags.VM_DESTROY, vm);
        }
        getVmsCreatedList().clear();
    }

    /**
     * Send an internal event communicating the end of the simulation.
     *
     * @pre $none
     * @post $none
     */
    protected void finishExecution() {
        sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
    }

    @Override
    public void shutdownEntity() {
        println(String.format("%s is shutting down...", getName()));
    }

    @Override
    public void startEntity() {
        println(String.format("%s is starting...", getName()));
        schedule(getSimulation().getCloudInfoServiceEntityId(), 0, CloudSimTags.DATACENTER_LIST_REQUEST);
    }

    @Override
    public <T extends Vm> List<T> getVmsWaitingList() {
        return (List<T>) vmsWaitingList;
    }

    @Override
    public Vm getWaitingVm(final int index) {
        if (index >= 0 && index < vmsWaitingList.size()) {
            return vmsWaitingList.get(index);
        }
        return Vm.NULL;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletsWaitingList() {
        return (List<T>) cloudletsWaitingList;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletsFinishedList() {
        return (List<T>) new ArrayList<>(cloudletsFinishedList);
    }

    @Override
    public <T extends Vm> List<T> getVmsCreatedList() {
        return (List<T>) vmsCreatedList;
    }

    /**
     * Gets a Vm at a given index from the {@link #getVmsCreatedList() list of created VMs}.
     *
     * @param vmIndex the index where a VM has to be got from the created VM list
     * @return the VM at the given index or {@link Vm#NULL} if the index is invalid
     */
    protected Vm getVmFromCreatedList(int vmIndex) {
        return vmIndex >= 0 && vmIndex < vmsCreatedList.size() ? vmsCreatedList.get(vmIndex) : Vm.NULL;
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
     * Gets the VM to Datacenter map, where each key is a VM and each value is
     * the Datacenter where the VM is placed.
     *
     * @return the VM to Datacenter map
     */
    protected Map<Vm, Datacenter> getVmsToDatacentersMap() {
        return vmsToDatacentersMap;
    }

    /**
     * Gets the Datacenter where a VM is placed.
     *
     * @param vm the VM to get its Datacenter
     * @return
     */
    protected Datacenter getVmDatacenter(Vm vm) {
        return vmsToDatacentersMap.get(vm);
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
    public Set<Cloudlet> getCloudletsCreatedList() {
        return cloudletCreationRequestsMap.keySet();
    }

    @Override
    public final void setDatacenterSupplier(Supplier<Datacenter> datacenterSupplier) {
        Objects.requireNonNull(datacenterSupplier);
        this.datacenterSupplier = datacenterSupplier;
    }

    @Override
    public final void setFallbackDatacenterSupplier(Supplier<Datacenter> fallbackDatacenterSupplier) {
        Objects.requireNonNull(fallbackDatacenterSupplier);
        this.fallbackDatacenterSupplier = fallbackDatacenterSupplier;
    }

    @Override
    public final void setVmMapper(Function<Cloudlet, Vm> vmMapper) {
        Objects.requireNonNull(vmMapper);
        this.vmMapper = vmMapper;
    }

    @Override
    public void setVmComparator(Comparator<Vm> comparator) {
        this.vmComparator = comparator;
    }

    @Override
    public void setCloudletComparator(Comparator<Cloudlet> comparator) {
        this.cloudletComparator = comparator;
    }

    @Override
    public DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener) {
        return addOneTimeOnCreationOfWaitingVmsFinishListener(listener, false);
    }

    @Override
    public DatacenterBroker addOneTimeOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener) {
        return addOneTimeOnCreationOfWaitingVmsFinishListener(listener, true);
    }

    public DatacenterBroker addOneTimeOnCreationOfWaitingVmsFinishListener(final EventListener<DatacenterBrokerEventInfo> listener, final Boolean oneTimeListener) {
        Objects.requireNonNull(listener);
        this.onVmsCreatedListeners.put(listener, oneTimeListener);
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }
}
