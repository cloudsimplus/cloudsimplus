package org.cloudbus.cloudsim.examples.observer;

import java.util.List;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;

/**
 * A VmAllocationPolicySimple that implements an Observer pattern in 
 * order to intercept when a host is allocated for a given host.
 * @author Manoel Campos da Silva Filho
 * @see VmAllocationPolicySimple
 */
public class VmAllocationPolicySimpleObservable extends VmAllocationPolicySimple {
    /**
     * The object that will be notified when a Host be allocated to a Vm.
     */
    private final HostToVmAllocationObserver observer;
    
    /**
     * 
     * @param observer The object that will be notified when a Host be allocated to a Vm.
     * @param list Machines available in a {@link Datacenter}
     */
    public VmAllocationPolicySimpleObservable(HostToVmAllocationObserver observer, List<? extends Host> list) {    
        super(list);
        this.observer = observer;
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        if(super.allocateHostForVm(vm)){
            observer.notify(vm, vm.getHost());
            return true;
        } 
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if(super.allocateHostForVm(vm, host)){
            observer.notify(vm, host);
            return true;
        } 
        return false;
    }    
    
}
