/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.placementpolicies.migration;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.placementpolicies.VmPlacementPolicy;
import org.cloudsimplus.placementpolicies.VmPlacementPolicyAbstract;
import org.cloudsimplus.core.CloudInformationService;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.util.TimeUtil;
import org.cloudsimplus.vms.Vm;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.*;
import static org.cloudsimplus.resources.Processor.totalMips;
import org.jspecify.annotations.Nullable;

/// An abstract VM placement policy that dynamically optimizes the
/// VM allocation (placement) using migration.
/// **It's a Best Fit policy that selects the Host with most efficient power usage to place a given VM.**
/// Such a behaviour can be overridden by subclasses.
///
/// Any [VmPlacementPolicyMigration] implementation must be based on this class.
///
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 3.0
@Accessors
public non-sealed abstract class VmPlacementPolicyMigrationAbstract
    extends VmPlacementPolicyAbstract
    implements VmPlacementPolicyMigration
{
    /// Default CPU utilization percentage (\[0..1\]) that indicates a Host is underloaded.
    /// @see #setUnderUtilizationThreshold(double)
    public static final double DEF_UNDER_UTILIZATION_THRESHOLD = 0.35;

    /// @see VmPlacementPolicyMigration#getUnderUtilizationThreshold()
    @Getter
    private double underUtilizationThreshold;

    /// @see #getVmSelectionPolicy()
    @NonNull @Getter @Setter
    private VmSelectionPolicy vmSelectionPolicy;

    /// @see VmPlacementPolicyMigration#isUnderloaded()
    @Getter
    private boolean underloaded;

    /// @see VmPlacementPolicyMigration#isOverloaded()
    @Getter
    private boolean overloaded;

    /// The migrations planned by the current migration check,
    /// or [MigrationPlan#NONE] when no check is running.
    /// Hosts are evaluated against it, so the check never changes the real VM placement.
    private MigrationPlan plan = MigrationPlan.NONE;

    /// The datacenter to try migrating VMs to.
    /// The initial value is the [datacenter][#getDatacenter()] this policy is linked to,
    /// so that the policy tries migrating VMs inside the datacenter (inter-datacenter migration)
    /// before looking for a different one.
    private Datacenter targetMigrationDc = Datacenter.NULL;

    /// The index of the datacenter from the entire [CloudInformationService]
    /// datacenter list to try migrating VMs to, if the [#targetMigrationDc] has no suitable Hosts.
    /// This is just used when the [datacenter][#getDatacenter()] linked to this policy
    /// doesn't have suitable Hosts for inter-datacenter VM migration. This way, a different datacenter is tried.
    private int targetMigrationDcIndex;

    /// Creates a VmPlacementPolicy using a [default under utilization threshold][#DEF_UNDER_UTILIZATION_THRESHOLD].
    ///
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    public VmPlacementPolicyMigrationAbstract(final VmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, null);
    }

    /// Creates a new VmPlacementPolicy, changing the [Function] to select a Host for a Vm.
    /// It uses a [default under utilization threshold][#DEF_UNDER_UTILIZATION_THRESHOLD].
    ///
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @param findHostForVmFunction a [Function] to select a Host for a given Vm.
    /// Passing null makes the Function to be set as the default [#findHostForVm(Vm)].
    /// @see VmPlacementPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
    /// @see #setUnderUtilizationThreshold(double)
    public VmPlacementPolicyMigrationAbstract(
        final VmSelectionPolicy vmSelectionPolicy,
        @Nullable final BiFunction<VmPlacementPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(findHostForVmFunction);
        this.underUtilizationThreshold = DEF_UNDER_UTILIZATION_THRESHOLD;
        setVmSelectionPolicy(vmSelectionPolicy);
    }

    @Override
    public VmPlacementPolicyAbstract setDatacenter(final Datacenter datacenter) {
        super.setDatacenter(datacenter);
        this.targetMigrationDc = datacenter;
        return this;
    }

    /// {@inheritDoc}
    /// The returned map keeps the order the migrations were planned.
    @Override
    public Map<Vm, Host> getOptimizedPlacementMap(final List<? extends Vm> vmList) {
        final var overloadedHosts = getOverloadedHosts();
        this.overloaded = !overloadedHosts.isEmpty();
        printOverUtilizedHosts(overloadedHosts);

        setMigrationPlan(new MigrationPlan());
        try {
            final Map<Vm, Host> migrationMap = getMigrationMapFromOverloadedHosts(overloadedHosts);
            updateMigrationMapFromUnderloadedHosts(overloadedHosts, migrationMap);

            if (overloaded && migrationMap.isEmpty()) {
                hostSearchRetry();
            }

            return migrationMap;
        } finally {
            setMigrationPlan(MigrationPlan.NONE);
        }
    }

    /// Sets the migrations planned by the current migration check.
    /// A policy that delegates Host evaluations to another one (such as a fallback policy)
    /// overrides it to pass the plan to that policy too.
    /// @param plan the plan of the current check, or [MigrationPlan#NONE] when the check finishes
    void setMigrationPlan(final MigrationPlan plan) {
        this.plan = plan;
    }

    /// {@return the MIPS a Host's VMs use, considering the migrations planned by the current migration check}
    /// Outside a check, it's the [Host#getCpuMipsUtilization()].
    /// @param host the Host to get the MIPS utilization
    protected final double getPlannedCpuMipsUtilization(final Host host) {
        return plan.cpuMipsUtilization(host);
    }

    private void hostSearchRetry() {
        final var dcList = getDatacenter().getSimulation().getCis().getDatacenterList();

        final double hostSearchRetryDelay = getDatacenter().getHostSearchRetryDelay();
        final var msg = hostSearchRetryDelay > 0 ?
            "in " + TimeUtil.secondsToStr(hostSearchRetryDelay) :
            "as soon as possible";

        final boolean singleDc = dcList.size() == 1;
        final var targetDcName = singleDc || getDatacenter().equals(targetMigrationDc) ? "" : "on %s ".formatted(targetMigrationDc);
        LOGGER.warn(
            "{}: {}: An under or overload situation was detected on {}, however there aren't suitable Hosts {}to manage that. Trying again {}.",
            getDatacenter().getSimulation().clock(), getClass().getSimpleName(), getDatacenter(), targetDcName, msg);

        if (!singleDc) {
            //Next time, try migrating some VMs from this to other datacenter
            targetMigrationDcIndex = ++targetMigrationDcIndex % dcList.size();
            this.targetMigrationDc = dcList.get(targetMigrationDcIndex);
        }
    }

    /// Updates the  map of VMs that will be migrated from under utilized hosts.
    ///
    /// @param overloadedHosts the List of over utilized hosts
    /// @param migrationMap current migration map that will be updated
    private void updateMigrationMapFromUnderloadedHosts(
        final Set<Host> overloadedHosts,
        final Map<Vm, Host> migrationMap)
    {
        final var switchedOffHosts = getSwitchedOffHosts();

        // overloaded hosts + hosts that are selected to migrate VMs from overloaded hosts
        final var ignoredSourceHosts = getIgnoredHosts(overloadedHosts, switchedOffHosts);

        /* Hosts that will receive VMs from overloaded Hosts aren't sources of VMs from underloaded Hosts,
         * since the planned VMs will take them out of the underload state. */
        ignoredSourceHosts.addAll(migrationMap.values());

        // overloaded + underloaded hosts
        final var ignoredTargetHosts = getIgnoredHosts(overloadedHosts, switchedOffHosts);

        final int numberOfHosts = getHostList().size();

        this.underloaded = false;
        while (true) {
            if (numberOfHosts == ignoredSourceHosts.size()) {
                break;
            }

            final var underloadedHost = getUnderloadedHost(ignoredSourceHosts);
            if (Host.NULL.equals(underloadedHost)) {
                break;
            }
            this.underloaded = true;

            LOGGER.info("{}: VmPlacementPolicy: Underloaded hosts: {}", getDatacenter().getSimulation().clockStr(), underloadedHost);

            ignoredSourceHosts.add(underloadedHost);
            ignoredTargetHosts.add(underloadedHost);

            final var vmsToMigrateList = getVmsToMigrateFromUnderUtilizedHost(underloadedHost);
            if (!vmsToMigrateList.isEmpty()) {
                logVmsToBeReallocated(underloadedHost, vmsToMigrateList);
                final Map<Vm, Host> newVmPlacement = getNewVmPlacementFromUnderloadedHost(
                        vmsToMigrateList,
                        ignoredTargetHosts);

                ignoredSourceHosts.addAll(extractHostListFromMigrationMap(newVmPlacement));
                migrationMap.putAll(newVmPlacement);
            }
        }
    }

    private void logVmsToBeReallocated(final Host underloadedHost, final List<? extends Vm> migratingOutVms) {
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("{}: VmPlacementPolicy: VMs to be reallocated from the underloaded {}: {}",
                getDatacenter().getSimulation().clockStr(), underloadedHost, getVmIds(migratingOutVms));
        }
    }

    private Set<Host> getIgnoredHosts(final Set<Host> overloadedHosts, final List<Host> switchedOffHosts) {
        final var ignoredHostsSet = new HashSet<Host>();
        ignoredHostsSet.addAll(overloadedHosts);
        ignoredHostsSet.addAll(switchedOffHosts);
        return ignoredHostsSet;
    }

    private String getVmIds(final List<? extends Vm> vmList) {
        return vmList.stream().map(vm -> String.valueOf(vm.getId())).collect(joining(", "));
    }

    /// Prints the over-utilized hosts.
    ///
    /// @param overloadedHosts the over-utilized hosts
    private void printOverUtilizedHosts(final Set<Host> overloadedHosts) {
        if (!overloadedHosts.isEmpty() && LOGGER.isWarnEnabled()) {
            final String hosts = overloadedHosts.stream().map(this::overloadedHostToString).collect(joining(System.lineSeparator()));
            LOGGER.warn("{}: VmPlacementPolicy: Overloaded hosts in {}:{}{}",
                getDatacenter().getSimulation().clockStr(), getDatacenter(), System.lineSeparator(), hosts);
        }
    }

    private String overloadedHostToString(final Host host) {
        return
            "      Host %d (upper CPU threshold %.2f, current utilization: %.2f)"
            .formatted(host.getId(), getOverUtilizationThreshold(host), host.getCpuPercentUtilization());
    }

    /// Gets the difference between the power consumption after and before the supposed placement of a VM into a given Host.
    ///
    /// @param host the host to check the power consumption
    /// @param vm the candidate vm
    /// @return the host power consumption difference (in Watts) after the supposed VM placement;
    /// or 0 if the power consumption could not be determined
    protected double powerDiffAfterAllocation(final Host host, final Vm vm){
        final double powerAfterAllocation = getPowerAfterAllocation(host, vm);
        if (powerAfterAllocation > 0) {
            return powerAfterAllocation - plan.power(host);
        }

        return 0;
    }

    /// Checks if a Host has enough resources for a candidate VM and isn't overloaded.
    /// The overload is computed from the MIPS requested by the Host's VMs and by the VMs
    /// already planned to migrate into it by the current check, without the candidate VM's MIPS.
    ///
    /// @param host the host to verify
    /// @param vm the candidate vm
    /// @return true if the Host is suitable for the VM and isn't overloaded; false otherwise
    private boolean isNotHostOverloadedAfterAllocation(final Host host, final Vm vm) {
        return plan.isSuitable(host, vm) && !isHostOverloaded(host, getHostCpuPercentRequested(host));
    }

    /// {@inheritDoc}
    /// It's based on the current CPU usage.
    /// During a migration check, it considers the migrations already planned by the check.
    @Override
    public boolean isOverloaded(final Host host) {
        return isHostOverloaded(host, plan.cpuPercentUtilization(host));
    }

    /// Checks if a Host is overloaded based on the given CPU utilization percent (between 0 and 1).
    /// @param host the Host to check
    /// @param cpuUsagePercent the Host's CPU utilization percent. The values may be:
    ///
    /// - the current CPU utilization, in case you want to check if the Host is overloaded right now;
    /// - the requested CPU utilization after the supposed placement of a VM into the Host,
    ///   just to check if it supports that VM without being overloaded.
    ///
    /// @return true if the Host is overloaded, false otherwise
    private boolean isHostOverloaded(final Host host, final double cpuUsagePercent){
        return cpuUsagePercent > getOverUtilizationThreshold(host);
    }

    /// Checks if a host is under-utilized, based on the MIPS its VMs request.
    /// During a migration check, it considers the migrations already planned by the check.
    ///
    /// @param host the host to check
    /// @return true, if the host is underloaded; false otherwise
    @Override
    public boolean isUnderloaded(final Host host) {
        return getHostCpuPercentRequested(host) < getUnderUtilizationThreshold();
    }

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        return findHostForVm(vm, host -> true);
    }

    /// Finds a Host that has enough resources to place a given VM and that will not
    /// be overloaded after the placement. The selected Host will be that
    /// one with the most efficient power usage for the given VM.
    ///
    /// This method performs the basic filtering and delegates additional ones
    /// and the final selection of the Host to other method.
    ///
    /// @param vm the VM to find a host for
    /// @param predicate an additional [Predicate] to be used to filter the Host to place the VM,
    /// or `host -> true` if no additional filtering is needed.
    /// @return an [Optional] containing a suitable Host to place the VM, or an empty [Optional] if not found
    /// @see #findHostForVmInternal(Vm, Predicate)
    private Optional<Host> findHostForVm(final Vm vm, final Predicate<Host> predicate) {
        final var newPredicate =
            predicate
                .and(host -> !host.equals(vm.getHost()))
                .and(host -> isNotHostOverloadedAfterAllocation(host, vm));

        return findHostForVmInternal(vm, newPredicate);
    }

    /// Applies additional filters to the Hosts Stream and performs the actual Host selection.
    /// It can be overridden by subclasses to change the way to select the Host for a given VM.
    ///
    /// @param vm the VM to find a Host to be placed into
    /// @param predicate a [Predicate] to filter suitable Hosts
    /// @return an [Optional] containing a suitable Host to place the VM, or an empty [Optional] if not found
    /// @see #findHostForVm(Vm, Predicate)
    protected Optional<Host> findHostForVmInternal(final Vm vm, final Predicate<Host> predicate){
        final Comparator<Host> powerConsumptionComparator = comparingDouble(host -> powerDiffAfterAllocation(host, vm));
        return getHostList().stream().filter(predicate).min(powerConsumptionComparator);
    }

    /// {@return the host list from a migration map}
    /// @param migrationMap the migration map
    private List<Host> extractHostListFromMigrationMap(final Map<Vm, Host> migrationMap) {
        return new ArrayList<>(migrationMap.values());
    }

    /// Gets a new VM placement considering the list of VMs to migrate from overloaded Hosts.
    ///
    /// @param overloadedHosts the list of overloaded Hosts
    /// @return the new VM placement map where each key is a VM
    /// and each value is the Host to place it, in the order the migrations were planned;
    /// an empty map if no suitable target Hosts were found
    /// or if there is no overloaded host.
    /// The map is mutable, since the VMs from underloaded Hosts are added to it later.
    private Map<Vm, Host> getMigrationMapFromOverloadedHosts(final Set<Host> overloadedHosts) {
        final var migrationMap = new LinkedHashMap<Vm, Host>();
        if(overloadedHosts.isEmpty()) {
            return migrationMap;
        }

        final var vmsToMigrateList = getVmsToMigrateFromOverloadedHosts(overloadedHosts);
        sortByCpuUtilization(vmsToMigrateList, getDatacenter().getSimulation().clock());

        final var builder = new StringBuilder();
        for (final var vm : vmsToMigrateList) {
            findTargetHostForVmFromOverloadedHost(vm).ifPresentOrElse(
                targetHost -> {
                    addVmToMigrationMap(migrationMap, vm, targetHost);
                    appendVmMigrationMsgToStringBuilder(builder, vm, targetHost);
                },
                () -> plan.removeVmOut(vm));
        }

        if(!migrationMap.isEmpty()) {
            LOGGER.info(
                "{}: {}: Reallocation of VMs from overloaded hosts: {}{}",
                getDatacenter().getSimulation().clockStr(), getClass().getSimpleName(), System.lineSeparator(), builder);
        }

        return migrationMap;
    }

    /// Finds a target Host for a VM selected to leave an overloaded Host,
    /// using the placement policy of the [#targetMigrationDc].
    /// If it's a migration policy of another datacenter, it gets the plan of this check while it searches.
    /// Any other policy, or a custom [VmPlacementPolicy#setFindHostForVmFunction(BiFunction) function],
    /// doesn't know the plan, so the Host it selects is only accepted
    /// if it's not the VM's current Host and fits the VM besides the VMs already planned to it.
    ///
    /// @param vm the VM to find a target Host for
    /// @return an [Optional] containing the target Host, or an empty one if no suitable Host was found
    private Optional<Host> findTargetHostForVmFromOverloadedHost(final Vm vm) {
        final var targetPolicy = targetMigrationDc.getVmPlacementPolicy();
        if (targetPolicy == this || !(targetPolicy instanceof VmPlacementPolicyMigrationAbstract targetMigrationPolicy)) {
            return findAcceptedTargetHost(targetPolicy, vm);
        }

        targetMigrationPolicy.setMigrationPlan(plan);
        try {
            return findAcceptedTargetHost(targetPolicy, vm);
        } finally {
            targetMigrationPolicy.setMigrationPlan(MigrationPlan.NONE);
        }
    }

    private Optional<Host> findAcceptedTargetHost(final VmPlacementPolicy targetPolicy, final Vm vm) {
        return targetPolicy.findHostForVm(vm).filter(host -> !host.equals(vm.getHost()) && plan.isSuitable(host, vm));
    }

    private void appendVmMigrationMsgToStringBuilder(final StringBuilder builder, final Vm vm, final Host targetHost) {
        if(LOGGER.isInfoEnabled()) {
            builder.append("      ").append(vm).append(" will be migrated from ")
              .append(vm.getHost()).append(" to ").append(targetHost)
              .append(System.lineSeparator());
        }
    }

    /// {@return a new vm placement for the given VMs, or an empty Map if no suitable Host was found}
    /// @param vmsToMigrate the list of VMs to migrate from the underloaded Host
    /// @param excludedHosts the list of hosts that aren't selected as destination hosts
    private Map<Vm, Host> getNewVmPlacementFromUnderloadedHost(
        final List<? extends Vm> vmsToMigrate,
        final Set<? extends Host> excludedHosts)
    {
        final var migrationMap = new LinkedHashMap<Vm, Host>();
        sortByCpuUtilization(vmsToMigrate, getDatacenter().getSimulation().clock());
        for (final Vm vm : vmsToMigrate) {
            //try to find a target Host to place a VM from an underloaded Host that is not underloaded too
            final var optionalHost = findHostForVm(vm, host -> !excludedHosts.contains(host) && !isUnderloaded(host));
            if (optionalHost.isEmpty()) {
                LOGGER.warn(
                    "{}: VmPlacementPolicy: A new Host, which isn't also underloaded or won't be overloaded, couldn't be found to migrate {}. Migration of VMs from the underloaded {} cancelled.",
                    getDatacenter().getSimulation().clockStr(), vm, vm.getHost());
                migrationMap.forEach(plan::removeMigration);
                return new LinkedHashMap<>();
            }
            addVmToMigrationMap(migrationMap, vm, optionalHost.get());
        }

        return migrationMap;
    }

    /// Sort a given list of VMs by descending order of CPU utilization.
    ///
    /// @param vmList the vm list to be sorted
    /// @param simulationTime the simulation time to get the current CPU utilization for each Vm
    private void sortByCpuUtilization(final List<? extends Vm> vmList, final double simulationTime) {
        final Comparator<Vm> comparator = comparingDouble(vm -> vm.getTotalCpuMipsUtilization(simulationTime));
        vmList.sort(comparator.reversed());
    }

    /// Adds a VM to the migration map and to the plan of the current check,
    /// so that if the same target Host is evaluated for the next VMs,
    /// the resources of the VMs already planned to it are considered.
    private <T extends Host> void addVmToMigrationMap(final Map<Vm, T> migrationMap, final Vm vm, final T targetHost) {
        plan.addMigration(vm, targetHost);
        migrationMap.put(vm, targetHost);
    }

    /// Gets the VMs to migrate from overloaded Hosts.
    ///
    /// @param overloadedHosts the List of overloaded Hosts to migrate VMs from
    /// @return the VMs to be migrated from the given hosts
    private List<Vm> getVmsToMigrateFromOverloadedHosts(final Set<Host> overloadedHosts) {
        final var vmsToMigrateList = new LinkedList<Vm>();
        for (final var host : overloadedHosts) {
            vmsToMigrateList.addAll(getVmsToMigrateFromOverloadedHost(host));
        }

        return vmsToMigrateList;
    }

    /// Selects VMs to migrate from an overloaded Host until it isn't overloaded anymore,
    /// planning each selected VM to leave the Host.
    ///
    /// @param host the overloaded Host
    /// @return the VMs selected to migrate
    private List<Vm> getVmsToMigrateFromOverloadedHost(final Host host) {
        final var vmsToMigrateList = new LinkedList<Vm>();
        final Predicate<Vm> notSelected = vm -> !plan.isMigratingOut(vm);
        while (true) {
            final var optionalVm = getVmSelectionPolicy().getVmToMigrate(host, notSelected);
            if (optionalVm.isEmpty()) {
                break;
            }

            final var vm = optionalVm.get();
            vmsToMigrateList.add(vm);
            plan.addVmOut(vm);
            if (!isOverloaded(host)) {
                break;
            }
        }

        return vmsToMigrateList;
    }

    /// Gets the VMs to migrate from an under-utilized host.
    ///
    /// @param host an underloaded host
    /// @return the vms to migrate from under-utilized host
    protected List<? extends Vm> getVmsToMigrateFromUnderUtilizedHost(final Host host) {
        return host.getMigratableVms();
    }

    /// {@return hosts that are switched off}
    protected List<Host> getSwitchedOffHosts() {
        return this.getHostList().stream()
            .filter(this::isShutdownOrFailed)
            .collect(toList());
    }

    private boolean isShutdownOrFailed(final Host host) {
        return !host.isActive() || host.isFailed();
    }

    /// Gets a Set of overloaded hosts.
    /// If a Host is overloaded, but it has VMs migrating out,
    /// then it's not included in the returned Set.
    /// That is because the outgoing VMs will take the Host out of the overload state.
    ///
    /// @return the over utilized hosts
    private Set<Host> getOverloadedHosts() {
        return this.getHostList().stream()
            .filter(this::isOverloaded)
            .filter(host -> host.getVmsMigratingOut().isEmpty())
            .collect(toCollection(LinkedHashSet::new));
    }

    /// Gets the most underloaded Host.
    /// If a Host is underloaded, but it has VMs migrating in, then it's not considered.
    /// That is because the incoming VMs will take the Host out of the underload state.
    /// Likewise, if all VMs are migrating out, nothing has to be
    /// done anymore. It has just to wait the VMs to finish the migration so that the
    /// Host can be turned off.
    ///
    /// @param excludedHosts the Hosts that have to be ignored when looking for the under utilized Host
    /// @return the most under utilized host or [Host#NULL] if no Host is found
    private Host getUnderloadedHost(final Set<? extends Host> excludedHosts) {
        return this.getHostList().stream()
            .filter(host -> !excludedHosts.contains(host))
            .filter(Host::isActive)
            .filter(this::isUnderloaded)
            .filter(host -> host.getVmsMigratingIn().isEmpty())
            .filter(this::notAllVmsAreMigratingOut)
            .min(comparingDouble(plan::cpuPercentUtilization))
            .orElse(Host.NULL);
    }

    private double getHostCpuPercentRequested(final Host host) {
        return getHostTotalRequestedMips(host) / totalMips(host);
    }

    /// {@return the total MIPS requested by all VMs inside the Host and the ones migrating into it,
    /// considering the migrations planned by the current check}
    /// @param host the Host to get the total MIPS
    private double getHostTotalRequestedMips(final Host host) {
        return hostVmsAndMigratingIn(host).mapToDouble(Vm::getTotalCpuMipsRequested).sum() + plan.cpuMipsRequestedDelta(host);
    }

    /// {@return the VMs inside a Host and the ones migrating into it}
    /// VMs migrating in are not in the Host VM list until their migration finishes.
    private static Stream<Vm> hostVmsAndMigratingIn(final Host host) {
        return Stream.concat(host.getVmList().stream(), host.getVmsMigratingIn().stream());
    }

    /// Checks if all VMs of a Host are **NOT** migrating out.
    /// In this case, the given Host will not be selected as an underloaded Host at the current moment.
    ///
    /// @param host the host to check
    /// @return true if at least one VM isn't migrating, false if all VMs are migrating
    private boolean notAllVmsAreMigratingOut(final Host host) {
        return host.getVmList().stream().anyMatch(vm -> !vm.isInMigration());
    }

    /// {@return the power consumption of a host after the supposed placement of a candidate VM,
    /// or 0 if the power consumption could not be determined}
    /// The VM is not in fact placed at the host.
    /// @param host the host to check the power consumption
    /// @param vm the candidate vm
    protected double getPowerAfterAllocation(final Host host, final Vm vm) {
        try {
            return host.getPowerModel().getPower(getMaxUtilizationAfterAllocation(host, vm));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Power consumption for {} could not be determined: {}", host, e.getMessage());
        }

        return 0;
    }

    /// Gets the max power consumption of a host after placement of a candidate
    /// VM. The VM is not in fact placed at the host. We assume that load is
    /// balanced between cores. The only restriction is: VM's max MIPS less than core's MIPS
    ///
    /// @param host the host
    /// @param vm the vm
    /// @return the power after allocation
    protected double getMaxUtilizationAfterAllocation(final Host host, final Vm vm) {
        final double requestedTotalMips = vm.getTotalCpuMipsRequested();
        final double hostUtilizationMips = getUtilizationOfCpuMips(host);
        final double hostPotentialMipsUse = hostUtilizationMips + requestedTotalMips;
        return hostPotentialMipsUse / totalMips(host);
    }

    /// {@return the MIPS allocated to the VMs inside a given Host and the ones migrating into it,
    /// considering the migrations planned by the current check}
    /// @param host the host to get the utilization of CPU MIPS
    protected double getUtilizationOfCpuMips(final Host host) {
        final double allocatedMips = hostVmsAndMigratingIn(host)
            .mapToDouble(vm -> additionalCpuUtilizationDuringMigration(host, vm) + host.getVmScheduler().getTotalAllocatedMipsForVm(vm))
            .sum();
        return allocatedMips + plan.allocatedMipsDelta(host);
    }

    /// {@return the additional potential CPU usage (in MIPS) the Host will use if a VM is migrating into it;
    /// 0 if the VM is not migrating}
    /// @param host the Hosts that is being computed the current utilization of CPU MIPS
    /// @param vm a VM from that Host
    private double additionalCpuUtilizationDuringMigration(final Host host, final Vm vm) {
        if (!host.getVmsMigratingIn().contains(vm)) {
            return 0;
        }

        final double maxCpuUtilization = host.getVmScheduler().getMaxCpuUsagePercentDuringOutMigration();
        final double migrationOverhead = host.getVmScheduler().getVmMigrationCpuOverhead();
        return host.getVmScheduler().getTotalAllocatedMipsForVm(vm) * maxCpuUtilization / migrationOverhead;
    }

    @Override
    public void setUnderUtilizationThreshold(final double underUtilizationThreshold) {
        if(underUtilizationThreshold <= 0 || underUtilizationThreshold >= 1){
            throw new IllegalArgumentException("Under utilization threshold must be greater than 0 and lower than 1.");
        }

        this.underUtilizationThreshold = underUtilizationThreshold;
    }

    @Override
    public final boolean isVmMigrationSupported() {
        return true;
    }
}
