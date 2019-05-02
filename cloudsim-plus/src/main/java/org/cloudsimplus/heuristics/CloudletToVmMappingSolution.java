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
package org.cloudsimplus.heuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A possible solution for mapping a set of Cloudlets to a set of Vm's.
 * It represents a solution generated using a {@link Heuristic} implementation.
 *
 * @author Manoel Campos da Silva Filho
 * @see Heuristic
 * @since CloudSim Plus 1.0
 */
public class CloudletToVmMappingSolution implements HeuristicSolution<Map<Cloudlet, Vm>> {
    /**
     * When two double values are subtracted to check if they are equal zero,
     * there may be some precision issues. This value is used to check the absolute difference between the two values
     * to avoid that solutions with little decimal difference be
     * considered different one of the other.
     */
    public static final double MIN_DIFF = 0.0001;

    /**
     * @see #getResult()
     */
    private final Map<Cloudlet, Vm> cloudletVmMap;

    /**
     * Indicates if the {@link #getCost() ()} has to be recomputed
     * due to changes in {@link #cloudletVmMap}.
     * When it is computed, its value is stored to be used
     * in subsequent calls, until the map is changed again, in order to
     * improve performance.
     */
    private boolean recomputeCost = true;

    /**
     * The last computed cost value, since the
     * last time the {@link #cloudletVmMap} was changed.
     * @see #getCost()
     * @see #recomputeCost
     */
    private double lastCost;

    private final Heuristic heuristic;

    /**
     * Creates a new solution for mapping a set of cloudlets to VMs using
     * a given heuristic implementation.
     *
     * @param heuristic the heuristic implementation used to find the solution
     * being created.
     */
    public CloudletToVmMappingSolution(final Heuristic heuristic){
        this(heuristic, new HashMap<>());
    }

    private CloudletToVmMappingSolution(final Heuristic heuristic, final Map<Cloudlet, Vm> cloudletVmMap){
        this.heuristic = heuristic;
        this.cloudletVmMap = cloudletVmMap;
    }

