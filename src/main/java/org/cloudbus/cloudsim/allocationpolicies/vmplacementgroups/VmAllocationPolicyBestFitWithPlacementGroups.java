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
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.vmplacementgroup.VmPlacementGroup;
import org.cloudsimplus.traces.azure.TracesSimulationManager;
import org.cloudsimplus.traces.azure.TracesStatisticsManager;

/**
 * An extension of the {@link VmAllocationPolicyBestFit} that supports requests with {@link VmPlacementGroup}s. 
 * It works in a similar way as {@link VmAllocationPolicyBestFit} by trying to place all the 
 * VMs of a request in a sequential manner (i.e., one by one). Since we try to allocate resources to 
 * groups of VMs, a request can be rejected if no resources exist for all the VMs cumulatively.
 *
 * The {@link VmAllocationPolicyBestFitWithPlacementGroups} considers that the hosts are connected to
 * top-of-rack (ToR) switches and it keeps statistics about how many ToRs are used for the placement
 * of the requests. 
 *
 * <p><b>NOTE: As it is the case with the {@link VmAllocationPolicyBestFit}, this policy does not 
 * perform optimization of VM allocation by means of VM migration.</b></p>
 *
 * @since CloudSim Plus 7.3.2
 *
 * @author Pavlos Maniotis
 */
public class VmAllocationPolicyBestFitWithPlacementGroups extends VmAllocationPolicyBestFit {
	
	/**
	 * The simulation object
	 */
	final private CloudSim simulation;
	
	/**
	 * Number of hosts per switch
	 */
	final private long hostsPerSwitch;

	/**
	 * The simulation manager
	 */
	final protected TracesSimulationManager simulationManager;
    
	/**
	 * Takes as input the {@link CloudSim} and {@link TracesSimulationManager} and performs basic
	 * checks about the number of hosts per switch. See {@link TracesSimulationManager} for more
	 * information about how to run this kind of trace-based simulations. 
	 * 
	 * @param simulation a handle to the {@link CloudSim} object 
	 * @param simulationManager see {@link TracesSimulationManager}
	 */
	public VmAllocationPolicyBestFitWithPlacementGroups(final CloudSim simulation, final TracesSimulationManager simulationManager) {
		super();
		
		this.simulation = simulation;
		
		this.simulationManager = simulationManager;
		
		this.hostsPerSwitch = this.simulationManager.getHostsPerSwitch();
		
		if(this.hostsPerSwitch <= 0) {
			LOGGER.error("{}: {}: Number of hosts per switch <= 0 for {}. Abort",
						 this.simulation.clockStr(),
						 this.getClass().getSimpleName());
			
			this.simulation.abort();
		}
		
		if (this.simulationManager.getHosts().size() % this.hostsPerSwitch != 0) {
			
			LOGGER.error("{}: {}: Number of hosts must be multiple integer of the number hosts per switch. Abort",
						 this.simulation.clockStr(),
						 this.getClass().getSimpleName());
			
			this.simulation.abort();
		}
		

	}
	
