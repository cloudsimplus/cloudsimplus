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
package org.cloudsimplus.vms;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.autoscaling.VmScaling;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletAbstract;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.CustomerEntityAbstract;
import org.cloudsimplus.core.Machine;
import org.cloudsimplus.core.Startable;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.Listeners;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.util.MathUtil;
import org.cloudsimplus.utilizationmodels.BootModel;

import java.util.*;
import org.jspecify.annotations.Nullable;

import static org.cloudsimplus.resources.Processor.totalMips;


/// A base class for implementing [Vm]s.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 8.3.0
@Accessors(makeFinal = false) @Getter
public non-sealed abstract class VmAbstract extends CustomerEntityAbstract implements Vm {
    /// Gets the Default RAM capacity (in MB) for creating VMs.
    /// This value is used when the RAM capacity is not given in a VM constructor.
    private static long defaultRamCapacity = 1024;

    /// Gets the Default Bandwidth capacity (in Mbps) for creating VMs.
    /// This value is used when the BW capacity is not given in a VM constructor.
    private static long defaultBwCapacity = 100;

    /// Gets the Default Storage capacity (in MB) for creating VMs.
    /// This value is used when the Storage capacity is not given in a VM constructor.
    private static long defaultStorageCapacity = 1024;

    protected final Processor processor;
    /// @see #getStateHistory()
    protected final List<VmStateHistoryEntry> stateHistory;
    private List<EventListener<VmHostEventInfo>> onMigrationStartListeners = List.of();
    private List<EventListener<VmHostEventInfo>> onMigrationFinishListeners = List.of();
    private List<EventListener<VmHostEventInfo>> onHostPlacementListeners = List.of();
    private List<EventListener<VmHostEventInfo>> onHostRemovalListeners = List.of();
    private List<EventListener<VmHostEventInfo>> onUpdateProcessingListeners = List.of();
    private List<EventListener<VmDatacenterEventInfo>> onCreationFailureListeners = List.of();

    @Setter @NonNull
    private BootModel bootModel;

    /// @see #getCpuCap()
    @Setter @NonNull
    private CpuCap cpuCap;
    protected List<ResourceManageable> resources;

    @Setter
    protected String description;

    /// The expected number of free cores, estimated by the broker when it maps Cloudlets to the VM.
    /// It's different from the [number of free cores][Processor#getFreeCores()], which
    /// considers only Cloudlets already running.
    /// @see #getExpectedFreeCores()
    protected long expectedFreeCores;
    /// @see #setMipsShares(CoreCapacity, CoreCapacity)
    protected CoreCapacity allocatedMips;
    /// @see #setMipsShares(CoreCapacity, CoreCapacity)
    protected CoreCapacity requestedMips;
    @Setter
    private String vmm;
    private Host host;
    private double timeZone;
    private double submissionDelay;
    @Nullable
    private VmGroup group;
    private boolean failed;
    private SimpleStorage storage;
    private VmRam ram;
    private Bandwidth bw;
    @NonNull
    private CloudletScheduler cloudletScheduler;
    private boolean created;
    /// Indicates if the VM is in migration process or not.
    @Setter
    private boolean inMigration;
    private HorizontalVmScaling horizontalScaling;
    private VerticalVmScaling ramVerticalScaling;
    private VerticalVmScaling bwVerticalScaling;
    private VerticalVmScaling cpuVerticalScaling;
    private VmResourceStats cpuUtilizationStats;

    /// A copy constructor that creates a VM based on the configuration of another one.
    /// The created VM will have the same MIPS capacity, number of CPU cores,
    /// BW, RAM and size of the given VM, but a default [CloudletScheduler] and no [DatacenterBroker].
    /// @param sourceVm the VM to be cloned
    public VmAbstract(final Vm sourceVm) {
        this(-1, sourceVm.getProcessor().toCoreCapacity());
        this.cpuCap = sourceVm.getCpuCap();
        this.setBw(sourceVm.getBw().getCapacity())
            .setRam(sourceVm.getRam().getCapacity())
            .setSize(sourceVm.getStorage().getCapacity());
    }

    protected VmAbstract(final long id, final CoreCapacity capacity) {
        this(id, capacity, new CloudletSchedulerTimeShared());
    }

    @SuppressWarnings("NullAway.Init") // fields are initialized by mutableAttributesInit()
    protected VmAbstract(final long id, @NonNull final CoreCapacity capacity, final CloudletScheduler cloudletScheduler) {
        super();
        setId(id);
        this.processor = new ProcessorSimple(this, capacity);
        setCloudletScheduler(cloudletScheduler);

        this.resources = new ArrayList<>(4);
        this.bootModel = BootModel.NULL;
        this.cpuCap = CpuCap.NONE;

        this.expectedFreeCores = capacity.cores();

        this.allocatedMips = CoreCapacity.NULL;
        this.requestedMips = CoreCapacity.NULL;
        this.stateHistory = new LinkedList<>();
        mutableAttributesInit();
    }

    /// Sets the Default RAM capacity (in MB) for creating VMs.
    /// This value is used when the RAM capacity is not given in a VM constructor.
    public static void setDefaultRamCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultRamCapacity = defaultCapacity;
    }

    /// Sets the Default Bandwidth capacity (in Mbps) for creating VMs.
    /// This value is used when the BW capacity is not given in a VM constructor.
    public static void setDefaultBwCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultBwCapacity = defaultCapacity;
    }

    /// Sets the Default Storage capacity (in MB) for creating VMs.
    /// This value is used when the Storage capacity is not given in a VM constructor.
    public static void setDefaultStorageCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultStorageCapacity = defaultCapacity;
    }

    /// Accepts a Vm or `List<Vm>`. If a Vm is given, it is returned.
    /// If a List is given, returns the first VM in that List.
    /// @param vmOrList a single Vm object or a `List<Vm>`
    /// @return the Vm object or the first Vm inside the List (according to the given parameter)
    /// @param <T> the type of the parameter, which may be a Vm or `List<Vm>`
    public static <T> VmAbstract getFirstVm(final T vmOrList) {
        return isVmList(vmOrList) ? ((List<VmAbstract>) vmOrList).get(0) : (VmAbstract) vmOrList;
    }

    /// Accepts a Vm or `List<Vm>` and return a `List<Vm>`
    /// @param vmOrList a single Vm object or a `List<Vm>`
    /// @return a List with 1 Vm object if a Vm instance is given; or a `List<Vm>` if a List is given
    /// @param <T> the type of the parameter, which may be a Vm or `List<Vm>`
    public static <T> List<Vm> getList(final T vmOrList) {
        return isVmList(vmOrList) ? ((List<Vm>) vmOrList) : List.of((Vm) vmOrList);
    }

    /// Checks if an object is a Vm or `List<Vm>`.
    /// @param vmOrList a Vm `List<Vm>`
    /// @return true if it's a `List<Vm>`, false otherwise
    /// @param <T> the type of the parameter, which may be a Vm or `List<Vm>`
    public static <T> boolean isVmList(final T vmOrList) {
        return vmOrList instanceof List<?>;
    }

    /// Accepts a single Vm object or a `List<Vm>` and returns the number of Vms in the given object.
    /// @param vmOrList a Vm `List<Vm>`
    /// @return 1 if the given object is a single Vm object, [List#size()] if it's a List.
    /// @param <T> the type of the parameter, which may be a Vm or `List<Vm>`
    public static <T> int getVmCount(final T vmOrList) {
        return isVmList(vmOrList) ? ((List<Vm>)vmOrList).size() : 1;
    }

    protected void mutableAttributesInit() {
        this.description = "";
        setBroker(DatacenterBroker.NULL);
        setSubmissionDelay(0);
        setVmm("Xen");

        setInMigration(false);
        this.host = Host.NULL;

        this.setHorizontalScaling(HorizontalVmScaling.NULL);
        this.setRamVerticalScaling(VerticalVmScaling.NULL);
        this.setBwVerticalScaling(VerticalVmScaling.NULL);
        this.setCpuVerticalScaling(VerticalVmScaling.NULL);

        cpuUtilizationStats = VmResourceStats.NULL;

        setRam(new VmRam(this, defaultRamCapacity));
        setBw(new Bandwidth(defaultBwCapacity));
        setStorage(new SimpleStorage(defaultStorageCapacity));
    }

    /// Updates the processing of cloudlets running on this VM at the current simulation time.
    ///
    /// @param mipsShare list with MIPS share of each core available to the scheduler
    /// @return the predicted completion time of the earliest finishing cloudlet
    /// (which is a relative delay from the current simulation time),
    /// or [Double#MAX_VALUE] if there is no next Cloudlet to execute
    public double updateProcessing(final CoreCapacity mipsShare) {
        return updateProcessing(getSimulation().clock(), mipsShare);
    }

    /// Updates the processing of cloudlets running on this VM.
    ///
    /// @param currentTime current simulation time
    /// @param mipsShare list with MIPS share of each core available to the scheduler
    /// @return the predicted completion time of the earliest finishing cloudlet
    /// (which is a relative delay from the current simulation time),
    /// or [Double#MAX_VALUE] if there is no next Cloudlet to execute
    public double updateProcessing(final double currentTime, @NonNull final CoreCapacity mipsShare) {
        if (!cloudletScheduler.isEmpty() || isStartingUp()) {
            setLastBusyTime(getSimulation().clock());
        }

        if(isStartupDelayed() && currentTime == getStartupCompletionTime())
            LOGGER.info("{}: {}: {} has completed booting up.", getSimulation().clockStr(), getClass().getSimpleName(), this);

        return isShuttingDown() ? Double.MAX_VALUE : processing(currentTime, mipsShare);
    }

    private double processing(final double currentTime, final CoreCapacity mipsShare) {
        notifyOnUpdateProcessingListeners();
        return isStartingUp() ? bootProcessing() : cloudletsProcessing(currentTime, mipsShare);
    }

    /// Process the VM boot up.
    /// @return the remaining startup time that indicates how longer the boot process will take
    /// so that the vm processing is updated after that time.
    private double bootProcessing() {
        final var dc = host.getDatacenter();
        final double nextProcessingTime = dc.getSchedulingInterval() > -1 ? dc.getSchedulingInterval() : getRemainingStartupTime();
        dc.schedule(nextProcessingTime, CloudSimTag.VM_UPDATE_CLOUDLET_PROCESSING);
        return nextProcessingTime;
    }

    private double cloudletsProcessing(final double currentTime, final CoreCapacity mipsShare) {
        final double nextSimulationDelay = cloudletScheduler.updateProcessing(currentTime, mipsShare);

        cpuUtilizationStats.add(currentTime);
        getBroker().requestIdleVmDestruction(this);
        if (nextSimulationDelay == Double.MAX_VALUE) {
            return nextSimulationDelay;
        }

        /* If the current time is some value with the decimals greater than x.0
         * (such as 45.1) and the next event delay is any integer number such as 5,
         * then the next simulation time would be 50.1.
         * At time 50.1 the utilization will be reduced due to the completion of the Cloudlet.
         * At time 50.0 the Cloudlet is still running, so there is some CPU utilization.
         * But since the next update would be only at time 50.1, the utilization
         * at time 50.0 wouldn't be collected to enable knowing the exact time
         * before the utilization drops.
         * The condition and computation below is used to ensure VM processing occurs
         * at time 50 and 50.1.
         */
        final double decimals = currentTime - (int) currentTime;
        return nextSimulationDelay - decimals < 0 ? nextSimulationDelay : nextSimulationDelay - decimals;
    }

    /// Adds a given number of expected free cores to the total number of expected free cores.
    /// This value is updated as cloudlets are assigned to VMs but not submitted to the broker for running yet.
    ///
    /// @param coresToAdd the number of expected free cores to add
    public Vm addExpectedFreeCores(final long coresToAdd) {
        return setExpectedFreeCores(expectedFreeCores + coresToAdd);
    }

    /// Removes a given number of expected free cores from the total number of expected free cores.
    /// This value is updated as cloudlets are assigned to VMs but not submitted to the broker for running yet.
    ///
    /// @param coresToRemove the number of expected free cores to remove
    public Vm removeExpectedFreeCores(final long coresToRemove) {
        return setExpectedFreeCores(expectedFreeCores - coresToRemove);
    }

    /// Sets the expected number of free cores before the VM starts executing.
    ///
    /// @param expectedFreeCores the expected number of free cores to set
    private Vm setExpectedFreeCores(final long expectedFreeCores) {
        this.expectedFreeCores = Math.clamp(expectedFreeCores, 0, processor.getCores());
        return this;
    }

    @Override
    public double getCpuPercentUtilization() {
        return getCpuPercentUtilization(getSimulation().clock());
    }

    @Override
    public double getCpuPercentUtilization(final double time) {
        return isStartingUp() ? bootModel.getCpuPercentUtilization() : cloudletScheduler.getCpuPercentUtilization(time);
    }

    @Override
    public double getHostCpuUtilization(final double time) {
        return getExpectedHostCpuUtilization(getCpuPercentUtilization(time));
    }

    @Override
    public double getExpectedHostCpuUtilization(final double vmCpuUtilizationPercent) {
        return vmCpuUtilizationPercent * getTotalMipsCapacity() / totalMips(host);
    }

    @Override
    public double getHostRamUtilization() {
        return ram.getOccupied() / (double) host.getRam().getCapacity();
    }

    @Override
    public double getHostBwUtilization() {
        return bw.getOccupied() / (double) host.getBw().getCapacity();
    }

    @Override
    public double getTotalCpuMipsUtilization() {
        return getTotalCpuMipsUtilization(getSimulation().clock());
    }

    @Override
    public double getTotalCpuMipsUtilization(final double time) {
        return getCpuPercentUtilization(time) * getTotalMipsCapacity();
    }

    @Override
    public double getTotalCpuMipsRequested() {
        return getCurrentRequestedMips().totalMips();
    }

    @Override
    public CoreCapacity getCurrentRequestedMips() {
        // TODO This method is confusing, since there is a getRequestedMips() (created with lombok)
        // See https://github.com/cloudsimplus/cloudsimplus/issues/591
        if (isCreated() && !isStartingUp()) {
            return host.getVmScheduler().getRequestedMips(this);
        }

        return getMipsToRequest();
    }

    /// Gets the MIPS share the VM has to request from its Host, according to its processor,
    /// [CPU cap][#getCpuCap()] and [boot state][#isStartingUp()].
    /// While the VM is booting, it requests just the CPU percentage defined by its [BootModel].
    /// @return the MIPS share to request from the Host
    public CoreCapacity getMipsToRequest() {
        return isStartingUp() ? cpuCap.limit(processor, bootModel.getCpuPercentUtilization()) : cpuCap.limit(processor);
    }

    /// Sets the MIPS shares a [VmScheduler][org.cloudsimplus.schedulers.vm.VmScheduler] requested for and allocated to this VM.
    /// Both shares always change together. This method is meant to be called just by VmSchedulers,
    /// since the Host cores depend on these shares.
    ///
    /// @param requestedMips the MIPS share requested by the VM
    /// @param allocatedMips the MIPS share allocated to the VM
    /// @return this VM
    public VmAbstract setMipsShares(@NonNull final CoreCapacity requestedMips, @NonNull final CoreCapacity allocatedMips) {
        this.requestedMips = requestedMips;
        this.allocatedMips = allocatedMips;
        return this;
    }

    @Override
    public long getCurrentRequestedBw() {
        if (isCreated()) {
            return (long)(cloudletScheduler.getCurrentRequestedBwPercentUtilization() * bw.getCapacity());
        }

        return bw.getCapacity();
    }

    @Override
    public double getTotalMipsCapacity() {
        return cpuCap.limitTotalMips(processor);
    }

    @Override
    public long getCurrentRequestedRam() {
        if (isCreated()) {
            final double percent = isStartingUp() ? bootModel.getRamPercentUtilization() : cloudletScheduler.getCurrentRequestedRamPercentUtilization();
            return (long)(percent * ram.getCapacity());
        }

        return ram.getCapacity();
    }

    @Override
    protected void onStart(final double time) {/**/}

    @Override
    protected void onFinish(final double time) {
        notifyOnHostRemovalListeners(host);
    }

    /// Checks if the VM has ever started some Cloudlet.
    /// @return true if the VM has started some Cloudlet, false otherwise
    public boolean hasStartedSomeCloudlet() {
        return getLastBusyTime() > NOT_ASSIGNED;
    }

    /// Sets a new [Ram] resource for the Vm.
    ///
    /// @param ram the Ram resource to set
    private void setRam(@NonNull final VmRam ram) {
        this.ram = ram;
    }

    @Override
    public final Vm setRam(final long ramCapacity) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("RAM capacity can just be changed when the Vm was not created inside a Host yet.");
        }

        setRam(new VmRam(this, ramCapacity));
        return this;
    }

    /// Sets a new [Bandwidth] resource for the Vm.
    ///
    /// @param bw the Bandwidth resource to set
    private void setBw(@NonNull final Bandwidth bw) {
        this.bw = bw;
    }

    @Override
    public final Vm setBw(final long bwCapacity) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("Bandwidth capacity can just be changed when the Vm was not created inside a Host yet.");
        }
        setBw(new Bandwidth(bwCapacity));
        return this;
    }

    /// Sets a new [SimpleStorage] resource for the Vm.
    ///
    /// @param storage the storage resource to set
    protected void setStorage(@NonNull final SimpleStorage storage) {
        this.storage = storage;
    }

    @Override
    public final Vm setSize(final long size) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("Storage size can just be changed when the Vm was not created inside a Host yet.");
        }
        setStorage(new SimpleStorage(size));
        return this;
    }

    /// Sets the PM that hosts the VM.
    /// Only the [Host] calls it, when it creates the VM, so that the VM and the Host are always in sync.
    ///
    /// @param host Host to run the VM
    /// @return this Vm
    public Vm setHost(@NonNull final Host host) {
        this.host = host;
        return this;
    }

    @Override
    public final Vm setCloudletScheduler(@NonNull final CloudletScheduler cloudletScheduler) {
        if (isCreated()) {
            throw new UnsupportedOperationException("CloudletScheduler can just be changed when the Vm was not created inside a Host yet.");
        }

        this.cloudletScheduler = cloudletScheduler;
        this.cloudletScheduler.setVm(this);
        return this;
    }

    /// Notifies the listeners when the VM starts migration to a target Host.
    ///
    /// @param targetHost the Host the VM is migrating to
    public void updateMigrationStartListeners(final Host targetHost) {
        for (final var listener : onMigrationStartListeners) {
            listener.update(VmHostEventInfo.of(listener, this, targetHost));
        }
    }

    /// Notifies the listeners when the VM finishes migration to a target Host.
    ///
    /// @param targetHost the Host the VM has just migrated to
    public void updateMigrationFinishListeners(final Host targetHost) {
        for (final var listener : onMigrationFinishListeners) {
            listener.update(VmHostEventInfo.of(listener, this, targetHost));
        }
    }

    @Override
    public boolean isSuitableForCloudlet(final Cloudlet cloudlet) {
        return processor.getCores() >= cloudlet.getRequiredCores() &&
            storage.getFree() >= cloudlet.getFileSize();
    }

    /// Changes the created status of the Vm inside the Host.
    /// Only the [Host] calls it, when it creates or destroys the VM, so that the VM and the Host are always in sync.
    ///
    /// @param requestCreation true to indicate the VM was created inside the Host; false otherwise
    /// @see #isCreated()
    public void setCreated(final boolean requestCreation) {
        if (requestCreation && !this.created) {
            setCreationTime();
            setStartTime(getSimulation().clock());
            setShutdownBeginTime(NOT_ASSIGNED);
            this.setFailed(false);
            validateAndConfigureVmScaling(cpuVerticalScaling);
            validateAndConfigureVmScaling(ramVerticalScaling);
            validateAndConfigureVmScaling(bwVerticalScaling);
            validateAndConfigureVmScaling(horizontalScaling);
        }

        this.created = requestCreation;
    }

    @Override
    public Startable setStartTime(final double startTime) {
        cloudletScheduler.setPreviousTime(startTime);
        return super.setStartTime(startTime);
    }

    @Override
    public List<VmStateHistoryEntry> getStateHistory() {
        /*
         * @TODO Instead of using a list, this attribute would be a map, where the
         *       key can be the history time and the value the history itself. This
         *       way, if one wants to get the history for a given time, he/she doesn't
         *       have to iterate over the entire list to find the desired entry.
         * See https://github.com/cloudsimplus/cloudsimplus/issues/523
         */
        return Collections.unmodifiableList(stateHistory);
    }

    /// Adds a VM state history entry.
    ///
    /// @param entry the data about the state of the VM at given time
    public void addStateHistoryEntry(final VmStateHistoryEntry entry) {
        if (!stateHistory.isEmpty()) {
            final VmStateHistoryEntry previousState = stateHistory.get(stateHistory.size() - 1);
            if (previousState.getTime() == entry.getTime()) {
                stateHistory.set(stateHistory.size() - 1, entry);
                return;
            }
        }
        stateHistory.add(entry);
    }

    @Override
    public List<ResourceManageable> getResources() {
        if (getSimulation().isRunning() && resources.isEmpty()) {
            resources = Arrays.asList(ram, bw, storage, processor);
        }

        return Collections.unmodifiableList(resources);
    }

    @Override
    public Vm addOnHostPlacementListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        onHostPlacementListeners = Listeners.add(onHostPlacementListeners, listener);
        return this;
    }

    @Override
    public Vm addOnMigrationStartListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        onMigrationStartListeners = Listeners.add(onMigrationStartListeners, listener);
        return this;
    }

    @Override
    public Vm addOnMigrationFinishListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        onMigrationFinishListeners = Listeners.add(onMigrationFinishListeners, listener);
        return this;
    }

    @Override
    public Vm addOnHostRemovalListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        onHostRemovalListeners = Listeners.add(onHostRemovalListeners, listener);
        return this;
    }

    @Override
    public Vm addOnCreationFailureListener(@NonNull final EventListener<VmDatacenterEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        onCreationFailureListeners = Listeners.add(onCreationFailureListeners, listener);
        return this;
    }

    @Override
    public Vm addOnUpdateProcessingListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        onUpdateProcessingListeners = Listeners.add(onUpdateProcessingListeners, listener);
        return this;
    }

    @Override
    public boolean removeOnCreationFailureListener(@NonNull final EventListener<VmDatacenterEventInfo> listener) {
        final var oldList = onCreationFailureListeners;
        onCreationFailureListeners = Listeners.remove(oldList, listener);
        return onCreationFailureListeners.size() != oldList.size();
    }

    @Override
    public boolean removeOnUpdateProcessingListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        final var oldList = onUpdateProcessingListeners;
        onUpdateProcessingListeners = Listeners.remove(oldList, listener);
        return onUpdateProcessingListeners.size() != oldList.size();
    }

    @Override
    public boolean removeOnHostPlacementListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        final var oldList = onHostPlacementListeners;
        onHostPlacementListeners = Listeners.remove(oldList, listener);
        return onHostPlacementListeners.size() != oldList.size();
    }

    @Override
    public boolean removeOnHostRemovalListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        final var oldList = onHostRemovalListeners;
        onHostRemovalListeners = Listeners.remove(oldList, listener);
        return onHostRemovalListeners.size() != oldList.size();
    }

    @Override
    public void setFailed(final boolean failed) {
        this.failed = failed;

        if (failed) {
            setCloudletsToFailed();
        }
    }

    public void setCloudletsToFailed() {
        getBroker().getCloudletWaitingList()
            .stream()
            .filter(cl -> this.equals(cl.getVm()))
            .forEach(cl -> ((CloudletAbstract)cl).setStatus(Cloudlet.Status.FAILED_RESOURCE_UNAVAILABLE));
    }

    @Override
    public boolean isWorking() {
        return !isFailed();
    }

    @Override
    public final void setSubmissionDelay(final double submissionDelay) {
        this.submissionDelay = MathUtil.nonNegative(submissionDelay, "submissionDelay");
    }

    @Override
    public boolean isSubmissionDelayed() {
        return submissionDelay > 0;
    }

    /// Notifies all registered listeners when the [Vm] is placed into a [Host].
    public void notifyOnHostPlacementListeners() {
        for (final var listener : onHostPlacementListeners) {
            listener.update(VmHostEventInfo.of(listener, this));
        }
    }

    /// Notifies all registered listeners when the [Vm] is moved/removed from a [Host].
    ///
    /// @param deallocatedHost the [Host] the [Vm] was moved/removed from
    private void notifyOnHostRemovalListeners(@NonNull final Host deallocatedHost) {
        for (final var listener : onHostRemovalListeners) {
            listener.update(VmHostEventInfo.of(listener, this, deallocatedHost));
        }
    }

    /// Notifies all registered listeners when the processing of the Vm is updated in its [Host].
    private void notifyOnUpdateProcessingListeners() {
        for (final var listener : onUpdateProcessingListeners) {
            listener.update(VmHostEventInfo.of(listener, this));
        }
    }

    /// Notifies all registered listeners when the Vm fail in
    /// being placed for lack of a [Host] with enough resources in a specific [Datacenter].
    ///
    /// @param failedDatacenter the Datacenter where the VM creation failed
    public void notifyOnCreationFailureListeners(@NonNull final Datacenter failedDatacenter) {
        for (final var listener : onCreationFailureListeners) {
            listener.update(VmDatacenterEventInfo.of(listener, this, failedDatacenter));
        }
    }

    @Override
    public boolean removeOnMigrationStartListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        final var oldList = onMigrationStartListeners;
        onMigrationStartListeners = Listeners.remove(oldList, listener);
        return onMigrationStartListeners.size() != oldList.size();
    }

    @Override
    public boolean removeOnMigrationFinishListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        final var oldList = onMigrationFinishListeners;
        onMigrationFinishListeners = Listeners.remove(oldList, listener);
        return onMigrationFinishListeners.size() != oldList.size();
    }

    @Override
    public final Vm setHorizontalScaling(final HorizontalVmScaling horizontalScaling) throws IllegalArgumentException {
        this.horizontalScaling = horizontalScaling;
        return this;
    }

    @Override
    public final Vm setRamVerticalScaling(final VerticalVmScaling ramVerticalScaling) throws IllegalArgumentException {
        this.ramVerticalScaling = ramVerticalScaling;
        return this;
    }

    @Override
    public final Vm setBwVerticalScaling(final VerticalVmScaling bwVerticalScaling) throws IllegalArgumentException {
        this.bwVerticalScaling = bwVerticalScaling;
        return this;
    }

    @Override
    public final Vm setCpuVerticalScaling(final VerticalVmScaling cpuVerticalScaling) throws IllegalArgumentException {
        this.cpuVerticalScaling = cpuVerticalScaling;
        return this;
    }

    private void validateAndConfigureVmScaling(@NonNull final VmScaling vmScaling) {
        if (vmScaling.getVm() != null && vmScaling.getVm() != NULL && vmScaling.getVm() != this) {
            final var name = vmScaling.getClass().getSimpleName();
            final var msg = "The %1$s given is already linked to a Vm. " +
                            "Each Vm must have its own %1$s object or none at all. " +
                            "Another %1$s has to be provided for this Vm.";
            throw new IllegalArgumentException(msg.formatted(name));
        }

        vmScaling.setVm(this);
        this.addOnUpdateProcessingListener(vmScaling::requestUpScalingIfPredicateMatches);
    }

    @Override
    public void enableUtilizationStats() {
        if (cpuUtilizationStats == null || cpuUtilizationStats == VmResourceStats.NULL) {
            this.cpuUtilizationStats = new VmResourceStats(this, vm -> vm.getCpuPercentUtilization(getSimulation().clock()));
        }
    }

    @Override
    public Vm setTimeZone(final double timeZone) {
        this.timeZone = validateTimeZone(timeZone);
        return this;
    }

    @Override
    public boolean isFinished() {
        return !created;
    }

    public Ram getRam() {
        return this.ram;
    }

    public Vm setDescription(final String description) {
        this.description = description;
        return this;
    }

    public Vm setVmm(final String vmm) {
        this.vmm = vmm;
        return this;
    }

    /// Sets the group this VM belongs to. Only the [VmGroup] calls it, when the VM is added to the group.
    /// @param group the group of the VM
    /// @return this VM
    Vm setGroup(final VmGroup group) {
        this.group = group;
        return this;
    }

    @Override
    public void shutdown() {
        final var lifeTimeMsg = this.isLifeTimeReached() ? " after reaching defined lifetime" : "";
        final var shutDownMsg = this.isShutDownDelayed()
                                        ? "expected to finish in %.2f seconds".formatted(this.getShutDownDelay())
                                        : "will finish immediately (since no Vm shutDownDelay was set)";
        this.setShutdownBeginTime(getSimulation().clock());
        LOGGER.info(
            "{}: {}: Requesting {} destruction on {}{}. Shutdown {}.",
            getSimulation().clockStr(), getClass().getSimpleName(), this, this.host, lifeTimeMsg, shutDownMsg);
        final var dc = host.getDatacenter();
        dc.schedule(dc, this.getShutDownDelay(), CloudSimTag.VM_DESTROY, this);
    }
}
