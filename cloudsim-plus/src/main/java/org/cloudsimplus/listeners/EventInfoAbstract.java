package org.cloudsimplus.listeners;

/**
 * An abstract class to that implements the basic methods
 * of the {@link EventInfo} interface.
 *
 * @author Manoel Campos da Silva Filho
 */
public class EventInfoAbstract implements EventInfo {
    private final double time;

    /**
     * Creates an EventInfo at the the given time.
     * 
     * @param time time the event was fired
     */
    public EventInfoAbstract(double time){
        this.time = time;
    }

    @Override
    public double getTime() {
        return time;
    }

}
