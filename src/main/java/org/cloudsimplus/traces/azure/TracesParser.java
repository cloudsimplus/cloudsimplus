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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroup;

/**
 * A parser for parsing the <i>AzureTracesForPacking2020</i> dataset and other
 * datasets that follow the same schema. For more information about the dataset 
 * follow this link: https://github.com/Azure/AzurePublicDataset/blob/master/AzureTracesForPacking2020.md
 * <br>
 * Other datasets can be used provided that they follow the same schema for the 
 * VM types (see {@link VmTypeRecord}) and VM requests (see {@link VmRecord}).  
 * <br>
 * The parser expects two csv files with the VM types and VM requests, i.e., the
 * names of the files should be <i>vmType.csv</i> and <i>vm.csv</i>, respectively. 
 * Each line in vmType.csv should follow this format: 
 * 
 * <pre> 
 * id, vmTypeId, machineId, core, memory, hdd, ssd, nic.
 * </pre>
 * 
 * Each line in vm.csv should follow this format: 
 * 
 * <pre>
 * vmId, tenantId, vmTypeId, priority, starttime, endtime.
 * </pre>
 * 
 * The parser skips the first line of both files because it expects header lines.
 * <br>
 * Beyond parsing the dataset, the class is responsible for (a) processing the requests
 * to identify {@link VmPlacementGroup} requests, and (b) creating and returning a list 
 * with all the requests, i.e., single {@link Vm} and/or {@link VmPlacementGroup}
 * requests, in chronological order (based on their start times).
 * <br> 
 * It is considered that a set of VMs belongs in a {@link VmPlacementGroup} if all the 
 * following conditions are met: 
 * <pre>
 * - the VMs share the same tenant and vmType ids, 
 * - they have the same priority, and 
 * - they start and end at the same times with a max difference of a predefined amount 
 *   of time
 * </pre> 
 * 
 * @see VmRecord
 * @see VmTypeRecord
 * @see TracesStatisticsManager
 * @see TracesBatchesManager
 *
 * @since CloudSim Plus 7.3.2
 *
 * @author Pavlos Maniotis
 */

//TODO can we have better integration by extending TraceReader? 
public class TracesParser {	
	
	/**
	 * The traces simulation manager
	 */
	final private TracesSimulationManager simulationManager;

	/** 
	 * Each {@link VmTypeRecord#getId()} is associated with multiple resource 
	 * request ratios depending on different types of servers. The resource 
	 * ratios are kept in descending order according to the core utilization, 
	 * i.e., the first item in the list corresponds to the ratio with the max 
	 * core utilization.
	 */
    final private Map<Long, List<VmTypeRecord>> vmTypeRecords;
        
    /**
     * Total number of {@link VmTypeRecord}s
     */
    private long totalVmTypeRecords = 0;
    
    /**
     * Total number of invalid {@link VmTypeRecord}s, 
     * i.e., with invalid fields
     */
    private long invalidVmTypeRecords = 0;
    
    /**
     * Total number of {@link VmRecord}s
     */
    private long totalVmRecords = 0;
    
    /**
     * Total number of invalid {@link VmRecord}s, 
     * i.e., with invalid fields
     */
    private long invalidVmRecords = 0;
    
    /**
     * Total number of {@link VmRecord}s with negative start times 
     */
    private long negativeVmRecords = 0; 
    
    /**
     * Total number of single {@link Vm} requests identified in the dataset 
     */
    private long singleVmsAfterThreshold = 0;

    /**
     * Total number of {@link VmPlacementGroup} requests identified in the dataset 
     */
    private long vmPlacementGroupsAfterThreshold = 0;
    
    /**
     * Total number of {@link Vm}s from {@link VmPlacementGroup} requests 
     */
    private long vmsFromVmPlacementGroupsAfterThreshold = 0;
    
    /**
     * Constructor to initialize the {@link TracesSimulationManager} and to parse
     * the {@link VmTypeRecord}s
     */
	public TracesParser(TracesSimulationManager simulationManager) {
						
		this.simulationManager = simulationManager;
		
		this.vmTypeRecords = this.parseVmTypes();		
	}
	
