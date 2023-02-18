/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2023 IBM Research.
 *     Author: Pavlos Maniotis
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
package org.cloudsimplus.traces.azure;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyBestFitWithPlacementGroups;
import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyBestFitWithPlacementGroups_LRRL;
import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyChicSchedAllPack;
import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyFirstFitWithPlacementGroups;
import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyWithPlacementGroups;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroup;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupAffinityType;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupEnforcement;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupScope;

/**
 * A class for facilitating the trace-based simulations by keeping all the configuration 
 * parameters in a single place. It also provides a set of helper functions for common tasks
 * like creating {@link Vm}s and {@link Host}s.
 * <br>
 * Steps to properly run trace-based simulations with this class:
 * <pre>
 *  (1) Create the {@link TracesSimulationManager} and set the configuration parameters 
 *      with the setter functions
 *  (2) Create the {@link CloudSim} and {@link DatacenterBroker} objects
 *  (3) Initialize {@link TracesSimulationManager} by calling {@link #initialize(CloudSim, DatacenterBroker)}
 *  (4) Create the data center, i.e., {@link DatacenterSimple}, by 
 *      providing as input the {@link Host}s and the {@link VmAllocationPolicy} 
 *      returned by the {@link #getHosts()} and {@link #getVmAllocationPolicy()} functions
 *  (5) Start the simulation by calling {@link CloudSim#start()}
 *  (6) Save the results by using {@link #saveResults()}
 * </pre>
 * For a detailed example see class VmPlacementGroupsWithAzureTraces in org.cloudsimplus.examples.traces.vmplacementgroups.
 * <br>
 * <br>
 * An important aspect of these simulations is that we are only interested in the 
 * lifetimes of the VMs and not in the particular Cloudlets that run in them. To control 
 * how long the VMs stay alive we follow these steps:
 * <pre>
 *  (1) We set the MIPS capacity equal to 1 for all the PEs of all 
 *      hosts. See {@link #createHost(long, long, long, long)}
 *  (2) We create only one new Cloudlet for every request. 
 *      See {@link TracesBatchesManager#createRequest}
 *  (3) We set the Cloudlet's MI parameter equal to the lifetime of 
 *      the VM in seconds. See {@link TracesBatchesManager#vmAllocationListener}
 *  (4) Once the Cloudlets finish their execution we destroy the VMs 
 *      of the corresponding requests. See {@link TracesBatchesManager#cloudletFinishListener}
 * </pre>
 * 
 * Alternatively we could build a solution around {@link Cloudlet}'s function setLifeTime(). 
 * feature, which, however, was not available when this solution was built.  
 * <br>
 * <br>
 * For the Pes we use the {@link PeProvisionerSimple} while for the Cloudlet we 
 * use the {@link CloudletSchedulerSpaceShared}. 
 * 
 * @see TracesBatchesManager
 * @see TracesParser
 * @see TracesStatisticsManager
 * @see PeProvisionerSimple
 * @see CloudletSchedulerSpaceShared
 *
 * @since CloudSim Plus 7.3.2
 *
 * @author Pavlos Maniotis
 */
public class TracesSimulationManager {
	
	private long uniqueId = 1; 
	/*FIXME: create our own unique ids for the VMs and the cloudlets.
	 * Before 7.3.1 ids worked fine but now seems like individual VMs of different 
	 * VmPlacementGroups get same ids which leads to other errors. */
	
	// configuration parameters
	/** The working path for the simulations */
    private String workingPath;
    
    /** The path where the dataset files reside */
    private String tracePath;
    
    
    
    /** Number of cores per host */
    private long hostCores;
    
    /** Amount of RAM per host */
    private long hostRamMiB;
    
    /** Amount of bandwidth per host */
	private long hostBwMbps;
	
    /** Amount of storage per host */
	private long hostStorageMiB;
	
	
	
    /** Number of top-of-rack switches in the data center */
	private long numOfSwitches;
	
    /** Number of hosts per top-of-rack switch */
	private long hostsPerSwitch;
	
	
	
    /** Stop simulating after that time  */
	private long simEndTimeLimitSec;
    
	/** Simulate VM requests starting from this timestamp  */
	private long startTimesFromSec;
    
	/** Simulate VM requests until this timestamp */
	private long startTimesUntilSec;
    
	/** Cap time for the VM lifetimes */
	private long vmCapSec;
	
	
	
	/** Minimum number of hosts per request */
	private long minHostsPerRequest;
	
	/** Minimum number of VMs per request */
	private long minVmsPerRequest;
	
