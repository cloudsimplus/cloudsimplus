/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * NetworkVmAllocationPolicy is an {@link VmAllocationPolicy} that chooses, as
 * the host for a VM, that one with less PEs in use. This policy doesn't perform
 * optimization of VM allocation (placement) by means of VM migration.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 1.0
 */
public final class NetworkVmAllocationPolicy extends VmAllocationPolicyAbstract {

    /**
     * Creates a VmAllocationPolicySimple.
     *
     * @pre $none
     * @post $none
     */
    public NetworkVmAllocationPolicy() {
        super();
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        int requiredPes = vm.getNumberOfPes();
        boolean result = false;
        int tries = 0;
        List<Integer> freePesTmp = new ArrayList<>();
        for (Integer freePesNumber : getFreePesList()) {
            freePesTmp.add(freePesNumber);
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

                Host host = this.getHostList().get(idx);
                result = host.vmCreate(vm);

                if (result) { // if vm were succesfully created in the host
                    getVmTable().put(vm.getUid(), host);
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

    /**
     * Gets the max utilization among the PEs of a given VM placed at a given
     * host.
     *
     * @param host The host where the VM is placed
     * @param vm The VM to get the max PEs utilization
     * @return The max utilization among the PEs of the VM
     */
    protected double getMaxUtilizationAfterAllocation(NetworkHost host, Vm vm) {
        List<Double> allocatedMipsForVm = null;
        NetworkHost allocatedHost = (NetworkHost) vm.getHost();

        if (allocatedHost != null) {
            allocatedMipsForVm = vm.getHost().getAllocatedMipsForVm(vm);
        }

        if (!host.allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
            return -1;
        }

        double maxUtilization = host.getMaxUtilizationAmongVmsPes(vm);

        host.deallocatePesForVm(vm);

        if (allocatedHost != null && allocatedMipsForVm != null) {
            vm.getHost().allocatePesForVm(vm, allocatedMipsForVm);
        }

        return maxUtilization;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        Host host = getVmTable().remove(vm.getUid());
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
     * The method in this VmAllocationPolicy doesn't perform any VM placement
     * optimization and, in fact, has no effect.
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
        if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
            getVmTable().put(vm.getUid(), host);

            int requiredPes = vm.getNumberOfPes();
            int idx = getHostList().indexOf(host);
            getUsedPes().put(vm.getUid(), requiredPes);
            getFreePesList().set(idx, getFreePesList().get(idx) - requiredPes);

            Log.printFormattedLine(
                    "%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
                    host.getSimulation().clock());
            return true;
        }

        return false;
    }
}
