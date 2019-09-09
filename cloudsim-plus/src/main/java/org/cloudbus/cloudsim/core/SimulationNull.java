package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * A class that implements the Null Object Design Pattern for {@link Simulation}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Simulation#NULL
 */
final class SimulationNull implements Simulation {
    @Override public boolean isTerminationTimeSet() { return false; }
    @Override public void abort() {/**/}
    @Override public void addEntity(CloudSimEntity entity) {/**/}
    @Override public SimEvent cancel(SimEntity src, Predicate<SimEvent> predicate) {
        return SimEvent.NULL;
    }
    @Override public boolean cancelAll(SimEntity src, Predicate<SimEvent> predicate) {
        return false;
    }
    @Override public double clock() { return 0.0; }
    @Override public String clockStr() { return ""; }
    @Override public double clockInMinutes() { return 0.0; }
    @Override public double clockInHours() { return 0.0; }
    @Override public SimEvent findFirstDeferred(SimEntity dest, Predicate<SimEvent> predicate) { return SimEvent.NULL; }
    @Override public Calendar getCalendar() {
        return Calendar.getInstance();
    }
    @Override public CloudInformationService getCloudInfoService() {
        return null;
    }
    @Override public List<SimEntity> getEntityList() { return Collections.emptyList(); }
    @Override public double getMinTimeBetweenEvents() {
        return 0;
    }
    @Override public int getNumEntities() {
        return 0;
    }
    @Override public boolean removeOnEventProcessingListener(EventListener<SimEvent> listener) {
        return false;
    }
    @Override public Simulation addOnSimulationPauseListener(EventListener<EventInfo> listener) {
        return this;
    }
    @Override public Simulation addOnSimulationStartListener(EventListener<EventInfo> listener) { return this; }
    @Override public boolean removeOnSimulationPauseListener(EventListener<EventInfo> listener) {
        return false;
    }
    @Override public boolean isPaused() {
        return false;
    }
    @Override public void pauseEntity(SimEntity src, double delay) {/**/}
    @Override public boolean pause() {
        return false;
    }
    @Override public boolean pause(double time) {
        return false;
    }
    @Override public boolean resume() {
        return false;
    }
    @Override public boolean isRunning() {
        return false;
    }
    @Override public SimEvent select(SimEntity dest, Predicate<SimEvent> predicate) { return SimEvent.NULL; }
    @Override public void send(SimEvent evt) {/**/}
    @Override public void send(SimEntity src, SimEntity dest, double delay, int tag, Object data) {/**/}
    @Override public void sendFirst(SimEvent evt) {/**/}
    @Override public void sendFirst(SimEntity src, SimEntity dest, double delay, int tag, Object data) {/**/}
    @Override public void sendNow(SimEntity src, SimEntity dest, int tag, Object data) {/**/}
    @Override public double runFor(double interval) { return 0; }
    @Override public Simulation addOnEventProcessingListener(EventListener<SimEvent> listener) {
        return this;
    }
    @Override public Simulation addOnClockTickListener(EventListener<EventInfo> listener) {
        return this;
    }
    @Override public boolean removeOnClockTickListener(EventListener<? extends EventInfo> listener) {
        return false;
    }
    @Override public double start() throws RuntimeException { return 0; }
    @Override public void startSync() { /**/ }
    @Override public boolean isTimeToTerminateSimulationUnderRequest() { return false; }
    @Override public boolean terminate() {
        return false;
    }
    @Override public boolean terminateAt(double time) {
        return false;
    }
    @Override public void wait(CloudSimEntity src, Predicate<SimEvent> predicate) {/**/}
    @Override public NetworkTopology getNetworkTopology() { return NetworkTopology.NULL; }
    @Override public void setNetworkTopology(NetworkTopology networkTopology) {/**/}
    @Override public long getNumberOfFutureEvents(Predicate<SimEvent> predicate) { return 0; }
    @Override public double getLastCloudletProcessingUpdate() { return 0; }
    @Override public void setLastCloudletProcessingUpdate(double lastCloudletProcessingUpdate) {/**/}
}
