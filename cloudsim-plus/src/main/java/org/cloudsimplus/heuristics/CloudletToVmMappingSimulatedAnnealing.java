package org.cloudsimplus.heuristics;

import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

/**
 * A heuristic that uses <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated Annealing</a> 
 * to find a sub-optimal mapping among a set of Cloudlets and VMs in order to reduce
 * task completion time.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class CloudletToVmMappingSimulatedAnnealing extends SimulatedAnnealing<CloudletToVmMappingSolution> implements CloudletToVmMappingHeuristic {
    private CloudletToVmMappingSolution initialSolution;
    
    /** @see #getVmList() */
    private List<Vm> vmList;
    
    /** @see #getCloudletList()  */
    private List<Cloudlet> cloudletList;

    /**
     * Creates a new Simulated Annealing Heuristic for solving Cloudlets to Vm's mapping.
     * 
     * @param initialTemperature the system initial temperature
     * @param random a random number generator
     * @see #setColdTemperature(int) 
     * @see #setCoolingRate(double) 
     */
    public CloudletToVmMappingSimulatedAnnealing(double initialTemperature, ContinuousDistribution random) {
        super(CloudletToVmMappingSolution.class, random);
        initialSolution = new CloudletToVmMappingSolution(this);
        setCurrentTemperature(initialTemperature);
    }

    public CloudletToVmMappingSolution generatesTotallyRandomSolution() {
        CloudletToVmMappingSolution solution = new CloudletToVmMappingSolution(this);
        cloudletList.stream()
                .forEach(c -> solution.bindCloudletToVm(c, getRandomVm()));    
        
        return solution;
    }
    
    protected boolean isReadToGenerateInitialSolution(){
        return !cloudletList.isEmpty() && !vmList.isEmpty();
    }
    
    protected boolean isThereInitialSolution(){
        return !initialSolution.getResult().isEmpty();
    }

    @Override
    public CloudletToVmMappingSolution getInitialSolution() {
        if(!isThereInitialSolution() && isReadToGenerateInitialSolution()) {
            initialSolution = generatesTotallyRandomSolution();
        }
        
        return initialSolution;
    }

    /**
     * 
     * @return the list of available Vm's to host Cloudlets.
     */
    @Override
    public List<Vm> getVmList() {
        return vmList;
    }

    /**
     * Sets the list of available Vm's to host Cloudlets.
     * @param vmList 
     */
    @Override
    public void setVmList(List<Vm> vmList) {
        this.vmList = vmList;
    }

    /**
     * 
     * @return the list of cloudlets to be mapped to {@link #getVmList() available Vm's}.
     */
    @Override
    public List<Cloudlet> getCloudletList() {
        return cloudletList;
    }

    /**
     * Sets the list of cloudlets to be mapped to {@link #getVmList() available Vm's}.
     * @param cloudletList 
     */
    @Override
    public void setCloudletList(List<Cloudlet> cloudletList) {
        this.cloudletList = cloudletList;
    }

    /**
     * @return a random Vm from the  {@link #getVmList() available Vm's list}.
     */
    private Vm getRandomVm() {
        final int i = getRandomValue(vmList.size());
        return vmList.get(i);
    }
    
}
