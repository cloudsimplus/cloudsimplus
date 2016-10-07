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
import org.cloudsimplus.testbeds.heuristics.DatacenterBrokerHeuristicRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A base class to implement simulation experiments.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class SimulationExperiment implements Runnable {
	public static final String VMM = "Xen";
	protected final DatacenterBrokerHeuristicRunner runner;
	private final List<Cloudlet> cloudletList;
	/**
	 * @see #getIndex()
	 */
	private final int index;

	/**
	 * @see #getAfterExperimentFinish()
	 */
	private Consumer<? extends SimulationExperiment> afterExperimentFinish;

	private List<Vm> vmList;
	private int numberOfCreatedCloudlets = 0;
	private int numberOfCreatedVms = 0;
	private int numberOfCreatedHosts = 0;
	private boolean verbose;

	/**
	 * @see #getHostsToCreate()
	 */
	private int hostsToCreate;

	/**
	 * Creates a simulation experiment.
	 *
	 * @param index the index that identifies the current experiment run.
	 * @param runner The {@link ExperimentRunner} that is in charge
	 * of executing this experiment a defined number of times and to collect
	 * data for statistical analysis.
	 */
	public SimulationExperiment(int index, DatacenterBrokerHeuristicRunner runner) {
		this.verbose = false;
		this.vmList = new ArrayList<>();
		this.index = index;
		this.cloudletList = new ArrayList<>();
		this.brokerList = new ArrayList<>();
		this.runner = runner;
		afterExperimentFinish = exp->{};
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
	public void setVerbose(boolean verbose) {
	    this.verbose = verbose;
	}

	/**
	 * @return the number of hosts to create
	 */
	public int getHostsToCreate() {
		return hostsToCreate;
	}

	public final void setHostsToCreate(int hostsToCreate) {
		this.hostsToCreate = hostsToCreate;
	}

	/**
	 * Number of cloudlets created so far.
	 */
	public int getNumberOfCreatedCloudlets() {
		return numberOfCreatedCloudlets;
	}

	/**
	 * Number of VMs created so far.
	 */
	public int getNumberOfCreatedVms() {
		return numberOfCreatedVms;
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
	 * Indicates if simulation results have to be output or not.
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * @see #getBrokerList()
	 */
	private List<DatacenterBroker> brokerList;

	protected Host createHost() {
		int mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
		int ram = 2048; // host memory (MB)
		long storage = 1000000; // host storage
		long bw = 10000;
		List<Pe> cpuCoresList = new ArrayList<>();
        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
		for (int i = 0; i < 8; i++) {
			cpuCoresList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
		}

		return new HostSimple(numberOfCreatedHosts++,
			new ResourceProvisionerSimple<>(new Ram(ram)),
			new ResourceProvisionerSimple<>(new Bandwidth(bw)), storage, cpuCoresList,
			new VmSchedulerTimeShared(cpuCoresList));
	}

	protected Vm createVm(DatacenterBroker broker, int pesNumber) {
		double mips = 1000;
		long storage = 10000; // vm image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000; // vm bandwidth
		return new VmSimple(numberOfCreatedVms++, broker.getId(), mips,
			pesNumber, ram, bw, storage, VMM,
			new CloudletSchedulerTimeShared());
	}

	protected Cloudlet createCloudlet(DatacenterBroker broker, int numberOfPes) {
		long length = 400000; //in Million Structions (MI)
		long fileSize = 300; //Size (in bytes) before execution
		long outputSize = 300; //Size (in bytes) after execution
		//Defines how CPU, RAM and Bandwidth resources are used
		//Sets the same utilization model for all these resources.
		UtilizationModel utilization = new UtilizationModelFull();
		Cloudlet cloudlet = new CloudletSimple(numberOfCreatedCloudlets++,
			length, numberOfPes, fileSize, outputSize,
			utilization, utilization, utilization);
		cloudlet.setUserId(broker.getId());
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

	private void buildScenario() {
		int numberOfCloudUsers = 1;
		boolean traceEvents = false;

		CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents);
		Datacenter datacenter0 = createDatacenter("Datacenter0");
		DatacenterBroker broker0 = createBrokerAndAddToList();
		createAndSubmitVms(broker0);
		createAndSubmitCloudlets(broker0);
	}

	/**
	 * Creates a DatacenterBroker.
	 * @return the created DatacenterBroker
	 */
	protected abstract DatacenterBroker createBroker();

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
	 * @see #setVmPesArray(int[])
	 */
	private int vmPesArray[];

	/**
	 * @see #setCloudletPesArray(int[])
	 */
	private int cloudletPesArray[];

	protected void createAndSubmitCloudlets(DatacenterBroker broker0) {
		for (int pes: cloudletPesArray) {
			getCloudletList().add(createCloudlet(broker0, pes));
		}
		broker0.submitCloudletList(getCloudletList());
	}

	protected void createAndSubmitVms(DatacenterBroker broker0) {
		setVmList(new ArrayList<>(vmPesArray.length));
		for (int pes : vmPesArray) {
			getVmList().add(createVm(broker0, pes));
		}
		broker0.submitVmList(getVmList());
	}

	protected DatacenterSimple createDatacenter(String name) {
		List<Host> hostList = new ArrayList<>();
		for (int i = 0; i < getHostsToCreate(); i++) {
			hostList.add(createHost());
		}
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

	/**
	 * Sets the array with Number of PEs for each VM to create.
	 * The length of the array defines the number of VMs to create.
	 * @param vmPesArray VMs PEs array to set
	 */
	public SimulationExperiment setVmPesArray(int vmPesArray[]) {
		this.vmPesArray = vmPesArray;
		return this;
	}

	/**
	 * Sets the array with Number of PEs for each Cloudlet to create.
	 * The length of the array defines the number of Cloudlets to create.
	 * @param cloudletPesArray Cloudlets PEs array to set
	 */
	public SimulationExperiment setCloudletPesArray(int[] cloudletPesArray) {
		this.cloudletPesArray = cloudletPesArray;
		return this;
	}

	public DatacenterBrokerHeuristicRunner getRunner() {
		return runner;
	}

	/**
	 * @return list of created DatacenterBrokers.
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
	public <T extends SimulationExperiment> void setAfterExperimentFinish(Consumer<T> afterExperimentFinishConsumer){
		this.afterExperimentFinish = afterExperimentFinishConsumer;
	}

	/**
	 * <p>Gets a {@link Consumer} object that will receive the experiment instance after the experiment
	 * finishes executing and performs some post-processing tasks.
	 * These tasks are defined by the developer using the current class
	 * and can include collecting data for statistical analysis.</p>
	 *
	 */
	private <T extends SimulationExperiment> Consumer<T> getAfterExperimentFinish() {
		return (Consumer<T>) afterExperimentFinish;
	}

}
