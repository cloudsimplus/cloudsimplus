package org.cloudbus.cloudsim.allocationpolicies;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

/**
 * An interface to be implemented by each class that represents a policy used by
 * a {@link Datacenter} to choose a {@link Host} to place or migrate a
 * given {@link Vm}.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface VmAllocationPolicy {

    /**
     * Allocates a host for a given VM.
     *
     * @param vm the VM to allocate a host to
     * @return $true if the host could be allocated; $false otherwise
     * @pre $none
     * @post $none
     */
    boolean allocateHostForVm(Vm vm);

    /**
     * Allocates a specified host for a given VM.
     *
     * @param vm the VM to allocate a host to
     * @param host the host to allocate to the given VM
     * @return $true if the host could be allocated; $false otherwise
     * @pre $none
     * @post $none
     */
    boolean allocateHostForVm(Vm vm, Host host);

    /**
     * Releases the host used by a VM.
     *
     * @param vm the vm to get its host released
     * @pre $none
     * @post $none
     */
    void deallocateHostForVm(Vm vm);

    /**
     * Get the host that is executing the given VM.
     *
     * @param vm the vm
     * @return the Host with the given vmID; $null if not found
     *
     * @pre $none
     * @post $none
     */
    Host getHost(Vm vm);

    /**
     * Get the host that is executing the given VM belonging to the given user.
     *
     * @param vmId the vm id
     * @param userId the user id
     * @return the Host with the given vmID and userID; $null if not found
     * @pre $none
     * @post $none
     */
    Host getHost(int vmId, int userId);

    /**
     * Gets the list of Hosts available in a {@link Datacenter}, that will be
     * used by the Allocation Policy to place VMs.
     *
     * @param <T> The generic type
     * @return the host list
     */
     <T extends Host> List<T> getHostList();

    /**
     * Optimize allocation of the VMs according to current utilization.
     *
     * @param vmList the vm list
     * @return the new vm placement list, where each item is a map
     * where the key is the first item is the VM and the second is the
     * host where it has to be placed. The map key is just to identify
     * if the map item is the VM or Host
     *
     */
    Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList);

    /**
     * A property that implements the Null Object Design Pattern for {@link VmAllocationPolicy}
     * objects.
     */
    VmAllocationPolicy NULL = new VmAllocationPolicy() {
        @Override public boolean allocateHostForVm(Vm vm){ return false; }
        @Override public boolean allocateHostForVm(Vm vm, Host host) { return false; }
        @Override public void deallocateHostForVm(Vm vm){}
        @Override public Host getHost(Vm vm){ return Host.NULL; }
        @Override public Host getHost(int vmId, int userId) { return Host.NULL; }
        @Override public List<Host> getHostList(){ return Collections.emptyList(); }
        @Override public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList) { return Collections.emptyMap(); }
    };
}
