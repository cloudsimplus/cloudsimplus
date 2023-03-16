/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.hosts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.core.*;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudsimplus.power.PowerAware;
import org.cloudsimplus.power.models.PowerModelHost;
import org.cloudsimplus.provisioners.ResourceProvisioner;
import org.cloudsimplus.provisioners.ResourceProvisionerSimple;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.util.BytesConversion;
import org.cloudsimplus.util.MathUtil;
import org.cloudsimplus.util.TimeUtil;
import org.cloudsimplus.vms.*;

import java.util.*;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * A Host class that implements the most basic features of a Physical Machine
 * (PM) inside a {@link Datacenter}. It executes actions related to management
 * of virtual machines (e.g., creation and destruction). A host has a defined
 * policy for provisioning memory and bw, as well as an allocation policy for
 * PEs to {@link Vm Virtual Machines}. A host is associated to a Datacenter and
 * can host virtual machines.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
@Accessors @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HostSimple implements Host {
    /**
     * The Default RAM capacity (in MB) for creating Hosts.
     * This value is used when the RAM capacity is not given in a Host constructor.
     */
    @Getter
    private static long defaultRamCapacity = (long) BytesConversion.gigaToMega(10);

    /**
     * The Default Bandwidth capacity (in Mbps) for creating Hosts.
     * This value is used when the BW capacity is not given in a Host constructor.
     */
    @Getter
    private static long defaultBwCapacity = 1000;

    /**
     * The Default Storage capacity (in MB) for creating Hosts.
     * This value is used when the Storage capacity is not given in a Host constructor.
     */
    @Getter
    private static long defaultStorageCapacity = (long) BytesConversion.gigaToMega(500);

    @Getter @Setter @EqualsAndHashCode.Include
    private long id;

    @Getter @Setter @NonNull @EqualsAndHashCode.Include
    private Simulation simulation;

    @Getter
    private Datacenter datacenter;

    /**
     * {@return true or false} to indicate the Host must be automatically started up when the assigned Datacenter is initialized or not.
     */
    @Getter
    private boolean activateOnDatacenterStartup;

    @Getter
    private PowerModelHost powerModel;

    @Getter
    private boolean failed;

    @Getter
    private boolean active;

    /**
     * Indicates if a power on/off operation is in progress.
     */
    private boolean activationChangeInProgress;

    @Getter
    private double startTime = -1;

    @Getter
    private double firstStartTime = -1;

    @Getter
    private double shutdownTime;

    /** @see #getTotalUpTime() */
    private double totalUpTime;

    @Getter
    private double lastBusyTime;

    @Getter @Setter
    private double idleShutdownDeadline;

    private final Ram ram;
    private final Bandwidth bw;

    /** @see #getStorage() */
    private final HarddriveStorage disk;

    @Getter @NonNull
    private ResourceProvisioner ramProvisioner;

    @Getter @NonNull
    private ResourceProvisioner bwProvisioner;

    @Getter @NonNull
    private VmScheduler vmScheduler;

    @Getter @NonNull
    private List<Pe> peList;

    /** @see #getVmList() */
    private final List<Vm> vmList = new ArrayList<>();

    @Getter
    private HostResourceStats cpuUtilizationStats;

    @Getter @Setter
    private boolean stateHistoryEnabled;

    /** @see #getStateHistory() */
    private final List<HostStateHistoryEntry> stateHistory;

    /** @see #getVmsMigratingIn() */
    private final Set<Vm> vmsMigratingIn;

    /** @see #getVmsMigratingOut() */
    private final Set<Vm> vmsMigratingOut;

    /** @see #addOnUpdateProcessingListener(EventListener) */
    private final Set<EventListener<HostUpdatesVmsProcessingEventInfo>> onUpdateProcessingListeners;

    /** @see #addOnStartupListener(EventListener) (EventListener) */
    private final List<EventListener<HostEventInfo>> onStartupListeners;

    /** @see #addOnShutdownListener(EventListener) (EventListener) */
    private final List<EventListener<HostEventInfo>> onShutdownListeners;

    /** @see #getResources() */
    private List<ResourceManageable> resources;

    private List<ResourceProvisioner> provisioners;
    private final List<Vm> vmCreatedList;

    @Getter
    private int freePesNumber;

    @Getter
    private int busyPesNumber;

    @Getter
    private int workingPesNumber;

    @Getter
    private int failedPesNumber;

    /**
     * {@inheritDoc}
     * <p><b>It's enabled by default.</b></p>
     */
    @Getter @Setter
    private boolean lazySuitabilityEvaluation;

    /**
     * Creates and powers on a Host without a pre-defined ID,
     * 10GB of RAM, 1000Mbps of Bandwidth and 500GB of Storage.
     * It creates a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth. Finally, it sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param peList the host's {@link Pe} list
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public HostSimple(final List<Pe> peList) {
        this(peList, true);
    }

    /**
     * Creates a Host without a pre-defined ID,
     * 10GB of RAM, 1000Mbps of Bandwidth and 500GB of Storage
     * and enabling the host to be powered on or not.
     *
     * <p>It creates a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth. Finally, it sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.</p>
     *
     * @param peList the host's {@link Pe} list
     * @param activate define the Host activation status: true to power on, false to power off
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public HostSimple(final List<Pe> peList, final boolean activate) {
        this(defaultRamCapacity, defaultBwCapacity, defaultStorageCapacity, peList, activate);
    }

    /**
     * Creates and powers on a Host with the given parameters and a
     * {@link VmSchedulerSpaceShared} as default.
     *
     * @param ramProvisioner the ram provisioner with capacity in Megabytes
     * @param bwProvisioner the bw provisioner with capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's PEs list
     *
     * @see #setVmScheduler(VmScheduler)
     */
    public HostSimple(
        final ResourceProvisioner ramProvisioner,
        final ResourceProvisioner bwProvisioner,
        final long storage,
        final List<Pe> peList)
    {
        this(ramProvisioner.getCapacity(), bwProvisioner.getCapacity(), storage, peList);
        setRamProvisioner(ramProvisioner);
        setBwProvisioner(bwProvisioner);
        setPeList(peList);
    }

    /**
     * Creates and powers on a Host without a pre-defined ID.
     * It uses a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth and also sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     */
    public HostSimple(final long ram, final long bw, final long storage, final List<Pe> peList) {
        this(ram, bw, new HarddriveStorage(storage), peList);
    }

    public HostSimple(
        final long ram, final long bw,
        final HarddriveStorage storage, final List<Pe> peList)
    {
        this(ram, bw, storage, peList, true);
    }

    /**
     * Creates a Host without a pre-defined ID. It uses a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth and also sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     * @param activate define the Host activation status: true to power on, false to power off
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     */
    public HostSimple(
        final long ram, final long bw, final long storage,
        final List<Pe> peList, final boolean activate)
    {
        this(ram, bw, new HarddriveStorage(storage), peList, activate);
    }

    private HostSimple(
        final long ram, final long bw, @NonNull final HarddriveStorage storage,
        final List<Pe> peList, final boolean activate)
    {
        this.setId(-1);
        this.setSimulation(Simulation.NULL);
        this.idleShutdownDeadline = DEF_IDLE_SHUTDOWN_DEADLINE;
        this.lazySuitabilityEvaluation = true;

        this.ram = new Ram(ram);
        this.bw = new Bandwidth(bw);
        this.disk = storage;
        this.setRamProvisioner(new ResourceProvisionerSimple());
        this.setBwProvisioner(new ResourceProvisionerSimple());

        this.setVmScheduler(new VmSchedulerSpaceShared());
        this.setPeList(peList);
        this.setFailed(false);
        this.shutdownTime = -1;
        this.setDatacenter(Datacenter.NULL);

        this.onUpdateProcessingListeners = new HashSet<>();
        this.onStartupListeners = new ArrayList<>();
        this.onShutdownListeners = new ArrayList<>();
        this.cpuUtilizationStats = HostResourceStats.NULL;

        this.resources = new ArrayList<>();
        this.vmCreatedList = new ArrayList<>();
        this.provisioners = new ArrayList<>();
        this.vmsMigratingIn = new HashSet<>();
        this.vmsMigratingOut = new HashSet<>();
        this.powerModel = PowerModelHost.NULL;
        this.stateHistory = new LinkedList<>();
        this.activateOnDatacenterStartup = activate;
    }

    /**
     * Sets the Default RAM capacity (in MB) for creating Hosts.
     * This value is used when the RAM capacity is not given in a Host constructor.
     */
    public static void setDefaultRamCapacity(final long defaultCapacity) {
        AbstractMachine.validateCapacity(defaultCapacity);
        defaultRamCapacity = defaultCapacity;
    }

    /**
     * Sets the Default Bandwidth capacity (in Mbps) for creating Hosts.
     * This value is used when the BW capacity is not given in a Host constructor.
     */
    public static void setDefaultBwCapacity(final long defaultCapacity) {
        AbstractMachine.validateCapacity(defaultCapacity);
        defaultBwCapacity = defaultCapacity;
    }

    /**
     * Sets the Default Storage capacity (in MB) for creating Hosts.
     * This value is used when the Storage capacity is not given in a Host constructor.
     */
    public static void setDefaultStorageCapacity(final long defaultCapacity) {
        AbstractMachine.validateCapacity(defaultCapacity);
        defaultStorageCapacity = defaultCapacity;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public double updateProcessing(final double currentTime) {
        if(vmList.isEmpty() && isIdleEnough(idleShutdownDeadline)){
            setActive(false);
        }

        double nextSimulationDelay = Double.MAX_VALUE;

        /* Uses an indexed for to avoid ConcurrentModificationException,
         * e.g., in cases when Vm is destroyed during simulation execution.*/
        for (int i = 0; i < vmList.size(); i++) {
            nextSimulationDelay = updateVmProcessing(vmList.get(i), currentTime, nextSimulationDelay);
        }

        notifyOnUpdateProcessingListeners(currentTime);
        cpuUtilizationStats.add(currentTime);
        addStateHistory(currentTime);
        if (!vmList.isEmpty()) {
            lastBusyTime = currentTime;
        }

        return nextSimulationDelay;
    }

    protected double updateVmProcessing(final Vm vm, final double currentTime, final double nextSimulationDelay) {
        final double delay = vm.updateProcessing(currentTime, vmScheduler.getAllocatedMips(vm));
        return delay > 0 ? Math.min(delay, nextSimulationDelay) : nextSimulationDelay;
    }

    private void notifyOnUpdateProcessingListeners(final double nextSimulationTime) {
        onUpdateProcessingListeners.forEach(l -> l.update(HostUpdatesVmsProcessingEventInfo.of(l,this, nextSimulationTime)));
    }

    @Override
    public HostSuitability createVm(final Vm vm) {
        final HostSuitability suitability = createVmInternal(vm);
        if(suitability.fully()) {
            addVmToCreatedList(vm);
            vm.setHost(this);
            vm.setCreated(true);
            vm.setStartTime(getSimulation().clock());
        }

        return suitability;
    }

    @Override
    public HostSuitability createTemporaryVm(final Vm vm) {
        return createVmInternal(vm);
    }

    private HostSuitability createVmInternal(final Vm vm) {
        if(vm instanceof VmGroup){
            return new HostSuitability("Just internal VMs inside a VmGroup can be created, not the VmGroup itself.");
        }

        final HostSuitability suitability = allocateResourcesForVm(vm, false);
        if(suitability.fully()){
            vmList.add(vm);
        }

        return suitability;
    }

    /**
     * Try to allocate all resources that a VM requires (Storage, RAM, BW and MIPS)
     * to be placed at this Host.
     *
     * @param vm the VM to try allocating resources to
     * @param inMigration indicates whether the VM is migrating into the Host
     *                    or it is being just created for the first time.
     * @return a {@link HostSuitability} to indicate if the Vm was placed into the host or not
     * (if the Host doesn't have enough resources to allocate the Vm)
     */
    private HostSuitability allocateResourcesForVm(final Vm vm, final boolean inMigration){
        final HostSuitability suitability = isSuitableForVm(vm, inMigration, true);
        if(!suitability.fully()) {
            return suitability;
        }

        if(inMigration) {
            vmsMigratingIn.add(vm);
        }
        vm.setInMigration(inMigration);
        allocateResourcesForVm(vm);

        return suitability;
    }

    private void allocateResourcesForVm(final Vm vm) {
        ramProvisioner.allocateResourceForVm(vm, vm.getCurrentRequestedRam());
        bwProvisioner.allocateResourceForVm(vm, vm.getCurrentRequestedBw());
        disk.getStorage().allocateResource(vm.getStorage());
        vmScheduler.allocatePesForVm(vm, vm.getCurrentRequestedMips());
    }

    private void logAllocationError(
        final boolean showFailureLog, final Vm vm,
        final boolean inMigration, final String resourceUnit,
        final Resource pmResource, final Resource vmRequestedResource)
    {
        if(!showFailureLog){
            return;
        }

        final var migration = inMigration ? "VM Migration" : "VM Creation";
        final var msg = pmResource.getAvailableResource() > 0 ?
                            "just "+pmResource.getAvailableResource()+" " + resourceUnit :
                            "no amount";
        LOGGER.error(
            "{}: {}: [{}] Allocation of {} to {} failed due to lack of {}. Required {} but there is {} available.",
            simulation.clockStr(), getClass().getSimpleName(), migration, vm, this,
            pmResource.getClass().getSimpleName(), vmRequestedResource.getCapacity(), msg);
    }

    @Override
    public void reallocateMigratingInVms() {
        for (final Vm vm : getVmsMigratingIn()) {
            if (!vmList.contains(vm)) {
                vmList.add(vm);
            }

            allocateResourcesForVm(vm);
        }
    }

    @Override
    public boolean isSuitableForVm(final Vm vm) {
        return getSuitabilityFor(vm).fully();
    }

    /**
     * Checks if the host is suitable for vm
     * (if it has enough resources to attend the VM)
     * and the Host is not failed.
     *
     * @param vm the VM to check
     * @param inMigration indicates whether the VM is migrating into the Host
     *                    or it is being just created for the first time
     *                    (in the last case, just for logging purposes).
     * @param showFailureLog indicates if a error log must be shown when the Host is not suitable
     * @return a {@link HostSuitability} object that indicate for which resources the Host
     *         is suitable or not for the given VM
     * @see #isLazySuitabilityEvaluation()
     */
    private HostSuitability isSuitableForVm(final Vm vm, final boolean inMigration, final boolean showFailureLog) {
        final var suitability = new HostSuitability();

        suitability.setForStorage(disk.isAmountAvailable(vm.getStorage()));
        if (!suitability.forStorage()) {
            logAllocationError(showFailureLog, vm, inMigration, "MB", this.getStorage(), vm.getStorage());
            if(lazySuitabilityEvaluation)
                return suitability;
        }

        suitability.setForRam(ramProvisioner.isSuitableForVm(vm, vm.getRam()));
        if (!suitability.forRam()) {
            logAllocationError(showFailureLog, vm, inMigration, "MB", this.getRam(), vm.getRam());
            if(lazySuitabilityEvaluation)
                return suitability;
        }

        suitability.setForBw(bwProvisioner.isSuitableForVm(vm, vm.getBw()));
        if (!suitability.forBw()) {
            logAllocationError(showFailureLog, vm, inMigration, "Mbps", this.getBw(), vm.getBw());
            if(lazySuitabilityEvaluation)
                return suitability;
        }

        suitability.setForPes(vmScheduler.isSuitableForVm(vm));
        return suitability;
    }

    @Override
    public HostSuitability getSuitabilityFor(final Vm vm) {
        return isSuitableForVm(vm, false, false);
    }

    @Override
    public boolean hasEverStarted() {
        return this.firstStartTime > -1;
    }

    @Override
    public final Host setActive(final boolean activate) {
        if(!activate) {
            activateOnDatacenterStartup = false;
        }

        final double delay = activate ? powerModel.getStartupDelay() : powerModel.getShutDownDelay();
        if(this.active == activate || delay > 0 && activationChangeInProgress){
            return this;
        }

        if(isFailed() && activate){
            throw new IllegalStateException("The Host is failed and cannot be activated.");
        }

        if (delay == 0) {
           //If there is no delay, start up or shutdown the Host right away.
           processActivation(activate);
           return this;
        }

        /*If the simulation is not running and there is a startup delay,
        * when the datacenter is started up, it will request such a Host activation. */
        if(!simulation.isRunning()){
            return this;
        }

        final int tag = activate ? CloudSimTag.HOST_POWER_ON : CloudSimTag.HOST_POWER_OFF;
        final String msg = (activate ? "on" : "off") + " (expected time: {} seconds).";
        LOGGER.info("{}: {} is being powered " + msg, getSimulation().clockStr(), this, delay);
        datacenter.schedule(delay, tag, this);
        activationChangeInProgress = true;

        return this;
    }

    /**
     * Process an event for actually powering the {@link Host} <b>on</b> or <b>off</b>
     * after any defined start up/shutdown delay (if some delay is set).
     * @param activate true to start the Host up, false to shut it down
     * @see #setActive(boolean)
     */
    public void processActivation(final boolean activate) {
        final boolean wasActive = this.active;
        if(activate) {
            setStartTime(getSimulation().clock());
            powerModel.addStartupTotals();
        } else {
            setShutdownTime(getSimulation().clock());
            powerModel.addShutDownTotals();
        }

        this.active = activate;
        ((DatacenterSimple) datacenter).updateActiveHostsNumber(this);
        activationChangeInProgress = false;
        notifyStartupOrShutdown(activate, wasActive);
    }

    /**
     * Notifies registered listeners about host start up or shutdown,
     * then prints information when the Host starts up or shuts down.
     * @param activate the activation value that is being requested to set
     * @param wasActive the previous value of the {@link #active} attribute
     *                  (before being updated)
     * @see #setActive(boolean)
     */
    private void notifyStartupOrShutdown(final boolean activate, final boolean wasActive) {
        if(Simulation.NULL.equals(simulation) || !simulation.isRunning() ) {
            return;
        }

        if(activate && !wasActive){
            LOGGER.info("{}: {} is powered on.", getSimulation().clockStr(), this);
            updateOnStartupListeners();
        }
        else if(!activate && wasActive){
            final String reason = isIdleEnough(idleShutdownDeadline) ? " after becoming idle" : "";
            LOGGER.info("{}: {} is powered off{}.", getSimulation().clockStr(), this, reason);
            updateOnShutdownListeners();
        }
    }

    private void updateOnShutdownListeners() {
        for (int i = 0; i < onShutdownListeners.size(); i++) {
            final var listener = onShutdownListeners.get(i);
            listener.update(HostEventInfo.of(listener, this, simulation.clock()));
        }
    }

    private void updateOnStartupListeners() {
        for (int i = 0; i < onStartupListeners.size(); i++) {
            final var listener = onStartupListeners.get(i);
            listener.update(HostEventInfo.of(listener, this, simulation.clock()));
        }
    }

    @Override
    public void destroyVm(final Vm vm) {
        if(!vm.isCreated()){
            return;
        }

        destroyVmInternal(vm);
        vm.setStopTime(getSimulation().clock());
        vm.notifyOnHostDeallocationListeners(this);
    }

    @Override
    public void destroyTemporaryVm(final Vm vm) {
        destroyVmInternal(vm);
    }

    private void destroyVmInternal(final Vm vm) {
        deallocateResourcesOfVm(requireNonNull(vm));
        vmList.remove(vm);
        vm.getBroker().getVmExecList().remove(vm);
    }

    /**
     * Deallocate all resources that a VM was using.
     *
     * @param vm the VM to deallocate resources from
     */
    protected void deallocateResourcesOfVm(final Vm vm) {
        vm.setCreated(false);
        ramProvisioner.deallocateResourceForVm(vm);
        bwProvisioner.deallocateResourceForVm(vm);
        vmScheduler.deallocatePesFromVm(vm);
        disk.getStorage().deallocateResource(vm.getStorage());
    }

    @Override
    public void destroyAllVms() {
        final var peProvisioner = getPeList().get(0).getPeProvisioner();
        for (final Vm vm : vmList) {
            ramProvisioner.deallocateResourceForVm(vm);
            bwProvisioner.deallocateResourceForVm(vm);
            peProvisioner.deallocateResourceForVm(vm);
            vm.setCreated(false);
            disk.getStorage().deallocateResource(vm.getStorage());
        }

        vmList.clear();
    }

    @Override
    public Host addOnStartupListener(@NonNull final EventListener<HostEventInfo> listener) {
        if(EventListener.NULL.equals(listener)){
            return this;
        }

        onStartupListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnStartupListener(@NonNull final EventListener<HostEventInfo> listener) {
        return onStartupListeners.remove(listener);
    }

    @Override
    public Host addOnShutdownListener(@NonNull final EventListener<HostEventInfo> listener) {
        if(EventListener.NULL.equals(listener)){
            return this;
        }

        onShutdownListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnShutdownListener(@NonNull final EventListener<HostEventInfo> listener) {
        return onShutdownListeners.remove(listener);
    }

    @Override
    public boolean removeOnUpdateProcessingListener(@NonNull final EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        return onUpdateProcessingListeners.remove(listener);
    }

    @Override
    public Host addOnUpdateProcessingListener(@NonNull final EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        if(EventListener.NULL.equals(listener)){
            return this;
        }

        this.onUpdateProcessingListeners.add(listener);
        return this;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @see #getWorkingPesNumber()
     * @see #getFreePesNumber()
     * @see #getFailedPesNumber()
     */
    @Override
    public long getPesNumber() {
        return peList.size();
    }

    /**
     * Gets the MIPS share of each Pe that is allocated to a given VM.
     *
     * @param vm the vm
     * @return an array containing the amount of MIPS of each pe that is available to the VM
     */
    protected MipsShare getAllocatedMipsForVm(final Vm vm) {
        return vmScheduler.getAllocatedMips(vm);
    }

    @Override
    public double getMips() {
        return peList.stream().mapToDouble(Pe::getCapacity).findFirst().orElse(0);
    }

    @Override
    public double getTotalMipsCapacity() {
        return peList.stream()
                     .filter(Pe::isWorking)
                     .mapToDouble(Pe::getCapacity)
                     .sum();
    }

    @Override
    public double getTotalAvailableMips() {
        return vmScheduler.getTotalAvailableMips();
    }

    @Override
    public double getTotalAllocatedMips() {
        return getTotalMipsCapacity() - getTotalAvailableMips();
    }

    @Override
    public double getTotalAllocatedMipsForVm(final Vm vm) {
        return vmScheduler.getTotalAllocatedMipsForVm(vm);
    }

    @Override
    public Resource getBw() {
        return bwProvisioner.getPmResource();
    }

    @Override
    public Resource getRam() {
        return ramProvisioner.getPmResource();
    }

    @Override
    public FileStorage getStorage() {
        return disk;
    }

    @Override
    public final Host setRamProvisioner(final ResourceProvisioner ramProvisioner) {
        checkSimulationIsRunningAndAttemptedToChangeHost("RAM");
        this.ramProvisioner = ramProvisioner;
        this.ramProvisioner.setResources(ram, vm -> ((VmSimple)vm).getRam());
        return this;
    }

    @Override
    public final Host setBwProvisioner(final ResourceProvisioner bwProvisioner) {
        checkSimulationIsRunningAndAttemptedToChangeHost("BW");
        this.bwProvisioner = bwProvisioner;
        this.bwProvisioner.setResources(bw, vm -> ((VmSimple)vm).getBw());
        return this;
    }

    private void checkSimulationIsRunningAndAttemptedToChangeHost(final String resourceName) {
        if(simulation.isRunning()){
            final var msg = "It is not allowed to change a Host's %s after the simulation started.";
            throw new IllegalStateException(msg.formatted(resourceName));
        }
    }

    @Override
    public final Host setVmScheduler(final VmScheduler vmScheduler) {
        this.vmScheduler = vmScheduler;
        vmScheduler.setHost(this);
        return this;
    }

    @Override
    public Host setStartTime(final double startTime) {
        this.startTime = MathUtil.nonNegative(Math.floor(startTime), "startTime");
        if(firstStartTime == -1){
            firstStartTime = this.startTime;
        }

        this.lastBusyTime = startTime;

        //If the Host is being activated or re-activated, the shutdown time is reset
        this.shutdownTime = -1;
        return this;
    }

    @Override
    public void setShutdownTime(final double shutdownTime) {
        this.shutdownTime = MathUtil.nonNegative(Math.floor(shutdownTime), "shutdownTime");
        this.totalUpTime += getUpTime();
    }

    @Override
    public double getUpTime() {
        return active ? simulation.clock() - startTime : shutdownTime - startTime;
    }

    @Override
    public double getTotalUpTime() {
        return totalUpTime + (active ? getUpTime() : 0);
    }

    @Override
    public double getUpTimeHours() {
        return TimeUtil.secondsToHours(getUpTime());
    }

    @Override
    public double getTotalUpTimeHours() {
        return TimeUtil.secondsToHours(getTotalUpTime());
    }

    /**
     * Sets the PE list.
     *
     * @param peList the new pe list
     */
    private void setPeList(final List<Pe> peList) {
        if(peList.isEmpty()){
            throw new IllegalArgumentException("The PE list for a Host cannot be empty");
        }

        checkSimulationIsRunningAndAttemptedToChangeHost("List of PE");
        this.peList = peList;

        long peId = Math.max(peList.get(peList.size()-1).getId(), -1);
        for(final Pe pe: peList){
            if(pe.getId() < 0) {
                pe.setId(++peId);
            }
            pe.setStatus(Pe.Status.FREE);
        }

        failedPesNumber = 0;
        busyPesNumber = 0;
        freePesNumber = peList.size();
        workingPesNumber = freePesNumber;
    }

    @Override
    public <T extends Vm> List<T> getVmList() {
        return (List<T>) vmList;
    }

    @Override
    public <T extends Vm> List<T> getVmCreatedList() {
        return (List<T>) Collections.unmodifiableList(vmCreatedList);
    }

    protected void addVmToList(@NonNull final Vm vm){
        vmList.add(vm);
    }

    protected void addVmToCreatedList(@NonNull final Vm vm){
        vmCreatedList.add(vm);
    }

    @Override
    public final boolean setFailed(final boolean failed) {
        this.failed = failed;
        final var newStatus = failed ? Pe.Status.FAILED : Pe.Status.FREE;
        setPeStatus(peList, newStatus);

        /*Just changes the active state when the Host is set to active.
        * In other situations, the active status must remain as it was.
        * For example, if the host was inactive and now it's set to failed,
        * it must remain inactive.*/
        if(failed && this.active){
            this.active = false;
        }

        return true;
    }

    /**
     * Sets the status of a given (sub)list of {@link Pe} to a new status.
     * @param peList the (sub)list of {@link Pe} to change the status
     * @param newStatus the new status
     */
    public final void setPeStatus(final List<Pe> peList, final Pe.Status newStatus){
        /*For performance reasons, stores the number of free and failed PEs
        instead of iterating over the PE list every time to find out.*/
        for (final Pe pe : peList) {
            updatePeStatus(pe, newStatus);
        }
    }

    private void updatePeStatus(final Pe pe, final Pe.Status newStatus) {
        if(pe.getStatus() != newStatus) {
            updatePeStatusCount(pe.getStatus(), false);
            updatePeStatusCount(newStatus, true);
            pe.setStatus(newStatus);
        }
    }

    /**
     * Update the number of PEs for a given status.
     * You must call the method before the Pe status change and after it
     * so that the numbers for the previous and new PE status are updated.
     * @param status the status of the PE to process (either a previous or new status)
     * @param isIncrement true to increment the number of PEs in the given status to 1, false to decrement
     */
    private void updatePeStatusCount(final Pe.Status status, final boolean isIncrement) {
        final int inc = isIncrement ? 1 : -1;
        switch (status) {
            case FAILED -> incFailedPesNumber(inc);
            case FREE   -> incFreePesNumber(inc);
            case BUSY   -> incBusyPesNumber(inc);
        }
    }

    /**
     * Updates the number of failed (and working) PEs,
     * decreasing it if a negative number of given, or increasing otherwise.
     * @param inc the value to sum (positive or negative) to the number of busy PEs
     */
    protected void incFailedPesNumber(final int inc) {
        this.failedPesNumber += inc;
        workingPesNumber -= inc;
    }

    /**
     * Updates the number of free PEs, decreasing it if a negative number of given,
     * or increasing otherwise.
     * @param inc the value to sum (positive or negative) to the number of busy PEs
     */
    protected void incFreePesNumber(final int inc) {
        this.freePesNumber += inc;
    }

    /**
     * Updates the number of busy PEs, decreasing it if a negative number of given,
     * or increasing otherwise.
     * @param inc the value to sum (positive or negative) to the number of busy PEs
     */
    protected void incBusyPesNumber(final int inc) {
        this.busyPesNumber += inc;
    }

    @Override
    public <T extends Vm> Set<T> getVmsMigratingIn() {
        return (Set<T>)vmsMigratingIn;
    }

    @Override
    public boolean hasMigratingVms(){
        return !(vmsMigratingIn.isEmpty() && vmsMigratingOut.isEmpty());
    }

    @Override
    public boolean addMigratingInVm(final Vm vm) {
        if (vmsMigratingIn.contains(vm)) {
            return false;
        }

        if (allocateResourcesForVm(vm, true).fully()) {
            ((VmSimple) vm).updateMigrationStartListeners(this);

            updateProcessing(simulation.clock());
            vm.getHost().updateProcessing(simulation.clock());

            return true;
        }

        return false;
    }

    @Override
    public void removeMigratingInVm(@NonNull final Vm vm) {
        vmsMigratingIn.remove(vm);
        vmList.remove(vm);
        vm.setInMigration(false);
    }

    @Override
    public Set<Vm> getVmsMigratingOut() {
        return Collections.unmodifiableSet(vmsMigratingOut);
    }

    @Override
    public boolean addVmMigratingOut(@NonNull final Vm vm) {
        return this.vmsMigratingOut.add(vm);
    }

    @Override
    public boolean removeVmMigratingOut(@NonNull final Vm vm) {
        return this.vmsMigratingOut.remove(vm);
    }

    @Override
    public final void setDatacenter(@NonNull final Datacenter datacenter) {
        if(!Datacenter.NULL.equals(this.datacenter)) {
            checkSimulationIsRunningAndAttemptedToChangeHost("Datacenter");
        }

        this.datacenter = datacenter;
    }

    @Override
    public String toString() {
        final String dc =
                datacenter == null || Datacenter.NULL.equals(datacenter) ? "" :
                "/DC %d".formatted(datacenter.getId());
        return "Host %d%s".formatted(getId(), dc);
    }

    @Override
    public long getAvailableStorage() {
        return disk.getAvailableResource();
    }

    @Override
    public double getBusyPesPercent() {
        return getBusyPesNumber() / (double) getPesNumber();
    }

    @Override
    public double getBusyPesPercent(final boolean hundredScale) {
        final double scale = hundredScale ? 100 : 1;
        return getBusyPesPercent() * scale;
    }

    @Override
    public int compareTo(final Host other) {
        if(this.equals(requireNonNull(other))) {
            return 0;
        }

        return Long.compare(this.id, other.getId());
    }

    @Override
    public List<ResourceManageable> getResources() {
        if(simulation.isRunning() && resources.isEmpty()){
            resources = Arrays.asList(ram, bw);
        }

        return Collections.unmodifiableList(resources);
    }

    @Override
    public ResourceProvisioner getProvisioner(final Class<? extends ResourceManageable> resourceClass) {
        if(simulation.isRunning() && provisioners.isEmpty()){
            provisioners = Arrays.asList(ramProvisioner, bwProvisioner);
        }

        return provisioners
            .stream()
            .filter(provisioner -> provisioner.getPmResource().isSubClassOf(resourceClass))
            .findFirst()
            .orElse(ResourceProvisioner.NULL);
    }

    @Override
    public List<Pe> getWorkingPeList() {
        return getFilteredPeList(Pe::isWorking);
    }

    @Override
    public List<Pe> getBusyPeList() {
        return getFilteredPeList(Pe::isBusy);
    }

    @Override
    public List<Pe> getFreePeList() {
        return getFilteredPeList(Pe::isFree);
    }

    private List<Pe> getFilteredPeList(final Predicate<Pe> status) {
        return peList.stream().filter(status).collect(toList());
    }

    @Override
    public double getCpuPercentUtilization() {
        return computeCpuUtilizationPercent(getCpuMipsUtilization());
    }

    @Override
    public double getCpuPercentRequested() {
        return computeCpuUtilizationPercent(getCpuMipsRequested());
    }

    private double computeCpuUtilizationPercent(final double mipsUsage){
        final double totalMips = getTotalMipsCapacity();
        if(totalMips == 0){
            return 0;
        }

        final double utilization = mipsUsage / totalMips;
        return utilization > 1 && utilization < 1.01 ? 1 : utilization;
    }

    @Override
    public double getCpuMipsUtilization() {
        return vmList.stream().mapToDouble(Vm::getTotalCpuMipsUtilization).sum();
    }

    private double getCpuMipsRequested() {
        return vmList.stream().mapToDouble(Vm::getTotalCpuMipsRequested).sum();
    }

    @Override
    public long getRamUtilization() {
        return ramProvisioner.getTotalAllocatedResource();
    }

    @Override
    public long getBwUtilization() {
        return bwProvisioner.getTotalAllocatedResource();
    }

    @Override
    public void enableUtilizationStats() {
        if (cpuUtilizationStats != null && cpuUtilizationStats != HostResourceStats.NULL) {
            return;
        }

        this.cpuUtilizationStats = new HostResourceStats(this, Host::getCpuPercentUtilization);
        if(vmList.isEmpty()){
            final String host = this.getId() > -1 ? this.toString() : "Host";
            LOGGER.info("Automatically enabling computation of utilization statistics for VMs on {} could not be performed because it doesn't have VMs yet. You need to enable it for each VM created.", host);
        }
        else vmList.forEach(ResourceStatsComputer::enableUtilizationStats);
    }

    @Override
    public final PowerAware<PowerModelHost> setPowerModel(final PowerModelHost powerModel) {
        requireNonNull(powerModel,
            "powerModel cannot be null. You could provide a " +
            PowerModelHost.class.getSimpleName() + ".NULL instead.");

        if(powerModel.getHost() != null && powerModel.getHost() != NULL && !this.equals(powerModel.getHost())){
            throw new IllegalStateException("The given PowerModel is already assigned to another Host. Each Host must have its own PowerModel instance.");
        }

        this.powerModel = powerModel;
        powerModel.setHost(this);
        return this;
    }

    @Override
    public List<Vm> getFinishedVms() {
        return getVmList().stream()
            .filter(vm -> !vm.isInMigration())
            .filter(vm -> vm.getTotalCpuMipsRequested() == 0)
            .collect(toList());
    }

    /**
     * Adds the VM resource usage to the History if the VM is not migrating into the Host.
     * @param vm the VM to add its usage to the history
     * @param currentTime the current simulation time
     * @return the total allocated MIPS for the given VM
     */
    private double addVmResourceUseToHistoryIfNotMigratingIn(final Vm vm, final double currentTime) {
        double totalAllocatedMips = getVmScheduler().getTotalAllocatedMipsForVm(vm);
        if (getVmsMigratingIn().contains(vm)) {
            LOGGER.info("{}: {}: {} is migrating in", getSimulation().clockStr(), this, vm);
            return totalAllocatedMips;
        }

        final double totalRequestedMips = vm.getTotalCpuMipsRequested();
        if (totalAllocatedMips + 0.1 < totalRequestedMips) {
            final String reason = getVmsMigratingOut().contains(vm) ? "migration overhead" : "capacity unavailability";
            final long notAllocatedMipsByPe = (long)((totalRequestedMips - totalAllocatedMips)/vm.getPesNumber());
            LOGGER.warn(
                "{}: {}: {} MIPS not allocated for each one of the {} PEs from {} due to {}.",
                getSimulation().clockStr(), this, notAllocatedMipsByPe, vm.getPesNumber(), vm, reason);
        }

        final var entry = new VmStateHistoryEntry(
                           currentTime, totalAllocatedMips, totalRequestedMips,
                           vm.isInMigration() && !getVmsMigratingIn().contains(vm));
        vm.addStateHistoryEntry(entry);

        if (vm.isInMigration()) {
            LOGGER.info("{}: {}: {} is migrating out ", getSimulation().clockStr(), this, vm);
            totalAllocatedMips /= getVmScheduler().getMaxCpuUsagePercentDuringOutMigration();
        }

        return totalAllocatedMips;
    }

    private void addStateHistory(final double currentTime) {
        if(!stateHistoryEnabled){
            return;
        }

        double hostTotalRequestedMips = 0;

        for (final Vm vm : getVmList()) {
            final double totalRequestedMips = vm.getTotalCpuMipsRequested();
            addVmResourceUseToHistoryIfNotMigratingIn(vm, currentTime);
            hostTotalRequestedMips += totalRequestedMips;
        }

        addStateHistoryEntry(currentTime, getCpuMipsUtilization(), hostTotalRequestedMips, active);
    }

    /**
     * Adds a host state history entry.
     *
     * @param time the time
     * @param allocatedMips the allocated mips
     * @param requestedMips the requested mips
     * @param isActive the is active
     */
    private void addStateHistoryEntry(
        final double time,
        final double allocatedMips,
        final double requestedMips,
        final boolean isActive)
    {
        final var newState = new HostStateHistoryEntry(time, allocatedMips, requestedMips, isActive);
        if (!stateHistory.isEmpty()) {
            final var previousState = stateHistory.get(stateHistory.size() - 1);
            if (previousState.time() == time) {
                stateHistory.set(stateHistory.size() - 1, newState);
                return;
            }
        }

        stateHistory.add(newState);
    }

    @Override
    public List<HostStateHistoryEntry> getStateHistory() {
        return Collections.unmodifiableList(stateHistory);
    }

    @Override
    public List<Vm> getMigratableVms() {
        return vmList.stream().filter(vm -> !vm.isInMigration()).collect(toList());
    }
}
