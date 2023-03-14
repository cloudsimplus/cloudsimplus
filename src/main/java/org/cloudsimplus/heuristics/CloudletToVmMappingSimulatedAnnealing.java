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
package org.cloudsimplus.heuristics;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

/**
 * A heuristic that uses <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated Annealing</a>
 * to find a suboptimal mapping among a set of Cloudlets and VMs in order to reduce
 * the number of idle or overloaded Vm Pe's.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CloudletToVmMappingSimulatedAnnealing
      extends SimulatedAnnealingAbstract<CloudletToVmMappingSolution>
      implements CloudletToVmMappingHeuristic
{
    private CloudletToVmMappingSolution initialSolution;

    @Getter @Setter @NonNull
    private List<Vm> vmList;

    @Getter @Setter @NonNull
    private List<Cloudlet> cloudletList;

    /**
     * Creates a new Simulated Annealing Heuristic for solving Cloudlets to Vm's mapping.
     *
     * @param initialTemperature the system initial temperature
     * @param random a random number generator
     * @see #setColdTemperature(double)
     * @see #setCoolingRate(double)
     */
    public CloudletToVmMappingSimulatedAnnealing(final double initialTemperature, final ContinuousDistribution random) {
        super(random, CloudletToVmMappingSolution.class);
	    setCurrentTemperature(initialTemperature);
        initialSolution = new CloudletToVmMappingSolution(this);
    }

    private CloudletToVmMappingSolution generateRandomSolution() {
        final var solution = new CloudletToVmMappingSolution(this);
        cloudletList.forEach(cloudlet -> solution.bindCloudletToVm(cloudlet, getRandomVm()));
        return solution;
    }

    private boolean isReadToGenerateInitialSolution(){
        return !cloudletList.isEmpty() && !vmList.isEmpty();
    }

    private boolean isThereInitialSolution(){
        return !initialSolution.getResult().isEmpty();
    }

    @Override
    public CloudletToVmMappingSolution getInitialSolution() {
        if(!isThereInitialSolution() && isReadToGenerateInitialSolution()) {
            initialSolution = generateRandomSolution();
        }

        return initialSolution;
    }

    /**
     * @return a random Vm from the {@link #getVmList() available Vm's list}.
     */
    private Vm getRandomVm() {
        final int idx = getRandomValue(vmList.size());
        return vmList.get(idx);
    }

    @Override
    public CloudletToVmMappingSolution createNeighbor(final CloudletToVmMappingSolution source) {
        final var clone = new CloudletToVmMappingSolution(source);
        clone.swapVmsOfTwoRandomSelectedMapEntries();
        return clone;
    }
}
