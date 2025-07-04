package org.cloudsimplus.core.events;

import lombok.Getter;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An {@link EventQueue} that orders {@link SimEvent}s based on their time attribute.
 * Since the time of a new event is usually equal or higher than the previous event
 * in regular simulations, this class uses a {@link LinkedList} instead
 * of a {@link java.util.SortedSet} such as {@link java.util.TreeSet}
 * because the {@link LinkedList} provides constant O(1) complexity
 * to add elements to the end.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.4.2
 */
public final class DeferredQueue implements EventQueue {
    /**
     * Despite the events are sorted by time, and there are
     * sorted collections such as {@link java.util.SortedSet},
     * since the time of a new event is usually higher than the previous
     * one, in such a case, the {@link LinkedList#add(Object)} provides
     * better performance, which is O(1).
     */
    private final List<SimEvent> eventList = new LinkedList<>();

    /**
     * The max time that an added event is scheduled.
     */
    private double maxTime = -1;

    /**
     * The total number of events added to the tail of the queue,
     * just for debug purpose.
     */
    @Getter
    private int addedToTail;

    /**
     * The total number of events added to the middle of the queue,
     * just for debug purpose.
     */
    @Getter
    private int addedToMiddle;

    /**
     * The maximum number of events added to the queue, just for debug purpose.
     */
    @Getter
    private int maxSize;

    /**
     * Adds a new event to the queue, preserving the temporal order
     * of the events.
     *
     * @param newEvent the event to be added
     */
    public void addEvent(final SimEvent newEvent) {
        // The event has to be inserted as the last of all events
        // with the same event_time(). Yes, this matters.
        final double eventTime = newEvent.getTime();
        maxSize = Math.max(maxSize, eventList.size());
        if (eventTime >= maxTime) {
            eventList.add(newEvent);
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
        final var reverseEvtIterator = eventList.listIterator(eventList.size() - 1);
        while (reverseEvtIterator.hasPrevious()) {
            if (reverseEvtIterator.previous().getTime() <= eventTime) {
                reverseEvtIterator.next();
                reverseEvtIterator.add(newEvent);
                addedToMiddle++;
                return;
            }
        }

        eventList.add(newEvent);
    }

    @Override
    public Iterator<SimEvent> iterator() {
        return eventList.iterator();
    }

    @Override
    public Stream<SimEvent> stream() {
        return eventList.stream();
    }

    @Override
    public int size() {
        return eventList.size();
    }

    @Override
    public boolean isEmpty() {
        return eventList.isEmpty();
    }

    /**
     * Removes the event from the queue.
     *
     * @param event the event
     * @return true if successful; false otherwise
     */
    public boolean remove(final SimEvent event) {
        return eventList.remove(event);
    }

    /**
     * Removes all the given events from the queue.
     *
     * @param events the events to be removed
     * @return true if successful; false otherwise
     */
    public boolean removeAll(final Collection<SimEvent> events) {
        return eventList.removeAll(events);
    }

    /**
     * Removes events that match a given {@link Predicate}.
     * @param predicate the predicate to match
     * @return true if any elements were removed; false otherwise
     */
    public boolean removeIf(final Predicate<SimEvent> predicate) {
        return eventList.removeIf(predicate);
    }

    /**
     * Clears the queue removing all elements.
     */
    public void clear() {
        eventList.clear();
    }

    @Override
    public SimEvent first() throws NoSuchElementException {
        if (eventList.isEmpty()) {
            throw new NoSuchElementException("The Deferred Queue is empty.");
        }

        return eventList.get(0);
    }
}
