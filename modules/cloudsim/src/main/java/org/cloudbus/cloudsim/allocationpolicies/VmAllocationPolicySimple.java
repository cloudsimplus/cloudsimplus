/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;

import org.cloudbus.cloudsim.core.CloudSim;

/**
 * A VmAllocationPolicy implementation that chooses, as
 * the host for a VM, that one with less PEs in use. It is therefore a Worst Fit
 * policy, allocating VMs into the host with most available PEs.
 * This policy doesn't perform optimization of VM allocation (placement) 
 * by means of VM migration.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmAllocationPolicySimple extends VmAllocationPolicyAbstract {

    /**
     * @see #getUsedPes()
     */
    private Map<String, Integer> usedPes;

    /**
     * @see #getFreePesList()
     */
    private List<Integer> freePesList;

    /**
     * Creates a new VmAllocationPolicySimple object.
     *
     * @param list the list of hosts
     * @pre $none
     * @post $none
     */
    public VmAllocationPolicySimple(List<Host> list) {
        super(list);

        setFreePesList(new ArrayList<>());
        getHostList().forEach(host -> getFreePesList().add(host.getNumberOfPes()));

        setVmTable(new HashMap<>());
        setUsedPes(new HashMap<>());
    }

    /**
     * Allocates the host with less PEs in use for a given VM.
     *
     * @param vm {@inheritDoc}
     * @return {@inheritDoc}
     * @pre $none
     * @post $none
     */
    @Override
    public boolean allocateHostForVm(Vm vm) {
        int requiredPes = vm.getNumberOfPes();
        boolean result = false;
        int tries = 0;
        List<Integer> freePesTmp = new ArrayList<>();
        getFreePesList().forEach(freePes -> freePesTmp.add(freePes));

        if (!getVmTable().containsKey(vm.getUid())) { // if this vm was not created
            do {// we still trying until we find a host or until we try all of them
                int moreFree = Integer.MIN_VALUE;
                int idx = -1;

                // we want the host with less pes in use
                for (int i = 0; i < freePesTmp.size(); i++) {
                    if (freePesTmp.get(i) > moreFree) {
                        moreFree = freePesTmp.get(i);
                        idx = i;
                    }
                }

                Host host = getHostList().get(idx);
                result = host.vmCreate(vm);

                if (result) {
                    mapVmToPm(vm, host);
                    getUsedPes().put(vm.getUid(), requiredPes);
                    getFreePesList().set(idx, getFreePesList().get(idx) - requiredPes);
                    result = true;
                    break;
                } else {
                    freePesTmp.set(idx, Integer.MIN_VALUE);
                }
                tries++;
            } while (!result && tries < getFreePesList().size());

        }

        return result;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        Host host = unmapVmFromPm(vm);
        int idx = getHostList().indexOf(host);
        int pes = getUsedPes().remove(vm.getUid());
        if (host != null) {
            host.vmDestroy(vm);
            getFreePesList().set(idx, getFreePesList().get(idx) + pes);
        }
    }

    @Override
    public Host getHost(Vm vm) {
        return getVmTable().get(vm.getUid());
    }

    @Override
    public Host getHost(int vmId, int userId) {
        return getVmTable().get(VmSimple.getUid(userId, vmId));
    }

    /**
     * Gets the map between each VM and the number of PEs used. The map key is a
     * VM UID and the value is the number of used Pes for that VM.
     *
     * @return the used PEs map
     */
    protected Map<String, Integer> getUsedPes() {
        return usedPes;
    }

    /**
     * Sets the used pes.
     *
     * @param usedPes the used pes
     */
    protected final void setUsedPes(Map<String, Integer> usedPes) {
        this.usedPes = usedPes;
    }

    /**
     * Gets the number of free PEs for each host from {@link #getHostList()}.
     *
     * @return the free PEs list
     */
    protected final List<Integer> getFreePesList() {
        return freePesList;
    }

    /**
     * Sets the free pes.
     *
     * @param freePesList the new free pes
     */
    protected final void setFreePesList(List<Integer> freePesList) {
        this.freePesList = freePesList;
    }

    /**
     * The method in this VmAllocationPolicy doesn't perform any
     * VM placement optimization and, in fact, has no effect.
     * 
     * @param vmList
     * @return an empty map to indicate that it never performs optimization
     */
    @Override
    public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList) {
        return Collections.EMPTY_MAP;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            mapVmToPm(vm, host);

            int requiredPes = vm.getNumberOfPes();
            int idx = getHostList().indexOf(host);
            getUsedPes().put(vm.getUid(), requiredPes);
            getFreePesList().set(idx, getFreePesList().get(idx) - requiredPes);

            Log.printFormattedLine(
                    "%.2f: VM #%d has been allocated to the host #%d", 
                    CloudSim.clock(), vm.getId(), host.getId());
            return true;
        }

        return false;
    }
}
