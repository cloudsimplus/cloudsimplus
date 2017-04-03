/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * This is a Time-Shared VM Scheduler, which allows over-subscription. In other
 * words, the scheduler still allows the allocation of VMs that require more CPU
 * capacity than is available. Oversubscription results in performance
 * degradation.
 *
 * @author Anton Beloglazov
 * @author Rodrigo N. Calheiros
 * @since CloudSim Toolkit 3.0
 */
public class VmSchedulerTimeSharedOverSubscription extends VmSchedulerTimeShared {
    /**
     * Allocates PEs for vm. The policy allows over-subscription. In other
     * words, the policy still allows the allocation of VMs that require more
     * CPU capacity than is available. Oversubscription results in performance
     * degradation. It cannot be allocated more CPU capacity for each virtual PE
     * than the MIPS capacity of a single physical PE.
     *
     * @param vm the vm
     * @param mipsShareRequested the list of mips share requested
     * @return true, if successful
     */
    @Override
    protected boolean updateMapOfRequestedMipsForVm(Vm vm, List<Double> mipsShareRequested) {
        double totalRequestedMips = 0;

        // if the requested mips is bigger than the capacity of a single PE, we cap
        // the request to the PE's capacity
        final List<Double> mipsShareRequestedCapped = new ArrayList<>();
        final double peMips = getPeCapacity();
        for (final double mips : mipsShareRequested) {
            if (mips > peMips) {
                mipsShareRequestedCapped.add(peMips);
                totalRequestedMips += peMips;
            } else {
                mipsShareRequestedCapped.add(mips);
                totalRequestedMips += mips;
            }
        }

        getMipsMapRequested().put(vm, mipsShareRequested);
        setPesInUse(getPesInUse() + mipsShareRequested.size());

        if (getVmsMigratingIn().contains(vm)) {
            // the destination host only experience 10% of the migrating VM's MIPS
            totalRequestedMips *= 0.1;
        }

        if (getAvailableMips() >= totalRequestedMips) {
            final List<Double> mipsShareAllocated = new ArrayList<>();
            for (final double mipsRequested : mipsShareRequestedCapped) {
                if (getVmsMigratingOut().contains(vm)) {
                    // performance degradation due to migration = 10% MIPS
                    mipsShareAllocated.add(mipsRequested * 0.9);
                } else if (getVmsMigratingIn().contains(vm)) {
                    // the destination host only experience 10% of the migrating VM's MIPS
                    mipsShareAllocated.add(mipsRequested * 0.1);
                }
                else mipsShareAllocated.add(mipsRequested);
            }

            getMipsMapAllocated().put(vm, mipsShareAllocated);
            setAvailableMips(getAvailableMips() - totalRequestedMips);
        } else {
            redistributeMipsDueToOverSubscription();
        }

        return true;
    }

    /**
     * Recalculates distribution of MIPs among VMs, considering eventual
     * shortage of MIPS compared to the amount requested by VMs.
     */
    protected void redistributeMipsDueToOverSubscription() {
        // First, we calculate the scaling factor - the MIPS allocation for all VMs will be scaled proportionally
        final Map<Vm, List<Double>> mipsMapCap = new HashMap<>();
        final double totalRequiredMipsByAllVms = getTotalRequiredMipsByAllVms(mipsMapCap);

        final double totalAvailableMips = PeList.getTotalMips(getPeList());
        final double scalingFactor = totalAvailableMips / totalRequiredMipsByAllVms;

        updateActualMipsAllocatedToVms(mipsMapCap, scalingFactor);

        // As the host is oversubscribed, there is no more available MIPS
        setAvailableMips(0);
    }

    private void updateActualMipsAllocatedToVms(Map<Vm, List<Double>> mipsMapCap, double scalingFactor) {
        // Clear the old MIPS allocation
        getMipsMapAllocated().clear();
        for (final Entry<Vm, List<Double>> entry : mipsMapCap.entrySet()) {
            final Vm vm = entry.getKey();
            final List<Double> requestedMips = entry.getValue();

            final List<Double> updatedMipsAllocation = new ArrayList<>();
            for (final double mips : requestedMips) {
                if (getVmsMigratingOut().contains(vm)) {
                    /*the original amount is scaled
                    then, performance 10% degradation due to migration is applied*/
                    updatedMipsAllocation.add(mips * scalingFactor * 0.9);
                } else if (getVmsMigratingIn().contains(vm)) {
                    /*the destination host only experiences 10% of the migrating VM's MIPS
                    then, the final 10% of the requested MIPS are scaled*/
                    updatedMipsAllocation.add(mips * 0.1 * scalingFactor);
                } else {
                    updatedMipsAllocation.add(mips * scalingFactor);
                }
            }

            // add in the new map
            getMipsMapAllocated().put(vm, updatedMipsAllocation);
        }
    }

    /**
     * Gets the total of MIPS required by all VMs and fills a map of MIPS cap
     * for each VM, that is, the physical MIPS capacity that each VM can use
     * for each virtual PE.
     *
     * @param mipsMapCap the map of MIPS cap to be filled
     * @return the total of MIPS required by all VMs
     */
    private double getTotalRequiredMipsByAllVms(Map<Vm, List<Double>> mipsMapCap) {
        double requiredMipsByAllVms = 0;
        for (final Entry<Vm, List<Double>> entry : getMipsMapRequested().entrySet()) {
            double requiredMipsByThisVm = 0.0;
            final Vm vm = entry.getKey();
            final List<Double> mipsShareRequested = entry.getValue();
            final List<Double> mipsShareRequestedCapped = new ArrayList<>();
            final double peMips = getPeCapacity();
            for (final double mips : mipsShareRequested) {
                if (mips > peMips) {
                    mipsShareRequestedCapped.add(peMips);
                    requiredMipsByThisVm += peMips;
                } else {
                    mipsShareRequestedCapped.add(mips);
                    requiredMipsByThisVm += mips;
                }
            }

            mipsMapCap.put(vm, mipsShareRequestedCapped);

            if (getVmsMigratingIn().contains(entry.getKey())) {
                // the destination host only experience 10% of the migrating VM's MIPS
                requiredMipsByThisVm *= 0.1;
            }
            requiredMipsByAllVms += requiredMipsByThisVm;
        }

        return requiredMipsByAllVms;
    }
}
