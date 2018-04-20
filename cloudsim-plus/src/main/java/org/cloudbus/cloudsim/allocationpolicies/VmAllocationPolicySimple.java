/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.*;

/**
 * A VmAllocationPolicy implementation that chooses, as
 * the host for a VM, that one with fewer PEs in use. It is therefore a Worst Fit
 * policy, allocating VMs into the host with most available PEs.
 *
 * <p><b>NOTE: This policy doesn't perform optimization of VM allocation (placement)
 * by means of VM migration.</b></p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class VmAllocationPolicySimple extends VmAllocationPolicyAbstract {

    /**
     * Creates a new VmAllocationPolicySimple object.
     */
    public VmAllocationPolicySimple() {
        super();
    }

    /**
     * Allocates the host with less PEs in use for a given VM.
     *
     * @param vm {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean allocateHostForVm(final Vm vm) {
        if(getHostList().isEmpty()){
            Log.printFormattedLine(
                "%.2f: %s: Vm %s could not be allocated because there isn't any Host for Datacenter %d",
                vm.getSimulation().clock(), vm.getId(), getDatacenter().getId());
            return false;
        }

        if (vm.isCreated()) {
            return false;
        }

        final List<Host> hostsWhereVmCreationFailed = new ArrayList<>();
        //We still trying until we find a host or until we try all of them
        for(int tries = 0; tries < getHostFreePesMap().size(); tries++) {
            final Host host = getHostWithLessUsedPes(hostsWhereVmCreationFailed);
            if (allocateHostForVm(vm, host)) {
                return true;
            }
            hostsWhereVmCreationFailed.add(host);
        }

        return false;
    }

    @Override
    public boolean allocateHostForVm(final Vm vm, final Host host) {
        if (!host.createVm(vm)) {
            return false;
        }

        addUsedPes(vm);
        getHostFreePesMap().put(host, getHostFreePesMap().get(host) - vm.getNumberOfPes());

        Log.printFormattedLine(
            "%.2f: %s: %s has been allocated to %s",
            vm.getSimulation().clock(), getClass().getSimpleName(),  vm, host);
        return true;
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
     * @return the Host with less used PEs or {@link Host#NULL} if not found
     * key if not found
     * @todo sorting the Host list may degrade performance
     * for large scale simulations. The number of free PEs
     * may be taken directly from each Host in a List,
     * avoiding the use of Maps that doesn't ensure order.
     * The entries are being sorted just to ensure that
     * the results are always the same for a specific static simulation.
     * Without the sort, usually the allocation of Hosts to VMs
     * is different during debug, because of the unsorted
     * nature of the Map.
     */
    private Host getHostWithLessUsedPes(final List<Host> ignoredHosts) {
        final Map<Host, Long> map = getHostFreePesMap();
        return map.keySet()
                .stream()
                .filter(h -> !ignoredHosts.contains(h))
                .sorted(Comparator.comparingInt(Host::getId))
                .max(Comparator.comparingLong(map::get))
                .orElseGet(() -> Host.NULL);
    }

    @Override
    public void deallocateHostForVm(final Vm vm) {
        final Host host = vm.getHost();
        final long pes = removeUsedPes(vm);
        if (host != Host.NULL) {
            host.destroyVm(vm);
            getHostFreePesMap().put(host, getHostFreePesMap().get(host) + pes);
        }
    }

    /**
     * The method in this VmAllocationPolicy doesn't perform any
     * VM placement optimization and, in fact, has no effect.
     *
     * @param vmList the list of VMs
     * @return an empty map to indicate that it never performs optimization
     */
    @Override
    public Map<Vm, Host> optimizeAllocation(final List<? extends Vm> vmList) {
        return Collections.EMPTY_MAP;
    }
}
