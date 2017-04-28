/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import java.util.*;
import java.util.stream.LongStream;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

import static java.util.stream.Collectors.toList;

/**
 * An abstract class for implementation of {@link VmScheduler}s.
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
    private Map<Vm, List<Pe>> peMap;

    /**
     * @see #getMipsMapAllocated()
     */
    private Map<Vm, List<Double>> mipsMapAllocated;

    /**
     * @see #getVmsMigratingIn()
     */
    private Set<Vm> vmsMigratingIn;

    /**
     * The VMs migrating out the host (departing). It is the list of VM
     */
    private Set<Vm> vmsMigratingOut;

    /**
     * Creates a VmScheduler.
     *
     * @post $none
     */
    public VmSchedulerAbstract() {
        setHost(Host.NULL);
        setVmsMigratingIn(new HashSet<>());
        setVmsMigratingOut(new HashSet<>());
    }

    @Override
    public final boolean isSuitableForVm(Vm vm) {
        return isSuitableForVm(vm.getCurrentRequestedMips());
    }

    @Override
    public boolean allocatePesForVm(Vm vm) {
        final List<Double> mipsList = LongStream.range(0, vm.getNumberOfPes()).mapToObj(i -> vm.getMips()).collect(toList());
        return allocatePesForVm(vm, mipsList);
    }

    @Override
    public void deallocatePesForAllVms() {
        getMipsMapAllocated().clear();
        getWorkingPeList().forEach(pe -> pe.getPeProvisioner().deallocateResourceForAllVms());
    }

    @Override
    public List<Pe> getPesAllocatedForVM(Vm vm) {
        return getPeMap().getOrDefault(vm, new ArrayList<>());
    }

    @Override
    public List<Double> getAllocatedMipsForVm(Vm vm) {
        return getMipsMapAllocated().getOrDefault(vm, new ArrayList<>());
    }

    @Override
    public double getTotalAllocatedMipsForVm(Vm vm) {
        return getAllocatedMipsForVm(vm).stream().mapToDouble(v -> v).sum();
    }

    @Override
    public double getMaxAvailableMips() {
        return getWorkingPeList().stream()
                .map(Pe::getPeProvisioner)
                .mapToDouble(PeProvisioner::getAvailableResource)
                .max().orElse(0.0);
    }

    @Override
    public long getPeCapacity() {
        return getWorkingPeList().stream().map(Pe::getCapacity).findFirst().orElse(0L);
    }

    @Override
    public final List<Pe> getWorkingPeList() {
        return host.getWorkingPeList();
    }

    /**
     * Gets the map of VMs to MIPS, were each key is a VM and each value is the
     * currently allocated MIPS from the respective PE to that VM. The PEs where
     * the MIPS capacity is get are defined in the {@link #peMap}.
     *
     * @return the mips map
     */
    protected Map<Vm, List<Double>> getMipsMapAllocated() {
        return mipsMapAllocated;
    }

    /**
     * Sets the map of VMs to MIPS, were each key is a VM and each value is the
     * currently allocated MIPS from the respective PE to that VM. The PEs where
     * the MIPS capacity is get are defined in the {@link #peMap}.
     *
     * @param mipsMapAllocated the mips map
     */
    protected final void setMipsMapAllocated(Map<Vm, List<Double>> mipsMapAllocated) {
        this.mipsMapAllocated = mipsMapAllocated;
    }

    @Override
    public double getAvailableMips() {
        final double totalAllocatedMips = 
            getMipsMapAllocated().values().stream()
                .flatMap(list -> list.stream())
                .reduce(0.0, Double::sum);
        return host.getTotalMipsCapacity() - totalAllocatedMips;
    }

    @Override
    public Set<Vm> getVmsMigratingIn() {
        return Collections.unmodifiableSet(vmsMigratingIn);
    }

    @Override
    public Set<Vm> getVmsMigratingOut() {
        return Collections.unmodifiableSet(vmsMigratingOut);
    }

    /**
     * Sets the vms migrating out.
     *
     * @param vmsMigratingOut the new vms migrating out
     */
    protected final void setVmsMigratingOut(Set<Vm> vmsMigratingOut) {
        this.vmsMigratingOut = Objects.isNull(vmsMigratingOut) ? new HashSet<>() : vmsMigratingOut;
    }

    /**
     * Sets the vms migrating in.
     *
     * @param vmsMigratingIn the new vms migrating in
     */
    protected final void setVmsMigratingIn(Set<Vm> vmsMigratingIn) {
        this.vmsMigratingIn = Objects.isNull(vmsMigratingIn) ? new HashSet<>() : vmsMigratingIn;
    }

    @Override
    public Map<Vm, List<Pe>> getPeMap() {
        return peMap;
    }

    /**
     * Sets the map of VMs to PEs, where each key is a VM and each value is a list
     * of PEs allocated to that VM.
     *
     * @param peMap the pe map
     */
    protected final void setPeMap(Map<Vm, List<Pe>> peMap) {
        this.peMap = peMap;
    }

    @Override
    public Host getHost() {
        return host;
    }

    @Override
    public VmScheduler setHost(Host host) {
        Objects.requireNonNull(host);

        if(isOtherHostAssigned(host)){
            throw new IllegalArgumentException("VmScheduler already has a Host assigned to it. Each Host must have its own VmScheduler instance.");
        }

        this.host = host;

        setPeMap(new HashMap<>());
        setMipsMapAllocated(new HashMap<>());

        return this;
    }


    /**
     * Checks if the {@link VmScheduler} has a {@link Host} assigned that is
     * different from the given one
     *
     * @param host the Host to check if assigned scheduler's Host is different from
     * @return
     */
    private boolean isOtherHostAssigned(Host host) {
        return !Objects.isNull(this.host) && this.host != Host.NULL && !host.equals(this.host);
    }

    @Override
    public boolean addVmMigratingIn(Vm vm) {
        return this.vmsMigratingIn.add(vm);
    }

    @Override
    public boolean addVmMigratingOut(Vm vm) {
        return this.vmsMigratingOut.add(vm);
    }

    @Override
    public boolean removeVmMigratingIn(Vm vm) {
        return this.vmsMigratingIn.remove(vm);
    }

    @Override
    public boolean removeVmMigratingOut(Vm vm) {
        return this.vmsMigratingOut.remove(vm);
    }
}
