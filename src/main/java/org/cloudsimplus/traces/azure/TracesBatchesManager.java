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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroup;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.VmEventInfo;

/**
 * A class for handling the dynamic submission of VM requests to the {@link DatacenterBroker}. 
 * The user can define a batch size and the requests are dynamically submitted in groups of 
 * that size. A batch size of 1 means that the requests are submitted one by one. A batch size
 * of 100 means that the requests are submitted in groups of 100. 
 * 
 * This solution has been implemented because the simulator becomes slow if a big number of 
 * requests is submitted at the beginning of the simulation. In contrast, the simulator  
 * becomes significantly faster if smaller batches are submitted dynamically. This is probably 
 * related to the way the requests are handled by the {@link DatacenterBroker}. 
 * 
 * <b> TODO: This has not been investigated further and wore work is needed to assess if a 
 * similar solution would be beneficial at the broker level.</b>
 * 
 * @see TracesSimulationManager
 * @see TracesParser
 * @see TracesStatisticsManager
 *
 * @since CloudSim Plus 7.3.2
 *
 * @author Pavlos Maniotis
 */
public class TracesBatchesManager {
	
	/** 
	 * See {@link TracesSimulationManager} 
	 */
	final private TracesSimulationManager simulationManager;
	
	/**
	 * See {@link TracesParser} 
	 */
    final private TracesParser parser;
    
    /**
     * See {@link TracesStatisticsManager}
     */
	final private TracesStatisticsManager statisticsManager;

	/**
	 * A {@link List} with the request batches
	 */
    final private List<List<Vm>> requestBatches;
    
    /**
     * Number of submitted batches 
     */
    private int submittedBatches = 0;
    
    /**
     * Any potential lag until the submission of the first batch 
     */
    private double startupLag = 0;
	
    /**
     * A {@link Map} with the requests and their lifetimes
     */
    final private Map<Vm, Long> requestsToLifetimes = new IdentityHashMap<Vm, Long>();    
    
    /**
     * Constructor to initialize the {@link TracesSimulationManager} and
     * to add the necessary listeners for the simulation events. The {@link TracesBatchesManager}
     * monitors the events in order to now when to submit the request batches.
     */
    public TracesBatchesManager(final TracesSimulationManager simulationManager) {
    	
    	this.simulationManager = simulationManager;
    	this.simulationManager.getSimulation().addOnSimulationStartListener(this::simulationStartListener);
    	this.simulationManager.getSimulation().addOnEventProcessingListener(this::simulationEventListener);
    	    	
    	
    	this.parser = new TracesParser(simulationManager);
    	this.statisticsManager = new TracesStatisticsManager(simulationManager);

    	this.requestBatches = this.createRequestBatches();
    }
    
	/**
	 * Creates and returns a {@link List} with the request batches from the dataset
	 */
	private List<List<Vm>> createRequestBatches() {

		final List<Vm> requests = this.createRequestsFromRecords();

		final List <List<Vm>> batches = new LinkedList<List<Vm>>();
	    
	    List<Vm> batch = new LinkedList<Vm>();
	    
		for (Iterator<Vm> it = requests.iterator(); it.hasNext();) {
				
		    Vm vm = it.next();
		    
		    batch.add(vm);
		    
		    if(batch.size() == this.simulationManager.getBatchSize()) {
		    			    	
		    	batches.add(batch);
		    	
		    	batch = new LinkedList<Vm>();
		    }
		}
		
		if(!batch.isEmpty())	
	    	batches.add(batch);
		
		
		return batches;
	}
	
	/**
	 * Creates a {@link List} of requests from the dataset
	 */
	private List<Vm> createRequestsFromRecords () {
		
		final List<Vm> requests = new ArrayList<Vm>();
				
		for(Iterator<List<VmRecord>> it = this.parser.getVmRecords().iterator(); it.hasNext();) {
			
			final List<VmRecord> record = it.next();
						
			Vm vm = this.createRequest(record);
			
			if(vm != null)
				requests.add(vm);
		}
		
		this.statisticsManager.setNumOfTotalRequests(requests.size()); 
		
		return requests;
	}
	
