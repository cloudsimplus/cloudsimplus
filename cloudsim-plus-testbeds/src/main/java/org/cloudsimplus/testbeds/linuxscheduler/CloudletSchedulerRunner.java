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
package org.cloudsimplus.testbeds.linuxscheduler;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.cloudsimplus.testbeds.linuxscheduler.CloudletSchedulerExperiment.*;

/**
 * An abstract runner for {@link CloudletSchedulerExperiment}.
 *
 * @author Manoel Campos da Silva Filho
 * @param <T> the type of experiments the runner will execute
 */
abstract class CloudletSchedulerRunner<T extends CloudletSchedulerExperiment> extends ExperimentRunner<T> {

    /**
     * A Pseudo Random Number Generator (PRNG) used to generate the number of
     * Cloudlets to be created for each experiment run.
     */
    protected ContinuousDistribution numberOfCloudletsPRNG;

    /**
     * A list of Cloudlets' completion time mean for each experiment run.
     */
    private List<Double> cloudletsCompletionTimeMeans;

    /**
     * Number of cloudlets in each experiment run.
     */
    private List<Double> cloudletsNumber;

    /**
     * Instantiates a runner and sets all parameters required to run the
     * experiments. Such parameters are shared among all runners that extends
     * this class.
     */
    CloudletSchedulerRunner() {
        /*
	    Values used for CloudSim Plus Paper:
	        NumberOfSimulationRuns: 1200
	        ApplyAntitheticVariatesTechnique: true
	        NumberOfBatches: 6
	        BaseSeed: 1475098589732L
         */
        this.setNumberOfSimulationRuns(1200)
                .setApplyAntitheticVariatesTechnique(false)
                //.setNumberOfBatches(6) //Comment this or set to 0 to disable the "Batch Means Method"
                .setBaseSeed(1475098589732L) //Comment this to use the current time as base seed
                .setVerbose(true);
    }

    @Override
    protected void setup() {
        cloudletsCompletionTimeMeans = new ArrayList<>(getNumberOfSimulationRuns());
        cloudletsNumber = new ArrayList<>(getNumberOfSimulationRuns());
        numberOfCloudletsPRNG = new UniformDistr(VM_PES / 2, VM_PES + 1, getBaseSeed());
    }

    @Override
    protected void printSimulationParameters() {
        System.out.printf("\n----------------------------------%s----------------------------------\n", getClass().getSimpleName());
        System.out.printf("Hosts:           %5d | PEs:               %2d | VMs: %d | PEs: %d\n", HOSTS_TO_CREATE, HOST_PES, VMS_TO_CREATE, VM_PES);
        System.out.printf("Experiment Runs: %5d | Max Cloudlets PES: %2d\n",
                getNumberOfSimulationRuns(), (MAX_CLOUDLET_PES - 1));

    }

    @Override
    protected void printFinalResults(String metricName, SummaryStatistics stats) {
        System.out.printf("Results for metric %s\n", metricName);
        System.out.printf("  Mean Number of Cloudlets:         %.2f\n", cloudletsNumber.stream().mapToDouble(n -> n).average().orElse(0.0));
        System.out.printf("  Cloudlet Completion Time Avg:     %.2f | Std dev:      %.2f\n", stats.getMean(), stats.getStandardDeviation());
        System.out.printf("  Cloudlet Completion Min Avg Time: %.2f | Max avg time: %.2f\n", stats.getMin(), stats.getMax());
        System.out.println();
    }

    @Override
    protected SummaryStatistics computeFinalStatistics(List<Double> values) {
        SummaryStatistics stats = new SummaryStatistics();
        for (double cloudletExecutionTimeMean : cloudletsCompletionTimeMeans) {
            stats.addValue(cloudletExecutionTimeMean);
        }
        return stats;
    }

    /**
     * Method automatically called after every experiment finishes running. It
     * performs some post-processing such as collection of data for statistic
     * analysis.
     *
     * @param experiment the finished experiment
     */
    protected void afterExperimentFinish(T experiment) {
        Consumer<DatacenterBroker> addExperimentStatisticsToLists = broker -> {
            Double average = broker.getCloudletsFinishedList().stream()
                    .mapToDouble(Cloudlet::getActualCpuTime)
                    .average()
                    .orElse(0.0);
            cloudletsCompletionTimeMeans.add(average);
            cloudletsNumber.add((double)broker.getCloudletsFinishedList().size());
        };

        experiment.getBrokerList().stream().findFirst().ifPresent(addExperimentStatisticsToLists);
    }

    @Override
    protected Map<String, List<Double>> createMetricsMap() {
        Map<String, List<Double>> map = new HashMap<>();
        map.put("Cloudlets Completion Time Means", cloudletsCompletionTimeMeans);
        map.put("Cloudlets Number", cloudletsNumber);
        return map;
    }

}
