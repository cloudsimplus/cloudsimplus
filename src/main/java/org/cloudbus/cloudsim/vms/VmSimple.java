/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.AbstractMachine;
import org.cloudbus.cloudsim.core.CustomerEntityAbstract;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.MipsShare;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.util.MathUtil;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.autoscaling.VmScaling;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Implements the basic features of a Virtual Machine (VM), which runs inside a
 * {@link Host} that may be shared among other VMs. It processes
 * {@link Cloudlet cloudlets}. This processing happens according to a policy,
 * defined by the {@link CloudletScheduler}. Each VM has an owner (user), which
 * can submit cloudlets to the VM to execute them.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
@Accessors(makeFinal = false) @Getter
public class VmSimple extends CustomerEntityAbstract implements Vm {
    /** @see #setDefaultRamCapacity(long) */
    private static long defaultRamCapacity = 1024;

    /** @see #setDefaultBwCapacity(long) */
    private static long defaultBwCapacity = 100;

    /** @see #setDefaultStorageCapacity(long) */
    private static long defaultStorageCapacity = 1024;

    @Setter
    private String description;

    @Setter
    private String vmm;

    private Host host;

    private double timeZone;

    private double submissionDelay;
    private double startTime;
    private double stopTime;
    private double lastBusyTime;

    @Setter @NonNull
    private VmGroup group;

    private boolean failed;

    private SimpleStorage storage;

    private Ram ram;

    private Bandwidth bw;
    private final Processor processor;

    @NonNull
    private CloudletScheduler cloudletScheduler;

    private boolean created;

    @Setter
    private boolean inMigration;

    private List<ResourceManageable> resources;

    private long freePesNumber;

    private long expectedFreePesNumber;

    private HorizontalVmScaling horizontalScaling;
    private VerticalVmScaling ramVerticalScaling;
    private VerticalVmScaling bwVerticalScaling;
    private VerticalVmScaling peVerticalScaling;

    @Setter @NonNull
    private MipsShare allocatedMips;

    @Setter @NonNull
    private MipsShare requestedMips;

    private VmResourceStats cpuUtilizationStats;

    /** @see #getStateHistory() */
    private final List<VmStateHistoryEntry> stateHistory;

    private final List<EventListener<VmHostEventInfo>> onMigrationStartListeners;
    private final List<EventListener<VmHostEventInfo>> onMigrationFinishListeners;
    private final List<EventListener<VmHostEventInfo>> onHostAllocationListeners;
    private final List<EventListener<VmHostEventInfo>> onHostDeallocationListeners;
    private final List<EventListener<VmHostEventInfo>> onUpdateProcessingListeners;
    private final List<EventListener<VmDatacenterEventInfo>> onCreationFailureListeners;

    /**
     * A copy constructor that creates a VM based on the configuration of another one.
     * The created VM will have the same MIPS capacity, number of PEs,
     * BW, RAM and size of the given VM, but a default CloudletScheduler and no broker.
     * @param sourceVm the VM to be cloned
     * @see #VmSimple(double, long)
     */
    public VmSimple(final Vm sourceVm) {
        this(sourceVm.getMips(), sourceVm.getNumberOfPes());
        this.setBw(sourceVm.getBw().getCapacity())
            .setRam(sourceVm.getRam().getCapacity())
            .setSize(sourceVm.getStorage().getCapacity());
    }

