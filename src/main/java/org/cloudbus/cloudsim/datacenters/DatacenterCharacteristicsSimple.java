/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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
@Getter @Setter
public class DatacenterCharacteristicsSimple implements DatacenterCharacteristics {
    @NonNull
    private final Datacenter datacenter;

    @NonNull
    private String architecture;

    @NonNull
    private String os;

    @NonNull
    private String vmm;

    private double costPerSecond;
    private double costPerMem;
    private double costPerStorage;
    private double costPerBw;

    /**
     * Creates a DatacenterCharacteristics with default values
     * for {@link #getArchitecture() architecture}, {@link #getOs() OS} and
     * {@link #getVmm() VMM}.
     *
     * <p>The costs for {@link #getCostPerBw() BW},
     * {@link #getCostPerMem()} () RAM} and {@link #getCostPerStorage()} () Storage} are set to zero.
     * </p>
     */
    public DatacenterCharacteristicsSimple(final Datacenter datacenter){
        this(datacenter, DEFAULT_ARCH, DEFAULT_OS, DEFAULT_VMM, 0, 0, 0, 0);
    }

    /**
     * A copy constructor
     * @param source the source object to copy
     * @param dc the target Datacenter for the copy
     */
    public DatacenterCharacteristicsSimple(final DatacenterCharacteristics source, final Datacenter dc){
        this(
            dc,
            source.getArchitecture(),
            source.getOs(),
            source.getVmm(),
            source.getCostPerSecond(),
            source.getCostPerMem(),
            source.getCostPerStorage(),
            source.getCostPerBw()
        );
    }

    private DatacenterCharacteristicsSimple(
        final @NonNull Datacenter datacenter, final @NonNull String architecture,
        final @NonNull String os, final @NonNull String vmm,
        final double costPerSecond, final double costPerMem,
        final double costPerStorage, final double costPerBw)
    {
        setArchitecture(architecture);
        setOs(os);
        setVmm(vmm);
        setCostPerSecond(costPerSecond);
        setCostPerMem(costPerMem);
        setCostPerStorage(costPerStorage);
        setCostPerBw(costPerBw);
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
    public long getId() {
        return datacenter.getId();
    }
}
