/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core.predicates;

import org.cloudbus.cloudsim.core.events.SimEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A predicate which selects events that have not been sent by specific entities.
 *
 * @author Marcos Dias de Assuncao
 * @see PredicateFrom
 * @see Predicate
 * @since CloudSim Toolkit 1.0
 */
public class PredicateNotFrom implements Predicate<SimEvent> {

    /**
     * The IDs of source entities to check if events were not sent from.
     */
    private final List<Integer> ids;

    /**
     * Constructor used to select events that were not sent by a specific entity.
     *
     * @param sourceId the id number of the source entity
     */
    public PredicateNotFrom(int sourceId) {
        this.ids = new ArrayList<>(1);
        this.ids.add(sourceId);
    }

    /**
     * Constructor used to select events that were not sent by any entity from a given set.
     *
     * @param sourceIds the set of id numbers of the source entities
     */
    public PredicateNotFrom(int[] sourceIds) {
        this.ids = Arrays.stream(sourceIds).boxed().collect(Collectors.toList());
    }

    /**
     * Matches any event <b>NOT</b> received from the registered sources.
     *
     * @param ev {@inheritDoc}
     * @return {@inheritDoc}
     * @see #ids
     */
    @Override
    public boolean test(SimEvent ev) {
        return ids.stream().noneMatch(id -> id == ev.getSource());
    }

}
