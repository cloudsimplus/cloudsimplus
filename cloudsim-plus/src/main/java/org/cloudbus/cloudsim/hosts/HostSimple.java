/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.core.ChangeableId;
import org.cloudbus.cloudsim.core.Machine;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterPowerSupply;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.vms.UtilizationHistory;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmStateHistoryEntry;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(HostSimple.class.getSimpleName());

    private static long defaultRamCapacity = (long)Conversion.gigaToMega(10);
    private static long defaultBwCapacity = 1000;
    private static long defaultStorageCapacity = (long)Conversion.gigaToMega(500);

    /** @see #getStateHistory() */
    private final List<HostStateHistoryEntry> stateHistory;

    /**@see #getPowerModel() */
    private PowerModel powerModel;

    /** @see #getId() */
    private long id;

    /** @see #isFailed() */
    private boolean failed;

    private boolean active;
    private boolean stateHistoryEnabled;

    /** @see #getStartTime() */
    private double startTime = -1;

    /** @see #getShutdownTime() */
    private double shutdownTime;

    /** @see #getTotalUpTime() */
    private double totalUpTime;

    /** @see #getLastBusyTime() */
    private double lastBusyTime;

    /** @see #getIdleShutdownDeadline() */
    private double idleShutdownDeadline;

    private final Ram ram;
    private final Bandwidth bw;

    /** @see #getStorage() */
    private Storage storage;

    /** @see #getRamProvisioner() */
    private ResourceProvisioner ramProvisioner;

    /** @see #getBwProvisioner() */
    private ResourceProvisioner bwProvisioner;

    /** @see #getVmScheduler() */
    private VmScheduler vmScheduler;

    /** @see #getVmList() */
    private final List<Vm> vmList = new ArrayList<>();

    /** @see #getPeList() */
    private List<Pe> peList;

    /** @see #getVmsMigratingIn() */
    private final Set<Vm> vmsMigratingIn;

    /** @see #getVmsMigratingOut() */
    private final Set<Vm> vmsMigratingOut;

    /** @see #getDatacenter() */
    private Datacenter datacenter;

    /** @see Host#removeOnUpdateProcessingListener(EventListener) */
    private final Set<EventListener<HostUpdatesVmsProcessingEventInfo>> onUpdateProcessingListeners;

    /** @see #getSimulation() */
    private Simulation simulation;

    /** @see #getResources() */
    private List<ResourceManageable> resources;
    
    private List<ResourceProvisioner> provisioners;
    private final List<Vm> vmCreatedList;

    /**
     * The previous amount of MIPS used.
     */
    private double previousUtilizationMips;

    /** @see #getFreePesNumber() */
    private int freePesNumber;
    /** @see #getFailedPesNumber() */
    private int failedPesNumber;

    /**
     * Creates and powers on a Host without a pre-defined ID,
     * 10GB of RAM, 1000Mbps of Bandwidth and 500GB of Storage.
     * It creates a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth. Finally, it sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param peList the host's {@link Pe} list
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public HostSimple(final List<Pe> peList) {
        this(peList, true);
    }

    /**
     * Creates a Host without a pre-defined ID,
     * 10GB of RAM, 1000Mbps of Bandwidth and 500GB of Storage
     * and enabling the host to be powered on or not.
     *
     * <p>It creates a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth. Finally, it sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.</p>
     *
     * @param peList the host's {@link Pe} list
     * @param activate define the Host activation status: true to power on, false to power off
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public HostSimple(final List<Pe> peList, final boolean activate) {
        this(defaultRamCapacity, defaultBwCapacity, defaultStorageCapacity, peList, activate);
    }

    /**
     * Creates and powers on a Host with the given parameters and a {@link VmSchedulerSpaceShared} as default.
     *
     * @param ramProvisioner the ram provisioner with capacity in Megabytes
     * @param bwProvisioner the bw provisioner with capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's PEs list
     *
     * @see #setVmScheduler(VmScheduler)
     */
    public HostSimple(
        final ResourceProvisioner ramProvisioner,
        final ResourceProvisioner bwProvisioner,
        final long storage,
        final List<Pe> peList)
    {
        this(ramProvisioner.getCapacity(), bwProvisioner.getCapacity(), storage, peList);
        setRamProvisioner(ramProvisioner);
        setBwProvisioner(bwProvisioner);
        setPeList(peList);
    }

    /**
     * Creates and powers on a Host without a pre-defined ID. It uses a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth and also sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     */
    public HostSimple(final long ram, final long bw, final long storage, final List<Pe> peList) {
        this(ram, bw, storage, peList, true);
    }

    /**
     * Creates a Host without a pre-defined ID. It uses a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth and also sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     * @param activate define the Host activation status: true to power on, false to power off
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     */
    public HostSimple(final long ram, final long bw, final long storage, final List<Pe> peList, final boolean activate) {
        this.setId(-1);
        this.setSimulation(Simulation.NULL);
        this.setActive(activate);
        this.idleShutdownDeadline = DEF_IDLE_SHUTDOWN_DEADLINE;

        this.ram = new Ram(ram);
        this.bw = new Bandwidth(bw);
        this.setStorage(storage);
        this.setRamProvisioner(new ResourceProvisionerSimple());
        this.setBwProvisioner(new ResourceProvisionerSimple());

        this.setVmScheduler(new VmSchedulerSpaceShared());
        this.setPeList(peList);
        this.setFailed(false);
        this.shutdownTime = -1;
        this.setDatacenter(Datacenter.NULL);
        this.onUpdateProcessingListeners = new HashSet<>();
        this.resources = new ArrayList<>();
        this.vmCreatedList = new ArrayList<>();
        this.provisioners = new ArrayList<>();
        this.vmsMigratingIn = new HashSet<>();
        this.vmsMigratingOut = new HashSet<>();
        this.powerModel = PowerModel.NULL;
        this.stateHistory = new LinkedList<>();
    }

    /**
     * Gets the Default RAM capacity (in MB) for creating Hosts.
     * This value is used when the RAM capacity is not given in a Host constructor.
     */
    public static long getDefaultRamCapacity() {
        return defaultRamCapacity;
    }

    /**
     * Sets the Default RAM capacity (in MB) for creating Hosts.
     * This value is used when the RAM capacity is not given in a Host constructor.
     */
    public static void setDefaultRamCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultRamCapacity = defaultCapacity;
    }

    /**
     * Gets the Default Bandwidth capacity (in Mbps) for creating Hosts.
     * This value is used when the BW capacity is not given in a Host constructor.
     */
    public static long getDefaultBwCapacity() {
        return defaultBwCapacity;
    }

    /**
     * Sets the Default Bandwidth capacity (in Mbps) for creating Hosts.
     * This value is used when the BW capacity is not given in a Host constructor.
     */
    public static void setDefaultBwCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultBwCapacity = defaultCapacity;
    }

    /**
     * Gets the Default Storage capacity (in MB) for creating Hosts.
     * This value is used when the Storage capacity is not given in a Host constructor.
     */
    public static long getDefaultStorageCapacity() {
        return defaultStorageCapacity;
    }

    /**
     * Sets the Default Storage capacity (in MB) for creating Hosts.
     * This value is used when the Storage capacity is not given in a Host constructor.
     */
    public static void setDefaultStorageCapacity(final long defaultCapacity) {
        Machine.validateCapacity(defaultCapacity);
        defaultStorageCapacity = defaultCapacity;
    }

    @Override
    public double getTotalMipsCapacity() {
        return peList.stream()
                .filter(Pe::isWorking)
                .mapToDouble(Pe::getCapacity)
                .sum();
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public double updateProcessing(final double currentTime) {
        /*The previous utilization mips is just used when there is a DatacenterPowerSupply instance
        attached to the datacenter. Since getting the utilization of CPU is an expensive
        operation in large scale experiments, if a Datacenter power supply is not set,
        the value is not stored.*/
        if(((DatacenterSimple)datacenter).getPowerSupply() != DatacenterPowerSupply.NULL) {
            setPreviousUtilizationMips(getUtilizationOfCpuMips());
        }

        if (!vmList.isEmpty()) {
            lastBusyTime = simulation.clock();
        } else if(isIdleEnough(idleShutdownDeadline)){
            setActive(false);
        }

        double nextSimulationTime = Double.MAX_VALUE;

        /* Uses an indexed for to avoid ConcurrentModificationException,
         * e.g., in cases when Vm is destroyed during simulation execution.*/
        for (int i = 0; i < vmList.size(); i++) {
            final Vm vm = vmList.get(i);
            final double nextTime = vm.updateProcessing(currentTime, vmScheduler.getAllocatedMips(vm));
            nextSimulationTime = nextTime > 0 ? Math.min(nextTime, nextSimulationTime) : nextSimulationTime;
        }

        notifyOnUpdateProcessingListeners(nextSimulationTime);
        addStateHistory(currentTime);

        return nextSimulationTime;
    }

    private void notifyOnUpdateProcessingListeners(final double nextSimulationTime) {
        onUpdateProcessingListeners.forEach(l -> l.update(HostUpdatesVmsProcessingEventInfo.of(l,this, nextSimulationTime)));
    }

    @Override
    public boolean createVm(final Vm vm) {
        final boolean result = createVmInternal(vm);
        if(result) {
            addVmToCreatedList(vm);
            vm.setHost(this);
            vm.notifyOnHostAllocationListeners();
            if(vm.getStartTime() < 0) {
               vm.setStartTime(getSimulation().clock());
            }
        }

        return result;
    }

    @Override
    public boolean createTemporaryVm(final Vm vm) {
        return createVmInternal(vm);
    }

    private boolean createVmInternal(final Vm vm) {
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
    private boolean allocateResourcesForVm(final Vm vm, final boolean inMigration){
        if (!storage.isAmountAvailable(vm.getStorage())) {
            logAllocationError(vm, inMigration, "MB", this.getStorage(), vm.getStorage());
            return false;
        }

        if (!ramProvisioner.isSuitableForVm(vm, vm.getCurrentRequestedRam())) {
            logAllocationError(vm, inMigration, "MB", this.getRam(), vm.getRam());
            return false;
        }

        if (!bwProvisioner.isSuitableForVm(vm, vm.getCurrentRequestedBw())) {
            logAllocationError(vm, inMigration, "Mbps", this.getBw(), vm.getBw());
            return false;
        }

        if (!vmScheduler.isSuitableForVm(vm)) {
            return false;
        }

        vm.setInMigration(inMigration);
        allocateResourcesForVm(vm);

        return true;
    }

    private void allocateResourcesForVm(Vm vm) {
        ramProvisioner.allocateResourceForVm(vm, vm.getCurrentRequestedRam());
        bwProvisioner.allocateResourceForVm(vm, vm.getCurrentRequestedBw());
        vmScheduler.allocatePesForVm(vm, vm.getCurrentRequestedMips());
        storage.allocateResource(vm.getStorage());
    }

    private void logAllocationError(
        final Vm vm, final boolean inMigration, final String resourceUnit,
        final Resource pmResource, final Resource vmRequestedResource)
    {
        final String migration = inMigration ? "VM Migration" : "VM Creation";
        final String msg = pmResource.getAvailableResource() > 0 ? "just "+pmResource.getAvailableResource()+" " + resourceUnit : "no amount";
        LOGGER.error(
            "{}: {}: [{}] Allocation of {} to {} failed due to lack of {}. Required {} but there is {} available.",
            simulation.clock(), getClass().getSimpleName(), migration, vm, this,
            pmResource.getClass().getSimpleName(), vmRequestedResource.getCapacity(), msg);
    }

    @Override
    public void reallocateMigratingInVms() {
        for (final Vm vm : getVmsMigratingIn()) {
            if (!vmList.contains(vm)) {
                vmList.add(vm);
            }

            allocateResourcesForVm(vm);
        }
    }

    @Override
    public boolean isSuitableForVm(final Vm vm) {
        return !isFailed() && hasEnoughResources(vm);
    }

    private boolean hasEnoughResources(final Vm vm) {
        /* Since && is a short-circuit operation,
         * the more complex method calls are placed last.
         * The freePesNumber and peList.size() are used just to improve performance
         * and avoid calling the other complex methods
         * when all PEs are used. */
        return freePesNumber > 0 && peList.size() >= vm.getNumberOfPes() &&
               storage.isAmountAvailable(vm.getStorage()) &&
               ramProvisioner.isSuitableForVm(vm, vm.getCurrentRequestedRam()) &&
               bwProvisioner.isSuitableForVm(vm, vm.getCurrentRequestedBw()) &&
               vmScheduler.isSuitableForVm(vm, vm.getCurrentRequestedMips());
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean hasEverStarted() {
        return this.startTime > -1;
    }

    @Override
    public final Host setActive(final boolean activate) {
        if(isFailed() && activate){
            throw new IllegalStateException("The Host is failed and cannot be activated.");
        }

        showActivationLogBeforeModification(activate);

        if(activate && !this.active) {
            setStartTime(getSimulation().clock());
        } else if(!activate && this.active){
            setShutdownTime(getSimulation().clock());
        }

        this.active = activate;
        return this;
    }

    /**
     * Prints information about the (de)activation of the Host,
     * before its status is changed
     * @param activate the activation value that is being requested to set
     *                 (and will be set after this method call)
     * @see #setActive(boolean)
     */
    private void showActivationLogBeforeModification(final boolean activate) {
        if(simulation == null || !simulation.isRunning() ) {
            return;
        }

        if(activate && !this.active){
            LOGGER.info("{}: {} is being powered on.", getSimulation().clock(), this);
        }
        else if(!activate && this.active){
            final String reason = isIdleEnough(idleShutdownDeadline) ? " after becoming idle" : "";
            LOGGER.info("{}: {} is being powered off{}.", getSimulation().clock(), this, reason);
        }
    }

    @Override
    public void destroyVm(final Vm vm) {
        if(!vm.isCreated()){
            return;
        }

        destroyVmInternal(vm);
        vm.notifyOnHostDeallocationListeners(this);
        vm.setStopTime(getSimulation().clock());
    }

    @Override
    public void destroyTemporaryVm(final Vm vm) {
        destroyVmInternal(vm);
    }

    private void destroyVmInternal(final Vm vm) {
        deallocateResourcesOfVm(requireNonNull(vm));
        vmList.remove(vm);
        vm.getBroker().getVmExecList().remove(vm);
    }

    /**
     * Deallocate all resources that a VM was using.
     *
     * @param vm the VM
     */
    protected void deallocateResourcesOfVm(final Vm vm) {
        vm.setCreated(false);
        ramProvisioner.deallocateResourceForVm(vm);
        bwProvisioner.deallocateResourceForVm(vm);
        vmScheduler.deallocatePesFromVm(vm);
        storage.deallocateResource(vm.getStorage());
    }

    @Override
    public void destroyAllVms() {
        deallocateResourcesOfAllVms();
        for (final Vm vm : vmList) {
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

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @see #getWorkingPesNumber()
     * @see #getFreePesNumber()
     * @see #getFailedPesNumber()
     */
    @Override
    public long getNumberOfPes() {
        return peList.size();
    }

    @Override
    public int getFreePesNumber() {
        return freePesNumber;
    }

    /**
     * Gets the MIPS share of each Pe that is allocated to a given VM.
     *
     * @param vm the vm
     * @return an array containing the amount of MIPS of each pe that is available to the VM
     */
    protected List<Double> getAllocatedMipsForVm(final Vm vm) {
        return vmScheduler.getAllocatedMips(vm);
    }

    @Override
    public double getTotalAllocatedMipsForVm(final Vm vm) {
        return vmScheduler.getTotalAllocatedMipsForVm(vm);
    }

    /**
     * Returns the maximum available MIPS among all the PEs of the host.
     *
     * @return max mips
     */
    protected double getMaxAvailableMips() {
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
    public long getId() {
        return id;
    }

    @Override
    public final void setId(long id) {
        this.id = id;
    }

    @Override
    public ResourceProvisioner getRamProvisioner() {
        return ramProvisioner;
    }

    @Override
    public final Host setRamProvisioner(final ResourceProvisioner ramProvisioner) {
        checkSimulationIsRunningAndAttemptedToChangeHost("RAM");
        this.ramProvisioner = requireNonNull(ramProvisioner);
        this.ramProvisioner.setResource(ram);
        return this;
    }

    private void checkSimulationIsRunningAndAttemptedToChangeHost(final String resourceName) {
        if(simulation.isRunning()){
            throw new IllegalStateException("It is not allowed to change a Host's "+resourceName+" after the simulation started.");
        }
    }

    @Override
    public ResourceProvisioner getBwProvisioner() {
        return bwProvisioner;
    }

    @Override
    public final Host setBwProvisioner(final ResourceProvisioner bwProvisioner) {
        checkSimulationIsRunningAndAttemptedToChangeHost("BW");
        this.bwProvisioner = requireNonNull(bwProvisioner);
        this.bwProvisioner.setResource(bw);
        return this;
    }

    @Override
    public VmScheduler getVmScheduler() {
        return vmScheduler;
    }

    @Override
    public final Host setVmScheduler(final VmScheduler vmScheduler) {
        this.vmScheduler = requireNonNull(vmScheduler);
        vmScheduler.setHost(this);
        return this;
    }

    @Override
    public double getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(final double startTime) {
        if(startTime < 0){
            throw new IllegalArgumentException("Host start time cannot be negative");
        }

        this.startTime = Math.floor(startTime);
        //If the Host is being activated or re-activated, the shutdown time is reset
        this.shutdownTime = -1;
    }

    @Override
    public double getShutdownTime() {
        return shutdownTime;
    }

    @Override
    public void setShutdownTime(final double shutdownTime) {
        if(shutdownTime < 0){
            throw new IllegalArgumentException("Host shutdown time cannot be negative");
        }

        this.shutdownTime = Math.floor(shutdownTime);
        this.totalUpTime += getUpTime();
    }

    @Override
    public double getUpTime() {
        return active ? simulation.clock() - startTime : shutdownTime - startTime;
    }

    @Override
    public double getTotalUpTime() {
        return totalUpTime + (active ? getUpTime() : 0);
    }

    @Override
    public double getIdleShutdownDeadline() {
        return idleShutdownDeadline;
    }

    @Override
    public Host setIdleShutdownDeadline(final double deadline) {
        this.idleShutdownDeadline = deadline;
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
    protected final Host setPeList(final List<Pe> peList) {
        requireNonNull(peList);
        checkSimulationIsRunningAndAttemptedToChangeHost("List of PE");
        this.peList = peList;

        long peId = this.peList.stream().filter(pe -> pe.getId() > 0).mapToLong(Pe::getId).max().orElse(-1);
        final List<Pe> pesWithoutIds = this.peList.stream().filter(pe -> pe.getId() < 0).collect(toList());
        for(final Pe pe: pesWithoutIds){
            pe.setId(++peId);
        }

        failedPesNumber = 0;
        freePesNumber = peList.size();

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

    protected void addVmToList(final Vm vm){
        vmList.add(requireNonNull(vm));
    }

    protected void addVmToCreatedList(final Vm vm){
        vmCreatedList.add(requireNonNull(vm));
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public final boolean setFailed(final boolean failed) {
        this.failed = failed;
        final Pe.Status newStatus = failed ? Pe.Status.FAILED : Pe.Status.FREE;
        setPeStatus(peList, newStatus);

        /*Just changes the active state when the Host is set to active.
        * In other situations, the active status must remain as it was.
        * For example, if the host was inactive and now it's set to failed,
        * it must remain inactive.*/
        if(failed && this.active){
            this.active = false;
        }

        return true;
    }

    /**
     * Sets the status of a given (sub)list of {@link Pe} to a new status.
     * @param peList the (sub)list of {@link Pe} to change the status
     * @param newStatus the new status
     */
    public final void setPeStatus(final List<Pe> peList, final Pe.Status newStatus){
        /*For performance reasons, stores the number of free and failed PEs
        instead of iterating over the PE list every time to find out.*/
        for (final Pe pe : peList) {
            if(pe.getStatus() == newStatus) {
                continue;
            }

            if(newStatus == Pe.Status.FAILED) {
                this.failedPesNumber++;
                if(pe.getStatus() == Pe.Status.FREE)
                    this.freePesNumber--;
            } else if(newStatus == Pe.Status.FREE) {
                this.freePesNumber++;
                if(pe.getStatus() == Pe.Status.FAILED)
                    this.failedPesNumber--;
            }

            pe.setStatus(newStatus);
        }
    }

    @Override
    public <T extends Vm> Set<T> getVmsMigratingIn() {
        return (Set<T>)vmsMigratingIn;
    }

    @Override
    public boolean addMigratingInVm(final Vm vm) {
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
    public void removeMigratingInVm(final Vm vm) {
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
    public boolean addVmMigratingOut(final Vm vm) {
        return this.vmsMigratingOut.add(vm);
    }

    @Override
    public boolean removeVmMigratingOut(final Vm vm) {
        return this.vmsMigratingOut.remove(vm);
    }

    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }

    @Override
    public final void setDatacenter(final Datacenter datacenter) {
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
    public boolean removeOnUpdateProcessingListener(final EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        return onUpdateProcessingListeners.remove(listener);
    }

    @Override
    public Host addOnUpdateProcessingListener(final EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        if(listener.equals(EventListener.NULL)){
            return this;
        }

        this.onUpdateProcessingListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public long getAvailableStorage() {
        return storage.getAvailableResource();
    }

    @Override
    public int getWorkingPesNumber() {
        return peList.size() - getFailedPesNumber();
    }

    @Override
    public int getFailedPesNumber() {
        return failedPesNumber;
    }

    private Host setStorage(final long size) {
        this.storage = new Storage(size);
        return this;
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
    }

    @Override
    public double getLastBusyTime() {
        return lastBusyTime;
    }

    @Override
    public final Host setSimulation(final Simulation simulation) {
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
    public int compareTo(final Host o) {
        return Double.compare(getTotalMipsCapacity(), o.getTotalMipsCapacity());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final HostSimple that = (HostSimple) o;

        if (id != that.id) return false;
        return simulation.equals(that.simulation);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
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
    public ResourceProvisioner getProvisioner(final Class<? extends ResourceManageable> resourceClass) {
        if(simulation.isRunning() && provisioners.isEmpty()){
            provisioners = Arrays.asList(ramProvisioner, bwProvisioner);
        }

        return provisioners
            .stream()
            .filter(provisioner -> provisioner.getResource().isSubClassOf(resourceClass))
            .findFirst()
            .orElse(ResourceProvisioner.NULL);
    }

    @Override
    public List<Pe> getWorkingPeList() {
        return getFilteredPeList(Pe::isWorking);
    }

    @Override
    public List<Pe> getBusyPeList() {
        return getFilteredPeList(Pe::isBusy);
    }

    @Override
    public List<Pe> getFreePeList() {
        return getFilteredPeList(Pe::isFree);
    }

    private List<Pe> getFilteredPeList(final Predicate<Pe> status) {
        return peList.stream().filter(status).collect(toList());
    }

    @Override
    public double getUtilizationOfCpu() {
        return computeCpuUtilizationPercent(getUtilizationOfCpuMips());
    }

    private double computeCpuUtilizationPercent(final double mipsUsage){
        final double totalMips = getTotalMipsCapacity();
        if(totalMips == 0){
            return 0;
        }

        final double utilization = mipsUsage / totalMips;
        return (utilization > 1 && utilization < 1.01 ? 1 : utilization);
    }

    @Override
    public double getUtilizationOfCpuMips() {
        return vmList.stream().mapToDouble(Vm::getTotalCpuMipsUsage).sum();
    }

    @Override
    public long getUtilizationOfRam() {
        return ramProvisioner.getTotalAllocatedResource();
    }

    @Override
    public long getUtilizationOfBw() {
        return bwProvisioner.getTotalAllocatedResource();
    }

    @Override
    public SortedMap<Double, DoubleSummaryStatistics> getUtilizationHistory() {
        //Gets a Stream containing the utilization entries for every Vm inside the Host
        final Stream<Entry<Double, Double>> utilizationEntriesStream = this.vmCreatedList
            .stream()
            .map(Vm::getUtilizationHistory)
            .map(this::remapUtilizationHistory)
            .flatMap(vmUtilization -> vmUtilization.entrySet().stream());

        //Groups the CPU utilization entries by the time the values were collected
        return utilizationEntriesStream
                    .collect(
                        groupingBy(Entry::getKey, TreeMap::new, summarizingDouble(Entry::getValue))
                    );
    }

    @Override
    public SortedMap<Double, Double> getUtilizationHistorySum() {
        /*Remaps the value of an entry inside the Utilization History map.*/
        final Function<Entry<Double, DoubleSummaryStatistics>, Double> valueMapper = entry -> entry.getValue().getSum();

        return getUtilizationHistory()
                    .entrySet()
                    .stream()
                    .collect(toMap(Entry::getKey, valueMapper, this::mergeFunction, TreeMap::new));
    }

    /**
     * Remaps the entire Vm's {@link UtilizationHistory} by updating the CPU utilization value in each entry
     * to correspond to the relative percentage of the Host CPU capacity that Vm is using.
     * This is required since the {@link UtilizationHistory} contains the VM's CPU utilization
     * relative to the VM's capacity.
     *
     * @param utilizationHistory the VM {@link UtilizationHistory} with the history entries
     * @return
     */
    private SortedMap<Double, Double> remapUtilizationHistory(final UtilizationHistory utilizationHistory) {
        return utilizationHistory
                    .getHistory()
                    .entrySet()
                    .stream()
                    .collect(
                        toMap(Entry::getKey, vmUtilizationMapper(utilizationHistory), this::mergeFunction, TreeMap::new)
                    );
    }

    /**
     * A merge {@link BinaryOperator} used to resolve conflicts when remapping the values of the utilization history map
     * using the {@link Collectors#toMap(Function, Function, BinaryOperator, Supplier)}.
     *
     * If there are two values for the same key, the last value is used.
     * However, since we are just remapping an existing map, there won't be such a situation.
     *
     * @param usage1 the 1st CPU utilization value found for a key
     * @param usage2 the 2dn CPU utilization value found for the same key
     * @return the higher value between the given two ones
     *
     * @see #getUtilizationHistorySum()
     * @see #remapUtilizationHistory(UtilizationHistory)
     */
    private double mergeFunction(final double usage1, final double usage2) {
        return Math.max(usage1, usage2);
    }

    /**
     * Receives a Vm {@link UtilizationHistory} and returns a {@link Function} that
     * requires a map entry from the history (representing a VM's CPU utilization for a given time),
     * and returns the percentage of the Host CPU capacity that such a Vm is using at that time.
     * This way, the value that represents how much of the VM's CPU is being used
     * will be converted to how much that VM is using from the Host's CPU.
     *
     * @param utilizationHistory the VM {@link UtilizationHistory} with the history entries
     * @return
     */
    private Function<Entry<Double, Double>, Double> vmUtilizationMapper(final UtilizationHistory utilizationHistory) {
        //The entry key is the time and the value is the percentage of the VM CPU that is being used
        return entry -> entry.getValue() * utilizationHistory.getVm().getRelativeMipsCapacityPercent();
    }

    @Override
    public PowerModel getPowerModel() {
        return powerModel;
    }

    @Override
    public Host setPowerModel(final PowerModel powerModel) {
        requireNonNull(powerModel);
        if(powerModel.getHost() != null && powerModel.getHost() != Host.NULL && !powerModel.getHost().equals(this)){
            throw new IllegalStateException("The given PowerModel is already assigned to another Host. Each Host must have its own PowerModel instance.");
        }

        this.powerModel = powerModel;
        powerModel.setHost(this);
        return this;
    }

    @Override
    public double getPreviousUtilizationOfCpu() {
        return computeCpuUtilizationPercent(previousUtilizationMips);
    }

    @Override
    public void enableStateHistory() {
        this.stateHistoryEnabled = true;
    }

    @Override
    public void disableStateHistory() {
        this.stateHistoryEnabled = false;
    }

    @Override
    public boolean isStateHistoryEnabled() {
        return this.stateHistoryEnabled;
    }

    /**
     * Sets the previous utilization of CPU in mips.
     *
     * @param previousUtilizationMips the new previous utilization of CPU in
     * mips
     */
    private void setPreviousUtilizationMips(final double previousUtilizationMips) {
        this.previousUtilizationMips = previousUtilizationMips;
    }

    @Override
    public List<Vm> getFinishedVms() {
        return getVmList().stream()
            .filter(vm -> !vm.isInMigration())
            .filter(vm -> vm.getCurrentRequestedTotalMips() == 0)
            .collect(toList());
    }

    /**
     * Adds the VM resource usage to the History if the VM is not migrating into the Host.
     * @param vm the VM to add its usage to the history
     * @param currentTime the current simulation time
     * @return the total allocated MIPS for the given VM
     */
    private double addVmResourceUseToHistoryIfNotMigratingIn(final Vm vm, final double currentTime) {
        double totalAllocatedMips = getVmScheduler().getTotalAllocatedMipsForVm(vm);
        if (getVmsMigratingIn().contains(vm)) {
            LOGGER.info("{}: {}: {} is migrating in", getSimulation().clock(), this, vm);
            return totalAllocatedMips;
        }

        final double totalRequestedMips = vm.getCurrentRequestedTotalMips();
        if (totalAllocatedMips + 0.1 < totalRequestedMips) {
            final String reason = getVmsMigratingOut().contains(vm) ? "migration overhead" : "capacity unavailability";
            final long notAllocatedMipsByPe = (long)((totalRequestedMips - totalAllocatedMips)/vm.getNumberOfPes());
            LOGGER.error(
                "{}: {}: {} MIPS not allocated for each one of the {} PEs from {} due to {}.",
                getSimulation().clock(), this, notAllocatedMipsByPe, vm.getNumberOfPes(), vm, reason);
        }

        final VmStateHistoryEntry entry = new VmStateHistoryEntry(
                currentTime,
                totalAllocatedMips,
                totalRequestedMips,
                vm.isInMigration() && !getVmsMigratingIn().contains(vm));
        vm.addStateHistoryEntry(entry);

        if (vm.isInMigration()) {
            LOGGER.info("{}: {}: {} is migrating out ", getSimulation().clock(), this, vm);
            totalAllocatedMips /= getVmScheduler().getMaxCpuUsagePercentDuringOutMigration();
        }

        return totalAllocatedMips;
    }

    private void addStateHistory(final double currentTime) {
        if(!stateHistoryEnabled){
            return;
        }

        double hostTotalRequestedMips = 0;

        for (final Vm vm : getVmList()) {
            final double totalRequestedMips = vm.getCurrentRequestedTotalMips();
            addVmResourceUseToHistoryIfNotMigratingIn(vm, currentTime);
            hostTotalRequestedMips += totalRequestedMips;
        }

        addStateHistoryEntry(currentTime, getUtilizationOfCpuMips(), hostTotalRequestedMips, active);
    }

    /**
     * Adds a host state history entry.
     *
     * @param time the time
     * @param allocatedMips the allocated mips
     * @param requestedMips the requested mips
     * @param isActive the is active
     */
    private void addStateHistoryEntry(
        final double time,
        final double allocatedMips,
        final double requestedMips,
        final boolean isActive)
    {
        final HostStateHistoryEntry newState = new HostStateHistoryEntry(time, allocatedMips, requestedMips, isActive);
        if (!stateHistory.isEmpty()) {
            final HostStateHistoryEntry previousState = stateHistory.get(stateHistory.size() - 1);
            if (previousState.getTime() == time) {
                stateHistory.set(stateHistory.size() - 1, newState);
                return;
            }
        }

        stateHistory.add(newState);
    }

    @Override
    public List<HostStateHistoryEntry> getStateHistory() {
        return Collections.unmodifiableList(stateHistory);
    }

    @Override
    public List<Vm> getMigratableVms() {
        return vmList.stream()
            .filter(vm -> !vm.isInMigration())
            .collect(Collectors.toList());
    }
}
