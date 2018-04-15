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
package org.cloudsimplus.testbeds.heuristics;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.distributions.NormalDistr;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudsimplus.heuristics.CloudletToVmMappingSolution;
import org.cloudsimplus.heuristics.Heuristic;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.testbeds.SimulationExperiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

/**
 * Runs the {@link DatacenterBrokerHeuristicExperiment} the number of times
 * defines by {@link #getSimulationRuns()} and compute statistics.
 *
 * @author Manoel Campos da Silva Filho
 */
final class DatacenterBrokerHeuristicRunner extends ExperimentRunner<DatacenterBrokerHeuristicExperiment> {

    /**
     * Number of Cloudlets to create for each experiment.
     */
    public static final int CLOUDLETS_TO_CREATE = 100;

    /**
     * Possible number of PEs for VMs to be created. Each VM has to have one of
     * this number of PEs, to be chosen randomly.
     */
    private static final int VM_PES_NUMBERS[] = {2, 4, 8};

    /**
     * Number of Vm's to create for each experiment.
     */
    private static final int VMS_TO_CREATE = VM_PES_NUMBERS.length * 20;

    /**
     * Number of PEs for each created Cloudlet. All the experiments run with the
     * same scenario configuration, including number of hosts, VMs and
     * Cloudlets. What changes is the random number generator seed for each
     * experiment.
     */
    private int cloudletPesArray[];

    /**
     * Number of PEs for each created VM. All the experiments run with the same
     * scenario configuration, including number of hosts, VMs and Cloudlets.
     * What changes is the random number generator seed for each experiment.
     */
    private int vmPesArray[];

    /**
     * The cost to map Cloudlets to VMs for each executed experiment.
     */
    private List<Double> experimentCosts;

    /**
     * An object that compute statistics about experiment execution time of all
     * executed experiment runs.
     */
    private final SummaryStatistics runtimeStats;

    /**
     * A Cloudlet to VM mapping that used a Round-Robin implementation to
     * cyclically select a Vm from the Vm list to host a Cloudlet. This is the
     * implementation used by the {@link DatacenterBrokerSimple} class.
     */
    private CloudletToVmMappingSolution roundRobinSolution;

    /**
     * Indicates if each experiment will output execution logs or not.
     */
    private final boolean experimentVerbose = false;

    DatacenterBrokerHeuristicRunner(final boolean applyAntitheticVariatesTechnique, final long baseSeed) {
        super(applyAntitheticVariatesTechnique, baseSeed);
        experimentCosts = new ArrayList<>();
        runtimeStats = new SummaryStatistics();
        vmPesArray = new int[0];
        cloudletPesArray = new int[0];
    }

