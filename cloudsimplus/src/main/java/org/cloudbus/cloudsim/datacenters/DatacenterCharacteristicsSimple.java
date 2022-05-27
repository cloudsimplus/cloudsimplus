/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.hosts.Host;

/**
 * Represents static properties of a Datacenter such as architecture,
 * Operating System (OS), management policy (time- or space-shared),
 * cost and time zone at which the resource is located along resource configuration.
 * Each {@link Datacenter} has to have its own instance of this class,
 * since it stores the Datacenter host list.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterCharacteristicsSimple implements DatacenterCharacteristics {
    /** @see #getArchitecture() */
    private String architecture;

    /** @see #getOs() */
    private String os;

    /** @see #getCostPerSecond() */
    private double costPerSecond;

    /** @see #getVmm() */
    private String vmm;

    /** @see #getCostPerMem() */
    private double costPerMem;

    /** @see #getCostPerStorage() */
    private double costPerStorage;

    /** @see #getCostPerBw() */
    private double costPerBw;

    /** @see #getDatacenter() */
    private final Datacenter datacenter;

    /**
     * Creates a DatacenterCharacteristics with default values
     * for {@link #getArchitecture() architecture}, {@link #getOs() OS} and
     * {@link #getVmm() VMM}. The costs for {@link #getCostPerBw() BW},
     * {@link #getCostPerMem()} () RAM} and {@link #getCostPerStorage()} () Storage} are set to zero.
     */
    public DatacenterCharacteristicsSimple(final Datacenter datacenter){
        setArchitecture(DEFAULT_ARCH);
        setOs(DEFAULT_OS);
        setVmm(DEFAULT_VMM);
        setCostPerSecond(0);
        setCostPerMem(0);
        setCostPerStorage(0);
        setCostPerBw(0);
        this.datacenter = datacenter;
    }

    @Override
    public double getMips() {
        return datacenter.getHostList().stream().mapToDouble(Host::getTotalMipsCapacity).sum();
    }

    @Override
    public int getNumberOfPes() {
        return (int)datacenter.getHostList().stream().mapToLong(Host::getNumberOfPes).sum();
    }

    @Override
    public long getNumberOfFailedHosts() {
        return datacenter.getHostList().stream().filter(Host::isFailed).count();
    }

    @Override
    public boolean isWorking() {
        return getNumberOfFailedHosts() == 0;
    }

    @Override
    public double getCostPerMem() {
        return costPerMem;
    }

    @Override
    public final DatacenterCharacteristics setCostPerMem(final double costPerMem) {
        this.costPerMem = costPerMem;
        return this;
    }

    @Override
    public double getCostPerStorage() {
        return costPerStorage;
    }

    @Override
    public final DatacenterCharacteristics setCostPerStorage(final double costPerStorage) {
        this.costPerStorage = costPerStorage;
        return this;
    }

    @Override
    public double getCostPerBw() {
        return costPerBw;
    }

    @Override
    public final DatacenterCharacteristics setCostPerBw(final double costPerBw) {
        this.costPerBw = costPerBw;
        return this;
    }

    @Override
    public String getVmm() {
        return vmm;
    }

    /**
     * Gets the Datacenter id, setup when Datacenter is created.
     * @return
     */
    @Override
    public long getId() {
        return datacenter.getId();
    }

    @Override
    public String getArchitecture() {
        return architecture;
    }

    @Override
    public final DatacenterCharacteristics setArchitecture(final String architecture) {
        this.architecture = architecture;
        return this;
    }

    @Override
    public String getOs() {
        return os;
    }

    @Override
    public final DatacenterCharacteristics setOs(final String os) {
        this.os = os;
        return this;
    }

    @Override
    public double getCostPerSecond() {
        return costPerSecond;
    }

    @Override
    public final DatacenterCharacteristics setCostPerSecond(final double costPerSecond) {
        this.costPerSecond = costPerSecond;
        return this;
    }

    @Override
    public final DatacenterCharacteristics setVmm(final String vmm) {
        this.vmm = vmm;
        return this;
    }

    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }
}
