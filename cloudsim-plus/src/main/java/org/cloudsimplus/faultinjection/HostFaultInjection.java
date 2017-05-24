/*
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
package org.cloudsimplus.faultinjection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import static java.util.stream.Collectors.toList;
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
 * A Fault Injection object
 * usually has to be created after the VMs are created,
 * to make it easier to define a function to be used
 * to clone failed VMs.
 *
 * The events happens in the following order:
 * <ol>
 *  <li>a time to inject a Host failure is generated using a given Random Number Generator;</li>
 *  <li>a Host is randomly selected to fail at that time using an internal Uniform Random Number Generator with the same seed of the given generator;</li>
 *  <li>the number of Host PEs to fail is randomly generated using the internal generator;</li>
 *  <li>failed physical PEs are removed from affected VMs, VMs with no remaining PEs and destroying and clones of them are submitted to the {@link DatacenterBroker} of the failed VMs;</li>
 *  <li>another failure is scheduled for a future time using the given generator;</li>
 *  <li>the process repeats until the end of the simulation.</li>
 * </ol>
 *
 * <p>
 * When Host's PEs fail, if there are more available PEs
 * than the required by its running VMs, no VM will be affected.
 * </p>
 *
 * <p>
 * Considering that X is the number of failed PEs and it is
 * lower than the total available PEs.
 * In this case, the X PEs will be removed cyclically, 1 by 1,
 * from running VMs.
 * This way, some VMs may continue running
 * with less PEs than they requested initially.
 * On the other hand, if after the failure the number of Host working PEs
 * is lower than the required to run all VMs, some VMs will be
 * destroyed.
 * </p>
 *
 * <p>
 * If all PEs are removed from a VM, it is automatically destroyed
 * and a snapshot (clone) from it is taken and submitted
 * to the broker, so that the clone can start executing
 * into another host. In this case, all the cloudlets
 * which were running inside the VM yet, will be
 * cloned to and restart executing from the beginning.
 * </p>
 *
 * <p>
 * If a cloudlet running inside a VM which was affected by a PE failure
 * requires Y PEs but the VMs doesn't have such PEs anymore,
 * the Cloudlet will continue executing, but it will spend
 * more time to finish.
 * For instance, if a Cloudlet requires 2 PEs but after the failure
 * the VM was left with just 1 PE, the Cloudlet will spend the double
 * of the time to finish.
 * </p>
 *
 * <p>
 * <b>NOTES:</b>
 * <ul>
 *     <li>
 *      Host PEs failures may happen after all its VMs have finished executing.
 *      This way, the presented simulation results may show that the
 *      number of PEs into a Host is lower than the required by its VMs.
 *      In this case, the VMs shown in the results finished executing before
 *      some failures have happened. Analysing the logs is easy to
 *      confirm that.
 *      </li>
 *      <li>Failures inter-arrivals are defined in minutes, since seconds is a too
 *      small time unit to define such value. Furthermore, it doesn't make sense to define
 *      the number of failures per second. This way, the generator of failure arrival times
 *      given to the constructor considers the time in minutes, despite the simulation
 *      time unit is seconds. Since commonly Cloudlets just take some seconds to finish,
 *      mainly in simulation examples, failures may happen just after the cloudlets
 *      have finished. This way, one usually should make sure that Cloudlets' length
 *      are large enough to allow failures to happen before they end.
 *      </li>
 * </ul>
 * </p>
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.0
 * @see <a href="https://blogs.sap.com/2014/07/21/equipment-availability-vs-reliability/">SAP Blog: Availability vs Reliability</a>
 *
 * @todo The class has multiple responsibilities.
 * The fault injection mechanism must be separated from
 * the fault recovery. The cloner methods are fault recovery.
 */
public class HostFaultInjection extends CloudSimEntity {
    /**
     * @see #getLastFailedHost()
     */
    private Host lastFailedHost;

    /**
     * @see #getDatacenter()
     */
    private Datacenter datacenter;

    /**
     * A Pseudo Random Number Generator used to select a Host
     * and the number of PEs to set as fail.
     */
    private ContinuousDistribution random;

    /**
     * A map that stores a {@link Function} to be used to clone
     * a VM. If a VM isn't in this map, it will not
     * be cloned in case of failure.
     *
     * @see #setVmCloner(org.cloudbus.cloudsim.vms.Vm, java.util.function.UnaryOperator)
     */
    private Map<Vm, UnaryOperator<Vm>> vmClonerMap;

    /**
     * @see #setCloudletsCloner(java.util.function.Function)
     */
    private Function<Vm, List<Cloudlet>> cloudletsCloner;

