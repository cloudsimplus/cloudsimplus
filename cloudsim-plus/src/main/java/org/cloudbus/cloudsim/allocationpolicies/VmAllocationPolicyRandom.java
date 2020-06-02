package org.cloudbus.cloudsim.allocationpolicies;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A VM allocation policy
 * which finds a random Host having suitable resources to place a given VM.
 * This is a high time-efficient policy with a best-case complexity O(1)
 * and a worst-case complexity O(N), where N is the number of Hosts.
 *
 * <p>
 *     <b>NOTES:</b>
 *     <ul>
 *         <li>This policy doesn't perform optimization of VM allocation by means of VM migration.</li>
 *         <li>It has a low computational complexity (high time-efficient) but may return
 *         and inactive Host that will be activated, while there may be active Hosts
 *         suitable for the VM.</li>
 *         <li>Despite the low computational complexity, such a policy may increase the number of active Hosts,
 *         that increases power consumption.</li>
 *     </ul>
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.4.2
 */
public class VmAllocationPolicyRandom extends VmAllocationPolicyAbstract implements VmAllocationPolicy {
    /**
     * A Pseudo-Random Number Generator (PRNG) used to select a Host.
     */
    private final ContinuousDistribution random;

    /**
     * Instantiates a VmAllocationPolicyRandom.
     *
     * @param random a Pseudo-Random Number Generator (PRNG) used to select a Host.
     *               The PRNG must return values between 0 and 1.
     */
    public VmAllocationPolicyRandom(final ContinuousDistribution random){
        super();
        this.random = Objects.requireNonNull(random);
    }

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final List<Host> hostList = getHostList();
        /* The for loop just defines the maximum number of Hosts to try.
         * When a suitable Host is found, the method returns immediately. */
        final int maxTries = hostList.size();
        for (int i = 0; i < maxTries; i++) {
            final int hostIndex = (int)(random.sample() * hostList.size());
            final Host host = hostList.get(hostIndex);
            if (host.isSuitableForVm(vm)) {
                return Optional.of(host);
            }
        }

        return Optional.empty();
    }
}
