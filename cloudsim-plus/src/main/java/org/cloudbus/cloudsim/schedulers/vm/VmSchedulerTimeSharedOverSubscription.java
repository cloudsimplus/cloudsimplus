/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.vms.Vm;

/**
 * A Time-Shared VM Scheduler, which allows over-subscription. In other
 * words, the scheduler still allows the allocation of VMs which require more CPU
 * capacity than is available. 
 * 
 * <p>The scheduler doesn't in fact allocates more MIPS for Virtual PEs (vPEs)
 * than there is in the physical PEs. It just reduces the allocated
 * amount according to the available MIPS.
 * This is an oversubscription, resulting in performance degradation
 * because less MIPS may be allocated than the required by a VM.
 * </p>
 * 
 * @author Anton Beloglazov
 * @author Rodrigo N. Calheiros
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 3.0
 */
public class VmSchedulerTimeSharedOverSubscription extends VmSchedulerTimeShared {
    @Override
    protected void allocateMipsShareForVm(List<Double> mipsShareRequestedReduced, Vm vm) {
        final double totalRequestedMips = mipsShareRequestedReduced.stream().reduce(0.0, Double::sum);
        if (getAvailableMips() >= totalRequestedMips) {
            super.allocateMipsShareForVm(mipsShareRequestedReduced, vm);
        } else {
            redistributeMipsDueToOverSubscription();
        }
    }
    
    /**
     * Redistribute the allocation of MIPs among all VMs when the total
     * MIPS requested by all of them is higher than the total available MIPS.
     * This way, it reduces the MIPS allocated to all VMs in order to
     * enable all MIPS requests to be fulfilled.
     * 
     * <p>Updates the Map containing the list of allocated MIPS by all VMs,
     * reducing the amount requested according to a scaling factor.
     * This is performed when the amount of total requested MIPS by all VMs
     * is higher than the total available MIPS. The reduction
     * of the MIPS requested by all VMs enables all requests to be fulfilled.</p>
     * @see #getMipsMapAllocated() 
     */
    protected void redistributeMipsDueToOverSubscription() {
        // First, we calculate the scaling factor - the MIPS allocation for all VMs will be scaled proportionally
        final Map<Vm, List<Double>> mipsMapRequestedReduced = newTotalRequiredMipsByAllVms();
        final double totalRequiredMipsByAllVms = 
                mipsMapRequestedReduced.values()
                    .stream()
                    .flatMap(list -> list.stream())
                    .mapToDouble(mips -> mips)
                    .sum();

        //the factor that will be used to reduce the amount of MIPS allocated to each vPE
        final double scalingFactor = getHost().getTotalMipsCapacity() / totalRequiredMipsByAllVms;
        
        getMipsMapAllocated().clear();
        for (final Entry<Vm, List<Double>> entry : mipsMapRequestedReduced.entrySet()) {
            final Vm vm = entry.getKey();
            List<Double> updatedMipsAllocation = getMipsShareToAllocate(entry.getValue(), vm);
            updatedMipsAllocation = getMipsShareToAllocate(updatedMipsAllocation, vm, scalingFactor);
            getMipsMapAllocated().put(vm, updatedMipsAllocation);
        }
    }

    /**
     * Generate a new Map containing the list of required MIPS by all VMs,
     * ensuring the MIPS requested for each vPE doesn't exceeds 
     * the capacity of each Physical PE.
     *
     * @return the new map of requested MIPS for all VMs
     * @see #getMipsMapRequested() 
     */
    private Map<Vm, List<Double>> newTotalRequiredMipsByAllVms() {
        final Map<Vm, List<Double>> mipsMapRequestedReduced = new HashMap<>(getMipsMapRequested().entrySet().size());
        for (final Entry<Vm, List<Double>> entry : getMipsMapRequested().entrySet()) {
            final Vm vm = entry.getKey();
            final List<Double> mipsShareRequestedReduced = getMipsShareRequestedReduced(entry.getValue());
            mipsMapRequestedReduced.put(vm, mipsShareRequestedReduced);
        }

        return mipsMapRequestedReduced;
    }
}
