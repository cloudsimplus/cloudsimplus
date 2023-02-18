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

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyBestFit;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSuitability;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroup;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupAffinityType;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupEnforcement;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroupScope;
import org.cloudsimplus.traces.azure.TracesSimulationManager;

/**
 * An extension of the {@link VmAllocationPolicyBestFitWithPlacementGroups} that implements
 * an all-pack configuration of the topology-aware Chic-sched scheduler published in [1]. This 
 * implementation assumes that the servers are connected through a single-zone 2-level network as in 
 * the example of Fig. 2 in [1]. As for the algorithm constraints, this implementation considers (Pack, Soft)
 * at the server level, while it supports both (Pack, Soft) and (Pack, Hard) for the rack levels.  
 * Packing at the lowest levels is of particular importance in oversubscribed networks: it reduces 
 * the amount of traffic crossing higher-layer switches, which decreases the likelihood of network congestion 
 * and of performance variability due to latency tails for tightly-coupled workloads. Placing a request  
 * in a single rack has two key advantages: (a) communication cost is always one-hop maximum, and (b) network 
 * contention due to crossing the network spine is eliminated.
 * 
 * Since we allocate resources to groups of VMs, a request is rejected if no resources exist 
 * for all the VMs of a group request cumulatively.
 *
 * <p><b>NOTE: As it is the case with the {@link VmAllocationPolicyBestFitWithPlacementGroups} implementation, 
 * this policy doesn't perform optimization of VM allocation by means of VM migration.</b></p>
 *
 * References:
 * [1] Asser Tantawi, Pavlos Maniotis, Ming-Hung Chen, Claudia Misale, Seetharami Seelam, 
 *     Hao Yu, Laurent Schares, "Chic-sched: a HPC Placement-Group Scheduler on Hierarchical 
 *     Topologies with Constraints," 37th IEEE International Parallel & Distributed Processing 
 *     Symposium (IPDPS 2023), St. Petersburg, Florida, USA, May 15-19, 2023
 *     
 * @author Pavlos Maniotis
 *
 * @since CloudSim Plus 7.3.2
 *
 * @see VmAllocationPolicyBestFitWithPlacementGroups
 * @see VmAllocationPolicyBestFit
 */

public class VmAllocationPolicyChicSchedAllPack extends VmAllocationPolicyBestFitWithPlacementGroups {
	
	/**
	 * Each item of the list represents a switch with the hosts attached to it.
	 */
	protected final List<List<Host>> switchHosts = new ArrayList<List<Host>>();
	
	/**
	 * Takes as input the {@link CloudSim} and {@link TracesSimulationManager} and feeds them to the constructor
	 * of the superclass to performs basic checks about the number of hosts per switch. It also initializes 
	 * {@link #switchHosts}. 
	 * 
	 * @param simulation a handle to the {@link CloudSim} object 
	 * @param simulationManager see {@link TracesSimulationManager} for info about how to run this kind of trace-based simulations. 
	 */
	public VmAllocationPolicyChicSchedAllPack(final CloudSim simulation, final TracesSimulationManager simulationManager) {
		
		super(simulation, simulationManager);
		
		this.setupSwitches(this.simulationManager.getHosts());
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

            this.sortSwitchHostsForPlacement(switchId); 
    		
            final Optional<Host> optional = this.findHostForTempVmInSwitch(vm, switchId);
            
            if(optional.isPresent())
            	return optional;
    	}

