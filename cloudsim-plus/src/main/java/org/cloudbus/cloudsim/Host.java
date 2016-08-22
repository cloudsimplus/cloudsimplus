package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.core.Identificable;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.VmScheduler;
import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;

/**
 * An interface to be implemented by each class that provides
 * Physical Machines (Hosts) features. 
 * The interface implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException} 
 * when using the {@link Host#NULL} object instead
 * of attributing {@code null} to {@link Host} variables.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface Host extends Identificable {

    /**
     * Adds a VM migrating into the current host.
     *
     * @param vm the vm
     */
    void addMigratingInVm(Vm vm);

    /**
     * Allocates PEs for a VM.
     *
     * @param vm the vm
     * @param mipsShare the list of MIPS share to be allocated to the VM
     * @return $true if this policy allows a new VM in the host, $false otherwise
     * @pre $none
     * @post $none
     */
    boolean allocatePesForVm(Vm vm, List<Double> mipsShare);

    /**
     * Releases PEs allocated to a VM.
     *
     * @param vm the vm
     * @pre $none
     * @post $none
     */
    void deallocatePesForVm(Vm vm);

    /**
     * Gets the MIPS share of each Pe that is allocated to a given VM.
     *
     * @param vm the vm
     * @return an array containing the amount of MIPS of each pe that is available to the VM
     * @pre $none
     * @post $none
     */
    List<Double> getAllocatedMipsForVm(Vm vm);

    /**
     * Gets the total free MIPS available at the host.
     *
     * @return the free mips
     */
    double getAvailableMips();

    /**
     * Gets the total free storage available at the host.
     *
     * @return the free storage
     */
    long getAvailableStorage();

    /**
     * Gets the host bw capacity.
     *
     * @return the host bw capacity
     * @pre $none
     * @post $result > 0
     */
    long getBwCapacity();

    /**
     * Gets the bandwidth(BW) provisioner.
     *
     * @return the bw provisioner
     */
    ResourceProvisioner<Long> getBwProvisioner();

    /**
     * Gets the datacenter where the host is placed.
     *
     * @return the data center of the host
     */
    Datacenter getDatacenter();

    /**
     * Returns the maximum available MIPS among all the PEs of the host.
     *
     * @return max mips
     */
    double getMaxAvailableMips();

    /**
     * Gets the free pes number.
     *
     * @return the free pes number
     */
    int getNumberOfFreePes();

    /**
     * Gets the PEs number.
     *
     * @return the pes number
     */
    int getNumberOfPes();

    /**
     * Gets the Processing Elements (PEs) of the host, that
     * represent its CPU cores and thus, its processing capacity.
     *
     * @return the pe list
     */
    List<Pe> getPeList();

    /**
     * Gets the host memory capacity.
     *
     * @return the host memory capacity
     * @pre $none
     * @post $result > 0
     */
    int getRamCapacity();

    /**
     * Gets the ram provisioner.
     *
     * @return the ram provisioner
     */
    ResourceProvisioner<Integer> getRamProvisioner();

    /**
     * Gets the host storage capacity.
     *
     * @return the host storage capacity
     * @pre $none
     * @post $result >= 0
     */
    long getStorageCapacity();

    /**
     * Gets the total allocated MIPS for a VM along all its PEs.
     *
     * @param vm the vm
     * @return the allocated mips for vm
     */
    double getTotalAllocatedMipsForVm(Vm vm);

    /**
     * Gets the total mips.
     *
     * @return the total mips
     */
    int getTotalMips();

    /**
     * Gets a VM by its id and user.
     *
     * @param vmId the vm id
     * @param userId ID of VM's owner
     * @return the virtual machine object, $null if not found
     * @pre $none
     * @post $none
     */
    Vm getVm(int vmId, int userId);

    /**
     * Gets the list of VMs assigned to the host.
     *
     * @param <T> The generic type
     * @return the vm list
     */
    <T extends Vm> List<T> getVmList();

    /**
     * Gets the policy for allocation of host PEs to VMs in order to schedule VM execution.
     *
     * @return the VM scheduler
     * @see VmSchedulerAbstract
     */
    VmScheduler getVmScheduler();

    /**
     * Gets the list of VMs migrating into this host.
     * 
     * @param <T> the generic type
     * @return the vms migrating in
     */
    <T extends Vm> List<T> getVmsMigratingIn();

    /**
     * Checks if the host is working properly or has failed.
     *
     * @return true, if the host PEs have failed; false otherwise
     */
    boolean isFailed();

    /**
     * Checks if the host is suitable for vm. If it has enough resources
     * to attend the VM.
     *
     * @param vm the vm
     * @return true, if is suitable for vm
     */
    boolean isSuitableForVm(Vm vm);

    /**
     * Reallocate VMs migrating into the host. Gets the VM in the migrating in queue
     * and allocate them on the host.
     */
    void reallocateMigratingInVms();

    /**
     * Removes a migrating in vm.
     *
     * @param vm the vm
     */
    void removeMigratingInVm(Vm vm);

    /**
     * Sets the datacenter where the host is placed.
     *
     * @param datacenter the new data center to move the host
     */
    void setDatacenter(Datacenter datacenter);

    /**
     * Sets the status of all host PEs to FAILED. NOTE: <tt>resName</tt> is used for debugging
     * purposes, which is <b>ON</b> by default. Use {@link #setFailed(boolean)} if you do not want
     * this information.
     *
     * @param resName the name of the resource
     * @param failed the failed
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    boolean setFailed(String resName, boolean failed);

    /**
     * Sets the PEs of the host to a FAILED status.
     *
     * @param failed the failed
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    boolean setFailed(boolean failed);

    /**
     * Sets the particular Pe status on the host.
     *
     * @param peId the pe id
     * @param status the new Pe status
     * @return <tt>true</tt> if the Pe status has changed, <tt>false</tt> otherwise (Pe id might not
     *         be exist)
     * @pre peID >= 0
     * @post $none
     */
    boolean setPeStatus(int peId, Pe.Status status);

    /**
     * Requests updating of cloudlets' processing in VMs running in this host.
     *
     * @param currentTime the current time
     * @return expected time of completion of the next cloudlet in all VMs in this host or
     *         {@link Double#MAX_VALUE} if there is no future events expected in this host
     * @pre currentTime >= 0.0
     * @post $none
     */
    double updateVmsProcessing(double currentTime);

    /**
     * Try to allocate resources to a new VM in the Host.
     *
     * @param vm Vm being started
     * @return $true if the VM could be started in the host; $false otherwise
     * @pre $none
     * @post $none
     */
    boolean vmCreate(Vm vm);

    /**
     * Destroys a VM running in the host.
     *
     * @param vm the VM
     * @pre $none
     * @post $none
     * @todo The methods vmDestroy, vmDestroyAll, vmDeallocate, vmDeallocateAll
     * appear to be just duplicated code.
     */
    void vmDestroy(Vm vm);

    /**
     * Destroys all VMs running in the host.
     *
     * @pre $none
     * @post $none
     */
    void vmDestroyAll();
    
    /**
     * Gets the listener object that will be notified every time when 
     * the host updates the processing of all its {@link Vm VMs}.
     * 
     * @return the onUpdateVmsProcessingListener
     * @see #updateVmsProcessing(double) 
     */
    EventListener<HostUpdatesVmsProcessingEventInfo> getOnUpdateVmsProcessingListener();

    /**
     * Sets the listener object that will be notified every time when 
     * the host updates the processing of all its {@link Vm VMs}.
     *
     * @param onUpdateVmsProcessingListener the onUpdateVmsProcessingListener to set
     * @see #updateVmsProcessing(double) 
     */
    void setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener);    
    
    /**
     * A property that implements the Null Object Design Pattern for {@link Host}
     * objects.
     */
    Host NULL = new Host(){
        @Override public void addMigratingInVm(Vm vm) {}
        @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) { return false;}
        @Override public void deallocatePesForVm(Vm vm) {}
        @Override public List<Double> getAllocatedMipsForVm(Vm vm) { return Collections.emptyList(); }
        @Override public double getAvailableMips() { return 0; }
        @Override public long getBwCapacity() { return 0; }
        @Override public ResourceProvisioner<Long> getBwProvisioner() { return ResourceProvisioner.NULL_LONG; }
        @Override public Datacenter getDatacenter() { return Datacenter.NULL; }
        @Override public int getId() { return 0; }
        @Override public double getMaxAvailableMips() { return 0.0; }
        @Override public int getNumberOfFreePes() { return 0; }
        @Override public int getNumberOfPes() { return 0; }
        @Override public List<Pe> getPeList() { return Collections.emptyList(); }
        @Override public int getRamCapacity() { return 0; }
        @Override public ResourceProvisioner<Integer> getRamProvisioner() { return ResourceProvisioner.NULL_INT; }
        @Override public long getStorageCapacity() { return 0L; }
        @Override public double getTotalAllocatedMipsForVm(Vm vm) { return 0.0; }
        @Override public int getTotalMips() { return 0; }
        @Override public Vm getVm(int vmId, int userId) { return Vm.NULL; }
        @Override public List<Vm> getVmList() { return Collections.emptyList(); }
        @Override public VmScheduler getVmScheduler() {return VmScheduler.NULL; }
        @Override public List<Vm> getVmsMigratingIn() { return Collections.EMPTY_LIST; }
        @Override public boolean isFailed() { return false; }
        @Override public boolean isSuitableForVm(Vm vm) { return false; }
        @Override public void reallocateMigratingInVms() {}
        @Override public void removeMigratingInVm(Vm vm) {}
        @Override public void setDatacenter(Datacenter datacenter) {}
        @Override public boolean setFailed(String resName, boolean failed) { return false; }
        @Override public boolean setFailed(boolean failed) { return false; }
        @Override public boolean setPeStatus(int peId, Pe.Status status) { return false; }
        @Override public double updateVmsProcessing(double currentTime) { return 0.0; }
        @Override public boolean vmCreate(Vm vm) { return false; }
        @Override public void vmDestroy(Vm vm) {}
        @Override public void vmDestroyAll() {}
        @Override public EventListener<HostUpdatesVmsProcessingEventInfo> getOnUpdateVmsProcessingListener() { return EventListener.NULL; }
        @Override public void setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener) {}
        @Override public long getAvailableStorage() { return 0L; }
    };
}
