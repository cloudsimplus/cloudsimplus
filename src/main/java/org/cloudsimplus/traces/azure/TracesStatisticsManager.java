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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyRequestStatus;
import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyWithPlacementGroups;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.utilization.ResourcesUtilizationRecord;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroup;
import org.cloudsimplus.builders.tables.vmstatistics.TimeStatsTableBuilder;
import org.cloudsimplus.builders.tables.vmstatistics.VmTypeRecordsTableBuilder;
import org.cloudsimplus.traces.vmallocationpolicywithgroups.VmAllocationPolicyTraceRecord;
import org.cloudsimplus.traces.vmallocationpolicywithgroups.VmAllocationPolicyTraceTableBuilder;

/**
 * A class for facilitating the statistics/traces collection by keeping all 
 * the measurements in a single place.
 * 
 * @see TracesStatisticsManager
 * @see TracesBatchesManager
 * @see TracesParser
 *
 * @since CloudSim Plus 7.3.2
 *
 * @author Pavlos Maniotis
 */
public class TracesStatisticsManager {

	/**
	 * see {@link TracesSimulationManager}
	 */
	final private TracesSimulationManager simulationManager;

	/**
	 * {@link List} with the {@link VmAllocationPolicyTraceRecord}s
	 */
	final private List<VmAllocationPolicyTraceRecord> vmAllocationPolicyTrace = new ArrayList<VmAllocationPolicyTraceRecord>();
	
	/**
	 * {@link Map} with associations between {@link Vm}s and {@link VmAllocationPolicyTraceRecord}s
	 */
	final private Map<Vm, VmAllocationPolicyTraceRecord> vmToAllocationPolicyRecordsMap = new IdentityHashMap<Vm, VmAllocationPolicyTraceRecord>();
	
	/**
	 * {@link List} with the sizes of the {@link VmAllocationPolicyRequestStatus#SUCCESS successful} requests. 
	 * Once saved to a csv file can be used to create plots with the number of VMs versus time
	 */
	final private List<String> successfulRequestsSizesTrace = new ArrayList<String>();
	
	/**
	 * {@link List} with the sizes of the {@link VmAllocationPolicyRequestStatus#FAIL failed} requests. 
	 * Once saved to a csv file can be used to create plots with the number of VMs versus time
	 */
	final private List<String> failedRequestsSizesTrace = new ArrayList<String>();

	/**
	 * {@link List} with the {@link ResourcesUtilizationRecord}s
	 */
    final private List<ResourcesUtilizationRecord> resourcesUtilizationTrace =
    		new ArrayList<ResourcesUtilizationRecord>();
    
    /**
     * helper counter for the {@link #resourcesUtilizationTrace}. See {@link #updateResourcesUtilizationTrace(double)}
     */
    private long   lastAllocatedCores   = 0;

    /**
     * helper counter for the {@link #resourcesUtilizationTrace}. See {@link #updateResourcesUtilizationTrace(double)}
     */
    private long   lastAllocatedRam     = 0;
    
    /**
     * helper counter for the {@link #resourcesUtilizationTrace}. See {@link #updateResourcesUtilizationTrace(double)}
     */
    private long   lastAllocatedBw      = 0;
    
    /**
     * helper counter for the {@link #resourcesUtilizationTrace}. See {@link #updateResourcesUtilizationTrace(double)}
     */
    private long   lastAllocatedStorage = 0;
    
    /**
     * helper counter for the {@link #resourcesUtilizationTrace}. See {@link #updateResourcesUtilizationTrace(double)}
     */
    private double lastAllocationTime   = 0;
    
    
    /**
     * Total number of requests
     */
    private long totalRequests = 0;

    /**
     * Total number of requests with negative start times 
     */
    private long negativeRequests = 0;
    
    /**
     * Total number of single {@link Vm} requests 
     */
    private long singleVmRequests = 0;
    
    /**
     * Total number of single {@link Vm} requests with negative start times
     */
    private long negativeSingleVmRequests = 0;

    /**
     * Total number of {@link VmGroup} requests
     */
    private long vmGroupRequests = 0;
    
    /**
     * Total number of {@link VmGroup} requests with negative start times
     */
    private long negativeVmGroupRequests = 0;
    
    /**
     * Total number of {@link Vm}s from {@link VmGroup} requests
     */
    private long vmsFromVmGroupRequests = 0; 
    
