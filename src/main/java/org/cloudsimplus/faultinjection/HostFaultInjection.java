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
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.distributions.ContinuousDistribution;
import org.cloudsimplus.distributions.PoissonDistr;
import org.cloudsimplus.distributions.StatisticalDistribution;
import org.cloudsimplus.distributions.UniformDistr;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Processor;
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
import static org.cloudsimplus.resources.Processor.cores;
import static org.cloudsimplus.resources.Processor.workingCores;
import static org.cloudsimplus.util.Conversion.HUNDRED_PERCENT;

/// Generates random failures for the cores of [Host]s inside a given [Datacenter].
/// A Fault Injection object usually has to be created after the VMs are created,
/// making it easier to define a function to be used to clone failed VMs.
///
/// The events happen in the following order:
///
/// 1. a time to inject a Host failure is generated using a given [Random Number Generator][org.cloudsimplus.distributions.StatisticalDistribution];
/// 2. a Host is randomly selected to fail at that time using an internal
///  [Uniform Random Number Generator][UniformDistr] with the same seed of the given generator;
/// 3. the number of Host cores to fail is randomly generated using the internal generator.
///  Since the Host just counts its cores in each status, a failure first takes free cores:
///  when a busy core fails, the hypervisor moves its work to a free core, so the VMs aren't affected.
///  Only when there are no free cores left, busy cores fail;
/// 4. failed physical cores are removed from affected VMs, VMs with no remaining
///  cores and destroying and clones of them are submitted to the [DatacenterBroker] of the failed VMs;
/// 5. another failure is scheduled for a future time using the given generator;
/// 6. the process repeats until the end of the simulation.
///
///
/// When Host's cores fail, if there are more available cores
/// than required by its running VMs, no VM will be affected.
///
/// Considering that `X` is the number of failed cores, and it is lower than the total available cores.
/// In this case, the `X` cores will be removed cyclically, 1 by 1, from running VMs.
/// This way, some VMs may continue running with fewer cores than they requested initially.
/// On the other hand, if after the failure, the number of Host's working cores
/// is lower than the required to run all VMs, some VMs will be destroyed.
///
/// If all cores are removed from a VM, it is automatically destroyed,
/// and a snapshot (clone) from it is taken and submitted
/// to the [DatacenterBroker], so that the VM clone can start executing into another host.
/// In this case, all Cloudlets that still were running inside the VM
/// will be cloned too and restart executing from the beginning.
///
/// If a cloudlet running inside a VM which was affected by a core failure
/// requires `Y` cores but the VM doesn't have all the cores anymore,
/// the Cloudlet will continue executing, but it will spend
/// more time to finish.
/// For instance, if a Cloudlet requires 2 cores but after the failure,
/// the VM was left with just 1 core, the Cloudlet will spend the double
/// of the time to finish.
///
/// **NOTES:**
///
/// - Host cores failures may happen after all its VMs have finished executing.
///   This way, the presented simulation results may show that the
///   number of CPU cores into a Host is lower than required by its VMs.
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
/// TODO The class has multiple responsibilities.
///      The fault injection mechanism must be separated from
///      the fault recovery. The cloner methods are fault recovery.
/// See https://github.com/cloudsimplus/cloudsimplus/issues/591
public class HostFaultInjection extends CloudSimEntity {
    /// Maximum number of seconds for a VM to recovery from a failure,
    /// which is randomly selected based on this value.
    /// The recovery time is the delay that will be set
    /// to start a clone from a failed VM.
    private static final int MAX_VM_RECOVERY_TIME_SECS = 450;

    private static final Logger LOGGER = LoggerFactory.getLogger(HostFaultInjection.class.getSimpleName());

    /// The last failed Host or [Host#NULL] if not Host has failed yet.
    @Getter
    private Host lastFailedHost;

    /// Number of CPU cores failed into the [#lastFailedHost].
    private long lastFailedCores;

    /// The datacenter in which failures will be injected.
    @Getter
    private Datacenter datacenter;

    /// A Pseudo Random Number Generator used to select a [Host]
    /// and the number of CPU cores to set as fail.
    private final ContinuousDistribution random;

    /// A map that stores [VmCloner] objects to be used to clone
    /// the VMs belonging to a broker.
    ///
    /// @see #addVmCloner(DatacenterBroker, VmCloner)
    private final Map<DatacenterBroker, VmCloner> vmClonerMap;

