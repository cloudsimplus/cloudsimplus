/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * An abstract class to implement simulation experiments
 * that can be executed repeatably by a {@link ExperimentRunner}.
 *
 * @param <T> the type of the subclass extending this class
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
@Accessors
public abstract class Experiment<T extends Experiment<T>> extends AbstractRunnable {
    /**
     * The object that is in charge to run the experiment.
     */
    @Getter
    private final ExperimentRunner<?> runner;

    protected int hostsNumber;

    @Getter @Setter
    private int datacentersNumber;

    /**
     * The number of brokers to create.
     */
    @Getter
    private int brokersNumber;

    @Getter
    private final CloudSimPlus simulation;

    @Getter
    private final List<Datacenter> datacenterList;

    /**
     * The list of created {@link DatacenterBroker}s.
     */
    @Getter
    private final List<DatacenterBroker> brokerList;

    @Getter
    private final List<Vm> vmList;

    @Getter
    private final List<Cloudlet> cloudletList;

    @Getter
    private final long seed;

    /**
     * The index that identifies the current experiment run.
     */
    @Getter
    private final int index;

    private int lastVmId;

    private int lastCloudletId;

    /**
     * Sets a {@link Consumer} that will be called before starting the simulation.
     * This is optional.
     *
     * @param <T> the class of the experiment
     * @param beforeExperimentRun the beforeExperimentRun Consumer to set
     */
    @Setter @NonNull
    private Consumer<T> beforeExperimentRun;

    /**
     * Sets a {@link Consumer} object that will receive the experiment instance
     * after the experiment finishes, then it performs some post-processing
     * tasks. These tasks are defined by the developer using the current class
     * and can include collecting data for statistical analysis.
     *
     * <p>Inside this Consumer, you must call {@link ExperimentRunner#addMetricValue(String, Double)}
     * to collect values for each desired metric. Setting a Consumer object is optional.</p>
     *
     * @param <T> the class of the experiment
     * @param afterExperimentFinishConsumer a {@link Consumer} instance to set
     */
    @Setter @NonNull
    private Consumer<T> afterExperimentFinish;

    /**
     * Sets a {@link Consumer} that will be called before the simulation scenario is built.
     * This is optional.
     *
     * @param <T> the class of the experiment
     * @param beforeExperimentBuild the beforeExperimentBuild Consumer to set
     */
    @Setter @NonNull
    private Consumer<? extends Experiment> beforeExperimentBuild;

    /**
     * Sets a {@link Consumer} that will be called after the simulation scenario is built,
     * which is before starting the simulation.
     * This is optional.
     *
     * @param <T> the class of the experiment
     * @param afterExperimentBuild the afterExperimentBuild Consumer to set
     */
    @Setter @NonNull
    private Consumer<? extends Experiment> afterExperimentBuild;

    /**
     * A {@link Function} that receives a {@link DatacenterBroker} and returns the
     * number of Vms to create for that broker.
     * If you want all brokers to have the same number of VMs,
     * you can give a lambda expression such as {@code broker -> NUMBER_OF_VMS_TO_CREATE}.
     */
    @Getter @Setter @NonNull
    private Function<DatacenterBroker, Integer> vmsByBrokerFunction;

    @Setter
    private Supplier<VmAllocationPolicy> vmAllocationPolicySupplier;

    /**
     * Creates a simulation experiment which is not linked to a runner,
     * to enable it to execute just once.
     */
    public Experiment(final long seed) {
        this(0, null, seed);
    }

    /**
     * Instantiates a simulation experiment with 1 Datacenter by default.
     *
     * @param index the index that identifies the current experiment run.
     * @param runner the {@link ExperimentRunner} that is in charge of executing
     * this experiment a defined number of times and to collect data for statistical analysis.
     * @see #setDatacentersNumber(int)
     */
    public Experiment(final int index, final ExperimentRunner<?> runner) {
        //the seed will be generated from the Runner base seed
        this(index, runner, -1);
    }