    /**
     * Total number of {@link VmPlacementGroup} requests
     */
    private long vmPlacementGroupRequests = 0;
    
    /**
     * Total number of {@link VmPlacementGroup} requests with negative start times
     */
    private long negativeVmPlacementGroupRequests = 0;
    
    /**
     * Total number of {@link Vm}s from {@link VmPlacementGroup} requests
     */
    private long vmsFromVmPlacementGroupRequests = 0;
    
    /**
     * Histogram with info about the VM types for single {@link Vm} requests
     */
    final private Map<String, Long> vmTypes = new HashMap<String, Long>();

    /**
     * Histogram with info about the VM types for {@link VmGroup} requests
     */
    final private Map<String, Long> vmGroupTypes = new HashMap<String, Long>();
    
    /**
     * Histogram with info about the VM types for {@link VmPlacementGroup} requests
     */
    final private Map<String, Long> vmPlacementGroupTypes = new HashMap<String, Long>();
    
    /**
     * The arrival times of single {@link Vm}s
     */
    private List<Long> vmArrivalTimes = new ArrayList<Long>();
    
    /**
     * The lifetimes of single {@link Vm}s
     */
    private List<Long> vmLifetimes = new ArrayList<Long>();
    
    /**
     * The arrival times of the {@link VmGroup}s
     */
    private List<Long> vmGroupArrivalTimes = new ArrayList<Long>();
    
    /**
     * The lifetimes of the {@link VmGroup}s
     */
    private List<Long> vmGroupLifetimes = new ArrayList<Long>(); 
    
    /**
     * The arrival times of the {@link VmPlacementGroup}s
     */
    private List<Long> vmPlacementGroupArrivalTimes = new ArrayList<Long>();
    
    /**
     * The lifetimes of the {@link VmPlacementGroup}s
     */
    private List<Long> vmPlacementGroupLifetimes = new ArrayList<Long>(); 
    
    
    /**
     * Constructor to initialize the {@link TracesSimulationManager}
     */
    public TracesStatisticsManager(final TracesSimulationManager simulationManager) {
    	    	
    	this.simulationManager = simulationManager;
	}
    
	/**
	 * Updates the traces about the {@link VmAllocationPolicyWithPlacementGroups}
	 * 
	 * @param vm the request for which we update the traces
	 */
    public void updateAllocationPolicyTraces (final Vm vm) {
                
    	final VmAllocationPolicyTraceRecord newResultsRecord = new VmAllocationPolicyTraceRecord (vm);
        
    	this.vmAllocationPolicyTrace.add(newResultsRecord);
    	
    	this.vmToAllocationPolicyRecordsMap.put(vm, newResultsRecord);
    	
		final String request = 
				Double.toString((double) newResultsRecord.getArrivalTime() / 3600d / 24d) + "," + 
		        Long.toString((newResultsRecord.getNumOfVms() * newResultsRecord.getNumOfCores())) + "," +
		        Long.toString(newResultsRecord.getNumOfVms()) + "," +
				Long.toString(newResultsRecord.getIdealNumOfHosts());

		final String requestToZero = 
				Double.toString(((double) newResultsRecord.getArrivalTime() / 3600d / 24d)) + "," + 
		        Integer.toString(-10_000) + "," + Integer.toString(-10_000) + "," + Integer.toString(-10_000);
		
		if(newResultsRecord.getRequestStatus() == VmAllocationPolicyRequestStatus.SUCCESS) {
			this.successfulRequestsSizesTrace.add(requestToZero);
			this.successfulRequestsSizesTrace.add(request);
			this.successfulRequestsSizesTrace.add(requestToZero);
		}
		else {
			this.failedRequestsSizesTrace.add(requestToZero);
			this.failedRequestsSizesTrace.add(request);
			this.failedRequestsSizesTrace.add(requestToZero);
		}

    }
    
