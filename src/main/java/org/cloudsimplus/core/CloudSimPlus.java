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

import lombok.NonNull;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Starts and manages cloud computing simulations running in the CloudSim Plus framework.
 * @author Manoel Campos da Silva Filho
 */
public class CloudSimPlus extends CloudSim {

    /** Default value for {@link #getMinTimeBetweenEvents()} (in seconds) */
    private static final double DEF_MIN_TIME_BETWEEN_EVENTS = 0.1;

    private final CircularTimeQueue clockQueue;

    private final Set<EventListener<SimEvent>> onEventProcessingListeners;
    private final Set<EventListener<EventInfo>> onSimulationPauseListeners;
    private final Set<EventListener<EventInfo>> onClockTickListeners;
    private final Set<EventListener<EventInfo>> onSimulationStartListeners;

    /**
     * Creates a CloudSim Plus simulation.
     * Internally, it creates a {@link CloudInformationService}.
     *
     * @see CloudInformationService
     * @see #CloudSimPlus(double)
     */
    public CloudSimPlus() {
        this(DEF_MIN_TIME_BETWEEN_EVENTS);
    }

    /**
     * Creates a CloudSim Plus simulation that tracks events happening in a time interval
     * as little as the minTimeBetweenEvents parameter.
     * Internally it creates a {@link CloudInformationService}.
     *
     * @param minTimeBetweenEvents the minimal period between events.
     * Events within shorter periods after the last event are discarded.
     * @see CloudInformationService
     */
    public CloudSimPlus(final double minTimeBetweenEvents) {
        super(minTimeBetweenEvents);
        this.onEventProcessingListeners = new HashSet<>();
        this.onSimulationPauseListeners = new HashSet<>();
        this.onClockTickListeners = new HashSet<>();
        this.onSimulationStartListeners = new HashSet<>();
        this.clockQueue = new CircularTimeQueue(this);
    }

    @Override
    public double runFor(final double interval) {
        final double until = interval == Double.MAX_VALUE ? interval : clock() + interval;

        if(!processEvents(until)){
            /* If no event happening up to the given time, increases the clock
             * so that when the runFor method is called again with the new clock,
             * some events may be processed that time.
             * If some event is processed, the clock is automatically increased.*/
            setClock(until);

            if(noFutureEvents()){
                finish();
            }
        }

        return clock();
    }

    @Override
    protected void processEvent(SimEvent evt) {
        super.processEvent(evt);
        for (final var listener : onEventProcessingListeners) {
            listener.update(evt);
        }
    }

    @Override
    protected boolean doPause() {
        final boolean wasPaused = super.doPause();
        if(wasPaused)
            notifyEventListeners(onSimulationPauseListeners, clock());

        return wasPaused;
    }

    /**
     * {@inheritDoc}
     * @see #onClockTickListeners
     */
    @Override
    protected double setClock(double newTime) {
        final double oldTime = super.setClock(newTime);
        clockQueue.tryToUpdateListeners(previousTime -> notifyEventListeners(onClockTickListeners, previousTime));
        return oldTime;
    }

    private void notifyEventListeners(final Set<EventListener<EventInfo>> listeners, final double clock) {
        listeners.forEach(listener -> listener.update(EventInfo.of(listener, clock)));
    }

    @Override
    protected void notifyOnSimulationStartListeners() {
        if(!onSimulationStartListeners.isEmpty() && clock() > 0) {
            notifyEventListeners(onSimulationStartListeners, clock());
            //Since the simulation starts just once, clear the listeners to avoid them to be notified again
            onSimulationStartListeners.clear();
        }
    }

    @Override
    public Simulation addOnSimulationPauseListener(@NonNull final EventListener<EventInfo> listener) {
        this.onSimulationPauseListeners.add(listener);
        return this;
    }

    @Override
    public Simulation addOnSimulationStartListener(@NonNull final EventListener<EventInfo> listener) {
        this.onSimulationStartListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnSimulationPauseListener(@NonNull final EventListener<EventInfo> listener) {
        return this.onSimulationPauseListeners.remove(listener);
    }

    @Override
    public Simulation addOnEventProcessingListener(@NonNull final EventListener<SimEvent> listener) {
        this.onEventProcessingListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnEventProcessingListener(@NonNull final EventListener<SimEvent> listener) {
        return onEventProcessingListeners.remove(listener);
    }

    @Override
    public Simulation addOnClockTickListener(@NonNull final EventListener<EventInfo> listener) {
        onClockTickListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnClockTickListener(@NonNull final EventListener<? extends EventInfo> listener) {
        return onClockTickListeners.remove(listener);
    }
}
