package org.cloudsimplus.experiments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
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
public class DatacenterBrokerHeuristicRunner {
    private static final int HOSTS_TO_CREATE = 100;
    private static final int VMS_TO_CREATE = 50;
    private static final int CLOUDLETS_TO_CREATE = 100;
    
    /**
     * Number of times the cloud simulation will be executed
     * in order to get values such as means and standard deviations.
     * It has to be an even number due to the use
     * of "Antithetic Variates Technique".
     */
    private static final int NUMBER_OF_SIMULATION_RUNS = 1000;
    
    /**
     * Virtual Machine Monitor name.
     */
    private static final String VMM = "Xen"; 
    
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private CloudletToVmMappingSimulatedAnnealing heuristic;
    
    /**
     * The final solution for a simulation run, representing the mapping
     * among Cloudlets and Vm's.
     */
    private CloudletToVmMappingSolution heuristicSolution;
    
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
    
    private final UniformDistr randomGen;
        
    /**
     * A number that identifies the experiment being run.
     */
    private int experimentIndex;
    
    /**
     * A number generator to randomly generate number of PEs
     * to be used for creation of VMs and Cloudlets.
     */
    private static final UniformDistr randomPesGen = new UniformDistr(0, 4);

    /**
     * Number of PEs for each created VM.
     */
    private static final int VMS_PES[] = createArrayOfPes(VMS_TO_CREATE);
    
    /**
     * Number of PEs for each created Cloudlet.
     */
    private static final int CLOUDLETS_PES[] =  createArrayOfPes(CLOUDLETS_TO_CREATE);
    
    /**
     * A Cloudlet to VM mapping that uses a Round-Robin implementation to
     * cyclically select a VM from the VM list to host a Cloudlet.
     * This is the implementation used by the {@link DatacenterBrokerSimple} class.
     */
    private static CloudletToVmMappingSolution roundRobinSolution = null;
    
    /**
     * Seeds used to run each experiment.
     * The experiments will apply the "Antithetic Variates Technique" to reduce
     * results variance. 
     * See {@link UniformDistr#isApplyAntitheticVariatesTechnique()} for more details.
     */
    private static final long seeds[] = new long[NUMBER_OF_SIMULATION_RUNS];

    /**
     * The cost of each executed experiment.
     */
    private static final double experimentCosts[] = new double[NUMBER_OF_SIMULATION_RUNS];

    /**
     * Starts the simulation.
     * @param args 
     */
    public static void main(String[] args) {
        Log.printFormatted("Starting %d experiments ...\n", NUMBER_OF_SIMULATION_RUNS);
        long startTime = System.currentTimeMillis();
        Log.disable();
        
        
        DatacenterBrokerHeuristicRunner experiment;
        for(int i = 0; i < NUMBER_OF_SIMULATION_RUNS; i++){
            experiment = new DatacenterBrokerHeuristicRunner(i, false);
            experimentCosts[i] = experiment.heuristicSolution.getCost();
        }
        
        DescriptiveStatistics costsStats = computeStatisticsApplyingAntitheticVariatesTechnique();
        
        System.out.printf("# Results for %d simulation runs\n", NUMBER_OF_SIMULATION_RUNS);
        System.out.printf(
            "\tRound-robin solution used by DatacenterBrokerSimple - Cost: %.2f\n", 
            roundRobinSolution.getCost());
        System.out.printf(
                "\tHeuristic solutions - Mean cost: %.2f Std. Dev.: %.2f\n", 
                costsStats.getMean(), costsStats.getStandardDeviation());
        System.out.printf(
            "\tThe mean cost of heuristic solutions represent %.2f%% of the Round-robin mapping used by the DatacenterBrokerSimple\n", 
            costsStats.getMean()*100.0/roundRobinSolution.getCost());
        
        Log.enable();
        long totalExecutionSeconds = (System.currentTimeMillis() - startTime)/1000;
        Log.printFormatted("\nExperiments finished in %d seconds!", totalExecutionSeconds);
        System.out.printf("Used seeds: \n\t");
        for(long seed: seeds){
            System.out.printf("%d ", seed);
        }
    }

    /**
     * Uses the "Antithetic Variates Technique" to compute new solution costs means
     * in order to reduce variance.
     * @return a {@link DescriptiveStatistics} object with costs mean of each
     * experiment after applying the "Antithetic Variates Technique".
     * Using such object, a general mean and standard deviation can be obtained.
     */
    private static DescriptiveStatistics computeStatisticsApplyingAntitheticVariatesTechnique() {
        final int half = halfExperiments();
        double costsMeansAntithetic[] = new double[half];
        for(int i = 0; i < half; i++){
            costsMeansAntithetic[i] = (experimentCosts[i]+experimentCosts[half+i])/2;
        }
        DescriptiveStatistics costsStats = new DescriptiveStatistics(costsMeansAntithetic);
        return costsStats;
    }

    private static int halfExperiments() {
        return NUMBER_OF_SIMULATION_RUNS/2;
    }
    
