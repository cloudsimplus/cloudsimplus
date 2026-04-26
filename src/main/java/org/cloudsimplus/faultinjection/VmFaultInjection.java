/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
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
import org.cloudsimplus.util.TimeUtil;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.cloudsimplus.core.CloudSimTag.VM_FAILURE;
import static org.cloudsimplus.util.Conversion.HUNDRED_PERCENT;

/// Generates random failures at the VM level for VMs running in a given [Datacenter].
/// While [HostFaultInjection] models hardware faults at the [Host] [org.cloudsimplus.resources.Pe]
/// level, this class models software-level VM faults (kernel panic, OS crash, hypervisor-induced
/// VM termination, runaway process, etc.) that are independent of the underlying physical host.
///
/// The events happen in the following order:
///
/// 1. a time to inject a VM failure is generated using a given
///    [Random Number Generator][StatisticalDistribution];
/// 2. a working VM is randomly selected across all Hosts of the Datacenter using an internal
///    [UniformDistr] with the seed of the given generator + 1;
/// 3. according to the configured [#getFaultMode() FaultMode], either the entire VM is destroyed
///    (and its currently running Cloudlets are lost) or a random subset of the Cloudlets running
///    inside the VM is set to [Cloudlet.Status#FAILED];
/// 4. when a VM is destroyed and a [VmCloner] is registered for the broker that owns it,
///    a clone is submitted with a random recovery delay so the customer service can be recovered;
/// 5. another failure is scheduled for a future time using the given generator;
/// 6. the process repeats until the end of the simulation.
///
/// The class deliberately does not mutate existing CloudSim Plus components: all interactions go
/// through public APIs ([DatacenterBroker], [CloudSimTag#VM_DESTROY],
/// [org.cloudsimplus.schedulers.cloudlet.CloudletScheduler#cloudletFail(Cloudlet)] and the
/// existing [VmCloner] recovery facility). It can therefore coexist with [HostFaultInjection]
/// in the same simulation without any change to the rest of the framework.
///
/// Inter-arrival times follow the same convention as in [HostFaultInjection]: the values returned
/// by the given generator are interpreted as **hours** and converted to seconds internally.
///
/// @author CloudSim Plus contributors
/// @see HostFaultInjection
/// @see VmCloner
public class VmFaultInjection extends CloudSimEntity {

    /// Possible behaviors when a VM is selected to fail.
    public enum FaultMode {
        /// The whole VM is destroyed (its running Cloudlets are lost). Default.
        VM_CRASH,

        /// A random subset of the Cloudlets currently running inside the VM is failed,
        /// while the VM itself keeps running.
        CLOUDLET_FAILURE
    }

    /**
     * Maximum number of seconds for a VM to recover from a failure. The recovery time is the
     * delay applied to a clone submitted by the registered {@link VmCloner}.
     */
    private static final int MAX_VM_RECOVERY_TIME_SECS = 450;

    private static final Logger LOGGER = LoggerFactory.getLogger(VmFaultInjection.class.getSimpleName());

    /**
     * The Datacenter whose VMs will receive faults.
     */
    @Getter
    private Datacenter datacenter;

    /**
     * The last VM that was selected to fail, or {@link Vm#NULL} if no failure has happened yet.
     */
    @Getter
    private Vm lastFailedVm;

    /**
     * Total number of VM-level faults generated, regardless of whether the VM had a registered
     * cloner. Counts every fault attempt that hit a real VM (not {@link Vm#NULL}).
     */
    @Getter
    private int vmFaultsNumber;

    /**
     * The behavior applied when a VM is selected to fail.
     */
    @Getter @Setter @NonNull
    private FaultMode faultMode;

    /**
     * The maximum time, in hours, during which faults will be generated. Past that time no
     * additional fault is scheduled. Useful to bound runs in which clones keep restarting.
     */
    @Getter @Setter
    private double maxTimeToFailInHours;

    /**
     * Pseudo-random generator for the inter-arrival times (in hours) of VM faults.
     */
    private final StatisticalDistribution faultArrivalHoursGenerator;

