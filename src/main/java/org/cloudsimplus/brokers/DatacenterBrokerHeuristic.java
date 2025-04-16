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
package org.cloudsimplus.brokers;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.heuristics.CloudletToVmMappingHeuristic;
import org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing;
import org.cloudsimplus.heuristics.CloudletToVmMappingSolution;
import org.cloudsimplus.vms.Vm;

import java.util.stream.Collectors;

/**
 * <p>A {@link DatacenterBroker} that uses some {@link #setHeuristic(CloudletToVmMappingHeuristic) heuristic}
 * to get a suboptimal mapping among submitted cloudlets and Vm's.
 * Such heuristic can be, for instance, the {@link CloudletToVmMappingSimulatedAnnealing}
 * that implements a Simulated Annealing algorithm.
 * The Broker then places the submitted VMs at the first Datacenter found.
 * If there isn't capacity in that one, it will try other available ones.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
@Getter @Setter
public class DatacenterBrokerHeuristic extends DatacenterBrokerSimple {
    /**
     * A heuristic to be used to find a suboptimal mapping between
     * Cloudlets and VMs. <b>The list of Cloudlets and VMs to be used by the heuristic
     * will be set automatically by the DatacenterBroker. Accordingly,
     * the developer don't have to set those lists manually,
     * once they will be overridden.</b>
     *
     * <p>The time taken to find a suboptimal mapping of Cloudlets to Vm's
     * depends on the heuristic parameters that have to be set carefully.
     * Check the {@link CloudletToVmMappingHeuristic} documentation for details.</p>
     */
    @NonNull
    private CloudletToVmMappingHeuristic heuristic;

    /**
     * Creates a DatacenterBroker.
     *
     * @param simulation the {@link CloudSimPlus} instance that represents the simulation the broker is related to
     * @see #setHeuristic(CloudletToVmMappingHeuristic)
     */
    public DatacenterBrokerHeuristic(final CloudSimPlus simulation) {
        super(simulation);
        heuristic = CloudletToVmMappingHeuristic.NULL;
    }

    @Override
    protected void requestDatacentersToCreateWaitingCloudlets() {
        setupAndStartHeuristic();
        super.requestDatacentersToCreateWaitingCloudlets();
    }

    /**
     * Setups some heuristic parameters and starts the heuristic to find a suboptimal mapping for Cloudlets and VMs.
     */
    private void setupAndStartHeuristic() {
        heuristic.setVmList(getVmExecList());
        heuristic.setCloudletList(
	        getCloudletWaitingList().stream()
                        .filter(cloudlet -> !cloudlet.isBoundToVm())
                        .collect(Collectors.toList()));
        /*
        Starts the heuristic to get a suboptimal solution for the Cloudlets to VMs mapping.
        Depending on the heuristic parameters, it may take a while to get a solution.
        */
        LOGGER.info(
                "{} started the heuristic to get a suboptimal solution for mapping Cloudlets to Vm's running {} neighborhood searches by iteration.{}{}",
                this, heuristic.getSearchesByIteration(),
                System.lineSeparator(),
                "Please wait... It may take a while, depending on heuristic parameters and number of Cloudlets and Vm's.");

	    final CloudletToVmMappingSolution solution = heuristic.solve();
        LOGGER.info(
                "{} finished the solution find for mapping Cloudlets to Vm's in {} seconds with a solution cost of {}",
                this, heuristic.getSolveTime(), solution.getCost());
    }

    @Override
    public Vm defaultVmMapper(final Cloudlet cloudlet) {
        /*
         * Defines a fallback Vm in the case the heuristic solution
         * didn't assign a Vm to the given cloudlet.
         */
        final Vm fallbackVm = super.defaultVmMapper(cloudlet);

        //If user didn't bind this cloudlet, and it has not been executed yet,
        //gets the Vm for the Cloudlet from the heuristic solution.
        return heuristic.getBestSolutionSoFar().getResult().getOrDefault(cloudlet, fallbackVm);
    }
}
