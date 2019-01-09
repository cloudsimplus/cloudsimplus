/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.core.CloudSimEntity;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.PredicateType;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.network.IcmpPacket;
import org.cloudbus.cloudsim.resources.DatacenterStorage;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.util.MathUtil;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Objects.requireNonNull;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DatacenterSimple.class.getSimpleName());

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


    /** @see #getSchedulingInterval() */
    private double schedulingInterval;

    /** @see #getDatacenterStorage() */
	private DatacenterStorage datacenterStorage;

    private List<EventListener<HostEventInfo>> onHostAvailableListeners;

    /**
     * Creates a Datacenter with an empty {@link #getDatacenterStorage() storage}
     * and no Hosts.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @see #DatacenterSimple(Simulation, List, VmAllocationPolicy)
     * @see #DatacenterSimple(Simulation, List, VmAllocationPolicy, DatacenterStorage)
     * @see #addHostList(List)
     */
    public DatacenterSimple(
        final Simulation simulation,
        final VmAllocationPolicy vmAllocationPolicy)
    {
        this(simulation, new ArrayList<>(), vmAllocationPolicy, new DatacenterStorage());
    }

    /**
     * Creates a Datacenter with an empty {@link #getDatacenterStorage() storage}.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @see #DatacenterSimple(Simulation, List, VmAllocationPolicy, DatacenterStorage)
     */
    public DatacenterSimple(
        final Simulation simulation,
        final List<? extends Host> hostList,
        final VmAllocationPolicy vmAllocationPolicy)
    {
        this(simulation, hostList, vmAllocationPolicy, new DatacenterStorage());
    }

    /**
     * Creates a Datacenter attaching a given storage list to its {@link #getDatacenterStorage() storage}.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @param storageList the storage list to attach to the {@link #getDatacenterStorage() datacenter storage}
     */
    public DatacenterSimple(
        final Simulation simulation,
        final List<? extends Host> hostList,
        final VmAllocationPolicy vmAllocationPolicy,
        final List<FileStorage> storageList)
    {
        this(simulation, hostList, vmAllocationPolicy, new DatacenterStorage(storageList));
    }

    /**
     * Creates a Datacenter with a given {@link #getDatacenterStorage() storage}.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @param storage the {@link #getDatacenterStorage() storage} for this Datacenter
     * @see DatacenterStorage#getStorageList()
     */
    public DatacenterSimple(
        final Simulation simulation,
        final List<? extends Host> hostList,
        final VmAllocationPolicy vmAllocationPolicy,
        final DatacenterStorage storage)
    {
        super(simulation);
        setHostList(hostList);

        setLastProcessTime(0.0);
        setSchedulingInterval(0);
        setDatacenterStorage(storage);

        this.onHostAvailableListeners = new ArrayList<>();
        this.characteristics = new DatacenterCharacteristicsSimple(this);
        this.bandwidthPercentForMigration = DEF_BW_PERCENT_FOR_MIGRATION;
        this.migrationsEnabled = true;

        setVmAllocationPolicy(vmAllocationPolicy);
    }

    private void setHostList(final List<? extends Host> hostList) {
        this.hostList = requireNonNull(hostList);
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
    public void processEvent(final SimEvent evt) {
        if (processCloudletEvents(evt) || processVmEvents(evt) || processNetworkEvents(evt) || processHostEvents(evt)) {
            return;
        }

        LOGGER.trace("{}: {}: Unknown event {} received.", getSimulation().clock(), this, evt.getTag());
    }

    private boolean processHostEvents(final SimEvent evt) {
        if (evt.getTag() == CloudSimTags.HOST_ADD) {
            processHostAdditionRequest(evt);
            return true;
        } else if (evt.getTag() == CloudSimTags.HOST_REMOVE) {
            processHostRemovalRequest(evt);
            return true;
        }

        return false;
    }

    /**
     * Process a Host addition request received during simulation runtime.
     * @param evt
     */
    private void processHostAdditionRequest(final SimEvent evt) {
        getHostFromHostEvent(evt).ifPresent(host -> {
            this.addHost(host);
            LOGGER.info(
                "{}: {}: Host {} added to {} during simulation runtime",
                getSimulation().clock(), getClass().getSimpleName(), host.getId(), this);
            //Notification must be sent only for Hosts added during simulation runtime
            notifyOnHostAvailableListeners(host);
        });
    }

    /**
     * Process a Host removal request received during simulation runtime.
     * @param srcEvt the received event
     */
    private void processHostRemovalRequest(final SimEvent srcEvt) {
        final long hostId = (long)srcEvt.getData();
        final Host host = getHostById(hostId);
        if(host == Host.NULL) {
            LOGGER.warn(
                "{}: {}: Host {} was not found to be removed from {}.",
                getSimulation().clock(), getClass().getSimpleName(), hostId, this);
            return;
        }

        HostFaultInjection fault = new HostFaultInjection(this);
        try {
            LOGGER.error(
                "{}: {}: Host {} removed from {} due to injected failure.",
                getSimulation().clock(), getClass().getSimpleName(), host.getId(), this);
            fault.generateHostFault(host);
        } finally{
            fault.shutdownEntity();
        }

        /*If the Host was found in this Datacenter, cancel the message sent to others
        * Datacenters to try to find the Host for removal.*/
        getSimulation().cancelAll(
            getSimulation().getCloudInfoService(),
            evt -> MathUtil.same(evt.getTime(), srcEvt.getTime()) &&
                   evt.getTag() == CloudSimTags.HOST_REMOVE &&
                   (long)evt.getData() == host.getId());
    }

    private Optional<Host> getHostFromHostEvent(final SimEvent evt) {
        if(evt.getData() instanceof Host){
            return Optional.of((Host)evt.getData());
        }

        return Optional.empty();
    }

    private boolean processNetworkEvents(final SimEvent evt) {
        if (evt.getTag() == CloudSimTags.ICMP_PKT_SUBMIT) {
            processPingRequest(evt);
            return true;
        }

        return false;
    }

    /**
     * Process a received event.
     * @param evt the event to be processed
     */
    private boolean processVmEvents(final SimEvent evt) {
        switch (evt.getTag()) {
            case CloudSimTags.VM_CREATE:
                processVmCreate(evt, false);
                return true;
            case CloudSimTags.VM_CREATE_ACK:
                processVmCreate(evt, true);
                return true;
            case CloudSimTags.VM_VERTICAL_SCALING:
                requestVmVerticalScaling(evt);
                return true;
            case CloudSimTags.VM_DESTROY:
                processVmDestroy(evt, false);
                return true;
            case CloudSimTags.VM_DESTROY_ACK:
                processVmDestroy(evt, true);
                return true;
            case CloudSimTags.VM_MIGRATE:
                finishVmMigration(evt, false);
                return true;
            case CloudSimTags.VM_MIGRATE_ACK:
                finishVmMigration(evt, true);
                return true;
            case CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING:
                updateCloudletProcessing();
                checkCloudletsCompletionForAllHosts();
                return true;
        }

        return false;
    }

    /**
     * Process a {@link CloudSimTags#VM_VERTICAL_SCALING} request, trying to scale
     * a Vm resource.
     *
     * @param evt the received  {@link CloudSimTags#VM_VERTICAL_SCALING} event
     * @return true if the Vm was scaled, false otherwise
     */
    private boolean requestVmVerticalScaling(final SimEvent evt) {
        if(!(evt.getData() instanceof VerticalVmScaling)){
            return false;
        }

        return vmAllocationPolicy.scaleVmVertically((VerticalVmScaling)evt.getData());
    }

    private boolean processCloudletEvents(final SimEvent evt) {
        switch (evt.getTag()) {
            // New Cloudlet arrives
            case CloudSimTags.CLOUDLET_SUBMIT:
                processCloudletSubmit(evt, false);
                return true;
            // New Cloudlet arrives, but the sender asks for an ack
            case CloudSimTags.CLOUDLET_SUBMIT_ACK:
                processCloudletSubmit(evt, true);
                return true;
            // Cancels a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_CANCEL:
                processCloudlet(evt, CloudSimTags.CLOUDLET_CANCEL);
                return true;
            // Pauses a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_PAUSE:
                processCloudlet(evt, CloudSimTags.CLOUDLET_PAUSE);
                return true;
            // Pauses a previously submitted Cloudlet, but the sender
            // asks for an acknowledgement
            case CloudSimTags.CLOUDLET_PAUSE_ACK:
                processCloudlet(evt, CloudSimTags.CLOUDLET_PAUSE_ACK);
                return true;
            // Resumes a previously submitted Cloudlet
            case CloudSimTags.CLOUDLET_RESUME:
                processCloudlet(evt, CloudSimTags.CLOUDLET_RESUME);
                return true;
            // Resumes a previously submitted Cloudlet, but the sender
            // asks for an acknowledgement
            case CloudSimTags.CLOUDLET_RESUME_ACK:
                processCloudlet(evt, CloudSimTags.CLOUDLET_RESUME_ACK);
                return true;
        }

        return false;
    }

    /**
     * Processes a ping request.
     *
     * @param evt information about the event just happened
     */
    protected void processPingRequest(final SimEvent evt) {
        final IcmpPacket pkt = (IcmpPacket) evt.getData();
        pkt.setTag(CloudSimTags.ICMP_PKT_RETURN);
        pkt.setDestination(pkt.getSource());

        // returns the packet to the sender
        sendNow(pkt.getSource(), CloudSimTags.ICMP_PKT_RETURN, pkt);
    }

    /**
     * Processes a Cloudlet based on the event type.
     *
     * @param evt information about the event just happened
     * @param type event type
     */
    protected void processCloudlet(final SimEvent evt, final int type) {
        Cloudlet cloudlet;
        try {
            cloudlet = (Cloudlet) evt.getData();
        } catch (ClassCastException e) {
            LOGGER.error("{}: Error in processing Cloudlet: {}", super.getName(), e.getMessage());
            return;
        }

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
                LOGGER.trace(
                    "{}: Unable to handle a request from {} with event tag = {}",
                    this, evt.getSource().getName(), evt.getTag());

        }
    }
    /**
     * Processes the submission of a Cloudlet by a DatacenterBroker.
     *
     * @param evt information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     */
    protected void processCloudletSubmit(final SimEvent evt, final boolean ack) {
        final Cloudlet cloudlet = (Cloudlet) evt.getData();
        if (cloudlet.isFinished()) {
            notifyBrokerAboutFinishedCloudlet(cloudlet, ack);
            return;
        }

        cloudlet.assignToDatacenter(this);
        submitCloudletToVm(cloudlet, ack);
    }

    /**
     * Submits a cloudlet to be executed inside its bind VM.
     *
     * @param cloudlet the cloudlet to the executed
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     */
    private void submitCloudletToVm(final Cloudlet cloudlet, final boolean ack) {
        // time to transfer cloudlet's files
        final double fileTransferTime = getDatacenterStorage().predictFileTransferTime(cloudlet.getRequiredFiles());

        final CloudletScheduler scheduler = cloudlet.getVm().getCloudletScheduler();
        final double estimatedFinishTime = scheduler.cloudletSubmit(cloudlet, fileTransferTime);

        // if this cloudlet is in the exec queue
        if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
            send(this,
                getCloudletProcessingUpdateInterval(estimatedFinishTime),
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING);
        }

        sendCloudletSubmitAckToBroker(cloudlet, ack);
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
        if(schedulingInterval == 0) {
            return nextFinishingCloudletTime;
        }

        final double time = Math.floor(getSimulation().clock());
        final double mod = time % schedulingInterval;
        /* If a scheduling interval is set, ensures the next time that Cloudlets' processing
         * are updated is multiple of the scheduling interval.
         * If there is an event happening before such a time, then the event
         * will be scheduled as usual. Otherwise, the update
         * is scheduled to the next time multiple of the scheduling interval.*/
        final double delay = mod == 0 ? schedulingInterval : (time - mod + schedulingInterval) - time;
        return Math.min(nextFinishingCloudletTime, delay);
    }

    /**
     * Processes a Cloudlet resume request.
     *
     * @param cloudlet cloudlet to be resumed
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     */
    protected void processCloudletResume(final Cloudlet cloudlet, final boolean ack) {
        final double estimatedFinishTime = cloudlet.getVm()
            .getCloudletScheduler().cloudletResume(cloudlet);

        if (estimatedFinishTime > 0.0 && estimatedFinishTime > getSimulation().clock()) {
            schedule(this,
                getCloudletProcessingUpdateInterval(estimatedFinishTime),
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING);
        }

        sendAck(ack, cloudlet, CloudSimTags.CLOUDLET_RESUME_ACK);
    }

    private void sendAck(boolean ack, Cloudlet cloudlet, int cloudSimTagAck) {
        if (ack) {
            sendNow(cloudlet.getBroker(), cloudSimTagAck, cloudlet);
        }
    }

    /**
     * Processes a Cloudlet pause request.
     *
     * @param cloudlet cloudlet to be paused
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     */
    protected void processCloudletPause(final Cloudlet cloudlet, final boolean ack) {
        cloudlet.getVm().getCloudletScheduler().cloudletPause(cloudlet);
        sendAck(ack, cloudlet, CloudSimTags.CLOUDLET_PAUSE_ACK);
    }

    /**
     * Processes a Cloudlet cancel request.
     *
     * @param cloudlet cloudlet to be canceled
     */
    protected void processCloudletCancel(final Cloudlet cloudlet) {
        cloudlet.getVm().getCloudletScheduler().cloudletCancel(cloudlet);
        sendNow(cloudlet.getBroker(), CloudSimTags.CLOUDLET_CANCEL, cloudlet);
    }

    /**
     * Process the event for a Broker which wants to create a VM in this
     * Datacenter. This Datacenter will then send the status back to
     * the Broker.
     *
     * @param evt information about the event just happened
     * @param ackRequested indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     * @return true if a host was allocated to the VM; false otherwise
     */
    protected boolean processVmCreate(final SimEvent evt, final boolean ackRequested) {
        final Vm vm = (Vm) evt.getData();

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
     * @param evt information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     */
    protected void processVmDestroy(final SimEvent evt, final boolean ack) {
        final Vm vm = (Vm) evt.getData();
        vmAllocationPolicy.deallocateHostForVm(vm);

        if (ack) {
            sendNow(vm.getBroker(), CloudSimTags.VM_DESTROY_ACK, vm);
        }

        final String warningMsg = generateNotFinishedCloudletsWarning(vm);
        final String msg = String.format(
                "%.2f: %s: %s destroyed on %s. %s\n",
                getSimulation().clock(), getClass().getSimpleName(), vm, vm.getHost(), warningMsg);
        if(warningMsg.isEmpty())
            LOGGER.info(msg);
        else LOGGER.warn(msg);
    }

    private String generateNotFinishedCloudletsWarning(final Vm vm) {
        final int cloudletsNoFinished = vm.getCloudletScheduler().getCloudletList().size();
        if(cloudletsNoFinished == 0) {
            return "";
        }

        return String.format(
                "It had a total of %d cloudlets (running + waiting). %s", cloudletsNoFinished,
                "Some events may have been missed. Try decreasing CloudSim's minTimeBetweenEvents attribute.");
    }

    /**
     * Finishes the process of migrating a VM.
     *
     * @param evt information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * acknowledge message when the event finishes to be processed
     */
    protected void finishVmMigration(final SimEvent evt, final boolean ack) {
        if (!(evt.getData() instanceof Map.Entry<?, ?>)) {
            throw new ClassCastException("The data object must be Map.Entry<Vm, Host>");
        }

        final Map.Entry<Vm, Host> entry = (Map.Entry<Vm, Host>) evt.getData();

        final Vm vm = entry.getKey();
        final Host targetHost = entry.getValue();

        //Updates processing of all Hosts to get the latest state for all Hosts before migrating VMs
        updateHostsProcessing();

        //Deallocates the VM on the source Host (where it is migrating out)
        vmAllocationPolicy.deallocateHostForVm(vm);

        targetHost.removeMigratingInVm(vm);
        final boolean result = vmAllocationPolicy.allocateHostForVm(vm, targetHost);

        if (ack) {
            sendNow(evt.getSource(), CloudSimTags.VM_CREATE_ACK, vm);
        }

        vm.setInMigration(false);

        final SimEvent event = getSimulation().findFirstDeferred(this, new PredicateType(CloudSimTags.VM_MIGRATE));
        if (event == null || event.getTime() > getSimulation().clock()) {
            //Updates processing of all Hosts again to get the latest state for all Hosts after the VMs migrations
            updateHostsProcessing();
        }

        if (result)
            LOGGER.info("{}: Migration of {} to {} is completed", getSimulation().clock(), vm, targetHost);
        else LOGGER.error("{}: Allocation of {} to the destination Host failed!", this, vm);
    }

    /**
     * Checks if a submitted cloudlet has already finished.
     * If it is the case, the Datacenter notifies the Broker that
     * the Cloudlet cannot be created again because it has already finished.
     *
     * @param cloudlet the finished cloudlet
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     */
    private void notifyBrokerAboutFinishedCloudlet(final Cloudlet cloudlet, final boolean ack) {
        LOGGER.warn(
            "{}: {} owned by {} is already completed/finished. It won't be executed again.",
            getName(), cloudlet, cloudlet.getBroker());

        /*
         NOTE: If a Cloudlet has finished, then it won't be processed.
         So, if ack is required, this method sends back a result.
         If ack is not required, this method don't send back a result.
         Hence, this might cause CloudSim to be hanged since waiting
         for this Cloudlet back.
        */
        sendCloudletSubmitAckToBroker(cloudlet, ack);

        sendNow(cloudlet.getBroker(), CloudSimTags.CLOUDLET_RETURN, cloudlet);
    }

    /**
     * Sends an ACK to the DatacenterBroker that submitted the Cloudlet for execution
     * in order to respond the reception of the submission request,
     * informing if the cloudlet was created or not.
     *
     * The ACK is sent just if the Broker is waiting for it and that condition
     * is indicated in the ack parameter.
     *
     * @param cloudlet the cloudlet to respond to DatacenterBroker if it was created or not
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     */
    private void sendCloudletSubmitAckToBroker(final Cloudlet cloudlet, final boolean ack) {
        if(!ack){
            return;
        }

        sendNow(cloudlet.getBroker(), CloudSimTags.CLOUDLET_SUBMIT_ACK, cloudlet);
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
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING);
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
        String msg1;
        if (sourceHost == Host.NULL) {
            msg1 = String.format(
                "%.2f: Migration of %s to %s is started.",
                currentTime, entry.getKey(), targetHost);
        } else {
            msg1 = String.format(
                "%.2f: Migration of %s from %s to %s is started.",
                currentTime, entry.getKey(), sourceHost, targetHost);
        }

        final String msg2 = String.format(
            "It's expected to finish in %.2f seconds, considering the %.0f%% of bandwidth allowed for migration and the VM RAM size.",
            delay, getBandwidthPercentForMigration()*100);
        LOGGER.info("{}{}{}", msg1, System.lineSeparator(), msg2);


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

    private void checkCloudletsCompletionForGivenVm(final Vm vm) {
        final List<Cloudlet> nonReturnedCloudlets =
            vm.getCloudletScheduler().getCloudletFinishedList().stream()
                .map(CloudletExecution::getCloudlet)
                .filter(cloudlet -> !vm.getCloudletScheduler().isCloudletReturned(cloudlet))
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
    public void shutdownEntity() {
        super.shutdownEntity();
        LOGGER.info("{}: {} is shutting down...", getSimulation().clock(), getName());
    }

    @Override
    protected void startEntity() {
        LOGGER.info("{} is starting...", getName());
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
        requireNonNull(vmAllocationPolicy);
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
    public DatacenterStorage getDatacenterStorage() {
        return this.datacenterStorage;
    }

    @Override
    public final void setDatacenterStorage(final DatacenterStorage datacenterStorage) {
        datacenterStorage.setDatacenter(this);
        this.datacenterStorage = datacenterStorage;
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
    public Host getHostById(final long id) {
        return hostList.stream().filter(host -> host.getId()==id).findFirst().map(host -> (Host)host).orElse(Host.NULL);
    }

    @Override
    public <T extends Host> Datacenter addHostList(final List<T> hostList) {
        requireNonNull(hostList);
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
        if(host.getStartTime() <= 0) {
            host.setStartTime((int) getSimulation().clock());
        }
        ((List<T>)hostList).add(host);

        //Sets the Datacenter again so that the new Host is registered internally on the VmAllocationPolicy
        vmAllocationPolicy.setDatacenter(this);
        return this;
    }

    private <T extends Host> void notifyOnHostAvailableListeners(final T host) {
        onHostAvailableListeners.forEach(listener -> listener.update(HostEventInfo.of(listener, host, getSimulation().clock())));
    }

    @Override
    public <T extends Host> Datacenter removeHost(final T host) {
        hostList.remove(host);
        ((VmAllocationPolicyAbstract)vmAllocationPolicy).addPesFromHost(host);
        return this;
    }

    @Override
    public String toString() {
        return String.format("Datacenter %d", getId());
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        final DatacenterSimple that = (DatacenterSimple) object;

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

    @Override
    public Datacenter addOnHostAvailableListener(final EventListener<HostEventInfo> listener) {
        onHostAvailableListeners.add(requireNonNull(listener));
        return this;
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
