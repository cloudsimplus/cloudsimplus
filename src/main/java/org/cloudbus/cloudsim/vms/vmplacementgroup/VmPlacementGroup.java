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
package org.cloudbus.cloudsim.vms.vmplacementgroup;

import java.util.List;

import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyBestFitWithPlacementGroups_LRRL;
import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyChicSchedAllPack;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;

/**
 * A VmPlacementGroup is a group of VMs that is requested to be placed according 
 * to a set of rules/constrains that take into account the topology of the system. 
 * These rules are characterized by:  
 * <pre>
 * (1) the {@link VmPlacementGroupScope} of the group
 * (2) the {@link VmPlacementGroupAffinityType} of the group
 * (3) the {@link VmPlacementGroupEnforcement} of the rules
 * </pre>
 * @see VmPlacementGroupScope
 * @see VmPlacementGroupAffinityType
 * @see VmPlacementGroupEnforcement
 * @see VmAllocationPolicyBestFitWithPlacementGroups_LRRL
 * @see VmAllocationPolicyChicSchedAllPack
 *
 * @since CloudSim Plus 7.3.2
 *
 * @author Pavlos Maniotis
 */
public class VmPlacementGroup extends VmGroup {
	
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
	 * The ideal number of hosts refers to the minimum number of hosts 
	 * that can be used to place the VMs of the group request.
	 */
	final private long idealNumOfHosts;
	
	/**
	 * The ideal number of switches refers to the minimum number of switches 
	 * that can be used to place the VMs of the group request.
	 */
	final private long idealNumOfSwitches;
	
	/**
	 * The number of hosts is updated once the VM group is placed
	 * corresponds to the actual number of hosts that have been used for
	 * the placement.
	 */
	private long numOfHosts = 0;

	/**
	 * The number of switches is updated once the VM group is placed
	 * corresponds to the actual number of switches that have been used for
	 * the placement.
	 */
	private long numOfSwitches = 0;
	
	/**
	 * Constructor to initialize the {@link VmPlacementGroup}
	 * 
	 * @param vmList the VMs of the group
	 * @param scope the {@link VmPlacementGroupScope} of the group
	 * @param affinityType the {@link VmPlacementGroupAffinityType} of the group
	 * @param enforcement the {@link VmPlacementGroupEnforcement} of the group
	 * @param idealNumOfHosts the ideal number of hosts needed to place the group
	 * @param idealNumOfSwitches the ideal number of switches needed to place the group
	 */
	public VmPlacementGroup(final List<Vm> vmList, VmPlacementGroupScope scope, 
			VmPlacementGroupAffinityType affinityType, VmPlacementGroupEnforcement enforcement, 
			long idealNumOfHosts, long idealNumOfSwitches) {
		super(vmList);
		
		this.scope = scope;
		this.affinityType = affinityType;
		this.enforcement = enforcement;
		this.idealNumOfHosts = idealNumOfHosts;
		this.idealNumOfSwitches = idealNumOfSwitches;
	}
	
	/**
	 * returns the {@link VmPlacementGroupScope} of the group
	 */
	public VmPlacementGroupScope getScope() {
		return this.scope;
	}

	/**
	 * returns the {@link VmPlacementGroupAffinityType} the group
	 */
	public VmPlacementGroupAffinityType getAffinityType() {
		return this.affinityType;
	}

	/**
	 * returns the {@link VmPlacementGroupEnforcement} of the group
	 */
	public VmPlacementGroupEnforcement getEnforcement() {
		return this.enforcement;
	}
	
	/**
	 * returns the ideal number of hosts needed to place the group
	 */
	public long getIdealNumOfHosts() {
		return this.idealNumOfHosts;
	}

	/**
	 * returns the ideal number of switches needed to place the group
	 */
	public long getIdealNumOfSwitches() {
		return this.idealNumOfSwitches;
	}
	
