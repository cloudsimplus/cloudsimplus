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
package org.cloudsimplus.traces.vmallocationpolicywithgroups;

import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyRequestStatus;
import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyRequestType;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroup;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupAffinityType;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupEnforcement;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupScope;
import org.cloudsimplus.traces.azure.TracesStatisticsManager;

/**
 * 
 * A class for creating records of how the VM requests are handled by the
 * VM placement group allocation policies (see {@link VmAllocationPolicyRequestType}).
 * The records are used to create trace files.
 *  
 * @see VmAllocationPolicyTraceTableBuilder
 * @see TracesStatisticsManager
 * 
 * @since CloudSim Plus 7.3.2
 * 
 * @author Pavlos Maniotis
 */
public class VmAllocationPolicyTraceRecord {

	/**
	 * A unique id
	 */
	final private long id;
	
	/**
	 * Arrival time of the request
	 */
	final private double arrivalTime;
	
	/**
	 * Departure time of the request
	 */
	private double departureTime;
	
	/**
	 * The requested lifetime
	 */
	private double requestedLifeTime;
	
	/**
	 * Cores per VM
	 */
	final private long numOfCores;
	
	/**
	 * RAM per VM
	 */
	final private long ramMiB;
	
	/**
	 * Bandwidth per VM
	 */
	final private long bwMbps;
	
	/**
	 * Storage size per VM
	 */
	final private long storageMiB;
	
	/**
	 * See {@link VmAllocationPolicyRequestType}
	 */
	final private VmAllocationPolicyRequestType requestType;
	
	/**
	 * See {@link VmAllocationPolicyRequestStatus}
	 */
	final private VmAllocationPolicyRequestStatus requestStatus;
	
	/**
	 * See {@link VmPlacementGroupScope}
	 */
	final private VmPlacementGroupScope scope;	
	
	/**
	 * See {@link VmPlacementGroupAffinityType}
	 */
	final private VmPlacementGroupAffinityType affinityType;
	
	/**
	 * See {@link VmPlacementGroupEnforcement}
	 */
	final private VmPlacementGroupEnforcement enforcement;
	
	/**
	 * Number of VMs in the request
	 */
	final private long numOfVms;
	
	/**
	 * Ideal number of switches under which the request can be placed
	 */
	final private long idealNumOfSwitches;
	
	/**
	 * Number of switches under which the request has been placed
	 */
	final private long numOfSwitches;
	
	/**
	 * Ideal number of hosts in which the request can be placed
	 */
	final private long idealNumOfHosts;
	
	/**
	 * Number of hosts in which the request has been placed
	 */
	final private long numOfHosts;
	
	/**
	 * Constructor to initialize the record
	 * 
	 * @param vm the request for which we create the record 
	 */
	public VmAllocationPolicyTraceRecord(final Vm vm) {
		
		this.arrivalTime       = vm.getSimulation().clock();
		this.departureTime     = -1; 
		this.requestedLifeTime = -1;
		this.requestStatus     = vm.isCreated() ? VmAllocationPolicyRequestStatus.SUCCESS : VmAllocationPolicyRequestStatus.FAIL;
		this.id                = vm.getId();

		
		
		if(vm instanceof VmPlacementGroup) {
			
			VmPlacementGroup vmGroup = (VmPlacementGroup) vm;
			
			this.requestType   = VmAllocationPolicyRequestType.VM_PLACEMENT_GROUP; 
			this.numOfCores    = vmGroup.getVmList().get(0).getNumberOfPes();
			this.ramMiB        = vmGroup.getVmList().get(0).getRam().getCapacity();
			this.bwMbps        = vmGroup.getVmList().get(0).getBw().getCapacity();
			this.storageMiB    = vmGroup.getVmList().get(0).getStorage().getCapacity();
			this.numOfVms      = vmGroup.getVmList().size();
			this.scope         = vmGroup.getScope();
			this.affinityType  = vmGroup.getAffinityType();
			this.enforcement   = vmGroup.getEnforcement();
			this.idealNumOfSwitches = vmGroup.getIdealNumOfSwitches();
			this.numOfSwitches      = vmGroup.getNumOfSwitches();
			this.idealNumOfHosts    = vmGroup.getIdealNumOfHosts();
			this.numOfHosts         = vmGroup.getNumOfHosts();
			return;
		}
		else if (vm instanceof VmGroup) {
			
			VmGroup vmGroup = (VmGroup) vm;

			this.requestType   = VmAllocationPolicyRequestType.VM_GROUP;
			this.numOfCores    = vmGroup.getNumberOfPes();
			this.ramMiB        = vmGroup.getRam().getCapacity();
			this.bwMbps        = vmGroup.getBw().getCapacity();
			this.storageMiB    = vmGroup.getStorage().getCapacity();
			this.numOfVms      = vmGroup.getVmList().size();;
			this.scope         = VmPlacementGroupScope.HOST;
			this.affinityType  = VmPlacementGroupAffinityType.AFFINITY;
			this.enforcement   = VmPlacementGroupEnforcement.NONE;
			this.idealNumOfSwitches = 1;
			this.numOfSwitches      = vmGroup.isCreated() ? 1 : 0;
			this.idealNumOfHosts    = 1;
			this.numOfHosts         = vmGroup.isCreated() ? 1 : 0;
		}
		else {
			this.requestType   = VmAllocationPolicyRequestType.SINGLE_VM;
			this.numOfCores    = vm.getNumberOfPes();
			this.ramMiB        = vm.getRam().getCapacity();
			this.bwMbps        = vm.getBw().getCapacity();
			this.storageMiB    = vm.getStorage().getCapacity();
			this.numOfVms      = 1;
			this.scope         = VmPlacementGroupScope.HOST;
			this.affinityType  = VmPlacementGroupAffinityType.AFFINITY;
			this.enforcement   = VmPlacementGroupEnforcement.STRICT;
			this.idealNumOfSwitches = 1;
			this.numOfSwitches      = vm.isCreated() ? 1 : 0;
			this.idealNumOfHosts    = 1;
			this.numOfHosts         = vm.isCreated() ? 1 : 0;
		}
		
			
	}

