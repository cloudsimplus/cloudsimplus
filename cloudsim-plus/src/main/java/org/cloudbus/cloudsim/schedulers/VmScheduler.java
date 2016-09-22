package org.cloudbus.cloudsim.schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.Vm;

/**
 * An interface to be implemented by VmScheduler objects in order to provide
 * scheduling algorithms that allocate host's PEs for VMs running on it.
 * It also implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException} 
 * when using the {@link VmScheduler#NULL} object instead
 * of attributing {@code null} to {@link VmScheduler} variables.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface VmScheduler {
    /**
     * Requests the allocation of PEs for a VM.
     *
     * @param vm the vm
     * @param mipsShareRequested the list of MIPS share to be allocated to a VM
     * @return $true if this policy allows a new VM in the host, $false
     * otherwise
     *
     * @pre $none
     * @post $none
     *
     * @todo @author manoelcampos All implementing classes don't consider the
     * situation when a Vm already has allocated MIPS and the method is
     * called again. In this case, what is supposed to do? Increase the current
     * allocation or change it? I think that the obvious action is to change the
     * allocation, however, the implementations aren't working to deal this
     * situation. For that, they have to use some method such as
     * {@link org.cloudbus.cloudsim.resources.Resource#isSuitable(java.lang.Number)}
     * to first check if the difference from the current allocated mips and the
     * requested one is available. Currently the implementations wrongly check
     * if the total requested mips is available, while only the difference has
     * to be checked. It has to be added some tests to check this issue.
     */
    boolean allocatePesForVm(Vm vm, List<Double> mipsShareRequested);

    /**
     * Releases PEs allocated to all the VMs of the host the VmSchedulerAbstract
     * is associated to. After that, all PEs will be available to be used on
     * demand for requesting VMs.
     *
     * @pre $none
     * @post $none
     */
    void deallocatePesForAllVms();

    /**
     * Releases PEs allocated to a VM. After that, the PEs may be used on demand
     * by other VMs.
     *
     * @param vm the vm
     * @pre $none
     * @post $none
     */
    void deallocatePesForVm(Vm vm);

    /**
     * Returns the MIPS share of each host's Pe that is allocated to a given VM.
     *
     * @param vm the vm
     * @return an array containing the amount of MIPS of each pe that is
     * available to the VM
     * @pre $none
     * @post $none
     */
    List<Double> getAllocatedMipsForVm(Vm vm);

    /**
     * Gets the free mips.
     *
     * @return the free mips
     */
    double getAvailableMips();
    
    /**
     * Checks if the PM using this scheduler has enough MIPS capacity
     * to host a given VM.
     * 
     * @param vm the vm to check if there is enough available resource on the PM to host it
     * 
     * @return true, if it is possible to allocate the the VM into the host; false otherwise
     */
    boolean isSuitableForVm(Vm vm);    

    /**
     * Returns maximum available MIPS among all the host's PEs.
     *
     * @return max mips
     */
    double getMaxAvailableMips();

    /**
     * Returns PE capacity in MIPS.
     *
     * @return mips
     * @todo It considers that all PEs have the same capacity, what has been
     * shown doesn't be assured. The peList received by the VmScheduler can be
     * heterogeneous PEs.
     */
    double getPeCapacity();

    /**
     * Gets the pe list.
     *
     * @param <T> the generic type
     * @return the pe list
     *
     */
    <T extends Pe> List<T> getPeList();

    /**
     * Gets the pe map.
     *
     * @return the pe map
     */
    Map<String, List<Pe>> getPeMap();

    /**
     * Gets the pes allocated for a vm.
     *
     * @param vm the vm
     * @return the pes allocated for the given vm
     */
    List<Pe> getPesAllocatedForVM(Vm vm);

    /**
     * Gets the total allocated MIPS for a VM along all its allocated PEs.
     *
     * @param vm the vm
     * @return the total allocated mips for the vm
     */
    double getTotalAllocatedMipsForVm(Vm vm);

    /**
     * Gets the vms migrating in.
     *
     * @return the vms migrating in
     */
    List<String> getVmsMigratingIn();

    /**
     * Gets the vms migrating out.
     *
     * @return the vms in migration
     */
    List<String> getVmsMigratingOut();
    
    /**
     * Defines the percentage of Host's CPU usage increase when a 
     * VM is migrating in or out of the Host. 
     * The value is in scale from 0 to 1 (where 1 is 100%).
     * 
     * @return the Host's CPU migration overhead percentage.
     */
    double getCpuOverheadDueToVmMigration();

    /**
     * A property that implements the Null Object Design Pattern for {@link VmScheduler}
     * objects.
     */
    VmScheduler NULL = new VmScheduler(){
        @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) { return false; }
        @Override public void deallocatePesForAllVms() {}
        @Override public void deallocatePesForVm(Vm vm) {}
        @Override public List<Double> getAllocatedMipsForVm(Vm vm) { return Collections.emptyList(); }
        @Override public double getAvailableMips() { return 0.0; }
        @Override public double getMaxAvailableMips() { return 0.0; }
        @Override public double getPeCapacity() { return 0.0; }
        @Override public <T extends Pe> List<T> getPeList() { return Collections.emptyList(); }
        @Override public Map<String, List<Pe>> getPeMap() { return Collections.emptyMap(); }
        @Override public List<Pe> getPesAllocatedForVM(Vm vm) { return Collections.emptyList(); }
        @Override public double getTotalAllocatedMipsForVm(Vm vm) { return 0.0; }
        @Override public List<String> getVmsMigratingIn() { return Collections.emptyList(); }
        @Override public List<String> getVmsMigratingOut() { return Collections.emptyList(); }
        @Override public boolean isSuitableForVm(Vm vm) { return false; }
        @Override public double getCpuOverheadDueToVmMigration() { return 0.0; }
    };
}
