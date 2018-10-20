/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms;

import org.apache.commons.lang3.StringUtils;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CustomerEntityAbstract;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.autoscaling.VmScaling;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Implements the basic features of a Virtual Machine (VM) that runs inside a
 * {@link Host} that may be shared among other VMs. It processes
 * {@link Cloudlet cloudlets}. This processing happens according to a policy,
 * defined by the {@link CloudletScheduler}. Each VM has a owner (user), which
 * can submit cloudlets to the VM to execute them.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmSimple extends CustomerEntityAbstract implements Vm {
    /** @see #getUtilizationHistory() */
    private final UtilizationHistory utilizationHistory;

    /** @see #getStateHistory() */
    private final List<VmStateHistoryEntry> stateHistory;

    private HorizontalVmScaling horizontalScaling;
    private boolean failed;

    private final Processor processor;

    /**
     * @see #getVmm()
     */
    private String vmm;

    /**
     * @see #getCloudletScheduler()
     */
    private CloudletScheduler cloudletScheduler;

    /**
     * @see #getHost()
     */
    private Host host;

    /**
     * @see #isInMigration()
     */
    private boolean inMigration;

    /**
     * @see #isCreated()
     */
    private boolean created;

    private List<ResourceManageable> resources;


    /**
     * The VM's storage resource that represents the Vm size in disk.
     * This object contains information about capacity and allocation.
     */
    private Storage storage;

    /**
     * The VM's RAM resource, containing information about capacity and
     * allocation.
     */
    private Ram ram;

    /**
     * The VM's Bandwidth (BW) resource, containing information about capacity
     * and allocation (in Megabits/s).
     */
    private Bandwidth bw;

    /**
     * @see #getSubmissionDelay()
     */
    private double submissionDelay;

    private final Set<EventListener<VmHostEventInfo>> onHostAllocationListeners;
    private final Set<EventListener<VmHostEventInfo>> onHostDeallocationListeners;
    private final Set<EventListener<VmHostEventInfo>> onUpdateProcessingListeners;
    private final Set<EventListener<VmDatacenterEventInfo>> onCreationFailureListeners;

    private VerticalVmScaling ramVerticalScaling;
    private VerticalVmScaling bwVerticalScaling;
    private VerticalVmScaling peVerticalScaling;

    private String description;
    private double startTime;
    private double stopTime;
    private double lastBusyTime;

    /**
     * Creates a Vm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
     *
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is being instantiated}, such values can be changed freely.
     *
     * @param id unique ID of the VM
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     */
    public VmSimple(final int id, final long mipsCapacity, final long numberOfPes) {
        this.resources = new ArrayList<>(4);
        setInMigration(false);
        setHost(Host.NULL);
        setCloudletScheduler(CloudletScheduler.NULL);
        this.processor = new Processor(this, mipsCapacity, numberOfPes);
        this.description = "";
        this.startTime = -1;
        this.stopTime = -1;
        this.lastBusyTime = 0;

        setId(id);
        setBroker(DatacenterBroker.NULL);
        setMips(mipsCapacity);
        setNumberOfPes(numberOfPes);

        setRam(new Ram(1024));
        setBw(new Bandwidth(1000));
        setStorage(new Storage(1024));

        setSubmissionDelay(0);
        setVmm("Xen");
        stateHistory = new LinkedList<>();

        this.onHostAllocationListeners = new HashSet<>();
        this.onHostDeallocationListeners = new HashSet<>();
        this.onCreationFailureListeners = new HashSet<>();
        this.onUpdateProcessingListeners = new HashSet<>();
        this.setHorizontalScaling(HorizontalVmScaling.NULL);
        this.setRamVerticalScaling(VerticalVmScaling.NULL);
        this.setBwVerticalScaling(VerticalVmScaling.NULL);
        this.setPeVerticalScaling(VerticalVmScaling.NULL);

        //By default, the VM doesn't store utilization history. This has to be enabled by the user as wanted
        utilizationHistory = new VmUtilizationHistory(this, false);
    }

    /**
     * Creates a Vm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is being instantiated}, such values can be changed freely.
     *
     * <p>It is not defined an id for the Vm. The id is defined when the Vm is submitted to
     * a {@link DatacenterBroker}.</p>
     *
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     */
    public VmSimple(final long mipsCapacity, final long numberOfPes) {
        this(-1, mipsCapacity, numberOfPes);
    }

    /**
     * Creates a Vm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
     *
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is being instantiated}, such values can be changed freely.
     *
     * <p>It receives the amount of MIPS as a double value but converts it internally
     * to a long. The method is just provided as a handy-way to create a Vm
     * using a double value for MIPS that usually is generated from some computations.</p>
     *
     * @param id unique ID of the VM
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     */
    public VmSimple(final int id, final double mipsCapacity, final long numberOfPes) {
        this(id, (long)mipsCapacity, numberOfPes);
    }

    @Override
    public double updateProcessing(final double currentTime, final List<Double> mipsShare) {
        requireNonNull(mipsShare);

        if(!cloudletScheduler.getCloudletExecList().isEmpty()){
            this.lastBusyTime = getSimulation().clock();
        }
        final double nextEventDelay = cloudletScheduler.updateProcessing(currentTime, mipsShare);
        notifyOnUpdateProcessingListeners();

        /* If the current time is some value with the decimals greater than x.0
         * (such as 45.1) and the next event delay is any integer number such as 5,
         * then the next simulation time will be 50.1.
         * At time 50.1 the utilization will be reduced due to the completion of the Cloudlet.
         * At time 50.0 the Cloudlet is still running, so there is some CPU utilization.
         * But since the next update will be only at time 50.1, the utilization
         * at time 50.0 won't be collected to enable knowing the exact time
         * before the utilization dropped.
         */
        final double decimals = currentTime - (int) currentTime;
        utilizationHistory.addUtilizationHistory(currentTime);
        return nextEventDelay - decimals;
    }

    @Override
    public double getCpuPercentUsage() {
        return getCpuPercentUsage(getSimulation().clock());
    }

    @Override
    public double getCpuPercentUsage(final double time) {
        return cloudletScheduler.getRequestedCpuPercentUtilization(time);
    }

    @Override
    public double getTotalCpuMipsUsage() {
        return getTotalCpuMipsUsage(getSimulation().clock());
    }

    @Override
    public double getTotalCpuMipsUsage(final double time) {
        return getCpuPercentUsage(time) * getTotalMipsCapacity();
    }

    @Override
    public double getCurrentRequestedMaxMips() {
        return getCurrentRequestedMipsStream().max().orElse(0.0);
    }

    @Override
    public double getCurrentRequestedTotalMips() {
        return getCurrentRequestedMipsStream().sum();
    }

    private DoubleStream getCurrentRequestedMipsStream() {
        return getCurrentRequestedMips().stream().mapToDouble(mips -> mips);
    }

    @Override
    public List<Double> getCurrentRequestedMips() {
        if (isCreated()) {
            return host.getVmScheduler().getRequestedMips(this);
        }

        return LongStream.range(0, getNumberOfPes())
                .mapToObj(i -> getMips())
                .collect(toList());
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
        return getMips()*getNumberOfPes();
    }

    @Override
    public long getCurrentRequestedRam() {
        if (!isCreated()) {
            return ram.getCapacity();
        }

        return (long) (cloudletScheduler.getCurrentRequestedRamPercentUtilization() * ram.getCapacity());
    }

    @Override
    public double getStartTime() {
        return this.startTime;
    }

    @Override
    public Vm setStartTime(final double startTime) {
        this.startTime = Math.max(startTime, -1);
        return this;
    }

    @Override
    public double getStopTime() {
        return this.stopTime;
    }

    @Override
    public Vm setStopTime(final double stopTime) {
        this.stopTime = Math.max(stopTime, -1);
        return this;
    }

    @Override
    public double getLastBusyTime() {
        return this.lastBusyTime;
    }

    @Override
    public double getIdleInterval() {
        return getSimulation().clock() - lastBusyTime;
    }

    @Override
    public boolean isIdle() {
        /*If the idle interval is not zero
        * but the cloudletScheduler doesn't have any running or waiting Cloudlet,
        * the VM has just become idle.
        * That is way it's idle interval is zero. */
        return getIdleInterval() > 0 || cloudletScheduler.isEmpty();
    }

    @Override
    public boolean isIdleEnough(final double time) {
        if(time <= 0 && !isIdle()) {
            return false;
        }

        return getIdleInterval() >= time;
    }

    @Override
    public double getTotalExecutionTime() {
        if(startTime < 0) {
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

    @Override
    public Processor getProcessor() {
        return processor;
    }

    @Override
    public Resource getRam() {
        return ram;
    }

    /**
     * Sets a new {@link Ram} resource for the Vm.
     * @param ram the Ram resource to set
     */
    private void setRam(final Ram ram) {
        this.ram = requireNonNull(ram);
    }

    @Override
    public final Vm setRam(final long ramCapacity) {
        if(this.isCreated()){
            throw new UnsupportedOperationException("RAM capacity can just be changed when the Vm was not created inside a Host yet.");
        }

        setRam(new Ram(ramCapacity));
        return this;
    }

    @Override
    public Resource getBw() {
        return bw;
    }

    /**
     * Sets a new {@link Bandwidth} resource for the Vm.
     * @param bw the Bandwidth resource to set
     */
    private void setBw(final Bandwidth bw){
        this.bw = requireNonNull(bw);
    }

    @Override
    public final Vm setBw(final long bwCapacity) {
        if(this.isCreated()){
            throw new UnsupportedOperationException("Bandwidth capacity can just be changed when the Vm was not created inside a Host yet.");
        }
        setBw(new Bandwidth(bwCapacity));
        return this;
    }

    @Override
    public Resource getStorage() {
        return storage;
    }

    /**
     * Sets a new {@link Storage} resource for the Vm.
     * @param storage the RawStorage resource to set
     */
    private void setStorage(final Storage storage){
        this.storage = requireNonNull(storage);
    }

    @Override
    public final Vm setSize(final long size) {
        if(this.isCreated()){
            throw new UnsupportedOperationException("Storage size can just be changed when the Vm was not created inside a Host yet.");
        }
        setStorage(new Storage(size));
        return this;
    }

    @Override
    public String getVmm() {
        return vmm;
    }

    /**
     * Sets the Virtual Machine Monitor (VMM) that manages the VM.
     *
     * @param vmm the new VMM
     */
    protected final void setVmm(final String vmm) {
        this.vmm = vmm;
    }

    @Override
    public final void setHost(final Host host) {
        if(host == Host.NULL){
            setCreated(false);
        }
        this.host = host;
    }

    @Override
    public Host getHost() {
        return host;
    }

    @Override
    public CloudletScheduler getCloudletScheduler() {
        return cloudletScheduler;
    }

    @Override
    public final Vm setCloudletScheduler(final CloudletScheduler cloudletScheduler) {
        requireNonNull(cloudletScheduler);
        if(isCreated()){
            throw new UnsupportedOperationException("CloudletScheduler can just be changed when the Vm was not created inside a Host yet.");
        }

        this.cloudletScheduler = cloudletScheduler;
        this.cloudletScheduler.setVm(this);
        return this;
    }

    @Override
    public boolean isInMigration() {
        return inMigration;
    }

    @Override
    public final void setInMigration(final boolean migrating) {
        this.inMigration = migrating;
    }

    @Override
    public final boolean isCreated() {
        return created;
    }

    @Override
    public boolean isSuitableForCloudlet(final Cloudlet cloudlet) {
        return getNumberOfPes() >= cloudlet.getNumberOfPes() &&
               storage.getAvailableResource() >= cloudlet.getFileSize();
    }

    @Override
    public final void setCreated(final boolean created) {
        this.created = created;
    }

    @Override
    public List<VmStateHistoryEntry> getStateHistory() {
        /*
         * @todo Instead of using a list, this attribute would be a map, where the
         * key can be the history time and the value the history itself. This
         * way, if one wants to get the history for a given time, he/she doesn't
         * have to iterate over the entire list to find the desired entry.
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
    public void allocateResource(final Class<? extends ResourceManageable> resourceClass, final long newTotalResourceAmount) {
        getResource(resourceClass).allocateResource(newTotalResourceAmount);
    }

    @Override
    public void deallocateResource(final Class<? extends ResourceManageable> resourceClass) {
        getResource(resourceClass).deallocateAllResources();
    }

    @Override
    public List<ResourceManageable> getResources() {
        if(getSimulation().isRunning() && resources.isEmpty()){
            resources = Arrays.asList(ram, bw, storage, processor);
        }

        return Collections.unmodifiableList(resources);
    }

    @Override
    public Vm addOnHostAllocationListener(final EventListener<VmHostEventInfo> listener) {
        this.onHostAllocationListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public Vm addOnHostDeallocationListener(final EventListener<VmHostEventInfo> listener) {
        this.onHostDeallocationListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnHostAllocationListener(final EventListener<VmHostEventInfo> listener) {
        return onHostAllocationListeners.remove(requireNonNull(listener));
    }

    @Override
    public boolean removeOnHostDeallocationListener(final EventListener<VmHostEventInfo> listener) {
        return onHostDeallocationListeners.remove(requireNonNull(listener));
    }

    @Override
    public String toString() {
        final String desc = StringUtils.isBlank(description) ? "" : String.format(" (%s)", description);
        final String brokerName = getBroker() == DatacenterBroker.NULL ? "" : "/Broker " + getBroker().getId();
        return String.format("Vm %d%s%s", getId(), brokerName, desc);
    }

    @Override
    public Vm addOnCreationFailureListener(final EventListener<VmDatacenterEventInfo> listener) {
        this.onCreationFailureListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnCreationFailureListener(final EventListener<VmDatacenterEventInfo> listener) {
        return onCreationFailureListeners.remove(requireNonNull(listener));
    }

    @Override
    public Vm addOnUpdateProcessingListener(final EventListener<VmHostEventInfo> listener) {
        this.onUpdateProcessingListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnUpdateProcessingListener(final EventListener<VmHostEventInfo> listener) {
        return onUpdateProcessingListeners.remove(requireNonNull(listener));
    }

    /**
     * Compare this Vm with another one based on {@link #getTotalMipsCapacity()}.
     *
     * @param o the Vm to compare to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(final Vm o) {
        return Double.compare(getTotalMipsCapacity(), o.getTotalMipsCapacity());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final VmSimple vmSimple = (VmSimple) o;

        if (getId() != vmSimple.getId()) return false;
        return getBroker().equals(vmSimple.getBroker());
    }

    @Override
    public void setFailed(final boolean failed) {
        this.failed = failed;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public boolean isWorking() {
        return !isFailed();
    }

    @Override
    public double getSubmissionDelay() {
        return this.submissionDelay;
    }

    @Override
    public final void setSubmissionDelay(final double submissionDelay) {
        if(submissionDelay < 0) {
            return;
        }
        this.submissionDelay = submissionDelay;
    }

    @Override
    public void notifyOnHostAllocationListeners() {
        onHostAllocationListeners.forEach(l -> l.update(VmHostEventInfo.of(l, this)));
    }

    @Override
    public void notifyOnHostDeallocationListeners(final Host deallocatedHost) {
        requireNonNull(deallocatedHost);
        onHostDeallocationListeners.forEach(l -> l.update(VmHostEventInfo.of(l,this, deallocatedHost)));
    }

    /**
         * Notifies all registered listeners when the processing of the Vm is updated in its {@link Host}.
         */
    public void notifyOnUpdateProcessingListeners() {
        onUpdateProcessingListeners.forEach(l -> l.update(VmHostEventInfo.of(l,this)));
    }

    @Override
    public void notifyOnCreationFailureListeners(final Datacenter failedDatacenter) {
        requireNonNull(failedDatacenter);
        onCreationFailureListeners.forEach(l -> l.update(VmDatacenterEventInfo.of(l,this, failedDatacenter)));
    }


    @Override
    public HorizontalVmScaling getHorizontalScaling() {
        return horizontalScaling;
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

    @Override
    public VerticalVmScaling getRamVerticalScaling() {
        return ramVerticalScaling;
    }

    @Override
    public VerticalVmScaling getBwVerticalScaling() {
        return bwVerticalScaling;
    }

    @Override
    public VerticalVmScaling getPeVerticalScaling() {
        return peVerticalScaling;
    }

    private <T extends VmScaling> T validateAndConfigureVmScaling(final T vmScaling) {
        requireNonNull(vmScaling);
        if(vmScaling.getVm() != null && vmScaling.getVm() != Vm.NULL && vmScaling.getVm() != this){
            final String name = vmScaling.getClass().getSimpleName();
            throw new IllegalArgumentException(
                "The "+name+" given is already linked to a Vm. " +
                "Each Vm must have its own "+name+" object or none at all. " +
                "Another "+name+" has to be provided for this Vm.");
        }

        vmScaling.setVm(this);
        this.addOnUpdateProcessingListener(vmScaling::requestUpScalingIfPredicateMatches);
        return vmScaling;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Vm setDescription(final String description) {
        this.description = description == null ? "" : description;
        return this;
    }

    @Override
    public UtilizationHistory getUtilizationHistory() {
        return utilizationHistory;
    }
}