    /**
     * Pseudo-random generator used for selecting the VM to fail and for recovery delays.
     * Seeded from {@link #faultArrivalHoursGenerator} + 1, mirroring {@link HostFaultInjection}.
     */
    private final ContinuousDistribution random;

    /**
     * VM cloners registered per broker, used to recover VMs destroyed by a VM-level fault.
     */
    private final Map<DatacenterBroker, VmCloner> vmClonerMap;

    /**
     * Maps each VM whose fault caused a clone (or that remained unrecovered) to the recovery
     * time in seconds. Negative values mean the VM was never recovered: the absolute value is
     * the failure timestamp, used by {@link #totalVmsRecoveryTimeInMinutes(DatacenterBroker)}
     * to charge the time from failure to end-of-simulation as downtime.
     */
    private final Map<Vm, Double> vmRecoveryTimeSecsMap;

    /**
     * Per-broker counter of faults that destroyed all VMs of that broker (VM_CRASH mode only).
     */
    private final Map<DatacenterBroker, Integer> vmFaultsByBroker;

    /**
     * Timestamps (in seconds) of every fault generated, grouped by VM.
     */
    private final Map<Vm, List<Double>> vmFaultsTimeSecsMap;

    /**
     * Creates a VM fault-injection mechanism using a default {@link UniformDistr} for the
     * fault inter-arrival times, expressed in hours.
     *
     * @param datacenter the Datacenter whose VMs will be subject to faults
     */
    public VmFaultInjection(final Datacenter datacenter) {
        this(datacenter, new UniformDistr());
    }

    /**
     * Creates a VM fault-injection mechanism.
     *
     * @param datacenter the Datacenter whose VMs will be subject to faults
     * @param faultArrivalHoursGenerator a Pseudo Random Number Generator producing the fault
     *        inter-arrival times. <b>The values returned by the generator are considered to be
     *        hours</b>. A {@link PoissonDistr} is a common choice but any
     *        {@link ContinuousDistribution} is accepted.
     */
    public VmFaultInjection(final Datacenter datacenter, final StatisticalDistribution faultArrivalHoursGenerator) {
        super(datacenter.getSimulation());
        setDatacenter(datacenter);
        this.lastFailedVm = Vm.NULL;
        this.faultArrivalHoursGenerator = faultArrivalHoursGenerator;
        this.random = new UniformDistr(faultArrivalHoursGenerator.getSeed() + 1);
        this.vmClonerMap = new HashMap<>();
        this.vmRecoveryTimeSecsMap = new HashMap<>();
        this.vmFaultsByBroker = new HashMap<>();
        this.vmFaultsTimeSecsMap = new HashMap<>();
        this.maxTimeToFailInHours = Double.MAX_VALUE;
        this.faultMode = FaultMode.VM_CRASH;
    }

    @Override
    protected void startInternal() {
        scheduleFaultInjection();
    }

    @Override
    public void processEvent(final SimEvent evt) {
        if (evt.getTag() == VM_FAILURE) {
            generateVmFaultAndScheduleNext();
        }
    }

    /**
     * Schedules an internal message to inject a VM fault, but only if there are still
     * pending events from other entities. This avoids extending the simulation forever.
     */
    private void scheduleFaultInjection() {
        final var sim = getSimulation();
        final Predicate<SimEvent> otherEventsPredicate = evt -> evt.getTag() != VM_FAILURE;

        if (sim.clock() < getMaxTimeToFailInSecs() || sim.isThereAnyFutureEvt(otherEventsPredicate)) {
            schedule(this, getTimeDelayForNextFault(), VM_FAILURE);
        }
    }

    /**
     * @return the delay (in seconds) from the current simulation time to the next fault.
     * Values returned by {@link #faultArrivalHoursGenerator} are interpreted as hours.
     */
    private double getTimeDelayForNextFault() {
        return faultArrivalHoursGenerator.sample() * 3600;
    }

