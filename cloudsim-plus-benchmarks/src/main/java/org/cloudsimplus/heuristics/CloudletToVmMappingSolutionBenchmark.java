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
        CloudletToVmMappingSolution result = new CloudletToVmMappingSolution(heuristic);
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
    public double testGetCost_FirstCall() {
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
    public double testGetCost_SecondCall() {
        return instance2.getCost();
    }
}
