/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core.events;

import org.cloudbus.cloudsim.core.CloudSim;

/**
 * This class implements the future event queue used by {@link CloudSim}.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class FutureQueue extends SortedQueue {

    /** @see #getSerial() */
    private long serial;

    /** @see #getMaxEventsNumber() */
    private long maxEventsNumber;

    @Override
    public void addEvent(final SimEvent newEvent) {
        newEvent.setSerial(serial++);
        super.addEvent(newEvent);
        maxEventsNumber = Math.max(maxEventsNumber, size());
    }

    @Override
    public void addEventFirst(final SimEvent newEvent) {
        super.addEventFirst(newEvent);
    }

    /** Gets an incremental number used for {@link SimEvent#getSerial()} event attribute. */
    public long getSerial() {
        return serial;
    }

    /**
     * Maximum number of events that have ever existed at the same time
     * inside the queue.
     */
    public long getMaxEventsNumber() {
        return maxEventsNumber;
    }
}
