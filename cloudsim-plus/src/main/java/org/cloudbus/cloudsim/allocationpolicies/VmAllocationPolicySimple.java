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
                "%.2f: %s: Vm %s could not be allocated because there isn't any Host for Datacenter %d",
                vm.getSimulation().clock(), vm.getId(), getDatacenter().getId());
            return false;
        }

        if (vm.isCreated()) {
            return false;
        }

        List<Host> hostsWhereVmCreationFailed = new ArrayList<>();
        //We still trying until we find a host or until we try all of them
        for(int tries = 0; tries < getHostFreePesMap().size(); tries++) {
            Map.Entry<Host, Long> entry = getHostWithLessUsedPes(hostsWhereVmCreationFailed);
            Host host = entry.getKey();
            final long hostFreePes = entry.getValue();
            if (host.createVm(vm)) {
                addUsedPes(vm);
                getHostFreePesMap().put(host, hostFreePes - vm.getNumberOfPes());
                if(!hostsWhereVmCreationFailed.isEmpty()){
                    Log.printFormattedLine(
                            "%.2f: %s: %s was successfully allocated to %s",
                             vm.getSimulation().clock(), getClass().getSimpleName(), vm, host);
                }
                return true;
            } else {
                hostsWhereVmCreationFailed.add(host);
            }
        }

        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (!host.createVm(vm)) {
            return false;
        }

        final long requiredPes = vm.getNumberOfPes();
        addUsedPes(vm);
        getHostFreePesMap().put(host, getHostFreePesMap().get(host) - requiredPes);

        Log.printFormattedLine(
            "%.2f: %s: VM #%d has been allocated to the host #%d",
            vm.getSimulation().clock(), getClass().getSimpleName(),  vm.getId(), host.getId());
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
     * @return an Entry where the key is the Host and the value is
     * the number of used PEs if a Host is found, or an Entry with a {@link Host#NULL}
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
    private Map.Entry<Host, Long> getHostWithLessUsedPes(List<Host> ignoredHosts) {
        final Map<Host, Long> map = getHostFreePesMap();
        final Host host = map.keySet()
                .stream()
                .filter(h -> !ignoredHosts.contains(h))
                .sorted(Comparator.comparingInt(Host::getId))
                .max(Comparator.comparingLong(h -> map.get(h)))
                .orElseGet(() -> Host.NULL);

        return new TreeMap.SimpleEntry<>(host, map.getOrDefault(host, 0L));
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
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
    public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList) {
        return Collections.EMPTY_MAP;
    }
}