	/** Threshold for VM start and VM end times to consider a set of requests as a group request */
	private long groupThresholdSec;
	
	/** Batch size to be used for the requests submission to the broker */
	private long batchSize;
	
	
	
	/** Which {@link VmAllocationPolicyWithPlacementGroups} to simulate */
    private VmAllocationPolicyWithPlacementGroups vmAllocationPolicyType;
	
    /** The VM allocation policy to simulate */
	private VmAllocationPolicy vmAllocationPolicy;
	
	/** Should the simulation finish when the first request allocation failure occurs? */
	private boolean quitOnAllocationFailure;
    
	
	
	/** See {@link CloudSim#CloudSim(double)} */
	private double minTimeBetweenEvents; 
	
	
	
	// these are calculated in the initialize function
	/** Total number of host in the data center */
	private long totalHosts;
	
	/** Total number of cores in the data center */
	private long totalCores;
	
	/** Total amount of RAM in the data center */
	private long totalRamMiB;
	
	/** Total host bandwidth the data center */
	private long totalBwMbps;
	
	/** Total amount of storage in the data center */
	private long totalStorageMiB;

	
	
	/** The simulation object */
	private CloudSim simulation;
	
	/** The data center broker */
	private DatacenterBroker broker;
	
	/** A list with the hosts of the data center */
	private List<Host> hosts;
	
	/** The batches manager of the simulation */
    private TracesBatchesManager batchesManager;
    
    
    /** Print to file? */
    private boolean printToFile;
    
    /** Where to print during the simulation */
	private PrintStream simOut;
	
	/** Timestamp for the completion of the simulation initialization */
    private double initializationCompletionTimestamp;

    
    /** The constructor */
	public TracesSimulationManager () {
		
	}
	
	
	/**
	 * Takes as input a {@link CloudSim} object and a {@link DatacenterBroker} and 
	 * initializes the {@link TracesSimulationManager}, i.e., it creates the {@link Host}s, 
	 * the {@link VmAllocationPolicy}, and the {@link TracesBatchesManager}. 
	 */
    public void initialize(final CloudSim simulation, final DatacenterBroker broker) {
    	
    	//broker.setFailedVmsRetryDelay(-1d); // do not retry allocating failed requests
    	broker.getVmCreation().setRetryDelay(-1);

    	final double timeZeroTimestamp = TimeUtil.currentTimeSecs();

    	
    	new File (this.workingPath).mkdirs();

    	PrintStream systemOut = System.out;
    	
    	if(this.printToFile) {
    		try {
    			
    			final File file = new File(this.workingPath + "console.txt");
    			
    			final FileOutputStream fileOutputStream = new FileOutputStream(file, true);
    			
    			this.simOut = new PrintStream(fileOutputStream);
    			System.setOut(simOut);
    			
    		} catch (FileNotFoundException e) {
    			
    			this.simOut = systemOut;
    			this.simOut.println("Cannot open PrintStream for console output. Printing to System.out");
    		}
    	} else {
    		this.simOut = systemOut;
    	}

		    	
    	SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    	Date date = new Date(System.currentTimeMillis());
    	simOut.println(formatter.format(date));
    	
    	simOut.println("\n"+"Start of initialization phase");
		
    	this.simulation = simulation;
    	this.broker = broker;
    	
    	
    	this.totalHosts      = numOfSwitches * hostsPerSwitch;
    	this.totalCores      = totalHosts * hostCores;
    	this.totalRamMiB     = totalHosts * hostRamMiB;
    	this.totalBwMbps     = totalHosts * hostBwMbps;
    	this.totalStorageMiB = totalHosts * hostStorageMiB;
    	
    	
    	this.hosts = this.createHosts();    	
    	
    	
    	this.batchesManager = new TracesBatchesManager(this);

    	
        switch(this.vmAllocationPolicyType) {
        
        case BestFitWithGroups:
        	
            this.vmAllocationPolicy = 
    		new VmAllocationPolicyBestFitWithPlacementGroups(this.simulation, this);
        	break;
        	
        case BestFitWithGroups_LRRL:
        	
            this.vmAllocationPolicy = 
    		new VmAllocationPolicyBestFitWithPlacementGroups_LRRL(this.simulation, this);
        	break;
            
        case ChicSchedAllPack:
        	this.vmAllocationPolicy=
        	new VmAllocationPolicyChicSchedAllPack(this.simulation, this);
        	break;
        	
        default:
        	
            this.vmAllocationPolicy = 
    		new VmAllocationPolicyFirstFitWithPlacementGroups(this.simulation, this);
            break;
        }
        
    	this.initializationCompletionTimestamp = TimeUtil.currentTimeSecs();
        final double initializationDuration = TimeUtil.elapsedSeconds(timeZeroTimestamp);
        simOut.println("\n"+"Initialization phase completed in " + TimeUtil.secondsToStr(initializationDuration));
    }