	/**
	 * Takes as input a trace record in the form of {@link List} and creates a request. 
	 * If the list contains one record it creates a single {@link Vm} request,
	 * while it creates a {@link VmPlacementGroup} in case it contains more than 
	 * one records. 
	 */
	private Vm createRequest(final List<VmRecord> record) {
		
		final long startTimeStats = record.get(0).getStartTime();
		long startTime = startTimeStats;

		if(startTimeStats < 0)
			startTime = 0;
		

		final long endTimeStats = record.get(0).getEndTime();
		long endTime = endTimeStats;
		
		if(endTime > this.simulationManager.getSimEndTimeLimitSec())
			endTime = this.simulationManager.getSimEndTimeLimitSec();
		
		final long vmTypeId = record.get(0).getTypeId();

		// The first vmType maximizes the core utilization since the vmTypes are sorted in descending order based on the requested core resources. 
		VmTypeRecord vmType = this.parser.getVmTypes().get(vmTypeId).get(0);
		
		long cores = (int) Math.round( vmType.getCore() * (double) this.simulationManager.getHostCores() );
		if (cores == 0) cores = 1; //TODO declare somewhere default values?
		
		long ramMiB = (long) Math.round( vmType.getMemory() * (double) this.simulationManager.getHostRamMiB() );
		if (ramMiB == 0) ramMiB = 4_096; //TODO declare somewhere default values?
		
		long bwMbps = (long) Math.round( vmType.getNic() * (double) this.simulationManager.getHostBwMbps() );
		if(bwMbps == 0) bwMbps = 12_500; //TODO declare somewhere default values?
		
		long storageMiB = 0; // TODO storage is set to zero since it is not taken into account for now 
		
		long lifetime = endTime - startTime;

		if(lifetime <= Math.round(this.simulationManager.getMinTimeBetweenEvents()))
			lifetime = Math.round(this.simulationManager.getMinTimeBetweenEvents()) + 1l;
		
		final long numOfVms = record.size();

		long idealNumOfHosts = this.simulationManager.calculateIdealNumOfHosts(numOfVms, cores, ramMiB, bwMbps, storageMiB);
		
		
		if(startTimeStats < this.simulationManager.getStartTimesFromSec() 
		  || startTimeStats > this.simulationManager.getStartTimesUntilSec() 
		  || idealNumOfHosts < this.simulationManager.getMinHostsPerRequest()
		  || numOfVms < this.simulationManager.getMinVmsPerRequest()) {
			
			return null;
		}
		
		Vm vm = null; 
		
		if(numOfVms > 1) {
			
			vm = this.simulationManager.createVmPlacementGroup(record.size(), cores, ramMiB, bwMbps, storageMiB, startTime);
						
			VmPlacementGroup vmGroup = (VmPlacementGroup) vm;
			
			if(vmGroup.getIdealNumOfHosts() < this.simulationManager.getMinHostsPerRequest()) {
				return null;
			}
			
			final int lastVm = (int) (numOfVms-1);
			vmGroup.getVmList().get(lastVm).addOnHostAllocationListener(this::vmAllocationListener);
			this.requestsToLifetimes.put(vmGroup.getVmList().get(lastVm), lifetime);
		}
		else {
			
			vm = this.simulationManager.createVm(cores, ramMiB, bwMbps, storageMiB, startTime);
			vm.addOnHostAllocationListener(this::vmAllocationListener);
			this.requestsToLifetimes.put(vm, lifetime);
			
		}
		
		this.statisticsManager.updateStatisticsForRequest(vm, startTimeStats, endTimeStats-startTimeStats); 

		return vm;
	}
	
	/**
	 * Submits the next batch of VM requests to the {@link DatacenterBroker}.
	 * It also schedules the next batch submission in case there are additional
	 * batches. The event to schedule the next submission is marked with a {@link CloudSimTag#BATCH_TAG}.
	 */
    private void submitNextBatch(final EventInfo info) {
    	    	
    	Iterator<List<Vm>> it = this.requestBatches.iterator();
    	
    	if(it.hasNext()) {
    		
    		List<Vm> batch = it.next();
    		
    		it.remove();
    		    		    		
    		batch.forEach(r -> {
    			
    			double submissionDelay =  this.startupLag + r.getSubmissionDelay() - info.getTime(); 
    			r.setSubmissionDelay(submissionDelay);
    		
    		});
    		
    		this.simulationManager.getBroker().submitVmList(batch);
    		        	
        	if(it.hasNext()) {
        		
        		List<Vm> nextBatch = it.next();
        		
        		double nextBatchDelay =  this.startupLag + nextBatch.get(0).getSubmissionDelay() - info.getTime();

        		CloudSimTag batchTag = CloudSimTag.BATCH_TAG;
        		
        		if(!this.simulationManager.getBroker().schedule(nextBatchDelay, batchTag)) {
        			this.simulationManager.getSimOut().println("Cannot schedule batch submission. Abort");
            		this.simulationManager.getSimulation().abort();	
        		}
        	}        	
    	}
    	
    	this.submittedBatches++;
    }
    
