package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.core.events.SimEvent;

/**
 * A class that implements the Null Object Design Pattern for {@link SimEntity}
 * class.
 * @author Manoel Campos da Silva Filho
 * @see SimEntity#NULL
 */
final class SimEntityNull implements SimEntity {
    @Override public int compareTo(SimEntity entity) { return 0; }
    @Override public State getState() { return State.FINISHED; }
    @Override public SimEntity setState(State state) { return this; }
    @Override public boolean isStarted() { return false; }
    @Override public boolean isAlive() { return false; }
    @Override public boolean isFinished() { return false; }
    @Override public Simulation getSimulation() { return Simulation.NULL; }
    @Override public SimEntity setSimulation(Simulation simulation) { return this; }
    @Override public void processEvent(SimEvent evt) {/**/}
    @Override public boolean schedule(SimEvent evt) { return false; }
    @Override public boolean schedule(SimEntity dest, double delay, int tag, Object data) { return false; }
    @Override public boolean schedule(double delay, int tag, Object data) { return false; }
    @Override public boolean schedule(SimEntity dest, double delay, int tag) {/**/
        return false;
    }
    @Override public void run() {/**/}
    @Override public void start() {/**/}
    @Override public void shutdownEntity() {/**/}
    @Override public SimEntity setName(String newName) throws IllegalArgumentException { return this; }
    @Override public String getName() {  return ""; }
    @Override public long getId() { return 0; }
}
