/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.allocationpolicies;

import lombok.NonNull;
import org.cloudsimplus.distributions.ContinuousDistribution;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

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
 *         an inactive Host that will be activated, while there may be active Hosts
 *         suitable for the VM.</li>
 *         <li>Despite the low computational complexity, such a policy may increase the number of active Hosts,
 *         that increases power consumption.</li>
 *     </ul>
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.4.2
 */
public class VmAllocationPolicyRandom extends VmAllocationPolicyAbstract {
    /**
     * A Pseudo-Random Number Generator (PRNG) used to select a Host.
     * The PRNG must return values between 0 and 1.
     */
    private final ContinuousDistribution random;

    /**
     * Creates a VmAllocationPolicy.
     *
     * @param random a Pseudo-Random Number Generator (PRNG) used to select a Host.
     *               The PRNG must return values between 0 and 1.
     */
    public VmAllocationPolicyRandom(@NonNull final ContinuousDistribution random){
        super();
        this.random = random;
    }

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final var hostList = getHostList();
        /* The for loop just defines the maximum number of Hosts to try.
         * When a suitable Host is found, the method returns immediately. */
        final int maxTries = hostList.size();
        for (int i = 0; i < maxTries; i++) {
            final int hostIndex = (int)(random.sample() * hostList.size());
            final var host = hostList.get(hostIndex);
            if (host.isSuitableForVm(vm)) {
                return Optional.of(host);
            }
        }

        return Optional.empty();
    }
}
