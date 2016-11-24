package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.Vm;

/**
 * A basic implementation of the {@link VmEventInfo} interface.
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmEventInfoSimple extends EventInfoAbstract implements VmEventInfo {
    private Vm vm;

    /**
     * Creates a EventInfo with the given parameters.
     * 
     * @param time the time the event was generated
     * @param vm Vm that fired the event
     */
     public VmEventInfoSimple(double time, Vm vm) {
        super(time);
        setVm(vm);
    }
    
    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public final void setVm(Vm vm) {
        this.vm = vm;
    }

    
}
