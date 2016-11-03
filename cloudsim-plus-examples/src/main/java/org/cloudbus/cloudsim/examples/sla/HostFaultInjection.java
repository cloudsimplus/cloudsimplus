/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.examples.sla;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
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

    /**
     * 1 means that the host failure is true and 0 otherwise
     */
    private static final int HOST_FAILURE = 1;
    private Host host;

    public HostFaultInjection(String name) {
        super(name);
    }

    @Override
    public void startEntity() {
        int delay = delayRandomly(10);
        Log.printLine(getName() + " is starting...");
        schedule(getId(), delay, HOST_FAILURE);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case HOST_FAILURE:
                host.setFailed(true); // set to true
                if (host.isFailed()) {
                    Log.printLine(CloudSim.clock() + " ---> Host " + host + " FAILURE...\n");
                    for (Vm vm : host.getVmList()) {
                        vm.setFailed(true, vm);
                    }
                }
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
     * @return the host
     */
    public Host getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(Host host) {
        this.host = host;
    }
}
