package org.cloudbus.cloudsim.schedulers.vm;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.resources.Resource;

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
     * {@link Resource#isResourceAmountAvailable(long)}
     * to first check if the difference from the current allocated mips and the
     * requested one is available. Currently the implementations wrongly check
     * if the total requested mips is available, while only the difference has
     * to be checked. It has to be added some tests to check this issue.
     */
    boolean allocatePesForVm(Vm vm, List<Double> mipsShareRequested);

    /**
     * Releases PEs allocated to all the VMs of the host the VmScheduler
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
     * Gets the MIPS share of each host's Pe that is allocated to a given VM.
     *
     * @param vm the vm to get the MIPS share
     * @return
     * @pre $none
     * @post $none
     */
    List<Double> getAllocatedMipsForVm(Vm vm);

    /**
     * Gets the amount of MIPS that is free.
     *
     * @return
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
     * Gets the maximum available MIPS among all the host's PEs.
     *
     * @return
     */
    double getMaxAvailableMips();

    /**
     * Gets PE capacity in MIPS.
     *
     * @return
     * @todo It considers that all PEs have the same capacity, what has been
     * shown doesn't be assured. The peList received by the VmScheduler can be
     * heterogeneous PEs.
     */
    double getPeCapacity();

    /**
     * Gets the list of PEs from the Host.
     *
     * @param <T> the generic type
     * @return
     *
     */
    <T extends Pe> List<T> getPeList();

    /**
     * Gets the map of VMs to PEs, where each key is a VM UID and each value is a list
     * of PEs allocated to that VM.
     *
     * @return
     */
    Map<Vm, List<Pe>> getPeMap();

    /**
     * Gets the list of PEs allocated for a VM.
     *
     * @param vm the VM to get the allocated PEs
     * @return
     */
    List<Pe> getPesAllocatedForVM(Vm vm);

    /**
     * Gets the total allocated MIPS for a VM along all its allocated PEs.
     *
     * @param vm the VM to get the total allocated MIPS
     * @return
     */
    double getTotalAllocatedMipsForVm(Vm vm);

    /**
     * Gets a <b>read-only</b> list of VMs migrating in.
     *
     * @return
     */
    Set<Vm> getVmsMigratingIn();


    /**
     * Gets a <b>read-only</b> list of VMs migrating out.
     *
     * @return
     */
    Set<Vm> getVmsMigratingOut();

    /**
     * Adds a {@link Vm} to the list of VMs migrating in.
     * @param vm the vm to be added
     * @return true if the VM wasn't into the list and was added, false otherwise
     */
    boolean addVmMigratingIn(Vm vm);

    /**
     * Adds a {@link Vm} to the list of VMs migrating out.
     * @param vm the vm to be added
     * @return true if the VM wasn't into the list and was added, false otherwise
     */
    boolean addVmMigratingOut(Vm vm);

    /**
     * Adds a {@link Vm} to the list of VMs migrating in.
     * @param vm the vm to be added
     */
    boolean removeVmMigratingIn(Vm vm);

    /**
     * Adds a {@link Vm} to the list of VMs migrating out.
     * @param vm the vm to be added
     */
    boolean removeVmMigratingOut(Vm vm);

    /**
     * Defines the percentage of Host's CPU usage increase when a
     * VM is migrating in or out of the Host.
     * The value is in scale from 0 to 1 (where 1 is 100%).
     *
     * @return the Host's CPU migration overhead percentage.
     */
    double getCpuOverheadDueToVmMigration();

    /**
     * Gets the host that the VmScheduler get the list of PEs to allocate to VMs.
     * @return
     */
    Host getHost();

    /**
     * Sets the host that the VmScheduler get the list of PEs to allocate to VMs.
     * A host for the VmScheduler is set when the VmScheduler is set to a given host.
     * Thus, the host is in charge to set itself to a VmScheduler.
     * @param host the host to be set
     * @throws IllegalArgumentException when the scheduler already is assigned to another Host, since
     * each Host must have its own scheduler
     * @throws NullPointerException when the host parameter is null
     */
    VmScheduler setHost(Host host);

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
        @Override public Map<Vm, List<Pe>> getPeMap() { return Collections.emptyMap(); }
        @Override public List<Pe> getPesAllocatedForVM(Vm vm) { return Collections.emptyList(); }
        @Override public double getTotalAllocatedMipsForVm(Vm vm) { return 0.0; }
        @Override public Set<Vm> getVmsMigratingIn() { return Collections.emptySet(); }
        @Override public Set<Vm> getVmsMigratingOut() { return Collections.emptySet(); }
        @Override public boolean addVmMigratingIn(Vm vm) { return false; }
        @Override public boolean addVmMigratingOut(Vm vm) { return false; }
        @Override public boolean removeVmMigratingIn(Vm vm) { return false; }
        @Override public boolean removeVmMigratingOut(Vm vm) { return false; }
        @Override public boolean isSuitableForVm(Vm vm) { return false; }
        @Override public double getCpuOverheadDueToVmMigration() { return 0.0; }
        @Override public Host getHost() { return Host.NULL; }
        @Override public VmScheduler setHost(Host host) { return this; }
    };
}