    /**
     * Picks a random working VM and injects a fault into it. Always reschedules the next
     * attempt, even if no VM was eligible at this firing.
     */
    private void generateVmFaultAndScheduleNext() {
        try {
            final Vm vm = getRandomWorkingVm();
            generateVmFault(vm);
        } finally {
            scheduleFaultInjection();
        }
    }

    /**
     * Injects a fault into the given VM, applying the configured {@link FaultMode}.
     * If the VM is {@link Vm#NULL} or already failed, nothing happens.
     *
     * @param vm the VM to fail
     */
    public void generateVmFault(final Vm vm) {
        if (Vm.NULL.equals(vm) || vm == null || !vm.isWorking()) {
            return;
        }

        this.lastFailedVm = vm;
        vmFaultsNumber++;
        registerVmFaultTime(vm);

        if (faultMode == FaultMode.CLOUDLET_FAILURE) {
            failRandomCloudlets(vm);
        } else {
            crashVm(vm);
        }
    }

    /**
     * Injects a fault that fails a random subset of the cloudlets currently executing
     * inside the given VM. The VM keeps running. Convenience method allowing a caller to
     * force a Cloudlet-level fault regardless of the configured {@link #faultMode}.
     *
     * @param vm the VM whose running cloudlets may be failed
     * @return the number of cloudlets actually failed
     */
    public int generateCloudletFault(final Vm vm) {
        if (Vm.NULL.equals(vm) || vm == null || !vm.isWorking()) {
            return 0;
        }

        this.lastFailedVm = vm;
        vmFaultsNumber++;
        registerVmFaultTime(vm);
        return failRandomCloudlets(vm);
    }

    private int failRandomCloudlets(final Vm vm) {
        final var running = vm.getCloudletScheduler()
                              .getCloudletExecList()
                              .stream()
                              .map(cle -> cle.getCloudlet())
                              .collect(toList());

        if (running.isEmpty()) {
            LOGGER.info(
                "{}: {}: {} was selected to a Cloudlet-level fault but has no running Cloudlets.",
                getSimulation().clockStr(), getClass().getSimpleName(), vm);
            return 0;
        }

        // At least 1 cloudlet, at most all of them.
        final int toFail = (int) (random.sample() * running.size()) + 1;
        int failed = 0;
        for (int i = 0; i < toFail && i < running.size(); i++) {
            final int idx = (int) (random.sample() * running.size());
            final Cloudlet cloudlet = running.get(idx);
            if (cloudlet.getStatus() == Cloudlet.Status.INEXEC || cloudlet.getStatus() == Cloudlet.Status.QUEUED) {
                vm.getCloudletScheduler().cloudletFail(cloudlet);
                failed++;
            }
        }

        LOGGER.error(
            "{}: {}: {} of {}'s {} running Cloudlet(s) were FAILED inside VM {} (Cloudlet-level fault).",
            getSimulation().clockStr(), getClass().getSimpleName(),
            failed, vm.getBroker(), running.size(), vm.getId());
        return failed;
    }

