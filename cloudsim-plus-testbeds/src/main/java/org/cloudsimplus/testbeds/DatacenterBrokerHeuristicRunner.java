package org.cloudsimplus.testbeds;

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
public class DatacenterBrokerHeuristicRunner extends ExperimentRunnerAbstract {
	/**
	 * Possible number of PEs for VMs to be created.
	 * Each VM has to have one of this number of PEs,
	 * to be chosen randomly.
	 */
	private static final int VM_PES_NUMBERS[] = {2, 4, 8};

    /**
     * Number of Vm's to create for each experiment.
     */
	private static final int VMS_TO_CREATE = VM_PES_NUMBERS.length * 20;

    /**
     * Number of Cloudlets to create for each experiment.
     */
    private static final int CLOUDLETS_TO_CREATE = 100;

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
     * The cost to map Cloudlets to VMs for each executed experiment.
     */
    private double experimentCosts[];

	/**
	 * An object that compute statistics about experiment run time.
	 */
	private final SummaryStatistics runtimeStats;

	/**
     * A Cloudlet to VM mapping that used a Round-Robin implementation to
     * cyclically select a Vm from the Vm list to host a Cloudlet.
     * This is the implementation used by the {@link DatacenterBrokerSimple} class.
     */
    private CloudletToVmMappingSolution roundRobinSolution = null;

