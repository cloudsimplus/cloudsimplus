package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Datacenter;

/**
 * An {@link EventInfo} interface that stores data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * about events happened for a given {@link Datacenter}.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface DatacenterEventInfo extends EventInfo {

    /**
     *
     * @return the Datacenter for which the event happened
     */
    Datacenter getDatacenter();

    /**
     * Sets the Datacenter for which the event happened.
     * 
     * @param datacenter
     */
    void setDatacenter(Datacenter datacenter);
}
