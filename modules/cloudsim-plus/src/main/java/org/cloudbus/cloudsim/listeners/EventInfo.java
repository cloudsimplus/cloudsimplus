package org.cloudbus.cloudsim.listeners;

/**
 * An interface which defines the basic methods
 * that an object representing data about 
 * a happened event must have.
 * 
 * Classes implementing this interface are to
 * pass information about an happened event to an {@link EventListener} 
 * object.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface EventInfo {
    /**
     * A constant that indicates the event time
     * was not set and have to be defined
     * by the constructor as the current simulation time.
     * 
     * @see org.cloudbus.cloudsim.core.CloudSim#clock() 
     */
    int USE_CURRENT_SIMULATION_TIME = -1;
    
    /**
     * Gets the time the event happened.
     * 
     * @return 
     */
   double getTime(); 
}
