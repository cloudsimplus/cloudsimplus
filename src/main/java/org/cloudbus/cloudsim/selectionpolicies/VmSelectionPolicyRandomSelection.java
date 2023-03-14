/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.selectionpolicies;

import lombok.NonNull;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A VM selection policy that randomly select VMs to migrate from a host.
 * It uses a uniform Pseudo Random Number Generator (PRNG) as default to select VMs.
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:</p>
 *
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
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
