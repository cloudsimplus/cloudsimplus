/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core.events;

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
public sealed interface EventQueue permits DeferredQueue, FutureQueue {
    /**
     * Adds a new event to the queue, preserving the temporal order of the events.
     *
     * @param newEvent The event to be added
     */
    void addEvent(SimEvent newEvent);

    /**
     * @return an iterator to the elements into the queue.
     */
    Iterator<SimEvent> iterator();

    /**
     * @return a stream to the elements into the queue.
     */
    Stream<SimEvent> stream();

    /**
     * @return the size of this event queue.
     */
    int size();

    /**
     * {@return true if the queue is empty, false otherwise}
     */
    boolean isEmpty();

    /**
     * @return the first element of the queue.
     * @throws NoSuchElementException when the queue is empty
     */
    SimEvent first() throws NoSuchElementException;
}
