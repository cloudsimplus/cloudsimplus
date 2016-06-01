/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.examples.network.datacenter;

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

    public VmCost() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
     * @return costMemory
     */
    public double costMemory() {
        return datacenter.getCharacteristics().getCostPerMem() * getVm().getRam();
    }

    /**
     * Return the cost of BW
     *
     * @return costBw
     */
    public double costBw() {
        return datacenter.getCharacteristics().getCostPerBw() * getVm().getBw();
    }

    /**
     * Return the cost of processing
     *
     * @return costProcessing
     */
    public double costProcessing() {
        return datacenter.getCharacteristics().getCostPerMi() * getVm().getMips() * getVm().getNumberOfPes();
    }

    /**
     * Return the cost of storage
     *
     * @return costStorage
     */
    public double costStorage() {
        return datacenter.getCharacteristics().getCostPerStorage() * getVm().getSize();
    }

    /**
     * Sum of costs of a vm
     *
     * @return
     */
    public double sumCostOfaVm() {
        return costBw() + costMemory() + costProcessing() + costStorage();

    }

    /**
     * @param vm the vm to set
     */
    public void setVm(Vm vm) {
        this.vm = vm;
    }
}
