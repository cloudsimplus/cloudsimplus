/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.examples.brokers;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.heuristics.CloudletToVmMappingHeuristic;
import org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing;
import org.cloudsimplus.heuristics.CloudletToVmMappingSolution;
import org.cloudsimplus.heuristics.HeuristicSolution;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An example that uses a
 * <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated Annealing</a>
 * heuristic to find a suboptimal mapping between Cloudlets and Vm's submitted to a
 * DatacenterBroker. The number of {@link Pe}s of Vm's and Cloudlets are defined
 * randomly.
 *
 * <p>The {@link DatacenterBrokerHeuristic} is used
 * with the {@link CloudletToVmMappingSimulatedAnnealing} class
 * in order to find an acceptable solution with a high
 * {@link HeuristicSolution#getFitness() fitness value}.</p>
 *
 * <p>Different {@link CloudletToVmMappingHeuristic} implementations can be used
 * with the {@link DatacenterBrokerHeuristic} class.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class DatacenterBrokerHeuristicExample {
    private static final int HOSTS_TO_CREATE = 100;
    private static final int VMS_TO_CREATE = 50;
    private static final int CLOUDLETS_TO_CREATE = 100;

    /**
     * Simulated Annealing (SA) parameters.
     */
    public static final double SA_INITIAL_TEMPERATURE = 1.0;
    public static final double SA_COLD_TEMPERATURE = 0.0001;
    public static final double SA_COOLING_RATE = 0.003;
    public static final int    SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES = 50;

    private final CloudSim simulation;
    private final List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private CloudletToVmMappingSimulatedAnnealing heuristic;

    /**
     * Number of cloudlets created so far.
     */
    private int createdCloudlets = 0;
    /**
     * Number of VMs created so far.
     */
    private int createdVms = 0;
    /**
     * Number of hosts created so far.
     */
    private int createdHosts = 0;

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
    private DatacenterBrokerHeuristicExample() {
        //Enables just some level of log messages.
        Log.setLevel(Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();

        simulation = new CloudSim();

        final Datacenter datacenter0 = createDatacenter();

        DatacenterBrokerHeuristic broker0 = createBroker();

        createAndSubmitVms(broker0);
        createAndSubmitCloudlets(broker0);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

        print(broker0);
    }

	private DatacenterBrokerHeuristic createBroker() {
		createSimulatedAnnealingHeuristic();
		final DatacenterBrokerHeuristic broker0 = new DatacenterBrokerHeuristic(simulation);
		broker0.setHeuristic(heuristic);
		return broker0;
	}

	private void createAndSubmitCloudlets(final DatacenterBrokerHeuristic broker0) {
		for(int i = 0; i < CLOUDLETS_TO_CREATE; i++){
		    cloudletList.add(createCloudlet(broker0, getRandomPesNumber(4)));
		}
		broker0.submitCloudletList(cloudletList);
	}

	private void createAndSubmitVms(final DatacenterBrokerHeuristic broker0) {
		vmList = new ArrayList<>(VMS_TO_CREATE);
		for(int i = 0; i < VMS_TO_CREATE; i++){
		    vmList.add(createVm(broker0, getRandomPesNumber(4)));
		}
		broker0.submitVmList(vmList);
	}

	private void createSimulatedAnnealingHeuristic() {
		heuristic =
		        new CloudletToVmMappingSimulatedAnnealing(SA_INITIAL_TEMPERATURE, new UniformDistr(0, 1));
		heuristic.setColdTemperature(SA_COLD_TEMPERATURE);
		heuristic.setCoolingRate(SA_COOLING_RATE);
		heuristic.setNeighborhoodSearchesByIteration(SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);
	}

	private void print(final DatacenterBrokerHeuristic broker0) {
        final double roundRobinMappingCost = computeRoundRobinMappingCost();
		printSolution(
		        "Heuristic solution for mapping cloudlets to Vm's         ",
		        heuristic.getBestSolutionSoFar(), false);

		System.out.printf(
		    "\tThe heuristic solution cost represents %.2f%% of the round robin mapping cost used by the DatacenterBrokerSimple%n",
		    heuristic.getBestSolutionSoFar().getCost()*100.0/roundRobinMappingCost);
		System.out.printf("\tThe solution finding spend %.2f seconds to finish%n", broker0.getHeuristic().getSolveTime());
		System.out.println("\tSimulated Annealing Parameters");
        System.out.printf("\t\tNeighborhood searches by iteration: %d%n", SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);
        System.out.printf("\t\tInitial Temperature: %18.6f%n", SA_INITIAL_TEMPERATURE);
        System.out.printf("\t\tCooling Rate       : %18.6f%n", SA_COOLING_RATE);
        System.out.printf("\t\tCold Temperature   : %18.6f%n%n", SA_COLD_TEMPERATURE);
        System.out.println(getClass().getSimpleName() + " finished!");
	}

	/**
     * Randomly gets a number of PEs (CPU cores).
     *
     * @param maxPesNumber the maximum value to get a random number of PEs
     * @return the randomly generated PEs number
     */
    private int getRandomPesNumber(final int maxPesNumber) {
        return heuristic.getRandomValue(maxPesNumber)+1;
    }

    private DatacenterSimple createDatacenter() {
        final List<Host> hostList = new ArrayList<>();
        for(int i = 0; i < HOSTS_TO_CREATE; i++) {
            hostList.add(createHost());
        }

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private Host createHost() {
        final long mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        final int  ram = 2048; // host memory (Megabyte)
        final long storage = 1000000; // host storage
        final long bw = 10000;

        final List<Pe> peList = new ArrayList<>();
        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        for(int i = 0; i < 8; i++)
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));

       return new HostSimple(ram, bw, storage, peList)
                   .setRamProvisioner(new ResourceProvisionerSimple())
                   .setBwProvisioner(new ResourceProvisionerSimple())
                   .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm(final DatacenterBroker broker, final int pesNumber) {
        final long mips = 1000;
        final long   storage = 10000; // vm image size (Megabyte)
        final int    ram = 512; // vm memory (Megabyte)
        final long   bw = 1000; // vm bandwidth

        return new VmSimple(createdVms++, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(storage)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(final DatacenterBroker broker, final int numberOfPes) {
        final long length = 400000; //in Million Structions (MI)
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution

        final UtilizationModel utilizationFull = new UtilizationModelFull();
        final UtilizationModel utilizationDynamic = new UtilizationModelDynamic(0.1);

        return new CloudletSimple(createdCloudlets++, length, numberOfPes)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModelCpu(utilizationFull)
                    .setUtilizationModelRam(utilizationDynamic)
                    .setUtilizationModelBw(utilizationDynamic);
    }

    private double computeRoundRobinMappingCost() {
        final CloudletToVmMappingSolution roundRobinSolution = new CloudletToVmMappingSolution(heuristic);
        int i = 0;
        for (Cloudlet c : cloudletList) {
            //cyclically selects a Vm (as in a circular queue)
            roundRobinSolution.bindCloudletToVm(c, vmList.get(i));
            i = (i+1) % vmList.size();
        }

        printSolution(
            "Round robin solution used by DatacenterBrokerSimple class",
            roundRobinSolution, false);
        return roundRobinSolution.getCost();
    }

    private void printSolution(
        final String title,
        final CloudletToVmMappingSolution solution,
        final boolean showIndividualCloudletFitness)
    {
        System.out.printf("%n%s (cost %.2f fitness %.6f)%n",
                title, solution.getCost(), solution.getFitness());
        if(!showIndividualCloudletFitness)
            return;

        for(Map.Entry<Cloudlet, Vm> e: solution.getResult().entrySet()){
            System.out.printf(
                "Cloudlet %3d (%d PEs, %6d MI) mapped to Vm %3d (%d PEs, %6.0f MIPS)%n",
                e.getKey().getId(),
                e.getKey().getNumberOfPes(), e.getKey().getLength(),
                e.getValue().getId(),
                e.getValue().getNumberOfPes(), e.getValue().getMips());
        }

        System.out.println();
    }

}
