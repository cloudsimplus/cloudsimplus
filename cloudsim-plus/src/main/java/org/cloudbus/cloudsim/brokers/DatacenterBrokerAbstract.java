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
     * @see #getVmWaitingList()
     */
    private final List<Vm> vmWaitingList;

    /**
     * A map of requests for VM creation sent to Datacenters.
     * The key is a VM and the value is a Datacenter to where
     * a request to create that VM was sent.
     * If the value is null or the VM isn't in the map,
     * it wasn't requested to be created yet.
     */
    private final Map<Vm, Datacenter> vmCreationRequestsMap;

    /**
     * @see #getVmExecList()
     */
    private final List<Vm> vmExecList;

    /**
     * @see #getVmCreatedList()
     */
    private final List<Vm> vmCreatedList;

    /**
     * @see #getCloudletWaitingList()
     */
    private final List<Cloudlet> cloudletWaitingList;

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
     * @see #getCloudletFinishedList()
     */
    private final List<Cloudlet> cloudletsFinishedList;

    /**
     * @see #getCloudletCreatedList()
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

    private final Map<Vm, Datacenter> vmsToDatacentersMap;
    private Cloudlet lastSubmittedCloudlet;
    private Vm lastSubmittedVm;

    /**
     * @see #getVmDestructionDelayFunction()
     */
    private Function<Vm, Double> vmDestructionDelayFunction;

    /**
     * Creates a DatacenterBroker object.
     *
     * @param simulation the CloudSim instance that represents the simulation the Entity is related to
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

        this.vmWaitingList = new ArrayList<>();
        this.vmExecList = new ArrayList<>();
        this.vmCreatedList = new ArrayList<>();
        this.cloudletWaitingList = new ArrayList<>();
        this.cloudletsFinishedList = new ArrayList<>();

        setDatacenterList(new TreeSet<>());
        datacenterRequestedList = new TreeSet<>();
        vmCreationRequestsMap = new HashMap<>();
        cloudletCreationRequestsMap = new HashMap<>();
        vmsToDatacentersMap = new HashMap<>();

        setDefaultPolicies();

        vmDestructionDelayFunction = DEFAULT_VM_DESTRUCTION_DELAY_FUNCTION;
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
        vmWaitingList.addAll(list);

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
     * Defines IDs for a list of {@link CustomerEntity} entities that don't
     * have one already assigned. Such entities can be a {@link Cloudlet},
     * {@link Vm} or any object that implements {@link CustomerEntity}.
     *
     * @param list list of objects to define an ID
     * @param lastSubmittedEntity the last Entity that was submitted to the broker
     * @return the last Entity in the given List of the lastSubmittedEntity if the List is empty
     */
    private <T extends CustomerEntity> T setIdForEntitiesWithoutOne(List<? extends T> list, T lastSubmittedEntity){
        return Simulation.setIdForEntitiesWithoutOne(list, lastSubmittedEntity) ? list.get(list.size()-1) : lastSubmittedEntity;
    }

    private void sortVmsIfComparatorIsSet(List<? extends Vm> list) {
        if(!Objects.isNull(vmComparator)) {
            list.sort(vmComparator);
        }
    }

    @Override
    public void submitVm(Vm vm) {
        if(vm == null || vm == Vm.NULL){
            return;
        }
        final List<Vm> newList = new ArrayList<>(1);
        newList.add(vm);
        submitVmList(newList);
    }

    @Override
    public void submitCloudlet(Cloudlet cloudlet) {
        if(cloudlet == null || cloudlet == Cloudlet.NULL){
            return;
        }

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
        cloudletWaitingList.addAll(list);

        if (!isStarted()) {
            return;
        }

        println(String.format(
            "%.2f: %s: List of %d Cloudlets submitted to the broker during simulation execution.",
            getSimulation().clock(), getName(), list.size()));

        //If there aren't more VMs to be created, then request Cloudlets creation
        if(getVmWaitingList().isEmpty()){
            println(" Cloudlets creation request sent to Datacenter.");
            requestDatacentersToCreateWaitingCloudlets();
            notifyOnCreationOfWaitingVmsFinishListeners();
        } else
            println(String.format(
                    " Waiting creation of %d VMs to send Cloudlets creation request to Datacenter.",
                    vmWaitingList.size()));
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
     * Sets the delay for a list of {@link CustomerEntity} entities that don't
     * have a delay already assigned. Such entities can be a {@link Cloudlet},
     * {@link Vm} or any object that implements {@link CustomerEntity}.
     *
     * <p>If the delay is defined as a negative number, objects' delay
     * won't be changed.</p>
     *
     * @param list            list of objects to set their delays
     * @param submissionDelay the submission delay to set
     */
    private void setDelayForEntitiesWithNoDelay(List<? extends CustomerEntity> list, double submissionDelay) {
        if(submissionDelay < 0){
            return;
        }

        list.stream()
            .filter(e -> e.getSubmissionDelay() <= 0)
            .forEach(e -> e.setSubmissionDelay(submissionDelay));
    }

    @Override
    public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm) {
        if (!getCloudletWaitingList().contains(cloudlet)) {
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
            case CloudSimTags.VM_DESTROY:
                processBrokerVmDestroyRequest((Vm)ev.getData());
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
                break;
        }
    }

    /**
     * Process the request sent by the broker itself, to check
     * if it's time to destroy a given VM.
     *
     * <p>If the VM is idle yet (since the request
     * is sent only after the delay defined by the {@link #getVmDestructionDelayFunction()}),
     * the VM destruction has to be actually requested to the Datacenter
     * without any further delay.
     * This way, it's given a {@link Function} to the {@link #destroyVmAndRemoveFromList(Vm, Function)}
     * method which always returns a delay equals to 0 for any given VM.
     * Since the {@link Function} always returns the same value,
     * its Vm parameter is ignored (it's defined as __ ).
     * </p>
     *
     * @param vm the VM to try to destroy
     */
    private void processBrokerVmDestroyRequest(Vm vm) {
        if(vm.getCloudletScheduler().isEmpty()) {
            destroyVmAndRemoveFromList(vm, __ -> 0.0);
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
     * Process a request to get the list of all Datacenters registered in the
     * Cloud Information Service (CIS) of the {@link #getSimulation() simulation}.
     *
     * @param ev a CloudSimEvent object
     * @pre ev != $null
     * @post $none
     */
    protected void processDatacenterListRequest(SimEvent ev) {
        setDatacenterList((Set<Datacenter>) ev.getData());
        println(String.format(
            "\n%.2f: %s: List of Datacenters received with %d datacenters(s).",
            getSimulation().clock(), getName(), datacenterList.size()));
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
        if (getVmWaitingList().isEmpty()) {
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
        if (vmExecList.isEmpty()) {
            println(String.format("%.2f: %s: %s", getSimulation().clock(), getName(),
                "none of the required VMs could be created. Aborting"));
            requestShutDown();
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
        for (final Vm vm : vmWaitingList) {
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
        vmsToDatacentersMap.put(vm, datacenter);
        vmWaitingList.remove(vm);
        vmExecList.add(vm);
        vmCreatedList.add(vm);
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
     * @param ev the cloudlet that has just finished to execute and was returned to the broker
     * @pre ev != $null
     * @post $none
     */
    protected void processCloudletReturn(SimEvent ev) {
        final Cloudlet c = (Cloudlet) ev.getData();
        cloudletsFinishedList.add(c);
        println(String.format("%.2f: %s: %s %d finished and returned to broker.",
            getSimulation().clock(), getName(), c.getClass().getSimpleName(), c.getId()));
        cloudletsCreated--;

        if(isNotAllRunningCloudletsReturned()){
            destroyVmAndRemoveFromList(c.getVm(), vmDestructionDelayFunction);
            return;
        }

        final Function<Vm, Double> func = vmDestructionDelayFunction.apply(c.getVm()) < 0 ? vm -> 0.0 : vmDestructionDelayFunction;
        //If gets here, all running cloudlets have finished and returned to the broker.
        if (cloudletWaitingList.isEmpty()) {
            println(String.format(
                "%.2f: %s: All submitted Cloudlets finished executing. Destroying VMs and requesting broker shutdown...",
                getSimulation().clock(), getName()));
            destroyVms(func);
            requestShutDown();
            return;
        }

        /*There are some cloudlets waiting their VMs to be created.
        Then, destroys finished VMs and requests creation of waiting ones.
        When there is waiting Cloudlets, it always request the destruction
        of idle VMs to possibly free resources to start waiting
        VMs. This way, the a VM destruction delay function is not set,
        defines one which always return 0 to indicate
        that in this situation, idle VMs must be destroyed immediately.
        */
        destroyVms(func);
        requestDatacenterToCreateWaitingVms();
    }

    /**
     * Checks if <b>NOT</b> all created Cloudlets have returned to the broker,
     * indicating some of them are executing yet.
     * @return
     */
    private boolean isNotAllRunningCloudletsReturned() {
        return cloudletsCreated > 0;
    }

    /**
     * Try to destroy all created broker's VMs at the time defined by a delay {@link Function}.
     *
     * @param vmDestructionDelayFunction a {@link Function} which indicates to time the VM will wait before being destructed
     * @pre $none
     * @post $none
     * @see #getVmDestructionDelayFunction()
     */
    protected void destroyVms(Function<Vm,Double> vmDestructionDelayFunction) {
        List<Vm> remove = new ArrayList<>();
        for (final Vm vm : vmExecList) {
            if(destroyVm(vm, vmDestructionDelayFunction)){
                remove.add(vm);
            }
        }

        vmExecList.removeAll(remove);
    }

    /**
     * Try to destroy a specific VM at the time defined by a delay {@link Function} and then
     * removes it from the list of created VMs.
     * The VM will be destroyed if the given delay function doesn't return a negative value.
     * In this case, it means it's not time to destroy the VM.
     *
     * @param vm the VM to destroy
     * @param vmDestructionDelayFunction a {@link Function} which indicates to time the VM will wait before being destructed
     * @return true if the VM was destroyed, false otherwise
     * @see #getVmDestructionDelayFunction()
     */
    private boolean destroyVmAndRemoveFromList(Vm vm, Function<Vm,Double> vmDestructionDelayFunction) {
        if(destroyVm(vm, vmDestructionDelayFunction)) {
            vmExecList.remove(vm);
            return true;
        }

        return false;
    }

    /**
     * Try to destroy a specific VM at the time defined by a delay {@link Function} and
     * keeps it into the list of created VMs.
     * The VM will be destroyed if the given delay function doesn't return a negative value.
     * In this case, it means it's not time to destroy the VM.
     *
     * @param vm the VM to destroy
     * @param vmDestructionDelayFunction a {@link Function} which indicates to time the VM will wait before being destructed
     * @return true if the VM was destroyed, false otherwise
     * @see #getVmDestructionDelayFunction()
     */
    private boolean destroyVm(Vm vm, Function<Vm,Double>vmDestructionDelayFunction) {
        final double delay = vmDestructionDelayFunction.apply(vm);
        if (delay < 0){
            return false;
        }

        if(vm.getIdleInterval() >= delay)  {
            println(String.format("%.2f: %s: Destroying %s", getSimulation().clock(), getName(), vm));
            //request the Datacenter to destroy the VM
            sendNow(getVmDatacenter(vm).getId(), CloudSimTags.VM_DESTROY, vm);
            return true;
        }

        /*
        Makes another request to the broker itself to check if the VM should be destroyed.
        The request will be processed only after the time specified by the delay has passed.
        */
        send(getId(), delay, CloudSimTags.VM_DESTROY, vm);
        return false;
    }

    /**
     * Request the creation of VMs in the
     * {@link #getVmWaitingList() VM waiting list}
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
     * {@link #getVmWaitingList() VM waiting list}.
     *
     * @param datacenter id of the Datacenter to request the VMs creation
     * @pre $none
     * @post $none
     * @see #submitVmList(java.util.List)
     */
    protected void requestDatacenterToCreateWaitingVms(Datacenter datacenter) {
        int requestedVms = 0;
        for (final Vm vm : getVmWaitingList()) {
            if (!vmsToDatacentersMap.containsKey(vm) && !vmCreationRequestsMap.containsKey(vm)) {
                println(String.format(
                    "%.2f: %s: Trying to Create %s in %s",
                    getSimulation().clock(), getName(), vm, datacenter.getName()));
                sendNow(datacenter.getId(), CloudSimTags.VM_CREATE_ACK, vm);
                vmCreationRequestsMap.put(vm, datacenter);
                requestedVms++;
            }
        }
        datacenterRequestedList.add(datacenter);
        this.vmCreationRequests += requestedVms;
    }

    /**
     * <p>Request Datacenters to create the Cloudlets in the
     * {@link #getCloudletWaitingList() Cloudlets waiting list}.
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
        for (final Cloudlet cloudlet : getCloudletWaitingList()) {
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
        cloudletWaitingList.removeAll(successfullySubmitted);
    }

    /**
     * Send an internal event to the broker itself, communicating there is not more
     * events to process (no more VMs to create or Cloudlets to execute).
     *
     * @pre $none
     * @post $none
     */
    protected void requestShutDown() {
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
    public Set<Cloudlet> getCloudletCreatedList() {
        return cloudletCreationRequestsMap.keySet();
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
    protected Vm getVmFromCreatedList(int vmIndex) {
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

    @Override
    public Function<Vm, Double> getVmDestructionDelayFunction() {
        return vmDestructionDelayFunction;
    }

    @Override
    public DatacenterBroker setVmDestructionDelayFunction(final Function<Vm, Double> function) {
        this.vmDestructionDelayFunction = function == null ? DEFAULT_VM_DESTRUCTION_DELAY_FUNCTION : function;
        return this;
    }

    @Override
    public boolean isThereWaitingCloudlets() {
        return !cloudletWaitingList.isEmpty();
    }
}
