/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * VmSchedulerTimeShared is a Virtual Machine Monitor (VMM), also called Hypervisor,
 * that defines a policy to allocate one or more PEs from a PM to a VM, and allows sharing of PEs
 * by multiple VMs. <b>This class also implements 10% performance degradation due
 * to VM migration. It does not support over-subscription.</b>
 *
 * <p>Each host has to use is own instance of a VmScheduler that will so
 * schedule the allocation of host's PEs for VMs running on it.</p>
 *
 * <p>
 * It does not perform a preemption process in order to move running
 * VMs to the waiting list in order to make room for other already waiting
 * VMs to run. It just imposes there is not waiting VMs,
 * <b>oversimplifying</b> the scheduling, considering that for a given simulation
 * second <tt>t</tt>, the total processing capacity of the processor cores (in
 * MIPS) is equally divided by the VMs that are using them.
 * </p>
 *
 * <p>In processors enabled with <a href="https://en.wikipedia.org/wiki/Hyper-threading">Hyper-threading technology (HT)</a>,
 * it is possible to run up to 2 processes at the same physical CPU core.
 * However, this scheduler implementation
 * oversimplifies a possible HT feature by allowing several VMs to use a fraction of the MIPS capacity from
 * physical PEs, until that the total capacity of the virtual PE is allocated.
 * Consider that a virtual PE is requiring 1000 MIPS but there is no physical PE
 * with such a capacity. The scheduler will allocate these 1000 MIPS across several physical PEs,
 * for instance, by allocating 500 MIPS from PE 0, 300 from PE 1 and 200 from PE 2, totaling the 1000 MIPS required
 * by the virtual PE.
 * </p>
 *
 * <p>In a real hypervisor in a Host that has Hyper-threading CPU cores, two virtual PEs can be
 * allocated to the same physical PE, but a single virtual PE must be allocated to just one physical PE.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmSchedulerTimeShared extends VmSchedulerAbstract {

    /**
     * @see #getMipsMapRequested()
     */
    private Map<Vm, List<Double>> mipsMapRequested;

    /**
     * The number of host's PEs in use.
     */
    private long pesInUse;

    /**
     * Creates a vm time-shared scheduler.
     *
     */
    public VmSchedulerTimeShared() {
        super();
        setMipsMapRequested(new HashMap<>());
    }

    @Override
    public boolean allocatePesForVm(Vm vm, List<Double> mipsShareRequested) {
        if (vm.isInMigration()) {
            if (!getVmsMigratingIn().contains(vm) && !getVmsMigratingOut().contains(vm)) {
                addVmMigratingOut(vm);
            }
        } else if (getVmsMigratingOut().contains(vm)) {
            removeVmMigratingOut(vm);
        }
        final boolean result = updateMapOfRequestedMipsForVm(vm, mipsShareRequested);

        updatePesAllocationForAllVms();
        return result;
    }

    /**
     * Update allocation of Host PEs for all VMs.
     */
    private void updatePesAllocationForAllVms() {
        clearAllocationOfPesForAllVms();
        getMipsMapAllocated().entrySet().forEach(this::allocatePesListForVm);
    }

    /**
     * Clear the allocation of any PE for all VMs in order to start a new allocation.
     * By this way, a PE that was previously allocated to a given VM will be released
     * and when the new allocation is performed, a different list of PEs can be alocated
     * that VM.
     * @see #updatePesAllocationForAllVms()
     */
    private void clearAllocationOfPesForAllVms() {
        getPeMap().clear();
        getWorkingPeList().forEach(pe -> pe.getPeProvisioner().deallocateResourceForAllVms());

        for (final Map.Entry<Vm, List<Double>> entry : getMipsMapAllocated().entrySet()) {
            final Vm vm = entry.getKey();
            getPeMap().put(vm, new ArrayList<>(entry.getValue().size()));
        }
    }

    /**
     * Allocates Host PEs for a given VM.
     * @param entry an entry from the {@link #getMipsMapAllocated()} containing a VM and
     *              the list of MIPS to be allocated for each of its PEs
     */
    private void allocatePesListForVm(Map.Entry<Vm, List<Double>> entry) {
        final Vm vm = entry.getKey();
        final Iterator<Pe> hostPesIterator = getWorkingPeList().iterator();
        //Iterate over the list of MIPS requested by each VM PE
        for (final double requestedMipsForVmPe : entry.getValue()) {
            final double allocatedMipsForVmPe = allocateMipsFromHostPesToGivenVirtualPe(vm, requestedMipsForVmPe, hostPesIterator);
            if(requestedMipsForVmPe > 0.1 && allocatedMipsForVmPe <= 0.1){
                Log.printFormattedLine(
                    "%s is requiring a total of %.0f MIPS but the PEs of %s currently don't have such an available MIPS amount. Only %.0f MIPS were allocated.",
                    vm, requestedMipsForVmPe, getHost(), allocatedMipsForVmPe);
            }
        }
    }

    /**
     * Try to allocate MIPS from one or more Host PEs to a specific Virtual PE (PE of a VM).
     *
     * @param vm the VM to try to find Host PEs for one of its Virtual PEs
     * @param requestedMipsForVmPe the amount of MIPS requested by such a VM PE
     * @param hostPesIterator a interator over the PEs of the {@link #getHost() Host} that the schedler will
     *                        iterate over to allocate PEs for a VM
     * @return the total MIPS allocated from one or more Host PEs for the requested VM PE
     *
     * @TODO @author manoelcampos The method implementation must to be checked. See the comments inside.
     * Probably there was performed an oversimplification when implementing this method,
     * as it as made for CloudletSchedulerTimeShared class.
     *
     */
    private double allocateMipsFromHostPesToGivenVirtualPe(Vm vm, final double requestedMipsForVmPe, Iterator<Pe> hostPesIterator) {
        if(requestedMipsForVmPe <= 0){
            return 0;
        }

        Pe selectedHostPe;

        double allocatedMipsForVmPe = 0;
        /*
        * While all the requested MIPS for the VM PE was not allocated, try to find a Host PE
        * with that MIPS amount available.
        */
        while (allocatedMipsForVmPe <= 0 && hostPesIterator.hasNext()) {
            selectedHostPe = hostPesIterator.next();
            if (getAvailableMipsForHostPe(selectedHostPe) >= requestedMipsForVmPe) {
                /*
                 * If the selected Host PE has enough available MIPS that is requested by the
                 * current VM PE (Virtual PE, vPE or vCore), allocate that MIPS in that Host PE for that vPE.
                 * For each next vPE, in case that the same previous selected Host PE yet
                 * has available MIPS to allocate to it, that Host PE will be allocated
                 * to that next vPE. However, for the best of my knowledge,
                 * in real scheduling, it is not possible to allocate
                 * more than one VM to the same CPU core.
                 * The last picture in the following article makes it clear:
                 * https://support.rackspace.com/how-to/numa-vnuma-and-cpu-scheduling/
                 *
                 */
                allocateMipsFromHostPeForVm(vm, selectedHostPe, requestedMipsForVmPe);
                allocatedMipsForVmPe = requestedMipsForVmPe;
            } else if (getAvailableMipsForHostPe(selectedHostPe) > 0){
                /*
                 * If the selected Host PE doesn't have the available MIPS requested by the current
                 * vPE, allocate the MIPS that is available in that PE for the vPE
                 * and try to find another Host PE to allocate the remaining MIPS required by the
                 * current vPE. However, for the best of my knowledge,
                 * in real scheduling, it is not possible to allocate
                 * more than one VM to the same CPU core. If the current Host PE doesn't have the
                 * MIPS required by the vPE, another Host PE has to be found.
                 * Using the current implementation, the same Host PE could be used
                 * by different Hosts.
                 */

                //gets the available MIPS from Host PE before allocating it to the VM
                allocatedMipsForVmPe += getAvailableMipsForHostPe(selectedHostPe);
                allocateMipsFromHostPeForVm(vm, selectedHostPe, getAvailableMipsForHostPe(selectedHostPe));
                if (requestedMipsForVmPe > 0 && !hostPesIterator.hasNext()) {
                    break;
                }
            }
        }

        return allocatedMipsForVmPe;
    }

    private long getAvailableMipsForHostPe(Pe hostPe) {
        return hostPe.getPeProvisioner().getAvailableResource();
    }

    /**
     * Allocates a given amount of MIPS from a specific PE for a given VM.
     * @param vm the VM to allocate the MIPS from a given PE
     * @param pe the PE that will have MIPS allocated to the VM
     * @param mipsToAllocate the amount of MIPS from the PE that have to be allocated to the VM
     */
    private void allocateMipsFromHostPeForVm(Vm vm, Pe pe, double mipsToAllocate) {
        pe.getPeProvisioner().allocateResourceForVm(vm, (long)mipsToAllocate);
        getPeMap().get(vm).add(pe);
    }

    @Override
    public boolean isSuitableForVm(List<Double> vmMipsList) {
        return getTotalCapacityToBeAllocatedToVm(vmMipsList) > 0.0;
    }

    /**
     * Checks if the requested amount of MIPS is available to be allocated to a
     * VM.
     *
     * @param vmRequestedMipsShare a VM's list of requested MIPS
     * @return the sum of total requested mips if there is enough capacity to be
     * allocated to the VM, 0 otherwise.
     */
    protected double getTotalCapacityToBeAllocatedToVm(List<Double> vmRequestedMipsShare) {
        final double pmMips = getPeCapacity();
        double totalRequestedMips = 0;
        for (final double vmMips : vmRequestedMipsShare) {
            // each virtual PE of a VM must require not more than the capacity of a physical PE
            if (vmMips > pmMips) {
                return 0;
            }
            totalRequestedMips += vmMips;
        }

        // This scheduler does not allow over-subscription
        if (getAvailableMips() < totalRequestedMips || getWorkingPeList().size() < vmRequestedMipsShare.size()) {
            return 0.0;
        }

        return totalRequestedMips;
    }

    /**
     * Update the {@link #getMipsMapRequested()} with the list of MIPS requested by a given VM.
     *
     * @param vm the VM
     * @param mipsShareRequested the list of mips share requested by the vm
     * @return true if successful, false otherwise
     */
    protected boolean updateMapOfRequestedMipsForVm(Vm vm, List<Double> mipsShareRequested) {
        if (getTotalCapacityToBeAllocatedToVm(mipsShareRequested) == 0) {
            return false;
        }

        getMipsMapRequested().put(vm, mipsShareRequested);
        setPesInUse(getPesInUse() + mipsShareRequested.size());

        allocateMipsShareForVm(getMipsShareRequestedReduced(mipsShareRequested), vm);

        return true;
    }

    /**
     * Performs the allocation of a MIPS List to a given VM.
     * The actual MIPS to be allocated to the VM may be reduced
     * if the VM is in migration, due to migration overhead.
     * 
     * @param mipsShareRequestedReduced the list of MIPS to allocate to the VM,
     * after it being adjusted by the {@link #getMipsShareRequestedReduced(java.util.List)} method.
     * @param vm the VM to allocate MIPS to
     * @see #getMipsShareRequestedReduced(java.util.List) 
     */
    protected void allocateMipsShareForVm(List<Double> mipsShareRequestedReduced, Vm vm) {
        final List<Double> mipsShare = getMipsShareToAllocate(mipsShareRequestedReduced, vm);
        getMipsMapAllocated().put(vm, mipsShare);
    }
    
    /**
     * Adjusts a List of MIPS requested by a VM, reducing every MIPS which is higher
     * than the {@link #getPeCapacity() capacity of each physical PE} to that value.
     * 
     * @param mipsShareRequested the VM requested MIPS List
     * @return the VM requested MIPS List without MIPS higher than the PE capacity. 
     */
    protected List<Double> getMipsShareRequestedReduced(List<Double> mipsShareRequested){
        final double peMips = getPeCapacity();
        return mipsShareRequested.stream()
                .map(mips -> Math.min(mips, peMips))
                .collect(toList());
    }    

    /**
     * Gets the actual MIPS that will be allocated to each vPE (Virtual PE),
     * considering the VM migration status.
     * If the VM is in migration, this will cause overhead, reducing
     * the amount of MIPS allocated to the VM.
     * 
     * @param mipsShareRequested the list of MIPS requested for each vPE
     * @param vm the VM requesting allocation of MIPS
     * @return the List of MIPS allocated to the VM
     */
    protected List<Double> getMipsShareToAllocate(List<Double> mipsShareRequested, Vm vm) {
        return getMipsShareToAllocate(mipsShareRequested, vm, percentOfMipsToRequest(vm));
    }
    
    /**
     * Gets the actual MIPS that will be allocated to each vPE (Virtual PE),
     * considering the VM migration status.
     * If the VM is in migration, this will cause overhead, reducing
     * the amount of MIPS allocated to the VM.
     * 
     * @param mipsShareRequested the list of MIPS requested for each vPE
     * @param vm the VM requesting allocation of MIPS
     * @param scalingFactor the factor that will be used to reduce the amount of MIPS
     * allocated to each vPE (which is a percentage value between [0 .. 1])
     * @return the List of MIPS allocated to the VM
     */
    protected List<Double> getMipsShareToAllocate(List<Double> mipsShareRequested, Vm vm, double scalingFactor) {
        return mipsShareRequested
                .stream()
                .map(mips -> mips*scalingFactor)
                .collect(toList());
    }    

    @Override
    protected void deallocatePesFromVmInternal(Vm vm, int pesToRemove) {        
        final int removedPes = removePesFromMap(vm, getMipsMapRequested(), pesToRemove);
        setPesInUse(pesInUse - removedPes);
        removePesFromMap(vm, getMipsMapAllocated(), pesToRemove);
        
        for (final Map.Entry<Vm, List<Double>> entry : getMipsMapRequested().entrySet()) {
            updateMapOfRequestedMipsForVm(entry.getKey(), entry.getValue());
        }

        updatePesAllocationForAllVms();
    }  

    /**
     * Releases PEs allocated to all the VMs.
     *
     * @pre $none
     * @post $none
     */
    @Override
    public void deallocatePesForAllVms() {
        super.deallocatePesForAllVms();
        getMipsMapRequested().clear();
        setPesInUse(0);
    }

    /**
     * Returns maximum available MIPS among all the PEs. For the time shared
     * policy it is just all the avaiable MIPS.
     *
     * @return max mips
     */
    @Override
    public double getMaxAvailableMips() {
        return getAvailableMips();
    }

    /**
     * Sets the number of PEs in use.
     *
     * @param pesInUse the new pes in use
     */
    protected void setPesInUse(long pesInUse) {
        this.pesInUse = Math.max(pesInUse, 0);
    }

    /**
     * Gets the number of PEs in use.
     *
     * @return the pes in use
     */
    protected long getPesInUse() {
        return pesInUse;
    }

    /**
     * Gets the map of mips requested by each VM, where each key is a VM and each value is a
     * list of MIPS requested by that VM.
     *
     * @return
     */
    protected Map<Vm, List<Double>> getMipsMapRequested() {
        return mipsMapRequested;
    }

    /**
     * Sets the mips map requested.
     *
     * @param mipsMapRequested the mips map requested
     */
    protected final void setMipsMapRequested(Map<Vm, List<Double>> mipsMapRequested) {
        this.mipsMapRequested = mipsMapRequested;
    }

    @Override
    public double getVmMigrationCpuOverhead() {
        return 0.1;
    }
}
