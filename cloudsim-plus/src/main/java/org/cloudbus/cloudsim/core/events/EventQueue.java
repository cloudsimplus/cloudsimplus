package org.cloudbus.cloudsim.core.events;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * An interface to be implemented by event queues.
 *
 * @author Manoel Campos da Silva Filho
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