	/**
     * It overrides the {@link VmAllocationPolicyBestFit#allocateHostForVm(Vm)} in order to provide support
     * for {@link VmPlacementGroup} requests. It handles also the update of the statistics through 
     * the {@link TracesStatisticsManager} and it updates the LOGGER with messages about the placement of 
     * the requested resources.
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
            
            this.simulation.abort();
            
            return new HostSuitability("Cannot place request because it has already been placed.");
        }
        
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
        	this.simulation.terminate();
        }
        	
        
        return suitability;
    }
    
    /**
     * Tries to find one or more hosts to allocate resources for the VMs of a {@link VmPlacementGroup}.
     * Temporary VMs are used as placeholders in order to check if all VMs can be placed successfully. 
     * If yes, the temporary VMs are replaced by the actual VMs of the {@link VmPlacementGroup}. Otherwise,
     * the temporary VMs are destroyed.
     * 
     * @param vmPlacementGroup the requested {@link VmPlacementGroup}
     * @return true if all VMs are placed successfully or false otherwise. 
     */
    protected boolean findAndAllocateHostsForVmPlacementGroup(final VmPlacementGroup vmPlacementGroup) {
    	
    	final Map<Vm, Vm> tempToRealVms = this.createTempToRealVmsMap(vmPlacementGroup);
    	final List<Vm> tempVms = new ArrayList<Vm>(tempToRealVms.keySet());
    	final Map<Vm, Host> tempVmsToHosts = new IdentityHashMap<Vm, Host>();
    	
    	final Set<Long> uniqueSwitches = new HashSet<Long>(); 
    	final Set<Host> uniqueHosts = Collections.newSetFromMap(new IdentityHashMap<>()); 
    	    	
    	
        for (ListIterator<Vm> tempVmIt = tempVms.listIterator(); tempVmIt.hasNext();) {
        	
        	final Vm tempVm = tempVmIt.next();
            
	        final Optional<Host> optional = findHostForTempVm(tempVm);

	        if (optional.isPresent() && optional.get().createTemporaryVm(tempVm).fully()) {
	        	tempVmsToHosts.put(tempVm, optional.get());
	        	tempVmIt.remove();
            	long hostIndex = this.getHostList().indexOf(optional.get()); // TODO: We use the host index to define the ToR it is connected to.
            	                                                             // Could use some form of other id?
            	long switchId = hostIndex / this.hostsPerSwitch;
            	uniqueSwitches.add(switchId);
            	uniqueHosts.add(optional.get());
	        }
	        else {
	        	break;
	        }
        }
        
        if (tempVms.isEmpty()) {
    		vmPlacementGroup.setNumOfSwitches(uniqueSwitches.size());
    		vmPlacementGroup.setNumOfHosts(uniqueHosts.size());
        	this.replaceTempVmsWithRealVms(tempToRealVms, tempVmsToHosts);
        	vmPlacementGroup.setCreated(true);
        	vmPlacementGroup.setStartTime(this.simulation.clock());
    		return true;
        }
    	
    	this.destroyCreatedTempVms(tempVmsToHosts);
    	
    	return false;
    }
    
    /**
     * Takes as input a {@link VmPlacementGroup} and returns a {@link Map} with temporary to real VM associations
     * for the VMs of the group.
     * 
     * @param vmPlacementGroup the {@link VmPlacementGroup} for which we want to create temporary VMs
     * @return the {@link Map} with the temporary to real VM associations
     */
    protected Map<Vm, Vm> createTempToRealVmsMap(final VmPlacementGroup vmPlacementGroup) {
    	
    	final Map<Vm, Vm> tempVms = new IdentityHashMap<Vm, Vm> ();
    	
    	for (Vm realVm : vmPlacementGroup.getVmList()) {
    		
        	Vm tempVm = new VmSimple(realVm);
        	tempVm.setId(Long.MAX_VALUE - realVm.getId()); // A unique temp id for the tempVM. There shouldn't be 
        	                                               // any duplicate temp ids as long as real ids are unique
        	tempVm.setBroker(realVm.getBroker());
        	tempVms.put(tempVm, realVm);
    	}

    	return tempVms;
    }
    
    /**
     * Takes as input a {@link Map} with the temporary to real VM associations and a {@link Map} with the temporary 
     * VMs to host associations (hosts that have used to place them). It replaces the temporary VMs with the real VMs, while the 
     * temporary VMs are destroyed.  
     *  
     * @param tempToRealVms the temporary to real VM associations
     * @param tempVmsToHosts the temporary VMs and the hosts have been used to place them  
     */
    protected void replaceTempVmsWithRealVms(final Map<Vm, Vm> tempToRealVms, final Map<Vm, Host> tempVmsToHosts) {
     	
    	for (Map.Entry<Vm, Host> tempVmToHost : tempVmsToHosts.entrySet()) {
    		
    		Vm tempVm = tempVmToHost.getKey();
    		
    		Host host = tempVmToHost.getValue();
    		
			host.destroyTemporaryVm(tempVm);

			Vm realVm = tempToRealVms.get(tempVm);
						
			if(!host.createVm(realVm).fully()) {
				 LOGGER.error("{}: {}: Cannot replace temp Vm with real Vm in {} in Datacenter {}. Abort",
							  this.simulation.clockStr(),
							  this.getClass().getSimpleName(),
							  host,
							  this.getDatacenter().getId());
				 this.simulation.abort();
				 return;
			}
						
		   	host.setActive(true);
    	}       	
    }
    
    /** 
     * Takes as input a {@link Map} with temporary VMs to hosts associations 
     * and destroys the VMs to free up the allocated resources.
     * 
     * @param tempVmsToHosts the temporary VMs to host associations
     */
    protected void destroyCreatedTempVms(final Map<Vm, Host> tempVmsToHosts) {
    	
    	tempVmsToHosts.entrySet().stream().forEach(es -> {
    		
    		es.getValue().destroyTemporaryVm(es.getKey());
    	});
    }
    