    /// A Pseudo Random Number Generator which generates the times (in hours)
    /// that Hosts failures will occur.
    private final StatisticalDistribution faultArrivalHoursGenerator;

    /// The total number of faults happened for existing hosts.
    /// This isn't the total number of failed hosts because one
    /// host may fail multiple times.
    @Getter
    private int hostFaultsNumber;

    /// A map to store the time (in seconds) VM failures took to be recovered,
    /// which is when a clone from the last failed VM for a given [DatacenterBroker] is created.
    /// Since a broker just creates a VM clone when all its VMs have failed,
    /// only at that time the failure is in fact recovered.
    /// It means the time period the failure of all VMs persisted
    /// before a clone was created.
    private final Map<Vm, Double> vmRecoveryTimeSecsMap;

    /// A map to store the times (in seconds) for each Host failure.
    private final Map<Host, List<Double>> hostFaultsTimeSecsMap;

    /// A map to store the number of faults that caused all VMs from a [DatacenterBroker] to be destroyed.
    private final Map<DatacenterBroker, Integer> vmFaultsByBroker;

    /// The maximum time to generate a failure (in hours).
    /// After that time, no failure will be generated.
    /// Setting a value ensures that simulation won't run much longer after expected.
    /// If your simulation is running forever (because failures are causing VMs to be restarted as clones),
    /// set an explicit value here.
    /// @see #getMaxTimeToFailInSecs()
    @Getter @Setter
    private double maxTimeToFailInHours;

    /// Creates a fault injection mechanism for the [Host]s of a given [Datacenter].
    /// The Hosts failures are randomly injected according to a [UniformDistr]
    /// pseudo random number generator, which indicates the mean of failures to be
    /// generated per **hour**, (which is also called **event rate** or **rate parameter**).
    ///
    /// @param datacenter the Datacenter to which failures will be randomly injected for its Hosts
    /// @see #HostFaultInjection(Datacenter, StatisticalDistribution)
    public HostFaultInjection(final Datacenter datacenter) {
      this(datacenter, new UniformDistr());
    }

    /// Creates a fault injection mechanism for the [Host]s of a given [Datacenter].
    /// The Hosts failures are randomly injected according to the given
    /// pseudo random number generator, that indicates the mean of failures to be generated
    /// per **minute**, (which is also called **event rate** or **rate parameter**).
    ///
    /// @param datacenter the Datacenter to which failures will be randomly injected for its Hosts
    /// @param faultArrivalHoursGenerator a Pseudo Random Number Generator which generates the
    /// times Hosts failures will occur (in hours).
    /// **The values returned by the generator will be considered to be hours**.
    /// Frequently it is used a
    /// [PoissonDistr] to generate failure arrivals, but any [ContinuousDistribution] can be provided
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

    /// Schedules a message to be processed internally to try injecting a failure into Host cores.
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

    /// Gets the time delay in seconds, from the current simulation time,
    /// that the next failure will be injected.
    /// Since the values returned by the [#faultArrivalHoursGenerator]
    /// are considered to be in **hours**, such values are converted to seconds.
    ///
    /// @return the next failure injection delay in seconds
    private double getTimeDelayForNextFault() {
        return faultArrivalHoursGenerator.sample() * 3600;
    }

    @Override
    public void processEvent(final SimEvent evt) {
        if (evt.getTag() == HOST_FAILURE) {
            generateHostFaultAndScheduleNext();
        }
    }

    /// Generates a fault for all cores of a Host.
    /// @param host the Host to generate the fault to.
    public void generateHostFault(final Host host){
        generateHostFault(host, workingCores(host));
    }

