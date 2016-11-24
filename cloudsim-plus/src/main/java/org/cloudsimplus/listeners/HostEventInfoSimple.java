package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.Host;

/**
 * A basic implementation of the {@link HostEventInfo} interface.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class HostEventInfoSimple extends EventInfoAbstract implements HostEventInfo {
    private Host host;

    /**
     * Creates an EventInfo with the given parameters.
     * 
     * @param time time when the event was fired
     * @param host the Host that fired the event
     */
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
