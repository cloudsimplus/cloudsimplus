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

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimTag;

/**
 * Defines the type of {@link TaskEvent} (a line) in the trace file
 * that represents the state of the job.
 * Each enum instance is a possible value for the {@link TaskEventField#EVENT_TYPE} field.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public enum TaskEventType {
    /**
     * 0: A task or job became eligible for scheduling.
     */
    SUBMIT{
        @Override
        protected boolean process(final GoogleTaskEventsTraceReader reader) {
            if(!reader.allowCloudletCreation()) {
                return false;
            }

            final var event = TaskEvent.of(reader);
            final var cloudlet = reader.createCloudlet(event);
            // Since Cloudlet id must be unique, it will be the concatenation of the job and task id
            cloudlet.setId(event.getUniqueTaskId());
            cloudlet.setJobId(event.getJobId());
            final double delay = TaskEventField.TIMESTAMP.getValue(reader);
            cloudlet.setSubmissionDelay(delay);

            /* Set status to FROZEN to avoid the cloudlet to start running after being submitted.
            The execution must start only after a SCHEDULE event happens. */
            if(delay > 0) {
                cloudlet.setStatus(Cloudlet.Status.FROZEN);
            }

            final var broker = reader.getBrokerManager().getOrCreateBroker(event.getUserName());
            if(reader.isAutoSubmitCloudlets()) {
                broker.submitCloudlet(cloudlet);
            }

            return reader.addAvailableObject(cloudlet);
        }
    },

    /**
     * 1: A job or task was scheduled on a machine (it may not start running
     * immediately due to code-shipping time, etc).
     * For jobs, this occurs the first time any task of the job is scheduled on a machine.
     */
    SCHEDULE{
        @Override
        protected boolean process(final GoogleTaskEventsTraceReader reader) {
            return reader.requestCloudletStatusChange(CloudSimTag.CLOUDLET_READY);
        }
    },

    /**
     * 2: A task or job was de-scheduled because of a higher priority task or job,
     * because the scheduler over-committed and the actual demand exceeded the machine capacity,
     * because the machine on which it was running became unusable (e.g. taken offline for repairs),
     * or because a disk holding the task’s data was lost.
     */
    EVICT{
        @Override
        protected boolean process(final GoogleTaskEventsTraceReader reader) {
            return reader.requestCloudletStatusChange(CloudSimTag.CLOUDLET_PAUSE);
        }
    },

    /**
     * 3: A task or job was de-scheduled (or, in rare cases, ceased to be eligible
     * for scheduling while it was pending) due to a task failure.
     */
    FAIL{
        @Override
        protected boolean process(final GoogleTaskEventsTraceReader reader) {
            return reader.requestCloudletStatusChange(CloudSimTag.CLOUDLET_FAIL);
        }
    },

    /**
     * 4: A task or job completed normally.
     */
    FINISH{
        @Override
        protected boolean process(final GoogleTaskEventsTraceReader reader) {
            return reader.requestCloudletStatusChange(CloudSimTag.CLOUDLET_FINISH);
        }
    },

    /**
     * 5: A task or job was cancelled by the user or a driver program or because
     * another job or task on which this job was dependent died.
     */
    KILL{
        @Override
        protected boolean process(final GoogleTaskEventsTraceReader reader) {
            return reader.requestCloudletStatusChange(CloudSimTag.CLOUDLET_CANCEL);
        }
    },

    /**
     * 6: A task or job was presumably terminated, but a record indicating its
     * termination was missing from our source data.
     */
    LOST{
        @Override
        protected boolean process(final GoogleTaskEventsTraceReader reader) {
            return false;
        }
    },

    /**
     * 7: A task or job’s scheduling class, resource requirements, or
     * constraints were updated while it was waiting to be scheduled.
     */
    UPDATE_PENDING{
        @Override
        protected boolean process(final GoogleTaskEventsTraceReader reader) {
            return false;
        }
    },

    /**
     * 8: A task or job’s scheduling class, resource requirements, or
     * constraints were updated while it was scheduled.
     */
    UPDATE_RUNNING{
        @Override
        protected boolean process(final GoogleTaskEventsTraceReader reader) {
            return false;
        }
    };

    /**
     * Gets an enum instance from its ordinal value.
     * @param ordinal the ordinal value to get the enum instance from
     * @return the enum instance
     */
    public static TaskEventType getValue(final int ordinal){
        return values()[ordinal];
    }

    /**
     * Executes an operation with the Cloudlet according to the Event Type.
     * Each enum value must implement this method to include its own processing logic.
     *
     * @return true if trace line for the event type was processed, false otherwise
     */
    protected abstract boolean process(GoogleTaskEventsTraceReader reader);

    /**
     * Gets the enum value that represents the event type of the current trace line.
     *
     * @return the {@link MachineEventType} value
     */
    protected static TaskEventType of(final GoogleTaskEventsTraceReader reader) {
        return TaskEventField.EVENT_TYPE.getValue(reader);
    }
}
