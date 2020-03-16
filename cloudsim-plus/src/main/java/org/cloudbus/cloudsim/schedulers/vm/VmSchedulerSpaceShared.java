/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * VmSchedulerSpaceShared is a VMM allocation policy that allocates one or more
 * PEs from a host to a Virtual Machine Monitor (VMM), and doesn't allow sharing
 * of PEs. The allocated PEs will be used until the VM finishes running. If
 * there is no enough free PEs as required by a VM, or whether the available PEs
 * doesn't have enough capacity, the allocation fails. In the case of fail, no
 * PE is allocated to the requesting VM.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class VmSchedulerSpaceShared extends VmSchedulerAbstract {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmSchedulerSpaceShared.class.getSimpleName());

    /**
     * Creates a space-shared VM scheduler.
     *
     */
    public VmSchedulerSpaceShared() {
        this(DEFAULT_VM_MIGRATION_CPU_OVERHEAD);
    }

    /**
     * Creates a space-shared VM scheduler, defining a CPU overhead for VM migration.
     *
     * @param vmMigrationCpuOverhead the percentage of Host's CPU usage increase when a
     * VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).
     */
    public VmSchedulerSpaceShared(final double vmMigrationCpuOverhead){
        super(vmMigrationCpuOverhead);
    }

    @Override
    protected boolean isSuitableForVmInternal(final Vm vm, final List<Double> requestedMips) {
        final List<Pe> selectedPes = getTotalCapacityToBeAllocatedToVm(requestedMips);
        return selectedPes.size() >= requestedMips.size();
    }

    /**
     * Checks if the requested amount of MIPS is available to be allocated to a VM
     * @param requestedMips a list of MIPS requested by a VM
     * @return the list of PEs that may be allocated to the VM. If the size of this list is
     *         lower than the size of the requestedMips, it means there aren't enough PEs
     *         with requested MIPS to be allocated to the VM
     */
    private List<Pe> getTotalCapacityToBeAllocatedToVm(final List<Double> requestedMips) {
        if (getHost().getWorkingPesNumber() < requestedMips.size()) {
            return getHost().getWorkingPeList();
        }

        final List<Pe> freePeList = getHost().getFreePeList();
        final List<Pe> selectedPes = new ArrayList<>();
        if(freePeList.isEmpty()){
            return selectedPes;
        }

        final Iterator<Pe> peIterator = freePeList.iterator();
        Pe pe = peIterator.next();
        for (final double mips : requestedMips) {
            if (mips <= pe.getCapacity()) {
                selectedPes.add(pe);
                if (!peIterator.hasNext()) {
                    break;
                }
                pe = peIterator.next();
            }
        }

        return selectedPes;
    }

    @Override
    public boolean allocatePesForVmInternal(final Vm vm, final List<Double> requestedMips) {
        final List<Pe> selectedPes = getTotalCapacityToBeAllocatedToVm(requestedMips);
        if(selectedPes.size() < requestedMips.size()){
            return false;
        }

        putAllocatedMipsMap(vm, requestedMips);
        return true;
    }

    @Override
    protected void deallocatePesFromVmInternal(final Vm vm, final int pesToRemove) {
        removePesFromMap(vm, getAllocatedMipsMap(), pesToRemove);
    }
}
