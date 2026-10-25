/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
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
import org.cloudsimplus.listeners.Listeners;
import org.cloudsimplus.power.PowerAware;
import org.cloudsimplus.power.models.PowerModelHost;
import org.cloudsimplus.allocators.ResourceAllocator;
import org.cloudsimplus.allocators.ResourceAllocatorAbstract;
import org.cloudsimplus.allocators.ResourceAllocatorSimple;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.util.BytesConversion;
import org.cloudsimplus.vms.*;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.cloudsimplus.resources.Processor.cores;

/// A base class for implementing [Host]s.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 8.3.0
@Accessors @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public non-sealed abstract class HostAbstract extends ExecDelayableAbstract implements Host {
    /// The Default RAM capacity (in MB) for creating Hosts.
    /// This value is used when the RAM capacity is not given in a Host constructor.
    @Getter
    protected static long defaultRamCapacity = (long) BytesConversion.gigaToMega(10);

    /// The Default Bandwidth capacity (in Mbps) for creating Hosts.
    /// This value is used when the BW capacity is not given in a Host constructor.
    @Getter
    protected static long defaultBwCapacity = 1000;

    /// The Default Storage capacity (in MB) for creating Hosts.
    /// This value is used when the Storage capacity is not given in a Host constructor.
    @Getter
    protected static long defaultStorageCapacity = (long) BytesConversion.gigaToMega(500);

    protected final Ram ram;
    protected final Bandwidth bw;

    /// @see #getStorage()
    protected final HarddriveStorage disk;

    /// The Host state history, which is [HostStateHistory#NULL] until it's enabled,
    /// so that Hosts that don't record it don't use memory for it.
    /// It's declared as a [HostStateHistorySimple] (which [HostStateHistory#NULL] extends),
    /// so that it can be enabled or disabled without casts.
    /// @see #getStateHistory()
    private HostStateHistorySimple stateHistory;

    /// @see #getVmsMigratingIn()
    protected final Set<Vm> vmsMigratingIn;

    /// @see #getVmsMigratingOut()
    protected final Set<Vm> vmsMigratingOut;

    /// @see #addOnUpdateProcessingListener(EventListener)
    private List<EventListener<HostUpdatesVmsProcessingEventInfo>> onUpdateProcessingListeners = List.of();

    /// @see #addOnStartupListener(EventListener) (EventListener)
    private List<EventListener<HostEventInfo>> onStartupListeners = List.of();

    /// @see #addOnShutdownListener(EventListener) (EventListener)
    private List<EventListener<HostEventInfo>> onShutdownListeners = List.of();

    protected final List<Vm> vmCreatedList;

    /// @see #getVmList()
    private final List<Vm> vmList = new ArrayList<>();

    @Getter @Setter @EqualsAndHashCode.Include
    protected long id;

    /// Indicate whether the Host must be automatically started up when the assigned Datacenter is initialized or not.
    @Getter
    protected boolean activateOnDatacenterStartup;

    @Getter
    protected PowerModelHost powerModel;

    @Getter @Setter
    protected double idleShutdownDeadline;

    @Getter
    protected HostResourceStats cpuUtilizationStats;

    /// @see #getResources()
    protected List<ResourceManageable> resources;

    /// {@inheritDoc}
    ///
    /// **It's enabled by default.**
    @Getter @Setter
    protected boolean lazySuitabilityEvaluation;

    @Getter
    protected Datacenter datacenter;

    @Getter @Setter @NonNull @EqualsAndHashCode.Include
    private Simulation simulation;

    @Getter
    private boolean failed;

    @Getter
    private boolean active;

    /// Indicates if a power on/off operation is in progress.
    private boolean activationChangeInProgress;

    /// Indicates if the Host is waiting its [startup delay][#getStartupDelay()] to be powered on.
    private boolean poweringOn;

    @Getter
    private double firstStartTime = -1;

    /// @see #getTotalUpTime()
    private double totalUpTime;

    /// The RAM allocator. It's declared with the type that can allocate resources,
    /// but [#getRamAllocator()] returns it as a read-only [ResourceAllocator],
    /// so that only this Host changes allocations (when provisioning resources to VMs).
    @NonNull
    private ResourceAllocatorAbstract ramAllocator;

    /// The BW allocator.
    /// @see #ramAllocator
    @NonNull
    private ResourceAllocatorAbstract bwAllocator;

    @Getter @NonNull
    private VmScheduler vmScheduler;

    /// @see #getProcessor()
    @Getter
    private final Processor processor;

    public HostAbstract(
        final ResourceAllocator ramAllocator,
        final ResourceAllocator bwAllocator,
        final long storage,
        final CoreCapacity capacity)
    {
        this(ramAllocator.getCapacity(), bwAllocator.getCapacity(), new HarddriveStorage(storage), capacity, true);
        setRamAllocator(ramAllocator);
        setBwAllocator(bwAllocator);
    }

    public HostAbstract(final long ram, final long bw, final long storage, final CoreCapacity capacity) {
        this(ram, bw, new HarddriveStorage(storage), capacity, true);
    }

    protected HostAbstract(
        final long ram, final long bw,
        @NonNull final HarddriveStorage storage, final CoreCapacity capacity)
    {
        this(ram, bw, storage, capacity, true);
    }

    public HostAbstract(final CoreCapacity capacity) {
        this(capacity, true);
    }

    public HostAbstract(final CoreCapacity capacity, final boolean activate) {
        this(defaultRamCapacity, defaultBwCapacity, new HarddriveStorage(defaultStorageCapacity), capacity, activate);
    }

    protected HostAbstract(
        final long ram, final long bw, @NonNull final HarddriveStorage storage,
        @NonNull final CoreCapacity capacity, final boolean activate)
    {
        this.setId(-1);
        this.powerModel = PowerModelHost.NULL;
        this.activateOnDatacenterStartup = activate;
        this.ram = new Ram(ram);
        this.bw = new Bandwidth(bw);
        this.disk = storage;
        this.cpuUtilizationStats = HostResourceStats.NULL;
        this.stateHistory = HostStateHistoryNull.INSTANCE;
        this.vmsMigratingIn = new HashSet<>();
        this.vmsMigratingOut = new HashSet<>();
        this.resources = new ArrayList<>();
        this.vmCreatedList = new ArrayList<>();
        this.lazySuitabilityEvaluation = true;
        this.setSimulation(Simulation.NULL);

        this.processor = new ProcessorSimple(this, capacity);

        this.setDatacenter(Datacenter.NULL);
        this.setRamAllocator(new ResourceAllocatorSimple());
        this.setBwAllocator(new ResourceAllocatorSimple());
        this.setVmScheduler(new VmSchedulerSpaceShared());

        this.idleShutdownDeadline = DEF_IDLE_SHUTDOWN_DEADLINE;
    }

    /// Sets the Default RAM capacity (in MB) for creating Hosts.
    /// This value is used when the RAM capacity is not given in a Host constructor.
    public static void setDefaultRamCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultRamCapacity = defaultCapacity;
    }

    /// Sets the Default Bandwidth capacity (in Mbps) for creating Hosts.
    /// This value is used when the BW capacity is not given in a Host constructor.
    public static void setDefaultBwCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultBwCapacity = defaultCapacity;
    }

    /// Sets the Default Storage capacity (in MB) for creating Hosts.
    /// This value is used when the Storage capacity is not given in a Host constructor.
    public static void setDefaultStorageCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultStorageCapacity = defaultCapacity;
    }

    @Override @SuppressWarnings("ForLoopReplaceableByForEach")
    public double updateProcessing(final double currentTime) {
        if (vmList.isEmpty() && isIdleEnough(idleShutdownDeadline)) {
            setActive(false);
        }

        double nextSimulationDelay = Double.MAX_VALUE;

        /* Uses an indexed loop to avoid ConcurrentModificationException,
         * e.g., in cases when Vm is destroyed during simulation execution.*/
        for (int i = 0; i < vmList.size(); i++) {
            nextSimulationDelay = updateVmProcessing(vmList.get(i), currentTime, nextSimulationDelay);
        }

        notifyOnUpdateProcessingListeners(currentTime);
        cpuUtilizationStats.add(currentTime);
        addStateHistory(currentTime);
        if (!vmList.isEmpty()) {
            setLastBusyTime(currentTime);
        }

        return nextSimulationDelay;
    }

    protected double updateVmProcessing(final Vm vm, final double currentTime, final double nextSimulationDelay) {
        final double delay = vm.updateProcessing(currentTime, vmScheduler.getAllocatedMips(vm));
        return delay > 0 ? Math.min(delay, nextSimulationDelay) : nextSimulationDelay;
    }

    private void notifyOnUpdateProcessingListeners(final double nextSimulationTime) {
        for (final var listener : onUpdateProcessingListeners) {
            listener.update(HostUpdatesVmsProcessingEventInfo.of(listener, this, nextSimulationTime));
        }
    }

    @Override
    public HostSuitability createVm(final Vm vm) {
        final var suitability = createVmInternal(vm);
        if (suitability.fully()) {
            ((VmAbstract)vm).setHost(this);
            if(isStartupDelayed())
                LOGGER.info(
                    "{}: {}: {} is booting up in {} and it's expected to be ready in {} seconds.",
                    getSimulation().clockStr(), getClass().getSimpleName(), vm, this, getStartupDelay());
            else
                LOGGER.info(
                    "{}: {}: {} is booting up right away in {}, since no startup delay (boot time) was set.",
                    getSimulation().clockStr(), getClass().getSimpleName(), vm, this);
            addVmToCreatedList(vm);
        }

        return suitability;
    }

    @Override
    public HostSuitability createTemporaryVm(final Vm vm) {
        return createVmInternal(vm);
    }

    private HostSuitability createVmInternal(final Vm vm) {
        if (vm instanceof VmGroup) {
            return new HostSuitability(vm, "Just internal VMs inside a VmGroup can be created, not the VmGroup itself.");
        }

        final HostSuitability suitability = allocateResourcesForVm(vm, false);
        if (suitability.fully()) {
            vmList.add(vm);
            vmScheduler.updateHostBusyCores();
        }
        ((VmAbstract)vm).setCreated(suitability.fully());

        return suitability;
    }

    /// Try to allocate all resources that a VM requires (Storage, RAM, BW and MIPS)
    /// to be placed at this Host.
    ///
    /// @param vm          the VM to try allocating resources to
    /// @param inMigration indicates whether the VM is migrating into the Host
    /// or it is being just created for the first time.
    /// @return a [HostSuitability] to indicate if the Vm was placed into the host or not
    /// (if the Host doesn't have enough resources to place the Vm)
    private HostSuitability allocateResourcesForVm(final Vm vm, final boolean inMigration) {
        final HostSuitability suitability = isSuitableForVm(vm, inMigration, true);
        if (!suitability.fully()) {
            return suitability;
        }

        if (inMigration) {
            vmsMigratingIn.add(vm);
        }
        vm.setInMigration(inMigration);
        allocateResourcesForVm(vm);

        return suitability;
    }

    private void allocateResourcesForVm(final Vm vm) {
        provisionResourceForVm(vm, Ram.class, vm.getCurrentRequestedRam());
        provisionResourceForVm(vm, Bandwidth.class, vm.getCurrentRequestedBw());
        disk.getStorage().occupy(vm.getStorage());
        vmScheduler.allocateCoresForVm(vm, vm.getCurrentRequestedMips());
    }

    private void logAllocationError(
        final boolean showFailureLog, final Vm vm,
        final boolean inMigration, final String resourceUnit,
        final Resource pmResource, final Resource vmRequestedResource) {
        if (!showFailureLog) {
            return;
        }

        final var migration = inMigration ? "VM Migration" : "VM Creation";
        final var msg = pmResource.getFree() > 0 ?
            "just " + pmResource.getFree() + " " + resourceUnit :
            "no amount";
        LOGGER.error(
            "{}: {}: [{}] Allocation of {} to {} failed due to lack of {}. Required {} but there is {} available.",
            simulation.clockStr(), getClass().getSimpleName(), migration, vm, this,
            pmResource.getClass().getSimpleName(), vmRequestedResource.getCapacity(), msg);
    }

    @Override
    public void reallocateMigratingInVms() {
        for (final Vm vm : vmsMigratingIn) {
            if (!vmList.contains(vm)) {
                vmList.add(vm);
            }

            allocateResourcesForVm(vm);
        }

        vmScheduler.updateHostBusyCores();
    }

    @Override
    public boolean isSuitableForVm(final Vm vm) {
        return getSuitabilityFor(vm).fully();
    }

    /// Checks if the Host is suitable for a Vm
    /// (if it has enough resources to meet the Vm requirements)
    /// and the Host has not failed.
    ///
    /// @param vm             the Vm to check
    /// @param inMigration    indicates whether the Vm is migrating into the Host
    /// or it is being just created for the first time
    /// (in this last case, just for logging purposes).
    /// @param showFailureLog indicates if an error log must be shown when the Host is not suitable
    /// @return a [HostSuitability] object that indicate for which resources the Host
    /// is suitable or not for the given Vm
    /// @see #isLazySuitabilityEvaluation()
    private HostSuitability isSuitableForVm(final Vm vm, final boolean inMigration, final boolean showFailureLog) {
        final var suitability = new HostSuitability(this, vm);

        suitability.setForStorage(disk.isAmountAvailable(vm.getStorage()));
        if (!suitability.forStorage()) {
            logAllocationError(showFailureLog, vm, inMigration, "MB", this.getStorage(), vm.getStorage());
            if (lazySuitabilityEvaluation)
                return suitability;
        }

        suitability.setForRam(ramAllocator.isSuitableForVm(vm, vm.getRam()));
        if (!suitability.forRam()) {
            logAllocationError(showFailureLog, vm, inMigration, "MB", this.getRam(), vm.getRam());
            if (lazySuitabilityEvaluation)
                return suitability;
        }

        suitability.setForBw(bwAllocator.isSuitableForVm(vm, vm.getBw()));
        if (!suitability.forBw()) {
            logAllocationError(showFailureLog, vm, inMigration, "Mbps", this.getBw(), vm.getBw());
            if (lazySuitabilityEvaluation)
                return suitability;
        }

        suitability.setForCores(vmScheduler.isSuitableForVm(vm));
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
        if (!activate) {
            activateOnDatacenterStartup = false;
        }

        final double delay = activate ? getStartupDelay() : getShutDownDelay();
        if (this.active == activate || delay > 0 && activationChangeInProgress) {
            return this;
        }

        if (isFailed() && activate) {
            throw new IllegalStateException("The Host is failed and cannot be activated.");
        }

        if (delay == 0) {
            // If there is no delay, starts up or shuts down the Host right away.
            processActivation(activate);
            return this;
        }

        /* If the simulation is not running and there is a startup delay,
         * when the datacenter is started up, it will request such a Host activation. */
        if (!simulation.isRunning()) {
            return this;
        }

        final int tag = activate ? CloudSimTag.HOST_POWER_ON : CloudSimTag.HOST_POWER_OFF;
        final String msg = (activate ? "on" : "off") + " (expected time: {} seconds).";
        LOGGER.info("{}: {} is being powered " + msg, getSimulation().clockStr(), this, delay);
        datacenter.schedule(delay, tag, this);
        activationChangeInProgress = true;
        if (activate) {
            updatePoweringOn(true);
        }

        return this;
    }

    @Override
    public void shutdown() {
        setActive(false);
    }

    /// Process an event for actually powering the [Host] **on** or **off**
    /// after any defined startup/shutdown delay (if some delay is set).
    ///
    /// @param activate true to start the Host up, false to shut it down
    /// @see #setActive(boolean)
    public void processActivation(final boolean activate) {
        final boolean wasActive = this.active;
        if (activate) {
            setStartTime(getSimulation().clock());
            setShutdownBeginTime(NOT_ASSIGNED);
            if (poweringOn) {
                updatePoweringOn(false);
            }
            powerModel.addStartupTotals();
        } else {
            this.setFinishTime(getSimulation().clock());
            powerModel.addShutDownTotals();
        }

        this.active = activate;
        ((DatacenterSimple) datacenter).updateActiveHostsNumber(this);
        activationChangeInProgress = false;
        notifyStartupOrShutdown(activate, wasActive);
    }

    /// Sets if the Host is being powered on and updates the Datacenter accordingly.
    /// @param poweringOn true if the Host started powering on, false if it finished powering on
    private void updatePoweringOn(final boolean poweringOn) {
        this.poweringOn = poweringOn;
        ((DatacenterSimple) datacenter).updatePoweringOnHostsNumber(poweringOn);
    }

    /// Notifies registered listeners about host startup or shutdown,
    /// then prints information when the Host starts up or shuts down.
    ///
    /// @param activate  true if the Host is being activated, false it is being shut down
    /// @param wasActive the previous value of the [#active] attribute
    /// (before being updated)
    /// @see #setActive(boolean)
    private void notifyStartupOrShutdown(final boolean activate, final boolean wasActive) {
        if (Simulation.NULL.equals(simulation) || !simulation.isRunning()) {
            return;
        }

        if (activate && !wasActive) {
            LOGGER.info("{}: {} is powered on.", getSimulation().clockStr(), this);
            updateOnStartupListeners();
        } else if (!activate && wasActive) {
            final String reason = isIdleEnough(idleShutdownDeadline) ? " after becoming idle" : "";
            LOGGER.info("{}: {} is powered off{}.", getSimulation().clockStr(), this, reason);
            updateOnShutdownListeners();
        }
    }

    private void updateOnShutdownListeners() {
        for (final var listener : onShutdownListeners) {
            listener.update(HostEventInfo.of(listener, this, simulation.clock()));
        }
    }

    private void updateOnStartupListeners() {
        for (final var listener : onStartupListeners) {
            listener.update(HostEventInfo.of(listener, this, simulation.clock()));
        }
    }

    /// Destroys a Vm running in the Host and removes it from the [#getVmList()].
    /// If the Vm was not created yet, this method has no effect.
    ///
    /// @param vm the VM to be destroyed
    public void destroyVm(final Vm vm) {
        if (!vm.isCreated()) {
            return;
        }

        destroyVmInternal(vm);
    }

    /// Destroys a temporary Vm created into the Host to book resources.
    ///
    /// @param vm the Vm to be destroyed
    /// @see #createTemporaryVm(Vm)
    /// TODO https://github.com/cloudsimplus/cloudsimplus/issues/94
    public void destroyTemporaryVm(final Vm vm) {
        destroyVmInternal(vm);
    }

    public void destroyVmInternal(@NonNull final Vm vm) {
        deallocateResourcesOfVm(vm);
        vmList.remove(vm);
        vmScheduler.updateHostBusyCores();
        vm.getBroker().getVmExecList().remove(vm);
        vm.setFinishTime(getSimulation().clock());
    }

    /// Deallocate all resources that a Vm was using.
    ///
    /// @param vm the VM to deallocate resources from
    protected void deallocateResourcesOfVm(final Vm vm) {
        ramAllocator.deallocateResourceForVm(vm);
        bwAllocator.deallocateResourceForVm(vm);
        vmScheduler.deallocateCoresFromVm(vm);
        disk.getStorage().free(vm.getStorage());
        ((VmAbstract)vm).setCreated(false);
    }

    /// Destroys all Vm's running in the Host and removes them from the [#getVmList()].
    public void destroyAllVms() {
        for (final Vm vm : vmList) {
            deallocateResourcesOfVm(vm);
        }

        vmList.clear();
        vmScheduler.updateHostBusyCores();
    }

    @Override
    public Host addOnStartupListener(@NonNull final EventListener<HostEventInfo> listener) {
        if (EventListener.NULL.equals(listener)) {
            return this;
        }

        onStartupListeners = Listeners.add(onStartupListeners, listener);
        return this;
    }

    @Override
    public boolean removeOnStartupListener(@NonNull final EventListener<HostEventInfo> listener) {
        final var oldList = onStartupListeners;
        onStartupListeners = Listeners.remove(oldList, listener);
        return onStartupListeners.size() != oldList.size();
    }

    @Override
    public Host addOnShutdownListener(@NonNull final EventListener<HostEventInfo> listener) {
        if (EventListener.NULL.equals(listener)) {
            return this;
        }

        onShutdownListeners = Listeners.add(onShutdownListeners, listener);
        return this;
    }

    @Override
    public boolean removeOnShutdownListener(@NonNull final EventListener<HostEventInfo> listener) {
        final var oldList = onShutdownListeners;
        onShutdownListeners = Listeners.remove(oldList, listener);
        return onShutdownListeners.size() != oldList.size();
    }

    @Override
    public boolean removeOnUpdateProcessingListener(@NonNull final EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        final var oldList = onUpdateProcessingListeners;
        onUpdateProcessingListeners = Listeners.remove(oldList, listener);
        return onUpdateProcessingListeners.size() != oldList.size();
    }

    @Override
    public Host addOnUpdateProcessingListener(@NonNull final EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        if (EventListener.NULL.equals(listener)) {
            return this;
        }

        onUpdateProcessingListeners = Listeners.add(onUpdateProcessingListeners, listener);
        return this;
    }

    /// Gets the MIPS share of each core that is allocated to a given [Vm].
    ///
    /// @param vm the Vm to get the MIPS share
    /// @return an array containing the MIPS amount from each core that is available to the [Vm]
    protected CoreCapacity getAllocatedMipsForVm(final Vm vm) {
        return vmScheduler.getAllocatedMips(vm);
    }

    @Override
    public Resource getBw() {
        return bwAllocator.getPmResource();
    }

    @Override
    public Resource getRam() {
        return ramAllocator.getPmResource();
    }

    @Override
    public FileStorage getStorage() {
        return disk;
    }

    @Override
    public ResourceAllocator getRamAllocator() {
        return ramAllocator;
    }

    @Override
    public ResourceAllocator getBwAllocator() {
        return bwAllocator;
    }

    @Override
    public final Host setRamAllocator(@NonNull final ResourceAllocator ramAllocator) {
        checkSimulationIsRunningAndAttemptedToChangeHost("RAM");
        this.ramAllocator = asAbstract(ramAllocator);
        this.ramAllocator.setResources(ram, vm -> vm.getResource(Ram.class));
        return this;
    }

    @Override
    public final Host setBwAllocator(@NonNull final ResourceAllocator bwAllocator) {
        checkSimulationIsRunningAndAttemptedToChangeHost("BW");
        this.bwAllocator = asAbstract(bwAllocator);
        this.bwAllocator.setResources(bw, vm -> vm.getResource(Bandwidth.class));
        return this;
    }

    /// {@return a given allocator as a [ResourceAllocatorAbstract], which has the methods to allocate resources}
    /// The cast never fails, since [ResourceAllocator] only permits [ResourceAllocatorAbstract] to implement it.
    /// It's the only place where allocators are cast, and it's only called when an allocator is set.
    /// @param allocator the allocator to cast
    private static ResourceAllocatorAbstract asAbstract(final ResourceAllocator allocator) {
        return (ResourceAllocatorAbstract) allocator;
    }

    private void checkSimulationIsRunningAndAttemptedToChangeHost(final String resourceName) {
        if (simulation.isRunning()) {
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
    public void onStart(final double startTime) {
        if (firstStartTime <= NOT_ASSIGNED) {
            firstStartTime = startTime;
        }
    }

    @Override
    protected void onFinish(final double time) {
        this.totalUpTime += getUpTime();
    }

    @Override
    public boolean isFinished() {
        return !active;
    }

    @Override
    public double getUpTime() {
        return active ? simulation.clock() - getStartTime() : getFinishTime() - getStartTime();
    }

    @Override
    public double getTotalUpTime() {
        return totalUpTime + (active ? getUpTime() : 0);
    }

    @Override
    public <T extends Vm> List<T> getVmList() {
        return (List<T>) vmList;
    }

    @Override
    public <T extends Vm> List<T> getVmCreatedList() {
        return (List<T>) Collections.unmodifiableList(vmCreatedList);
    }

    protected void addVmToList(@NonNull final Vm vm) {
        vmList.add(vm);
    }

    protected void addVmToCreatedList(@NonNull final Vm vm) {
        vmCreatedList.add(vm);
    }

    @Override
    public final boolean setFailed(final boolean failed) {
        this.failed = failed;
        if (failed) {
            processor.failAllCores();
        } else {
            processor.recoverAllCores();
            vmScheduler.updateHostBusyCores();
        }

        /* Just changes the active state when the Host is set to active.
         * In other situations, the active status must remain as it was.
         * For example, if the host was inactive, and now it's set as failed, it must remain inactive. */
        if (failed && this.active) {
            this.active = false;
        }

        return true;
    }

    @Override
    public <T extends Vm> Set<T> getVmsMigratingIn() {
        return (Set<T>) vmsMigratingIn;
    }

    @Override
    public boolean hasMigratingVms() {
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
        vmScheduler.updateHostBusyCores();
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
        if (!Datacenter.NULL.equals(this.datacenter)) {
            checkSimulationIsRunningAndAttemptedToChangeHost("Datacenter");
        }

        this.datacenter = datacenter;
    }

    @Override
    public List<ResourceManageable> getResources() {
        if (simulation.isRunning() && resources.isEmpty()) {
            resources = Arrays.asList(ram, bw, processor);
        }

        return Collections.unmodifiableList(resources);
    }

    /// Allocates (reserves) an amount of a Host resource (RAM or BW) for a VM, using the allocator of that resource.
    /// @param vm the VM to allocate the resource for
    /// @param resourceClass the class of the resource to allocate
    /// @param amount the new total amount of the resource for the VM
    /// @return true if the resource was allocated, false if it couldn't be or if there is no allocator for that resource
    private boolean allocateResourceForVm(final Vm vm, final Class<? extends ResourceManageable> resourceClass, final long amount) {
        if (ramAllocator.getPmResource().isSubClassOf(resourceClass)) {
            return ramAllocator.allocateResourceForVm(vm, amount);
        }

        return bwAllocator.getPmResource().isSubClassOf(resourceClass) && bwAllocator.allocateResourceForVm(vm, amount);
    }

    @Override
    public double getCpuPercentUtilization() {
        return computeCpuUtilizationPercent(getCpuMipsUtilization());
    }

    private double computeCpuUtilizationPercent(final double mipsUsage) {
        final double totalMips = processor.getTotalMips();
        if (totalMips == 0) {
            return 0;
        }

        final double utilization = mipsUsage / totalMips;
        return utilization > 1 && utilization < 1.01 ? 1 : utilization;
    }

    @Override
    public double getCpuMipsUtilization() {
        return vmList.stream().mapToDouble(Vm::getTotalCpuMipsUtilization).sum();
    }

    @Override
    public boolean provisionResourceForVm(final Vm vm, final Class<? extends ResourceManageable> resourceClass, final long amount) {
        if (amount < 0 || !allocateResourceForVm(vm, resourceClass, amount)) {
            return false;
        }

        setVmResourceCapacity(vm.getResource(resourceClass), amount);
        return true;
    }

    /// Sets the capacity of a VM resource, keeping its current utilization,
    /// which is the amount used by the VM's Cloudlets (not the amount allocated to the VM).
    /// If such utilization is higher than the new capacity (e.g., when the VM is downscaled),
    /// it is cleared, since the Cloudlet scheduler recomputes it at the next update.
    ///
    /// @param vmResource the VM resource to set the capacity
    /// @param newCapacity the new capacity for the VM resource
    private static void setVmResourceCapacity(final ResourceManageable vmResource, final long newCapacity) {
        if (vmResource.getOccupied() > newCapacity) {
            vmResource.freeAll();
        }

        vmResource.setCapacity(newCapacity);
    }

    @Override
    public long getRamUtilization() {
        return vmList.stream().mapToLong(vm -> vm.getRam().getOccupied()).sum();
    }

    @Override
    public long getBwUtilization() {
        return vmList.stream().mapToLong(vm -> vm.getBw().getOccupied()).sum();
    }

    @Override
    public void enableUtilizationStats() {
        if (cpuUtilizationStats != null && cpuUtilizationStats != HostResourceStats.NULL) {
            return;
        }

        this.cpuUtilizationStats = new HostResourceStats(this, Host::getCpuPercentUtilization);
        if (vmList.isEmpty()) {
            final String host = this.getId() > -1 ? this.toString() : "Host";
            LOGGER.info("Automatically enabling computation of utilization statistics for VMs on {} could not be performed because it doesn't have VMs yet. You need to enable it for each VM created.", host);
        } else vmList.forEach(ResourceStatsComputer::enableUtilizationStats);
    }

    @Override
    public final PowerAware<PowerModelHost> setPowerModel(final PowerModelHost powerModel) {
        requireNonNull(powerModel,
            "powerModel cannot be null. You could provide a " +
                PowerModelHost.class.getSimpleName() + ".NULL instead.");

        if (powerModel.getHost() != null && powerModel.getHost() != Host.NULL && !this.equals(powerModel.getHost())) {
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

    /// Adds the Vm resource usage to the History if the Vm is not migrating into the Host.
    ///
    /// @param vm          the Vm to add its usage to the history
    /// @param currentTime the current simulation time
    /// @return the total allocated MIPS for the given Vm
    private double addVmResourceUseToHistoryIfNotMigratingIn(final Vm vm, final double currentTime) {
        double totalAllocatedMips = getVmScheduler().getTotalAllocatedMipsForVm(vm);
        if (getVmsMigratingIn().contains(vm)) {
            LOGGER.info("{}: {}: {} is migrating in", getSimulation().clockStr(), this, vm);
            return totalAllocatedMips;
        }

        final double totalRequestedMips = vm.getTotalCpuMipsRequested();
        if (totalAllocatedMips + 0.1 < totalRequestedMips) {
            final String reason = getVmsMigratingOut().contains(vm) ? "migration overhead" : "capacity unavailability";
            final long vmCores = cores(vm);
            final double notAllocatedMipsByCore = (totalRequestedMips - totalAllocatedMips) / vmCores;
            LOGGER.warn(
                "{}: {}: {} MIPS not allocated for each one of the {} CPU cores from {} due to {}.",
                getSimulation().clockStr(), this, "%.2f".formatted(notAllocatedMipsByCore), vmCores, vm, reason);
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
        if (!stateHistory.isEnabled()) {
            return;
        }

        double hostTotalRequestedMips = 0;

        for (final Vm vm : getVmList()) {
            final double totalRequestedMips = vm.getTotalCpuMipsRequested();
            addVmResourceUseToHistoryIfNotMigratingIn(vm, currentTime);
            hostTotalRequestedMips += totalRequestedMips;
        }

        stateHistory.add(new HostStateHistoryEntry(currentTime, getCpuMipsUtilization(), hostTotalRequestedMips, active));
    }

    @Override
    public final Host setStateHistoryEnabled(final boolean enable) {
        // The history object is only created when it's enabled for the first time
        if (stateHistory == HostStateHistory.NULL) {
            stateHistory = enable ? new HostStateHistorySimple() : HostStateHistoryNull.INSTANCE;
            return this;
        }

        stateHistory.setEnabled(enable);
        return this;
    }

    @Override
    public HostStateHistory getStateHistory() {
        return stateHistory;
    }

    @Override
    public List<Vm> getMigratableVms() {
        return vmList.stream()
                    .filter(Vm::isCreated)
                    .filter(vm -> !vm.isInMigration())
                    .collect(toList());
    }
}
