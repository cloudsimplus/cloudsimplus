/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.listeners.VmToCloudletEventInfo;
import org.cloudbus.cloudsim.listeners.DatacenterToVmEventInfo;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * 
 * <p><b>A simple implementation of {@link DatacenterBroker} that try to host customer's VMs
 * at the first datacenter found. If there isn't capacity in that one,
 * it will try the other ones.</b></p>
 * The selection of VMs for each cloudlet is is based on a Round-Robin policy,
 * cyclically selecting the next VM from the broker VM list for each requesting
 * cloudlet.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterBrokerSimple extends SimEntity implements DatacenterBroker {
    /**
     * The latest VM selected to run a cloudlet.
     */
    private Vm lastSelectedVm = Vm.NULL;

    /**
     * @see #getVmsWaitingList()
     */
    protected List<Vm> vmsWaitingList;

    /**
     * @see #getVmsCreatedList()
     */
    protected List<Vm> vmsCreatedList;

    /**
     * @see #getCloudletsWaitingList()
     */
    protected List<Cloudlet> cloudletsWaitingList;

    /**
     * @see #getCloudletsFinishedList()
     */
    protected List<Cloudlet> cloudletsFinishedList;

    /**
     * The total number of cloudlets created inside some Vm.
     */
    protected int cloudletsCreated;

    /**
     * @see #getVmCreationRequests()
     */
    protected int vmCreationRequests;

    /**
     *@see #getVmCreationAcks()
     */
    protected int vmCreationAcks;

    /**
     * @see #getVmsDestroyed()
     */
    protected int vmsDestroyed;

    /**
     * @see #getDatacenterIdsList()
     */
    protected List<Integer> datacenterIdsList;

    /**
     * @see #getDatacenterRequestedIdsList()
     */
    protected List<Integer> datacenterRequestedIdsList;

    /**
     * @see #getVmsToDatacentersMap()
     */
    protected Map<Integer, Integer> vmsToDatacentersMap;

    /**
     * @see #getDatacenterCharacteristicsMap()
     */
    protected Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsMap;

    /**
     * Created a new DatacenterBrokerSimple object.
     *
     * @param name name to be associated with this entity
     * @throws IllegalArgumentException when the entity name is invalid
     * @pre name != null
     * @post $none
     */
    public DatacenterBrokerSimple(String name) {
        super(name);

        this.vmsWaitingList = new ArrayList<>();
        this.vmsCreatedList = new ArrayList<>();
        this.cloudletsWaitingList = new ArrayList<>();
        this.cloudletsFinishedList = new ArrayList<>();

        cloudletsCreated = 0;
        vmCreationRequests = 0;
        vmCreationAcks = 0;
        vmsDestroyed = 0;

        setDatacenterIdsList(new LinkedList<>());
        datacenterRequestedIdsList = new ArrayList<>();
        this.vmsToDatacentersMap = new HashMap<>();
        this.datacenterCharacteristicsMap = new HashMap<>();
    }

    @Override
    public void submitVmList(List<? extends Vm> list) {
        getVmsWaitingList().addAll(list);
    }

    @Override
    public void submitCloudletList(List<? extends Cloudlet> list) {
        getCloudletsWaitingList().addAll(list);
    }

    @Override
    public void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay) {
        list.forEach(cloudlet -> cloudlet.setSubmissionDelay(submissionDelay));
        submitCloudletList(list);
    }

    @Override
    public void bindCloudletToVm(int cloudletId, int vmId) {
        CloudletList.getById(getCloudletsWaitingList(), cloudletId).setVmId(vmId);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            // Resource characteristics request
            case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
                processResourceCharacteristicsRequest(ev);
                break;
            // Resource characteristics answer
            case CloudSimTags.DATACENTER_CHARACTERISTICS:
                processResourceCharacteristics(ev);
                break;
            // VM Creation answer
            case CloudSimTags.VM_CREATE_ACK:
                processVmCreate(ev);
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
     * Process the return of a request for the characteristics of a Datacenter.
     *
     * @param ev a SimEvent object
     * @pre ev != $null
     * @post $none
     */
    protected void processResourceCharacteristics(SimEvent ev) {
        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
        getDatacenterCharacteristicsMap().put(characteristics.getId(), characteristics);

        if (getDatacenterCharacteristicsMap().size() == getDatacenterIdsList().size()) {
            this.datacenterRequestedIdsList = new ArrayList<>();
            createVmsInDatacenter(getDatacenterIdsList().get(0));
        }
    }

    /**
     * Process a request for the characteristics of a Datacenter.
     *
     * @param ev a SimEvent object
     * @pre ev != $null
     * @post $none
     */
    protected void processResourceCharacteristicsRequest(SimEvent ev) {
        setDatacenterIdsList(CloudSim.getDatacenterIdsList());
        this.datacenterCharacteristicsMap = new HashMap<>();

        Log.printConcatLine(CloudSim.clock(),
                ": ", getName(), ": Cloud Datacenter List received with ",
                getDatacenterIdsList().size(), " datacenter(s)");

        for (Integer datacenterId : getDatacenterIdsList()) {
            sendNow(datacenterId, CloudSimTags.DATACENTER_CHARACTERISTICS, getId());
        }
    }

    /**
     * Process the ack received due to a request for VM creation.
     *
     * @param ev a SimEvent object
     * @return true if the VM was created successfully, false otherwise
     * @pre ev != null
     * @post $none
     */
    protected boolean processVmCreate(SimEvent ev) {
        int[] data = (int[]) ev.getData();
        int datacenterId = data[0];
        int vmId = data[1];
        int result = data[2];
        boolean created = false;

        if (result == CloudSimTags.TRUE) {
            getVmsToDatacentersMap().put(vmId, datacenterId);
            
            Vm vm = VmList.getById(getVmsWaitingList(), vmId);
            created = vm != Vm.NULL;
            if(created){
                getVmsCreatedList().add(vm);
                Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": VM #", vmId,
                        " has been created in Datacenter #", datacenterId, ", Host #",
                        VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
            } else Log.printFormattedLine(
                "The request to create Vm %d was not processed because the Vm was not found in the waiting list.", 
                vmId);
        } else {
            Vm vm = VmList.getById(getVmsWaitingList(), vmId);
            if (vm != Vm.NULL) {
                Datacenter datacenter =
                        datacenterCharacteristicsMap.get(datacenterId).getDatacenter();
                DatacenterToVmEventInfo info =
                        new DatacenterToVmEventInfo(datacenter, vm);
                vm.getOnVmCreationFailureListener().update(info);
            }
            Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Creation of VM #", vmId,
                    " failed in Datacenter #", datacenterId);
        }

        vmCreationAcks++;

        // all the requested VMs have been created
        if (getVmsCreatedList().size() == getVmsWaitingList().size() - getVmsDestroyed()) {
            createCloudletsInVms();
        } else {
            // all the acks received, but some VMs were not created
            if (getVmCreationRequests() == getVmCreationAcks()) {
                // find id of the next datacenter that has not been tried
                for (int nextDatacenterId : getDatacenterIdsList()) {
                    if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
                        createVmsInDatacenter(nextDatacenterId);
                        return created;
                    }
                }

                // all datacenters already queried
                if (getVmsCreatedList().size() > 0) { // if some vm were created
                    createCloudletsInVms();
                } else { // no vms created. abort
                    Log.printLine(CloudSim.clock() + ": " + getName()
                            + ": none of the required VMs could be created. Aborting");
                    finishExecution();
                }
            }
        }

        return created;
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

        Log.printFormattedLine("%.1f: %s: %s %d received",
                CloudSim.clock(), getName(),
                cloudlet.getClass().getSimpleName(), cloudlet.getId());
        cloudletsCreated--;
        if (getCloudletsWaitingList().isEmpty() && cloudletsCreated == 0) { // all cloudlets executed
            Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": All Cloudlets executed. Finishing...");
            clearDatacenters();
            finishExecution();
        } else if (hasMoreCloudletsToBeExecuted()) {
            // All the cloudlets sent have finished. It means that some bound
            // cloudlet is waiting its VM to be created
            clearDatacenters();
            createVmsInDatacenter(0);
        }
    }

    @Override
    public boolean hasMoreCloudletsToBeExecuted() {
        return getCloudletsWaitingList().size() > 0 && cloudletsCreated == 0;
    }

    private void notifyCloudletFinishListener(Cloudlet cloudlet) {
        Vm vm = VmList.getById(vmsWaitingList, cloudlet.getVmId());
        VmToCloudletEventInfo info = new VmToCloudletEventInfo(vm, cloudlet);
        cloudlet.getOnCloudletFinishEventListener().update(info);
    }

    /**
     * Process non-default received events that aren't processed by the
     * {@link #processEvent(org.cloudbus.cloudsim.core.SimEvent)} method. This
     * method should be overridden by subclasses in other to process new defined
     * events.
     *
     * @param ev a SimEvent object
     * @pre ev != null
     * @post $none
     * @todo to ensure the method will be overridden, it should be defined as
     * abstract in a super class from where new brokers have to be extended.
     */
    protected void processOtherEvent(SimEvent ev) {
        if (ev == null) {
            Log.printConcatLine(getName(), ".processOtherEvent(): ", "Error - an event is null.");
            return;
        }

        Log.printConcatLine(getName(), ".processOtherEvent(): Error - event unknown by this DatacenterBroker.");
    }

    /**
     * Create the submitted virtual machines in a datacenter.
     *
     * @param datacenterId Id of the chosen Datacenter
     * @pre $none
     * @post $none
     * @see #submitVmList(java.util.List)
     */
    protected void createVmsInDatacenter(int datacenterId) {
        int requestedVms = 0;
        String datacenterName = CloudSim.getEntityName(datacenterId);
        for (Vm vm : getVmsWaitingList()) {
            if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
                Log.printLine(CloudSim.clock() + ": " + getName() +
                        ": Trying to Create VM #" + vm.getId()
                        + " in " + datacenterName);
                sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
                requestedVms++;
            }
        }

        getDatacenterRequestedIdsList().add(datacenterId);
        this.vmCreationRequests += requestedVms;
        vmCreationAcks= 0;
    }

    /**
     * Try to create each submitted cloudlet inside a created VM.
     * If there isn't available VMs to host all cloudlets,
     * the creation of some ones will be postponed.
     *
     * @pre $none
     * @post $none
     * @see #submitCloudletList(java.util.List)
     */
    protected void createCloudletsInVms() {
        List<Cloudlet> successfullySubmitted = new ArrayList<>();
        for (Cloudlet cloudlet : getCloudletsWaitingList()) {
            lastSelectedVm = selectVmForCloudlet(cloudlet);

            if (lastSelectedVm == Vm.NULL) { // vm was not created
                Log.printConcatLine(CloudSim.clock(), ": ", getName(),
                        ": Postponing execution of cloudlet ",
                        cloudlet.getId(), ": bounded VM not available");
                continue;
            }

            Log.printFormattedLine(
                    "%.2f: %s: Sending %s %d to VM #%d",
                    CloudSim.clock(), getName(),
                    cloudlet.getClass().getSimpleName(),
                    cloudlet.getId(), lastSelectedVm.getId());

            cloudlet.setVmId(lastSelectedVm.getId());
            send(getVmsToDatacentersMap().get(
                    lastSelectedVm.getId()), cloudlet.getSubmissionDelay(), 
                    CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
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
     * {@inheritDoc}
     * 
     * <br>This method applies a Round-Robin policy to cyclically select
     * the next Vm from the broker Vm list.
     * 
     * @param cloudlet {@inheritDoc}
     * @return  {@inheritDoc}
     */
    @Override
    public Vm selectVmForCloudlet(Cloudlet cloudlet) {
        if (cloudlet.isBoundedToVm()) {
            // submit to the specific vm
            return VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
        } 

        //if user didn't bind this cloudlet and it has not been executed yet
        return getVmFromCreatedList(getNextVmIndex());
    }

    /**
     * Gets the index of next VM in the broker's created VM list.
     * If not VM was selected yet, selects the first one,
     * otherwise, cyclically selects the next VM.
     * 
     * @return the index of the next VM to bind a cloudlet to
     */
    private int getNextVmIndex() {
        int vmIndex = getVmsCreatedList().indexOf(lastSelectedVm);
        vmIndex = (vmIndex == -1 ? 0 : (vmIndex + 1) % getVmsCreatedList().size());
        return vmIndex;
    }

    /**
     * Destroy all virtual machines running in datacenters.
     *
     * @pre $none
     * @post $none
     */
    protected void clearDatacenters() {
        for (Vm vm : getVmsCreatedList()) {
            Log.printConcatLine(CloudSim.clock(), ": " + getName(), ": Destroying VM #", vm.getId());
            sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
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
        schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
    }

    @Override
    public <T extends Vm> List<T> getVmsWaitingList() {
        return (List<T>)vmsWaitingList;
    }

    @Override
    public Vm getVm(final int index) {
        if (index >= 0 && index < vmsWaitingList.size()) {
            return vmsWaitingList.get(index);
        }
        return Vm.NULL;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletsWaitingList() {
        return (List<T>)cloudletsWaitingList;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletsFinishedList() {
        return (List<T>)cloudletsFinishedList;
    }

    @Override
    public <T extends Vm> List<T> getVmsCreatedList() {
        return (List<T>)vmsCreatedList;
    }
    
    /**
     * Gets a Vm at a given index from the {@link #getVmsCreatedList() list of created VMs}.
     * 
     * @param vmIndex the index where a VM has to be got from the created VM list
     * @return the VM at the given index or {@link Vm#NULL} if the index is invalid
     */
    private Vm getVmFromCreatedList(int vmIndex){
        return (vmIndex >= 0 && vmIndex < vmsCreatedList.size() ? 
                vmsCreatedList.get(vmIndex): 
                Vm.NULL);
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
     * Gets the number of acknowledges (ACKs) sent in response to requests of VM creation.
     *
     * @return the number vm creation acks
     */
    protected int getVmCreationAcks() {
        return vmCreationAcks;
    }

    /**
     * Gets the number of destroyed VMs.
     *
     * @return the number of vms destroyed
     */
    protected int getVmsDestroyed() {
        return vmsDestroyed;
    }

    /**
     * Gets the id's list of available datacenters.
     *
     * @return the datacenter id's list
     */
    protected List<Integer> getDatacenterIdsList() {
        return datacenterIdsList;
    }

    /**
     * Sets the id's list of available datacenters.
     *
     * @param datacenterIdsList the new datacenter id's list
     */
    protected final void setDatacenterIdsList(List<Integer> datacenterIdsList) {
        this.datacenterIdsList = datacenterIdsList;
    }

    /**
     * Gets the vms to datacenters map, where each key is a VM id and each value is
     * the datacenter id whwere the VM is placed.
     *
     * @return the vms to datacenters map
     */
    protected Map<Integer, Integer> getVmsToDatacentersMap() {
        return vmsToDatacentersMap;
    }

    /**
     * Gets the datacenter characteristics map where each key is a datacenter id and
     * each value is its characteristics.
     *
     * @return the datacenter characteristics map
     */
    protected Map<Integer, DatacenterCharacteristics> getDatacenterCharacteristicsMap() {
        return datacenterCharacteristicsMap;
    }

    /**
     * Gets the list of datacenters where was requested to place VMs.
     *
     * @return the datacenter requested id's list
     */
    protected List<Integer> getDatacenterRequestedIdsList() {
        return datacenterRequestedIdsList;
    }

}
