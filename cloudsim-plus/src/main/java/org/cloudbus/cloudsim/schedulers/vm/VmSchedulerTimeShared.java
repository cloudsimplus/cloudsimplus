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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

import org.cloudbus.cloudsim.lists.PeList;

/**
 * VmSchedulerTimeShared is a Virtual Machine Monitor (VMM) allocation policy
 * that allocates one or more PEs from a PM to a VM, and allows sharing of PEs
 * by multiple VMs. <b>This class also implements 10% performance degradation due
 * to VM migration. It does not support over-subscription.</b>
 *
 * <p>Each host has to use is own instance of a VmSchedulerAbstract that will so
 * schedule the allocation of host's PEs for VMs running on it.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmSchedulerTimeShared extends VmSchedulerAbstract {

    /**
     * @see #getMipsMapRequested()
     */
    private Map<String, List<Double>> mipsMapRequested;

    /**
     * The number of host's PEs in use.
     */
    private int pesInUse;

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
        /*
         * @todo add the same to RAM and BW provisioners
         */
        if (vm.isInMigration()) {
            if (!getVmsMigratingIn().contains(vm.getUid()) && !getVmsMigratingOut().contains(vm.getUid())) {
                getVmsMigratingOut().add(vm.getUid());
            }
        } else if (getVmsMigratingOut().contains(vm.getUid())) {
            getVmsMigratingOut().remove(vm.getUid());
        }
        boolean result = updateMapOfRequestedMipsForVm(vm.getUid(), mipsShareRequested);

        updatePesAllocationForAllVms();
        return result;
    }

    /**
     * Update allocation of Host PEs for all VMs.
     *
     */
    private void updatePesAllocationForAllVms() {
        clearAllocationOfPesForAllVms();

        for (Map.Entry<String, List<Double>> entry : getMipsMapAllocated().entrySet()) {
            final String vmUid = entry.getKey();
            getPeMap().put(vmUid, new LinkedList<>());
            allocatePesListForVm(entry, vmUid);
        }
    }

    /**
     * Allocates Host PEs for a given VM.
     *
     * @param entry an entry from the {@link #getMipsMapAllocated()} containing a VM UID and
     *              the list of MIPS to be allocated for each VM PE
     * @param vmUid the UID of the VM to allocate PEs for it
     * @todo @author manoelcampos The method implementation must to be checked. See the comments inside.
     */
    private void allocatePesListForVm(Map.Entry<String, List<Double>> entry, String vmUid) {
        int vmPeId = -1;
        //Iterate over the list of MIPS requested by each VM PE
        for (double requestedMipsForVmPe : entry.getValue()) {
            double allocatedMipsForVmPe = allocateMipsFromHostPesToGivenVirtualPe(vmUid, requestedMipsForVmPe);
            if(requestedMipsForVmPe > 0.1 && allocatedMipsForVmPe <= 0.1){
                Log.printFormattedLine(
                    "Vm %s is requiring a total of %.0f MIPS for its PE %d but the Host PEs currently don't have such an available MIPS amount. Only %.0f MIPS were allocated.",
                    vmUid, requestedMipsForVmPe, vmPeId, allocatedMipsForVmPe);
            }
        }
    }

    /**
     * Try to allocate MIPS from one or more Host PEs to a specific Virtual PE (PE of a VM).
     *
     * @param vmUid the UID of the VM to try to find Host PEs for one of its Virtual PEs
     * @param requestedMipsForVmPe the amount of MIPS requested by such a VM PE
     * @return the total MIPS allocated for the requested VM PE
     */
    private double allocateMipsFromHostPesToGivenVirtualPe(String vmUid, final double requestedMipsForVmPe) {
        if(requestedMipsForVmPe <= 0.1){
            return 0;
        }

        final Iterator<Pe> hostPesIterator = getPeList().iterator();
        Pe selectedHostPe = hostPesIterator.next();

        double allocatedMipsForVmPe = 0;
        /*
        * While all the requestred MIPS for the VM PE was not allocated, try to find a Host PE
        * with that MIPS amount available.
        */
        while (allocatedMipsForVmPe < 0.1) {
            if (getAvailableMipsForHostPe(selectedHostPe) >= requestedMipsForVmPe) {
                /*
                 * If the selected Host PE has enough available MIPS that is requested by the
                 * current VM PE, allocate that MIPS in that Host PE for that VM PE.
                 * For each next VM PE, in case that the same previous selected Host PE yet
                 * has available MIPS to allocate to it, that Host PE will be allocated
                 * to that next VM PE. However, for the best of my knowledge,
                 * in real virtualization, it is not possible to allocate
                 * more than one VM to the same CPU core.
                 *
                 */
                allocateMipsFromPeForVm(vmUid, selectedHostPe, requestedMipsForVmPe);
                allocatedMipsForVmPe = requestedMipsForVmPe;
            } else {
                /*
                 * If the selected Host PE doesn't have the available MIPS requested by the current
                 * VM PE, allocate the MIPS that is available in that PE for the VM PE
                 * and try to find another Host PE to allocate the remaining MIPS required by the
                 * current VM PE. However, for the best of my knowledge,
                 * in real virtualization, it is not possible to allocate
                 * more than one VM to the same CPU core. If the current Host PE doesn't have the
                 * MIPS required by the VM PE, another Host PE has to be found.
                 * Using the current implementation, the same Host PE could be used
                 * by different Hosts.
                 */

                //gets the available MIPS from Host PE before allocating it to the VM
                allocatedMipsForVmPe += getAvailableMipsForHostPe(selectedHostPe);
                allocateMipsFromPeForVm(vmUid, selectedHostPe, getAvailableMipsForHostPe(selectedHostPe));
                if (requestedMipsForVmPe >= 0.1 && !hostPesIterator.hasNext()) {
                    break;
                }

                selectedHostPe = hostPesIterator.next();
            }
        }

        return allocatedMipsForVmPe;
    }

    private double getAvailableMipsForHostPe(Pe hostPe) {
        return hostPe.getPeProvisioner().getAvailableMips();
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
        getPeList().forEach(pe -> pe.getPeProvisioner().deallocateMipsForAllVms());
    }

    @Override
    public boolean isSuitableForVm(Vm vm) {
        return getTotalCapacityToBeAllocatedToVm(vm.getCurrentRequestedMips()) > 0.0;
    }

    /**
     * Checks if the requested amount of MIPS is available to be allocated to a
     * VM
     *
     * @param vmRequestedMipsShare a VM's list of requested MIPS
     * @return the sum of total requested mips if there is enough capacity to be
     * allocated to the VM, 0 otherwise.
     */
    protected double getTotalCapacityToBeAllocatedToVm(List<Double> vmRequestedMipsShare) {
        double peMips = getPeCapacity();
        double totalRequestedMips = 0;
        for (Double mips : vmRequestedMipsShare) {
            // each virtual PE of a VM must require not more than the capacity of a physical PE
            if (mips > peMips) {
                return 0.0;
            }
            totalRequestedMips += mips;
        }

        // This scheduler does not allow over-subscription
        if (getAvailableMips() < totalRequestedMips || getPeList().size() < vmRequestedMipsShare.size()) {
            return 0.0;
        }

        return totalRequestedMips;
    }

    /**
     * Update the {@link #getMipsMapRequested()} with the list of MIPS requested by a given VM.
     *
     * @param vmUid the VM uid
     * @param mipsShareRequested the list of mips share requested by the vm
     * @return true if successful, false otherwise
     */
    protected boolean updateMapOfRequestedMipsForVm(String vmUid, List<Double> mipsShareRequested) {
        double totalRequestedMips = getTotalCapacityToBeAllocatedToVm(mipsShareRequested);
        if (totalRequestedMips == 0) {
            return false;
        }

        getMipsMapRequested().put(vmUid, mipsShareRequested);
        setPesInUse(getPesInUse() + mipsShareRequested.size());

        if (getVmsMigratingIn().contains(vmUid)) {
            // the destination host experience a percentage of CPU overhead due to migrating VM
            totalRequestedMips *= getCpuOverheadDueToVmMigration();
        }

        List<Double> mipsShareAllocated = new ArrayList<>();
        for (Double mipsRequested : mipsShareRequested) {
            if (getVmsMigratingOut().contains(vmUid)) {
                // performance degradation due to migration = 10% MIPS
                mipsRequested *= 1 - getCpuOverheadDueToVmMigration();
            } else if (getVmsMigratingIn().contains(vmUid)) {
                // the destination host only experience 10% of the migrating VM's MIPS
                mipsRequested *= getCpuOverheadDueToVmMigration();
            }
            mipsShareAllocated.add(mipsRequested);
        }

        getMipsMapAllocated().put(vmUid, mipsShareAllocated);
        setAvailableMips(getAvailableMips() - totalRequestedMips);

        return true;
    }


    /**
     * Allocates a given amount of MIPS from a specific PE for a given VM.
     * @param vmUid the VM UID to allocate the MIPS from a given PE
     * @param pe the PE that will have MIPS allocated to the VM
     * @param mipsToAllocate the amount of MIPS from the PE that have to be allocated to the VM
     */
    private void allocateMipsFromPeForVm(String vmUid, Pe pe, double mipsToAllocate) {
        pe.getPeProvisioner().allocateMipsForVm(vmUid, mipsToAllocate);
        getPeMap().get(vmUid).add(pe);
    }

    @Override
    public void deallocatePesForVm(Vm vm) {
        getMipsMapRequested().remove(vm.getUid());
        setPesInUse(0);
        getMipsMapAllocated().clear();
        setAvailableMips(PeList.getTotalMips(getPeList()));

        for (Pe pe : getPeList()) {
            pe.getPeProvisioner().deallocateMipsForVm(vm);
        }

        for (Map.Entry<String, List<Double>> entry : getMipsMapRequested().entrySet()) {
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
    protected void setPesInUse(int pesInUse) {
        this.pesInUse = pesInUse;
    }

    /**
     * Gets the number of PEs in use.
     *
     * @return the pes in use
     */
    protected int getPesInUse() {
        return pesInUse;
    }

    /**
     * Gets the map of mips requested by each VM, where each key is a VM and each value is a
     * list of MIPS requested by that VM.
     *
     * @return
     */
    protected Map<String, List<Double>> getMipsMapRequested() {
        return mipsMapRequested;
    }

    /**
     * Sets the mips map requested.
     *
     * @param mipsMapRequested the mips map requested
     */
    protected final void setMipsMapRequested(Map<String, List<Double>> mipsMapRequested) {
        this.mipsMapRequested = mipsMapRequested;
    }

    @Override
    public double getCpuOverheadDueToVmMigration() {
        return 0.1;
    }

}
