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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

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
 * @since CloudSim Toolkit 1.0
 */
public class VmSchedulerSpaceShared extends VmSchedulerAbstract {

    /**
     * A map between each VM and its allocated PEs, where the key is a VM ID and
     * the value a list of PEs allocated to VM.
     */
    private Map<Vm, List<Pe>> peAllocationMap;

    /**
     * The list of free PEs yet available in the host.
     */
    private List<Pe> freePesList;

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
        setPeAllocationMap(new HashMap<>());
        setFreePesList(new ArrayList<>());
    }

    @Override
    public VmScheduler setHost(Host host) {
        super.setHost(host);
        setPeAllocationMap(new HashMap<>());
        setFreePesList(new ArrayList<>(this.getHost().getWorkingPeList()));
        return this;
    }

    @Override
    public boolean isSuitableForVm(List<Double> vmMipsList) {
        return !getTotalCapacityToBeAllocatedToVm(vmMipsList).isEmpty();
    }

    /**
     * Checks if the requested amount of MIPS is available to be allocated to a VM
     * @param vmRequestedMipsShare a VM's list of requested MIPS
     * @return the list of PEs that can be allocated to the VM or
     * an empty list if there isn't enough capacity that can be allocated
     */
    protected List<Pe> getTotalCapacityToBeAllocatedToVm(List<Double> vmRequestedMipsShare) {
        // if there is no enough free PEs, fails
        if (freePesList.size() < vmRequestedMipsShare.size()) {
            return Collections.EMPTY_LIST;
        }

        List<Pe> selectedPes = new ArrayList<>();
        Iterator<Pe> peIterator = freePesList.iterator();
        Pe pe = peIterator.next();
        for (double mips : vmRequestedMipsShare) {
            if (mips <= pe.getCapacity()) {
                selectedPes.add(pe);
                if (!peIterator.hasNext()) {
                    break;
                }
                pe = peIterator.next();
            }
        }

        if (vmRequestedMipsShare.size() > selectedPes.size()) {
            return Collections.EMPTY_LIST;
        }

        return selectedPes;
    }

    @Override
    public boolean allocatePesForVmInternal(Vm vm, final List<Double> mipsShareRequested) {
        final List<Pe> selectedPes = getTotalCapacityToBeAllocatedToVm(mipsShareRequested);
        if(selectedPes.isEmpty()){
            return false;
        }

        final double totalMips = mipsShareRequested.stream().mapToDouble(m -> m).sum();

        freePesList.removeAll(selectedPes);

        peAllocationMap.put(vm, selectedPes);
        getMipsMapAllocated().put(vm, mipsShareRequested);
        return true;
    }

    @Override
    protected void deallocatePesFromVmInternal(Vm vm, int pesToRemove) {
        freePesList.addAll(getAllocatedWorkingPesForVm(vm));
        removePesFromMap(vm, peAllocationMap,  pesToRemove);
        removePesFromMap(vm, getMipsMapAllocated(), pesToRemove);
    }

    /**
     * Gets a list or working PEs (non-failed) which are allocated to a
     * given VM.
     * @param vm the VM to get the list of allocated working PEs
     * @return
     */
    private List<Pe> getAllocatedWorkingPesForVm(Vm vm) {
        return peAllocationMap
                .getOrDefault(vm, new ArrayList<>())
                .stream()
                .filter(Pe::isWorking)
                .collect(toList());
    }

    /**
     * Sets the pe allocation map.
     *
     * @param peAllocationMap the pe allocation map
     */
    protected final void setPeAllocationMap(Map<Vm, List<Pe>> peAllocationMap) {
        this.peAllocationMap = peAllocationMap;
    }

    /**
     * Gets the pe allocation map.
     *
     * @return the pe allocation map
     */
    protected Map<Vm, List<Pe>> getPeAllocationMap() {
        return peAllocationMap;
    }

    /**
     * Sets the free pes list.
     *
     * @param freePesList the new free pes list
     */
    protected final void setFreePesList(List<Pe> freePesList) {
        this.freePesList = freePesList;
    }

    /**
     * Gets the free pes list.
     *
     * @return the free pes list
     */
    protected final List<Pe> getFreePesList() {
        return freePesList;
    }

}
