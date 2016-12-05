/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
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
package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An {@link EventInfo} class that stores data to be passed
 * to Vm's {@link EventListener} objects that are registered to be notified
 * about events of a Vm that happened inside a given Datacenter.
 * So it represents the data of the notification passed from a Datacenter to a Vm.
 *
 * @see Vm#getOnVmCreationFailureListener()
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterToVmEventInfo extends VmEventInfoSimple implements DatacenterEventInfo {
    private Datacenter datacenter;

    /**
     * Creates an EventInfo with the given parameters.
     *
     * @param time time when the event was fired
     * @param datacenter Datacenter where the Vm is placed
     * @param vm Vm that fired the event
     */
    public DatacenterToVmEventInfo(double time, Datacenter datacenter, Vm vm) {
        super(time, vm);
        setDatacenter(datacenter);
    }

    /**
     *
     * @return the Datacenter that caused the Vm event
     */
    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }

    /**
     * Sets the Datacenter that caused the Vm event
     * @param datacenter
     */
    @Override
    public final void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
