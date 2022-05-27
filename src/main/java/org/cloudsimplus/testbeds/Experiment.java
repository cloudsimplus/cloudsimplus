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

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An abstract class to implement simulation experiments
 * that can be executed in a repeatable way
 * by a {@link ExperimentRunner}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public abstract class Experiment extends AbstractRunnable {
    protected final ExperimentRunner runner;
    protected int hostsNumber;
    private int datacentersNumber;
    private int brokersNumber;

    private final CloudSim simulation;
    private final List<Datacenter> datacenterList;
    private final List<DatacenterBroker> brokerList;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final long seed;

    private final int index;
    private int lastVmId;
    private int lastCloudletId;

    private Consumer<? extends Experiment> beforeExperimentRun;
    private Consumer<? extends Experiment> afterExperimentFinish;
    private Consumer<? extends Experiment> beforeExperimentBuild;
    private Consumer<? extends Experiment> afterExperimentBuild;

    /**@see #setVmsByBrokerFunction(Function) */
    private Function<DatacenterBroker, Integer> vmsByBrokerFunction;

    /**@see #setVmAllocationPolicySupplier(Supplier)  */
    private Supplier<VmAllocationPolicy> vmAllocationPolicySupplier;

    /**
     * Creates a simulation experiment that is not linked to a runner,
     * to enable it to execute just one run.
     *
     */
    public Experiment(final long seed) {
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
    public Experiment(final int index, final ExperimentRunner runner) {
        //the seed will be generate from the Runner base seed
        this(index, runner, -1);
    }

    /**
     * Instantiates a simulation experiment
     * that will create 1 broker and 1 Datacenter by default.
     *
     * @param index the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} to execute the experiment.
     *               If omitted, it means the experiment is independent and may be run just once.
     *               If you don't provide a runner, you must provide a seed
     * @param seed the seed to be set. If a runner is given, this value is ignored
     *             and the seed is generated from the runner base seed.
     *             If you don't provide a seed, you must provide a runner.
     * @see #setBrokersNumber(int)
     * @see #setDatacentersNumber(int)
     */
    protected Experiment(final int index, final ExperimentRunner runner, final long seed) {
        super();
        if(seed == -1){
            Objects.requireNonNull(runner);
        }
        this.brokersNumber = 1;
        this.datacentersNumber = 1;
        this.simulation = new CloudSim();
        this.vmList = new ArrayList<>();
        this.index = index;
        this.datacenterList = new ArrayList<>();
        this.brokerList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.runner = runner;

        //Defines an empty Consumer to avoid NullPointerException if an actual one is not set
        afterExperimentFinish = exp -> {};
        beforeExperimentBuild = exp -> {};
        afterExperimentBuild = exp -> {};
        beforeExperimentRun = exp -> {};

        this.seed = validateSeed(seed);
    }

    public final List<Cloudlet> getCloudletList() {
        return Collections.unmodifiableList(cloudletList);
    }

    public List<Vm> getVmList() {
        return Collections.unmodifiableList(vmList);
    }

    /**
     * The index that identifies the current experiment run.
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * Builds the simulation scenario and starts execution.
     *
     * @throws RuntimeException
     */
    @Override
    public final void run() {
        Objects.requireNonNull(vmsByBrokerFunction, "You need to set the function that indicates the number of VMs to create for each broker.");
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
     *
     * The method has to be implemented by subclasses in order to output the
     * experiment results.
     *
     * @see #printResultsInternal()
     */
    public abstract void printResults();

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
     * Sets a {@link Consumer} that will be called before the simulation scenario is built.
     *
     * <p>Setting a Consumer object is optional.</p>
     * @param <T> the class of the experiment
     * @param beforeExperimentBuild the beforeExperimentBuild Consumer to set
     * @return
     */
    public <T extends Experiment> T setBeforeExperimentBuild(final Consumer<T> beforeExperimentBuild) {
        this.beforeExperimentBuild = Objects.requireNonNull(beforeExperimentBuild);
        return (T)this;
    }

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

    /**
     * Creates a DatacenterBroker and adds it to the
     * {@link #getBrokerList() DatacenterBroker list}.
     *
     * @return the created DatacenterBroker.
     * @see #createBrokers()
     */
    private DatacenterBroker createBrokerAndAddToList() {
        final var broker = createBroker();
        brokerList.add(broker);
        return broker;
    }

    /**
     * Creates a datacenter using a {@link VmAllocationPolicy}
     * supplied by the {@link #vmAllocationPolicySupplier}.
     * @param index index of the datacenter being created, from the {@link #datacentersNumber}.
     * @return
     * @see #setVmAllocationPolicySupplier(Supplier)
     */
    protected Datacenter createDatacenter(final int index) {
        return new DatacenterSimple(simulation, createHosts(), newVmAllocationPolicy());
    }

    private void createDatacenters() {
        if(datacentersNumber <= 0){
            throw new IllegalStateException("The number of Datacenters to create was not set");
        }

        for (int i = 0; i < datacentersNumber; i++) {
            datacenterList.add(createDatacenter(i));
        }
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
     * @param broker
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

    private <T extends Experiment> void beforeExperimentBuild(final T experiment) {
        ((Consumer<T>)this.beforeExperimentBuild).accept(experiment);
    }

    /**
     * Sets a {@link Consumer} that will be called after the simulation scenario is built,
     * which is before starting the simulation.
     *
     * <p>Setting a Consumer object is optional.</p>
     * @param <T> the class of the experiment
     * @param afterExperimentBuild the afterExperimentBuild Consumer to set
     * @return
     */
    public <T extends Experiment> Experiment setAfterExperimentBuild(final Consumer<T> afterExperimentBuild) {
        this.afterExperimentBuild = Objects.requireNonNull(afterExperimentBuild);
        return this;
    }

    private <T extends Experiment> void afterExperimentBuild(final T experiment) {
        ((Consumer<T>)this.afterExperimentBuild).accept(experiment);
    }

    /**
     * Sets a {@link Consumer} that will be called before starting the simulation.
     *
     * <p>Setting a Consumer object is optional.</p>
     * @param <T> the class of the experiment
     * @param beforeExperimentRun the beforeExperimentRun Consumer to set
     * @return
     */
    public <T extends Experiment> T setBeforeExperimentRun(final Consumer<T> beforeExperimentRun) {
        this.beforeExperimentRun = Objects.requireNonNull(beforeExperimentRun);
        return (T)this;
    }

    private <T extends Experiment> void beforeExperimentRun(final T experiment) {
        ((Consumer<T>)this.beforeExperimentRun).accept(experiment);
    }

    /**
     * Sets a {@link Consumer} object that will receive the experiment instance
     * after the experiment finishes, then it performs some post-processing
     * tasks. These tasks are defined by the developer using the current class
     * and can include collecting data for statistical analysis.
     * <p>Inside this Consumer, you must call {@link ExperimentRunner#addMetricValue(String, Double)}
     * to collect values for each desired metric.</p>
     *
     * <p>Setting a Consumer object is optional.</p>
     *
     * @param <T> the class of the experiment
     * @param afterExperimentFinishConsumer a {@link Consumer} instance to set
     * @return
     */
    public <T extends Experiment> Experiment setAfterExperimentFinish(final Consumer<T> afterExperimentFinishConsumer) {
        this.afterExperimentFinish = Objects.requireNonNull(afterExperimentFinishConsumer);
        return this;
    }

    private <T extends Experiment> void afterExperimentFinish(final T experiment) {
        ((Consumer<T>) this.afterExperimentFinish).accept(experiment);
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
    public Experiment setBrokersNumber(final int brokersNumber) {
        if(brokersNumber <= 0){
            throw new IllegalArgumentException("The number of brokers must be greater than 0");
        }

        this.brokersNumber = brokersNumber;
        return this;
    }

    public List<Datacenter> getDatacenterList() {
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
     * you can give a lambda expression such as {@code broker -> NUMBER_OF_VMS_TO_CREATE}.
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

    public void setDatacentersNumber(final int datacentersNumber) {
        this.datacentersNumber = datacentersNumber;
    }

    public void setVmAllocationPolicySupplier(final Supplier<VmAllocationPolicy> vmAllocationPolicySupplier) {
        this.vmAllocationPolicySupplier = Objects.requireNonNull(vmAllocationPolicySupplier);
    }

    protected final VmAllocationPolicy newVmAllocationPolicy() {
        return vmAllocationPolicySupplier == null ? new VmAllocationPolicySimple() : vmAllocationPolicySupplier.get();
    }
}
