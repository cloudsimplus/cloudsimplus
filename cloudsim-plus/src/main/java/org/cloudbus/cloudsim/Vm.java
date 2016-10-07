package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.core.Identificable;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.listeners.DatacenterToVmEventInfo;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostToVmEventInfo;
import org.cloudbus.cloudsim.resources.ResourceManageable;

/**
 * An interface to be implemented by each class that provides basic
 * features of Virtual Machines (VMs).
 * The interface implements the Null Object
 * Design Pattern in order to start avoiding {@link NullPointerException} when
 * using the {@link Vm#NULL} object instead of attributing {@code null} to
 * {@link Vm} variables.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface Vm extends Identificable, Comparable<Vm> {

    /**
     * Adds a VM state history entry.
     *
     * @param entry the data about the state of the VM at given time
     */
    void addStateHistoryEntry(VmStateHistoryEntry entry);

    /**
     * Gets bandwidth capacity.
     *
     * @return bandwidth capacity.
     * @pre $none
     * @post $none
     */
    long getBw();

    /**
     * Gets the the Cloudlet scheduler the VM uses to schedule cloudlets
     * execution.
     *
     * @return the cloudlet scheduler
     */
    CloudletScheduler getCloudletScheduler();

    /**
     * Gets the current allocated bw.
     *
     * @return the current allocated bw
     */
    long getCurrentAllocatedBw();

    /**
     * Gets the current allocated MIPS for each VM's {@link Pe}. This list
     * represents the amount of MIPS in each VM's Pe that is available to be
     * allocated for VM's Cloudlets.
     *
     * @return the current allocated MIPS
     */
    List<Double> getCurrentAllocatedMips();

    /**
     * Gets the current allocated ram.
     *
     * @return the current allocated ram
     */
    int getCurrentAllocatedRam();

    /**
     * Gets the current allocated storage size.
     *
     * @return the current allocated size
     * @see #getSize()
     */
    long getCurrentAllocatedSize();

    /**
     * Gets the current requested bw.
     *
     * @return the current requested bw
     */
    long getCurrentRequestedBw();

    /**
     * Gets the current requested max mips among all virtual PEs.
     *
     * @return the current requested max mips
     */
    double getCurrentRequestedMaxMips();

    /**
     * Gets the current requested mips.
     *
     * @return the current requested mips
     */
    List<Double> getCurrentRequestedMips();

    /**
     * Gets the current requested ram.
     *
     * @return the current requested ram
     */
    int getCurrentRequestedRam();

    /**
     * Gets the current requested total mips. It is the sum of MIPS capacity
     * requested for every VM's Pe.
     *
     * @return the current requested total mips
     * @see #getCurrentRequestedMips()
     */
    double getCurrentRequestedTotalMips();

    /**
     * Gets the PM that hosts the VM.
     *
     * @return the host
     */
    Host getHost();

    /**
     * Gets unique string identifier of the VM.
     *
     * @return string uid
     */
    String getUid();

    /**
     * Gets the individual MIPS capacity of any VM's PE, considering that all
     * PEs have the same capacity.
     *
     * @return the mips
     */
    double getMips();

    /**
     * Gets the number of PEs required by the VM. Each PE has the capacity
     * defined in {@link #getMips()}
     *
     * @return the number of PEs
     * @see #getMips()
     */
    int getNumberOfPes();

    /**
     * Gets the total MIPS capacity (across all PEs) of this VM.
     *
     * @return MIPS capacity sum of all PEs
     *
     * @see #getMips()
     * @see #getNumberOfPes()
     */
    double getTotalMipsCapacity();

    <T extends Number, R extends ResourceManageable<? extends T>> ResourceManageable<T> getResource(Class<R> resourceClass);


    /**
     * Gets the listener object that will be notified when a {@link Host}
     * is allocated to the Vm, that is, when the Vm is placed into a
     * given Host. The listener receives the placed Vm and the
     * Host where it is placed.
     *
     * @return the onHostAllocationListener
     */
    EventListener<HostToVmEventInfo> getOnHostAllocationListener();

    /**
     * Gets the listener object that will be notified when a {@link Host}
     * is deallocated to the Vm, that is, when the Vm is
     * moved/removed from the Host is was placed. The listener receives
     * the moved/removed Vm and the Host where it was placed.
     *
     * @return the onHostDeallocationListener
     */
    EventListener<HostToVmEventInfo> getOnHostDeallocationListener();

    /**
     * Gets the listener object that will be notified when a Vm fail in
     * being placed for lack of a {@link Host} in the {@link Datacenter}
     * with enough resources.
     *
     * @return the onVmCreationFailureListener
     */
    EventListener<DatacenterToVmEventInfo> getOnVmCreationFailureListener();

    /**
     * Gets the RAM capacity.
     *
     * @return the RAM capacity
     * @pre $none
     * @post $none
     */
    int getRam();

    /**
     * Gets the storage size (capacity) of the VM image (the amount of storage
     * it will use, at least initially).
     *
     * @return amount of storage
     * @pre $none
     * @post $none
     */
    long getSize();

    /**
     * Gets the history of MIPS capacity allocated to the VM.
     *
     * @return the state history
     */
    List<VmStateHistoryEntry> getStateHistory();

    /**
     * Gets total CPU utilization percentage of all clouddlets running on this
     * VM at the given time
     *
     * @param time the time
     * @return total utilization percentage
     */
    double getTotalUtilizationOfCpu(double time);

    /**
     * Gets the total CPU utilization of all cloudlets running on this VM at the
     * given time (in MIPS).
     *
     * @param time the time
     * @return total cpu utilization in MIPS
     * @see #getTotalUtilizationOfCpu(double)
     *
     */
    double getTotalUtilizationOfCpuMips(double time);

    /**
     * Gets the ID of the owner of the VM.
     *
     * @return VM's owner ID
     * @pre $none
     * @post $none
     */
    int getUserId();

    /**
     * Gets the Virtual Machine Monitor (VMM) that manages the VM.
     *
     * @return VMM
     * @pre $none
     * @post $none
     */
    String getVmm();

    /**
     * Checks if the VM is being instantiated.
     *
     * @return true, if is being instantiated; false otherwise
     */
    boolean isBeingInstantiated();

    /**
     * Checks if the VM is in migration process.
     *
     * @return true, if it is in migration
     */
    boolean isInMigration();

    /**
     * Indicates if the VM is being instantiated.
     *
     * @param beingInstantiated true to indicate the VM is being instantiated;
     * false otherwise
     */
    void setBeingInstantiated(boolean beingInstantiated);

    /**
     * Sets the BW capacity
     *
     * @param bwCapacity new BW capacity
     * @return true if bwCapacity > 0 and bwCapacity >= current allocated
     * resource, false otherwise
     * @pre bwCapacity > 0
     * @post $none
     */
    boolean setBw(long bwCapacity);

    /**
     * Sets the current allocated bw.
     *
     * @param newTotalAllocateddBw the new total allocated bw
     */
    void setCurrentAllocatedBw(long newTotalAllocateddBw);

    /**
     * Sets the current allocated MIPS for each VM's PE.
     *
     * @param currentAllocatedMips the new current allocated mips
     */
    void setCurrentAllocatedMips(List<Double> currentAllocatedMips);

    /**
     * Sets the current allocated ram.
     *
     * @param newTotalAllocateddRam the new total allocated ram
     */
    void setCurrentAllocatedRam(int newTotalAllocateddRam);

    /**
     * Sets the PM that hosts the VM.
     *
     * @param host Host to run the VM
     * @pre host != $null
     * @post $none
     */
    void setHost(Host host);

    /**
     * Defines if the VM is in migration process.
     *
     * @param inMigration true to indicate the VM is in migration, false
     * otherwise
     */
    void setInMigration(boolean inMigration);

    /**
     * Sets the listener object that will be notified when a {@link Host}
     * is allocated to the Vm, that is, when the Vm is placed into a
     * given Host.
     *
     * @param onHostAllocationListener the listener to set
     */
    void setOnHostAllocationListener(EventListener<HostToVmEventInfo> onHostAllocationListener);

    /**
     * Sets the listener object that will be notified when a {@link Host}
     * is deallocated to the Vm, that is, when the Vm is
     * moved/removed from the Host it was placed.
     *
     * @param onHostDeallocationListener the listener to set
     */
    void setOnHostDeallocationListener(EventListener<HostToVmEventInfo> onHostDeallocationListener);

    /**
     * Sets the listener object that will be notified when the Vm fail in
     * being placed for lack of a {@link Host} in the {@link Datacenter}
     * with enough resources.
     *
     * @param onVmCreationFailureListener the listener to set
     * @see #updateVmProcessing(double, java.util.List)
     */
    void setOnVmCreationFailureListener(EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener);

    /**
     * Gets the listener object that will be notified every time when
     * the processing of the Vm is updated in its {@link Host}.
     *
     * @return the onUpdateVmProcessingListener
     */
    EventListener<HostToVmEventInfo> getOnUpdateVmProcessingListener();

    /**
     * Sets the listener object that will be notified every time when
     * the processing of the Vm is updated in its {@link Host}.
     *
     * @param onUpdateVmProcessingListener the listener to set
     * @see #updateVmProcessing(double, java.util.List)
     */
    void setOnUpdateVmProcessingListener(EventListener<HostToVmEventInfo> onUpdateVmProcessingListener);

    /**
     * Sets RAM capacity.
     *
     * @param ramCapacity new RAM capacity
     * @return true if ramCapacity > 0 and ramCapacity >= current allocated
     * resource, false otherwise
     * @pre ramCapacity > 0
     * @post $none
     */
    boolean setRam(int ramCapacity);

    /**
     * Sets the storage size (capacity) of the VM image.
     *
     * @param size new storage size
     * @return true if size > 0 and size >= current allocated resource, false
     * otherwise
     * @pre size > 0
     * @post $none
     *
     */
    boolean setSize(long size);

    /**
     * Sets the ID of the User (UID) that owns the VM.
     *
     * @param uid the new UID
     */
    void setUid(String uid);

    /**
     * Updates the processing of cloudlets running on this VM.
     *
     * @param currentTime current simulation time
     * @param mipsShare list with MIPS share of each Pe available to the
     * scheduler
     * @return predicted completion time of the earliest finishing
     * cloudlet, or {@link Double#MAX_VALUE} if there is no next events
     * @pre currentTime >= 0
     * @post $none
     */
    double updateVmProcessing(double currentTime, List<Double> mipsShare);

    /**
     * A property that implements the Null Object Design Pattern for {@link Vm}
     * objects.
     */
    Vm NULL = new Vm() {
        @Override public void addStateHistoryEntry(VmStateHistoryEntry entry) {}
        @Override public long getBw(){ return 0L; }
        @Override public CloudletScheduler getCloudletScheduler() { return CloudletScheduler.NULL; }
        @Override public long getCurrentAllocatedBw() { return 0L; }
        @Override public List<Double> getCurrentAllocatedMips(){ return Collections.emptyList(); }
        @Override public int getCurrentAllocatedRam(){ return 0; }
        @Override public long getCurrentAllocatedSize() { return 0L; }
        @Override public long getCurrentRequestedBw() { return 0L; }
        @Override public double getCurrentRequestedMaxMips() { return 0.0; }
        @Override public List<Double> getCurrentRequestedMips() { return Collections.emptyList(); }
        @Override public int getCurrentRequestedRam() { return 0; }
        @Override public double getCurrentRequestedTotalMips() { return 0.0; }
        @Override public Host getHost() { return Host.NULL; }
        @Override public int getId() { return 0; }
        @Override public double getMips() { return 0.0; }
        @Override public int getNumberOfPes() { return 0; }
        @Override public EventListener<HostToVmEventInfo> getOnHostAllocationListener() { return EventListener.NULL; }
        @Override public EventListener<HostToVmEventInfo> getOnHostDeallocationListener() { return EventListener.NULL; }
        @Override public EventListener<DatacenterToVmEventInfo> getOnVmCreationFailureListener() { return EventListener.NULL; }
        @Override public int getRam() { return 0; }
        @Override public long getSize(){ return 0L; }
        @Override public List<VmStateHistoryEntry> getStateHistory() { return Collections.emptyList(); }
        @Override public double getTotalUtilizationOfCpu(double time) { return 0.0; }
        @Override public double getTotalUtilizationOfCpuMips(double time) { return 0.0; }
        @Override public String getUid(){ return ""; }
        @Override public int getUserId() { return 0; }
        @Override public String getVmm() { return ""; }
        @Override public boolean isBeingInstantiated() { return false; }
        @Override public boolean isInMigration() { return false; }
        @Override public void setBeingInstantiated(boolean beingInstantiated){}
        @Override public boolean setBw(long bwCapacity) { return false; }
        @Override public void setCurrentAllocatedBw(long newTotalAllocateddBw) {}
        @Override public void setCurrentAllocatedMips(List<Double> currentAllocatedMips) {}
        @Override public void setCurrentAllocatedRam(int newTotalAllocateddRam) {}
        @Override public void setHost(Host host) {}
        @Override public void setInMigration(boolean inMigration) {}
        @Override public void setOnHostAllocationListener(EventListener<HostToVmEventInfo> onHostAllocationListener) {}
        @Override public void setOnHostDeallocationListener(EventListener<HostToVmEventInfo> onHostDeallocationListener) {}
        @Override public void setOnVmCreationFailureListener(EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener) {}
        @Override public boolean setRam(int ramCapacity) { return false; }
        @Override public boolean setSize(long size){ return false; }
        @Override public void setUid(String uid) {}
        @Override public double updateVmProcessing(double currentTime, List<Double> mipsShare){ return 0.0; }
        @Override public <T extends Number, R extends ResourceManageable<? extends T>> ResourceManageable<T> getResource(Class<R> resourceClass) { return ResourceManageable.NULL_DOUBLE; }
        @Override public EventListener<HostToVmEventInfo> getOnUpdateVmProcessingListener() { return EventListener.NULL; }
        @Override public void setOnUpdateVmProcessingListener(EventListener<HostToVmEventInfo> onUpdateVmProcessingListener) {}
        @Override public int compareTo(Vm o) { return 0; }
        @Override public double getTotalMipsCapacity() { return 0.0; }
    };
}
