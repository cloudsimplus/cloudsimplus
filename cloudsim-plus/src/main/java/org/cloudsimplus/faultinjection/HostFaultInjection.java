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
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.PoissonProcess;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Pe.Status;

/**
 * Generates random failures for the {@link Pe}'s of a specific {@link Host}. 
 * The events happens in the following order:
 * <ol>
 *  <li>a failure is injected randomly;</li>
 *  <li>a delay is defined for the failure to actually happen;</li>
 *  <li>the number of PEs to fail is randomly generated.</li>
 * </ol>
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.0
 * @see https://blogs.sap.com/2014/07/21/equipment-availability-vs-reliability/
 */
public class HostFaultInjection extends CloudSimEntity {
    private Host host;
    private ContinuousDistribution random;
    
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
    
    private PoissonProcess failureInjectionGenerator;
    
    /**
     * Creates a fault injection mechanism for a host that will inject
     * failures with a delay and number of failed PEs generated using a given
     * Pseudo Random Number Generator (PRNG).
     *
     * @param host the Host to which failures may be randomly generated
     * @param meanFailuresPerMinute the average number of failures expected to happen each minute.
     * @param seed the seed to initialize the internal uniform random number generator
     * @see #setMaxFailureDelay(double) 
     */
    public HostFaultInjection(Host host, double meanFailuresPerMinute, long seed) {
        super(host.getSimulation());
        this.setHost(host);
        this.failureInjectionGenerator = new PoissonProcess(meanFailuresPerMinute, seed);
        this.random = new UniformDistr(seed);
        
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
            final double delay = failureInjectionGenerator.getNextArrivalTime(); 
            schedule(getId(), delay, CloudSimTags.HOST_FAILURE);
        }
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
     * Generates a failure on Host's PEs, according to the number of PEs
     * to be set to failed.
     */
    private void generateHostFailure() {
        try {
            final int numberOfFailedPes = generateHostPesFailures();
            
            final long hostWorkingPes = host.getNumberOfWorkingPes();
            final long vmsRequiredPes = getPesSumOfWorkingVms();
            Log.printFormattedLine("%.2f: %s: Generated %d PEs failures for %s", 
                    getSimulation().clock(), getClass().getSimpleName(),
                    numberOfFailedPes, host);
            if(vmsRequiredPes == 0){
                Log.printFormattedLine("\tNumber of VMs: %d", host.getVmList().size());
            }
            Log.printFormattedLine("\tWorking PEs: %d | VMs required PEs: %d", hostWorkingPes, vmsRequiredPes);
            if(hostWorkingPes == 0){
                //double clockInicioFalha = getSimulation().clock(); 
                //Log.printFormattedLine("\n#Inicio Falha no Host %d : %f", host.getId(),clockInicioFalha);
                setAllVmsToFailed(); 

            } else if (hostWorkingPes >= vmsRequiredPes) {
                logNoVmFailure();  
            } else {
                deallocateFailedHostPesFromVms();
            } 

        } finally {
            /*schedules the failure check for the next minute
            to see if a failure will be injected at that time.*/
            scheduleFailureInjection();
        }
    }

    /**
     * Sets all VMs to failed when all Host PEs failed.
     */
    private void setAllVmsToFailed() {
        Log.printFormattedLine("\tAll the %d PEs failed, affecting all its %d VMs.\n",
                host.getNumberOfPes(), host.getVmList().size());
        host.getVmList().stream().forEach(this::setVmToFailedAndCreateClone);
    }
    
    /**
     * Shows that the failure of Host PEs hasn't affected any VM,
     * because there is more working PEs than required by all VMs.
     */
    private void logNoVmFailure() {
        final int vmsRequiredPes = (int)getPesSumOfWorkingVms();
        Log.printFormattedLine(
                "\tNumber of failed PEs is less than PEs required by all its %d VMs, thus it doesn't affect any VM.",
                host.getVmList().size());
        Log.printFormattedLine("\tTotal PEs: %d | Failed PEs: %d | Working PEs: %d | Current PEs required by VMs: %d.\n",
                host.getNumberOfPes(), host.getNumberOfFailedPes(), host.getNumberOfWorkingPes(),
                vmsRequiredPes);
    }
    
