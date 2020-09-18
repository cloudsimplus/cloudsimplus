/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.AbstractMachine;
import org.cloudbus.cloudsim.core.CloudSimEntity;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.PoissonDistr;
import org.cloudbus.cloudsim.distributions.StatisticalDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Generates random failures for the {@link Pe}'s of {@link Host}s
 * inside a given {@link Datacenter}.
 * A Fault Injection object
 * usually has to be created after the VMs are created,
 * to make it easier to define a function to be used
 * to clone failed VMs.
 *
 * <p>
 * The events happens in the following order:
 * <ol>
 *  <li>a time to inject a Host failure is generated using a given Random Number Generator;</li>
 *  <li>a Host is randomly selected to fail at that time using an internal Uniform Random Number Generator with the same seed of the given generator;</li>
 *  <li>the number of Host PEs to fail is randomly generated using the internal generator;</li>
 *  <li>failed physical PEs are removed from affected VMs, VMs with no remaining PEs and destroying and clones of them are submitted to the {@link DatacenterBroker} of the failed VMs;</li>
 *  <li>another failure is scheduled for a future time using the given generator;</li>
 *  <li>the process repeats until the end of the simulation.</li>
 * </ol>
 * </p>
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
 * <p>For more details, check
 * <a href="http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf">Raysa Oliveira's Master Thesis (only in Portuguese)</a>.</p>
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.0
 * @see <a href="https://blogs.sap.com/2014/07/21/equipment-availability-vs-reliability/">SAP Blog: Availability vs Reliability</a>
 *
 * @TODO The class has multiple responsibilities.
 *       The fault injection mechanism must be separated from
 *       the fault recovery. The cloner methods are fault recovery.
 */
public class HostFaultInjection extends CloudSimEntity {
    /**
     * Maximum number of seconds for a VM to recovery from a failure,
     * which is randomly selected based on this value.
     * The recovery time is the delay that will be set
     * to start a clone from a failed VM.
     */
    private static final int MAX_VM_RECOVERY_TIME_SECS = 450;

    private static final Logger LOGGER = LoggerFactory.getLogger(HostFaultInjection.class.getSimpleName());

    /**
     * @see #getLastFailedHost()
     */
    private Host lastFailedHost;

    /**
     * Number of PEs failed into the {@link #lastFailedHost}.
     */
    private int lastNumberOfFailedPes;

    /**
     * @see #getDatacenter()
     */
    private Datacenter datacenter;

    /**
     * A Pseudo Random Number Generator used to select a Host
     * and the number of PEs to set as fail.
     */
    private final ContinuousDistribution random;

    /**
     * A map that stores {@link VmCloner} objects to be used to clone
     * the VMs belonging to a broker.
     *
     * @see #addVmCloner(DatacenterBroker, VmCloner)
     */
    private final Map<DatacenterBroker, VmCloner> vmClonerMap;

    /**
     * A Pseudo Random Number Generator which generates the times (in hours)
     * that Hosts failures will occur.
     */
    private final StatisticalDistribution faultArrivalHoursGenerator;

    /**
     * The attribute counts how many host failures the simulation had
     */
    private int numberOfHostFaults;

    /**
     * A map to store the time (in seconds) VM failures took to be recovered,
     * which is when a clone from the last failed VM for a given broker is created.
     * Since a broker just creates a VM clone when all its VMs have failed,
     * only at that time the failure is in fact recovered.
     *
     * It means the time period failure of all VMs persisted
     * before a clone was created.
     */
    private final Map<Vm, Double> vmRecoveryTimeSecsMap;

    /**
     * A map to store the times (in seconds) for each Host failure.
     */
    private final Map<Host, List<Double>> hostFaultsTimeSecsMap;

    /**
     * A map to store the number of failures that affected all VMs from each broker.
     */
    private final Map<DatacenterBroker, Integer> faultsOfAllVmsByBroker;


    private double maxTimeToFailInHours;

