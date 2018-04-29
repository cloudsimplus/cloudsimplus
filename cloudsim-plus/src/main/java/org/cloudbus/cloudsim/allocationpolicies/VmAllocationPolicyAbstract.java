/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.LongStream;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;

import static java.util.stream.Collectors.toList;

/**
 * An abstract class that represents the policy
 * used by a {@link Datacenter} to choose a {@link Host} to place or migrate
 * a given {@link Vm}. It supports two-stage commit of reservation of
 * hosts: first, we reserve the Host and, once committed by the customer, the VM is
 * effectively allocated to that Host.
 *
 * <p>Each {@link Datacenter} must to have its own instance of a {@link VmAllocationPolicy}.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public abstract class VmAllocationPolicyAbstract implements VmAllocationPolicy {
    private BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction;

    /**
     * @see #getDatacenter()
     */
    private Datacenter datacenter;

    /**
     * @see #getHostFreePesMap()
     * @todo The number of free PEs in each Host could be determined dynamically, instead of storing
     *       such information in a HashMap.
     *       The information in the map can become out-of-date and cause issues.
     *       There is just a concern about performance if this information
     *       is computed every time when needed.
     */
    private Map<Host, Long> hostFreePesMap;

    /**
     * The number of PEs used in each VM.
     */
    private Map<Vm, Long> usedPes;

    /**
     * Creates a new VmAllocationPolicy.
     */
    public VmAllocationPolicyAbstract() {
        this(null);
    }

    /**
     * Creates a new VmAllocationPolicy, changing the {@link BiFunction} to select a Host for a Vm.
     * @param findHostForVmFunction a {@link BiFunction} to select a Host for a given Vm.
     * @see VmAllocationPolicy#setFindHostForVmFunction(BiFunction)
     */
    public VmAllocationPolicyAbstract(final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction) {
        setDatacenter(Datacenter.NULL);
        setFindHostForVmFunction(findHostForVmFunction);
    }

    @Override
    public final <T extends Host> List<T> getHostList() {
        return (List<T>) datacenter.getHostList();
    }

    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }

    /**
     * Sets the Datacenter associated to the Allocation Policy
     * @param datacenter the Datacenter to set
     */
    @Override
    public final void setDatacenter(final Datacenter datacenter){
        Objects.requireNonNull(datacenter);
        addPesFromHostsToFreePesList(datacenter);
        this.datacenter = datacenter;
    }

    /**
     * Gets the number of free PEs from each Host in a list
     * and adds these numbers to the {@link #getHostFreePesMap() list of free PEs}.
     * Before the Host starts being used, the number of free PEs is
     * the same as the number of working PEs.
     *
     * @param datacenter the Datacenter to get Hosts from
     */
    private void addPesFromHostsToFreePesList(final Datacenter datacenter) {
        Objects.requireNonNull(datacenter);
        if(datacenter == Datacenter.NULL || datacenter != this.datacenter) {
            setHostFreePesMap(new HashMap<>(datacenter.getHostList().size()));
            setUsedPes(new HashMap<>());
        }

        datacenter.getHostList().forEach(this::addPesFromHost);
    }

    /**
     * Gets the number of free PEs from a given Host
     * and adds these numbers to the {@link #getHostFreePesMap() list of free PEs}.
     * Before the Host starts being used, the number of free PEs is
     * the same as the number of working PEs.
     */
    private void addPesFromHost(final Host host) {
        hostFreePesMap.putIfAbsent(host, host.getNumberOfWorkingPes());
    }

    /**
     * Gets a map with the number of free PEs for each host from {@link #getHostList()}.
     *
     * @return a Map where each key is a host and each value is the number of free PEs of that host.
     */
    protected final Map<Host, Long> getHostFreePesMap() {
        return hostFreePesMap;
    }

    /**
     * Sets the Host free PEs Map.
     *
     * @param hostFreePesMap the new Host free PEs map
     * @return
     */
    protected final VmAllocationPolicy setHostFreePesMap(final Map<Host, Long> hostFreePesMap) {
        this.hostFreePesMap = hostFreePesMap;
        return this;
    }

    /**
     * Adds number used PEs for a Vm to the map between each VM and the number of PEs used.
     * @param vm the VM to add the number of used PEs to the map
     */
    protected void addUsedPes(final Vm vm) {
        usedPes.put(vm, vm.getNumberOfPes());
    }

    /**
     * Removes the used PEs for a Vm from the map between each VM and the number of PEs used.
     * @param vm
     * @return the used PEs number
     */
    protected long removeUsedPes(final Vm vm) {
        final Long pes = usedPes.remove(vm);
        return pes == null ? 0 : pes;
    }

    /**
     * Sets the used pes.
     *
     * @param usedPes the used pes
     */
    protected final void setUsedPes(final Map<Vm, Long> usedPes) {
        Objects.requireNonNull(usedPes);
        this.usedPes = usedPes;
    }

    @Override
    public boolean scaleVmVertically(final VerticalVmScaling scaling) {
        /* @TODO VM PEs scaling is not being implemented in a polymorphic way.
        *  More details in https://github.com/manoelcampos/cloudsim-plus/issues/75
        */

        if(scaling.isVmUnderloaded()){
            return downScaleVmVertically(scaling);
        }

        if(scaling.isVmOverloaded()){
            return upScaleVmVertically(scaling);
        }

        return false;
    }

    /**
     * Performs the up scaling of Vm resource associated to a given scaling object.
     *
     * @param scaling the Vm's scaling object
     * @return true if the Vm was overloaded and the up scaling was performed, false otherwise
     */
    private boolean upScaleVmVertically(final VerticalVmScaling scaling) {
        return isRequestingCpuScaling(scaling) ? scaleVmPesUpOrDown(scaling) : upScaleVmNonCpuResource(scaling);
    }

    /**
     * Performs the down scaling of Vm resource associated to a given scaling object.
     *
     * @param scaling the Vm's scaling object
     * @return true if the down scaling was performed, false otherwise
     */
    private boolean downScaleVmVertically(final VerticalVmScaling scaling) {
        return isRequestingCpuScaling(scaling) ? scaleVmPesUpOrDown(scaling) : downScaleVmNonCpuResource(scaling);
    }

    /**
     * Performs the up or down scaling of Vm {@link Pe}s,
     * depending if the VM is under or overloaded.
     *
     * @param scaling the Vm's scaling object
     * @return true if the scaling was performed, false otherwise
     * @see #upScaleVmVertically(VerticalVmScaling)
     */
    private boolean scaleVmPesUpOrDown(final VerticalVmScaling scaling) {
        final double numberOfPesForScaling = scaling.getResourceAmountToScale();
        if(numberOfPesForScaling == 0){
            return false;
        }

        if (scaling.isVmOverloaded() && isNotHostPesSuitableToUpScaleVm(scaling)) {
            showResourceIsUnavailable(scaling);
            return false;
        }

        final Vm vm = scaling.getVm();
        vm.getHost().getVmScheduler().deallocatePesFromVm(vm);
        if(scaling.isVmUnderloaded()) {
            vm.getProcessor().removeCapacity((long)numberOfPesForScaling);
        }
        else {
            vm.getProcessor().addCapacity((long)numberOfPesForScaling);
        }

        vm.getHost().getVmScheduler().allocatePesForVm(vm);
        return true;
    }

    private boolean isNotHostPesSuitableToUpScaleVm(final VerticalVmScaling scaling) {
        final Vm vm = scaling.getVm();
        final double numberOfPesForScaling = scaling.getResourceAmountToScale();
        final List<Double> additionalVmMips =
            LongStream.range(0, (long)numberOfPesForScaling).mapToObj(i -> vm.getMips()).collect(toList());

        return !vm.getHost().getVmScheduler().isSuitableForVm(additionalVmMips);
    }

    /**
     * Checks if the scaling object is in charge of scaling CPU resource.
     *
     * @param scaling the Vm scaling object
     * @return true if the scaling is for CPU, false if it is
     * for any other kind of resource
     */
    private boolean isRequestingCpuScaling(final VerticalVmScaling scaling) {
        return Processor.class.equals(scaling.getResourceClass());
    }

    /**
     * Performs the up scaling of a Vm resource that is anything else than CPU.
     *
     * @param scaling the Vm's scaling object
     * @return true if the up scaling was performed, false otherwise
     * @see #scaleVmPesUpOrDown(VerticalVmScaling)
     * @see #upScaleVmVertically(VerticalVmScaling)
     */
    private boolean upScaleVmNonCpuResource(final VerticalVmScaling scaling) {
        final Class<? extends ResourceManageable> resourceClass = scaling.getResourceClass();
        final ResourceManageable hostResource = scaling.getVm().getHost().getResource(resourceClass);
        final ResourceManageable vmResource = scaling.getVm().getResource(resourceClass);
        final double extraAmountToAllocate = scaling.getResourceAmountToScale();
        if(!hostResource.isResourceAmountAvailable(extraAmountToAllocate)) {
            return false;
        }

        final ResourceProvisioner provisioner = scaling.getVm().getHost().getProvisioner(resourceClass);
        final double newTotalVmResource = (double) vmResource.getCapacity() + extraAmountToAllocate;
        if(!provisioner.allocateResourceForVm(scaling.getVm(), newTotalVmResource)){
            showResourceIsUnavailable(scaling);
            return false;
        }

        Log.printFormattedLine(
            "%.2f: %s: %.0f more %s allocated to Vm %d: new capacity is %d. Current resource usage is %.2f%%",
            scaling.getVm().getSimulation().clock(),
            scaling.getClass().getSimpleName(),
            extraAmountToAllocate, resourceClass.getSimpleName(),
            scaling.getVm().getId(), vmResource.getCapacity(),
            vmResource.getPercentUtilization()*100);
        return true;
    }

    private void showResourceIsUnavailable(final VerticalVmScaling scaling) {
        final Class<? extends ResourceManageable> resourceClass = scaling.getResourceClass();
        final ResourceManageable hostResource = scaling.getVm().getHost().getResource(resourceClass);
        final double extraAmountToAllocate = scaling.getResourceAmountToScale();
        Log.printFormattedLine(
            "%.2f: %s: Vm %d requested more %.0f of %s capacity but the Host %d has just %d of available %s",
            scaling.getVm().getSimulation().clock(),
            scaling.getClass().getSimpleName(),
            scaling.getVm().getId(), extraAmountToAllocate,
            resourceClass.getSimpleName(), scaling.getVm().getHost().getId(),
            hostResource.getAvailableResource(), resourceClass.getSimpleName());
    }

    /**
     * Performs the down scaling of a Vm resource that is anything else than CPU.
     *
     * @param scaling the Vm's scaling object
     * @return true if the down scaling was performed, false otherwise
     * @see #downScaleVmVertically(VerticalVmScaling)
     */
    private boolean downScaleVmNonCpuResource(final VerticalVmScaling scaling) {
        final Class<? extends ResourceManageable> resourceClass = scaling.getResourceClass();
        final ResourceManageable vmResource = scaling.getVm().getResource(resourceClass);
        final double amountToDeallocate = scaling.getResourceAmountToScale();
        final ResourceProvisioner provisioner = scaling.getVm().getHost().getProvisioner(resourceClass);
        final double newTotalVmResource = vmResource.getCapacity() - amountToDeallocate;
        if(!provisioner.allocateResourceForVm(scaling.getVm(), newTotalVmResource)){
            Log.printFormattedLine(
                "%.2f: %s: Vm %d requested to reduce %s capacity by %.0f but an unexpected error occurred and the resource was not resized",
                scaling.getVm().getSimulation().clock(),
                scaling.getClass().getSimpleName(),
                scaling.getVm().getId(),
                resourceClass.getSimpleName(), amountToDeallocate);
            return false;
        }

        Log.printFormattedLine(
            "%.2f: %s: %.0f %s deallocated from Vm %d: new capacity is %d. Current resource usage is %.2f%%",
            scaling.getVm().getSimulation().clock(),
            scaling.getClass().getSimpleName(),
            amountToDeallocate, resourceClass.getSimpleName(),
            scaling.getVm().getId(), vmResource.getCapacity(),
            vmResource.getPercentUtilization()*100);
        return true;
    }

    /**
     * Allocates the host with less PEs in use for a given VM.
     *
     * @param vm {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean allocateHostForVm(final Vm vm) {
        if(getHostList().isEmpty()){
            Log.printFormattedLine(
                "%.2f: %s: Vm %s could not be allocated because there isn't any Host for Datacenter %d",
                vm.getSimulation().clock(), vm.getId(), getDatacenter().getId());
            return false;
        }

        if (vm.isCreated()) {
            return false;
        }

        /** Calls the Function that finds a Host for a VM.
         *  It doesn't call the {@link #findHostForVm(Vm)} directly
         *  since that method is the default implementation.
         *  However, such an implementation can be changed by
         *  calling {@link VmAllocationPolicy#setFindHostForVmFunction(BiFunction)}.
         */
        final Optional<Host> optional = findHostForVmFunction.apply(this, vm);
        if(optional.isPresent()){
            return allocateHostForVm(vm, optional.get());
        }

        Log.printFormattedLine("%.2f: No suitable host found for %s\n", vm.getSimulation().clock(), vm);
        return false;
    }

    @Override
    public boolean allocateHostForVm(final Vm vm, final Host host) {
        if (host.createVm(vm)) {
            Log.printFormattedLine(
                "%.2f: %s: %s has been allocated to %s",
                vm.getSimulation().clock(), getClass().getSimpleName(), vm, host);
            return true;
        }

        Log.printFormattedLine("%.2f: Creation of %s on %s failed\n", vm.getSimulation().clock(), vm, host);
        return false;
    }

    @Override
    public void deallocateHostForVm(final Vm vm) {
        vm.getHost().destroyVm(vm);
    }

    /**
     * {@inheritDoc}
     * The default implementation of such a Function is provided by the method {@link #findHostForVm(Vm)}.
     * @param findHostForVmFunction {@inheritDoc}.
     *                              Passing null makes the Function to be set as the default {@link #findHostForVm(Vm)}.
     */
    @Override
    public final void setFindHostForVmFunction(final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction) {
        this.findHostForVmFunction = findHostForVmFunction == null ? VmAllocationPolicy::findHostForVm : findHostForVmFunction;
    }
}
