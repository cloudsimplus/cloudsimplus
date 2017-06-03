/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms;

import java.util.*;
import java.util.stream.LongStream;

import org.cloudbus.cloudsim.core.UniquelyIdentificable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.autoscaling.VmScaling;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;

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
public class VmSimple implements Vm {
    private HorizontalVmScaling horizontalScaling;
    private boolean failed;

    /**
     * @see #getId()
     */
    private int id;

    private DatacenterBroker broker;

    private Processor processor;

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
     * @see #getStateHistory()
     */
    private final List<VmStateHistoryEntry> stateHistory;

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

    private Set<EventListener<VmHostEventInfo>> onHostAllocationListeners;
    private Set<EventListener<VmHostEventInfo>> onHostDeallocationListeners;
    private Set<EventListener<VmHostEventInfo>> onUpdateProcessingListeners;
    private Set<EventListener<VmDatacenterEventInfo>> onCreationFailureListeners;

    private VerticalVmScaling ramVerticalScaling;
    private VerticalVmScaling bwVerticalScaling;
    private VerticalVmScaling peVerticalScaling;

    private String description;
    private double startTime;
    private double stopTime;
    private double lastBuzyTime;

    /**
     * Creates a Vm with 1024 MEGABYTE of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGABYTE of Storage Size.
     *
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is being instantiated}, such values can be changed freely.
     *
     * @param id unique ID of the VM
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     *
     * @pre id >= 0
     * @pre numberOfPes > 0
     * @post $none
     */
    public VmSimple(int id, long mipsCapacity, long numberOfPes) {
        this.resources = new ArrayList<>(4);
        setInMigration(false);
        setHost(Host.NULL);
        setCloudletScheduler(CloudletScheduler.NULL);
        this.processor = new Processor(this, mipsCapacity, numberOfPes);
        this.description = "";
        this.startTime = -1;
        this.stopTime = -1;
        this.lastBuzyTime = 0;

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
    }

    /**
     * Creates a Vm with 1024 MEGABYTE of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGABYTE of Storage Size.
     * It is not defined an id for the Vm. The id is defined when the Vm is submitted to
     * a {@link DatacenterBroker}.
     *
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is being instantiated}, such values can be changed freely.
     *
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     *
     * @pre numberOfPes > 0
     * @post $none
     */
    public VmSimple(long mipsCapacity, long numberOfPes) {
        this(-1, mipsCapacity, numberOfPes);
    }


    /**
     * Creates a Vm with 1024 MEGABYTE of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGABYTE of Storage Size.
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
     *
     * @pre id >= 0
     * @pre numberOfPes > 0
     * @post $none
     */
    public VmSimple(int id, double mipsCapacity, long numberOfPes) {
        this(id, (long)mipsCapacity, numberOfPes);
    }

    /**
     * Creates a Vm with the given parameters.
     *
     * @param id unique ID of the VM
     * @param broker ID of the VM's owner, that is represented by the id of the {@link DatacenterBroker}
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     * @param ramCapacity amount of ram in Megabytes
     * @param bwCapacity amount of bandwidth to be allocated to the VM (in Megabits/s)
     * @param size size the VM image in Megabytes (the amount of storage it will use, at least initially).
     * @param vmm Virtual Machine Monitor that manages the VM lifecycle
     * @param cloudletScheduler scheduler that defines the execution policy for Cloudlets inside this Vm
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     *
     * @pre id >= 0
     * @pre broker >= 0
     * @pre storageCapacity > 0
     * @pre ramCapacity > 0
     * @pre bwCapacity > 0
     * @pre numberOfPes > 0
     * @pre cloudletScheduler != null
     * @post $none
     */
    @Deprecated
    public VmSimple(
            int id,
            DatacenterBroker broker,
            long mipsCapacity,
            int numberOfPes,
            long ramCapacity,
            long bwCapacity,
            long size,
            String vmm,
            CloudletScheduler cloudletScheduler)
    {
        this(id, mipsCapacity, numberOfPes);
        setBroker(broker);
        setRam(ramCapacity);
        setBw(bwCapacity);
        setSize(size);
        setVmm(vmm);
        setCloudletScheduler(cloudletScheduler);
    }

    @Override
    public double updateProcessing(double currentTime, List<Double> mipsShare) {
        if (Objects.isNull(mipsShare)) {
            return Double.MAX_VALUE;
        }

        if(!cloudletScheduler.getCloudletExecList().isEmpty()){
            this.lastBuzyTime = getSimulation().clock();
        }
        final double nextSimulationTime = cloudletScheduler.updateProcessing(currentTime, mipsShare);
        notifyOnUpdateProcessingListeners();
        return nextSimulationTime;
    }

