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
package org.cloudsimplus.core;

import lombok.Getter;
import lombok.Setter;

/**
 * A base implementation for {@link Startable} entities.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 8.2.0
 */
@Getter @Setter
public abstract class StartableAbstract implements Startable {
    private double startTime = NOT_ASSIGNED;
    private double finishTime = NOT_ASSIGNED;
    private double lastBusyTime = NOT_ASSIGNED;


    @Override
    public final double getTotalExecutionTime() {
        if (startTime <= NOT_ASSIGNED) {
            return 0;
        }

        final double time = finishTime > NOT_ASSIGNED ? finishTime : getSimulation().clock();
        return time - startTime;
    }

    @Override
    public Startable setStartTime(final double startTime) {
        final var previousTime = this.startTime;
        this.startTime = startTime;
        if(previousTime <= 0 && startTime > 0) {
            this.lastBusyTime = startTime;
            onStart(startTime);
            //If the entity is being activated or re-activated, the shutdown time is reset
            this.setFinishTime(NOT_ASSIGNED);
        }

        return this;
    }

    @Override
    public final Startable setFinishTime(final double finishTime) {
        final var finishTimeWaNotSet = this.finishTime < 0;
        this.finishTime = finishTime;
        if (finishTimeWaNotSet && finishTime > 0)
            onFinish(finishTime);
        return this;
    }

    /**
     * Notifies when the entity is actually started.
     * @param time the new start time
     */
    protected abstract void onStart(double time);

    /**
     * Notifies when the entity is actually finished.
     * @param time the new finish time
     */
    protected abstract void onFinish(double time);

}
