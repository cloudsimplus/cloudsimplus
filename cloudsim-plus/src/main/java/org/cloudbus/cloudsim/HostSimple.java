/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.VmScheduler;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.RawStorage;

/**
 * A Host class that implements the most basic features of a Physical Machine
 * (PM) inside a {@link Datacenter}. It executes actions related to management
 * of virtual machines (e.g., creation and destruction). A host has a defined
 * policy for provisioning memory and bw, as well as an allocation policy for
 * PEs to {@link Vm virtual machines}. A host is associated to a datacenter and
 * can host virtual machines.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class HostSimple implements Host {

    /**
     * @see #getId()
     */
    private int id;

    /**
     * @see #getStorage()
     */
    private final RawStorage storage;

    /**
     * @see #getRamProvisioner()
     */
    private ResourceProvisioner<Integer> ramProvisioner;

    /**
     * @see #getBwProvisioner()
     */
    private ResourceProvisioner<Long> bwProvisioner;

    /**
     * @see #getVmScheduler()
     */
    private VmScheduler vmScheduler;

    /**
     * @see #getVmList()
     */
    private final List<Vm> vmList = new ArrayList<>();

    /**
     * @see #getPeList()
     */
    private List<Pe> peList;

    /**
     * @see #isFailed()
     */
    private boolean failed;

    /**
     * @see #getVmsMigratingIn()
     */
    private final List<Vm> vmsMigratingIn = new ArrayList<>();

    /**
     * @see #getDatacenter()
     */
    private Datacenter datacenter;

    /**
     * @see #getOnUpdateVmsProcessingListener()
     */
    private EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener;

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
    public HostSimple(
            int id,
            ResourceProvisioner<Integer> ramProvisioner,
            ResourceProvisioner<Long> bwProvisioner,
            long storageCapacity,
            List<Pe> peList,
            VmScheduler vmScheduler) {
        setId(id);
        setRamProvisioner(ramProvisioner);
        setBwProvisioner(bwProvisioner);
        storage = new RawStorage(storageCapacity);
        setVmScheduler(vmScheduler);
        setPeList(peList);
        setFailed(false);
        onUpdateVmsProcessingListener = EventListener.NULL;
    }

    @Override
    public double updateVmsProcessing(double currentTime) {
        double completionTimeOfNextFinishingCloudlet = Double.MAX_VALUE;

        for (Vm vm : getVmList()) {
            double time = vm.updateVmProcessing(
                    currentTime, getVmScheduler().getAllocatedMipsForVm(vm));
            if (time < completionTimeOfNextFinishingCloudlet) {
                completionTimeOfNextFinishingCloudlet = time;
            }
        }

        HostUpdatesVmsProcessingEventInfo eventInfo = 
                new HostUpdatesVmsProcessingEventInfo(currentTime, this);
        eventInfo.setCompletionTimeOfNextFinishingCloudlet(completionTimeOfNextFinishingCloudlet);
        onUpdateVmsProcessingListener.update(eventInfo);

        return completionTimeOfNextFinishingCloudlet;
    }

    @Override
    public void addMigratingInVm(Vm vm) {
        if (!getVmsMigratingIn().contains(vm)) {
            if (!storage.isResourceAmountAvailable(vm.getSize())) {
                throw new RuntimeException(
                    String.format(
                        "[VmScheduler.addMigratingInVm] Allocation of VM #%d to Host #%d" +
                    " failed by storage", vm.getId(), getId()));
            }

            if (!getRamProvisioner().allocateResourceForVm(vm, vm.getCurrentRequestedRam())) {
                throw new RuntimeException(
                    String.format(
                        "[VmScheduler.addMigratingInVm] Allocation of VM #%d to Host #%d" +
                        " failed by RAM", vm.getId(), getId()));
            }

            if (!getBwProvisioner().allocateResourceForVm(vm, vm.getCurrentRequestedBw())) {
                throw new RuntimeException(
                    String.format(
                        "[VmScheduler.addMigratingInVm] Allocation of VM #%d to Host #%d" +
                        " failed by BW", vm.getId(), getId()));
            }

            getVmScheduler().getVmsMigratingIn().add(vm.getUid());
            vm.setInMigration(true);
            if (!getVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
                getVmScheduler().getVmsMigratingIn().remove(vm.getUid());
                vm.setInMigration(false);
                throw new RuntimeException(
                    String.format(
                        "[VmScheduler.addMigratingInVm] Allocation of VM #%d to Host #%d" +
                        " failed by MIPS", vm.getId(),  getId()));
            }
            
            getStorage().allocateResource(vm.getSize());

            getVmsMigratingIn().add(vm);
            updateVmsProcessing(CloudSim.clock());
            vm.getHost().updateVmsProcessing(CloudSim.clock());
        }
    }

    @Override
    public void removeMigratingInVm(Vm vm) {
        vmDeallocate(vm);
        getVmsMigratingIn().remove(vm);
        getVmList().remove(vm);
        getVmScheduler().getVmsMigratingIn().remove(vm.getUid());
        vm.setInMigration(false);
    }

    @Override
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

    @Override
    public boolean isSuitableForVm(Vm vm) {
        return (getVmScheduler().getPeCapacity() >= vm.getCurrentRequestedMaxMips()
                && getVmScheduler().getAvailableMips() >= vm.getCurrentRequestedTotalMips()
                && getRamProvisioner().isSuitableForVm(vm, vm.getCurrentRequestedRam())
                && getBwProvisioner().isSuitableForVm(vm, vm.getCurrentRequestedBw()));
    }

    @Override
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

    @Override
    public void vmDestroy(Vm vm) {
        if (vm != null) {
            vmDeallocate(vm);
            getVmList().remove(vm);
            vm.setHost(null);
        }
    }

    @Override
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

    @Override
    public Vm getVm(int vmId, int userId) {
        for (Vm vm : getVmList()) {
            if (vm.getId() == vmId && vm.getUserId() == userId) {
                return vm;
            }
        }
        return null;
    }

    @Override
    public int getNumberOfPes() {
        return getPeList().size();
    }

    @Override
    public int getNumberOfFreePes() {
        return PeList.getNumberOfFreePes(getPeList());
    }

    @Override
    public int getTotalMips() {
        return PeList.getTotalMips(getPeList());
    }

    @Override
    public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) {
        return getVmScheduler().allocatePesForVm(vm, mipsShare);
    }

    @Override
    public void deallocatePesForVm(Vm vm) {
        getVmScheduler().deallocatePesForVm(vm);
    }

    @Override
    public List<Double> getAllocatedMipsForVm(Vm vm) {
        return getVmScheduler().getAllocatedMipsForVm(vm);
    }

    @Override
    public double getTotalAllocatedMipsForVm(Vm vm) {
        return getVmScheduler().getTotalAllocatedMipsForVm(vm);
    }

    @Override
    public double getMaxAvailableMips() {
        return getVmScheduler().getMaxAvailableMips();
    }

    @Override
    public double getAvailableMips() {
        return getVmScheduler().getAvailableMips();
    }

    @Override
    public long getBwCapacity() {
        return getBwProvisioner().getCapacity();
    }

    @Override
    public int getRamCapacity() {
        return getRamProvisioner().getCapacity();
    }

    @Override
    public long getStorageCapacity() {
        return getStorage().getCapacity();
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public VmScheduler getVmScheduler() {
        return vmScheduler;
    }

    /**
     * Sets the policy for allocation of host PEs to VMs in order to schedule VM
     * execution.
     *
     * @param vmScheduler the vm scheduler
     */
    protected final void setVmScheduler(VmScheduler vmScheduler) {
        this.vmScheduler = vmScheduler;
    }

    @Override
    public List<Pe> getPeList() {
        return peList;
    }

    /**
     * Sets the pe list.
     *
     * @param peList the new pe list
     */
    protected final void setPeList(List<Pe> peList) {
        this.peList = peList;
    }

    @Override
    public <T extends Vm> List<T> getVmList() {
        return (List<T>) vmList;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public boolean setFailed(String resName, boolean failed) {
        // all the PEs are failed (or recovered, depending on fail)
        this.failed = failed;
        PeList.setStatusFailed(getPeList(), resName, getId(), failed);
        return true;
    }

    @Override
    public final boolean setFailed(boolean failed) {
        // all the PEs are failed (or recovered, depending on fail)
        this.failed = failed;
        PeList.setStatusFailed(getPeList(), failed);
        return true;
    }

    @Override
    public boolean setPeStatus(int peId, Pe.Status status) {
        return PeList.setPeStatus(getPeList(), peId, status);
    }

    @Override
    public <T extends Vm> List<T> getVmsMigratingIn() {
        return (List<T>)vmsMigratingIn;
    }

    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }

    @Override
    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    /**
     * Gets the storage device of the host.
     *
     * @return the storage device
     */
    protected RawStorage getStorage() {
        return storage;
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }

    @Override
    public EventListener<HostUpdatesVmsProcessingEventInfo> getOnUpdateVmsProcessingListener() {
        return onUpdateVmsProcessingListener;
    }

    @Override
    public void setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener) {
        if (onUpdateVmsProcessingListener == null) {
            onUpdateVmsProcessingListener = EventListener.NULL;
        }

        this.onUpdateVmsProcessingListener = onUpdateVmsProcessingListener;
    }

    @Override
    public long getAvailableStorage() {
        return getStorage().getAvailableResource();
    }
}