	/**
	 * Prints the simulation progress based on the number of submitted batches. 
	 */
    private void printSimulationProgress() {
    	
    	final double elapsedTimeSec = TimeUtil.elapsedSeconds(this.simulationManager.getInitializationCompletionTimestamp());
    	
        double remainingTimeSec = -1; 

        String remainingTimeStr = "";
        
    	if(this.submittedBatches == 0)
    		this.simulationManager.getSimOut().println("\n"+this.requestBatches.size()+" batches");
    	
    	else {
    		remainingTimeSec = elapsedTimeSec * (double) this.requestBatches.size() / (double) this.submittedBatches;
    		remainingTimeStr = "- ETR " + TimeUtil.secondsToStr(remainingTimeSec);
    	}

    	
    	final long progress = Math.round( 100d * (double)(this.submittedBatches) / (double) (this.submittedBatches + this.requestBatches.size()) );
        
        
        if(this.requestBatches.isEmpty())
        	this.simulationManager.getSimOut().printf("Simulation completed - 100%% %n");
        
        else
	        this.simulationManager.getSimOut().printf("Submitted batch %d/%d - %d%% %s%n",
	        				  this.submittedBatches + 1,
	        				  this.submittedBatches + this.requestBatches.size(),
	        				  progress,
	        				  remainingTimeStr);
        
        this.simulationManager.getSimOut().flush();
        
    }
	
	/**
	 * A listener to be called everytime a request is placed in the data center.
	 * Check {@link #createRequest(List)} to see how it is allocated to the requests.
	 * A {@link Cloudlet} is created for every request to simulate the VM lifetimes. 
	 */
    private void vmAllocationListener(final VmEventInfo info) {
    	
    	final Cloudlet cloudlet = this.simulationManager.createCloudlet(info.getVm(), this.requestsToLifetimes.get(info.getVm()));
		
    	cloudlet.addOnFinishListener(this::cloudletFinishListener);
    	
		this.simulationManager.getBroker().submitCloudlet(cloudlet);
		

		this.statisticsManager.updateResourcesUtilizationTrace(info.getTime());
    }
    
    /**
     * A listener to be called everytime a {@link Cloudlet} finishes its execution. 
     * The VMs of the request associated with the Cloudlet are destroyed when the 
     * listener is executed. 
     */
    private void cloudletFinishListener(final CloudletVmEventInfo info) {
    		
    	Vm vm = info.getCloudlet().getVm();

    	Vm vmForPolicyRecord = vm;
    	
    	if(vm.getGroup() != null) {
    		final VmGroup vmGroup = vm.getGroup(); 

    		vmForPolicyRecord = vmGroup;
    		
    		vmGroup.getVmList().forEach(vmg -> {
    			this.simulationManager.getBroker().destroyVm(vmg);
    		});
    		
    		vmGroup.setCreated(false);
    		vmGroup.setStopTime(info.getTime());   
    	}
    	else {
        	
    		this.simulationManager.getBroker().destroyVm(vm);
    	}
		
		this.simulationManager.getBatchesManager()
							  .getStatisticsManager()
							  .getVmAllocationPolicyTraceRecord(vmForPolicyRecord)
							  .setRequestedLifetime(this.requestsToLifetimes.get(vm));

		this.simulationManager.getBatchesManager()
					  		  .getStatisticsManager()
					  		  .getVmAllocationPolicyTraceRecord(vmForPolicyRecord)
					  		  .setDepartureTime(info.getTime());
    	
		this.statisticsManager.updateResourcesUtilizationTrace(info.getTime());
    }

    /**
     * A listener to be called at the beginning of the simulation. It 
     * calculates any potential startup lag and submits the first batch 
     * with the VM requests to the {@link DatacenterBroker}. 
     */
	public void simulationStartListener(final EventInfo info) {
	
		this.startupLag = info.getTime();
	
		this.printSimulationProgress();
		
		this.submitNextBatch(info);
	}
	
	/**
	 * A listener to check the simulation events in order to identify when
	 * it's time to submit the next batch of VM requests to the {@link DatacenterBroker}.
	 * 
	 * These events are marked with a {@link CloudSimTag#BATCH_TAG}.
	 */
    private void simulationEventListener(final EventInfo info) {

    	if (!(info instanceof SimEvent))
    		return;

    	final CloudSimTag tag = ((SimEvent) info).getTag();

    	if(tag != CloudSimTag.BATCH_TAG)
    		return;
    	
		this.printSimulationProgress();
    	
    	this.submitNextBatch(info);
    }
    
    /**
     * Returns the {@link TracesParser} of the dataset
     */
    public TracesParser getParser() {
    	return this.parser;
    }
    
    
    /**
     * Returns the {@link TracesStatisticsManager}
     */
    public TracesStatisticsManager getStatisticsManager() {
    	return this.statisticsManager;
    }
}
