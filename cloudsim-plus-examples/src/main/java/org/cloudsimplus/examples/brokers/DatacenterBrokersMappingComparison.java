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
import org.cloudbus.cloudsim.brokers.DatacenterBrokerBestFit;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.heuristics.*;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
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
 * <p>A comparison of cloudlet-VM mapping is done among the best fit approach,
 * heuristic approach and round robin mapping.</p>
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

    private final Simulation simulation;
    private final List<Cloudlet> cloudletList;
    private List<Vm> vmList;

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
     * Broker.
     */
    private DatacenterBroker broker;

    ContinuousDistribution random;

    /**
     * Starts the simulation.
     * @param args
     */
    public static void main(String[] args) {
        final long seed = 0;
        final boolean showTables = false;

        // Heuristic
        final CloudSim simulation0 = new CloudSim();
        UniformDistr random0 = new UniformDistr(0, 1, seed);
        final DatacenterBrokerHeuristic broker0 = createHeuristicBroker(simulation0, random0);
        new DatacenterBrokerHeuristicExample(broker0, random0, showTables);

        // BestFit
        final CloudSim simulation1 = new CloudSim();
        UniformDistr random1 = new UniformDistr(0, 1, seed);
        final DatacenterBroker broker1 = new DatacenterBrokerBestFit(simulation1);
        new DatacenterBrokerHeuristicExample(broker1, random1, showTables);

        // Simple - RoundRobin
        final CloudSim simulation2 = new CloudSim();
        UniformDistr random2 = new UniformDistr(0, 1, seed);
        final DatacenterBroker broker2 = new DatacenterBrokerSimple(simulation2);
        new DatacenterBrokerHeuristicExample(broker2, random2, showTables);
    }

    /**
     * Default constructor where the simulation is built.
     */
    public DatacenterBrokerHeuristicExample(final DatacenterBroker brkr, final ContinuousDistribution rand, final boolean showTables) {
        //Enables just some level of log messages.
        Log.setLevel(Level.ERROR);

        System.out.println("Starting " + getClass().getSimpleName());

        broker = brkr;
        simulation = broker.getSimulation();
        random = rand;

        final Datacenter datacenter = createDatacenter(simulation);

        vmList = createVms(random);
        cloudletList = createCloudlets(random);
        broker.submitVmList(vmList);
        broker.submitCloudletList(cloudletList);

        simulation.start();

        // print simulation results
        if (showTables) {
            final List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();
            finishedCloudlets.sort(Comparator.comparingLong(Cloudlet::getId));
            new CloudletsTableBuilder(finishedCloudlets).build();
        }

        print();
    }

    private static DatacenterBrokerHeuristic createHeuristicBroker(final CloudSim sim, final ContinuousDistribution rand) {
        CloudletToVmMappingSimulatedAnnealing heuristic = createSimulatedAnnealingHeuristic(rand);
		final DatacenterBrokerHeuristic broker = new DatacenterBrokerHeuristic(sim);
		broker.setHeuristic(heuristic);
		return broker;
	}

	private List<Cloudlet> createCloudlets(final ContinuousDistribution rand) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS_TO_CREATE);
		for(int i = 0; i < CLOUDLETS_TO_CREATE; i++){
            list.add(createCloudlet(getRandomPesNumber(4, rand)));
		}

        return list;
    }

	private List<Vm> createVms(final ContinuousDistribution random) {
        final List<Vm> list = new ArrayList<>(VMS_TO_CREATE);
		for(int i = 0; i < VMS_TO_CREATE; i++){
            list.add(createVm(getRandomPesNumber(4, random)));
		}

        return list;
	}

	private static CloudletToVmMappingSimulatedAnnealing createSimulatedAnnealingHeuristic(final ContinuousDistribution rand) {
        CloudletToVmMappingSimulatedAnnealing heuristic =
		        new CloudletToVmMappingSimulatedAnnealing(SA_INITIAL_TEMPERATURE, rand);
		heuristic.setColdTemperature(SA_COLD_TEMPERATURE);
		heuristic.setCoolingRate(SA_COOLING_RATE);
		heuristic.setNeighborhoodSearchesByIteration(SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);
		return heuristic;
	}

	private void print() {
        final double brokersMappingCost = computeBrokersMappingCost(false);
        final double basicRoundRobinCost = computeRoundRobinMappingCost(false);
        System.out.printf(
            "The solution based on %s mapper costs %.2f. Basic round robin implementation in this example costs %.2f.\n", broker.getClass().getSimpleName(), brokersMappingCost, basicRoundRobinCost);
        System.out.println(getClass().getSimpleName() + " finished!");
	}

	/**
     * Randomly gets a number of PEs (CPU cores).
     *
     * @param maxPesNumber the maximum value to get a random number of PEs
     * @return the randomly generated PEs number
     */
    private int getRandomPesNumber(final int maxPesNumber, final ContinuousDistribution random) {
        final double uniform = random.sample();

        /*always get an index between [0 and size[,
        regardless if the random number generator returns
        values between [0 and 1[ or >= 1*/
        return (int)(uniform >= 1 ? uniform % maxPesNumber : uniform * maxPesNumber) + 1;
    }

    private DatacenterSimple createDatacenter(final Simulation sim) {
        final List<Host> hostList = new ArrayList<>();
        for(int i = 0; i < HOSTS_TO_CREATE; i++) {
            hostList.add(createHost());
        }

        return new DatacenterSimple(sim, hostList, new VmAllocationPolicySimple());
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

        createdHosts++;
        return new HostSimple(ram, bw, storage, peList)
                   .setRamProvisioner(new ResourceProvisionerSimple())
                   .setBwProvisioner(new ResourceProvisionerSimple())
                   .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm(final int pesNumber) {
        final long mips = 1000;
        final long   storage = 10000; // vm image size (Megabyte)
        final int    ram = 512; // vm memory (Megabyte)
        final long   bw = 1000; // vm bandwidth

        return new VmSimple(createdVms++, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(storage)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(final int numberOfPes) {
        final long length = 400000; //in Million Structions (MI)
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        final UtilizationModel utilization = new UtilizationModelFull();

        return new CloudletSimple(createdCloudlets++, length, numberOfPes)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModel(utilization);
    }

    private double computeRoundRobinMappingCost(boolean doPrint) {
        CloudletToVmMappingSimulatedAnnealing heuristic =
            new CloudletToVmMappingSimulatedAnnealing(SA_INITIAL_TEMPERATURE, random);
        final CloudletToVmMappingSolution roundRobinSolution = new CloudletToVmMappingSolution(heuristic);
        int i = 0;
        for (Cloudlet c : cloudletList) {
            //cyclically selects a Vm (as in a circular queue)
            roundRobinSolution.bindCloudletToVm(c, vmList.get(i));
            i = (i+1) % vmList.size();
        }

        if (doPrint) {
            printSolution(
                "Round robin solution used by DatacenterBrokerSimple class",
                roundRobinSolution, false);
        }
        return roundRobinSolution.getCost();
    }

    private double computeBrokersMappingCost(boolean doPrint) {
        CloudletToVmMappingSimulatedAnnealing heuristic =
            new CloudletToVmMappingSimulatedAnnealing(SA_INITIAL_TEMPERATURE, random);

        final CloudletToVmMappingSolution bestFitSolution = new CloudletToVmMappingSolution(heuristic);
        int i = 0;
        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm()) {
                bestFitSolution.bindCloudletToVm(c, c.getVm());
            }
        }

        if (doPrint) {
            printSolution(
                "Best fit solution used by DatacenterBrokerSimple class",
                bestFitSolution, false);
        }
        return bestFitSolution.getCost();
    }

    private void printSolution(
        final String title,
        final CloudletToVmMappingSolution solution,
        final boolean showIndividualCloudletFitness)
    {
        System.out.printf("%s (cost %.2f fitness %.6f)\n",
                title, solution.getCost(), solution.getFitness());
        if(!showIndividualCloudletFitness)
            return;

        for(Map.Entry<Cloudlet, Vm> e: solution.getResult().entrySet()){
            System.out.printf(
                "Cloudlet %3d (%d PEs, %6d MI) mapped to Vm %3d (%d PEs, %6.0f MIPS)\n",
                e.getKey().getId(),
                e.getKey().getNumberOfPes(), e.getKey().getLength(),
                e.getValue().getId(),
                e.getValue().getNumberOfPes(), e.getValue().getMips());
        }

        System.out.println();
    }

}