    /**
     * Instantiates a simulation experiment
     * that will create 1 broker and 1 Datacenter by default.
     *
     * @param index the index that identifies the current experiment run.
     * @param runner the {@link ExperimentRunner} to execute the experiment.
     *               If omitted, it means the experiment is independent and may be run just once.
     *               If you don't provide a runner, you must provide a seed
     * @param seed the seed to be set. If a runner is given, this value is ignored
     *             and the seed is generated from the runner base seed.
     *             If you don't provide a seed, you must provide a runner.
     * @see #setBrokersNumber(int)
     * @see #setDatacentersNumber(int)
     */
    protected Experiment(final int index, final ExperimentRunner<?> runner, final long seed) {
        super();
        if(seed == -1){
            requireNonNull(runner);
        }
        this.brokersNumber = 1;
        this.datacentersNumber = 1;
        this.simulation = new CloudSimPlus();
        this.vmList = new ArrayList<>();
        this.index = index;
        this.datacenterList = new ArrayList<>();
        this.brokerList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.runner = runner;

        // Defines empty Consumers to avoid NullPointerException if specific ones are not given
        afterExperimentFinish = exp -> {};
        beforeExperimentBuild = exp -> {};
        afterExperimentBuild = exp -> {};
        beforeExperimentRun = exp -> {};

        this.seed = validateSeed(seed);
    }

    /**
     * Builds the simulation scenario and starts execution.
     *
     * @throws RuntimeException
     */
    @Override
    public final void run() {
        requireNonNull(vmsByBrokerFunction, "You need to set the function that indicates the number of VMs to create for each broker.");
        build();
        beforeExperimentRun(this);
        simulation.start();
        afterExperimentFinish(this);
        printResultsInternal();
        if(runner != null) {
            runner.printProgress(runner.incFinishedRuns());
        }
    }

    public boolean isFirstExperimentCreated(){
        return index == runner.getFirstExperimentCreated();
    }

    /**
     * Prints the results for the experiment.
     * The method has to be implemented by subclasses to output the experiment results.
     *
     * @see #printResultsInternal()
     */
    public abstract void printResults();

