/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.allocationpolicies.migration;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudsimplus.core.CloudInformationService;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostAbstract;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.util.TimeUtil;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmAbstract;
import org.cloudsimplus.vms.VmSimple;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.*;

/**
 * An abstract VM allocation policy that dynamically optimizes the
 * VM allocation (placement) using migration.
 * <b>It's a Best Fit policy that selects the Host with most efficient power usage to place a given VM.</b>
 * Such a behaviour can be overridden by subclasses.
 *
 * <p>Any {@link VmAllocationPolicyMigration} implementation must be based on this class.</p>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 3.0
 */
@Accessors
public non-sealed abstract class VmAllocationPolicyMigrationAbstract
    extends VmAllocationPolicyAbstract
    implements VmAllocationPolicyMigration
{
    /**
     * Default CPU utilization percentage ([0..1]) that indicates a Host is underloaded.
     * @see #setUnderUtilizationThreshold(double)
     */
    public static final double DEF_UNDER_UTILIZATION_THRESHOLD = 0.35;

    /** @see VmAllocationPolicyMigration#getUnderUtilizationThreshold() */
    @Getter
    private double underUtilizationThreshold;

    /** @see #getVmSelectionPolicy() */
    @NonNull @Getter @Setter
    private VmSelectionPolicy vmSelectionPolicy;

    /** @see VmAllocationPolicyMigration#isUnderloaded() */
    @Getter
    private boolean underloaded;

    /** @see VmAllocationPolicyMigration#isOverloaded() */
    @Getter
    private boolean overloaded;

    /**
     * A map between a VM and the host where it is placed.
     */
    private final Map<VmAbstract, Host> savedAllocation;

    /**
     * The datacenter to try migrating VMs to.
     * The initial value is the {@link #getDatacenter() datacenter} this policy is linked to,
     * so that the policy tries migrating VMs inside the datacenter (inter-datacenter migration)
     * before looking for a different one.
     */
    private Datacenter targetMigrationDc;

    /**
     * The index of the datacenter from the entire {@link CloudInformationService}
     * datacenter list to try migrating VMs to, if the {@link #targetMigrationDc} has no suitable Hosts.
     * This is just used when the {@link #getDatacenter() datacenter} linked to this policy
     * doesn't have suitable Hosts for inter-datacenter VM migration. This way, a different datacenter is tried.
     */
    private int targetMigrationDcIndex;

    /**
     * Creates a VmAllocationPolicy using a {@link #DEF_UNDER_UTILIZATION_THRESHOLD default under utilization threshold}.
     *
     * @param vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy}
     *                          that defines how VMs are selected for migration
     */
    public VmAllocationPolicyMigrationAbstract(final VmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, null);
    }

    /**
     * Creates a new VmAllocationPolicy, changing the {@link Function} to select a Host for a Vm.
     * It uses a {@link #DEF_UNDER_UTILIZATION_THRESHOLD default under utilization threshold}.
     *
     * @param vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy}
     *                          that defines how VMs are selected for migration
     * @param findHostForVmFunction a {@link Function} to select a Host for a given Vm.
     *                              Passing null makes the Function to be set as the default {@link #findHostForVm(Vm)}.
     * @see VmAllocationPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
     * @see #setUnderUtilizationThreshold(double)
     */
    public VmAllocationPolicyMigrationAbstract(
        final VmSelectionPolicy vmSelectionPolicy,
        final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(findHostForVmFunction);
        this.underUtilizationThreshold = DEF_UNDER_UTILIZATION_THRESHOLD;
        this.savedAllocation = new HashMap<>();
        setVmSelectionPolicy(vmSelectionPolicy);
    }

    @Override
    public VmAllocationPolicyAbstract setDatacenter(final Datacenter datacenter) {
        super.setDatacenter(datacenter);
        this.targetMigrationDc = datacenter;
        return this;
    }

    @Override
    public Map<Vm, Host> getOptimizedAllocationMap(final List<? extends Vm> vmList) {
        //@TODO See https://github.com/cloudsimplus/cloudsimplus/issues/94
        final var overloadedHosts = getOverloadedHosts();
        this.overloaded = !overloadedHosts.isEmpty();
        printOverUtilizedHosts(overloadedHosts);

        final Map<Vm, Host> migrationMap = getMigrationMapFromOverloadedHosts(overloadedHosts);
        updateMigrationMapFromUnderloadedHosts(overloadedHosts, migrationMap);

        if (overloaded && migrationMap.isEmpty()) {
            hostSearchRetry();
        }

        return migrationMap;
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

    /**
     * Updates the  map of VMs that will be migrated from under utilized hosts.
     *
     * @param overloadedHosts the List of over utilized hosts
     * @param migrationMap current migration map that will be updated
     */
    private void updateMigrationMapFromUnderloadedHosts(
        final Set<Host> overloadedHosts,
        final Map<Vm, Host> migrationMap)
    {
        final var switchedOffHosts = getSwitchedOffHosts();

        // overloaded hosts + hosts that are selected to migrate VMs from overloaded hosts
        final var ignoredSourceHosts = getIgnoredHosts(overloadedHosts, switchedOffHosts);

        /*
        During the computation of the new placement for VMs,
        the current VM placement is changed temporarily, before the actual migration of VMs.
        If VMs are being migrated from overloaded Hosts, they in fact already were removed
        from such Hosts and moved to destination ones.
        The target Host that maybe was shut down, might become underloaded too.
        This way, such Hosts are added to be ignored when
        looking for underloaded Hosts.
        See https://github.com/cloudsimplus/cloudsimplus/issues/94
         */
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

            LOGGER.info("{}: VmAllocationPolicy: Underloaded hosts: {}", getDatacenter().getSimulation().clockStr(), underloadedHost);

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
            LOGGER.info("{}: VmAllocationPolicy: VMs to be reallocated from the underloaded {}: {}",
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

    /**
     * Prints the over-utilized hosts.
     *
     * @param overloadedHosts the over-utilized hosts
     */
    private void printOverUtilizedHosts(final Set<Host> overloadedHosts) {
        if (!overloadedHosts.isEmpty() && LOGGER.isWarnEnabled()) {
            final String hosts = overloadedHosts.stream().map(this::overloadedHostToString).collect(joining(System.lineSeparator()));
            LOGGER.warn("{}: VmAllocationPolicy: Overloaded hosts in {}:{}{}",
                getDatacenter().getSimulation().clockStr(), getDatacenter(), System.lineSeparator(), hosts);
        }
    }

    private String overloadedHostToString(final Host host) {
        return
            "      Host %d (upper CPU threshold %.2f, current utilization: %.2f)"
            .formatted(host.getId(), getOverUtilizationThreshold(host), host.getCpuPercentUtilization());
    }

    /**
     * Gets the difference between the power consumption after and before the supposed placement of a VM into a given Host.
     *
     * @param host the host to check the power consumption
     * @param vm the candidate vm
     * @return the host power consumption difference (in Watts) after the supposed VM placement;
     * or 0 if the power consumption could not be determined
     */
    protected double powerDiffAfterAllocation(final Host host, final Vm vm){
        final double powerAfterAllocation = getPowerAfterAllocation(host, vm);
        if (powerAfterAllocation > 0) {
            return powerAfterAllocation - host.getPowerModel().getPower();
        }

        return 0;
    }

    /**
     * Checks if a host will be over-utilized after placing a candidate VM.
     *
     * @param host the host to verify
     * @param vm the candidate vm
     * @return true, if the host will be over utilized after VM placement; false otherwise
     */
    private boolean isNotHostOverloadedAfterAllocation(final Host host, final Vm vm) {
        final var tempVm = new VmSimple(vm);

        if (!host.createTemporaryVm(tempVm).fully()) {
            return false;
        }

        final double usagePercent = getHostCpuPercentRequested(host);
        final boolean notOverloadedAfterAllocation = !isHostOverloaded(host, usagePercent);
        ((HostAbstract)host).destroyTemporaryVm(tempVm);
        return notOverloadedAfterAllocation;
    }

    /**
     * {@inheritDoc}
     * It's based on the current CPU usage.
     */
    @Override
    public boolean isOverloaded(final Host host) {
        return isHostOverloaded(host, host.getCpuPercentUtilization());
    }

    /**
     * Checks if a Host is overloaded based on the given CPU utilization percent (between 0 and 1).
     * @param host the Host to check
     * @param cpuUsagePercent the Host's CPU utilization percent. The values may be:
     *                        <ul>
     *                          <li>the current CPU utilization, in case you want to check if the Host is overloaded right now;</li>
     *                          <li>the requested CPU utilization after temporarily placing a VM into the Host,
     *                          just to check if it supports that VM without being overloaded.
     *                          In this case, if the Host doesn't support the already temporarily placed VM,
     *                          the method will return true to indicate the Host will be overloaded
     *                          if the VM is actually placed into it.
     *                          </li>
     *                        </ul>
     * @return true if the Host is overloaded, false otherwise
     */
    private boolean isHostOverloaded(final Host host, final double cpuUsagePercent){
        return cpuUsagePercent > getOverUtilizationThreshold(host);
    }

    /**
     * Checks if a host is under-utilized, based on current CPU usage.
     *
     * @param host the host to check
     * @return true, if the host is underloaded; false otherwise
     */
    @Override
    public boolean isUnderloaded(final Host host) {
        return getHostCpuPercentRequested(host) < getUnderUtilizationThreshold();
    }

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        return findHostForVm(vm, host -> true);
    }

    /**
     * Finds a Host that has enough resources to place a given VM and that will not
     * be overloaded after the placement. The selected Host will be that
     * one with the most efficient power usage for the given VM.
     *
     * <p>This method performs the basic filtering and delegates additional ones
     * and the final selection of the Host to other method.</p>
     *
     * @param vm the VM to find a host for
     * @param predicate an additional {@link Predicate} to be used to filter the Host to place the VM,
     *                  or {@code host -> true} if no additional filtering is needed.
     * @return an {@link Optional} containing a suitable Host to place the VM, or an empty {@link Optional} if not found
     * @see #findHostForVmInternal(Vm, Predicate)
     */
    private Optional<Host> findHostForVm(final Vm vm, final Predicate<Host> predicate) {
        final var newPredicate =
            predicate
                .and(host -> !host.equals(vm.getHost()))
                .and(host -> host.isSuitableForVm(vm))
                .and(host -> isNotHostOverloadedAfterAllocation(host, vm));

        return findHostForVmInternal(vm, newPredicate);
    }

    /**
     * Applies additional filters to the Hosts Stream and performs the actual Host selection.
     * It can be overridden by subclasses to change the way to select the Host for a given VM.
     *
     * @param vm the VM to find a Host to be placed into
     * @param predicate a {@link Predicate} to filter suitable Hosts
     * @return an {@link Optional} containing a suitable Host to place the VM, or an empty {@link Optional} if not found
     * @see #findHostForVm(Vm, Predicate)
     */
    protected Optional<Host> findHostForVmInternal(final Vm vm, final Predicate<Host> predicate){
        final Comparator<Host> powerConsumptionComparator = comparingDouble(host -> powerDiffAfterAllocation(host, vm));
        return getHostList().stream().filter(predicate).min(powerConsumptionComparator);
    }

    /**
     * {@return the host list from a migration map}
     * @param migrationMap the migration map
     */
    private List<Host> extractHostListFromMigrationMap(final Map<Vm, Host> migrationMap) {
        return new ArrayList<>(migrationMap.values());
    }

    /**
     * Gets a new VM placement considering the list of VMs to migrate from overloaded Hosts.
     *
     * @param overloadedHosts the list of overloaded Hosts
     * @return the new VM placement map where each key is a VM
     * and each value is the Host to place it;
     * an empty map if no suitable target Hosts were found
     * or if there is no overloaded host.
     * TODO: See issue in {@link #getVmsToMigrateFromOverloadedHost(Host)}
     */
    private Map<Vm, Host> getMigrationMapFromOverloadedHosts(final Set<Host> overloadedHosts) {
        if(overloadedHosts.isEmpty()) {
            return Collections.emptyMap();
        }

        saveAllocation();
        final var migrationMap = new HashMap<Vm, Host>();
        try {
            final var vmsToMigrateList = getVmsToMigrateFromOverloadedHosts(overloadedHosts);
            sortByCpuUtilization(vmsToMigrateList, getDatacenter().getSimulation().clock());

            final var builder = new StringBuilder();
            final var targetVmAllocationPolicy = targetMigrationDc.getVmAllocationPolicy();
            for (final var vm : vmsToMigrateList) {
                targetVmAllocationPolicy.findHostForVm(vm).ifPresent(targetHost -> {
                    addVmToMigrationMap(migrationMap, vm, targetHost);
                    appendVmMigrationMsgToStringBuilder(builder, vm, targetHost);
                });
            }

            if(!migrationMap.isEmpty()) {
                LOGGER.info(
                    "{}: {}: Reallocation of VMs from overloaded hosts: {}{}",
                    getDatacenter().getSimulation().clockStr(), getClass().getSimpleName(), System.lineSeparator(), builder);
            }
        } finally {
            restoreAllocation();
        }

        return migrationMap;
    }

    private void appendVmMigrationMsgToStringBuilder(final StringBuilder builder, final Vm vm, final Host targetHost) {
        if(LOGGER.isInfoEnabled()) {
            builder.append("      ").append(vm).append(" will be migrated from ")
              .append(vm.getHost()).append(" to ").append(targetHost)
              .append(System.lineSeparator());
        }
    }

    /**
     * {@return a new vm placement for the given VMs, or an empty Map if no suitable Host was found}
     * @param vmsToMigrate the list of VMs to migrate from the underloaded Host
     * @param excludedHosts the list of hosts that aren't selected as destination hosts
     */
    private Map<Vm, Host> getNewVmPlacementFromUnderloadedHost(
        final List<? extends Vm> vmsToMigrate,
        final Set<? extends Host> excludedHosts)
    {
        final var migrationMap = new HashMap<Vm, Host>();
        sortByCpuUtilization(vmsToMigrate, getDatacenter().getSimulation().clock());
        for (final Vm vm : vmsToMigrate) {
            //try to find a target Host to place a VM from an underloaded Host that is not underloaded too
            final var optionalHost = findHostForVm(vm, host -> !isUnderloaded(host));
            if (optionalHost.isEmpty()) {
                LOGGER.warn(
                    "{}: VmAllocationPolicy: A new Host, which isn't also underloaded or won't be overloaded, couldn't be found to migrate {}. Migration of VMs from the underloaded {} cancelled.",
                    getDatacenter().getSimulation().clockStr(), vm, vm.getHost());
                return new HashMap<>();
            }
            addVmToMigrationMap(migrationMap, vm, optionalHost.get());
        }

        return migrationMap;
    }

    /**
     * Sort a given list of VMs by descending order of CPU utilization.
     *
     * @param vmList the vm list to be sorted
     * @param simulationTime the simulation time to get the current CPU utilization for each Vm
     */
    private void sortByCpuUtilization(final List<? extends Vm> vmList, final double simulationTime) {
        final Comparator<Vm> comparator = comparingDouble(vm -> vm.getTotalCpuMipsUtilization(simulationTime));
        vmList.sort(comparator.reversed());
    }

    private <T extends Host> void addVmToMigrationMap(final Map<Vm, T> migrationMap, final Vm vm, final T targetHost) {
        /*
        Temporarily creates the VM into the target Host so that
        when the next VM is got to be migrated, if the same Host
        is selected as destination, the resource to be
        used by the previous VM will be considered when
        assessing the suitability of such a Host for the next VM.
         */
        targetHost.createTemporaryVm(vm);
        migrationMap.put(vm, targetHost);
    }

    /**
     * Gets the VMs to migrate from overloaded Hosts.
     *
     * @param overloadedHosts the List of overloaded Hosts to migrate VMs from
     * @return the VMs to be migrated from the given hosts
     */
    private List<Vm> getVmsToMigrateFromOverloadedHosts(final Set<Host> overloadedHosts) {
        final var vmsToMigrateList = new LinkedList<Vm>();
        for (final var host : overloadedHosts) {
            vmsToMigrateList.addAll(getVmsToMigrateFromOverloadedHost(host));
        }

        return vmsToMigrateList;
    }

    private List<Vm> getVmsToMigrateFromOverloadedHost(final Host host) {
        /*
        @TODO The method doesn't just gets a list of VMs to migrate from an overloaded Host,
        but it temporarily destroys VMs on such Hosts.
        See https://github.com/cloudsimplus/cloudsimplus/issues/94
        */
        final var vmsToMigrateList = new LinkedList<Vm>();
        while (true) {
            final var optionalVm = getVmSelectionPolicy().getVmToMigrate(host);
            if (optionalVm.isEmpty()) {
                break;
            }

            final var vm = optionalVm.get();
            vmsToMigrateList.add(vm);
            /*Temporarily destroys the selected VM into the overloaded Host so that
            the loop gets VMs from such a Host until it is not overloaded anymore.*/
            ((HostAbstract)host).destroyTemporaryVm(vm);
            if (!isOverloaded(host)) {
                break;
            }
        }

        return vmsToMigrateList;
    }

    /**
     * Gets the VMs to migrate from an under-utilized host.
     *
     * @param host an underloaded host
     * @return the vms to migrate from under-utilized host
     */
    protected List<? extends Vm> getVmsToMigrateFromUnderUtilizedHost(final Host host) {
        return host.getMigratableVms();
    }

    /**
     * {@return hosts that are switched off}
     */
    protected List<Host> getSwitchedOffHosts() {
        return this.getHostList().stream()
            .filter(this::isShutdownOrFailed)
            .collect(toList());
    }

    private boolean isShutdownOrFailed(final Host host) {
        return !host.isActive() || host.isFailed();
    }

    /**
     * Gets a Set of overloaded hosts.
     * If a Host is overloaded, but it has VMs migrating out,
     * then it's not included in the returned Set.
     * That is because the outgoing VMs will take the Host out of the overload state.
     *
     * @return the over utilized hosts
     */
    private Set<Host> getOverloadedHosts() {
        return this.getHostList().stream()
            .filter(this::isOverloaded)
            .filter(host -> host.getVmsMigratingOut().isEmpty())
            .collect(toSet());
    }

    /**
     * Gets the most underloaded Host.
     * If a Host is underloaded, but it has VMs migrating in, then it's not considered.
     * That is because the incoming VMs will take the Host out of the underload state.
     * Likewise, if all VMs are migrating out, nothing has to be
     * done anymore. It has just to wait the VMs to finish the migration so that the
     * Host can be turned off.
     *
     * @param excludedHosts the Hosts that have to be ignored when looking for the under utilized Host
     * @return the most under utilized host or {@link Host#NULL} if no Host is found
     */
    private Host getUnderloadedHost(final Set<? extends Host> excludedHosts) {
        return this.getHostList().stream()
            .filter(host -> !excludedHosts.contains(host))
            .filter(Host::isActive)
            .filter(this::isUnderloaded)
            .filter(host -> host.getVmsMigratingIn().isEmpty())
            .filter(this::notAllVmsAreMigratingOut)
            .min(comparingDouble(Host::getCpuPercentUtilization))
            .orElse(Host.NULL);
    }

    private double getHostCpuPercentRequested(final Host host) {
        return getHostTotalRequestedMips(host) / host.getTotalMipsCapacity();
    }

    /**
     * {@return the total MIPS that is currently being used by all VMs inside the Host}
     * @param host the Host to get the total MIPS
     */
    private double getHostTotalRequestedMips(final Host host) {
        return host.getVmList().stream()
            .mapToDouble(Vm::getTotalCpuMipsRequested)
            .sum();
    }

    /**
     * Checks if all VMs of a Host are <b>NOT</b> migrating out.
     * In this case, the given Host will not be selected as an underloaded Host at the current moment.
     *
     * @param host the host to check
     * @return true if at least one VM isn't migrating, false if all VMs are migrating
     */
    private boolean notAllVmsAreMigratingOut(final Host host) {
        return host.getVmList().stream().anyMatch(vm -> !vm.isInMigration());
    }

    /**
     * Saves the current map between a VM and the host where it is placed.
     *
     * @see #savedAllocation
     */
    private void saveAllocation() {
        savedAllocation.clear();
        for (final var host : getHostList()) {
            for (final var vm : host.<VmAbstract>getVmList()) {
                /* TODO: this VM loop has a quadratic wost-case complexity (when
                    all Vms already in the VM list are migrating into this Host).
                *  Instead of looping over the vmsMigratingIn list for every VM,
                *  we could add a Host migratingIn attribute to the Vm.
                * Then, for every VM on the Host, we check this VM attribute
                * to see if the VM is migrating into the Host. */
                if (!host.getVmsMigratingIn().contains(vm)) {
                    savedAllocation.put(vm, host);
                }
            }
        }
    }

    /**
     * Restore VM allocation from the allocation history.
     * TODO: The allocation map only needs to be restored because
     * VMs are destroyed in order to assess a new VM placement.
     * After fixing this issue, there will be no need to restore VM mapping.
     * https://github.com/cloudsimplus/cloudsimplus/issues/94
     *
     * @see #savedAllocation
     */
    private void restoreAllocation() {
        for (final var host : this.<HostAbstract>getHostList()) {
            host.destroyAllVms();
            host.reallocateMigratingInVms();
        }

        for (final var vm : savedAllocation.keySet()) {
            final var host = savedAllocation.get(vm);
            if (host.createTemporaryVm(vm).fully())
                vm.setCreated(true);
            else LOGGER.error("VmAllocationPolicy: Couldn't restore {} on {}", vm, host);
        }
    }

    /**
     * {@return the power consumption of a host after the supposed placement of a candidate VM,
     * or 0 if the power consumption could not be determined}
     * The VM is not in fact placed at the host.
     * @param host the host to check the power consumption
     * @param vm the candidate vm
     *
     */
    protected double getPowerAfterAllocation(final Host host, final Vm vm) {
        try {
            return host.getPowerModel().getPower(getMaxUtilizationAfterAllocation(host, vm));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Power consumption for {} could not be determined: {}", host, e.getMessage());
        }

        return 0;
    }

    /**
     * Gets the max power consumption of a host after placement of a candidate
     * VM. The VM is not in fact placed at the host. We assume that load is
     * balanced between PEs. The only restriction is: VM's max MIPS less than PE's MIPS
     *
     * @param host the host
     * @param vm the vm
     * @return the power after allocation
     */
    protected double getMaxUtilizationAfterAllocation(final Host host, final Vm vm) {
        final double requestedTotalMips = vm.getTotalCpuMipsRequested();
        final double hostUtilizationMips = getUtilizationOfCpuMips(host);
        final double hostPotentialMipsUse = hostUtilizationMips + requestedTotalMips;
        return hostPotentialMipsUse / host.getTotalMipsCapacity();
    }

    /**
     * {@return the utilization of the CPU in MIPS for the current potentially allocated VMs in a given host}
     * @param host the host to get the utilization of CPU MIPS
     */
    protected double getUtilizationOfCpuMips(final Host host) {
        double hostUtilizationMips = 0;
        for (final var vm : host.getVmList()) {
            final double additionalMips = additionalCpuUtilizationDuringMigration(host, vm);
            hostUtilizationMips += additionalMips + host.getTotalAllocatedMipsForVm(vm);
        }

        return hostUtilizationMips;
    }

    /**
     * {@return the additional potential CPU usage (in MIPS) the Host will use if a VM is migrating into it;
     * 0 if the VM is not migrating}
     * @param host the Hosts that is being computed the current utilization of CPU MIPS
     * @param vm a VM from that Host
     */
    private double additionalCpuUtilizationDuringMigration(final Host host, final Vm vm) {
        if (!host.getVmsMigratingIn().contains(vm)) {
            return 0;
        }

        final double maxCpuUtilization = host.getVmScheduler().getMaxCpuUsagePercentDuringOutMigration();
        final double migrationOverhead = host.getVmScheduler().getVmMigrationCpuOverhead();
        return host.getTotalAllocatedMipsForVm(vm) * maxCpuUtilization / migrationOverhead;
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