	/**
	 * Returns the unique id
	 */
	public long getId() {
		return id;
	}

	
	/**
	 * Returns the arrival time of the request
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Returns the departure time of the request
	 */
	public double getDepartureTime() {
		return departureTime;
	}

	/**
	 * Sets the departure time of the request
	 */
	public void setRequestedLifetime(double requestedLifetime) {
		this.requestedLifeTime = requestedLifetime;
	}
	
	/**
	 * Returns the requested lifetime
	 */
	public double getRequestedLifetime() {
		return this.requestedLifeTime;
	}
	
	/**
	 * Sets the departure time of the request
	 */
	public void setDepartureTime(double departureTime) {
		this.departureTime = departureTime;
	}

	/**
	 * Returns the number of cores
	 */
	public long getNumOfCores() {
		return numOfCores;
	}

	/**
	 * Returns the amount of RAM
	 */
	public long getRamMiB() {
		return ramMiB;
	}

	/**
	 * Returns the amount of bandwidth
	 */
	public long getBwMbps() {
		return bwMbps;
	}

	/**
	 * Returns the amount of storage
	 */
	public long getStorageMiB() {
		return storageMiB;
	}

	/**
	 * Returns the {@link VmAllocationPolicyRequestType}
	 */
	public VmAllocationPolicyRequestType getRequestType() {
		return requestType;
	}

	/**
	 * Returns the {@link VmAllocationPolicyRequestStatus}
	 */
	public VmAllocationPolicyRequestStatus getRequestStatus() {
		return requestStatus;
	}
	
	/**
	 * Returns the {@link VmPlacementGroupScope}
	 */
	public VmPlacementGroupScope getScope() {
		return scope;
	}

	/**
	 * Returns the {@link VmPlacementGroupAffinityType}
	 */
	public VmPlacementGroupAffinityType getAffinityType() {
		return affinityType;
	}

	/**
	 * Returns the {@link VmPlacementGroupEnforcement}
	 */
	public VmPlacementGroupEnforcement getEnforcement() {
		return enforcement;
	}

	/**
	 * Returns the number of VMs in the request
	 */
	public long getNumOfVms() {
		return numOfVms;
	}

	/**
	 * Returns the ideal number of switches under which the request can be placed
	 */
	public long getIdealNumOfSwitches() {
		return idealNumOfSwitches;
	}

	/**
	 * Returns the number of switches under which the request has been placed
	 */
	public long getNumOfSwitches() {
		return numOfSwitches;
	}

	/**
	 * Returns the ideal number of hosts in which the request can be placed
	 */
	public long getIdealNumOfHosts() {
		return idealNumOfHosts;
	}

	/**
	 * Returns the number of hosts in which the request has been placed
	 */
	public long getNumOfHosts() {
		return numOfHosts;
	}
}
