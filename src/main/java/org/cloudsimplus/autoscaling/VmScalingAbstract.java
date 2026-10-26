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

/// An abstract class for implementing [HorizontalVmScaling] and
/// [VerticalVmScaling].
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.1.0
@Accessors(makeFinal = false)
public non-sealed abstract class VmScalingAbstract implements VmScaling {
    private double lastProcessingTime;

    @Getter @Setter @NonNull
    private Vm vm;

    protected VmScalingAbstract() {
        this.vm = Vm.NULL;
    }

    /// {@return true if it's time to check weather the Vm is over and underloaded, false otherwise}
    /// @param time current simulation time
    protected boolean isTimeToCheckPredicate(final double time) {
        return time > lastProcessingTime && (long) time % getVm().getHost().getDatacenter().getSchedulingInterval() == 0;
    }

    /// Requests the Vm to be scaled up or down if it is over or underloaded, respectively.
    /// The scaling request will be sent to the [org.cloudsimplus.brokers.DatacenterBroker] only
    /// if the under or overload condition is met, that depends on the implementation
    /// of the scaling mechanism.
    ///
    /// It's an internal method: the [Vm] calls it every time its processing is updated,
    /// to check if it's time to perform a down or up scaling.
    /// That is checked at the time interval defined by the
    /// [org.cloudsimplus.datacenters.Datacenter#getSchedulingInterval()],
    /// so make sure to set such a value to enable the periodic verification.
    ///
    /// @param time current simulation time
    /// @return true if the Vm is over or underloaded and up or down scaling request was sent to the broker;
    /// false otherwise
    public abstract boolean requestUpScalingIfPredicateMatches(double time);

    /// Performs the actual request to scale the Vm up or down,
    /// depending on whether it is over or underloaded, respectively.
    /// This method is automatically called by
    /// [#requestUpScalingIfPredicateMatches(double)]
    /// when it is verified that the Vm is over or underloaded.
    ///
    /// @param time current simulation time
    /// @return true if the request was actually sent, false otherwise
    protected abstract boolean requestScaling(double time);

    /// Sets the last time the scheduler checked for VM overload.
    /// @param lastProcessingTime the current simulation time will be set as the last processing time
    protected void setLastProcessingTime(final double lastProcessingTime) {
        this.lastProcessingTime = lastProcessingTime;
    }
}
