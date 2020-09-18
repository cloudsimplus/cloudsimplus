package org.cloudbus.cloudsim.core.events;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An {@link EventQueue} that orders {@link SimEvent}s based on their time attribute.
 * Since a new event's time is usually equal or higher than the previous event
 * in regular simulations, this classes uses a {@link LinkedList} instead
 * of a {@link java.util.SortedSet} such as {@link java.util.TreeSet}
 * because the {@link LinkedList} provides constant O(1) complexity
 * to add elements to the end.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.4.2
 */
public class DeferredQueue implements EventQueue {
    /**
     * Despite the events are sorted by time and there are
     * sorted collections such as {@link java.util.SortedSet},
     * since the time of a new event is usually higher than the previous
     * one, in such a case, the {@link LinkedList#add(Object)} provides
     * better performance, which is O(1).
     */
    private final List<SimEvent> list = new LinkedList<>();

    /**
     * The max time that an added event is scheduled.
     */
    private double maxTime = -1;

    private int addedToTail;
    private int addedToMiddle;
    private int maxSize;

    /**
     * Adds a new event to the queue, preserving the temporal order
     * of the events.
     *
     * @param newEvent the event to be added to the queue.
     */
    public void addEvent(final SimEvent newEvent) {
        // The event has to be inserted as the last of all events
        // with the same event_time(). Yes, this matters.
        final double eventTime = newEvent.getTime();
        maxSize = Math.max(maxSize, list.size());
        if (eventTime >= maxTime) {
            list.add(newEvent);
            maxTime = eventTime;
            addedToTail++;
            return;
        }

        /*
         * Adds an event in some position from the tail of the list.
         * If the event time is smaller than the maxTime, traverses the list
         * to find the place to insert the event.
         * It uses a reverse iterator because usually in such cases,
         * the time of the new event is close to the last events.
         * Starting from the tail of the list will ensure the lowest number
         * of iterations on the best cases.
         * */
        final ListIterator<SimEvent> reverseIterator = list.listIterator(list.size() - 1);
        while (reverseIterator.hasPrevious()) {
            if (reverseIterator.previous().getTime() <= eventTime) {
                reverseIterator.next();
                reverseIterator.add(newEvent);
                addedToMiddle++;
                return;
            }
        }

        list.add(newEvent);
    }

    /**
     * Returns an iterator to the events in the queue.
     *
     * @return the iterator
     */
    public Iterator<SimEvent> iterator() {
        return list.iterator();
    }

    /**
     * Returns a stream to the elements into the queue.
     *
     * @return the stream
     */
    public Stream<SimEvent> stream() {
        return list.stream();
    }

    /**
     * Returns the size of this event queue.
     *
     * @return the number of events in the queue.
     */
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Removes the event from the queue.
     *
     * @param event the event
     * @return true, if successful
     */
    public boolean remove(final SimEvent event) {
        return list.remove(event);
    }

    /**
     * Removes all the events from the queue.
     *
     * @param events the events
     * @return true, if successful
     */
    public boolean removeAll(final Collection<SimEvent> events) {
        return list.removeAll(events);
    }

    public boolean removeIf(final Predicate<SimEvent> predicate) {
        return list.removeIf(predicate);
    }

    /**
     * Clears the queue.
     */
    public void clear() {
        list.clear();
    }

    @Override
    public SimEvent first() throws NoSuchElementException {
        if (list.isEmpty()) {
            throw new NoSuchElementException("The Deferred Queue is empty.");
        }

        return list.get(0);
    }

    /**
     * Keeps track of the total number of events
     * added to the tail of the queue,
     * just for debug purpose.
     */
    public int getAddedToTail() {
        return addedToTail;
    }

    /**
     * Keeps track of the total number of events
     * added to the middle of the queue,
     * just for debug purpose.
     */
    public int getAddedToMiddle() {
        return addedToMiddle;
    }

    /**
     * Keeps track of the maximum number of events
     * added to the queue, just for debug purpose.
     */
    public int getMaxSize() {
        return maxSize;
    }
}
