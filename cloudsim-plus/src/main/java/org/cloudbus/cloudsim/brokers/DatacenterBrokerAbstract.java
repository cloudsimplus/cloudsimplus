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
import org.cloudbus.cloudsim.listeners.DatacenterToVmEventInfo;
import org.cloudbus.cloudsim.listeners.VmToCloudletEventInfo;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * An abstract class to be used as base for implementing a {@link DatacenterBroker}.
 * 
 * @author Manoel Campos da Silva Filho
 */
public abstract class DatacenterBrokerAbstract extends SimEntity implements DatacenterBroker {
    /**
     * @see #getLastSelectedVm() 
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
     * Creates a new DatacenterBroker object.
     *
     * @param name name to be associated with this entity
     * @throws IllegalArgumentException when the entity name is invalid
     * @pre name != null
     * @post $none
     */
    public DatacenterBrokerAbstract(String name) {
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
        list.forEach((cloudlet) -> cloudlet.setSubmissionDelay(submissionDelay));
        submitCloudletList(list);
    }

    @Override
    public void bindCloudletToVm(int cloudletId, int vmId) {
        CloudletList.getById(getCloudletsWaitingList(), cloudletId).setVmId(vmId);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
        // Datacenter characteristics request
            case CloudSimTags.DATACENTER_CHARACTERISTICS_REQUEST:
                processDatacenterCharacteristicsRequest(ev);
                break;
        // Datacenter characteristics response
            case CloudSimTags.DATACENTER_CHARACTERISTICS:
                processDatacenterCharacteristicsResponse(ev);
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
     * Process the response of a request for the characteristics of a Datacenter
     * and then requests the creation of waiting VMs in the received Datacenter.
     *
     * @param ev a SimEvent object
     * @pre ev != $null
     * @post $none
     */
    protected void processDatacenterCharacteristicsResponse(SimEvent ev) {
        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
        getDatacenterCharacteristicsMap().put(characteristics.getId(), characteristics);
        if (getDatacenterCharacteristicsMap().size() == getDatacenterIdsList().size()) {
            this.datacenterRequestedIdsList = new ArrayList<>();
            requestDatacenterToCreateWaitingVms(selectDatacenterForWaitingVms());
        }
    }

    /**
     * Process a request for the characteristics of a Datacenter
     * and gets the list of available Datacenters.
     *
     * @param ev a SimEvent object
     * @pre ev != $null
     * @post $none
     */
    protected void processDatacenterCharacteristicsRequest(SimEvent ev) {
        setDatacenterIdsList(CloudSim.getDatacenterIdsList());
        this.datacenterCharacteristicsMap = new HashMap<>();
        Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Cloud Datacenter List received with ", getDatacenterIdsList().size(), " datacenter(s)");
        for (Integer datacenterId : getDatacenterIdsList()) {
            sendNow(datacenterId, CloudSimTags.DATACENTER_CHARACTERISTICS, getId());
        }
    }

    /**
     * Process the ack received from a Datacenter to a broker request for
     * creation of a Vm in that Datacenter.
     *
     * @param ev a SimEvent object
     * @return true if the VM was created successfully, false otherwise
     * @pre ev != null
     * @post $none
     */
    protected boolean processVmCreateResponseFromDatacenter(SimEvent ev) {
        int[] data = (int[]) ev.getData();
        int datacenterId = data[0];
        int vmId = data[1];
        int result = data[2];
        boolean vmCreatedAndLocated = false;
        vmCreationAcks++;

        //if the VM was sucessfully created in the requested Datacenter
        if (result == CloudSimTags.TRUE) {
            vmCreatedAndLocated = processSuccessVmCreationInDatacenter(vmId, datacenterId);
        } else {
            processFailedVmCreationInDatacenter(vmId, datacenterId);
        }
        
        // all the requested VMs have been created
        if (getVmsCreatedList().size() == getVmsWaitingList().size() - getVmsDestroyed()) {
            requestDatacentersToCreateWaitingCloudlets();
        } else if (getVmCreationRequests() == getVmCreationAcks()) {
            requestCreationOfWaitingVmsToNextDatacenter();
        }
        return vmCreatedAndLocated;
    }

    /**
     * After the response (ack) of all VM creation request were received
     * but not all VMs could be created (what means some
     * acks informed about Vm creation failures), try to find
     * another datacenter to request the creation of the VMs
     * in the waiting list.
     */
    protected void requestCreationOfWaitingVmsToNextDatacenter() {
        final int nextDatacenterId = selectFallbackDatacenterForWaitingVms();
        if (nextDatacenterId != -1) {
            requestDatacenterToCreateWaitingVms(nextDatacenterId);
            return;
        }
        // all datacenters already queried
        if (getVmsCreatedList().size() > 0) {
            // if some VMs were created
            requestDatacentersToCreateWaitingCloudlets();
        } else {
            // no VMs created. abort
            Log.printFormattedLine("%.2f: %s: %s", CloudSim.clock(), getName(), "none of the required VMs could be created. Aborting");
            finishExecution();
        }
    }

    /**
     * Process a response from a datacenter informing that it was able to
     * create the VM requested by the broker.
     *
     * @param vmId id of the Vm that succeeded to be created inside the Datacenter
     * @param datacenterId id of the Datacenter where the request to create
     * the Vm succeeded
     * @return true if the created VM was found in broker VM list, false otherwise
     */
    protected boolean processSuccessVmCreationInDatacenter(int vmId, int datacenterId) {
        getVmsToDatacentersMap().put(vmId, datacenterId);
        Vm vm = VmList.getById(getVmsWaitingList(), vmId);
        boolean vmLocated = vm != Vm.NULL;
        if (vmLocated) {
            /**
             * @todo @author manoelcampos It should remove the created VM from the waiting list.
             */
            getVmsCreatedList().add(vm);
            Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": VM #", vmId, " has been created in Datacenter #", datacenterId, ", Host #", VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
        } else {
            Log.printFormattedLine("The request to create Vm %d was not processed because the Vm was not found in the waiting list.", vmId);
        }
        return vmLocated;
    }

    /**
     * Process a response from a datacenter informing that it was NOT able to
     * create the VM requested by the broker.
     *
     * @param vmId id of the Vm that failed to be created inside the Datacenter
     * @param datacenterId id of the Datacenter where the request to create
     * the Vm failed
     */
    protected void processFailedVmCreationInDatacenter(int vmId, int datacenterId) {
        Vm vm = VmList.getById(getVmsWaitingList(), vmId);
        if (vm != Vm.NULL) {
            Datacenter datacenter = datacenterCharacteristicsMap.get(datacenterId).getDatacenter();
            DatacenterToVmEventInfo info = new DatacenterToVmEventInfo(datacenter, vm);
            vm.getOnVmCreationFailureListener().update(info);
        }
        Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Creation of VM #", vmId, " failed in Datacenter #", datacenterId);
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
        Log.printFormattedLine("%.1f: %s: %s %d received", CloudSim.clock(), getName(), cloudlet.getClass().getSimpleName(), cloudlet.getId());
        cloudletsCreated--;
        if (getCloudletsWaitingList().isEmpty() && cloudletsCreated == 0) {
            // all cloudlets executed
            Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": All Cloudlets executed. Finishing...");
            destroyVms();
            finishExecution();
        } else if (hasMoreCloudletsToBeExecuted()) {
            /* All the cloudlets sent have finished. It means that some bounded
            cloudlet are waiting their VMs to be created*/
            destroyVms();
            requestDatacenterToCreateWaitingVms(selectDatacenterForWaitingVms());
        }
    }

