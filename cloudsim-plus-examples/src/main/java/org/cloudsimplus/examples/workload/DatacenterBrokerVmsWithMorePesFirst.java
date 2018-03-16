/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
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
package org.cloudsimplus.examples.workload;

import java.util.Comparator;
import java.util.List;

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * A Broker which requests for creation of VMs inside a Datacenter
 * following the order of VM's required PEs number. VMs that require
 * more PEs are submitted first.
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterBrokerVmsWithMorePesFirst extends DatacenterBrokerSimple {

    public DatacenterBrokerVmsWithMorePesFirst(CloudSim simulation) {
        super(simulation);
    }

    /**
     * Gets the list of submitted VMs in descending order of PEs number.
     * @param <T> the class of VMs inside the list
     * @return the list of submitted VMs
     */
    @Override
    public <T extends Vm> List<T> getVmWaitingList() {
        Comparator<Vm> comparator = Comparator.comparingLong(Vm::getNumberOfPes);
        super.getVmWaitingList().sort(comparator.reversed());

        return super.getVmWaitingList();
    }
}