	/**
	 * Updates the statistics about a new {@link Vm} request
	 * 
	 * @param vm the new request
	 * @param arrivalTime the arrival time of the request
	 * @param lifetime the lifetime of the request
	 */
	public void updateStatisticsForRequest(final Vm vm, final long arrivalTime, final long lifetime) {

		if(vm instanceof VmPlacementGroup) {
			
			final VmPlacementGroup vmPlacementGroup = (VmPlacementGroup) vm;
			this.vmPlacementGroupRequests++;
			
			if(arrivalTime < 0) {
				this.negativeRequests++;
				this.negativeVmPlacementGroupRequests++;
			}
			
			this.vmsFromVmPlacementGroupRequests += vmPlacementGroup.getVmList().size();
			
			final long cores = vmPlacementGroup.getVmList().get(0).getNumberOfPes();
			final long ram = vmPlacementGroup.getVmList().get(0).getRam().getCapacity();
			final long bw = vmPlacementGroup.getVmList().get(0).getBw().getCapacity();
			final long storage = vmPlacementGroup.getVmList().get(0).getStorage().getCapacity();
			
			final String vmType = Long.toString(cores) + "," + Long.toString(ram) + "," + Long.toString(bw) + "," + Long.toString(storage);
			
			this.vmPlacementGroupTypes.computeIfPresent(vmType, (k, v) -> v + 1);
			this.vmPlacementGroupTypes.putIfAbsent(vmType, 1L);
			
			this.vmPlacementGroupArrivalTimes.add(arrivalTime); 
			this.vmPlacementGroupLifetimes.add(lifetime);
		}
		else if (vm instanceof VmGroup) {
			
			final VmGroup vmGroup = (VmGroup) vm;
			this.vmGroupRequests++;
			
			if(arrivalTime < 0) {
				this.negativeRequests++;
				this.negativeVmGroupRequests++;
			}
			
			this.vmsFromVmGroupRequests += vmGroup.getVmList().size();
			
			final long cores = vmGroup.getNumberOfPes();
			final long ram = vmGroup.getRam().getCapacity();
			final long bw = vmGroup.getBw().getCapacity();
			final long storage = vmGroup.getStorage().getCapacity();
			
			final String vmType = Long.toString(cores) + "," + Long.toString(ram) + "," + Long.toString(bw) + "," + Long.toString(storage);
			
			this.vmGroupTypes.computeIfPresent(vmType, (k, v) -> v + 1);
			this.vmGroupTypes.putIfAbsent(vmType, 1L);
			
			this.vmGroupArrivalTimes.add(arrivalTime); 
			this.vmGroupLifetimes.add(lifetime);
		}
		else {
			
			this.singleVmRequests++;
			
			if(arrivalTime < 0) {
				this.negativeRequests++;
				this.negativeSingleVmRequests++;
			}
			
			final long cores = vm.getNumberOfPes();
			final long ram = vm.getRam().getCapacity();
			final long bw = vm.getBw().getCapacity();
			final long storage = vm.getStorage().getCapacity();

			final String vmType = Long.toString(cores) + "," + Long.toString(ram) + "," + Long.toString(bw) + "," + Long.toString(storage);
			
			this.vmTypes.computeIfPresent(vmType, (k, v) -> v + 1);
			this.vmTypes.putIfAbsent(vmType, 1L);
			
			this.vmArrivalTimes.add(arrivalTime); 	
			this.vmLifetimes.add(lifetime);
		}

	}
    
	/**
	 * Updates the {@link #resourcesUtilizationTrace}
	 * 
	 * @param time the time of the update
	 */
    public void updateResourcesUtilizationTrace(final double time) {

        if(time < this.lastAllocationTime)
        	return;
            	  
        final List<Host> hosts = this.simulationManager.getHosts();
        
        final long allocatedCores = hosts.stream().mapToLong(host -> host.getBusyPesNumber()).sum();
        final long allocatedRam   = hosts.stream().mapToLong(host -> host.getRam().getAllocatedResource()).sum();
        final long allocatedBw    = hosts.stream().mapToLong(host -> host.getBw().getAllocatedResource()).sum();
		final long allocatedStorage = hosts.stream().mapToLong(host -> host.getStorage().getAllocatedResource()).sum();
	    
    	if(allocatedCores  != this.lastAllocatedCores 
    	   || allocatedRam != this.lastAllocatedRam
    	   || allocatedBw  != this.lastAllocatedBw
    	   || allocatedStorage != this.lastAllocatedStorage) {
    		
    		final ResourcesUtilizationRecord firstEntry = 
    				new ResourcesUtilizationRecord(time, this.lastAllocatedCores, 
    						this.lastAllocatedRam, this.lastAllocatedBw, this.lastAllocatedStorage); 
    		
    		final ResourcesUtilizationRecord secondEntry = 
    				new ResourcesUtilizationRecord(time, allocatedCores, allocatedRam, allocatedBw, allocatedStorage); 
    		
    		this.resourcesUtilizationTrace.add(firstEntry);
    		this.resourcesUtilizationTrace.add(secondEntry);
    		
    		
	        this.lastAllocatedCores = allocatedCores;
	        this.lastAllocatedRam = allocatedRam;
	        this.lastAllocatedBw = allocatedBw;
	        this.lastAllocatedStorage = allocatedStorage;
    	}

        this.lastAllocationTime = time;
    }
    
