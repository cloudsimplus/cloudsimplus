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
package org.cloudbus.cloudsim.allocationpolicies.vmplacementgroups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyBestFit;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroup;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupAffinityType;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupEnforcement;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupScope;
import org.cloudsimplus.traces.azure.TracesSimulationManager;

/**
 * An extension of the {@link VmAllocationPolicyBestFitWithPlacementGroups} that uses the left-to-right and
 * right-to-left concepts depending on the number VMs of the {@link VmPlacementGroup} that is  requested to
 * be placed. This policy follows part of the principles of the scheduling policy presented in [1], which 
 * targets to reduce network sharing/fragmentation in HPC fat-tree systems. 
 * 
 * Small requests that fit under a single Top-of-Rack (ToR) switch populate the system from the first ToR 
 * onwards, whereas big requests that require multiple switches populate the system from the last ToR backwards. 
 * The concept has also been previously presented in [2]. Once a switch is selected for the placement, Best Fit 
 * is used to place the VMs of a {@link VmPlacementGroup} in a sequential manner (i.e., one by one).  
 * 
 * Since we try to allocate resources to groups of VMs, a request can be rejected if no resources exist for 
 * all the VMs cumulatively.
 *
 * <p><b>NOTE: As it is the case with {@link VmAllocationPolicyBestFitWithPlacementGroups}, 
 * this policy doesn't perform optimization of VM allocation by means of VM migration.</b></p>
 * 
 * <pre>
 * References:
 * 
 * [1] A. Jokanovic, et al., "Quiet Neighborhoods: Key to Protect Job Performance Predictability," 
 *     2015 IEEE International Parallel and Distributed Processing Symposium, 2015, pp. 449-459, 
 *     doi: 10.1109/IPDPS.2015.87.
 * [2] D. G. Feitelson, "Packing Schemes for Gang Scheduling," In Proceedings of the Workshop on 
 *     Job Scheduling Strategies for Parallel Processing (IPPS '96), 1996, Springer-Verlag, Berlin, 
 *     Heidelberg, pp. 89–110.
 * </pre>
 * 
 * @since CloudSim Plus 7.3.2
 * 
 * @author Pavlos Maniotis
 *
 * @see VmAllocationPolicyBestFitWithPlacementGroups
 * @see VmAllocationPolicyBestFit
 */
public class VmAllocationPolicyBestFitWithPlacementGroups_LRRL extends VmAllocationPolicyBestFitWithPlacementGroups {
	
	/**
	 * Each item represents a switch with its hosts.
	 */
	protected final List<List<Host>> switchHosts = new ArrayList<List<Host>>();
	
	/**
	 * Index of the switch to start placing VMs for the right-to-left part of the algorithm.
	 */
	private int rightToLeftFirstSwitchIndex;
		
	/**
	 * Takes as input the {@link CloudSim} and {@link TracesSimulationManager} and feeds them to the constructor
	 * of the superclass to performs basic checks about the number of hosts per switch. It also initializes 
	 * {@link #switchHosts}. 
	 * 
	 * @param simulation a handle to the {@link CloudSim} object 
	 * @param simulationManager see {@link TracesSimulationManager} for info about how to run this kind of trace-based simulations. 
	 */
	public VmAllocationPolicyBestFitWithPlacementGroups_LRRL(final CloudSim simulation, final TracesSimulationManager simulationManager) {
		
		super(simulation, simulationManager);
		
		this.setupSwitches(this.simulationManager.getHosts());
		
		this.rightToLeftFirstSwitchIndex = this.switchHosts.size() - 1;
	}
    
    /**
     * The default implementation to find a suitable host that has enough resources to place 
     * a {@link VmSimple} or a {@link VmGroup}. 
     * 
     * @param vm the temporary VM for which we try to allocate resources to
     * @return an {@link Optional} containing a suitable Host to place the VM or an empty {@link Optional} if no suitable Host was found
     */
    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
    	
    	for (int switchId = 0; switchId < this.switchHosts.size(); switchId++) {

            final Optional<Host> optional = this.findHostForTempVmInSwitch(vm, switchId);
            
            if(optional.isPresent())
            	return optional;
    	}

