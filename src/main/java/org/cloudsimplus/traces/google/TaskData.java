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

import lombok.Getter;
import lombok.Setter;

/**
 * A base class that stores data to identify a task.
 * It has to be extended by classes that read task's events from a trace file.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
@Getter @Setter
class TaskData extends MachineDataBase {
    /**
     * The id of the job this task belongs to.
     */
    private long jobId;

    /**
     * The task index within the job.
     */
    private long taskIndex;

    /// @return a unique ID to be used to identify Cloudlets.
    /// The ID is composed of the [Job ID][#getJobId()],
    /// concatenated with the [Task Index][#getTaskIndex()].
    public long getUniqueTaskId(){
        final String uniqueId = "%d%d".formatted(getJobId(), getTaskIndex());
        return Long.parseLong(uniqueId);
    }
}
