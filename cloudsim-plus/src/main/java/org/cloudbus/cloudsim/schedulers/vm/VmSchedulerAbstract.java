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
import java.util.stream.IntStream;

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
     * Creates a VmScheduler.
     *
     * @post $none
     */
    public VmSchedulerAbstract() {
        setHost(Host.NULL);
    }

    @Override
    public final boolean isSuitableForVm(Vm vm) {
        return isSuitableForVm(vm.getCurrentRequestedMips());
    }

    @Override
    public boolean allocatePesForVm(Vm vm) {
        final List<Double> mipsList =
                LongStream.range(0, vm.getNumberOfPes())
                        .mapToObj(i -> vm.getMips())
                        .collect(toList());
        return allocatePesForVm(vm, mipsList);
    }

    @Override
    public void deallocatePesFromVm(Vm vm) {
        deallocatePesFromVm(vm, (int)vm.getNumberOfPes());
    }

    @Override
    public void deallocatePesFromVm(Vm vm, int pesToRemove) {
        if(pesToRemove <= 0 || vm.getNumberOfPes() == 0){
            return;
        }

        deallocatePesFromVmInternal(vm, pesToRemove);
    }

    /**
     * Remove a given number of PEs from a given {@code Vm -> List<PE>} Map,
     * where each PE in the List associated to each Vm may be an actual
     * {@link Pe} object or just its capacity in MIPS (Double).
     *
     * <p>In other words, the map can be {@code Map<Vm, List<Double>>}
     * or {@code Map<Vm, List<Pe>>}.</p>
     *
     * @param <T> the type of the elements into the List associated to each map key,
     *            which can be a MIPS number (Double) or an actual {@link Pe} object.
     * @param vm the VM to remove PEs from
     * @param map the map where the PEs will be removed
     * @param pesToRemove the number of PEs to remove from the List of PEs associated to the Vm
     * @return the number of removed PEs
     */
    protected <T> int removePesFromMap(Vm vm, Map<Vm, List<T>> map, int pesToRemove) {
        final List<T> values = map.getOrDefault(vm, new ArrayList<>());
        if(values.isEmpty()){
            return 0;
        }

        pesToRemove = Math.min((int)vm.getNumberOfPes(), pesToRemove);
        pesToRemove = Math.min(pesToRemove, values.size());
        IntStream.range(0, pesToRemove).forEach(i -> values.remove(0));
        if(values.isEmpty()){
            map.remove(vm);
        }

        return pesToRemove;
    }

    protected abstract void deallocatePesFromVmInternal(Vm vm, int pesToRemove);

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
            getMipsMapAllocated().keySet()
                .stream()
                .mapToDouble(this::actualVmTotalRequestedMips)
                .sum();

        return host.getTotalMipsCapacity() - totalAllocatedMips;
    }

    /**
     * Gets the sum of MIPS requested by each VM PE, including
     * the CPU overhead if the VM is in migration to this Host.
     *
     * <p>For instance, if the migration overhead is 10% and
     * the total requested MIPS of a VM is 1000 MIPS,
     * it will be allocated just 900 MIPS, but from this values, this method
     * returns the 1000 MIPS, which is the actual MIPS being
     * used by the Host (900 by the VM and 100 by migration overhead).</p>
     *
     * @param vm the VM to get the actual requested MIPS across all PEs
     * @return the actual requested MIPS sum across all VM PEs,
     * including the CPU overhead of the VM is in migration to this Host
     */
    private double actualVmTotalRequestedMips(Vm vm) {
        final double totalVmRequestedMips =
                getMipsMapAllocated()
                    .getOrDefault(vm, new ArrayList<>())
                    .stream()
                    .reduce(0.0, Double::sum);

        /*If the VM is migrating in or out this Host,
        there is a migration overhead.
        Considering the overhead is 10%,
        if it's migrating in, it's just allocated just this 10%
        for the migration process.
        If it's migrating out, it's allocated 90% for the VM,
        and 10% for the migration process.

        The line below computes the original
        requested MIPS (which correspond to 100%)
        */
        return totalVmRequestedMips / percentOfMipsToRequest(vm);
    }

    /**
     * Gets the max percentage of CPU a VM migrating into this Host can use,
     * considering the {@link #getVmMigrationCpuOverhead() CPU migration overhead}.
     * @return the max percentage of CPU usage during migration (in scale from [0 to 1], where 1 is 100%)
     */
    public double getMaxCpuUsagePercentDuringMigration() {
        return 1 - getVmMigrationCpuOverhead();
    }

    /**
     * Gets the map of VMs to PEs, where each key is a VM and each value is a list
     * of PEs allocated to that VM.
     *
     * @return
     */
    protected Map<Vm, List<Pe>> getPeMap() {
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

    /**
     * Gets the percentage of the MIPS requested by a VM
     * that will be in fact requested to the Host, according to the VM migration
     * status:
     *
     * <ul>
     *  <li>VM is migrating out of this Host: the MIPS requested by VM will be reduced
     *   according to the
     *   {@link #getVmMigrationCpuOverhead() CPU migration overhead}.
     *   The number of MIPS corresponding to the CPU overhead is used
     *   by the Host to perform the migration;</li>
     *  <li>VM is migrating into this Host: only a fraction of its requested MIPS will be
     *   in fact requested to the Host. This amount is computed by reducing the
     *   {@link #getVmMigrationCpuOverhead() CPU migration overhead};</li>
     *  <li>VM is not in migration: 100% of its requested MIPS will be
     *   in fact requested to the Host</li>
     * </ul>
     * @param vm the VM that is requesting MIPS from the Host
     * @return the percentage of MIPS requested by the VM that will be in fact
     * requested to the Host (in scale from [0 to 1], where  is 100%)
     */
    protected double percentOfMipsToRequest(Vm vm) {
        if (host.getVmsMigratingIn().contains(vm)) {
            /* While the VM is migrating in,
            the destination host only increases CPU usage according
            to the CPU migration overhead.
             */
            return getVmMigrationCpuOverhead();
        }

        if (host.getVmsMigratingOut().contains(vm)) {
            /* While the VM is migrating out, the host where it's migrating from
            experiences a performance degradation.
            Thus, the allocated MIPS for that VM is reduced according to the CPU migration
            overhead.*/
            return getMaxCpuUsagePercentDuringMigration();
        }

        //VM is not migrating, thus 100% of its requested MIPS will be requested to the Host.
        return 1;
    }

    /**
     * Checks if the requested amount of MIPS is available to be allocated to a
     * VM.
     *
     * @param vmRequestedMipsShare a list of MIPS requested by a VM
     * @return true if the requested MIPS List is available, false otherwise
     */
    @Override
    public boolean isAllowedToAllocateMips(List<Double> vmRequestedMipsShare) {
        final double pmMips = getPeCapacity();
        double totalRequestedMips = 0;
        for (final double vmMips : vmRequestedMipsShare) {
            // each virtual PE of a VM must require not more than the capacity of a physical PE
            if (vmMips > pmMips) {
                return false;
            }
            totalRequestedMips += vmMips;
        }

        // This scheduler does not allow over-subscription
        if (getAvailableMips() < totalRequestedMips || getWorkingPeList().size() < vmRequestedMipsShare.size()) {
            return false;
        }

        return true;
    }
}
