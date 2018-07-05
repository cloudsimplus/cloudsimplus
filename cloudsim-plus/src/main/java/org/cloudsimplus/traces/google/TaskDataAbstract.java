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

/**
 * A base class that stores data to identify a task. It has to be extended by classes that read task's events
 * from a trace file.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public abstract class TaskDataAbstract extends MachineDataAbstract {
    private int jobId;
    private int taskIndex;


    /**
     * Gets the id of the job this task belongs to.
     * @return
     */
    public int getJobId(){ return jobId; }

    protected TaskDataAbstract setJobId(final int jobId) {
        this.jobId = jobId;
        return this;
    }

    /**
     * Gets the task index within the job.
     * @return
     */
    public int getTaskIndex() {
        return taskIndex;
    }

    protected TaskDataAbstract setTaskIndex(final int taskIndex) {
        this.taskIndex = taskIndex;
        return this;
    }

    /**
     * An unique ID to be used to identify Cloudlets.
     * The ID is composed of the {@link #getJobId() Job ID} concatenated with the {@link #getTaskIndex() Task Index}.
     * @return
     */
    public int getUniqueTaskId(){
        return Integer.valueOf(String.format("%d%d", getJobId(), getTaskIndex()));
    }

}
