package org.cloudsimplus.heuristics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.groupingBy;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.groupingBy;

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
     * Indicates if the {@link #getFitness()} has to be recomputed
     * due to changes in {@link #cloudletVmMap}. 
     * When it is computed, its value is stored to be used
     * in subsequent calls, until the map is changed again, in order to 
     * improve performance.
     */
    private boolean recomputeFitness = true;
    
    /**
     * Indicates if the {@link #getCloudletsGroupedByVmMap()} has to be regenerated
     * due to changes in {@link #cloudletVmMap}. 
     * When it is computed, its value is stored to be used
     * in subsequent calls, until the map is changed again, in order to 
     * improve performance.
     */
    private boolean regenerateMapOfCloudletsGroupedByVm = true;

    /**
     * The last computed fitness value, since the
     * last time the {@link #cloudletVmMap} was changed.
     * @see #getFitness() 
     * @see #recomputeFitness
     */
    private double lastFitness = 0;
    
    /**
     * The last computed Map of Cloudlets grouped by Vm, since the
     * last time the {@link #cloudletVmMap} was changed.
     * @see #getCloudletsGroupedByVmMap()  
     * @see #recomputeFitness
     */
    private Map<Vm, Set<Cloudlet>> lastCloudletsGroupedByVmMap = Collections.EMPTY_MAP;
    
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
        recomputeFitness = true;
        regenerateMapOfCloudletsGroupedByVm = true;
    }

    /**
     * {@inheritDoc}
     * 
     * It computes the fitness of the entire mapping between Vm's and cloudlets.
     * 
     * @return {@inheritDoc}
     */
    @Override
    public double getFitness() {
        if(recomputeFitness){
            lastFitness = getCloudletsGroupedByVmMap().entrySet().stream()
                    .mapToDouble(e -> getFitnessOfCloudletListToVm(e))
                    .sum();
            recomputeFitness = false;
        }
        
        return lastFitness;
    }
    
    /**
     * It computes the fitness of the entire mapping between Vm's and cloudlets.
     * 
     * @param forceRecompute indicate if the fitness has to be recomputed anyway
     * @return the fitness of the entire mapping between Vm's and cloudlets
     * @see #getFitness() 
     */
    public double getFitness(boolean forceRecompute) {
        recomputeFitness |= forceRecompute;
        return getFitness();
    }

    /**
     * <p>Computes the fitness for the currently assigned cloudlets to a given Vm.
     * By this way, it defines how good is the mapping of these cloudlets assigned
     * to that Vm.</p> 
     * 
     * <p>As greater is the fitness, better is the mapping of the cloudlets
     * to the given Vm.</p>
     * 
     * @param entry an entry that defines the mapping between a list of cloudlets
     * to a given Vm
     * 
     * @return the fitness value of the mapping between the list of cloudlets to a 
     * given Vm
     */
    public double getFitnessOfCloudletListToVm(Map.Entry<Vm, Set<Cloudlet>> entry) {
        return entry.getValue().stream()
                .mapToDouble(c -> getFitnessOfCloudletToVm(c, entry.getKey()))
                .sum();
    }

    /**
     * Gets the fitness value to run a Cloudlet in a given Vm.
     * By this way, it defines how good is the mapping of the cloudlet assigned
     * to that Vm.
     * This is the inverse of the estimated cloudlet completion time.
     * Therefore, as lower is the estimated completion time,
     * greater is the fitness.
     * 
     * @param cloudlet the cloudlet to get the fitness of running inside a given Vm
     * @param vm the Vm to check a cloudlet's fitness value
     * @return the fitness to run the Cloudlet in the given Vm
     * @see #getCloudletEstimatedCompletionTime(org.cloudbus.cloudsim.Cloudlet, org.cloudbus.cloudsim.Vm) 
     */
    public double getFitnessOfCloudletToVm(Cloudlet cloudlet, Vm vm) {
        return 1.0/getCloudletEstimatedCompletionTime(cloudlet, vm);
    }

    /**
     * Gets the estimated completion time of a given cloudlet if executed
     * inside a given Vm.
     * 
     * @param cloudlet the cloudlet to check the estimated completion time
     * @param vm the expected Vm to run the cloudlet
     * @return cloudlet estimated completion time (in seconds)
     * 
     * @todo @author manoelcampos The estimation just considers
     * that the cloudlet will use the Vm's PEs all the time 
     * (as in a CloudletSchedulerSpaceShared). To get a more accurate 
     * estimation, this calculation would be made by the Vm's CloudletScheduler.
     * However, this is more complex and would require that all other cloudlets
     * are assigned to the Vm in order to estimate the completion time
     * in a environment with concurring Pe access.
     */
    public double getCloudletEstimatedCompletionTime(Cloudlet cloudlet, Vm vm) {
        return cloudlet.getCloudletTotalLength()/vm.getTotalMipsCapacity();
    }

    /**
     * @return a transformed map from the {@link #cloudletVmMap}
     * that groups the cloudlets hosted by each Vm. 
     */
    public Map<Vm, Set<Cloudlet>> getCloudletsGroupedByVmMap() {
        if(regenerateMapOfCloudletsGroupedByVm){
            lastCloudletsGroupedByVmMap = cloudletVmMap.entrySet().stream()
                .collect(
                    groupingBy(
                        e -> e.getValue(),
                        mapping(e -> e.getKey(), Collectors.toSet())
                    )
                );
            
            regenerateMapOfCloudletsGroupedByVm = false;
        }
        
        return lastCloudletsGroupedByVmMap;
    }

    /**
     * Gets a map of Cloudlets grouped by the hosting Vm.
     * 
     * @param forceRegenerate indicate if the map has to be regenerated anyway
     * @return a transformed map from the {@link #cloudletVmMap}
     * that groups the cloudlets hosted by each Vm. 
     * 
     * @see #getCloudletsGroupedByVmMap() 
     */
    public Map<Vm, Set<Cloudlet>> getCloudletsGroupedByVmMap(boolean forceRegenerate) {
        regenerateMapOfCloudletsGroupedByVm |= forceRegenerate;
        return getCloudletsGroupedByVmMap();
    }
    
    @Override
    public int compareTo(HeuristicSolution o) {
        return Double.compare(this.getFitness(), o.getFitness()); 
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
     * @see #swapVmsOfTwoMapEntries(java.util.Map.Entry<org.cloudbus.cloudsim.Cloudlet,org.cloudbus.cloudsim.Vm>[]) 
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
     * @see #swapVmsOfTwoMapEntries(java.util.Map.Entry<org.cloudbus.cloudsim.Cloudlet,org.cloudbus.cloudsim.Vm>[]) 
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
    
}
