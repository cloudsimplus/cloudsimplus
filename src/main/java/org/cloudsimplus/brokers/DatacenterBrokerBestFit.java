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
package org.cloudsimplus.brokers;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.vms.Vm;

import java.util.Comparator;

/**
 * A {@link DatacenterBroker} that uses a <a href="https://en.wikipedia.org/wiki/Best-fit_bin_packing">Best Fit</a>
 * mapping between submitted cloudlets and VMs, trying to place a Cloudlet
 * at the best suitable Vm that can be found (according to the required Cloudlet's PEs).
 * The Broker then places the submitted VMs at the first Datacenter found.
 * If there isn't capacity in that one, it will try other available ones.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.3.8
 */
public class DatacenterBrokerBestFit extends DatacenterBrokerSimple {

    /**
     * Creates a DatacenterBroker.
     *
     * @param simulation The {@link CloudSimPlus} instance that represents the simulation the broker is related to
     */
    public DatacenterBrokerBestFit(final CloudSimPlus simulation) {
        super(simulation);
    }

    /**
     * Selects the VM with the lowest number of PEs that is able to run a given Cloudlet.
     * @param cloudlet the Cloudlet to find a VM to run it
     * @return the VM selected for the Cloudlet or {@link Vm#NULL} if no suitable VM was found
     */
    @Override
    public Vm defaultVmMapper(final Cloudlet cloudlet) {
        if (cloudlet.isBoundToVm()) {
            return cloudlet.getVm();
        }

        final Vm mappedVm = getVmExecList()
            .stream()
            .filter(vm -> vm.getExpectedFreePesNumber() >= cloudlet.getPesNumber())
            .min(Comparator.comparingLong(Vm::getExpectedFreePesNumber))
            .orElse(Vm.NULL);

        if (Vm.NULL.equals(mappedVm)) {
            LOGGER.warn("{}: {}: {} (PEs: {}) couldn't be mapped to any suitable VM.",
                getSimulation().clockStr(), getName(), cloudlet, cloudlet.getPesNumber());
        } else {
            LOGGER.trace("{}: {}: {} (PEs: {}) mapped to {} (available PEs: {}, tot PEs: {})",
                getSimulation().clockStr(), getName(), cloudlet, cloudlet.getPesNumber(), mappedVm,
                mappedVm.getExpectedFreePesNumber(), mappedVm.getFreePesNumber());
        }

        return mappedVm;
    }
}
