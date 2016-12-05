/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies;

import java.util.*;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

/**
 * A VmAllocationPolicy implementation that chooses, as
 * the host for a VM, that one with less PEs in use. It is therefore a Worst Fit
 * policy, allocating VMs into the host with most available PEs.
 *
 * <p><b>NOTE: This policy doesn't perform optimization of VM allocation (placement)
 * by means of VM migration.</b></p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmAllocationPolicySimple extends VmAllocationPolicyAbstract {

    /**
     * Creates a new VmAllocationPolicySimple object.
     *
     * @pre $none
     * @post $none
     */
    public VmAllocationPolicySimple() {
        super();
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
        if(getHostList().isEmpty()){
            Log.printFormattedLine(
                "Vm %s could not be allocated because there isn't any Host for Datacenter %d",
                vm.getId(), getDatacenter().getId());
            return false;
        }

        // if this vm was already created
        if (getVmHostMap().containsKey(vm.getUid())) {
            return false;
        }

        List<Host> hostsWhereVmCreationFailed = new ArrayList<>();
        //We still trying until we find a host or until we try all of them
        for(int tries = 0; tries < getHostFreePesMap().size(); tries++) {
            Map.Entry<Host, Integer> entry = getHostWithLessUsedPes(hostsWhereVmCreationFailed);
            Host host = entry.getKey();
            final int hostFreePes = entry.getValue();
            if (host.vmCreate(vm)) {
                mapVmToPm(vm, host);
                getUsedPes().put(vm.getUid(), vm.getNumberOfPes());
                getHostFreePesMap().put(host, hostFreePes - vm.getNumberOfPes());
                if(!hostsWhereVmCreationFailed.isEmpty()){
                    Log.printFormattedLine("[VmAllocationPolicy] VM #%d was successfully allocated to Host #%d", vm.getId(), host.getId());
                }
                return true;
            } else {
                hostsWhereVmCreationFailed.add(host);
            }
        }

        return false;
    }

    /**
     * Gets the host from the {@link #getHostList()} that has
     * the less number of used PEs.
     *
     * <b>The method must not be called without checking if the host list is empty first.</b>
     *
     * @param ignoredHosts the list of hosts that have to be ignored when selecting the
     *                     host with less used PEs. These list can be, for instance,
     *                     the list of hosts that the creation of a given VM failed.
     * @return
     */
    private Map.Entry<Host, Integer> getHostWithLessUsedPes(List<Host> ignoredHosts) {
        return getHostFreePesMap().entrySet().stream()
            .filter(entry -> !ignoredHosts.contains(entry.getKey()))
            .max(Comparator.comparing(Map.Entry::getValue))
            .get();
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        Host host = unmapVmFromPm(vm);
        int pes = getUsedPes().remove(vm.getUid());
        if (host != null) {
            host.destroyVm(vm);
            getHostFreePesMap().put(host, getHostFreePesMap().get(host) + pes);
        }
    }

    @Override
    public Host getHost(Vm vm) {
        return getVmHostMap().get(vm.getUid());
    }

    @Override
    public Host getHost(int vmId, int userId) {
        return getVmHostMap().get(VmSimple.getUid(userId, vmId));
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

            final int requiredPes = vm.getNumberOfPes();
            getUsedPes().put(vm.getUid(), requiredPes);
            getHostFreePesMap().put(host, getHostFreePesMap().get(host) - requiredPes);

            Log.printFormattedLine(
                    "%.2f: VM #%d has been allocated to the host #%d",
                    vm.getSimulation().clock(), vm.getId(), host.getId());
            return true;
        }

        return false;
    }
}
