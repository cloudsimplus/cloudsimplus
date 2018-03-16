package org.cloudbus.cloudsim.core.events;

import org.cloudbus.cloudsim.core.Simulation;

/**
 * A class that implements the Null Object Design Pattern for {@link SimEvent}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see SimEvent#NULL
 */
final class SimEventNull implements SimEvent {
    @Override public Type getType() { return Type.NULL; }
    @Override public int getDestination() {
        return 0;
    }
    @Override public int getSource() {
        return 0;
    }
    @Override public double eventTime() {
        return 0;
    }
    @Override public double endWaitingTime() {
        return 0;
    }
    @Override public int scheduledBy() {
        return 0;
    }
    @Override public int getTag() {
        return 0;
    }
    @Override public Object getData() {
        return 0;
    }
    @Override public SimEvent setSource(int source) {
        return this;
    }
    @Override public SimEvent setDestination(int destination) {
        return this;
    }
    @Override public double getTime() {
        return 0;
    }
    @Override public int compareTo(SimEvent o) {
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
