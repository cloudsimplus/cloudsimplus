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
package org.cloudsimplus.testbeds.heuristics;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
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
import org.cloudsimplus.heuristics.CloudletToVmMappingHeuristic;
import org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing;
import org.cloudsimplus.heuristics.HeuristicSolution;
import org.cloudsimplus.testbeds.Experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * An experiment that uses a
 * <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated
 * Annealing</a>
 * heuristic to find a suboptimal mapping between Cloudlets and Vm's submitted
 * to a DatacenterBroker. The number of {@link Pe}s of Vm's and Cloudlets are
 * defined randomly by the {@link DatacenterBrokerHeuristicRunner} that
 * instantiates and runs several of this experiment and collect statistics from
 * the results.
 *
 * The {@link DatacenterBrokerHeuristic} is used with the
 * {@link CloudletToVmMappingSimulatedAnnealing} class in order to find an
 * acceptable solution with a high
 * {@link HeuristicSolution#getFitness() fitness value}.</p>
 *
 * <p>
 * Different {@link CloudletToVmMappingHeuristic} implementations can be used
 * with the {@link DatacenterBrokerHeuristic} class.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
final class DatacenterBrokerHeuristicExperiment extends Experiment {
    private static final int HOSTS = 100;

    /**
     * Simulated Annealing (SA) parameters.
     */
    public static final double SA_INIT_TEMPERATURE = 1.0;
    public static final double SA_COLD_TEMPERATURE = 0.0001;
    public static final double SA_COOLING_RATE = 0.003;
    public static final int SA_NEIGHBORHOOD_SEARCHES = 50;

    /**
     * The array with Number of PEs for each VM to create.
     * The length of the array defines the number of VMs to create.
     */
    private int vmPesArray[];

    /**
     * @see #setCloudletPesArray(int[])
     */
    private int cloudletPesArray[];

    /**
     * Pseudo random number generator used in the experiment.
     */
    private ContinuousDistribution randomGen;

    private CloudletToVmMappingSimulatedAnnealing heuristic;

    /**
     * Instantiates the simulation experiment.
     *
     * @param index a number the identifies the current experiment being run
     * @param runner the runner that will be in charge to setup and run the
     * experiment
     */
    DatacenterBrokerHeuristicExperiment(final int index, final DatacenterBrokerHeuristicRunner runner, final int []vmPesArray) {
        super(index, runner);
        this.vmPesArray = vmPesArray;
        /*The number of VMs to create is equal to the size of the pes array,
        creating one VM with each number of PEs defined in the array*/
        setVmsByBrokerFunction(broker -> vmPesArray.length);
        setHostsNumber(HOSTS);
        this.randomGen = new UniformDistr(0, 1);
        createSimulatedAnnealingHeuristic();
    }

    private void createSimulatedAnnealingHeuristic() {
        heuristic = new CloudletToVmMappingSimulatedAnnealing(SA_INIT_TEMPERATURE, randomGen);
        heuristic.setColdTemperature(SA_COLD_TEMPERATURE);
        heuristic.setCoolingRate(SA_COOLING_RATE);
        heuristic.setNeighborhoodSearchesByIteration(SA_NEIGHBORHOOD_SEARCHES);
    }

    @Override
    protected DatacenterBrokerHeuristic createBroker() {
        return new DatacenterBrokerHeuristic(getSimulation()).setHeuristic(heuristic);
    }

    @Override
    protected Vm createVm(final DatacenterBroker broker, final int id) {
        final int i = (int)(randomGen.sample()*vmPesArray.length);
        final long mips = 1000;
        final long storage = 10000; // vm image size (MB)
        final int ram = 512; // vm memory (MB)
        final long bw = 1000; // vm bandwidth (Mbps)
        return new VmSimple(id, mips, vmPesArray[i])
                .setRam(ram).setBw(bw).setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    @Override
    protected List<Cloudlet> createCloudlets(final DatacenterBroker broker) {
        final List<Cloudlet> list = new ArrayList<>(cloudletPesArray.length);
        for (int id = getCloudletList().size(); id < getCloudletList().size() + cloudletPesArray.length; id++) {
            list.add(createCloudlet(broker));
        }

        return list;
    }

    @Override
    protected Cloudlet createCloudlet(final DatacenterBroker broker) {
        final int i = (int)(randomGen.sample()*cloudletPesArray.length);
        final long length = 400000; //in Million Instructions (MI)
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution

        final UtilizationModel utilizationFull = new UtilizationModelFull();
        final UtilizationModel utilizationDynamic = new UtilizationModelDynamic(0.1);
        return new CloudletSimple(nextCloudletId(), length, cloudletPesArray[i])
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModelRam(utilizationDynamic)
            .setUtilizationModelBw(utilizationDynamic)
            .setUtilizationModelCpu(utilizationFull);
    }

    @Override
    protected Host createHost(final int id) {
        final long mips = 1000;
        final long ram = 2048; // MEGA
        final long storage = 1000000;
        final long bw = 10000;
        final List<Pe> peList = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        final Host host = new HostSimple(ram, bw, storage, peList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
        host.setId(id);
        return host;
    }

    @Override
    public void printResults() {
        System.out.printf(
                "Experiment %d > Heuristic solution for mapping cloudlets to Vm's: ", getIndex());
        System.out.printf("cost %.2f fitness %.6f%n",
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
     * Sets the pseudo random number generator (PRNG) used internally in the
     * experiment by the {@link CloudletToVmMappingSimulatedAnnealing} to try
     * finding a suboptimal solution for mapping Cloudlets to VMs.
     *
     * @param randomGen the PRNG to set
     */
    public DatacenterBrokerHeuristicExperiment setRandomGen(final ContinuousDistribution randomGen) {
        this.randomGen = randomGen;
        return this;
    }

    /**
     * Sets the array with Number of PEs for each Cloudlet to create. The length
     * of the array defines the number of Cloudlets to create.
     *
     * @param cloudletPes Cloudlets PEs array to set
     * @return
     */
    public DatacenterBrokerHeuristicExperiment setCloudletPesArray(int... cloudletPes) {
        this.cloudletPesArray = Arrays.copyOf(cloudletPes, cloudletPes.length);
        return this;
    }
}
