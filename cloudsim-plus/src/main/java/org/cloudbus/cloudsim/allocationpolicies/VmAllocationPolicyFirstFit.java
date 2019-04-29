package org.cloudbus.cloudsim.allocationpolicies;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;
import java.util.Optional;

/**
 * An <b>First Fit VM allocation policy</b>
 * which finds the first Host having suitable resources to place a given VM.
 * This is a very time efficient policy with a best-case complexity O(1)
 * and a worst-case complexity O(N), where N is the number of Hosts.
 *
 * <p>
 *     <b>NOTES:</b>
 *     <ul>
 *         <li>This policy doesn't perform optimization of VM allocation by means of VM migration.</li>
 *         <li>It has a low computational complexity but may return
 *         and inactive Host that will be activated, while there may be active Hosts
 *         suitable for the VM.</li>
 *     </ul>
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0.0
 */
public class VmAllocationPolicyFirstFit extends VmAllocationPolicyAbstract implements VmAllocationPolicy {
    /**
     * The index of the last host used to place a VM.
     */
    private int lastHostIndex;

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final List<Host> hostList = getHostList();
        for (int i = lastHostIndex; i < hostList.size(); i++) {
            final Host host = hostList.get(i);
            if (host.isSuitableForVm(vm)) {
                return Optional.of(hostList.get(i));
            }

            /*If it gets here, the previous Host doesn't have capacity to place the VM.
             * Then, moves to the next Host.
             * If the end of the Host list is reached, starts from the beginning.*/
            lastHostIndex = ++lastHostIndex % hostList.size();
        }

        return Optional.empty();
    }
}
