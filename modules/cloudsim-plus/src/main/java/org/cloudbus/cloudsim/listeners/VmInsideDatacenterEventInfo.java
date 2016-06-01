package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Vm;

/**
 * An {@link EventInfo} class that stores data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * about events of a Vm that happened inside a given Datacenter.
 * 
 * @see Vm#getOnVmCreationFailureListener() 
 * @author Manoel Campos da Silva Filho
 */
public class VmInsideDatacenterEventInfo extends VmEventInfoSimple implements DatacenterEventInfo {
    private Datacenter datacenter;

    /**
     * Default constructor that uses the current simulation time
     * as the event time.
     * 
     * @param datacenter
     * @param vm
     * @see CloudSim#clock() 
     */
    public VmInsideDatacenterEventInfo(Datacenter datacenter, Vm vm) {
        this(USE_CURRENT_SIMULATION_TIME, datacenter, vm);
    }    

    public VmInsideDatacenterEventInfo(double time, Datacenter datacenter, Vm vm) {
        super(time, vm);
        setDatacenter(datacenter);
    }

    /**
     * 
     * @return the Datacenter that caused the Vm event
     */
    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }

    /**
     * Sets the Datacenter that caused the Vm event
     * @param datacenter 
     */
    @Override
    public final void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
