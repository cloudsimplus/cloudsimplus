package org.cloudbus.cloudsim.power;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;

/**
 * An interface to be implemented by VM allocation policy for Power-aware VMs.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface PowerVmAllocationPolicy extends VmAllocationPolicy{
    /**
     * Finds the first host that has enough resources to host a given VM.
     * 
     * @param vm the vm to find a host for it
     * @return the first host found that can host the VM
     */
    PowerHostSimple findHostForVm(Vm vm);
}
