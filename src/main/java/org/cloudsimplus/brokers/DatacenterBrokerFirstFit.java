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

/**
 * A {@link DatacenterBroker} that uses a <a href="https://en.wikipedia.org/wiki/First-fit_bin_packing">First Fit</a>
 * mapping between submitted cloudlets and VMs, trying to place a Cloudlet
 * at the first suitable Vm which can be found (according to the required Cloudlet's PEs).
 * The Broker then places the submitted VMs at the first Datacenter found.
 * If there isn't capacity in that one, it will try other available ones.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.6.0
 */
public class DatacenterBrokerFirstFit extends DatacenterBrokerSimple {
    /**
     * The index of the last Vm used to place a Cloudlet.
     */
    private int lastVmIndex;

    /**
     * Creates a DatacenterBroker.
     *
     * @param simulation The {@link CloudSimPlus} instance that represents the simulation the broker is related to
     */
    public DatacenterBrokerFirstFit(final CloudSimPlus simulation) {
        super(simulation);
    }

    /**
     * Selects the first VM with the lowest number of PEs that is able to run a given Cloudlet.
     * In case the algorithm can't find such a VM, it uses the
     * default DatacenterBroker VM mapper as a fallback.
     *
     * @param cloudlet the Cloudlet to find a VM to run it
     * @return the VM selected for the Cloudlet or {@link Vm#NULL} if no suitable VM was found
     */
    @Override
    public Vm defaultVmMapper(final Cloudlet cloudlet) {
        if (cloudlet.isBoundToVm()) {
            return cloudlet.getVm();
        }

        /* The for loop just defines the maximum number of Hosts to try.
         * When a suitable Host is found, the method returns immediately. */
        final int maxTries = getVmCreatedList().size();
        for (int i = 0; i < maxTries; i++) {
            final Vm vm = getVmCreatedList().get(lastVmIndex);
            if (vm.getExpectedFreePesNumber() >= cloudlet.getPesNumber()) {
                LOGGER.trace("{}: {}: {} (PEs: {}) mapped to {} (available PEs: {}, tot PEs: {})",
                    getSimulation().clockStr(), getName(), cloudlet, cloudlet.getPesNumber(), vm,
                    vm.getExpectedFreePesNumber(), vm.getFreePesNumber());
                return vm;
            }

            /* If it gets here, the previous Vm doesn't have capacity to place the Cloudlet.
             * Then, moves to the next Vm.
             * If the end of the Vm list is reached, starts from the beginning,
             * until the max number of tries.*/
            lastVmIndex = ++lastVmIndex % getVmCreatedList().size();
        }

        LOGGER.warn("{}: {}: {} (PEs: {}) couldn't be mapped to any suitable VM.",
                getSimulation().clockStr(), getName(), cloudlet, cloudlet.getPesNumber());

        return Vm.NULL;
    }


}
