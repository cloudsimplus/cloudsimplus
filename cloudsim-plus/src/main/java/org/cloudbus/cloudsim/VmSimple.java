/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.cloudbus.cloudsim.listeners.DatacenterToVmEventInfo;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostToVmEventInfo;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.resources.RawStorage;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import org.cloudbus.cloudsim.resources.ResourceManageable;

/**
 * Implements the basic features of a Virtual Machine (VM) that runs inside a
 * {@link Host} that may be shared among other VMs. It processes {@link Cloudlet cloudlets}.
 * This processing happens according to a policy, defined by the
 * {@link CloudletScheduler}. Each VM has a owner (user), which can submit cloudlets to
 * the VM to execute them.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 *
 * @todo @author manoelcampos Instead of having the methods getRam, getUsedRam,
 getAvailableRam (and the same for other resources), it would be returned the
 ResourceManageable object for each different kind of resource, in order to allow the
 user to get the resource capacity, used and available capacity without
 defining a specific method for each one.
 */
public class VmSimple implements Vm {

    /** @see #getId() */
    private int id;

    /** @see #getUserId() */
    private int userId;

    /**
     * A Unique Identifier (UID) for the VM, that is compounded by the user id
     * and VM id.
     */
    private String uid;

    /** @see #getMips() */
    private double mips;

    /** @see #getCurrentAllocatedMips() */
    private List<Double> currentAllocatedMips;

    /** @see #getNumberOfPes() */
    private int numberOfPes;

    /** @see #getVmm() */
    private String vmm;

    /** @see #getCloudletScheduler() */
    private CloudletScheduler cloudletScheduler;

    /** @see #getHost() */
    private Host host;

    /** @see #isInMigration() */
    private boolean inMigration;

    /** @see #isBeingInstantiated() */
    private boolean beingInstantiated;

    /**
     * The map of resources the VM has. Each key is the class of a given VM
     * resource and each value is the resource itself.
     */
    private final Map<Class<? extends ResourceManageable<? extends Number>>, ResourceManageable<? extends Number>> resources;

    /** @see #getStateHistory() */
    private final List<VmStateHistoryEntry> stateHistory = new LinkedList<>();

    /**
     * The VM's storage resource, containing information about capacity and
     * allocation.
     */
    private final RawStorage storage;

    /**
     * The VM's RAM resource, containing information about capacity and
     * allocation.
     */
    private final Ram ram;

    /**
     * The VM's Bandwidth (BW) resource, containing information about capacity
     * and allocation (in Megabits/s).
     */
    private final Bandwidth bw;

    /** @see #getOnHostAllocationListener() */
    private EventListener<HostToVmEventInfo> onHostAllocationListener = EventListener.NULL;

    /** @see #getOnHostDeallocationListener() */
    private EventListener<HostToVmEventInfo> onHostDeallocationListener = EventListener.NULL;

    /** @see #getOnVmCreationFailureListener() */
    private EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener = EventListener.NULL;

    /** @see #getOnUpdateVmProcessingListener() */
    private EventListener<HostToVmEventInfo> onUpdateVmProcessingListener = EventListener.NULL;

    /**
     * Creates a new Vm object.
     *
     * @param id unique ID of the VM
     * @param userId ID of the VM's owner
     * @param mipsCapacity the mips
     * @param numberOfPes amount of CPUs
     * @param ramCapacity amount of ram
     * @param bwCapacity amount of bandwidth to be allocated to the VM (in Megabits/s)
     * @param storageCapacity The size the VM image (the amount of storage it
     * will use, at least initially).
     * @param vmm virtual machine monitor
     * @param cloudletScheduler cloudletScheduler policy for cloudlets
     * scheduling
     *
     * @pre id >= 0
     * @pre userId >= 0
     * @pre storageCapacity > 0
     * @pre ramCapacity > 0
     * @pre bwCapacity > 0
     * @pre numberOfPes > 0
     * @pre priority >= 0
     * @pre cloudletScheduler != null
     * @post $none
     */
    public VmSimple(
            int id,
            int userId,
            double mipsCapacity,
            int numberOfPes,
            int ramCapacity,
            long bwCapacity,
            long storageCapacity,
            String vmm,
            CloudletScheduler cloudletScheduler) {
        resources = new HashMap<>();
        this.ram = new Ram(ramCapacity);
        this.bw = new Bandwidth(bwCapacity);
        this.storage = new RawStorage(storageCapacity);
        assignVmResources();

        setId(id);
        setUserId(userId);
        setUid(getUid(userId, id));
        setMips(mipsCapacity);
        setNumberOfPes(numberOfPes);
        setSize(storageCapacity);
        setVmm(vmm);
        setCloudletScheduler(cloudletScheduler);

        setInMigration(false);
        setBeingInstantiated(true);

        setCurrentAllocatedBw(0);
        setCurrentAllocatedMips(null);
        setCurrentAllocatedRam(0);
        setCurrentAllocatedSize(0);
    }