    /**
     * Creates a fault injection mechanism for the Hosts of a given {@link Datacenter}.
     * The Hosts failures are randomly injected according to a {@link UniformDistr}
     * pseudo random number generator, which indicates the mean of failures to be generated per <b>hour</b>,
     * (which is also called <b>event rate</b> or <b>rate parameter</b>).
     *
     * @param datacenter the Datacenter to which failures will be randomly injected for its Hosts
     * @see #HostFaultInjection(Datacenter, StatisticalDistribution)
     */
    public HostFaultInjection(final Datacenter datacenter) {
      this(datacenter, new UniformDistr());
    }

    /**
     * Creates a fault injection mechanism for the Hosts of a given {@link Datacenter}.
     * The Hosts failures are randomly injected according to the given
     * pseudo random number generator, that indicates the mean of failures to be generated per <b>minute</b>,
     * (which is also called <b>event rate</b> or <b>rate parameter</b>).
     *  @param datacenter the Datacenter to which failures will be randomly injected for its Hosts
     *
     * @param faultArrivalHoursGenerator a Pseudo Random Number Generator which generates the
     * times Hosts failures will occur (in hours).
     * <b>The values returned by the generator will be considered to be hours</b>.
     * Frequently it is used a
     * {@link PoissonDistr} to generate failure arrivals, but any {@link ContinuousDistribution}
     */
    public HostFaultInjection(final Datacenter datacenter, final StatisticalDistribution faultArrivalHoursGenerator) {
        super(datacenter.getSimulation());
        this.setDatacenter(datacenter);
        this.lastFailedHost = Host.NULL;
        this.faultArrivalHoursGenerator = faultArrivalHoursGenerator;
        this.random = new UniformDistr(faultArrivalHoursGenerator.getSeed()+1);
        this.vmRecoveryTimeSecsMap = new HashMap<>();
        this.hostFaultsTimeSecsMap = new HashMap<>();
        this.faultsOfAllVmsByBroker = new HashMap<>();
        this.vmClonerMap = new HashMap<>();
        this.maxTimeToFailInHours = Double.MAX_VALUE;
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

        if (numOfOtherEvents > 0 || getSimulation().clock() < getMaxTimeToFailInSecs()) {
            schedule(this, getTimeDelayForNextFault(), CloudSimTags.HOST_FAILURE);
        }
    }

    /**
     * Gets the time delay in seconds, from the current simulation time,
     * that the next failure will be injected.
     * Since the values returned by the {@link #faultArrivalHoursGenerator}
     * are considered to be in <b>hours</b>, such values are converted to seconds.
     *
     * @return the next failure injection delay in seconds
     */
    private double getTimeDelayForNextFault() {
        return faultArrivalHoursGenerator.sample() * 3600;
    }

    @Override
    public void processEvent(final SimEvent evt) {
        if (evt.getTag() == CloudSimTags.HOST_FAILURE) {
            generateHostFaultAndScheduleNext();
        }
    }

    /**
     * Generates a fault for all PEs of a Host.
     * @param host the Host to generate the fault to.
     */
    public void generateHostFault(final Host host){
        generateHostFault(host, host.getWorkingPesNumber());
    }

    /**
     * Generates a fault for a given number of random PEs of a Host.
     * @param host the Host to generate the fault to.
     * @param numberOfPesToFail number of PEs that must fail
     */
    public void generateHostFault(final Host host, final int numberOfPesToFail){
        if(Host.NULL == host){
            return;
        }

        this.lastFailedHost = host;

        numberOfHostFaults++;
        registerHostFaultTime();

        final long previousNumOfWorkingPes = lastFailedHost.getWorkingPesNumber();
        this.lastNumberOfFailedPes = generateHostPesFaults(numberOfPesToFail);
        final long hostWorkingPes = lastFailedHost.getWorkingPesNumber();
        final long vmsRequiredPes = getPesSumOfWorkingVms();

        final String msg = lastFailedHost.getVmList().isEmpty() ? "" : " | VMs required PEs: " + vmsRequiredPes;
        if(hostWorkingPes > 0) {
            LOGGER.error(
                "{}: {}: Generated {} PEs failures from {} previously working PEs for {} at minute {}.{}" +
                    "\t  Current Working PEs: {} | Number of VMs: {}{}",
                getSimulation().clockStr(), getClass().getSimpleName(), lastNumberOfFailedPes,
                previousNumOfWorkingPes, lastFailedHost, getSimulation().clock() / 60, System.lineSeparator(),
                hostWorkingPes, lastFailedHost.getVmList().size(), msg);
        }

        if (hostWorkingPes == 0) {
            setAllVmsToFailed();
        } else if (hostWorkingPes >= vmsRequiredPes) {
            logNoVmFault();
        } else {
            deallocateFailedHostPesFromVms();
        }
    }

