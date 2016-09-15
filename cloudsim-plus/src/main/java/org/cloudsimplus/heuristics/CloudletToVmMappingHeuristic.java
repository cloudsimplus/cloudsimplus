package org.cloudsimplus.heuristics;

import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * Provides the methods to be used for implementing a heuristic to get
 * a sub-optimal solution for mapping Cloudlets to Vm's.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface CloudletToVmMappingHeuristic extends Heuristic<CloudletToVmMappingSolution> {

    /**
     *
     * @return the list of cloudlets to be mapped to {@link #getVmList() available Vm's}.
     */
    List<Cloudlet> getCloudletList();

    /**
     *
     * @return the list of available Vm's to host Cloudlets.
     */
    List<Vm> getVmList();

    /**
     * Sets the list of cloudlets to be mapped to {@link #getVmList() available Vm's}.
     * @param cloudletList
     */
    void setCloudletList(List<Cloudlet> cloudletList);

    /**
     * Sets the list of available Vm's to host Cloudlets.
     * @param vmList
     */
    void setVmList(List<Vm> vmList);
        
    /**
     * A property that implements the Null Object Design Pattern for {@link Heuristic}
     * objects.
     */
    public static final CloudletToVmMappingHeuristic NULL = new CloudletToVmMappingHeuristicNull();    
}

/**
 * A class to allow the implementation of Null Object Design Pattern
 * for this interface and extensions of it.
 */
class CloudletToVmMappingHeuristicNull extends HeuristicNull<CloudletToVmMappingSolution> implements CloudletToVmMappingHeuristic {
    @Override public List<Cloudlet> getCloudletList() { return Collections.EMPTY_LIST; }
    @Override public List<Vm> getVmList() { return Collections.EMPTY_LIST; }
    @Override public void setCloudletList(List<Cloudlet> cloudletList) {}
    @Override public void setVmList(List<Vm> vmList) {}
}