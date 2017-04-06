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
import org.cloudbus.cloudsim.util.ExecutionTimeMeasurer;

/**
 * An abstract power-aware VM allocation policy that dynamically optimizes the
 * VM allocation (placement) using migration.
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
    private double underUtilizationThreshold = 0.35;

    /**
     * The vm selection policy.
     */
    private PowerVmSelectionPolicy vmSelectionPolicy;

    /**
     * A list of maps between a VM and the host where it is placed.
     */
    private final Map<Vm, Host> savedAllocation = new HashMap<>();

    /**
     * A map of CPU utilization history (in percentage) for each host, where
     * each key is a hos and each value is the CPU utilization percentage history.
     */
    private final Map<Host, List<Double>> utilizationHistory = new HashMap<>();

    /**
     * The metric history.
     *
     * @TODO the map stores different data. Sometimes it stores the upper
     * threshold, other it stores utilization threshold or predicted
     * utilization, that is very confusing.
     */
    private final Map<Host, List<Double>> metricHistory = new HashMap<>();

    /**
     * The time when entries in each history list was added. All history lists
     * are updated at the same time.
     */
    private final Map<Host, List<Double>> timeHistory = new HashMap<>();

    /**
     * The history of time spent in VM selection every time the optimization of
     * VM allocation method is called.
     *
     * @see #optimizeAllocation(java.util.List)
     */
    private final List<Double> executionTimeHistoryVmSelection = new LinkedList<>();

    /**
     * The history of time spent in host selection every time the optimization
     * of VM allocation method is called.
     *
     * @see #optimizeAllocation(java.util.List)
     */
    private final List<Double> executionTimeHistoryHostSelection = new LinkedList<>();

    /**
     * The history of time spent in VM reallocation every time the optimization
     * of VM allocation method is called.
     *
     * @see #optimizeAllocation(java.util.List)
     */
    private final List<Double> executionTimeHistoryVmReallocation = new LinkedList<>();

    /**
     * The history of total time spent in every call of the optimization of VM
     * allocation method.
     *
     * @see #optimizeAllocation(java.util.List)
     */
    private final List<Double> executionTimeHistoryTotal = new LinkedList<>();

    /**
     * Creates a PowerVmAllocationPolicyMigrationAbstract.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     */
    public PowerVmAllocationPolicyMigrationAbstract(PowerVmSelectionPolicy vmSelectionPolicy) {
        super();
        setVmSelectionPolicy(vmSelectionPolicy);
    }

    @Override
    public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList) {
        final String allocationTotalStr = "optimizeAllocationTotal";
        ExecutionTimeMeasurer.start(allocationTotalStr);


        final String hostSelectionStr = "optimizeAllocationHostSelection";
        ExecutionTimeMeasurer.start(hostSelectionStr);
        final List<PowerHostUtilizationHistory> overUtilizedHosts = getOverUtilizedHosts();
        getExecutionTimeHistoryHostSelection().add(
                ExecutionTimeMeasurer.end(hostSelectionStr));

        printOverUtilizedHosts(overUtilizedHosts);

        saveAllocation();

        final String vmSelectionStr = "optimizeAllocationVmSelection";
        ExecutionTimeMeasurer.start(vmSelectionStr);
        final List<Vm> vmsToMigrate = getVmsToMigrateFromHosts(overUtilizedHosts);
        getExecutionTimeHistoryVmSelection().add(ExecutionTimeMeasurer.end(vmSelectionStr));

        Map<Vm, Host> migrationMap = new HashMap<>();
        if(!overUtilizedHosts.isEmpty()){
            Log.printLine("Reallocation of VMs from the over-utilized hosts: ");
            final String vmReallocationStr = "optimizeAllocationVmReallocation";
            ExecutionTimeMeasurer.start(vmReallocationStr);
            migrationMap =
                    getNewVmPlacement(vmsToMigrate, new HashSet<>(overUtilizedHosts));
            getExecutionTimeHistoryVmReallocation().add(
                    ExecutionTimeMeasurer.end(vmReallocationStr));
            Log.printLine();
        }

        migrationMap.putAll(getMigrationMapFromUnderUtilizedHosts(overUtilizedHosts));
        restoreAllocation();
        getExecutionTimeHistoryTotal().add(ExecutionTimeMeasurer.end(allocationTotalStr));
        return migrationMap;
    }

    /**
     * Gets the migration map from under utilized hosts.
     *
     * @param overUtilizedHosts the over utilized hosts
     * @return the migration map from under utilized hosts
     */
    protected Map<Vm, Host> getMigrationMapFromUnderUtilizedHosts(
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

            Log.printConcatLine("Under-utilized host: host #", underUtilizedHost.getId(), "\n");

            excludedHostsFromUnderUsedSearch.add(underUtilizedHost);
            excludedHostsForFindingNewVmPlacement.add(underUtilizedHost);

            List<? extends Vm> vmsToMigrateFromUnderUsedHost = getVmsToMigrateFromUnderUtilizedHost(underUtilizedHost);
            if (!vmsToMigrateFromUnderUsedHost.isEmpty()) {
                Log.print("Reallocation of VMs from the under-utilized host: ");
                printVmIDs(vmsToMigrateFromUnderUsedHost);

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

    private void printVmIDs(List<? extends Vm> vmList) {
        if (!Log.isDisabled()) {
            vmList.forEach(vm -> Log.print(vm.getId() + " "));
            Log.printLine();
        }
    }

    /**
     * Prints the over utilized hosts.
     *
     * @param overUtilizedHosts the over utilized hosts
     */
    protected void printOverUtilizedHosts(List<PowerHostUtilizationHistory> overUtilizedHosts) {
        if (!Log.isDisabled() && !overUtilizedHosts.isEmpty()) {
            Log.printLine("Over-utilized hosts:");
            overUtilizedHosts.forEach(host -> Log.printConcatLine("Host #", host.getId()));
            Log.printLine();
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
    protected boolean isHostNotOverusedAfterAllocation(PowerHost host, Vm vm) {
        boolean isHostOverUsedAfterAllocation = true;
        if (host.vmCreate(vm)) {
            isHostOverUsedAfterAllocation = isHostOverUtilized(host);
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
     * Finds a PM that has enough resources to host a given VM and that will not
     * be overloaded after placing the VM on it. The selected host will be that
     * one with most efficient power usage for the given VM.
     *
     * @param vm the VM
     * @param excludedHosts the excluded hosts
     * @return the PM found to host the VM or {@link PowerHost#NULL} if not found
     */
    public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
        final Comparator<PowerHost> hostPowerConsumptionComparator =
            Comparator.comparingDouble(h -> getPowerAfterAllocationDifference(h, vm));

        return this.<PowerHost>getHostList().stream()
            .filter(h -> !excludedHosts.contains(h))
            .filter(h -> h.isSuitableForVm(vm))
            .filter(h -> isHostNotOverusedAfterAllocation(h, vm))
            .filter(h -> getPowerAfterAllocation(h, vm) > 0)
            .min(hostPowerConsumptionComparator)
            .orElse(PowerHost.NULL);
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
     * Gets a new vm placement considering the list of VM to migrate.
     *
     * @param vmsToMigrate the list of VMs to migrate
     * @param excludedHosts the list of hosts that aren't selected as
     * destination hosts
     * @return the new vm placement map where each key is a Vm
     * and each value is the host to place it.
     */
    protected Map<Vm, Host> getNewVmPlacement(
            List<Vm> vmsToMigrate, Set<Host> excludedHosts) {
        final Map<Vm, Host> migrationMap = new HashMap<>();
        VmList.sortByCpuUtilization(vmsToMigrate, getDatacenter().getSimulation().clock());
        for (final Vm vm : vmsToMigrate) {
            final PowerHost allocatedHost = findHostForVm(vm, excludedHosts);
            if (allocatedHost != PowerHost.NULL) {
                allocatedHost.vmCreate(vm);
                Log.printConcatLine("VM #", vm.getId(), " allocated to host #", allocatedHost.getId());

                migrationMap.put(vm, allocatedHost);
            }
        }

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
            List<? extends Vm> vmsToMigrate,
            Set<? extends Host> excludedHosts) {
        final Map<Vm, Host> migrationMap = new HashMap<>();
        VmList.sortByCpuUtilization(vmsToMigrate, getDatacenter().getSimulation().clock());
        for (final Vm vm : vmsToMigrate) {
            final PowerHost allocatedHost = findHostForVm(vm, excludedHosts);
            if (!PowerHost.NULL.equals(allocatedHost )) {
                allocatedHost.vmCreate(vm);
                Log.printConcatLine("VM #", vm.getId(), " allocated to host #", allocatedHost.getId());
                migrationMap.put(vm, allocatedHost);
            } else {
                Log.printLine("Not all VMs can be reallocated from the host, reallocation cancelled");
                migrationMap.entrySet().forEach(e -> e.getValue().destroyVm(e.getKey()));
                migrationMap.clear();
                break;
            }
        }

        return migrationMap;
    }

    /**
     * Gets the VMs to migrate from hosts.
     *
     * @param overUtilizedHosts the over utilized hosts
     * @return the VMs to migrate from hosts
     */
    protected List<Vm> getVmsToMigrateFromHosts(List<PowerHostUtilizationHistory> overUtilizedHosts) {
        final List<Vm> vmsToMigrate = new LinkedList<>();
        for (final PowerHostUtilizationHistory host : overUtilizedHosts) {
            while (true) {
                final Vm vm = getVmSelectionPolicy().getVmToMigrate(host);
                if (Vm.NULL.equals(vm)) {
                    break;
                }
                vmsToMigrate.add(vm);
                host.destroyVm(vm);
                if (!isHostOverUtilized(host)) {
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
            .filter(this::isHostOverUtilized)
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
     * @return the most under utilized host or {@link PowerHost#NULL}
     * if no Host was found
     */
    protected PowerHost getUnderUtilizedHost(Set<? extends Host> excludedHosts) {
        return this.<PowerHost>getHostList().stream()
            .filter(h -> !excludedHosts.contains(h))
            .filter(h -> h.getUtilizationOfCpu() > 0)
            .filter(h -> isNotAllVmsMigratingOutNorVmsAreMigratingIn(h))
            .min(Comparator.comparingDouble(HostDynamicWorkload::getUtilizationOfCpu))
            .orElse(PowerHost.NULL);
    }

    /**
     * Checks if all VMs of a Host are <b>NOT</b> migrating out nor there are VMs migrating in.
     * If all VMs are migrating out or there is at least
     * one VM migrating in, the given Host will not be selected as an underutilized Host at the current moment.
     *
     * @param host the host to check
     * @return
     */
    protected boolean isNotAllVmsMigratingOutNorVmsAreMigratingIn(PowerHost host) {
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
                throw new RuntimeException(
                    String.format(
                        "Couldn't restore VM #%d on host #%d",
                        vm.getId(), host.getId()));
            }
            getVmHostMap().put(vm, host);
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
        getTimeHistory().putIfAbsent(host, new LinkedList<>());
        getUtilizationHistory().putIfAbsent(host, new LinkedList<>());
        getMetricHistory().putIfAbsent(host, new LinkedList<>());

        final Simulation simulation = host.getSimulation();
        if (!getTimeHistory().get(host).contains(simulation.clock())) {
            getTimeHistory().get(host).add(simulation.clock());
            getUtilizationHistory().get(host).add(host.getUtilizationOfCpu());
            getMetricHistory().get(host).add(metric);
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

    /**
     * Gets the utilization history.
     *
     * @return the utilization history
     */
    public Map<Host, List<Double>> getUtilizationHistory() {
        return utilizationHistory;
    }

    /**
     * Gets the metric history.
     *
     * @return the metric history
     */
    public Map<Host, List<Double>> getMetricHistory() {
        return metricHistory;
    }

    /**
     * Gets the time history.
     *
     * @return the time history
     */
    public Map<Host, List<Double>> getTimeHistory() {
        return timeHistory;
    }

    /**
     * Gets the execution time history vm selection.
     *
     * @return the execution time history vm selection
     */
    public List<Double> getExecutionTimeHistoryVmSelection() {
        return executionTimeHistoryVmSelection;
    }

    /**
     * Gets the execution time history host selection.
     *
     * @return the execution time history host selection
     */
    public List<Double> getExecutionTimeHistoryHostSelection() {
        return executionTimeHistoryHostSelection;
    }

    /**
     * Gets the execution time history vm reallocation.
     *
     * @return the execution time history vm reallocation
     */
    public List<Double> getExecutionTimeHistoryVmReallocation() {
        return executionTimeHistoryVmReallocation;
    }

    /**
     * Gets the execution time history total.
     *
     * @return the execution time history total
     */
    public List<Double> getExecutionTimeHistoryTotal() {
        return executionTimeHistoryTotal;
    }

    /**
     * Checks if a host is over utilized, based on current CPU usage.
     *
     * @param host the host
     * @return true, if the host is over utilized; false otherwise
     */
    @Override
    public boolean isHostOverUtilized(PowerHost host) {
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
     * Checks if a host is under utilized, based on current CPU usage.
     *
     * @param host the host
     * @return true, if the host is under utilized; false otherwise
     */
    @Override
    public boolean isHostUnderUtilized(PowerHost host) {
        final double underThreshold = getUnderUtilizationThreshold();
        return getHostCpuUtilizationPercentage(host) < underThreshold;
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
