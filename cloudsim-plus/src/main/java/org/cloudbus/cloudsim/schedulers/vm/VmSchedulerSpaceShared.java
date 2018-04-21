/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

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
    public VmScheduler setHost(final Host host) {
        super.setHost(host);
        return this;
    }

    @Override
    public boolean isSuitableForVm(final List<Double> vmMipsList) {
        return !getTotalCapacityToBeAllocatedToVm(vmMipsList).isEmpty();
    }

    /**
     * Checks if the requested amount of MIPS is available to be allocated to a VM
     * @param vmRequestedMips a VM's list of requested MIPS
     * @return the list of PEs that can be allocated to the VM or
     * an empty list if there isn't enough capacity that can be allocated
     */
    private List<Pe> getTotalCapacityToBeAllocatedToVm(final List<Double> vmRequestedMips) {
        // if there is no enough free PEs, fails
        if (getHost().getFreePeList().size() < vmRequestedMips.size()) {
            return Collections.EMPTY_LIST;
        }

        final List<Pe> selectedPes = new ArrayList<>();
        final Iterator<Pe> peIterator = getHost().getFreePeList().iterator();
        Pe pe = peIterator.next();
        for (final double mips : vmRequestedMips) {
            if (mips <= pe.getCapacity()) {
                selectedPes.add(pe);
                if (!peIterator.hasNext()) {
                    break;
                }
                pe = peIterator.next();
            }
        }

        if (vmRequestedMips.size() > selectedPes.size()) {
            return Collections.EMPTY_LIST;
        }

        return selectedPes;
    }

    @Override
    public boolean allocatePesForVmInternal(final Vm vm, final List<Double> requestedMips) {
        final List<Pe> selectedPes = getTotalCapacityToBeAllocatedToVm(requestedMips);
        if(selectedPes.isEmpty()){
            return false;
        }

        getAllocatedMipsMap().put(vm, requestedMips);
        return true;
    }

    @Override
    protected void deallocatePesFromVmInternal(final Vm vm, final int pesToRemove) {
        removePesFromMap(vm, getAllocatedMipsMap(), pesToRemove);
    }
}