    /**
     * A Pseudo Random Number Generator which generates the times (in minutes)
     * that Hosts failures will occur.
     */
    private ContinuousDistribution faultArrivalTimesGenerator;

    /**
     * The attribute counts how many host failures the simulation had
     */
    private int numberOfHostFaults;

    /**
     * A map to store the time (in seconds) each failed VM took to be recovered.
     * It also means the failure time for each Vm.
     */
    private final Map<Vm, Double> vmRecoveryTimeMap;

    /**
     * A map to store the times (in seconds) for each Host failure.
     */
    private final Map<Host, List<Double>> hostFaultsTimeMap;

    /**
     * A function that in fact doesn't clone any VM.
     * The {@link #cloudletsCloner} is initialized with this function to avoid {@code NullPointerException}
     * if the attribute is not set.
     */
    private static final Function<Vm, List<Cloudlet>> CLOUDLETS_CLONER_NULL = vm -> Collections.EMPTY_LIST;

    /**
     * A function that in fact doesn't clone VMs.
     * It's used as the default value when a VM doesn't have a cloner function.
     */
    private static final UnaryOperator<Vm> VM_CLONER_NULL = vm -> Vm.NULL;

    /**
     * Maximum number of seconds for a VM to recovery from a failure,
     * which is randomly selected based on this value.
     * The recovery time is the delay that will be set
     * to start a clone from a failed VM.
     */
    private static final int MAX_VM_RECOVERY_TIME_SECS = 1800;

    /**
     * Creates a fault injection mechanism for the Hosts of a given {@link Datacenter}.
     * The failures are randomly injected according to the given
     * mean of failures to be generated per <b>minute</b>,
     * which is also called <b>event rate</b> or <b>rate parameter</b>.
     *
     * @param datacenter the Datacenter to which failures will be randomly injected for its Hosts
     *
     * @param faultArrivalTimesGenerator a Pseudo Random Number Generator which generates the
     * times that Hosts failures will occur.
     * <b>The values returned by the generator will be considered to be minutes</b>.
     * Frequently it is used a
     * {@link PoissonDistr} to generate failure arrivals, but any {@link ContinuousDistribution}
     * can be used.
     */
    public HostFaultInjection(Datacenter datacenter, ContinuousDistribution faultArrivalTimesGenerator) {
        super(datacenter.getSimulation());
        this.setDatacenter(datacenter);
        this.lastFailedHost = Host.NULL;
        this.faultArrivalTimesGenerator = faultArrivalTimesGenerator;
        this.random = new UniformDistr(faultArrivalTimesGenerator.getSeed()+1);
        this.vmRecoveryTimeMap = new HashMap<>();
        this.hostFaultsTimeMap = new HashMap<>();

        this.vmClonerMap = new HashMap<>();

        /*Sets a default cloudletCloner which in fact doesn't
        clone anything, just returns an empty List.
        This is used just to ensure that if a clone function
        is not set, it wont be thrown a NullPointerException
        when trying to use it.*/
        this.cloudletsCloner = CLOUDLETS_CLONER_NULL;
    }

    @Override
    protected void startEntity() {
        scheduleFaultInjection();
    }

    /**
     * Schedules a message to be processed internally
     * to inject a Host PEs failure.
     */
    private void scheduleFaultInjection() {
        final long numOfOtherEvents =
                getSimulation()
                        .getNumberOfFutureEvents(
                            evt -> evt.getTag() != CloudSimTags.HOST_FAILURE);
        /*
        Just re-schedule more failures if there are other events to be processed.
        Otherwise, the simulation has finished and no more failures should be scheduled.
        */
        if(numOfOtherEvents > 0) {
            schedule(getId(), getTimeDelayForNextFault(), CloudSimTags.HOST_FAILURE);
        }
    }

