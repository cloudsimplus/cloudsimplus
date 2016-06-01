package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Host;

/**
 * An {@link EventInfo} interface that stores data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * about events of a given {@link Host}.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface HostEventInfo extends EventInfo {

    /**
     * @return the Host for which the event happened
     */
    Host getHost();

    /**
     * Sets the Host for which the event happened.
     * 
     * @param host
     */
    void setHost(Host host);
}