    	return Optional.empty();
    }
	
	/**
     * It overrides the {@link VmAllocationPolicyBestFitWithPlacementGroups#allocateHostForVm(Vm)}
     * to add support for the Chic-sched implementation. 
     * 
     * @param vm {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public HostSuitability allocateHostForVm(final Vm vm) {
    	    	
        if (getHostList().isEmpty()) {
            LOGGER.error(
                "{}: {}: {} could not be allocated because there isn't any Host for Datacenter {}",
                vm.getSimulation().clockStr(), getClass().getSimpleName(), vm, getDatacenter().getId());
            
            return new HostSuitability("Cannot place request because the datacenter has no hosts.");
        }

        if (vm.isCreated()) {
            LOGGER.error(
                    "{}: {}: Tried to allocate host for {}, which has been already allocated to {} in Datacenter {}. Abort",
                    vm.getSimulation().clockStr(), getClass().getSimpleName(), vm, vm.getHost(), getDatacenter().getId());
            
            this.getSimulation().abort();
            
            return new HostSuitability("Cannot place request because it has already been placed.");
        }
        
        this.sortSwitchesForPlacement();
        
        HostSuitability suitability = new HostSuitability();
        
        if (!(vm instanceof VmPlacementGroup)) {	

	        final Optional<Host> optional = findHostForVm(vm);
	        	        
	        if (optional.filter(Host::isActive).isPresent()) 
	        	suitability = allocateHostForVm(vm, optional.get());
        }
        else {
        	if(findAndAllocateHostsForVmPlacementGroup((VmPlacementGroup) vm)) {
        		suitability.setForPes(true).setForRam(true).setForBw(true).setForStorage(true);
        	}
        }
                
        this.printLog(vm);
        
        this.simulationManager.getBatchesManager()
                              .getStatisticsManager()
                              .updateAllocationPolicyTraces(vm);
        
        if(!suitability.fully() && this.simulationManager.quitOnAllocationFailure()) {
        	LOGGER.info("Terminating because request placement has failed and quitOnAllocationFailure is true");
        	this.getSimulation().terminate();
        }
        
        return suitability;
    }
    
    /**
     * {@inheritDoc}} 
     */
    @Override
    protected boolean findAndAllocateHostsForVmPlacementGroup(VmPlacementGroup vmPlacementGroup) { 	    	
    	
    	if(vmPlacementGroup.isSwitchAffinityStrict())
    		return switchAffinityStrict(vmPlacementGroup);

    	else if(vmPlacementGroup.isSwitchAffinityBestEffort())
    		return switchAffinityBestEffort(vmPlacementGroup);
    	
    	else 
    		return false;
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
    protected boolean switchAffinityStrict(VmPlacementGroup vmPlacementGroup) {
        
    	final Map<Vm, Vm> tempToRealVms = this.createTempToRealVmsMap(vmPlacementGroup);
    	final List<Vm> tempVms = new ArrayList<Vm>(tempToRealVms.keySet());
    	final Map<Vm, Host> tempVmsToHosts = new IdentityHashMap<Vm, Host>();

    	final Set<Host> uniqueHosts = Collections.newSetFromMap(new IdentityHashMap<>()); 
    	    	
    	for (int switchId = 0; switchId < this.switchHosts.size(); switchId++) {
    		            
            boolean failedAttempt = false;
            
            this.sortSwitchHostsForPlacement(switchId); 
            
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
    protected boolean switchAffinityBestEffort(VmPlacementGroup vmPlacementGroup) {

    	final Map<Vm, Vm> tempToRealVms = this.createTempToRealVmsMap(vmPlacementGroup);
    	final List<Vm> tempVms = new ArrayList<Vm>(tempToRealVms.keySet());
    	final Map<Vm, Host> tempVmsToHosts = new IdentityHashMap<Vm, Host>();

    	final Set<Integer> uniqueSwitches = new HashSet<Integer>(); 
    	final Set<Host> uniqueHosts = Collections.newSetFromMap(new IdentityHashMap<>()); 
    	
    	
    	for (int switchId = 0; switchId < this.switchHosts.size(); switchId++) {

            this.sortSwitchHostsForPlacement(switchId); 

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

        		return true;
    		}
    	}

    	this.destroyCreatedTempVms(tempVmsToHosts);

    	return false;   	
    }
	
	
    /**
     * Takes as input a {@link List} with hosts and initializes the {@link #switchHosts}.
     * It is called in the constructor {@link #VmAllocationPolicyChicSchedAllPack(CloudSim, TracesSimulationManager)}
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
     * Takes as input a temporary VM and a switch id and tries to find a host for this VM that is 
     * connected to this switch. 
     */
    protected Optional<Host> findHostForTempVmInSwitch (final Vm tempVm, final int switchId) {
    	
    	final List<Host> switchHosts = this.switchHosts.get(switchId);
    	
    	for(ListIterator<Host> tempHostIt = switchHosts.listIterator(); tempHostIt.hasNext();) {
    		
    		final Host tempHost = tempHostIt.next();
    		
    		if(tempHost.isSuitableForVm(tempVm)) {
    			return Optional.ofNullable(tempHost);
    		}
    	}
    	
    	return Optional.empty();
    }

    /**
     * Sorts the hosts of a switch in descending order because we pack at the host level.
     * The sorting is realized according to the number of free PEs per host.
     * 
     * @param switchId the id of the switch for which we sort the hosts
     */
    private void sortSwitchHostsForPlacement(final int switchId) {

    	final List<Host> switchHosts = this.switchHosts.get(switchId);
    	
    	Comparator<Host> comparator = 
    			Comparator.comparingInt(Host::getFreePesNumber);

    	Comparator<Host> comparatorReversed = comparator.reversed();
    	
    	switchHosts.sort(comparatorReversed);
    }
    
    
    /**
     * Sorts the switches in descending order because we pack at the switch level.
     * The sorting is realized according to the free number of PEs per switch.
     */
    private void sortSwitchesForPlacement() {

    	Comparator<List<Host>> comparator = 
    			Comparator.comparingInt(i -> i.stream().mapToInt(Host::getFreePesNumber).sum());

    	Comparator<List<Host>> comparatorReversed = comparator.reversed();

    	this.switchHosts.sort(comparatorReversed);
    }
}



