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
import org.cloudsimplus.listeners.Listeners;

import java.util.List;

/// Starts and manages cloud computing simulations running in the CloudSim Plus framework.
/// @author Manoel Campos da Silva Filho
public class CloudSimPlus extends CloudSim {

    /// Default value for [#getMinTimeBetweenEvents()] (in seconds)
    private static final double DEF_MIN_TIME_BETWEEN_EVENTS = 0.1;

    private final CircularTimeQueue clockQueue;

    private List<EventListener<SimEvent>> onEventProcessingListeners = List.of();
    private List<EventListener<EventInfo>> onSimulationPauseListeners = List.of();
    private List<EventListener<EventInfo>> onClockTickListeners = List.of();
    private List<EventListener<EventInfo>> onSimulationStartListeners = List.of();

    /// Creates a CloudSim Plus simulation.
    /// Internally, it creates a [CloudInformationService].
    ///
    /// @see CloudInformationService
    /// @see #CloudSimPlus(double)
    public CloudSimPlus() {
        this(DEF_MIN_TIME_BETWEEN_EVENTS);
    }

    /// Creates a CloudSim Plus simulation that tracks events happening in a time interval
    /// as little as the minTimeBetweenEvents parameter.
    /// Internally it creates a [CloudInformationService].
    ///
    /// @param minTimeBetweenEvents the minimal period between events.
    /// Events within shorter periods after the last event are discarded.
    /// @see CloudInformationService
    public CloudSimPlus(final double minTimeBetweenEvents) {
        super(minTimeBetweenEvents);
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

    /// {@inheritDoc}
    /// @see #onClockTickListeners
    @Override
    protected double setClock(double newTime) {
        final double oldTime = super.setClock(newTime);
        clockQueue.tryToUpdateListeners(previousTime -> notifyEventListeners(onClockTickListeners, previousTime));
        return oldTime;
    }

    private void notifyEventListeners(final List<EventListener<EventInfo>> listeners, final double clock) {
        for (final var listener : listeners) {
            listener.update(EventInfo.of(listener, clock));
        }
    }

    @Override
    protected void notifyOnSimulationStartListeners() {
        if(!onSimulationStartListeners.isEmpty() && clock() > 0) {
            notifyEventListeners(onSimulationStartListeners, clock());
            //Since the simulation starts just once, clear the listeners to avoid them to be notified again
            onSimulationStartListeners = List.of();
        }
    }

    @Override
    public Simulation addOnSimulationPauseListener(@NonNull final EventListener<EventInfo> listener) {
        onSimulationPauseListeners = Listeners.add(onSimulationPauseListeners, listener);
        return this;
    }

    @Override
    public Simulation addOnSimulationStartListener(@NonNull final EventListener<EventInfo> listener) {
        onSimulationStartListeners = Listeners.add(onSimulationStartListeners, listener);
        return this;
    }

    @Override
    public boolean removeOnSimulationPauseListener(@NonNull final EventListener<EventInfo> listener) {
        final var oldList = onSimulationPauseListeners;
        onSimulationPauseListeners = Listeners.remove(oldList, listener);
        return onSimulationPauseListeners.size() != oldList.size();
    }

    @Override
    public Simulation addOnEventProcessingListener(@NonNull final EventListener<SimEvent> listener) {
        onEventProcessingListeners = Listeners.add(onEventProcessingListeners, listener);
        return this;
    }

    @Override
    public boolean removeOnEventProcessingListener(@NonNull final EventListener<SimEvent> listener) {
        final var oldList = onEventProcessingListeners;
        onEventProcessingListeners = Listeners.remove(oldList, listener);
        return onEventProcessingListeners.size() != oldList.size();
    }

    @Override
    public Simulation addOnClockTickListener(@NonNull final EventListener<EventInfo> listener) {
        onClockTickListeners = Listeners.add(onClockTickListeners, listener);
        return this;
    }

    @Override
    public boolean removeOnClockTickListener(@NonNull final EventListener<? extends EventInfo> listener) {
        final var oldList = onClockTickListeners;
        onClockTickListeners = Listeners.remove(oldList, listener);
        return onClockTickListeners.size() != oldList.size();
    }
}
