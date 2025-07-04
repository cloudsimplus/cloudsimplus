/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.core.events;

import lombok.Getter;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An {@link EventQueue} that stores future simulation events.
 * It uses a {@link TreeSet} to ensure the events
 * are stored ordered. Using a {@link java.util.LinkedList}
 * as defined by {@link DeferredQueue} to improve performance
 * doesn't work for this queue.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @see java.util.TreeSet
 * @since CloudSim Toolkit 1.0
 */
public final class FutureQueue implements EventQueue {

    /**
     * The sorted set of events.
     */
    private final SortedSet<SimEvent> sortedSet = new TreeSet<>();

    /** An incremental number used for {@link SimEvent#getSerial()} event attribute. */
    @Getter
    private long serial;

    private long lowestSerial;

    /**
     * Maximum number of events that have ever existed at the same time
     * inside the queue.
     */
    @Getter
    private long maxEventsNumber;

    @Override
    public void addEvent(final SimEvent newEvent) {
        newEvent.setSerial(serial++);
        sortedSet.add(newEvent);
        maxEventsNumber = Math.max(maxEventsNumber, sortedSet.size());
    }

    /**
     * Adds a new event to the head of the queue.
     *
     * @param newEvent The event to be put in the queue.
     */
    public void addEventFirst(final SimEvent newEvent) {
        newEvent.setSerial(--lowestSerial);
        sortedSet.add(newEvent);
    }

    @Override
    public Iterator<SimEvent> iterator() {
        return sortedSet.iterator();
    }

    @Override
    public Stream<SimEvent> stream() {
        return sortedSet.stream();
    }

    @Override
    public int size() {
        return sortedSet.size();
    }

    @Override
    public boolean isEmpty() {
        return sortedSet.isEmpty();
    }

    /**
     * Removes the event from the queue.
     *
     * @param event the event
     * @return true if successful; false if not event was removed
     */
    public boolean remove(final SimEvent event) {
        return sortedSet.remove(event);
    }

    /**
     * Removes all the events from the queue.
     *
     * @param events the events
     * @return true if successful; false if no event was removed
     */
    public boolean removeAll(final Collection<SimEvent> events) {
        return sortedSet.removeAll(events);
    }

    public boolean removeIf(final Predicate<SimEvent> predicate){
        return sortedSet.removeIf(predicate);
    }

    @Override
    public SimEvent first() throws NoSuchElementException {
        return sortedSet.first();
    }

    /**
     * Clears all the events in the queue.
     */
    public void clear() {
        sortedSet.clear();
    }
}
