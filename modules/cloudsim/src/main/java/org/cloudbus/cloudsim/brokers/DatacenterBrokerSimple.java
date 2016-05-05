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
import org.cloudbus.cloudsim.listeners.CloudletInsideVmEventInfo;
import org.cloudbus.cloudsim.listeners.VmInsideDatacenterEventInfo;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * DatacentreBroker represents a broker acting on behalf of a user. It hides VM
 * management, as vm creation, submission of cloudlets to VMs and destruction of
 * VMs.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterBrokerSimple extends SimEntity implements DatacenterBroker {

    /**
     * The list of VMs submitted to be managed by the broker.
     */
    protected List<Vm> vmList;

    /**
     * The list of VMs created by the broker.
     */
    protected List<Vm> vmsCreatedList;

    /**
     * The list of cloudlet submitted to the broker.
     *
     * @see #submitCloudletList(java.util.List)
     */
    protected List<Cloudlet> cloudletList;

    /**
     * The list of received cloudlet.
     */
    protected List<Cloudlet> cloudletReceivedList;

    /**
     * The number of submitted cloudlets.
     */
    protected int cloudletsSubmitted;

    /**
     * The number of requests to create VM.
     */
    protected int vmsRequested;

    /**
     * The number of acknowledges (ACKs) sent in response to VM creation
     * requests.
     */
    protected int vmsAcks;

    /**
     * The number of destroyed VMs.
     */
    protected int vmsDestroyed;

    /**
     * The id's list of available datacenters.
     */
    protected List<Integer> datacenterIdsList;

    /**
     * The list of datacenters where was requested to place VMs.
     */
    protected List<Integer> datacenterRequestedIdsList;

    /**
     * The vms to datacenters map, where each key is a VM id and each value is
     * the datacenter id whwere the VM is placed.
     */
    protected Map<Integer, Integer> vmsToDatacentersMap;

    /**
     * The datacenter characteristics map where each key is a datacenter id and
     * each value is its characteristics..
     */
    protected Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList;
    
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

        this.vmList = new ArrayList<>();
        setVmsCreatedList(new ArrayList<>());
        setCloudletList(new ArrayList<>());
        setCloudletReceivedList(new ArrayList<>());

        cloudletsSubmitted = 0;
        setVmsRequested(0);
        setVmsAcks(0);
        setVmsDestroyed(0);

        setDatacenterIdsList(new LinkedList<>());
        setDatacenterRequestedIdsList(new ArrayList<>());
        setVmsToDatacentersMap(new HashMap<>());
        setDatacenterCharacteristicsList(new HashMap<>());
    }

    @Override
    public <T extends Vm> void submitVmList(List<T> list) {
        getVmList().addAll(list);
    }

    /**
     * This method is used to send to the broker the list of cloudlets.
     *
     * @param list the list
     * @pre list !=null
     * @post $none
     *
     * @todo The name of the method is confused with the
     * {@link #submitCloudlets()}, that in fact submit cloudlets to VMs. The
     * term "submit" is being used ambiguously. The method
     * {@link #submitCloudlets()} would be named "sendCloudletsToVMs"
     *
     * The method {@link #submitVmList(java.util.List)} may have be checked too.
     */
    @Override
    public <T extends Cloudlet> void submitCloudletList(List<T> list) {
        getCloudletList().addAll(list);
    }

    /**
     * Specifies that a given cloudlet must run in a specific virtual machine.
     *
     * @param cloudletId ID of the cloudlet being bount to a vm
     * @param vmId the vm id
     * @pre cloudletId > 0
     * @pre id > 0
     * @post $none
     * @todo @author manoelcampos This method would receive a Cloudlet object
     * because it is just setting the vmId cloudlet attribute. When the method
     * is called prior to call
     * {@link DatacenterBroker#submitCloudletList(java.util.List)}, it tries to
     * locate the cloudlet in the submitted list and, when it doesn't exist yet,
     * it is thrown a NullPointerException. At leat, an overloaded version of
     * the method would be created and this one would try to find the cloudlet
     * and, when it is not found, thrown an specific exception asking if the
     * cloudlet already was submitted.
     */
    @Override
    public void bindCloudletToVm(int cloudletId, int vmId) {
        CloudletList.getById(getCloudletList(), cloudletId).setVmId(vmId);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            // Resource characteristics request
            case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
                processResourceCharacteristicsRequest(ev);
                break;
            // Resource characteristics answer
            case CloudSimTags.RESOURCE_CHARACTERISTICS:
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
        getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

        if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
            setDatacenterRequestedIdsList(new ArrayList<>());
            createVmsInDatacenter(getDatacenterIdsList().get(0));
        }
    }

    /**
     * Process a request for the characteristics of a PowerDatacenter.
     *
     * @param ev a SimEvent object
     * @pre ev != $null
     * @post $none
     */
    protected void processResourceCharacteristicsRequest(SimEvent ev) {
        setDatacenterIdsList(CloudSim.getCloudResourceList());
        setDatacenterCharacteristicsList(new HashMap<>());

        Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Cloud Resource List received with ",
                getDatacenterIdsList().size(), " resource(s)");

        for (Integer datacenterId : getDatacenterIdsList()) {
            sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
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
            getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
            Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": VM #", vmId,
                    " has been created in Datacenter #", datacenterId, ", Host #",
                    VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
            created = true;
        } else {
            Vm vm = VmList.getById(getVmList(), vmId);
            
            if (vm != null) {
                Datacenter datacenter = 
                        datacenterCharacteristicsList.get(datacenterId).getDatacenter();
                VmInsideDatacenterEventInfo info = 
                        new VmInsideDatacenterEventInfo(datacenter, vm);
                vm.getOnVmCreationFailureListener().update(info);
            }
            Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Creation of VM #", vmId,
                    " failed in Datacenter #", datacenterId);
        }

        incrementVmsAcks();

        // all the requested VMs have been created
        if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
            submitCloudlets();
        } else {
            // all the acks received, but some VMs were not created
            if (getVmsRequested() == getVmsAcks()) {
                // find id of the next datacenter that has not been tried
                for (int nextDatacenterId : getDatacenterIdsList()) {
                    if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
                        createVmsInDatacenter(nextDatacenterId);
                        return created;
                    }
                }

                // all datacenters already queried
                if (getVmsCreatedList().size() > 0) { // if some vm were created
                    submitCloudlets();
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
     * Processes the end of execution of a given cloudlet inside
     * a Vm.
     *
     * @param ev The cloudlet that has just finished to execute
     * @pre ev != $null
     * @post $none
     */
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletReceivedList().add(cloudlet);
        Vm vm = VmList.getById(vmList, cloudlet.getVmId());
        CloudletInsideVmEventInfo info = new CloudletInsideVmEventInfo(vm, cloudlet);
        cloudlet.getOnCloudletFinishEventListener().update(info);
        Log.printConcatLine(
                CloudSim.clock(), ": ", getName(), ": Cloudlet ", 
                cloudlet.getId(), " received");
        cloudletsSubmitted--;
        if (getCloudletList().isEmpty() && cloudletsSubmitted == 0) { // all cloudlets executed
            Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": All Cloudlets executed. Finishing...");
            clearDatacenters();
            finishExecution();
        } else { // some cloudlets haven't finished yet
            if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
                // all the cloudlets sent finished. It means that some bount
                // cloudlet is waiting its VM be created
                clearDatacenters();
                createVmsInDatacenter(0);
            }
        }
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
        // send as much vms as possible for this datacenter before trying the next one
        int requestedVms = 0;
        String datacenterName = CloudSim.getEntityName(datacenterId);
        for (Vm vm : getVmList()) {
            if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
                Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId()
                        + " in " + datacenterName);
                sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
                requestedVms++;
            }
        }

        getDatacenterRequestedIdsList().add(datacenterId);

        setVmsRequested(requestedVms);
        setVmsAcks(0);
    }

    /**
     * Submit cloudlets to the created VMs.
     *
     * @pre $none
     * @post $none
     * @see #submitCloudletList(java.util.List)
     */
    protected void submitCloudlets() {
        int vmIndex = 0;
        List<Cloudlet> successfullySubmitted = new ArrayList<>();
        for (Cloudlet cloudlet : getCloudletList()) {
            Vm vm;
            // if user didn't bind this cloudlet and it has not been executed yet
            if (cloudlet.getVmId() == -1) {
                vm = getVmsCreatedList().get(vmIndex);
            } else { // submit to the specific vm
                vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
                if (vm == null) { // vm was not created
                    if (!Log.isDisabled()) {
                        Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Postponing execution of cloudlet ",
                                cloudlet.getId(), ": bount VM not available");
                    }
                    continue;
                }
            }

            if (!Log.isDisabled()) {
                Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Sending cloudlet ",
                        cloudlet.getId(), " to VM #", vm.getId());
            }

            cloudlet.setVmId(vm.getId());
            sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            cloudletsSubmitted++;
            vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
            successfullySubmitted.add(cloudlet);
        }

        // remove submitted cloudlets from waiting list
        getCloudletList().removeAll(successfullySubmitted);
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
    public <T extends Vm> List<T> getVmList() {
        return (List<T>)vmList;
    }

    @Override
    public Vm getVm(final int index) {
        if (index >= 0 && index < vmList.size()) {
            return vmList.get(index);
        }
        return Vm.NULL;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletList() {
        return (List<T>)cloudletList;
    }

    /**
     * Sets the cloudlet list.
     *
     * @param cloudletList the new cloudlet list
     */
    protected final void setCloudletList(List<Cloudlet> cloudletList) {
        this.cloudletList = cloudletList;
    }

    @Override
    public <T extends Cloudlet> List<T> getCloudletReceivedList() {
        return (List<T>)cloudletReceivedList;
    }

    /**
     * Sets the cloudlet received list.
     *
     * @param cloudletReceivedList the new cloudlet received list
     */
    protected final void setCloudletReceivedList(List<Cloudlet> cloudletReceivedList) {
        this.cloudletReceivedList = cloudletReceivedList;
    }

    @Override
    public <T extends Vm> List<T> getVmsCreatedList() {
        return (List<T>)vmsCreatedList;
    }

    /**
     * Sets the vm created list.
     *
     * @param vmsCreatedList the vms created list
     */
    protected final void setVmsCreatedList(List<Vm> vmsCreatedList) {
        this.vmsCreatedList = vmsCreatedList;
    }

    /**
     * Gets the vms requested.
     *
     * @return the vms requested
     */
    protected int getVmsRequested() {
        return vmsRequested;
    }

    /**
     * Sets the vms requested.
     *
     * @param vmsRequested the new vms requested
     */
    protected final void setVmsRequested(int vmsRequested) {
        this.vmsRequested = vmsRequested;
    }

    /**
     * Gets the vms acks.
     *
     * @return the vms acks
     */
    protected int getVmsAcks() {
        return vmsAcks;
    }

    /**
     * Sets the vms acks.
     *
     * @param vmsAcks the new vms acks
     */
    protected final void setVmsAcks(int vmsAcks) {
        this.vmsAcks = vmsAcks;
    }

    /**
     * Increment the number of acknowledges (ACKs) sent in response to requests
     * of VM creation.
     */
    protected void incrementVmsAcks() {
        vmsAcks++;
    }

    /**
     * Gets the vms destroyed.
     *
     * @return the vms destroyed
     */
    protected int getVmsDestroyed() {
        return vmsDestroyed;
    }

    /**
     * Sets the vms destroyed.
     *
     * @param vmsDestroyed the new vms destroyed
     */
    protected final void setVmsDestroyed(int vmsDestroyed) {
        this.vmsDestroyed = vmsDestroyed;
    }

    /**
     * Gets the datacenter ids list.
     *
     * @return the datacenter ids list
     */
    protected List<Integer> getDatacenterIdsList() {
        return datacenterIdsList;
    }

    /**
     * Sets the datacenter ids list.
     *
     * @param datacenterIdsList the new datacenter ids list
     */
    protected final void setDatacenterIdsList(List<Integer> datacenterIdsList) {
        this.datacenterIdsList = datacenterIdsList;
    }

    /**
     * Gets the vms to datacenters map.
     *
     * @return the vms to datacenters map
     */
    protected Map<Integer, Integer> getVmsToDatacentersMap() {
        return vmsToDatacentersMap;
    }

    /**
     * Sets the vms to datacenters map.
     *
     * @param vmsToDatacentersMap the vms to datacenters map
     */
    protected final void setVmsToDatacentersMap(Map<Integer, Integer> vmsToDatacentersMap) {
        this.vmsToDatacentersMap = vmsToDatacentersMap;
    }

    /**
     * Gets the datacenter characteristics list.
     *
     * @return the datacenter characteristics list
     */
    protected Map<Integer, DatacenterCharacteristics> getDatacenterCharacteristicsList() {
        return datacenterCharacteristicsList;
    }

    /**
     * Sets the datacenter characteristics list.
     *
     * @param datacenterCharacteristicsList the datacenter characteristics list
     */
    protected final void setDatacenterCharacteristicsList(
            Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList) {
        this.datacenterCharacteristicsList = datacenterCharacteristicsList;
    }

    /**
     * Gets the datacenter requested ids list.
     *
     * @return the datacenter requested ids list
     */
    protected List<Integer> getDatacenterRequestedIdsList() {
        return datacenterRequestedIdsList;
    }

    /**
     * Sets the datacenter requested ids list.
     *
     * @param datacenterRequestedIdsList the new datacenter requested ids list
     */
    protected final void setDatacenterRequestedIdsList(List<Integer> datacenterRequestedIdsList) {
        this.datacenterRequestedIdsList = datacenterRequestedIdsList;
    }

}
