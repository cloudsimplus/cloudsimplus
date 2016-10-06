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
import java.util.function.Function;

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
	 */
	public SimulationExperiment(int index, DatacenterBrokerHeuristicRunner runner) {
		this.verbose = false;
		this.vmList = new ArrayList<>();
		this.index = index;
		this.cloudletList = new ArrayList<>();
		this.runner = runner;
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
	 * Builds the simulation scenario and starts simulation.
	 *
	 * @return the final solution
	 * @throws RuntimeException
	 */
	@Override
	public void run() {
		buildScenario();

		CloudSim.startSimulation();
		CloudSim.stopSimulation();
		printSolution(
			String.format(
				"Experiment %d > Heuristic solution for mapping cloudlets to Vm's",
				getIndex()));
	}

	public abstract void printSolution(String title);

	private void buildScenario() {
		int numberOfCloudUsers = 1;
		boolean traceEvents = false;

		CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents);
		Datacenter datacenter0 = createDatacenter("Datacenter0");
		DatacenterBroker broker0 = createBroker();
		createAndSubmitVms(broker0);
		createAndSubmitCloudlets(broker0);
	}

	protected abstract DatacenterBroker createBroker();

	/**
	 * Array with Number of PEs for each created VM. The length of the array defines
	 * the number of VMs to create.
	 */
	private int vmPesArray[];

	/**
	 * Array with Number of PEs for each created Cloudlet. The length of the array defines
	 * the number of Cloudlets to create.
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

	public void setVmPesArray(int vmPesArray[]) {
		this.vmPesArray = vmPesArray;
	}

	public void setCloudletPesArray(int[] cloudletPesArray) {
		this.cloudletPesArray = cloudletPesArray;
	}

	public DatacenterBrokerHeuristicRunner getRunner() {
		return runner;
	}
}
