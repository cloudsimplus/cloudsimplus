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
 * A base class that stores data to identify a machine.
 * It has to be extended by classes that read trace files containing some machine
 * data (such as the ID of a machine to be created or the ID of a machine where a task should run).
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
abstract class MachineDataAbstract {
    private long machineId;

    /**
     * Gets the machineID that indicates the machine onto which the task was scheduled.
     * If the field is empty, -1 is returned instead.
     * @return
     */
    public long getMachineId(){ return machineId; }

    /* default */ MachineDataAbstract setMachineId(final long machineId) {
        this.machineId = machineId;
        return this;
    }
}