    @Override
    public double getCpuPercentUsage() {
        return getCpuPercentUsage(getSimulation().clock());
    }

    @Override
    public double getCpuPercentUsage(double time) {
        return cloudletScheduler.getRequestedCpuPercentUtilization(time);
    }

    @Override
    public double getTotalCpuMipsUsage(double time) {
        return getCpuPercentUsage(time) * getTotalMipsCapacity();
    }

    @Override
    public double getCurrentRequestedMaxMips() {
        return getCurrentRequestedMips().stream().mapToDouble(m->m).max().orElse(0.0);
    }

    @Override
    public double getCurrentRequestedTotalMips() {
        return getCurrentRequestedMips().stream().mapToDouble(m->m).sum();
    }

    @Override
    public List<Double> getCurrentRequestedMips() {
        if (isCreated()) {
            return host.getVmScheduler().getRequestedMips(this);
        }

        return LongStream.range(0, getNumberOfPes())
                .mapToObj(i->getMips())
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
    public String getUid() {
        return UniquelyIdentificable.getUid(broker.getId(), id);
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * Sets the VM id.
     *
     * @param id the new VM id, that has to be unique for the current {@link #getBroker() broker}
     * @todo The uniqueness of VM id for a given user is not being ensured
     */
    @Override
    public final void setId(int id) {
        this.id = id;
    }

    @Override
    public final Vm setBroker(DatacenterBroker broker) {
        this.broker = Objects.isNull(broker) ? DatacenterBroker.NULL : broker;
        return this;
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
    public double getLastBuzyTime() {
        return this.lastBuzyTime;
    }

    @Override
    public double getIdleInterval() {
        return getSimulation().clock() - lastBuzyTime;
    }

    @Override
    public double getTotalExecutionTime() {
        if(startTime < 0) {
            return 0;
        }

        return stopTime < 0 ? getSimulation().clock() - startTime : stopTime - startTime;
    }

    @Override
    public DatacenterBroker getBroker() {
        return broker;
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
    protected final void setMips(double mips) {
        processor.setMips(mips);
    }

    @Override
    public long getNumberOfPes() {
        return processor.getCapacity();
    }

    private void setNumberOfPes(long numberOfPes) {
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
    private void setRam(Ram ram) {
        Objects.requireNonNull(ram);
        this.ram = ram;
    }

    @Override
    public final Vm setRam(long ramCapacity) {
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
    private void setBw(Bandwidth bw){
        Objects.requireNonNull(bw);
        this.bw = bw;
    }

    @Override
    public final Vm setBw(long bwCapacity) {
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
    private void setStorage(Storage storage){
        Objects.requireNonNull(storage);
        this.storage = storage;
    }

    @Override
    public final Vm setSize(long size) {
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
    protected final void setVmm(String vmm) {
        this.vmm = vmm;
    }

    @Override
    public final void setHost(Host host) {
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
    public final Vm setCloudletScheduler(CloudletScheduler cloudletScheduler) {
        if(isCreated()){
            throw new UnsupportedOperationException("CloudletScheduler can just be changed when the Vm was not created inside a Host yet.");
        }

        this.cloudletScheduler = Objects.isNull(cloudletScheduler) ? CloudletScheduler.NULL : cloudletScheduler;
        this.cloudletScheduler.setVm(this);
        return this;
    }

    @Override
    public boolean isInMigration() {
        return inMigration;
    }

    @Override
    public final void setInMigration(boolean inMigration) {
        this.inMigration = inMigration;
    }

    /**
     * Gets the current allocated storage size.
     *
     * @return the current allocated size
     * @see Vm#getStorage()
     */
    @Override
    public long getCurrentAllocatedSize() {
        return storage.getAllocatedResource();
    }

    @Override
    public long getCurrentAllocatedRam() {
        return ram.getAllocatedResource();
    }

    @Override
    public long getCurrentAllocatedBw() {
        return bw.getAllocatedResource();
    }

    @Override
    public final boolean isCreated() {
        return created;
    }

    @Override
    public final void setCreated(boolean created) {
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
    public void addStateHistoryEntry(VmStateHistoryEntry entry) {
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
    public void allocateResource(Class<? extends ResourceManageable> resourceClass, long newTotalResourceAmount) {
        getResource(resourceClass).allocateResource(newTotalResourceAmount);
    }

    @Override
    public void deallocateResource(Class<? extends ResourceManageable> resourceClass) {
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
    public Vm addOnHostAllocationListener(EventListener<VmHostEventInfo> listener) {
        Objects.requireNonNull(listener);
        this.onHostAllocationListeners.add(listener);
        return this;
    }

    @Override
    public Vm addOnHostDeallocationListener(EventListener<VmHostEventInfo> listener) {
        Objects.requireNonNull(listener);
        this.onHostDeallocationListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnHostAllocationListener(EventListener<VmHostEventInfo> listener) {
        return onHostAllocationListeners.remove(listener);
    }

    @Override
    public boolean removeOnHostDeallocationListener(EventListener<VmHostEventInfo> listener) {
        return onHostDeallocationListeners.remove(listener);
    }

    @Override
    public String toString() {
        final String desc = description.trim().isEmpty() ? "" : String.format(" (%s)", description);
        return String.format("Vm %d%s", getId(), desc);
    }

    @Override
    public Vm addOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener) {
        Objects.requireNonNull(listener);
        this.onCreationFailureListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener) {
        return onCreationFailureListeners.remove(listener);
    }

    @Override
    public Vm addOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener) {
        Objects.requireNonNull(listener);
        this.onUpdateProcessingListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener) {
        return onUpdateProcessingListeners.remove(listener);
    }

    /**
     * Compare this Vm with another one based on {@link #getTotalMipsCapacity()}.
     *
     * @param o the Vm to compare to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(Vm o) {
        return Double.compare(getTotalMipsCapacity(), o.getTotalMipsCapacity());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final VmSimple vmSimple = (VmSimple) o;

        if (id != vmSimple.id) return false;
        return broker.equals(vmSimple.broker);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + broker.hashCode();
        return result;
    }

    @Override
    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public Simulation getSimulation() {
        return broker.getSimulation();
    }

    @Override
    public double getSubmissionDelay() {
        return this.submissionDelay;
    }

    @Override
    public final void setSubmissionDelay(double submissionDelay) {
        if(submissionDelay < 0) {
            return;
        }
        this.submissionDelay = submissionDelay;
    }

    @Override
    public void notifyOnHostAllocationListeners() {
        final VmHostEventInfo info = VmHostEventInfo.of(this);
        onHostAllocationListeners.forEach(l -> l.update(info));
    }

    @Override
    public void notifyOnHostDeallocationListeners(Host deallocatedHost) {
        if(Objects.isNull(deallocatedHost)){
            return;
        }

        final VmHostEventInfo info = VmHostEventInfo.of(this, deallocatedHost);
        onHostDeallocationListeners.forEach(l -> l.update(info));
    }

    /**
         * Notifies all registered listeners when the processing of the Vm is updated in its {@link Host}.
         */
    public void notifyOnUpdateProcessingListeners() {
        final VmHostEventInfo info = VmHostEventInfo.of(this);
        onUpdateProcessingListeners.forEach(l -> l.update(info));
    }

    @Override
    public void notifyOnCreationFailureListeners(Datacenter failedDatacenter) {
        if(Objects.isNull(failedDatacenter)){
            return;
        }

        final VmDatacenterEventInfo info = VmDatacenterEventInfo.of(this, failedDatacenter);
        onCreationFailureListeners.forEach(l -> l.update(info));
    }


    @Override
    public HorizontalVmScaling getHorizontalScaling() {
        return horizontalScaling;
    }

    @Override
    public final Vm setHorizontalScaling(HorizontalVmScaling horizontalScaling) throws IllegalArgumentException {
        this.horizontalScaling = validateAndConfigureVmScaling(horizontalScaling);
        return this;
    }

    @Override
    public final Vm setRamVerticalScaling(VerticalVmScaling ramVerticalScaling) throws IllegalArgumentException {
        this.ramVerticalScaling = validateAndConfigureVmScaling(ramVerticalScaling);
        return this;
    }

    @Override
    public final Vm setBwVerticalScaling(VerticalVmScaling bwVerticalScaling) throws IllegalArgumentException {
        this.bwVerticalScaling = validateAndConfigureVmScaling(bwVerticalScaling);
        return this;
    }

    @Override
    public final Vm setPeVerticalScaling(VerticalVmScaling peVerticalScaling) throws IllegalArgumentException {
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

    private <T extends VmScaling> T validateAndConfigureVmScaling(T vmScaling) {
        Objects.requireNonNull(vmScaling);
        if(vmScaling.getVm() != null && vmScaling.getVm() != Vm.NULL && vmScaling.getVm() != this){
            final String name = vmScaling.getClass().getSimpleName();
            throw new IllegalArgumentException(
                "The "+name+" given is already linked to a Vm. " +
                "Each Vm must have its own "+name+" objects or none at all. " +
                "A new scaling has to be provided for this Vm.");
        }

        vmScaling.setVm(this);
        this.addOnUpdateProcessingListener(evt -> vmScaling.requestScalingIfPredicateMatch(evt.getTime()));
        return vmScaling;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Vm setDescription(String description) {
        this.description = Objects.isNull(description) ? "" : description;
        return this;
    }
}