    /// Generates a fault for a given number of random cores of a Host.
    /// @param host the Host to generate the fault to.
    /// @param coreFailures number of CPU cores that must fail
    public void generateHostFault(final Host host, final long coreFailures){
        if(Host.NULL == host){
            return;
        }

        this.lastFailedHost = host;

        hostFaultsNumber++;
        registerHostFaultTime();

        final long previousWorkingCores = workingCores(lastFailedHost);
        this.lastFailedCores = generateHostCoreFaults(coreFailures);
        final long hostWorkingCores = workingCores(lastFailedHost);
        final long vmsRequiredCores = getWorkingVmsCores();

        final String msg = lastFailedHost.getVmList().isEmpty() ? "" : " | CPU cores required by VMs: " + vmsRequiredCores;
        if(hostWorkingCores > 0) {
            LOGGER.error(
                "{}: {}: Generated {} CPU core failures in {} at {}.{}" +
                "\t  Previous working CPU cores: {} | Current Working CPU cores: {} | Current VMs: {}{}",
                getSimulation().clockStr(), getClass().getSimpleName(), lastFailedCores,
                lastFailedHost, getTime(), System.lineSeparator(), previousWorkingCores,
                hostWorkingCores, lastFailedHost.getVmList().size(), msg);
        }

        if (hostWorkingCores == 0)
            setAllVmsToFailed();
        else if (hostWorkingCores >= vmsRequiredCores)
            logNoVmFault();
        else deallocateFailedHostCoresFromVms();
    }

    private String getTime() {
        return TimeUtil.secondsToStr(getSimulation().clock());
    }

    /// Generates a failure for a specific number of CPU cores from a
    /// randomly selected Host and schedules the next time to try generating a fault.
    private void generateHostFaultAndScheduleNext() {
        try {
            final Host host = getRandomHost();
            generateHostFault(host, randomFailedCores(host));
        } finally {
            //schedules the next failure-injection try
            scheduleFaultInjection();
        }
    }

    /// Register the time for a Host failure.
    private void registerHostFaultTime() {
        hostFaultsTimeSecsMap.computeIfAbsent(lastFailedHost, host -> new ArrayList<>()).add(getSimulation().clock());
    }

    /// Randomly gets a Host that will have some cores set as failed.
    ///
    /// @return the randomly selected Host; or [Host#NULL] if the Datacenter
    /// doesn't have Hosts or the selected one doesn't have more cores.
    private Host getRandomHost() {
        if (datacenter.getHostList().isEmpty()) {
            return Host.NULL;
        }

        final int idx = (int) (random.sample() * datacenter.getHostList().size());
        return datacenter.getHost(idx);
    }

    /// Sets all VMs inside the [last failed Host][#getLastFailedHost()] to
    /// failed, when all Host cores have failed.
    private void setAllVmsToFailed() {
        final int vms = lastFailedHost.getVmList().size();
        final String msg = vms > 0 ? "affecting all its %d VMs".formatted(vms) : "but there was no running VM";
        LOGGER.error(
                "{}: {}: All {} CPU cores from {} failed at {}, {}.",
                getSimulation().clockStr(), getClass().getSimpleName(),
                cores(lastFailedHost), lastFailedHost, getTime(), msg);
        setVmListToFailed(lastFailedHost.getVmList());
    }

    /// Shows that the failure of Host cores hasn't affected any VM, because there
    /// are more working cores than required by all VMs.
    private void logNoVmFault() {
        if(lastFailedHost.getVmList().isEmpty()){
            LOGGER.info("There aren't VMs running on the failed Host {}.", lastFailedHost.getId());
            return;
        }

        final long vmsRequiredCores = getWorkingVmsCores();
        LOGGER.info(
                "{}: {}: Number of failed CPU cores in Host {} is smaller than working ones required by all VMs, not affecting any VM.{}" +
                "\t  Total CPU cores: {} | Total Failed CPU cores: {} | Working CPU cores: {} | Current CPU cores required by VMs: {}.",
                getSimulation().clockStr(), getClass().getSimpleName(), lastFailedHost.getId(), System.lineSeparator(),
                cores(lastFailedHost), lastFailedHost.getProcessor().getFailedCores(),
                workingCores(lastFailedHost), vmsRequiredCores);
    }

    /// De-allocates the physical cores failed for the
    /// [last failed Host][#getLastFailedHost()] from affected VMs.
    private void deallocateFailedHostCoresFromVms() {
        cyclicallyRemoveFailedHostCoresFromVms();

        final List<Vm> vmsWithoutCores =
            lastFailedHost.getVmList()
                .stream()
                .filter(vm -> cores(vm) == 0)
                .collect(toList());
        setVmListToFailed(vmsWithoutCores);
    }

