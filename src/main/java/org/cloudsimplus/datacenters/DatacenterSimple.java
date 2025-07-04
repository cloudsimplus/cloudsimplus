/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.datacenters;

import lombok.*;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;
import org.cloudsimplus.allocationpolicies.migration.VmAllocationPolicyMigration;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.*;
import org.cloudsimplus.core.events.PredicateType;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.DatacenterCharacteristics.Distribution;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.listeners.DatacenterVmMigrationEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.network.IcmpPacket;
import org.cloudsimplus.power.models.PowerModelDatacenter;
import org.cloudsimplus.power.models.PowerModelDatacenterSimple;
import org.cloudsimplus.resources.DatacenterStorage;
import org.cloudsimplus.resources.SanStorage;
import org.cloudsimplus.util.Conversion;
import org.cloudsimplus.util.InvalidEventDataTypeException;
import org.cloudsimplus.util.MathUtil;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmAbstract;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static org.cloudsimplus.datacenters.DatacenterCharacteristics.Distribution.PRIVATE;
import static org.cloudsimplus.util.BytesConversion.bitsToBytes;

/**
 * Implements the basic features of a Virtualized Cloud Datacenter,
 * processing VM queries (i.e., handling of VMs).
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public non-sealed class DatacenterSimple extends CloudSimEntity implements Datacenter {
    @Getter @EqualsAndHashCode.Include
    private DatacenterCharacteristics characteristics;

    @Getter
    private double timeZone;

    @Getter
    private DatacenterStorage datacenterStorage;

    private List<? extends Host> hostList;

    @Getter
    private long activeHostsNumber;

    @Getter
    private PowerModelDatacenter powerModel = PowerModelDatacenter.NULL;

    @Getter
    private VmAllocationPolicy vmAllocationPolicy;

    @Getter
    private double hostSearchRetryDelay;

    /**
     * The last time some Host on the Datacenter was under or overloaded.
     *
     * <p>{@link Double#MIN_VALUE} is surprisingly not a negative number
     * (you can confirm that using {@link Math#signum(double)}).
     * Initializing this attribute with a too small value makes that the first
     * time an under or overload condition is detected,
     * it will try immediately to find suitable Hosts for migration.</p>
     */
    private double lastUnderOrOverloadedDetection = -Double.MAX_VALUE;

    @Getter
    private double schedulingInterval;

    /** The last time some cloudlet was processed in the Datacenter. */
    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED)
    private double lastProcessTime;

    /**
     * Indicates if migrations are enabled or not.
     */
    private boolean migrationsEnabled;

    @Getter
    private double bandwidthPercentForMigration;

    private final List<EventListener<HostEventInfo>> onHostAvailableListeners;
    private final List<EventListener<DatacenterVmMigrationEventInfo>> onVmMigrationFinishListeners;

    private Map<Vm, Host> lastMigrationMap;

    /**
     * Creates a Datacenter with an empty {@link #getDatacenterStorage() storage}
     * and a {@link VmAllocationPolicySimple} by default.
     *
     * <p><b>NOTE:</b> To change such attributes, just call the respective setters.</p>
     *
     * @param simulation the {@link CloudSimPlus} instance that represents the simulation the Entity belongs
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @see #DatacenterSimple(Simulation, List, VmAllocationPolicy, DatacenterStorage)
     */
    public DatacenterSimple(final Simulation simulation, final List<? extends Host> hostList) {
        this(simulation, hostList, new VmAllocationPolicySimple(), new DatacenterStorage());
    }

    /**
     * Creates a Datacenter with an empty {@link #getDatacenterStorage() storage}.
     *
     * @param simulation the {@link CloudSimPlus} instance that represents the simulation the Entity belongs
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @param vmAllocationPolicy the policy to be used to allocate {@link Vm}s into {@link Host}s
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
     * @param simulation the {@link CloudSimPlus} instance that represents the simulation the Entity belongs
     * @param vmAllocationPolicy the policy to be used to allocate {@link Vm}s into {@link Host}s
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
     * @param simulation the {@link CloudSimPlus} instance that represents the simulation the Entity belongs
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @param vmAllocationPolicy the policy to be used to allocate {@link Vm}s into {@link Host}s
     * @param storageList the {@link SanStorage} list to attach to the {@link #getDatacenterStorage() datacenter storage}
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
     * @param simulation the {@link CloudSimPlus} instance that represents the simulation the Entity belongs
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

    private void setHostList(@NonNull final List<? extends Host> hostList) {
        this.hostList = hostList;
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

        host.setSimulation(getSimulation());
        host.setDatacenter(this);
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
     * This way, a new Host can be made available to the Datacenter, expanding its capacity.
     * @param evt the event to process
     */
    private void processHostAdditionRequest(final SimEvent evt) {
        getHostFromHostEvent(evt).ifPresent(host -> {
            this.addHost(host);
            LOGGER.info(
                "{}: {}: Host {} added to {} during simulation runtime",
                getSimulation().clockStr(), this, host.getId(), this);
            //Notification must be sent only for Hosts added during simulation runtime
            notifyOnHostAvailableListeners(host);
        });
    }

    /**
     * Process a Host removal request received during simulation runtime.
     * This way, the Datacenter capacity is decreased by removing one Host.
     * @param srcEvt the event to process
     */
    private void processHostRemovalRequest(final SimEvent srcEvt) {
        final long hostId = (long)srcEvt.getData();
        final Host host = getHostById(hostId);
        if(Host.NULL.equals(host)) {
            LOGGER.warn(
                "{}: {}: Host {} was not found to be removed from {}.",
                getSimulation().clockStr(), this, hostId, this);
            return;
        }

        final var fault = new HostFaultInjection(this);
        try {
            LOGGER.error(
                "{}: {}: Host {} removed from {} due to injected failure.",
                getSimulation().clockStr(), this, host.getId(), this);
            fault.generateHostFault(host);
        } finally{
            fault.shutdown();
        }

        /*If the Host was found in this Datacenter, cancel the message sent to others
        * Datacenters to try to find the Host for removal.*/
        getSimulation().cancelAll(
            getSimulation().getCis(),
            evt -> MathUtil.same(evt.getTime(), srcEvt.getTime()) &&
                   evt.getTag() == CloudSimTag.HOST_REMOVE &&
                   (long)evt.getData() == host.getId());
    }

    private Optional<Host> getHostFromHostEvent(final SimEvent evt) {
        return evt.getData() instanceof Host h ? Optional.of(h) : Optional.empty();
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
            case CloudSimTag.VM_CREATE_ACK -> processVmCreate(evt);
            case CloudSimTag.VM_VERTICAL_SCALING  -> requestVmVerticalScaling(evt);
            case CloudSimTag.VM_DESTROY -> processVmDestroy(evt);
            case CloudSimTag.VM_MIGRATE -> finishVmMigration(evt, false);
            case CloudSimTag.VM_MIGRATE_ACK -> finishVmMigration(evt, true);
            case CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING -> updateCloudletProcessing() != Double.MAX_VALUE;
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
            case CloudSimTag.CLOUDLET_SUBMIT -> processCloudletSubmit(evt, false);
            // New Cloudlet arrives, but the sender asks for an ack
            case CloudSimTag.CLOUDLET_SUBMIT_ACK -> processCloudletSubmit(evt, true);
            // Cancels a previously submitted Cloudlet
            case CloudSimTag.CLOUDLET_CANCEL -> processCloudlet(evt, CloudSimTag.CLOUDLET_CANCEL);
            // Pauses a previously submitted Cloudlet
            case CloudSimTag.CLOUDLET_PAUSE -> processCloudlet(evt, CloudSimTag.CLOUDLET_PAUSE);
            // Pauses a previously submitted Cloudlet, but the sender asks for an acknowledgement
            case CloudSimTag.CLOUDLET_PAUSE_ACK -> processCloudlet(evt, CloudSimTag.CLOUDLET_PAUSE_ACK);
            // Resumes a previously submitted Cloudlet
            case CloudSimTag.CLOUDLET_RESUME -> processCloudlet(evt, CloudSimTag.CLOUDLET_RESUME);
            // Resumes a previously submitted Cloudlet, but the sender asks for an acknowledgement
            case CloudSimTag.CLOUDLET_RESUME_ACK -> processCloudlet(evt, CloudSimTag.CLOUDLET_RESUME_ACK);
            default -> false;
        };
    }

    /**
     * Processes a ping request.
     *
     * @param evt information about the event just happened
     */
    protected void processPingRequest(final SimEvent evt) {
        if(evt.getData() instanceof IcmpPacket pkt){
            pkt.setTag(CloudSimTag.ICMP_PKT_RETURN);
            pkt.setDestination(pkt.getSource());

            // returns the packet to the sender
            sendNow(pkt.getSource(), CloudSimTag.ICMP_PKT_RETURN, pkt);
        }

        throw new InvalidEventDataTypeException(evt, "ICMP_PKT_SUBMIT", IcmpPacket.class.getName());
    }

    /**
     * Processes a Cloudlet based on the event type.
     * @param evt information about the event just happened
     * @param tag event tag
     * @return true if the event was processed, false otherwise
     */
    protected boolean processCloudlet(final SimEvent evt, final int tag) {
        if (evt.getData() instanceof Cloudlet cloudlet){
            return switch (tag) {
                case CloudSimTag.CLOUDLET_CANCEL -> processCloudletCancel(cloudlet);
                case CloudSimTag.CLOUDLET_PAUSE -> processCloudletPause(cloudlet, false);
                case CloudSimTag.CLOUDLET_PAUSE_ACK -> processCloudletPause(cloudlet, true);
                case CloudSimTag.CLOUDLET_RESUME -> processCloudletResume(cloudlet, false);
                case CloudSimTag.CLOUDLET_RESUME_ACK -> processCloudletResume(cloudlet, true);
                default -> {
                    LOGGER.trace(
                        "{}: Unable to handle a request from {} with event tag = {}",
                        this, evt.getSource().getName(), evt.getTag());
                    yield false;
                }
            };
        }

        throw new InvalidEventDataTypeException(evt, "CLOUDLET Tags", Cloudlet.class.getName());
    }

    /**
     * Processes the submission of a Cloudlet by a DatacenterBroker.
     * @param evt information about the event just happened
     * @param ack indicates if the event's sender expects to receive an acknowledgement
     * @return true if the event was processed, false otherwise
     */
    protected boolean processCloudletSubmit(final SimEvent evt, final boolean ack) {
        if (evt.getData() instanceof Cloudlet cloudlet){
            if (cloudlet.isFinished()) {
                notifyBrokerAboutAlreadyFinishedCloudlet(cloudlet, ack);
                return false;
            }

            submitCloudletToVm(cloudlet, ack);
            return true;
        }

        throw new InvalidEventDataTypeException(evt, "CLOUDLET_SUBMIT Tags", Cloudlet.class.getName());
    }

    /**
     * Submits a cloudlet to be executed inside its VM.
     *
     * @param cloudlet the cloudlet to the executed
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     */
    private void submitCloudletToVm(final Cloudlet cloudlet, final boolean ack) {
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
     * {@return the next time Cloudlets processing will be updated (a relative delay from the current simulation time)}
     * This is the minimum value between the {@link #getSchedulingInterval()} and the given time
     * (if the scheduling interval is enabled, i.e., if it's greater than 0),
     * which indicates when the next update of Cloudlets processing has to be performed.
     *
     * @param nextFinishingCloudletTime the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
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
         * is updated is multiple of the scheduling interval.
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
     * @param ack indicates if the event's sender expects to receive a confirmation (ack) message
     * @return true to indicate the event was processed
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

    private void sendAck(final boolean ack, final Cloudlet cloudlet, final int tag) {
        if (ack) {
            sendNow(cloudlet.getBroker(), tag, cloudlet);
        }
    }

    /**
     * Processes a Cloudlet pause request.
     * @param cloudlet cloudlet to be paused
     * @param ack indicates if the event's sender expects to receive a confirmation (ack) message
     * @return true to indicate the event was processed
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
     * @return true to indicate the event was processed
     */
    protected boolean processCloudletCancel(final Cloudlet cloudlet) {
        cloudlet.getVm().getCloudletScheduler().cloudletCancel(cloudlet);
        sendNow(cloudlet.getBroker(), CloudSimTag.CLOUDLET_CANCEL, cloudlet);
        return true;
    }

    /**
     * Process the event for a Broker which wants to create a VM in this
     * Datacenter. This Datacenter will then send the status back to the Broker.
     *
     * @param evt information about the event just happened
     * @return true if some requested VM was created into some host; false otherwise
     * @see CloudSimTag#VM_CREATE_ACK
     */
    @SuppressWarnings("unchecked")
    private boolean processVmCreate(final SimEvent evt) {
        boolean created = false;
        if(evt.getData() instanceof Vm vm){
            vmAllocationPolicy.allocateHostForVm(vm);
            created = updateVmProcessing(vm);
        }
        else if(evt.getData() instanceof List<?> list){
            if(list.isEmpty())
                return false;

            if((list.get(0) instanceof Vm)) { // ensures safe cast
                final var suitabilities = vmAllocationPolicy.allocateHostForVm((List<Vm>)list);
                //Cannot use short-circuit stream operations (such as anyMatch) since all VMs must send an ack
                created = suitabilities
                        .stream()
                        .map(HostSuitability::getVm)
                        .map(this::updateVmProcessing)
                        .mapToInt(Conversion::boolToInt).sum() > 0;
            }
        } else throw new InvalidEventDataTypeException(evt, "VM_CREATE Tags", "Vm or List<Vm>");

        final var vm = VmAbstract.getFirstVm(evt.getData());
        /* Acknowledges that the request was received by the Datacenter,
        (the broker is expecting that if the Vm was created or not). */
        send(vm.getBroker(), getSimulation().getMinTimeBetweenEvents(), CloudSimTag.VM_CREATE_ACK, evt.getData());
        return created;
    }

    /**
     * Returns an ack to the broker which may confirm if the VM was created or not,
     * by checking its `created` attribute.
     * @param vm the VM whose creation request was processed
     * @return true or false to indicate if the VM was created or not
     */
    private boolean updateVmProcessing(final Vm vm) {
        if(vm.isCreated())
            vm.updateProcessing(vm.getHost().getVmScheduler().getAllocatedMips(vm));
        return vm.isCreated();
    }

    /**
     * Process the event sent by a Broker, requesting the destruction of a given VM
     * created in this Datacenter. This Datacenter may send,
     * upon request, the status back to the Broker.
     *
     * @param evt information about the event just happened
     * @return true or false to indicate the event was processed or not
     */
    protected boolean processVmDestroy(final SimEvent evt) {
        if(evt.getData() instanceof Vm vm){
            vmAllocationPolicy.deallocateHostForVm(vm);

            vm.getBroker().requestShutdownWhenIdle();
            if(getSimulation().isAborted() || getSimulation().isAbortRequested()) {
                return true;
            }

            final String warningMsg = generateNotFinishedCloudletsWarning(vm);
            final String msg =
                "%s: %s: %s destroyed on %s. %s"
                    .formatted(getSimulation().clockStr(), this, vm, vm.getHost(), warningMsg);
            if(warningMsg.isEmpty() || getSimulation().isTerminationTimeSet())
                LOGGER.info(msg);
            else LOGGER.warn(msg);
            return true;
        }

        throw new InvalidEventDataTypeException(evt, "VM_DESTROY Tags", Vm.class.getName());
    }

    private String generateNotFinishedCloudletsWarning(final Vm vm) {
        final int cloudletsNoFinished = vm.getCloudletScheduler().getCloudletList().size();
        /* If the VM is in migration and was destroyed, it's a non-live migration from/to a public-cloud DC.
         * This way, it's forced to be shut down and no warning must be shown. */
        if(cloudletsNoFinished == 0 || vm.isInMigration()) {
            return "";
        }

        final var options =
            """
            Some events may have been missed. You can try:
            (a) decreasing CloudSim's minTimeBetweenEvents and/or Datacenter's schedulingInterval attribute;
            (b) increasing broker's Vm destruction delay for idle VMs if you set it to zero;
            (c) defining Cloudlets with smaller length (your Datacenter's scheduling interval may be smaller than the time to finish some Cloudlets).
            """;

        return "It had a total of %d cloudlets (running + waiting). %s".formatted(cloudletsNoFinished, options);
    }

    /**
     * Finishes the process of migrating a VM.
     *
     * @param evt information about the event just happened
     * @param ack indicates if the event's sender expects to receive a confirmation (ack) message
     * @return true or false to indicate the event was processed or not
     * @see CloudSimTag#VM_MIGRATE
     */
    protected boolean finishVmMigration(final SimEvent evt, final boolean ack) {
        if (!(evt.getData() instanceof Map.Entry<?, ?>)) {
            throw new InvalidEventDataTypeException(evt, "VM_MIGRATE", "Map.Entry<VmAbstract, Host>");
        }

        final var entry = (Map.Entry<VmAbstract, Host>) evt.getData();
        final var vm = entry.getKey();
        final Host sourceHost = vm.getHost();
        final Host targetHost = entry.getValue();

        //Updates processing of all Hosts to get their latest state before migrating VMs
        updateHostsProcessing();

        //De-allocates the VM on the source Host (where it is migrating out)
        vmAllocationPolicy.deallocateHostForVm(vm);

        targetHost.removeMigratingInVm(vm);
        final HostSuitability suitability = vmAllocationPolicy.allocateHostForVm(vm, targetHost);
        if(suitability.fully()) {
            vm.updateMigrationFinishListeners(targetHost);
            /*When the VM is destroyed from the source host, it's removed from the vmExecList.
            After migration, we need to add it again.*/
            vm.getBroker().getVmExecList().add(vm);

            if (ack) {
                sendNow(evt.getSource(), CloudSimTag.VM_CREATE_ACK, vm);
            }
        }

        final var event = getSimulation().findFirstDeferred(this, new PredicateType(CloudSimTag.VM_MIGRATE));
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
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter receives the cloudlet submission
     */
    private void notifyBrokerAboutAlreadyFinishedCloudlet(final Cloudlet cloudlet, final boolean ack) {
        LOGGER.warn(
            "{}: {} owned by {} is already completed/finished. It won't be executed again.",
            getName(), cloudlet, cloudlet.getBroker());

        /*
         NOTE: If a Cloudlet has finished, then it won't be processed.
         So, if ack is required, this method sends back a result.
         If ack is not required, this method doesn't send back a result.
         Hence, this might cause CloudSimPlus to be hanged since waiting
         for this Cloudlet back.
        */
        sendCloudletSubmitAckToBroker(cloudlet, ack);

        sendNow(cloudlet.getBroker(), CloudSimTag.CLOUDLET_RETURN, cloudlet);
    }

    /**
     * Sends an ACK to the DatacenterBroker that submitted the Cloudlet for execution
     * for responding to the reception of the submission request,
     * informing if the cloudlet was created or not.
     *
     * <p>The ACK is sent just if the Broker is waiting for it.
     * That condition is indicated in the ack parameter.</p>
     *
     * @param cloudlet the cloudlet to respond to DatacenterBroker if it was created or not
     * @param ack indicates if the Broker is waiting for an ACK after the Datacenter
     * receives the cloudlet submission
     */
    private void sendCloudletSubmitAckToBroker(final Cloudlet cloudlet, final boolean ack) {
        if (ack) {
            sendNow(cloudlet.getBroker(), CloudSimTag.CLOUDLET_SUBMIT_ACK, cloudlet);
        }
    }

    /**
     * Updates the processing of all Hosts, meaning
     * it makes the processing of VMs running inside such hosts to be updated.
     * Finally, the processing of Cloudlets, running inside such VMs, is updated too.
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
     * Updates the processing of each Host, that fires the update of VMs,
     * which in turn updates cloudlets running in this Datacenter.
     * After that, the method schedules the next processing update.
     * That is necessary because Hosts and VMs are simple objects, not
     * entities. So, they don't receive events, therefore, updating cloudlets inside
     * them must be called from the outside.
     *
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time);
     * or {@link Double#MAX_VALUE} if:
     *   (i) there is no next Cloudlet to execute, or
     *   (ii) it isn't time to update the cloudlets.
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
     * <p><b>This is an expensive operation for large-scale simulations.</b></p>
     */
    private void checkIfVmMigrationsAreNeeded() {
        if (!isTimeToSearchForSuitableHosts()) {
            return;
        }

        lastMigrationMap = vmAllocationPolicy.getOptimizedAllocationMap(getVmList());
        for (final var vmHostEntry : lastMigrationMap.entrySet()) {
            requestVmMigration(vmHostEntry.getKey(), vmHostEntry.getValue());
        }

        if(areThereUnderOrOverloadedHostsAndMigrationIsSupported()){
            lastUnderOrOverloadedDetection = clock();
        }
    }

    /**
     * @return true if it's time to check if suitable Hosts are available to migrate VMs
     * from under or overload Hosts; false otherwise
     */
    private boolean isTimeToSearchForSuitableHosts(){
        final double elapsedSecs = clock() - lastUnderOrOverloadedDetection;
        return isMigrationsEnabled() && elapsedSecs >= hostSearchRetryDelay;
    }

    private boolean areThereUnderOrOverloadedHostsAndMigrationIsSupported(){
        if(vmAllocationPolicy instanceof VmAllocationPolicyMigration migrationPolicy){
            return migrationPolicy.isUnderOrOverloaded();
        }

        return false;
    }

    @Override
    public void requestVmMigration(final Vm sourceVm) {
        final var targetHost = vmAllocationPolicy.findHostForVm(sourceVm).orElse(Host.NULL);
        if(Host.NULL.equals(targetHost)) {
            LOGGER.warn("{}: {}: No suitable host found for {} in {}", sourceVm.getSimulation().clockStr(), this, sourceVm, this);
            return;
        }

        requestVmMigration(sourceVm, targetHost);
    }

    @Override
    public void requestVmMigration(final Vm sourceVm, final Host targetHost) {
        final var sourceHost = sourceVm.getHost();
        double delay = timeToMigrateVm(sourceVm, targetHost);
        final boolean nonLiveMigration = delay < 0;
        final var migrationType = nonLiveMigration ? "Non-live Migration (from/to a public-cloud datacenter)" : "Live Migration (across private-cloud datacenters)";

        delay = Math.abs(delay);
        final String msg1 =
            Host.NULL.equals(sourceHost) ?
                "%s to %s".formatted(sourceVm, targetHost) :
                "%s from %s to %s".formatted(sourceVm, sourceHost, targetHost);

        final String currentTime = getSimulation().clockStr();
        final var fmt = "It's expected to finish in %.2f seconds, considering the %.0f%% of bandwidth allowed for migration and the VM %s.";
        final var vmResource = nonLiveMigration ? "disk size" : "allocated RAM";
        final String msg2 = fmt.formatted(delay, getBandwidthPercentForMigration()*100, vmResource);
        LOGGER.info("{}: {}: {} of {} is started. {}", currentTime, this, migrationType, msg1, msg2);

        if(targetHost.addMigratingInVm(sourceVm)) {
            sourceHost.addVmMigratingOut(sourceVm);
            send(this, delay, CloudSimTag.VM_MIGRATE, new TreeMap.SimpleEntry<>(sourceVm, targetHost));
            shutdownVmIfNonLiveMigration(sourceVm, targetHost, delay, nonLiveMigration);
        }
    }

    private void shutdownVmIfNonLiveMigration(final Vm sourceVm, final Host targetHost, final double delay, final boolean nonLiveMigration) {
        if(nonLiveMigration) {
            sourceVm.shutdown();
            final var cloudlets = sourceVm.getCloudletScheduler().getCloudletList().stream().toList();
            sourceVm.getCloudletScheduler().clear();
            cloudlets.stream().map(Cloudlet::reset).forEach(c -> c.setVm(sourceVm).setBroker(sourceVm.getBroker()));

            final var targerDc = targetHost.getDatacenter();
            // Request restarting executing the Cloudlets after the VM finishes non-live migration
            cloudlets.forEach(c -> targerDc.schedule(delay + getSimulation().getMinTimeBetweenEvents(), CloudSimTag.CLOUDLET_SUBMIT, c));
        }
    }

    /**
     * Computes the expected time to migrate a VM to a given Host.
     * It is computed as: VM RAM (MB)/Target Host Bandwidth (Mb/s).
     *
     * <p><b>WARNING: </b>If the VM is being migrated across {@link Distribution#PRIVATE} Datacenters,
     * returns a positive value indicating the time to migrate the VM.
     * If the VM is being migrated across datacenters with different {@link Distribution}s,
     * returns the time to migrate the VM as a negative value.
     * </p>
     *
     * @param vm the VM to migrate.
     * @param targetHost the Host where tto migrate the VM
     * @return the time (in seconds) that is expected to migrate the VM
     */
    private double timeToMigrateVm(final Vm vm, final Host targetHost) {
        final double ramUtilizationMB   = vm.getRam().getAllocatedResource();
        final double  vmMigrationBwMBps = getBandwidthForMigration(targetHost);
        final var sourceHost = vm.getHost();

        //From private to private-cloud DC, performs live migration (doesn't stop the VM and transfers RAM state)
        if (isLiveMigration(sourceHost, targetHost))
            return ramUtilizationMB / vmMigrationBwMBps;

        //Otherwise, non-live VM migration stops the VM and transfers its image
        return -vm.getStorage().getCapacity() / vmMigrationBwMBps;
    }

    private static boolean isLiveMigration(final Host sourceHost, final Host targetHost) {
        final Function<Host, Distribution> dist = host -> host.getDatacenter().getCharacteristics().getDistribution();
        return dist.apply(sourceHost) == PRIVATE && dist.apply(targetHost) == PRIVATE;
    }

    /**
     * {@return the bandwidth that will be reserved for VM migration (in MB/s)}
     * @param targetHost the target Host to migrate a VM
     */
    private double getBandwidthForMigration(final Host targetHost) {
        return bitsToBytes(targetHost.getBw().getCapacity() * getBandwidthPercentForMigration());
    }

    @Override
    public void shutdown() {
        super.shutdown();
        LOGGER.info("{}: {} is shutting down...", getSimulation().clockStr(), this);
    }

    @Override
    protected void startInternal() {
        LOGGER.info("{}: {} is starting...", getSimulation().clockStr(), this);
        hostList.stream()
                .filter(not(Host::isActive))
                .map(host -> (HostSimple)host)
                .forEach(host -> host.setActive(host.isActivateOnDatacenterStartup()));
        sendNow(getSimulation().getCis(), CloudSimTag.DC_REGISTRATION_REQUEST, this);
    }

    @Override
    public <T extends Host> List<T> getHostList() {
        return (List<T>)Collections.unmodifiableList(hostList);
    }

    @Override
    public Stream<? extends Host> getActiveHostStream() {
        return hostList.stream().filter(Host::isActive);
    }

    /**
     * Sets the policy to be used by the Datacenter to allocate {@link Vm}s into {@link Host}s.
     *
     * @param vmAllocationPolicy the new vm allocation policy
     * @return this Datacenter
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

    @Override
    public final void setDatacenterStorage(final DatacenterStorage datacenterStorage) {
        datacenterStorage.setDatacenter(this);
        this.datacenterStorage = datacenterStorage;
    }

    /**
     * {@return a <b>read-only</b> list containing all VMs from all Hosts of this Datacenter}
     * @param <T> the class of VMs inside the list
     */
    private <T extends Vm> List<T> getVmList() {
        return (List<T>) getHostList()
                    .stream()
                    .map(Host::getVmList)
                    .flatMap(List::stream)
                    .toList();
    }

    @Override
    public final Datacenter setSchedulingInterval(final double schedulingInterval) {
        this.schedulingInterval = Math.max(schedulingInterval, 0);
        return this;
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
        return "%cDatacenter %d".formatted(getCharacteristics().getDistribution().symbol(), getId());
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
                getSimulation().clockStr(), this, vmAllocationPolicy.getClass().getSimpleName());
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
    public final Datacenter setPowerModel(final PowerModelDatacenter powerModel) {
        requireNonNull(powerModel,
            "powerModel cannot be null. You could provide a " +
            PowerModelDatacenter.class.getSimpleName() + ".NULL instead");

        if(powerModel.getDatacenter() != null && powerModel.getDatacenter() != Datacenter.NULL && !this.equals(powerModel.getDatacenter())){
            throw new IllegalStateException("The given PowerModel is already assigned to another Datacenter. Each Datacenter must have its own PowerModel instance.");
        }

        this.powerModel = powerModel;
        return null;
    }

    @Override
    public Datacenter setHostSearchRetryDelay(final double delay) {
        if(delay == 0){
            throw new IllegalArgumentException("hostSearchRetryDelay cannot be 0. Set a positive value to define an actual delay or a negative value to indicate a new Host search must be tried as soon as possible.");
        }

        this.hostSearchRetryDelay = delay;
        return this;
    }

    @Override
    public Datacenter setCharacteristics(final @NonNull DatacenterCharacteristics c) {
        ((DatacenterCharacteristicsSimple)c).setDatacenter(this);
        this.characteristics = c;
        return this;
    }
}