	/**
	 * returns the number of hosts that have been used to place the group or zero
	 * if the group has not been placed
	 */
	public long getNumOfHosts() {
		return this.numOfHosts;
	}

	
	/**
	 * returns the number of switches that have been used to place the group or zero
	 * if the group has not been placed
	 */
	public long getNumOfSwitches() {
		return this.numOfSwitches;
	}
	
	
	/**
	 * sets the number of switches used to place the group
	 */
	public void setNumOfSwitches (long numberOfSwitches) {
		this.numOfSwitches = numberOfSwitches;
	}

	/**
	 * sets the number of hosts used to place the group
	 */
	public void setNumOfHosts(long numberOfHosts) {
		this.numOfHosts = numberOfHosts;
	}

	/**
	 * returns true if it is SwitchAffinityStrict or false otherwise
	 */
	public boolean isSwitchAffinityStrict() {
		return this.scope           == VmPlacementGroupScope       .SWITCH 
			   && this.affinityType == VmPlacementGroupAffinityType.AFFINITY
			   && this.enforcement  == VmPlacementGroupEnforcement .STRICT;
	}
	
	/**
	 * returns true if it is SwitchAffinityBestEffort or false otherwise
	 */
	public boolean isSwitchAffinityBestEffort() {
		return this.scope           == VmPlacementGroupScope       .SWITCH 
			   && this.affinityType == VmPlacementGroupAffinityType.AFFINITY
			   && this.enforcement  == VmPlacementGroupEnforcement .BEST_EFFORT;
	}
	
	/**
	 * returns true if it is SwitchAntiAffinityStrict or false otherwise
	 */
	public boolean isSwitchAntiAffinityStrict() {
		return this.scope           == VmPlacementGroupScope       .SWITCH 
			   && this.affinityType == VmPlacementGroupAffinityType.ANTI_AFFINITY
			   && this.enforcement  == VmPlacementGroupEnforcement .STRICT;
	}
	
	/**
	 * returns true if it is SwitchAntiAffinityBestEffort or false otherwise
	 */
	public boolean isSwitchAntiAffinityBestEffort() {
		return this.scope           == VmPlacementGroupScope       .SWITCH 
			   && this.affinityType == VmPlacementGroupAffinityType.ANTI_AFFINITY
			   && this.enforcement  == VmPlacementGroupEnforcement .BEST_EFFORT;
	}
	
	/**
	 * returns true if it is HostAffinityStrict or false otherwise
	 */
	public boolean isHostAffinityStrict() {
		return this.scope           == VmPlacementGroupScope       .HOST 
			   && this.affinityType == VmPlacementGroupAffinityType.AFFINITY
			   && this.enforcement  == VmPlacementGroupEnforcement .STRICT;
	}
	
	/**
	 * returns true if it is HostAffinityBestEffort or false otherwise
	 */
	public boolean isHostAffinityBestEffort() {
		return this.scope           == VmPlacementGroupScope       .HOST 
			   && this.affinityType == VmPlacementGroupAffinityType.AFFINITY
			   && this.enforcement  == VmPlacementGroupEnforcement .BEST_EFFORT;
	}
	
	/**
	 * returns true if it is HostAntiAffinityStrict or false otherwise
	 */
	public boolean isHostAntiAffinityStrict() {
		return this.scope           == VmPlacementGroupScope       .HOST 
			   && this.affinityType == VmPlacementGroupAffinityType.ANTI_AFFINITY
			   && this.enforcement  == VmPlacementGroupEnforcement .STRICT;
	}
	
	/**
	 * returns true if it is HostAntiAffinityBestEffort or false otherwise
	 */
	public boolean isHostAntiAffinityBestEffort() {
		return this.scope           == VmPlacementGroupScope       .HOST 
			   && this.affinityType == VmPlacementGroupAffinityType.ANTI_AFFINITY
			   && this.enforcement  == VmPlacementGroupEnforcement .BEST_EFFORT;
	}
}