    private void crashVm(final Vm vm) {
        final DatacenterBroker broker = vm.getBroker();

        LOGGER.error(
            "{}: {}: VM {} of {} CRASHED at {} (running on {}). VM-level fault.",
            getSimulation().clockStr(), getClass().getSimpleName(),
            vm.getId(), broker, getTimeStr(), vm.getHost());

        vm.setFailed(true);
        // Mirrors HostFaultInjection: the broker is the conventional sender of VM_DESTROY.
        getSimulation().sendNow(broker, datacenter, CloudSimTag.VM_DESTROY, vm);

        if (isSomeVmWorking(broker)) {
            LOGGER.info(
                "{}: {}: {} crashed but the broker {} still has {} working VM(s); no clone will be created yet.",
                getSimulation().clockStr(), getClass().getSimpleName(),
                vm, broker, getRunningVmsNumber(broker));
            return;
        }

        // All VMs of the broker are gone.
        registerFaultOfAllVms(broker);
        if (!isVmClonerSet(broker) || getVmCloner(broker).isMaxClonesNumberReached()) {
            // Store failure timestamp as a negative recovery time so availability still
            // accounts for the unrecovered downtime, the same convention used by
            // HostFaultInjection#createVmCloneIfAllVmsDestroyed.
            vmRecoveryTimeSecsMap.put(vm, -getSimulation().clock());
        }

        if (!isVmClonerSet(broker)) {
            LOGGER.warn("A Vm Cloner was not set for {}. The VM failure will not be recovered.", broker);
            return;
        }

        final VmCloner cloner = getVmCloner(broker);
        if (cloner.isMaxClonesNumberReached()) {
            LOGGER.warn("The maximum allowed number of {} VM clones for {} has been reached.",
                cloner.getMaxClonesNumber(), broker);
            return;
        }

        final double recoveryTimeSecs = getRandomRecoveryTimeForVmInSecs();
        LOGGER.info(
            "{}: {}: Time to recovery from VM-level fault by cloning the last failed VM on {}: {} minutes.",
            getSimulation().clockStr(), getClass().getSimpleName(),
            broker, "%.2f".formatted(recoveryTimeSecs / 60.0));

        final Map.Entry<Vm, List<Cloudlet>> entry = cloner.clone(vm);
        final Vm clonedVm = entry.getKey();
        final List<Cloudlet> clonedCloudletList = entry.getValue();
        clonedVm.setSubmissionDelay(recoveryTimeSecs);
        clonedVm.addOnHostAllocationListener(evt -> vmRecoveryTimeSecsMap.put(evt.getVm(), recoveryTimeSecs));
        broker.submitVm(clonedVm);
        broker.submitCloudletList(clonedCloudletList, recoveryTimeSecs);
    }

    /**
     * @return a random working VM picked uniformly across all Hosts of the Datacenter, or
     *         {@link Vm#NULL} if no working VM exists.
     */
    private Vm getRandomWorkingVm() {
        final List<Vm> candidates = new ArrayList<>();
        for (final Host host : datacenter.getHostList()) {
            for (final var vm : host.getVmList()) {
                if (vm.isWorking()) {
                    candidates.add(vm);
                }
            }
        }

        if (candidates.isEmpty()) {
            return Vm.NULL;
        }

        final int idx = (int) (random.sample() * candidates.size());
        return candidates.get(idx);
    }

    private void registerVmFaultTime(final Vm vm) {
        vmFaultsTimeSecsMap.computeIfAbsent(vm, v -> new ArrayList<>()).add(getSimulation().clock());
    }

    private void registerFaultOfAllVms(final DatacenterBroker broker) {
        vmFaultsByBroker.merge(broker, 1, Integer::sum);
    }

    private boolean isSomeVmWorking(final DatacenterBroker broker) {
        return broker.getVmExecList().stream().anyMatch(Vm::isWorking);
    }

    private long getRunningVmsNumber(final DatacenterBroker broker) {
        return broker.getVmExecList().stream().filter(Vm::isWorking).count();
    }

    private VmCloner getVmCloner(final DatacenterBroker broker) {
        return vmClonerMap.getOrDefault(broker, VmCloner.NULL);
    }

    private boolean isVmClonerSet(final DatacenterBroker broker) {
        return vmClonerMap.getOrDefault(broker, VmCloner.NULL) != VmCloner.NULL;
    }

    /**
     * Registers a {@link VmCloner} that creates a clone for the last failed VM of the given
     * {@link DatacenterBroker} when all of that broker's VMs have been destroyed by a
     * {@link FaultMode#VM_CRASH VM-level crash}.
     *
     * @param broker the broker the cloner will recover
     * @param cloner the {@link VmCloner} instance to use
     */
    public void addVmCloner(@NonNull final DatacenterBroker broker, @NonNull final VmCloner cloner) {
        this.vmClonerMap.put(broker, cloner);
    }

