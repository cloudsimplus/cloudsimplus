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
import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.core.CustomerEntityAbstract;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.PredicateType;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.hosts.HostSuitability;
import org.cloudbus.cloudsim.network.IcmpPacket;
import org.cloudbus.cloudsim.power.models.PowerModelDatacenter;
import org.cloudbus.cloudsim.power.models.PowerModelDatacenterSimple;
import org.cloudbus.cloudsim.resources.DatacenterStorage;
import org.cloudbus.cloudsim.resources.SanStorage;
import org.cloudbus.cloudsim.util.InvalidEventDataTypeException;
import org.cloudbus.cloudsim.util.MathUtil;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.listeners.DatacenterVmMigrationEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static org.cloudbus.cloudsim.util.BytesConversion.bitesToBytes;

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
    private double lastUnderOrOverloadedDetection = -Double.MAX_VALUE;

    /** @see #getBandwidthPercentForMigration() */
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
    private final List<EventListener<DatacenterVmMigrationEventInfo>> onVmMigrationFinishListeners;

    /** @see #getTimeZone() */
    private double timeZone;
    private Map<Vm, Host> lastMigrationMap;

    /** @see #getHostSearchRetryDelay() */
    private double hostSearchRetryDelay;

    private PowerModelDatacenter powerModel = PowerModelDatacenter.NULL;
    private long activeHostsNumber;

    /**
     * Creates a Datacenter with an empty {@link #getDatacenterStorage() storage}
     * and a {@link VmAllocationPolicySimple} by default.
     *
     * <p><b>NOTE:</b> To change such attributes, just call the respective setters.</p>
     *
     * @param simulation the CloudSim instance that represents the simulation the Entity belongs
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @see #DatacenterSimple(Simulation, List, VmAllocationPolicy, DatacenterStorage)
     */
    public DatacenterSimple(final Simulation simulation, final List<? extends Host> hostList) {
        this(simulation, hostList, new VmAllocationPolicySimple(), new DatacenterStorage());
    }

    /**
     * Creates a Datacenter with an empty {@link #getDatacenterStorage() storage}.
     *
     * @param simulation the CloudSim instance that represents the simulation the Entity belongs
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
     * @param simulation the CloudSim instance that represents the simulation the Entity belongs
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @see #DatacenterSimple(Simulation, List, VmAllocationPolicy)
     * @see #DatacenterSimple(Simulation, List, VmAllocationPolicy, DatacenterStorage)
     * @see #addHost(Host)
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
     * @param simulation the CloudSim instance that represents the simulation the Entity belongs
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @param storageList the storage list to attach to the {@link #getDatacenterStorage() datacenter storage}
     */
    public DatacenterSimple(
        final Simulation simulation,
        final List<? extends Host> hostList,
        final VmAllocationPolicy vmAllocationPolicy,
        final List<SanStorage> storageList)
    {
        this(simulation, hostList, vmAllocationPolicy, new DatacenterStorage(storageList));
    }

    /**
     * Creates a Datacenter with a given {@link #getDatacenterStorage() storage}.
     *
     * @param simulation the CloudSim instance that represents the simulation the Entity belongs
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
        setPowerModel(new PowerModelDatacenterSimple(this));

        this.onHostAvailableListeners = new ArrayList<>();
        this.onVmMigrationFinishListeners = new ArrayList<>();
        this.characteristics = new DatacenterCharacteristicsSimple(this);
        this.bandwidthPercentForMigration = DEF_BW_PERCENT_FOR_MIGRATION;
        this.migrationsEnabled = true;
        this.hostSearchRetryDelay = -1;

        this.lastMigrationMap = Collections.emptyMap();

        setVmAllocationPolicy(vmAllocationPolicy);
    }

    private void setHostList(final List<? extends Host> hostList) {
        this.hostList = requireNonNull(hostList);
        setupHosts();
    }

    private void setupHosts() {
        long lastHostId = getLastHostId();
        for (final Host host : hostList) {
            lastHostId = setupHost(host, lastHostId);
        }
    }

    private long getLastHostId() {
        return hostList.isEmpty() ? -1 : hostList.get(hostList.size()-1).getId();
    }

    protected long setupHost(final Host host, long nextId) {
        nextId = Math.max(nextId, -1);
        if(host.getId() < 0) {
            host.setId(++nextId);
        }

        host.setSimulation(getSimulation()).setDatacenter(this);
        host.setActive(((HostSimple)host).isActivateOnDatacenterStartup());
        return nextId;
    }

    @Override
    public void processEvent(final SimEvent evt) {
        if (processCloudletEvents(evt) || processVmEvents(evt) || processNetworkEvents(evt) || processHostEvents(evt)) {
            return;
        }

        LOGGER.trace("{}: {}: Unknown event {} received.", getSimulation().clockStr(), this, evt.getTag());
    }

    private boolean processHostEvents(final SimEvent evt) {
        if (evt.getTag() == CloudSimTag.HOST_ADD) {
            processHostAdditionRequest(evt);
            return true;
        } else if (evt.getTag() == CloudSimTag.HOST_REMOVE) {
            processHostRemovalRequest(evt);
            return true;
        } else if (evt.getTag() == CloudSimTag.HOST_POWER_ON || evt.getTag() == CloudSimTag.HOST_POWER_OFF) {
            final HostSimple host = (HostSimple)evt.getData();
            host.processActivation(evt.getTag() == CloudSimTag.HOST_POWER_ON);
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
        if(Host.NULL.equals(host)) {
            LOGGER.warn(
                "{}: {}: Host {} was not found to be removed from {}.",
                getSimulation().clockStr(), getClass().getSimpleName(), hostId, this);
            return;
        }

        final var fault = new HostFaultInjection(this);
        try {
            LOGGER.error(
                "{}: {}: Host {} removed from {} due to injected failure.",
                getSimulation().clockStr(), getClass().getSimpleName(), host.getId(), this);
            fault.generateHostFault(host);
        } finally{
            fault.shutdown();
        }

        /*If the Host was found in this Datacenter, cancel the message sent to others
        * Datacenters to try to find the Host for removal.*/
        getSimulation().cancelAll(
            getSimulation().getCloudInfoService(),
            evt -> MathUtil.same(evt.getTime(), srcEvt.getTime()) &&
                   evt.getTag() == CloudSimTag.HOST_REMOVE &&
                   (long)evt.getData() == host.getId());
    }

    private Optional<Host> getHostFromHostEvent(final SimEvent evt) {
        if(evt.getData() instanceof Host host){
            return Optional.of(host);
        }

        return Optional.empty();
    }

    private boolean processNetworkEvents(final SimEvent evt) {
        if (evt.getTag() == CloudSimTag.ICMP_PKT_SUBMIT) {
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
        return switch (evt.getTag()) {
            case VM_CREATE_ACK -> processVmCreate(evt);
            case VM_VERTICAL_SCALING  -> requestVmVerticalScaling(evt);
            case VM_DESTROY -> processVmDestroy(evt, false);
            case VM_DESTROY_ACK -> processVmDestroy(evt, true);
            case VM_MIGRATE -> finishVmMigration(evt, false);
            case VM_MIGRATE_ACK -> finishVmMigration(evt, true);
            case VM_UPDATE_CLOUDLET_PROCESSING -> updateCloudletProcessing() != Double.MAX_VALUE;
            default -> false;
        };
    }

    /**
     * Process a {@link CloudSimTag#VM_VERTICAL_SCALING} request,
     * trying to scale a Vm resource.
     *
     * @param evt the received  {@link CloudSimTag#VM_VERTICAL_SCALING} event
     * @return true if the Vm was scaled, false otherwise
     */
    private boolean requestVmVerticalScaling(final SimEvent evt) {
        if (evt.getData() instanceof VerticalVmScaling scaling) {
            return vmAllocationPolicy.scaleVmVertically(scaling);
        }

        throw new InvalidEventDataTypeException(evt, "VM_VERTICAL_SCALING", "VerticalVmScaling");
    }

    private boolean processCloudletEvents(final SimEvent evt) {
        return switch (evt.getTag()) {
            // New Cloudlet arrives
            case CLOUDLET_SUBMIT -> processCloudletSubmit(evt, false);
            // New Cloudlet arrives, but the sender asks for an ack
            case CLOUDLET_SUBMIT_ACK -> processCloudletSubmit(evt, true);
            // Cancels a previously submitted Cloudlet
            case CLOUDLET_CANCEL -> processCloudlet(evt, CloudSimTag.CLOUDLET_CANCEL);
            // Pauses a previously submitted Cloudlet
            case CLOUDLET_PAUSE -> processCloudlet(evt, CloudSimTag.CLOUDLET_PAUSE);
            // Pauses a previously submitted Cloudlet, but the sender asks for an acknowledgement
            case CLOUDLET_PAUSE_ACK -> processCloudlet(evt, CloudSimTag.CLOUDLET_PAUSE_ACK);
            // Resumes a previously submitted Cloudlet
            case CLOUDLET_RESUME -> processCloudlet(evt, CloudSimTag.CLOUDLET_RESUME);
            // Resumes a previously submitted Cloudlet, but the sender asks for an acknowledgement
            case CLOUDLET_RESUME_ACK -> processCloudlet(evt, CloudSimTag.CLOUDLET_RESUME_ACK);
            default -> false;
        };
    }

    /**
     * Processes a ping request.
     *
     * @param evt information about the event just happened
     */
    protected void processPingRequest(final SimEvent evt) {
        final IcmpPacket pkt = (IcmpPacket) evt.getData();
        pkt.setTag(CloudSimTag.ICMP_PKT_RETURN);
        pkt.setDestination(pkt.getSource());

        // returns the packet to the sender
        sendNow(pkt.getSource(), CloudSimTag.ICMP_PKT_RETURN, pkt);
    }

    /**
     * Processes a Cloudlet based on the event type.
     * @param evt information about the event just happened
     * @param tag event tag
     * @return
     */
    protected boolean processCloudlet(final SimEvent evt, final CloudSimTag tag) {
        final Cloudlet cloudlet;
        try {
            cloudlet = (Cloudlet) evt.getData();
        } catch (ClassCastException e) {
            LOGGER.error("{}: Error in processing Cloudlet: {}", super.getName(), e.getMessage());
            return false;
        }

        return switch (tag) {
            case CLOUDLET_CANCEL -> processCloudletCancel(cloudlet);
            case CLOUDLET_PAUSE -> processCloudletPause(cloudlet, false);
            case CLOUDLET_PAUSE_ACK -> processCloudletPause(cloudlet, true);
            case CLOUDLET_RESUME -> processCloudletResume(cloudlet, false);
            case CLOUDLET_RESUME_ACK -> processCloudletResume(cloudlet, true);
            default -> {
                LOGGER.trace(
                    "{}: Unable to handle a request from {} with event tag = {}",
                    this, evt.getSource().getName(), evt.getTag());
                yield false;
            }
        };
    }

    /**
     * Processes the submission of a Cloudlet by a DatacenterBroker.
     * @param evt information about the event just happened
     * @param ack indicates if the event's sender expects to receive an acknowledgement
     * @return
     */
    protected boolean processCloudletSubmit(final SimEvent evt, final boolean ack) {
        final var cloudlet = (Cloudlet) evt.getData();
        if (cloudlet.isFinished()) {
            notifyBrokerAboutAlreadyFinishedCloudlet(cloudlet, ack);
            return false;
        }

        submitCloudletToVm(cloudlet, ack);
        return true;
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

        final var scheduler = cloudlet.getVm().getCloudletScheduler();
        final double estimatedFinishTime = scheduler.cloudletSubmit(cloudlet, fileTransferTime);

        // if this cloudlet is in the exec queue
        if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
            send(this,
                getCloudletProcessingUpdateInterval(estimatedFinishTime),
                CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING);
        }

        ((CustomerEntityAbstract)cloudlet).setCreationTime();
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
     * @param cloudlet cloudlet to be resumed
     * @param ack indicates if the event's sender expects to receive an
     * @return
     */
    protected boolean processCloudletResume(final Cloudlet cloudlet, final boolean ack) {
        final double estimatedFinishTime = cloudlet.getVm().getCloudletScheduler().cloudletResume(cloudlet);

        if (estimatedFinishTime > 0.0 && estimatedFinishTime > clock()) {
            schedule(this,
                getCloudletProcessingUpdateInterval(estimatedFinishTime),
                CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING);
        }

        sendAck(ack, cloudlet, CloudSimTag.CLOUDLET_RESUME_ACK);
        return true;
    }

    private void sendAck(final boolean ack, final Cloudlet cloudlet, final CloudSimTag tag) {
        if (ack) {
            sendNow(cloudlet.getBroker(), tag, cloudlet);
        }
    }

    /**
     * Processes a Cloudlet pause request.
     *  @param cloudlet cloudlet to be paused
     * @param ack indicates if the event's sender expects to receive an
     * @return
     */
    protected boolean processCloudletPause(final Cloudlet cloudlet, final boolean ack) {
        cloudlet.getVm().getCloudletScheduler().cloudletPause(cloudlet);
        sendAck(ack, cloudlet, CloudSimTag.CLOUDLET_PAUSE_ACK);
        return true;
    }

    /**
     * Processes a Cloudlet cancel request.
     *
     * @param cloudlet cloudlet to be canceled
     * @return
     */
    protected boolean processCloudletCancel(final Cloudlet cloudlet) {
        cloudlet.getVm().getCloudletScheduler().cloudletCancel(cloudlet);
        sendNow(cloudlet.getBroker(), CloudSimTag.CLOUDLET_CANCEL, cloudlet);
        return true;
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
        final var vm = (Vm) evt.getData();

        final boolean hostAllocatedForVm = vmAllocationPolicy.allocateHostForVm(vm).fully();
        if (hostAllocatedForVm) {
            vm.updateProcessing(vm.getHost().getVmScheduler().getAllocatedMips(vm));
        }

        /* Acknowledges that the request was received by the Datacenter,
          (the broker is expecting that if the Vm was created or not). */
        send(vm.getBroker(), getSimulation().getMinTimeBetweenEvents(), CloudSimTag.VM_CREATE_ACK, vm);

        return hostAllocatedForVm;
    }

    /**
     * Process the event sent by a Broker, requesting the destruction of a given VM
     * created in this Datacenter. This Datacenter may send,
     * upon request, the status back to the Broker.
     *
     * @param evt information about the event just happened
     * @param ack indicates if the event's sender expects to receive an
     * @return
     */
    protected boolean processVmDestroy(final SimEvent evt, final boolean ack) {
        final var vm = (Vm) evt.getData();
        vmAllocationPolicy.deallocateHostForVm(vm);

        if (ack) {
            sendNow(vm.getBroker(), CloudSimTag.VM_DESTROY_ACK, vm);
        }

        vm.getBroker().requestShutdownWhenIdle();
        if(getSimulation().isAborted() || getSimulation().isAbortRequested()) {
            return true;
        }

        final String warningMsg = generateNotFinishedCloudletsWarning(vm);
        final String msg = String.format(
                "%s: %s: %s destroyed on %s. %s",
                getSimulation().clockStr(), getClass().getSimpleName(), vm, vm.getHost(), warningMsg);
        if(warningMsg.isEmpty() || getSimulation().isTerminationTimeSet())
            LOGGER.info(msg);
        else LOGGER.warn(msg);
        return true;
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
     * acknowledgement message when the event finishes being processed
     * @see CloudSimTag#VM_MIGRATE
     * @return
     */
    protected boolean finishVmMigration(final SimEvent evt, final boolean ack) {
        if (!(evt.getData() instanceof Map.Entry<?, ?>)) {
            throw new InvalidEventDataTypeException(evt, "VM_MIGRATE", "Map.Entry<Vm, Host>");
        }

        final var entry = (Map.Entry<Vm, Host>) evt.getData();

        final Vm vm = entry.getKey();
        final Host sourceHost = vm.getHost();
        final Host targetHost = entry.getValue();

        //Updates processing of all Hosts to get their latest state before migrating VMs
        updateHostsProcessing();

        //De-allocates the VM on the source Host (where it is migrating out)
        vmAllocationPolicy.deallocateHostForVm(vm);

        targetHost.removeMigratingInVm(vm);
        final HostSuitability suitability = vmAllocationPolicy.allocateHostForVm(vm, targetHost);
        if(suitability.fully()) {
            ((VmSimple)vm).updateMigrationFinishListeners(targetHost);
            /*When the VM is destroyed from the source host, it's removed from the vmExecList.
            After migration, we need to add it again.*/
            vm.getBroker().getVmExecList().add(vm);

            if (ack) {
                sendNow(evt.getSource(), CloudSimTag.VM_CREATE_ACK, vm);
            }
        }

        final SimEvent event = getSimulation().findFirstDeferred(this, new PredicateType(CloudSimTag.VM_MIGRATE));
        if (event == null || event.getTime() > clock()) {
            //Updates processing of all Hosts again to get their latest state after the VMs migrations
            updateHostsProcessing();
        }

        if (suitability.fully())
            LOGGER.info("{}: Migration of {} from {} to {} is completed.", getSimulation().clockStr(), vm, sourceHost, targetHost);
        else LOGGER.error(
            "{}: {}: Allocation of {} to the destination {} failed due to {}!",
            getSimulation().clockStr(), this, vm, targetHost, suitability);

        onVmMigrationFinishListeners.forEach(listener -> listener.update(DatacenterVmMigrationEventInfo.of(listener, vm, suitability)));
        return true;
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

        sendNow(cloudlet.getBroker(), CloudSimTag.CLOUDLET_RETURN, cloudlet);
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

        sendNow(cloudlet.getBroker(), CloudSimTag.CLOUDLET_SUBMIT_ACK, cloudlet);
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
    protected double updateHostsProcessing() {
        double nextSimulationDelay = Double.MAX_VALUE;
        for (final Host host : getHostList()) {
            final double delay = host.updateProcessing(clock());
            nextSimulationDelay = Math.min(delay, nextSimulationDelay);
        }

        // Guarantees a minimal interval before scheduling the event
        final double minTimeBetweenEvents = getSimulation().getMinTimeBetweenEvents()+0.01;
        nextSimulationDelay = nextSimulationDelay == 0 ? nextSimulationDelay : Math.max(nextSimulationDelay, minTimeBetweenEvents);

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
            schedule(nextSimulationDelay, CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING);
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

        lastMigrationMap = vmAllocationPolicy.getOptimizedAllocationMap(getVmList());
        for (final Map.Entry<Vm, Host> entry : lastMigrationMap.entrySet()) {
            requestVmMigration(entry.getKey(), entry.getValue());
        }

        if(areThereUnderOrOverloadedHostsAndMigrationIsSupported()){
            lastUnderOrOverloadedDetection = clock();
        }
    }



    /**
     * Indicates if it's time to check if suitable Hosts are available to migrate VMs
     * from under or overload Hosts.
     * @return
     */
    private boolean isTimeToSearchForSuitableHosts(){
        final double elapsedSecs = clock() - lastUnderOrOverloadedDetection;
        return isMigrationsEnabled() && elapsedSecs >= hostSearchRetryDelay;
    }

    private boolean areThereUnderOrOverloadedHostsAndMigrationIsSupported(){
        if(vmAllocationPolicy instanceof VmAllocationPolicyMigration migrationPolicy){
            return migrationPolicy.areHostsUnderOrOverloaded();
        }

        return false;
    }

    @Override
    public void requestVmMigration(final Vm sourceVm) {
        requestVmMigration(sourceVm, Host.NULL);
    }

    @Override
    public void requestVmMigration(final Vm sourceVm, Host targetHost) {
        //If Host.NULL is given, it must try to find a target host
        if(Host.NULL.equals(targetHost)){
            targetHost = vmAllocationPolicy.findHostForVm(sourceVm).orElse(Host.NULL);
        }

        //If a host couldn't be found yet
        if(Host.NULL.equals(targetHost)) {
            LOGGER.warn("{}: {}: No suitable host found for {} in {}", sourceVm.getSimulation().clockStr(), getClass().getSimpleName(), sourceVm, this);
            return;
        }

        final Host sourceHost = sourceVm.getHost();
        final double delay = timeToMigrateVm(sourceVm, targetHost);
        final String msg1 =
            Host.NULL.equals(sourceHost) ?
                String.format("%s to %s", sourceVm, targetHost) :
                String.format("%s from %s to %s", sourceVm, sourceHost, targetHost);

        final String currentTime = getSimulation().clockStr();
        final var fmt = "It's expected to finish in %.2f seconds, considering the %.0f%% of bandwidth allowed for migration and the VM RAM size.";
        final String msg2 = String.format(fmt, delay, getBandwidthPercentForMigration()*100);
        LOGGER.info("{}: {}: Migration of {} is started. {}", currentTime, getName(), msg1, msg2);

        if(targetHost.addMigratingInVm(sourceVm)) {
            sourceHost.addVmMigratingOut(sourceVm);
            send(this, delay, CloudSimTag.VM_MIGRATE, new TreeMap.SimpleEntry<>(sourceVm, targetHost));
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
        return vm.getRam().getCapacity() / bitesToBytes(targetHost.getBw().getCapacity() * getBandwidthPercentForMigration());
    }

    @Override
    public void shutdown() {
        super.shutdown();
        LOGGER.info("{}: {} is shutting down...", getSimulation().clockStr(), getName());
    }

    @Override
    protected void startInternal() {
        LOGGER.info("{}: {} is starting...", getSimulation().clockStr(), getName());
        hostList.stream()
                .filter(not(Host::isActive))
                .map(host -> (HostSimple)host)
                .forEach(host -> host.setActive(host.isActivateOnDatacenterStartup()));
        sendNow(getSimulation().getCloudInfoService(), CloudSimTag.DC_REGISTRATION_REQUEST, this);
    }

    @Override
    public <T extends Host> List<T> getHostList() {
        return (List<T>)Collections.unmodifiableList(hostList);
    }

    @Override
    public Stream<? extends Host> getActiveHostStream() {
        return hostList.stream().filter(Host::isActive);
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
                getHostList()
                    .stream()
                    .map(Host::getVmList)
                    .flatMap(List::stream)
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
        return activeHostsNumber;
    }

    /**
     * Update the number of active Hosts inside the datacenter
     */
    public void updateActiveHostsNumber(final Host host){
        activeHostsNumber += host.isActive() ? 1 : -1;
    }

    @Override
    public long size() {
        return hostList.size();
    }

    @Override
    public Host getHostById(final long id) {
        return hostList.stream().filter(host -> host.getId() == id).findFirst().map(host -> (Host)host).orElse(Host.NULL);
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

        setupHost(host, getLastHostId());
        ((List<T>)hostList).add(host);
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

    @Override
    public Datacenter addOnHostAvailableListener(final EventListener<HostEventInfo> listener) {
        onHostAvailableListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public Datacenter addOnVmMigrationFinishListener(final EventListener<DatacenterVmMigrationEventInfo> listener) {
        onVmMigrationFinishListeners.add(requireNonNull(listener));
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
    public PowerModelDatacenter getPowerModel() {
        return powerModel;
    }

    @Override
    public final void setPowerModel(final PowerModelDatacenter powerModel) {
        requireNonNull(powerModel,
            "powerModel cannot be null. You could provide a " +
            PowerModelDatacenter.class.getSimpleName() + ".NULL instead");

        if(powerModel.getDatacenter() != null && powerModel.getDatacenter() != Datacenter.NULL && !this.equals(powerModel.getDatacenter())){
            throw new IllegalStateException("The given PowerModel is already assigned to another Datacenter. Each Datacenter must have its own PowerModel instance.");
        }

        this.powerModel = powerModel;
    }

    @Override
    public double getHostSearchRetryDelay() {
        return hostSearchRetryDelay;
    }

    @Override
    public Datacenter setHostSearchRetryDelay(final double delay) {
        if(delay == 0){
            throw new IllegalArgumentException("hostSearchRetryDelay cannot be 0. Set a positive value to define an actual delay or a negative value to indicate a new Host search must be tried as soon as possible.");
        }

        this.hostSearchRetryDelay = delay;
        return this;
    }
}
