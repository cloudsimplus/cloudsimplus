/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.allocationpolicies.power;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.hosts.power.PowerHostSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.core.Simulation;

/**
 * An abstract power-aware VM allocation policy.
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 * </p>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public abstract class PowerVmAllocationPolicyAbstract extends VmAllocationPolicyAbstract implements PowerVmAllocationPolicy {

    /**
     * @see #getVmHostMap()
     */
    private final Map<String, Host> vmHostMap = new HashMap<>();

    /**
     * Creates a PowerVmAllocationPolicy.
     */
    public PowerVmAllocationPolicyAbstract() {
        super();
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        return allocateHostForVm(vm, findHostForVm(vm));
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        final Simulation simulation = vm.getSimulation();
        if (host == PowerHost.NULL) {
            Log.printFormattedLine("%.2f: No suitable host found for VM #" + vm.getId() + "\n", simulation.clock());
            return false;
        }

        if (host.vmCreate(vm)) { // if vm has been successfully created in the host
            getVmHostMap().put(vm.getUid(), host);
            Log.printFormattedLine(
                "%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
                simulation.clock());
            return true;
        }
        Log.printFormattedLine(
            "%.2f: Creation of VM #" + vm.getId() + " on the host #" + host.getId() + " failed\n",
            simulation.clock());
        return false;
    }

    @Override
    public PowerHost findHostForVm(Vm vm) {
        return this.<PowerHost>getHostList().stream()
            .filter(h -> h.isSuitableForVm(vm))
            .findFirst().orElse(PowerHost.NULL);
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        Host host = getVmHostMap().remove(vm.getUid());
        if (!Objects.isNull(host)) {
            host.destroyVm(vm);
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
     * Gets the map where each key is a VM UID and
     * each value is the host where the VM is placed.
     *
     * @return
     */
    public Map<String, Host> getVmHostMap() {
        return vmHostMap;
    }
}