    /**
     * Generates a failure for a specific number of PEs from a
     * randomly selected Host and schedules the next time to try generating a fault.
     */
    private void generateHostFaultAndScheduleNext() {
        try {
            final Host host = getRandomHost();
            generateHostFault(host, randomNumberOfFailedPes(host));
        } finally {
            //schedules the next failure injection try
            scheduleFaultInjection();
        }
    }

    /**
     * Register the time for a Host failure.
     */
    private void registerHostFaultTime() {
        hostFaultsTimeSecsMap.computeIfAbsent(lastFailedHost, host -> new ArrayList<>()).add(getSimulation().clock());
    }

    /**
     * Randomly gets a Host that will have some PEs set to failed.
     *
     * @return the randomly selected Host; or {@link Host#NULL} if the Datacenter
     * doesn't have Hosts or the selected one doesn't have more PEs.
     */
    private Host getRandomHost() {
        if (datacenter.getHostList().isEmpty()) {
            return Host.NULL;
        }

        final int idx = (int) (random.sample() * datacenter.getHostList().size());
        return datacenter.getHost(idx);
    }

    /**
     * Sets all VMs inside the {@link #getLastFailedHost() last failed Host} to
     * failed, when all Host PEs have failed.
     */
    private void setAllVmsToFailed() {
        final int vms = lastFailedHost.getVmList().size();
        final String msg = vms > 0 ? String.format("affecting all its %d VMs", vms) : "but there was no running VM";
        LOGGER.error(
                "{}: All the {} PEs of {} failed, {}.",
                getSimulation().clockStr(), lastFailedHost.getNumberOfPes(), lastFailedHost, msg);
        setVmListToFailed(lastFailedHost.getVmList());
    }

    /**
     * Shows that the failure of Host PEs hasn't affected any VM, because there
     * is more working PEs than required by all VMs.
     */
    private void logNoVmFault() {
        if(lastFailedHost.getVmList().isEmpty()){
            LOGGER.info("\tThere aren't VMs running on the failed Host.");
            return;
        }

        final int vmsRequiredPes = (int) getPesSumOfWorkingVms();
        LOGGER.info(
                "\tNumber of failed PEs is less than PEs required by all its {} VMs, thus it doesn't affect any VM.{}" +
                "Total PEs: {} | Total Failed PEs: {} | Working PEs: {} | Current PEs required by VMs: {}.",
                lastFailedHost.getVmList().size(), System.lineSeparator(),
                lastFailedHost.getNumberOfPes(), lastFailedHost.getFailedPesNumber(),
                lastFailedHost.getWorkingPesNumber(), vmsRequiredPes);
    }

    /**
     * De-allocates the physical PEs failed for the
     * {@link #getLastFailedHost() last failed Host} from affected VMs.
     */
    private void deallocateFailedHostPesFromVms() {
        LOGGER.error("\t{} PEs just failed. There is a total of {} working PEs.",
                lastNumberOfFailedPes,
                lastFailedHost.getWorkingPesNumber());
        cyclicallyRemoveFailedHostPesFromVms();

        final List<Vm> vmsWithoutPes =
            lastFailedHost.getVmList()
                .stream()
                .filter(vm -> vm.getNumberOfPes() == 0)
                .collect(toList());
        setVmListToFailed(vmsWithoutPes);
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

        LOGGER.warn("\t{} VMs affected from a total of {}. {} PEs are going to be removed from them.",
                affectedVms, lastFailedHost.getVmList().size(), failedPesToRemoveFromVms);
        int idx = 0;
        while (!vmsWithPes.isEmpty() && failedPesToRemoveFromVms > 0) {
            failedPesToRemoveFromVms--;
            idx = idx % vmsWithPes.size();
            final Vm vm = vmsWithPes.get(idx);
            lastFailedHost.getVmScheduler().deallocatePesFromVm(vm, 1);
            vm.getCloudletScheduler().deallocatePesFromVm(1);
            //remove 1 failed PE from the VM
            vm.getProcessor().deallocateAndRemoveResource(1);

            LOGGER.warn(
                    "\tRemoving 1 PE from VM {} due to Host PE failure. New VM PEs Number: {}",
                    vm.getId(), vm.getNumberOfPes());
            idx++;
            vmsWithPes = getVmsWithPEsFromFailedHost();
        }
    }

