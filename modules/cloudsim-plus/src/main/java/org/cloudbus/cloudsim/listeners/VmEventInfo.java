package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Vm;

/**
 * An {@link EventInfo} interface that stores data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * about events happened for a given {@link Vm}.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface VmEventInfo extends EventInfo {

    /**
     *
     * @return the Vm for which the event happened
     */
    Vm getVm();

    /**
     * Sets the Vm for which the event happened.
     * 
     * @param vm
     */
    void setVm(Vm vm);
}