    /**
     * Setter function for {@link #totalRequests}
     */
	public void setNumOfTotalRequests(final long numOfRequests) {
		
		this.totalRequests = numOfRequests;
	}
    
	/**
	 * Prints the statistics about the requests
	 */
    public void printRequestStatistics(final PrintStream out) {
    	    	
    	out.println();
        out.println("----------- Created requests ------------");
        out.println("Total requests                     : " + this.totalRequests);
        out.println("Negative requests                  : " + this.negativeRequests);
        out.println("Single Vm requests                 : " + this.singleVmRequests + " (negative: " + this.negativeSingleVmRequests + ")");
        out.println("VmGroup requests                   : " + this.vmGroupRequests + " (negative: " + this.negativeVmGroupRequests + ")");
        out.println("Vms from VmGroup requests          : " + this.vmsFromVmGroupRequests);
        out.println("VmPlacementGroup requests          : " + this.vmPlacementGroupRequests + " (negative: " + this.negativeVmPlacementGroupRequests + ")");
        out.println("Vms from VmPlacementGroup requests : " + this.vmsFromVmPlacementGroupRequests);
        
    }
    
	/**
	 * Prints the statistics from the {@link DatacenterBroker}
	 */
    public void printBrokerResults(final PrintStream out) {
        /*final List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();
        finishedCloudlets.sort(Comparator.comparingLong(cloudlet -> cloudlet.getVm().getId()));
        new CloudletsTableBuilder(finishedCloudlets).build();*/
    	
    	final int createdVms   = this.simulationManager.getBroker().getVmCreatedList().size();
    	final int waitingVms   = this.simulationManager.getBroker().getVmWaitingList().size();
    	final int executingVms = this.simulationManager.getBroker().getVmExecList().size();
    	final int failedVms    = this.simulationManager.getBroker().getVmFailedList().size();
        
    	out.println();
        out.println("-------- Broker statistics (vms) --------");
        out.println("Created vms   : " + createdVms);
        out.println("Waiting vms   : " + waitingVms);
        out.println("Executing vms : " + executingVms);
        out.println("Failed vms    : " + failedVms);


        final int submittedCloudlets = this.simulationManager.getBroker().getCloudletSubmittedList().size();
        final int createdCloudlets   = this.simulationManager.getBroker().getCloudletCreatedList().size();
        final int waitingCloudlets   = this.simulationManager.getBroker().getCloudletWaitingList().size();
        final int finishedCloudlets  = this.simulationManager.getBroker().getCloudletFinishedList().size();

        
    	out.println();
        out.println("----- Broker statistics (cloudlets) -----");
        out.println("Submitted cloudlets : " + submittedCloudlets);
        out.println("Created cloudlets   : " + createdCloudlets);
        out.println("Waiting cloudlets   : " + waitingCloudlets);
        out.println("Finished cloudlets  : " + finishedCloudlets);
    }
    
    /**
     * Takes as input a {@link PrintStream} and prints the histograms with the request types
     */
    public void printRequestTypes(final PrintStream out) {
    	
    	if (!this.vmTypes.isEmpty()) {
    		out.println("Histogram for single VM request types");
    		new VmTypeRecordsTableBuilder(this.getTypeStatsAsList(this.vmTypes), out).build();
    		out.println();
    		out.println();
    	}
    		
    	if(!this.vmGroupTypes.isEmpty()) {
    		out.println("Histogram for VmGroup request types");
    		new VmTypeRecordsTableBuilder(this.getTypeStatsAsList(this.vmGroupTypes), out).build();
    		out.println();
    		out.println();
    	}

    	if(!this.vmPlacementGroupTypes.isEmpty()) {
    		out.println("Histogram for VmPlacementGroup request types");
    		new VmTypeRecordsTableBuilder(this.getTypeStatsAsList(this.vmPlacementGroupTypes), out).build();
    	}
    	               
    }
    
