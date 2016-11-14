package org.cloudsimplus.testbeds;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A base class to implement simulation experiments.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class SimulationExperiment implements Runnable {
    public static final String VMM = "Xen";
	protected final ExperimentRunner runner;
	private final List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private List<Host> hostList;
	/**
	 * @see #getIndex()
	 */
	private final int index;

	/**
	 * @see #getAfterExperimentFinish()
	 */
	private Consumer<? extends SimulationExperiment> afterExperimentFinish;

	private int numberOfCreatedHosts = 0;
    private int numberOfCreatedCloudlets = 0;
    private int numberOfCreatedVms = 0;
	private boolean verbose;

    /**
	 * Creates a simulation experiment.
	 *
	 * @param index the index that identifies the current experiment run.
	 * @param runner The {@link ExperimentRunner} that is in charge
	 * of executing this experiment a defined number of times and to collect
	 * data for statistical analysis.
	 */
	public SimulationExperiment(int index, ExperimentRunner runner) {
		this.verbose = false;
		this.vmList = new ArrayList<>();
		this.index = index;
		this.cloudletList = new ArrayList<>();
		this.brokerList = new ArrayList<>();
        this.hostList = new ArrayList<>();
		this.runner = runner;

        //Defines an empty Consumer to avoid NullPointerException if an actual one is not set
		afterExperimentFinish = exp -> {};
	}

	public List<Cloudlet> getCloudletList() {
	    return cloudletList;
	}

	public List<Vm> getVmList() {
	    return vmList;
	}

	protected void setVmList(List<Vm> vmList) {
		this.vmList = vmList;
	}

    /**
     * Defines if simulation results of the experiment have to be output or not.
     * @param verbose true if the results have to be output, falser otherwise
     */
	public SimulationExperiment setVerbose(boolean verbose) {
	    this.verbose = verbose;
        return this;
	}

	/**
	 * Number of hosts created so far.
	 */
	public int getNumberOfCreatedHosts() {
		return numberOfCreatedHosts;
	}

	/**
	 * The index that identifies the current experiment run.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Indicates if simulation results of the experiment have to be output or not.
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * @see #getBrokerList()
	 */
	private List<DatacenterBroker> brokerList;

    /**
     * Adds a Vm created by a {@link Supplier} function to the list of created Vms.
     *
     * @param vmSupplier a {@link Supplier} function that is able to create a Vm
     * @return the created Vm
     */
	protected Vm addNewVmToList(Supplier<Vm> vmSupplier) {
        Vm vm = vmSupplier.get();
        getVmList().add(vm);
        numberOfCreatedVms++;
        return vm;
	}

    /**
     * Adds a Cloudlet created by a {@link Supplier} function to the list of created Cloudlets.
     *
     * @param cloudletSupplier a {@link Supplier} function that is able to create a Cloudlet
     * @return the created Cloudlet
     */
    protected Cloudlet addNewCloudletToList(Supplier<Cloudlet> cloudletSupplier) {
        Cloudlet cloudlet = cloudletSupplier.get();
        getCloudletList().add(cloudlet);
        numberOfCreatedCloudlets++;
        return cloudlet;
	}

	/**
	 * Builds the simulation scenario and starts execution.
	 *
	 * @return the final solution
	 * @throws RuntimeException
	 */
	@Override
	public void run() {
		buildScenario();

		CloudSim.startSimulation();
		CloudSim.stopSimulation();
		getAfterExperimentFinish().accept(this);

		printResultsInternal();
	}


	/**
	 * Checks if {@link #isVerbose()} is true
	 * in order to call {@link #printResults()}
	 * to print the experiment results.
	 *
	 * @see #printResults()
	 */
	private void printResultsInternal(){
		if(!isVerbose())
			return;

		printResults();
	}

	/**
	 * Prints the results for the experiment.
	 *
	 * The method has to be implemented by subclasses in order to output
	 * the experiment results.
	 *
	 * @see #printResultsInternal()
	 */
	public abstract void printResults();

    /**
     * Creates the simulation scenario to run the experiment.
     */
	protected void buildScenario() {
		int numberOfCloudUsers = 1;
		boolean traceEvents = false;

		CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents);
		Datacenter datacenter0 = createDatacenter("Datacenter0");
		DatacenterBroker broker0 = createBrokerAndAddToList();
		createAndSubmitVmsInternal(broker0);
		createAndSubmitCloudletsInternal(broker0);
	}

	/**
	 * Creates a DatacenterBroker.
	 * @return the created DatacenterBroker
	 */
	protected abstract DatacenterBroker createBroker();

    /**
     * Creates the Cloudlets to be used by the experiment.
     * @param broker broker that the Cloudlets belong to
     */
    protected abstract void createCloudlets(DatacenterBroker broker);

    /**
     * Creates the Vms to be used by the experiment.
     * @param broker broker that the Vms belong to
     */
    protected abstract void createVms(DatacenterBroker broker);

	/**
	 * Creates a DatacenterBroker and adds it to the {@link #getBrokerList() DatacenterBroker list}.
	 * @return the created DatacenterBroker.
	 */
	private DatacenterBroker createBrokerAndAddToList(){
		DatacenterBroker broker = createBroker();
		brokerList.add(broker);
		return broker;
	}

    /**
     * Creates all the Cloudlets required by the experiment and submits them to a Broker.
     * @param broker broker to submit Cloudlets to
     */
	protected void createAndSubmitCloudletsInternal(DatacenterBroker broker) {
        createCloudlets(broker);
        broker.submitCloudletList(getCloudletList());
    }

    /**
     * Creates all the VMs required by the experiment and submits them to a Broker.
     * @param broker broker to submit VMs to
     */
	private void createAndSubmitVmsInternal(DatacenterBroker broker){
        createVms(broker);
        broker.submitVmList(getVmList());
    }

    private DatacenterSimple createDatacenter(String name) {
        createHosts();
		//Defines the characteristics of the data center
		String arch = "x86"; // system architecture of datacenter hosts
		String os = "Linux"; // operating system of datacenter hosts
		double time_zone = 10.0; // time zone where the datacenter is located
		double cost = 3.0; // the cost of using processing in this datacenter
		double costPerMem = 0.05; // the cost of using memory in this datacenter
		double costPerStorage = 0.001; // the cost of using storage in this datacenter
		double costPerBw = 0.0; // the cost of using bw in this datacenter
		LinkedList<FileStorage> storageList = new LinkedList<>(); // we are not adding SAN devices
		DatacenterCharacteristics characteristics =
			new DatacenterCharacteristicsSimple(arch, os, VMM,
				hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
		return new DatacenterSimple(name, characteristics,
			new VmAllocationPolicySimple(hostList), storageList, 0);
	}

    protected abstract void createHosts();

    /**
     * Adds a Host created by a {@link Supplier} function to the list of created Hosts.
     *
     * @param hostSupplier a {@link Supplier} function that is able to create a Host
     * @return the created Host
     */
    protected Host addNewHostToList(Supplier<Host> hostSupplier) {
        Host host = hostSupplier.get();
        hostList.add(host);
        numberOfCreatedHosts++;
        return host;
    }


    /**
     * Gets the object that is in charge to run the experiment.
     * @return
     */
	public ExperimentRunner getRunner() {
		return runner;
	}

	/**
     * Gets the list of created DatacenterBrokers.
	 * @return
	 */
	public List<DatacenterBroker> getBrokerList() {
		return brokerList;
	}

	/**
	 * <p>Sets a {@link Consumer} object that will receive the experiment instance after the experiment
	 * finishes executing and performs some post-processing tasks.
	 * These tasks are defined by the developer using the current class
	 * and can include collecting data for statistical analysis.</p>
	 *
	 * <p>Setting a Consumer object is optional.</p>
	 *
	 * @param afterExperimentFinishConsumer a {@link Consumer} instance to set.
	 * @param <T> a generic class that defines the type of experiment the {@link Consumer}
	 *           will deal with. It is used to ensure that when the {@link Consumer} is called,
	 *           it will receive an object of the exact type of the {@link SimulationExperiment}
	 *           instance that the Consumer is being associated to.
	 */
	public <T extends SimulationExperiment> SimulationExperiment setAfterExperimentFinish(Consumer<T> afterExperimentFinishConsumer){
		this.afterExperimentFinish = afterExperimentFinishConsumer;
        return this;
	}

	/**
	 * Gets a {@link Consumer} object that will receive the experiment instance after the experiment
	 * finishes executing and performs some post-processing tasks.
	 * These tasks are defined by the developer using the current class
	 * and can include collecting data for statistical analysis.
     * @return
	 *
	 */
	private <T extends SimulationExperiment> Consumer<T> getAfterExperimentFinish() {
		return (Consumer<T>) afterExperimentFinish;
	}

    public int getNumberOfCreatedCloudlets() {
        return numberOfCreatedCloudlets;
    }

    public int getNumberOfCreatedVms() {
        return numberOfCreatedVms;
    }
}
