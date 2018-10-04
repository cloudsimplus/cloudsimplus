package org.cloudbus.cloudsim.core.events;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

/**
 * A class that implements the Null Object Design Pattern for {@link SimEvent}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see SimEvent#NULL
 */
final class SimEventNull implements SimEvent {
    @Override public SimEvent setSimulation(Simulation simulation) { return this; }
    @Override public Type getType() { return Type.NULL; }
    @Override public SimEntity getDestination() { return SimEntity.NULL; }
    @Override public SimEntity getSource() {
        return SimEntity.NULL;
    }
    @Override public double getEndWaitingTime() { return 0; }
    @Override public SimEntity scheduledBy() {
        return SimEntity.NULL;
    }
    @Override public int getTag() {
        return 0;
    }
    @Override public Object getData() {
        return 0;
    }
    @Override public SimEvent setSource(SimEntity source) {
        return this;
    }
    @Override public SimEvent setDestination(SimEntity destination) {
        return this;
    }
    @Override public double getTime() {
        return 0;
    }
    @Override public EventListener<EventInfo> getListener() { return EventListener.NULL; }
    @Override public int compareTo(SimEvent evt) {
        return 0;
    }
    @Override public long getSerial() {
        return 0;
    }
    @Override public void setSerial(long serial) {/**/}
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
}
