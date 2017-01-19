/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms;

import java.util.*;

import org.cloudbus.cloudsim.core.UniquelyIdentificable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.util.Log;
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
 *
 * @TODO @author manoelcampos Instead of having the methods getRam, getUsedRam,
 * getAvailableRam (and the same for other resources), it would be returned the
 * ResourceManageable object for each different kind of resource, in order to
 * allow the user to get the resource capacity, used and available capacity
 * without defining a specific method for each one.
 */
public class VmSimple implements Vm {
    private HorizontalVmScaling horizontalScaling;
    private boolean failed;

    /**
     * @see #getId()
     */
    private int id;

    private DatacenterBroker broker;

    /**
     * @see #getMips()
     */
    private double mips;

    /**
     * @see #getNumberOfPes()
     */
    private int numberOfPes;

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
    private RawStorage storage;

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

    private List<EventListener<VmHostEventInfo>> onHostAllocationListeners;
    private List<EventListener<VmHostEventInfo>> onHostDeallocationListeners;
    private List<EventListener<VmHostEventInfo>> onUpdateVmProcessingListeners;
    private List<EventListener<VmDatacenterEventInfo>> onVmCreationFailureListeners;
    private VerticalVmScaling ramVerticalScaling;
    private VerticalVmScaling bwVerticalScaling;

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
    public VmSimple(int id, long mipsCapacity, int numberOfPes) {
        this.resources = new ArrayList<>(4);
        setInMigration(false);
        setHost(Host.NULL);

        setId(id);
        setBroker(DatacenterBroker.NULL);
        setMips(mipsCapacity);
        setNumberOfPes(numberOfPes);

        setRam(new Ram(1024));
        setBw(new Bandwidth(1000));
        setStorage(new RawStorage(1024));

        setSubmissionDelay(0);
        setVmm("Xen");
        setCloudletScheduler(CloudletScheduler.NULL);
        stateHistory = new LinkedList<>();

        this.onHostAllocationListeners = new ArrayList<>();
        this.onHostDeallocationListeners = new ArrayList<>();
        this.onVmCreationFailureListeners = new ArrayList<>();
        this.onUpdateVmProcessingListeners = new ArrayList<>();
        this.setHorizontalScaling(HorizontalVmScaling.NULL);
        this.setRamVerticalScaling(VerticalVmScaling.NULL);
        this.setBwVerticalScaling(VerticalVmScaling.NULL);
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
    public VmSimple(int id, double mipsCapacity, int numberOfPes) {
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
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        if (Objects.isNull(mipsShare)) {
            return Double.MAX_VALUE;
        }

        final double nextSimulationTime = getCloudletScheduler().updateVmProcessing(currentTime, mipsShare);
        notifyOnUpdateVmProcessing();
        return nextSimulationTime;
    }

    @Override
    public double getCurrentCpuPercentUse() {
        return getCpuPercentUse(getSimulation().clock());
    }

    @Override
    public double getCpuPercentUse(double time) {
        return getCloudletScheduler().getRequestedCpuPercentUtilization(time);
    }

    @Override
    public double getTotalUtilizationOfCpuMips(double time) {
        return getCpuPercentUse(time) * getTotalMipsCapacity();
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
        List<Double> currentRequestedMips = getCloudletScheduler().getCurrentRequestedMips();
        if (!isCreated()) {
            currentRequestedMips = new ArrayList<>(getNumberOfPes());
            for (int i = 0; i < getNumberOfPes(); i++) {
                currentRequestedMips.add(getMips());
            }
        }

        return currentRequestedMips;
    }

    @Override
    public long getCurrentRequestedBw() {
        if (!isCreated()) {
            return getBw().getCapacity();
        }

        return (long) (getCloudletScheduler().getCurrentRequestedBwPercentUtilization() * getBw().getCapacity());
    }

    @Override
    public long getCurrentRequestedRam() {
        if (!isCreated()) {
            return getRam().getCapacity();
        }

        return (long) (getCloudletScheduler().getCurrentRequestedRamPercentUtilization() * getRam().getCapacity());
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
    protected final void setId(int id) {
        this.id = id;
    }

    @Override
    public final Vm setBroker(DatacenterBroker broker) {
        if(Objects.isNull(broker)) {
            broker = DatacenterBroker.NULL;
        }
        this.broker = broker;
        return this;
    }

    @Override
    public DatacenterBroker getBroker() {
        return broker;
    }

    @Override
    public double getMips() {
        return mips;
    }

    /**
     * Sets the individual MIPS capacity of any VM's PE, considering that all
     * PEs have the same capacity.
     *
     * @param mips the new mips for every VM's PE
     */
    protected final void setMips(double mips) {
        this.mips = mips;
    }

    @Override
    public int getNumberOfPes() {
        return numberOfPes;
    }

    /**
     * Sets the number of PEs required by the VM.
     *
     * @param numberOfPes the new number of PEs
     */
    protected final void setNumberOfPes(int numberOfPes) {
        this.numberOfPes = numberOfPes;
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
     * Sets a new {@link RawStorage} resource for the Vm.
     * @param storage the RawStorage resource to set
     */
    private void setStorage(RawStorage storage){
        Objects.requireNonNull(storage);
        this.storage = storage;
    }

    @Override
    public final Vm setSize(long size) {
        if(this.isCreated()){
            throw new UnsupportedOperationException("Storage size can just be changed when the Vm was not created inside a Host yet.");
        }
        setStorage(new RawStorage(size));
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
    public void setHost(Host host) {
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

        if (Objects.isNull(cloudletScheduler)) {
            cloudletScheduler = CloudletScheduler.NULL;
        }

        cloudletScheduler.setVm(this);
        this.cloudletScheduler = cloudletScheduler;
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
    public boolean isCreated() {
        return created;
    }

    @Override
    public final void setCreated(boolean created) {
        this.created = created;
    }

    /**
     * Gets the history of MIPS capacity allocated to the VM.
     *
     * @todo Instead of using a list, this attribute would be a map, where the
     * key can be the history time and the value the history itself. By this
     * way, if one wants to get the history for a given time, he/she doesn't
     * have to iterate over the entire list to find the desired entry.
     *
     * @return the state history
     */
    @Override
    public List<VmStateHistoryEntry> getStateHistory() {
        return stateHistory;
    }

    @Override
    public void addStateHistoryEntry(VmStateHistoryEntry entry) {
        if (!getStateHistory().isEmpty()) {
            VmStateHistoryEntry previousState = getStateHistory().get(getStateHistory().size() - 1);
            if (previousState.getTime() == entry.getTime()) {
                getStateHistory().set(getStateHistory().size() - 1, entry);
                return;
            }
        }
        getStateHistory().add(entry);
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
            resources = Arrays.asList(ram, bw, storage);
        }

        return Collections.unmodifiableList(resources);
    }

    @Override
    public Vm addOnHostAllocationListener(EventListener<VmHostEventInfo> listener) {
        if (!Objects.isNull(listener)) {
            this.onHostAllocationListeners.add(listener);
        }

        return this;
    }

    @Override
    public Vm addOnHostDeallocationListener(EventListener<VmHostEventInfo> listener) {
        if (!Objects.isNull(listener)) {
            this.onHostDeallocationListeners.add(listener);
        }

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
        return String.format("Vm %d", getId());
    }

    @Override
    public boolean removeOnVmCreationFailureListener(EventListener<VmDatacenterEventInfo> listener) {
        return onVmCreationFailureListeners.remove(listener);
    }

    @Override
    public Vm addOnVmCreationFailureListener(EventListener<VmDatacenterEventInfo> listener) {
        if (!Objects.isNull(listener)) {
            this.onVmCreationFailureListeners.add(listener);
        }

        return this;
    }

    @Override
    public boolean removeOnUpdateVmProcessingListener(EventListener<VmHostEventInfo> listener) {
        return onUpdateVmProcessingListeners.remove(listener);
    }

    @Override
    public Vm addOnUpdateVmProcessingListener(EventListener<VmHostEventInfo> listener) {
        if (!Objects.isNull(listener)) {
            this.onUpdateVmProcessingListeners.add(listener);
        }

        return this;
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

        VmSimple vmSimple = (VmSimple) o;

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
    public double getTotalMipsCapacity() {
        return getMips() * getNumberOfPes();
    }

    @Override
    public void setFailed(boolean failed) {
        // all the PEs are failed (or recovered, depending on fail parameter)
        this.failed = failed;
        if(failed) {
            Log.printLine(getSimulation().clock() + " ---> VM " + getUid() + " FAILURE...\n");
        }
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
        VmHostEventInfo info = VmHostEventInfo.of(this);
        onHostAllocationListeners.forEach(l -> l.update(info));
    }

    @Override
    public void notifyOnHostDeallocationListeners(Host deallocatedHost) {
        if(Objects.isNull(deallocatedHost)){
            return;
        }

        VmHostEventInfo info = VmHostEventInfo.of(this, deallocatedHost);
        onHostDeallocationListeners.forEach(l -> l.update(info));
    }

    /**
         * Notifies all registered listeners when the processing of the Vm is updated in its {@link Host}.
         */
    public void notifyOnUpdateVmProcessing() {
        VmHostEventInfo info = VmHostEventInfo.of(this);
        onUpdateVmProcessingListeners.forEach(l -> l.update(info));
    }

    @Override
    public void notifyOnVmCreationFailureListeners(Datacenter failedDatacenter) {
        if(Objects.isNull(failedDatacenter)){
            return;
        }

        VmDatacenterEventInfo info = VmDatacenterEventInfo.of(this, failedDatacenter);
        onVmCreationFailureListeners.forEach(l -> l.update(info));
    }


    @Override
    public HorizontalVmScaling getHorizontalScaling() {
        return horizontalScaling;
    }

    @Override
    public final Vm setHorizontalScaling(HorizontalVmScaling horizontalScaling) throws IllegalArgumentException {
        this.horizontalScaling = validateAndConfigureVmScaling(horizontalScaling, HorizontalVmScaling.NULL);
        return this;
    }

    @Override
    public final Vm setRamVerticalScaling(VerticalVmScaling ramVerticalScaling) throws IllegalArgumentException {
        this.ramVerticalScaling = validateAndConfigureVmScaling(ramVerticalScaling, VerticalVmScaling.NULL);
        return this;
    }

    @Override
    public final Vm setBwVerticalScaling(VerticalVmScaling bwVerticalScaling) throws IllegalArgumentException {
        this.bwVerticalScaling = validateAndConfigureVmScaling(bwVerticalScaling, VerticalVmScaling.NULL);
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

    private <T extends VmScaling> T validateAndConfigureVmScaling(T vmScaling, T defaultValue) {
        final T result = Objects.isNull(vmScaling) ? defaultValue : vmScaling;

        if(vmScaling.getVm() != null && vmScaling.getVm() != Vm.NULL && vmScaling.getVm() != this){
            String name = defaultValue.getClass().getSimpleName();
            throw new IllegalArgumentException(
                "The "+name+" given already is linked to a Vm. " +
                    "Each Vm must have its own "+name+" objects or none at all. " +
                    "A new scaling has to be provided to this Vm.");
        }

        result.setVm(this);
        this.addOnUpdateVmProcessingListener(listener -> result.requestUpScalingIfOverloaded(listener.getTime()));
        return result;
    }

}
