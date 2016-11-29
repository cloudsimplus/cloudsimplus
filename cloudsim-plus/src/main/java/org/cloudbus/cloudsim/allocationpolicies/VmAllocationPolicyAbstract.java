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

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.HostToVmEventInfo;

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
     * @see #getVmTable()
     */
    private Map<String, Host> vmTable;

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
    private Map<String, Integer> usedPes;

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
    protected Map<String, Host> getVmTable() {
        return vmTable;
    }

    /**
     * Sets the vm table.
     *
     * @param vmTable the vm table
     */
    protected final void setVmTable(Map<String, Host> vmTable) {
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
        // if vm were succesfully created in the host
        getVmTable().put(vm.getUid(), host);
        HostToVmEventInfo info =
                new HostToVmEventInfo(host.getSimulation().clock(), host, vm);
        vm.getOnHostAllocationListener().update(info);
    }

    /**
     * Unregister the allocation of a Host to a given Vm, unmapping the Vm to
     * the Host where it was. The method has to be called when a Vm is
     * moved/removed from a Host.
     *
     * @param vm the moved/removed Vm
     * @return the Host where the Vm was removed/moved from
     */
    protected Host unmapVmFromPm(Vm vm) {
        final Host host = getVmTable().remove(vm.getUid());
        HostToVmEventInfo info =
                new HostToVmEventInfo(host.getSimulation().clock(), host, vm);
        vm.getOnHostDeallocationListener().update(info);
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
    public void setDatacenter(Datacenter datacenter){
        if(datacenter == null){
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
        getHostList().stream().forEach(host -> hostFreePesMap.put(host, host.getNumberOfPes()));
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
     * VM UID and the value is the number of used Pes for that VM.
     *
     * @return the used PEs map
     */
    protected Map<String, Integer> getUsedPes() {
        return usedPes;
    }

    /**
     * Sets the used pes.
     *
     * @param usedPes the used pes
     */
    protected final void setUsedPes(Map<String, Integer> usedPes) {
        this.usedPes = usedPes;
    }
}
