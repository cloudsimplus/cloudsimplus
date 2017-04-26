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
package org.cloudsimplus.sla;

import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.*;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Pe.Status;

/**
 * This class shows how to generate a fault. In this case the fault is in the
 * host.
 *
 * Notes: This class does not work with the injection fault in time 0. Only
 * works after the creation of cloudlets and VMs, because when destroying the
 * VM, the cloudlet is not returned to be chosen by another VM that did not
 * fail, thus causing error in the simulator.
 *
 * @author raysaoliveira
 * 
 * see <link>https://blogs.sap.com/2014/07/21/equipment-availability-vs-reliability/
 *
 */
public class HostFaultInjection extends CloudSimEntity {
    private Host host;
    private ContinuousDistribution numberOfFailedPesRandom;
    private ContinuousDistribution delayForFailureOfHostRandom;

    /**
     * Creates a fault injection mechanism for a host that will generate
     * failures with a delay and number of failed PEs generated using a Uniform
     * Pseudo Random Number Generator (PRNG) for each one.
     *
     * @param simulation The CloudSim instance that represents the simulation
     * the Entity is related to
     * @see
     * #setDelayForFailureOfHostRandom(org.cloudbus.cloudsim.distributions.ContinuousDistribution)
     * @see
     * #setNumberOfFailedPesRandom(org.cloudbus.cloudsim.distributions.ContinuousDistribution)
     */
    public HostFaultInjection(CloudSim simulation) {
        super(simulation);
        this.numberOfFailedPesRandom = new UniformDistr();
        this.delayForFailureOfHostRandom = new UniformDistr();
    }

