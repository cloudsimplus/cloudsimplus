/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core.predicates;

import org.cloudbus.cloudsim.core.events.SimEvent;

import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * A predicate to select events with specific {@link SimEvent#getTag() tags}.
 *
 * @author Marcos Dias de Assuncao
 * @see PredicateNotType
 * @see Predicate
 * @since CloudSim Toolkit 1.0
 */
public class PredicateType implements Predicate<SimEvent> {

    /**
     * Array of tags to verify if the tag of received events correspond to.
     */
    private final IntStream tags;

    /**
     * Constructor used to select events with the given tag value.
     *
     * @param tag an event {@link SimEvent#getTag() tag} value
     */
    public PredicateType(int tag) {
        tags = IntStream.of(tag);
    }

    /**
     * Constructor used to select events with a tag value equal to any of the specified tags.
     *
     * @param tags the list of {@link SimEvent#getTag() tags}
     */
    public PredicateType(int[] tags) {
        this.tags = IntStream.of(tags);
    }

    /**
     * Matches any event that has one of the specified {@link #tags}.
     *
     * @param ev {@inheritDoc}
     * @return {@inheritDoc}
     * @see #tags
     */
    @Override
    public boolean test(SimEvent ev) {
        return tags.anyMatch(tag -> tag == ev.getTag());
    }

}
