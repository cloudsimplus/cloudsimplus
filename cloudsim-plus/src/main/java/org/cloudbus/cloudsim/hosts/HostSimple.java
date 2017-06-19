/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;

import java.util.*;

import org.cloudbus.cloudsim.core.Simulation;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;

import static java.util.stream.Collectors.toList;

/**
 * A Host class that implements the most basic features of a Physical Machine
 * (PM) inside a {@link Datacenter}. It executes actions related to management
 * of virtual machines (e.g., creation and destruction). A host has a defined
 * policy for provisioning memory and bw, as well as an allocation policy for
 * PEs to {@link Vm virtual machines}. A host is associated to a Datacenter and
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

    private Ram ram;

    private Bandwidth bw;

    /**
     * @see #getStorage()
     */
    private Storage storage;

    /**
     * @see #getRamProvisioner()
     */
    private ResourceProvisioner ramProvisioner;

    /**
     * @see #getBwProvisioner()
     */
    private ResourceProvisioner bwProvisioner;

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
    private final Set<Vm> vmsMigratingIn;

    /**
     * @see #getVmsMigratingOut()
     */
    private final Set<Vm> vmsMigratingOut;


    /**
     * @see #getDatacenter()
     */
    private Datacenter datacenter;

    /**
     * @see Host#removeOnUpdateProcessingListener(EventListener)
     */
    private Set<EventListener<HostUpdatesVmsProcessingEventInfo>> onUpdateProcessingListeners;

    /**
     * @see #getSimulation()
     */
    private Simulation simulation;

    /**
     * A list of resources the VM has, that represent virtual resources corresponding to physical resources
     * from the Host where the VM is placed.
     *
     * @see #getResource(Class)
     */
    private List<ResourceManageable> resources;
    private List<ResourceProvisioner> provisioners;
    private boolean active;
    private List<Vm> vmCreatedList;

    /**
     * Creates a Host without a pre-defined ID.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     * @see #setId(int)
     */
    public HostSimple(long ram, long bw, long storage, List<Pe> peList) {
        this.setId(-1);
        this.setActive(true);
        this.setSimulation(Simulation.NULL);

        this.ram = new Ram(ram);
        this.bw = new Bandwidth(bw);
        this.setStorage(storage);
        this.setRamProvisioner(ResourceProvisioner.NULL);
        this.setBwProvisioner(ResourceProvisioner.NULL);

        this.setVmScheduler(VmScheduler.NULL);
        this.setPeList(peList);
        this.setFailed(false);
        this.setDatacenter(Datacenter.NULL);
        this.onUpdateProcessingListeners = new HashSet<>();
        this.resources = new ArrayList();
        this.vmCreatedList = new ArrayList();
        this.provisioners = new ArrayList();
        this.vmsMigratingIn = new HashSet<>();
        this.vmsMigratingOut = new HashSet<>();
    }

    @Override
    public double getTotalMipsCapacity() {
        return peList.stream()
                .filter(Pe::isWorking)
                .mapToDouble(Pe::getCapacity)
                .sum();
    }

    /**
     * Creates a Host with the given parameters.
     *
     * @param id the host id
     * @param ramProvisioner the ram provisioner with capacity in Megabytes
     * @param bwProvisioner the bw provisioner with capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's PEs list
     * @param vmScheduler the vm scheduler
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public HostSimple(
            int id,
            ResourceProvisioner ramProvisioner,
            ResourceProvisioner bwProvisioner,
            long storage,
            List<Pe> peList,
            VmScheduler vmScheduler)
    {
        this(ramProvisioner.getCapacity(), bwProvisioner.getCapacity(), storage, peList);
        setRamProvisioner(ramProvisioner);
        setBwProvisioner(bwProvisioner);
        setPeList(peList);
        setVmScheduler(vmScheduler);
    }

    @Override
    public double updateProcessing(double currentTime) {
        final double nextSimulationTime =
            vmList.stream()
                .mapToDouble(vm -> vm.updateProcessing(currentTime, vmScheduler.getAllocatedMips(vm)))
                .min()
                .orElse(Double.MAX_VALUE);

        notifyOnUpdateProcessingListeners(nextSimulationTime);
        return nextSimulationTime;
    }

    private void notifyOnUpdateProcessingListeners(double nextSimulationTime) {
        final HostUpdatesVmsProcessingEventInfo info = HostUpdatesVmsProcessingEventInfo.of(this, nextSimulationTime);
        onUpdateProcessingListeners.forEach(l -> l.update(info));
    }

    @Override
    public boolean removeVmMigratingIn(Vm vm){
        return vmsMigratingIn.remove(vm);
    }

    @Override
    public boolean createVm(Vm vm) {
        final boolean result = createVmInternal(vm);
        if(result) {
            vmCreatedList.add(vm);
            vm.setHost(this);
            vm.notifyOnHostAllocationListeners();
            if(vm.getStartTime() < 0) {
               vm.setStartTime(getSimulation().clock());
            }
        }

        return result;
    }

    @Override
    public boolean createTemporaryVm(Vm vm) {
        return createVmInternal(vm);
    }

    private boolean createVmInternal(Vm vm) {
        if(!allocateResourcesForVm(vm, false)){
            return false;
        }

        vmList.add(vm);
        return true;
    }

    /**
     * Try to allocate all resources that a VM requires (Storage, RAM, BW and MIPS) to be placed at this Host.
     *
     * @param vm the VM to try allocating resources to
     * @param inMigration If the VM is migrating into the Host or it is being just created for the first time
     * @return true if the Vm was placed into the host, false if the Host doesn't have enough resources to allocate the Vm
     */
    private boolean allocateResourcesForVm(Vm vm, boolean inMigration){
        final String msg = inMigration ? "VM Migration" : "VM Creation";
        if (!storage.isResourceAmountAvailable(vm.getStorage())) {
            Log.printFormattedLine(
                "%.2f: %s: [%s] Allocation of %s to %s failed due to lack of storage. Required %d but there is just %d MB available.",
                simulation.clock(), getClass().getSimpleName(),
                msg, vm, this, vm.getStorage().getCapacity(), storage.getAvailableResource());
            return false;
        }

        if (!ramProvisioner.isSuitableForVm(vm, vm.getCurrentRequestedRam())) {
            Log.printFormattedLine(
                "%.2f: %s: [%s] Allocation of %s to %s failed due to lack of RAM. Required %d but there is just %d MB available.",
                simulation.clock(), getClass().getSimpleName(),
                msg, vm, this, vm.getRam().getCapacity(), ram.getAvailableResource());
            return false;
        }

        if (!bwProvisioner.isSuitableForVm(vm, vm.getCurrentRequestedBw())) {
            Log.printFormattedLine(
                "%.2f: %s: [%s] Allocation of %s to %s failed due to lack of BW. Required %d but there is just %d Mbps available.",
                simulation.clock(), getClass().getSimpleName(),
                msg, vm, this, vm.getBw().getCapacity(), bw.getAvailableResource());
            return false;
        }

        if (!vmScheduler.isSuitableForVm(vm)) {
            Log.printFormattedLine(
                    "%.2f: %s: [%s] Allocation of %s to %s failed due to lack of PEs.\n\t  "+
                    "Required %d PEs of %.0f MIPS (%.0f MIPS total). However, there are just %d working PEs of %.0f MIPS, from which %.0f MIPS are available.",
                    getSimulation().clock(), getClass().getSimpleName(), msg, vm, this,
                    vm.getNumberOfPes(), vm.getMips(), vm.getTotalMipsCapacity(),
                    vmScheduler.getWorkingPeList().size(), getMips(), vmScheduler.getAvailableMips());
            return false;
        }

        vm.setInMigration(inMigration);
        storage.allocateResource(vm.getStorage());
        ramProvisioner.allocateResourceForVm(vm, vm.getCurrentRequestedRam());
        bwProvisioner.allocateResourceForVm(vm, vm.getCurrentRequestedBw());
        vmScheduler.allocatePesForVm(vm, vm.getCurrentRequestedMips());

        return true;
    }

    @Override
    public void reallocateMigratingInVms() {
        for (Vm vm : getVmsMigratingIn()) {
            if (!vmList.contains(vm)) {
                vmList.add(vm);
            }
            ramProvisioner.allocateResourceForVm(vm, vm.getCurrentRequestedRam());
            bwProvisioner.allocateResourceForVm(vm, vm.getCurrentRequestedBw());
            vmScheduler.allocatePesForVm(vm, vm.getCurrentRequestedMips());
            storage.allocateResource(vm.getStorage());
        }
    }

    @Override
    public boolean isSuitableForVm(Vm vm) {
        return active && (vmScheduler.getPeCapacity() >= vm.getCurrentRequestedMaxMips()
                && vmScheduler.getAvailableMips() >= vm.getCurrentRequestedTotalMips()
                && ramProvisioner.isSuitableForVm(vm, vm.getCurrentRequestedRam())
                && bwProvisioner.isSuitableForVm(vm, vm.getCurrentRequestedBw()));
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public final Host setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public void destroyVm(Vm vm) {
        destroyVmInternal(vm);
        vm.notifyOnHostDeallocationListeners(this);
        vm.setStopTime(getSimulation().clock());
    }

    @Override
    public void destroyTemporaryVm(Vm vm) {
        destroyVmInternal(vm);
    }

    private void destroyVmInternal(Vm vm) {
        if (!Objects.isNull(vm)) {
            deallocateResourcesOfVm(vm);
            vmList.remove(vm);
        }
    }

    /**
     * Deallocate all resources that a VM was using.
     *
     * @param vm the VM
     */
    protected void deallocateResourcesOfVm(Vm vm) {
        vm.setCreated(false);
        ramProvisioner.deallocateResourceForVm(vm);
        bwProvisioner.deallocateResourceForVm(vm);
        vmScheduler.deallocatePesFromVm(vm);
        storage.deallocateResource(vm.getStorage());
    }

    @Override
    public void destroyAllVms() {
        deallocateResourcesOfAllVms();
        for (Vm vm : vmList) {
            vm.setCreated(false);
            storage.deallocateResource(vm.getStorage());
        }

        vmList.clear();
    }

    /**
     * Deallocate all resources that all VMs were using.
     */
    protected void deallocateResourcesOfAllVms() {
        ramProvisioner.deallocateResourceForAllVms();
        bwProvisioner.deallocateResourceForAllVms();
        vmScheduler.deallocatePesForAllVms();
    }

    @Override
    public Vm getVm(int vmId, int brokerId) {
        return vmList.stream()
            .filter(vm -> vm.getId() == vmId && vm.getBroker().getId() == brokerId)
            .findFirst().orElse(Vm.NULL);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @see #getNumberOfWorkingPes()
     * @see #getNumberOfFreePes()
     * @see #getNumberOfFailedPes()
     */
    @Override
    public long getNumberOfPes() {
        return peList.size();
    }

    @Override
    public int getNumberOfFreePes() {
        return PeList.getNumberOfFreePes(getPeList());
    }

    @Override
    public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) {
        return vmScheduler.allocatePesForVm(vm, mipsShare);
    }

    @Override
    public void deallocatePesForVm(Vm vm) {
        vmScheduler.deallocatePesFromVm(vm);
    }

    @Override
    public List<Double> getAllocatedMipsForVm(Vm vm) {
        return vmScheduler.getAllocatedMips(vm);
    }

    @Override
    public double getTotalAllocatedMipsForVm(Vm vm) {
        return vmScheduler.getTotalAllocatedMipsForVm(vm);
    }

    @Override
    public double getMaxAvailableMips() {
        return vmScheduler.getMaxAvailableMips();
    }

    @Override
    public double getMips() {
        return peList.stream().mapToDouble(Pe::getCapacity).findFirst().orElse(0);
    }

    @Override
    public double getAvailableMips() {
        return vmScheduler.getAvailableMips();
    }

    @Override
    public Resource getBw() {
        return bwProvisioner.getResource();
    }

    @Override
    public Resource getRam() {
        return ramProvisioner.getResource();
    }

    @Override
    public Resource getStorage() {
        return storage;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public final void setId(int id) {
        this.id = id;
    }

    @Override
    public ResourceProvisioner getRamProvisioner() {
        return ramProvisioner;
    }

    @Override
    public final Host setRamProvisioner(ResourceProvisioner ramProvisioner) {
        checkSimulationIsRunningAndAttemptedToChangeHost("RAM");
        this.ramProvisioner = ramProvisioner;
        this.ramProvisioner.setResource(ram);
        return this;
    }

    private void checkSimulationIsRunningAndAttemptedToChangeHost(String resourceName) {
        if(simulation.isRunning()){
            throw new UnsupportedOperationException("It is not allowed to change a Host's "+resourceName+" after the simulation started.");
        }
    }

    @Override
    public ResourceProvisioner getBwProvisioner() {
        return bwProvisioner;
    }

    @Override
    public final Host setBwProvisioner(ResourceProvisioner bwProvisioner) {
        checkSimulationIsRunningAndAttemptedToChangeHost("BW");
        this.bwProvisioner = bwProvisioner;
        this.bwProvisioner.setResource(bw);
        return this;
    }

    @Override
    public VmScheduler getVmScheduler() {
        return vmScheduler;
    }

    @Override
    public final Host setVmScheduler(VmScheduler vmScheduler) {
        if(Objects.isNull(vmScheduler)){
            vmScheduler = VmScheduler.NULL;
        }

        vmScheduler.setHost(this);
        this.vmScheduler = vmScheduler;
        return this;
    }

    @Override
    public List<Pe> getPeList() {
        return peList;
    }

    /**
     * Sets the pe list.
     *
     * @param peList the new pe list
     * @return
     */
    protected final Host setPeList(List<Pe> peList) {
        checkSimulationIsRunningAndAttemptedToChangeHost("List of PE");
        this.peList = Objects.isNull(peList) ? new ArrayList<>() : peList;

        int peId = this.peList.stream().filter(pe -> pe.getId() > 0).mapToInt(Pe::getId).max().orElse(-1);
        List<Pe> pesWithoutIds = this.peList.stream().filter(pe -> pe.getId() < 0).collect(toList());
        for(Pe pe: pesWithoutIds){
            pe.setId(++peId);
        }

        return this;
    }

    @Override
    public <T extends Vm> List<T> getVmList() {
        return (List<T>) Collections.unmodifiableList(vmList);
    }

    @Override
    public <T extends Vm> List<T> getVmCreatedList() {
        return (List<T>) Collections.unmodifiableList(vmCreatedList);
    }

    protected void addVmToList(Vm vm){
        Objects.requireNonNull(vm);
        vmList.add(vm);
    }

    protected void removeVmFromList(Vm vm){
        Objects.requireNonNull(vm);
        vmList.remove(vm);
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public final boolean setFailed(boolean failed) {
        this.failed = failed;
        PeList.setStatusFailed(peList, getId(), failed);
        return true;
    }

    @Override
    public boolean setPeStatus(int peId, Pe.Status status) {
        return PeList.setPeStatus(peList, peId, status);
    }

    @Override
    public <T extends Vm> Set<T> getVmsMigratingIn() {
        return (Set<T>)vmsMigratingIn;
    }

    @Override
    public boolean addMigratingInVm(Vm vm) {
        if (vmsMigratingIn.contains(vm)) {
            return false;
        }

        vmsMigratingIn.add(vm);
        if(!allocateResourcesForVm(vm, true)){
            vmsMigratingIn.remove(vm);
            return false;
        }

        updateProcessing(simulation.clock());
        vm.getHost().updateProcessing(simulation.clock());

        return true;
    }

    @Override
    public void removeMigratingInVm(Vm vm) {
        deallocateResourcesOfVm(vm);
        vmsMigratingIn.remove(vm);
        vmList.remove(vm);
        vm.setInMigration(false);
    }

    @Override
    public Set<Vm> getVmsMigratingOut() {
        return Collections.unmodifiableSet(vmsMigratingOut);
    }

    @Override
    public boolean addVmMigratingOut(Vm vm) {
        return this.vmsMigratingOut.add(vm);
    }

    @Override
    public boolean removeVmMigratingOut(Vm vm) {
        return this.vmsMigratingOut.remove(vm);
    }

    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }

    @Override
    public final void setDatacenter(Datacenter datacenter) {
        checkSimulationIsRunningAndAttemptedToChangeHost("Datacenter");
        this.datacenter = datacenter;
    }

    @Override
    public String toString() {
        final String dc =
                Datacenter.NULL.equals(datacenter) ? "" :
                String.format("/DC %d", datacenter.getId());
        return String.format("Host %d%s", getId(), dc);
    }

    @Override
    public boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        return onUpdateProcessingListeners.remove(listener);
    }

    @Override
    public Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        Objects.requireNonNull(listener);
        this.onUpdateProcessingListeners.add(listener);
        return this;
    }

    @Override
    public long getAvailableStorage() {
        return storage.getAvailableResource();
    }

    @Override
    public long getNumberOfWorkingPes() {
        return peList.size() - getNumberOfFailedPes();
    }

    @Override
    public long getNumberOfFailedPes() {
        return peList.stream()
                .filter(Pe::isFailed)
                .count();
    }

    private Host setStorage(long size) {
        this.storage = new Storage(size);
        return this;
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
    }

    @Override
    public final Host setSimulation(Simulation simulation) {
        this.simulation = simulation;
        return this;
    }

    /**
     * Compare this Host with another one based on {@link #getTotalMipsCapacity()}.
     *
     * @param o the Host to compare to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(Host o) {
        return Double.compare(getTotalMipsCapacity(), o.getTotalMipsCapacity());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HostSimple that = (HostSimple) o;

        if (id != that.id) return false;
        return simulation.equals(that.simulation);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + simulation.hashCode();
        return result;
    }

    @Override
    public List<ResourceManageable> getResources() {
        if(simulation.isRunning() && resources.isEmpty()){
            resources = Arrays.asList(ramProvisioner.getResource(), bwProvisioner.getResource());
        }
        return Collections.unmodifiableList(resources);
    }

    @Override
    public ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> resourceClass) {
        if(simulation.isRunning() && provisioners.isEmpty()){
            provisioners = Arrays.asList(ramProvisioner, bwProvisioner);
        }

        return provisioners.stream()
            .filter(r -> r.getResource().isObjectSubClassOf(resourceClass))
            .findFirst()
            .orElse(ResourceProvisioner.NULL);
    }

    @Override
    public List<Pe> getWorkingPeList() {
        return peList.stream()
                .filter(Pe::isWorking)
                .collect(toList());
    }

    @Override
    public double getUtilizationOfCpu() {
        return computeCpuUtilizationPercent(getUtilizationOfCpuMips());
    }

    protected double computeCpuUtilizationPercent(double mipsUsage){
        final double totalMips = getTotalMipsCapacity();
        if(totalMips == 0){
            return 0;
        }

        final double utilization = mipsUsage / totalMips;
        return (utilization > 1 && utilization < 1.01 ? 1 : utilization);
    }

    @Override
    public double getUtilizationOfCpuMips() {
        return vmList.stream()
                .mapToDouble(vm -> vmScheduler.getTotalAllocatedMipsForVm(vm))
                .sum();
    }

    @Override
    public long getUtilizationOfRam() {
        return ramProvisioner.getTotalAllocatedResource();
    }

    @Override
    public long getUtilizationOfBw() {
        return bwProvisioner.getTotalAllocatedResource();
    }
}