    /**
     * Builds and starts a simulation scenario.
     * 
     * @param experimentIndex a number the identifies the current experiment being run
     * @param verbose true if it has to be provided some output after the simulation execution,
     * false if not
     */
    public DatacenterBrokerHeuristicRunner(int experimentIndex, boolean verbose) {
        this.experimentIndex = experimentIndex;
        randomGen = createRandomGen();
        try {
            this.vmList = new ArrayList<>();
            this.cloudletList = new ArrayList<>();
            int numberOfCloudUsers = 1; 
            boolean traceEvents = false;
            seeds[experimentIndex] = randomGen.getSeed();

            CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents);
            Datacenter datacenter0 = createDatacenter("Datacenter0");
            DatacenterBrokerHeuristic broker0 = createBroker();
            createAndSubmitVms(broker0);
            createAndSubmitCloudlets(broker0);

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            if(roundRobinSolution == null) {
                roundRobinSolution = createRoundRobinSolution();
            }

            heuristicSolution = heuristic.getBestSolutionSoFar();
            if(verbose){
                printSolution(
                    String.format(
                        "Run %d > Heuristic solution for mapping cloudlets to Vm's",
                        experimentIndex), 
                    heuristicSolution);
            }
        } catch (Exception e) {
            Log.printFormattedLine("Unexpected errors happened: %s", e.getMessage());
        }
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
    private UniformDistr createRandomGen() {
        UniformDistr rnd;
        if (experimentIndex < halfExperiments()) {
            rnd = new UniformDistr(0, 1);
        } else {
            int previousExperiment = experimentIndex - halfExperiments();
            rnd = new UniformDistr(0, 1, seeds[previousExperiment]);
            rnd.setApplyAntitheticVariatesTechnique(true);
        }
        
        return rnd;
    }

    private DatacenterBrokerHeuristic createBroker() {
        heuristic =
                new CloudletToVmMappingSimulatedAnnealing(1, this.randomGen);
        heuristic.setColdTemperature(0.00001);
        heuristic.setCoolingRate(0.003);
        heuristic.setNumberOfNeighborhoodSearchsByIteration(100);
        DatacenterBrokerHeuristic broker0 = new DatacenterBrokerHeuristic("Broker0");
        broker0.setHeuristic(heuristic);
        return broker0;
    }

    private void createAndSubmitCloudlets(DatacenterBrokerHeuristic broker0) {
        for(int i = 0; i < CLOUDLETS_TO_CREATE; i++){
            cloudletList.add(createCloudlet(broker0, CLOUDLETS_PES[i]));
        }
        broker0.submitCloudletList(cloudletList);
    }

    private void createAndSubmitVms(DatacenterBrokerHeuristic broker0) {
        vmList = new ArrayList<>(VMS_TO_CREATE);
        for(int i = 0; i < VMS_TO_CREATE; i++){
            vmList.add(createVm(broker0, VMS_PES[i]));
        }
        broker0.submitVmList(vmList);
    }

    /**
     * Creates an array with a list of PEs created randomly to be used
     * to defined the number of PEs of VMs or Cloudlets.
     * 
     * @return the created array with PEs numbers
     */
    private static int[] createArrayOfPes(int arraySize) {
        int array[] = new int[arraySize];
        for(int i = 0; i < arraySize; i++){
            array[i] = (int)randomPesGen.sample()+1;
        }
        
        return array;
    }

    private DatacenterSimple createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        for(int i = 0; i < HOSTS_TO_CREATE; i++) {
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

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(
                arch, os, VMM, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        return new DatacenterSimple(name, characteristics, 
                new VmAllocationPolicySimple(hostList), storageList, 0);
    }    

    private Host createHost() {
        int  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        int  ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 10000;
        
        List<Pe> cpuCoresList = new ArrayList<>();
        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        for(int i = 0; i < 8; i++)
            cpuCoresList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        
        return new HostSimple(numberOfCreatedHosts++,
                new ResourceProvisionerSimple<>(new Ram(ram)),
                new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                storage, cpuCoresList,
                new VmSchedulerTimeShared(cpuCoresList));
    }

    private Vm createVm(DatacenterBroker broker, int pesNumber) {
        double mips = 1000;
        long   storage = 10000; // vm image size (MB)
        int    ram = 512; // vm memory (MB)
        long   bw = 1000; // vm bandwidth 
        
        return new VmSimple(numberOfCreatedVms++, 
                broker.getId(), mips, pesNumber, ram, bw, storage,
                VMM, new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, int numberOfPes) {
        long length = 400000; //in Million Structions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        
        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();
        
        Cloudlet cloudlet
                = new CloudletSimple(
                        numberOfCreatedCloudlets++, length, numberOfPes, 
                        fileSize, outputSize, 
                        utilization, utilization, utilization);
        cloudlet.setUserId(broker.getId());
        
        return cloudlet;
    }
    
    private CloudletToVmMappingSolution createRoundRobinSolution() {
        CloudletToVmMappingSolution solution =
                new CloudletToVmMappingSolution(heuristic);
        int i = 0;
        for (Cloudlet c : cloudletList) {
            //cyclically selects a Vm (as in a circular queue)
            solution.bindCloudletToVm(c, vmList.get(i));
            i = (i+1) % vmList.size(); 
        }

        return solution;
    }

    private static void printSolution(String title, CloudletToVmMappingSolution solution) {
        System.out.printf(
                "%s: cost %.2f fitness %.6f\n", 
                title, solution.getCost(), solution.getFitness());
    }

}
