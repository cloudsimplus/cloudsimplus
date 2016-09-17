package org.cloudsimplus.experiments;

import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
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
 * DatacenterBroker.
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
public class DatacenterBrokerHeuristicExample {
    /**
     * Virtual Machine Monitor name.
     */
    private static final String VMM = "Xen"; 
    
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private CloudletToVmMappingSimulatedAnnealing heuristic;
    
    private int numberOfCreatedCloudlets = 0;
    private int numberOfCreatedVms = 0;
    private int numberOfCreatedHosts = 0;

    /**
     * Starts the simulation.
     * @param args 
     */
    public static void main(String[] args) {
        new DatacenterBrokerHeuristicExample();
    }

    /**
     * Default constructor where the simulation is built.
     */
    public DatacenterBrokerHeuristicExample() {
        Log.printFormattedLine("Starting %s ...", getClass().getSimpleName());
        try {
            this.vmList = new ArrayList<>();
            this.cloudletList = new ArrayList<>();
            int numberOfCloudUsers = 1; 
            boolean traceEvents = false;
            
            CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents);

            Datacenter datacenter0 = createDatacenter("Datacenter0", 10);

            heuristic = 
                    new CloudletToVmMappingSimulatedAnnealing(10000, new UniformDistr(0, 1));
            heuristic.setColdTemperature(1);
            heuristic.setCoolingRate(0.003);
            
            DatacenterBrokerHeuristic broker0 = new DatacenterBrokerHeuristic("Broker0");
            broker0.setHeuristic(heuristic);

            final int numberOfVms = 5;
            final int numberOfCloudlets = 10;
            vmList = new ArrayList<>(numberOfVms);
            for(int i = 0; i < numberOfVms; i++){
                vmList.add(createVm(broker0));
            }
            broker0.submitVmList(vmList);

            for(int i = 0; i < numberOfCloudlets; i++){
                cloudletList.add(createCloudlet(broker0));
            }
            broker0.submitCloudletList(cloudletList);

            CloudSim.startSimulation();
            CloudSim.stopSimulation();
            printSolution(
                    "\nFinal heuristic solution for mapping cloudlets to Vm's", 
                    heuristic.bestSolutionSoFar());
            computeRoudRobinMappingFitness();

            List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
            CloudletsTableBuilderHelper.print(new TextTableBuilder(), finishedCloudlets);
            Log.printFormattedLine("%s finished!", getClass().getSimpleName());
        } catch (Exception e) {
            Log.printFormattedLine("Unexpected errors happened: %s", e.getMessage());
        }
    }

    private DatacenterSimple createDatacenter(String name, int numberOfHosts) {
        List<Host> hostList = new ArrayList<>();
        for(int i = 0; i < numberOfHosts; i++)
            hostList.add(createHost()); 

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
        for(int i = 0; i < 4; i++)
            cpuCoresList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        
        return new HostSimple(numberOfCreatedHosts++,
                new ResourceProvisionerSimple<>(new Ram(ram)),
                new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                storage, cpuCoresList,
                new VmSchedulerTimeShared(cpuCoresList));
    }

    private Vm createVm(DatacenterBroker broker) {
        double mips = 1000;
        long   storage = 10000; // vm image size (MB)
        int    ram = 512; // vm memory (MB)
        long   bw = 1000; // vm bandwidth 
        int    pesNumber = 4; // number of CPU cores
        
        return new VmSimple(numberOfCreatedVms++, 
                broker.getId(), mips, pesNumber, ram, bw, storage,
                VMM, new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker) {
        long length = 400000; //in Million Structions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        int  numberOfCpuCores = 2;
        
        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();
        
        Cloudlet cloudlet
                = new CloudletSimple(
                        numberOfCreatedCloudlets++, length, numberOfCpuCores, 
                        fileSize, outputSize, 
                        utilization, utilization, utilization);
        cloudlet.setUserId(broker.getId());
        
        return cloudlet;
    }
    
    private void computeRoudRobinMappingFitness() {
        CloudletToVmMappingSolution roudRobinSolution =
                new CloudletToVmMappingSolution(heuristic);
        int i = 0;
        for (Cloudlet c : cloudletList) {
            //cyclically selects a Vm (as in a circular queue)
            i = i % vmList.size(); 
            roudRobinSolution.bindCloudletToVm(c, vmList.get(i++));
        }
        printSolution("Round robin solution used by DatacenterBrokerSimple class:", roudRobinSolution);
    }

    private static void printSolution(String title, CloudletToVmMappingSolution solution) {
        System.out.printf("%s (fitness %.2f)\n", title, solution.getFitness());
        for(Map.Entry<Cloudlet, Vm> e: solution.getResult().entrySet()){
            System.out.printf(
                "Cloudlet %d (%d PEs, %6d MI) mapped to Vm %d (%d PEs, %6.0f MIPS) with fitness %.2f\n",
                e.getKey().getId(),
                e.getKey().getNumberOfPes(), e.getKey().getCloudletLength(),
                e.getValue().getId(),
                e.getValue().getNumberOfPes(), e.getValue().getMips(),
                solution.getFitnessOfCloudletToVm(e.getKey(), e.getValue()));
        }
        System.out.println();
    }    

}
