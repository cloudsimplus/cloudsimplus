/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.allocationpolicies;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Optional;

/**
 * An <b>First Fit VM allocation policy</b>
 * which finds the first Host having suitable resources to place a given VM.
 * Find first operation has a low <a href="https://en.wikipedia.org/wiki/Time_complexity">time complexity</a> because it doesn't need to
 * sort the Host list to find a suitable Host. The best-case complexity to find a first suitable host
 * is O(1) and the worst-case complexity is O(N), where N is the number of Hosts.
 *
 * <p>
 *     <b>NOTES:</b>
 *     <ul>
 *         <li>This policy doesn't perform optimization of VM allocation by means of VM migration.</li>
 *         <li>The find first operation has a small time complexity but it may return
 *         and inactive Host that will be activated, while there may be active Hosts
 *         suitable for the VM.</li>
 *     </ul>
 * </p>
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012</a>
 * </li>
 * </ul>
 * </p>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 3.0
 */
public class VmAllocationPolicyFirstFit extends VmAllocationPolicyAbstract implements VmAllocationPolicy {
    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        return this.getHostList()
                .stream()
                .filter(host -> host.isSuitableForVm(vm))
                .findFirst();
    }
}
