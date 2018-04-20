/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.allocationpolicies;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An <b>First Fit VM allocation policy</b>
 * which finds the first Host having suitable resources to place a given VM.
 *
 * <p><b>NOTE: This policy doesn't perform optimization of VM allocation (placement)
 * by means of VM migration.</b></p>
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
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 3.0
 */
public class VmAllocationPolicyFirstFit extends VmAllocationPolicyAbstract implements VmAllocationPolicy {
    @Override
    public Optional<Host> findHostForVm(final Vm vm) {
        return this.getHostList()
                .stream()
                .sorted()
                .filter(h -> h.isSuitableForVm(vm))
                .findFirst();
    }

    /**
     * This implementation doesn't perform any
     * VM placement optimization and, in fact, has no effect.
     *
     * @param vmList the list of VMs
     * @return an empty map to indicate that it never performs optimization
     */
    @Override
    public Map<Vm, Host> getOptimizedAllocationMap(final List<? extends Vm> vmList) {
        return Collections.EMPTY_MAP;
    }
}
