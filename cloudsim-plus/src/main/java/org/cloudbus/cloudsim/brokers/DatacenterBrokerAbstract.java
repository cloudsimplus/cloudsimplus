/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import java.util.*;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.*;
import org.cloudsimplus.listeners.DatacenterToVmEventInfo;
import org.cloudsimplus.listeners.VmToCloudletEventInfo;

/**
 * An abstract class to be used as base for implementing a {@link DatacenterBroker}.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
public abstract class DatacenterBrokerAbstract extends CloudSimEntity implements DatacenterBroker {
    /**
     * @see #getLastSelectedVm()
     */
    private Vm lastSelectedVm = Vm.NULL;

    /**
     * @see #getVmsWaitingList()
     */
    private List<Vm> vmsWaitingList;
    /**
     * @see #getVmsCreatedList()
     */
    private List<Vm> vmsCreatedList;
    /**
     * @see #getCloudletsWaitingList()
     */
    private List<Cloudlet> cloudletsWaitingList;
    /**
     * @see #getCloudletsFinishedList()
     */
    private List<Cloudlet> cloudletsFinishedList;

	/**
	 * @see #getCloudletsCreated()
	 */
    private int cloudletsCreated;
    /**
     * @see #getVmCreationRequests()
     */
    private int vmCreationRequests;
    /**
     *@see #getVmCreationAcks()
     */
    private int vmCreationAcks;
    /**
     * @see #getDatacenterList()
     */
    private List<Datacenter> datacenterList;
    /**
     * @see #getDatacenterRequestedList()
     */
    private Set<Datacenter> datacenterRequestedList;
    /**
     * @see #getVmsToDatacentersMap()
     */
    private Map<Vm, Datacenter> vmsToDatacentersMap;

    /**
     * Creates a new DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @post $none
     */
    public DatacenterBrokerAbstract(CloudSim simulation) {
        super(simulation);
        this.getSimulation().incrementNumberOfBrokers();

        this.vmsWaitingList = new ArrayList<>();
        this.vmsCreatedList = new ArrayList<>();
        this.cloudletsWaitingList = new ArrayList<>();
        this.cloudletsFinishedList = new ArrayList<>();

        cloudletsCreated = 0;
        vmCreationRequests = 0;
        vmCreationAcks = 0;

        setDatacenterList(new TreeSet<>());
        datacenterRequestedList = new TreeSet<>();
        this.vmsToDatacentersMap = new HashMap<>();
    }

    @Override
    public void submitVmList(List<? extends Vm> list, double submissionDelay) {
        setDelayForSubmittedEntities(list, submissionDelay);
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
     * @param {@inheritDoc}
     */
    @Override
    public void submitVmList(List<? extends Vm> list) {
        setSimulationInstanceForSubmittedVms(list);
        getVmsWaitingList().addAll(list);

        if(isStarted()){
            Log.printConcatLine(getSimulation().clock(), ": ", getName(),
                ": List of ", list.size(),
                " VMs submitted to the broker during simulation execution. VMs creation request sent to Datacenter.");
            requestDatacenterToCreateWaitingVms();
        }
    }

    private void setSimulationInstanceForSubmittedVms(List<? extends Vm> list) {
        for(Vm vm: list){
            vm.setSimulation(this.getSimulation());
        }
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
     * @param {@inheritDoc}
     * @see #submitCloudletList(List, double)
     */
    @Override
    public void submitCloudletList(List<? extends Cloudlet> list) {
        setSimulationInstanceForSubmittedCloudlets(list);
        getCloudletsWaitingList().addAll(list);
        Log.printConcat(getSimulation().clock(), ": ", getName(),
            ": List of ", list.size(), " Cloudlets submitted to the broker during simulation execution.");
        if(isStarted() && getVmsWaitingList().isEmpty()){
            Log.printConcat(" Cloudlets creation request sent to Datacenter.");
            requestDatacentersToCreateWaitingCloudlets();
        } else Log.printConcat(" Waiting VMs creation to send Cloudlets creation request to Datacenter.");
        Log.printLine();
    }

    private void setSimulationInstanceForSubmittedCloudlets(List<? extends Cloudlet> list) {
        for(Cloudlet cloudlet: list){
            cloudlet.setSimulation(this.getSimulation());
        }
    }

    @Override
    public void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay) {
        setDelayForSubmittedEntities(list, submissionDelay);
        submitCloudletList(list);
    }

    /**
     * Sets the delay for a list of {@link Delayable} entities, that can be a {@link Cloudlet},
     * {@link Vm} or any object that implements {@link Delayable}.
     *
     * @param list list of objects to set their delays
     * @param submissionDelay the submission delay to set
     */
    private void setDelayForSubmittedEntities(List<? extends Delayable> list, double submissionDelay) {
        list.forEach(entity -> entity.setSubmissionDelay(submissionDelay));
    }

    @Override
    public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm) {
        if(!getCloudletsWaitingList().contains(cloudlet)){
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
            // VM Creation response
            case CloudSimTags.VM_CREATE_ACK:
                processVmCreateResponseFromDatacenter(ev);
            break;
            // A finished cloudlet returned
            case CloudSimTags.CLOUDLET_RETURN:
                processCloudletReturn(ev);
            break;
            // if the simulation finishes
            case CloudSimTags.END_OF_SIMULATION:
                shutdownEntity();
            break;
            // other unknown tags are processed by this method
            default:
                processOtherEvent(ev);
            break;
        }
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
        setDatacenterList((Set<Datacenter>)ev.getData());
        Log.printConcatLine(getSimulation().clock(), ": ", getName(),
            ": List of Cloud Datacenter received with ", getDatacenterList().size(), " datacenters(s)");
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
        Vm vm = (Vm) ev.getData();
        boolean vmCreated = false;
        vmCreationAcks++;

        //if the VM was sucessfully created in the requested Datacenter
        if (vm.isCreated()) {
            processSuccessVmCreationInDatacenter(vm, vm.getHost().getDatacenter());
            vmCreated = true;
        } else {
            processFailedVmCreationInDatacenter(vm, vm.getHost().getDatacenter());
        }

        // all the requested VMs have been created
        if (getVmsWaitingList().isEmpty()) {
            requestDatacentersToCreateWaitingCloudlets();
        } else if (getVmCreationRequests() == getVmCreationAcks()) {
            requestCreationOfWaitingVmsToNextDatacenter();
        }

        return vmCreated;
    }

    /**
     * After the response (ack) of all VM creation request were received
     * but not all VMs could be created (what means some
     * acks informed about Vm creation failures), try to find
     * another Datacenter to request the creation of the VMs
     * in the waiting list.
     */
    protected void requestCreationOfWaitingVmsToNextDatacenter() {
        final Datacenter nextDatacenter = selectFallbackDatacenterForWaitingVms();
        if (nextDatacenter != Datacenter.NULL) {
            requestDatacenterToCreateWaitingVms(nextDatacenter);
            return;
        }

        /*If it gets here, it means that all datacenters were already queried
        * and not all VMs could be created, but some of them could.*/
        if (!getVmsCreatedList().isEmpty()) {
            requestDatacentersToCreateWaitingCloudlets();
        } else {
            Log.printFormattedLine("%.2f: %s: %s", getSimulation().clock(), getName(), "none of the required VMs could be created. Aborting");
            finishExecution();
        }
    }

    /**
     * Process a response from a Datacenter informing that it was able to
     * create the VM requested by the broker.
     *
     * @param vm id of the Vm that succeeded to be created inside the Datacenter
     * @param datacenter id of the Datacenter where the request to create
     * the Vm succeeded
     */
    protected void processSuccessVmCreationInDatacenter(Vm vm, Datacenter datacenter) {
        getVmsToDatacentersMap().put(vm, datacenter);
        getVmsWaitingList().remove(vm);
        getVmsCreatedList().add(vm);
        Log.printConcatLine(
            getSimulation().clock(), ": ", getName(),
            ": VM #", vm, " has been created in Datacenter #", datacenter, ", Host #",
            vm.getHost().getId());
    }

    /**
     * Process a response from a Datacenter informing that it was NOT able to
     * create the VM requested by the broker.
     *
     * @param vm id of the Vm that failed to be created inside the Datacenter
     * @param datacenter id of the Datacenter where the request to create
     */
    protected void processFailedVmCreationInDatacenter(Vm vm, Datacenter datacenter) {
        DatacenterToVmEventInfo info =
                new DatacenterToVmEventInfo(getSimulation().clock(), datacenter, vm);
        vm.getOnVmCreationFailureListener().update(info);
        Log.printConcatLine(getSimulation().clock(), ": ", getName(),
            ": Creation of VM #", vm, " failed in Datacenter #", datacenter);
    }

    /**
     * Processes the end of execution of a given cloudlet inside a Vm.
     *
     * @param ev The cloudlet that has just finished to execute
     * @pre ev != $null
     * @post $none
     */
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletsFinishedList().add(cloudlet);
        notifyCloudletFinishListener(cloudlet);
        Log.printFormattedLine("%.1f: %s: %s %d received", getSimulation().clock(), getName(), cloudlet.getClass().getSimpleName(), cloudlet.getId());
        cloudletsCreated--;
        if (getCloudletsWaitingList().isEmpty() && cloudletsCreated == 0) {
            // all cloudlets executed
            Log.printConcatLine(getSimulation().clock(), ": ", getName(), ": All Cloudlets executed. Finishing...");
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
        return getCloudletsWaitingList().size() > 0 && cloudletsCreated == 0;
    }

    protected void notifyCloudletFinishListener(Cloudlet cloudlet) {
        VmToCloudletEventInfo info =
                new VmToCloudletEventInfo(getSimulation().clock(), cloudlet.getVm(), cloudlet);
        cloudlet.getOnCloudletFinishEventListener().update(info);
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
            Log.printConcatLine(getName(), ".processOtherEvent(): ", "Error - an event is null.");
            return;
        }
        Log.printConcatLine(getName(), ".processOtherEvent(): Error - event unknown by this DatacenterBroker.");
    }

    /**
     * Request the {@link #selectDatacenterForWaitingVms() next Datacenter in the list} to create the VM in the
     * {@link #getVmsWaitingList() VM waiting list}.
     *
     * @pre $none
     * @post $none
     * @see #submitVmList(java.util.List)
     */
    protected void requestDatacenterToCreateWaitingVms() {
        requestDatacenterToCreateWaitingVms(selectDatacenterForWaitingVms());
    }

    /**
     * Request a Datacenter to create the VM in the
     * {@link #getVmsWaitingList() VM waiting list}.
     *
     * @param datacenter id of the Datacenter to request the VMs creation
     * @pre $none
     * @post $none
     * @see #submitVmList(java.util.List)
     */
    protected void requestDatacenterToCreateWaitingVms(Datacenter datacenter) {
        int requestedVms = 0;
        for (Vm vm : getVmsWaitingList()) {
            if (!getVmsToDatacentersMap().containsKey(vm)) {
                Log.printLine(getSimulation().clock() +
                    ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in " + datacenter.getName());
                sendNow(datacenter.getId(), CloudSimTags.VM_CREATE_ACK, vm);
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
     *
     * <p>This method is called after all submitted VMs are created
     * in some Datacenter.</p>
     *
     * @pre $none
     * @post $none
     * @see #submitCloudletList(java.util.List)
     * @todo @author manoelcampos Where is checked if the Vm to where
     * a cloudlet was submitted has the required resources?
     */
    protected void requestDatacentersToCreateWaitingCloudlets() {
        List<Cloudlet> successfullySubmitted = new ArrayList<>();
        for (Cloudlet cloudlet : getCloudletsWaitingList()) {
            lastSelectedVm = selectVmForWaitingCloudlet(cloudlet);
            if (getLastSelectedVm() == Vm.NULL) {
                // vm was not created
                Log.printConcatLine(getSimulation().clock(), ": ", getName(), ": Postponing execution of cloudlet ", cloudlet.getId(), ": bounded VM not available");
                continue;
            }
            Log.printFormattedLine("%.2f: %s: Sending %s %d to VM #%d", getSimulation().clock(), getName(), cloudlet.getClass().getSimpleName(), cloudlet.getId(), getLastSelectedVm().getId());
            cloudlet.setVm(lastSelectedVm);
            send(getVmDatacenter(getLastSelectedVm()).getId(),
                    cloudlet.getSubmissionDelay(), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            cloudletsCreated++;
            successfullySubmitted.add(cloudlet);
        }
        // remove created cloudlets from waiting list
        getCloudletsWaitingList().removeAll(successfullySubmitted);
        /*sets the last selected VM to null so that the next
        time cloudlets are requested to be created, the VM selection will
        restarting from the first VM.*/
        lastSelectedVm = Vm.NULL;
    }

    /**
     * Destroy all created broker's VMs.
     *
     * @pre $none
     * @post $none
     */
    protected void destroyVms() {
        for (Vm vm : getVmsCreatedList()) {
            Log.printConcatLine(getSimulation().clock(), ": " + getName(), ": Destroying VM #", vm.getId());
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
        Log.printConcatLine(getName(), " is shutting down...");
    }

    @Override
    public void startEntity() {
        Log.printConcatLine(getName(), " is starting...");
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
        return (List<T>) cloudletsFinishedList;
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
     * @return the datacenter list
     */
    protected List<Datacenter> getDatacenterList() {
        return datacenterList;
    }

    /**
     * Sets the list of available datacenters.
     *
     * @param datacenterList the new datacenter list
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
     *
     * @return latest VM selected to run a cloudlet.
     */
    protected Vm getLastSelectedVm() {
        return lastSelectedVm;
    }

	/**
	 * Gets the total number of cloudlets created inside some Vm.
	 */
	protected int getCloudletsCreated() {
		return cloudletsCreated;
	}

}