	/**
	 * Creates a hosts with MIPS = 1 for all Pes
	 * 
	 * @param cores the number of cores 
	 * @param ramMiB the amount of RAM
	 * @param bwMbps the amount of bandwidth
	 * @param storageMiB the amount of storage
	 * @return a {@link Host} with MPIS = 1 for all Pes
	 */
    private Host createHost(final long cores, final long ramMiB, final long bwMbps, final long storageMiB) {
    	
        List<Pe> peList = new ArrayList<>((int) cores);

        // We set the MIPS capacity equal to 1 for all the PEs of the host
        for (long i = 0; i < cores; i++)
            peList.add(new PeSimple(1d, new PeProvisionerSimple()));

        Host host = new HostSimple(ramMiB, bwMbps, storageMiB, peList)
        					.setRamProvisioner(new ResourceProvisionerSimple())
        					.setBwProvisioner(new ResourceProvisionerSimple())
        					.setVmScheduler(new VmSchedulerSpaceShared());
        
        return host;
    }
    
	/**
	 * Creates the {@link Host}s for the simulation 
	 */
	private List<Host> createHosts () {
		
		List<Host> hosts = new ArrayList<Host>();
		
		for (long i = 0; i < this.totalHosts; i++) {
			hosts.add(createHost(this.hostCores, this.hostRamMiB, this.hostBwMbps, this.hostStorageMiB));
		}	
		
		return hosts;
	}
	
	/**
	 * Creates a single {@link Vm} with submission delay
	 * 
	 * @param cores the number of cores 
	 * @param ramMiB the amount of RAM
	 * @param bwMbps the amount of bandwidth
	 * @param storageMiB the amount of storage
	 * @param submissionDelay the submission Delay
	 * @return a {@link Vm} with submission delay
	 */
	public Vm createVm (long cores, long ramMiB, long bwMbps, long storageMiB, long submissionDelay) {
		
		Vm vm = new VmSimple(1d, cores)
				.setRam(ramMiB)
				.setBw(bwMbps)
				.setSize(storageMiB)
				.setCloudletScheduler(new CloudletSchedulerSpaceShared());
		
		vm.setSubmissionDelay(submissionDelay);
		
		vm.setId(this.uniqueId++);
		
		return vm;
	}
	
	/**
	 * Creates a {@link VmPlacementGroup} with submission delay.
	 * All {@link VmPlacementGroup}s are created as {@link VmPlacementGroupScope#SWITCH}, 
	 * {@link VmPlacementGroupAffinityType#AFFINITY} with {@link VmPlacementGroupEnforcement#BEST_EFFORT}
	 * 
	 * @param numOfVms the number of VMs in the VM placement group
	 * @param cores the number of cores / VM
	 * @param ramMiB the amount of RAM / VM
	 * @param bwMbps the amount of bandwidth / VM
	 * @param storageMiB the amount of storage / VM
	 * @param submissionDelay the submission Delay
	 * @return a {@link VmPlacementGroup} with submission delay
	 */
	public VmPlacementGroup createVmPlacementGroup(long numOfVms, long cores, long ramMiB, long bwMbps, long storageMiB, long submissionDelay) {
		
		final List<Vm> vms = new ArrayList<Vm>(); 
		
		for (long i = 0; i < numOfVms; i++) {
			
			Vm vm = new VmSimple(1d, cores)
					.setRam(ramMiB)
					.setBw(bwMbps)
					.setSize(storageMiB)
					.setCloudletScheduler(new CloudletSchedulerSpaceShared());
			
			vm.setId(this.uniqueId++);
			
			vms.add(vm);		
		}
		
		long idealNumOfHosts = this.calculateIdealNumOfHosts(numOfVms, cores, ramMiB, bwMbps, storageMiB);

		long idealNumOfSwitches = this.calculateIdealNumOfSwitches(idealNumOfHosts);
		
		VmPlacementGroup vmPlacementGroup = 
				new VmPlacementGroup(vms, 
						VmPlacementGroupScope.SWITCH, VmPlacementGroupAffinityType.AFFINITY, VmPlacementGroupEnforcement.BEST_EFFORT, 
						idealNumOfHosts, idealNumOfSwitches);
		
		vmPlacementGroup.setSubmissionDelay(submissionDelay);
		
		vmPlacementGroup.setId(this.uniqueId++);
		
		return vmPlacementGroup;
	}
	
