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
 * A predicate which selects events coming from specific registered entities.<br>
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 * @see PredicateNotFrom
 * @see Predicate
 */
public class PredicateFrom implements Predicate<SimEvent> {

	/** The IDs of source entities to check the reception of events from. */
	private final List<Integer> ids;

	/**
	 * Constructor used to select events that were sent by a specific entity.
	 *
	 * @param sourceId the id number of the source entity
	 */
	public PredicateFrom(int sourceId) {
        this.ids = new ArrayList<>(1);
        this.ids.add(sourceId);
    }

	/**
	 * Constructor used to select events that were sent by any entity from a given set.
	 *
	 * @param sourceIds the set of id numbers of the source entities
	 */
	public PredicateFrom(int[] sourceIds) {
        this.ids = Arrays.stream(sourceIds).boxed().collect(Collectors.toList());
	}

	/**
	 * Matches any event received from the registered sources.
	 *
	 * @param ev {@inheritDoc}
	 * @return {@inheritDoc}
     * @see #ids
	 */
	@Override
	public boolean test(SimEvent ev) {
        return ids.stream().anyMatch(id -> id == ev.getSource());
	}

}
