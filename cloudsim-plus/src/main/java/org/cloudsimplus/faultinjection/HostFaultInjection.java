/**
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.faultinjection;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.*;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.distributions.PoissonDistr;

/**
 * Generates random failures for the {@link Pe}'s of {@link Host}s
 * inside a given {@link Datacenter}. 
 * 
 * The events happens in the following order:
 * <ol>
 *  <li>a is randomly selected to fail;</li>
 *  <li>the number of PEs to fail is randomly generated;</li>
 *  <li>failed physical PEs are removed from affected VMs;</li>
 *  <li>another failure is scheduled for a future time.</li>
 * </ol>
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.0
 * @see https://blogs.sap.com/2014/07/21/equipment-availability-vs-reliability/
 */
public class HostFaultInjection extends CloudSimEntity {
    private Datacenter datacenter;
    /**
     * A Pseudo Random Number Generator used to select a Host 
     * and the number of PEs to set as fail. 
     */
    private ContinuousDistribution random;
    
    /**
     * @see #getLastFailedHost() 
     */
    private Host lastFailedHost;
    
    /**
     * @see #setVmCloner(java.util.function.UnaryOperator) 
     * 
     * @todo The class has multiple responsibilities.
     * The fault injection mechanism must be separated from
     * the fault recovery.
     * The cloner methods are fault recovery.
     * 
     */
    private UnaryOperator<Vm> vmCloner;
    
    /**
     * @see #setCloudletsCloner(java.util.function.Function) 
     */
    private Function<Vm, List<Cloudlet>> cloudletsCloner;
    
    /**
     * A Pseudo Random Number Generator which generates the
     * times (in minutes) that Hosts failures will occur.
     */
    private ContinuousDistribution failureArrivalTimesGenerator;
    
    /**
     * Creates a fault injection mechanism for the Hosts of a given {@link Datacenter}.
     * The failures are randomly injected according to the given 
     * mean of failures to be generated per <b>minute</b>,
     * which is also called <b>event rate</b> or <b>rate parameter</b>.
     *
     * @param datacenter the Datacenter to which failures will be randomly injected for its Hosts
     * 
     * @param failureArrivalTimesGenerator a Pseudo Random Number Generator which generates the
     * times that Hosts failures will occur. 
     * <b>The values returned by the generator will be considered to be minutes</b>.
     * Frequently it is used a 
     * {@link PoissonDistr} to generate failure arrivals, but any {@link ContinuousDistribution}
     * can be used.
     */
    public HostFaultInjection(Datacenter datacenter, ContinuousDistribution failureArrivalTimesGenerator) {
        super(datacenter.getSimulation());
        this.setDatacenter(datacenter);
        this.lastFailedHost = Host.NULL;
        this.failureArrivalTimesGenerator = failureArrivalTimesGenerator;
        this.random = new UniformDistr(failureArrivalTimesGenerator.getSeed());
        
        /*Sets a default vmCloner which in fact doesn't
        clone a VM, just returns the Vm.NULL object.
        This is used just to ensure that if a vmClone
        is not set, it wont be thrown a NullPointerException
        and no VM will be cloned.
        A similar thing is made to the cloudletsCloner below.*/
        this.vmCloner = vm -> { 
            Log.printFormattedLine(
                "%s: You should define a VM Cloner Function to enable creating a new instance of a VM when it is destroyed due to a Host failure.", 
                getClass().getSimpleName());
            return Vm.NULL; 
        };
        this.cloudletsCloner = vm -> {
            Log.printFormattedLine(
                "%s: You should define a Cloudlets Cloner Function to re-create the List of running Cloudlets from a faulty VM into the Cloned VM when there is a Host failure", 
                getClass().getSimpleName());
            return Collections.EMPTY_LIST;
        };
    }    

    @Override
    protected void startEntity() {
        scheduleFailureInjection();
    }

