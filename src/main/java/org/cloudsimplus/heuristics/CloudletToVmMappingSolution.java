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
package org.cloudsimplus.heuristics;

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * A possible solution for mapping a set of Cloudlets to a set of Vm's.
 * It represents a solution generated using a {@link Heuristic} implementation.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CloudletToVmMappingSolution implements HeuristicSolution<Map<Cloudlet, Vm>> {
    /**
     * When two double values are subtracted to check if they are equal zero,
     * there may be some precision issues. This value is used to check the absolute
     * difference between two values.
     * It is used to avoid that solutions with little decimal deviations are
     * considered different from each other.
     */
    public static final double MIN_DIFF = 0.0001;

    /** @see #getResult() */
    private final Map<Cloudlet, Vm> cloudletVmMap;

    /** The id used to identify the index of the solution inside the set of all solutions created. */
    @Getter
    private final int id;

    /**
     * Indicates if the {@link #getCost()} has to be recomputed due to changes in {@link #cloudletVmMap}.
     * When it is computed, its value is stored to be used
     * in later calls until the map is changed again, so that improving performance.
     */
    private boolean recomputeCost = true;

    /**
     * The last computed cost value, since the
     * last time the {@link #cloudletVmMap} was changed.
     * @see #getCost()
     * @see #recomputeCost
     */
    private double lastCost;

    @Getter
    private final Heuristic heuristic;

    /**
     * Creates a new solution for mapping a set of cloudlets to VMs using
     * a given {@link Heuristic} implementation.
     *
     * @param heuristic the heuristic implementation used to configure the solution being created.
     */
    public CloudletToVmMappingSolution(final Heuristic heuristic){
        this(heuristic, new HashMap<>(), 0);
    }

    /**
     * Creates a new solution for mapping a set of cloudlets to VMs using
     * a given {@link Heuristic} implementation.
     *
     * @param heuristic the heuristic implementation used to configure the solution being created.
     * @param id unique solution id to identify the index of the solution inside the set of all solutions created
     */
    public CloudletToVmMappingSolution(final Heuristic heuristic, final int id){
        this(heuristic, new HashMap<>(), id);
    }

    private CloudletToVmMappingSolution(@NonNull final Heuristic heuristic, @NonNull final Map<Cloudlet, Vm> cloudletVmMap, final int id){
        this.heuristic = heuristic;
        this.cloudletVmMap = cloudletVmMap;
        this.id = id;
    }

    /**
     * Clones a given solution.
     *
     * @param solution the solution to be cloned
     * @param id unique solution id to identify the index of the solution inside the set of all solutions created
     */
    public CloudletToVmMappingSolution(@NonNull final CloudletToVmMappingSolution solution, final int id){
        this(solution.heuristic, new HashMap<>(solution.cloudletVmMap), id);
        this.recomputeCost = solution.recomputeCost;
        this.lastCost = solution.lastCost;
        this.cloudletVmMap.putAll(solution.cloudletVmMap);
    }

    /**
     * Binds a cloudlet to be executed by a given Vm.
     *
     * @param cloudlet the cloudlet to be added to a Vm
     * @param vm the Vm to assign a cloudlet to
     */
    public void bindCloudletToVm(@NonNull final Cloudlet cloudlet, @NonNull final Vm vm){
        cloudletVmMap.put(cloudlet, vm);
        recomputeCost = true;
    }

    private double computeCostOfAllVms() {
        return groupCloudletsByVm()
                .entrySet()
                .stream()
                .mapToDouble(this::getVmCost)
                .sum();
    }

    /**
     * @return a Map of Cloudlets grouped by their VMs,
     * where each key is a VM and the value is a List of Map Entries
     * containing the Cloudlets running in that VM.
     */
    private Map<Vm, List<Map.Entry<Cloudlet, Vm>>> groupCloudletsByVm() {
        return cloudletVmMap
                .entrySet()
                .stream()
                .collect(groupingBy(Map.Entry::getValue));
    }

    /**
     * {@inheritDoc}
     * It computes the cost of the entire mapping between VMs and Cloudlets.
     *
     * @return {@inheritDoc}
     */
    @Override
    public double getCost() {
        recomputeCostIfRequested();
        return this.lastCost;
    }

    private void recomputeCostIfRequested() {
        if (this.recomputeCost) {
            this.lastCost = computeCostOfAllVms();
            this.recomputeCost = false;
        }
    }

    /**
     * It computes the costs of the entire mapping between VMs and cloudlets.
     *
     * @param forceRecompute indicate if the cost has to be recomputed anyway
     * @return the cost of the entire mapping between VMs and cloudlets
     * @see #getCost()
     */
    public double getCost(final boolean forceRecompute) {
        recomputeCost |= forceRecompute;
        return getCost();
    }

    /**
     * Computes the cost of all Cloudlets hosted by a given Vm.
     * The cost is based on the number of PEs from the VM that
     * will be idle or overloaded.
     *
     * @param entry a Map Entry where the key is a VM hosting some Cloudlets
     *              and the value is the Cloudlets hosted in this VM.
     * @return the VM cost to host the Cloudlets
     */
    public double getVmCost(final Map.Entry<Vm, List<Map.Entry<Cloudlet, Vm>>> entry) {
        final Vm vm = entry.getKey();
        final var cloudletList = convertMapEntryListToCloudletList(entry.getValue());
        return getVmCost(vm, cloudletList);
    }

    /**
     * Computes the cost of all Cloudlets hosted by a given Vm.
     * The cost is based on the number of PEs from the VM that
     * will be idle or overloaded.
     *
     * @param vm the VM to compute the cost to host some Cloudlets
     * @param cloudlets the list of Cloudlets to be hosted by the VM to compute the cost
     * @return the VM cost to host the Cloudlets
     */
    public double getVmCost(final Vm vm, final List<Cloudlet> cloudlets) {
        return Math.abs(vm.getPesNumber() - getTotalCloudletsPes(cloudlets));
    }

    private List<Cloudlet> convertMapEntryListToCloudletList(final List<Map.Entry<Cloudlet, Vm>> entriesList) {
        return entriesList
            .stream()
            .map(Map.Entry::getKey)
            .toList();
    }

    /**
     * Gets the total number of PEs from a list of Cloudlets
     * @param cloudletListForVm the list of Cloudlets to get the total number of PEs
     * @return the total number of PEs from all given Cloudlets
     */
    private long getTotalCloudletsPes(final List<Cloudlet> cloudletListForVm) {
        return cloudletListForVm
                .stream()
                .mapToLong(Cloudlet::getPesNumber)
                .sum();
    }

    /**
     * Compares this solution with another given one, based on the solution cost.
     * The current object is considered to be:
     * - equal to the given object if they have the same cost;
     * - greater than the given object if it has a lower cost;
     * - lower than the given object if it has a higher cost;
     *
     * @param solution the solution to compare this instance to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(@NonNull final HeuristicSolution solution) {
        final double diff = this.getCost() - solution.getCost();
        if(Math.abs(diff) <= MIN_DIFF) {
            return 0;
        }

        return diff > 0 ? -1 : 1;
    }

    /**
     * @return the actual solution, providing the mapping between Cloudlets and Vm's.
     */
    @Override
    public Map<Cloudlet, Vm> getResult() {
        return Collections.unmodifiableMap(cloudletVmMap);
    }

    /**
     * Swap the VMs of 2 randomly selected cloudlets
     * in the {@link #cloudletVmMap} in order to
     * provide a neighbor solution.
     *
     * <p>The method changes the given Map entries, moving the
     * cloudlet of the first entry to the Vm of the second entry
     * and vice versa.</p>
     *
     * @param entries a List of 2 entries containing Cloudlets to swap their VMs.
     * If the entries don't have 2 elements, the method will
     * return without performing any change in the entries.
     * @return true if the VMs of the Cloudlets where swapped, false otherwise
     */
    protected final boolean swapVmsOfTwoMapEntries(final List<Map.Entry<Cloudlet, Vm>> entries) {
        if(entries == null || entries.size() != 2 || entries.get(0) == null || entries.get(1) == null) {
            return false;
        }

        final Vm vm0 = entries.get(0).getValue();
        final Vm vm1 = entries.get(1).getValue();
        entries.get(0).setValue(vm1);
        entries.get(1).setValue(vm0);

        return recomputeCost = true;
    }

    /**
     * Swap the VMs of 2 randomly selected cloudlets
     * in the {@link #cloudletVmMap} in order to
     * provide a neighbor solution.
     *
     * <p>The method changes the given Map entries, moving the
     * cloudlet of the first entry to the Vm of the second entry
     * and vice versa.</p>
     *
     * @see #swapVmsOfTwoMapEntries(List)
     * @return true if the Cloudlet's VMs where swapped, false otherwise
     */
    boolean swapVmsOfTwoRandomSelectedMapEntries() {
        return swapVmsOfTwoMapEntries(getRandomMapEntries());
    }

    /**
     * Try to get 2 randomly selected entries from the {@link #cloudletVmMap}.
     *
     * @return a List with 2 entries from the {@link #cloudletVmMap} if the map size is at least 2;
     *         a unitary List if the map has only 1 entry;
     *         or an empty List if there is no entry in the map.
     *
     * @see #swapVmsOfTwoMapEntries(List)
     */
    protected List<Map.Entry<Cloudlet, Vm>> getRandomMapEntries() {
        if(cloudletVmMap.isEmpty()) {
            return new ArrayList<>();
        }

        if(cloudletVmMap.size() == 1) {
            return createListWithFirstMapEntry();
        }

        return createListWithTwoRandomEntries();
    }

    /**
     * @return a single-entry List with either the first {@link #cloudletVmMap} entry,
     *         or an empty List in case no entry is found.
     */
    private List<Map.Entry<Cloudlet, Vm>> createListWithFirstMapEntry() {
        return cloudletVmMap
                .entrySet()
                .stream()
                .limit(1)
                .collect(Collectors.toList());
    }

    /**
     * @return a List with 2 randomly selected entries from the {@link #cloudletVmMap}.
     * The way the method is called ensures there are at least two entries
     * in the {@link #cloudletVmMap}.
     */
    private List<Map.Entry<Cloudlet, Vm>> createListWithTwoRandomEntries() {
        final int size = cloudletVmMap.size();
        final int firstIdx = heuristic.getRandomValue(size);
        final int secondIdx = heuristic.getRandomValue(size);

        final List<Map.Entry<Cloudlet, Vm>> selected = new ArrayList<>(2);
        final var entryIterator = cloudletVmMap.entrySet().iterator();

        /*
        Loop over the entries until those defined by the first and second index
        are found and added to the List.
        Since Map doesn't have an index, we can't access the ith entry directly.
        This loop ensures we iterate the least number of times
        until finding the required entries, without creating
        a List with all entries to get just two of them.
        */
        for(int i = 0; selected.size() < 2 && entryIterator.hasNext(); i++){
            final Map.Entry<Cloudlet, Vm> solution = entryIterator.next();
            if(i == firstIdx || i == secondIdx){
                selected.add(solution);
            }
        }

        return selected;
    }
}
