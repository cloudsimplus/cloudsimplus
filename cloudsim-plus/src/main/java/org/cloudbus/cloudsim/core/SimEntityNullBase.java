package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.core.events.SimEvent;

/**
 * A base interface used internally to implement the Null Object Design Pattern
 * for interfaces extending {@link SimEntity}.
 * It's just used to avoid the boilerplate code in such Null Object implementations.
 *
 * @author Manoel Campos da Silva Filho
 * @see SimEntity#NULL
 */
public interface SimEntityNullBase extends SimEntity {
    @Override default State getState() { return State.FINISHED; }
    @Override default SimEntity setState(State state) { return this; }
    @Override default boolean isStarted() { return false; }
    @Override default boolean isAlive() { return false; }
    @Override default boolean isFinished() { return false; }
    @Override default Simulation getSimulation() { return Simulation.NULL; }
    @Override default SimEntity setSimulation(Simulation simulation) { return this; }
    @Override default void processEvent(SimEvent evt) {/**/}
    @Override default boolean schedule(SimEvent evt) { return false; }
    @Override default boolean schedule(SimEntity dest, double delay, int tag, Object data) { return false; }
    @Override default boolean schedule(double delay, int tag, Object data) { return false; }
    @Override default boolean schedule(SimEntity dest, double delay, int tag) { return false; }
    @Override default boolean schedule(int tag, Object data) { return false; }
    @Override default boolean schedule(double delay, int tag) { return false; };
    @Override default void run() {/**/}
    @Override default void start() {/**/}
    @Override default void shutdownEntity() {/**/}
    @Override default SimEntity setName(String newName) throws IllegalArgumentException { return this; }
    @Override default String getName() {  return ""; }
    @Override default long getId() { return -1; }
}
