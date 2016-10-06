package org.cloudsimplus.testbeds;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudsimplus.heuristics.CloudletToVmMappingHeuristic;
import org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing;
import org.cloudsimplus.heuristics.CloudletToVmMappingSolution;
import org.cloudsimplus.heuristics.HeuristicSolution;

import java.util.function.Consumer;

/**
 * <p>An experiment that uses a
 * <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated Annealing</a>
 * heuristic to find a suboptimal mapping between Cloudlets and Vm's submitted to a
 * DatacenterBroker. The number of {@link Pe}s of Vm's and Cloudlets are defined
 * randomly by the {@link DatacenterBrokerHeuristicRunner} that instantiates
 * and runs several of this experiment and collect statistics from the results.
 *
 * The {@link DatacenterBrokerHeuristic} is used
 * with the {@link CloudletToVmMappingSimulatedAnnealing} class
 * in order to find an acceptable solution with a high
 * {@link HeuristicSolution#getFitness() fitness value}.</p>
 *
 * <p>Different {@link CloudletToVmMappingHeuristic} implementations can be used
 * with the {@link DatacenterBrokerHeuristic} class.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterBrokerHeuristicExperiment extends SimulationExperiment {
	public static final int HOSTS_TO_CREATE = 100;

	/**
	 * Simulated Annealing (SA) parameters.
	 */
	public static final double SA_INITIAL_TEMPERATURE = 1.0;
	public static final double SA_COLD_TEMPERATURE = 0.0001;
	public static final double SA_COOLING_RATE = 0.003;
	public static final int    SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES = 50;

	private CloudletToVmMappingSimulatedAnnealing heuristic;

	private Consumer<? super DatacenterBrokerHeuristicExperiment> afterExperimentFinishConsumer;

	/**
     * Instantiates the simulation experiment.
     *
	 * @param runner the runner that will be in charge to setup and run the experiment
     * @param randomGen pseudo random number generator used in the experiment
     * @param index a number the identifies the current experiment being run
     */
    public DatacenterBrokerHeuristicExperiment(DatacenterBrokerHeuristicRunner runner, UniformDistr randomGen, int index) {
	    super(index, runner);
		setHostsToCreate(HOSTS_TO_CREATE);
	    createSimulatedAnnealingHeuristic(randomGen);
	    afterExperimentFinishConsumer = exp->{};
    }

	private void createSimulatedAnnealingHeuristic(UniformDistr randomGen) {
		heuristic = new CloudletToVmMappingSimulatedAnnealing(SA_INITIAL_TEMPERATURE, randomGen);
		heuristic.setColdTemperature(SA_COLD_TEMPERATURE);
		heuristic.setCoolingRate(SA_COOLING_RATE);
		heuristic.setNumberOfNeighborhoodSearchesByIteration(SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);
	}

	@Override
	protected DatacenterBrokerHeuristic createBroker() {
		DatacenterBrokerHeuristic broker0 = new DatacenterBrokerHeuristic("Broker0");
		broker0.setHeuristic(heuristic);
		return broker0;
	}

	@Override
    public void printSolution(String title) {
		if(!isVerbose())
			return;

        System.out.printf("%s: cost %.2f fitness %.6f\n", title,
	        heuristic.getBestSolutionSoFar().getCost(),
	        heuristic.getBestSolutionSoFar().getFitness());
    }

    @Override
	public void run(){
		super.run();
	    afterExperimentFinishConsumer.accept(this);
    }

    /**
     * The heuristic used to solve the mapping between cloudlets and Vm's.
     */
    public CloudletToVmMappingSimulatedAnnealing getHeuristic() {
        return heuristic;
    }

	/**
	 * Sets a consumer object that receives the experiment instance after the experiment
	 * finishes executing and performs some post-processing tasks.
	 * These tasks can include collecting some statistics.
	 *
	 * @param afterExperimentFinishConsumer an instance of a {@link Consumer functional consumer interface}
	 * that will perform some operation using the current instance of this class.
	 */
	protected void setAfterExperimentFinish(Consumer<DatacenterBrokerHeuristicExperiment> afterExperimentFinishConsumer){
		this.afterExperimentFinishConsumer = afterExperimentFinishConsumer;
	}

}
