package org.cloudsimplus.experiments;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.distributions.NormalDistr;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudsimplus.heuristics.CloudletToVmMappingSolution;

/**
 * Runs the {@link DatacenterBrokerHeuristicExperiment}
 * the number of times defines by {@link #numberOfSimulationRuns}
 * and compute statistics.
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterBrokerHeuristicRunner {
	/**
	 * Possible number of PEs for VMs to be created.
	 * Each VM has to have one of this number of PEs,
	 * to be chosen randomly.
	 */
	private static final int VM_PES_NUMBERS[] = {2, 4, 8};

    /**
     * Number of Vm's to create for each experiment.
     */
	private static final int VMS_TO_CREATE = VM_PES_NUMBERS.length*20;

    /**
     * Number of Cloudlets to create for each experiment.
     */
    private static final int CLOUDLETS_TO_CREATE = 100;

    private int numberOfSimulationRuns;

    /**
     * Number of PEs for each created Cloudlet.
     * All the experiments run with the same scenario configuration,
     * including number of hosts, VMs and Cloudlets.
     * What changes is the random number generator seed for each experiment.
     */
    private final int cloudletPesArray[];

	/**
	 * Number of PEs for each created VM.
	 * All the experiments run with the same scenario configuration,
	 * including number of hosts, VMs and Cloudlets.
	 * What changes is the random number generator seed for each experiment.
	 */
	private final int vmPesArray[];
    /**
     * Seeds used to run each experiment.
     * The experiments will apply the "Antithetic Variates Technique" to reduce
     * results variance.
     *
     * @see UniformDistr#isApplyAntitheticVariatesTechnique()
     */
    private long seeds[];
    /**
     * The cost of each executed experiment.
     */
    private double experimentCosts[];

    /**
     * An object that stores the cost of each
     * experiment after applying the "Antithetic Variates Technique"
     * and compute some statistics.
     */
    private final SummaryStatistics costsStats;

	/**
	 * An object that compute statistics about experiment run time.
	 */
	private final SummaryStatistics timeStats;

	/**
	 * @see #isApplyAntitheticVariatesTechnique()
	 */
	private boolean applyAntitheticVariatesTechnique;

	/**
	 * @see #getNumberOfBatches()
	 */
	private int numberOfBatches;

	/**
	 * A seed used for pseudo random number generators.
	 * For each executed experiment, a different seed is generated
	 * based on this one.
	 */
	private final long baseSeed;
    /**
     * A Cloudlet to VM mapping that used a Round-Robin implementation to
     * cyclically select a Vm from the Vm list to host a Cloudlet.
     * This is the implementation used by the {@link DatacenterBrokerSimple} class.
     */
    private CloudletToVmMappingSolution roundRobinSolution = null;
	/**
	 * Time in seconds the experiments started.
	 */
	private long experimentsStartTime = 0;
	/**
	 * Time in seconds the experiments finished.
	 */
	private long experimentsFinishTime = 0;

    public DatacenterBrokerHeuristicRunner(){
	    experimentCosts = new double[0];
	    seeds = new long[0];
	    costsStats = new SummaryStatistics();
	    timeStats = new SummaryStatistics();
	    baseSeed = System.currentTimeMillis();
	    vmPesArray = createVmPesArray();
		cloudletPesArray = createCloudletPesArray();
	    setNumberOfBatches(0);
    }

    /**
     * Starts the execution of the experiments
     * the number of times defines in {@link #numberOfSimulationRuns}.
     * @param args
     */
    public static void main(String[] args) {
        new DatacenterBrokerHeuristicRunner()
	        .setNumberOfSimulationRuns(240)
	        .setApplyAntitheticVariatesTechnique(true)
	        .setNumberOfBatches(6)
	        .start();
    }

	private int[] createCloudletPesArray() {
		int[] array = new int[CLOUDLETS_TO_CREATE];
		int totalNumberOfPes = 0;
		NormalDistr random = new NormalDistr(baseSeed, 2, 0.6);
		System.out.printf("PEs created for %d Cloudlets:\n\t", CLOUDLETS_TO_CREATE);
		for(int i = 0; i < CLOUDLETS_TO_CREATE; i++){
			array[i] =  (int) random.sample()+1;
			System.out.printf("%d ", array[i]);
			totalNumberOfPes += array[i];
		}
		System.out.printf("\n\tTotal of %d Cloudlet PEs created.\n\n", totalNumberOfPes);

		return array;
	}

	private int[] createVmPesArray() {
		UniformDistr random = new UniformDistr(0, VM_PES_NUMBERS.length, baseSeed);
		int[] array = new int[VMS_TO_CREATE];
		int totalNumberOfPes = 0;
		System.out.printf("PEs created for %d VMs:\n\t", VMS_TO_CREATE);
		for(int i = 0; i < VMS_TO_CREATE; i++){
			array[i] =  VM_PES_NUMBERS[(int)random.sample()];
			System.out.printf("%d ", array[i]);
			totalNumberOfPes += array[i];
		}
		System.out.printf("\n\tTotal of %d VMs PEs created.\n\n", totalNumberOfPes);
		return array;
	}

	/**
	 * Start the experiments.
	 */
	private void start() {
		setup();

		System.out.printf("Executing %d experiments. Please wait ... It may take a while.\n", getNumberOfSimulationRuns());
		System.out.println("Experiments configurations:");
		System.out.printf("\tBase seed: %d | Number of VMs: %d | Number of Cloudlets: %d\n", baseSeed, VMS_TO_CREATE, CLOUDLETS_TO_CREATE);
		System.out.printf("\tApply Antithetic Variates Technique: %b\n", isApplyAntitheticVariatesTechnique());
		if(numberOfBatches > 1) {
			System.out.printf("\tNumber of Batches for Batch Means Method: %d", numberOfBatches);
			System.out.printf("\tBatch Size: %d\n", batchSizeCeil());
		}
		System.out.printf("\nSimulated Annealing Parameters\n");
		System.out.printf(
			"\tInitial Temperature: %.2f | Cold Temperature: %.4f | Cooling Rate: %.3f | Neighborhood searches by iteration: %d\n",
			DatacenterBrokerHeuristicExperiment.SA_INITIAL_TEMPERATURE,
			DatacenterBrokerHeuristicExperiment.SA_COLD_TEMPERATURE,
			DatacenterBrokerHeuristicExperiment.SA_COOLING_RATE,
			DatacenterBrokerHeuristicExperiment.SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);

		Log.disable();

		experimentsStartTime = System.currentTimeMillis();
		for(int i = 0; i < getNumberOfSimulationRuns(); i++){
			System.out.print((i % 100 == 0 ? "\n." : "."));
			DatacenterBrokerHeuristicExperiment experiment = createExperiment(i, false);
			CloudletToVmMappingSolution solution = experiment.start();
			experimentCosts[i] = solution.getCost();
			timeStats.addValue(solution.getHeuristic().getSolveTime());
			createRoundRobinSolutionIfNotCreatedYet(experiment);
		}
        experimentsFinishTime = (System.currentTimeMillis() - experimentsStartTime)/1000;

		computeStatistics();
		printResults();
	}

	/**
	 * Setup experiment attributes considering
	 * the dependency between each other.
	 * The method is called by the {@link #start()} method,
	 * just after all the attributes were set.
	 * By this way, it initialize internal attributes
	 * and validate other ones.
	 */
	private void setup() {
		/*
		 * The "Antithetic Variates Technique" for variance reduction
		 * requires an even number of simulation runs.
		 * Accordingly, if the "Batch Means Method" is used
		 * simultaneously, the number of batches has
		 * to be even.
		 */
		if(applyAntitheticVariatesTechnique){
			if(numberOfSimulationRuns%2!=0)
				numberOfSimulationRuns++;

			if(numberOfBatches%2!=0)
				numberOfBatches++;
		}

		/*
		If it is to use the  "Batch Means Method" and the number of
		simulation runs is not multiple of the number of batches,
		adjust the number of simulation runs, once each batch
		has to have the same size.
		If applyAntitheticVariatesTechnique is true, the number of batches will
		be even and consequently, the number of simulation runs after being
		adjusted will be even too.
		 */
		if(numberOfBatches > 1 && numberOfSimulationRuns % numberOfBatches != 0){
			numberOfSimulationRuns = batchSizeCeil() * numberOfBatches;
		}

		experimentCosts = new double[numberOfSimulationRuns];
		seeds = new long[numberOfSimulationRuns];
	}

	/**
	 * @return the batch size rounded by the {@link Math#ceil(double)} method.
	 */
	private int batchSizeCeil() {
		return (int)Math.ceil(numberOfSimulationRuns / (double)numberOfBatches);
	}

	/**
     * Creates an experiment.
     *
     * @param index a number that identifies the experiment
     * @oaram verbose if experiment execution information must be shown
     * @return the created experiment
     */
    private DatacenterBrokerHeuristicExperiment createExperiment(int index, boolean verbose) {
        UniformDistr randomGen = createRandomGen(index);
	    DatacenterBrokerHeuristicExperiment exp =
		    new DatacenterBrokerHeuristicExperiment(randomGen, index);

        exp.setVerbose(verbose);
	    exp.setVmPesArray(vmPesArray);
        exp.setCloudletPesArray(cloudletPesArray);
		exp.buildScenario();

        return exp;
    }

    private void printResults() {
        System.out.printf("\n# Results for %d simulation runs\n", getNumberOfSimulationRuns());
        System.out.printf(
            "\tRound-robin solution used by DatacenterBrokerSimple - Cost: %.2f\n",
            roundRobinSolution.getCost());

	    if(getNumberOfSimulationRuns() > 1){
	        System.out.printf(
	            "\tHeuristic solutions - Mean cost: %.2f Std. Dev.: %.2f\n",
	            costsStats.getMean(), costsStats.getStandardDeviation());
	        showConfidenceInterval();
	        System.out.printf(
	            "\n\tThe mean cost of heuristic solutions represent %.2f%% of the Round-robin mapping used by the DatacenterBrokerSimple\n",
		        heuristicSolutionCostPercentageOfRoundRobinSolution(costsStats.getMean()));
		    System.out.printf("Experiment execution mean time: %.2f seconds\n", timeStats.getMean());
	    }

        Log.enable();
        Log.printFormattedLine("\nExperiments finished in %d seconds!", experimentsFinishTime);
    }

	/**
	 * Computes the percentage of the Round-robin solution cost that the heuristic solution
	 * cost represents.
	 * @param heuristicSolutionCost the cost of the heuristic solution
	 * @return the percentage of the Round-robin solution cost that the heuristic solution represents
	 */
	private double heuristicSolutionCostPercentageOfRoundRobinSolution(double heuristicSolutionCost) {
		return heuristicSolutionCost*100.0/roundRobinSolution.getCost();
	}

	/**
     * Fill the {@link #costsStats} object with the cost of each experiment.
	 * Using such object, a general mean and standard deviation can be obtained.
	 *
     * If the {@link #isApplyAntitheticVariatesTechnique()}
	 * is true, apples the "Antithetic Variates Technique"
     * in order to reduce variance of simulation results.
	 * In this case, just half of the results are returned,
	 * representing the means between each value from the first
	 * half with each value from the second half.
     */
    private void computeStatistics() {
        double costs[] = experimentCosts;

	    costs = computeBatchMeansCosts(costs);
        costs = computeAntitheticCosts(costs);

        for(double cost: costs){
            costsStats.addValue(cost);
        }
    }

	/**
	 * If the "Antithetic Variates Technique" is to be applied,
	 * computes the antithetic values for the given costs
	 * or mapping Cloudlets to VMs.
	 *
	 * @param costs the costs to compute the antithetic values from
	 * @return the Antithetic costs if the "Antithetic Variates Technique" is to be applied,
	 * otherwise return the same given costs array.
	 */
	private double[] computeAntitheticCosts(double costs[]) {
		if(!isApplyAntitheticVariatesTechnique())
			return costs;

		final int half = costs.length/2;
		double antitheticCostsMeans[] = new double[half];
		//applies the "Antithetic Variates Technique" to reduce variance
		for(int i = 0; i < half; i++){
			antitheticCostsMeans[i] = (costs[i]+costs[half+i])/2;
		}

		System.out.printf(
			"\tAntithetic Variates Technique applied. The number of samples was reduced to the half (%d).\n", half);

		return antitheticCostsMeans;
	}

	public boolean isApplyBatchMeansMethod(){
		return numberOfBatches > 1;
	}

	/**
	 * If the "Batch Means Method"{@link #isApplyBatchMeansMethod() is to be used},
	 * creates the costs means to map cloudlets to VMs based on this method.
	 *
	 * @param costs the array with costs to map cloudlets to VMs
	 * @return the cost means after applying the "Batch Means Method"
	 * in case the method is enabled to be applied, otherwise
	 * return the same given costs array
	 */
	private double[] computeBatchMeansCosts(double costs[]) {
		if(!isApplyBatchMeansMethod())
			return costs;

		double batchMeans[] = new double[numberOfBatches];
		int k = batchSizeCeil();
		for(int i = 0; i < numberOfBatches; i++){
			SummaryStatistics stats = new SummaryStatistics();
			for(int j = 0; j < k; j++){
				stats.addValue(costs[i*k + j]);
			}
			batchMeans[i] = stats.getMean();
		}

		System.out.printf(
			"\tBatch Means Method applied. The number of samples was reduced to %d after computing the mean for each batch.\n", numberOfBatches);

		return batchMeans;
	}

	private void showConfidenceInterval() {
        // Calculate 95% confidence interval
        double intervalSize = computeConfidenceErrorMargin(costsStats, 0.95);
        double lower = costsStats.getMean() - intervalSize;
        double upper = costsStats.getMean() + intervalSize;
        System.out.printf(
            "\tSolution cost mean 95%% Confidence Interval: %.2f ∓ %.2f, that is [%.2f to %.2f]\n",
            costsStats.getMean(), intervalSize, lower, upper);
    }

    /**
     * <p>Computes the confidence interval error margin in order to enable finding
     * the interval lower and upper bound around a mean value.
     * By this way, the confidence interval can be computed
     * as [mean + error margin .. mean - error margin].
     *
     * </p>
     *
     * <p>To reduce the confidence interval by half, one have to execute
     * the experiments 4 more times. This is called the "Replication Method" and just
     * works when the samples are i.i.d. (independent and identically distributed).
     * Thus, if you have correlation between samples of each simulation run, a different
     * method such as a bias compensation, batch means or regenerative method
     * has to be used. </p>
     *
     * <b>NOTE:</b> How to compute the error margin is a little bit confusing.
     * The Harry Perros book states that if less than 30 samples are collected,
     * the t-Distribution has to be used to that purpose.
     *
     * However, the article https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps
     * says that if the standard deviation of the real population is known,
     * it has to be used the z-value from the standard normal distribution.
     * Otherwise, it has to be used the t-value from the t-Distribution
     * to calculate the critical value.
     * The book "Numeric Computation and Statistical Data Analysis on the Java Platform" confirms
     * the last statement and such approach was followed.
     *
     * @param stats the statistic object with the values to compute
     * the size of confidence interval
     * @param confidenceLevel the confidence interval level, in the interval from ]0 to 1[,
     * such as 0.95 to 95% of confidence.
     * @return the confidence interval error margin to compute the
     * lower and upper bound of the confidence interval.
     * @see <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda3672.htm">Critical Values of the Student's t Distribution</a>
     * @see <a href="https://en.wikipedia.org/wiki/Student%27s_t-distribution">t-Distribution</a>
     * @see <a href="http://www4.ncsu.edu/~hp/files/simulation.pdf">Harry Perros, "Computer Simulation Techniques: The definitive introduction!," 2009</a>
     * @see <a href="http://www.springer.com/gp/book/9783319285290">Numeric Computation and Statistical Data Analysis on the Java Platform</a>
     */
    private double computeConfidenceErrorMargin(SummaryStatistics stats, double confidenceLevel) {
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

    private int halfExperiments() {
        return getNumberOfSimulationRuns() /2;
    }

    /**
     * Creates a pseudo random number generator (PRNG) for the experiment.
     * For the second half of experiments, it uses the seed
     * of the first half to apply "Antithetic Variates Technique"
     * in order to reduce results variance.
     *
     * @return the created PRNG
     * @see UniformDistr#isApplyAntitheticVariatesTechnique()
     */
    private UniformDistr createRandomGen(int experimentIndex) {
	    final long experimentSeed = baseSeed + experimentIndex + 1;
	    UniformDistr rnd;
        if (isApplyAntitheticVariatesTechnique() && experimentIndex >= halfExperiments()) {
	        int previousExperiment = experimentIndex - halfExperiments();
	        rnd = new UniformDistr(0, 1, seeds[previousExperiment]);
	        rnd.setApplyAntitheticVariatesTechnique(true);
        }
		else rnd = new UniformDistr(0, 1, experimentSeed);

	    seeds[experimentIndex] = rnd.getSeed();
	    return rnd;
    }


    /**
     * Creates a Round-robin mapping between Cloudlets and Vm's
     * from the Cloudlets and Vm's of a given experiment,
     * in the same way as the {@link DatacenterBrokerSimple} does.
     *
     * @param exp the experiment to get the list of Cloudlets and Vm's
     */
    private void createRoundRobinSolutionIfNotCreatedYet(DatacenterBrokerHeuristicExperiment exp) {
		if(roundRobinSolution != null)
			return;

        roundRobinSolution = new CloudletToVmMappingSolution(exp.getHeuristic());
        int i = 0;
        for (Cloudlet c : exp.getCloudletList()) {
            //cyclically selects a Vm (as in a circular queue)
            roundRobinSolution.bindCloudletToVm(c, exp.getVmList().get(i));
            i = (i+1) % exp.getVmList().size();
        }
    }

	/**
	 * Antithetic Variates Technique is to be used to reduce
	 * simulation results variance, and consequently
	 * the confidence interval.
	 */ /**
	 * Indicates if the "Antithetic Variates Technique" has to be applied
	 * to reduce results variance.
	 * @return true if the technique is to be applied, false otherwise
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic variates</a>
	 */
	public boolean isApplyAntitheticVariatesTechnique() {
		//the "Antithetic Variates Technique" only can be applied for even number of simulation runs
		return applyAntitheticVariatesTechnique && getNumberOfSimulationRuns() % 2 == 0;
	}

	/**
	 * Number of times the cloud simulation will be executed
	 * in order to get values such as means and standard deviations.
	 * It has to be an even number due to the use
	 * of "Antithetic Variates Technique".
	 */
	public int getNumberOfSimulationRuns() {
		return numberOfSimulationRuns;
	}

	private DatacenterBrokerHeuristicRunner setNumberOfSimulationRuns(int numberOfSimulationRuns) {
		this.numberOfSimulationRuns = numberOfSimulationRuns;
		return this;
	}

	private DatacenterBrokerHeuristicRunner setApplyAntitheticVariatesTechnique(boolean applyAntitheticVariatesTechnique) {
		this.applyAntitheticVariatesTechnique = applyAntitheticVariatesTechnique;
		return this;
	}

	/**
	 * Gets the number of batches in which the simulation runs will be divided.
	 *
	 * If this number is greather than zero, the Batch Means Method is used to reduce the correlation
	 * between results of the same simulation run.
	 * In this experiment, as for each simulation
	 * run there is just one cost for mapping Cloudlets to VMs,
	 * if the method is applied, the simulation runs are divided in several
	 * batches, obtaining means for each of these batches.
	 */
	public int getNumberOfBatches() {
		return numberOfBatches;
	}

	/**
	 * Sets the number of batches in which the simulation runs will be divided.
	 * @param numberOfBatches number of simulation run batches
	 * @return
	 * @see #getNumberOfBatches()
	 */
	private DatacenterBrokerHeuristicRunner setNumberOfBatches(int numberOfBatches) {
		this.numberOfBatches = numberOfBatches;
		return this;
	}
}
