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
package org.cloudsimplus.testbeds;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
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

import static java.util.stream.Collectors.joining;
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
public abstract class ExperimentRunner<T extends Experiment> extends AbstractExperiment {
    /**
     * The confidence level for computing confidence interval.
     */
    public static final double CONFIDENCE_LEVEL = 0.95;

    /**@see #isParallel() */
    private final boolean parallel;

    private boolean showProgress;
    private boolean progressBarInNewLine;

    private int firstExperimentCreated = -1;

    /**
     * @see #getBaseSeed()
     */
    private final long baseSeed;

    /** List of seeds used for each experiment.
     * @see #addSeed(long)  */
    private final List<Long> seeds;

    /**
     * @see #getSimulationRuns()
     */
    private int simulationRuns;

    private AtomicInteger finishedRuns;

    /**
     * @see #getExperimentsStartTimeSecs()
     */
    private long experimentsStartTimeSecs;

    /**
     * @see #getExperimentsExecutionTimeSecs()
     */
    private long experimentsExecutionTimeSecs;

    /**
     * @see #isApplyAntitheticVariatesTechnique()
     */
    private final boolean applyAntitheticVariatesTechnique;

    /**
     * @see #getBatchesNumber()
     */
    private int batchesNumber;

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

    /**@see #setDescription(String) */
    private String description;

    /**@see #setResultsTableId(String) */
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
     * @param antitheticVariatesTechnique indicates if it's to be applied the
     *                                    <a href="https://en.wikipedia.org/wiki/Antithetic_variates">antithetic variates technique</a>.
     */
    protected ExperimentRunner(final long baseSeed, final int simulationRuns, final boolean antitheticVariatesTechnique) {
        this(baseSeed, simulationRuns, 0, antitheticVariatesTechnique, false, false);
    }

    /**
     * Creates an experiment runner with a given {@link #getBaseSeed() base seed}
     * that runs sequentially.
     * @param baseSeed the seed to be used as base for each experiment seed
     * @param simulationRuns the number of times the experiment will be executed
     * @param batchesNumber number of simulation run batches (zero disables the batch means method)
     * @param antitheticVariatesTechnique indicates if it's to be applied the
     *                                    <a href="https://en.wikipedia.org/wiki/Antithetic_variates">antithetic variates technique</a>.
     */
    protected ExperimentRunner(final long baseSeed, final int simulationRuns, final int batchesNumber, final boolean antitheticVariatesTechnique) {
        this(baseSeed, simulationRuns, batchesNumber, antitheticVariatesTechnique, false, false);
    }

    /**
     * Creates an experiment runner with a given {@link #getBaseSeed() base seed}.
     * @param baseSeed the seed to be used as base for each experiment seed
     * @param simulationRuns the number of times the experiment will be executed
     * @param batchesNumber number of simulation run batches (zero disables the batch means method)
     * @param antitheticVariatesTechnique indicates if it's to be applied the
     *                                    <a href="https://en.wikipedia.org/wiki/Antithetic_variates">antithetic variates technique</a>.
     * @param parallel whether experiments will run in parallel or sequentially. It's just actually enabled when the simulation runs
     *                 is larger than 1.
     * @param latexTableResultsGeneration Enables/disables the generation of a result table in Latex format for computed metrics.
     */
    protected ExperimentRunner(
        final long baseSeed, final int simulationRuns, final int batchesNumber,
        final boolean antitheticVariatesTechnique,
        final boolean parallel, final boolean latexTableResultsGeneration)
    {
        this.baseSeed = baseSeed;
        this.applyAntitheticVariatesTechnique = antitheticVariatesTechnique;
        if(simulationRuns <= 0)
            throw new IllegalArgumentException("Simulation runs must be greater than 0.");
        this.simulationRuns = simulationRuns;
        this.finishedRuns = new AtomicInteger();
        this.parallel = parallel && simulationRuns > 1;
        this.showProgress = true;

        if(batchesNumber < 0 || batchesNumber == 1) {
            throw new IllegalArgumentException("Batches number must be greater than 1. Use 0 just to disable the Batch Means method.");
        }
        this.batchesNumber = batchesNumber;
        this.latexTableResultsGeneration = latexTableResultsGeneration;

        /*Since experiments may run in parallel and these fields are shared across them,
        * we need to synchronize these collections.*/
        this.seeds = parallel ? Collections.synchronizedList(new ArrayList<>()) : new ArrayList<>();
        this.metricsMap = parallel ? Collections.synchronizedMap(new TreeMap<>()) : new TreeMap<>();

        if (isApplyBatchMeansMethod() || isApplyAntitheticVariatesTechnique()) {
            setSimulationRunsAndBatchesToEvenNumber();
        }

        if (isApplyBatchMeansAndSimulationRunsIsNotMultipleOfBatches()) {
            setNumberOfSimulationRunsAsMultipleOfNumberOfBatches();
        }
    }