	public DatacenterBrokerHeuristicRunner(){
		super();
		experimentCosts = new double[0];
	    runtimeStats = new SummaryStatistics();
		vmPesArray = createVmPesArray();
		cloudletPesArray = createCloudletPesArray();
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

	/**
	 * Creates an array with the configuration of PEs for each Cloudlet to be
	 * created in each experiment run. Every experiment will use the same
	 * Cloudlets configurations.
	 *
	 * @return the created cloudlet PEs array
	 */
	private int[] createCloudletPesArray() {
		int[] array = new int[CLOUDLETS_TO_CREATE];
		int totalNumberOfPes = 0;
		NormalDistr random = new NormalDistr(getBaseSeed(), 2, 0.6);
		System.out.printf("PEs created for %d Cloudlets:\n\t", CLOUDLETS_TO_CREATE);
		for(int i = 0; i < CLOUDLETS_TO_CREATE; i++){
			array[i] =  (int) random.sample()+1;
			System.out.printf("%d ", array[i]);
			totalNumberOfPes += array[i];
		}
		System.out.printf("\n\tTotal of %d Cloudlet PEs created.\n\n", totalNumberOfPes);

		return array;
	}

	/**
	 * Creates an array with the configuration of PEs for each VM to be
	 * created in each experiment run. Every experiment will use the same
	 * VMs configurations.
	 *
	 * @return the created VMs PEs array
	 */
	private int[] createVmPesArray() {
		UniformDistr random = new UniformDistr(0, VM_PES_NUMBERS.length, getBaseSeed());
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

	@Override
	protected void createAndRunExperiment(int i) {
		System.out.print((i % 100 == 0 ? "\n." : "."));
		DatacenterBrokerHeuristicExperiment experiment = newExperiment(i, false);
		CloudletToVmMappingSolution solution = experiment.start();
		experimentCosts[i] = solution.getCost();
		runtimeStats.addValue(solution.getHeuristic().getSolveTime());
		createRoundRobinSolutionIfNotCreatedYet(experiment);
	}

	/**
	 * Creates an experiment.
	 *
	 * @param index a number that identifies the experiment
	 * @oaram verbose if experiment execution information must be shown
	 * @return the created experiment
	 */
	private DatacenterBrokerHeuristicExperiment newExperiment(int index, boolean verbose) {
		UniformDistr randomGen = createRandomGen(index);
		DatacenterBrokerHeuristicExperiment exp =
			new DatacenterBrokerHeuristicExperiment(randomGen, index);

		exp.setVerbose(verbose);
		exp.setVmPesArray(vmPesArray);
		exp.setCloudletPesArray(cloudletPesArray);
		exp.buildScenario();

		return exp;
	}

	@Override
	protected void printSimulationParameters() {
		System.out.printf("Executing %d experiments. Please wait ... It may take a while.\n", getNumberOfSimulationRuns());
		System.out.println("Experiments configurations:");
		System.out.printf("\tBase seed: %d | Number of VMs: %d | Number of Cloudlets: %d\n", getBaseSeed(), VMS_TO_CREATE, CLOUDLETS_TO_CREATE);
		System.out.printf("\tApply Antithetic Variates Technique: %b\n", isApplyAntitheticVariatesTechnique());
		if(isApplyBatchMeansMethod()) {
			System.out.println("\tApply Batch Means Method to reduce simulation results correlation: true");
			System.out.printf("\tNumber of Batches for Batch Means Method: %d", getNumberOfBatches());
			System.out.printf("\tBatch Size: %d\n", batchSizeCeil());
		}
		System.out.printf("\nSimulated Annealing Parameters\n");
		System.out.printf(
			"\tInitial Temperature: %.2f | Cold Temperature: %.4f | Cooling Rate: %.3f | Neighborhood searches by iteration: %d\n",
			DatacenterBrokerHeuristicExperiment.SA_INITIAL_TEMPERATURE,
			DatacenterBrokerHeuristicExperiment.SA_COLD_TEMPERATURE,
			DatacenterBrokerHeuristicExperiment.SA_COOLING_RATE,
			DatacenterBrokerHeuristicExperiment.SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);
	}

	@Override
	protected void setup() {
		experimentCosts = new double[getNumberOfSimulationRuns()];
	}

    @Override
    protected void printResults(SummaryStatistics stats) {
        System.out.printf("\n# Results for %d simulation runs\n", getNumberOfSimulationRuns());
        System.out.printf(
            "\tRound-robin solution used by DatacenterBrokerSimple - Cost: %.2f\n",
            roundRobinSolution.getCost());

	    if(getNumberOfSimulationRuns() > 1){
	        System.out.printf(
	            "\tHeuristic solutions - Mean cost: %.2f Std. Dev.: %.2f\n",
		        stats.getMean(), stats.getStandardDeviation());
	        showConfidenceInterval(stats);
	        System.out.printf(
	            "\n\tThe mean cost of heuristic solutions represent %.2f%% of the Round-robin mapping used by the DatacenterBrokerSimple\n",
		        heuristicSolutionCostPercentageOfRoundRobinSolution(stats.getMean()));
		    System.out.printf("Experiment execution mean time: %.2f seconds\n", runtimeStats.getMean());
	    }

        Log.enable();
        Log.printFormattedLine("\nExperiments finished in %d seconds!", getExperimentsFinishTime());
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
     * Creates a {@link SummaryStatistics} object with the cost of each experiment.
	 * Using such a object, a general mean and standard deviation can be obtained.
	 *
     * If the {@link #isApplyAntitheticVariatesTechnique()} is true,
	 * applies the "Antithetic Variates Technique"
     * in order to reduce variance of simulation results.
	 * In this case, just half of the results are returned,
	 * representing the means between each value from the first
	 * half with each value from the second half.
	 *
	 * If the {@link #isApplyBatchMeansMethod()} is true,
	 * applies the "Batch Means Method" to reduce simulation results
	 * correlation.
	 * @return a {@link SummaryStatistics} object with the cost of each experiment
	 * to allow get mean, standard deviation and confidence interval
     */
	@Override
    protected SummaryStatistics computeStatistics() {
		SummaryStatistics costsStats = new SummaryStatistics();
        double costs[] = experimentCosts;

	    costs = computeBatchMeans(costs);
        costs = computeAntitheticMeans(costs);

        for(double cost: costs){
            costsStats.addValue(cost);
        }

        return costsStats;
    }

	private void showConfidenceInterval(SummaryStatistics stats) {
        // Calculate 95% confidence interval
        double intervalSize = computeConfidenceErrorMargin(stats, 0.95);
        double lower = stats.getMean() - intervalSize;
        double upper = stats.getMean() + intervalSize;
        System.out.printf(
            "\tSolution cost mean 95%% Confidence Interval: %.2f ∓ %.2f, that is [%.2f to %.2f]\n",
	        stats.getMean(), intervalSize, lower, upper);
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

}
