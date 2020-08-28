/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigration;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
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
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;

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

    /**
     * The last time some Host on the Datacenter was under or overloaded.
     *
     * <p>Double.MIN_VALUE is surprisingly not a negative number.
     * Initializing this attribute with a too small value makes that the first
     * time an under or overload condition is detected,
     * it will try immediately to find suitable Hosts for migration.</p>
     */
    private double lastTimeUnderOrOverloadedHostsDetected = -Double.MAX_VALUE;

    /**
     * @see #getBandwidthPercentForMigration()
     */
    private double bandwidthPercentForMigration;

    /**
     * Indicates if migrations are disabled or not.
     */
    private boolean migrationsEnabled;

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

    private final List<EventListener<HostEventInfo>> onHostAvailableListeners;

    /** @see #setPowerSupply(DatacenterPowerSupply) */
    private DatacenterPowerSupply powerSupply;

    /**
     * @see #getTimeZone()
     */
    private double timeZone;
    private Map<Vm, Host> lastMigrationMap;

    /** @see #getHostSearchForMigrationDelay() */
    private double hostSearchForMigrationDelay;

    /**
     * Creates a Datacenter with an empty {@link #getDatacenterStorage() storage}
     * and a {@link VmAllocationPolicySimple} by default.
     *
     * <p><b>NOTE:</b> To change such attributes, just call the respective setters.</p>
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @see #DatacenterSimple(Simulation, List, VmAllocationPolicy, DatacenterStorage)
     */
    public DatacenterSimple(final Simulation simulation, final List<? extends Host> hostList) {
        this(simulation, hostList, new VmAllocationPolicySimple(), new DatacenterStorage());
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
        this.powerSupply = DatacenterPowerSupply.NULL;

        setLastProcessTime(0.0);
        setSchedulingInterval(0);
        setDatacenterStorage(storage);

        this.onHostAvailableListeners = new ArrayList<>();
        this.characteristics = new DatacenterCharacteristicsSimple(this);
        this.bandwidthPercentForMigration = DEF_BW_PERCENT_FOR_MIGRATION;
        this.migrationsEnabled = true;
        this.hostSearchForMigrationDelay = -1;

        this.lastMigrationMap = Collections.emptyMap();

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

        LOGGER.trace("{}: {}: Unknown event {} received.", getSimulation().clockStr(), this, evt.getTag());
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
                getSimulation().clockStr(), getClass().getSimpleName(), host.getId(), this);
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
                getSimulation().clockStr(), getClass().getSimpleName(), hostId, this);
            return;
        }

        HostFaultInjection fault = new HostFaultInjection(this);
        try {
            LOGGER.error(
                "{}: {}: Host {} removed from {} due to injected failure.",
                getSimulation().clockStr(), getClass().getSimpleName(), host.getId(), this);
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
            case CloudSimTags.VM_CREATE_ACK:
                processVmCreate(evt);
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
            notifyBrokerAboutAlreadyFinishedCloudlet(cloudlet, ack);
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
     * @return next time cloudlets processing will be updated (a relative delay from the current simulation time)
     *
     * @see #updateCloudletProcessing()
     */
    protected double getCloudletProcessingUpdateInterval(final double nextFinishingCloudletTime){
        if(schedulingInterval == 0) {
            return nextFinishingCloudletTime;
        }

        final double time = Math.floor(clock());
        final double mod = time % schedulingInterval;
        /* If a scheduling interval is set, ensures the next time that Cloudlets' processing
         * are updated is multiple of the scheduling interval.
         * If there is an event happening before such a time, then the event
         * will be scheduled as usual. Otherwise, the update
         * is scheduled to the next time multiple of the scheduling interval.*/
        final double delay = mod == 0 ? schedulingInterval : (time - mod + schedulingInterval) - time;
        return Math.min(nextFinishingCloudletTime, delay);
    }

    private double clock() {
        return getSimulation().clock();
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

        if (estimatedFinishTime > 0.0 && estimatedFinishTime > clock()) {
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
     * acknowledge message when the event finishes to be processed
     * @return true if a host was allocated to the VM; false otherwise
     */
    private boolean processVmCreate(final SimEvent evt) {
        final Vm vm = (Vm) evt.getData();

        final boolean hostAllocatedForVm = vmAllocationPolicy.allocateHostForVm(vm);
        if (hostAllocatedForVm) {
            vm.updateProcessing(vm.getHost().getVmScheduler().getAllocatedMips(vm));
        }

        /* Acknowledges that the request was received by the Datacenter,
          (the broker is expecting that if the Vm was created or not). */
        send(vm.getBroker(), getSimulation().getMinTimeBetweenEvents(), CloudSimTags.VM_CREATE_ACK, vm);

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
                "%s: %s: %s destroyed on %s. %s",
                getSimulation().clockStr(), getClass().getSimpleName(), vm, vm.getHost(), warningMsg);
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
                "Some events may have been missed. You can try: " +
                "(a) decreasing CloudSim's minTimeBetweenEvents and/or Datacenter's schedulingInterval attribute; " +
                "(b) increasing broker's Vm destruction delay for idle VMs if you set it to zero; " +
                "(c) defining Cloudlets with smaller length (your Datacenter's scheduling interval may be smaller than the time to finish some Cloudlets).");
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

        //Updates processing of all Hosts to get their latest state before migrating VMs
        updateHostsProcessing();

        //De-allocates the VM on the source Host (where it is migrating out)
        vmAllocationPolicy.deallocateHostForVm(vm);

        targetHost.removeMigratingInVm(vm);
        final boolean migrated = vmAllocationPolicy.allocateHostForVm(vm, targetHost);
        if(migrated) {
            ((VmSimple)vm).updateMigrationFinishListeners(targetHost);
            /*When the VM is destroyed from the source host, it's removed from the vmExecList.
            After migration, we need to add it again.*/
            vm.getBroker().getVmExecList().add(vm);

            if (ack) {
                sendNow(evt.getSource(), CloudSimTags.VM_CREATE_ACK, vm);
            }
        }

        final SimEvent event = getSimulation().findFirstDeferred(this, new PredicateType(CloudSimTags.VM_MIGRATE));
        if (event == null || event.getTime() > clock()) {
            //Updates processing of all Hosts again to get their latest state after the VMs migrations
            updateHostsProcessing();
        }

        if (migrated)
            LOGGER.info("{}: Migration of {} to {} is completed", getSimulation().clockStr(), vm, targetHost);
        else LOGGER.error("{}: {}: Allocation of {} to the destination Host failed!", getSimulation().clockStr(), this, vm);
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
    private void notifyBrokerAboutAlreadyFinishedCloudlet(final Cloudlet cloudlet, final boolean ack) {
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
        double nextSimulationDelay = Double.MAX_VALUE;
        for (final Host host : getHostList()) {
            final double delay = host.updateProcessing(clock());
            nextSimulationDelay = Math.min(delay, nextSimulationDelay);
        }

        // Guarantees a minimal interval before scheduling the event
        final double minTimeBetweenEvents = getSimulation().getMinTimeBetweenEvents()+0.01;
        nextSimulationDelay = nextSimulationDelay == 0 ? nextSimulationDelay : Math.max(nextSimulationDelay, minTimeBetweenEvents);

        if (nextSimulationDelay == Double.MAX_VALUE) {
            return nextSimulationDelay;
        }

        powerSupply.computePowerUtilizationForTimeSpan(lastProcessTime);

        return nextSimulationDelay;
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
        double nextSimulationDelay = updateHostsProcessing();

        if (nextSimulationDelay != Double.MAX_VALUE) {
            nextSimulationDelay = getCloudletProcessingUpdateInterval(nextSimulationDelay);
            schedule(nextSimulationDelay, CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING);
        }
        setLastProcessTime(clock());

        checkIfVmMigrationsAreNeeded();
        return nextSimulationDelay;
    }

    private boolean isTimeToUpdateCloudletsProcessing() {
        // if some time passed since last processing
        // R: for term is to allow loop at simulation start. Otherwise, one initial
        // simulation step is skipped and schedulers are not properly initialized
        return clock() < 0.111 ||
               clock() >= lastProcessTime + getSimulation().getMinTimeBetweenEvents();
    }

    /**
     * Checks if the {@link #getVmAllocationPolicy()} has defined
     * a new VM placement map, then sends the request to migrate VMs.
     *
     * <p><b>This is an expensive operation for large scale simulations.</b></p>
     */
    private void checkIfVmMigrationsAreNeeded() {
        if (!isTimeToSearchForSuitableHosts()) {
            return;
        }

        lastMigrationMap = getVmAllocationPolicy().getOptimizedAllocationMap(getVmList());
        for (final Map.Entry<Vm, Host> entry : lastMigrationMap.entrySet()) {
            requestVmMigration(entry.getKey(), entry.getValue());
        }

        if(areThereUnderOrOverloadedHostsAndMigrationIsSupported()){
            logHostSearchRetry();
            lastTimeUnderOrOverloadedHostsDetected = clock();
        }
    }

    private void logHostSearchRetry() {
        if(lastMigrationMap.isEmpty()) {
            final String msg = hostSearchForMigrationDelay > 0 ?
                                    "in " + TimeUtil.secondsToStr(hostSearchForMigrationDelay) :
                                    "as soon as possible";
            LOGGER.warn(
                "{}: Datacenter: An under or overload situation was detected but currently, however there aren't suitable Hosts to manage that. Trying again {}.",
                clock(), msg);
        }
    }

    /**
     * Indicates if it's time to check if suitable Hosts are available to migrate VMs
     * from under or overload Hosts.
     * @return
     */
    private boolean isTimeToSearchForSuitableHosts(){
        final double elapsedSecs = clock() - lastTimeUnderOrOverloadedHostsDetected;
        return isMigrationsEnabled() && (elapsedSecs >= hostSearchForMigrationDelay);
    }

    private boolean areThereUnderOrOverloadedHostsAndMigrationIsSupported(){
        if(vmAllocationPolicy instanceof VmAllocationPolicyMigration){
            final VmAllocationPolicyMigration policy = (VmAllocationPolicyMigration) vmAllocationPolicy;
            return policy.areHostsUnderOrOverloaded();
        }

        return false;
    }

    @Override
    public void requestVmMigration(final Vm sourceVm, final Host targetHost) {
        final String currentTime = getSimulation().clockStr();
        final Host sourceHost = sourceVm.getHost();

        final double delay = timeToMigrateVm(sourceVm, targetHost);
        final String msg1 =
            sourceHost == Host.NULL ?
                String.format("%s to %s", sourceVm, targetHost) :
                String.format("%s from %s to %s", sourceVm, sourceHost, targetHost);

        final String msg2 = String.format(
            "It's expected to finish in %.2f seconds, considering the %.0f%% of bandwidth allowed for migration and the VM RAM size.",
            delay, getBandwidthPercentForMigration()*100);
        LOGGER.info("{}: {}: Migration of {} is started. {}", currentTime, getName(), msg1, msg2);

        if(targetHost.addMigratingInVm(sourceVm)) {
            sourceHost.addVmMigratingOut(sourceVm);
            send(this, delay, CloudSimTags.VM_MIGRATE, new TreeMap.SimpleEntry<>(sourceVm, targetHost));
        }
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

    @Override
    public void shutdownEntity() {
        super.shutdownEntity();
        LOGGER.info("{}: {} is shutting down...", getSimulation().clockStr(), getName());
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

    /**
     * Gets a <b>read-only</b> list all VMs from all Hosts of this Datacenter.
     *
     * @param <T> the class of VMs inside the list
     * @return the list all VMs from all Hosts
     */
    private <T extends Vm> List<T> getVmList() {
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
    public double getTimeZone() {
        return timeZone;
    }

    @Override
    public final Datacenter setTimeZone(final double timeZone) {
        this.timeZone = validateTimeZone(timeZone);
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
    public long getActiveHostsNumber(){
        return hostList.stream().filter(Host::isActive).count();
    }

    @Override
    public long size() {
        return hostList.size();
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
        ((List<T>)hostList).add(host);

        //Sets the Datacenter again so that the new Host is registered internally on the VmAllocationPolicy
        vmAllocationPolicy.setDatacenter(this);
        return this;
    }

    private <T extends Host> void notifyOnHostAvailableListeners(final T host) {
        onHostAvailableListeners.forEach(listener -> listener.update(HostEventInfo.of(listener, host, clock())));
    }

    @Override
    public <T extends Host> Datacenter removeHost(final T host) {
        hostList.remove(host);
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

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws UnsupportedOperationException if Datacenter's power consumption computation was not enabled before the simulation start
     */
    @Override
    public double getPower() throws UnsupportedOperationException{
        final double power = powerSupply.getPower();
        if(power < 0){
            throw new UnsupportedOperationException(
                "The power consumption for " + this +
                " cannot be computed because a DatacenterPowerSupply object was not given." +
                " Call the setPowerSupply() before the simulation start to provide one. This enables power consumption computation.");
        }

        return power;
    }

    @Override
    public Datacenter addOnHostAvailableListener(final EventListener<HostEventInfo> listener) {
        onHostAvailableListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean isMigrationsEnabled() {
        return migrationsEnabled && vmAllocationPolicy.isVmMigrationSupported();
    }

    @Override
    public final Datacenter enableMigrations() {
        if(!vmAllocationPolicy.isVmMigrationSupported()){
            LOGGER.warn(
                "{}: {}: It was requested to enable VM migrations but the {} doesn't support that.",
                getSimulation().clockStr(), getName(), vmAllocationPolicy.getClass().getSimpleName());
            return this;
        }

        this.migrationsEnabled = true;
        return this;
    }

    @Override
    public final Datacenter disableMigrations() {
        this.migrationsEnabled = false;
        return this;
    }

    @Override
    public void setPowerSupply(final DatacenterPowerSupply powerSupply) {
        this.powerSupply = powerSupply == null ? DatacenterPowerSupply.NULL : powerSupply.setDatacenter(this);
    }

    @Override
    public DatacenterPowerSupply getPowerSupply(){ return powerSupply; }

    @Override
    public double getHostSearchForMigrationDelay() {
        return hostSearchForMigrationDelay;
    }

    @Override
    public Datacenter setHostSearchRetryDelay(final double hostSearchDelay) {
        if(hostSearchDelay == 0){
            throw new IllegalArgumentException("hostSearchDelay cannot be 0. Set a positive value to define an actual delay or a negative value to indicate a new Host search must be tried as soon as possible.");
        }

        this.hostSearchForMigrationDelay = hostSearchDelay;
        return this;
    }
}
