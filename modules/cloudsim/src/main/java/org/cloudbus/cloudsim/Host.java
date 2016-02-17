/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.RawStorage;

/**
 * A Host is a Physical Machine (PM) inside a {@link Datacenter}. It is also called a Server.
 * It executes actions related to management of virtual machines (e.g., creation and destruction).
 * A host has a defined policy for provisioning memory and bw, as well as an allocation policy for
 * PEs to {@link Vm virtual machines}. 
 * A host is associated to a datacenter and can host virtual machines.
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class Host {
    /** @see #getId()  */
    private int id;
    
    /** @see #getStorage() */
    private final RawStorage storage;

    /** @see #getRamProvisioner()  */
    private ResourceProvisioner<Integer> ramProvisioner;

    /** @see #getBwProvisioner()  */
    private ResourceProvisioner<Long> bwProvisioner;

    /** @see #getVmScheduler()  */
    private VmScheduler vmScheduler;

    /** @see #getVmList()  */
    private final List<? extends Vm> vmList = new ArrayList<>();

    /** @see #getPeList()  */
    private List<? extends Pe> peList;

    /** @see #isFailed()  */
    private boolean failed;

    /** @see #getVmsMigratingIn()  */
    private final List<? extends Vm> vmsMigratingIn = new ArrayList<>();

    /** @see #getDatacenter()  */
    private Datacenter datacenter;

    /**
     * Instantiates a new Host.
     * 
     * @param id the host id
     * @param ramProvisioner the ram provisioner
     * @param bwProvisioner the bw provisioner
     * @param storageCapacity the storage capacity
     * @param peList the host's PEs list
     * @param vmScheduler the vm scheduler
     */
    public Host(
                int id,
                ResourceProvisioner<Integer> ramProvisioner,
                ResourceProvisioner<Long> bwProvisioner,
                long storageCapacity,
                List<? extends Pe> peList,
                VmScheduler vmScheduler) {
        setId(id);
        setRamProvisioner(ramProvisioner);
        setBwProvisioner(bwProvisioner);
        storage = new RawStorage(storageCapacity);
        setVmScheduler(vmScheduler);
        setPeList(peList);
        setFailed(false);
    }

    /**
     * Requests updating of cloudlets' processing in VMs running in this host.
     * 
     * @param currentTime the current time
     * @return expected time of completion of the next cloudlet in all VMs in this host or
     *         {@link Double#MAX_VALUE} if there is no future events expected in this host
     * @pre currentTime >= 0.0
     * @post $none
     * @todo there is an inconsistency between the return value of this method
     * and the individual call of {@link Vm#updateVmProcessing(double, java.util.List),
     * and consequently the {@link CloudletScheduler#updateVmProcessing(double, java.util.List)}.
     * The current method returns {@link Double#MAX_VALUE}  while the other ones
     * return 0. It has to be checked if there is a reason for this
     * difference.}
     */
    public double updateVmsProcessing(double currentTime) {
        double smallerTime = Double.MAX_VALUE;

        for (Vm vm : getVmList()) {
            double time = vm.updateVmProcessing(
                currentTime, getVmScheduler().getAllocatedMipsForVm(vm));
            if (time > 0.0 && time < smallerTime) {
                smallerTime = time;
            }
        }

        return smallerTime;
    }

    /**
     * Adds a VM migrating into the current host.
     * 
     * @param vm the vm
     */
    public void addMigratingInVm(Vm vm) {
        vm.setInMigration(true);

        if (!getVmsMigratingIn().contains(vm)) {
            if (!storage.isResourceAmountAvailable(vm.getSize())) {
                Log.printConcatLine("[VmScheduler.addMigratingInVm] Allocation of VM #", vm.getId(), " to Host #",
                                getId(), " failed by storage");
                System.exit(0);
            }

            if (!getRamProvisioner().allocateResourceForVm(vm, vm.getCurrentRequestedRam())) {
                Log.printConcatLine("[VmScheduler.addMigratingInVm] Allocation of VM #", vm.getId(), " to Host #",
                                getId(), " failed by RAM");
                System.exit(0);
            }

            if (!getBwProvisioner().allocateResourceForVm(vm, vm.getCurrentRequestedBw())) {
                Log.printLine("[VmScheduler.addMigratingInVm] Allocation of VM #" + vm.getId() + " to Host #"
                                + getId() + " failed by BW");
                System.exit(0);
            }

            getVmScheduler().getVmsMigratingIn().add(vm.getUid());
            if (!getVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
                Log.printLine("[VmScheduler.addMigratingInVm] Allocation of VM #" + vm.getId() + " to Host #"
                                + getId() + " failed by MIPS");
                System.exit(0);
            }

            getStorage().allocateResource(vm.getSize());

            getVmsMigratingIn().add(vm);
            getVmList().add(vm);
            updateVmsProcessing(CloudSim.clock());
            vm.getHost().updateVmsProcessing(CloudSim.clock());
        }
    }

    /**
     * Removes a migrating in vm.
     * 
     * @param vm the vm
     */
    public void removeMigratingInVm(Vm vm) {
            vmDeallocate(vm);
            getVmsMigratingIn().remove(vm);
            getVmList().remove(vm);
            getVmScheduler().getVmsMigratingIn().remove(vm.getUid());
            vm.setInMigration(false);
    }

    /**
     * Reallocate VMs migrating into the host. Gets the VM in the migrating in queue
     * and allocate them on the host.
     */
    public void reallocateMigratingInVms() {
            for (Vm vm : getVmsMigratingIn()) {
                    if (!getVmList().contains(vm)) {
                            getVmList().add(vm);
                    }
                    if (!getVmScheduler().getVmsMigratingIn().contains(vm.getUid())) {
                            getVmScheduler().getVmsMigratingIn().add(vm.getUid());
                    }
                    getRamProvisioner().allocateResourceForVm(vm, vm.getCurrentRequestedRam());
                    getBwProvisioner().allocateResourceForVm(vm, vm.getCurrentRequestedBw());
                    getVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips());
                    getStorage().allocateResource(vm.getSize());
            }
    }

    /**
     * Checks if the host is suitable for vm. If it has enough resources
     * to attend the VM.
     * 
     * @param vm the vm
     * @return true, if is suitable for vm
     */
    public boolean isSuitableForVm(Vm vm) {
        return (getVmScheduler().getPeCapacity() >= vm.getCurrentRequestedMaxMips()
            && getVmScheduler().getAvailableMips() >= vm.getCurrentRequestedTotalMips()
            && getRamProvisioner().isSuitableForVm(vm, vm.getCurrentRequestedRam()) 
            && getBwProvisioner().isSuitableForVm(vm, vm.getCurrentRequestedBw()));
    }

    /**
     * Try to allocate resources to a new VM in the Host.
     * 
     * @param vm Vm being started
     * @return $true if the VM could be started in the host; $false otherwise
     * @pre $none
     * @post $none
     */
    public boolean vmCreate(Vm vm) {
            if (!storage.isResourceAmountAvailable(vm.getSize())) {
                    Log.printConcatLine("[VmScheduler.vmCreate] Allocation of VM #", vm.getId(), " to Host #", getId(),
                                    " failed by storage");
                    return false;
            }

            if (!getRamProvisioner().allocateResourceForVm(vm, vm.getCurrentRequestedRam())) {
                    Log.printConcatLine("[VmScheduler.vmCreate] Allocation of VM #", vm.getId(), " to Host #", getId(),
                                    " failed by RAM");
                    return false;
            }

            if (!getBwProvisioner().allocateResourceForVm(vm, vm.getCurrentRequestedBw())) {
                    Log.printConcatLine("[VmScheduler.vmCreate] Allocation of VM #", vm.getId(), " to Host #", getId(),
                                    " failed by BW");
                    getRamProvisioner().deallocateResourceForVm(vm);
                    return false;
            }

            if (!getVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
                    Log.printConcatLine("[VmScheduler.vmCreate] Allocation of VM #", vm.getId(), " to Host #", getId(),
                                    " failed by MIPS");
                    getRamProvisioner().deallocateResourceForVm(vm);
                    getBwProvisioner().deallocateResourceForVm(vm);
                    return false;
            }

            getStorage().allocateResource(vm.getSize());
            getVmList().add(vm);
            vm.setHost(this);
            return true;
    }

    /**
     * Destroys a VM running in the host.
     * 
     * @param vm the VM
     * @pre $none
     * @post $none
     * @todo The methods vmDestroy, vmDestroyAll, vmDeallocate, vmDeallocateAll
     * appear to be just duplicated code.
     */
    public void vmDestroy(Vm vm) {
        if (vm != null) {
            vmDeallocate(vm);
            getVmList().remove(vm);
            vm.setHost(null);
        }
    }

    /**
     * Destroys all VMs running in the host.
     * 
     * @pre $none
     * @post $none
     */
    public void vmDestroyAll() {
        vmDeallocateAll();
        for (Vm vm : getVmList()) {
            vm.setHost(null);
            getStorage().deallocateResource(vm.getSize());
        }
        getVmList().clear();
    }

    /**
     * Deallocate all resources of a VM.
     * 
     * @param vm the VM
     */
    protected void vmDeallocate(Vm vm) {
        getRamProvisioner().deallocateResourceForVm(vm);
        getBwProvisioner().deallocateResourceForVm(vm);
        getVmScheduler().deallocatePesForVm(vm);
        getStorage().deallocateResource(vm.getSize());
    }

    /**
     * Deallocate all resources of all VMs.
     */
    protected void vmDeallocateAll() {
        getRamProvisioner().deallocateResourceForAllVms();
        getBwProvisioner().deallocateResourceForAllVms();
        getVmScheduler().deallocatePesForAllVms();
    }

    /**
     * Gets a VM by its id and user.
     * 
     * @param vmId the vm id
     * @param userId ID of VM's owner
     * @return the virtual machine object, $null if not found
     * @pre $none
     * @post $none
     */
    public Vm getVm(int vmId, int userId) {
        for (Vm vm : getVmList()) {
            if (vm.getId() == vmId && vm.getUserId() == userId) {
                return vm;
            }
        }
        return null;
    }

    /**
     * Gets the PEs number.
     * 
     * @return the pes number
     */
    public int getNumberOfPes() {
        return getPeList().size();
    }

    /**
     * Gets the free pes number.
     * 
     * @return the free pes number
     */
    public int getNumberOfFreePes() {
        return PeList.getNumberOfFreePes(getPeList());
    }

    /**
     * Gets the total mips.
     * 
     * @return the total mips
     */
    public int getTotalMips() {
        return PeList.getTotalMips(getPeList());
    }

    /**
     * Allocates PEs for a VM.
     * 
     * @param vm the vm
     * @param mipsShare the list of MIPS share to be allocated to the VM
     * @return $true if this policy allows a new VM in the host, $false otherwise
     * @pre $none
     * @post $none
     */
    public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) {
        return getVmScheduler().allocatePesForVm(vm, mipsShare);
    }

    /**
     * Releases PEs allocated to a VM.
     * 
     * @param vm the vm
     * @pre $none
     * @post $none
     */
    public void deallocatePesForVm(Vm vm) {
        getVmScheduler().deallocatePesForVm(vm);
    }

    /**
     * Gets the MIPS share of each Pe that is allocated to a given VM.
     * 
     * @param vm the vm
     * @return an array containing the amount of MIPS of each pe that is available to the VM
     * @pre $none
     * @post $none
     */
    public List<Double> getAllocatedMipsForVm(Vm vm) {
        return getVmScheduler().getAllocatedMipsForVm(vm);
    }

    /**
     * Gets the total allocated MIPS for a VM along all its PEs.
     * 
     * @param vm the vm
     * @return the allocated mips for vm
     */
    public double getTotalAllocatedMipsForVm(Vm vm) {
        return getVmScheduler().getTotalAllocatedMipsForVm(vm);
    }

    /**
     * Returns the maximum available MIPS among all the PEs of the host.
     * 
     * @return max mips
     */
    public double getMaxAvailableMips() {
        return getVmScheduler().getMaxAvailableMips();
    }

    /**
     * Gets the total free MIPS available at the host.
     * 
     * @return the free mips
     */
    public double getAvailableMips() {
        return getVmScheduler().getAvailableMips();
    }

    /**
     * Gets the host bw capacity.
     * 
     * @return the host bw capacity
     * @pre $none
     * @post $result > 0
     */
    public long getBw() {
        return getBwProvisioner().getCapacity();
    }

    /**
     * Gets the host memory capacity.
     * 
     * @return the host memory capacity
     * @pre $none
     * @post $result > 0
     */
    public int getRam() {
        return getRamProvisioner().getCapacity();
    }

    /**
     * Gets the host storage capacity.
     * 
     * @return the host storage capacity
     * @pre $none
     * @post $result >= 0
     */
    public long getStorageCapacity  () {
        return getStorage().getCapacity();
    }

    /**
     * Gets the host id.
     * 
     * @return the host id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the host id.
     * 
     * @param id the new host id
     */
    protected final void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ram provisioner.
     * 
     * @return the ram provisioner
     */
    public ResourceProvisioner<Integer> getRamProvisioner() {
        return ramProvisioner;
    }

    /**
     * Sets the ram provisioner.
     * 
     * @param ramProvisioner the new ram provisioner
     */
    protected final void setRamProvisioner(ResourceProvisioner<Integer> ramProvisioner) {
        this.ramProvisioner = ramProvisioner;
    }

    /**
     * Gets the bandwidth(BW) provisioner.
     * 
     * @return the bw provisioner
     */
    public ResourceProvisioner<Long> getBwProvisioner() {
        return bwProvisioner;
    }

    /**
     * Sets the bandwidth(BW) provisioner.
     * 
     * @param bwProvisioner the new bw provisioner
     */
    protected final void setBwProvisioner(ResourceProvisioner<Long> bwProvisioner) {
        this.bwProvisioner = bwProvisioner;
    }

    /**
     * Gets the policy for allocation of host PEs to VMs in order to schedule VM execution.
     * 
     * @return the VM scheduler
     * @see VmScheduler
     */
    public VmScheduler getVmScheduler() {
        return vmScheduler;
    }

    /**
     * Sets the policy for allocation of host PEs to VMs in order to schedule VM execution.
     * 
     * @param vmScheduler the vm scheduler
     */
    protected final void setVmScheduler(VmScheduler vmScheduler) {
        this.vmScheduler = vmScheduler;
    }

    /**
     * Gets the Processing Elements (PEs) of the host, that
     * represent its CPU cores and thus, its processing capacity.
     * 
     * @return the pe list
     */
    public List <? extends Pe> getPeList() {
        return peList;
    }

    /**
     * Sets the pe list.
     * 
     * @param <T> the generic type
     * @param peList the new pe list
     */
    protected final <T extends Pe> void setPeList(List<T> peList) {
        this.peList = peList;
    }

    /**
     * Gets the list of VMs assigned to the host.
     * 
     * @param <T>
     * @return the vm list
     */
    public <T extends Vm> List<T> getVmList() {
        return (List<T>) vmList;
    }
    
    /**
     * Checks if the host is working properly or has failed.
     * 
     * @return true, if the host PEs have failed; false otherwise
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * Sets the status of all host PEs to FAILED. NOTE: <tt>resName</tt> is used for debugging
     * purposes, which is <b>ON</b> by default. Use {@link #setFailed(boolean)} if you do not want
     * this information.
     * 
     * @param resName the name of the resource
     * @param failed the failed
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setFailed(String resName, boolean failed) {
        // all the PEs are failed (or recovered, depending on fail)
        this.failed = failed;
        PeList.setStatusFailed(getPeList(), resName, getId(), failed);
        return true;
    }

    /**
     * Sets the PEs of the host to a FAILED status.
     * 
     * @param failed the failed
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public final boolean setFailed(boolean failed) {
        // all the PEs are failed (or recovered, depending on fail)
        this.failed = failed;
        PeList.setStatusFailed(getPeList(), failed);
        return true;
    }

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
    public boolean setPeStatus(int peId, Pe.Status status) {
        return PeList.setPeStatus(getPeList(), peId, status);
    }

    /**
     * Gets the list of VMs migrating into this host.
     * 
     * @param <T>
     * @return the vms migrating in
     */
    public <T extends Vm> List<T> getVmsMigratingIn() {
        return (List<T>) vmsMigratingIn;
    }

    /**
     * Gets the datacenter where the host is placed.
     * 
     * @return the data center of the host
     */
    public Datacenter getDatacenter() {
        return datacenter;
    }

    /**
     * Sets the datacenter where the host is placed.
     * 
     * @param datacenter the new data center to move the host
     */
    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    /**
     * Gets the storage device of the host.
     * @return the storage device
     */
    protected RawStorage getStorage() {
        return storage;
    }
}
