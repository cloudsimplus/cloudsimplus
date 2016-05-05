package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

/**
 * An {@link EventInfo} class that stores data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * about events of a Vm that happened inside a given Host.
 * 
 * This class can be used 
 * to notify observers when a Host is {@link Vm#getOnHostAllocationListener() allocated} or 
 * {@link Vm#getOnHostDeallocationListener() deallocated} to a given Vm,
 * when a Vm has its {@link Vm#getOnUpdateVmProcessingListener() processing updated by its Host},
 * etc.
 * 
 * @see Vm#getOnHostAllocationListener()
 * @see Vm#getOnHostDeallocationListener() 
 * @see Vm#getOnUpdateVmProcessingListener() 
 * @author Manoel Campos da Silva Filho
 */
public class VmInsideHostEventInfo extends VmEventInfoSimple implements HostEventInfo {
    private Host host;

    /**
     * Default constructor that uses the current simulation time
     * as the event time.
     * 
     * @param host
     * @param vm
     * @see CloudSim#clock() 
     */
    public VmInsideHostEventInfo(Host host, Vm vm) {
        this(USE_CURRENT_SIMULATION_TIME, host, vm);
    }    
    
    public VmInsideHostEventInfo(double time, Host host, Vm vm) {
        super(time, vm);
        setHost(host);
    }
    
    @Override
    public Host getHost() {
        return host;
    }

    @Override
    public final void setHost(Host host) {
        this.host = host;
    }

}
