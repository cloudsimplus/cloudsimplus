package org.cloudsimplus.experiments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.heuristics.CloudletToVmMappingHeuristic;
import org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing;
import org.cloudsimplus.heuristics.CloudletToVmMappingSolution;
import org.cloudsimplus.heuristics.HeuristicSolution;

/**
 * <p>An example that uses a
 * <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated Annealing</a>
 * heuristic to find a suboptimal mapping between Cloudlets and Vm's submitted to a
 * DatacenterBroker. The number of {@link Pe}s of Vm's and Cloudlets are defined
 * randomly.
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
public class DatacenterBrokerHeuristicExperiment {
	private static final String VMM = "Xen";
	public static final int HOSTS_TO_CREATE = 100;

	/**
	 * Simulated Annealing (SA) parameters.
	 */
	public static final int    SA_INITIAL_TEMPERATURE = 1;
	public static final double SA_COLD_TEMPERATURE = 0.00001;
	public static final double SA_COOLING_RATE = 0.003;
	public static final int    SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES = 100;

	private final List<Cloudlet> cloudletList;
	private List<Vm> vmList;
	private double secondsToRunExperiment;

    /**
     * Number of PEs for each created VM. The length of the array defines
     * the number of Vm's to create.
     */
    private int vmsPes[];

    /**
     * Number of PEs for each created Cloudlet. The length of the array defines
     * the number of Cloudlets to create.
     */
    private int cloudletsPes[];

    /**
     * The heuristic used to solve the mapping between cloudlets and Vm's.
     */
    private CloudletToVmMappingSimulatedAnnealing heuristic;

    /**
     * Number of cloudlets created so far.
     */
    private int numberOfCreatedCloudlets = 0;

    /**
     * Number of VMs created so far.
     */
    private int numberOfCreatedVms = 0;

    /**
     * Number of hosts created so far.
     */
    private int numberOfCreatedHosts = 0;

    /**
     * The pseudo random number generator used in the experiment.
     */
    private final UniformDistr randomGen;

    /**
     * A number that identifies the experiment being run.
     */
    private final int experimentIndex;

    /**
     * Indicates if simulation results have to be output or not.
     */
    private boolean verbose;

    /**
     * Instantiates the simulation experiment.
     *
     * @param randomGen pseudo random number generator used in the experiment
     * @param experimentIndex a number the identifies the current experiment being run
     */
    public DatacenterBrokerHeuristicExperiment(UniformDistr randomGen, int experimentIndex) {
        this.experimentIndex = experimentIndex;
        this.randomGen = randomGen;
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
    }

    /**
     * Build the simulation scenario and starts simulation.
     *
     * @return the final solution
     * @throws RuntimeException
     */
    public final CloudletToVmMappingSolution start() throws RuntimeException {
        buildScenario();
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
	    heuristic.solve();
        if (verbose) {
            printSolution(
                String.format(
                        "Run %d > Heuristic solution for mapping cloudlets to Vm's",
                        experimentIndex),
                heuristic.getBestSolutionSoFar());
        }

        return heuristic.getBestSolutionSoFar();
    }

    private void buildScenario() {
        int numberOfCloudUsers = 1;
        boolean traceEvents = false;

        CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents);
        Datacenter datacenter0 = createDatacenter("Datacenter0");
        DatacenterBrokerHeuristic broker0 = createBroker();
        createAndSubmitVms(broker0);
        createAndSubmitCloudlets(broker0);
    }

    private DatacenterBrokerHeuristic createBroker() {
        heuristic = new CloudletToVmMappingSimulatedAnnealing(SA_INITIAL_TEMPERATURE, this.randomGen);
        heuristic.setColdTemperature(SA_COLD_TEMPERATURE);
        heuristic.setCoolingRate(SA_COOLING_RATE);
        heuristic.setNumberOfNeighborhoodSearchesByIteration(SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);
        DatacenterBrokerHeuristic broker0 = new DatacenterBrokerHeuristic("Broker0");
        broker0.setHeuristic(heuristic);
        return broker0;
    }

    private void createAndSubmitCloudlets(DatacenterBrokerHeuristic broker0) {
        for (int i = 0; i < cloudletsPes.length; i++) {
            cloudletList.add(createCloudlet(broker0, cloudletsPes[i]));
        }
        broker0.submitCloudletList(cloudletList);
    }

    private void createAndSubmitVms(DatacenterBrokerHeuristic broker0) {
        vmList = new ArrayList<>(vmsPes.length);
        for (int i = 0; i < vmsPes.length; i++) {
            vmList.add(createVm(broker0, vmsPes[i]));
        }
        broker0.submitVmList(vmList);
    }

    private DatacenterSimple createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < HOSTS_TO_CREATE; i++) {
            hostList.add(createHost());
        }
        //Defines the characteristics of the data center
        String arch = "x86"; // system architecture of datacenter hosts
        String os = "Linux"; // operating system of datacenter hosts
        double time_zone = 10.0; // time zone where the datacenter is located
        double cost = 3.0; // the cost of using processing in this datacenter
        double costPerMem = 0.05; // the cost of using memory in this datacenter
        double costPerStorage = 0.001; // the cost of using storage in this datacenter
        double costPerBw = 0.0; // the cost of using bw in this datacenter
        LinkedList<FileStorage> storageList = new LinkedList<>(); // we are not adding SAN devices
        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(arch, os, VMM, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
        return new DatacenterSimple(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
    }

    private Host createHost() {
        int mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 10000;
        List<Pe> cpuCoresList = new ArrayList<>();
        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        for (int i = 0; i < 8; i++) {
            cpuCoresList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }
        return new HostSimple(numberOfCreatedHosts++, new ResourceProvisionerSimple<>(new Ram(ram)), new ResourceProvisionerSimple<>(new Bandwidth(bw)), storage, cpuCoresList, new VmSchedulerTimeShared(cpuCoresList));
    }

    private Vm createVm(DatacenterBroker broker, int pesNumber) {
        double mips = 1000;
        long storage = 10000; // vm image size (MB)
        int ram = 512; // vm memory (MB)
        long bw = 1000; // vm bandwidth
        return new VmSimple(numberOfCreatedVms++, broker.getId(), mips, pesNumber, ram, bw, storage, VMM, new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, int numberOfPes) {
        long length = 400000; //in Million Structions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();
        Cloudlet cloudlet = new CloudletSimple(numberOfCreatedCloudlets++, length, numberOfPes, fileSize, outputSize, utilization, utilization, utilization);
        cloudlet.setUserId(broker.getId());
        return cloudlet;
    }

    private void printSolution(String title, CloudletToVmMappingSolution solution) {
        System.out.printf("%s: cost %.2f fitness %.6f\n", title, solution.getCost(), solution.getFitness());
    }

    public CloudletToVmMappingSimulatedAnnealing getHeuristic() {
        return heuristic;
    }

    public List<Cloudlet> getCloudletList() {
        return cloudletList;
    }

    public List<Vm> getVmList() {
        return vmList;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setVmsPes(int[] vmsPes) {
        this.vmsPes = vmsPes;
    }

    public void setCloudlestPes(int[] cloudlestPes) {
        this.cloudletsPes = cloudlestPes;
    }

	/**
	 * The number of seconds the experijment took to run.
	 */
	public double getSecondsToRunExperiment() {
		return secondsToRunExperiment;
	}
}
