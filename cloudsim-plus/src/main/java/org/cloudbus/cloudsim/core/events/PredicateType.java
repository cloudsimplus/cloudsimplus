/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core.events;

import java.util.function.Predicate;

/**
 * A predicate to select events with specific {@link SimEvent#getTag() tag}.
 *
 * @author Marcos Dias de Assuncao
 * @see Predicate
 * @since CloudSim Toolkit 1.0
 */
public class PredicateType implements Predicate<SimEvent> {

    private final int tag;

    /**
     * Constructor used to select events with the given tag value.
     *
     * @param tag an event {@link SimEvent#getTag() tag} value
     */
    public PredicateType(final int tag) {
        this.tag = tag;
    }

    /**
     * Matches any event that has one of the specified {@link #tag}.
     *
     * @param evt {@inheritDoc}
     * @return {@inheritDoc}
     * @see #tag
     */
    @Override
    public boolean test(final SimEvent evt) {
        return tag == evt.getTag();
    }

}
