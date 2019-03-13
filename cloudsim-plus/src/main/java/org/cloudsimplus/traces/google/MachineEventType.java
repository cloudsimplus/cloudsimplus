/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.traces.google;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudsimplus.traces.google.GoogleMachineEventsTraceReader.FieldIndex;

/**
 * Defines the type of an event (a line) in the trace file
 * that represents the operation to be performed with the {@link Host}.
 * Each enum instance is a possible value for the {@link FieldIndex#EVENT_TYPE} field.
 *
 * <p>This enum defines a some methods to move the processing logic of each event type
 * to the enum value associated to it. Since the enum includes the {@link #process(GoogleMachineEventsTraceReader)}
 * abstract method, if a new enum value is added, we just need to implement the method for that value.
 * Using such approach we avoid spreading if chains to check which event type a trace line is
 * to call the corresponding process method.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public enum MachineEventType {
    /**
     * 0: A {@link Host} became available to the cluster - all machines in the trace will have an ADD event.
     */
    ADD{
        @Override
        protected boolean process(final GoogleMachineEventsTraceReader reader) {
            final Host host = reader.createHostFromTraceLine();
            host.setStartTime(FieldIndex.TIMESTAMP.getValue(reader));
            return host.getStartTime() == 0 ? reader.addAvailableObject(host) : reader.addLaterAvailableHost(host);
        }
    },

    /**
     * 1: A {@link Host} was removed from the cluster. Removals can occur due to failures or maintenance.
     */
    REMOVE{
        @Override
        protected boolean process(final GoogleMachineEventsTraceReader reader) {
            final Host host = reader.createHostFromTraceLine();
            host.setShutdownTime(FieldIndex.TIMESTAMP.getValue(reader));
            return reader.addHostToRemovalList(host);
        }
    },

    /**
     * 2: A {@link Host} available to the cluster had its available resources changed.
     */
    UPDATE{
        /**
         * Update events aren't being processed yet.
         *
         * @param reader the trace file reader
         * @return always false to indicate the method is not implemented yet
         *
         * @TODO Update events are NOT being processed yet.
         */
        @Override
        protected boolean process(final GoogleMachineEventsTraceReader reader) {
            return false;
        }
    };

    /**
     * Gets an enum instance from its ordinal value.
     * @param ordinal the ordinal value to get the enum instance from
     * @return the enum instance
     */
    public static MachineEventType getValue(final int ordinal){
        return values()[ordinal];
    }

    /**
     * Executes an operation with the Hosts according to the Event Type.
     * Each enum value must implement this method to include its own processing logic.
     *
     * @param reader the trace file reader
     * @return true if trace line for the event type was processed, false otherwise
     */
    protected abstract boolean process(GoogleMachineEventsTraceReader reader);
}