    /**
     *
     * Sets the number of simulation runs and batches to a even number. The
     * "Antithetic Variates Technique" for variance reduction requires an even
     * number of simulation runs. Accordingly, if the "Batch Means Method" is
     * used simultaneously, the number of batches has to be even.
     */
    private void setSimulationRunsAndBatchesToEvenNumber() {
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
        simulationRuns = batchSizeCeil() * getBatchesNumber();
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
        final boolean numSimulationRunsGraterThanBatches = simulationRuns > batchesNumber;
        return batchesGreaterThan1 && numSimulationRunsGraterThanBatches;
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

        final List<Double> batchMeans = new ArrayList<>(getBatchesNumber());
        for (int i = 0; i < getBatchesNumber(); i++) {
            batchMeans.add(getBatchAverage(samples, i));
        }

        System.out.printf(
                "\tBatch Means Method applied. The number of samples was reduced to %d after computing the mean for each batch.%n", getBatchesNumber());

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
     * @param i the index of the batch to get it's values average
     * @return the average for the values of a given batch
     */
    private double getBatchAverage(final List<Double> samples, final int i) {
        final int k = batchSizeCeil();
        return IntStream.range(0, k).mapToDouble(j -> samples.get(getBatchElementIndex(i, j))).average().orElse(0.0);
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
    public boolean isApplyAntitheticVariatesTechnique() {
        return applyAntitheticVariatesTechnique;
    }

    /**
     * Gets the number of times the experiment will be executed in order to get
     * values such as means and standard deviations. It has to be an even number
     * if the
     * {@link #isApplyAntitheticVariatesTechnique() "Antithetic Variates Technique"}
     * is to be used.
     * @return
     */
    public int getSimulationRuns() {
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
         simulationRuns = getBatchesNumber() * (int)Math.ceil(getSimulationRuns() / getBatchesNumber());
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

    long getSeed(final int experimentIndex) {
        return seeds.get(experimentIndex);
    }

    /**
     * Uses the provided {@link Function} to create a pseudo random number generator (PRNG) for a experiment run.
     * The kind and parameters for this PRNG is defined internally by the given Function.
     * This method calls that Function just providing the seed to be used for the current experiment run.
     *
     * If it is to apply the
     * {@link #isApplyAntitheticVariatesTechnique() "Antithetic Variates Technique"}
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
    public <T extends StatisticalDistribution> T createRandomGen(final int experimentIndex, final Function<Long, T> randomGenCreator) {
        Objects.requireNonNull(randomGenCreator, "The Function to instantiate the Random Number Generator cannot be null.");

        if(seeds.isEmpty()){
            throw new IllegalStateException(
                "You have to create at least 1 SimulationExperiment before requesting a ExperimentRunner to create a pseudo random number generator (PRNG)!");
        }

        if (isToReuseSeedFromFirstHalfOfExperiments(experimentIndex)) {
            final int expIndexFromFirstHalf = experimentIndex - halfSimulationRuns();
            final T prng = randomGenCreator.apply(getSeed(expIndexFromFirstHalf));
            prng.setApplyAntitheticVariates(true);
            return prng;
        }

        return randomGenCreator.apply(getSeed(experimentIndex));
    }

    /**
     * Creates a pseudo random number generator (PRNG) for a experiment run that
     * generates uniform values between [0 and 1[. If it is to apply the
     * {@link #isApplyAntitheticVariatesTechnique() "Antithetic Variates Technique"}
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
     * {@link #isApplyAntitheticVariatesTechnique() "Antithetic Variates Technique"}
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
        return isApplyAntitheticVariatesTechnique() &&
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
        if(description != null && !description.trim().isEmpty()){
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
        final Map<String, SummaryStatistics> statsMap = new TreeMap<>();
        metricsMap.entrySet().forEach(e -> statsMap.put(e.getKey(), computeAndPrintFinalResults(e)));
        buildLatexMetricsResultTable(statsMap);
    }

    /**
     * Generates the latex table for metrics results.
     * @param statsMap a Map where each key is a metric name and it value is
     *                 a statistics object summarizing the metric results for all
     *                 simulation runs.
     */
    private void buildLatexMetricsResultTable(final Map<String, SummaryStatistics> statsMap) {
        if(!latexTableResultsGeneration) {
            return;
        }

        if (simulationRuns == 1) {
            System.out.println("Latex table with metrics' results is just built when the number of simulation runs is greater than 1.");
            return;
        }

        final StringBuilder latex = startLatexTable();
        statsMap.forEach((metric, stats) -> latexRow(latex, metric, stats));
        latex.append("  \\end{tabular}\n").append("\\end{table}\n");
        System.out.println();
        System.out.println(latex);
    }

    /**
     * Creates a row for the latex table containing the result metrics
     * @param latex the StringBuilder where the latex table is being built
     * @param metricName the name of the current metric to be presented as a table row
     * @param stats summary statistics for this metric
     */
    private void latexRow(final StringBuilder latex, final String metricName, final SummaryStatistics stats) {
        final String errorMargin = String.format("%.4f", confidenceErrorMargin(stats));
        //If there is a % in the metric name, that needs to be escaped to show on Latex, since % starts a Latex comment

        final String escapedMetricName = StringUtils.replace(metricName,"%", "\\%");
        latex.append(escapedMetricName)
             .append(" & ")
             .append(String.format("%.2f", stats.getMean()))
             .append(" $\\pm$ & ")
             .append(errorMargin)
             .append(" & ")
             .append(String.format("%.4f", stats.getStandardDeviation()))
             .append("\\\\ \\hline\n");
    }

    private StringBuilder startLatexTable() {
        final StringBuilder latex = new StringBuilder();
        latex.append("\\begin{table}[!hbt]\n")
             .append(String.format("  \\caption{%s}\n", description))
             .append(String.format("  \\label{%s}\n", resultsTableId))
             .append("  \\begin{tabular}{|p{3.2cm}|rr|>{\\raggedleft\\arraybackslash}p{1.3cm}|}\n")
             .append("      \\hline\n")
             .append("      \\textbf{Metric} & \\multicolumn{2}{c|}{\\textbf{95\\% Confidence Interval}} & \\textbf{*Standard Deviation} \\\\ \\hline\n");
        return latex;
    }

    private Stream<Experiment> getStream(final List<Experiment> experiments) {
        return parallel ? experiments.stream().parallel() : experiments.stream();
    }

    /**
     * Template method that creates an experiment to be run for the i'th time.
     *
     * @param i a number that identifies the experiment
     * @return the created experiment
     * @see #createExperimentInternal(int)
     */
    private Experiment createExperiment(final int i) {
        print(((i + 1) % 100 == 0 ? String.format(". Run #%d%n", i + 1) : "."));
        setFirstExperimentCreated(i);
        return createExperimentInternal(i);
    }

    /**
     * Creates an experiment to be run for the i'th time.
     *
     * @param i a number that identifies the experiment
     * @return the created experiment
     * @see #createExperiment(int)
     */
    protected abstract T createExperimentInternal(final int i);

    /**
     * Computes and prints final simulation results, including mean, standard deviations and
     * confidence intervals for a given metric computed across all simulation runs.
     *
     * @param metricEntry a map entry represented by the name of the metric and
     *                    its list of values across multiple simulation runs
     * @return the computed {@link SummaryStatistics} from the provided values for the metric
     */
    protected SummaryStatistics computeAndPrintFinalResults(final Map.Entry<String, List<Double>> metricEntry){
        final List<Double> metricValues = metricEntry.getValue();
        final SummaryStatistics stats = computeFinalStatistics(metricValues);
        final String valuesStr = metricValues.stream().map(v -> String.format("%.2f", v)).collect(joining(", "));
        final String sampleWord = metricValues.size() > 1 ? "samples" : "sample";
        System.out.printf(
            "# %s: %.2f (%d %s: %s)%n",
            metricEntry.getKey(), stats.getMean(),
            metricValues.size(), sampleWord, valuesStr);

        if (simulationRuns > 1) {
            showConfidenceInterval(stats);
        }

        return stats;
    }

    /**
     * Creates a SummaryStatistics object from a list of
     * Double values, allowing computation of statistics
     * such as mean over these values.
     * The method also checks if the
     * {@link #isApplyAntitheticVariatesTechnique() Antithetic Variates}
     * and the {@link #isApplyBatchMeansMethod() Batch Means} techniques
     * are enabled and then apply them over the given list of Doubles.
     * These techniques are used for variance reduction.
     *
     * @param values the List of values to add to the {@link SummaryStatistics} object
     * @return the {@link SummaryStatistics} object containing
     * the double values, after applying the the techniques for
     * variance reduction.
     */
    protected final SummaryStatistics computeFinalStatistics(final List<Double> values) {
        final SummaryStatistics stats = new SummaryStatistics();
        final List<Double> adjustedValues = computeAntitheticMeans(computeBatchMeans(values));
        adjustedValues.forEach(stats::addValue);
        return stats;
    }

    /**
     * Shows confidence interval for the average value of a given metric for all executed simulations.
     * @param stats a {@link SummaryStatistics} computed from the list of values for a metric across all simulation runs
     * @see #computeFinalStatistics(List)
     */
    private void showConfidenceInterval(final SummaryStatistics stats) {
        // Computes 95% confidence interval
        final double intervalSize = confidenceErrorMargin(stats);
        final double criticalValue = getConfidenceIntervalCriticalValue(stats.getN());
        System.out.printf("\tt-Distribution critical value for %d samples: %f%n", stats.getN(), criticalValue);

        final double lower = stats.getMean() - intervalSize;
        final double upper = stats.getMean() + intervalSize;
        System.out.printf(
            "\t95%% Confidence Interval: %.6f ∓ %.4f, that is [%.4f to %.4f]%n",
            stats.getMean(), intervalSize, lower, upper);
        System.out.printf("\tStandard Deviation: %.4f%n", stats.getStandardDeviation());
    }

    /**
     * <p>
     * Computes the confidence interval error margin for a given set of samples
     * in order to enable finding the interval lower and upper bound around a
     * mean value. By this way, the confidence interval can be computed as [mean
     * + errorMargin .. mean - errorMargin].
     * </p>
     *
     * <p>
     * To reduce the confidence interval by half, one have to execute the
     * experiments 4 more times. This is called the "Replication Method" and
     * just works when the samples are i.i.d. (independent and identically
     * distributed). Thus, if you have correlation between samples of each
     * simulation run, a different method such as a bias compensation,
     * {@link #isApplyBatchMeansMethod() batch means} or regenerative method has
     * to be used. </p>
     *
     * <b>NOTE:</b> How to compute the error margin is a little bit confusing.
     * The Harry Perros' book states that if less than 30 samples are collected,
     * the t-Distribution has to be used to that purpose.
     *
     * However, this article
     * <a href="https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps">Wikipedia
     * article</a>
     * says that if the standard deviation of the real population is known, it
     * has to be used the z-value from the Standard Normal Distribution.
     * Otherwise, it has to be used the t-value from the t-Distribution to
     * calculate the critical value for defining the error margin (also called
     * standard error). The book "Numeric Computation and Statistical Data
     * Analysis on the Java Platform" confirms the last statement and such
     * approach was followed.
     *
     * @param stats the statistic object with the values to compute the error
     * margin of the confidence interval
     * @return the error margin to compute the lower and upper bound of the
     * confidence interval
     *
     * @see
     * <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda3672.htm">Critical
     * Values of the Student's t Distribution</a>
     * @see
     * <a href="https://en.wikipedia.org/wiki/Student%27s_t-distribution">t-Distribution</a>
     * @see <a href="http://www4.ncsu.edu/~hp/files/simulation.pdf">Harry
     * Perros, "Computer Simulation Techniques: The definitive introduction!,"
     * 2009</a>
     * @see <a href="http://www.springer.com/gp/book/9783319285290">Numeric
     * Computation and Statistical Data Analysis on the Java Platform</a>
     */
    protected double confidenceErrorMargin(final SummaryStatistics stats) {
        try {
            final long samples = stats.getN();
            final double criticalValue = getConfidenceIntervalCriticalValue(samples);
            return criticalValue * stats.getStandardDeviation() / Math.sqrt(samples);
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }

    /**
     * Computes the confidence interval critical value for a given number of samples and conficende level.
     * @param samples number of collected samples
     * @return
     */
    private double getConfidenceIntervalCriticalValue(final long samples) {
        /* Creates a T-Distribution with N-1 degrees of freedom
        * since were are computing the sample's confidence interval
        * instead of the entire population. */
        final double freedomDegrees = samples - 1;

        /* The t-Distribution is used to determine the probability that
        the real population mean lies in a given interval. */
        final TDistribution tDist = new TDistribution(freedomDegrees);
        final double significance = 1.0 - CONFIDENCE_LEVEL;
        final double criticalValue = tDist.inverseCumulativeProbability(1.0 - significance / 2.0);
        return criticalValue;
    }

    /**
     * Add a value to a given metric inside the {@link #metricsMap}.
     *
     * <p>This method must be called for each metric inside the experiment finish listener.
     * The listener can be set inside the runner's {@link #createExperimentInternal(int)}.</p>
     * @see Experiment#setAfterExperimentFinish(Consumer)
     */
    protected final void addMetricValue(final String metricName, final double value){
        final List<Double> metricValues = getMetricValues(metricName);
        metricValues.add(value);
    }

    protected final List<Double> getMetricValues(final String metricName) {
        return metricsMap.compute(metricName, (key, values) -> values == null ? new ArrayList<>(simulationRuns) : values);
    }

    /**
     * <p>
     * Computes the antithetic means for the given samples if the
     * {@link #isApplyAntitheticVariatesTechnique() "Antithetic Variates Technique" is to be applied}.
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
        if (!isApplyAntitheticVariatesTechnique()) {
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

    /**
     * Enables the generation of a result table in Latex format for computed metrics.
     * @return
     */
    public ExperimentRunner enableLatexTableResultsGeneration(){
        this.latexTableResultsGeneration = true;
        return this;
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
