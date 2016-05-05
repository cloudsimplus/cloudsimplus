package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Host;

/**
 * A basic implementation of the {@link HostEventInfo} interface.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class HostEventInfoSimple extends EventInfoAbstract implements HostEventInfo {
    private Host host;

    /**
     * Default constructor that uses the current simulation time
     * as the event time.
     * 
     * @param host
     * @see CloudSim#clock() 
     */
    public HostEventInfoSimple(Host host) {
        this(USE_CURRENT_SIMULATION_TIME, host);
    }

    public HostEventInfoSimple(double time, Host host) {
        super(time);
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