    /**
     * Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is being instantiated}, such values can be changed freely.
     *
     * <p>It is not defined an id for the Vm. The id is defined when the Vm is submitted to
     * a {@link DatacenterBroker}.</p>
     *
     * <p><b>NOTE:</b> The Vm will use a {@link CloudletSchedulerTimeShared} by default. If you need to change that,
     * just call {@link #setCloudletScheduler(CloudletScheduler)}.</p>
     *
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes  amount of {@link Pe} (CPU cores)
     * @see #setRam(long)
     * @see #setBw(long)
     * @see #setStorage(SimpleStorage)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public VmSimple(final double mipsCapacity, final long numberOfPes) {
        this(-1, mipsCapacity, numberOfPes);
    }

    /**
     * Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is being instantiated}, such values can be changed freely.
     *
     * <p>It is not defined an id for the Vm. The id is defined when the Vm is submitted to
     * a {@link DatacenterBroker}.</p>
     *
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes  amount of {@link Pe} (CPU cores)
     * @see #setRam(long)
     * @see #setBw(long)
     * @see #setStorage(SimpleStorage)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public VmSimple(final double mipsCapacity, final long numberOfPes, final CloudletScheduler cloudletScheduler) {
        this(-1, mipsCapacity, numberOfPes);
        setCloudletScheduler(cloudletScheduler);
    }

    /**
     * Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
     * <p>
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is being instantiated}, such values can be changed freely.
     *
     * <p>It receives the amount of MIPS as a double value but converts it internally
     * to a long. The method is just provided as a handy-way to create a Vm
     * using a double value for MIPS that usually is generated from some computations.</p>
     *
     * <p><b>NOTE:</b> The Vm will use a {@link CloudletSchedulerTimeShared} by default. If you need to change that,
     * just call {@link #setCloudletScheduler(CloudletScheduler)}.</p>
     *
     * @param id           unique ID of the VM
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes  amount of {@link Pe} (CPU cores)
     * @see #setRam(long)
     * @see #setBw(long)
     * @see #setStorage(SimpleStorage)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public VmSimple(final long id, final double mipsCapacity, final long numberOfPes) {
        this(id, (long) mipsCapacity, numberOfPes);
    }

    /**
     * Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and
     * 1024 MEGA of Storage Size.
     *
     * <p>
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is being instantiated}, such values can be changed freely.
     *
     * <p><b>NOTE:</b> The Vm will use a {@link CloudletSchedulerTimeShared} by default. If you need to change that,
     * just call {@link #setCloudletScheduler(CloudletScheduler)}.</p>
     *
     * @param id           unique ID of the VM
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes  amount of {@link Pe} (CPU cores)
     * @see #setRam(long)
     * @see #setBw(long)
     * @see #setStorage(SimpleStorage)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public VmSimple(final long id, final long mipsCapacity, final long numberOfPes) {
        super();
        setId(id);
        this.resources = new ArrayList<>(4);
        this.onMigrationStartListeners = new ArrayList<>();
        this.onMigrationFinishListeners = new ArrayList<>();
        this.onHostAllocationListeners = new ArrayList<>();
        this.onHostDeallocationListeners = new ArrayList<>();
        this.onCreationFailureListeners = new ArrayList<>();
        this.onUpdateProcessingListeners = new ArrayList<>();
        this.stateHistory = new LinkedList<>();
        this.allocatedMips = new MipsShare();
        this.requestedMips = new MipsShare();

        this.processor = new Processor(this, numberOfPes, mipsCapacity);
        setMips(mipsCapacity);
        setNumberOfPes(numberOfPes);

        mutableAttributesInit();

        //initiate number of free PEs as number of PEs of VM
        freePesNumber = numberOfPes;
        expectedFreePesNumber = numberOfPes;
    }

    private void mutableAttributesInit() {
        this.description = "";
        this.startTime = -1;
        this.stopTime = -1;
        this.lastBusyTime = Double.MAX_VALUE;
        setBroker(DatacenterBroker.NULL);
        setSubmissionDelay(0);
        setVmm("Xen");

        setInMigration(false);
        this.host = Host.NULL;
        setCloudletScheduler(new CloudletSchedulerTimeShared());

        this.setHorizontalScaling(HorizontalVmScaling.NULL);
        this.setRamVerticalScaling(VerticalVmScaling.NULL);
        this.setBwVerticalScaling(VerticalVmScaling.NULL);
        this.setPeVerticalScaling(VerticalVmScaling.NULL);

        cpuUtilizationStats = VmResourceStats.NULL;

        setRam(new Ram(defaultRamCapacity));
        setBw(new Bandwidth(defaultBwCapacity));
        setStorage(new SimpleStorage(defaultStorageCapacity));
    }

    @Override
    public double updateProcessing(MipsShare mipsShare) {
        return updateProcessing(getSimulation().clock(), mipsShare);
    }

    @Override
    public double updateProcessing(final double currentTime, final MipsShare mipsShare) {
        requireNonNull(mipsShare);

        if (!cloudletScheduler.isEmpty()) {
            setLastBusyTime();
        }
        final double nextSimulationDelay = cloudletScheduler.updateProcessing(currentTime, mipsShare);
        notifyOnUpdateProcessingListeners();

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
         * before the utilization drop.
         * Condition and computation below is used to ensure VM processing occurs
         * at time 50 and 50.1.
         */
        final double decimals = currentTime - (int) currentTime;
        return nextSimulationDelay - decimals < 0 ? nextSimulationDelay : nextSimulationDelay - decimals;
    }

    /**
     * Sets the current number of free PEs.
     *
     * @return the new free pes number
     */
    public Vm setFreePesNumber(long freePesNumber) {
        if (freePesNumber < 0) {
            freePesNumber = 0;
        }
        this.freePesNumber = Math.min(freePesNumber, getNumberOfPes());
        return this;
    }

    /**
     * Adds a given number of expected free PEs to the total number of expected free PEs.
     * This value is updated as cloudlets are assigned to VMs but not submitted to the broker for running yet.
     *
     * @param pesToAdd the number of expected free PEs to add
     */
    public Vm addExpectedFreePesNumber(final long pesToAdd) {
        return setExpectedFreePesNumber(expectedFreePesNumber + pesToAdd);
    }

    /**
     * Adds a given number of expected free PEs to the total number of expected free PEs.
     * This value is updated as cloudlets are assigned to VMs but not submitted to the broker for running yet.
     *
     * @param pesToRemove the number of expected free PEs to remove
     */
    public Vm removeExpectedFreePesNumber(final long pesToRemove) {
        return setExpectedFreePesNumber(expectedFreePesNumber - pesToRemove);
    }

    /**
     * Sets the expected free PEs number before the VM starts executing.
     *
     * @param expectedFreePes the expected free PEs number to set
     */
    private Vm setExpectedFreePesNumber(final long expectedFreePes) {
        this.expectedFreePesNumber = Math.max(expectedFreePes, 0);
        return this;
    }

    @Override
    public double getCpuPercentUtilization() {
        return getCpuPercentUtilization(getSimulation().clock());
    }

    @Override
    public double getCpuPercentUtilization(final double time) {
        return cloudletScheduler.getAllocatedCpuPercent(time);
    }

    @Override
    public double getCpuPercentRequested() {
        return getCpuPercentRequested(getSimulation().clock());
    }

    @Override
    public double getCpuPercentRequested(final double time) {
        return cloudletScheduler.getRequestedCpuPercent(time);
    }

    @Override
    public double getHostCpuUtilization(final double time) {
        return host.getExpectedRelativeCpuUtilization(this, getCpuPercentUtilization(time));
    }

    @Override
    public double getExpectedHostCpuUtilization(final double vmCpuUtilizationPercent) {
        return host.getExpectedRelativeCpuUtilization(this, vmCpuUtilizationPercent);
    }

    @Override
    public double getHostRamUtilization() {
        return host.getRelativeRamUtilization(this);
    }

    @Override
    public double getHostBwUtilization() {
        return host.getRelativeBwUtilization(this);
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
    public MipsShare getCurrentRequestedMips() {
        //TODO This method is confusing, since there is a getRequestedMips() (created with lombok)
        if (isCreated()) {
            return host.getVmScheduler().getRequestedMips(this);
        }

        return new MipsShare(processor);
    }

    @Override
    public long getCurrentRequestedBw() {
        if (!isCreated()) {
            return bw.getCapacity();
        }

        return (long) (cloudletScheduler.getCurrentRequestedBwPercentUtilization() * bw.getCapacity());
    }

    @Override
    public double getTotalMipsCapacity() {
        return getMips() * getNumberOfPes();
    }

    @Override
    public long getCurrentRequestedRam() {
        if (isCreated()) {
            return (long) (cloudletScheduler.getCurrentRequestedRamPercentUtilization() * ram.getCapacity());
        }

        return ram.getCapacity();

    }

    @Override
    public Vm setStartTime(final double startTime) {
        this.startTime = MathUtil.nonNegative(startTime, "startTime");
        setLastBusyTime(startTime);
        return this;
    }

    @Override
    public Vm setStopTime(final double stopTime) {
        this.stopTime = Math.max(stopTime, -1);
        return this;
    }

    /**
     * Checks if the VM has ever started some Cloudlet.
     *
     * @return
     */
    public boolean hasStartedSomeCloudlet() {
        return lastBusyTime != Double.MAX_VALUE;
    }

    private void setLastBusyTime() {
        this.lastBusyTime = getSimulation().clock();
    }

    private void setLastBusyTime(final double time) {
        this.lastBusyTime = time;
    }

    @Override
    public double getTotalExecutionTime() {
        if (startTime < 0) {
            return 0;
        }

        return stopTime < 0 ? getSimulation().clock() - startTime : stopTime - startTime;
    }

    @Override
    public double getMips() {
        return processor.getMips();
    }

    /**
     * Sets the individual MIPS capacity of any VM's PE, considering that all
     * PEs have the same capacity.
     *
     * @param mips the new mips for every VM's PE
     */
    protected final void setMips(final double mips) {
        processor.setMips(mips);
    }

    @Override
    public long getNumberOfPes() {
        return processor.getCapacity();
    }

    private void setNumberOfPes(final long numberOfPes) {
        processor.setCapacity(numberOfPes);
    }

    /**
     * Sets a new {@link Ram} resource for the Vm.
     *
     * @param ram the Ram resource to set
     */
    private void setRam(@NonNull final Ram ram) {
        this.ram = ram;
    }

    @Override
    public final Vm setRam(final long ramCapacity) {
        if (this.isCreated()) {
            throw new UnsupportedOperationException("RAM capacity can just be changed when the Vm was not created inside a Host yet.");
        }

        setRam(new Ram(ramCapacity));
        return this;
    }

    /**
     * Sets a new {@link Bandwidth} resource for the Vm.
     *
     * @param bw the Bandwidth resource to set
     */
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

    /**
     * Sets a new {@link SimpleStorage} resource for the Vm.
     *
     * @param storage the RawStorage resource to set
     */
    private void setStorage(@NonNull final SimpleStorage storage) {
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

    @Override
    public Vm setHost(@NonNull final Host host) {
        if (Host.NULL.equals(host))  {
            setCreated(false);
        }

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

    /**
     * Notifies the listeners when the VM starts migration to a target Host.
     * @param targetHost the Host the VM is migrating to
     */
    public void updateMigrationStartListeners(final Host targetHost){
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onMigrationStartListeners.size(); i++) {
            final var listener = onMigrationStartListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this, targetHost));
        }
    }

    /**
     * Notifies the listeners when the VM finishes migration to a target Host.
     * @param targetHost the Host the VM has just migrated to
     */
    public void updateMigrationFinishListeners(final Host targetHost){
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onMigrationFinishListeners.size(); i++) {
            final var listener = onMigrationFinishListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this, targetHost));
        }
    }

    @Override
    public boolean isSuitableForCloudlet(final Cloudlet cloudlet) {
        return getNumberOfPes() >= cloudlet.getNumberOfPes() &&
            storage.getAvailableResource() >= cloudlet.getFileSize();
    }

    @Override
    public void setCreated(final boolean created) {
        if(!this.created && created){
            setCreationTime();
        }

        this.created = created;
        this.setFailed(false);
    }

    @Override
    public List<VmStateHistoryEntry> getStateHistory() {
        /*
         * @TODO Instead of using a list, this attribute would be a map, where the
         *       key can be the history time and the value the history itself. This
         *       way, if one wants to get the history for a given time, he/she doesn't
         *       have to iterate over the entire list to find the desired entry.
         */
        return Collections.unmodifiableList(stateHistory);
    }

    @Override
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
    public ResourceManageable getResource(Class<? extends ResourceManageable> resourceClass) {
        if(Pe.class.isAssignableFrom(resourceClass) || Processor.class.isAssignableFrom(resourceClass)) {
            return processor;
        }

        return Vm.super.getResource(resourceClass);
    }

    @Override
    public Vm addOnHostAllocationListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        this.onHostAllocationListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnMigrationStartListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        onMigrationStartListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnMigrationFinishListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        onMigrationFinishListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnHostDeallocationListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        this.onHostDeallocationListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnCreationFailureListener(@NonNull final EventListener<VmDatacenterEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        this.onCreationFailureListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnUpdateProcessingListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        if (listener.equals(EventListener.NULL)) {
            return this;
        }

        this.onUpdateProcessingListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnCreationFailureListener(@NonNull final EventListener<VmDatacenterEventInfo> listener) {
        return onCreationFailureListeners.remove(listener);
    }

    @Override
    public boolean removeOnUpdateProcessingListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onUpdateProcessingListeners.remove(listener);
    }

    @Override
    public boolean removeOnHostAllocationListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onHostAllocationListeners.remove(listener);
    }

    @Override
    public boolean removeOnHostDeallocationListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onHostDeallocationListeners.remove(listener);
    }

    @Override
    public String toString() {
        final String desc = StringUtils.isBlank(description) ? "" : " (%s)".formatted(description);
        final String type = this instanceof VmGroup ? "VmGroup" : "Vm";
        return "%s %d%s".formatted(type, getId(), desc);
    }

    /**
     * Compare this Vm with another one based on {@link #getTotalMipsCapacity()}.
     *
     * @param obj the Vm to compare to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(@NonNull final Vm obj) {
        if(this.equals(obj)) {
            return 0;
        }

        return Double.compare(getTotalMipsCapacity(), obj.getTotalMipsCapacity()) +
               Long.compare(this.getId(), obj.getId()) +
               this.getBroker().compareTo(obj.getBroker());
    }

    @Override
    public void setFailed(final boolean failed) {
        this.failed = failed;

        if(failed) {
            setCloudletsToFailed();
        }
    }

    public void setCloudletsToFailed() {
        getBroker().getCloudletWaitingList()
                   .stream()
                   .filter(cl -> this.equals(cl.getVm()))
                   .forEach(cl -> cl.setStatus(Cloudlet.Status.FAILED_RESOURCE_UNAVAILABLE));
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
    public boolean isDelayed() {
        return submissionDelay > 0;
    }

    @Override
    public void notifyOnHostAllocationListeners() {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onHostAllocationListeners.size(); i++) {
            final var listener = onHostAllocationListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this));
        }
    }

    @Override
    public void notifyOnHostDeallocationListeners(@NonNull final Host deallocatedHost) {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onHostDeallocationListeners.size(); i++) {
            final var listener = onHostDeallocationListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this, deallocatedHost));
        }
    }

    /**
     * Notifies all registered listeners when the processing of the Vm is updated in its {@link Host}.
     */
    public void notifyOnUpdateProcessingListeners() {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onUpdateProcessingListeners.size(); i++) {
            final var listener = onUpdateProcessingListeners.get(i);
            listener.update(VmHostEventInfo.of(listener, this));
        }
    }

    @Override
    public void notifyOnCreationFailureListeners(@NonNull final Datacenter failedDatacenter) {
        // TODO: Workaround - Uses indexed for to avoid ConcurrentModificationException
        for (int i = 0; i < onCreationFailureListeners.size(); i++) {
            final var listener = onCreationFailureListeners.get(i);
            listener.update(VmDatacenterEventInfo.of(listener, this, failedDatacenter));
        }
    }

    @Override
    public boolean removeOnMigrationStartListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onMigrationStartListeners.remove(listener);
    }

    @Override
    public boolean removeOnMigrationFinishListener(@NonNull final EventListener<VmHostEventInfo> listener) {
        return onMigrationFinishListeners.remove(listener);
    }

    @Override
    public final Vm setHorizontalScaling(final HorizontalVmScaling horizontalScaling) throws IllegalArgumentException {
        this.horizontalScaling = validateAndConfigureVmScaling(horizontalScaling);
        return this;
    }

    @Override
    public final Vm setRamVerticalScaling(final VerticalVmScaling ramVerticalScaling) throws IllegalArgumentException {
        this.ramVerticalScaling = validateAndConfigureVmScaling(ramVerticalScaling);
        return this;
    }

    @Override
    public final Vm setBwVerticalScaling(final VerticalVmScaling bwVerticalScaling) throws IllegalArgumentException {
        this.bwVerticalScaling = validateAndConfigureVmScaling(bwVerticalScaling);
        return this;
    }

    @Override
    public final Vm setPeVerticalScaling(final VerticalVmScaling peVerticalScaling) throws IllegalArgumentException {
        this.peVerticalScaling = validateAndConfigureVmScaling(peVerticalScaling);
        return this;
    }

    private <T extends VmScaling> T validateAndConfigureVmScaling(@NonNull final T vmScaling) {
        if (vmScaling.getVm() != null && vmScaling.getVm() != Vm.NULL && vmScaling.getVm() != this) {
            final String name = vmScaling.getClass().getSimpleName();
            throw new IllegalArgumentException(
                "The " + name + " given is already linked to a Vm. " +
                    "Each Vm must have its own " + name + " object or none at all. " +
                    "Another " + name + " has to be provided for this Vm.");
        }

        vmScaling.setVm(this);
        this.addOnUpdateProcessingListener(vmScaling::requestUpScalingIfPredicateMatches);
        return vmScaling;
    }

    @Override
    public void enableUtilizationStats(){
        if(cpuUtilizationStats == null || cpuUtilizationStats == VmResourceStats.NULL) {
            this.cpuUtilizationStats = new VmResourceStats(this, vm -> vm.getCpuPercentUtilization(getSimulation().clock()));
        }
    }

    /**
     * Gets the Default RAM capacity (in MB) for creating VMs.
     * This value is used when the RAM capacity is not given in a VM constructor.
     */
    public static long getDefaultRamCapacity() {
        return defaultRamCapacity;
    }

    /**
     * Sets the Default RAM capacity (in MB) for creating VMs.
     * This value is used when the RAM capacity is not given in a VM constructor.
     */
    public static void setDefaultRamCapacity(final long defaultCapacity) {
        AbstractMachine.validateCapacity(defaultCapacity);
        defaultRamCapacity = defaultCapacity;
    }

    /**
     * Gets the Default Bandwidth capacity (in Mbps) for creating VMs.
     * This value is used when the BW capacity is not given in a VM constructor.
     */
    public static long getDefaultBwCapacity() {
        return defaultBwCapacity;
    }

    /**
     * Sets the Default Bandwidth capacity (in Mbps) for creating VMs.
     * This value is used when the BW capacity is not given in a VM constructor.
     */
    public static void setDefaultBwCapacity(final long defaultCapacity) {
        AbstractMachine.validateCapacity(defaultCapacity);
        defaultBwCapacity = defaultCapacity;
    }

    /**
     * Gets the Default Storage capacity (in MB) for creating VMs.
     * This value is used when the Storage capacity is not given in a VM constructor.
     */
    public static long getDefaultStorageCapacity() {
        return defaultStorageCapacity;
    }

    /**
     * Sets the Default Storage capacity (in MB) for creating VMs.
     * This value is used when the Storage capacity is not given in a VM constructor.
     */
    public static void setDefaultStorageCapacity(final long defaultCapacity) {
        AbstractMachine.validateCapacity(defaultCapacity);
        defaultStorageCapacity = defaultCapacity;
    }

    @Override
    public Vm setTimeZone(final double timeZone) {
        this.timeZone = validateTimeZone(timeZone);
        return this;
    }
}
