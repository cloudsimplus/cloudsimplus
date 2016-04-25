package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Cloudlet;

/**
 * A basic implementation of the {@link CloudletEventInfo} interface.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class CloudletEventInfoSimple extends EventInfoAbstract implements CloudletEventInfo {
    
    private Cloudlet cloudlet;

    /**
     * Default constructor that uses the current simulation time
     * as the event time.
     * 
     * @param cloudlet
     * @see CloudSim#clock() 
     */
    public CloudletEventInfoSimple(Cloudlet cloudlet) {
        this(USE_CURRENT_SIMULATION_TIME, cloudlet);
    }
    
    public CloudletEventInfoSimple(double time, Cloudlet cloudlet) {
        super(time);
        setCloudlet(cloudlet);
    }

    @Override
    public Cloudlet getCloudlet() {
        return cloudlet;
    }
    
    @Override
    public final void setCloudlet(Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
    }
    
}
