package org.cloudbus.cloudsim.examples.observer;

import java.util.List;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * A VmAllocationPolicySimple that implements an Observer pattern in 
 * order to intercept when a host is allocated for a given host.
 * @author Manoel Campos da Silva Filho
 * @see VmAllocationPolicySimple
 * @see VmAllocationPolicySimpleObserver
 */
public class VmAllocationPolicySimpleObservable extends VmAllocationPolicySimple {
    /**
     * The object that will be notified when a Host be allocated to a Vm.
     */
    private final VmAllocationPolicySimpleObserver observer;
    
    /**
     * 
     * @param observer The object that will be notified when a Host be allocated to a Vm.
     * @param list Machines available in a {@link Datacenter}
     */
    public VmAllocationPolicySimpleObservable(VmAllocationPolicySimpleObserver observer, 
            List<? extends Host> list) {    
        super(list);
        
        if(observer == null)
            this.observer = VmAllocationPolicySimpleObserver.NULL;
        else this.observer = observer;
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        if(super.allocateHostForVm(vm)){
            observer.notifyAllocationOfHostToVm(CloudSim.clock(), vm, vm.getHost());
            return true;
        } 
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if(super.allocateHostForVm(vm, host)){
            observer.notifyAllocationOfHostToVm(CloudSim.clock(), vm, host);
            return true;
        } 
        return false;
    }    

    @Override
    public void deallocateHostForVm(Vm vm) {
        final int vmId = vm.getId();
        final int userId = vm.getUserId();
        final Host host = vm.getHost();
        super.deallocateHostForVm(vm); 
        observer.notifyDeallocationOfHostForVm(CloudSim.clock(), vmId, userId, host);
        
    }

}
