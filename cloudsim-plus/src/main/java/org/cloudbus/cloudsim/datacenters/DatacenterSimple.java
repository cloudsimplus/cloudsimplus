/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import org.apache.commons.lang3.BooleanUtils;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.network.IcmpPacket;
import org.cloudbus.cloudsim.util.DataCloudTags;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.*;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;

import java.util.*;

import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudsimplus.autoscaling.VerticalVmScaling;

import static java.util.stream.Collectors.toList;

/**
 * Implements the basic features of a Virtualized Cloud Datacenter. It deals
 * with processing of VM queries (i.e., handling of VMs) instead of processing
 * Cloudlet-related queries.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterSimple extends CloudSimEntity implements Datacenter {
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

    /** @see #getSchedulingInterval() */
    private double schedulingInterval;

    /**
     * Creates a Datacenter with the given parameters.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the characteristics of the Datacenter to be created
     * @param storageList a List of storage elements, for data simulation
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @param schedulingInterval the scheduling interval to process each
     * Datacenter received event (in seconds)
     * @throws IllegalArgumentException when this entity has <tt>zero</tt> number of PEs (Processing Elements).
     * No PEs mean the Cloudlets can't be processed. A CloudResource must
     * contain one or more Machines. A Machine must contain one or more PEs.
     *
     * @post $none
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public DatacenterSimple(
        Simulation simulation,
        DatacenterCharacteristics characteristics,
        VmAllocationPolicy vmAllocationPolicy,
        List<FileStorage> storageList,
        double schedulingInterval)
    {
        this(simulation, characteristics, vmAllocationPolicy);
        setStorageList(storageList);
        setSchedulingInterval(schedulingInterval);
    }

    /**
     * Creates a Datacenter.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the characteristics of the Datacenter to be created
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @throws IllegalArgumentException when this entity has <tt>zero</tt> number of PEs (Processing Elements).
     * <br>
     * No PEs mean the Cloudlets can't be processed. A CloudResource must
     * contain one or more Machines. A Machine must contain one or more PEs.
     *
     * @post $none
     */
    public DatacenterSimple(
        Simulation simulation,
        DatacenterCharacteristics characteristics,
        VmAllocationPolicy vmAllocationPolicy)
    {
        super(simulation);

        // If this resource doesn't have any PEs then it isn't useful at all
        if (characteristics.getNumberOfPes() == 0) {
            throw new IllegalArgumentException(super.getName()
                + " : Error - this entity has no PEs. Therefore, can't process any Cloudlets.");
        }

        // DatacenterCharacteristics stores the id of the Datacenter
        setCharacteristics(characteristics);
        setSimulationInstanceForHosts(characteristics);
        setVmAllocationPolicy(vmAllocationPolicy);
        setLastProcessTime(0.0);
        setSchedulingInterval(0);
        setStorageList(new ArrayList<>());
        assignHostsToCurrentDatacenter();
    }

    private void setSimulationInstanceForHosts(DatacenterCharacteristics characteristics) {
        characteristics.getHostList().forEach(host -> host.setSimulation(getSimulation()));
    }

    private void assignHostsToCurrentDatacenter() {
        characteristics.getHostList().forEach(host -> host.setDatacenter(this));
    }

    @Override
    public void processEvent(SimEvent ev) {
        int processed = 0;
        processed += processCloudletEvents(ev);
        processed += processVmEvents(ev);
        processed += processDatacenterEvents(ev);
        processed += processNetworkEvents(ev);

        if(processed == 0){
            processOtherEvent(ev);
        }
    }

    private int processNetworkEvents(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.ICMP_PKT_SUBMIT:
                processPingRequest(ev);
                return 1;
        }

        return 0;
    }

    private int processDatacenterEvents(SimEvent ev) {
        int srcId;// Resource dynamic info inquiry
        switch (ev.getTag()) {
            case CloudSimTags.RESOURCE_DYNAMICS:
                srcId = (Integer) ev.getData();
                sendNow(srcId, ev.getTag(), 0);
                return 1;

            case CloudSimTags.RESOURCE_NUM_PE:
                srcId = (Integer) ev.getData();
                sendNow(srcId, ev.getTag(), getCharacteristics().getNumberOfPes());
                return 1;

            case CloudSimTags.RESOURCE_NUM_FREE_PE:
                srcId = (Integer) ev.getData();
                sendNow(srcId, ev.getTag(), getCharacteristics().getNumberOfFreePes());
                return 1;
        }

        return 0;
    }

    /**
     * Process a received event.
     * @param ev the event to be processed
     * @return 1 if the event was processed, 0 otherwise
     */
    private int processVmEvents(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.VM_CREATE:
                processVmCreate(ev, false);
                return 1;
            case CloudSimTags.VM_CREATE_ACK:
                processVmCreate(ev, true);
                return 1;
            case CloudSimTags.VM_VERTICAL_SCALING:
                return BooleanUtils.toInteger(requestVmVerticalScaling(ev));
            case CloudSimTags.VM_DESTROY:
                processVmDestroy(ev, false);
                return 1;
            case CloudSimTags.VM_DESTROY_ACK:
                processVmDestroy(ev, true);
                return 1;
            case CloudSimTags.VM_MIGRATE:
                processVmMigrate(ev, false);
                return 1;
            case CloudSimTags.VM_MIGRATE_ACK:
                processVmMigrate(ev, true);
                return 1;
            case CloudSimTags.VM_DATA_ADD:
                processDataAdd(ev, false);
                return 1;
            case CloudSimTags.VM_DATA_ADD_ACK:
                processDataAdd(ev, true);
                return 1;
            case CloudSimTags.VM_DATA_DEL:
                processDataDelete(ev, false);
                return 1;
            case CloudSimTags.VM_DATA_DEL_ACK:
                processDataDelete(ev, true);
                return 1;
            case CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT:
                updateCloudletProcessing();
                checkCloudletsCompletionForAllHosts();
                return 1;
        }

        return 0;
    }

    /**
     * Process a {@link CloudSimTags#VM_VERTICAL_SCALING} request, trying to scale
     * a Vm resource.
     *
     * @param ev the received  {@link CloudSimTags#VM_VERTICAL_SCALING} event
     * @return true if the Vm was scaled, false otherwise
     */
    private boolean requestVmVerticalScaling(SimEvent ev) {
        if(!(ev.getData() instanceof VerticalVmScaling)){
            return false;
        }

        return vmAllocationPolicy.scaleVmVertically((VerticalVmScaling)ev.getData());
    }

    private int processCloudletEvents(SimEvent ev) {
        switch (ev.getTag()) {
            // New Cloudlet arrives
            case CloudSimTags.CLOUDLET_SUBMIT:
                processCloudletSubmit(ev, false);
                return 1;

            // New Cloudlet arrives, but the sender asks for an ack
            case CloudSimTags.CLOUDLET_SUBMIT_ACK:
                processCloudletSubmit(ev, true);
                return 1;

            // Cancels a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_CANCEL:
                processCloudlet(ev, CloudSimTags.CLOUDLET_CANCEL);
                return 1;

            // Pauses a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_PAUSE:
                processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE);
                return 1;

            // Pauses a previously submitted Cloudlet, but the sender
            // asks for an acknowledgement
            case CloudSimTags.CLOUDLET_PAUSE_ACK:
                processCloudlet(ev, CloudSimTags.CLOUDLET_PAUSE_ACK);
                return 1;

            // Resumes a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_RESUME:
                processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME);
                return 1;

            // Resumes a previously submitted Cloudlet, but the sender
            // asks for an acknowledgement
            case CloudSimTags.CLOUDLET_RESUME_ACK:
                processCloudlet(ev, CloudSimTags.CLOUDLET_RESUME_ACK);
                return 1;

            // Moves a previously submitted Cloudlet to a different Datacenter
            case CloudSimTags.CLOUDLET_MOVE:
                processCloudletMove((Object[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE);
                return 1;

            // Moves a previously submitted Cloudlet to a different Datacenter
            case CloudSimTags.CLOUDLET_MOVE_ACK:
                processCloudletMove((Object[]) ev.getData(), CloudSimTags.CLOUDLET_MOVE_ACK);
                return 1;
        }

        return 0;
    }

    /**
     * Process a file deletion request.
     *
     * @param ev information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     */
    protected void processDataDelete(SimEvent ev, boolean ack) {
        if (Objects.isNull(ev)) {
            return;
        }

        final Object[] data = (Object[]) ev.getData();
        if (Objects.isNull(data)) {
            return;
        }

        final String filename = (String) data[0];
        final int reqSource = (Integer) data[1];
        int tag;

        // check if this file can be deleted (do not delete is right now)
        final int msg = deleteFileFromStorage(filename);
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

            sendNow(reqSource, tag, pack);
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
        if (Objects.isNull(ev)) {
            return;
        }

        final Object[] pack = (Object[]) ev.getData();
        if (Objects.isNull(pack)) {
            return;
        }

        final File file = (File) pack[0]; // get the file
        file.setMasterCopy(true); // set the file into a master copy
        final int sentFrom = (Integer) pack[1]; // get sender ID

        final Object[] data = new Object[3];
        data[0] = file.getName();

        final int msg = addFile(file); // add the file

        if (ack) {
            data[1] = -1; // no sender id
            data[2] = msg; // the result of adding a master file
            sendNow(sentFrom, DataCloudTags.MASTERFILE_ADD_RESULT, data);
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
        final IcmpPacket pkt = (IcmpPacket) ev.getData();
        pkt.setTag(CloudSimTags.ICMP_PKT_RETURN);
        pkt.setDestination(pkt.getSource());

        // returns the packet to the sender
        sendNow(pkt.getSource().getId(), CloudSimTags.ICMP_PKT_RETURN, pkt);
    }

    /**
     * Process non-default received events that aren't processed by the
     * {@link #processEvent(SimEvent)} method. This
     * method should be overridden by subclasses in other to process new defined
     * events.
     *
     * @param ev information about the event just happened
     *
     * @pre $none
     * @post $none
     */
    protected void processOtherEvent(SimEvent ev) {
        if (Objects.isNull(ev)) {
            Log.printConcatLine(getName(), ".processOtherEvent(): Error - an event is null.");
        }
    }

    /**
     * Process the event for a Broker which wants to create a VM in this
     * Datacenter. This Datacenter will then send the status back to
     * the Broker.
     *
     * @param ev information about the event just happened
     * @param ackRequested indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     * @return true if a host was allocated to the VM; false otherwise
     *
     * @pre ev != null
     * @post $none
     */
    protected boolean processVmCreate(SimEvent ev, boolean ackRequested) {
        final Vm vm = (Vm) ev.getData();

        final boolean hostAllocatedForVm = vmAllocationPolicy.allocateHostForVm(vm);

        if (ackRequested) {
            send(vm.getBroker().getId(), getSimulation().getMinTimeBetweenEvents(), CloudSimTags.VM_CREATE_ACK, vm);
        }

        if (hostAllocatedForVm) {
            if (!vm.isCreated()) {
                vm.setCreated(true);
            }

            final List<Double> mipsList = vm.getHost().getVmScheduler().getAllocatedMips(vm);
            vm.updateProcessing(getSimulation().clock(), mipsList);
        }

        return hostAllocatedForVm;
    }

    /**
     * Process the event sent by a Broker, requesting the destruction of a given VM
     * created in this Datacenter. This Datacenter may send, upon
     * request, the status back to the Broker.
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
        final int cloudlets = vm.getCloudletScheduler().getCloudletList().size();
        vmAllocationPolicy.deallocateHostForVm(vm);

        if (ack) {
            sendNow(vm.getBroker().getId(), CloudSimTags.VM_DESTROY_ACK, vm);
        }

        final String msg = cloudlets > 0 ?
            String.format("It had a total of %d cloudlets (running + waiting).", cloudlets) :
            "It had no running or waiting cloudlets.";
        Log.printFormatted("%.2f: %s: %s destroyed on %s. %s\n",
                getSimulation().clock(), getClass().getSimpleName(), vm, vm.getHost(), msg);
    }

    /**
     * Process the event from the Datacenter to migrate a VM.
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

        final Map.Entry<Vm, Host> entry = (Map.Entry<Vm, Host>) ev.getData();

        final Vm vm = entry.getKey();
        final Host targetHost = entry.getValue();

        vmAllocationPolicy.deallocateHostForVm(vm);
        targetHost.removeMigratingInVm(vm);
        final boolean result = vmAllocationPolicy.allocateHostForVm(vm, targetHost);

        if (ack) {
            sendNow(ev.getSource(), CloudSimTags.VM_CREATE_ACK, vm);
        }

        vm.setInMigration(false);
        if (result) {
            Log.printFormattedLine(
                "%.2f: Migration of VM #%d to Host #%d is completed",
                getSimulation().clock(), vm.getId(), targetHost.getId());
        } else {
            Log.printFormattedLine("[Datacenter] VM %d allocation to the destination host failed!", vm.getId());

        }
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
        Cloudlet cloudlet;
        try {
            cloudlet = (Cloudlet) ev.getData();
        }
        catch (ClassCastException e) {
            Log.printConcatLine(super.getName(), ": Error in processing Cloudlet");
            Log.printLine(e.getMessage());
            return;
        }

        // begins executing ....
        switch (type) {
            case CloudSimTags.CLOUDLET_CANCEL:
                processCloudletCancel(cloudlet);
            break;
            case CloudSimTags.CLOUDLET_PAUSE:
                processCloudletPause(cloudlet, false);
            break;
            case CloudSimTags.CLOUDLET_PAUSE_ACK:
                processCloudletPause(cloudlet, true);
            break;
            case CloudSimTags.CLOUDLET_RESUME:
                processCloudletResume(cloudlet, false);
            break;
            case CloudSimTags.CLOUDLET_RESUME_ACK:
                processCloudletResume(cloudlet, true);
            break;
        }
    }

    /**
     * Process the event for an User/Broker who wants to move a Cloudlet.
     *
     * @param receivedData an Object array containing data about the migration,
     *                     where the index 0 will be a Cloudlet and
     *                     the index 1 will be the id of the destination VM
     * @param type event type
     *
     * @pre receivedData != null
     * @pre type > 0
     * @post $none
     */
    protected void processCloudletMove(Object[] receivedData, int type) {
        updateCloudletProcessing();

        final Cloudlet cloudlet = (Cloudlet)receivedData[0];
        final int destVmId = (int)receivedData[1];

        final Vm sourceVm = cloudlet.getVm();
        final Host sourceHost = sourceVm.getHost();
        final Vm destVm = sourceHost.getVm(destVmId, cloudlet.getBroker().getId());
        final int destDatacenterId = destVm.getHost().getDatacenter().getId();
        final Cloudlet cl = sourceVm.getCloudletScheduler().cloudletCancel(cloudlet.getId());

        if (Cloudlet.NULL.equals(cl)) {
            return;
        }

        // Has the cloudlet already finished?
        if (cl.getStatus() == Cloudlet.Status.SUCCESS) {// if yes, send it back to user
            sendNow(cl.getBroker().getId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, cl);
            sendNow(cl.getBroker().getId(), CloudSimTags.CLOUDLET_RETURN, cl);
        }

        // Prepare cloudlet for migration
        cl.setVm(destVm);

        // Cloudlet will migrate from one Datacenter to another
        if (destDatacenterId != getId()) {
            final int tag = ((type == CloudSimTags.CLOUDLET_MOVE_ACK)
                    ? CloudSimTags.CLOUDLET_SUBMIT_ACK
                    : CloudSimTags.CLOUDLET_SUBMIT);
            sendNow(destDatacenterId, tag, cl);
        }
        // Cloudlet will migrate from one vm to another. Does the destination VM exist?
        else if (!Vm.NULL.equals(destVm)) {
            // time to transfer the files
            final double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());
            destVm.getCloudletScheduler().cloudletSubmit(cl, fileTransferTime);
        }

        if (type == CloudSimTags.CLOUDLET_MOVE_ACK) {// send ACK if requested
            sendNow(cl.getBroker().getId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, cloudlet);
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
        final Cloudlet cl = (Cloudlet) ev.getData();
        if (checksIfSubmittedCloudletIsAlreadyFinishedAndNotifyBroker(cl, ack)) {
            return;
        }

        // process this Cloudlet to this Datacenter
        cl.assignToDatacenter(this);
        submitCloudletToVm(cl, ack);
    }

    /**
     * Submits a cloudlet to be executed inside its bind VM.
     *
     * @param cl the cloudlet to the executed
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     */
    private void submitCloudletToVm(Cloudlet cl, boolean ack) {
        // time to transfer cloudlet files
        final double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

        final CloudletScheduler scheduler = cl.getVm().getCloudletScheduler();
        final double estimatedFinishTime = scheduler.cloudletSubmit(cl, fileTransferTime);

        // if this cloudlet is in the exec queue
        if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
            send(getId(),
                getCloudletProcessingUpdateInterval(estimatedFinishTime),
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }

        sendCloudletSubmitAckToBroker(ack, cl, true);
    }

    /**
     * Gets the time when the next update of cloudlets has to be performed.
     * This is the minimum value between the {@link #getSchedulingInterval()} and the given time
     * (if the scheduling interval is enable, i.e. if it's greater than 0),
     * which represents when the next update of Cloudlets processing
     * has to be performed.
     *
     * @param nextFinishingCloudletTime the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     * @return next time cloudlets processing will be updated
     *
     * @see #updateCloudletProcessing()
     */
    protected double getCloudletProcessingUpdateInterval(double nextFinishingCloudletTime){
        return (schedulingInterval == 0 ?
            nextFinishingCloudletTime :
            Math.min(nextFinishingCloudletTime, schedulingInterval));
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

        final String name = getSimulation().getEntityName(cl.getBroker().getId());
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
        sendCloudletSubmitAckToBroker(ack, cl,  false);

        sendNow(cl.getBroker().getId(), CloudSimTags.CLOUDLET_RETURN, cl);
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
     * by the Datacenter or not
     */
    private void sendCloudletSubmitAckToBroker(boolean ack, Cloudlet cl, final boolean cloudletCreated) {
        if(!ack){
            return;
        }

        sendNow(cl.getBroker().getId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, cl);
    }
    /**
     * Predict the total time to transfer a list of files.
     *
     * @param requiredFiles the files to be transferred
     * @return the predicted time
     */
    protected double predictFileTransferTime(List<String> requiredFiles) {
        double time = 0.0;

        for (final String fileName: requiredFiles) {
            for (final FileStorage storage: getStorageList()) {
                final File file = storage.getFile(fileName);
                if (file != null) {
                    time += file.getSize() / storage.getMaxTransferRate();
                    break;
                }
            }
        }

        return time;
    }

    /**
     * Processes a Cloudlet resume request.
     *
     * @param cloudlet cloudlet to be resumed
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     * @pre $none
     * @post $none
     */
    protected void processCloudletResume(Cloudlet cloudlet, boolean ack) {
        final double estimatedFinishTime = cloudlet.getVm()
                .getCloudletScheduler().cloudletResume(cloudlet.getId());

        if (estimatedFinishTime > 0.0) { // if this cloudlet is in the exec queue
            if (estimatedFinishTime > getSimulation().clock()) {
                schedule(getId(),
                    getCloudletProcessingUpdateInterval(estimatedFinishTime),
                    CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
            }
        }

        if (ack) {
            sendNow(cloudlet.getBroker().getId(), CloudSimTags.CLOUDLET_RESUME_ACK, cloudlet);
        }
    }

    /**
     * Processes a Cloudlet pause request.
     *
     * @param cloudlet cloudlet to be paused
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     * @pre $none
     * @post $none
     */
    protected void processCloudletPause(Cloudlet cloudlet, final boolean ack) {
        cloudlet.getVm().getCloudletScheduler().cloudletPause(cloudlet.getId());

        if (ack) {
            sendNow(cloudlet.getBroker().getId(), CloudSimTags.CLOUDLET_PAUSE_ACK, cloudlet);
        }
    }

    /**
     * Processes a Cloudlet cancel request.
     *
     * @param cloudlet cloudlet to be canceled
     * @pre $none
     * @post $none
     */
    protected void processCloudletCancel(Cloudlet cloudlet) {
        cloudlet.getVm().getCloudletScheduler().cloudletCancel(cloudlet.getId());
        sendNow(cloudlet.getBroker().getId(), CloudSimTags.CLOUDLET_CANCEL, cloudlet);
    }

    /**
     * Updates processing of each Host, that fires the update of VMs,
     * which in turn updates cloudlets running in this Datacenter.
     * After that, the method schedules the next processing update.
     * It is necessary because Hosts and VMs are simple objects, not
     * entities. So, they don't receive events and updating cloudlets inside
     * them must be called from the outside.
     *
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     * or it isn't time to update the cloudlets
     * @pre $none
     * @post $none
     */
    protected double updateCloudletProcessing() {
        if (!isTimeToUpdateCloudletsProcessing()){
            return Double.MAX_VALUE;
        }

        double nextSimulationTime = updateHostsProcessing();
        if (nextSimulationTime != Double.MAX_VALUE) {
            nextSimulationTime = getCloudletProcessingUpdateInterval(nextSimulationTime);
            schedule(getId(),
                nextSimulationTime,
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }
        setLastProcessTime(getSimulation().clock());
        return nextSimulationTime;
    }

    protected boolean isTimeToUpdateCloudletsProcessing() {
        // if some time passed since last processing
        // R: for term is to allow loop at simulation start. Otherwise, one initial
        // simulation step is skipped and schedulers are not properly initialized
        return getSimulation().clock() < 0.111 ||
               getSimulation().clock() >= lastProcessTime + getSimulation().getMinTimeBetweenEvents();
    }

    /**
     * Updates the processing of all Hosts, that means
     * that makes the processing of VMs running inside such hosts to be updated.
     * Finally, the processing of Cloudlets running inside such VMs is updated.
     *
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     */
    protected double updateHostsProcessing() {
        double nextSimulationTime = Double.MAX_VALUE;
        for (final Host host : getHostList()) {
            final double time = host.updateProcessing(getSimulation().clock());
            nextSimulationTime = Math.min(time, nextSimulationTime);
        }

        // Guarantees a minimal interval before scheduling the event
        final double minTimeBetweenEvents = getSimulation().getMinTimeBetweenEvents()+0.01;
        nextSimulationTime = Math.max(nextSimulationTime, minTimeBetweenEvents);

        if (nextSimulationTime == Double.MAX_VALUE) {
            return nextSimulationTime;
        }

        return nextSimulationTime;
    }

    /**
     * Verifies if some cloudlet inside the hosts of this Datacenter have already finished.
     * If yes, send them to the User/Broker
     *
     * @pre $none
     * @post $none
     */
    protected void checkCloudletsCompletionForAllHosts() {
        List<? extends Host> hosts = vmAllocationPolicy.getHostList();
        hosts.forEach(this::checkCloudletsCompletionForGivenHost);
    }

    protected void checkCloudletsCompletionForGivenHost(Host host) {
        host.getVmList().forEach(this::checkCloudletsCompletionForGivenVm);
    }

    public void checkCloudletsCompletionForGivenVm(Vm vm) {
        final List<Cloudlet> nonReturnedCloudlets =
            vm.getCloudletScheduler().getCloudletFinishedList().stream()
                .map(CloudletExecutionInfo::getCloudlet)
                .filter(c -> !vm.getCloudletScheduler().isCloudletReturned(c))
                .collect(toList());

        nonReturnedCloudlets.stream().forEach(this::returnFinishedCloudletToBroker);
    }

    /**
     * Notifies the broker about the end of execution of a given Cloudlet,
     * by returning the Cloudlet to it.
     *
     * @param cloudlet the Cloudlet to return to broker in order to notify it about the Cloudlet execution end
     */
    private void returnFinishedCloudletToBroker(Cloudlet cloudlet) {
        sendNow(cloudlet.getBroker().getId(), CloudSimTags.CLOUDLET_RETURN, cloudlet);
        cloudlet.getVm().getCloudletScheduler().addCloudletToReturnedList(cloudlet);
    }

    @Override
    public int addFile(File file) {
        if (Objects.isNull(file)) {
            return DataCloudTags.FILE_ADD_ERROR_EMPTY;
        }

        if (contains(file.getName())) {
            return DataCloudTags.FILE_ADD_ERROR_EXIST_READ_ONLY;
        }

        // check storage space first
        if (getStorageList().isEmpty()) {
            return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
        }

        for (final FileStorage storage : getStorageList()) {
            if (storage.isResourceAmountAvailable((long) file.getSize())) {
                storage.addFile(file);
                return DataCloudTags.FILE_ADD_SUCCESSFUL;
            }
        }

        return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
    }

    /**
     * Checks whether the Datacenter has the given file.
     *
     * @param file a file to be searched
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    protected boolean contains(File file) {
        if (Objects.isNull(file)) {
            return false;
        }
        return contains(file.getName());
    }

    /**
     * Checks whether the Datacenter has the given file.
     *
     * @param fileName a file name to be searched
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    protected boolean contains(String fileName) {
        if (Objects.isNull(fileName) || fileName.isEmpty()) {
            return false;
        }

        return storageList.stream().anyMatch(storage -> storage.contains(fileName));
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
    protected void startEntity() {
        Log.printConcatLine(getName(), " is starting...");
        // this resource should register to regional CIS.
        // However, if not specified, then register to system CIS (the
        // default CloudInformationService) entity.
        int cisID = getSimulation().getEntityId(regionalCisName);
        if (cisID == -1) {
            cisID = getSimulation().getCloudInfoServiceEntityId();
        }

        // send the registration to CIS
        sendNow(cisID, CloudSimTags.DATACENTER_REGISTRATION_REQUEST, this);
    }

    @Override
    public <T extends Host> List<T> getHostList() {
        return characteristics.getHostList();
    }

    @Override
    public DatacenterCharacteristics getCharacteristics() {
        return characteristics;
    }

    /**
     * Sets the Datacenter characteristics.
     *
     * @param characteristics the new Datacenter characteristics
     */
    protected final void setCharacteristics(DatacenterCharacteristics characteristics) {
        characteristics.setDatacenter(this);
        this.characteristics = characteristics;
        Simulation.setIdForEntitiesWithoutOne(characteristics.getHostList());
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
     * Sets the policy to be used by the Datacenter to allocate VMs into hosts.
     *
     * @param vmAllocationPolicy the new vm allocation policy
     */
    protected final Datacenter setVmAllocationPolicy(VmAllocationPolicy vmAllocationPolicy) {
        Objects.requireNonNull(vmAllocationPolicy);
        vmAllocationPolicy.setDatacenter(this);
        this.vmAllocationPolicy = vmAllocationPolicy;
        return this;
    }

    /**
     * Gets the last time some cloudlet was processed in the Datacenter.
     *
     * @return the last process time
     */
    protected double getLastProcessTime() {
        return lastProcessTime;
    }

    /**
     * Sets the last time some cloudlet was processed in the Datacenter.
     *
     * @param lastProcessTime the new last process time
     */
    protected final void setLastProcessTime(double lastProcessTime) {
        this.lastProcessTime = lastProcessTime;
    }

    @Override
    public List<FileStorage> getStorageList() {
        return Collections.unmodifiableList(storageList);
    }

    /**
     * Sets the list of storage devices of the Datacenter.
     *
     * @param storageList the new storage list
     * @return
     */
    @Override
    public final Datacenter setStorageList(List<FileStorage> storageList) {
        if(Objects.isNull(storageList)){
            storageList = new ArrayList<>();
        }

        this.storageList = storageList;
        setAllFilesOfAllStoragesToThisDatacenter();

        return this;
    }

    /**
     * Assigns all files of all storage devices to this Datacenter.
     */
    private void setAllFilesOfAllStoragesToThisDatacenter() {
        storageList.stream()
                .map(FileStorage::getFileList)
                .flatMap(List::stream)
                .forEach(file -> file.setDatacenter(this));
    }

    @Override
    public <T extends Vm> List<T> getVmList() {
        return (List<T>) Collections.unmodifiableList(
                getHostList().stream()
                    .flatMap(h -> h.getVmList().stream())
                    .collect(toList()));
    }

    @Override
    public double getSchedulingInterval() {
        return schedulingInterval;
    }

    @Override
    public final Datacenter setSchedulingInterval(double schedulingInterval) {
        this.schedulingInterval = Math.max(schedulingInterval, 0);
        return this;
    }

    @Override
    public Host getHost(int index) {
        if (index >= 0 && index < getHostList().size()) {
            return getHostList().get(index);
        }

        return Host.NULL;
    }

    @Override
    public String toString() {
        return String.format("Datacenter %d", getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DatacenterSimple that = (DatacenterSimple) o;

        return (!characteristics.equals(that.characteristics));
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + characteristics.hashCode();
        return result;
    }
}