	/**
	 * Calculates the minimum number of {@link Host}s that can be used to place the {@link VmPlacementGroup}.
	 * 
	 * @param numOfVms the number of VMs in the VM placement group
	 * @param cores the number of cores / VM
	 * @param ramMiB the amount of RAM / VM
	 * @param bwMbps the amount of bandwidth / VM
	 * @param storageMiB the amount of storage / VM
	 * @return the minimum number of {@link Host}s that can be used to place the {@link VmPlacementGroup}.
	 */
	public long calculateIdealNumOfHosts(long numOfVms, long cores, long ramMiB, long bwMbps, long storageMiB) {
		
		if(numOfVms == 1)
			return 1;
		
		final long idealVmsPerHostByCore    = cores      == 0 ? Long.MAX_VALUE : this.hostCores / cores;
		final long idealVmsPerHostByRam     = ramMiB     == 0 ? Long.MAX_VALUE : this.hostRamMiB / ramMiB;
		final long idealVmsPerHostByBw      = bwMbps     == 0 ? Long.MAX_VALUE : this.hostBwMbps / bwMbps;
		final long idealVmsPerHostByStorage = storageMiB == 0 ? Long.MAX_VALUE : this.hostStorageMiB / storageMiB;
		
		
		final long asArray[] = {idealVmsPerHostByCore, idealVmsPerHostByRam, idealVmsPerHostByBw, idealVmsPerHostByStorage};
		
		long idealVmsPerHost = asArray[0];
		
		for(int i = 1; i < asArray.length; i++) {
			if(asArray[i] < idealVmsPerHost)
				idealVmsPerHost = asArray[i];
		}
		
		long idealNumOfHosts = (long) Math.ceil((double) numOfVms / (double) idealVmsPerHost);
		
		return idealNumOfHosts;
	}
	
	/**
	 * Calculates the minimum number of switches that can be used to place a {@link VmPlacementGroup}.
	 * 
	 * @param idealNumOfHosts the minimum number of {@link Host}s that can be used to place the {@link VmPlacementGroup}
	 * @return the minimum number of switches that can be used to place a {@link VmPlacementGroup}
	 */
	private long calculateIdealNumOfSwitches(long idealNumOfHosts) {
		
		if(idealNumOfHosts == 1)
			return 1;
		
		return (long) Math.ceil((double) idealNumOfHosts / (double) this.hostsPerSwitch);
	}
	
	
	/**
	 * A {@link UtilizationModelFull} for the {@link Cloudlet}s created in the VMs 
	 */
	final private UtilizationModel utilModelFull = new UtilizationModelFull();
	
	/**
	 * Creates a {@link Cloudlet} for a specific {@link Vm}
	 * 
	 * @param vm the VM which will host the Cloudlet
	 * @param lifetime how long we want the Cloudlet to run
	 */
	public Cloudlet createCloudlet(final Vm vm, final long lifetime) {
		
		// We set the MI parameter of the Cloudlet equal to the VM lifetime
        Cloudlet cloudlet = new CloudletSimple(lifetime, 1)
        							.setFileSize(1)
        							.setOutputSize(1)
        							.setUtilizationModelBw(this.utilModelFull)
        							.setUtilizationModelRam(this.utilModelFull)
        							.setUtilizationModelCpu(this.utilModelFull)
        							.setVm(vm);
        
        cloudlet.setId(this.uniqueId++);
        
        return cloudlet;
	}
	
	/**
	 * Takes as input a file name and a {@link Consumer} function and prints
	 * the output of the function in the file. 
	 */
    public void printToFile (final String filename, final Consumer<PrintStream> printFunction) {

		try {
			
			final File file = new File(this.workingPath + filename);
			
			final FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			
			final PrintStream printStream = new PrintStream(fileOutputStream);
			
			printFunction.accept(printStream);
			
			printStream.close();

			if(file.length() == 0)
				file.delete();
			
		} catch (FileNotFoundException e) {
			
			this.simOut.println("Cannot open PrintStream for file " + this.workingPath + filename + ". Printing to simOut");
			
			printFunction.accept(this.simOut);
		}		
    }
	
