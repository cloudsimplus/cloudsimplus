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

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.util.TimeUtil;

/**
 * The index of each field in a Google Task Events trace file.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public enum TaskEventField implements TraceField<GoogleTaskEventsTraceReader> {
    /**
     * 0: The index of the field containing the time the event happened (stored in microsecond
     * but converted to seconds when read from the file).
     */
    TIMESTAMP {
        /**
         * {@return the timestamp converted to seconds}
         * @param reader the reader for the trace file
         */
        @Override
        public Double getValue(final GoogleTaskEventsTraceReader reader) {
            return TimeUtil.microToSeconds(reader.getFieldDoubleValue(this));
        }
    },

    /// 1: When it seems Google Cluster is missing an event record, it's synthesized a replacement.
    /// Similarly, we look for a record of every job or task that is active at the end of the trace time window,
    /// and synthesize a missing record if we don't find one.
    /// Synthesized records have a number (called the "missing info" field)
    /// to represent why they were added to the trace, according to [MissingInfo] values.
    ///
    /// When there is no info missing, the field is empty in the trace.
    /// In this case, -1 is returned instead.
    MISSING_INFO {
        @Override
        public Integer getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldIntValue(this, -1);
        }
    },

    /**
     * 2: The index of the field containing the id of the job this task belongs to.
     */
    JOB_ID {
        @Override
        public Long getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldLongValue(this);
        }
    },

    /**
     * 3: The index of the field containing the task index within the job.
     */
    TASK_INDEX {
        @Override
        public Long getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldLongValue(this);
        }
    },

    /**
     * 4: The index of the field containing the machineID.
     * If the field is present, indicates the machine onto which the task was scheduled,
     * otherwise, the reader will return -1 as default value.
     */
    MACHINE_ID {
        @Override
        public Long getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldLongValue(this, -1);
        }
    },

    /**
     * 5: The index of the field containing the type of event.
     */
    EVENT_TYPE {
        @Override
        public TaskEventType getValue(final GoogleTaskEventsTraceReader reader) {
            return TaskEventType.getValue(reader.getFieldIntValue(this));
        }
    },

    /**
     * 6: The index of the field containing the hashed username provided as an opaque
     * base64-encoded string that can be tested for equality.
     * For each distinct username, a corresponding {@link DatacenterBroker} is created.
     */
    USERNAME {
        @Override
        public String getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldValue(this);
        }
    },

    /// 7: All jobs and tasks have a scheduling class that roughly represents
    /// how latency-sensitive it is.
    /// The scheduling class is represented by a single number,
    /// with 3 representing a more latency-sensitive task
    /// (e.g., serving revenue-generating user requests)
    /// and 0 representing a non-production task
    /// (e.g., development, non-business-critical analyses, etc.).
    ///
    /// Note that scheduling class is not a priority,
    /// although more latency-sensitive tasks tend to have higher task priorities.
    /// Scheduling class affects machine-local policy for resource access.
    /// Priority determines whether a task is scheduled on a machine.
    ///
    /// **WARNING**: Currently, this field is totally ignored by CloudSim Plus.
    SCHEDULING_CLASS {
        @Override
        public Integer getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldIntValue(this);
        }
    },

    /// 8: Each task has a priority, a small integer that is mapped here into a sorted set of values,
    /// with 0 as the lowest priority (least important).
    /// Tasks with larger priority numbers generally get preference for resources
    /// over tasks with smaller priority numbers.
    ///
    /// There are some special priority ranges:
    ///
    /// - **"free" priorities**: these are the lowest priorities.
    ///   Resources requested at these priorities incur little internal charging.
    /// - **"production" priorities**: these are the highest priorities.
    ///   The cluster scheduler attempts to prevent latency-sensitive tasks at
    ///   these priorities from being evicted due to over-allocation of machine resources.
    /// - **"monitoring" priorities**: these priorities are intended for jobs
    ///   which monitor the health of other, lower-priority jobs
    ///
    PRIORITY {
        @Override
        public Integer getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldIntValue(this);
        }
    },

    /// 9: The index of the field containing the maximum number of CPU cores
    /// the task is permitted to use (in percentage from 0 to 1).
    ///
    /// When there is no value for the field, 0 is returned instead.
    RESOURCE_REQUEST_FOR_CPU_CORES {
        @Override
        public Double getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldDoubleValue(this, 0);
        }
    },

    /// 10: The index of the field containing the maximum amount of RAM
    /// the task is permitted to use (in percentage from 0 to 1).
    ///
    /// When there is no value for the field, 0 is returned instead.
    RESOURCE_REQUEST_FOR_RAM {
        @Override
        public Double getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldDoubleValue(this, 0);
        }
    },

    /// 11: The index of the field containing the maximum amount of local disk space
    /// the task is permitted to use (in percentage from 0 to 1).
    ///
    /// When there is no value for the field, 0 is returned instead.
    RESOURCE_REQUEST_FOR_LOCAL_DISK_SPACE {
        @Override
        public Double getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldDoubleValue(this, 0);
        }
    },

    /// 12: If the different-machine constraint field is present, and true (1),
    /// it indicates that a task must be scheduled to execute on a
    /// different machine than any other currently running task in the job.
    /// It is a special type of constraint.
    ///
    /// When there is no value for the field, -1 is returned instead.
    DIFFERENT_MACHINE_CONSTRAINT {
        @Override
        public Integer getValue(final GoogleTaskEventsTraceReader reader) {
            return reader.getFieldIntValue(this, -1);
        }
    }
}
