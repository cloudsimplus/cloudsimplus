/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

/**
 * This class shows how to generate a fault. In this case the fault is in the
 * host.
 * 
 * @author raysaoliveira
 * 
 */
public class HostFaultInjection extends SimEntity {
    
    private Host host;

    public HostFaultInjection(String name) {
        super(name);
    }

    @Override
    public void startEntity() {
        int delay = delayRandomly(10);
        Log.printLine(getName() + " is starting...");
        schedule(getId(), delay, CloudSimTags.HOST_FAILURE);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.HOST_FAILURE:
                host.setFailed(true); 
                break;

            default:
                Log.printLine(getName() + ": unknown event type");
                break;
        }
    }

    @Override
    public void shutdownEntity() {
        Log.printLine(getName() + ": is shutting down...");
    }

    /**
     * The value of the delay will be generated within that range (0 -
     * MAX_TIME_SIMULATION).
     *
     * @param max_simulation represents the max time for simulation.
     * @return
     */
    public int delayRandomly(int max_simulation) {
        return 1 + (int) (Math.random() * max_simulation);
    }

    /**
     * Gets the host in which a failure may happen.
     * @return 
     */
    public Host getHost() {
        return host;
    }

    /**
     * Sets the host in which failure may happen.
     * @param host the host to set
     */
    public void setHost(Host host) {
        this.host = host;
    }
}
