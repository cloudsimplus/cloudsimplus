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

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An {@link EventInfo} class that stores data to be passed
 * to Cloudlet's {@link EventListener} objects that are registered to be notified
 * about events of a Cloudlet that happened inside a given Vm.
 * So it represents the data of the notification passed from a Vm to a Cloudlet.
 *
 * This class can be used
 * to notify observers, for instance, when a Cloudlet finishes executing .
 *
 * @see Cloudlet#getOnCloudletFinishEventListener()
 * @author Manoel Campos da Silva Filho
 * @todo Such classes should be defined as a FunctionalInterface
 */
public class VmToCloudletEventInfo extends CloudletEventInfoSimple implements  VmEventInfo {
    private Vm vm;

    /**
     * Creates an EventInfo with the given parameters.
     *
     * @param time time the event was fired
     * @param vm Vm where the Cloudlet is running
     * @param cloudlet the Cloudlet that fired the event
     */
    public VmToCloudletEventInfo(double time, Vm vm, Cloudlet cloudlet) {
        super(time, cloudlet);
        this.vm = vm;
    }

    /**
     * @return the Vm that was executing the cloudlet
     */
    @Override
    public Vm getVm() {
        return vm;
    }

    /**
     * Sets the Vm that was executing the cloudlet
     * @param vm
     */
    @Override
    public void setVm(Vm vm) {
        this.vm = vm;
    }
}