    private void deallocateFailedHostPesFromVms() {
        final int hostWorkingPes = (int)host.getNumberOfWorkingPes();
        final int vmsRequiredPes = (int)getPesSumOfWorkingVms();
        
        int failedPesToRemoveFromVms = vmsRequiredPes-hostWorkingPes;
        final int affectedVms = Math.min(host.getVmList().size(), failedPesToRemoveFromVms);
        Log.printFormattedLine(
                "\t%d PEs failed, from a total of %d PEs. There are %d PEs working.",
                host.getNumberOfFailedPes(), 
                host.getNumberOfPes(), host.getNumberOfWorkingPes());
        Log.printFormattedLine(
                "\t%d VMs affected from a total of %d. %d PEs are going to be removed from VMs", 
                affectedVms, host.getVmList().size(), failedPesToRemoveFromVms);
        cyclicallyRemoveFailedHostPesFromVms(failedPesToRemoveFromVms, affectedVms);
        
        Log.printLine();
        setVmsToFailed();
    }

    /**
     * Removes one physical failed PE from one affected VM at a time.
     * The affected VMs are dealt as a circular list, visiting
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
            Vm vm = host.getVmList().get(i);
            
            host.getVmScheduler().deallocatePesFromVm(vm, 1);
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
     * Sets to failed all VMs that have all their PEs removed due to Host PEs failures.
     */
    private void setVmsToFailed() {
        host.getVmList().stream()
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
        return host.getVmList()
                .stream()
                .filter(Vm::isFailed)
                .count();
    }

    /**
     * Checks if the the host is failed and sets all a given VM to failed.
     *
     * @param vm vm to set to failed
     */
    private void setVmToFailedAndCreateClone(Vm vm) {
        if (!this.isFailed()) {
            return;
        }
        
        final DatacenterBroker broker = vm.getBroker();
        final Vm clone = vmCloner.apply(vm);
        setBrokerToEntity(clone, vm.getBroker());
       
        List<Cloudlet> cloudlets = cloudletsCloner.apply(vm);
        cloudlets.stream().forEach(c -> setBrokerToEntity(c, broker));
        
        vm.setFailed(true);
       
        /*
         As the broker is expected to request vm creation and destruction,
         it is set here as the sender of the vm destroy request.
         */
        getSimulation().sendNow(
                vm.getBroker().getId(), host.getDatacenter().getId(),
                CloudSimTags.VM_DESTROY, vm);
      
        broker.submitVm(clone); 
        broker.submitCloudletList(cloudlets, clone);
    }

    /**
     * Sets the {@link DatacenterBroker} to a object
     * if it doesn't have one set.
     * 
     * @param entity the {@link CustomerEntity} to set a {@link DatacenterBroker}
     */
    private void setBrokerToEntity(CustomerEntity entity, DatacenterBroker broker) {
        if(DatacenterBroker.NULL.equals(entity.getBroker())){
            entity.setBroker(broker);
        }
    }
    
    /**
     * Generates random failures for the Host's PEs.
     * The minimum number of PEs to fail is 1.
     *
     * @return the number of failed PEs
     */
    private int generateHostPesFailures() {
        final int numberOfFailedPes = randomNumberOfFailedPes();
        for (int i = 0; i < numberOfFailedPes; i++) {
            host.getPeList().get(i).setStatus(Pe.Status.FAILED);
        }
        
        return numberOfFailedPes;
    }

    /**
     * Gets the total number of PEs from all working VMs.
     * @return 
     */
    private long getPesSumOfWorkingVms() {
        return host.getVmList().stream()
                .filter(vm -> !vm.isFailed())
                .mapToLong(vm -> vm.getNumberOfPes())
                .sum();
    }

    /**
     * Randomly generates a number of PEs which will fail for the host.
     * The minimum number of PEs to fail is 1.
     *
     * @return the generated number of failed PEs for the host,
     * between [1 and Number of PEs].
     * 
     */
    private int randomNumberOfFailedPes() {
        /*the random generator return values from [0 to 1]
         and multiplying by the number of PEs we get a number between
         0 and number of PEs*/
        return (int) (random.sample()*host.getPeList().size()) + 1;
    }

    /**
     * Checks if the Host has any failed PEs.
     * @return true if any Host PEs has failed, false otherwise
     */
    private boolean isFailed() {
        return host.getPeList()
                .stream()
                .map(Pe::getStatus)
                .anyMatch(Status.FAILED::equals);
    }

    /**
     * Gets the host in which a failure may happen.
     *
     * @return
     */
    public Host getHost() {
        return host;
    }

    /**
     * Sets the host in which failure may happen.
     *
     * @param host the host to set
     */
    protected final void setHost(Host host) {
        Objects.requireNonNull(host);
        this.host = host;
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
     * which will be started into another host in order to
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
     * Gets the average number of failures expected to happen each minute.
     * @return
     */
    public double getMeanFailuresPerMinute() {
        return failureInjectionGenerator.getLambda();
    }
    
    @Override
    public void shutdownEntity() {/**/}

}
