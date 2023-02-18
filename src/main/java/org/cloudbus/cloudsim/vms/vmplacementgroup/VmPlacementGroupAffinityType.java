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

/**
 * An enumerator for the affinity type of a {@link VmPlacementGroup}. Depending on the 
 * {@link VmPlacementGroupScope} of the group, <i>Affinity</i> corresponds to the case where we 
 * want pack the VMs together (e.g., under a switch or inside a host), while <i>Anti-affinity</i>
 * corresponds to the opposite case where we want to spread out the VMs (e.g., under different
 * switches or in different hosts).
 *
 * @see VmPlacementGroupScope
 * @see VmPlacementGroupEnforcement
 * @see VmAllocationPolicyBestFitWithPlacementGroups_LRRL
 * @see VmAllocationPolicyChicSchedAllPack
 *
 * @since CloudSim Plus 7.3.2
 *
 * @author Pavlos Maniotis
 */
public enum VmPlacementGroupAffinityType {
	
	/**
	 * Denotes the desire to pack the VMs together
	 */
	AFFINITY,
	
	/**
	 * Denotes the desire to spread out the VMs
	 */
	ANTI_AFFINITY
}