    	return Optional.empty();
    }
    
    /**
     * {@inheritDoc}} 
     */
    @Override
    protected boolean findAndAllocateHostsForVmPlacementGroup(final VmPlacementGroup vmPlacementGroup) { 	    	
    	
    	if(vmPlacementGroup.isSwitchAffinityStrict())
    		return switchAffinityStrict(vmPlacementGroup);

    	else if(vmPlacementGroup.isSwitchAffinityBestEffort())
    		return switchAffinityBestEffort(vmPlacementGroup);
    	
    	else {            
            LOGGER.error(
                    "{}: {}: \"Switch Affinity Strict\" or \"Switch Affinity Best Effort\" are the only options supported for now. Do not know how to handle {} in datacenter {}. Abort",
                    vmPlacementGroup.getSimulation().clockStr(), getClass().getSimpleName(), vmPlacementGroup, getDatacenter().getId());
            
            this.getSimulation().abort();
            
            return false;
    	} 	    	
    }
    
    
    /**
     * Tries to place the VMs of a {@link VmPlacementGroup} with {@link VmPlacementGroupScope#SWITCH} scope, 
     * {@link VmPlacementGroupAffinityType#AFFINITY} affinity and {@link VmPlacementGroupEnforcement#STRICT} enforcement.
     * Temporary VMs are used as placeholders in order to check if all VMs can be placed successfully. 
     * If yes, the temporary VMs are replaced by the actual VMs of the {@link VmPlacementGroup}. Otherwise,
     * the temporary VMs are destroyed.
     * 
     * @param vmPlacementGroup the {@link VmPlacementGroup} that is requested to be placed
     * @return true if all VMs are placed successfully or false otherwise. 
     */
    protected boolean switchAffinityStrict(final VmPlacementGroup vmPlacementGroup) {
        
    	final Map<Vm, Vm> tempToRealVms = this.createTempToRealVmsMap(vmPlacementGroup);
    	final List<Vm> tempVms = new ArrayList<Vm>(tempToRealVms.keySet());
    	final Map<Vm, Host> tempVmsToHosts = new IdentityHashMap<Vm, Host>();

    	final Set<Host> uniqueHosts = Collections.newSetFromMap(new IdentityHashMap<>()); 
    	
    	for (int switchId = 0; switchId < this.switchHosts.size(); switchId++) {
    		            
            boolean failedAttempt = false;
            
            for (ListIterator<Vm> tempVmIt = tempVms.listIterator(); tempVmIt.hasNext();) {
    				        
    			final Vm tempVm = tempVmIt.next();
    			
                final Optional<Host> optional = this.findHostForTempVmInSwitch(tempVm, switchId);
	            
	            if(optional.isPresent() && optional.get().createTemporaryVm(tempVm).fully()) {
	            	tempVmsToHosts.put(tempVm, optional.get());
	                uniqueHosts.add(optional.get());
	            }
	            else {
	            	failedAttempt = true;
	            	break;
	            }
            
    		}
    		
    		if(failedAttempt) {
    			this.destroyCreatedTempVms(tempVmsToHosts);
    			tempVmsToHosts.clear();
        		uniqueHosts.clear();
    		}
    		else  {
        		vmPlacementGroup.setNumOfSwitches(1);
        		vmPlacementGroup.setNumOfHosts(uniqueHosts.size());
            	this.replaceTempVmsWithRealVms(tempToRealVms, tempVmsToHosts);
            	vmPlacementGroup.setCreated(true);
            	vmPlacementGroup.setStartTime(this.getSimulation().clock());
    			return true;
    		}
    	}
    	
    	return false; 	
    }
    
    /**
     * Tries to place the VMs of a {@link VmPlacementGroup} with {@link VmPlacementGroupScope#SWITCH} scope, 
     * {@link VmPlacementGroupAffinityType#AFFINITY} affinity and {@link VmPlacementGroupEnforcement#BEST_EFFORT} enforcement.
     * Temporary VMs are used as placeholders in order to check if all VMs can be placed successfully. 
     * If yes, the temporary VMs are replaced by the actual VMs of the {@link VmPlacementGroup}. Otherwise,
     * the temporary VMs are destroyed.
     * 
     * @param vmPlacementGroup the {@link VmPlacementGroup} that is requested to be placed
     * @return true if all VMs are placed successfully or false otherwise. 
     */
    protected boolean switchAffinityBestEffort(final VmPlacementGroup vmPlacementGroup) {
    	
    	// if the request can ideally fit under one switch, we try first to place it that way
    	if(vmPlacementGroup.getIdealNumOfSwitches() == 1 && this.switchAffinityStrict(vmPlacementGroup))
    		return true;

    	final Map<Vm, Vm> tempToRealVms = this.createTempToRealVmsMap(vmPlacementGroup);
    	final List<Vm> tempVms = new ArrayList<Vm>(tempToRealVms.keySet());
    	final Map<Vm, Host> tempVmsToHosts = new IdentityHashMap<Vm, Host>();

    	final Set<Integer> uniqueSwitches = new HashSet<Integer>(); 
    	final Set<Host> uniqueHosts = Collections.newSetFromMap(new IdentityHashMap<>()); 
    	
    	
    	for (int switchId = this.rightToLeftFirstSwitchIndex; switchId >=0 ; switchId--) {
    		        	    		
            for (ListIterator<Vm> tempVmIt = tempVms.listIterator(); tempVmIt.hasNext();) {
    				        
    			final Vm tempVm = tempVmIt.next();
            	            		        	
	            final Optional<Host> optional = this.findHostForTempVmInSwitch(tempVm, switchId);
	            
	            if(optional.isPresent() && optional.get().createTemporaryVm(tempVm).fully()) {
		        	tempVmsToHosts.put(tempVm, optional.get());
                	tempVmIt.remove();
	            	uniqueSwitches.add(switchId);
	            	uniqueHosts.add(optional.get());
	            }
            }
    		
    		if (tempVms.isEmpty()) {
        		vmPlacementGroup.setNumOfSwitches(uniqueSwitches.size());
        		vmPlacementGroup.setNumOfHosts(uniqueHosts.size());
            	this.replaceTempVmsWithRealVms(tempToRealVms, tempVmsToHosts);
            	vmPlacementGroup.setCreated(true);
            	vmPlacementGroup.setStartTime(this.getSimulation().clock());
            	
            	if(switchId >= 2)
            		this.rightToLeftFirstSwitchIndex = switchId - 1;
            	else
            		this.rightToLeftFirstSwitchIndex = this.switchHosts.size() - 1;
            	
        		return true;
    		}
    	}

    	this.destroyCreatedTempVms(tempVmsToHosts);

    	// if the placement failed and we didn't start from the last switch, let's try 
    	// one more time to see if the request can be placed by starting from the last switch
    	if(this.rightToLeftFirstSwitchIndex != this.switchHosts.size() - 1) {
    		
    		this.rightToLeftFirstSwitchIndex = this.switchHosts.size() - 1;
    		
    		return this.switchAffinityBestEffort(vmPlacementGroup);
    	}
    	else {
    		return false;
    	}   	
    }
	
    /**
     * Takes as input a {@link List} with hosts and initializes the {@link #switchHosts}.
     * It is called in the constructor {@link #VmAllocationPolicyBestFitWithPlacementGroups_LRRL}
     * 
     * @param hosts the list with the hosts
     */
	private final void setupSwitches(final List<Host> hosts) {
    	
		long numberOfSwitches = hosts.size() / this.getNumOfHostsPerSwitch(); 

		for(long i = 0; i < numberOfSwitches; i++) {
			
			int firstHostOfSwitch = (int) (i * this.getNumOfHostsPerSwitch());
			int lastHostOfSwitch = (int) (firstHostOfSwitch + this.getNumOfHostsPerSwitch() - 1);
	    		    	
	    	this.switchHosts.add(
	    			new ArrayList<Host>(hosts.subList(firstHostOfSwitch, lastHostOfSwitch + 1))); // +1 because toIndex is exclusive

		}
    }
	
    /**
     * Returns the default comparator that is used when searching for hosts to place the VMs. 
     * According to this implementation we always choose the host with the minimum number of 
     * free Pes.
     * 
     * @return the default {@link Comparator} for hosts
     */
	private final Comparator<Host> getDefaultHostComparator() {
		
        /* Since it's being used the min operation, the active comparator must be reversed so that
         * we get active hosts with minimum number of free PEs. */
        final Comparator<Host> activeComparator = Comparator.comparing(Host::isActive).reversed();
        final Comparator<Host> comparator = activeComparator.thenComparingLong(Host::getFreePesNumber);

        return comparator;
	}
	
    /**
     * Takes as input a temporary VM and a switch id and tries to find a host for this VM that is 
     * connected to this switch. 
     */
    protected Optional<Host> findHostForTempVmInSwitch (final Vm tempVm, final int switchId) {
    	
    	final List<Host> switchHosts = this.switchHosts.get(switchId);
    	
        
    	final Stream<Host> stream = isParallelHostSearchEnabled() ? switchHosts.stream().parallel() : switchHosts.stream();
    	
    	return stream.filter(host-> host.isSuitableForVm(tempVm)).min(this.getDefaultHostComparator());
    }
}

