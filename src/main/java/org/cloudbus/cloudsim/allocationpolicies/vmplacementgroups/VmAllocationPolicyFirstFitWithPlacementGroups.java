/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2023 IBM Research.
 *     Author: Pavlos Maniotis
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
package org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups;

import java.util.List;
import java.util.Optional;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyBestFit;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.traces.azure.TracesSimulationManager;

/**
 * An extension of the {@link VmAllocationPolicyBestFitWithPlacementGroups} that places the individual VMs 
 * of a group request according to First Fit algorithm instead of Best Fit. It dublicates the code of
 * {@link VmAllocationPolicyFirstFit}.
 *
 * @see VmAllocationPolicyFirstFit
 * @see VmAllocationPolicyBestFit
 * @see VmAllocationPolicyBestFitWithPlacementGroups
 * 
 * @since CloudSim Plus 7.3.2
 * 
 * @author Pavlos Maniotis
 */
public class VmAllocationPolicyFirstFitWithPlacementGroups extends VmAllocationPolicyBestFitWithPlacementGroups {
    /** @see #getLastHostIndex() */
    private int lastHostIndex;
	
    /**
     * The constructor
     */
    public VmAllocationPolicyFirstFitWithPlacementGroups(CloudSim simulation, TracesSimulationManager simulationManager) {
		super(simulation, simulationManager);
	}

    /**
     * {@inheritDoc}} 
     */
    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final List<Host> hostList = getHostList();
        /* The for loop just defines the maximum number of Hosts to try.
         * When a suitable Host is found, the method returns immediately. */
        final int maxTries = hostList.size();
        for (int i = 0; i < maxTries; i++) {
            final Host host = hostList.get(lastHostIndex);
            if (host.isSuitableForVm(vm)) {
                return Optional.of(host);
            }

            /* If it gets here, the previous Host doesn't have capacity to place the VM.
             * Then, moves to the next Host.*/
            incLastHostIndex();
        }

        return Optional.empty();
    }

    /**
     * Gets the index of the last host where a VM was placed.
     */
    protected int getLastHostIndex() {
        return lastHostIndex;
    }

    /**
     * Increment the index to move to the next Host.
     * If the end of the Host list is reached, starts from the beginning. 
     */
    protected void incLastHostIndex() {
        lastHostIndex = ++lastHostIndex % getHostList().size();
    }
    
}

