package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * An {@link EventInfo} class that stores data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * about events of a Cloudlet that happened inside a given Vm.
 * 
 * This class can be used 
 * to notify observers, for instance, when a Cloudlet finishes executing .
 * 
 * @see Cloudlet#getOnCloudletFinishEventListener() 
 * @author Manoel Campos da Silva Filho
 */
public class CloudletInsideVmEventInfo extends CloudletEventInfoSimple implements  VmEventInfo {
    private Vm vm;

    public CloudletInsideVmEventInfo(Vm vm, Cloudlet cloudlet) {
        this(USE_CURRENT_SIMULATION_TIME, vm, cloudlet);
    }

    public CloudletInsideVmEventInfo(double time, Vm vm, Cloudlet cloudlet) {
        super(time, cloudlet);
        this.vm = vm;
    }
    
    /**
     * @return the Vm that was executing the cloudlet
     */
    @Override
    public Vm getVm() {
        return vm;
    }    

    /**
     * Sets the Vm that was executing the cloudlet
     * @param vm 
     */
    @Override
    public void setVm(Vm vm) {
        this.vm = vm;
    }
}
