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
 * the number of times defines by {@link #NUMBER_OF_SIMULATION_RUNS}
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

    /**
     * Number of times the cloud simulation will be executed
     * in order to get values such as means and standard deviations.
     * It has to be an even number due to the use
     * of "Antithetic Variates Technique".
     */
    private static final int NUMBER_OF_SIMULATION_RUNS = 1000;

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
     * A Cloudlet to VM mapping that used a Round-Robin implementation to
     * cyclically select a Vm from the Vm list to host a Cloudlet.
     * This is the implementation used by the {@link DatacenterBrokerSimple} class.
     */
    private CloudletToVmMappingSolution roundRobinSolution = null;

    /**
     * Seeds used to run each experiment.
     * The experiments will apply the "Antithetic Variates Technique" to reduce
     * results variance.
     *
     * @see UniformDistr#isApplyAntitheticVariatesTechnique()
     */
    private final long seeds[];

    /**
     * The cost of each executed experiment.
     */
    private final double experimentCosts[];

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
	 * Antithetic Variates Technique is to be used to reduce
	 * simulation results variance.
	 */
    private final boolean applyAntitheticVariatesTechnique;

	/**
	 * Time in seconds the experiments started.
	 */
	private long experimentsStartTime = 0;

	/**
	 * Time in seconds the experiments finished.
	 */
	private long experimentsFinishTime = 0;

	/**
	 * A seed used for pseudo random number generators.
	 * For each executed experiment, a different seed is generated
	 * based on this one.
	 */
	private final long baseSeed;

    /**
     * Starts the execution of the experiments
     * the number of times defines in {@link #NUMBER_OF_SIMULATION_RUNS}.
     * @param args
     */
    public static void main(String[] args) {
        new DatacenterBrokerHeuristicRunner().start();
    }

    public DatacenterBrokerHeuristicRunner(){
        experimentCosts = new double[NUMBER_OF_SIMULATION_RUNS];
        seeds = new long[NUMBER_OF_SIMULATION_RUNS];
	    costsStats = new SummaryStatistics();
	    timeStats = new SummaryStatistics();
	    applyAntitheticVariatesTechnique = true;

	    baseSeed = System.currentTimeMillis();
		cloudletPesArray = createCloudletPesArray();
	    vmPesArray = createVmPesArray();
    }

	private int[] createCloudletPesArray() {
		int[] array = new int[CLOUDLETS_TO_CREATE];
		int totalNumberOfPes = 0;
		NormalDistr random = new NormalDistr(baseSeed, 2, 0.6);
		System.out.printf("PEs created for %d Cloudlets:\n\t", CLOUDLETS_TO_CREATE);
		for(int i = 0; i < CLOUDLETS_TO_CREATE; i++){
			array[i] =  (int)random.sample()+1;
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
		System.out.printf("Executing %d experiments. Please wait ... It may take a while.\n", NUMBER_OF_SIMULATION_RUNS);
		System.out.println("Experiments configurations:");
		System.out.printf("\tBase seed: %d | Number of VMs: %d | Number of Cloudlets: %d\n", baseSeed, VMS_TO_CREATE, CLOUDLETS_TO_CREATE);
		System.out.printf("\tApply Antithetic Variates Technique: %b\n", isApplyAntitheticVariatesTechnique());
		System.out.printf("\nSimulated Annealing Parameters\n");
		System.out.printf(
			"\tInitial Temperature: %.2f | Cold Temperature: %.4f | Cooling Rate: %.3f | Neighborhood searches by iteration: %d\n",
			DatacenterBrokerHeuristicExperiment.SA_INITIAL_TEMPERATURE,
			DatacenterBrokerHeuristicExperiment.SA_COLD_TEMPERATURE,
			DatacenterBrokerHeuristicExperiment.SA_COOLING_RATE,
			DatacenterBrokerHeuristicExperiment.SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);

		Log.disable();

		experimentsStartTime = System.currentTimeMillis();
		for(int i = 0; i < NUMBER_OF_SIMULATION_RUNS; i++){
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
        System.out.printf("\n# Results for %d simulation runs\n", NUMBER_OF_SIMULATION_RUNS);
        System.out.printf(
            "\tRound-robin solution used by DatacenterBrokerSimple - Cost: %.2f\n",
            roundRobinSolution.getCost());

	    if(NUMBER_OF_SIMULATION_RUNS > 1){
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
     */
    private void computeStatistics() {
        double costs[] = experimentCosts;

        if(isApplyAntitheticVariatesTechnique()){
	        final int half = halfExperiments();
	        double antitheticCostsMeans[] = new double[half];
	        //applies the "Antithetic Variates Technique" to reduce variance
	        for(int i = 0; i < half; i++){
		        antitheticCostsMeans[i] = (experimentCosts[i]+experimentCosts[half+i])/2;
	        }
	        costs = antitheticCostsMeans;
        }

        for(double cost: costs){
            costsStats.addValue(cost);
        }
    }

    private void showConfidenceInterval() {
        // Calculate 95% confidence interval
        double intervalSize = computeMeanConfidenceIntervalSize(costsStats, 0.95);
        double lower = costsStats.getMean() - intervalSize;
        double upper = costsStats.getMean() + intervalSize;
        System.out.printf(
            "\tSolution cost mean 95%% Confidence Interval: %.2f ∓ %.2f, that is [%.2f to %.2f]\n",
            costsStats.getMean(), intervalSize, lower, upper);
    }

    /**
     * Compute the confidence interval size to enable finding
     * the interval lower and upper bound around a mean value.
     *
     * @param stats the statistic object with the values to compute
     * the size of confidence interval
     * @param level the confidence interval level, in the interval from ]0 to 1[,
     * such as 0.95 to 95% of confidence.
     * @return the confidence interval size to compute the
     * lower and upper bound of the confidence interval.
     */
    private double computeMeanConfidenceIntervalSize(SummaryStatistics stats, double level) {
        try {
            // Create T Distribution with N-1 degrees of freedom
            TDistribution tDist = new TDistribution(stats.getN() - 1);
            // Calculate critical value
            double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - level) / 2);
            // Calculate confidence interval
            return critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN());
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }

    private int halfExperiments() {
        return NUMBER_OF_SIMULATION_RUNS/2;
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
	 * Indicates if the "Antithetic Variates Technique" has to be applied
	 * to reduce results variance.
	 * @return true if the technique is to be applied, false otherwise
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic variates</a>
	 */
	public boolean isApplyAntitheticVariatesTechnique() {
		//the "Antithetic Variates Technique" only can be applied for even number of simulation runs
		return applyAntitheticVariatesTechnique && NUMBER_OF_SIMULATION_RUNS % 2 == 0;
	}
}
