/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.testbeds;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.StatisticalDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.util.Util;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * A base class to run a given experiment a defined number of times and collect
 * statistics about the execution. The runner represents a testbed compounded of
 * a set of experiments that it runs.
 *
 * @param <T> the type of {@link Experiment} the runner will execute
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public abstract class ExperimentRunner<T extends Experiment> extends AbstractRunnable {
    /**@see #isParallel() */
    private final boolean parallel;

    private boolean showProgress;
    private boolean progressBarInNewLine;

    private int firstExperimentCreated = -1;

    /** @see #getBaseSeed() */
    private final long baseSeed;

    /** List of seeds used for each experiment.
     * @see #addSeed(long)  */
    private final List<Long> seeds;

    /** @see #getSimulationRuns() */
    private int simulationRuns;

    private final AtomicInteger finishedRuns;

    /** @see #getExperimentsStartTimeSecs() */
    private long experimentsStartTimeSecs;

    /** @see #getExperimentsExecutionTimeSecs() */
    private long experimentsExecutionTimeSecs;

    /** @see #isApplyAntitheticVariates() */
    private final boolean applyAntitheticVariates;

    /** @see #getBatchesNumber() */
    private final int batchesNumber;

    /**
     * A Map containing a List of values for each metric to be computed.
     * The computation of final experiments results are performed on this map.
     *
     * <p>Each key is the metric name and each value is a List of Double
     * containing the values collected for that metric, for each experiment run.
     * These values will be then summarized to compute the final value
     * for each metric.</p>
     *
     * <p>The values to be added for each metric on this map
     * should be collected by the experiment finish listener.
     * The listener can be set inside the runner's {@link #createExperimentInternal(int)}.</p>
     * @see Experiment#setAfterExperimentFinish(Consumer)
     */
    private final Map<String, List<Double>> metricsMap;

    /** @see #setDescription(String) */
    private String description;

    /** @see #setResultsTableId(String) */
    private String resultsTableId;

    /** @see #enableLatexTableResultsGeneration()*/
    private boolean latexTableResultsGeneration;
    private List<Experiment> experiments;

    /**
     * Creates an experiment runner with a given {@link #getBaseSeed() base seed}
     * that runs sequentially.
     * @param baseSeed the seed to be used as base for each experiment seed
     * @param simulationRuns the number of times the experiment will be executed
     */
    protected ExperimentRunner(final long baseSeed, final int simulationRuns) {
        this(baseSeed, simulationRuns, false);
    }

    /**
     * Creates an experiment runner with a given {@link #getBaseSeed() base seed}
     * that runs sequentially.
     * @param baseSeed the seed to be used as base for each experiment seed
     * @param simulationRuns the number of times the experiment will be executed
     * @param latexTableResultsGeneration Enables/disables the generation of a result table in Latex format for computed metrics.
     * @param parallel whether experiments will run in parallel or sequentially
     */
    protected ExperimentRunner(final long baseSeed, final int simulationRuns, final boolean latexTableResultsGeneration, final boolean parallel) {
        this(baseSeed, simulationRuns, 0, false, parallel, latexTableResultsGeneration);
    }

    /**
     * Creates an experiment runner with a given {@link #getBaseSeed() base seed}
     * that runs sequentially.
     * @param baseSeed the seed to be used as base for each experiment seed
     * @param simulationRuns the number of times the experiment will be executed
     * @param applyAntitheticVariates indicates if it's to be applied the
     *                                    <a href="https://en.wikipedia.org/wiki/Antithetic_variates">antithetic variates technique</a>.
     */
    protected ExperimentRunner(final long baseSeed, final int simulationRuns, final boolean applyAntitheticVariates) {
        this(baseSeed, simulationRuns, 0, applyAntitheticVariates, false, false);
    }

    /**
     * Creates an experiment runner with a given {@link #getBaseSeed() base seed}
     * that runs sequentially.
     * @param baseSeed the seed to be used as base for each experiment seed
     * @param simulationRuns the number of times the experiment will be executed
     * @param batchesNumber number of simulation run batches (zero disables the batch means method)
     * @param applyAntitheticVariates indicates if it's to be applied the
     *                                    <a href="https://en.wikipedia.org/wiki/Antithetic_variates">antithetic variates technique</a>.
     */
    protected ExperimentRunner(final long baseSeed, final int simulationRuns, final int batchesNumber, final boolean applyAntitheticVariates) {
        this(baseSeed, simulationRuns, batchesNumber, applyAntitheticVariates, false, false);
    }

    /**
     * Creates an experiment runner with a given {@link #getBaseSeed() base seed}.
     * @param baseSeed the seed to be used as base for each experiment seed
     * @param simulationRuns the number of times the experiment will be executed
     * @param batchesNumber number of simulation run batches (zero disables the batch means method)
     * @param applyAntitheticVariates indicates if it's to be applied the
     *                                    <a href="https://en.wikipedia.org/wiki/Antithetic_variates">antithetic variates technique</a>.
     * @param parallel whether experiments will run in parallel or sequentially. It's just actually enabled when the simulation runs
     *                 is larger than 1.
     * @param latexTableResultsGeneration Enables/disables the generation of a result table in Latex format for computed metrics.
     */
    protected ExperimentRunner(
        final long baseSeed, final int simulationRuns, final int batchesNumber,
        final boolean applyAntitheticVariates,
        final boolean parallel, final boolean latexTableResultsGeneration)
    {
        super();
        this.baseSeed = baseSeed;
        this.applyAntitheticVariates = applyAntitheticVariates;
        this.simulationRuns = validateSimulationRuns(simulationRuns);
        this.finishedRuns = new AtomicInteger();
        this.parallel = parallel && simulationRuns > 1;
        this.showProgress = true;

        this.batchesNumber = validateBatchesNumber(batchesNumber);
        this.latexTableResultsGeneration = latexTableResultsGeneration;

        /*Since experiments may run in parallel and these fields are shared across them,
        * we need to synchronize these collections.*/
        this.seeds = parallel ? Collections.synchronizedList(new ArrayList<>()) : new ArrayList<>();
        this.metricsMap = parallel ? Collections.synchronizedMap(new TreeMap<>()) : new TreeMap<>();

        setSimulationRunsAndBatchesToEvenNumber();
        setNumberOfSimulationRunsAsMultipleOfNumberOfBatches();
    }

    private int validateBatchesNumber(final int batchesNumber) {
        if(batchesNumber < 0 || batchesNumber == 1) {
            throw new IllegalArgumentException("Batches number must be greater than 1. Use 0 just to disable the Batch Means method.");
        }

        return batchesNumber;
    }

    private int validateSimulationRuns(final int simulationRuns) {
        if(simulationRuns <= 0) {
            throw new IllegalArgumentException("Simulation runs must be greater than 0.");
        }

        return simulationRuns;
    }

    /**
     * Sets the number of simulation runs and batches to an even number. The
     * "Antithetic Variates Technique" for variance reduction requires an even
     * number of simulation runs. Accordingly, if the "Batch Means Method" is
     * used simultaneously, the number of batches has to be even.
     */
    private void setSimulationRunsAndBatchesToEvenNumber() {
        if (!(isApplyBatchMeansMethod() || isApplyAntitheticVariates())) {
            return;
        }

        if (getSimulationRuns() % 2 != 0) {
            simulationRuns++;
        }

        if (getBatchesNumber() > 0 && getSimulationRuns() % getBatchesNumber()  != 0) {
            setSimulationRunsAsMultipleOfBatchNumber();
        }
    }

    /**
     * Adjusts the number of simulation runs to be multiple of the number of
     * batches, once each batch has to have the same size. If
     * applyAntitheticVariatesTechnique is true, the number of batches will be
     * even and consequently, the number of simulation runs after being adjusted
     * will be even too.
     */
    private void setNumberOfSimulationRunsAsMultipleOfNumberOfBatches() {
        if (isApplyBatchMeansAndSimulationRunsIsNotMultipleOfBatches()) {
            simulationRuns = batchSizeCeil() * getBatchesNumber();
        }
    }

    /**
     * Checks if it is to use the "Batch Means Method" and the number of
     * simulation runs is not multiple of the number of batches.
     */
    private boolean isApplyBatchMeansAndSimulationRunsIsNotMultipleOfBatches() {
        return isApplyBatchMeansMethod() && getSimulationRuns() % getBatchesNumber() != 0;
    }

    /**
     * @return the batch size rounded by the {@link Math#ceil(double)} method.
     */
    public int batchSizeCeil() {
        return (int) Math.ceil(simulationRuns / (double) batchesNumber);
    }

    /**
     *
     * Checks if the "Batch Means Method" is to be applied to reduce correlation
     * between the results for different experiment runs.
     * That happens if the number of simulation runs and the number of batches are
     * compatible.
     * @return
     */
    public boolean isApplyBatchMeansMethod() {
        final boolean batchesGreaterThan1 = batchesNumber > 1;
        final boolean runsIsGraterThanBatches = simulationRuns > batchesNumber;
        return batchesGreaterThan1 && runsIsGraterThanBatches;
    }

    /**
     * Gets an list of samples and apply the "Batch Means Method" to reduce
     * samples correlation, if the "Batch Means Method"
     * {@link #isApplyBatchMeansMethod() is to be applied}.
     *
     * @param samples the list with samples to apply the "Batch Means Method".
     *                Samples size is defined by the {@link #getSimulationRuns()}.
     * @return the samples list after applying the "Batch Means Method", in case
     * the method is enabled to be applied, which will reduce the array to the
     * number of batches defined by {@link #getBatchesNumber()} (each value in
     * the returned array will be the mean of every sample batch). Otherwise,
     * returns the same given array
     */
    protected List<Double> computeBatchMeans(final List<Double> samples) {
        if (!isApplyBatchMeansMethod()) {
            return samples;
        }

        final var batchMeans = new ArrayList<Double>(getBatchesNumber());
        for (int i = 0; i < getBatchesNumber(); i++) {
            batchMeans.add(getBatchAverage(samples, i));
        }

        System.out.printf(
            "\tBatch Means Method applied. The number of samples was reduced to %d after computing the mean for each batch.%n",
            getBatchesNumber());

        return batchMeans;
    }

    /**
     * Gets the average for the values of a given batch <i>i</i>.
     * If there are 10 simulation runs and the number of batches is 5,
     * the batch size is 2 (⎡10/2⎤) and each batch will be formed as follows:
     *
     * <center>
     *     {0 1} {2 3} {4 5} {6 7} {8 9}
     * </center>
     *
     * @param samples the list with samples to apply the "Batch Means Method".
     *                Samples size is defined by the {@link #getSimulationRuns()}.
     * @param index the index of the batch to get its values average
     * @return the average for the values of a given batch
     */
    private double getBatchAverage(final List<Double> samples, final int index) {
        final int k = batchSizeCeil();
        return IntStream.range(0, k).mapToDouble(j -> samples.get(getBatchElementIndex(index, j))).average().orElse(0.0);
    }

    /**
     * Gets the absolute position of the <i>jth</i> element of a batch <i>i</i>,
     * from the samples of all experiments.
     * If there are 12 simulation runs and the number of batches is 3,
     * the batch size is 4 (⎡12/3⎤). The elements of the batch 2, for instance, from
     * the samples of all experiments, will be the ones inside the brackets below:
     *
     * <center>
     *     0 1 2 3 4 5 {6 <b>7</b> 8} 9 10 11
     * </center>
     *
     * <p>This way, the absolute position of the 2nd (<i>j</i>) element inside the batch 3 (<i>i</i>) is 7 (in bold).</p>
     *
     * @param i the index of the batch to get the absolute position of one of its elements
     * @param j the relative position of the element to get inside the batch
     * @return the absolute position of the <i>jth</i> element of the batch
     */
    private int getBatchElementIndex(final int i, final int j) {
        final int k = batchSizeCeil();
        return i*k + j;
    }

    /**
     * Checks if the "Antithetic Variates Technique" is to be applied to reduce
     * results variance.
     *
     * @return
     * @see
     * <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic variates</a>
     */
    public final boolean isApplyAntitheticVariates() {
        return applyAntitheticVariates;
    }

    /**
     * Gets the number of times the experiment will be executed in order to get
     * values such as means and standard deviations. It has to be an even number
     * if the
     * {@link #isApplyAntitheticVariates() "Antithetic Variates Technique"}
     * is to be used.
     * @return
     */
    public final int getSimulationRuns() {
        return simulationRuns;
    }

    /**
     * Checks if the experiment will run a single time or not.
     * @return true if the experiment will run a single time,
     *         false if there are multiple simulation runs.
     */
    public boolean isSingleRun(){
        return simulationRuns == 1;
    }

    /**
     * Adjusts the current number of simulations to be equal to its closer
     * multiple of the number of batches.
     * @return
     */
    private ExperimentRunner setSimulationRunsAsMultipleOfBatchNumber() {
        final double batches = getBatchesNumber();
        simulationRuns = (int)(batches * Math.ceil(simulationRuns / batches));
         return this;
    }

    /**
     * Gets the number of batches in which the simulation runs will be divided.
     *
     * If this number is greater than 1, the "Batch Means Method" is used to
     * reduce the correlation between experiment runs.
     * @return
     */
    public int getBatchesNumber() {
        return batchesNumber;
    }

    /**
     * Gets the seed to be used for the first executed experiment.
     * The seed for each subsequent experiment is this seed plus the index
     * of the experiment.
     *
     * @return
     */
    public long getBaseSeed() {
        return baseSeed;
    }

    public long getSeed(final int experimentIndex) {
        return seeds.get(experimentIndex);
    }

    /**
     * Uses the provided {@link Function} to create a pseudo random number generator (PRNG) for a experiment run.
     * The kind and parameters for this PRNG is defined internally by the given Function.
     * This method calls that Function just providing the seed to be used for the current experiment run.
     *
     * If it is to apply the
     * {@link #isApplyAntitheticVariates() "Antithetic Variates Technique"}
     * to reduce results variance, the second half of experiments will used the
     * seeds from the first half.
     *
     * @param experimentIndex index of the experiment run to create a PRNG
     * @param randomGenCreator a {@link Function} that receives a seed generated by the runner and returns a new instance of some PRNG
     * @return the created PRNG with the seed provided by the runner
     *
     * @see UniformDistr#isApplyAntitheticVariates()
     * @see #createRandomGen(int, double, double)
     */
    public <S extends StatisticalDistribution> S createRandomGen(final int experimentIndex, final Function<Long, S> randomGenCreator) {
        Objects.requireNonNull(randomGenCreator, "The Function to instantiate the Random Number Generator cannot be null.");

        if(seeds.isEmpty()){
            throw new IllegalStateException(
                "You have to create at least 1 SimulationExperiment before requesting a ExperimentRunner to create a pseudo random number generator (PRNG)!");
        }

        if (isToReuseSeedFromFirstHalfOfExperiments(experimentIndex)) {
            final int expIndexFromFirstHalf = experimentIndex - halfSimulationRuns();
            final S prng = randomGenCreator.apply(getSeed(expIndexFromFirstHalf));
            prng.setApplyAntitheticVariates(true);
            return prng;
        }

        return randomGenCreator.apply(getSeed(experimentIndex));
    }

    /**
     * Creates a pseudo random number generator (PRNG) for a experiment run that
     * generates uniform values between [0 and 1[. If it is to apply the
     * {@link #isApplyAntitheticVariates() "Antithetic Variates Technique"}
     * to reduce results variance, the second half of experiments will used the
     * seeds from the first half.
     *
     * @param experimentIndex index of the experiment run to create a PRNG
     * @return the created PRNG
     *
     * @see UniformDistr#isApplyAntitheticVariates()
     * @see #createRandomGen(int, double, double)
     */
    public ContinuousDistribution createRandomGen(final int experimentIndex) {
        return createRandomGen(experimentIndex,0, 1);
    }

    /**
     * Creates a pseudo random number generator (PRNG) for a experiment run that
     * generates uniform values between [min and max[. If it is to apply the
     * {@link #isApplyAntitheticVariates() "Antithetic Variates Technique"}
     * to reduce results' variance, the second half of experiments will use the
     * seeds from the first half.
     *
     * @param experimentIndex index of the experiment run to create a PRNG
     * @param minInclusive the minimum value the generator will return (inclusive)
     * @param maxExclusive the maximum value the generator will return (exclusive)
     * @return the created PRNG
     *
     * @see UniformDistr#isApplyAntitheticVariates()
     * @see #createRandomGen(int)
     */
    public ContinuousDistribution createRandomGen(final int experimentIndex, final double minInclusive, final double maxExclusive) {
        return createRandomGen(
            experimentIndex,
            seed -> new UniformDistr(minInclusive, maxExclusive, seed));
    }

    public boolean isToReuseSeedFromFirstHalfOfExperiments(final int currentExperimentIndex) {
        return isApplyAntitheticVariates() &&
               simulationRuns > 1 && currentExperimentIndex >= halfSimulationRuns();
    }

    /**
     * Adds a seed to the list of seeds used for each experiment.
     *
     * @param seed seed of the current experiment to add to the list
     */
    void addSeed(final long seed) {
        if(!seeds.contains(seed)){
            seeds.add(seed);
        }
    }

    /**
     * @return the half of {@link #getSimulationRuns()}
     */
    public int halfSimulationRuns() {
        return simulationRuns / 2;
    }

    /**
     * Time in seconds the experiments took to finish.
     * @return
     */
    public long getExperimentsExecutionTimeSecs() {
        return experimentsExecutionTimeSecs;
    }

    /**
     * Time in seconds the experiments started.
     * @return
     */
    public long getExperimentsStartTimeSecs() {
        return experimentsStartTimeSecs;
    }

    /**
     * Setups and starts the execution of all experiments sequentially or in {@link #parallel}.
     */
    @Override
    public void run() {
        createAllExperimentsBeforeFirstRun();

        final String runWord = simulationRuns > 1 ? "runs" : "run";
        System.out.printf(
            "Started %s for %d %s using %s (real local time: %s)%n",
            getClass().getSimpleName(), simulationRuns, runWord, CloudSim.VERSION, LocalTime.now());
        if(description != null && !description.isBlank()){
            System.out.println(description);
        }
        printSimulationParameters();

        experimentsStartTimeSecs = Math.round(System.currentTimeMillis()/1000.0);
        printProgress(0);
        getStream(this.experiments).forEach(Experiment::run);
        System.out.println();
        experimentsExecutionTimeSecs = TimeUtil.elapsedSeconds(experimentsStartTimeSecs);

        System.out.printf(
            "%nFinal simulation results for %d metrics in %d simulation runs -------------------%n",
            metricsMap.size(), simulationRuns);
        if (batchesNumber > 1 && !isApplyBatchMeansMethod()) {
            System.out.println("Batch means method was not be applied because the number of simulation runs is not greater than the number of batches.");
        }
        computeAndPrintFinalResults();

        System.out.printf(
            "%nExperiments for %d runs finished in %s (real local time: %s)!%n",
            simulationRuns, TimeUtil.secondsToStr(experimentsExecutionTimeSecs), LocalTime.now());
    }

    /** Since experiments may execute in parallel and during execution
     * they access the shared seeds list, all experiments have to
     * be created before starting execution.
     * Otherwise, if they are run in parallel, we get IndexOutOfBoundsException's.
     */
    private void createAllExperimentsBeforeFirstRun() {
        if(experiments == null) {
            experiments = IntStream.range(0, simulationRuns).mapToObj(this::createExperiment).collect(toList());
        }
    }

    private void computeAndPrintFinalResults() {
        final List<ConfidenceInterval> confidenceIntervals =
            metricsMap.entrySet()
                      .stream()
                      .map(this::computeFinalResults)
                      .collect(toCollection(() -> new ArrayList<>(metricsMap.size())));

        final var table = new ResultTable<>(this, confidenceIntervals);
        table.buildLatexMetricsResultTable();
        table.buildCsvResultsTable();
    }

    private Stream<Experiment> getStream(final List<Experiment> experiments) {
        return parallel ? experiments.stream().parallel() : experiments.stream();
    }

    /**
     * Template method that creates an experiment to be run for the i'th time.
     *
     * @param index a number that identifies the experiment
     * @return the created experiment
     * @see #createExperimentInternal(int)
     */
    private Experiment createExperiment(final int index) {
        print((index + 1) % 100 == 0 ? String.format(". Run #%d%n", index + 1) : ".");
        setFirstExperimentCreated(index);
        return createExperimentInternal(index);
    }

    /**
     * Creates an experiment to be run for the i'th time.
     *
     * @param index a number that identifies the experiment
     * @return the created experiment
     * @see #createExperiment(int)
     */
    protected abstract T createExperimentInternal(int index);

    /**
     * Computes final simulation results, including mean, standard deviations and
     * confidence intervals for a given metric computed across all simulation runs.
     *
     * @param metricEntry a map entry represented by the name of the metric and
     *                    its list of values across multiple simulation runs
     * @return the computed {@link ConfidenceInterval} from the provided values for the metric
     */
    protected ConfidenceInterval computeFinalResults(final Map.Entry<String, List<Double>> metricEntry){
        final List<Double> metricValues = metricEntry.getValue();
        final SummaryStatistics stats = computeFinalStatistics(metricValues);
        //System.out.printf("# %s: %.6f (%d %s)%n", metricEntry.getKey(), stats.getMean(), metricValues.size(), metricValues.size() > 1 ? "samples" : "sample");

        return new ConfidenceInterval(stats, metricEntry.getKey());
    }

    /**
     * Creates a {@link SummaryStatistics} object from a list of
     * Double values, allowing computation of statistics such as mean over these values.
     * The method also checks if the
     * {@link #isApplyAntitheticVariates() Antithetic Variates}
     * and the {@link #isApplyBatchMeansMethod() Batch Means} techniques
     * are enabled, applying them over the given list of Doubles.
     * These techniques are used for variance reduction.
     *
     * @param values the List of values to add to the {@link SummaryStatistics} object
     * @return the {@link SummaryStatistics} object containing
     * the double values, after applying the techniques for variance reduction.
     */
    protected final SummaryStatistics computeFinalStatistics(final List<Double> values) {
        final var stats = new SummaryStatistics();
        final List<Double> adjustedValues = computeAntitheticMeans(computeBatchMeans(values));
        adjustedValues.forEach(stats::addValue);
        return stats;
    }

    /**
     * Add a value to a given metric inside the {@link #metricsMap}.
     *
     * <p>This method must be called for each metric inside the experiment finish listener.
     * The listener can be set inside the runner's {@link #createExperimentInternal(int)}.</p>
     * @see Experiment#setAfterExperimentFinish(Consumer)
     * @param metricName the name of the metric to collect the data for a simulation run
     * @param value the value for at metric for a given simulation run.
     *              If null is given, that means no value was collected for that metric
     *              in the run, but the metric entry must exist in the metrics map,
     *              so that the final results table show the metric entry with 0.
     */
    protected final void addMetricValue(final String metricName, final Double value){
        final List<Double> metricValues = getMetricValues(metricName);
        if(value != null)
            metricValues.add(value);
    }

    protected final List<Double> getMetricValues(final String metricName) {
        return metricsMap.compute(metricName, (key, values) -> values == null ? new ArrayList<>(simulationRuns) : values);
    }

    /**
     * <p>
     * Computes the antithetic means for the given samples if the
     * {@link #isApplyAntitheticVariates() "Antithetic Variates Technique" is to be applied}.
     *
     * These values are the mean between the first half of samples with the
     * second half. By this way, the resulting value is an array with half of
     * the samples length.
     * </p>
     *
     * <p>
     * <b>NOTE:</b> To correctly compute the antithetic values the seeds from
     * the first half of experiments must be used for the second half.</p>
     *
     * @param samples the list of samples to compute the antithetic means from
     * @return the computed antithetic means from the given samples if the
     * "Antithetic Variates Technique" is to be applied, otherwise return the
     * same given samples list.
     *
     * @see #createRandomGen(int, double, double)
     */
    protected List<Double> computeAntitheticMeans(final List<Double> samples) {
        if (!isApplyAntitheticVariates()) {
            return samples;
        }

        final int half = samples.size() / 2;
        final List<Double> antitheticMeans = new ArrayList<>(half);
        //applies the "Antithetic Variates Technique" to reduce variance
        for (int i = 0; i < half; i++) {
            antitheticMeans.add((samples.get(i) + samples.get(half + i)) / 2.0);
        }

        System.out.printf(
                "\tAntithetic Variates Technique applied. The number of samples was reduced to the half (%d).%n", half);

        return antitheticMeans;
    }

    protected abstract void printSimulationParameters();

    public void setFirstExperimentCreated(final int firstExperimentCreated) {
        if(this.firstExperimentCreated < 0) {
            this.firstExperimentCreated = firstExperimentCreated;
        }
    }

    public int getFirstExperimentCreated() {
        return firstExperimentCreated;
    }

    /**
     * Sets a description for this experiment which is shown when it starts.
     * It's also used to generate a caption for the Latex table.
     * @param description the description to set
     * @see #enableLatexTableResultsGeneration()
     */
    public ExperimentRunner setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    /**
     * An id used to identify the experiment results table generated in formats such as Latex
     * for computed metrics.
     * @param resultsTableId the name to set
     * @see #latexTableResultsGeneration
     */
    public ExperimentRunner setResultsTableId(final String resultsTableId) {
        this.resultsTableId = resultsTableId;
        return this;
    }

    public String getResultsTableId() {
        return resultsTableId;
    }

    /**
     * Enables the generation of a result table in Latex format for computed metrics.
     * @return
     */
    public ExperimentRunner enableLatexTableResultsGeneration(){
        this.latexTableResultsGeneration = true;
        return this;
    }

    /**
     * Checks if generation of a result table in Latex format for computed metrics is enabled.
     * @return
     */
    public boolean isLatexTableResultsGeneration() {
        return latexTableResultsGeneration;
    }

    /**
     * Checks if a progress bar is to be printed to show when each experiment run finishes.
     * It's just printed when the number of simulations is greater than 1
     * and experiments are not set as verbose. It's shown by default if those conditions are met.
     * @return
     */
    public boolean isShowProgress() {
        return showProgress;
    }

    /**
     * Enable or disables a progress bar to show when each experiment run finishes.
     * It's just printed when the number of simulations is greater than 1
     * and experiments are not set as verbose.
     * @param showProgress true to enable the progress bar, false to disable
     * @return
     */
    public ExperimentRunner<T> setShowProgress(final boolean showProgress) {
        this.showProgress = showProgress;
        return this;
    }

    public int getFinishedRuns() {
        return finishedRuns.get();
    }

    /**
     * Increments the number of finished runs and returns the updated value.
     * @return
     */
    final int incFinishedRuns() {
        return finishedRuns.incrementAndGet();
    }

    final void printProgress(final int current) {
        if(simulationRuns > 1 && showProgress) {
            Util.printProgress(current, simulationRuns, progressBarInNewLine);
        }
    }

    public ExperimentRunner<T> setProgressBarInNewLine(final boolean progressBarInNewLine) {
        this.progressBarInNewLine = progressBarInNewLine;
        return this;
    }

    /**
     * {@inheritDoc}
     * If {@link #isParallel() parallel} execution is enabled,
     * you may consider disabling verbosity for individual {@link Experiment}s created,
     * since messages from different runs will be mixed up
     * and may cause confusion.
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isVerbose() {
        return super.isVerbose();
    }

    /**
     * If experiments are executed in parallel, each experiment verbosity is disabled,
     * otherwise, you'll see mixed log messages from different
     * experiment runs.
     */
    public boolean isParallel() {
        return parallel;
    }
}
