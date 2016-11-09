package org.cloudsimplus.testbeds.linuxscheduler;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

import static org.cloudsimplus.testbeds.linuxscheduler.CloudletSchedulerExperiment.MAX_CLOUDLET_PES;

/**
 * @author Manoel Campos da Silva Filho
 */
public class CloudletSchedulerTimeSharedRunner extends CloudletSchedulerRunner<CloudletSchedulerTimeSharedExperiment> {
    /**
     * Starts the execution of the experiments
     * the number of times defines in {@link #numberOfSimulationRuns}.
     * @param args
     */
    public static void main(String[] args) {
        new CloudletSchedulerTimeSharedRunner().run();
    }

    @Override
    protected CloudletSchedulerTimeSharedExperiment createExperiment(int i) {
        ContinuousDistribution cloudletPesPrng = createRandomGenAndAddSeedToList(i, 1, MAX_CLOUDLET_PES);
        CloudletSchedulerTimeSharedExperiment exp = new CloudletSchedulerTimeSharedExperiment(i, this);

        exp
            .setCloudletPesPrng(cloudletPesPrng)
            .setNumberOfCloudletsToCreate((int) numberOfCloudletsPRNG.sample())
            .setAfterExperimentFinish(this::afterExperimentFinish)
            .setVerbose(false);

        return exp;
    }
}