    /**
     * Assigns each individual VM resource to the {@link #resources} map in
     * order to allow getting the information of a given resource in a
     * generic/parameterized way.
     *
     * @see #getResource(java.lang.Class)
     */
    private void assignVmResources() {
        resources.put(Ram.class, ram);
        resources.put(Bandwidth.class, bw);
        resources.put(RawStorage.class, storage);
    }

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        if (mipsShare != null) {
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
        if (isBeingInstantiated()) {
            currentRequestedMips = new ArrayList<>();
            for (int i = 0; i < getNumberOfPes(); i++) {
                currentRequestedMips.add(getMips());
            }
        }
        return currentRequestedMips;
    }

    @Override
    public double getCurrentRequestedTotalMips() {
        return getCurrentRequestedMips().stream().reduce(0.0, Double::sum);
    }

    @Override
    public double getCurrentRequestedMaxMips() {
        Optional<Double> result = getCurrentRequestedMips().stream().max(Double::compare);
        return result.orElse(0.0);
    }

    @Override
    public long getCurrentRequestedBw() {
        if (isBeingInstantiated()) {
            return getBw();
        }
        
        return (long) (getCloudletScheduler().getCurrentRequestedUtilizationOfBw() * getBw());
    }

    @Override
    public int getCurrentRequestedRam() {
        if (isBeingInstantiated()) {
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
    public final void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getUid() {
        return uid;
    }

    /**
     * Generate unique string identifier of the VM.
     *
     * @param userId the user id
     * @param vmId the vm id
     * @return string uid
     */
    public static String getUid(int userId, int vmId) {
        return userId + "-" + vmId;
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * Sets the VM id.
     *
     * @param id the new VM id, that has to be unique for the current
     * {@link #getUserId() user}
     * @todo The uniqueness of VM id for a given user is not being ensured
     */
    protected final void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the ID of the owner of the VM.
     *
     * @param userId the new user id
     */
    protected final void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public int getUserId() {
        return userId;
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
    public int getRam() {
        return ram.getCapacity();
    }

    @Override
    public final boolean setRam(int ramCapacity) {
        return ram.setCapacity(ramCapacity);
    }

    @Override
    public long getBw() {
        return bw.getCapacity();
    }

    @Override
    public final boolean setBw(long bwCapacity) {
        return bw.setCapacity(bwCapacity);
    }

    @Override
    public long getSize() {
        return storage.getCapacity();
    }

    @Override
    public final boolean setSize(long size) {
        return this.storage.setCapacity(size);
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

    /**
     * Sets the Cloudlet scheduler the VM uses to schedule cloudlets execution.
     *
     * @param cloudletScheduler the new cloudlet scheduler
     */
    protected final void setCloudletScheduler(CloudletScheduler cloudletScheduler) {
        if(cloudletScheduler == null)
            cloudletScheduler = CloudletScheduler.NULL;
        
        cloudletScheduler.setVm(this);
        this.cloudletScheduler = cloudletScheduler;
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
     * @todo It has never been used.
     */
    @Override
    public long getCurrentAllocatedSize() {
        return storage.getAllocatedResource();
    }

    /**
     * Sets the current allocated storage size.
     *
     * @param currentAllocatedSize the new current allocated size
     * @todo It has never been used.
     */
    protected final void setCurrentAllocatedSize(long currentAllocatedSize) {
        storage.setAllocatedResource(currentAllocatedSize);
    }

    @Override
    public int getCurrentAllocatedRam() {
        return ram.getAllocatedResource();
    }

    @Override
    public final void setCurrentAllocatedRam(int newTotalAllocateddRam) {
        ram.setAllocatedResource(newTotalAllocateddRam);
    }

    @Override
    public long getCurrentAllocatedBw() {
        return bw.getAllocatedResource();
    }

    @Override
    public final void setCurrentAllocatedBw(long newTotalAllocateddBw) {
        bw.setAllocatedResource(newTotalAllocateddBw);
    }

    /**
     * Gets the current allocated MIPS for each VM's {@link Pe}. This list
     * represents the amount of MIPS in each VM's Pe that is available to be
     * allocated for VM's Cloudlets.
     *
     * @return the current allocated MIPS
     * @TODO The method doesn't appear to be used.
     */
    @Override
    public List<Double> getCurrentAllocatedMips() {
        return currentAllocatedMips;
    }

    /**
     * Sets the current allocated MIPS for each VM's PE.
     *
     * @param currentAllocatedMips the new current allocated mips
     * @todo The method doesn't appear to be used.
     */
    @Override
    public final void setCurrentAllocatedMips(List<Double> currentAllocatedMips) {
        this.currentAllocatedMips = currentAllocatedMips;
    }

    @Override
    public boolean isBeingInstantiated() {
        return beingInstantiated;
    }

    @Override
    public final void setBeingInstantiated(boolean beingInstantiated) {
        this.beingInstantiated = beingInstantiated;
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
     * AbstractResource, with children in different levels of the class
     * hierarchy.
     * @param <T>
     * @param <R>
     * @param resourceClass
     * @return
     */
    @Override
    public <T extends Number, R extends ResourceManageable<? extends T>>
            ResourceManageable<T> getResource(Class<R> resourceClass) {
        //reference: http://stackoverflow.com/questions/2284949/how-do-i-declare-a-member-with-multiple-generic-types-that-have-non-trivial-rela
        return (ResourceManageable<T>) resources.get(resourceClass);
    }

    @Override
    public void setOnHostAllocationListener(EventListener<HostToVmEventInfo> onHostAllocationListener) {
        if (onHostAllocationListener == null)
            onHostAllocationListener = EventListener.NULL;
        
        this.onHostAllocationListener = onHostAllocationListener;
    }

    @Override
    public void setOnHostDeallocationListener(EventListener<HostToVmEventInfo> onHostDeallocationListener) {
        if (onHostDeallocationListener == null)
            onHostDeallocationListener = EventListener.NULL;
        
        this.onHostDeallocationListener = onHostDeallocationListener;
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
        return this.uid;
    }

    @Override
    public EventListener<DatacenterToVmEventInfo> getOnVmCreationFailureListener() {
        return onVmCreationFailureListener;
    }

    @Override
    public void setOnVmCreationFailureListener(EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener) {
        if (onVmCreationFailureListener == null) 
            onVmCreationFailureListener = EventListener.NULL;
        
        this.onVmCreationFailureListener = onVmCreationFailureListener;
    }

    @Override
    public EventListener<HostToVmEventInfo> getOnUpdateVmProcessingListener() {
        return onUpdateVmProcessingListener;
    }

    @Override
    public void setOnUpdateVmProcessingListener(EventListener<HostToVmEventInfo> onUpdateVmProcessingListener) {
        if(onUpdateVmProcessingListener == null)
            onUpdateVmProcessingListener = EventListener.NULL;
        
        this.onUpdateVmProcessingListener = onUpdateVmProcessingListener;
    }

    /**
     * <p>Compares this Vm with another one, considering
     * the {@link #getTotalMipsCapacity() total MIPS capacity of the Vm's}.</p>
     * 
     * @param o the Vm to be compared to
     * @return {@inheritDoc }
     * @see #getTotalMipsCapacity() 
     */
    @Override
    public int compareTo(Vm o) {
        return Double.compare(this.getTotalMipsCapacity(), o.getTotalMipsCapacity());
    }

    @Override
    public double getTotalMipsCapacity() {
        return getMips() * getNumberOfPes();
    }
}
