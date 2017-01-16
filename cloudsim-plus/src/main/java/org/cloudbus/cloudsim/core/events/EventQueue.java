/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core.events;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * An interface to be implemented by event queues.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface EventQueue {
    /**
     * Adds a new event to the queue. Adding a new event to the queue preserves the temporal order of
     * the events in the queue.
     *
     * @param newEvent The event to be put in the queue.
     */
    void addEvent(SimEvent newEvent);

    /**
     * Returns an iterator to the elements into the queue.
     *
     * @return the iterator
     */
    Iterator<SimEvent> iterator();

    /**
     * Returns a stream to the elements into the queue.
     *
     * @return the stream
     */
    Stream<SimEvent> stream();

    /**
     * Returns the size of this event queue.
     *
     * @return the size
     */
    int size();

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Gets the first element of the queue.
     *
     * @return the first element
     * @throws NoSuchElementException when the queue is empty
     */
    SimEvent first() throws NoSuchElementException;
}