    @Override
    protected void startEntity() {
        double delay = delayForFailureOfHostRandom.sample() + 1;
        schedule(getId(), delay, CloudSimTags.HOST_FAILURE);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.HOST_FAILURE:
                generateFailure();
                break;

            default:
                Log.printLine(getName() + ": unknown event type");
                break;
        }
    }

    @Override
    public void shutdownEntity() {/**/}

    /**
     * Generates a failure on Host's PEs or not, according to the number of PEs
     * to be set to failed, returned by the {@link #numberOfFailedPesRandom}
     * PRNG.
     *
     * @return <tt>true</tt> if the failure was generated, <tt>false</tt>
     * otherwise
     */
    public final boolean generateFailure() {
        final int numberOfFailedPes = setFailedHostPes();
        final long hostWorkingPes = host.getNumberOfWorkingPes();
        final long vmsRequiredPes = getPesSumOfWorkingVms();
                
        if(hostWorkingPes == 0){
            setAllVmsToFailed();  
        } else if (hostWorkingPes >= vmsRequiredPes) {
            logNoVmFailure();  
        } else {
            deallocateFailedHostPesFromVms();
        } 
        
        return numberOfFailedPes > 0;
    }

    /**
     * Sets all VMs to failed when all Host PEs failed.
     */
    private void setAllVmsToFailed() {
        host.getVmList().stream().forEach(this::setVmToFailedWhenHostIsFailed);
        Log.printFormattedLine("\t%.2f: Host %d -> All the %d PEs failed, affecting all its %d VMs.\n",
                getSimulation().clock(), host.getId(), host.getNumberOfPes(), host.getVmList().size());
    }
    
    /**
     * Shows that the failure of Host PEs hasn't affected any VM,
     * because there is more working PEs than required by all VMs.
     */
    private void logNoVmFailure() {
        final int vmsRequiredPes = (int)getPesSumOfWorkingVms();
        Log.printFormattedLine(
                "\t%.2f: Host %d -> Number of failed PEs is less than the number of PEs required by all its %d VMs, thus it doesn't affect any VM.",
                getSimulation().clock(), host.getId(), host.getVmList().size());
        Log.printFormattedLine("\t      Host %d -> Total PEs: %d | Failed PEs: %d | Working PEs: %d | Current PEs required by VMs: %d.\n",
                host.getId(), host.getNumberOfPes(), host.getNumberOfFailedPes(), host.getNumberOfWorkingPes(),
                vmsRequiredPes);
    }
    
    private void deallocateFailedHostPesFromVms() {
        final int hostWorkingPes = (int)host.getNumberOfWorkingPes();
        final int vmsRequiredPes = (int)getPesSumOfWorkingVms();
        
        int i = 0;
        int failedPesToRemoveFromVms = vmsRequiredPes-hostWorkingPes;
        final int affectedVms = Math.min(host.getVmList().size(), failedPesToRemoveFromVms);
        Log.printFormattedLine(
                "\t%.2f: Host %d -> %d PEs failed, from a total of %d PEs. There are %d PEs working.",
                getSimulation().clock(), host.getId(), host.getNumberOfFailedPes(), 
                host.getNumberOfPes(), host.getNumberOfWorkingPes());
        Log.printFormattedLine(
                "\t      %d VMs affected from a total of %d\n", 
                affectedVms, host.getVmList().size());
        while(failedPesToRemoveFromVms-- > 0){
            i = i % affectedVms;
            Vm vm = host.getVmList().get(i);
            //@todo needs to check if the VM pesNumber is being reduced
            host.getVmScheduler().deallocatePesForVm(vm, 1);
            i++;
        }
        
        setVmsToFailed();
    }

    /**
     * Sets to failed all VMs which all their PEs were removed due to Host PEs failures.
     */
    private void setVmsToFailed() {
        host.getVmList().stream()
                .filter(vm -> vm.getNumberOfPes() == 0)
                .forEach(this::setVmToFailedWhenHostIsFailed);
    }

    /**
     * Checks if the the host is failed and sets all its Vm' to failed.
     *
     * @param vm vm to set to failed
     */
    public void setVmToFailedWhenHostIsFailed(Vm vm) {
        if (!this.isFailed()) {
            return;
        }
        
        vm.setFailed(true);
        /*
         As the broker is expected to request vm creation and destruction,
         it is set here as the sender of the vm destroy request.
         */
        getSimulation().sendNow(
                vm.getBroker().getId(), host.getDatacenter().getId(),
                CloudSimTags.VM_DESTROY, vm);
    }
    
    private int setFailedHostPes() {
        final int numberOfFailedPes = generateNumberOfFailedPes();
        for (int i = 0; i < numberOfFailedPes; i++) {
            host.getPeList().get(i).setStatus(Pe.Status.FAILED);
        }
        return numberOfFailedPes;
    }

    public long getFailedVmsCount() {
        return host.getVmList()
                .stream()
                .filter(Vm::isFailed)
                .count();
    }

    public long getPesSumOfWorkingVms() {
        return host.getVmList().stream()
                .filter(vm -> !vm.isFailed())
                .mapToLong(vm -> vm.getNumberOfPes())
                .sum();
    }

    /**
     * Generates a number of PEs that will fail for the host using the
     * {@link #numberOfFailedPesRandom},
     *
     * @return the generated number of failed PEs for the host
     */
    public int generateNumberOfFailedPes() {
        /*the random generator return values from [0 to 1]
         and multiplying by the number of PEs we get a number between
         0 and numbero of PEs*/
        return (int) (numberOfFailedPesRandom.sample() * host.getPeList().size() + 1);
    }

    /**
     * Checks if the Host has any failed PEs.
     * @return true if any Host PEs has failed, false otherwise
     */
    public boolean isFailed() {
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
    public void setHost(Host host) {
        this.host = host;
    }

    /**
     * Gets the pseudo random number generator (PRNG) that is used to define the
     * number of PEs to be set to failed for the related host. The PRNG returns
     * values between [0 and 1[
     *
     * @return
     */
    public ContinuousDistribution getNumberOfFailedPesRandom() {
        return numberOfFailedPesRandom;
    }

    /**
     * Sets the pseudo random number generator (PRNG) that is used to define the
     * number of PEs to be set to failed for the related host. The PRNG must
     * return values between [0 and 1[
     *
     * @param numberOfFailedPesRandom the numberOfFailedPesRandom to set
     */
    public void setNumberOfFailedPesRandom(ContinuousDistribution numberOfFailedPesRandom) {
        this.numberOfFailedPesRandom = numberOfFailedPesRandom;
    }

    /**
     * Sets the delayForFailureOfHostRandom number generator returning
     * values between [min - max]. This value will be used to set
     * the delay time the host will fail.
     *
     * @param delayForFailureOfHostRandom the delayForFailureOfHostRandom to set
     */
    public void setDelayForFailureOfHostRandom(ContinuousDistribution delayForFailureOfHostRandom) {
        this.delayForFailureOfHostRandom = delayForFailureOfHostRandom;
    }

    /**
     * Gets the pseudo delayForFailureOfHostRandom number generator returning
     * values between [min - max]. This value will be used to set
     * the delay time the host will fail.
     *
     * @return the delayForFailureOfHostRandom
     */
    public ContinuousDistribution getDelayForFailureOfHostRandom() {
        return delayForFailureOfHostRandom;
    }

}
