/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

/**
 * A basic implementation of the {@link CloudletEventInfo} interface.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class CloudletEventInfoSimple extends EventInfoSimple implements CloudletEventInfo {

    private Cloudlet cloudlet;

    /**
     * Create an EventInfo with the given parameters.
     *
     * @param time the time the event was fired
     * @param cloudlet the Cloudlet that fired the event
     */
    public CloudletEventInfoSimple(double time, Cloudlet cloudlet) {
        super(time);
        setCloudlet(cloudlet);
    }

    @Override
    public Cloudlet getCloudlet() {
        return cloudlet;
    }

    @Override
    public final void setCloudlet(Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
    }

}
