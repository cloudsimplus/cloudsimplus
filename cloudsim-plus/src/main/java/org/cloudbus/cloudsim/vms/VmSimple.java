/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms;

import java.util.*;

import org.cloudbus.cloudsim.core.UniquelyIdentificable;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudsimplus.listeners.DatacenterToVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostToVmEventInfo;
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
 * @todo @author manoelcampos Instead of having the methods getRam, getUsedRam,
 * getAvailableRam (and the same for other resources), it would be returned the
 * ResourceManageable object for each different kind of resource, in order to
 * allow the user to get the resource capacity, used and available capacity
 * without defining a specific method for each one.
 */
public class VmSimple implements Vm {
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

    /**
     * The map of resources the VM has. Each key is the class of a given VM
     * resource and each value is the resource itself.
     */
    private final Map<Class<? extends ResourceManageable>, ResourceManageable> resources;

    /**
     * @see #getStateHistory()
     */
    private final List<VmStateHistoryEntry> stateHistory = new LinkedList<>();

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

    /**
     * @see #getOnHostAllocationListener()
     */
    private EventListener<HostToVmEventInfo> onHostAllocationListener = EventListener.NULL;

    /**
     * @see #getOnHostDeallocationListener()
     */
    private EventListener<HostToVmEventInfo> onHostDeallocationListener = EventListener.NULL;

    /**
     * @see #getOnVmCreationFailureListener()
     */
    private EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener = EventListener.NULL;

    /**
     * @see #getOnUpdateVmProcessingListener()
     */
    private EventListener<HostToVmEventInfo> onUpdateVmProcessingListener = EventListener.NULL;

    /**
     * @see #getSimulation()
     */
    private Simulation simulation;

