/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.core;

import org.cloudsimplus.listeners.EventListener;

import java.util.function.Consumer;

/**
 * A static-sized queue that removes the oldest time value when it's full.
 * It keeps track of the last two simulation events time to check whether
 * they happened at different times, meaning the simulation clock has changed
 * and the simulation Clock Tick Listeners must be fired
 *
 * @since CloudSim Plus 6.0.0
 * @author Manoel Campos da Silva Filho
 * @see Simulation#addOnClockTickListener(EventListener)
 */
final class CircularTimeQueue {
    /**
     * The simulation clock time queue.
     * It's an array that works as a circular queue with capacity for just 2 elements
     * (defined in the constructor). When a new element is added to the queue,
     * the first element is removed to open space for that new one.
     * This queue stores the last 2 simulation clock values.
     * It is used to know when it's time to notify listeners that
     * the simulation clock has increased.
     *
     * <p>The head (value at index 0) of the queue is the oldest simulation time stored,
     * the tail (value at index 1) is the newest one.
     * Such a structure is required because multiple events
     * can be received consecutively for the same simulation time.
     * When the head of the queue is lower than the tail,
     * it means the last event for that head time
     * was already processed and a more recent event
     * has just arrived.
     * </p>
     */
    private final double[] queue;
    private final Simulation simulation;

    /**
     * Last time the clock has effectively changed and the
     * Clock Tick Listeners were updated.
     */
    private double lastClockTickUpdate;

    /**
     * Creates the time queue.
     * @param simulation the simulation instance.
     */
    CircularTimeQueue(final Simulation simulation){
        this.simulation = simulation;
        this.lastClockTickUpdate = simulation.getMinTimeBetweenEvents();
        this.queue = new double[]{lastClockTickUpdate, lastClockTickUpdate};
    }

    /**
     * Tries to notify all Listeners about onClockTick event when the simulation clock changes.
     * If multiple events are received consecutively but for the same simulation time,
     * it will only notify the Listeners when the last event for that time is received.
     * It ensures that when Listeners receive the notification, all the events
     * for such a simulation time were already processed. This way,
     * Listeners will have access to the most up-to-date simulation state.
     *
     * @param notifyClockTickListeners a {@link Consumer} that will receive the <b>previous clock time</b>
     *                                and update the listeners for that time.
     */
    void tryToUpdateListeners(final Consumer<Double> notifyClockTickListeners) {
        if(!isTimeToUpdateClockTickListeners()) {
            return;
        }

        addCurrentTime();
        if (isPreviousTimeOlder()) {
            lastClockTickUpdate = previous();
            notifyClockTickListeners.accept(lastClockTickUpdate);
        }
    }

    /**
     * Adds the current clock time to the queue, making it rotate, i.e.:
     * removing the first time value, then adding a new one.
     */
    private void addCurrentTime() {
        queue[0] = queue[1]; // moves the previous newest time to the oldest position
        queue[1] = simulation.clock(); // store the current simulation time as the newest one
    }

    /**
     * @return true if the previous stored time is older than the newest one, false otherwise.
     */
    private boolean isPreviousTimeOlder(){
        return queue[0] < queue[1];
    }

    /**
     * @return the previous stored time (in seconds).
     */
    private double previous(){
        return queue[0];
    }

    /**
     * @return true if the simulation current time is newer than the last Clock Tick Listener update,
     *         indicating those listeners should be updated; false otherwise.
     */
    private boolean isTimeToUpdateClockTickListeners(){
        return simulation.clock() > lastClockTickUpdate;
    }
}
