/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimEntity;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.Machine;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.distributions.ContinuousDistribution;
import org.cloudsimplus.distributions.PoissonDistr;
import org.cloudsimplus.distributions.StatisticalDistribution;
import org.cloudsimplus.distributions.UniformDistr;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.util.TimeUtil;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.cloudsimplus.core.CloudSimTag.HOST_FAILURE;
import static org.cloudsimplus.util.Conversion.HUNDRED_PERCENT;

/// Generates random failures for the [Pe]'s of [Host]s inside a given [Datacenter].
/// A Fault Injection object usually has to be created after the VMs are created,
/// making it easier to define a function to be used to clone failed VMs.
///
/// The events happen in the following order:
///
/// 1. a time to inject a Host failure is generated using a given [Random Number Generator][org.cloudsimplus.distributions.StatisticalDistribution];
/// 2. a Host is randomly selected to fail at that time using an internal
///  [Uniform Random Number Generator][UniformDistr] with the same seed of the given generator;
/// 3. the number of Host [Pe]s to fail is randomly generated using the internal generator;
/// 4. failed physical PEs are removed from affected VMs, VMs with no remaining
///  PEs and destroying and clones of them are submitted to the [DatacenterBroker] of the failed VMs;
/// 5. another failure is scheduled for a future time using the given generator;
/// 6. the process repeats until the end of the simulation.
///
///
/// When Host's PEs fail, if there are more available PEs
/// than required by its running VMs, no VM will be affected.
///
/// Considering that `X` is the number of failed PEs, and it is lower than the total available PEs.
/// In this case, the `X` PEs will be removed cyclically, 1 by 1, from running VMs.
/// This way, some VMs may continue running with fewer PEs than they requested initially.
/// On the other hand, if after the failure, the number of Host's working PEs
/// is lower than the required to run all VMs, some VMs will be destroyed.
///
/// If all PEs are removed from a VM, it is automatically destroyed,
/// and a snapshot (clone) from it is taken and submitted
/// to the [DatacenterBroker], so that the VM clone can start executing into another host.
/// In this case, all Cloudlets that still were running inside the VM
/// will be cloned too and restart executing from the beginning.
///
/// If a cloudlet running inside a VM which was affected by a PE failure
/// requires `Y` PEs but the VM doesn't have all the PEs anymore,
/// the Cloudlet will continue executing, but it will spend
/// more time to finish.
/// For instance, if a Cloudlet requires 2 PEs but after the failure,
/// the VM was left with just 1 PE, the Cloudlet will spend the double
/// of the time to finish.
///
/// **NOTES:**
///
/// - Host PEs failures may happen after all its VMs have finished executing.
///   This way, the presented simulation results may show that the
///   number of PEs into a Host is lower than required by its VMs.
///   In this case, the VMs shown in the results finished executing before
///   some failures have happened. Analyzing the logs is easy to confirm that.
/// - Inter-arrivals failures are defined in minutes, since seconds is a too
///  small time unit to define such value. Furthermore, it doesn't make sense to define
///  the number of failures per second. This way, the generator of failure arrival times
///  given to the constructor considers the time in minutes, despite the simulation
///  time unit is seconds. Since usually Cloudlets just take some seconds to finish,
///  mainly in simulation examples, failures may happen just after the cloudlets
///  have finished. This way, you should make sure that Cloudlet lengths
///  are large enough to allow failures to happen before they end running.
///
/// For more details, check
/// [Raysa Oliveira's Master Thesis (only in Portuguese)](https://ubibliorum.ubi.pt/handle/10400.6/7839).
///
/// @author raysaoliveira
/// @since CloudSim Plus 1.2.0
/// @link [SAP Blog: Availability vs Reliability](https://blogs.sap.com/2014/07/21/equipment-availability-vs-reliability/)
/// 2TODO The class has multiple responsibilities.
///      The fault injection mechanism must be separated from
///      the fault recovery. The cloner methods are fault recovery.
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
     * The last failed Host or {@link Host#NULL} if not Host has failed yet.
     */
    @Getter
    private Host lastFailedHost;

    /**
     * Number of PEs failed into the {@link #lastFailedHost}.
     */
    private int lastFailedPesNumber;

    /**
     * The datacenter in which failures will be injected.
     */
    @Getter
    private Datacenter datacenter;

    /**
     * A Pseudo Random Number Generator used to select a {@link Host}
     * and the number of {@link Pe}s to set as fail.
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
     * The total number of faults happened for existing hosts.
     * This isn't the total number of failed hosts because one
     * host may fail multiple times.
     */
    @Getter
    private int hostFaultsNumber;

    /**
     * A map to store the time (in seconds) VM failures took to be recovered,
     * which is when a clone from the last failed VM for a given {@link DatacenterBroker} is created.
     * Since a broker just creates a VM clone when all its VMs have failed,
     * only at that time the failure is in fact recovered.
     * It means the time period the failure of all VMs persisted
     * before a clone was created.
     */
    private final Map<Vm, Double> vmRecoveryTimeSecsMap;

    /**
     * A map to store the times (in seconds) for each Host failure.
     */
    private final Map<Host, List<Double>> hostFaultsTimeSecsMap;

    /**
     * A map to store the number of faults that caused all VMs from a {@link DatacenterBroker} to be destroyed.
     */
    private final Map<DatacenterBroker, Integer> vmFaultsByBroker;

    /**
     * The maximum time to generate a failure (in hours).
     * After that time, no failure will be generated.
     * Setting a value ensures that simulation won't run much longer after expected.
     * If your simulation is running forever (because failures are causing VMs to be restarted as clones),
     * set an explicit value here.
     * @see #getMaxTimeToFailInSecs()
     */
    @Getter @Setter
    private double maxTimeToFailInHours;

    /**
     * Creates a fault injection mechanism for the {@link Host}s of a given {@link Datacenter}.
     * The Hosts failures are randomly injected according to a {@link UniformDistr}
     * pseudo random number generator, which indicates the mean of failures to be
     * generated per <b>hour</b>, (which is also called <b>event rate</b> or <b>rate parameter</b>).
     *
     * @param datacenter the Datacenter to which failures will be randomly injected for its Hosts
     * @see #HostFaultInjection(Datacenter, StatisticalDistribution)
     */
    public HostFaultInjection(final Datacenter datacenter) {
      this(datacenter, new UniformDistr());
    }

    /**
     * Creates a fault injection mechanism for the {@link Host}s of a given {@link Datacenter}.
     * The Hosts failures are randomly injected according to the given
     * pseudo random number generator, that indicates the mean of failures to be generated
     * per <b>minute</b>, (which is also called <b>event rate</b> or <b>rate parameter</b>).
     *
     * @param datacenter the Datacenter to which failures will be randomly injected for its Hosts
     * @param faultArrivalHoursGenerator a Pseudo Random Number Generator which generates the
     * times Hosts failures will occur (in hours).
     * <b>The values returned by the generator will be considered to be hours</b>.
     * Frequently it is used a
     * {@link PoissonDistr} to generate failure arrivals, but any {@link ContinuousDistribution} can be provided
     */
    public HostFaultInjection(final Datacenter datacenter, final StatisticalDistribution faultArrivalHoursGenerator) {
        super(datacenter.getSimulation());
        this.setDatacenter(datacenter);
        this.lastFailedHost = Host.NULL;
        this.faultArrivalHoursGenerator = faultArrivalHoursGenerator;
        this.random = new UniformDistr(faultArrivalHoursGenerator.getSeed()+1);
        this.vmRecoveryTimeSecsMap = new HashMap<>();
        this.hostFaultsTimeSecsMap = new HashMap<>();
        this.vmFaultsByBroker = new HashMap<>();
        this.vmClonerMap = new HashMap<>();
        this.maxTimeToFailInHours = Double.MAX_VALUE;
    }

    @Override
    protected void startInternal() {
        scheduleFaultInjection();
    }

    /**
     * Schedules a message to be processed internally to try injecting a failure into Host {@link Pe}s.
     */
    private void scheduleFaultInjection() {
        final var sim = getSimulation();
        final Predicate<SimEvent> otherEventsPredicate = evt -> evt.getTag() != HOST_FAILURE;

        /*
        Just re-schedule more failures if there are other events to be processed.
        Otherwise, the simulation has finished and no more failures should be scheduled.

        The 2nd condition may be a complex operation that must be called only when necessary
        in the short-circuit below.
        */
        if (sim.clock() < getMaxTimeToFailInSecs() || sim.isThereAnyFutureEvt(otherEventsPredicate)) {
            schedule(this, getTimeDelayForNextFault(), HOST_FAILURE);
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
        if (evt.getTag() == HOST_FAILURE) {
            generateHostFaultAndScheduleNext();
        }
    }

    /**
     * Generates a fault for all {@link Pe}s of a Host.
     * @param host the Host to generate the fault to.
     */
    public void generateHostFault(final Host host){
        generateHostFault(host, host.getWorkingPesNumber());
    }

    /**
     * Generates a fault for a given number of random {@link Pe}s of a Host.
     * @param host the Host to generate the fault to.
     * @param pesFailures number of {@link Pe}s that must fail
     */
    public void generateHostFault(final Host host, final int pesFailures){
        if(Host.NULL == host){
            return;
        }

        this.lastFailedHost = host;

        hostFaultsNumber++;
        registerHostFaultTime();

        final long previousWorkingPes = lastFailedHost.getWorkingPesNumber();
        this.lastFailedPesNumber = generateHostPesFaults(pesFailures);
        final long hostWorkingPes = lastFailedHost.getWorkingPesNumber();
        final long vmsRequiredPes = getWorkingVmsPesCount();

        final String msg = lastFailedHost.getVmList().isEmpty() ? "" : " | VMs required PEs: " + vmsRequiredPes;
        if(hostWorkingPes > 0) {
            LOGGER.error(
                "{}: {}: Generated {} PEs failures in {} at {}.{}" +
                "\t  Previous working PEs: {} | Current Working PEs: {} | Current VMs: {}{}",
                getSimulation().clockStr(), getClass().getSimpleName(), lastFailedPesNumber,
                lastFailedHost, getTime(), System.lineSeparator(), previousWorkingPes,
                hostWorkingPes, lastFailedHost.getVmList().size(), msg);
        }

        if (hostWorkingPes == 0)
            setAllVmsToFailed();
        else if (hostWorkingPes >= vmsRequiredPes)
            logNoVmFault();
        else deallocateFailedHostPesFromVms();
    }

    private String getTime() {
        return TimeUtil.secondsToStr(getSimulation().clock());
    }

    /**
     * Generates a failure for a specific number of {@link Pe}s from a
     * randomly selected Host and schedules the next time to try generating a fault.
     */
    private void generateHostFaultAndScheduleNext() {
        try {
            final Host host = getRandomHost();
            generateHostFault(host, randomFailedPesNumber(host));
        } finally {
            //schedules the next failure-injection try
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
     * Randomly gets a Host that will have some {@link Pe}s set as failed.
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
     * failed, when all Host {@link Pe}s have failed.
     */
    private void setAllVmsToFailed() {
        final int vms = lastFailedHost.getVmList().size();
        final String msg = vms > 0 ? "affecting all its %d VMs".formatted(vms) : "but there was no running VM";
        LOGGER.error(
                "{}: {}: All {} PEs from {} failed at {}, {}.",
                getSimulation().clockStr(), getClass().getSimpleName(),
                lastFailedHost.getPesNumber(), lastFailedHost, getTime(), msg);
        setVmListToFailed(lastFailedHost.getVmList());
    }

    /**
     * Shows that the failure of Host {@link Pe}s hasn't affected any VM, because there
     * are more working PEs than required by all VMs.
     */
    private void logNoVmFault() {
        if(lastFailedHost.getVmList().isEmpty()){
            LOGGER.info("There aren't VMs running on the failed Host {}.", lastFailedHost.getId());
            return;
        }

        final int vmsRequiredPes = (int) getWorkingVmsPesCount();
        LOGGER.info(
                "{}: {}: Number of failed PEs in Host {} is smaller than working ones required by all VMs, not affecting any VM.{}" +
                "\t  Total PEs: {} | Total Failed PEs: {} | Working PEs: {} | Current PEs required by VMs: {}.",
                getSimulation().clockStr(), getClass().getSimpleName(), lastFailedHost.getId(), System.lineSeparator(),
                lastFailedHost.getPesNumber(), lastFailedHost.getFailedPesNumber(),
                lastFailedHost.getWorkingPesNumber(), vmsRequiredPes);
    }

    /**
     * De-allocates the physical {@link Pe}s failed for the
     * {@link #getLastFailedHost() last failed Host} from affected VMs.
     */
    private void deallocateFailedHostPesFromVms() {
        cyclicallyRemoveFailedHostPesFromVms();

        final List<Vm> vmsWithoutPes =
            lastFailedHost.getVmList()
                .stream()
                .filter(vm -> vm.getPesNumber() == 0)
                .collect(toList());
        setVmListToFailed(vmsWithoutPes);
    }

    /**
     * Removes one physical failed {@link Pe} from one affected VM at a time.
     * Affected VMs are handled as a circular list, visiting
     * one VM at a time to remove 1 PE from it,
     * until all the failed PEs are removed.
     */
    private void cyclicallyRemoveFailedHostPesFromVms() {
        int failedPesToRemoveFromVms = failedPesToRemoveFromVms();
        List<Vm> vmsWithPes = getVmsWithPEsFromFailedHost();
        final int affectedVms = Math.min(vmsWithPes.size(), failedPesToRemoveFromVms);

        LOGGER.warn("{} VMs affected from a total of {}. {} PEs are going to be removed from them.",
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
                    "Removing 1 PE from VM {} due to Host PE failure. New VM PEs Number: {}",
                    vm.getId(), vm.getPesNumber());
            idx++;
            vmsWithPes = getVmsWithPEsFromFailedHost();
        }
    }

    /**
     * @return the number of failed {@link Pe}s to remove from VMs.
     */
    private int failedPesToRemoveFromVms() {
        final int hostWorkingPes = lastFailedHost.getWorkingPesNumber();
        final int vmsRequiredPes = (int) getWorkingVmsPesCount();
        return vmsRequiredPes - hostWorkingPes;
    }

    /**
     * @return a List of VMs that have any {@link Pe} from the {@link #lastFailedHost}.
     */
    private List<Vm> getVmsWithPEsFromFailedHost() {
        return lastFailedHost
                .getVmList()
                .stream()
                .filter(vm -> vm.getPesNumber() > 0)
                .collect(toList());
    }

    /**
     * Sets all VMs from a given list as failed, due to Host {@link Pe}s failures.
     */
    private void setVmListToFailed(final List<Vm> vms) {
        final var lastVmFailedByBrokerMap = getLastFailedVmByBroker(vms);

        vms.forEach(this::setVmToFailed);
        lastVmFailedByBrokerMap.forEach(this::createVmCloneIfAllVmsDestroyed);
    }

    private Map<DatacenterBroker, Vm> getLastFailedVmByBroker(final List<Vm> vmsWithoutPes) {
        final var vmComparator = Comparator.comparingLong(Vm::getId);
        return vmsWithoutPes
                    .stream()
                    .collect(
                        toMap(Vm::getBroker, Function.identity(), BinaryOperator.maxBy(vmComparator))
                    );
    }

    /**
     * Clones the last failed VM if all VMs belonging to the broker have failed
     * and the maximum number of clones to create was not reached.
     *
     * <p>
     * If all VMs have failed and a {@link VmCloner} is not set or the max number of
     * clones already was created, the customer service will be completely unavailable
     * from the time of the failure until the end of the simulation.
     * </p>
     *
     * <p>Since the {@link #vmRecoveryTimeSecsMap} stores recovery times and not unavailability times,
     * it's being stored the failure time as a negative value.
     * This way, when computing the availability for the customer,
     * these negative values are changed to: lastSimulationTime - |negativeRecoveryTime|.
     * Using this logic, it is as if the VM was recovered only at the end of the simulation.
     * It, in fact, is not recovered, but this logic has to be applied to allow computing the availability.
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
            LOGGER.warn("A Vm Cloner was not set for {}. So that VM failure will not be recovered.", broker);
            return;
        }

        final VmCloner cloner = getVmCloner(broker);
        if(cloner.isMaxClonesNumberReached()){
            LOGGER.warn("The maximum allowed number of {} VMs to create has been reached.", cloner.getMaxClonesNumber());
            return;
        }

        registerFaultOfAllVms(broker);
        final double recoveryTimeSecs = getRandomRecoveryTimeForVmInSecs();
        final String time = "%.2f".formatted(recoveryTimeSecs / 60.0);
        LOGGER.info(
            "{}: {}: Time to recovery from fault by cloning the last failed VM on {}: {} minutes.",
            getSimulation().clockStr(), getClass().getSimpleName(),
            lastVmFailedFromBroker.getBroker(), time);

        final Map.Entry<Vm, List<Cloudlet>> entry = cloner.clone(lastVmFailedFromBroker);

        final Vm clonedVm = entry.getKey();
        final var clonedCloudletList = entry.getValue();
        clonedVm.setSubmissionDelay(recoveryTimeSecs);
        clonedVm.addOnHostAllocationListener(evt -> vmRecoveryTimeSecsMap.put(evt.getVm(), recoveryTimeSecs));
        broker.submitVm(clonedVm);
        broker.submitCloudletList(clonedCloudletList, recoveryTimeSecs);
    }

    /**
     * Sets a VM inside the {@link #getLastFailedHost() last failed Host} to
     * failed and use the VM and Cloudlets cloner functions to create a clone of
     * the VMs with all its Cloudlets, to simulate the initialization of a new
     * VM instance from a snapshot of the failed VM.
     *
     * @param vm VM to set as failed
     */
    private void setVmToFailed(final Vm vm) {
        if (Host.NULL.equals(lastFailedHost)) {
            return;
        }

        vm.setFailed(true);
        final DatacenterBroker broker = vm.getBroker();
        if(isVmClonerSet(broker) && isSomeVmWorking(broker)){
            LOGGER.info(
                "{}: {}: {} destroyed on {} but not cloned, since there are {} VMs for the broker yet.",
                getSimulation().clockStr(), broker, vm, vm.getHost(), getRunningVmsNumber(broker));
        }

        /*
         As the broker is expected to request vm creation and destruction,
         it is set here as the sender of the vm destroy request.
         */
        getSimulation().sendNow(broker, datacenter, CloudSimTag.VM_DESTROY, vm);
    }

    /**
     * Register 1 more fault happened which caused all VMs from a given {@link DatacenterBroker} to fault.
     *
     * @param broker the broker to increase the number of faults
     */
    private void registerFaultOfAllVms(final DatacenterBroker broker) {
        vmFaultsByBroker.merge(broker, 1, Integer::sum);
    }

    /**
     * Gets the {@link VmCloner} object to clone a {@link Vm}.
     *
     * @param broker the {@link DatacenterBroker} the VM belongs to
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
     * Checks if a {@link VmCloner} is set to a given {@link DatacenterBroker}.
     * @param broker broker to check if it has a {@link VmCloner}.
     * @return true if the broker has a {@link VmCloner}, false otherwise
     */
    private boolean isVmClonerSet(final DatacenterBroker broker) {
        return vmClonerMap.getOrDefault(broker, VmCloner.NULL) != VmCloner.NULL;
    }

    /**
     * @return the {@link Datacenter}'s availability as a percentage value between 0 and 1,
     * based on VMs' downtime (the times VMs took to be repaired).
     */
    public double availability() {
        //If there is no fault for any broker, the total availability is 1 (100%)
        return vmFaultsByBroker.keySet().stream().mapToDouble(this::availability).average().orElse(HUNDRED_PERCENT);
    }

    /**
     * {@return the availability for a given broker as a percentage value between 0 and 1}
     * That is based on VMs' downtime (the times VMs took to be repaired).
     *
     * @param broker the broker to get the availability of its VMs
     *
     */
    public double availability(final DatacenterBroker broker) {
        //no failure means 100% availability
        final double mtbf = meanTimeBetweenVmFaultsInMinutes(broker);
        if(mtbf == 0) {
            return 1;
        }

        final double mttr = meanTimeToRepairVmFaultsInMinutes(broker);
       // System.out.println(" Availability: broker " + broker + " value: " + mtbf / (mtbf + mttr));
        return mtbf / (mtbf + mttr);
    }

    /**
     * @return the total number of faults which affected all VMs from any {@link DatacenterBroker}.
     * @see #getTotalFaultsNumber(DatacenterBroker)
     */
    public long getTotalFaultsNumber() {
        return vmFaultsByBroker.values().stream().mapToLong(v -> v).sum();
    }

    /**
     * Gets the total number of Host faults which affected all VMs from a given broker
     * or VMs from all existing brokers.
     *
     * @param broker the broker to get the number of Host faults affecting its VMs
     * @return
     * @see #getTotalFaultsNumber()
     */
    public long getTotalFaultsNumber(@NonNull final DatacenterBroker broker) {
        return vmFaultsByBroker.getOrDefault(broker, 0);
    }

    /**
     * @return the time average (in minutes) all failed VMs belonging to a {@link DatacenterBroker} took
     * to recovery from failure.
     * See the method {@link #createVmCloneIfAllVmsDestroyed(DatacenterBroker, Vm)}
     * to understand the logic of the values in the recovery times map.
     */
    private double totalVmsRecoveryTimeInMinutes(final DatacenterBroker broker) {
        final var timeStream = broker == null ?
                vmRecoveryTimeSecsMap.values().stream() :
                vmRecoveryTimeSecsMap.entrySet().stream()
                    .filter(entry -> broker.equals(entry.getKey().getBroker()))
                    .map(Map.Entry::getValue);

        final double recoverySeconds = timeStream
                                        .mapToDouble(secs -> secs >= 0 ? secs : getSimulation().clock() - Math.abs(secs))
                                        .sum();

        return TimeUtil.secondsToMinutes(recoverySeconds);
    }

    /**
     * Computes the current Mean Time Between host Failures (MTBF) in minutes.
     * Since Hosts don't actually recover from failures,
     * there isn't a recovery time to make easier the computation
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

        //Computes the differences between failure times t2 - t1
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
     * which affected VMs from any {@link DatacenterBroker} for the entire Datacenter.
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
        return vmFaultsByBroker.keySet().stream().mapToDouble(this::meanTimeBetweenVmFaultsInMinutes).average().orElse(0);
    }

    /**
     * Computes the current Mean Time Between host Failures (MTBF) in minutes,
     * which affected VMs from a given {@link DatacenterBroker}.
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
        final double faults = getTotalFaultsNumber(broker);
        return faults == 0 ? 0 : getSimulation().clockInMinutes() - meanTimeToRepairVmFaultsInMinutes(broker);

    }

    /**
     * Computes the current Mean Time To Repair failures of VMs in minutes (MTTR)
     * in the Datacenter, for all existing {@link DatacenterBroker}s.
     *
     * @return the MTTR (in minutes) or zero if no VM was destroyed due to Host failure
     */
    public double meanTimeToRepairVmFaultsInMinutes() {
        return vmFaultsByBroker.keySet().stream().mapToDouble(this::meanTimeToRepairVmFaultsInMinutes).average().orElse(0);
    }

    /**
     * Computes the current Mean Time To Repair Failures of VMs in minutes (MTTR)
     * belonging to a given {@link DatacenterBroker}.
     * If a null broker is given, computes the MTTR of all VMs for all existing brokers.
     *
     * @param broker the broker to get the MTTR for
     * @return the current MTTR (in minutes) or zero if no VM was destroyed due to Host failure
     */
    public double meanTimeToRepairVmFaultsInMinutes(final DatacenterBroker broker) {
        final double faults = getTotalFaultsNumber(broker);
        return faults == 0 ? 0 : totalVmsRecoveryTimeInMinutes(broker) / faults;

    }

    /**
     * Generates failures for a given number of {@link Pe}s from the
     * {@link #getLastFailedHost() last failed Host}.
     * The minimum number of PEs to fail is 1.
     *
     * @param pesFailures number of PEs to set as failed
     * @return the number of PEs just failed for the Host, which is equal to the input number
     */
    private int generateHostPesFaults(final int pesFailures) {
        final var peList = lastFailedHost.getWorkingPeList()
            .stream()
            .limit(pesFailures)
            .toList();

        ((HostSimple)lastFailedHost).setPeStatus(peList, Pe.Status.FAILED);

        return pesFailures;
    }

    /**
     * @return the total number of PEs from all working VMs.
     */
    private long getWorkingVmsPesCount() {
        return lastFailedHost.getVmList().stream()
                .filter(Vm::isWorking)
                .mapToLong(Machine::getPesNumber)
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
    private int randomFailedPesNumber(final Host host) {
        /* The random generator return values from [0 to 1]
         and multiplying by the number of PEs we get a number between
         0 and the number of PEs. */
        return (int) (random.sample() * host.getWorkingPesNumber()) + 1;
    }

    /**
     * Sets the datacenter in which failures will be injected.
     *
     * @param datacenter the datacenter to set
     */
    protected final void setDatacenter(@NonNull final Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    /**
     * Adds a {@link VmCloner} that creates a clone for the last failed {@link Vm}
     * belonging to a given {@link DatacenterBroker}, when all VMs of that broker have failed.
     *
     * <p>This is optional, but <b>if a {@link VmCloner} is not set,
     * VMs will not be recovered from failures</b>.</p>
     *
     * @param broker the broker to set the VM cloner Function to
     * @param cloner the {@link VmCloner} to set
     */
    public void addVmCloner(@NonNull final DatacenterBroker broker, @NonNull final VmCloner cloner) {
        this.vmClonerMap.put(broker, cloner);
    }

    /**
     * @return a Pseudo Random Number used to give a
     * recovery time (in seconds) for each VM that was failed.
     */
    public double getRandomRecoveryTimeForVmInSecs() {
        return random.sample()*MAX_VM_RECOVERY_TIME_SECS + 1;
    }

    /**
     * @return the maximum time to generate a failure (in seconds).
     * After that time, no failure will be generated.
     * @see #getMaxTimeToFailInHours()
     */
    private double getMaxTimeToFailInSecs() {
        return maxTimeToFailInHours *3600;
    }
}
