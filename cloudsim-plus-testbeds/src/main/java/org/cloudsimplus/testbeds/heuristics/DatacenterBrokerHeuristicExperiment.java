package org.cloudsimplus.testbeds.heuristics;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.heuristics.CloudletToVmMappingHeuristic;
import org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing;
import org.cloudsimplus.heuristics.HeuristicSolution;
import org.cloudsimplus.testbeds.SimulationExperiment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
final class DatacenterBrokerHeuristicExperiment extends SimulationExperiment {
	public static final int HOSTS_TO_CREATE = 100;

    /**
	 * Simulated Annealing (SA) parameters.
	 */
	public static final double SA_INITIAL_TEMPERATURE = 1.0;
	public static final double SA_COLD_TEMPERATURE = 0.0001;
	public static final double SA_COOLING_RATE = 0.003;
	public static final int    SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES = 50;


    /**
     * Sets the array with Number of PEs for each VM to create.
     * The length of the array defines the number of VMs to create.
     * @param vmPesArray VMs PEs array to set
     */
    private int vmPesArray[];

    /**
     * Sets the array with Number of PEs for each Cloudlet to create.
     * The length of the array defines the number of Cloudlets to create.
     * @param cloudletPesArray Cloudlets PEs array to set
     */
    private int cloudletPesArray[];

    /**
     * Pseudo random number generator used in the experiment.
     */
    private UniformDistr randomGen;

	private CloudletToVmMappingSimulatedAnnealing heuristic;

	/**
     * Instantiates the simulation experiment.
     *
	 * @param runner the runner that will be in charge to setup and run the experiment
     * @param index a number the identifies the current experiment being run
     */
    public DatacenterBrokerHeuristicExperiment(DatacenterBrokerHeuristicRunner runner, int index) {
	    super(index, runner);
	    this.randomGen = new UniformDistr(0, 1);
    }

    @Override
    protected void buildScenario() {
        createSimulatedAnnealingHeuristic();
        super.buildScenario();
    }

    private void createSimulatedAnnealingHeuristic() {
		heuristic = new CloudletToVmMappingSimulatedAnnealing(SA_INITIAL_TEMPERATURE, randomGen);
		heuristic.setColdTemperature(SA_COLD_TEMPERATURE);
		heuristic.setCoolingRate(SA_COOLING_RATE);
		heuristic.setNumberOfNeighborhoodSearchesByIteration(SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);
	}

	@Override
	protected DatacenterBrokerHeuristic createBroker() {
		return new DatacenterBrokerHeuristic(getCloudsim()).setHeuristic(heuristic);
	}

    @Override
    protected void createCloudlets(DatacenterBroker broker) {
        for (int pes: cloudletPesArray) {
            addNewCloudletToList(getCloudletSupplier(broker, pes));
        }
    }

    /**
     * Gets a {@link Supplier} function that is able to create a Cloudlet.
     *
     * @param broker broker that the Cloudlet to be created by the Supplier function will belong to
     * @param cloudletPes number of PEs for the Cloudlet to be created by the Supplier function
     * @return the Supplier function that can create a Cloudlet when requested
     */
    private Supplier<Cloudlet> getCloudletSupplier(DatacenterBroker broker, int cloudletPes) {
        return () -> {
            long length = 400000; //in Million Instructions (MI)
            long fileSize = 300; //Size (in bytes) before execution
            long outputSize = 300; //Size (in bytes) after execution
            //Defines how CPU, RAM and Bandwidth resources are used
            //Sets the same utilization model for all these resources.
            UtilizationModel utilization = new UtilizationModelFull();
            return new CloudletSimple(getNumberOfCreatedCloudlets(), length, cloudletPes)
                .setCloudletFileSize(fileSize)
                .setCloudletOutputSize(outputSize)
                .setUtilizationModel(utilization)
                .setBroker(broker);

        };
    }

    @Override
    protected void createVms(DatacenterBroker broker) {
        setVmList(new ArrayList<>(vmPesArray.length));
        for (int pes: vmPesArray) {
            addNewVmToList(getVmSupplier(broker, pes));
        }

    }

    /**
     * Gets a {@link Supplier} function that is able to create a Vm.
     *
     * @param broker broker that the Vm to be created by the Supplier function will belong to
     * @param vmPes number of PEs for the Vm to be created by the Supplier function
     * @return the Supplier function that can create a Vm when requested
     */
    private Supplier<Vm> getVmSupplier(DatacenterBroker broker, int vmPes) {
        return () -> {
            double mips = 1000;
            long storage = 10000; // vm image size (MB)
            int ram = 512; // vm memory (MB)
            long bw = 1000; // vm bandwidth
            return new VmSimple(getNumberOfCreatedVms(), mips, vmPes)
                .setRam(ram).setBw(bw).setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerTimeShared())
                .setBroker(broker);
        };
    }

    @Override
    protected void createHosts()  {
        for (int i = 0; i < HOSTS_TO_CREATE; i++) {
            addNewHostToList(this::createHost);
        }
    }

    private Host createHost() {
        int mips = 1000;
        int ram = 2048; // MB
        long storage = 1000000;
        long bw = 10000;
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            peList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }

        return new HostSimple(getNumberOfCreatedHosts(), storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    @Override
    public void printResults() {
		System.out.printf(
			"Experiment %d > Heuristic solution for mapping cloudlets to Vm's: ",  getIndex());
		System.out.printf("cost %.2f fitness %.6f\n",
	        heuristic.getBestSolutionSoFar().getCost(),
	        heuristic.getBestSolutionSoFar().getFitness());
    }

    /**
     * The heuristic used to solve the mapping between cloudlets and Vm's.
     */
    public CloudletToVmMappingSimulatedAnnealing getHeuristic() {
        return heuristic;
    }

    /**
     * Sets the pseudo random number generator (PRNG) used internally in the experiment
     * by the {@link CloudletToVmMappingSimulatedAnnealing} to try finding a suboptimal solution
     * for mapping Cloudlets to VMs.
     *
     * @param randomGen the PRNG to set
     */
    public DatacenterBrokerHeuristicExperiment setRandomGen(UniformDistr randomGen) {
        this.randomGen = randomGen;
        return this;
    }

    public DatacenterBrokerHeuristicExperiment setVmPesArray(int[] vmPesArray) {
        this.vmPesArray = vmPesArray;
        return this;
    }

    public DatacenterBrokerHeuristicExperiment setCloudletPesArray(int[] cloudletPesArray) {
        this.cloudletPesArray = cloudletPesArray;
        return this;
    }
}
