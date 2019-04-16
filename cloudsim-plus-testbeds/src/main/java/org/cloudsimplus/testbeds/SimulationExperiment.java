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
package org.cloudsimplus.testbeds;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A base class to implement simulation experiments.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public abstract class SimulationExperiment implements Runnable {
    private final ExperimentRunner runner;
    private CloudSim simulation;
    private List<DatacenterSimple> datacenterList;
    private List<DatacenterBroker> brokerList;
    private List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final long seed;

    private int datacentersNumber;
    protected int hostsNumber;
    private int brokersNumber;

    private final int index;
    private boolean verbose;

    private Consumer<? extends SimulationExperiment> afterExperimentFinish;
    private Consumer<? extends SimulationExperiment> afterScenarioBuild;

    /**@see #setVmsByBrokerFunction(Function) */
    private Function<DatacenterBroker, Integer> vmsByBrokerFunction;

    /**
     * Creates a simulation experiment that is not linked to a runner,
     * to enable it to execute just one run.
     *
     */
    public SimulationExperiment(final long seed) {
        this(0, null, seed);
    }

    /**
     * Instantiates a simulation experiment with 1 Datacenter by default.
     *
     * @param index the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} that is in charge of executing
     * this experiment a defined number of times and to collect data for
     * statistical analysis.
     * @see #setDatacentersNumber(int)
     */
    public SimulationExperiment(final int index, final ExperimentRunner runner) {
        //the seed will be generate from the Runner base seed
        this(index, runner, -1);
    }

    /**
     * Instantiates a simulation experiment
     * that will create 1 broker and 1 Datacenter by default.
     *
     * @param index the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} to execute the experiment.
     *               If omitted, it means the experiment is independent and may be run just once
     * @param seed the seed to be set. If a runner is given, this value is ignored
     *             and the seed is generated from the runner base seed.
     * @see #setBrokersNumber(int)
     * @see #setDatacentersNumber(int)
     */
    protected SimulationExperiment(final int index, final ExperimentRunner runner, final long seed) {
        if(seed == -1){
            Objects.requireNonNull(runner);
        }
        this.brokersNumber = 1;
        this.datacentersNumber = 1;
        this.verbose = false;
        this.simulation = new CloudSim();
        this.vmList = new ArrayList<>();
        this.index = index;
        this.datacenterList = new ArrayList<>();
        this.brokerList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.runner = runner;

        //Defines an empty Consumer to avoid NullPointerException if an actual one is not set
        afterExperimentFinish = exp -> {};
        afterScenarioBuild = exp -> {};

        this.seed = validateSeed(seed);
    }

    private long validateSeed(long seed) {
        if(runner == null){
            return seed;
        }

        if (runner.isToReuseSeedFromFirstHalfOfExperiments(index)) {
            final int previousExperiment = index - runner.halfSimulationRuns();
            seed = runner.getSeed(previousExperiment);
        } else {
            seed = runner.getBaseSeed() + index;
        }

        runner.addSeed(seed);
        return seed;
    }

    public final List<Cloudlet> getCloudletList() {
        return Collections.unmodifiableList(cloudletList);
    }

    public List<Vm> getVmList() {
        return Collections.unmodifiableList(vmList);
    }

    /**
     * Defines if simulation results of the experiment have to be output or not.
     *
     * @param verbose true if the results have to be output, false otherwise
     * @return
     */
    public SimulationExperiment setVerbose(final boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    /**
     * The index that identifies the current experiment run.
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * Indicates if simulation results of the experiment don't have to be
     * output.
     * @return
     */
    public boolean isNotVerbose() {
        return !verbose;
    }

    /**
     * Indicates if simulation results of the experiment have to be output.
     * @return
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Builds the simulation scenario and starts execution.
     *
     * @throws RuntimeException
     */
    @Override
    public final void run() {
        if(vmsByBrokerFunction == null){
            throw new NullPointerException("You need to set the function that indicates the number of VMs to create for each broker.");
        }

        buildScenario();
        simulation.start();
        getAfterExperimentFinish().accept(this);
        printResultsInternal();
    }

    /**
     * Checks if {@link #isVerbose()} in order to call {@link #printResults()}
     * to print the experiment results.
     *
     * @see #printResults()
     */
    private void printResultsInternal() {
        if (isVerbose()) {
            printResults();
        }
    }

    /**
     * Prints the results for the experiment.
     *
     * The method has to be implemented by subclasses in order to output the
     * experiment results.
     *
     * @see #printResultsInternal()
     */
    public abstract void printResults();

    /**
     * Creates the simulation scenario to run the experiment.
     */
    protected final void buildScenario() {
        createDatacenters();
        createBrokers();

        brokerList.stream().sorted().forEach(b -> {
                createAndSubmitVmsInternal(b);
                createAndSubmitCloudletsInternal(b);
        });

        afterScenarioBuild(this);
    }

    private void createDatacenters() {
        if(datacentersNumber <= 0){
            throw new IllegalStateException("The number of Datacenters to create was not set");
        }

        for (int i = 0; i < datacentersNumber; i++) {
            datacenterList.add(createDatacenter());
        }
    }

    protected DatacenterSimple createDatacenter() {
        final List<Host> hosts = createHosts();
        return new DatacenterSimple(simulation, hosts, new VmAllocationPolicySimple());
    }

    /**
     * Creates a list of brokers.
     * This is the entry-point for broker creation.
     */
    protected void createBrokers() {
        if(brokersNumber <= 0){
            throw new IllegalStateException("The number of brokers to create was not set");
        }

        for (int i = 0; i < brokersNumber; i++) {
            createBrokerAndAddToList();
        }
    }

    /**
     * Creates a DatacenterBroker.
     *
     * @return the created DatacenterBroker
     */
    protected abstract DatacenterBroker createBroker();

    /**
     * Creates a list of Cloudlets to be used by the experiment.
     *
     * @return the list of created cloudlets
     * @param broker the broker to create the Cloudlets to
     */
    protected abstract List<Cloudlet> createCloudlets(DatacenterBroker broker);

    /**
     * Creates the Vms to be used by the experiment.
     *
     * @return the List of created VMs
     * @param broker
     */
    protected List<Vm> createVms(final DatacenterBroker broker) {
        final int numVms = getVmsByBrokerFunction().apply(broker);
        final List<Vm> list = new ArrayList<>(numVms);
        final int id = vmList.size();
        for (int i = 0; i < numVms; i++) {
            Vm vm = createVm(broker);
            list.add(vm);
        }

        return list;
    }

    protected abstract Vm createVm(DatacenterBroker broker);

    protected abstract Cloudlet createCloudlet(DatacenterBroker broker);

    /**
     * Creates a DatacenterBroker and adds it to the
     * {@link #getBrokerList() DatacenterBroker list}.
     *
     * @return the created DatacenterBroker.
     * @see #createBrokers()
     */
    private DatacenterBroker createBrokerAndAddToList() {
        DatacenterBroker broker = createBroker();
        brokerList.add(broker);
        return broker;
    }

    /**
     * Creates all the Cloudlets required by the experiment and submits them to
     * a Broker. This the entry-point for Cloudlets creation.
     *
     * @param broker broker to submit Cloudlets to
     */
    protected void createAndSubmitCloudletsInternal(final DatacenterBroker broker) {
        final List<Cloudlet> list = createCloudlets(broker);
        cloudletList.addAll(list);
        broker.submitCloudletList(list);
    }

    /**
     * Creates all the VMs required by the experiment and submits them to a
     * Broker. This is the entry-point to start creating VMs.
     *
     * @param broker broker to submit VMs to
     */
    private void createAndSubmitVmsInternal(final DatacenterBroker broker) {
        final List<Vm> list = createVms(broker);
        vmList.addAll(list);
        broker.submitVmList(list);
    }

    protected final List<Host> createHosts() {
        if(hostsNumber <= 0){
            throw new IllegalStateException("The number of hosts to create was not set");
        }

        final List<Host> list = new ArrayList<>(hostsNumber);
        for (int i = 0; i < hostsNumber; i++) {
            list.add(createHost(i));
        }

        return list;
    }

    protected abstract Host createHost(int id);

    /**
     * Gets the object that is in charge to run the experiment.
     *
     * @return
     */
    public ExperimentRunner getRunner() {
        return runner;
    }

    /**
     * Gets the list of created DatacenterBrokers.
     *
     * @return
     */
    public List<DatacenterBroker> getBrokerList() {
        return brokerList;
    }

    /**
     * Sets a {@link Consumer} object that will receive the experiment instance
     * after the experiment finishes executing and performs some post-processing
     * tasks. These tasks are defined by the developer using the current class
     * and can include collecting data for statistical analysis.
     *
     * <p>Setting a Consumer object is optional.</p>
     *
     * @param <T> the class of the experiment
     * @param afterExperimentFinishConsumer a {@link Consumer} instance to set.
     * @return
     */
    public <T extends SimulationExperiment> SimulationExperiment setAfterExperimentFinish(final Consumer<T> afterExperimentFinishConsumer) {
        this.afterExperimentFinish = Objects.requireNonNull(afterExperimentFinishConsumer);
        return this;
    }

    public <T extends SimulationExperiment> Consumer<T> getAfterExperimentFinish() {
        return (Consumer<T>) this.afterExperimentFinish;
    }

    /**
     * Sets a {@link Consumer} that will be called after the simulation scenario is built,
     * which is before starting the simulation.
     *
     * <p>Setting a Consumer object is optional.</p>
     * @param <T> the class of the experiment
     * @param afterScenarioBuild the afterScenarioBuild to set
     * @return
     */
    public <T extends SimulationExperiment> SimulationExperiment setAfterScenarioBuild(final Consumer<T> afterScenarioBuild) {
        this.afterScenarioBuild = Objects.requireNonNull(afterScenarioBuild);
        return this;
    }

    private <T extends SimulationExperiment> void afterScenarioBuild(final T scenario) {
        ((Consumer<T>)this.afterScenarioBuild).accept(scenario);
    }

    public final CloudSim getSimulation() {
        return simulation;
    }

    /**
     * Gets the number of brokers to create.
     * @return
     */
    public int getBrokersNumber() {
        return brokersNumber;
    }

    /**
     * Sets the number of brokers to create.
     * @param brokersNumber the value to set
     */
    public SimulationExperiment setBrokersNumber(final int brokersNumber) {
        if(brokersNumber <= 0){
            throw new IllegalArgumentException("The number of brokers must be greater than 0");
        }

        this.brokersNumber = brokersNumber;
        return this;
    }

    public List<DatacenterSimple> getDatacenterList() {
        return datacenterList;
    }

    public long getSeed(){
        return seed;
    }

    @Override
    public String toString() {
        return String.format("Experiment %d", index);
    }

    /**
     * Sets a {@link Function} that receives a {@link DatacenterBroker} and returns the
     * number of Vms to create for that broker.
     * If you want all brokers to have the same amount of VMs,
     * you can give a lambda expression such as {@code broker -> NUMER_OF_VMS_TO_CREATE}.
     * @param vmsByBrokerFunction the {@link Function} to set
     */
    public final void setVmsByBrokerFunction(final Function<DatacenterBroker, Integer> vmsByBrokerFunction) {
        this.vmsByBrokerFunction = Objects.requireNonNull(vmsByBrokerFunction);
    }

    /**
     * Gets a {@link Function} that receives a {@link DatacenterBroker} and returns the
     * number of Vms to create for that broker.
     */
    protected Function<DatacenterBroker, Integer> getVmsByBrokerFunction() {
        return vmsByBrokerFunction;
    }

    protected final void setHostsNumber(final int hostsNumber) {
        if(hostsNumber <= 0){
            throw new IllegalArgumentException("Number of hosts must be greater than zero.");
        }

        this.hostsNumber = hostsNumber;
    }

    public int getDatacentersNumber() {
        return datacentersNumber;
    }

    public void setDatacentersNumber(int datacentersNumber) {
        this.datacentersNumber = datacentersNumber;
    }
}
