package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.core.events.SimEvent;

/**
 * A class that implements the Null Object Design Pattern for {@link SimEntity}
 * class.
 * @author Manoel Campos da Silva Filho
 * @see SimEntity#NULL
 */
final class SimEntityNull implements SimEntity {
    @Override public int compareTo(SimEntity o) { return 0; }
    @Override public SimEntity setState(State state) { return this; }
    @Override public boolean isStarted() { return false; }
    @Override public boolean isAlive() { return false; }
    @Override public boolean isFinished() { return false; }
    @Override public Simulation getSimulation() { return Simulation.NULL; }
    @Override public SimEntity setSimulation(Simulation simulation) { return this; }
    @Override public void processEvent(SimEvent ev) {/**/}
    @Override public void schedule(SimEntity dest, double delay, int tag) {/**/}
    @Override public void run() {/**/}
    @Override public void start() {/**/}
    @Override public void shutdownEntity() {/**/}
    @Override public SimEntity setName(String newName) throws IllegalArgumentException { return this; }
    @Override public String getName() {  return ""; }
    @Override public int getId() { return 0; }
    @Override public void setLog(boolean log) {/**/}
}
