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

import ch.qos.logback.classic.Level;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * A base class to run a given experiment a defined number of times and collect
 * statistics about the execution. The runner represents a testbed compounded of
 * a set of experiments that it runs.
 *
 * @param <T> the type of {@link Experiment} the runner will execute
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public abstract class ExperimentRunner<T extends Experiment> implements Runnable {
    private boolean verbose;

    /**
     * @see #getBaseSeed()
     */
    private long baseSeed;

    private List<Long> seeds;

    /**
     * @see #getSimulationRuns()
     */
    private int simulationRuns;

    /**
     * @see #getExperimentsStartTime()
     */
    private long experimentsStartTime;

    /**
     * @see #getExperimentsFinishTime()
     */
    private long experimentsFinishTime;

    /**
     * @see #isApplyAntitheticVariatesTechnique()
     */
    private boolean applyAntitheticVariatesTechnique;

    /**
     * @see #getNumberOfBatches()
     */
    private int numberOfBatches;

    /**
     * Creates an experiment runner, setting the
     * {@link #getBaseSeed() base seed} as the current time.
     * @param antitheticVariatesTechnique indicates if it's to be applied the
     *                                    <a href="https://en.wikipedia.org/wiki/Antithetic_variates">antithetic variates technique</a>.
     */
    public ExperimentRunner(final boolean antitheticVariatesTechnique) {
        this(antitheticVariatesTechnique, System.currentTimeMillis());
    }

    /**
     * Creates an experiment runner with a given {@link #getBaseSeed() base seed}.
     * @param antitheticVariatesTechnique indicates if it's to be applied the
     *                                    <a href="https://en.wikipedia.org/wiki/Antithetic_variates">antithetic variates technique</a>.
     * @param baseSeed the seed to be used as base for each experiment seed
     */
    public ExperimentRunner(final boolean antitheticVariatesTechnique, final long baseSeed) {
        seeds = new ArrayList<>();
        setBaseSeed(baseSeed);
        setNumberOfBatches(0);
        setApplyAntitheticVariatesTechnique(antitheticVariatesTechnique);
    }

    /**
     * <p>
     * Setup experiment attributes considering the dependency between each
     * other. The method is called by the {@link #run()} method, just after all
     * the attributes were set. By this way, it initializes internal attributes
     * and validates other ones.</p>
     *
     * <p>
     * <b>NOTE:</b> As a good practice, it is tried to reduce the number of
     * parameters for the class constructor, as it tends to increase as the
     * experiment code evolves. Accordingly, all the parameters have to be
     * defined using the corresponding setters. By this way,
     * <b>it has to be avoided setting up attributes inside the constructor,
     * once they can become invalid or out-of-date because dependency between
     * parameters.</b>
     * The constructor has just to initialize objects to avoid
     * {@link NullPointerException}. This way, one have to set all the
     * parameters inside this method. For instance, if the constructor creates
     * and Random Number Generator (PRNG) using a default seed but the method
     * setSeed is called after the constructor, the PRNG will not be update to
     * use the new seed.</p>
     */
    protected abstract void setup();

    /**
     * An internal setup method that performs base setup for every experiment
     * runner and call the additional {@link #setup()} method that has to be
     * implemented by child classes.
     */
    private void setupInternal() {
        if (isApplyBatchMeansMethod() || isApplyAntitheticVariatesTechnique()) {
            setSimulationRunsAndBatchesToEvenNumber();
        }

        if (isApplyBatchMeansAndSimulationRunsIsNotMultipleOfBatches()) {
            setNumberOfSimulationRunsAsMultipleOfNumberOfBatches();
        }

        setup();
        seeds = new ArrayList<>(getSimulationRuns());
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
            setSimulationRuns(getSimulationRuns() + 1);
        }

        if (getSimulationRuns() % getNumberOfBatches()  != 0) {
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
        setSimulationRuns(batchSizeCeil() * getNumberOfBatches());
    }

    /**
     * Checks if it is to use the "Batch Means Method" and the number of
     * simulation runs is not multiple of the number of batches.
     */
    private boolean isApplyBatchMeansAndSimulationRunsIsNotMultipleOfBatches() {
        return isApplyBatchMeansMethod() && getSimulationRuns() % getNumberOfBatches() != 0;
    }

    /**
     * @return the batch size rounded by the {@link Math#ceil(double)} method.
     */
    public int batchSizeCeil() {
        return (int) Math.ceil(getSimulationRuns() / (double) getNumberOfBatches());
    }

    /**
     * Checks if the number of simulation runs and the number of batches are
     * compatible
     *
     * @return
     */
    public boolean simulationRunsAndNumberOfBatchesAreCompatible() {
        final boolean batchesGreaterThan1 = getNumberOfBatches() > 1;
        final boolean numSimulationRunsGraterThanBatches = getSimulationRuns() > getNumberOfBatches();
        return batchesGreaterThan1 && numSimulationRunsGraterThanBatches;
    }

    /**
     *
     * Checks if the "Batch Means Method" is to be applied to reduce correlation
     * between the results for different experiment runs.
     * @return
     */
    public boolean isApplyBatchMeansMethod() {
        return simulationRunsAndNumberOfBatchesAreCompatible();
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
     * number of batches defined by {@link #getNumberOfBatches()} (each value in
     * the returned array will be the mean of every sample batch). Otherwise,
     * returns the same given array
     */
    protected List<Double> computeBatchMeans(final List<Double> samples) {
        if (!isApplyBatchMeansMethod()) {
            return samples;
        }

        final List<Double> batchMeans = new ArrayList<>(getNumberOfBatches());
        for (int i = 0; i < getNumberOfBatches(); i++) {
            batchMeans.add(getBatchAverage(samples, i));
        }

        System.out.printf(
                "\tBatch Means Method applied. The number of samples was reduced to %d after computing the mean for each batch.%n", getNumberOfBatches());

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
     * @param confidenceLevel the confidence level, in the interval from ]0 to
     * 1[, such as 0.95 to indicate 95% of confidence.
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
    protected double computeConfidenceErrorMargin(final SummaryStatistics stats, final double confidenceLevel) {
        try {
            // Creates a T-Distribution with N-1 degrees of freedom
            final double degreesOfFreedom = stats.getN() - 1;

            /*
		    The t-Distribution is used to determine the probability that
		    the real population mean lies in a given interval.
             */
            final TDistribution tDist = new TDistribution(degreesOfFreedom);
            final double significance = 1.0 - confidenceLevel;
            final double criticalValue = tDist.inverseCumulativeProbability(1.0 - significance / 2.0);
            System.out.printf("%n\tt-Distribution critical value for %d samples: %f%n", stats.getN(), criticalValue);

            // Calculates the confidence interval error margin
            return criticalValue * stats.getStandardDeviation() / Math.sqrt(stats.getN());
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
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

    protected ExperimentRunner setSimulationRuns(final int simulationRuns) {
        this.simulationRuns = simulationRuns;
        return this;
    }

    /**
     * Adjusts the current number of simulations to be equal to its closer
     * multiple of the number of batches.
     * @return
     */
    protected ExperimentRunner setSimulationRunsAsMultipleOfBatchNumber() {
         setSimulationRuns(getNumberOfBatches() * (int)Math.ceil(getSimulationRuns() / getNumberOfBatches()));
         return this;
    }

    private ExperimentRunner setApplyAntitheticVariatesTechnique(final boolean applyAntitheticVariatesTechnique) {
        this.applyAntitheticVariatesTechnique = applyAntitheticVariatesTechnique;
        return this;
    }

    /**
     * Gets the number of batches in which the simulation runs will be divided.
     *
     * If this number is greater than 1, the "Batch Means Method" is used to
     * reduce the correlation between experiment runs.
     * @return
     */
    public int getNumberOfBatches() {
        return numberOfBatches;
    }

    /**
     * Sets the number of batches in which the simulation runs will be divided.
     *
     * @param numberOfBatches number of simulation run batches
     * @return
     * @see #getNumberOfBatches()
     */
    public final ExperimentRunner setNumberOfBatches(final int numberOfBatches) {
        this.numberOfBatches = numberOfBatches;
        return this;
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
     * @return the created PRNG
     *
     * @see UniformDistr#isApplyAntitheticVariates()
     * @see #createRandomGen(int, double, double)
     */
    public <T extends ContinuousDistribution> T createRandomGen(final int experimentIndex, final Function<Long, T> randomGenCreator) {
        Objects.requireNonNull(randomGenCreator, "The Function to instantiate the Random Number Generator cannot be null.");

        if(seeds.isEmpty()){
            throw new IllegalStateException(
                "You have to create at least 1 SimulationExperiment before requesting a ExperimentRunner to create a pseudo random number generator (PRNG)!");
        }

        if (isToReuseSeedFromFirstHalfOfExperiments(experimentIndex)) {
            final int expIndexFromFirstHalf = experimentIndex - halfSimulationRuns();
            final T prng = randomGenCreator.apply(seeds.get(expIndexFromFirstHalf));
            prng.setApplyAntitheticVariates(true);
            return prng;
        }

        return randomGenCreator.apply(seeds.get(experimentIndex));
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
     * Time in seconds the experiments finished.
     * @return
     */
    public long getExperimentsFinishTime() {
        return experimentsFinishTime;
    }

    /**
     * Time in seconds the experiments started.
     * @return
     */
    public long getExperimentsStartTime() {
        return experimentsStartTime;
    }

    /**
     * Setups and starts the execution of all experiments.
     */
    @Override
    public void run() {
        setupInternal();

        printSimulationParameters();

        Log.setLevel(Level.OFF);
        try {
            experimentsStartTime = System.currentTimeMillis();
            for (int i = 0; i < getSimulationRuns(); i++) {
                if (isVerbose()) {
                    System.out.print(((i + 1) % 100 == 0 ? String.format(". Run #%d%n", i + 1) : "."));
                }
                createExperiment(i).run();
            }
            System.out.println();
            experimentsFinishTime = (System.currentTimeMillis() - experimentsStartTime) / 1000;
        } finally {
            Log.setLevel(Level.INFO);
        }

        final Map<String, List<Double>> metricsMap = createMetricsMap();
        System.out.printf("%n------------------------------------------------------------------%n");
        metricsMap.entrySet().forEach(this::computeAndPrintFinalResults);
        System.out.printf("%nExperiments finished in %d seconds!%n", getExperimentsFinishTime());
    }

    /**
     * Creates a Map adding a List of values for each metric to be computed.
     * The computation of final experiments results are performed on this map.
     *
     * <p>Each key is the name of metric and each value is a List of Double
     * containing the values collected for that metric, for each experiment run.
     * These values will be then summarized to compute the final value
     * for each metric.</p>
     * @return the populated metricsMap
     */
    protected abstract Map<String, List<Double>> createMetricsMap();

    /**
     * Creates an experiment to be run for the i'th time.
     *
     * @param i a number that identifies the experiment
     * @return the created experiment
     */
    protected abstract T createExperiment(final int i);

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
    protected SummaryStatistics computeFinalStatistics(List<Double> values) {
        final SummaryStatistics stats = new SummaryStatistics();
        values = computeBatchMeans(values);
        values = computeAntitheticMeans(values);
        values.forEach(stats::addValue);
        return stats;
    }

    /**
     * Computes and prints final simulation results such as means, standard deviations and
     * confidence intervals.
     *
     * @param metricEntry a Map Entry where the key is the name of the metric to print results
     * and the value is a {@link SummaryStatistics} object containing means of each
     * experiment run that will be used to computed an overall mean and other
     * statistics
     * @see #printFinalResults(java.lang.String, org.apache.commons.math3.stat.descriptive.SummaryStatistics)
     */
    private void computeAndPrintFinalResults(final Map.Entry<String, List<Double>> metricEntry){
        printFinalResults(metricEntry.getKey(), computeFinalStatistics(metricEntry.getValue()));
    }

    /**
     * Prints final simulation results such as means, standard deviations and
     * confidence intervals.
     *
     * @param metricName the name of the metric to be printed
     * @param stats the {@link SummaryStatistics} containing means of each
     * experiment run that will be used to computed an overall mean and other
     * statistics
     */
    protected abstract void printFinalResults(String metricName, SummaryStatistics stats);

    public final ExperimentRunner setBaseSeed(final long baseSeed) {
        this.baseSeed = baseSeed;
        return this;
    }

    /**
     * Indicates if the runner will output execution logs or not. This doesn't
     * affect the verbosity of individual experiments executed. Each
     * {@link Experiment} has its own verbose attribute.
     * @return
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Defines if the runner will output execution logs or not. This doesn't
     * affect the verbosity of individual experiments executed. Each
     * {@link Experiment} has its own verbose attribute.
     *
     * @param verbose true if results have to be output, false otherwise
     * @return
     */
    public ExperimentRunner setVerbose(final boolean verbose) {
        this.verbose = verbose;
        return this;
    }
}