    /**
     * Clones a given solution.
     *
     * @param solution the solution to be cloned
     */
    public CloudletToVmMappingSolution(final CloudletToVmMappingSolution solution){
        this(solution.heuristic, new HashMap<>(solution.cloudletVmMap));
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
    public void bindCloudletToVm(final Cloudlet cloudlet, final Vm vm){
        cloudletVmMap.put(cloudlet, vm);
        recomputeCost = true;
    }

    @Override
    public Heuristic<HeuristicSolution<Map<Cloudlet, Vm>>> getHeuristic() {
        return heuristic;
    }

    private void recomputeCostIfRequested() {
        if(!this.recomputeCost) {
            return;
        }

        this.lastCost = computeCostOfAllVms();
        this.recomputeCost = false;
    }

    private double computeCostOfAllVms() {
        return groupCloudletsByVm()
                .entrySet()
                .stream()
                .mapToDouble(this::getVmCost)
                .sum();
    }

    /**
     * Gets a Map of Cloudlets grouped by their VMs,
     * where each key is a VM and the value is a List of Map Entries
     * containing the Cloudlets running in that VM.
     * @return
     */
    private Map<Vm, List<Map.Entry<Cloudlet, Vm>>> groupCloudletsByVm() {
        return cloudletVmMap.entrySet().stream()
            .collect(Collectors.groupingBy(Map.Entry::getValue));
    }

    /**
     * {@inheritDoc}
     *
     * It computes the cost of the entire mapping between Vm's and Cloudlets.
     *
     * @return {@inheritDoc}
     */
    @Override
    public double getCost() {
        recomputeCostIfRequested();
        return this.lastCost;
    }

    /**
     * It computes the costs of the entire mapping between Vm's and cloudlets.
     *
     * @param forceRecompute indicate if the cost has to be recomputed anyway
     * @return the cost of the entire mapping between Vm's and cloudlets
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
        final List<Cloudlet> cloudlets = convertListOfMapEntriesToListOfCloudlets(entry.getValue());
        return getVmCost(vm, cloudlets);
    }

    /**
     * Computes the cost of all Cloudlets hosted by a given Vm.
     * The cost is based on the number of PEs from the VM that
     * will be idle or overloaded.
     *
     * @param vm the VM to compute the cost to host some Cloudlets
     * @param cloudlets the list of Cloudlets to be hosted by the VM in order to compute the cost
     * @return the VM cost to host the Cloudlets
     */
    public double getVmCost(final Vm vm, final List<Cloudlet> cloudlets) {
        return Math.abs(vm.getNumberOfPes() - getTotalCloudletsPes(cloudlets));
    }

    private List<Cloudlet> convertListOfMapEntriesToListOfCloudlets(final List<Map.Entry<Cloudlet, Vm>> entriesList) {
        return entriesList
            .stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * Gets the total number of PEs from a list of Cloudlets
     * @param listOfCloudletsForVm the list of Cloudlets to get the total number of PEs
     * @return the total number of PEs from all given Cloudlets
     */
    private long getTotalCloudletsPes(final List<Cloudlet> listOfCloudletsForVm) {
        return listOfCloudletsForVm
            .stream()
            .mapToLong(Cloudlet::getNumberOfPes)
            .sum();
    }

    /**
     * Compares this solution with another given one, based on the solution
     * cost. The current object is considered to be:
     * equal to the given object if they have the same cost;
     * greater than the given object if it has a lower cost;
     * lower than the given object if it has a higher cost;
     *
     * @param solution the solution to compare this instance to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(final HeuristicSolution solution) {
        final double diff = this.getCost() - solution.getCost();

        if(Math.abs(diff) <= MIN_DIFF) {
            return 0;
        }

        return (diff > 0 ? -1 : 1);
    }

    /**
     *
     * @return the actual solution, providing the mapping between Cloudlets
     * and Vm's.
     */
    @Override
    public Map<Cloudlet, Vm> getResult() {
        return Collections.unmodifiableMap(cloudletVmMap);
    }

    /**
     * Swap the Vm's of 2 randomly selected cloudlets
     * in the {@link #cloudletVmMap} in order to
     * provide a neighbor solution.
     *
     * The method change the given Map entries, moving the
     * cloudlet of the first entry to the Vm of the second entry
     * and vice-versa.
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

        return true;
    }

    /**
     * Swap the Vm's of 2 randomly selected cloudlets
     * in the {@link #cloudletVmMap} in order to
     * provide a neighbor solution.
     *
     * The method change the given Map entries, moving the
     * cloudlet of the first entry to the Vm of the second entry
     * and vice-versa.
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
     *         an unitary List if the map has only 1 entry;
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
     * Creates a List using only the first entry in the {@link #cloudletVmMap}.
     * @return a single-entry List with either the first {@link #cloudletVmMap} entry,
     *         or an empty List in case no entry is found.
     */
    private List<Map.Entry<Cloudlet, Vm>> createListWithFirstMapEntry() {
        return cloudletVmMap.entrySet()
            .stream()
            .limit(1)
            .collect(Collectors.toList());
    }

    /**
     * Creates a List with 2 randomly selected entries from the {@link #cloudletVmMap}.
     * The way the method is called is ensured there is at least to entries
     * in the {@link #cloudletVmMap}.
     *
     * @return a List with the 2 randomly selected entries
     */
    private List<Map.Entry<Cloudlet, Vm>> createListWithTwoRandomEntries() {
        final int size = cloudletVmMap.entrySet().size();

        final int firstIdx = heuristic.getRandomValue(size);
        final int secondIdx = heuristic.getRandomValue(size);

        final List<Map.Entry<Cloudlet, Vm>> selected = new ArrayList<>(2);
        final Iterator<Map.Entry<Cloudlet, Vm>> it = cloudletVmMap.entrySet().iterator();

        /*
        Loop over the entries until those ones defined by the first and second index
        are found and added to the List.
        Since Map doesn't have an index, we can't access the ith entry directly.
        This loop ensures we iterate the least number of times
        until finding the required entries, without creating
        a List with all entries in order to get just two of them.
        */
        for(int i = 0; selected.size() < 2 && it.hasNext(); i++){
            final Map.Entry<Cloudlet, Vm> solution = it.next();
            if(i == firstIdx || i == secondIdx){
                selected.add(solution);
            }
        }

        return selected;
    }
}
