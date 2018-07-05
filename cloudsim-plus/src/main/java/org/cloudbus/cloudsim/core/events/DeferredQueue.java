/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core.events;

import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;
import java.util.stream.Stream;

/**
 * This class implements the deferred event queue used by {@link CloudSim}.
 * The event queue uses a linked list to store the events.
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 * @see CloudSim
 * @see SimEvent
 */
public class DeferredQueue implements EventQueue {

	/** The list of events. */
	private final List<SimEvent> list = new LinkedList<>();

	/** The max time that an added event is scheduled. */
	private double maxTime = -1;

	/**
	 * Adds a new event to the queue. Adding a new event to the queue preserves the temporal order
	 * of the events.
	 *
	 * @param newEvent The event to be added to the queue.
	 */
	public void addEvent(final SimEvent newEvent) {
		// The event has to be inserted as the last of all events
		// with the same event_time(). Yes, this matters.
		final double eventTime = newEvent.getTime();
		if (eventTime >= maxTime) {
			list.add(newEvent);
			maxTime = eventTime;
			return;
		}

		final ListIterator<SimEvent> iterator = list.listIterator();
		SimEvent event;
		while (iterator.hasNext()) {
			event = iterator.next();
			if (event.getTime() > eventTime) {
				iterator.previous();
				iterator.add(newEvent);
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


    @Override
    public SimEvent first() throws NoSuchElementException {
	    if(list.isEmpty())
	        throw new NoSuchElementException("The Deferred Queue is empty.");

        return list.get(0);
    }

    /**
	 * Clears the queue.
	 */
	public void clear() {
		list.clear();
	}

}