	/**
	 * Saves the simulation parameters and the results into files
	 */
    public void saveResults() {

    	this.printToFile("config.txt",
    			i -> this.printConfig(i));
    	
    	this.printToFile("config.txt", 
    			i -> this.getBatchesManager()
    			.getParser()
    			.printDatasetMetadata(i));
    	
    	this.printToFile("config.txt", 
    			i -> this.getBatchesManager()
    			.getStatisticsManager()
    			.printRequestStatistics(i));
    	
    	this.printToFile("config.txt", 
    			i -> this.getBatchesManager()
    			.getStatisticsManager()
    			.printBrokerResults(i));
    	
    	this.printToFile("resourcesUtilizationTrace.csv", 
    			i -> this.getBatchesManager()
    			.getStatisticsManager()
    			.printResourcesUtilizationTrace(i));
    	
    	this.printToFile("requestTypes.csv", 
    			i -> this.getBatchesManager()
    			.getStatisticsManager()
    			.printRequestTypes(i));
    	
    	this.printToFile("lifetimes.csv", 
    			i -> this.getBatchesManager()
    			.getStatisticsManager()
    			.printLifetimeStatistics(i));
    	
    	this.printToFile("interarrivalTimes.csv", 
    			i -> this.getBatchesManager()
    			.getStatisticsManager()
    			.printInterarrivalTimeStatistics(i));
    	
    	this.printToFile("placementTrace.csv", 
    			i -> this.getBatchesManager()
    			.getStatisticsManager()
    			.printVmAllocationPolicyTrace(i));
    	
    	this.printToFile("requestsTrace.csv", 
    			i -> this.getBatchesManager()
    			.getStatisticsManager()
    			.printVmAllocationPolicyRequestSizeTrace(i));
    	
    	this.printToFile("switchesHistograms.csv", 
    			i -> this.getBatchesManager()
    			.getStatisticsManager()
    			.printNumOfSwitchesHistograms(i));
    }
    
    /**
     * Prints the configuration parameters
     */
    private void printConfig(PrintStream out) {
    	
    	out.println("----------- Simulation Config -----------");
    	out.println("Host cores   : " + hostCores);
    	out.println("Host ram     : " + hostRamMiB / 1024 + " GiB");
    	out.println("Host bw      : " + hostBwMbps / 1000 + " Gbps");
    	out.println("Host storage : " + hostStorageMiB / 1024 + " GiB");
    	out.println();
    	out.println("Switches     : " + numOfSwitches);
    	out.println("Hosts/switch : " + hostsPerSwitch);
    	out.println();
    	out.println("Total hosts   : " + totalHosts);
    	out.println("Total cores   : " + totalCores);
    	out.println("Total ram     : " + totalRamMiB / 1024 + " GiB");
    	out.println("Total bw      : " + totalBwMbps / 1000 + " Gbps");
    	out.println("Total storage : " + totalStorageMiB / 1024 + " GiB");
    	out.println();
    	out.println("Sim. end time limit : " + (double) simEndTimeLimitSec / 3600d / 24d + " days");
    	out.println("Start times from    : " + startTimesFromSec / 3600d / 24d + " days");
    	out.println("Start times until   : " + startTimesUntilSec / 3600d / 24d + " days");
    	out.println("Vm endTime cap      : " + vmCapSec / 3600d / 24d + " days");
    	out.println();
    	out.println("Min hosts per request        : " + minHostsPerRequest);
    	out.println("Min VMs per request          : " + minVmsPerRequest);
    	out.println("Vm placement group threshold : " + groupThresholdSec + " sec");
    	out.println("Batch size                   : " + batchSize);
    	out.println();
    	out.println("Vm allocation policy       : " + vmAllocationPolicyType);
    	out.println("Quit on allocation failure : " + quitOnAllocationFailure);
    	out.println();
    	out.println("Min time between events : " + minTimeBetweenEvents);
    }
	
    
    // setters for the configuration parameters
    
	public void setWorkingPath(String workingPath) {
		this.workingPath = workingPath;
	}

	public void setTracePath(String tracePath) {
		this.tracePath = tracePath;
	}

	public void setHostCores(long hostCores) {
		this.hostCores = hostCores;
	}

	public void setHostRamMiB(long hostRamMiB) {
		this.hostRamMiB = hostRamMiB;
	}

	public void setHostBwMbps(long hostBwMbps) {
		this.hostBwMbps = hostBwMbps;
	}

	public void setHostStorageMiB(long hostStorageMiB) {
		this.hostStorageMiB = hostStorageMiB;
	}

