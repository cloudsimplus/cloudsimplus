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
/*
 */
package org.cloudsimplus.testbeds.hostfaultinjection;

import java.util.*;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.testbeds.ExperimentRunner;

import static java.util.stream.Collectors.toMap;

/**
 * Runs the {@link HostFaultInjectionExperiment} the number of
 * times defines by {@link #getSimulationRuns()} and compute statistics.
 *
 * <p>For more details, check
 * <a href="http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf">Raysa Oliveira's Master Thesis (only in Portuguese)</a>.</p>
 *
 * @author raysaoliveira
 */
public final class HostFaultInjectionRunner extends ExperimentRunner<HostFaultInjectionExperiment> {
    /**
     * Different lengths that will be randomly assigned to created Cloudlets.
     */
    static final long[] CLOUDLET_LENGTHS = {1000_000_000L, 1800_000_000L, 2800_000_000L };

    /**
     * Indicates if each experiment will output execution logs or not.
     */
    private final boolean experimentVerbose = false;

    /**
     * Datacenter availability for each experiment.
     */
    private final List<Double> availability;

    /**
     * A map of each availability achieved by each broker for each experiment.
     * The key is a broker's name and the List for that key represents the availability
     * reached by that broker in each experiment, for instance:
     *
     * <ul>
     *    <li>Broker1 -> {AvailabilityExperiment1, AvailabilityExperiment2 ... AvailabilityExperimentN}</li>
     *    <li>Broker2 -> {AvailabilityExperiment1, AvailabilityExperiment2 ... AvailabilityExperimentN}</li>
     *    <li>BrokerN -> {AvailabilityExperiment1, AvailabilityExperiment2 ... AvailabilityExperimentN}</li>
     * </ul>
     *
     * <p>It's used the broker's name instead of an actual broker instance
     * because, despite each simulation run creates the same brokers
     * (one for each SLA JSON file), they are different instances.
     * As we want to relate, for instance, the broker 0 of one experiment
     * run with the same broker 0 in all other runs,
     * it's needed to use a String.
     * </p>
     */
    private Map<String, List<Double>> availabilityByBroker;

    /**
     * Percentage of brokers meeting Availability average for each experiment.
     */
    private List<Double> percentageOfBrokersMeetingAvailability;

    /**
     * Average number of VMs for each existing Host.
     */
    private final List<Double> ratioVmsPerHost;

    /**
     * Gets the cost total of each broker.
     */
    private Map<String, List<Double>> costTotal;

    private final Map<String, List<Double>> template;

    private HostFaultInjectionRunner(final boolean applyAntitheticVariatesTechnique, final long baseSeed) {
        super(applyAntitheticVariatesTechnique, baseSeed);
        availabilityByBroker = new HashMap<>();
        availability = new ArrayList<>();
        percentageOfBrokersMeetingAvailability = new ArrayList<>();
        ratioVmsPerHost = new ArrayList<>();
        costTotal = new HashMap<>();
        template = new HashMap<>();
    }

    /**
     * Starts the execution of the experiments the number of times defines in
     * {@link #getSimulationRuns()}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new HostFaultInjectionRunner(true, 1475098589732L)
            .setSimulationRuns(400)
            .setNumberOfBatches(5) //Comment this or set to 0 to disable the "Batch Means Method"
            .setVerbose(true)
            .run();
    }

    @Override
    protected HostFaultInjectionExperiment createExperiment(final int i) {
        final HostFaultInjectionExperiment exp = new HostFaultInjectionExperiment(i, this);
        exp.setVerbose(experimentVerbose)
            .setAfterExperimentFinish(this::afterExperimentFinish);
        return exp;
    }

    /**
     * Method automatically called after every experiment finishes running. It
     * performs some post-processing such as collection of data for statistic
     * analysis.
     *
     * @param exp the finished experiment
     */
    private void afterExperimentFinish(HostFaultInjectionExperiment exp) {
        final HostFaultInjection faultInjection = exp.getFaultInjection();

        availability.add(faultInjection.availability() * 100);
        ratioVmsPerHost.add(exp.getRatioVmsPerHost());
        percentageOfBrokersMeetingAvailability.add(exp.getPercentageOfAvailabilityMeetingSla() * 100);

        //The availability for each broker for a single experiment.
        final Map<DatacenterBroker, Double> brokersAvailabilities = exp.getBrokerList()
            .stream()
            .sorted()
            .collect(toMap(b -> b, faultInjection::availability));

        final Map<DatacenterBroker, Double> costBrokers = exp.getBrokerList()
            .stream()
            .sorted()
            .collect(toMap(b -> b, exp::getTotalCost));

        final Map<DatacenterBroker, Double> getCustomerActualPricePerHour = exp.getBrokerList()
            .stream()
            .sorted()
            .collect(toMap(b -> b, exp::getCustomerActualPricePerHour));

        /*
         * Gets the availability of each broker for the current experiment
         * and adds such a value to the List of availability of each broker
         * inside the map of all brokers
         */
        brokersAvailabilities.forEach(this::addExperimentAvailabilityToBroker);

        costBrokers.forEach(this::addExperimentCostToBroker);

        getCustomerActualPricePerHour.forEach(this::addExperimentPriceCustomerPerHour);
    }

