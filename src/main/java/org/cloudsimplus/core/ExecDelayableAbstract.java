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
import org.cloudsimplus.util.MathUtil;

/// A base implementation for [ExecDelayable] entities.
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 8.3.0
@Getter
public abstract class ExecDelayableAbstract extends StartableAbstract implements ExecDelayable {
    private double startupDelay;
    private double shutDownDelay;

    @Setter
    private double shutdownBeginTime = NOT_ASSIGNED;

    @Override
    public boolean isShuttingDown() {
        return !isFinished() && shutdownBeginTime > 0;
    }

    @Override
    public ExecDelayable setStartupDelay(final double delay) {
        this.startupDelay = MathUtil.nonNegative(delay, "Startup Delay");
        return this;
    }

    @Override
    public ExecDelayable setShutDownDelay(final double delay) {
        this.shutDownDelay = MathUtil.nonNegative(delay, "Shutdown Delay");
        return this;
    }
}