	/**
	 * It parses the file with the {@link VmTypeRecord}s and returns 
	 * a {@link Map} with the {@link VmTypeRecord#getId()}s associated
	 * with multiple {@link VmTypeRecord}s in descending order according to core utilization
	 */
	private Map<Long, List<VmTypeRecord>> parseVmTypes() {
		
		final List<VmTypeRecord> vmTypes = new ArrayList<VmTypeRecord>();
		
		BufferedReader reader = getBufferedReader(this.simulationManager.getTracePath() + "vmType.csv");
		String line = null;
		
		try {
			line = reader.readLine(); // discard first line
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(line != null) {
			
			this.totalVmTypeRecords++;
			
			final String splittedLine[] = line.split(",", -1);
			
			try                   { line = reader.readLine(); } 
			catch (IOException e) { e.printStackTrace();      }
			
			final long id        = splittedLine[0].equals("") ? -1 : Long.valueOf(splittedLine[0]);
			final long vmTypeId  = splittedLine[1].equals("") ? -1 : Long.valueOf(splittedLine[1]);
			final long machineId = splittedLine[2].equals("") ? -1 : Long.valueOf(splittedLine[2]);
			final double core    = splittedLine[3].equals("") ? -1 : Double.valueOf(splittedLine[3]);
			final double memory  = splittedLine[4].equals("") ? -1 : Double.valueOf(splittedLine[4]);
			final double hdd     = splittedLine[5].equals("") ? -1 : Double.valueOf(splittedLine[5]);
			final double ssd     = splittedLine[6].equals("") ? -1 : Double.valueOf(splittedLine[6]);
			final double nic     = splittedLine[7].equals("") ? -1 : Double.valueOf(splittedLine[7]);
						
			if (id < 0 || vmTypeId < 0 || machineId < 0 || core <= 0 || memory <= 0 || nic <= 0) {
				this.invalidVmTypeRecords++;
				continue;
			}
			
			final VmTypeRecord vmType = new VmTypeRecord(id, vmTypeId, machineId, core, memory, hdd, ssd, nic);
			vmTypes.add(vmType);
		}
			
		try 				  { reader.close(); } 
		catch (IOException e) {	e.printStackTrace(); }
		
		
		final Map<Long, List<VmTypeRecord>> vmTypeRecords = 
				vmTypes.stream()
					   .collect(Collectors.groupingBy(VmTypeRecord::getVmTypeId));
		
		final Comparator<VmTypeRecord> comparator = Comparator.comparingDouble(VmTypeRecord::getCore).reversed();
		
		// For each vmType we sort the resource request ratios in descending order based on the requested core resources.
		vmTypeRecords.forEach((k, v) -> {
			v.sort(comparator);
		});		
		
		return vmTypeRecords;
	}
	
	/**
	 * It parses the file with the {@link VmRecord}s and returns a {@link List} with them.
	 */
	private List<VmRecord> parseVms() {
		
		final List<VmRecord> vmRecords = new ArrayList<VmRecord>();

		BufferedReader reader = getBufferedReader(this.simulationManager.getTracePath() + "vm.csv");
		String line = null;
		
		try {
			line = reader.readLine(); // discard first line
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(line != null) {
			
			this.totalVmRecords++;
			
			String splittedLine[] = line.split(",", -1);

			try                   { line = reader.readLine(); } 
			catch (IOException e) { e.printStackTrace();      }
			
			final long vmId      = splittedLine[0].equals("") ? -1 : Long.valueOf(splittedLine[0]);
			final long tenantId  = splittedLine[1].equals("") ? -1 : Long.valueOf(splittedLine[1]);
			final long vmTypeId  = splittedLine[2].equals("") ? -1 : Long.valueOf(splittedLine[2]);
			final long priority  = splittedLine[3].equals("") ? -1 : Long.valueOf(splittedLine[3]);
			final long startTime = splittedLine[4].equals("") ? -1 : Math.round(Double.valueOf(splittedLine[4]) * 3600d * 24d);
			long endTime   = splittedLine[5].equals("") ? this.simulationManager.getVmCapSec() : Math.round(Double.valueOf(splittedLine[5]) * 3600d * 24d);
			
			
			if(vmId < 0 || tenantId < 0 || vmTypeId < 0 || (priority < 0 || priority > 1) || (endTime - startTime) < 0) {
				this.invalidVmRecords++;
				continue;
			}
			
			if(startTime == endTime)
				endTime = startTime + 1;
			
			if(startTime < 0)
				this.negativeVmRecords++;
							
				
			final VmRecord vmRecord = new VmRecord(vmId, tenantId, vmTypeId, priority, startTime, endTime);				
			vmRecords.add(vmRecord);

		}
		
		try                   { reader.close(); } 
		catch (IOException e) { e.printStackTrace(); }	
		
		return vmRecords;
	}
	
	/** 
	 * It processes the {@link VmRecord}s returned by the {@link #parseVms()} 
	 * in order to identify potential {@link VmPlacementGroup} requests and it returns
	 * a list with all the requests in chronological order (based on their start times).
	 */
	private List<List<VmRecord>> processVmRecords() {
		
		final List<VmRecord> vmRecords = this.parseVms();
		
		// first we group records by [tenantId, typeId, priority]
		final Map<List<Long>, List<VmRecord>> vmsByIdsAndPriority = 
				vmRecords.stream().collect(Collectors.groupingBy(r -> 
				Arrays.asList(r.getTenantId(), r.getTypeId(), r.getPriority())));
		
		
		// we sort them by startTime
		vmsByIdsAndPriority.forEach((k, v) -> {
			v.sort(Comparator.comparingLong(VmRecord::getStartTime));
		});

		// we group them by [tenantId, typeId, priority, startTime]
		final Map<List<Long>, List<VmRecord>> vmsByStartTime = new HashMap<List<Long>, List<VmRecord>>();

		vmsByIdsAndPriority.forEach((key, value) -> {
			
			List<Long> newKey = new ArrayList<Long>();
			
			newKey.addAll(key);
			
			List<VmRecord> newValue = new ArrayList<VmRecord>();

			newValue.add(value.get(0));
			
			for(int i = 1; i < value.size(); i++) {
				
				if((value.get(i).getStartTime() - newValue.get(0).getStartTime()) <= this.simulationManager.getGroupThresholdSec()) {
					newValue.add(value.get(i));
				}
				else {
					newKey.add(newValue.get(0).getStartTime());
					vmsByStartTime.put(newKey, newValue);
					
					newKey = new ArrayList<Long>();
					newKey.addAll(key);
					
					newValue = new ArrayList<VmRecord>();
					newValue.add(value.get(i));
				}
			}
			
			if(!newValue.isEmpty()) {
				newKey.add(newValue.get(0).getStartTime());
				vmsByStartTime.put(newKey, newValue);
			}
		});
		
		
		// we sort them by endTime
		vmsByStartTime.forEach((k, v) -> {
			v.sort(Comparator.comparingLong(VmRecord::getEndTime));
		});


		// finally we group them by [tenantId, typeId, priority, startTime, endTime]
		final Map<List<Long>, List<VmRecord>> vmsByStartAndEndTime = new HashMap<List<Long>, List<VmRecord>>();

		vmsByStartTime.forEach((key, value) -> {
			
			List<Long> newKey = new ArrayList<Long>();
			
			newKey.addAll(key);
			
			List<VmRecord> newValue = new ArrayList<VmRecord>();

			newValue.add(value.get(0));
			
			for(int i = 1; i < value.size(); i++) {
				
				if(value.get(i).getEndTime() - newValue.get(0).getEndTime() <= this.simulationManager.getGroupThresholdSec()) {
					newValue.add(value.get(i));
				}
				else {
					newKey.add(newValue.get(0).getEndTime());
					vmsByStartAndEndTime.put(newKey, newValue);
					
					newKey = new ArrayList<Long>();
					newKey.addAll(key);
					
					newValue = new ArrayList<VmRecord>();
					newValue.add(value.get(i));
				}
			}
			
			if(!newValue.isEmpty()) {
				newKey.add(newValue.get(0).getEndTime());
				vmsByStartAndEndTime.put(newKey, newValue);
			}
		});
		
		// we sort them again by start time
		vmsByStartAndEndTime.forEach((k, v) -> {
			v.sort(Comparator.comparingLong(VmRecord::getStartTime));
		});
		
		// vm records in single vms or vm groups ordered by their start times
		final List<List<VmRecord>> processedVmRecords = new LinkedList<List<VmRecord>>(vmsByStartAndEndTime.values());
		
		// grouped records are finally stored in ascending order according to their start time
		final Comparator<List<VmRecord>> comparator = Comparator.comparing(i -> i.get(0).getStartTime());
		
		processedVmRecords.sort(comparator);
		
		processedVmRecords.forEach(r -> {
			
			if(r.size() == 1)
				this.singleVmsAfterThreshold++;
			else {
				this.vmPlacementGroupsAfterThreshold++;
				this.vmsFromVmPlacementGroupsAfterThreshold += r.size();
			}
		});
		
		return processedVmRecords;
	}
	
	
	/**
	 * Takes as input a file name and returns a {@link BufferedReader} to read the file. 
	 */
	private BufferedReader getBufferedReader(String fileName) {
		
		BufferedReader reader = null;
		
		try {	
			reader = new BufferedReader(new FileReader(fileName));			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return reader;
	}
	
	/**
	 * Takes as input a {@link PrintStream} and prints the 
	 * metadata about the dataset. 
	 */
	public void printDatasetMetadata(final PrintStream out) {
				
    	out.println();
        out.println("------------ VmType file -------------");
        out.println("Total vmTypes   : " + this.totalVmTypeRecords);
        out.println("Invalid vmTypes : " + this.invalidVmTypeRecords);
    	out.println();
        out.println("-------------- Vm file ---------------");
        out.println("Total vms    : " + this.totalVmRecords);
        out.println("Invalid vms  : " + this.invalidVmRecords);
        out.println("Negative vms : " + this.negativeVmRecords);
    	out.println();
        out.println("--------- Processed records ----------");
        out.println("Single vms                   : " + this.singleVmsAfterThreshold);
        out.println("Vm placement groups          : " + this.vmPlacementGroupsAfterThreshold);
        out.println("Vms from vm placement groups : " + this.vmsFromVmPlacementGroupsAfterThreshold);
	}
	
	/**
	 * Returns the {@link Map} with the {@link VmTypeRecord}s
	 */
	public Map<Long, List<VmTypeRecord>> getVmTypes() {

		return this.vmTypeRecords;
	}
	
	/**
	 * Returns the list with all the requests created by the {@link #processVmRecords()}
	 */
	public List<List<VmRecord>> getVmRecords() {
		
		return this.processVmRecords();
	}
}
