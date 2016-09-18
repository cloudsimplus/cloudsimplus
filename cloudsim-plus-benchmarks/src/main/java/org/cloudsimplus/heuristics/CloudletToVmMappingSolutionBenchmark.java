package org.cloudsimplus.heuristics;

import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
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
    private CloudletToVmMappingSolution instance1;
    private CloudletToVmMappingSolution instance2;

    @Setup
    public void doSetup() {
        instance1 = createInstance();
        
        instance2 = createInstance();
        /*Call the getCost the first time without measure it
        in order to measure the time for the second call,
        when the cost is already computed*/
        instance2.getCost();
    }

    private CloudletToVmMappingSolution createInstance() {
        CloudletToVmMappingSolution result = new CloudletToVmMappingSolution(Heuristic.NULL);
        UtilizationModel um = UtilizationModel.NULL;
        IntStream.range(0, 100).forEach(i
                -> result.bindCloudletToVm(
                        new CloudletSimple(i, 1, 1, 1, 1, um, um, um),
                        new VmSimple(i, 0, 1000, 1, 1, 1, 1, "xen", CloudletScheduler.NULL))
        );
        
        return result;
    }

    @Benchmark
    public CloudletToVmMappingSolution testCreateNeighbor() {
        return instance1.createNeighbor();
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