    /**
     * Takes as input a {@link PrintStream} and prints the lifetime statistics
     */
    public void printLifetimeStatistics(final PrintStream out) {
    	
    	if(!this.vmLifetimes.isEmpty()) {
    		out.println("Single VM lifetimes");
    		new TimeStatsTableBuilder(this.vmLifetimes, out).build();
    		out.println();
    		out.println();
    	}
    	
    	if(!this.vmGroupLifetimes.isEmpty()) {
    		out.println("VmGroup lifetimes");
    		new TimeStatsTableBuilder(this.vmGroupLifetimes, out).build();
    		out.println();
    		out.println();
    	}
    	
    	if(!this.vmPlacementGroupLifetimes.isEmpty()) {
    		out.println("VmPlacementGroupLifetimes lifetimes");
    		new TimeStatsTableBuilder(this.vmPlacementGroupLifetimes, out).build();
    	}
    }
    
    /**
     * Takes as input a {@link PrintStream} and prints the interarrival time statistics
     */
    public void printInterarrivalTimeStatistics (final PrintStream out) {
    	
    	if(!this.vmArrivalTimes.isEmpty()) {
        	List<Long> interarrivalTimes = this.calculateInterarrivalTimes(this.vmArrivalTimes); 
    		out.println("Single VM interarrival times");
            new TimeStatsTableBuilder(interarrivalTimes, out).build();
            out.println();
            out.println();
    	}

    	if(!this.vmGroupArrivalTimes.isEmpty()) {
        	List<Long> interarrivalTimes = this.calculateInterarrivalTimes(this.vmGroupArrivalTimes); 
    		out.println("VmGroup interarrival times");
            new TimeStatsTableBuilder(interarrivalTimes, out).build();
            out.println();
            out.println();
    	}
    	
    	if(!this.vmPlacementGroupArrivalTimes.isEmpty()) {
        	List<Long> interarrivalTimes = this.calculateInterarrivalTimes(this.vmPlacementGroupArrivalTimes); 
    		out.println("VmPlacementGroup interarrival times");
            new TimeStatsTableBuilder(interarrivalTimes, out).build();
    	}
    }
    
    /**
     * Takes as input a {@link PrintStream} and prints the {@link #resourcesUtilizationTrace}
     */
    public void printResourcesUtilizationTrace(final PrintStream out) {
    	
    	if(this.resourcesUtilizationTrace.isEmpty())
    		return;
    	
    	this.updateResourcesUtilizationTrace(this.simulationManager.getSimulation().clock());
    	
    	out.println("Time (sec), Time (days), core allocation, ram allocation (%), bw allocation (%), storage allocation (%)");
		out.println("0, 0, 0, 0, 0, 0");
		
		for (ResourcesUtilizationRecord entry : this.resourcesUtilizationTrace) {

			double timeInSec         = entry.getTimeStamp();
			double timeInDays        = timeInSec / 3600d / 24d;
			double coreAllocation    = 100d * (double) entry.getAllocatedCores() / (double) this.simulationManager.getTotalCores();
			double ramAllocation     = 100d * (double) entry.getAllocatedRamMiB() / (double) this.simulationManager.getTotalRamMiB();
			double bwAllocation      = 100d * (double) entry.getAllocatedBwMbps() / (double) this.simulationManager.getTotalBwMbps();
			double storageAllocation = 100d * (double) entry.getAllocatedStorageMiB() / (double) this.simulationManager.getTotalStorageMiB();

			
			out.println(timeInSec + "," + timeInDays + "," + 
					coreAllocation + "," + ramAllocation + "," + 
					bwAllocation + "," + storageAllocation);
		}
    }
    
