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
package org.cloudsimplus.autoscaling;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.vms.Vm;

/**
 * An abstract class for implementing {@link HorizontalVmScaling} and
 * {@link VerticalVmScaling}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.1.0
 */
@Accessors(makeFinal = false)
public non-sealed abstract class VmScalingAbstract implements VmScaling {
    private double lastProcessingTime;

    @Getter @Setter @NonNull
    private Vm vm;

    protected VmScalingAbstract() {
        this.vm = Vm.NULL;
    }

    /**
     * {@return true if it's time to check weather the Vm is over and underloaded, false otherwise}
     * @param time current simulation time
     */
    protected boolean isTimeToCheckPredicate(final double time) {
        return time > lastProcessingTime && (long) time % getVm().getHost().getDatacenter().getSchedulingInterval() == 0;
    }

    /**
     * Performs the actual request to scale the Vm up or down,
     * depending on whether it is over or underloaded, respectively.
     * This method is automatically called by
     * {@link VmScaling#requestUpScalingIfPredicateMatches(org.cloudsimplus.listeners.VmHostEventInfo)}
     * when it is verified that the Vm is over or underloaded.
     *
     * @param time current simulation time
     * @return true if the request was actually sent, false otherwise
     */
    protected abstract boolean requestScaling(double time);

    /**
     * Sets the last time the scheduler checked for VM overload.
     * @param lastProcessingTime the current simulation time will be set as the last processing time
     */
    protected void setLastProcessingTime(final double lastProcessingTime) {
        this.lastProcessingTime = lastProcessingTime;
    }
}