    /**
     * Creates the simulation scenario to run the experiment.
     */
    protected final void build() {
        beforeExperimentBuild(this);

        createDatacenters();
        createBrokers();

        brokerList.stream().sorted().forEach(b -> {
            createAndSubmitVmsInternal(b);
            createAndSubmitCloudletsInternal(b);
        });

        afterExperimentBuild(this);
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

    /// Creates a DatacenterBroker and adds it to the
    /// [DatacenterBroker list][#getBrokerList()].
    ///
    /// @return the created DatacenterBroker.
    /// @see #createBrokers()
    private DatacenterBroker createBrokerAndAddToList() {
        final var broker = createBroker();
        brokerList.add(broker);
        return broker;
    }

    private void createDatacenters() {
        if(datacentersNumber <= 0){
            throw new IllegalStateException("The number of Datacenters to create was not set");
        }

        for (int i = 0; i < datacentersNumber; i++) {
            datacenterList.add(createDatacenter(i));
        }
    }

    /**
     * Creates a datacenter using a {@link VmAllocationPolicy}
     * supplied by the {@link #vmAllocationPolicySupplier}.
     * @param index index of the datacenter being created, from the {@link #datacentersNumber}.
     * @return the created Datacenter
     * @see #setVmAllocationPolicySupplier(Supplier)
     */
    protected Datacenter createDatacenter(final int index) {
        return new DatacenterSimple(simulation, createHosts(), newVmAllocationPolicy());
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

    /**
     * Checks if {@link #isVerbose()} in order to call {@link #printResults()}
     * to print the experiment results.
     *
     * @see #printResults()
     */
    private void printResultsInternal() {
        if (runner == null || runner.isVerbose()) {
            printResults();
        }
    }

    /**
     * Creates a list of Cloudlets to be used by the experiment.
     *
     * @return the list of created cloudlets
     * @param broker the broker to create the Cloudlets to
     */
    protected abstract List<Cloudlet> createCloudlets(DatacenterBroker broker);

    protected abstract Cloudlet createCloudlet(DatacenterBroker broker);

    /**
     * Creates the Vms to be used by the experiment.
     *
     * @return the List of created VMs
     * @param broker the DatacenterBroker to attach VMs to
     */
    protected List<Vm> createVms(final DatacenterBroker broker) {
        final int numVms = vmsByBrokerFunction.apply(broker);
        final List<Vm> newList = new ArrayList<>(numVms);
        for (int id = vmList.size(); id < vmList.size() + numVms; id++) {
            final var vm = createVm(broker, nextVmId());
            newList.add(vm);
        }

        return newList;
    }

    protected abstract Vm createVm(DatacenterBroker broker, int id);

    protected final int nextVmId(){
        return ++lastVmId;
    }
    protected final int nextCloudletId(){
        return ++lastCloudletId;
    }

    /**
     * Creates all the Cloudlets required by the experiment and submits them to
     * a Broker. This the entry-point for Cloudlets creation.
     *
     * @param broker broker to submit Cloudlets to
     */
    protected void createAndSubmitCloudletsInternal(final DatacenterBroker broker) {
        final var newCloudletList = createCloudlets(broker);
        cloudletList.addAll(newCloudletList);
        broker.submitCloudletList(newCloudletList);
    }

    /**
     * Creates all the VMs required by the experiment and submits them to a
     * Broker. This is the entry-point to start creating VMs.
     *
     * @param broker broker to submit VMs to
     */
    private void createAndSubmitVmsInternal(final DatacenterBroker broker) {
        final var newVmList = createVms(broker);
        vmList.addAll(newVmList);
        broker.submitVmList(newVmList);
    }

    protected final List<Host> createHosts() {
        if(hostsNumber <= 0){
            throw new IllegalStateException("The number of hosts to create was not set");
        }

        final var hostList = new ArrayList<Host>(hostsNumber);
        for (int i = 0; i < hostsNumber; i++) {
            hostList.add(createHost(i));
        }

        return hostList;
    }

    protected abstract Host createHost(int id);

    private <T extends Experiment> void beforeExperimentBuild(final T experiment) {
        ((Consumer<T>)this.beforeExperimentBuild).accept(experiment);
    }

    private <T extends Experiment> void afterExperimentBuild(final T experiment) {
        ((Consumer<T>)this.afterExperimentBuild).accept(experiment);
    }

    private <T extends Experiment> void beforeExperimentRun(final T experiment) {
        ((Consumer<T>)this.beforeExperimentRun).accept(experiment);
    }

    private <T extends Experiment> void afterExperimentFinish(final T experiment) {
        ((Consumer<T>) this.afterExperimentFinish).accept(experiment);
    }

    /**
     * Sets the number of brokers to create.
     * @param brokersNumber the value to set
     * @return this experiment instance
     */
    public Experiment setBrokersNumber(final int brokersNumber) {
        if(brokersNumber <= 0){
            throw new IllegalArgumentException("The number of brokers must be greater than 0");
        }

        this.brokersNumber = brokersNumber;
        return this;
    }

    protected final void setHostsNumber(final int hostsNumber) {
        if(hostsNumber <= 0){
            throw new IllegalArgumentException("Number of hosts must be greater than zero.");
        }

        this.hostsNumber = hostsNumber;
    }

    protected final VmAllocationPolicy newVmAllocationPolicy() {
        return vmAllocationPolicySupplier == null ? new VmAllocationPolicySimple() : vmAllocationPolicySupplier.get();
    }

    @Override
    public String toString() {
        return "Experiment %d".formatted(index);
    }
}