    /**
     * @return the Datacenter availability as a percentage in [0, 1], averaged over the brokers
     *         that experienced at least one full-broker fault. Returns 1 when no fault occurred.
     */
    public double availability() {
        return vmFaultsByBroker.keySet().stream().mapToDouble(this::availability).average().orElse(HUNDRED_PERCENT);
    }

    /**
     * @param broker the broker to compute availability for
     * @return availability for the given broker as MTBF / (MTBF + MTTR), or 1 if no fault has
     *         destroyed all the broker's VMs.
     */
    public double availability(final DatacenterBroker broker) {
        final double mtbf = meanTimeBetweenVmFaultsInMinutes(broker);
        if (mtbf == 0) {
            return 1;
        }
        final double mttr = meanTimeToRepairVmFaultsInMinutes(broker);
        return mtbf / (mtbf + mttr);
    }

    /**
     * @return the total number of full-broker faults across all brokers
     */
    public long getTotalFaultsNumber() {
        return vmFaultsByBroker.values().stream().mapToLong(v -> v).sum();
    }

    /**
     * @param broker the broker to query
     * @return the number of faults that destroyed all VMs of the given broker
     */
    public long getTotalFaultsNumber(@NonNull final DatacenterBroker broker) {
        return vmFaultsByBroker.getOrDefault(broker, 0);
    }

    /**
     * @return the Mean Time Between Failures (in minutes) averaged across all brokers, or 0 if
     *         no full-broker fault occurred.
     */
    public double meanTimeBetweenVmFaultsInMinutes() {
        return vmFaultsByBroker.keySet().stream().mapToDouble(this::meanTimeBetweenVmFaultsInMinutes).average().orElse(0);
    }

    /**
     * @param broker the broker to compute the MTBF for
     * @return MTBF in minutes for the given broker, or 0 if no fault destroyed all its VMs.
     */
    public double meanTimeBetweenVmFaultsInMinutes(final DatacenterBroker broker) {
        final double faults = getTotalFaultsNumber(broker);
        return faults == 0 ? 0 : getSimulation().clockInMinutes() - meanTimeToRepairVmFaultsInMinutes(broker);
    }

    /**
     * @return the Mean Time To Repair (in minutes) averaged across all brokers, or 0 if no
     *         full-broker fault occurred.
     */
    public double meanTimeToRepairVmFaultsInMinutes() {
        return vmFaultsByBroker.keySet().stream().mapToDouble(this::meanTimeToRepairVmFaultsInMinutes).average().orElse(0);
    }

    /**
     * @param broker the broker to compute MTTR for
     * @return MTTR in minutes for the given broker, or 0 if no fault destroyed all its VMs.
     */
    public double meanTimeToRepairVmFaultsInMinutes(final DatacenterBroker broker) {
        final double faults = getTotalFaultsNumber(broker);
        return faults == 0 ? 0 : totalVmsRecoveryTimeInMinutes(broker) / faults;
    }

    private double totalVmsRecoveryTimeInMinutes(final DatacenterBroker broker) {
        final var stream = broker == null
            ? vmRecoveryTimeSecsMap.values().stream()
            : vmRecoveryTimeSecsMap.entrySet().stream()
                  .filter(e -> broker.equals(e.getKey().getBroker()))
                  .map(Map.Entry::getValue);

        final double recoverySeconds = stream
            .mapToDouble(secs -> secs >= 0 ? secs : getSimulation().clock() - Math.abs(secs))
            .sum();
        return TimeUtil.secondsToMinutes(recoverySeconds);
    }

    /**
     * @return a random recovery time, in seconds, in (1, MAX_VM_RECOVERY_TIME_SECS + 1].
     */
    public double getRandomRecoveryTimeForVmInSecs() {
        return random.sample() * MAX_VM_RECOVERY_TIME_SECS + 1;
    }

    /**
     * @return the maximum time to generate a fault expressed in seconds.
     */
    private double getMaxTimeToFailInSecs() {
        return maxTimeToFailInHours * 3600;
    }

    private String getTimeStr() {
        return TimeUtil.secondsToStr(getSimulation().clock());
    }

    protected final void setDatacenter(@NonNull final Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
