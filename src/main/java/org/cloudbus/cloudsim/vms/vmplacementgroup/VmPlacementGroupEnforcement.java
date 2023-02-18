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

import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyBestFitWithPlacementGroups_LRRL;
import org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups.VmAllocationPolicyChicSchedAllPack;
import org.cloudbus.cloudsim.vms.VmGroup;

/**
 * An enumerator for the enforcement of the placement rules for a {@link VmPlacementGroup}. 
 * Strict enforcement means that a request is rejected if the constrains cannot be satisfied. 
 * Best effort means that the algorithm will try to satisfy the constrains but the 
 * request will not be rejected if they cannot be satisfied (provided that there are available 
 * resources to realize the placement). 
 *
 * @see VmPlacementGroupScope
 * @see VmPlacementGroupAffinityType
 * @see VmAllocationPolicyBestFitWithPlacementGroups_LRRL
 * @see VmAllocationPolicyChicSchedAllPack
 * 
 * @since CloudSim Plus 7.3.2
 *
 * @author Pavlos Maniotis
 */
public enum VmPlacementGroupEnforcement {
	
	/**
	 * Denotes that a request is rejected if the constrains cannot be satisfied
	 */
	STRICT,
	
	/**
	 * Denotes that a request is not rejected if the constrains cannot be satisfied,
	 * provided that there are available resources to realize the placement 
	 * 
	 */
	BEST_EFFORT,

	/**
	 * Denotes that a request type without enforcement, e.g., {@link VmGroup}
	 */
	NONE
}