    private int numberOfFailedPesToRemoveFromVms() {
        final int hostWorkingPes = lastFailedHost.getWorkingPesNumber();
        final int vmsRequiredPes = (int)getPesSumOfWorkingVms();
        return vmsRequiredPes - hostWorkingPes;
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
     * Sets to failed all VMs from a given list due to
     * Host PEs failures.
     */
    private void setVmListToFailed(final List<Vm> vms) {
        final Map<DatacenterBroker, Vm> lastVmFailedByBroker = getLastFailedVmByBroker(vms);

        vms.forEach(this::setVmToFailed);
        lastVmFailedByBroker.forEach(this::createVmCloneIfAllVmsDestroyed);
    }

    private Map<DatacenterBroker, Vm> getLastFailedVmByBroker(final List<Vm> vmsWithoutPes) {
        final Comparator<Vm> comparator = Comparator.comparingLong(Vm::getId);
        return vmsWithoutPes
                    .stream()
                    .collect(
                        toMap(Vm::getBroker, Function.identity(), BinaryOperator.maxBy(comparator))
                    );
    }

    /**
     * Creates a VM for the last failed VM if all VMs belonging to the broker have failed
     * and the maximum number of clones to create was not reached.
     *
     * <p>
     * If all VMs have failed and a {@link VmCloner} is not set or the max number of
     * clones already was created, from the time of the failure
     * until the end of the simulation, this interval the customer
     * service is completely unavailable.
     *
     * Since the map below stores recovery times and not unavailability times,
     * it's being store the failure time as a negative value.
     * This way, when computing the availability for the customer,
     * these negative values are changed to: lastSimulationTime - |negativeRecoveryTime|.
     * Using this logic, is like the VM was recovered only in the end of the simulation.
     * It in fact is not recovered, but this logic has to be applied to
     * allow computing the availability.
     *
     * </p>
     * @param broker
     * @param lastVmFailedFromBroker
     */
    private void createVmCloneIfAllVmsDestroyed(final DatacenterBroker broker, final Vm lastVmFailedFromBroker) {
        if(isSomeVmWorking(broker)){
            return;
        }

        if(!isVmClonerSet(broker) || getVmCloner(broker).isMaxClonesNumberReached()) {
            vmRecoveryTimeSecsMap.put(lastVmFailedFromBroker, -getSimulation().clock());
        }

        if(!isVmClonerSet(broker)) {
            LOGGER.warn("\tA Vm Cloner was not set for {}. So that VM failure will not be recovered.", broker);
            return;
        }

        final VmCloner cloner = getVmCloner(broker);
        if(cloner.isMaxClonesNumberReached()){
            LOGGER.warn("\tThe maximum allowed number of {} VMs to create has been reached.", cloner.getMaxClonesNumber());
            return;
        }

        registerFaultOfAllVms(broker);
        final double recoveryTimeSecs = getRandomRecoveryTimeForVmInSecs();
        LOGGER.info("\tTime to recovery from fault by cloning the failed VM: {} minutes", recoveryTimeSecs/60.0);

        final Map.Entry<Vm, List<Cloudlet>> entry = cloner.clone(lastVmFailedFromBroker);

        final Vm clonedVm = entry.getKey();
        final List<Cloudlet> clonedCloudlets = entry.getValue();
        clonedVm.setSubmissionDelay(recoveryTimeSecs);
        clonedVm.addOnHostAllocationListener(evt -> vmRecoveryTimeSecsMap.put(evt.getVm(), recoveryTimeSecs));
        broker.submitVm(clonedVm);
        broker.submitCloudletList(clonedCloudlets, recoveryTimeSecs);
    }

    /**
     * Sets a VM inside the {@link #getLastFailedHost() last failed Host} to
     * failed and use the VM and Cloudlets cloner functions to create a clone of
     * the VMs with all its Cloudlets, to simulate the initialization of a new
     * VM instance from a snapshot of the failed VM.
     *
     * @param vm VM to set to failed
     */
    private void setVmToFailed(final Vm vm) {
        if (Host.NULL.equals(lastFailedHost)) {
            return;
        }

        vm.setFailed(true);
        final DatacenterBroker broker = vm.getBroker();
        if(isVmClonerSet(broker) && isSomeVmWorking(broker)){
            LOGGER.info(
                "\t{} destroyed but not cloned, since there are {} VMs for the {} yet",
                vm, getRunningVmsNumber(broker), broker);
        }

        /*
         As the broker is expected to request vm creation and destruction,
         it is set here as the sender of the vm destroy request.
         */
        getSimulation().sendNow(
                broker, datacenter,
                CloudSimTags.VM_DESTROY, vm);
    }

    /**
     * Register 1 more fault happened which caused all VMs from a given broker
     * to fault.
     *
     * @param broker the broker to increase the number of faults
     */
    private void registerFaultOfAllVms(final DatacenterBroker broker) {
        faultsOfAllVmsByBroker.merge(broker, 1, Integer::sum);
    }

    /**
     * Gets the {@link VmCloner} object to clone a {@link Vm}.
     *
     * @param broker the broker the VM belongs to
     * @return the {@link VmCloner} object or {@link VmCloner#NULL} if no cloner was set
     */
    private VmCloner getVmCloner(final DatacenterBroker broker) {
        return vmClonerMap.getOrDefault(broker, VmCloner.NULL);
    }

    private boolean isSomeVmWorking(final DatacenterBroker broker) {
        return broker.getVmExecList().stream().anyMatch(Vm::isWorking);
    }

    private long getRunningVmsNumber(final DatacenterBroker broker) {
        return broker.getVmExecList().stream().filter(Vm::isWorking).count();
    }

    /**
     * Checks if a {@link VmCloner} is set to a given broker.
     * @param broker broker to check if it has a {@link VmCloner}.
     * @return true if the broker has a {@link VmCloner}, false otherwise
     */
    private boolean isVmClonerSet(final DatacenterBroker broker) {
        return vmClonerMap.getOrDefault(broker, VmCloner.NULL) != VmCloner.NULL;
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
         return availability(null);
    }

    /**
     * Gets the availability for a given broker as a percentage value between 0 to 1,
     * based on VMs' downtime (the times VMs took to be repaired).
     *
     * @param broker the broker to get the availability of its VMs
     * @return
     */
    public double availability(final DatacenterBroker broker) {
        //no failures means 100% availability
        final double mtbf = meanTimeBetweenVmFaultsInMinutes(broker);
        if(mtbf == 0) {
            return 1;
        }

        final double mttr = meanTimeToRepairVmFaultsInMinutes(broker);
       // System.out.println(" Availability: broker " + broker + " value: " + mtbf / (mtbf + mttr));
        return mtbf / (mtbf + mttr);

    }

    /**
     * Gets the total number of faults which affected all VMs from any broker.
     * @return
     */
    public long getNumberOfFaults() {
        return (long) faultsOfAllVmsByBroker.values().size();
    }

    /**
     * Gets the total number of Host faults which affected all VMs from a given broker
     * or VMs from all existing brokers.
     *
     * @param broker the broker to get the number of Host faults affecting its VMs or null
     *               whether is to be counted Host faults affecting VMs from any broker
     * @return
     */
    public long getNumberOfFaults(final DatacenterBroker broker) {
        if(broker == null){
            return getNumberOfFaults();
        }

        return faultsOfAllVmsByBroker.getOrDefault(broker, 0);
    }

    /**
     * Gets the average of the time (in minutes) all failed VMs belonging to a broker took to recovery
     * from failure.
     * See the method {@link #createVmCloneIfAllVmsDestroyed(DatacenterBroker, Vm)}
     * to understand the logic of the values in the recovery times map.
     * @return
     */
    private double totalVmsRecoveryTimeInMinutes(final DatacenterBroker broker) {
        final Stream<Double> stream = broker == null ?
                vmRecoveryTimeSecsMap.values().stream() :
                vmRecoveryTimeSecsMap.entrySet().stream()
                    .filter(entry -> broker.equals(entry.getKey().getBroker()))
                    .map(Map.Entry::getValue);

        final double recoverySeconds = stream
                                        .map(secs -> secs >= 0 ? secs : getSimulation().clock() - Math.abs(secs))
                                        .reduce(0.0, Double::sum);

        //@TODO why is it converted to long if the method return is double?
        return (long)(recoverySeconds/60.0);
    }

    /**
     * Computes the current Mean Time Between host Failures (MTBF) in minutes.
     * Since Hosts don't actually recover from failures,
     * there aren't recovery time to make easier the computation
     * of MTBF for Host as it is directly computed for VMs.
     *
     * @return the current mean time (in minutes) between Host failures (MTBF)
     * or zero if no failures have happened yet
     * @see #meanTimeBetweenVmFaultsInMinutes()
     */
    public double meanTimeBetweenHostFaultsInMinutes() {
        final double[] faultTimes = hostFaultsTimeSecsMap
            .values()
            .stream()
            .flatMap(Collection::stream)
            .mapToDouble(time -> time)
            .sorted()
            .toArray();

        if(faultTimes.length == 0){
            return 0;
        }

        //computes the differences between failure times t2 - t1
        double sum=0;
        double previous=faultTimes[0];
        for(final double time: faultTimes) {
            sum += time - previous;
            previous = time;
        }

        final double seconds = sum/faultTimes.length;
        return (long)(seconds/60.0);
    }

    /**
     * Computes the current Mean Time Between host Failures (MTBF) in minutes,
     * which affected VMs from any broker for the entire Datacenter.
     * It uses a straightforward way to compute the MTBF.
     * Since it's stored the VM recovery times, it's possible
     * to use such values to make easier the MTBF computation,
     * different from the Hosts MTBF.
     *
     * @return the current Mean Time Between host Failures (MTBF) in minutes
     * or zero if no VM was destroyed due to Host failure
     * @see #meanTimeBetweenHostFaultsInMinutes()
     */
    public double meanTimeBetweenVmFaultsInMinutes() {
        return meanTimeBetweenVmFaultsInMinutes(null);
    }

    /**
     * Computes the current Mean Time Between host Failures (MTBF) in minutes,
     * which affected VMs from a given broker.
     * It uses a straightforward way to compute the MTBF.
     * Since it's stored the VM recovery times, it's possible
     * to use such values to make easier the MTBF computation,
     * different from the Hosts MTBF.
     *
     * @param broker the broker to get the MTBF for
     * @return the current mean time (in minutes) between Host failures (MTBF)
     * or zero if no VM was destroyed due to Host failure
     * @see #meanTimeBetweenHostFaultsInMinutes()
     */
    public double meanTimeBetweenVmFaultsInMinutes(final DatacenterBroker broker) {
        final double faultsFromBroker = getNumberOfFaults(broker);
        if(faultsFromBroker == 0){
            return 0;
        }

        final double totalVmsRecoveryTimeInMinutes = meanTimeToRepairVmFaultsInMinutes(broker);
        return getSimulation().clockInMinutes() - totalVmsRecoveryTimeInMinutes;
    }

    /**
     * Computes the current Mean Time To Repair failures of VMs in minutes (MTTR)
     * in the Datacenter, for all existing brokers.
     *
     * @return the MTTR (in minutes) or zero if no VM was destroyed due to Host failure
     */
    public double meanTimeToRepairVmFaultsInMinutes() {
        return meanTimeToRepairVmFaultsInMinutes(null);
    }

    /**
     * Computes the current Mean Time To Repair Failures of VMs in minutes (MTTR)
     * belonging to given broker.
     * If a null broker is given, computes the MTTR of all VMs for all existing brokers.
     *
     * @param broker the broker to get the MTTR for or null if the MTTR is to be computed for all brokers
     * @return the current MTTR (in minutes) or zero if no VM was destroyed due to Host failure
     */
    public double meanTimeToRepairVmFaultsInMinutes(final DatacenterBroker broker) {
        final double faultsFromBroker = getNumberOfFaults(broker);
        if(faultsFromBroker == 0){
            return 0;
        }
        return totalVmsRecoveryTimeInMinutes(broker) / faultsFromBroker;
    }

    /**
     * Generates failures for a given number of PEs from the
     * {@link #getLastFailedHost() last failed Host}.
     * The minimum number of PEs to fail is 1.
     *
     * @param numberOfPesToFail number of PEs to set as failed
     * @return the number of PEs just failed for the Host, which is equals to the input number
     */
    private int generateHostPesFaults(final int numberOfPesToFail) {
        final List<Pe> peList = lastFailedHost.getWorkingPeList()
            .stream()
            .limit(numberOfPesToFail)
            .collect(toList());

        ((HostSimple)lastFailedHost).setPeStatus(peList, Pe.Status.FAILED);

        return numberOfPesToFail;
    }

    /**
     * Gets the total number of PEs from all working VMs.
     * @return
     */
    private long getPesSumOfWorkingVms() {
        return lastFailedHost.getVmList().stream()
                .filter(Vm::isWorking)
                .mapToLong(AbstractMachine::getNumberOfPes)
                .sum();
    }

    /**
     * Randomly generates a number of PEs which will fail for the datacenter.
     * The minimum number of PEs to fail is 1.
     *
     * @param host the Host to generate a number of PEs to fail
     * @return the generated number of failed PEs for the datacenter,
     * between [1 and Number of PEs].
     */
    private int randomNumberOfFailedPes(final Host host) {
        /*the random generator return values from [0 to 1]
         and multiplying by the number of PEs we get a number between
         0 and number of PEs*/
        return (int) (random.sample() * host.getWorkingPesNumber()) + 1;
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
    protected final void setDatacenter(final Datacenter datacenter) {
        this.datacenter = requireNonNull(datacenter);
    }

    /**
     * Adds a {@link VmCloner} that creates a clone for the last failed {@link Vm} belonging to a given broker,
     * when all VMs of that broker have failed.
     *
     * <p>This is optional. If a {@link VmCloner} is not set,
     * VMs will not be recovered from failures.</p>
     *
     * @param broker the broker to set the VM cloner Function to
     * @param cloner the {@link VmCloner} to set
     */
    public void addVmCloner(final DatacenterBroker broker, final VmCloner cloner) {
        this.vmClonerMap.put(requireNonNull(broker), requireNonNull(cloner));
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

    /**
     * Gets a Pseudo Random Number used to give a
     * recovery time (in seconds) for each VM that was failed.
     * @return
     */
    public double getRandomRecoveryTimeForVmInSecs() {
        return random.sample()*MAX_VM_RECOVERY_TIME_SECS + 1;
    }

    /**
     * Gets the maximum time to generate a failure (in hours).
     * After that time, no failure will be generated.
     * @see #getMaxTimeToFailInSecs()
     */
    public double getMaxTimeToFailInHours() {
        return maxTimeToFailInHours;
    }

    /**
     * Gets the maximum time to generate a failure (in seconds).
     * After that time, no failure will be generated.
     * @see #getMaxTimeToFailInHours()
     */
    private double getMaxTimeToFailInSecs() {
        return maxTimeToFailInHours *3600;
    }

    /**
     * Sets the maximum time to generate a failure (in hours).
     * After that time, no failure will be generated.
     *
     * @param maxTimeToFailInHours the maximum time to set (in hours)
     */
    public void setMaxTimeToFailInHours(final double maxTimeToFailInHours) {
        this.maxTimeToFailInHours = maxTimeToFailInHours;
    }
}
