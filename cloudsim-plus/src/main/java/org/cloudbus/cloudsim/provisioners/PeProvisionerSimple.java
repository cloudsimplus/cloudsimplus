/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.provisioners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.vms.Vm;

/**
 * PeProvisionerSimple is an extension of {@link PeProvisioner} which uses a
 * best-effort policy to allocate virtual PEs to VMs: if there is available MIPS
 * on the physical PE, it allocates to a virtual PE; otherwise, it fails. Each
 * host's PE has to have its own instance of a PeProvisioner.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PeProvisionerSimple extends PeProvisioner {

    /**
     * The PE map, where each key is a VM and each value is the list of PEs
     * (in terms of their amount of MIPS) allocated to that VM.
     */
    private Map<Vm, List<Double>> peTable;

    /**
     * Instantiates a new pe provisioner simple.
     *
     * @param availableMips The total mips capacity of the PE that the
     * provisioner can allocate to VMs.
     *
     * @pre $none
     * @post $none
     */
    public PeProvisionerSimple(double availableMips) {
        super(availableMips);
        setPeTable(new HashMap<>());
    }

    @Override
    public boolean allocateMipsForVm(Vm vm, double mips) {
        if (getAvailableMips() < mips) {
            return false;
        }

        List<Double> allocatedMips = getPeTable().getOrDefault(vm, new ArrayList<>());
        allocatedMips.add(mips);

        setAvailableMips(getAvailableMips() - mips);
        getPeTable().put(vm, allocatedMips);

        return true;
    }

    @Override
    public boolean allocateMipsForVm(Vm vm, List<Double> mipsShare) {
        double totalMipsToAllocate = mipsShare.stream().reduce(0.0, Double::sum);

        if (getAvailableMips() + getTotalAllocatedMipsForVm(vm) < totalMipsToAllocate) {
            return false;
        }

        setAvailableMips(getAvailableMips() + getTotalAllocatedMipsForVm(vm) - totalMipsToAllocate);

        getPeTable().put(vm, mipsShare);

        return true;
    }

    @Override
    public void deallocateMipsForAllVms() {
        super.deallocateMipsForAllVms();
        getPeTable().clear();
    }

    @Override
    public double getAllocatedMipsForVmByVirtualPeId(Vm vm, int peId) {
        if (getPeTable().containsKey(vm) && peId >= 0 && peId < getPeTable().get(vm).size()) {
            return getPeTable().get(vm).get(peId);
        }

        return 0;
    }

    @Override
    public List<Double> getAllocatedMipsForVm(Vm vm) {
        return getPeTable().getOrDefault(vm, new ArrayList<>());
    }

    @Override
    public double getTotalAllocatedMipsForVm(Vm vm) {
        if (getPeTable().containsKey(vm)) {
            return getPeTable().get(vm).stream().mapToDouble(mips -> mips).sum();
        }

        return 0;
    }

    @Override
    public void deallocateMipsForVm(Vm vm) {
        if (getPeTable().containsKey(vm)) {
            for (double mips : getPeTable().get(vm)) {
                setAvailableMips(getAvailableMips() + mips);
            }
            getPeTable().remove(vm);
        }
    }

    /**
     * Gets the pe map.
     *
     * @return the pe map
     */
    protected Map<Vm, List<Double>> getPeTable() {
        return peTable;
    }

    /**
     * Sets the pe map.
     *
     * @param peTable the peTable to set
     */
    protected final void setPeTable(Map<Vm, List<Double>> peTable) {
        this.peTable = peTable;
    }

}
