/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

import org.cloudbus.cloudsim.lists.PeList;

/**
 * VmSchedulerAbstract is an abstract class that represents the policy used by a
 * Virtual Machine Monitor (VMM) to share processing power of a PM among VMs
 * running in a host. Each host has to use is own instance of a
 * class that extends VmSchedulerAbstract that will so schedule the allocation of host's PEs for
 * VMs running on it.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public abstract class VmSchedulerAbstract implements VmScheduler {
    /**
     * @see #getHost()
     */
    private Host host;

    /**
     * @see #getPeMap()
     */
    private Map<String, List<Pe>> peMap;

    /**
     * @see #getMipsMapAllocated()
     */
    private Map<String, List<Double>> mipsMapAllocated;

    /**
     * The total available MIPS that can be allocated on demand for VMs.
     */
    private double availableMips;

    /**
     * The VMs migrating in the host (arriving). It is the list of VM ids
     */
    private List<String> vmsMigratingIn;

    /**
     * The VMs migrating out the host (departing). It is the list of VM ids
     */
    private List<String> vmsMigratingOut;

    /**
     * Creates a new VmScheduler.
     *
     * @post $none
     */
    public VmSchedulerAbstract() {
        setHost(Host.NULL);
        setVmsMigratingIn(new ArrayList<>());
        setVmsMigratingOut(new ArrayList<>());
    }

    /**
     * Releases PEs allocated to all the VMs of the host the VmSchedulerAbstract
     * is associated to. After that, all PEs will be available to be used on
     * demand for requesting VMs.
     *
     * @pre $none
     * @post $none
     */
    @Override
    public void deallocatePesForAllVms() {
        getMipsMapAllocated().clear();
        setAvailableMips(PeList.getTotalMips(getPeList()));
        getPeList().forEach(pe -> pe.getPeProvisioner().deallocateMipsForAllVms());
    }

    /**
     * Gets the pes allocated for a vm.
     *
     * @param vm the VM
     * @return the PEs allocated for the given VM
     * or an immutable empty list if there aren't allocated PEs
     * for it
     */
    @Override
    public List<Pe> getPesAllocatedForVM(Vm vm) {
        List<Pe> list = getPeMap().get(vm.getUid());
        if(list == null)
            return Collections.EMPTY_LIST;
        return list;
    }

    /**
     * Returns the MIPS share of each host's Pe that is allocated to a given VM.
     *
     * @param vm the VM
     * @return an array containing the amount of MIPS of each PE that is
     * available to the VM or an immutable empty list if there aren't allocated PEs
     * for the given VM
     * @pre $none
     * @post $none
     */
    @Override
    public List<Double> getAllocatedMipsForVm(Vm vm) {
        final List<Double> list = getMipsMapAllocated().get(vm.getUid());
        if(list == null)
            return Collections.EMPTY_LIST;

        return list;
    }

    /**
     * Gets the total allocated MIPS for a VM along all its allocated PEs.
     *
     * @param vm the vm
     * @return the total allocated mips for the vm
     */
    @Override
    public double getTotalAllocatedMipsForVm(Vm vm) {
        return getAllocatedMipsForVm(vm).stream().reduce(0.0, Double::sum);
    }

    /**
     * Returns maximum available MIPS among all the host's PEs.
     *
     * @return max mips
     */
    @Override
    public double getMaxAvailableMips() {
        if (getPeList().isEmpty()) {
            Log.printLine("Pe list is empty");
            return 0;
        }

        return getPeList().stream()
                    .map(Pe::getPeProvisioner)
                    .mapToDouble(PeProvisioner::getAvailableMips)
                    .max().orElse(0.0);
    }

    /**
     * Returns PE capacity in MIPS.
     *
     * @return mips
     * @todo It considers that all PEs have the same capacity, what has been
     * shown doesn't be assured. The peList received by the VmScheduler can be
     * heterogeneous PEs.
     */
    @Override
    public double getPeCapacity() {
        if (getPeList().isEmpty()) {
            Log.printLine("Pe list is empty");
            return 0;
        }

        return getPeList().get(0).getMips();
    }

    /**
     * Gets the list of PEs from the Host.
     * @return
     */
    @Override
    public final List<Pe> getPeList() {
        return host.getPeList();
    }

    /**
     * Gets the map of VMs to MIPS, were each key is a VM id and each value is the
     * currently allocated MIPS from the respective PE to that VM. The PEs where
     * the MIPS capacity is get are defined in the {@link #peMap}.
     *
     * @return the mips map
     */
    protected Map<String, List<Double>> getMipsMapAllocated() {
        return mipsMapAllocated;
    }

    /**
     * Sets the map of VMs to MIPS, were each key is a VM id and each value is the
     * currently allocated MIPS from the respective PE to that VM. The PEs where
     * the MIPS capacity is get are defined in the {@link #peMap}.
     *
     * @param mipsMapAllocated the mips map
     */
    protected final void setMipsMapAllocated(Map<String, List<Double>> mipsMapAllocated) {
        this.mipsMapAllocated = mipsMapAllocated;
    }

    /**
     * Gets the free mips.
     *
     * @return the free mips
     */
    @Override
    public double getAvailableMips() {
        return availableMips;
    }

    /**
     * Sets the free mips.
     *
     * @param availableMips the new free mips
     */
    protected final void setAvailableMips(double availableMips) {
        this.availableMips = availableMips;
    }

    /**
     * Gets the vms migrating out.
     *
     * @return the vms in migration
     */
    @Override
    public List<String> getVmsMigratingOut() {
        return vmsMigratingOut;
    }

    /**
     * Sets the vms migrating out.
     *
     * @param vmsInMigration the new vms migrating out
     */
    protected final void setVmsMigratingOut(List<String> vmsInMigration) {
        vmsMigratingOut = vmsInMigration;
    }

    /**
     * Gets the vms migrating in.
     *
     * @return the vms migrating in
     */
    @Override
    public List<String> getVmsMigratingIn() {
        return vmsMigratingIn;
    }

    /**
     * Sets the vms migrating in.
     *
     * @param vmsMigratingIn the new vms migrating in
     */
    protected final void setVmsMigratingIn(List<String> vmsMigratingIn) {
        this.vmsMigratingIn = vmsMigratingIn;
    }

    /**
     * Gets the map of VMs to PEs, where each key is a VM id and each value is a list
     * of PEs allocated to that VM.
     *
     * @return the pe map
     */
    @Override
    public Map<String, List<Pe>> getPeMap() {
        return peMap;
    }

    /**
     * Sets the map of VMs to PEs, where each key is a VM id and each value is a list
     * of PEs allocated to that VM.
     *
     * @param peMap the pe map
     */
    protected final void setPeMap(Map<String, List<Pe>> peMap) {
        this.peMap = peMap;
    }

    @Override
    public Host getHost() {
        return host;
    }

    @Override
    public VmScheduler setHost(Host host) {
        if(host == null){
            host = Host.NULL;
        }

        this.host = host;

        setPeMap(new HashMap<>());
        setMipsMapAllocated(new HashMap<>());
        setAvailableMips(PeList.getTotalMips(getPeList()));

        return this;
    }
}