    /// Removes one physical failed core from one affected VM at a time.
    /// Affected VMs are handled as a circular list, visiting
    /// one VM at a time to remove 1 core from it,
    /// until all the failed cores are removed.
    private void cyclicallyRemoveFailedHostCoresFromVms() {
        long failedCoresToRemoveFromVms = failedCoresToRemoveFromVms();
        List<Vm> vmsWithCores = getVmsWithCoresFromFailedHost();
        final long affectedVms = Math.min(vmsWithCores.size(), failedCoresToRemoveFromVms);

        LOGGER.warn("{} VMs affected from a total of {}. {} CPU cores are going to be removed from them.",
                affectedVms, lastFailedHost.getVmList().size(), failedCoresToRemoveFromVms);
        int idx = 0;
        while (!vmsWithCores.isEmpty() && failedCoresToRemoveFromVms > 0) {
            idx = idx % vmsWithCores.size();
            final Vm vm = vmsWithCores.get(idx);
            if (!lastFailedHost.getVmScheduler().resizeVmCores(vm, -1)) {
                LOGGER.error("Unable to remove 1 CPU core from VM {} due to Host CPU core failure.", vm.getId());
                vmsWithCores.remove(idx);
                continue;
            }

            failedCoresToRemoveFromVms--;
            LOGGER.warn(
                    "Removing 1 CPU core from VM {} due to Host CPU core failure. New number of VM CPU cores: {}",
                    vm.getId(), cores(vm));
            if (cores(vm) == 0) {
                vmsWithCores.remove(idx);
            } else {
                idx++;
            }
        }
    }

    /// @return the number of failed cores to remove from VMs.
    private long failedCoresToRemoveFromVms() {
        final long hostWorkingCores = workingCores(lastFailedHost);
        final long vmsRequiredCores = getWorkingVmsCores();
        return vmsRequiredCores - hostWorkingCores;
    }

    /// @return a List of VMs that have any core from the [#lastFailedHost].
    private List<Vm> getVmsWithCoresFromFailedHost() {
        return lastFailedHost
                .getVmList()
                .stream()
                .filter(vm -> cores(vm) > 0)
                .collect(toList());
    }

    /// Sets all VMs from a given list as failed, due to Host cores failures.
    private void setVmListToFailed(final List<Vm> vms) {
        final var lastVmFailedByBrokerMap = getLastFailedVmByBroker(vms);

        vms.forEach(this::setVmToFailed);
        lastVmFailedByBrokerMap.forEach(this::createVmCloneIfAllVmsDestroyed);
    }

    private Map<DatacenterBroker, Vm> getLastFailedVmByBroker(final List<Vm> vmsWithoutCores) {
        final var vmComparator = Comparator.comparingLong(Vm::getId);
        return vmsWithoutCores
                    .stream()
                    .collect(
                        toMap(Vm::getBroker, Function.identity(), BinaryOperator.maxBy(vmComparator))
                    );
    }