    /**
     * Starts the execution of the experiments the number of times defines in
     * {@link #getSimulationRuns()}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        /*
        Values used for CloudSim Plus Paper:
            NumberOfSimulationRuns: 1200
            ApplyAntitheticVariatesTechnique: true
            NumberOfBatches: 6
            BaseSeed: 1475098589732L
         */
        new DatacenterBrokerHeuristicRunner(true, 1475098589732L)
                .setSimulationRuns(1200)
                .setNumberOfBatches(6) //Comment this or set to 0 to disable the "Batch Means Method"
                .setVerbose(true)
                .run();
    }

    /**
     * Creates an array with the configuration of PEs for each Cloudlet to be
     * created in each experiment run. Every experiment will use the same
     * Cloudlets configurations.
     *
     * @return the created cloudlet PEs array
     */
    private int[] createCloudletPesArray() {
        int[] pesArray = new int[CLOUDLETS_TO_CREATE];
        int totalNumberOfPes = 0;
        final ContinuousDistribution random = new NormalDistr(getBaseSeed(), 2, 0.6);
        for (int i = 0; i < CLOUDLETS_TO_CREATE; i++) {
            pesArray[i] = (int) random.sample() + 1;
            totalNumberOfPes += pesArray[i];
        }

        return pesArray;
    }

    /**
     * Creates an array with the configuration of PEs for each VM to be created
     * in each experiment run. Every experiment will use the same VMs
     * configurations.
     *
     * @return the created VMs PEs array
     */
    private int[] createVmPesArray() {
        final UniformDistr random = new UniformDistr(0, VM_PES_NUMBERS.length, getBaseSeed());
        int[] pesArray = new int[VMS_TO_CREATE];
        int totalNumberOfPes = 0;
        for (int i = 0; i < VMS_TO_CREATE; i++) {
            pesArray[i] = VM_PES_NUMBERS[(int) random.sample()];
            totalNumberOfPes += pesArray[i];
        }

        return pesArray;
    }

    /**
     * Adds the computed cost to map Cloudlets to a VM for the current
     * experiment to the list of mapping costs.
     *
     * @param cost the cost to add
     */
    public void addExperimentCost(double cost) {
        experimentCosts.add(cost);
    }

    /**
     * Adds the run time that the simulated annealing heuristic spent to compute
     * the mapping of Cloudlets to a VM for the current experiment to the list
     * of run times.
     *
     * @param runTime the run time to add
     */
    public void addSimulatedAnnealingRuntime(double runTime) {
        runtimeStats.addValue(runTime);
    }

    @Override
    protected DatacenterBrokerHeuristicExperiment createExperiment(int i) {
        final ContinuousDistribution prng = createRandomGen(i, 0, 1);
        final DatacenterBrokerHeuristicExperiment exp
                = new DatacenterBrokerHeuristicExperiment(i, this)
                        .setRandomGen(prng)
                        .setCloudletPesArray(cloudletPesArray)
                        .setVmPesArray(vmPesArray);

        exp.setVerbose(experimentVerbose).setAfterExperimentFinish(this::afterExperimentFinish);
        return exp;
    }

    @Override
    protected void setup() {
        experimentCosts = new ArrayList<>(getSimulationRuns());
        vmPesArray = createVmPesArray();
        cloudletPesArray = createCloudletPesArray();
    }

    /**
     * Method automatically called after every experiment finishes running. It
     * performs some post-processing such as collection of data for statistic
     * analysis.
     *
     * @param experiment the finished experiment
     */
    private void afterExperimentFinish(DatacenterBrokerHeuristicExperiment experiment) {
        final CloudletToVmMappingSolution solution = experiment.getHeuristic().getBestSolutionSoFar();
        addExperimentCost(solution.getCost());
        addSimulatedAnnealingRuntime(solution.getHeuristic().getSolveTime());
        createRoundRobinSolutionIfNotCreatedYet(experiment);
    }

    @Override
    protected Map<String, List<Double>> createMetricsMap() {
        final Map<String, List<Double>> map = new HashMap<>();
        map.put("Experiments Cost", experimentCosts);
        return map;
    }

    @Override
    protected void printSimulationParameters() {
        System.out.printf("Executing %d experiments. Please wait ... It may take a while.\n", getSimulationRuns());
        System.out.println("Experiments configurations:");
        System.out.printf("\tBase seed: %d | Number of VMs: %d | Number of Cloudlets: %d\n", getBaseSeed(), VMS_TO_CREATE, CLOUDLETS_TO_CREATE);
        System.out.printf("\tApply Antithetic Variates Technique: %b\n", isApplyAntitheticVariatesTechnique());
        if (isApplyBatchMeansMethod()) {
            System.out.println("\tApply Batch Means Method to reduce simulation results correlation: true");
            System.out.printf("\tNumber of Batches for Batch Means Method: %d", getNumberOfBatches());
            System.out.printf("\tBatch Size: %d\n", batchSizeCeil());
        }
        System.out.printf("\nSimulated Annealing Parameters\n");
        System.out.printf(
                "\tInitial Temperature: %.2f | Cold Temperature: %.4f | Cooling Rate: %.3f | Neighborhood searches by iteration: %d\n",
                DatacenterBrokerHeuristicExperiment.SA_INIT_TEMPERATURE,
                DatacenterBrokerHeuristicExperiment.SA_COLD_TEMPERATURE,
                DatacenterBrokerHeuristicExperiment.SA_COOLING_RATE,
                DatacenterBrokerHeuristicExperiment.SA_NEIGHBORHOOD_SEARCHES);
    }

    @Override
    protected void printFinalResults(String metricName, SummaryStatistics stats) {
        System.out.printf("\n# %s for %d simulation runs\n", metricName, getSimulationRuns());
        if (!simulationRunsAndNumberOfBatchesAreCompatible()) {
            System.out.println("\tBatch means method was not be applied because the number of simulation runs is not greater than the number of batches.");
        }
        System.out.printf(
                "\tRound-robin solution used by DatacenterBrokerSimple - Cost: %.2f\n",
                roundRobinSolution.getCost());

        if (getSimulationRuns() > 1) {
            System.out.printf(
                    "\tHeuristic solutions - Mean cost: %.2f Std. Dev.: %.2f\n",
                    stats.getMean(), stats.getStandardDeviation());
            showConfidenceInterval(stats);
            System.out.printf(
                    "\n\tThe mean cost of heuristic solutions represent %.2f%% of the Round-robin mapping used by the DatacenterBrokerSimple\n",
                    heuristicSolutionCostPercentageOfRoundRobinSolution(stats.getMean()));
            System.out.printf("Experiment execution mean time: %.2f seconds\n", runtimeStats.getMean());
        }
    }


    /**
     * Computes the percentage of the Round-robin solution cost that the
     * heuristic solution cost represents.
     *
     * @param heuristicCost the cost of the heuristic solution
     * @return the percentage of the Round-robin solution cost that the
     * heuristic solution represents
     */
    private double heuristicSolutionCostPercentageOfRoundRobinSolution(double heuristicCost) {
        return heuristicCost * 100.0 / roundRobinSolution.getCost();
    }

    private void showConfidenceInterval(SummaryStatistics stats) {
        // Calculate 95% confidence interval
        final double intervalSize = computeConfidenceErrorMargin(stats, 0.95);
        final double lower = stats.getMean() - intervalSize;
        final double upper = stats.getMean() + intervalSize;
        System.out.printf(
                "\tSolution cost mean 95%% Confidence Interval: %.2f ∓ %.2f, that is [%.2f to %.2f]\n",
                stats.getMean(), intervalSize, lower, upper);
    }

    /**
     * Creates a Round-robin mapping between Cloudlets and Vm's from the
     * Cloudlets and Vm's of a given experiment, in the same way as the
     * {@link DatacenterBrokerSimple} does.
     *
     * @param exp the experiment to get the list of Cloudlets and Vm's
     */
    public void createRoundRobinSolutionIfNotCreatedYet(SimulationExperiment exp) {
        if (roundRobinSolution != null) {
            return;
        }

        roundRobinSolution = new CloudletToVmMappingSolution(Heuristic.NULL);
        int i = 0;
        for (final Cloudlet c : exp.getCloudletList()) {
            //cyclically selects a Vm (as in a circular queue)
            roundRobinSolution.bindCloudletToVm(c, exp.getVmList().get(i));
            i = (i + 1) % exp.getVmList().size();
        }
    }

}
