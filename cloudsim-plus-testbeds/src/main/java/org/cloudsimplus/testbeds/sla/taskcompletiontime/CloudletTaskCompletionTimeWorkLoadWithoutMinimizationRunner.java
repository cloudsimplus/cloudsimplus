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

import org.cloudsimplus.testbeds.ExperimentRunner;

/**
 * Runs the {@link CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment} the number of
 * times defines by {@link #getSimulationRuns()} and compute statistics.
 *
 * @author raysaoliveira
 */
public class CloudletTaskCompletionTimeWorkLoadWithoutMinimizationRunner extends ExperimentRunner<CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment> {
    private static final int CLOUDLETS = 100;
    static final int VMS = 30;
    static final int[] VM_PES = {2, 4};

    /**
     * Indicates if each experiment will output execution logs or not.
     */
    private final boolean experimentVerbose = false;

    /**
     * Starts the execution of the experiments the number of times defines in
     * {@link #getSimulationRuns()}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new CloudletTaskCompletionTimeWorkLoadWithoutMinimizationRunner(true, 1475098589732L)
                .setVerbose(true)
                .run();
    }

    private CloudletTaskCompletionTimeWorkLoadWithoutMinimizationRunner(final boolean applyAntitheticVariatesTechnique, final long baseSeed) {
        super(baseSeed, 100, 5, applyAntitheticVariatesTechnique);
    }

    @Override
    protected CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment createExperimentInternal(int i) {
        CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment exp
                = new CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment(i, this);
        exp.setAfterExperimentFinish(this::afterExperimentFinish).setVerbose(experimentVerbose);
        return exp;
    }

    /**
     * Method automatically called after every experiment finishes running. It
     * performs some post-processing such as collection of data for statistic
     * analysis.
     *
     * @param exp the finished experiment
     */
    private void afterExperimentFinish(CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment exp) {
        addMetricValue("Cloudlet Task Completion Time", exp.getTaskCompletionTimeAverage());
        addMetricValue("Percentage Of Cloudlets Meeting the Task Completion Time", exp.getPercentageOfCloudletsMeetingTaskCompletionTime());
        addMetricValue("Average of vPEs/CloudletsPEs", exp.getRatioOfExistingVmPesToRequiredCloudletPes());
    }

    @Override
    protected void printSimulationParameters() {
        System.out.printf("Executing %d experiments. Please wait ... It may take a while.%n", getSimulationRuns());
        System.out.println("Experiments configurations:");
        System.out.printf("\tBase seed: %d | Number of VMs: %d | Number of Cloudlets: %d%n", getBaseSeed(), VMS, CLOUDLETS);
        System.out.printf("\tApply Antithetic Variates Technique: %b%n", isApplyAntitheticVariatesTechnique());
        if (isApplyBatchMeansMethod()) {
            System.out.println("\tApply Batch Means Method to reduce simulation results correlation: true");
            System.out.printf("\tNumber of Batches for Batch Means Method: %d", getBatchesNumber());
            System.out.printf("\tBatch Size: %d%n", batchSizeCeil());
        }
        System.out.printf("%nSimulated Annealing Parameters%n");
    }
}