    /**
     * Finds a suitable host that has enough resources to place a temporary VM. Internally it 
     * may use a default implementation or one set in runtime. The host is not set to active status
     * because the resources are allocated to a temporary VM.  
     * 
     * @param tempVm the temporary VM for which we try to allocate resources to
     * @return an {@link Optional} containing a suitable Host to place the VM or an empty {@link Optional} if no suitable Host was found
     */
    private Optional<Host> findHostForTempVm (final Vm tempVm) {
        return this.findHostForVmFunction == null ? defaultFindHostForVm(tempVm) : this.findHostForVmFunction.apply(this, tempVm);
        // In contrast to findHostForVm, we don't call the host.setActive(true)) function.
        // It will be called once the tempVm will be replaced by the realVm. see replaceTempVmsWithRealVms above
    }
    
    /**
     * Takes as input a {@link Vm} that was requested to be placed and prints messages through 
     * the LOGGER about the status of the request.
     * 
     * @param vm the requested {@link Vm}
     */
    protected void printLog (final Vm vm) {
    	
    	if(!(vm instanceof VmPlacementGroup)) {
    		if (!vm.isCreated())
    	        LOGGER.warn("{}: {}: No suitable host found for {} in {}", 
    	        		     vm.getSimulation().clockStr(), 
    	        		     getClass().getSimpleName(), 
    	        		     vm, 
    	        		     this.getDatacenter());
    		else {
            	long hostIndex = this.getHostList().indexOf(vm.getHost());  // TODO: same as above regarding the host index
            	long switchId = hostIndex / this.hostsPerSwitch;
    			LOGGER.info("{}: {}: {} has been placed on {} in switch {} in {}", 
					     vm.getSimulation().clockStr(), 
					     getClass().getSimpleName(), 
					     vm, 
					     vm.getHost(), 
					     switchId,
					     this.getDatacenter());
    		}

    		return;
    	}
    	
    	VmPlacementGroup vmPlacementGroup = (VmPlacementGroup) vm;
    	    	
    	if (!vmPlacementGroup.isCreated()) {
	        LOGGER.warn("{}: {}: No suitable host(s) found for {} (size {}) with {} {} {} in {}", 
	        			 vmPlacementGroup.getSimulation().clockStr(), 
       		             getClass().getSimpleName(), 
       		             vmPlacementGroup,
       		             vmPlacementGroup.getVmList().size(),
       		             vmPlacementGroup.getScope(),
       		             vmPlacementGroup.getAffinityType(),
       		             vmPlacementGroup.getEnforcement(),
       		             this.getDatacenter());
    	}
    	else {
    		LOGGER.info("{}: {}: {} (size {}) with {} {} {} has been placed in {} unique host(s) and {} unique switches in {}",
                         vmPlacementGroup.getSimulation().clockStr(), 
                         getClass().getSimpleName(), 
                         vmPlacementGroup, 
       		             vmPlacementGroup.getVmList().size(),
       		             vmPlacementGroup.getScope(),
       		             vmPlacementGroup.getAffinityType(),
       		             vmPlacementGroup.getEnforcement(),                         
       		             vmPlacementGroup.getNumOfHosts(),
                         vmPlacementGroup.getNumOfSwitches(),
                         this.getDatacenter());
            
    		final List<Vm> vmPlacementGroupVms = vmPlacementGroup.getVmList();
    		
            for (Vm createdVm : vmPlacementGroupVms) {
            	long hostIndex = this.getHostList().indexOf(createdVm.getHost()); // TODO: same as above regarding the host index
            	long switchId = hostIndex / this.hostsPerSwitch;
    			LOGGER.info("{}: {}: {} -> {} has been placed on {} in switch {} in {}", 
					     createdVm.getSimulation().clockStr(), 
					     getClass().getSimpleName(), 
					     vmPlacementGroup,
					     createdVm, 
					     createdVm.getHost(), 
					     switchId,
					     this.getDatacenter());
            }
    	}
    }
    
    /**
     * Getter for the {@link CloudSim} object of the simulation 
     * 
     * @return the {@link CloudSim} object of the simulation
     */
    protected CloudSim getSimulation() {
    	return this.simulation;
    }
    
    /**
     * Getter for the number of hosts per switch
     * 
     * @return the number of hosts per switch
     */
    protected long getNumOfHostsPerSwitch() {
    	return this.hostsPerSwitch;
    }
}

