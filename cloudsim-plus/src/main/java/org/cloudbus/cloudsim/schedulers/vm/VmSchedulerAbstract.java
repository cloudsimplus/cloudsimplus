/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import java.util.*;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.Log;
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
     * Creates a VmScheduler.
     *
     * @post $none
     */
    public VmSchedulerAbstract() {
        setHost(Host.NULL);
        setVmsMigratingIn(new ArrayList<>());
        setVmsMigratingOut(new ArrayList<>());
    }

    @Override
    public void deallocatePesForAllVms() {
        getMipsMapAllocated().clear();
        setAvailableMips(PeList.getTotalMips(getPeList()));
        getPeList().forEach(pe -> pe.getPeProvisioner().deallocateMipsForAllVms());
    }

    @Override
    public List<Pe> getPesAllocatedForVM(Vm vm) {
        getPeMap().putIfAbsent(vm.getUid(), new ArrayList<>());
        return getPeMap().get(vm.getUid());
    }

    @Override
    public List<Double> getAllocatedMipsForVm(Vm vm) {
        getMipsMapAllocated().putIfAbsent(vm.getUid(), new ArrayList<>());
        return getMipsMapAllocated().get(vm.getUid());
    }

    @Override
    public double getTotalAllocatedMipsForVm(Vm vm) {
        return getAllocatedMipsForVm(vm).stream().reduce(0.0, Double::sum);
    }

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

    @Override
    public double getPeCapacity() {
        if (getPeList().isEmpty()) {
            Log.printLine("Pe list is empty");
            return 0;
        }

        return getPeList().get(0).getMips();
    }

    @Override
    public final List<Pe> getPeList() {
        return host.getPeList();
    }

    /**
     * Gets the map of VMs to MIPS, were each key is a VM UID and each value is the
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

    @Override
    public double getAvailableMips() {
        return availableMips;
    }

    /**
     * Sets the amount of mips that is free.
     *
     * @param availableMips the new free mips amount
     */
    protected final void setAvailableMips(double availableMips) {
        this.availableMips = availableMips;
    }

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
        if(Objects.isNull(host)){
            host = Host.NULL;
        }

        this.host = host;

        setPeMap(new HashMap<>());
        setMipsMapAllocated(new HashMap<>());
        setAvailableMips(PeList.getTotalMips(getPeList()));

        return this;
    }
}
