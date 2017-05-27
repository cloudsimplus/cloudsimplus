/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies.power;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostDynamicWorkload;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.Simulation;

/**
 * An abstract power-aware VM allocation policy that dynamically optimizes the
 * VM allocation (placement) using migration.
 * <b>It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.</b>
 * Such a behaviour can be overridden by sub-classes.
 *
 * <p>If you are using any algorithms, policies or workload included in the
 * power package please cite the following paper:
 *
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and
 * Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of
 * Virtual Machines in Cloud Data Centers", Concurrency and Computation:
 * Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John
 * Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 * </p>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public abstract class PowerVmAllocationPolicyMigrationAbstract extends PowerVmAllocationPolicyAbstract
    implements PowerVmAllocationPolicyMigration {

    /**@see #getUnderUtilizationThreshold() */
    private double underUtilizationThreshold;

    /**
     * The vm selection policy.
     */
    private PowerVmSelectionPolicy vmSelectionPolicy;

    /**
     * A map between a VM and the host where it is placed.
     */
    private final Map<Vm, Host> savedAllocation;

    /**
     * A map of CPU utilization history (in percentage) for each host, where
     * each key is a hos and each value is the CPU utilization percentage history.
     *
     * @todo this value is duplicated from
     * such as the {@link PowerHostUtilizationHistory}.
     */
    private final Map<Host, List<Double>> utilizationHistory;

    /**
     * @see #getMetricHistory()
     */
    private final Map<Host, List<Double>> metricHistory;

    /**
     * @see #getTimeHistory()
     */
    private final Map<Host, List<Double>> timeHistory;

    /**
     * Creates a PowerVmAllocationPolicyMigrationAbstract.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     */
    public PowerVmAllocationPolicyMigrationAbstract(PowerVmSelectionPolicy vmSelectionPolicy) {
        super();
        this.underUtilizationThreshold = 0.35;
        this.savedAllocation = new HashMap<>();
        this.utilizationHistory = new HashMap<>();
        this.metricHistory = new HashMap<>();
        this.timeHistory = new HashMap<>();
        setVmSelectionPolicy(vmSelectionPolicy);
    }

    @Override
    public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList) {
        final List<PowerHostUtilizationHistory> overloadedHosts = getOverUtilizedHosts();
        printOverUtilizedHosts(overloadedHosts);
        saveAllocation();
        final List<Vm> vmsToMigrate = getVmsToMigrateFromOverloadedHosts(overloadedHosts);
        final Map<Vm, Host> migrationMap = getMigrationMapFromOverloadedHosts(vmsToMigrate, new HashSet<>(overloadedHosts));
        migrationMap.putAll(getMigrationMapFromUnderloadedHosts(overloadedHosts));
        restoreAllocation();
        return migrationMap;
    }

    /**
     * Gets the migration map from under utilized hosts.
     *
     * @param overUtilizedHosts the over utilized hosts
     * @return the migration map from under utilized hosts
     */
    protected Map<Vm, Host> getMigrationMapFromUnderloadedHosts(
            List<PowerHostUtilizationHistory> overUtilizedHosts) {
        final Map<Vm, Host> migrationMap = new HashMap<>();
        final List<PowerHost> switchedOffHosts = getSwitchedOffHosts();

        // over-utilized hosts + hosts that are selected to migrate VMs to from over-utilized hosts
        final Set<Host> excludedHostsFromUnderUsedSearch = new HashSet<>();
        excludedHostsFromUnderUsedSearch.addAll(overUtilizedHosts);
        excludedHostsFromUnderUsedSearch.addAll(switchedOffHosts);
        excludedHostsFromUnderUsedSearch.addAll(
                extractHostListFromMigrationMap(migrationMap));

        // over-utilized + under-utilized hosts
        final Set<PowerHost> excludedHostsForFindingNewVmPlacement = new HashSet<>();
        excludedHostsForFindingNewVmPlacement.addAll(overUtilizedHosts);
        excludedHostsForFindingNewVmPlacement.addAll(switchedOffHosts);

        final int numberOfHosts = getHostList().size();

        while (true) {
            if (numberOfHosts == excludedHostsFromUnderUsedSearch.size()) {
                break;
            }

            final PowerHost underUtilizedHost = getUnderUtilizedHost(excludedHostsFromUnderUsedSearch);
            if (underUtilizedHost == PowerHost.NULL) {
                break;
            }

            Log.printFormattedLine("%.2f: PowerVmAllocationPolicy: Underloaded hosts: %s", getDatacenter().getSimulation().clock(),  underUtilizedHost);

            excludedHostsFromUnderUsedSearch.add(underUtilizedHost);
            excludedHostsForFindingNewVmPlacement.add(underUtilizedHost);

            List<? extends Vm> vmsToMigrateFromUnderUsedHost = getVmsToMigrateFromUnderUtilizedHost(underUtilizedHost);
            if (!vmsToMigrateFromUnderUsedHost.isEmpty()) {
                Log.printFormatted("\tVMs to be reallocated from the underloaded Host %d: ", underUtilizedHost.getId());
                printVmIds(vmsToMigrateFromUnderUsedHost);

                final Map<Vm, Host> newVmPlacement = getNewVmPlacementFromUnderUtilizedHost(
                        vmsToMigrateFromUnderUsedHost,
                        excludedHostsForFindingNewVmPlacement);

                excludedHostsFromUnderUsedSearch.addAll(extractHostListFromMigrationMap(newVmPlacement));
                migrationMap.putAll(newVmPlacement);
                Log.printLine();
            }
        }

        return migrationMap;
    }

    private void printVmIds(List<? extends Vm> vmList) {
        if (!Log.isDisabled()) {
            vmList.forEach(vm -> Log.printFormatted("Vm %d ", vm.getId()));
            Log.printLine();
        }
    }

    /**
     * Prints the over utilized hosts.
     *
     * @param overloadedHosts the over utilized hosts
     */
    protected void printOverUtilizedHosts(List<PowerHostUtilizationHistory> overloadedHosts) {
        if (!Log.isDisabled() && !overloadedHosts.isEmpty()) {
            Log.printFormattedLine("%.2f: PowerVmAllocationPolicy: Overloaded hosts: %s", getDatacenter().getSimulation().clock(), overloadedHosts);
        }
    }

    /**
     * Gets the power consumption different after the supposed placement of a VM into a given Host
     * and the original Host power consumption.
     *
     * @param host the host to check the power consumption
     * @param vm the candidate vm
     * @return the host power consumption different after the supposed VM placement or 0 if the power
     * consumption could not be determined
     */
    protected double getPowerAfterAllocationDifference(PowerHost host, Vm vm){
        final double powerAfterAllocation = getPowerAfterAllocation(host, vm);
        if (powerAfterAllocation > 0) {
            return powerAfterAllocation - host.getPower();
        }

        return 0;
    }

    /**
     * Checks if a host will be over utilized after placing of a candidate VM.
     *
     * @param host the host to verify
     * @param vm the candidate vm
     * @return true, if the host will be over utilized after VM placement; false
     * otherwise
     */
    protected boolean isNotHostOverusedAfterAllocation(PowerHost host, Vm vm) {
        boolean isHostOverUsedAfterAllocation = true;
        if (host.vmCreate(vm)) {
            isHostOverUsedAfterAllocation = isHostOverloaded(host);
            host.destroyVm(vm);
        }
        return !isHostOverUsedAfterAllocation;
    }

    @Override
    public PowerHost findHostForVm(Vm vm) {
        final Set<Host> excludedHosts = new HashSet<>();
        excludedHosts.add(vm.getHost());
        return findHostForVm(vm, excludedHosts);
    }

    /**
     * Finds a Host that has enough resources to place a given VM and that will not
     * be overloaded after the placement. The selected Host will be that
     * one with most efficient power usage for the given VM.
     *
     * <p>This method performs the basic filtering and delegates additional ones
     * and the final selection of the Host to other method.</p>
     *
     * @param vm the VM
     * @param excludedHosts the excluded hosts
     * @return the PM found to host the VM or {@link PowerHost#NULL} if not found
     * @see #findHostForVmInternal(Vm, Stream)
     */
    public PowerHost findHostForVm(final Vm vm, final Set<? extends Host> excludedHosts) {
        final Stream<PowerHost> stream = this.<PowerHost>getHostList().stream()
            .filter(h -> !excludedHosts.contains(h))
            .filter(h -> h.isSuitableForVm(vm))
            .filter(h -> isNotHostOverusedAfterAllocation(h, vm));

        return findHostForVmInternal(vm, stream).orElse(PowerHost.NULL);
    }

    /**
     * Applies additional filters to the Hosts Stream and performs the actual Host selection.
     * This method is a Stream's final operation, that it, it closes the Stream and returns an {@link Optional} value.
     *
     * <p>This method can be overridden by sub-classes to change the method used to select the Host for the given VM.</p>
     *
     * @param vm the VM to find a Host to be placed into
     * @param hostStream a {@link Stream} containing the Hosts after passing the basic filtering
     * @return an {@link Optional} that may or may not contain the Host to place the VM
     * @see #findHostForVm(Vm, Set)
     * @see #additionalHostFilters(Vm, Stream)
     */
    protected Optional<PowerHost> findHostForVmInternal(final Vm vm, final Stream<PowerHost> hostStream){
        final Comparator<PowerHost> hostPowerConsumptionComparator =
            Comparator.comparingDouble(h -> getPowerAfterAllocationDifference(h, vm));

        return additionalHostFilters(vm, hostStream).min(hostPowerConsumptionComparator);
    }

    /**
     * Applies additional filters to select a Host to place a given VM.
     * This implementation filters the stream of Hosts to get those ones
     * that the placement of the VM impacts its power usage.
     *
     * <p>This method can be overridden by sub-classes to change filtering.</p>
     *
     * @param vm the VM to find a Host to be placed into
     * @param hostStream a {@link Stream} containing the Hosts after passing the basic filtering
     * @return the Hosts {@link Stream} after applying the additional filters
     */
    protected Stream<PowerHost> additionalHostFilters(final Vm vm, final Stream<PowerHost> hostStream){
        return hostStream.filter(h -> getPowerAfterAllocation(h, vm) > 0);
    }

    /**
     * Extracts the host list from a migration map.
     *
     * @param migrationMap the migration map
     * @return the list
     */
    protected List<Host> extractHostListFromMigrationMap(Map<Vm, Host> migrationMap) {
        return migrationMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Gets a new VM placement considering the list of VM to migrate
     * from overloaded Hosts.
     *
     * @param vmsToMigrate the list of VMs to migrate from overloaded Hosts.
     * @param overloadedHosts the list of overloaded Hosts
     * @return the new VM placement map where each key is a VM
     * and each value is the Host to place it.
     */
    protected Map<Vm, Host> getMigrationMapFromOverloadedHosts(
        final List<Vm> vmsToMigrate, final Set<Host> overloadedHosts)
    {
        final Map<Vm, Host> migrationMap = new HashMap<>();
        if(overloadedHosts.isEmpty()) {
            return migrationMap;
        }

        Log.printLine("\tReallocation of VMs from overloaded hosts: ");
        VmList.sortByCpuUtilization(vmsToMigrate, getDatacenter().getSimulation().clock());
        for (final Vm vm : vmsToMigrate) {
            final PowerHost allocatedHost = findHostForVm(vm, overloadedHosts);
            if (allocatedHost != PowerHost.NULL) {
                Log.printConcatLine("\tVM #", vm.getId(), " allocated to host #", allocatedHost.getId());
                migrationMap.put(vm, allocatedHost);
            }
        }
        Log.printLine();

        return migrationMap;
    }

    /**
     * Gets the new vm placement from under utilized host.
     *
     * @param vmsToMigrate the list of VMs to migrate
     * @param excludedHosts the list of hosts that aren't selected as
     * destination hosts
     * @return the new vm placement from under utilized host
     */
    protected Map<Vm, Host> getNewVmPlacementFromUnderUtilizedHost(
            final List<? extends Vm> vmsToMigrate,
            final Set<? extends Host> excludedHosts)
    {
        final Map<Vm, Host> migrationMap = new HashMap<>();
        VmList.sortByCpuUtilization(vmsToMigrate, getDatacenter().getSimulation().clock());
        for (final Vm vm : vmsToMigrate) {
            final PowerHost allocatedHost = findHostForVm(vm, excludedHosts);
            if (!PowerHost.NULL.equals(allocatedHost )) {
                allocatedHost.vmCreate(vm);
                Log.printConcatLine("VM #", vm.getId(), " allocated to host #", allocatedHost.getId());
                migrationMap.put(vm, allocatedHost);
            } else {
                Log.printFormattedLine("\tA new suitable Host couldn't be found for %s. Reallocation cancelled.", vm);
                migrationMap.entrySet().forEach(e -> e.getValue().destroyVm(e.getKey()));
                return new HashMap<>();
            }
        }

        return migrationMap;
    }

    /**
     * Gets the VMs to migrate from Hosts
     * and destroys such VMs into these Hosts.
     *
     * @param overloadedHosts the List of overloaded Hosts
     * @return the VMs to migrate from hosts
     */
    protected List<Vm> getVmsToMigrateFromOverloadedHosts(List<PowerHostUtilizationHistory> overloadedHosts) {
        final List<Vm> vmsToMigrate = new LinkedList<>();
        for (final PowerHostUtilizationHistory host : overloadedHosts) {
            while (true) {
                final Vm vm = getVmSelectionPolicy().getVmToMigrate(host);
                if (Vm.NULL.equals(vm)) {
                    break;
                }
                vmsToMigrate.add(vm);
                host.destroyVm(vm);
                if (!isHostOverloaded(host)) {
                    break;
                }
            }
        }

        return vmsToMigrate;
    }

    /**
     * Gets the VMs to migrate from under utilized host.
     *
     * @param host the host
     * @return the vms to migrate from under utilized host
     */
    protected List<? extends Vm> getVmsToMigrateFromUnderUtilizedHost(PowerHost host) {
        return host.getVmList().stream()
            .filter(vm -> !vm.isInMigration())
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Gets the over utilized hosts.
     *
     * @return the over utilized hosts
     */
    protected List<PowerHostUtilizationHistory> getOverUtilizedHosts() {
        return this.<PowerHostUtilizationHistory>getHostList().stream()
            .filter(this::isHostOverloaded)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Gets the switched off hosts.
     *
     * @return the switched off hosts
     */
    protected List<PowerHost> getSwitchedOffHosts() {
        return this.<PowerHost>getHostList().stream()
            .filter(host -> host.getUtilizationOfCpu() == 0)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Gets the most under utilized Host.
     *
     * @param excludedHosts the Hosts that have to be disconsidering when looking for the under utilized Host
     * @return the most under utilized host or {@link PowerHost#NULL} if no Host is found
     */
    private PowerHost getUnderUtilizedHost(Set<? extends Host> excludedHosts) {
        return this.<PowerHost>getHostList().stream()
            .filter(h -> !excludedHosts.contains(h))
            .filter(h -> h.getUtilizationOfCpu() > 0)
            .filter(this::isHostUnderUtilized)
            .filter(h -> areNotAllVmsMigratingOutNeitherAreVmsMigratingIn(h))
            .min(Comparator.comparingDouble(HostDynamicWorkload::getUtilizationOfCpu))
            .orElse(PowerHost.NULL);
    }

    /**
     * Checks if a host is under utilized, based on current CPU usage.
     *
     * @param host the host
     * @return true, if the host is under utilized; false otherwise
     */
    @Override
    public boolean isHostUnderUtilized(PowerHost host) {
        return getHostCpuUtilizationPercentage(host) < getUnderUtilizationThreshold();
    }

    /**
     * {@inheritDoc}
     * It's based on current CPU usage.
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isHostOverloaded(PowerHost host) {
        final double upperThreshold = getOverUtilizationThreshold(host);
        addHistoryEntryIfAbsent(host, upperThreshold);

        return getHostCpuUtilizationPercentage(host) > upperThreshold;
    }

    private double getHostCpuUtilizationPercentage(PowerHost host) {
        return getHostTotalRequestedMips(host) / host.getTotalMipsCapacity();
    }

    /**
     * Gets the total MIPS that is currently being used by all VMs inside the Host.
     * @param host
     * @return
     */
    private double getHostTotalRequestedMips(PowerHost host) {
        return host.getVmList().stream()
            .mapToDouble(Vm::getCurrentRequestedTotalMips)
            .sum();
    }

    /**
     * Checks if all VMs of a Host are <b>NOT</b> migrating out neither there are VMs migrating in.
     * If all VMs are migrating out or there is at least
     * one VM migrating in, the given Host will not be selected as an underutilized Host at the current moment.
     *
     * @param host the host to check
     * @return
     */
    protected boolean areNotAllVmsMigratingOutNeitherAreVmsMigratingIn(PowerHost host) {
        for (final PowerVm vm : host.<PowerVm>getVmList()) {
            if (!vm.isInMigration()) { //VM is not in migration process (in or out)
                //there is at least one VM that is not migrating anywhere (nor ir or out)
                return true;
            }

            //If the VM is in migration process, checks if it is migrating into the host.
            //If it is not contained into the migratingIn list, it is migrating out.
            if (host.getVmsMigratingIn().contains(vm)) {
                //there is at least one VM migrating into the host
                return false;
            }
        }

        //all VMs are migrating out
        return false;
    }

    /**
     * Updates the list of maps between a VM and the host where it is place.
     *
     * @see #savedAllocation
     */
    protected void saveAllocation() {
        getSavedAllocation().clear();
        for (final Host host : getHostList()) {
            for (final Vm vm : host.getVmList()) {
                if (!host.getVmsMigratingIn().contains(vm)) {
                    getSavedAllocation().put(vm, host);
                }
            }
        }
    }

    /**
     * Restore VM allocation from the allocation history.
     *
     * @see #savedAllocation
     */
    protected void restoreAllocation() {
        for (final Host host : getHostList()) {
            host.destroyAllVms();
            host.reallocateMigratingInVms();
        }

        for (final Vm vm : getSavedAllocation().keySet()) {
            final PowerHost host = (PowerHost) getSavedAllocation().get(vm);
            if (!host.vmCreate(vm)) {
                Log.printFormattedLine(
                        "Couldn't restore VM #%d on host #%d",
                        vm.getId(), host.getId());
                return;
            }
            addVmToHostMap(vm, host);
        }
    }

    /**
     * Gets the power consumption of a host after the supposed placement of a candidate VM.
     * The VM is not in fact placed at the host.
     *
     * @param host the host to check the power consumption
     * @param vm the candidate vm
     *
     * @return the host power consumption after the supposed VM placement or 0 if the power
     * consumption could not be determined
     */
    protected double getPowerAfterAllocation(PowerHost host, Vm vm) {
        try {
            return host.getPowerModel().getPower(getMaxUtilizationAfterAllocation(host, vm));
        } catch (Exception e) {
            Log.printFormattedLine("[ERROR] Power consumption for Host %d could not be determined: ", host.getId(), e.getMessage());
        }

        return 0;
    }

    /**
     * Gets the max power consumption of a host after placement of a candidate
     * VM. The VM is not in fact placed at the host. We assume that load is
     * balanced between PEs. The only restriction is: VM's max MIPS < PE's MIPS
     *
     * @param host the host
     * @param vm the vm
     *
     * @return the power after allocation
     */
    protected double getMaxUtilizationAfterAllocation(PowerHost host, Vm vm) {
        final double requestedTotalMips = vm.getCurrentRequestedTotalMips();
        final double hostUtilizationMips = getUtilizationOfCpuMips(host);
        final double hostPotentialMipsUse = hostUtilizationMips + requestedTotalMips;
        return hostPotentialMipsUse / host.getTotalMipsCapacity();
    }

    /**
     * Gets the utilization of the CPU in MIPS for the current potentially
     * allocated VMs.
     *
     * @param host the host
     *
     * @return the utilization of the CPU in MIPS
     */
    protected double getUtilizationOfCpuMips(PowerHost host) {
        double hostUtilizationMips = 0;
        for (final Vm vm2 : host.getVmList()) {
            if (host.getVmsMigratingIn().contains(vm2)) {
                // calculate additional potential CPU usage of a migrating in VM
                hostUtilizationMips += host.getTotalAllocatedMipsForVm(vm2) * 0.9 / 0.1;
            }
            hostUtilizationMips += host.getTotalAllocatedMipsForVm(vm2);
        }
        return hostUtilizationMips;
    }

    /**
     * Adds an entry for each history map of a host if it doesn't contain
     * an entry for the current simulation time.
     *
     * @param host the host to add metric history entries
     * @param metric the metric to be added to the metric history map
     */
    protected void addHistoryEntryIfAbsent(PowerHost host, double metric) {
        timeHistory.putIfAbsent(host, new LinkedList<>());
        utilizationHistory.putIfAbsent(host, new LinkedList<>());
        metricHistory.putIfAbsent(host, new LinkedList<>());

        final Simulation simulation = host.getSimulation();
        if (!timeHistory.get(host).contains(simulation.clock())) {
            timeHistory.get(host).add(simulation.clock());
            utilizationHistory.get(host).add(host.getUtilizationOfCpu());
            metricHistory.get(host).add(metric);
        }
    }

    /**
     * Gets the saved allocation.
     *
     * @return the saved allocation
     */
    protected Map<Vm, Host> getSavedAllocation() {
        return savedAllocation;
    }

    /**
     * Sets the vm selection policy.
     *
     * @param vmSelectionPolicy the new vm selection policy
     */
    protected final void setVmSelectionPolicy(PowerVmSelectionPolicy vmSelectionPolicy) {
        this.vmSelectionPolicy = vmSelectionPolicy;
    }

    /**
     * Gets the vm selection policy.
     *
     * @return the vm selection policy
     */
    protected PowerVmSelectionPolicy getVmSelectionPolicy() {
        return vmSelectionPolicy;
    }

    @Override
    public Map<Host, List<Double>> getUtilizationHistory() {
        return Collections.unmodifiableMap(utilizationHistory);
    }

    @Override
    public Map<Host, List<Double>> getMetricHistory() {
        return Collections.unmodifiableMap(metricHistory);
    }

    @Override
    public Map<Host, List<Double>> getTimeHistory() {
        return Collections.unmodifiableMap(timeHistory);
    }

    @Override
    public double getUnderUtilizationThreshold() {
        return underUtilizationThreshold;
    }

    @Override
    public void setUnderUtilizationThreshold(double underUtilizationThreshold) {
        this.underUtilizationThreshold = underUtilizationThreshold;
    }
}
