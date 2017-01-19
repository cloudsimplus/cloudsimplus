/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;

/**
 * An abstract class that represents the policy
 * used by a {@link Datacenter} to choose a {@link Host} to place or migrate
 * or migrate a given {@link Vm}. It supports two-stage commit of reservation of
 * hosts: first, we reserve the host and, once committed by the user, it is
 * effectively allocated to he/she.
 *
 * <p>Each {@link Datacenter} has to have its own instance of a class that extends
 * this class.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public abstract class VmAllocationPolicyAbstract implements VmAllocationPolicy {

    /**
     * @see #getVmHostMap()
     */
    private Map<Vm, Host> vmTable;

    /**
     * @see #getDatacenter()
     */
    private Datacenter datacenter;
    /**
     * @see #getHostFreePesMap()
     */
    private Map<Host, Integer> hostFreePesMap;
    /**
     * @see #getUsedPes()
     */
    private Map<Vm, Integer> usedPes;

    /**
     * Creates a new VmAllocationPolicy object.
     *
     * @pre $none
     * @post $none
     */
    public VmAllocationPolicyAbstract() {
        setDatacenter(Datacenter.NULL);
    }

    @Override
    public <T extends Host> List<T> getHostList() {
        return (List<T>) getDatacenter().getHostList();
    }

    /**
     * Gets the map between a VM and its allocated host. The map key is a VM UID
     * and the value is the allocated host for that VM.
     *
     * @return the VM map
     */
    protected Map<Vm, Host> getVmHostMap() {
        return vmTable;
    }

    /**
     * Sets the vm table.
     *
     * @param vmTable the vm table
     */
    protected final void setVmTable(Map<Vm, Host> vmTable) {
        this.vmTable = vmTable;
    }

    /**
     * Register the allocation of a given Host to a Vm. It maps the placement of
     * the Vm into the given Host.
     *
     * @param vm the placed Vm
     * @param host the Host where the Vm has just been placed
     */
    protected void mapVmToPm(Vm vm, Host host) {
        if(Objects.isNull(vm) || Objects.isNull(host)) {
            return;
        }

        getVmHostMap().put(vm, host);
    }

    /**
     * Unregister the allocation of a Host to a given Vm, unmapping the Vm to
     * the Host where it was. The method has to be called when a Vm is
     * moved/removed from a Host.
     *
     * @param vm the moved/removed Vm
     * @return the Host where the Vm was removed/moved from or {@link Host#NULL}
     * if the Vm wasn't associated to a Host
     */
    protected Host unmapVmFromPm(Vm vm) {
        if(Objects.isNull(vm)) {
            return Host.NULL;
        }

        Host host = getVmHostMap().remove(vm);
        if(Objects.isNull(host)) {
            return Host.NULL;
        }

        return host;
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
    public final void setDatacenter(Datacenter datacenter){
        if(Objects.isNull(datacenter)){
            datacenter = Datacenter.NULL;
        }

        this.datacenter = datacenter;
        addPesFromHostsToFreePesList();
    }

    /**
     * Gets the number of PEs from each Host in the {@link #getHostList() host list}
     * and adds these number of PEs to the {@link #getHostFreePesMap() list of free PEs}.
     * <b>The method expects that the {@link #datacenter} is already set.</b>
     *
     */
    private void addPesFromHostsToFreePesList() {
        setHostFreePesMap(new HashMap<>(getHostList().size()));
        setVmTable(new HashMap<>());
        setUsedPes(new HashMap<>());
        getHostList().forEach(host -> hostFreePesMap.put(host, host.getNumberOfPes()));
    }

    /**
     * Gets a map with the number of free PEs for each host from {@link #getHostList()}.
     *
     * @return a Map where each key is a host and each value is the number of free PEs of that host.
     */
    protected final Map<Host, Integer> getHostFreePesMap() {
        return hostFreePesMap;
    }

    /**
     * Sets the Host free PEs Map.
     *
     * @param hostFreePesMap the new Host free PEs map
     * @return
     */
    protected final VmAllocationPolicy setHostFreePesMap(Map<Host, Integer> hostFreePesMap) {
        this.hostFreePesMap = hostFreePesMap;
        return this;
    }

    /**
     * Gets the map between each VM and the number of PEs used. The map key is a
     * VM and the value is the number of used Pes for that VM.
     *
     * @return the used PEs map
     */
    protected Map<Vm, Integer> getUsedPes() {
        return usedPes;
    }

    /**
     * Adds number used PEs for a Vm to the map between each VM and the number of PEs used.
     * @param vm the VM to add the number of used PEs to the map
     */
    protected void addUsedPes(Vm vm) {
        usedPes.put(vm, vm.getNumberOfPes());
    }

    /**
     * Removes the used PEs for a Vm from the map between each VM and the number of PEs used.
     * @return the used PEs number
     */
    protected int removeUsedPes(Vm vm) {
        final Integer pes = usedPes.remove(vm);
        return (pes == null ? 0 : pes);
    }

    /**
     * Sets the used pes.
     *
     * @param usedPes the used pes
     */
    protected final void setUsedPes(Map<Vm, Integer> usedPes) {
        Objects.requireNonNull(usedPes);
        this.usedPes = usedPes;
    }

    @Override
    public boolean scaleVmVertically(VerticalVmScaling scaling) {
        final Class<? extends ResourceManageable> resourceClass = scaling.getResourceClassToScale();
        final ResourceManageable hostResource = scaling.getVm().getHost().getResource(resourceClass);
        final ResourceManageable vmResource = scaling.getVm().getResource(resourceClass);
        final double extraAmountToAllocate = vmResource.getCapacity() * scaling.getScalingFactor();
        if(!hostResource.isResourceAmountAvailable(extraAmountToAllocate)) {
            return false;
        }

        final ResourceProvisioner provisioner = scaling.getVm().getHost().getProvisioner(resourceClass);
        final double previousVmResourceCapacity = vmResource.getCapacity();
        final double newTotalVmResource = previousVmResourceCapacity + extraAmountToAllocate;
        if(!provisioner.allocateResourceForVm(scaling.getVm(), newTotalVmResource)){
            Log.printFormattedLine(
                "%.2f: %s: Vm %d requested more %d of %s capacity but the Host %d has just %d of available %s",
                scaling.getVm().getSimulation().clock(),
                scaling.getClass().getSimpleName(),
                scaling.getVm().getId(), scaling.getVm().getResource(resourceClass).getCapacity(),
                resourceClass.getSimpleName(), scaling.getVm().getHost().getId(),
                hostResource.getAvailableResource());
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

}
