/**
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.sla.responsetime;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

/**
 * Runs the {@link CloudletResponseTimeMinimizationExperiment} the number of times
 * defines by {@link #numberOfSimulationRuns} and compute statistics.
 *
 * @author raysaoliveira
 */
final class CloudletResponseTimeMinimizationRunner extends ExperimentRunner<CloudletResponseTimeMinimizationExperiment> {
    /**
     * Different lengths that will be randomly assigned to created Cloudlets.
     */
    static final long[] CLOUDLET_LENGTHS = {20000, 40000, 14000, 10000, 10000};
    static final int[] VM_PES = {2, 4};
    static final int VMS = 3;
    static final int CLOUDLETS = 6;
    
    /**
     * The response time average for all the experiments.
     */
    private List<Double> cloudletResponseTimes;

     /**
     * The percentage of cloudlets meeting response time average for all the experiments.
     */
    private List<Double> percentageOfCloudletsMeetingResponseTimes;

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
        new CloudletResponseTimeMinimizationRunner()
                .setNumberOfSimulationRuns(100)
                .setApplyAntitheticVariatesTechnique(true)
                .setNumberOfBatches(5) //Comment this or set to 0 to disable the "Batch Means Method"
                .setBaseSeed(1475098589732L) //Comment this to use the current time as base seed
                .setVerbose(true)
                .run();
    }
    
    CloudletResponseTimeMinimizationRunner() {
        super();
        cloudletResponseTimes = new ArrayList<>();
        percentageOfCloudletsMeetingResponseTimes = new ArrayList<>();
    }

    @Override
    protected CloudletResponseTimeMinimizationExperiment createExperiment(int i) {
        ContinuousDistribution randCloudlet = createRandomGenAndAddSeedToList(i);
        ContinuousDistribution randVm = createRandomGenAndAddSeedToList(i);  
        CloudletResponseTimeMinimizationExperiment exp
                = new CloudletResponseTimeMinimizationExperiment(randCloudlet, randVm);
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
    private void afterExperimentFinish(CloudletResponseTimeMinimizationExperiment experiment) {
        cloudletResponseTimes.add(experiment.getCloudletsResponseTimeAverage());
        percentageOfCloudletsMeetingResponseTimes.add(
                experiment.getPercentageOfCloudletsMeetingResponseTime());
    }

    @Override
    protected Map<String, List<Double>> createMetricsMap() {
        Map<String, List<Double>>  map = new HashMap<>();
        map.put("Cloudlet Response Time", cloudletResponseTimes);
        map.put("Percentage Of Cloudlets Meeting Response Times", percentageOfCloudletsMeetingResponseTimes);
        return map;
    }

    @Override
    protected void printSimulationParameters() {
        System.out.printf("Executing %d experiments. Please wait ... It may take a while.\n", getNumberOfSimulationRuns());
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
        System.out.printf("\n# %s for %d simulation runs\n", metricName, getNumberOfSimulationRuns());
        if (!simulationRunsAndNumberOfBatchesAreCompatible()) {
            System.out.println("\tBatch means method was not be applied because the number of simulation runs is not greater than the number of batches.");
        }
        if (getNumberOfSimulationRuns() > 1) {
            showConfidenceInterval(stats);       
        }
    }

    private void showConfidenceInterval(SummaryStatistics stats) {
        // Calculate 95% confidence interval
        double intervalSize = computeConfidenceErrorMargin(stats, 0.95);
        double lower = stats.getMean() - intervalSize;
        double upper = stats.getMean() + intervalSize;
        System.out.printf(
                "\tResponse time mean 95%% Confidence Interval: %.2f ∓ %.2f, that is [%.2f to %.2f]\n",
                stats.getMean(), intervalSize, lower, upper);
        System.out.printf("\tStandard Deviation: %.2f \n", stats.getStandardDeviation());
    }

}
