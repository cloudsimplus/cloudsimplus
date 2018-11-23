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

/**
 * A base class to implement simulation experiments.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class SimulationExperiment implements Runnable {
    private final ExperimentRunner runner;
    private final List<Cloudlet> cloudletList;
    private final long seed;
    private List<Vm> vmList;
    private List<Host> hostList;
    private List<DatacenterBroker> brokerList;
    private int numBrokersToCreate;

    private final int index;
    private boolean verbose;

    private CloudSim cloudsim;
    private Consumer<? extends SimulationExperiment> afterExperimentFinish;
    private Consumer<? extends SimulationExperiment> afterScenarioBuild;
    private DatacenterSimple datacenter0;

    /**
     * Creates a simulation experiment that is not linked to a runner,
     * to enable it to execute just one run.
     *
     */
    public SimulationExperiment(final long seed) {
        this(0, null, seed);
    }

    /**
     * Creates a simulation experiment.
     *
     * @param index the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} that is in charge of executing
     * this experiment a defined number of times and to collect data for
     * statistical analysis.
     */
    public SimulationExperiment(final int index, final ExperimentRunner runner) {
        //the seed will be generate from the Runner base seed
        this(index, runner, -1);
    }

    /**
     * Creates a simulation experiment.
     *
     * @param index the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} to execute the experiment.
     *               If omitted, it means the experiment is independent and may be run just once
     * @param seed the seed to be set. If a runner is given, this value is ignored
     *             and the seed is generated from the runner base seed.
     */
    protected SimulationExperiment(final int index, final ExperimentRunner runner, final long seed) {
        if(seed == -1){
            Objects.requireNonNull(runner);
        }
        this.numBrokersToCreate = 1;
        this.verbose = false;
        this.cloudsim = new CloudSim();
        this.vmList = new ArrayList<>();
        this.index = index;
        this.cloudletList = new ArrayList<>();
        this.brokerList = new ArrayList<>();
        this.hostList = new ArrayList<>();
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
     * @param verbose true if the results have to be output, falser otherwise
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
        buildScenario();
        cloudsim.start();
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
        if (isNotVerbose()) {
            return;
        }

        printResults();
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
        datacenter0 = createDatacenter();
        createBrokers();

        brokerList.stream().sorted().forEach(b -> {
                createAndSubmitVmsInternal(b);
                createAndSubmitCloudletsInternal(b);
        });

        getAfterScenarioBuild().accept(this);
    }

    protected void createBrokers() {
        for (int i = 0; i < numBrokersToCreate; i++) {
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
     * Creates a list of Cloudlets to be used by the experiment
     * and adds them to the {@link #getCloudletList()}.
     *
     * @return the list of created cloudlets
     */
    protected abstract List<Cloudlet> createCloudlets();

    /**
     * Creates the Vms to be used by the experiment.
     *
     * @return the List of created VMs
     * @param broker
     */
    protected abstract List<Vm> createVms(DatacenterBroker broker);

    /**
     * Creates a DatacenterBroker and adds it to the
     * {@link #getBrokerList() DatacenterBroker list}.
     *
     * @return the created DatacenterBroker.
     */
    private DatacenterBroker createBrokerAndAddToList() {
        DatacenterBroker broker = createBroker();
        brokerList.add(broker);
        return broker;
    }

    /**
     * Creates all the Cloudlets required by the experiment and submits them to
     * a Broker.
     *
     * @param broker broker to submit Cloudlets to
     */
    protected void createAndSubmitCloudletsInternal(DatacenterBroker broker) {
        final List<Cloudlet> list = createCloudlets();
        cloudletList.addAll(list);
        broker.submitCloudletList(list);
    }

    /**
     * Creates all the VMs required by the experiment and submits them to a
     * Broker.
     *
     * @param broker broker to submit VMs to
     */
    private void createAndSubmitVmsInternal(final DatacenterBroker broker) {
        final List<Vm> list = createVms(broker);
        vmList.addAll(list);
        broker.submitVmList(list);
    }

    protected DatacenterSimple createDatacenter() {
        final List<Host> hosts = createHosts();
        hostList.addAll(hosts);
        return new DatacenterSimple(cloudsim, hosts, new VmAllocationPolicySimple());
    }

    protected abstract List<Host> createHosts();

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

    public <T extends SimulationExperiment> Consumer<T> getAfterScenarioBuild() {
        return (Consumer<T>)this.afterScenarioBuild;
    }

    public final CloudSim getCloudSim() {
        return cloudsim;
    }

    /**
     * @return the hostList
     */
    public List<Host> getHostList() {
        return Collections.unmodifiableList(hostList);
    }

    /**
     * @return the numBrokersToCreate
     */
    public int getNumBrokersToCreate() {
        return numBrokersToCreate;
    }

    /**
     * @param numBrokersToCreate the numBrokersToCreate to set
     */
    public void setNumBrokersToCreate(final int numBrokersToCreate) {
        this.numBrokersToCreate = numBrokersToCreate;
    }

    /**
     * @return the datacenter0
     */
    public DatacenterSimple getDatacenter0() {
        return datacenter0;
    }

    public long getSeed(){
        return seed;
    }

    @Override
    public String toString() {
        return String.format("Experiment %d", index);
    }
}