    @Override
    public boolean hasMoreCloudletsToBeExecuted() {
        return getCloudletsWaitingList().size() > 0 && cloudletsCreated == 0;
    }

    protected void notifyCloudletFinishListener(Cloudlet cloudlet) {
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
     * Request a datacenter to create the VM in the {@link #getVmsWaitingList() VM waiting list}.
     *
     * @param datacenterId id of the datacenter to request the VMs creation
     * @pre $none
     * @post $none
     * @see #submitVmList(java.util.List)
     */
    protected void requestDatacenterToCreateWaitingVms(int datacenterId) {
        int requestedVms = 0;
        String datacenterName = CloudSim.getEntityName(datacenterId);
        for (Vm vm : getVmsWaitingList()) {
            if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
                Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in " + datacenterName);
                sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
                requestedVms++;
            }
        }
        getDatacenterRequestedIdsList().add(datacenterId);
        this.vmCreationRequests += requestedVms;
        vmCreationAcks = 0;
    }

    /**
     * Request Datacenters to create the Cloudlets in the
     * {@link #getCloudletsWaitingList() Cloudlets waiting list}.
     *
     * If there aren't available VMs to host all cloudlets,
     * the creation of some ones will be postponed.
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
                Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Postponing execution of cloudlet ", cloudlet.getId(), ": bounded VM not available");
                continue;
            }
            Log.printFormattedLine("%.2f: %s: Sending %s %d to VM #%d", CloudSim.clock(), getName(), cloudlet.getClass().getSimpleName(), cloudlet.getId(), getLastSelectedVm().getId());
            cloudlet.setVmId(getLastSelectedVm().getId());
            send(getVmsToDatacentersMap().get(getLastSelectedVm().getId()), 
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
        schedule(getId(), 0, CloudSimTags.DATACENTER_CHARACTERISTICS_REQUEST);
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
     * Gets the VM to Datacenter map, where each key is a VM id and each value is
     * the id of the datacenter where the VM is placed.
     *
     * @return the VM to Datacenter map
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

    /**
     * 
     * @return latest VM selected to run a cloudlet.
     */
    protected Vm getLastSelectedVm() {
        return lastSelectedVm;
    }
    
}
