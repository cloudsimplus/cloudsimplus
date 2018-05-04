/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.core.events.PredicateType;
import org.cloudbus.cloudsim.network.IcmpPacket;
import org.cloudbus.cloudsim.util.Conversion;
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

    /**
     * @see #getBandwidthPercentForMigration()
     */
    private double bandwidthPercentForMigration;

    /**
     * Indicates if migrations are disabled or not.
     */
    private boolean migrationsEnabled;

    /**
     * @see #getPower()
     */
    private double power;

    private List<? extends Host> hostList;

    /** @see #getCharacteristics() */
    private final DatacenterCharacteristics characteristics;

    /** @see #getVmAllocationPolicy() */
    private VmAllocationPolicy vmAllocationPolicy;

    /** @see #getLastProcessTime() */
    private double lastProcessTime;

    /** @see #getStorageList() */
    private List<FileStorage> storageList;

    /** @see #getSchedulingInterval() */
    private double schedulingInterval;

    /**
     * Creates a Datacenter.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @throws IllegalArgumentException when this entity has <tt>zero</tt> number of PEs (Processing Elements).
     * <br>
     * No PEs mean the Cloudlets can't be processed. A CloudResource must
     * contain one or more Machines. A Machine must contain one or more PEs.
     *
     * @post $none
     */
    public DatacenterSimple(
        final Simulation simulation,
        final List<? extends Host> hostList,
        final VmAllocationPolicy vmAllocationPolicy)
    {
        super(simulation);
        setHostList(hostList);

        setLastProcessTime(0.0);
        setSchedulingInterval(0);
        setStorageList(new ArrayList<>());

        this.characteristics = new DatacenterCharacteristicsSimple(this);
        this.bandwidthPercentForMigration = DEF_BANDWIDTH_PERCENT_FOR_MIGRATION;
        migrationsEnabled = true;

        setVmAllocationPolicy(vmAllocationPolicy);
    }

    private void setHostList(final List<? extends Host> hostList) {
        Objects.requireNonNull(hostList);
        this.hostList = hostList;
        setupHosts();
    }

    private void setupHosts() {
        for (final Host host : hostList) {
            host.setDatacenter(this);
            host.setSimulation(getSimulation());
        }

        Simulation.setIdForEntitiesWithoutOne(this.hostList);
    }

    @Override
    public void processEvent(final SimEvent ev) {
        processCloudletEvents(ev);
        processVmEvents(ev);
        processNetworkEvents(ev);
    }

    private void processNetworkEvents(final SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.ICMP_PKT_SUBMIT:
                processPingRequest(ev);
            break;
        }
    }

    /**
     * Process a received event.
     * @param ev the event to be processed
     */
    private void processVmEvents(final SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.VM_CREATE:
                processVmCreate(ev, false);
            break;
            case CloudSimTags.VM_CREATE_ACK:
                processVmCreate(ev, true);
            break;
            case CloudSimTags.VM_VERTICAL_SCALING:
                requestVmVerticalScaling(ev);
            break;
            case CloudSimTags.VM_DESTROY:
                processVmDestroy(ev, false);
            break;
            case CloudSimTags.VM_DESTROY_ACK:
                processVmDestroy(ev, true);
            break;
            case CloudSimTags.VM_MIGRATE:
                finishVmMigration(ev, false);
            break;
            case CloudSimTags.VM_MIGRATE_ACK:
                finishVmMigration(ev, true);
            break;
            case CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT:
                updateCloudletProcessing();
                checkCloudletsCompletionForAllHosts();
            break;
        }
    }

    /**
     * Process a {@link CloudSimTags#VM_VERTICAL_SCALING} request, trying to scale
     * a Vm resource.
     *
     * @param ev the received  {@link CloudSimTags#VM_VERTICAL_SCALING} event
     * @return true if the Vm was scaled, false otherwise
     */
    private boolean requestVmVerticalScaling(final SimEvent ev) {
        if(!(ev.getData() instanceof VerticalVmScaling)){
            return false;
        }

        return vmAllocationPolicy.scaleVmVertically((VerticalVmScaling)ev.getData());
    }

    private void processCloudletEvents(final SimEvent ev) {
        switch (ev.getTag()) {
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
    protected void processPingRequest(final SimEvent ev) {
        final IcmpPacket pkt = (IcmpPacket) ev.getData();
        pkt.setTag(CloudSimTags.ICMP_PKT_RETURN);
        pkt.setDestination(pkt.getSource());

        // returns the packet to the sender
        sendNow(pkt.getSource(), CloudSimTags.ICMP_PKT_RETURN, pkt);
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
    protected void processCloudlet(final SimEvent ev, final int type) {
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
            default:
                Log.printLine(this + ": Unable to handle a request from "
                    + ev.getSource().getName() + " with event tag = " + ev.getTag());

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
    protected void processCloudletSubmit(final SimEvent ev, final boolean ack) {
        final Cloudlet cl = (Cloudlet) ev.getData();
        if (checksIfSubmittedCloudletIsAlreadyFinishedAndNotifyBroker(cl, ack)) {
            return;
        }

        // process this Cloudlet to this Datacenter
        cl.assignToDatacenter(this);
        submitCloudletToVm(cl, ack);
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
    protected void processCloudletResume(final Cloudlet cloudlet, final boolean ack) {
        final double estimatedFinishTime = cloudlet.getVm()
            .getCloudletScheduler().cloudletResume(cloudlet.getId());

        if (estimatedFinishTime > 0.0 && estimatedFinishTime > getSimulation().clock()) {
            schedule(this,
                getCloudletProcessingUpdateInterval(estimatedFinishTime),
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }

        if (ack) {
            sendNow(cloudlet.getBroker(), CloudSimTags.CLOUDLET_RESUME_ACK, cloudlet);
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
    protected void processCloudletPause(final Cloudlet cloudlet, final boolean ack) {
        cloudlet.getVm().getCloudletScheduler().cloudletPause(cloudlet.getId());

        if (ack) {
            sendNow(cloudlet.getBroker(), CloudSimTags.CLOUDLET_PAUSE_ACK, cloudlet);
        }
    }

    /**
     * Processes a Cloudlet cancel request.
     *
     * @param cloudlet cloudlet to be canceled
     * @pre $none
     * @post $none
     */
    protected void processCloudletCancel(final Cloudlet cloudlet) {
        cloudlet.getVm().getCloudletScheduler().cloudletCancel(cloudlet.getId());
        sendNow(cloudlet.getBroker(), CloudSimTags.CLOUDLET_CANCEL, cloudlet);
    }

    /**
     * Submits a cloudlet to be executed inside its bind VM.
     *
     * @param cl the cloudlet to the executed
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     */
    private void submitCloudletToVm(final Cloudlet cl, final boolean ack) {
        // time to transfer cloudlet files
        final double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

        final CloudletScheduler scheduler = cl.getVm().getCloudletScheduler();
        final double estimatedFinishTime = scheduler.cloudletSubmit(cl, fileTransferTime);

        // if this cloudlet is in the exec queue
        if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
            send(this,
                getCloudletProcessingUpdateInterval(estimatedFinishTime),
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }

        sendCloudletSubmitAckToBroker(ack, cl);
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
    protected boolean processVmCreate(final SimEvent ev, final boolean ackRequested) {
        final Vm vm = (Vm) ev.getData();

        final boolean hostAllocatedForVm = vmAllocationPolicy.allocateHostForVm(vm);

        if (ackRequested) {
            send(vm.getBroker(), getSimulation().getMinTimeBetweenEvents(), CloudSimTags.VM_CREATE_ACK, vm);
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
    protected void processVmDestroy(final SimEvent ev, final boolean ack) {
        final Vm vm = (Vm) ev.getData();
        final int cloudlets = vm.getCloudletScheduler().getCloudletList().size();
        vmAllocationPolicy.deallocateHostForVm(vm);

        if (ack) {
            sendNow(vm.getBroker(), CloudSimTags.VM_DESTROY_ACK, vm);
        }

        final String msg = cloudlets > 0 ?
            String.format("It had a total of %d cloudlets (running + waiting).", cloudlets) :
            "It had no running or waiting cloudlets.";
        Log.printFormatted("%.2f: %s: %s destroyed on %s. %s\n",
                getSimulation().clock(), getClass().getSimpleName(), vm, vm.getHost(), msg);
    }

    /**
     * Finishes the process of migrating a VM.
     *
     * @param ev information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     *
     * @pre ev != null
     * @post $none
     */
    protected void finishVmMigration(final SimEvent ev, final boolean ack) {
        if (!(ev.getData() instanceof Map.Entry<?, ?>)) {
            throw new ClassCastException("The data object must be Map.Entry<Vm, Host>");
        }

        final Map.Entry<Vm, Host> entry = (Map.Entry<Vm, Host>) ev.getData();

        final Vm vm = entry.getKey();
        final Host targetHost = entry.getValue();

        //Updates processing of all Hosts to get the latest state for all Hosts before migrating VMs
        updateHostsProcessing();

        //Deallocates the VM on the source Host (where it is migrating out)
        vmAllocationPolicy.deallocateHostForVm(vm);

        targetHost.removeMigratingInVm(vm);
        final boolean result = vmAllocationPolicy.allocateHostForVm(vm, targetHost);

        if (ack) {
            sendNow(ev.getSource(), CloudSimTags.VM_CREATE_ACK, vm);
        }

        vm.setInMigration(false);

        final SimEvent event = getSimulation().findFirstDeferred(this, new PredicateType(CloudSimTags.VM_MIGRATE));
        if (event == null || event.eventTime() > getSimulation().clock()) {
            //Updates processing of all Hosts again to get the latest state for all Hosts after the VMs migrations
            updateHostsProcessing();
        }

        if (result) {
            Log.printFormattedLine(
                "%.2f: Migration of %s to %s is completed",
                getSimulation().clock(), vm, targetHost);
        } else {
            Log.printFormattedLine("[Datacenter] %s allocation to the destination host failed!", vm);

        }
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
    protected double getCloudletProcessingUpdateInterval(final double nextFinishingCloudletTime){
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
    private boolean checksIfSubmittedCloudletIsAlreadyFinishedAndNotifyBroker(final Cloudlet cl, final boolean ack) {
        if(!cl.isFinished()){
            return false;
        }

        Log.printConcatLine(
                getName(), ": Warning - Cloudlet #", cl.getId(), " owned by ", cl.getBroker().getName(),
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
        sendCloudletSubmitAckToBroker(ack, cl);

        sendNow(cl.getBroker(), CloudSimTags.CLOUDLET_RETURN, cl);
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
     */
    private void sendCloudletSubmitAckToBroker(final boolean ack, final Cloudlet cl) {
        if(!ack){
            return;
        }

        sendNow(cl.getBroker(), CloudSimTags.CLOUDLET_SUBMIT_ACK, cl);
    }

    /**
     * Predict the total time to transfer a list of files.
     *
     * @param requiredFiles the files to be transferred
     * @return the predicted time
     */
    protected double predictFileTransferTime(final List<String> requiredFiles) {
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
     * Updates the processing of all Hosts, meaning
     * it makes the processing of VMs running inside such hosts to be updated.
     * Finally, the processing of Cloudlets running inside such VMs is updated too.
     *
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     */
    private double updateHostsProcessing() {
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

        power += getDatacenterPowerUsageForTimeSpan();

        return nextSimulationTime;
    }

    /**
     * Gets an <b>estimation</b> of total power consumed (in Watts-sec) by all Hosts of the Datacenter
     * since the last time the processing of Cloudlets in this Host was updated.
     *
     * @return the <b>estimated</b> total power consumed (in Watts-sec) by all Hosts in the elapsed time span
     * @see #getPower()
     * @see #getPowerInKWattsHour()
     */
    private double getDatacenterPowerUsageForTimeSpan() {
        if (getSimulation().clock() - getLastProcessTime() == 0) { //time span
            return 0;
        }

        double datacenterTimeSpanPowerUse = 0;
        for (final Host host : this.getHostList()) {
            final double prevCpuUsage = host.getPreviousUtilizationOfCpu();
            final double cpuUsage = host.getUtilizationOfCpu();
            final double timeFrameHostEnergy =
                host.getPowerModel().getEnergyLinearInterpolation(prevCpuUsage, cpuUsage, getSimulation().clock() - getLastProcessTime());
            datacenterTimeSpanPowerUse += timeFrameHostEnergy;
        }

        return datacenterTimeSpanPowerUse;
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
     */
    protected double updateCloudletProcessing() {
        if (!isTimeToUpdateCloudletsProcessing()){
            return Double.MAX_VALUE;
        }

        double nextSimulationTime = updateHostsProcessing();
        if (nextSimulationTime != Double.MAX_VALUE) {
            nextSimulationTime = getCloudletProcessingUpdateInterval(nextSimulationTime);
            schedule(this,
                nextSimulationTime,
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }
        setLastProcessTime(getSimulation().clock());

        checkIfVmMigrationsAreNeeded();
        return nextSimulationTime;
    }

    private boolean isTimeToUpdateCloudletsProcessing() {
        // if some time passed since last processing
        // R: for term is to allow loop at simulation start. Otherwise, one initial
        // simulation step is skipped and schedulers are not properly initialized
        return getSimulation().clock() < 0.111 ||
               getSimulation().clock() >= lastProcessTime + getSimulation().getMinTimeBetweenEvents();
    }

    /**
     * Checks if the {@link #getVmAllocationPolicy()} has defined
     * a new VM placement map, then sends the request to migrate VMs.
     */
    private void checkIfVmMigrationsAreNeeded() {
        if (!isMigrationsEnabled()) {
            return;
        }

        final Map<Vm, Host> migrationMap = getVmAllocationPolicy().getOptimizedAllocationMap(getVmList());
        for (final Map.Entry<Vm, Host> entry : migrationMap.entrySet()) {
            requestVmMigration(entry);
        }
    }

    /**
     * Actually fires the event that starts the VM migration
     * @param entry a Map Entry that indicate to which Host a VM must be migrated
     */
    private void requestVmMigration(final Map.Entry<Vm, Host> entry) {
        final double currentTime = getSimulation().clock();
        final Host sourceHost = entry.getKey().getHost();
        final Host targetHost = entry.getValue();

        final double delay = timeToMigrateVm(entry.getKey(), targetHost);
        if (sourceHost == Host.NULL) {
            Log.printFormattedLine(
                "%.2f: Migration of %s to %s is started.",
                currentTime, entry.getKey(), targetHost);
        } else {
            Log.printFormattedLine(
                "%.2f: Migration of %s from %s to %s is started.",
                currentTime, entry.getKey(), sourceHost, targetHost);
        }
        Log.printFormattedLine(
            "\tIt's expected to finish in %.2f seconds, considering the %.0f%% of bandwidth allowed for migration and the VM RAM size.",
            delay, getBandwidthPercentForMigration()*100);


        sourceHost.addVmMigratingOut(entry.getKey());
        targetHost.addMigratingInVm(entry.getKey());

        send(this, delay, CloudSimTags.VM_MIGRATE, entry);
    }

    /**
     * Computes the expected time to migrate a VM to a given Host.
     * It is computed as: VM RAM (MB)/Target Host Bandwidth (Mb/s).
     *
     * @param vm the VM to migrate.
     * @param targetHost the Host where tto migrate the VM
     * @return the time (in seconds) that is expected to migrate the VM
     */
    private double timeToMigrateVm(final Vm vm, final Host targetHost) {
        return vm.getRam().getCapacity() / Conversion.bitesToBytes(targetHost.getBw().getCapacity() * getBandwidthPercentForMigration());
    }

    /**
     * Verifies if some cloudlet inside the hosts of this Datacenter have already finished.
     * If yes, send them to the User/Broker
     */
    protected void checkCloudletsCompletionForAllHosts() {
        final List<? extends Host> hosts = vmAllocationPolicy.getHostList();
        hosts.forEach(this::checkCloudletsCompletionForGivenHost);
    }

    private void checkCloudletsCompletionForGivenHost(final Host host) {
        host.getVmList().forEach(this::checkCloudletsCompletionForGivenVm);
    }

    private void checkCloudletsCompletionForGivenVm(Vm vm) {
        final List<Cloudlet> nonReturnedCloudlets =
            vm.getCloudletScheduler().getCloudletFinishedList().stream()
                .map(CloudletExecution::getCloudlet)
                .filter(c -> !vm.getCloudletScheduler().isCloudletReturned(c))
                .collect(toList());

        nonReturnedCloudlets.forEach(this::returnFinishedCloudletToBroker);
    }

    /**
     * Notifies the broker about the end of execution of a given Cloudlet,
     * by returning the Cloudlet to it.
     *
     * @param cloudlet the Cloudlet to return to broker in order to notify it about the Cloudlet execution end
     */
    private void returnFinishedCloudletToBroker(final Cloudlet cloudlet) {
        sendNow(cloudlet.getBroker(), CloudSimTags.CLOUDLET_RETURN, cloudlet);
        cloudlet.getVm().getCloudletScheduler().addCloudletToReturnedList(cloudlet);
    }

    @Override
    public int addFile(final File file) {
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
    protected boolean contains(final File file) {
        Objects.requireNonNull(file);
        return contains(file.getName());
    }

    /**
     * Checks whether the Datacenter has the given file.
     *
     * @param fileName a file name to be searched
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    protected boolean contains(final String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        return storageList.stream().anyMatch(storage -> storage.contains(fileName));
    }

    @Override
    public void shutdownEntity() {
        Log.printFormattedLine("%.2f: %s is shutting down...", getSimulation().clock(), getName());
    }

    @Override
    protected void startEntity() {
        Log.printConcatLine(getName(), " is starting...");
        sendNow(getSimulation().getCloudInfoService(), CloudSimTags.DATACENTER_REGISTRATION_REQUEST, this);
    }

    @Override
    public <T extends Host> List<T> getHostList() {
        return (List<T>)Collections.unmodifiableList(hostList);
    }

    @Override
    public DatacenterCharacteristics getCharacteristics() {
        return characteristics;
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
    public final Datacenter setVmAllocationPolicy(final VmAllocationPolicy vmAllocationPolicy) {
        Objects.requireNonNull(vmAllocationPolicy);
        if(vmAllocationPolicy.getDatacenter() != null && vmAllocationPolicy.getDatacenter() != Datacenter.NULL && !this.equals(vmAllocationPolicy.getDatacenter())){
            throw new IllegalStateException("The given VmAllocationPolicy is already used by another Datacenter.");
        }

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
    protected final void setLastProcessTime(final double lastProcessTime) {
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
    public final Datacenter setStorageList(final List<FileStorage> storageList) {
        Objects.requireNonNull(storageList);
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
    public final Datacenter setSchedulingInterval(final double schedulingInterval) {
        this.schedulingInterval = Math.max(schedulingInterval, 0);
        return this;
    }

    @Override
    public Host getHost(final int index) {
        if (index >= 0 && index < getHostList().size()) {
            return getHostList().get(index);
        }

        return Host.NULL;
    }

    @Override
    public <T extends Host> Datacenter addHostList(final List<T> hostList) {
        hostList.forEach(this::addHost);
        return this;
    }

    @Override
    public <T extends Host> Datacenter addHost(final T host) {
        if(vmAllocationPolicy == null || vmAllocationPolicy == VmAllocationPolicy.NULL){
            throw new IllegalStateException("A VmAllocationPolicy must be set before adding a new Host to the Datacenter.");
        }

        if(host.getId() <= -1) {
            host.setId(getHostList().size());
        }

        host.setDatacenter(this);
        ((List<T>)hostList).add(host);

        //Sets the Datacenter again so that the new Host is registered internally on the VmAllocationPolicy
        vmAllocationPolicy.setDatacenter(this);
        return this;
    }

    @Override
    public String toString() {
        return String.format("Datacenter %d", getId());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final DatacenterSimple that = (DatacenterSimple) o;

        return !characteristics.equals(that.characteristics);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + characteristics.hashCode();
        return result;
    }

    @Override
    public double getBandwidthPercentForMigration() {
        return bandwidthPercentForMigration;
    }

    @Override
    public void setBandwidthPercentForMigration(final double bandwidthPercentForMigration) {
        if(bandwidthPercentForMigration <= 0){
            throw new IllegalArgumentException("The bandwidth migration percentage must be greater than 0.");
        }

        if(bandwidthPercentForMigration > 1){
            throw new IllegalArgumentException("The bandwidth migration percentage must be lower or equal to 1.");
        }

        this.bandwidthPercentForMigration = bandwidthPercentForMigration;
    }

    @Override
    public double getPower() {
        return power;
    }

    /**
     * Checks if migrations are enabled.
     *
     * @return true, if migrations are enable; false otherwise
     */
    public boolean isMigrationsEnabled() {
        return migrationsEnabled;
    }

    /**
     * Enable VM migrations.
     *
     * @return
     */
    public final Datacenter enableMigrations() {
        this.migrationsEnabled = true;
        return this;
    }

    /**
     * Disable VM migrations.
     *
     * @return
     */
    public final Datacenter disableMigrations() {
        this.migrationsEnabled = false;
        return this;
    }
}
