package org.cloudsimplus.testbeds;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.distributions.UniformDistr;

/**
 * A base class to run a given experiment a defined number of times
 * and collect statistics about the execution.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class ExperimentRunnerAbstract {
	/**
	 * @see #getBaseSeed()
	 */
	private long baseSeed;

	/**
	 * @see #getSeeds()
	 */
	private long[] seeds;
	/**
	 * @see #getNumberOfSimulationRuns()
	 */
	private int numberOfSimulationRuns;

	/**
	 * @see #getExperimentsStartTime()
	 */
	private long experimentsStartTime = 0;

	/**
	 * @see #getExperimentsFinishTime()
	 */
	private long experimentsFinishTime = 0;

	/**
	 * @see #isApplyAntitheticVariatesTechnique()
	 */
	private boolean applyAntitheticVariatesTechnique;

	/**
	 * @see #getNumberOfBatches()
	 */
	private int numberOfBatches;

	/**
	 * Creates an experiment runner, setting the {@link #getBaseSeed() base seed}
	 * as the current time.
	 */
	public ExperimentRunnerAbstract() {
		seeds = new long[0];
		baseSeed = System.currentTimeMillis();
		setNumberOfBatches(0);
	}

	/**
	 * Setup experiment attributes considering
	 * the dependency between each other.
	 * The method is called by the {@link #start()} method,
	 * just after all the attributes were set.
	 * By this way, it initialize internal attributes
	 * and validate other ones.
	 */
	protected abstract void setup();

	/**
	 * An internal setup method that performs base setup for every
	 * experiment runner and call the additional {@link #setup()} method
	 * that has to be implemented by child classes.
	 */
	private void setupInternal(){
		if(isApplyBatchMeansMethod()){
			setSimulationRunsAndBatchesToEvenNumber();
		}

		if(isApplyBatchMeansAndSimulationRunsIsNotMultipleOfBatches()){
			setNumberOfSimulationRunsAsMultipleOfNumberOfBatches();
		}

		setup();
		seeds = new long[getNumberOfSimulationRuns()];
	}

	/**
	 *
	 * Sets the number of simulation runs and batches to
	 * a even number. The "Antithetic Variates Technique" for variance reduction
	 * requires an even number of simulation runs.
	 * Accordingly, if the "Batch Means Method" is used
	 * simultaneously, the number of batches has
	 * to be even.
	 */
	private void setSimulationRunsAndBatchesToEvenNumber() {
		if(getNumberOfSimulationRuns()%2!=0)
			setNumberOfSimulationRuns(getNumberOfSimulationRuns()+1);

		if(getNumberOfBatches()%2!=0)
			setNumberOfBatches(getNumberOfBatches()+1);
	}

	/**
	 * 	Adjusts the number of simulation runs to be multiple
	 * 	of the number of batches, once each batch has to have the same size.
	 * 	If applyAntitheticVariatesTechnique is true, the number of batches will
	 * 	be even and consequently, the number of simulation runs after being adjusted will be even too.
	 */
	private void setNumberOfSimulationRunsAsMultipleOfNumberOfBatches() {
		setNumberOfSimulationRuns(batchSizeCeil() * getNumberOfBatches());
	}

	/**
	 * 	Checks if it is to use the  "Batch Means Method" and the number of
	 * 	simulation runs is not multiple of the number of batches.
	 */
	private boolean isApplyBatchMeansAndSimulationRunsIsNotMultipleOfBatches() {
		return isApplyBatchMeansMethod() && getNumberOfSimulationRuns() % getNumberOfBatches() != 0;
	}

	/**
	 * @return the batch size rounded by the {@link Math#ceil(double)} method.
	 */
	public int batchSizeCeil() {
		return (int)Math.ceil(getNumberOfSimulationRuns() / (double) getNumberOfBatches());
	}

	/**
	 *
	 * Checks if the "Batch Means Method" is to be applied to reduce
	 * correlation between the results for different experiment runs.
	 */
	public boolean isApplyBatchMeansMethod(){
		return getNumberOfBatches() > 1;
	}

	/**
	 * Gets an array of samples and apply the "Batch Means Method"
	 * to reduce samples correlation, if the "Batch Means Method"
	 * {@link #isApplyBatchMeansMethod() is to be applied}.
	 *
	 * @param samples the array with samples to apply the "Batch Means Method"
	 * @return the samples array after applying the "Batch Means Method",
	 * in case the method is enabled to be applied, that will reduce the
	 * array to the number of batches defined by {@link #getNumberOfBatches()}
	 * (each value in the returned array will be the mean of every sample batch).
	 * Otherwise, return the same given array
	 */
	protected double[] computeBatchMeans(double samples[]) {
		if(!isApplyBatchMeansMethod())
			return samples;

		double batchMeans[] = new double[getNumberOfBatches()];
		int k = batchSizeCeil();
		for(int i = 0; i < getNumberOfBatches(); i++){
			batchMeans[i] = 0;
			for(int j = 0; j < k; j++){
				batchMeans[i] += samples[i*k + j];
			}
			batchMeans[i] /= k;
		}

		System.out.printf(
			"\tBatch Means Method applied. The number of samples was reduced to %d after computing the mean for each batch.\n", getNumberOfBatches());

		return batchMeans;
	}

	/**
	 * <p>Computes the confidence interval error margin for a given
	 * set of samples in order to enable finding
	 * the interval lower and upper bound around a mean value.
	 * By this way, the confidence interval can be computed
	 * as [mean + errorMargin .. mean - errorMargin].
	 * </p>
	 *
	 * <p>To reduce the confidence interval by half, one have to execute
	 * the experiments 4 more times. This is called the "Replication Method" and just
	 * works when the samples are i.i.d. (independent and identically distributed).
	 * Thus, if you have correlation between samples of each simulation run, a different
	 * method such as a bias compensation, {@link #isApplyBatchMeansMethod() batch means}
	 * or regenerative method has to be used. </p>
	 *
	 * <b>NOTE:</b> How to compute the error margin is a little bit confusing.
	 * The Harry Perros' book states that if less than 30 samples are collected,
	 * the t-Distribution has to be used to that purpose.
	 *
	 * However, this article <a href="https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps">Wikipedia article</a>
	 * says that if the standard deviation of the real population is known,
	 * it has to be used the z-value from the Standard Normal Distribution.
	 * Otherwise, it has to be used the t-value from the t-Distribution
	 * to calculate the critical value for defining the error margin (also called standard error).
	 * The book "Numeric Computation and Statistical Data Analysis on the Java Platform" confirms
	 * the last statement and such approach was followed.
	 *
	 * @param stats the statistic object with the values to compute the error margin of the confidence interval
	 * @param confidenceLevel the confidence level, in the interval from ]0 to 1[,
	 * such as 0.95 to indicate 95% of confidence.
	 * @return the error margin to compute the lower and upper bound of the confidence interval
	 *
	 * @see <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda3672.htm">Critical Values of the Student's t Distribution</a>
	 * @see <a href="https://en.wikipedia.org/wiki/Student%27s_t-distribution">t-Distribution</a>
	 * @see <a href="http://www4.ncsu.edu/~hp/files/simulation.pdf">Harry Perros, "Computer Simulation Techniques: The definitive introduction!," 2009</a>
	 * @see <a href="http://www.springer.com/gp/book/9783319285290">Numeric Computation and Statistical Data Analysis on the Java Platform</a>
	 */
	protected double computeConfidenceErrorMargin(SummaryStatistics stats, double confidenceLevel) {
	    try {
	        // Creates a T-Distribution with N-1 degrees of freedom
		    final double degreesOfFreedom = stats.getN() - 1;

		    /*
		    The t-Distribution is used to determine the probability that
		    the real population mean lies in a given interval.
		    */
		    TDistribution tDist = new TDistribution(degreesOfFreedom);
		    final double significance = 1.0 - confidenceLevel;
		    final double criticalValue = tDist.inverseCumulativeProbability(1.0 - significance/2.0);
		    System.out.printf("\n\tt-Distribution critical value for %d samples: %f\n", stats.getN(), criticalValue);
		    if(isApplyAntitheticVariatesTechnique()){
			    System.out.println("\tThere are less samples than simulation runs due to the application of 'Antithetic Variates Technique', that reduces the number of samples to half.");
		    }

	        // Calculates the confidence interval error margin
	        return criticalValue * stats.getStandardDeviation() / Math.sqrt(stats.getN());
	    } catch (MathIllegalArgumentException e) {
	        return Double.NaN;
	    }
	}

	/**
	 * Checks if the "Antithetic Variates Technique" is to be applied
	 * to reduce results variance.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic variates</a>
	 */
	public boolean isApplyAntitheticVariatesTechnique() {
		return applyAntitheticVariatesTechnique;
	}

	/**
	 * Gets the number of times the experiment will be executed
	 * in order to get values such as means and standard deviations.
	 * It has to be an even number if the {@link #isApplyAntitheticVariatesTechnique() "Antithetic Variates Technique"}
	 * is to be used.
	 */
	public int getNumberOfSimulationRuns() {
		return numberOfSimulationRuns;
	}

	protected ExperimentRunnerAbstract setNumberOfSimulationRuns(int numberOfSimulationRuns) {
		this.numberOfSimulationRuns = numberOfSimulationRuns;
		return this;
	}

	protected ExperimentRunnerAbstract setApplyAntitheticVariatesTechnique(boolean applyAntitheticVariatesTechnique) {
		this.applyAntitheticVariatesTechnique = applyAntitheticVariatesTechnique;
		return this;
	}

	/**
	 * Gets the number of batches in which the simulation runs will be divided.
	 *
	 * If this number is greater than 1, the "Batch Means Method" is used to reduce the correlation
	 * between experiment runs.
	 */
	public int getNumberOfBatches() {
		return numberOfBatches;
	}

	/**
	 * Sets the number of batches in which the simulation runs will be divided.
	 * @param numberOfBatches number of simulation run batches
	 * @see #getNumberOfBatches()
	 */
	protected ExperimentRunnerAbstract setNumberOfBatches(int numberOfBatches) {
		this.numberOfBatches = numberOfBatches;
		return this;
	}

	/**
	 * Gets the base seed used for pseudo random number generators.
	 * This seed is used for generation of random values used across
	 * all experiment runs.
	 * For each different experiment run, a different seed based
	 * on this one is used.
	 *
	 * @see #getSeeds()
	 */
	public long getBaseSeed() {
		return baseSeed;
	}

	/**
	 * Gets the seeds used to run each experiment.
	 *
	 * @see #createRandomGen(int)
	 */
	public long[] getSeeds() {
		return seeds;
	}

	/**
	 * Creates a pseudo random number generator (PRNG) for a experiment run.
	 * If it is to apply the {@link #isApplyAntitheticVariatesTechnique() "Antithetic Variates Technique"} to reduce
	 * results variance, the second half of experiments will used
	 * the seeds from the first half.
	 *
	 * @return the created PRNG
	 *
	 * @see UniformDistr#isApplyAntitheticVariatesTechnique()
	 */
	protected UniformDistr createRandomGen(int experimentIndex) {
		final long experimentSeed = getBaseSeed() + experimentIndex + 1;
		UniformDistr rnd;
		if (isApplyAntitheticVariatesTechnique() && experimentIndex >= halfSimulationRuns()) {
			int previousExperiment = experimentIndex - halfSimulationRuns();
			rnd = new UniformDistr(0, 1, getSeeds()[previousExperiment]);
			rnd.setApplyAntitheticVariatesTechnique(true);
		}
		else rnd = new UniformDistr(0, 1, experimentSeed);

		getSeeds()[experimentIndex] = rnd.getSeed();
		return rnd;
	}

	/**
	 * @return the half of {@link #getNumberOfSimulationRuns()}
	 */
	private int halfSimulationRuns() {
		return getNumberOfSimulationRuns()/2;
	}

	/**
	 * Time in seconds the experiments finished.
	 */
	public long getExperimentsFinishTime() {
		return experimentsFinishTime;
	}

	/**
	 * Time in seconds the experiments started.
	 */
	public long getExperimentsStartTime() {
		return experimentsStartTime;
	}

	/**
	 * Setup and starts the execution of the experiments.
	 */
	protected void start() {
		setupInternal();

		printSimulationParameters();
		Log.disable();

		experimentsStartTime = System.currentTimeMillis();
		for(int i = 0; i < getNumberOfSimulationRuns(); i++){
			createAndRunExperiment(i);
		}
		experimentsFinishTime = (System.currentTimeMillis() - experimentsStartTime)/1000;

		printResults(computeStatistics());
	}

	/**
	 * <p>Computes the antithetic means for the given samples
	 * if the {@link #isApplyAntitheticVariatesTechnique() "Antithetic Variates Technique" is to be applied}.
	 *
	 * These values are the mean between the first half of samples
	 * with the second half. By this way, the resulting value
	 * is an array with half of the samples length.
	 * </p>
	 *
	 * <p><b>NOTE:</b> To correctly compute the antithetic values
	 * the seeds from the first half of experiments must be used
	 * for the second half.</p>
	 *
	 * @param samples the samples to compute the antithetic means from
	 * @return the computed antithetic means from the given samples
	 * if the "Antithetic Variates Technique" is to be applied,
	 * otherwise return the same given samples array.
	 *
	 * @see #createRandomGen(int)
	 */
	protected double[] computeAntitheticMeans(double samples[]) {
		if(!isApplyAntitheticVariatesTechnique())
			return samples;

		final int half = samples.length/2;
		double antitheticMeans[] = new double[half];
		//applies the "Antithetic Variates Technique" to reduce variance
		for(int i = 0; i < half; i++){
			antitheticMeans[i] = (samples[i] + samples[half+i])/2.0;
		}

		System.out.printf(
			"\tAntithetic Variates Technique applied. The number of samples was reduced to the half (%d).\n", half);

		return antitheticMeans;
	}

	protected abstract void printSimulationParameters();
	protected abstract SummaryStatistics computeStatistics();

	/**
	 * Prints final simulation results such as means, standard deviations
	 * and confidence intervals.
	 *
	 * @param stats the {@link SummaryStatistics} object to compute the statistics
	 */
	protected abstract void printResults(SummaryStatistics stats);

	/**
	 * Creates and run an experiment for the i'th time.
	 * @param i the index that represents the current experiment run
	 */
	protected abstract void createAndRunExperiment(int i);
}