	public void setNumOfSwitches(long numOfSwitches) {
		this.numOfSwitches = numOfSwitches;
	}

	public void setHostsPerSwitch(long hostsPerSwitch) {
		this.hostsPerSwitch = hostsPerSwitch;
	}

	public void setSimEndTimeLimitSec(long simEndTimeLimitSec) {
		this.simEndTimeLimitSec = simEndTimeLimitSec;
	}
	
	public void setStartTimesFromSec(long startTimesFromSec) {
		this.startTimesFromSec = startTimesFromSec;
	}

	public void setStartTimesUntilSec(long startTimesUntilSec) {
		this.startTimesUntilSec = startTimesUntilSec;
	}

	public void setVmCapSec(long vmCapSec) {
		this.vmCapSec = vmCapSec;
	}

	public void setMinHostsPerRequest(long minHostsPerRequest) {
		this.minHostsPerRequest = minHostsPerRequest;
	}
	
	public void setMinVmsPerRequest(long minVmsPerRequest) {
		this.minVmsPerRequest = minVmsPerRequest;
	}

	public void setGroupThresholdSec(long groupThresholdSec) {
		this.groupThresholdSec = groupThresholdSec;
	}

	public void setBatchSize(long batchSize) {
		this.batchSize = batchSize;
	}

	public void setVmAllocationPolicyType(VmAllocationPolicyWithPlacementGroups vmAllocationPolicyType) {
		this.vmAllocationPolicyType = vmAllocationPolicyType;
	}
	
	public void setQuitOnAllocationFailure(boolean quitOnAllocationFailure) {
		this.quitOnAllocationFailure = quitOnAllocationFailure;
	}
	
	public void setMinTimeBetweenEvents(double minTimeBetweenEvents) {
		this.minTimeBetweenEvents = minTimeBetweenEvents;
	}
	
	public void setPrintToFile(boolean printToFile) {
		this.printToFile = printToFile;
	}
	
	
	// getters for the configuration parameters
	
	public String getWorkingPath() {
		return this.workingPath;
	}
	
	public String getTracePath() {
		return this.tracePath;
	}
	
	public long getHostCores() {
		return this.hostCores;
	}
	
	public long getHostRamMiB() {
		return this.hostRamMiB;
	}
	
	public long getHostBwMbps() {
		return this.hostBwMbps;
	}
	
	public long getHostStorageMiB() {
		return this.hostStorageMiB;
	}
	
	public long getNumOfSwitches() {
		return this.numOfSwitches;
	}
	
	public long getHostsPerSwitch() {
		return this.hostsPerSwitch;
	}
	
	public long getSimEndTimeLimitSec() {
		return this.simEndTimeLimitSec;
	}
	
	public long getStartTimesFromSec() {
		return startTimesFromSec;
	}

	public long getStartTimesUntilSec() {
		return startTimesUntilSec;
	}
	
	public long getVmCapSec() {
		return this.vmCapSec;
	}
	
	public long getMinHostsPerRequest() {
		return this.minHostsPerRequest;
	}
	
	public long getMinVmsPerRequest() {
		return this.minVmsPerRequest;
	}
	
	public long getGroupThresholdSec() {
		return this.groupThresholdSec;
	}
	
	public long getBatchSize() {
		return this.batchSize;
	}
	
	public boolean quitOnAllocationFailure() {
		return quitOnAllocationFailure;
	}

	public double getMinTimeBetweenEvents() {
		return minTimeBetweenEvents;
	}

	
	//	
	public long getTotalHosts() {
		return this.totalHosts;
	}
	
	public long getTotalCores() {
		return this.totalCores;
	}

	public long getTotalRamMiB() {
		return this.totalRamMiB;
	}

	public long getTotalBwMbps() {
		return totalBwMbps;
	}

	public long getTotalStorageMiB() {
		return this.totalStorageMiB;
	}

	
	//
	public CloudSim getSimulation() {
		return this.simulation;
	}
	
	public DatacenterBroker getBroker () {
		return this.broker;
	}
	
	public VmAllocationPolicy getVmAllocationPolicy() {
		return this.vmAllocationPolicy;
	}
	
	public TracesBatchesManager getBatchesManager() {
		return this.batchesManager;
	}

	public PrintStream getSimOut() {
		return simOut;
	}

	public double getInitializationCompletionTimestamp() {
		return initializationCompletionTimestamp;
	}
	
	public List<Host> getHosts() {
		return this.hosts;
	}
	
	public boolean printToFile() {
		return this.printToFile;
	}
}
