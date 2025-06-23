/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;

/**
 * A Virtual Machine Monitor (VMM), also called Hypervisor, that allocates one or more
 * {@link Pe}s from a {@link Host} to a {@link Vm}, **and doesn't allow sharing of PEs between VMs**.
 * The allocated PEs will be used until the VM finishes running.
 * If there aren't enough free PEs as required by a VM, or the available PEs
 * don't have enough capacity, the allocation fails. In the case of fail, no
 * PE is allocated to the requesting VM.
 *
 * <p>This scheduler is conventionally used when there is an SLA to be met by the cloud provider.
 * More affordable services usually don't allocate CPUs exclusively and may use time-shared schedulers.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class VmSchedulerSpaceShared extends VmSchedulerAbstract {

    /**
     * Creates a space-shared VM scheduler.
     */
    public VmSchedulerSpaceShared() {
        this(DEF_VM_MIGRATION_CPU_OVERHEAD);
    }

    /**
     * Creates a space-shared VM scheduler, defining a CPU overhead for VM migration.
     *
     * @param vmMigrationCpuOverhead the percentage of Host's CPU usage increase when a
     * VM is migrating in or out the Host. The value is in scale from 0 to 1 (where 1 is 100%).
     */
    public VmSchedulerSpaceShared(final double vmMigrationCpuOverhead){
        super(vmMigrationCpuOverhead);
    }

    @Override
    protected boolean isSuitableForVmInternal(final Vm vm, final MipsShare requestedMips) {
        final var selectedPesList = getTotalCapacityToBeAllocatedToVm(requestedMips);
        return selectedPesList.size() >= requestedMips.pes();
    }

    /**
     * Checks if the requested amount of MIPS is available to be allocated to a VM
     * @param requestedMips a MIPS share requested by a VM
     * @return the list of PEs that may be allocated to the VM. If the size of this list is
     *         lower than the PEs number of the requestedMips, it means there aren't enough PEs
     *         with requested MIPS to be allocated to the VM
     */
    private List<Pe> getTotalCapacityToBeAllocatedToVm(final MipsShare requestedMips) {
        if (getHost().getWorkingPesNumber() < requestedMips.pes()) {
            return getHost().getWorkingPeList();
        }

        final var freePeList = getHost().getFreePeList();
        final var selectedPesList = new ArrayList<Pe>();
        if(freePeList.isEmpty()){
            return selectedPesList;
        }

        final var peIterator = freePeList.iterator();
        Pe pe = peIterator.next();
        for (int i = 0; i < requestedMips.pes(); i++) {
            if (requestedMips.mips() <= pe.getCapacity()) {
                selectedPesList.add(pe);
                if (!peIterator.hasNext()) {
                    break;
                }
                pe = peIterator.next();
            }
        }

        return selectedPesList;
    }

    @Override
    public boolean allocatePesForVmInternal(final Vm vm, final MipsShare requestedMips) {
        final var selectedPesList = getTotalCapacityToBeAllocatedToVm(requestedMips);
        if(selectedPesList.size() < requestedMips.pes()){
            return false;
        }

        ((VmSimple)vm).setAllocatedMips(requestedMips);
        return true;
    }

    @Override
    protected long deallocatePesFromVmInternal(final Vm vm, final int pesToRemove) {
        return removePesFromVm(vm, ((VmSimple)vm).getAllocatedMips(), pesToRemove);
    }
}
