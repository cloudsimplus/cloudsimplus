package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.Identificable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudsimplus.listeners.DatacenterToVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostToVmEventInfo;

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
     * Gets the current allocated ram.
     *
     * @return the current allocated ram
     */
    long getCurrentAllocatedRam();

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
    long getCurrentRequestedRam();

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
     * Gets the Unique Identifier (UID) for the VM, that is compounded by the user id
     * and VM id.
     *
     * @return string UID
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

    /**
     * Gets a given Vm {@link Resource}, such as {@link Ram} or {@link Bandwidth},
     * from the class of the resource to get.
     *
     * @param resourceClass the class of the resource to get
     * @param <R> generic type that defines the class of resources that can be got
     * @return the Vm {@link Resource} corresponding to the given class
     */
    <R extends ResourceManageable> ResourceManageable getResource(Class<R> resourceClass);


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
     * Gets the RAM capacity in Megabytes.
     *
     * @return the RAM capacity
     * @pre $none
     * @post $none
     */
    long getRam();

    /**
     * Gets the storage size (capacity) of the VM image in Megabytes (the amount of storage
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
     * Gets the ID the {@link DatacenterBroker} that represents the owner of the VM.
     *
     * @return the broker ID or <tt>-1</tt> if a broker has not been set yet
     * @pre $none
     * @post $none
     */
    int getBrokerId();

    /**
     * Sets a {@link DatacenterBroker} that represents the owner of the VM.
     *
     * @param broker the {@link DatacenterBroker} to set
     */
    Vm setBroker(DatacenterBroker broker);


    /**
     * Gets the Virtual Machine Monitor (VMM) that manages the VM.
     *
     * @return VMM
     * @pre $none
     * @post $none
     */
    String getVmm();

    /**
     * Checks if the VM was created and placed inside a {@link Host}.
     * If so, resources required by the Vm already were provisioned.
     *
     * @return true, if it was created, false otherwise
     */
    boolean isCreated();

    /**
     * Changes the created status of the Vm.
     *
     * @param created true to indicate the VM was created; false otherwise
     * @see #isCreated()
     */
    void setCreated(boolean created);


    /**
     * Checks if the VM is in migration process.
     *
     * @return true, if it is in migration
     */
    boolean isInMigration();

    /**
     * Sets the BW capacity
     *
     * @param bwCapacity new BW capacity
     * @return
     * @pre bwCapacity > 0
     * @post $none
     */
    Vm setBw(long bwCapacity);

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
    Vm setOnHostAllocationListener(EventListener<HostToVmEventInfo> onHostAllocationListener);

    /**
     * Sets the listener object that will be notified when a {@link Host}
     * is deallocated to the Vm, that is, when the Vm is
     * moved/removed from the Host it was placed.
     *
     * @param onHostDeallocationListener the listener to set
     */
    Vm setOnHostDeallocationListener(EventListener<HostToVmEventInfo> onHostDeallocationListener);

    /**
     * Sets the listener object that will be notified when the Vm fail in
     * being placed for lack of a {@link Host} in the {@link Datacenter}
     * with enough resources.
     *
     * @param onVmCreationFailureListener the listener to set
     * @see #updateVmProcessing(double, java.util.List)
     */
    Vm setOnVmCreationFailureListener(EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener);

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
    Vm setOnUpdateVmProcessingListener(EventListener<HostToVmEventInfo> onUpdateVmProcessingListener);

    /**
     * Sets RAM capacity in Megabytes.
     *
     * @param ramCapacity new RAM capacity
     * @return
     * @pre ramCapacity > 0
     * @post $none
     */
    Vm setRam(long ramCapacity);

    /**
     * Sets the storage size (capacity) of the VM image in Megabytes.
     *
     * @param size new storage size
     * @return
     * @pre size > 0
     * @post $none
     *
     */
    Vm setSize(long size);

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
     * Sets the Cloudlet scheduler the VM uses to schedule cloudlets execution.
     *
     * @param cloudletScheduler the cloudlet scheduler to set
     * @return
     */
    Vm setCloudletScheduler(CloudletScheduler cloudletScheduler);

    /**
     * Sets the status of VM to FAILED.
     *
     * @param failed the failed
     */
    void setFailed(boolean failed);

    /**
     * Checks if the Vm is failed or not.
     * @return
     */
    boolean isFailed();

    /**
     * Gets the CloudSim instance that represents the simulation the Entity is related to.
     * @return
     * @see #setSimulation(CloudSim)
     */
    Simulation getSimulation();

    /**
     * Sets the CloudSim instance that represents the simulation the Entity is related to.
     * Such attribute has to be set by the {@link DatacenterBroker} that creates
     * the Vm on behalf of its owner.
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @return
     */
    Vm setSimulation(Simulation simulation);


    /**
     * A property that implements the Null Object Design Pattern for {@link Vm}
     * objects.
     */
    Vm NULL = new Vm() {
        @Override public void addStateHistoryEntry(VmStateHistoryEntry entry) {}
        @Override public long getBw(){ return 0; }
        @Override public CloudletScheduler getCloudletScheduler() { return CloudletScheduler.NULL; }
        @Override public long getCurrentAllocatedBw() { return 0; }
        @Override public long getCurrentAllocatedRam(){ return 0; }
        @Override public long getCurrentAllocatedSize() { return 0; }
        @Override public long getCurrentRequestedBw() { return 0; }
        @Override public double getCurrentRequestedMaxMips() { return 0.0; }
        @Override public List<Double> getCurrentRequestedMips() { return Collections.emptyList(); }
        @Override public long getCurrentRequestedRam() { return 0; }
        @Override public double getCurrentRequestedTotalMips() { return 0.0; }
        @Override public Host getHost() { return Host.NULL; }
        @Override public int getId() { return -1; }
        @Override public double getMips() { return 0.0; }
        @Override public int getNumberOfPes() { return 0; }
        @Override public EventListener<HostToVmEventInfo> getOnHostAllocationListener() { return EventListener.NULL; }
        @Override public EventListener<HostToVmEventInfo> getOnHostDeallocationListener() { return EventListener.NULL; }
        @Override public EventListener<DatacenterToVmEventInfo> getOnVmCreationFailureListener() { return EventListener.NULL; }
        @Override public long getRam() { return 0; }
        @Override public long getSize(){ return 0; }
        @Override public List<VmStateHistoryEntry> getStateHistory() { return Collections.emptyList(); }
        @Override public double getTotalUtilizationOfCpu(double time) { return 0.0; }
        @Override public double getTotalUtilizationOfCpuMips(double time) { return 0.0; }
        @Override public String getUid(){ return ""; }
        @Override public int getBrokerId() { return -1; }
        @Override  public Vm setBroker(DatacenterBroker broker) { return Vm.NULL; }
        @Override public String getVmm() { return ""; }
        public boolean isCreated() { return false; }
        @Override public boolean isInMigration() { return false; }
        public void setCreated(boolean created){}
        @Override public Vm setBw(long bwCapacity) { return Vm.NULL; }
        @Override public void setHost(Host host) {}
        @Override public void setInMigration(boolean inMigration) {}
        @Override public Vm setOnHostAllocationListener(EventListener<HostToVmEventInfo> onHostAllocationListener) { return Vm.NULL; }
        @Override public Vm setOnHostDeallocationListener(EventListener<HostToVmEventInfo> onHostDeallocationListener) { return Vm.NULL; }
        @Override public Vm setOnVmCreationFailureListener(EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener) { return Vm.NULL; }
        @Override public Vm setRam(long ramCapacity) { return Vm.NULL; }
        @Override public Vm setSize(long size) { return Vm.NULL; }
        @Override public double updateVmProcessing(double currentTime, List<Double> mipsShare){ return 0.0; }
        @Override public Vm setCloudletScheduler(CloudletScheduler cloudletScheduler) { return Vm.NULL; }
        @Override public <R extends ResourceManageable> ResourceManageable getResource(Class<R> resourceClass) { return ResourceManageable.NULL; }
        @Override public EventListener<HostToVmEventInfo> getOnUpdateVmProcessingListener() { return EventListener.NULL; }
        @Override public Vm setOnUpdateVmProcessingListener(EventListener<HostToVmEventInfo> onUpdateVmProcessingListener) { return Vm.NULL; }
        @Override public int compareTo(Vm o) { return 0; }
        @Override public double getTotalMipsCapacity() { return 0.0; }
        @Override public void setFailed(boolean failed){}
        @Override public boolean isFailed() { return false; }
        @Override public Simulation getSimulation() { return Simulation.NULL; }
        @Override public Vm setSimulation(Simulation simulation) { return this; }
        @Override public String toString() { return "Vm.NULL"; }
    };
}
