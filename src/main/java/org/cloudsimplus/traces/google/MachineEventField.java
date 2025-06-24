/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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

import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.util.TimeUtil;

/**
 * The index of each field in the Google Machine Events trace file.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public enum MachineEventField implements TraceField<GoogleMachineEventsTraceReader> {
    /**
     * 0: The index of the field containing the time the event happened (in microsecond).
     */
    TIMESTAMP {
        /**
         * Gets the timestamp converted to milliseconds.
         * @param reader the reader for the trace file
         * @return
         */
        @Override
        public Double getValue(final GoogleMachineEventsTraceReader reader) {
            return TimeUtil.microToSeconds(reader.getFieldDoubleValue(this));
        }
    },

    /**
     * 1: The index of the field containing the machine ID.
     */
    MACHINE_ID {
        @Override
        public Long getValue(final GoogleMachineEventsTraceReader reader) {
            return reader.getFieldLongValue(this);
        }
    },

    /**
     * 2: The index of the field containing the type of event.
     * The possible values for this field are the ordinal values of the enum {@link MachineEventType}.
     */
    EVENT_TYPE {
        @Override
        public Integer getValue(final GoogleMachineEventsTraceReader reader) {
            return reader.getFieldIntValue(this);
        }
    },

    /**
     * 3: The index of the platform ID, which is an opaque string representing
     * the micro-architecture and chipset version of the machine.
     */
    PLATFORM_ID {
        @Override
        public Integer getValue(final GoogleMachineEventsTraceReader reader) {
            return reader.getFieldIntValue(this);
        }
    },

    /**
     * 4: The index of the CPU capacity field in the trace,
     * that represents a percentage (between 0 and 1)
     * of the {@link GoogleMachineEventsTraceReader#getMaxCpuCores()}.
     */
    CPU_CAPACITY {
        /**
         * Gets the actual number of {@link Pe}s (CPU cores) to be assigned to a Host,
         * according the {@link GoogleMachineEventsTraceReader#getMaxCpuCores()}.
         */
        @Override
        public Integer getValue(final GoogleMachineEventsTraceReader reader) {
            final double fieldValue = reader.getFieldDoubleValue(this);
            return (int) Math.round(fieldValue * reader.getMaxCpuCores());
        }
    },

    /**
     * 5: The index of the RAM capacity field in the trace,
     * that represents a percentage (between 0 and 1)
     * of the {@link GoogleMachineEventsTraceReader#getMaxRamCapacity()} ()}.
     */
    RAM_CAPACITY {
        /**
         * Gets the actual RAM capacity to be assigned to a Host,
         * according the {@link GoogleMachineEventsTraceReader#getMaxRamCapacity()}.
         */
        @Override
        public Long getValue(final GoogleMachineEventsTraceReader reader) {
            final double fieldValue = reader.getFieldDoubleValue(this);
            return Math.round(fieldValue * reader.getMaxCpuCores());
        }
    }
}