    /**
     * Gets the time delay in seconds, from the current simulation time,
     * that the next failure will be injected.
     * Since the values returned by the {@link #faultArrivalTimesGenerator}
     * are considered to be in <b>minutes</b>, such values are converted to seconds.
     *
     * @return the next failure injection delay in seconds
     */
    private double getTimeDelayForNextFault() {
        return faultArrivalTimesGenerator.sample() * 60;
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.HOST_FAILURE:
                generateHostFault();
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
    private void generateHostFault() {
        try {
            this.lastFailedHost = getRandomHost();
            if (Host.NULL.equals(lastFailedHost)) {
                return;
            }

            numberOfHostFaults++;
            registerHostFaultTime();

            final int numberOfPesToFail = generateHostPesFaults();
            final long hostWorkingPes = lastFailedHost.getNumberOfWorkingPes();
            final long vmsRequiredPes = getPesSumOfWorkingVms();

            Log.printFormattedLine("%.2f: %s: Generated %d PEs failures for %s at minute %.2f",
                    getSimulation().clock(), getClass().getSimpleName(),
                    numberOfPesToFail, lastFailedHost, getSimulation().clock() / 60);
            if (vmsRequiredPes == 0) {
                Log.printFormattedLine("\tNumber of VMs: %d", lastFailedHost.getVmList().size());
            }
            Log.printFormattedLine("\tWorking PEs: %d | VMs required PEs: %d", hostWorkingPes, vmsRequiredPes);

            if (hostWorkingPes == 0) {
                setAllVmsToFailed();
            } else if (hostWorkingPes >= vmsRequiredPes) {
                logNoVmFault();
            } else {
                deallocateFailedHostPesFromVms();
            }
        } finally {
            //schedules the next failure injection
            scheduleFaultInjection();
        }
    }

    /**
     * Register the time for a Host failure.
     */
    private void registerHostFaultTime() {
        hostFaultsTimeMap.computeIfAbsent(lastFailedHost, h -> new ArrayList<>()).add(getSimulation().clock());
    }

    /**
     * Randomly gets a Host that will have some PEs set to failed.
     *
     * @return the randomly selected Host or {@link Host#NULL} if the Datacenter
     * doesn't have Hosts.
     */
    private Host getRandomHost() {
        if (datacenter.getHostList().isEmpty()) {
            return Host.NULL;
        }

        final int i = (int) (random.sample() * datacenter.getHostList().size());
        System.out.printf("\t\t#%.2f: Random Host: %s Position: %d\n", getSimulation().clock(), datacenter.getHost(i), i);
        return datacenter.getHost(i);
    }

    /**
     * Sets all VMs inside the {@link #getLastFailedHost() last failed Host} to
     * failed, when all Host PEs have failed.
     */
    private void setAllVmsToFailed() {
        Log.printFormattedLine(
                "\tAll the %d PEs failed, affecting all its %d VMs.\n",
                lastFailedHost.getNumberOfPes(), lastFailedHost.getVmList().size());
        lastFailedHost.getVmList().stream().forEach(this::setVmToFailedAndCreateClone);
    }

    /**
     * Shows that the failure of Host PEs hasn't affected any VM, because there
     * is more working PEs than required by all VMs.
     */
    private void logNoVmFault() {
        final int vmsRequiredPes = (int) getPesSumOfWorkingVms();
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
        Log.printFormattedLine("\t%d PEs failed, from a total of %d PEs. There are %d PEs working.",
                lastFailedHost.getNumberOfFailedPes(),
                lastFailedHost.getNumberOfPes(), lastFailedHost.getNumberOfWorkingPes());
        cyclicallyRemoveFailedHostPesFromVms();

        Log.printLine();
        setVmsWithoutPesToFailed();
    }

    private int numberOfFailedPesToRemoveFromVms() {
        final int hostWorkingPes = (int)lastFailedHost.getNumberOfWorkingPes();
        final int vmsRequiredPes = (int)getPesSumOfWorkingVms();
        int failedPesToRemoveFromVms = vmsRequiredPes-hostWorkingPes;

        return failedPesToRemoveFromVms;
    }

    /**
     * Removes one physical failed PE from one affected VM at a time.
     * Affected VMs are dealt as a circular list, visiting
     * one VM at a time to remove 1 PE from it,
     * until all the failed PEs are removed.
     *
     */
    private void cyclicallyRemoveFailedHostPesFromVms() {
        int failedPesToRemoveFromVms = numberOfFailedPesToRemoveFromVms();
        List<Vm> vmsWithPes = getVmsWithPEsFromFailedHost();
        final int affectedVms = Math.min(vmsWithPes.size(), failedPesToRemoveFromVms);

        Log.printFormattedLine("\t%d VMs affected from a total of %d. %d PEs are going to be removed from VMs",
                affectedVms, lastFailedHost.getVmList().size(), failedPesToRemoveFromVms);
        int i = 0;
        while (!vmsWithPes.isEmpty() && failedPesToRemoveFromVms-- > 0) {
            i = i % vmsWithPes.size();
            Vm vm = vmsWithPes.get(i);

            lastFailedHost.getVmScheduler().deallocatePesFromVm(vm, 1);
            vm.getCloudletScheduler().deallocatePesFromVm(vm, 1);
            //remove 1 failed PE from the VM
            vm.getProcessor().removeCapacity(1);
            Log.printFormattedLine(
                    "\tRemoving 1 PE from VM %d due to Host PE failure. New VM PEs Number: %d\n",
                    vm.getId(), vm.getNumberOfPes());
            i++;
            vmsWithPes = getVmsWithPEsFromFailedHost();
        }
    }

    /**
     * Gets a List of VMs that have any PE from the {@link #lastFailedHost}.
     * @return
     */
    private List<Vm> getVmsWithPEsFromFailedHost() {
        return lastFailedHost
                .getVmList()
                .stream()
                .filter(vm -> vm.getNumberOfPes() > 0)
                .collect(toList());
    }

    /**
     * Sets to failed all VMs that have all their PEs removed due to
     * Host PEs failures.
     */
    private void setVmsWithoutPesToFailed() {
        lastFailedHost.getVmList().stream()
                .filter(vm -> vm.getNumberOfPes() == 0)
                .forEach(this::setVmToFailedAndCreateClone);
    }

    /**
     * Sets a VM inside the {@link #getLastFailedHost() last failed Host} to
     * failed and use the VM and Cloudlets cloner functions to create a clone of
     * the VMs with all its Cloudlets, to simulate the initialization of a new
     * VM instance from a snapshot of the failed VM.
     *
     * @param vm VM to set to failed
     */
    private void setVmToFailedAndCreateClone(Vm vm) {
        if (Host.NULL.equals(lastFailedHost)) {
            return;
        }

        final DatacenterBroker broker = vm.getBroker();
        if(isVmClonerSet(vm)){
            final double recoveryTime = getRandomRecoveryTimeForVm();
            Log.printFormattedLine("\t# Time to recovery the fault: %.2f minutes", recoveryTime/60);
            vmRecoveryTimeMap.put(vm, recoveryTime);

            final Vm vmClone = cloneVm(vm);
            final List<Cloudlet> cloudletsClone = cloudletsCloner.apply(vm);

            vmClone.setSubmissionDelay(recoveryTime);
            broker.submitVm(vmClone);
            broker.submitCloudletList(cloudletsClone, vmClone, recoveryTime);
        }
        vm.setFailed(true);

        /*
         As the broker is expected to request vm creation and destruction,
         it is set here as the sender of the vm destroy request.
         */
        getSimulation().sendNow(
                broker.getId(), datacenter.getId(),
                CloudSimTags.VM_DESTROY, vm);
        Log.printFormattedLine("\n\t\t\t #VM %d destroyd. But not clone\n", vm.getId());

    }

    private boolean isVmClonerSet(Vm vm) {
        final UnaryOperator<Vm> cloner = vmClonerMap.getOrDefault(vm, VM_CLONER_NULL);
        return !VM_CLONER_NULL.equals(cloner);
    }

    /**
     * Clones a given VM using it's cloner {@link Function} if it was in fact set.
     *
     * @param vm the VM to clone
     * @return the cloned VM or {@link Vm#NULL}
     * if no cloner function was set
     */
    private Vm cloneVm(Vm vm) {
       return vmClonerMap.getOrDefault(vm, VM_CLONER_NULL).apply(vm);
    }

    /**
     * Gets the total number of faults happened for VMs,
     * which means the total number of VMs that
     * were destroyed due to failure in Host PEs.
     * @return
     */
    public int getNumberOfDestroyedVms() {
        return vmRecoveryTimeMap.size();
    }

    /**
     * Gets the total number of faults happened for existing hosts.
     * This isn't the total number of failed hosts because one
     * host may fail multiple times.
     * @return
     */
    public int getNumberOfHostFaults() {
        return numberOfHostFaults;
    }

    /**
     * Gets the Datacenter's availability as a percentage value between 0 to 1,
     * based on VMs' downtime (the times VMs took to be repaired).
     * @return
     */
    public double availability() {
        //no failures means 100% availability
        if(meanTimeBetweenVmFaultsInMinutes() == 0){
            return 1;
        }

        return (meanTimeBetweenVmFaultsInMinutes()/(meanTimeBetweenVmFaultsInMinutes() + meanTimeToRepairVmFaultsInMinutes()));
    }

    /**
     * Computes the current mean time (in minutes) between Host failures (MTBF).
     * It uses a straightforward way to compute the MTBF.
     * Since it's stored the VM recovery times, it's possible
     * to use such values to make easier the MTBF computation,
     * different from the Hosts MTBF.
     *
     * @return the current mean time (in minutes) between Host failures (MTBF)
     * or zero if no VM was destroyed due to Host failure
     * @see #meanTimeBetweenHostFaultsInMinutes()
     */
    public double meanTimeBetweenVmFaultsInMinutes() {
        if(getNumberOfDestroyedVms() == 0){
            return 0;
        }

        return (getSimulation().clockInMinutes() - totalVmsRecoveryTimeInMinutes()) / getNumberOfDestroyedVms();
    }

    /**
     * Gets the total time (in minutes) every failed VM took to recovery
     * from failure.
     * @return
     */
    private double totalVmsRecoveryTimeInMinutes() {
        return vmRecoveryTimeMap.values().stream().reduce(0.0, Double::sum) / 60.0;
    }

    /**
     * Computes the current mean time (in minutes) between Host failures (MTBF).
     * Since Hosts don't actually recover from failures,
     * there aren't recovery time to make easier the computation
     * of MTBF for Host as it is directly computed for VMs.
     *
     * @return the current mean time (in minutes) between Host failures (MTBF)
     * or zero if no failures have happened yet
     * @see #meanTimeBetweenVmFaultsInMinutes()
     */
    public double meanTimeBetweenHostFaultsInMinutes() {
        final List<Double> values = hostFaultsTimeMap
                .values()
                .stream()
                .flatMap(list -> list.stream())
                .sorted()
                .collect(toList());
        if(values.isEmpty()){
            return 0;
        }

        //computes the differences between failure times t2 - t1
        double sum=0, previous=values.get(0);
        for(Double v: values) {
            sum += (v - previous);
            previous = v;
        }

        return (sum/values.size())/60.0;
    }

    /**
     * Computes the current mean time (in minutes) to repair VM failures (MTTR).
     * @return the current mean time (in minutes) to repair VM failures (MTTR)
     * or zero if no VM was destroyed due to Host failure
     */
    public double meanTimeToRepairVmFaultsInMinutes() {
        if(getNumberOfDestroyedVms() == 0){
            return 0;
        }
        return totalVmsRecoveryTimeInMinutes() / getNumberOfDestroyedVms();
    }

    /**
     * Generates random failures for the PEs from the
     * {@link #getLastFailedHost() last failed Host}.
     * The minimum number of PEs to fail is 1.
     *
     * @return the number of failed PEs for the Host
     */
    private int generateHostPesFaults() {
        return (int) lastFailedHost.getWorkingPeList()
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
     */
    private int randomNumberOfFailedPes() {
        /*the random generator return values from [0 to 1]
         and multiplying by the number of PEs we get a number between
         0 and number of PEs*/
        return (int) (random.sample() * lastFailedHost.getWorkingPeList().size()) + 1;
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
     * <p>This is optional. If a cloner function is not set,
     * VMs will not be recovered from failures.</p>
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
     * @param vm the source VM to be cloned using the given cloning function
     * @param clonerFunction the VM cloner {@link Function} to set
     * @see #setCloudletsCloner(java.util.function.Function)
     */
    public void setVmCloner(Vm vm, UnaryOperator<Vm> clonerFunction) {
        Objects.requireNonNull(clonerFunction);
        Objects.requireNonNull(vm);
        this.vmClonerMap.put(vm, clonerFunction);
    }

    /**
     * Sets a {@link Function} that will create a clone of all Cloudlets
     * which were running inside a {@link Vm} after a fail.
     * The same function is used to clone the cloudlets
     * of any cloned VM.
     *
     * <p>If a Vm cloner function is not set, setting a cloudlet's cloner function is optional.
     * Since VMs will not be recovered from failures
     * in this situation, cloudlets inside failed VM will not be recovered too.</p>
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
     * @see #setVmCloner(Vm, UnaryOperator)
     */
    public void setCloudletsCloner(Function<Vm, List<Cloudlet>> cloudletsCloner) {
        Objects.requireNonNull(cloudletsCloner);
        this.cloudletsCloner = cloudletsCloner;
    }

    /**
     * Gets the last Host for which a failure was injected.
     *
     * @return the last failed Host or {@link Host#NULL} if not Host has failed
     * yet.
     */
    public Host getLastFailedHost() {
        return lastFailedHost;
    }

    @Override
    public void shutdownEntity() {/**/}

    /**
     * Gets a Pseudo Random Number used to give a
     * recovery time (in seconds) for each VM that was failed.
     * @return
     */
    public double getRandomRecoveryTimeForVm() {
        return random.sample()*MAX_VM_RECOVERY_TIME_SECS + 1;
    }

}
