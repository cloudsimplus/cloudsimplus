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
package org.cloudsimplus.vms;

import lombok.Getter;
import org.cloudsimplus.datacenters.DatacenterCharacteristics;

import java.util.Objects;

/// Computes the monetary ($) cost to run a given [Vm],
/// including the [total cost][#getTotalCost()]
/// and individual resource cost, namely:
/// the processing power, bandwidth, memory and storage cost ($).
///
/// @author raysaoliveira
/// @since CloudSim Plus 1.0
public class VmCost {
    /**
     * The VM for which the total monetary cost will be computed.
     */
    @Getter
    private final Vm vm;

    /**
     * Creates an instance to compute the monetary cost ($) to run a given VM.
     * @param vm the VM to compute its monetary cost
     */
    public VmCost(final Vm vm) {
        this.vm = Objects.requireNonNull(vm);
    }

    /**
     * {@return the characteristics of the Datacenter where the VM is running}
     * Such characteristics include the price to run a VM in such a Datacenter.
     */
    private DatacenterCharacteristics getDcCharacteristics() {
        return vm.getHost().getDatacenter().getCharacteristics();
    }

    /**
     * @return the memory monetary cost ($) of the resource allocated to the VM
     */
    public double getMemoryCost() {
        return getDcCharacteristics().getCostPerMem() * vm.getRam().getCapacity();
    }

    /**
     * @return the bandwidth monetary cost ($) of the resource allocated to the VM
     */
    public double getBwCost() {
        return getDcCharacteristics().getCostPerBw() * vm.getBw().getCapacity();
    }

    /**
     * @return the processing monetary cost ($) of the PEs allocated from the PM hosting the VM,
     * considering the VM's PEs number and total execution time.
     */
    public double getProcessingCost() {
        final double hostMips = vm.getHost().getMips();
        final double costPerMI = hostMips == 0 ? 0.0 : getDcCharacteristics().getCostPerSecond() / hostMips;
        return costPerMI * vm.getTotalMipsCapacity() * vm.getTotalExecutionTime();
    }

    /**
     * @return the storage monetary cost ($) of the resource allocated to the VM
     */
    public double getStorageCost() {
        return getDcCharacteristics().getCostPerStorage() * vm.getStorage().getCapacity();
    }

    /**
     * @return the total monetary cost ($) of all resources allocated to the VM,
     * namely the processing power, bandwidth, memory and storage.
     */
    public double getTotalCost() {
        return getProcessingCost() + getStorageCost() + getMemoryCost() + getBwCost();
    }

    @Override
    public String toString() {
        return
            "%s costs ($) for %8.2f execution seconds - CPU: %8.2f$ RAM: %8.2f$ Storage: %8.2f$ BW: %8.2f$ Total: %8.2f$"
            .formatted(vm, getVm().getTotalExecutionTime(), getProcessingCost(), getMemoryCost(), getStorageCost(), getBwCost(), getTotalCost());
    }
}
