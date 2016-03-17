/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;

/**
 * VmAllocationPolicySimple is an VmAllocationPolicyAbstract that chooses, as the host
 for a VM, the host with less PEs in use. It is therefore a Worst Fit policy,
 * allocating VMs into the host with most available PE.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmAllocationPolicySimple extends VmAllocationPolicyAbstract {

    /** @see #getUsedPes() */
    private Map<String, Integer> usedPes;

    /** @see #getFreePes() */
    private List<Integer> freePes;

    /**
     * Creates a new VmAllocationPolicySimple object.
     *
     * @param list the list of hosts
     * @pre $none
     * @post $none
     */
    public VmAllocationPolicySimple(List<Host> list) {
        super(list);

        setFreePes(new ArrayList<Integer>());
        for (Host host : getHostList()) {
            getFreePes().add(host.getNumberOfPes());
        }

        setVmTable(new HashMap<String, Host>());
        setUsedPes(new HashMap<String, Integer>());
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
        for (Integer numberOfFreePes : getFreePes()) {
            freePesTmp.add(numberOfFreePes);
        }

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
                    getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
                    result = true;
                    break;
                } else {
                    freePesTmp.set(idx, Integer.MIN_VALUE);
                }
                tries++;
            } while (!result && tries < getFreePes().size());

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
            getFreePes().set(idx, getFreePes().get(idx) + pes);
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
     * Gets the map between each VM and the number of PEs used. The map key is a VM
     * UID and the value is the number of used Pes for that VM.
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
    protected final List<Integer> getFreePes() {
        return freePes;
    }

    /**
     * Sets the free pes.
     *
     * @param freePes the new free pes
     */
    protected final void setFreePes(List<Integer> freePes) {
        this.freePes = freePes;
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
            mapVmToPm(vm, host);

            int requiredPes = vm.getNumberOfPes();
            int idx = getHostList().indexOf(host);
            getUsedPes().put(vm.getUid(), requiredPes);
            getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

            Log.printFormattedLine(
                    "%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
                    CloudSim.clock());
            return true;
        }

        return false;
    }
}
