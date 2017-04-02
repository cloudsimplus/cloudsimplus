package org.cloudsimplus.heuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;

/**
 * A class to allow the implementation of Null Object Design Pattern
 * for {@link CloudletToVmMappingHeuristic} interface and extensions of it.
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudletToVmMappingHeuristic#NULL
 */
final class CloudletToVmMappingHeuristicNull extends HeuristicNull<CloudletToVmMappingSolution> implements CloudletToVmMappingHeuristic {
    @Override public List<Cloudlet> getCloudletList() { return Collections.EMPTY_LIST; }
    @Override public List<Vm> getVmList() { return Collections.EMPTY_LIST; }
    @Override public void setCloudletList(List<Cloudlet> cloudletList) {/**/}
    @Override public void setVmList(List<Vm> vmList) {/**/}
}
