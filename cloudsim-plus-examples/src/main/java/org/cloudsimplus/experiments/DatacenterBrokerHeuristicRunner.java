package org.cloudsimplus.experiments;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.Log;
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
     * Number of Vm's to create for each experiment.
     */
	private static final int VMS_TO_CREATE = 50;

    /**
     * Number of Cloudlets to create for each experiment.
     */
    private static final int CLOUDLETS_TO_CREATE = 100;

    /**
     * Maximum number of PEs that a Vm can have
     * and that a Cloudlet can require.
     */
    private static final int MAX_NUMBER_OF_PES = 4;

    /**
     * Number of times the cloud simulation will be executed
     * in order to get values such as means and standard deviations.
     * It has to be an even number due to the use
     * of "Antithetic Variates Technique".
     */
    private static final int NUMBER_OF_SIMULATION_RUNS = 1000;

    /**
     * A number generator to randomly generate number of PEs
     * to be used for creation of Vm's and Cloudlets.
     */
    private final UniformDistr randomPesGen;

    /**
     * Number of PEs for each created VM.
     * All the experiments run with the same scenario configuration,
     * including number of hosts, VMs and Cloudlets.
     * What changes is the random number generator seed for each experiment.
     */
    private final int vmsPes[];

    /**
     * Number of PEs for each created Cloudlet.
     * All the experiments run with the same scenario configuration,
     * including number of hosts, VMs and Cloudlets.
     * What changes is the random number generator seed for each experiment.
     */
    private final int coudletsPes[];

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
    private SummaryStatistics costsStats;

	/**
	 * An object that compute statistics about experiment run time.
	 */
	private SummaryStatistics timeStats;

    /**
     * Indicates if the "Antithetic Variates Technique" has to be applied
     * to reduce results variance.
     * @see <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic variates</a>
     */
    private final boolean applyAntitheticVariatesTechnique;

    /**
     * Starts the execution of the experiments
     * the number of times defines in {@link #NUMBER_OF_SIMULATION_RUNS}.
     * @param args
     */
    public static void main(String[] args) {
        new DatacenterBrokerHeuristicRunner();
    }

    public DatacenterBrokerHeuristicRunner(){
        randomPesGen = new UniformDistr(0, MAX_NUMBER_OF_PES);

        vmsPes = createArrayOfPes(VMS_TO_CREATE);
        coudletsPes = createArrayOfPes(CLOUDLETS_TO_CREATE);
        experimentCosts = new double[NUMBER_OF_SIMULATION_RUNS];
        seeds = new long[NUMBER_OF_SIMULATION_RUNS];
	    costsStats = new SummaryStatistics();
	    timeStats = new SummaryStatistics();

        Log.printFormatted("Executing %d experiments. Please wait ... It may take a while.\n", NUMBER_OF_SIMULATION_RUNS);
        long startTime = System.currentTimeMillis();
        Log.disable();

        this.applyAntitheticVariatesTechnique = true;
        for(int i = 0; i < NUMBER_OF_SIMULATION_RUNS; i++){
			System.out.print((i % 100 == 0 ? "\n." : "."));
			DatacenterBrokerHeuristicExperiment exp = createExperiment(i);
	        CloudletToVmMappingSolution solution = exp.start();
	        experimentCosts[i] = solution.getCost();
			timeStats.addValue(exp.getHeuristic().getSolveTime());
	        if(roundRobinSolution == null){
                roundRobinSolution = createRoundRobinSolution(exp);
            }
        }
        System.out.println();

        computeStatisticsApplyingAntitheticVariatesTechnique();
        printResults(startTime);
    }

    /**
     * Creates an experiment.
     *
     * @param index a number that identifies the experiment
     * @return the created experiment
     * @throws RuntimeException
     */
    private DatacenterBrokerHeuristicExperiment createExperiment(int index) throws RuntimeException {
        UniformDistr randomGen = createRandomGen(index);
        seeds[index] = randomGen.getSeed();
        DatacenterBrokerHeuristicExperiment exp =
                new DatacenterBrokerHeuristicExperiment(randomGen, index);
        exp.setVerbose(false);
        exp.setVmsPes(vmsPes);
        exp.setCloudlestPes(coudletsPes);

        return exp;
    }

    private void printResults(long startTime) {
        System.out.printf("# Results for %d simulation runs\n", NUMBER_OF_SIMULATION_RUNS);
        System.out.printf("# Antithetic Variates Technique applied: %b\n", applyAntitheticVariatesTechnique);
        System.out.printf(
            "\tRound-robin solution used by DatacenterBrokerSimple - Cost: %.2f\n",
            roundRobinSolution.getCost());
        System.out.printf(
            "\tHeuristic solutions - Mean cost: %.2f Std. Dev.: %.2f\n",
            costsStats.getMean(), costsStats.getStandardDeviation());
        showConfidenceInterval();
        System.out.printf(
            "\n\tThe mean cost of heuristic solutions represent %.2f%% of the Round-robin mapping used by the DatacenterBrokerSimple\n",
            costsStats.getMean()*100.0/roundRobinSolution.getCost());

        Log.enable();
        long totalExecutionSeconds = (System.currentTimeMillis() - startTime)/1000;
        Log.printFormattedLine("\nExperiments finished in %d seconds!", totalExecutionSeconds);
	    System.out.printf("Experiment execution mean time: %.2f seconds\n", timeStats.getMean());
	    System.out.printf("Used seeds: \n\t");
        for(long seed: seeds){
            System.out.printf("%d ", seed);
        }
    }

    /**
     * Creates a {@link SummaryStatistics} object with the cost of each
     * experiment after applying the "Antithetic Variates Technique"
     * in order to reduce variance.
     * Using such object, a general mean and standard deviation can be obtained.
     *
     * @see #costsStats
     */
    private void computeStatisticsApplyingAntitheticVariatesTechnique() {
        double costs[];

        if(!applyAntitheticVariatesTechnique){
            costs = experimentCosts;
        }
        else {
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
        double ci = computeMeanConfidenceInterval(costsStats, 0.95);
        double lower = costsStats.getMean() - ci;
        double upper = costsStats.getMean() + ci;
        System.out.printf(
            "\tSolution cost mean 95%% Confidence Interval: %.2f ∓ %.2f, that is [%.2f to %.2f]\n",
            costsStats.getMean(), ci, lower, upper);
    }

    /**
     * Compute the confidence interval to enable finding
     * the interval lower and upper bound around a mean value.
     *
     * @param stats the statistic object with the values to compute
     * the confidence interval
     * @param level the confidence interval level, in the interval from ]0 to 1[,
     * such as 0.95 to 95% of confidence.
     * @return the confidence interval value to compute the
     * lower and upper interval bound.
     */
    private double computeMeanConfidenceInterval(SummaryStatistics stats, double level) {
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
        if (!applyAntitheticVariatesTechnique || experimentIndex < halfExperiments()) {
            return new UniformDistr(0, 1);
        }

        int previousExperiment = experimentIndex - halfExperiments();
        UniformDistr rnd = new UniformDistr(0, 1, seeds[previousExperiment]);
        rnd.setApplyAntitheticVariatesTechnique(true);
        return rnd;
    }

    /**
     * Creates an array with a list of PEs created randomly to be used
     * to defined the number of PEs of VMs or Cloudlets.
     *
     * @return the created array with PEs numbers
     */
    private int[] createArrayOfPes(int arraySize) {
        int array[] = new int[arraySize];
        for(int i = 0; i < arraySize; i++){
            array[i] = (int)randomPesGen.sample()+1;
        }

        return array;
    }

    /**
     * Creates a Round-robin mapping between Cloudlets and Vm's
     * from the Cloudlets and Vm's of a given experiment,
     * in the same way as the {@link DatacenterBrokerSimple} does.
     *
     * @param exp the experiment to get the list of Cloudlets and Vm's
     * @return a Round-Robin mapping between Cloudlets and Vm's
     */
    private CloudletToVmMappingSolution createRoundRobinSolution(DatacenterBrokerHeuristicExperiment exp) {
        CloudletToVmMappingSolution solution =
                new CloudletToVmMappingSolution(exp.getHeuristic());
        int i = 0;
        for (Cloudlet c : exp.getCloudletList()) {
            //cyclically selects a Vm (as in a circular queue)
            solution.bindCloudletToVm(c, exp.getVmList().get(i));
            i = (i+1) % exp.getVmList().size();
        }

        return solution;
    }

}
