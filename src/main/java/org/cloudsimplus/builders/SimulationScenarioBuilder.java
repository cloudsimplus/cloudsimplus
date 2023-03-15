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
package org.cloudsimplus.builders;

import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

/**
 * An builder to help getting instance of other CloudSimPlus object builders.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class SimulationScenarioBuilder {
    private final DatacenterBuilder datacenterBuilder;
    private final BrokerBuilder brokerBuilder;
    private final CloudSimPlus simulation;

    public SimulationScenarioBuilder(CloudSimPlus simulation) {
        this.simulation = simulation;
        this.datacenterBuilder = new DatacenterBuilder(this);
        this.brokerBuilder = new BrokerBuilder(this);
    }

    public DatacenterBuilder getDatacenterBuilder() {
        return datacenterBuilder;
    }

    public BrokerBuilder getBrokerBuilder() {
        return brokerBuilder;
    }

    public Host getHostOfDatacenter(final int hostIndex, final int datacenterIndex){
        return datacenterBuilder.getHostOfDatacenter(hostIndex, datacenterIndex);
    }

    public Host getFirstHostFromFirstDatacenter(){
        return datacenterBuilder.getHostOfDatacenter(0,0);
    }

    public Vm getFirstVmFromFirstBroker() {
        return getVmFromBroker(0, 0);
    }

    public Vm getVmFromBroker(final int vmIndex, final int brokerIndex) {
        return brokerBuilder.get(brokerIndex).getWaitingVm(vmIndex);
    }

    public CloudSimPlus getSimulation() {
        return simulation;
    }
}
