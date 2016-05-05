package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Vm;

/**
 * A basic implementation of the {@link VmEventInfo} interface.
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmEventInfoSimple extends EventInfoAbstract implements VmEventInfo {
    private Vm vm;

    /**
     * Default constructor that uses the current simulation time
     * as the event time.
     * 
     * @param vm
     * @see CloudSim#clock() 
     */
    public VmEventInfoSimple(Vm vm) {
        this(USE_CURRENT_SIMULATION_TIME, vm);
    }

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