    /// Clones the last failed VM if all VMs belonging to the broker have failed
    /// and the maximum number of clones to create was not reached.
    ///
    /// If all VMs have failed and a [VmCloner] is not set or the max number of
    /// clones already was created, the customer service will be completely unavailable
    /// from the time of the failure until the end of the simulation.
    ///
    /// Since the [#vmRecoveryTimeSecsMap] stores recovery times and not unavailability times,
    /// it's being stored the failure time as a negative value.
    /// This way, when computing the availability for the customer,
    /// these negative values are changed to: lastSimulationTime - |negativeRecoveryTime|.
    /// Using this logic, it is as if the VM was recovered only at the end of the simulation.
    /// It, in fact, is not recovered, but this logic has to be applied to allow computing the availability.
    ///
    /// @param broker
    /// @param lastVmFailedFromBroker
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
        clonedVm.listeners().addOnHostPlacement(evt -> vmRecoveryTimeSecsMap.put(evt.getVm(), recoveryTimeSecs));
        broker.submitVm(clonedVm);
        broker.submitCloudletList(clonedCloudletList, recoveryTimeSecs);
    }

    /// Sets a VM inside the [last failed Host][#getLastFailedHost()] to
    /// failed and use the VM and Cloudlets cloner functions to create a clone of
    /// the VMs with all its Cloudlets, to simulate the initialization of a new
    /// VM instance from a snapshot of the failed VM.
    ///
    /// @param vm VM to set as failed
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

    /// Register 1 more fault happened which caused all VMs from a given [DatacenterBroker] to fault.
    ///
    /// @param broker the broker to increase the number of faults
    private void registerFaultOfAllVms(final DatacenterBroker broker) {
        vmFaultsByBroker.merge(broker, 1, Integer::sum);
    }

    /// Gets the [VmCloner] object to clone a [Vm].
    ///
    /// @param broker the [DatacenterBroker] the VM belongs to
    /// @return the [VmCloner] object or [VmCloner#NULL] if no cloner was set
    private VmCloner getVmCloner(final DatacenterBroker broker) {
        return vmClonerMap.getOrDefault(broker, VmCloner.NULL);
    }

    private boolean isSomeVmWorking(final DatacenterBroker broker) {
        return broker.getVmExecList().stream().anyMatch(Vm::isWorking);
    }

    private long getRunningVmsNumber(final DatacenterBroker broker) {
        return broker.getVmExecList().stream().filter(Vm::isWorking).count();
    }

    /// Checks if a [VmCloner] is set to a given [DatacenterBroker].
    /// @param broker broker to check if it has a [VmCloner].
    /// @return true if the broker has a [VmCloner], false otherwise
    private boolean isVmClonerSet(final DatacenterBroker broker) {
        return vmClonerMap.getOrDefault(broker, VmCloner.NULL) != VmCloner.NULL;
    }

    /// @return the [Datacenter]'s availability as a percentage value between 0 and 1,
    /// based on VMs' downtime (the times VMs took to be repaired).
    public double availability() {
        //If there is no fault for any broker, the total availability is 1 (100%)
        return vmFaultsByBroker.keySet().stream().mapToDouble(this::availability).average().orElse(HUNDRED_PERCENT);
    }

    /// {@return the availability for a given broker as a percentage value between 0 and 1}
    /// That is based on VMs' downtime (the times VMs took to be repaired).
    ///
    /// @param broker the broker to get the availability of its VMs
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

    /// @return the total number of faults which affected all VMs from any [DatacenterBroker].
    /// @see #getTotalFaultsNumber(DatacenterBroker)
    public long getTotalFaultsNumber() {
        return vmFaultsByBroker.values().stream().mapToLong(v -> v).sum();
    }

    /// Gets the total number of Host faults which affected all VMs from a given broker
    /// or VMs from all existing brokers.
    ///
    /// @param broker the broker to get the number of Host faults affecting its VMs
    /// @return
    /// @see #getTotalFaultsNumber()
    public long getTotalFaultsNumber(@NonNull final DatacenterBroker broker) {
        return vmFaultsByBroker.getOrDefault(broker, 0);
    }

    /// @return the time average (in minutes) all failed VMs belonging to a [DatacenterBroker] took
    /// to recovery from failure.
    /// See the method [#createVmCloneIfAllVmsDestroyed(DatacenterBroker,Vm)]
    /// to understand the logic of the values in the recovery times map.
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

    /// Computes the current Mean Time Between host Failures (MTBF) in minutes.
    /// Since Hosts don't actually recover from failures,
    /// there isn't a recovery time to make easier the computation
    /// of MTBF for Host as it is directly computed for VMs.
    ///
    /// @return the current mean time (in minutes) between Host failures (MTBF)
    /// or zero if no failures have happened yet
    /// @see #meanTimeBetweenVmFaultsInMinutes()
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

    /// Computes the current Mean Time Between host Failures (MTBF) in minutes,
    /// which affected VMs from any [DatacenterBroker] for the entire Datacenter.
    /// It uses a straightforward way to compute the MTBF.
    /// Since it's stored the VM recovery times, it's possible
    /// to use such values to make easier the MTBF computation,
    /// different from the Hosts MTBF.
    ///
    /// @return the current Mean Time Between host Failures (MTBF) in minutes
    /// or zero if no VM was destroyed due to Host failure
    /// @see #meanTimeBetweenHostFaultsInMinutes()
    public double meanTimeBetweenVmFaultsInMinutes() {
        return vmFaultsByBroker.keySet().stream().mapToDouble(this::meanTimeBetweenVmFaultsInMinutes).average().orElse(0);
    }

    /// Computes the current Mean Time Between host Failures (MTBF) in minutes,
    /// which affected VMs from a given [DatacenterBroker].
    /// It uses a straightforward way to compute the MTBF.
    /// Since it's stored the VM recovery times, it's possible
    /// to use such values to make easier the MTBF computation,
    /// different from the Hosts MTBF.
    ///
    /// @param broker the broker to get the MTBF for
    /// @return the current mean time (in minutes) between Host failures (MTBF)
    /// or zero if no VM was destroyed due to Host failure
    /// @see #meanTimeBetweenHostFaultsInMinutes()
    public double meanTimeBetweenVmFaultsInMinutes(final DatacenterBroker broker) {
        final double faults = getTotalFaultsNumber(broker);
        return faults == 0 ? 0 : getSimulation().clockInMinutes() - meanTimeToRepairVmFaultsInMinutes(broker);

    }

    /// Computes the current Mean Time To Repair failures of VMs in minutes (MTTR)
    /// in the Datacenter, for all existing [DatacenterBroker]s.
    ///
    /// @return the MTTR (in minutes) or zero if no VM was destroyed due to Host failure
    public double meanTimeToRepairVmFaultsInMinutes() {
        return vmFaultsByBroker.keySet().stream().mapToDouble(this::meanTimeToRepairVmFaultsInMinutes).average().orElse(0);
    }

    /// Computes the current Mean Time To Repair Failures of VMs in minutes (MTTR)
    /// belonging to a given [DatacenterBroker].
    /// If a null broker is given, computes the MTTR of all VMs for all existing brokers.
    ///
    /// @param broker the broker to get the MTTR for
    /// @return the current MTTR (in minutes) or zero if no VM was destroyed due to Host failure
    public double meanTimeToRepairVmFaultsInMinutes(final DatacenterBroker broker) {
        final double faults = getTotalFaultsNumber(broker);
        return faults == 0 ? 0 : totalVmsRecoveryTimeInMinutes(broker) / faults;

    }

    /// Generates failures for a given number of CPU cores from the
    /// [last failed Host][#getLastFailedHost()].
    /// Free cores fail first, as described in the [class documentation][HostFaultInjection].
    ///
    /// @param coreFailures number of CPU cores to set as failed
    /// @return the number of CPU cores just failed for the Host,
    ///         which is the given number limited to the number of working cores
    private long generateHostCoreFaults(final long coreFailures) {
        return lastFailedHost.getProcessor().failCores(coreFailures);
    }

    /// @return the total number of CPU cores from all working VMs.
    private long getWorkingVmsCores() {
        return lastFailedHost.getVmList().stream()
                .filter(Vm::isWorking)
                .mapToLong(Processor::cores)
                .sum();
    }

    /// Randomly generates a number of CPU cores which will fail for the datacenter.
    /// The minimum number of CPU cores to fail is 1.
    ///
    /// @param host the Host to generate a number of CPU cores to fail
    /// @return the generated number of failed cores for the datacenter,
    /// between \[1 and Number of CPU cores\].
    private long randomFailedCores(final Host host) {
        /* The random generator return values from [0 to 1]
         and multiplying by the number of Cores we get a number between
         0 and the number of Cores. */
        return (long) (random.sample() * workingCores(host)) + 1;
    }

    /// Sets the datacenter in which failures will be injected.
    ///
    /// @param datacenter the datacenter to set
    protected final void setDatacenter(@NonNull final Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    /// Adds a [VmCloner] that creates a clone for the last failed [Vm]
    /// belonging to a given [DatacenterBroker], when all VMs of that broker have failed.
    ///
    /// This is optional, but **if a [VmCloner] is not set,
    /// VMs will not be recovered from failures**.
    ///
    /// @param broker the broker to set the VM cloner Function to
    /// @param cloner the [VmCloner] to set
    public void addVmCloner(@NonNull final DatacenterBroker broker, @NonNull final VmCloner cloner) {
        this.vmClonerMap.put(broker, cloner);
    }

    /// @return a Pseudo Random Number used to give a
    /// recovery time (in seconds) for each VM that was failed.
    public double getRandomRecoveryTimeForVmInSecs() {
        return random.sample()*MAX_VM_RECOVERY_TIME_SECS + 1;
    }

    /// @return the maximum time to generate a failure (in seconds).
    /// After that time, no failure will be generated.
    /// @see #getMaxTimeToFailInHours()
    private double getMaxTimeToFailInSecs() {
        return maxTimeToFailInHours *3600;
    }
}
