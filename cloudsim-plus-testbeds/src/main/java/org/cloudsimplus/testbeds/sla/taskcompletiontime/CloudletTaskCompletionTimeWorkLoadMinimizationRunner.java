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
package org.cloudsimplus.testbeds.sla.taskcompletiontime;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runs the {@link CloudletTaskCompletionTimeWorkLoadMinimizationExperiment} the number of
 * times defines by {@link #getSimulationRuns()} and compute statistics.
 *
 * @author raysaoliveira
 */
class CloudletTaskCompletionTimeWorkLoadMinimizationRunner extends ExperimentRunner<CloudletTaskCompletionTimeWorkLoadMinimizationExperiment> {
    static final int[] VM_PES = {2, 4, 6};
    static final int[] VM_MIPS = {10000, 15000, 28000};
    public static final int VMS = 30;
    public static final int CLOUDLETS = 300;

    /**
     * The Task Completion Time average for all the experiments.
     */
    private List<Double> cloudletsCompletionTime;

     /**
     * The percentage of cloudlets meeting task completion time average for all the experiments.
     */
    private List<Double> percentOfCloudletsMeetingCompletionTime;

    /**
     * Amount of cloudlet PE per PE of vm.
     */
    private List<Double> ratioOfVmPesToRequiredCloudletPesList;

    /**
     * Indicates if each experiment will output execution logs or not.
     */
    private static final boolean experimentVerbose = false;

    /**
     * Starts the execution of the experiments the number of times defines in
     * {@link #getSimulationRuns()}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new CloudletTaskCompletionTimeWorkLoadMinimizationRunner(true, 1475098589732L)
                .setSimulationRuns(300)
                .setNumberOfBatches(5) //Comment this or set to 0 to disable the "Batch Means Method"
                .setVerbose(true)
                .run();
    }

    private CloudletTaskCompletionTimeWorkLoadMinimizationRunner(final boolean applyAntitheticVariatesTechnique, final long baseSeed) {
        super(applyAntitheticVariatesTechnique, baseSeed);
        cloudletsCompletionTime = new ArrayList<>();
        percentOfCloudletsMeetingCompletionTime = new ArrayList<>();
        ratioOfVmPesToRequiredCloudletPesList = new ArrayList<>();
    }

    @Override
    protected CloudletTaskCompletionTimeWorkLoadMinimizationExperiment createExperiment(final int i) {
        final CloudletTaskCompletionTimeWorkLoadMinimizationExperiment exp
                = new CloudletTaskCompletionTimeWorkLoadMinimizationExperiment(i, this);
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
    private void afterExperimentFinish(final CloudletTaskCompletionTimeWorkLoadMinimizationExperiment experiment) {
        cloudletsCompletionTime.add(experiment.getTaskCompletionTimeAverage());
        percentOfCloudletsMeetingCompletionTime.add(
                experiment.getPercentageOfCloudletsMeetingCompletionTime());
        ratioOfVmPesToRequiredCloudletPesList.add(experiment.getRatioOfExistingVmPesToRequiredCloudletPes());
    }

    @Override
    protected Map<String, List<Double>> createMetricsMap() {
        final Map<String, List<Double>> map = new HashMap<>();
        map.put("Task Completion Time", cloudletsCompletionTime);
        map.put("Percentage Of Cloudlets Meeting Task Completion Time", percentOfCloudletsMeetingCompletionTime);
        map.put("Average of vPEs/CloudletsPEs", ratioOfVmPesToRequiredCloudletPesList);
        return map;
    }

    @Override
    protected void printSimulationParameters() {
        System.out.printf("Executing %d experiments. Please wait ... It may take a while.%n", getSimulationRuns());
        System.out.println("Experiments configurations:");
        System.out.printf("\tBase seed: %d | Number of VMs: %d | Number of Cloudlets: %d%n", getBaseSeed(), VMS, CLOUDLETS);
        System.out.printf("\tApply Antithetic Variates Technique: %b%n", isApplyAntitheticVariatesTechnique());
        if (isApplyBatchMeansMethod()) {
            System.out.println("\tApply Batch Means Method to reduce simulation results correlation: true");
            System.out.printf("\tNumber of Batches for Batch Means Method: %d", getNumberOfBatches());
            System.out.printf("\tBatch Size: %d%n", batchSizeCeil());
        }
    }

    @Override
    protected void printFinalResults(final String metricName, final SummaryStatistics stats) {
        System.out.printf("%n# %s for %d simulation runs%n", metricName, getSimulationRuns());
        if (!simulationRunsAndNumberOfBatchesAreCompatible()) {
            System.out.println("\tBatch means method was not be applied because the number of simulation runs is not greater than the number of batches.");
        }
        if (getSimulationRuns() > 1) {
            showConfidenceInterval(stats);
        }
    }

    private void showConfidenceInterval(final SummaryStatistics stats) {
        // Calculate 95% confidence interval
        final double intervalSize = computeConfidenceErrorMargin(stats, 0.95);
        final double lower = stats.getMean() - intervalSize;
        final double upper = stats.getMean() + intervalSize;
        System.out.printf(
                "\tTask Completion Time mean 95%% Confidence Interval: %.2f ∓ %.2f, that is [%.2f to %.2f]%n",
                stats.getMean(), intervalSize, lower, upper);
        System.out.printf("\tStandard Deviation: %.2f%n", stats.getStandardDeviation());
    }

}
