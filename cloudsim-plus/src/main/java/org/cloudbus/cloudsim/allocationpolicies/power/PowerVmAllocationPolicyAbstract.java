/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.core.Simulation;

/**
 * An abstract power-aware VM allocation policy.
 * <b>It's a First Fit policy which finds the first Host having suitable resources to place a given VM.</b>
 * Such a behaviour can be overridden by sub-classes.
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

        if (host.createVm(vm)) { // if vm has been successfully created in the host
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
        return this.<PowerHost>getHostList()
                .stream()
                .sorted()
                .filter(h -> h.isSuitableForVm(vm))
                .findFirst().orElse(PowerHost.NULL);
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        vm.getHost().destroyVm(vm);
    }
}
