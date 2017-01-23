/**
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * This class contains the methods needed to calculate the cost of vms
 * @author raysaoliveira
 */
public class VmCost {

    private Vm vm;

    public VmCost(Vm vm) {
        this.vm = vm;
    }

    /**
     * @return the vm
     */
    public Vm getVm() {
        return vm;
    }

    /**
     * Return the cost of memory
     *
     * @return getMemoryCost
     */
    public double getMemoryCost() {
        return (getDatacenter().getCharacteristics().getCostPerMem() * vm.getRam().getCapacity());
    }

    private Datacenter getDatacenter() {
        return vm.getHost().getDatacenter();
    }

    /**
     * Return the cost of BW
     *
     * @return getBwCost
     */
    public double getBwCost() {
        return getDatacenter().getCharacteristics().getCostPerBw() * vm.getBw().getCapacity();
    }

    /**
     * Return the cost of processing for a given host
     *
     * @return getProcessingCost
     */
    public double getProcessingCost() {
        double hostMips = vm.getHost().getPeList().stream()
                .findFirst()
                .map(pe -> pe.getCapacity())
                .orElse(0L);
        double costPerMI = (hostMips > 0 ?
                getDatacenter().getCharacteristics().getCostPerSecond()/hostMips :
                0.0);

        return costPerMI * getVm().getMips() * getVm().getNumberOfPes();
    }

    /**
     * Return the cost of storage
     *
     * @return getStorageCost
     */
    public double getStorageCost() {
        return getDatacenter().getCharacteristics().getCostPerStorage() * vm.getStorage().getCapacity();
    }

    /**
     * Gets the total cost of a vm,
     * that includes the processing, bandwidth, memory and storage cost.
     *
     * @return
     */
    public double getTotalCost() {
        return getProcessingCost() + getStorageCost() + getMemoryCost() + getBwCost();
    }

    /**
     * @param vm the vm to set
     */
    public void setVm(Vm vm) {
        this.vm = vm;
    }
}
