/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core.events;

import org.cloudbus.cloudsim.core.CloudSim;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * This class implements the future event queue used by {@link CloudSim}.
 * The event queue uses a {@link TreeSet} in order to store the events.
 *
 * @author Marcos Dias de Assuncao
 * @see java.util.TreeSet
 * @since CloudSim Toolkit 1.0
 */
public class FutureQueue implements EventQueue {

    /**
     * The sorted set of events.
     */
    private final SortedSet<SimEvent> sortedSet = new TreeSet<>();

    /**
     * A incremental number used for {@link SimEvent#getSerial()} event attribute.
     */
    private long serial;

    @Override
    public void addEvent(SimEvent newEvent) {
        newEvent.setSerial(serial++);
        sortedSet.add(newEvent);
    }

    /**
     * Adds a new event to the head of the queue.
     *
     * @param newEvent The event to be put in the queue.
     */
    public void addEventFirst(SimEvent newEvent) {
        newEvent.setSerial(0);
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
     * @return true, if successful
     */
    public boolean remove(SimEvent event) {
        return sortedSet.remove(event);
    }

    /**
     * Removes all the events from the queue.
     *
     * @param events the events
     * @return true, if successful
     */
    public boolean removeAll(Collection<SimEvent> events) {
        return sortedSet.removeAll(events);
    }

    @Override
    public SimEvent first() throws NoSuchElementException {
        return sortedSet.first();
    }

    /**
     * Clears the queue.
     */
    public void clear() {
        sortedSet.clear();
    }

}
