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
package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;

/**
 * Computes the monetary ($) cost to run a given VM,
 * including the {@link #getTotalCost() total cost}
 * and individual resource cost, namely:
 * the processing power, bandwidth, memory and storage cost.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.0
 */
public class VmCost {
    /** @see #getVm()  */
    private final Vm vm;

    /**
     * Creates an instance to compute the monetary cost ($) to run a given VM.
     * @param vm the VM to compute its monetary cost
     */
    public VmCost(Vm vm) {
        this.vm = vm;
    }

    /**
     * Gets the VM for which the total monetary cost will be computed.
     * @return
     */
    public Vm getVm() {
        return vm;
    }

    /**
     * Gets the characteristics of the Datacenter where the VM is running.
     * Such characteristics include the price to run a VM in such a Datacenter.
     * @return
     */
    private DatacenterCharacteristics getDcCharacteristics() {
        return vm.getHost().getDatacenter().getCharacteristics();
    }

    /**
     * Gets the total monetary cost ($) of the VM's allocated memory.
     *
     * @return
     */
    public double getMemoryCost() {
        return getDcCharacteristics().getCostPerMem() * vm.getRam().getCapacity();
    }

    /**
     * Gets the total monetary cost ($) of the VM's allocated BW.
     *
     * @return
     */
    public double getBwCost() {
        return getDcCharacteristics().getCostPerBw() * vm.getBw().getCapacity();
    }

    /**
     * Gets the total monetary cost ($) of processing power allocated from the PM hosting the VM,
     * considering the VM's PEs number and total execution time.
     *
     * @return
     */
    public double getProcessingCost() {
        final double hostMips = vm.getHost().getMips();
        final double costPerMI = hostMips == 0 ? 0.0 : getDcCharacteristics().getCostPerSecond() / hostMips;
        return costPerMI * vm.getTotalMipsCapacity() * vm.getTotalExecutionTime();
    }

    /**
     * Gets the total monetary cost ($) of the VM's allocated storage.
     *
     * @return getStorageCost
     */
    public double getStorageCost() {
        return getDcCharacteristics().getCostPerStorage() * vm.getStorage().getCapacity();
    }

    /**
     * Gets the total monetary cost ($) of all resources allocated to the VM,
     * namely the processing power, bandwidth, memory and storage.
     *
     * @return
     */
    public double getTotalCost() {
        return getProcessingCost() + getStorageCost() + getMemoryCost() + getBwCost();
    }

    @Override
    public String toString() {
        return String.format(
            "%s costs ($) for %8.2f execution seconds - CPU: %8.2f$ RAM: %8.2f$ Storage: %8.2f$ BW: %8.2f$ Total: %8.2f$",
            vm, getVm().getTotalExecutionTime(), getProcessingCost(), getMemoryCost(), getStorageCost(), getBwCost(), getTotalCost());
    }
}