    /**
     * Gets the availability of a broker for an experiment
     * and adds such a value to the List of availability of that broker
     * inside the map of all brokers.
     * @param broker the broker to add the availability of an experiment to its list of availabilities
     * @param availability the availability of the broker for the experiment
     */
    private boolean addExperimentAvailabilityToBroker(final DatacenterBroker broker, final double availability) {
        return availabilityByBroker.computeIfAbsent(broker.getName(), name -> new ArrayList<>()).add(availability);
    }

    /**
     * Gets the cost of a broker for an experiment
     * and adds such a value to the List of costs of that broker
     * inside the map of all brokers.
     * @param broker the broker to add the availability of an experiment to its list of availabilities
     * @param cost the cost of the broker for the experiment
     */
    private boolean addExperimentCostToBroker(final DatacenterBroker broker, final double cost) {
        return costTotal.computeIfAbsent(broker.getName(), name -> new ArrayList<>()).add(cost);
    }

    /**
     * Gets the actual price of all customers VMs per hour for an experiment
     * and adds such a value to the List of costs of that broker
     * inside the map of all brokers.
     * @param broker the broker to add the availability of an experiment to its list of availabilities
     * @param priceCustomerPerHour the customer's price of the broker for the experiment
     */
    private boolean addExperimentPriceCustomerPerHour(final DatacenterBroker broker, final double priceCustomerPerHour) {
        return template.computeIfAbsent(broker.getName(), name -> new ArrayList<>()).add(priceCustomerPerHour);
    }

    @Override protected void setup() {/**/}

    @Override
    protected Map<String, List<Double>> createMetricsMap() {
        final Map<String, List<Double>> map = new HashMap<>();
        map.put("Average of Total Availability of Simulation", availability);
        map.put("VMs/Hosts Ratio: ", ratioVmsPerHost);
        map.put("Percentagem of brokers meeting the Availability: ", percentageOfBrokersMeetingAvailability);

        return map;
    }

    @Override
    protected void printSimulationParameters() {
        System.out.printf("Executing %d experiments. Please wait ... It may take a while.\n", getSimulationRuns());
        System.out.println("Experiments configurations:");
        System.out.printf("\tBase seed: %d \n", getBaseSeed());
        System.out.printf("\tApply Antithetic Variates Technique: %b\n", isApplyAntitheticVariatesTechnique());
        if (isApplyBatchMeansMethod()) {
            System.out.println("\tApply Batch Means Method to reduce simulation results correlation: true");
            System.out.printf("\tNumber of Batches for Batch Means Method: %d", getNumberOfBatches());
            System.out.printf("\tBatch Size: %d\n", batchSizeCeil());
        }
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
        // Computes 95% confidence interval
        double intervalSize = computeConfidenceErrorMargin(stats, 0.95);
        double lower = stats.getMean() - intervalSize;
        double upper = stats.getMean() + intervalSize;
        System.out.printf(
            "\tThis METRIC mean 95%% Confidence Interval: %.6f ∓ %.4f, that is [%.4f to %.4f]\n",
            stats.getMean(), intervalSize, lower, upper);
        System.out.printf("\tStandard Deviation: %.4f \n", stats.getStandardDeviation());
    }

}