    /**
     * Creates a Vm with 1024 MB of RAM, 1000 Megabits/s of Bandwidth and 1024 MB of Storage Size.
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
    public VmSimple(int id, double mipsCapacity, int numberOfPes) {
        this.resources = new HashMap<>();
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
        this.simulation = Simulation.NULL;
        setCloudletScheduler(CloudletScheduler.NULL);
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
            double mipsCapacity,
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
        if (!Objects.isNull(mipsShare)) {
            double result = getCloudletScheduler().updateVmProcessing(currentTime, mipsShare);
            HostToVmEventInfo info = new HostToVmEventInfo(currentTime, host, this);
            onUpdateVmProcessingListener.update(info);
            return result;
        }

        return Double.MAX_VALUE;
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
    public double getCurrentRequestedTotalMips() {
        return getCurrentRequestedMips().stream().mapToDouble(m->m).sum();
    }

    @Override
    public double getCurrentRequestedMaxMips() {
        return getCurrentRequestedMips().stream().mapToDouble(m->m).max().orElse(0.0);
    }

    @Override
    public long getCurrentRequestedBw() {
        if (!isCreated()) {
            return getBw();
        }

        return (long) (getCloudletScheduler().getCurrentRequestedUtilizationOfBw() * getBw());
    }

    @Override
    public long getCurrentRequestedRam() {
        if (!isCreated()) {
            return getRam();
        }

        return (int) (getCloudletScheduler().getCurrentRequestedUtilizationOfRam() * getRam());
    }

    @Override
    public double getTotalUtilizationOfCpu(double time) {
        return getCloudletScheduler().getTotalUtilizationOfCpu(time);
    }

    /**
     * Gets the total CPU utilization of all cloudlets running on this VM at the
     * given time (in MIPS).
     *
     * @param time the time
     * @return total cpu utilization in MIPS
     * @see #getTotalUtilizationOfCpu(double)
     *
     * @todo @author manoelcampos Lets consider the UtilizationModelFull for CPU
     * which defines that a cloudlet will use the entire CPU allocated to it all
     * the time, for all of its PEs. So, lets say that the Vm has 2 PEs of 1000
     * MIPS, that represents a total of 2000 MIPS capacity, and there is a
     * Cloudlet that is using all these 2 PEs capacity. I think this method is
     * supposed to return 2000, indicating that the entire VM MIPS capacity is
     * being used. However, it will return only 1000. It has to be included some
     * test cases do try figure out if the method is returning what it is
     * supposed to return or not.
     */
    @Override
    public double getTotalUtilizationOfCpuMips(double time) {
        return getTotalUtilizationOfCpu(time) * getMips();
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
    public long getRam() {
        return ram.getCapacity();
    }

    /**
     * Sets a new {@link Ram} resource for the Vm.
     * @param ram the Ram resource to set
     */
    private void setRam(Ram ram) {
        if(Objects.isNull(ram)){
            throw new NullPointerException("Vm RAM cannot be null");
        }
        this.ram = ram;
        resources.put(Ram.class, ram);
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
    public long getBw() {
        return bw.getCapacity();
    }

    /**
     * Sets a new {@link Bandwidth} resource for the Vm.
     * @param bw the Bandwidth resource to set
     */
    private void setBw(Bandwidth bw){
        if(Objects.isNull(bw)){
            throw new NullPointerException("Vm Bandwidth cannot be null");
        }
        this.bw = bw;
        resources.put(Bandwidth.class, bw);
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
    public long getSize() {
        return storage.getCapacity();
    }

    /**
     * Sets a new {@link RawStorage} resource for the Vm.
     * @param storage the RawStorage resource to set
     */
    private void setStorage(RawStorage storage){
        if(Objects.isNull(storage)){
            throw new NullPointerException("Vm Storage cannot be null");
        }
        this.storage = storage;
        resources.put(RawStorage.class, storage);
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
     * @see #getSize()
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

    /**
     * @todo The method has to be tested with different instances of
     * ResourceManageable, with children in different levels of the class
     * hierarchy.
     * @param resourceClass the class of the resource to be got
     * @return
     */
    @Override
    public <R extends ResourceManageable>
            ResourceManageable getResource(Class<R> resourceClass) {
        //reference: http://stackoverflow.com/questions/2284949/how-do-i-declare-a-member-with-multiple-generic-types-that-have-non-trivial-rela
        return resources.get(resourceClass);
    }

    @Override
    public Vm setOnHostAllocationListener(EventListener<HostToVmEventInfo> onHostAllocationListener) {
        if (Objects.isNull(onHostAllocationListener)) {
            onHostAllocationListener = EventListener.NULL;
        }

        this.onHostAllocationListener = onHostAllocationListener;
        return this;
    }

    @Override
    public Vm setOnHostDeallocationListener(EventListener<HostToVmEventInfo> onHostDeallocationListener) {
        if (Objects.isNull(onHostDeallocationListener)) {
            onHostDeallocationListener = EventListener.NULL;
        }

        this.onHostDeallocationListener = onHostDeallocationListener;
        return this;
    }

    @Override
    public EventListener<HostToVmEventInfo> getOnHostAllocationListener() {
        return onHostAllocationListener;
    }

    @Override
    public EventListener<HostToVmEventInfo> getOnHostDeallocationListener() {
        return onHostDeallocationListener;
    }

    @Override
    public String toString() {
        return String.format("Vm %d", getId());
    }

    @Override
    public EventListener<DatacenterToVmEventInfo> getOnVmCreationFailureListener() {
        return onVmCreationFailureListener;
    }

    @Override
    public Vm setOnVmCreationFailureListener(EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener) {
        if (Objects.isNull(onVmCreationFailureListener)) {
            onVmCreationFailureListener = EventListener.NULL;
        }

        this.onVmCreationFailureListener = onVmCreationFailureListener;
        return this;
    }

    @Override
    public EventListener<HostToVmEventInfo> getOnUpdateVmProcessingListener() {
        return onUpdateVmProcessingListener;
    }

    @Override
    public Vm setOnUpdateVmProcessingListener(EventListener<HostToVmEventInfo> onUpdateVmProcessingListener) {
        if (Objects.isNull(onUpdateVmProcessingListener)) {
            onUpdateVmProcessingListener = EventListener.NULL;
        }

        this.onUpdateVmProcessingListener = onUpdateVmProcessingListener;
        return this;
    }

    @Override
    public int compareTo(Vm o) {
        return this.getUid().compareTo(o.getUid());
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
        return this.simulation;
    }

    @Override
    public Vm setSimulation(Simulation simulation) {
        this.simulation = simulation;
        return this;
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
}
