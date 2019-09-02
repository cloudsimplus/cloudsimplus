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
 * @author Humaira Abdul Salam
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.3.8
 */
public class DatacenterBrokersMappingComparison {
    /**
     * Simulated Annealing (SA) parameters.
     */
    public static final double SA_INITIAL_TEMPERATURE = 1.0;
    public static final double SA_COLD_TEMPERATURE = 0.0001;
    public static final double SA_COOLING_RATE = 0.003;
    public static final int    SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES = 50;

    private static final int HOSTS_TO_CREATE = 100;
    private static final int VMS_TO_CREATE = 50;
    private static final int CLOUDLETS_TO_CREATE = 100;
    private final Simulation simulation;
    private final List<Cloudlet> cloudletList;
    private final ContinuousDistribution random;
    private final List<Vm> vmList;
    private DatacenterBroker broker;

    /**
     * Starts the simulation.
     *
     * @param args
     */
    public static void main(String[] args) {
        //Enables just some level of log messages.
        Log.setLevel(Level.WARN);

        System.out.println("Starting comparison...");

        final long seed = System.currentTimeMillis();
        final boolean verbose = false;

        // Heuristic
        final CloudSim simulation0 = new CloudSim();
        final UniformDistr random0 = new UniformDistr(0, 1, seed);
        final DatacenterBrokerHeuristic broker0 = createHeuristicBroker(simulation0, random0);
        new DatacenterBrokersMappingComparison(broker0, random0, verbose);

        // BestFit
        final CloudSim simulation1 = new CloudSim();
        final UniformDistr random1 = new UniformDistr(0, 1, seed);
        final DatacenterBroker broker1 = new DatacenterBrokerBestFit(simulation1);
        new DatacenterBrokersMappingComparison(broker1, random1, verbose);

        // Simple - RoundRobin
        final CloudSim simulation2 = new CloudSim();
        final UniformDistr random2 = new UniformDistr(0, 1, seed);
        final DatacenterBroker broker2 = new DatacenterBrokerSimple(simulation2);
        new DatacenterBrokersMappingComparison(broker2, random2, verbose);

        System.out.println("Comparison finished!");
    }

    /**
     * Default constructor where the simulation is built.
     */
    private DatacenterBrokersMappingComparison(final DatacenterBroker broker, final ContinuousDistribution random, final boolean verbose) {
        this.broker = broker;
        this.simulation = broker.getSimulation();
        this.random = random;

        final Datacenter datacenter = createDatacenter(simulation);

        vmList = createVms(random);
        cloudletList = createCloudlets(random);
        broker.submitVmList(vmList);
        broker.submitCloudletList(cloudletList);

        simulation.start();

        // print simulation results
        if (verbose) {
            final List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();
            finishedCloudlets.sort(Comparator.comparingLong(Cloudlet::getId));
            new CloudletsTableBuilder(finishedCloudlets).build();
        }

        print(verbose);
    }

    private static DatacenterBrokerHeuristic createHeuristicBroker(final CloudSim sim, final ContinuousDistribution rand) {
        CloudletToVmMappingSimulatedAnnealing heuristic = createSimulatedAnnealingHeuristic(rand);
        final DatacenterBrokerHeuristic broker = new DatacenterBrokerHeuristic(sim);
        broker.setHeuristic(heuristic);
        return broker;
    }

    private static CloudletToVmMappingSimulatedAnnealing createSimulatedAnnealingHeuristic(final ContinuousDistribution rand) {
        CloudletToVmMappingSimulatedAnnealing heuristic =
            new CloudletToVmMappingSimulatedAnnealing(SA_INITIAL_TEMPERATURE, rand);
        heuristic.setColdTemperature(SA_COLD_TEMPERATURE);
        heuristic.setCoolingRate(SA_COOLING_RATE);
        heuristic.setNeighborhoodSearchesByIteration(SA_NUMBER_OF_NEIGHBORHOOD_SEARCHES);
        return heuristic;
    }

    private List<Cloudlet> createCloudlets(final ContinuousDistribution rand) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS_TO_CREATE);
        for (int i = 0; i < CLOUDLETS_TO_CREATE; i++) {
            list.add(createCloudlet(i, getRandomPesNumber(4, rand)));
        }

        return list;
    }

    private List<Vm> createVms(final ContinuousDistribution random) {
        final List<Vm> list = new ArrayList<>(VMS_TO_CREATE);
        for (int i = 0; i < VMS_TO_CREATE; i++) {
            list.add(createVm(i, getRandomPesNumber(4, random)));
        }

        return list;
    }

    private void print(final boolean verbose) {
        final double brokersMappingCost = computeBrokersMappingCost(verbose);
        System.out.printf(
            "The solution based on %s mapper costs %.2f.%n", broker.getClass().getSimpleName(), brokersMappingCost);
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
        return (int) (uniform >= 1 ? uniform % maxPesNumber : uniform * maxPesNumber) + 1;
    }

    private DatacenterSimple createDatacenter(final Simulation sim) {
        final List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < HOSTS_TO_CREATE; i++) {
            hostList.add(createHost());
        }

        return new DatacenterSimple(sim, hostList, new VmAllocationPolicySimple());
    }

    private Host createHost() {
        final long mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        final int ram = 2048; // host memory (Megabyte)
        final long storage = 1000000; // host storage
        final long bw = 10000;

        final List<Pe> peList = new ArrayList<>();
        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        for (int i = 0; i < 8; i++)
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm(final long id, final int pesNumber) {
        final long mips = 1000;
        final long storage = 10000; // vm image size (Megabyte)
        final int ram = 512; // vm memory (Megabyte)
        final long bw = 1000; // vm bandwidth

        return new VmSimple(id, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(storage)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(final long id, final int numberOfPes) {
        final long length = 400000; //in Million Structions (MI)
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution

        //Defines how RAM and Bandwidth resources are used
        final UtilizationModel ramAndBwUtilizationModel = new UtilizationModelDynamic(0.1);

        return new CloudletSimple(id, length, numberOfPes)
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModelCpu(new UtilizationModelFull())
            .setUtilizationModelBw(ramAndBwUtilizationModel)
            .setUtilizationModelRam(ramAndBwUtilizationModel);
    }

    private double computeBrokersMappingCost(final boolean doPrint) {
        CloudletToVmMappingSimulatedAnnealing heuristic =
            new CloudletToVmMappingSimulatedAnnealing(SA_INITIAL_TEMPERATURE, random);

        final CloudletToVmMappingSolution mappingSolution = new CloudletToVmMappingSolution(heuristic);
        int i = 0;
        for (Cloudlet c : cloudletList) {
            if (c.isBoundToVm()) {
                mappingSolution.bindCloudletToVm(c, c.getVm());
            }
        }

        if (doPrint) {
            printSolution(
                "Best fit solution used by DatacenterBrokerSimple class",
                mappingSolution, false);
        }
        return mappingSolution.getCost();
    }

    private void printSolution(
        final String title,
        final CloudletToVmMappingSolution solution,
        final boolean showIndividualCloudletFitness) {
        System.out.printf("%s (cost %.2f fitness %.6f)%n",
            title, solution.getCost(), solution.getFitness());
        if (!showIndividualCloudletFitness)
            return;

        for (Map.Entry<Cloudlet, Vm> e : solution.getResult().entrySet()) {
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
