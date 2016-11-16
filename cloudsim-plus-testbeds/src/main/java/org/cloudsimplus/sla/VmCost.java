/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Vm;

/**
 * This class contains the methods needed to calculate the cost of vms
 * @author raysaoliveira
 */
public class VmCost {

    private Vm vm;
    private Datacenter datacenter;

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
     * @return getVmMemoryCost
     */
    public double getVmMemoryCost() {
        return (datacenter.getCharacteristics().getCostPerMem() * vm.getRam());
    }

    /**
     * Return the cost of BW
     *
     * @return getVmBwCost
     */
    public double getVmBwCost() {
        return datacenter.getCharacteristics().getCostPerBw() * vm.getBw();
    }

    /**
     * Return the cost of processing for a given host
     *
     * @return getVmProcessingCost
     */
    public double getVmProcessingCost() {
        double hostMips = vm.getHost().getPeList().stream().findFirst().map(pe -> pe.getMips()).orElse(0);
        double costPerMI = (hostMips > 0 ? datacenter.getCharacteristics().getCostPerSecond()/hostMips : 0);
        
        return costPerMI * getVm().getMips() * getVm().getNumberOfPes();
    }

    /**
     * Return the cost of storage
     *
     * @return getVmStorageCost
     */
    public double getVmStorageCost() {
        return datacenter.getCharacteristics().getCostPerStorage() * vm.getSize();
    }

    /**
     * Gets the total cost of a vm,
     * that includes the processing, bandwidth, memory and storage cost.
     *
     * @return
     */
    public double getVmTotalCost() {
        return getVmProcessingCost() + getVmStorageCost() + getVmMemoryCost() + getVmBwCost();
    }

    /**
     * @param vm the vm to set
     */
    public void setVm(Vm vm) {
        this.vm = vm;
    }
}
