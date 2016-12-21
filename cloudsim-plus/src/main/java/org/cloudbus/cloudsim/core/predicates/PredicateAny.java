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

/**
 * A predicate which will match any event on the deferred event queue.
 * See the publicly accessible instance of this predicate in
 * {@link org.cloudbus.cloudsim.core.CloudSim#SIM_ANY}, so no new instances needs to be created. <br>
 *
 * @author Marcos Dias de Assuncao
 * @see Predicate
 * @since CloudSim Toolkit 1.0
 */
public class PredicateAny implements Predicate<SimEvent> {

    /**
     * Considers that any event received by the predicate will match.
     *
     * @param ev {@inheritDoc}
     * @return always true to indicate that any received event is accepted
     */
    @Override
    public boolean test(SimEvent ev) {
        return true;
    }
}
