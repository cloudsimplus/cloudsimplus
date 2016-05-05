package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.core.CloudSim;

/**
 * An abstract class to that implements the basic methods
 * of the {@link EventInfo} interface.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class EventInfoAbstract implements EventInfo {
    private double time;

    /**
     * Default constructor that uses the current simulation time
     * as the event time.
     * 
     * @see CloudSim#clock() 
     */
    public EventInfoAbstract() {
        this(USE_CURRENT_SIMULATION_TIME);
    }
    
    /**
     * Class constructor that sets the event time.
     * @param time the simulation time which the event happened.
     * If the time is negative, it is set as the current simulation time.
     */
    public EventInfoAbstract(double time){
        if(time <= USE_CURRENT_SIMULATION_TIME)
            this.time = CloudSim.clock();
        else this.time = time;
    }
    
    @Override
    public double getTime() {
        return time;
    }
    
}
