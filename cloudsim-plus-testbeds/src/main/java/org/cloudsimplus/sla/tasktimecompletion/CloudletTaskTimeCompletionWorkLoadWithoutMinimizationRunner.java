/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.tasktimecompletion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudsimplus.testbeds.ExperimentRunner;

/**
 *
 * @author raysaoliveira
 */
public class CloudletTaskTimeCompletionWorkLoadWithoutMinimizationRunner extends ExperimentRunner<CloudletTaskTimeCompletionWorkLoadWithoutMinimizationExperiment> {
    
    /**
     * Different lengths that will be randomly assigned to created Cloudlets.
     */
    static final long[] CLOUDLET_LENGTHS = {10000, 14000, 20000, 40000};
    static final int[] VM_PES = {2};
    static final int VMS = 100;
    static final int CLOUDLETS = 100;

    /**
     * The TaskTimeCompletion average for all the experiments.
     */
    private List<Double> cloudletTaskTimeCompletion;

     /**
     * The percentage of cloudlets meeting TaskTimeCompletion average for all the experiments.
     */
    private List<Double> percentageOfCloudletsMeetingTaskTimeCompletion;
    
    /**
     * Amount of cloudlet PE per PE of vm.
     */
    private List<Double> ratioOfVmPesToRequiredCloudletPesList;

    /**
     * Indicates if each experiment will output execution logs or not.
     */
    private final boolean experimentVerbose = false;

    /**
     * Starts the execution of the experiments the number of times defines in
     * {@link #numberOfSimulationRuns}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new CloudletTaskTimeCompletionWorkLoadWithoutMinimizationRunner()
                .setSimulationRuns(100)
                .setApplyAntitheticVariatesTechnique(true)
                .setNumberOfBatches(5) //Comment this or set to 0 to disable the "Batch Means Method"
                .setBaseSeed(1475098589732L) //Comment this to use the current time as base seed 1475098589732L
                .setVerbose(true)
                .run();
    }

    CloudletTaskTimeCompletionWorkLoadWithoutMinimizationRunner() {
        super();
        cloudletTaskTimeCompletion = new ArrayList<>();
        percentageOfCloudletsMeetingTaskTimeCompletion = new ArrayList<>();
        ratioOfVmPesToRequiredCloudletPesList = new ArrayList<>();
    }

    @Override
    protected CloudletTaskTimeCompletionWorkLoadWithoutMinimizationExperiment createExperiment(int i) {
        ContinuousDistribution randCloudlet = createRandomGenAndAddSeedToList(i);
        ContinuousDistribution randVm = createRandomGenAndAddSeedToList(i);
        CloudletTaskTimeCompletionWorkLoadWithoutMinimizationExperiment exp
                = new CloudletTaskTimeCompletionWorkLoadWithoutMinimizationExperiment(randCloudlet, randVm);
        exp.setVerbose(experimentVerbose).setAfterExperimentFinish(this::afterExperimentFinish);
        return exp;
    }

    @Override
    protected void setup() {}

    /**
     * Method automatically called after every experiment finishes running. It
     * performs some post-processing such as collection of data for statistic
     * analysis.
     *
     * @param experiment the finished experiment
     */
    private void afterExperimentFinish(CloudletTaskTimeCompletionWorkLoadWithoutMinimizationExperiment experiment) {
        cloudletTaskTimeCompletion.add(experiment.getCloudletsTaskTimeCompletionAverage());
        percentageOfCloudletsMeetingTaskTimeCompletion.add(
                experiment.getPercentageOfCloudletsMeetingTaskTimeCompletion());
        ratioOfVmPesToRequiredCloudletPesList.add(experiment.getRatioOfExistingVmPesToRequiredCloudletPes());
    }

    @Override
    protected Map<String, List<Double>> createMetricsMap() {
        Map<String, List<Double>> map = new HashMap<>();
        map.put("Cloudlet Task Time Completion", cloudletTaskTimeCompletion);
        map.put("Percentage Of Cloudlets Meeting the Task Time Completion", percentageOfCloudletsMeetingTaskTimeCompletion);
        map.put("Average of vPEs/CloudletsPEs", ratioOfVmPesToRequiredCloudletPesList);
        return map;
    }

    @Override
    protected void printSimulationParameters() {
        System.out.printf("Executing %d experiments. Please wait ... It may take a while.\n", getSimulationRuns());
        System.out.println("Experiments configurations:");
        System.out.printf("\tBase seed: %d | Number of VMs: %d | Number of Cloudlets: %d\n", getBaseSeed(), VMS, CLOUDLETS);
        System.out.printf("\tApply Antithetic Variates Technique: %b\n", isApplyAntitheticVariatesTechnique());
        if (isApplyBatchMeansMethod()) {
            System.out.println("\tApply Batch Means Method to reduce simulation results correlation: true");
            System.out.printf("\tNumber of Batches for Batch Means Method: %d", getNumberOfBatches());
            System.out.printf("\tBatch Size: %d\n", batchSizeCeil());
        }
        System.out.printf("\nSimulated Annealing Parameters\n");
    }

    @Override
    protected void printFinalResults(String metricName, SummaryStatistics stats) {
        System.out.printf("\n# %s for %d simulation runs\n", metricName, getSimulationRuns());
        if (!simulationRunsAndNumberOfBatchesAreCompatible()) {
            System.out.println("\tBatch means method was not be applied because the number of simulation runs is not greater than the number of batches.");
        }
        if (getSimulationRuns() > 1) {
            showConfidenceInterval(stats);
        }
    }

    private void showConfidenceInterval(SummaryStatistics stats) {
        // Calculate 95% confidence interval
        double intervalSize = computeConfidenceErrorMargin(stats, 0.95);
        double lower = stats.getMean() - intervalSize;
        double upper = stats.getMean() + intervalSize;
        System.out.printf(
                "\tTaskTimeCompletion mean 95%% Confidence Interval: %.2f ∓ %.2f, that is [%.2f to %.2f]\n",
                stats.getMean(), intervalSize, lower, upper);
        System.out.printf("\tStandard Deviation: %.2f \n", stats.getStandardDeviation());
    }
}