    /**
     * Schedules a message to be processed internally 
     * to inject a Host PEs failure.
     */
    private void scheduleFailureInjection() {
        final long numOfOtherEvents = 
                getSimulation()
                        .getNumberOfFutureEvents(
                            evt -> evt.getTag() != CloudSimTags.HOST_FAILURE);
        if(numOfOtherEvents > 0){
            schedule(getId(), getTimeDelayForNextFailure(), CloudSimTags.HOST_FAILURE);
        }
    }

    /**
     * Gets the time delay in seconds, from the current simulation time,
     * that the next failure will be injected.
     * Since the values returned by the {@link #failureArrivalTimesGenerator} 
     * are considered to be in minutes, such values are converted to seconds.
     * 
     * @return the next failure injection delay in seconds
     */
    private double getTimeDelayForNextFailure() {
        return failureArrivalTimesGenerator.sample() * 60;
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.HOST_FAILURE:
                generateHostFailure();
            break;
            default:
                Log.printLine(getName() + ": unknown event type");
            break;
        }
    }

    /**
     * Generates a failure for a specific number of PEs from a 
     * randomly selected Host.
     */
    private void generateHostFailure() {
        try {
            this.lastFailedHost = getRandomHost();
            if(Host.NULL.equals(lastFailedHost)){
                return;
            }
            
            final int numberOfFailedPes = generateHostPesFailures();
            final long hostWorkingPes = lastFailedHost.getNumberOfWorkingPes();
            final long vmsRequiredPes = getPesSumOfWorkingVms();
            
            Log.printFormattedLine("%.2f: %s: Generated %d PEs failures for %s at minute %.2f", 
                    getSimulation().clock(), getClass().getSimpleName(),
                    numberOfFailedPes, lastFailedHost, getSimulation().clock()/60);
            if(vmsRequiredPes == 0){
                Log.printFormattedLine("\tNumber of VMs: %d", lastFailedHost.getVmList().size());
            }
            Log.printFormattedLine("\tWorking PEs: %d | VMs required PEs: %d", hostWorkingPes, vmsRequiredPes);
            
            if(hostWorkingPes == 0){
                setAllVmsToFailed(); 
            } else if (hostWorkingPes >= vmsRequiredPes) {
                logNoVmFailure();  
            } else {
                deallocateFailedHostPesFromVms();
            } 
        } finally {
            //schedules the next failure injection
            scheduleFailureInjection();
        }
    }

    /**
     * Randomly gets a Host that will have some PEs set to failed.
     * @return the randomly selected Host or {@link Host#NULL}
     * if the Datacenter doesn't have Hosts.
     */
    private Host getRandomHost() {
        if(datacenter.getHostList().isEmpty()){
            return Host.NULL;
        }
        
        return datacenter.getHost((int)(random.sample()*datacenter.getHostList().size()));
    }

    /**
     * Sets all VMs inside the {@link #getLastFailedHost() last failed Host} to failed,
     * when all Host PEs have failed.
     */
    private void setAllVmsToFailed() {
        Log.printFormattedLine(
            "\tAll the %d PEs failed, affecting all its %d VMs.\n",
            lastFailedHost.getNumberOfPes(), lastFailedHost.getVmList().size());
        lastFailedHost.getVmList().stream().forEach(this::setVmToFailedAndCreateClone);
    }
    
    /**
     * Shows that the failure of Host PEs hasn't affected any VM,
     * because there is more working PEs than required by all VMs.
     */
    private void logNoVmFailure() {
        final int vmsRequiredPes = (int)getPesSumOfWorkingVms();
        Log.printFormattedLine(
            "\tNumber of failed PEs is less than PEs required by all its %d VMs, thus it doesn't affect any VM.",
            lastFailedHost.getVmList().size());
        Log.printFormattedLine(
            "\tTotal PEs: %d | Failed PEs: %d | Working PEs: %d | Current PEs required by VMs: %d.\n",
            lastFailedHost.getNumberOfPes(), lastFailedHost.getNumberOfFailedPes(), lastFailedHost.getNumberOfWorkingPes(),
            vmsRequiredPes);
    }
    
    /**
     * Deallocates the physical PEs failed for the 
     * {@link #getLastFailedHost() last failed Host} from affected VMs.
     */
    private void deallocateFailedHostPesFromVms() {
        final int hostWorkingPes = (int)lastFailedHost.getNumberOfWorkingPes();
        final int vmsRequiredPes = (int)getPesSumOfWorkingVms();
        
        int failedPesToRemoveFromVms = vmsRequiredPes-hostWorkingPes;
        final int affectedVms = Math.min(lastFailedHost.getVmList().size(), failedPesToRemoveFromVms);
        Log.printFormattedLine("\t%d PEs failed, from a total of %d PEs. There are %d PEs working.",
                lastFailedHost.getNumberOfFailedPes(), 
                lastFailedHost.getNumberOfPes(), lastFailedHost.getNumberOfWorkingPes());
        Log.printFormattedLine("\t%d VMs affected from a total of %d. %d PEs are going to be removed from VMs", 
                affectedVms, lastFailedHost.getVmList().size(), failedPesToRemoveFromVms);
        cyclicallyRemoveFailedHostPesFromVms(failedPesToRemoveFromVms, affectedVms);
        
        Log.printLine();
        setVmsToFailed();
    }

    /**
     * Removes one physical failed PE from one affected VM at a time.
     * Affected VMs are dealt as a circular list, visiting
     * one VM at a time to remove 1 PE from it, 
     * until all the failed PEs are removed.
     * 
     * @param failedPesToRemoveFromVms number of physical PEs to remove
     * @param affectedVms number of VMs affected by PEs failures
     */
    private void cyclicallyRemoveFailedHostPesFromVms(int failedPesToRemoveFromVms, final int affectedVms) {
        int i = 0;
        while(failedPesToRemoveFromVms-- > 0){
            i = i % affectedVms;
            Vm vm = lastFailedHost.getVmList().get(i);
            
            lastFailedHost.getVmScheduler().deallocatePesFromVm(vm, 1);
            vm.getCloudletScheduler().deallocatePesFromVm(vm, 1);
            //remove 1 failed PE from the VM
            vm.getProcessor().removeCapacity(1); 
            Log.printFormattedLine(
                    "\tRemoving 1 PE from VM %d due to Host PE failure. New VM PEs Number: %d\n", 
                    vm.getId(), vm.getNumberOfPes());
            i++;
        }
    }

    /**
     * Sets to failed all VMs that have all their PEs removed due to 
     * Host PEs failures.
     */
    private void setVmsToFailed() {
        lastFailedHost.getVmList().stream()
                .filter(vm -> vm.getNumberOfPes() == 0)
                .forEach(this::setVmToFailedAndCreateClone);
    }
    
    /**
     * Gets the number of VMs that are completely failed 
     * (which all their PEs were removed due to Host PEs failure).
     * 
     * @return 
     */
    public long getFailedVmsCount() {
        return lastFailedHost.getVmList()
                .stream()
                .filter(Vm::isFailed)
                .count();
    }

    /**
     * Sets a VM inside the {@link #getLastFailedHost() last failed Host}
     * to failed and use the VM and Cloudlets cloner functions
     * to create a clone of the VMs with all its Cloudlets,
     * to simulate the initialization of a new VM instance
     * from a snapshot of the failed VM.
     *
     * @param vm VM to set to failed
     */
    private void setVmToFailedAndCreateClone(Vm vm) {
        if (Host.NULL.equals(lastFailedHost)) {
            return;
        }
        
        final DatacenterBroker broker = vm.getBroker();
        final Vm clone = vmCloner.apply(vm);
       
        List<Cloudlet> cloudlets = cloudletsCloner.apply(vm);
        vm.setFailed(true);
       
        /*
         As the broker is expected to request vm creation and destruction,
         it is set here as the sender of the vm destroy request.
         */
        getSimulation().sendNow(vm.getBroker().getId(), datacenter.getId(),
                CloudSimTags.VM_DESTROY, vm);
      
        broker.submitVm(clone); 
        broker.submitCloudletList(cloudlets, clone);
    }
    
    /**
     * Generates random failures for the PEs from the 
     * {@link #getLastFailedHost() last failed Host}.
     * The minimum number of PEs to fail is 1.
     *
     * @return the number of failed PEs for the Host
     */
    private int generateHostPesFailures() {
        return (int)lastFailedHost.getWorkingPeList()
                        .stream()
                        .limit(randomNumberOfFailedPes())
                        .peek(pe -> pe.setStatus(Pe.Status.FAILED))
                        .count();
    }

    /**
     * Gets the total number of PEs from all working VMs.
     * @return 
     */
    private long getPesSumOfWorkingVms() {
        return lastFailedHost.getVmList().stream()
                .filter(vm -> !vm.isFailed())
                .mapToLong(vm -> vm.getNumberOfPes())
                .sum();
    }

    /**
     * Randomly generates a number of PEs which will fail for the datacenter.
     * The minimum number of PEs to fail is 1.
     *
     * @return the generated number of failed PEs for the datacenter,
     * between [1 and Number of PEs].
     * 
     */
    private int randomNumberOfFailedPes() {
        /*the random generator return values from [0 to 1]
         and multiplying by the number of PEs we get a number between
         0 and number of PEs*/
        return (int) (random.sample()*lastFailedHost.getWorkingPeList().size()) + 1;
    }

    /**
     * Gets the datacenter in which failures will be injected.
     *
     * @return
     */
    public Datacenter getDatacenter() {
        return datacenter;
    }

    /**
     * Sets the datacenter in which failures will be injected.
     *
     * @param datacenter the datacenter to set
     */
    protected final void setDatacenter(Datacenter datacenter) {
        Objects.requireNonNull(datacenter);
        this.datacenter = datacenter;
    }  
    
    /**
     * Sets a {@link UnaryOperator} that creates a clone of a {@link Vm}
     * when all Host PEs fail or all VM's PEs are deallocated
     * because they have failed.
     * 
     * <p>The {@link UnaryOperator} is a {@link Function} that
     * receives a {@link Vm} and returns a clone of it.
     * When all PEs of the VM fail, this vmCloner {@link Function}
     * is used to create a copy of the VM to be submitted to another Host.
     * It is like a VM snapshot in a real cloud infrastructure,
     * which will be started into another datacenter in order to
     * recovery from a failure.
     * </p>
     * 
     * @param vmCloner the VM cloner {@link Function} to set
     * @see #setCloudletsCloner(java.util.function.Function) 
     */
    public void setVmCloner(UnaryOperator<Vm> vmCloner) {
        Objects.requireNonNull(vmCloner);
        this.vmCloner = vmCloner;
    }

    /**
     * Sets a {@link Function} that creates a clone of all Cloudlets 
     * which were running inside a given failed {@link Vm}.
     * 
     * <p>Such a Function is used to re-create and re-submit those Cloudlets 
     * to a clone of the failed VM. In this case, all the Cloudlets are 
     * recreated from scratch into the cloned VM,
     * re-starting their execution from the beginning.
     * Since a snapshot (clone) of the failed VM will be started
     * into another Host, the Cloudlets Cloner Function will recreated
     * all Cloudlets, simulating the restart of applications
     * into this new VM instance.</p>
     * 
     * @param cloudletsCloner the cloudlets cloner {@link Function} to set
     * @see #setVmCloner(java.util.function.UnaryOperator) 
     */
    public void setCloudletsCloner(Function<Vm, List<Cloudlet>> cloudletsCloner) {
        Objects.requireNonNull(cloudletsCloner);
        this.cloudletsCloner = cloudletsCloner;
    }
    
    /**
     * Gets the last Host for which a failure was injected.
     * @return the last failed Host or {@link Host#NULL} if not Host
     * has failed yet.
     */
    public Host getLastFailedHost() {
        return lastFailedHost;
    }

    @Override
    public void shutdownEntity() {/**/}
}
