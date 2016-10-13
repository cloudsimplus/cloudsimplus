/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.network.InfoPacket;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.resources.FileStorage;

/**
 * Implements the basic features of a Virtualized Cloud Datacenter. It deals
 * with processing of VM queries (i.e., handling of VMs) instead of processing
 * Cloudlet-related queries.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterSimple extends SimEntity implements Datacenter {
    /** @see #getCharacteristics() */
    private DatacenterCharacteristics characteristics;

    /** @see #getRegionalCisName() */
    private String regionalCisName;

    /** @see #getVmAllocationPolicy() */
    private VmAllocationPolicy vmAllocationPolicy;

    /** @see #getLastProcessTime() */
    private double lastProcessTime;

    /** @see #getStorageList() */
    private List<FileStorage> storageList;

    /** @see #getVmList() */
    private List<? extends Vm> vmList;

    /** @see #getSchedulingInterval() */
    private double schedulingInterval;

    /**
     * Instantiates a new Datacenter object.
     *
     * @param name the name of the datacenter
     * @param characteristics the characteristics of the datacenter to be
     * created
     * @param storageList a List of storage elements, for data simulation
     * @param vmAllocationPolicy the policy to be used to allocate VMs into
     * hosts
     * @param schedulingInterval the scheduling interval to process each
     * datacenter received event (in seconds)
     * @throws IllegalArgumentException when one of the following scenarios
     * occur:
     * <ul>
     * <li>creating this entity before initializing CloudSim package
     * <li>this entity name is <tt>null</tt> or empty
     * <li>this entity has <tt>zero</tt> number of PEs (Processing Elements).
     * <br>
     * No PEs mean the Cloudlets can't be processed. A CloudResource must
     * contain one or more Machines. A Machine must contain one or more PEs.
     * </ul>
     *
     * @pre name != null
     * @pre resource != null
     * @post $none
     */
    public DatacenterSimple(
            String name,
            DatacenterCharacteristics characteristics,
            VmAllocationPolicy vmAllocationPolicy,
            List<FileStorage> storageList,
            double schedulingInterval) {
        super(name);

        // If this resource doesn't have any PEs then it isn't useful at all
        if (characteristics.getNumberOfPes() == 0) {
            throw new IllegalArgumentException(super.getName()
                    + " : Error - this entity has no PEs. Therefore, can't process any Cloudlets.");
        }

        // stores id of this class
        setCharacteristics(characteristics);

        setVmAllocationPolicy(vmAllocationPolicy);
        setLastProcessTime(0.0);
        setStorageList(storageList);
        setVmList(new ArrayList<Vm>());
        setSchedulingInterval(schedulingInterval);
        assignHostsToCurrentDatacenter();
    }

    private void assignHostsToCurrentDatacenter() {
        getCharacteristics().getHostList().forEach(host -> host.setDatacenter(this));
    }

    /**
     * Overrides this method when making a new and different type of resource.
     * <br>
     *
     * @pre $none
     * @post $none
     *
     * @todo This method doesn't appear to be used
     */
    protected void registerOtherEntity() {
        // empty. This should be override by a child class
    }

    @Override
    public void processEvent(SimEvent ev) {
        int srcId;

        switch (ev.getTag()) {
            // Resource characteristics inquiry
            case CloudSimTags.DATACENTER_CHARACTERISTICS:
                srcId = ((Integer) ev.getData());
                sendNow(srcId, ev.getTag(), getCharacteristics());
                break;

            // Resource dynamic info inquiry
            case CloudSimTags.RESOURCE_DYNAMICS:
                srcId = ((Integer) ev.getData());
                sendNow(srcId, ev.getTag(), 0);
                break;

            case CloudSimTags.RESOURCE_NUM_PE:
                srcId = ((Integer) ev.getData());
                int numPE = getCharacteristics().getNumberOfPes();
                sendNow(srcId, ev.getTag(), numPE);
                break;

            case CloudSimTags.RESOURCE_NUM_FREE_PE:
                srcId = ((Integer) ev.getData());
                int freePesNumber = getCharacteristics().getNumberOfFreePes();
                sendNow(srcId, ev.getTag(), freePesNumber);
                break;

            // New Cloudlet arrives
            case CloudSimTags.CLOUDLET_SUBMIT:
                processCloudletSubmit(ev, false);
                break;

            // New Cloudlet arrives, but the sender asks for an ack
            case CloudSimTags.CLOUDLET_SUBMIT_ACK:
                processCloudletSubmit(ev, true);
                break;

            // Cancels a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_CANCEL:
                processCloudlet(ev, CloudSimTags.CLOUDLET_CANCEL);
                break;

            // Pauses a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_PAUSE:
                processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE);
                break;

            // Pauses a previously submitted Cloudlet, but the sender
            // asks for an acknowledgement
            case CloudSimTags.CLOUDLET_PAUSE_ACK:
                processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE_ACK);
                break;

            // Resumes a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_RESUME:
                processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME);
                break;

            // Resumes a previously submitted Cloudlet, but the sender
            // asks for an acknowledgement
            case CloudSimTags.CLOUDLET_RESUME_ACK:
                processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME_ACK);
                break;

            // Moves a previously submitted Cloudlet to a different Datacenter
            case CloudSimTags.CLOUDLET_MOVE:
                processCloudletMove((int[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE);
                break;

            // Moves a previously submitted Cloudlet to a different Datacenter
            case CloudSimTags.CLOUDLET_MOVE_ACK:
                processCloudletMove((int[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE_ACK);
                break;

            // Checks the status of a Cloudlet
            case CloudSimTags.CLOUDLET_STATUS:
                processCloudletStatus(ev);
                break;

            // Ping packet
            case CloudSimTags.INFOPKT_SUBMIT:
                processPingRequest(ev);
                break;

            case CloudSimTags.VM_CREATE:
                processVmCreate(ev, false);
                break;

            case CloudSimTags.VM_CREATE_ACK:
                processVmCreate(ev, true);
                break;

            case CloudSimTags.VM_DESTROY:
                processVmDestroy(ev, false);
                break;

            case CloudSimTags.VM_DESTROY_ACK:
                processVmDestroy(ev, true);
                break;

            case CloudSimTags.VM_MIGRATE:
                processVmMigrate(ev, false);
                break;

            case CloudSimTags.VM_MIGRATE_ACK:
                processVmMigrate(ev, true);
                break;

            case CloudSimTags.VM_DATA_ADD:
                processDataAdd(ev, false);
                break;

            case CloudSimTags.VM_DATA_ADD_ACK:
                processDataAdd(ev, true);
                break;

            case CloudSimTags.VM_DATA_DEL:
                processDataDelete(ev, false);
                break;

            case CloudSimTags.VM_DATA_DEL_ACK:
                processDataDelete(ev, true);
                break;

            case CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT:
                updateCloudletProcessing();
                checkCloudletsCompletionForAllHosts();
                break;

            // other unknown tags are processed by this method
            default:
                processOtherEvent(ev);
                break;
        }
    }

    /**
     * Process a file deletion request.
     *
     * @param ev information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     */
    protected void processDataDelete(SimEvent ev, boolean ack) {
        if (ev == null) {
            return;
        }

        Object[] data = (Object[]) ev.getData();
        if (data == null) {
            return;
        }

        String filename = (String) data[0];
        int req_source = ((Integer) data[1]);
        int tag;

        // check if this file can be deleted (do not delete is right now)
        int msg = deleteFileFromStorage(filename);
        if (msg == DataCloudTags.FILE_DELETE_SUCCESSFUL) {
            tag = DataCloudTags.CTLG_DELETE_MASTER;
        } else { // if an error occured, notify user
            tag = DataCloudTags.FILE_DELETE_MASTER_RESULT;
        }

        if (ack) {
            // send back to sender
            Object pack[] = new Object[2];
            pack[0] = filename;
            pack[1] = msg;

            sendNow(req_source, tag, pack);
        }
    }

    /**
     * Process a file inclusion request.
     *
     * @param ev information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     */
    protected void processDataAdd(SimEvent ev, boolean ack) {
        if (ev == null) {
            return;
        }

        Object[] pack = (Object[]) ev.getData();
        if (pack == null) {
            return;
        }

        File file = (File) pack[0]; // get the file
        file.setMasterCopy(true); // set the file into a master copy
        int sentFrom = ((Integer) pack[1]); // get sender ID

        Object[] data = new Object[3];
        data[0] = file.getName();

        int msg = addFile(file); // add the file

        if (ack) {
            data[1] = -1; // no sender id
            data[2] = msg; // the result of adding a master file
            sendNow(sentFrom, DataCloudTags.FILE_ADD_MASTER_RESULT, data);
        }
    }

    /**
     * Processes a ping request.
     *
     * @param ev information about the event just happened
     *
     * @pre ev != null
     * @post $none
     */
    protected void processPingRequest(SimEvent ev) {
        InfoPacket pkt = (InfoPacket) ev.getData();
        pkt.setTag(CloudSimTags.INFOPKT_RETURN);
        pkt.setDestId(pkt.getSrcId());

        // sends back to the sender
        sendNow(pkt.getSrcId(), CloudSimTags.INFOPKT_RETURN, pkt);
    }

    /**
     * Process the event for an User/Broker who wants to know the status of a
     * Cloudlet. This DatacenterSimple will then send the status back to the
     * User/Broker.
     *
     * @param ev information about the event just happened
     *
     * @pre ev != null
     * @post $none
     */
    protected void processCloudletStatus(SimEvent ev) {
        int cloudletId = 0;
        int userId = 0;
        int vmId = 0;
        int status = -1;

        try {
            // if a sender using cloudletXXX() methods
            int data[] = (int[]) ev.getData();
            cloudletId = data[0];
            userId = data[1];
            vmId = data[2];

            status = getCloudletStatus(vmId, userId, cloudletId);
        } // if a sender using normal send() methods
        catch (ClassCastException c) {
            try {
                Cloudlet cl = (Cloudlet) ev.getData();
                cloudletId = cl.getId();
                userId = cl.getUserId();

                status = getCloudletStatus(vmId, userId, cloudletId);
            } catch (Exception e) {
                Log.printConcatLine(getName(), ": Error in processing CloudSimTags.CLOUDLET_STATUS");
                Log.printLine(e.getMessage());
                return;
            }
        } catch (Exception e) {
            Log.printConcatLine(getName(), ": Error in processing CloudSimTags.CLOUDLET_STATUS");
            Log.printLine(e.getMessage());
            return;
        }

        int[] array = new int[3];
        array[0] = getId();
        array[1] = cloudletId;
        array[2] = status;

        int tag = CloudSimTags.CLOUDLET_STATUS;
        sendNow(userId, tag, array);
    }

    /**
     * Gets the status of a cloudlet with a given id, owned by a given user and
     * running inside a given VM
     *
     * @param vmId
     * @param userId
     * @param cloudletId
     * @return the cloudlet status
     */
    private int getCloudletStatus(final int vmId, final int userId, final int cloudletId) {
        return getVmAllocationPolicy()
                .getHost(vmId, userId)
                .getVm(vmId, userId)
                .getCloudletScheduler()
                .getCloudletStatus(cloudletId);
    }

    /**
     * Process non-default received events that aren't processed by the
     * {@link #processEvent(org.cloudbus.cloudsim.core.SimEvent)} method. This
     * method should be overridden by subclasses in other to process new defined
     * events.
     *
     * @param ev information about the event just happened
     *
     * @pre $none
     * @post $none
     */
    protected void processOtherEvent(SimEvent ev) {
        if (ev == null) {
            Log.printConcatLine(getName(), ".processOtherEvent(): Error - an event is null.");
        }
    }

    /**
     * Process the event for a Broker which wants to create a VM in this
     * Datacenter. This Datacenter will then send the status back to
     * the Broker.
     *
     * @param ev information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     * @return true if a host was allocated to the VM; false otherwise
     *
     * @pre ev != null
     * @post $none
     */
    protected boolean processVmCreate(SimEvent ev, boolean ack) {
        Vm vm = (Vm) ev.getData();

        boolean hostAllocatedForVm = getVmAllocationPolicy().allocateHostForVm(vm);

        if (ack) {
            int[] data = new int[3];
            data[0] = getId();
            data[1] = vm.getId();
            data[2] = (hostAllocatedForVm ? CloudSimTags.TRUE : CloudSimTags.FALSE);
            send(vm.getUserId(), CloudSim.getMinTimeBetweenEvents(),
                 CloudSimTags.VM_CREATE_ACK, data);
        }

        if (hostAllocatedForVm) {
            getVmList().add(vm);

            if (vm.isBeingInstantiated()) {
                vm.setBeingInstantiated(false);
            }

            vm.updateVmProcessing(CloudSim.clock(),
                    getVmAllocationPolicy()
                    .getHost(vm).getVmScheduler()
                    .getAllocatedMipsForVm(vm));
        }

        return hostAllocatedForVm;
    }

    /**
     * Process the event for an User/Broker who wants to destroy a VM previously
     * created in this DatacenterSimple. This DatacenterSimple may send, upon
     * request, the status back to the User/Broker.
     *
     * @param ev information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     *
     * @pre ev != null
     * @post $none
     */
    protected void processVmDestroy(SimEvent ev, boolean ack) {
        Vm vm = (Vm) ev.getData();
        getVmAllocationPolicy().deallocateHostForVm(vm);

        if (ack) {
            int[] data = new int[3];
            data[0] = getId();
            data[1] = vm.getId();
            data[2] = CloudSimTags.TRUE;

            sendNow(vm.getUserId(), CloudSimTags.VM_DESTROY_ACK, data);
        }

        getVmList().remove(vm);
    }

    /**
     * Process the event for an User/Broker who wants to migrate a VM. This
     * DatacenterSimple will then send the status back to the User/Broker.
     *
     * @param ev information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     *
     * @pre ev != null
     * @post $none
     */
    protected void processVmMigrate(SimEvent ev, boolean ack) {
        if (!(ev.getData() instanceof Map.Entry<?, ?>)) {
            throw new ClassCastException("The data object must be Map.Entry<Vm, Host>");
        }

        Map.Entry<Vm, Host> migrate = (Map.Entry<Vm, Host>) ev.getData();

        Vm vm = migrate.getKey();
        Host host = migrate.getValue();

        getVmAllocationPolicy().deallocateHostForVm(vm);
        host.removeMigratingInVm(vm);
        boolean result = getVmAllocationPolicy().allocateHostForVm(vm, host);
        if (!result) {
            throw new RuntimeException("[Datacenter.processVmMigrate] VM allocation to the destination host failed");
        }

        if (ack) {
            int[] data = new int[3];
            data[0] = getId();
            data[1] = vm.getId();

            if (result) {
                data[2] = CloudSimTags.TRUE;
            } else {
                data[2] = CloudSimTags.FALSE;
            }
            sendNow(ev.getSource(), CloudSimTags.VM_CREATE_ACK, data);
        }

        Log.printFormattedLine(
                "%.2f: Migration of VM #%d to Host #%d is completed",
                CloudSim.clock(), vm.getId(), host.getId());
        vm.setInMigration(false);
    }

    /**
     * Processes a Cloudlet based on the event type.
     *
     * @param ev information about the event just happened
     * @param type event type
     *
     * @pre ev != null
     * @pre type > 0
     * @post $none
     */
    protected void processCloudlet(SimEvent ev, int type) {
        int cloudletId = 0;
        int userId = 0;
        int vmId = 0;

        try { // if the sender using cloudletXXX() methods
            int data[] = (int[]) ev.getData();
            cloudletId = data[0];
            userId = data[1];
            vmId = data[2];
        } // if the sender using normal send() methods
        catch (ClassCastException c) {
            try {
                Cloudlet cl = (Cloudlet) ev.getData();
                cloudletId = cl.getId();
                userId = cl.getUserId();
                vmId = cl.getVmId();
            } catch (Exception e) {
                Log.printConcatLine(super.getName(), ": Error in processing Cloudlet");
                Log.printLine(e.getMessage());
                return;
            }
        } catch (Exception e) {
            Log.printConcatLine(super.getName(), ": Error in processing a Cloudlet.");
            Log.printLine(e.getMessage());
            return;
        }

        // begins executing ....
        switch (type) {
            case CloudSimTags.CLOUDLET_CANCEL:
                processCloudletCancel(cloudletId, userId, vmId);
            break;
            case CloudSimTags.CLOUDLET_PAUSE:
                processCloudletPause(cloudletId, userId, vmId, false);
            break;
            case CloudSimTags.CLOUDLET_PAUSE_ACK:
                processCloudletPause(cloudletId, userId, vmId, true);
            break;
            case CloudSimTags.CLOUDLET_RESUME:
                processCloudletResume(cloudletId, userId, vmId, false);
            break;
            case CloudSimTags.CLOUDLET_RESUME_ACK:
                processCloudletResume(cloudletId, userId, vmId, true);
            break;
        }
    }

    /**
     * Process the event for an User/Broker who wants to move a Cloudlet.
     *
     * @param receivedData information about the migration
     * @param type event type
     *
     * @pre receivedData != null
     * @pre type > 0
     * @post $none
     */
    protected void processCloudletMove(int[] receivedData, int type) {
        updateCloudletProcessing();

        int[] array = receivedData;
        int cloudletId = array[0];
        int userId = array[1];
        int vmId = array[2];
        int vmDestId = array[3];
        int destId = array[4];

        // get the cloudlet
        Cloudlet cl = getVmAllocationPolicy()
                .getHost(vmId, userId)
                .getVm(vmId, userId)
                .getCloudletScheduler()
                .cloudletCancel(cloudletId);

        boolean failed = false;
        if (cl == null) {// cloudlet doesn't exist
            failed = true;
        } else {
            // has the cloudlet already finished?
            if (cl.getStatus() == Cloudlet.Status.SUCCESS) {// if yes, send it back to user
                int[] data = new int[3];
                data[0] = getId();
                data[1] = cloudletId;
                data[2] = 0;
                sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, data);
                sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);
            }

            // prepare cloudlet for migration
            cl.setVmId(vmDestId);

            // The cloudlet will migrate from one vm to another. Does the destination VM exist?
            if (destId == getId()) {
                Vm vm = getVmAllocationPolicy().getHost(vmDestId, userId).getVm(vmDestId, userId);
                if (vm == null) {
                    failed = true;
                } else {
                    // time to transfer the files
                    double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());
                    vm.getCloudletScheduler().cloudletSubmit(cl, fileTransferTime);
                }
            } else {// the cloudlet will migrate from one resource to another
                int tag = ((type == CloudSimTags.CLOUDLET_MOVE_ACK)
                        ? CloudSimTags.CLOUDLET_SUBMIT_ACK
                        : CloudSimTags.CLOUDLET_SUBMIT);
                sendNow(destId, tag, cl);
            }
        }

        if (type == CloudSimTags.CLOUDLET_MOVE_ACK) {// send ACK if requested
            int[] data = new int[3];
            data[0] = getId();
            data[1] = cloudletId;
            if (failed) {
                data[2] = 0;
            } else {
                data[2] = 1;
            }
            sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, data);
        }
    }

    /**
     * Processes the submission of a Cloudlet by a DatacenterBroker.
     *
     * @param ev information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     *
     * @pre ev != null
     * @post $none
     */
    protected void processCloudletSubmit(SimEvent ev, boolean ack) {
        updateCloudletProcessing();

        try {
            // gets the Cloudlet object
            Cloudlet cl = (Cloudlet) ev.getData();
            if (checksIfSubmittedCloudletIsAlreadyFinishedAndNotifyBroker(cl, ack)) {
                return;
            }

            // process this Cloudlet to this Datacenter
            cl.assignCloudletToDatacenter(
                    getId(), getCharacteristics().getCostPerSecond(),
                    getCharacteristics().getCostPerBw());

            submitCloudletToVm(cl, ack);
        } catch (ClassCastException c) {
            Log.printLine(getName() + ".processCloudletSubmit(): " + "ClassCastException error.");
            c.printStackTrace();
        } catch (Exception e) {
            Log.printLine(getName() + ".processCloudletSubmit(): " + "Exception error.");
            e.printStackTrace();
        }

        checkCloudletsCompletionForAllHosts();
    }

    /**
     * Submits a cloudlet to be executed inside its bound VM.
     *
     * @param cl the cloudlet to the executed
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     */
    private void submitCloudletToVm(Cloudlet cl, boolean ack) {
        // time to transfer cloudlet files
        double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

        Host host = getVmAllocationPolicy().getHost(cl.getVmId(), cl.getUserId());
        Vm vm = host.getVm(cl.getVmId(), cl.getUserId());
        CloudletScheduler scheduler = vm.getCloudletScheduler();
        double estimatedFinishTime = scheduler.cloudletSubmit(cl, fileTransferTime);

        // if this cloudlet is in the exec queue
        if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
            send(getId(), estimatedFinishTime, CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }

        sendCloudletSubmitAckToBroker(ack, cl, CloudSimTags.TRUE);
    }

    /**
     * Checks if a submitted cloudlet has already finished.
     * If it is the case, the Datacenter notifies the Broker that
     * the Cloudlet cannot be created again because it has already finished.
     *
     * @param cl the submitted cloudlet
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     * @return true if the submitted cloudlet has already finished, indicating
     * it can be created again; false otherwise
     */
    private boolean checksIfSubmittedCloudletIsAlreadyFinishedAndNotifyBroker(Cloudlet cl, boolean ack) {
        if(!cl.isFinished()){
            return false;
        }

        String name = CloudSim.getEntityName(cl.getUserId());
        Log.printConcatLine(
                getName(), ": Warning - Cloudlet #", cl.getId(), " owned by ", name,
                " is already completed/finished.");
        Log.printLine("Therefore, it is not being executed again");
        Log.printLine();

        /*
         NOTE: If a Cloudlet has finished, then it won't be processed.
         So, if ack is required, this method sends back a result.
         If ack is not required, this method don't send back a result.
         Hence, this might cause CloudSim to be hanged since waiting
         for this Cloudlet back.
        */
        sendCloudletSubmitAckToBroker(ack, cl,  CloudSimTags.FALSE);

        sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);
        return true;
    }

    /**
     * Sends an ACK to the DatacenterBroker that submitted the Cloudlet for execution
     * in order to respond the reception of the submission request,
     * informing if the cloudlet was created or not.
     *
     * The ACK is sent just if the Broker is waiting for it and that condition
     * is indicated in the ack parameter.
     *
     * @oaram ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     * @param cl the cloudlet to respond to DatacenterBroker if it was created or not
     * @param cloudletCreated indicates if the cloudlet was successfully created
     * by the Datacenter, according to the {@link CloudSimTags#TRUE} or
     * {@link CloudSimTags#FALSE} tags.
     */
    private void sendCloudletSubmitAckToBroker(boolean ack, Cloudlet cl, final int cloudletCreated) {
        if(!ack){
            return;
        }

        int[] data = new int[3];
        data[0] = getId();
        data[1] = cl.getId();
        data[2] = cloudletCreated;

        sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, data);
    }
    /**
     * Predict the total time to transfer a list of files.
     *
     * @param requiredFiles the files to be transferred
     * @return the predicted time
     */
    protected double predictFileTransferTime(List<String> requiredFiles) {
        double time = 0.0;

        for (String fileName: requiredFiles) {
            for (FileStorage storageDevice: getStorageList()) {
                File file = storageDevice.getFile(fileName);
                if (file != null) {
                    time += file.getSize() / storageDevice.getMaxTransferRate();
                    break;
                }
            }
        }

        return time;
    }

    /**
     * Processes a Cloudlet resume request.
     *
     * @param cloudletId ID of the cloudlet to be resumed
     * @param userId ID of the cloudlet's owner
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     * @param vmId the id of the VM where the cloudlet has to be resumed
     *
     * @pre $none
     * @post $none
     */
    protected void processCloudletResume(int cloudletId, int userId, int vmId, boolean ack) {
        double eventTime = getVmAllocationPolicy().getHost(vmId, userId).getVm(vmId, userId)
                .getCloudletScheduler().cloudletResume(cloudletId);

        boolean status = false;
        if (eventTime > 0.0) { // if this cloudlet is in the exec queue
            status = true;
            if (eventTime > CloudSim.clock()) {
                schedule(getId(), eventTime, CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
            }
        }

        if (ack) {
            int[] data = new int[3];
            data[0] = getId();
            data[1] = cloudletId;
            if (status) {
                data[2] = CloudSimTags.TRUE;
            } else {
                data[2] = CloudSimTags.FALSE;
            }
            sendNow(userId, CloudSimTags.CLOUDLET_RESUME_ACK, data);
        }
    }

    /**
     * Processes a Cloudlet pause request.
     *
     * @param cloudletId ID of the cloudlet to be paused
     * @param userId ID of the cloudlet's owner
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     * @param vmId the id of the VM where the cloudlet has to be paused
     *
     * @pre $none
     * @post $none
     */
    protected void processCloudletPause(int cloudletId, int userId, int vmId, boolean ack) {
        boolean status = getVmAllocationPolicy().getHost(vmId, userId).getVm(vmId, userId)
                .getCloudletScheduler().cloudletPause(cloudletId);

        if (ack) {
            int[] data = new int[3];
            data[0] = getId();
            data[1] = cloudletId;
            if (status) {
                data[2] = CloudSimTags.TRUE;
            } else {
                data[2] = CloudSimTags.FALSE;
            }
            sendNow(userId, CloudSimTags.CLOUDLET_PAUSE_ACK, data);
        }
    }

    /**
     * Processes a Cloudlet cancel request.
     *
     * @param cloudletId ID of the cloudlet to be canceled
     * @param userId ID of the cloudlet's owner
     * @param vmId the id of the VM where the cloudlet has to be canceled
     *
     * @pre $none
     * @post $none
     */
    protected void processCloudletCancel(int cloudletId, int userId, int vmId) {
        Cloudlet cl = getVmAllocationPolicy().getHost(vmId, userId).getVm(vmId, userId)
                .getCloudletScheduler().cloudletCancel(cloudletId);
        sendNow(userId, CloudSimTags.CLOUDLET_CANCEL, cl);
    }

    /**
     * Updates processing of each cloudlet running in this DatacenterSimple
     * and schedules the next processing update.
     * It is necessary because Hosts and VMs are simple objects, not
     * entities. So, they don't receive events and updating cloudlets inside
     * them must be called from the outside.
     *
     * @pre $none
     * @post $none
     */
    protected void updateCloudletProcessing() {
        if (!isTimeToUpdateCloudletsProcessing())
            return;

        double delay = delayToUpdateCloudletProcessing();
        if (delay != Double.MAX_VALUE) {
            schedule(getId(), delay, CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }
        setLastProcessTime(CloudSim.clock());
    }

    protected boolean isTimeToUpdateCloudletsProcessing() {
        // if some time passed since last processing
        // R: for term is to allow loop at simulation start. Otherwise, one initial
        // simulation step is skipped and schedulers are not properly initialized
        return CloudSim.clock() < 0.111 ||
               CloudSim.clock() > getLastProcessTime() + CloudSim.getMinTimeBetweenEvents();
    }

    /**
     * Gets the time that one next cloudlet will finish executing on the list of
     * datacenter's hosts.
     *
     * @return the time that one next cloudlet will finish executing or
     * {@link Double#MAX_VALUE} if there isn't any cloudlet running.
     */
    protected double completionTimeOfNextFinishingCloudlet() {
        List<? extends Host> list = getVmAllocationPolicy().getHostList();
        double completionTimeOfNextFinishingCloudlet = Double.MAX_VALUE;
        for (Host host : list) {
            // inform VMs to update processing
            double time = host.updateVmsProcessing(CloudSim.clock());
            // what time do we expect that the next cloudlet will finish?
            completionTimeOfNextFinishingCloudlet = Math.min(time, completionTimeOfNextFinishingCloudlet);
        }

        // gurantees a minimal interval before scheduling the event
        if (completionTimeOfNextFinishingCloudlet < CloudSim.clock()+CloudSim.getMinTimeBetweenEvents()+0.01) {
            completionTimeOfNextFinishingCloudlet = CloudSim.clock()+CloudSim.getMinTimeBetweenEvents()+0.01;
        }
        return completionTimeOfNextFinishingCloudlet;
    }

    /**
     * Gets the time to wait before updating the processing of running
     * cloudlets.
     *
     * @return the cloudlet's processing delay or {@link Double#MAX_VALUE} if
     * there isn't any cloudlet running.
     *
     * @see #updateCloudletProcessing()
     */
    protected double delayToUpdateCloudletProcessing() {
        double completionTimeOfNextFinishingCloudlet = completionTimeOfNextFinishingCloudlet();
        if (completionTimeOfNextFinishingCloudlet == Double.MAX_VALUE) {
            return completionTimeOfNextFinishingCloudlet;
        }

        return getSchedulingInterval() > 0
                ? getSchedulingInterval()
                : completionTimeOfNextFinishingCloudlet - CloudSim.clock();
    }

    /**
     * Verifies if some cloudlet inside the hosts of this Datacenter have already finished.
     * If yes, send them to the User/Broker
     *
     * @pre $none
     * @post $none
     */
    protected void checkCloudletsCompletionForAllHosts() {
        List<? extends Host> list = getVmAllocationPolicy().getHostList();
        list.forEach(host -> checkCloudletsCompletionForGivenHost(host));
    }

    protected void checkCloudletsCompletionForGivenHost(Host host) {
        host.getVmList().forEach(vm -> checkCloudletsCompletionForGivenVm(vm));
    }

    public void checkCloudletsCompletionForGivenVm(Vm vm) {
        while (vm.getCloudletScheduler().hasFinishedCloudlets()) {
            Cloudlet cl = vm.getCloudletScheduler().getNextFinishedCloudlet();
            if (cl != Cloudlet.NULL) {
                sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);
            }
        }
    }

    @Override
    public int addFile(File file) {
        if (file == null) {
            return DataCloudTags.FILE_ADD_ERROR_EMPTY;
        }

        if (contains(file.getName())) {
            return DataCloudTags.FILE_ADD_ERROR_EXIST_READ_ONLY;
        }

        // check storage space first
        if (getStorageList().isEmpty()) {
            return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
        }

        int msg = DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;

        for (FileStorage storage : getStorageList()) {
            if (storage.isResourceAmountAvailable((long) file.getSize())) {
                storage.addFile(file);
                return DataCloudTags.FILE_ADD_SUCCESSFUL;
            }
        }

        return msg;
    }

    /**
     * Checks whether the datacenter has the given file.
     *
     * @param file a file to be searched
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    protected boolean contains(File file) {
        if (file == null) {
            return false;
        }
        return contains(file.getName());
    }

    /**
     * Checks whether the datacenter has the given file.
     *
     * @param fileName a file name to be searched
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    protected boolean contains(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return false;
        }

        for (FileStorage storage : getStorageList()) {
            if (storage.contains(fileName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Deletes the file from the storage. Also, check whether it is possible to
     * delete the file from the storage.
     *
     * @param fileName the name of the file to be deleted
     * @return the tag denoting the status of the operation, either
     * {@link DataCloudTags#FILE_DELETE_ERROR} or
     * {@link DataCloudTags#FILE_DELETE_SUCCESSFUL}
     */
    private int deleteFileFromStorage(String fileName) {
        int msg = DataCloudTags.FILE_DELETE_ERROR;

        for (FileStorage storage : getStorageList()) {
            storage.deleteFile(fileName);
            msg = DataCloudTags.FILE_DELETE_SUCCESSFUL;
        }

        return msg;
    }

    @Override
    public void shutdownEntity() {
        Log.printConcatLine(getName(), " is shutting down...");
    }

    @Override
    public void startEntity() {
        Log.printConcatLine(getName(), " is starting...");
        // this resource should register to regional CIS.
        // However, if not specified, then register to system CIS (the
        // default CloudInformationService) entity.
        int cisID = CloudSim.getEntityId(regionalCisName);
        if (cisID == -1) {
            cisID = CloudSim.getCloudInfoServiceEntityId();
        }

        // send the registration to CIS
        sendNow(cisID, CloudSimTags.DATACENTER_REGISTRATION_REQUEST, getId());
        // Below method is for a child class to override
        registerOtherEntity();
    }

    @Override
    public <T extends Host> List<T> getHostList() {
        return getCharacteristics().getHostList();
    }

    @Override
    public DatacenterCharacteristics getCharacteristics() {
        return characteristics;
    }

    /**
     * Sets the datacenter characteristics.
     *
     * @param characteristics the new datacenter characteristics
     */
    protected final void setCharacteristics(DatacenterCharacteristics characteristics) {
        characteristics.setDatacenter(this);
        this.characteristics = characteristics;
    }

    /**
     * Gets the regional Cloud Information Service (CIS) name.
     *
     * @return the regional CIS name
     * @see org.cloudbus.cloudsim.core.CloudInformationService
     */
    protected String getRegionalCisName() {
        return regionalCisName;
    }

    /**
     * Sets the regional Cloud Information Service (CIS) name.
     *
     * @param regionalCisName the new regional CIS name
     */
    protected void setRegionalCisName(String regionalCisName) {
        this.regionalCisName = regionalCisName;
    }

    @Override
    public VmAllocationPolicy getVmAllocationPolicy() {
        return vmAllocationPolicy;
    }

    /**
     * Sets the policy to be used by the datacenter to allocate VMs into hosts.
     *
     * @param vmAllocationPolicy the new vm allocation policy
     */
    protected final void setVmAllocationPolicy(VmAllocationPolicy vmAllocationPolicy) {
        this.vmAllocationPolicy = vmAllocationPolicy;
    }

    /**
     * Gets the last time some cloudlet was processed in the datacenter.
     *
     * @return the last process time
     */
    protected double getLastProcessTime() {
        return lastProcessTime;
    }

    /**
     * Sets the last time some cloudlet was processed in the datacenter.
     *
     * @param lastProcessTime the new last process time
     */
    protected final void setLastProcessTime(double lastProcessTime) {
        this.lastProcessTime = lastProcessTime;
    }

    /**
     * Gets the list of storage devices of the datacenter.
     *
     * @return the storage list
     */
    protected List<FileStorage> getStorageList() {
        return storageList;
    }

    /**
     * Sets the list of storage devices of the datacenter.
     *
     * @param storageList the new storage list
     */
    protected final void setStorageList(List<FileStorage> storageList) {
        this.storageList = storageList;
    }

    @Override
    public <T extends Vm> List<T> getVmList() {
        return (List<T>) vmList;
    }

    /**
     * Sets the list of VMs submitted to be ran in some host of this datacenter.
     *
     * @param <T>
     * @param vmList the new vm list
     */
    protected final <T extends Vm> void setVmList(List<T> vmList) {
        this.vmList = vmList;
    }

    @Override
    public double getSchedulingInterval() {
        return schedulingInterval;
    }

    /**
     * Sets the scheduling delay to process each event received by the
     * datacenter (in seconds).
     *
     * @param schedulingInterval the new scheduling interval
     */
    protected final void setSchedulingInterval(double schedulingInterval) {
        this.schedulingInterval = schedulingInterval;
    }

    @Override
    public Host getHost(int index) {
        if (index >= 0 && index < getHostList().size()) {
            return getHostList().get(index);
        }

        return Host.NULL;
    }


}
