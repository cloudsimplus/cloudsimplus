package org.cloudbus.cloudsim.examples.observer;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

/**
 *
 * An observer interface to be implemented by classes that wants to be
 * notified when a Host is allocated to a given Vm.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface HostToVmAllocationObserver {
    /**
     * Notifies that a Host was allocated to a given Vm
     * @param vm the Vm placed on the host
     * @param host the Host allocated to the Vm
     */
    void notify(Vm vm, Host host);
}
