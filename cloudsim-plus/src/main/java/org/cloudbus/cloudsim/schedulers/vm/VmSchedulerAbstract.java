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
                .mapToDouble(this::actualVmRequestedMipsSum)
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
    private double actualVmRequestedMipsSum(Vm vm) {
        final double totalVmRequestedMips = 
                getMipsMapAllocated()
                    .getOrDefault(vm, new ArrayList<>())
                    .stream()
                    .reduce(0.0, Double::sum);
        
        if(getVmsMigratingIn().contains(vm)){
            /*If the VM is migrating into this Host,
            due to migration overhead, it is just allocated a fraction of the 
            requested MIPS (such as 90%). The line below computes the original
            requested MIPS (which correspond to 100%)*/
            return totalVmRequestedMips / getMaxCpuUsagePercentDuringMigration();
        }
        
        return totalVmRequestedMips;
    }
    
    /**
     * Gets the max percentage of CPU a VM migrating into this Host can use,
     * considering the {@link #getVmMigrationCpuOverhead() CPU migration overhead}.
     * @return the max percentage of CPU usage during migration (in scale from [0 to 1], where 1 is 100%)
     */
    private double getMaxCpuUsagePercentDuringMigration() {
        return 1 - getVmMigrationCpuOverhead();
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
        if (getVmsMigratingOut().contains(vm)) {
            /* While the VM is migrating out, the host where it's migrating from
            experiences a performance degradation.
            Thus, the allocated MIPS for that VM is reduced according to the CPU migration
            overhead.*/
            return getMaxCpuUsagePercentDuringMigration();
        }
        if (getVmsMigratingIn().contains(vm)) {
            /* While the VM is migrating in,
            the destination host only increases CPU usage according
            to the CPU migration overhead.
             */
            return getVmMigrationCpuOverhead();
        }
        //VM is not migrating, thus 100% of its requested MIPS will be requested to the Host.
        return 1;
    }

    
}
