package org.cloudsimplus.testbeds.linuxscheduler;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

import static org.cloudsimplus.testbeds.linuxscheduler.CloudletSchedulerExperiment.MAX_CLOUDLET_PES;


/**
 * Runs the {@link CompletelyFairSchedulerExperiment} a defined number of times
 * and computes statistics.
 *
 * @author Manoel Campos da Silva Filho
 */
class CompletelyFairSchedulerRunner extends CloudletSchedulerRunner<CompletelyFairSchedulerExperiment> {
    /**
     * Starts the execution of the experiments
     * the number of times defines in {@link #numberOfSimulationRuns}.
     * @param args
     */
    public static void main(String[] args) {
        new CompletelyFairSchedulerRunner().run();
    }

    @Override
    protected CompletelyFairSchedulerExperiment createExperiment(int i) {
        ContinuousDistribution cloudletPesPrng = createRandomGenAndAddSeedToList(i, 1, MAX_CLOUDLET_PES);
        CompletelyFairSchedulerExperiment exp = new CompletelyFairSchedulerExperiment(i, this);

        exp
            .setCloudletPesPrng(cloudletPesPrng)
            .setNumberOfCloudletsToCreate((int) numberOfCloudletsPRNG.sample())
            .setAfterExperimentFinish(this::afterExperimentFinish)
            .setVerbose(false);

        return exp;
    }

}