    /**
     * Takes as input a {@link PrintStream} and prints two histograms:
     * <pre> 
     * (1) a histogram with the number of switches that can be 
     *     ideally used for placing the requests
     * (2) a histogram with the number of switches that have been 
     *     used for placing the requests
     * </pre> 
     */
    public void printNumOfSwitchesHistograms(final PrintStream out) {
    	
    	Comparator<Map.Entry<List<String>, List<VmAllocationPolicyTraceRecord>>> comparatorByStatus =
    			Comparator.comparing(i -> i.getKey().get(0));

    	Comparator<Map.Entry<List<String>, List<VmAllocationPolicyTraceRecord>>> comparatorBySwitchSize = 
    			comparatorByStatus.thenComparing(i -> Integer.valueOf(i.getKey().get(1)));
    	
    	
    	Map<List<String>, List<VmAllocationPolicyTraceRecord>> histogram1 = 
    			this.vmAllocationPolicyTrace.stream().collect(Collectors.groupingBy(r -> 
    				Arrays.asList(r.getRequestStatus().toString(), Long.toString(r.getIdealNumOfSwitches()))));
    	
    	Map<List<String>, List<VmAllocationPolicyTraceRecord>> histogram2 = 
    			this.vmAllocationPolicyTrace.stream().collect(Collectors.groupingBy(r -> 
    				Arrays.asList(r.getRequestStatus().toString(), Long.toString(r.getNumOfSwitches()))));
    	
    	
    	out.println("Ideal num of switches histogram");
    	out.println("Request status, Ideal num of switches, Count");
    	
    	histogram1.entrySet().stream().sorted(comparatorBySwitchSize).forEach(es -> out.println(es.getKey().get(0) + "," + es.getKey().get(1) + "," + es.getValue().size()));
    	
    	out.println();
    	out.println();
    	
    	out.println("Num of switches histogram");
    	out.println("Request status, Num of switches, Count");
    	
    	histogram2.entrySet().stream().sorted(comparatorBySwitchSize).forEach(es -> out.println(es.getKey().get(0) + "," + es.getKey().get(1) + "," + es.getValue().size()));
    }
    
    /**
     * Takes as input a {@link PrintStream} and prints the {@link #successfulRequestsSizesTrace} 
     * and {@link #failedRequestsSizesTrace} traces
     */
    public void printVmAllocationPolicyRequestSizeTrace(final PrintStream out) {
    	
    	if(this.successfulRequestsSizesTrace.isEmpty() &&
    	   this.failedRequestsSizesTrace.isEmpty())
    		return;
    
    	out.println("Successful, , , , , Failed, , , , ");
    	out.println("Time (days), Total cores, Total vms, Total ideal hosts, , Time (days), Total cores, Total vms, Total ideal hosts, ");
    	
    	int maxIndex = this.successfulRequestsSizesTrace.size();
    	
    	if(this.failedRequestsSizesTrace.size() > this.successfulRequestsSizesTrace.size()) {
    		
    		maxIndex = this.failedRequestsSizesTrace.size();
    	}
    	
    	for (int i = 0; i < maxIndex; i++) {
    		
    		if(i < this.successfulRequestsSizesTrace.size())
    			out.print(this.successfulRequestsSizesTrace.get(i));
    		else
    			out.print(",,,");
    		
    		if(i < this.failedRequestsSizesTrace.size())
    			out.print(",," + this.failedRequestsSizesTrace.get(i));
    		
    		out.print("\n");
    	}
    }
    
    /**
     * Takes as input a {@link PrintStream} and prints the {@link #vmAllocationPolicyTrace} 
     */
    public void printVmAllocationPolicyTrace(final PrintStream out) {
    	
    	if(this.vmAllocationPolicyTrace.isEmpty())
    		return;
    	
		new VmAllocationPolicyTraceTableBuilder(this.vmAllocationPolicyTrace, out).build();
    }
	
    /**
     * Takes as input a histogram in the form of a {@link Map} with 
     * info about the VM types and returns a {@link List} with the 
     * entries ({@link Entry}) of the {@link Map} 
     */
	private List<Map.Entry<String, Long>> getTypeStatsAsList(final Map<String, Long> vmTypes) {
		
		return vmTypes.entrySet().stream().collect(Collectors.toList());
	}
	
	/**
	 * Takes as input a {@link List} with some arrival times and returns 
	 * a {@link List} with the corresponding interarrival times. 
	 */
	private List<Long> calculateInterarrivalTimes(final List<Long> arrivalTimes) {
		
		final List<Long> interarrivalTimes = new ArrayList<Long>();
		
	    for(int i = 0; i < arrivalTimes.size() - 1; i++) {
	    	
	    	final long interarrivalTime = 
	    			arrivalTimes.get(i+1) - arrivalTimes.get(i);
	    	
	    	interarrivalTimes.add(interarrivalTime);
	    }
	    
	    return interarrivalTimes;
	}
	
	/**
	 * Takes as input a {@link Vm} and returns the {@link VmAllocationPolicyTraceRecord} 
	 * associated with it. 
	 */
	public VmAllocationPolicyTraceRecord getVmAllocationPolicyTraceRecord(final Vm vm) {
		
		return this.vmToAllocationPolicyRecordsMap.get(vm);
	}
}
