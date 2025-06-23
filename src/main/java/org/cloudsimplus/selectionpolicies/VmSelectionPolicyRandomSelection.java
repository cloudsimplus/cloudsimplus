/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.selectionpolicies;

import lombok.NonNull;
import org.cloudsimplus.distributions.ContinuousDistribution;
import org.cloudsimplus.distributions.UniformDistr;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

import java.util.Optional;

/**
 * A VM selection policy that randomly selects {@link Vm}s to migrate from a host.
 * It uses a uniform Pseudo Random Number Generator (PRNG) as default to select VMs.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 3.0
 */
public class VmSelectionPolicyRandomSelection implements VmSelectionPolicy {
    private final ContinuousDistribution rand;

    /**
     * Creates a PowerVmSelectionPolicyRandomSelection using
     * a uniform Pseudo Random Number Generator (PRNG) as default to select VMs to migrate.
     */
    public VmSelectionPolicyRandomSelection(){
        this(new UniformDistr());
    }

    /**
     * Creates a PowerVmSelectionPolicyRandomSelection using a given
     * Pseudo Random Number Generator (PRNG) to select VMs to migrate.
     *
     * @param rand a Pseudo Random Number Generator (PRNG) to randomly select VMs to migrate.
     */
    public VmSelectionPolicyRandomSelection(@NonNull final ContinuousDistribution rand){
        super();
        this.rand = rand;
    }

	@Override
	public Optional<Vm> getVmToMigrate(final Host host) {
		final var migratableVmList = host.getMigratableVms();
		if (migratableVmList.isEmpty()) {
			return Optional.empty();
		}

		final int index = (int)(rand.sample() * migratableVmList.size());
		return Optional.of(migratableVmList.get(index));
	}
}
