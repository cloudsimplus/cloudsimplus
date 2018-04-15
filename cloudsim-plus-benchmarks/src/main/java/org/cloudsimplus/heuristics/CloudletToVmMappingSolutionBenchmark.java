/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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

import java.util.stream.IntStream;

import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * A set of benchmarks for the {@link CloudletToVmMappingSolution} class.
 *
 * @author Manoel Campos da Silva Filho
 */
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@State(Scope.Thread)
public class CloudletToVmMappingSolutionBenchmark {
    private CloudletToVmMappingSimulatedAnnealing heuristic;
    private CloudletToVmMappingSolution instance1;
    private CloudletToVmMappingSolution instance2;

    @Setup
    public void doSetup() {
        CloudletToVmMappingSimulatedAnnealing heuristic =
            new CloudletToVmMappingSimulatedAnnealing(0, new UniformDistr(0, 1));
        instance1 = createInstance();
        instance2 = createInstance();
        /*Call the getCost the first time without measure it
        in order to measure the time for the second call,
        when the cost is already computed*/
        instance2.getCost();
    }

    private CloudletToVmMappingSolution createInstance() {
        final CloudletToVmMappingSolution result = new CloudletToVmMappingSolution(heuristic);
        UtilizationModel um = UtilizationModel.NULL;
        IntStream.range(0, 100).forEach(i
                -> result.bindCloudletToVm(
                        new CloudletSimple(i, 1, 1).setUtilizationModel(um),
                        new VmSimple(i, 1000, 1))
        );

        return result;
    }

    @Benchmark
    public CloudletToVmMappingSolution testCreateNeighbor() {
        return heuristic.createNeighbor(instance1);
    }

    @Benchmark
    public double testGetCostWhenFirstCall() {
        return instance1.getCost(true);
    }

    /**
     * The second time the getCost method is called
     * without changing Cloudlets to Vm's mapping,
     * the fitness will be the same and will not be computed again.
     * The first time the method is called is in the
     * {@link #doSetup()} method.
     *
     * @return the cost value
     */
    @Benchmark
    public double testGetCostWhenSecondCall() {
        return instance2.getCost();
    }
}
