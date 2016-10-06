package org.cloudsimplus.heuristics;

import java.util.*;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * A possible solution for mapping a set of Cloudlets to a set of Vm's.
 * It represents a solution generated using a {@link Heuristic} implementation.
 *
 * @author Manoel Campos da Silva Filho
 * @see Heuristic
 */
public class CloudletToVmMappingSolution implements HeuristicSolution<Map<Cloudlet, Vm>> {
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
    private double lastCost = 0;

    private final Heuristic heuristic;

    /**
     * Creates a new solution for mapping a set of cloudlets to VMs using
     * a given heuristic implementation.
     *
     * @param heuristic the heuristic implementation used to find the solution
     * being created.
     */
    public CloudletToVmMappingSolution(Heuristic heuristic){
        this(heuristic, new HashMap<>());
    }

    private CloudletToVmMappingSolution(Heuristic heuristic, Map<Cloudlet, Vm> cloudletVmMap){
        this.heuristic = heuristic;
        this.cloudletVmMap = cloudletVmMap;
    }

    /**
     * Clones a given solution.
     *
     * @param solution the solution to be cloned
     */
    public CloudletToVmMappingSolution(CloudletToVmMappingSolution solution){
        this(solution.heuristic, new HashMap<>(solution.cloudletVmMap));
    }

    /**
     * Binds a cloudlet to be executed by a given Vm.
     *
     * @param cloudlet the cloudlet to be added to a Vm
     * @param vm the Vm to assign a cloudlet to
     */
    public void bindCloudletToVm(Cloudlet cloudlet, Vm vm){
        cloudletVmMap.put(cloudlet, vm);
        recomputeCost = true;
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
        if(recomputeCost){
	        Map<Vm, List<Map.Entry<Cloudlet, Vm>>> cloudletsByVm =
		        cloudletVmMap.entrySet().stream()
			        .collect(Collectors.groupingBy(e -> e.getValue()));

	        lastCost = cloudletsByVm.entrySet().stream()
		        .mapToDouble(e-> getVmCost(e.getKey(), e.getValue()))
	            .sum();

            recomputeCost = false;
        }

        return lastCost;
    }

    /**
     * It computes the costs of the entire mapping between Vm's and cloudlets.
     *
     * @param forceRecompute indicate if the cost has to be recomputed anyway
     * @return the cost of the entire mapping between Vm's and cloudlets
     * @see #getCost()
     */
    public double getCost(boolean forceRecompute) {
        recomputeCost |= forceRecompute;
        return getCost();
    }

    /**
     * Computes the cost of all Cloudlets hosted by a given Vm.
     * The cost is based on the number of PEs from the VM that
     * will be idle or overloaded.
     *
     * @param vm VM to compute the cost based on the hosted Cloudlets
     * @param listOfCloudletsForVm A list containing all Cloudlets for a given VM
     * @return the VM cost to host the Cloudlets
     */
    public double getVmCost(Vm vm, List<Map.Entry<Cloudlet, Vm>> listOfCloudletsForVm) {
	    final int totalCloudletsPes = listOfCloudletsForVm.stream()
		    .mapToInt(e->e.getKey().getNumberOfPes())
		    .sum();

        return Math.abs(vm.getNumberOfPes() - totalCloudletsPes);
    }

    /**
     * Compares this solution with another given one, based on the solution
     * cost. The current object is considered to be:
     * equal to the given object if they have the same cost;
     * greater than the given object if it has a lower cost;
     * lower than the given object if it has a higher cost;
     *
     * @param o the solution to compare this instance to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(HeuristicSolution o) {
        double diff = this.getCost() - o.getCost();
        /*
        Precision Issue: checks the absolute difference between the two values
        in order to avoid that solutions with little decimal difference be
        considered different one of the other.*/
        if(Math.abs(diff) <= 0.0001)
            return 0;

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

    @Override
    public CloudletToVmMappingSolution createNeighbor() {
        CloudletToVmMappingSolution clone = new CloudletToVmMappingSolution(this);
        clone.swapVmsOfTwoRandomSelectedMapEntries();
        return clone;
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
     * @param entries an array of 2 entries that the Vm of their cloudlets should
     * be swapped. If the entries don't have 2 elements, the method will
     * return without performing any change in the entries.
     * @return true if the Cloudlet's VMs where swapped, false otherwise
     */
    protected boolean swapVmsOfTwoMapEntries(Map.Entry<Cloudlet, Vm> entries[]) {
        if(entries == null || entries.length != 2 || entries[0] == null || entries[1] == null)
            return false;

        Vm vm1 = entries[0].getValue();
        Vm vm2 = entries[1].getValue();
        entries[0].setValue(vm2);
        entries[1].setValue(vm1);

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
     * @see #swapVmsOfTwoMapEntries(Map.Entry[])
     * @return true if the Cloudlet's VMs where swapped, false otherwise
     */
    private boolean swapVmsOfTwoRandomSelectedMapEntries() {
        return swapVmsOfTwoMapEntries(getRandomMapEntries());
    }

    /**
     * Try to get 2 randomly selected entries from the {@link #cloudletVmMap}.
     *
     * @return an array with 2 entries from the {@link #cloudletVmMap}
     * if the map has at least 2 entries, an unitary array if the map
     * has only one entry, or an empty array if there is no entry.
     *
     * @see #swapVmsOfTwoMapEntries(Map.Entry[])
     */
    protected Map.Entry<Cloudlet, Vm>[] getRandomMapEntries() {
        if(cloudletVmMap.isEmpty())
            return new Map.Entry[0];

        if(cloudletVmMap.size() == 1) {
            Optional<Map.Entry<Cloudlet, Vm>> opt =
                    cloudletVmMap.entrySet().stream().findFirst();
            return new Map.Entry[]{opt.get()};
        }

        final int size = cloudletVmMap.entrySet().size();
        Map.Entry<Cloudlet, Vm>[] selectedEntries = new Map.Entry[2];
        Map.Entry<Cloudlet, Vm>[] allEntries = new Map.Entry[size];

        int i = heuristic.getRandomValue(size);
        int j = heuristic.getRandomValue(size);
        cloudletVmMap.entrySet().toArray(allEntries);
        selectedEntries[0] = allEntries[i];
        selectedEntries[1] = allEntries[j];
        return selectedEntries;
    }

	@Override
	public Heuristic getHeuristic() {
		return heuristic;
	}


}
