package org.cloudbus.cloudsim.examples.observer;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

/**
 *
 * An observer interface to be implemented by classes that wants to be
 * notified when a Host of a Datacenter (that uses a VmAllocationPolicySimple) 
 * is allocated or deallocated to a given Vm.
 * 
 * @author Manoel Campos da Silva Filho
 * @see VmAllocationPolicySimpleObservable
 */
public interface VmAllocationPolicySimpleObserver {
    /**
     * Gets notified when a Host is allocated to a given Vm.
     * @param clock the simulation time of the allocation
     * @param vm the Vm created
     * @param host the Host allocated to the Vm
     */
    void notifyAllocationOfHostToVm(double clock, Vm vm, Host host);
    
    /**
     * Gets notified when a Host is deallocated for a given Vm.
     * @param clock the simulation time of the deallocation
     * @param vmId id of the  destroyed Vm
     * @param userId id of the Vm owner
     * @param host the Host deallocated for the Vm
     */
    void notifyDeallocationOfHostForVm(double clock, int vmId, int userId, Host host);
    
    /**
     * A implementation of Null Object pattern that makes nothing (it doesn't
     * perform any operation on each existing method). The pattern avoid
     * NullPointerException's.
     * It is used to avoid always checking if a observer object is different
     * of null in order to call its methods.
     */
    public static final VmAllocationPolicySimpleObserver NULL = new VmAllocationPolicySimpleObserver() {
        @Override public void notifyAllocationOfHostToVm(double clock, Vm vm, Host host) {}
        @Override public void notifyDeallocationOfHostForVm(double clock, int vmId, int userId, Host host) {}
    };
}
