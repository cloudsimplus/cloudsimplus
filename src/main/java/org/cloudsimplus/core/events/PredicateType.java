/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core.events;

import java.util.function.Predicate;

/**
 * A predicate to select events with a specific {@link SimEvent#getTag() tag}.
 *
 * @author Marcos Dias de Assuncao
 * @param tag tag to match events
 * @see Predicate
 * @since CloudSim Toolkit 1.0
 */
public record PredicateType(int tag) implements Predicate<SimEvent> {

    /**
     * Matches any event that has the specified {@link #tag}.
     *
     * @param evt the event to math with the specified {@link #tag()}
     * @return {@inheritDoc}
     * @see #tag()
     */
    @Override
    public boolean test(final SimEvent evt) {
        return tag == evt.getTag();
    }
}
