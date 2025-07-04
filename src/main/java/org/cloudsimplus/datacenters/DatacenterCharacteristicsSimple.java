/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.datacenters;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.hosts.Host;

/**
 * Represents static properties of a Datacenter such as architecture,
 * Operating System (OS), cost and {@link Distribution Distribution}.
 * Each {@link Datacenter} must have its own instance of this class.
 * By default, all instances are created as {@link Distribution#PRIVATE PRIVATE} datacenters.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
@Accessors @Getter @Setter
public non-sealed class DatacenterCharacteristicsSimple implements DatacenterCharacteristics {
    @NonNull
    private Datacenter datacenter = Datacenter.NULL;

    @NonNull
    private Distribution distribution = Distribution.PRIVATE;

    private double costPerSecond;
    private double costPerMem;
    private double costPerStorage;
    private double costPerBw;

    /**
     * Creates a DatacenterCharacteristics with no costs for
     * {@link #getCostPerBw() BW},
     * {@link #getCostPerMem() RAM} and {@link #getCostPerStorage() Storage}.
     * The datacenter {@link #getDistribution() distribution} type is set as {@link Distribution#PRIVATE} by default.
     *
     * @see DatacenterCharacteristicsSimple#DatacenterCharacteristicsSimple(double, double, double)
     * @see DatacenterCharacteristicsSimple#DatacenterCharacteristicsSimple(double, double, double, double)
     */
    DatacenterCharacteristicsSimple(final Datacenter datacenter){
        this(0, 0, 0, 0);
        setDatacenter(datacenter);
    }

    /**
     * Creates a DatacenterCharacteristics with no cost for {@link #getCostPerBw() BW}.
     * The datacenter {@link #getDistribution() distribution} type is set as {@link Distribution#PRIVATE} by default.
     *
     * @see DatacenterCharacteristicsSimple#DatacenterCharacteristicsSimple(Datacenter)
     * @see DatacenterCharacteristicsSimple#DatacenterCharacteristicsSimple(double, double, double, double)
     */
    public DatacenterCharacteristicsSimple(
        final double costPerSecond, final double costPerMem, final double costPerStorage)
    {
        this(costPerSecond, costPerMem, costPerStorage, 0);
    }

    /**
     * Creates a DatacenterCharacteristics.
     * The datacenter {@link #getDistribution() distribution} type is set as {@link Distribution#PRIVATE} by default.
     *
     * @see DatacenterCharacteristicsSimple#DatacenterCharacteristicsSimple(Datacenter)
     * @see DatacenterCharacteristicsSimple#DatacenterCharacteristicsSimple(double, double, double)
     */
    public DatacenterCharacteristicsSimple(
        final double costPerSecond,  final double costPerMem,
        final double costPerStorage, final double costPerBw)
    {
        setCostPerSecond(costPerSecond);
        setCostPerMem(costPerMem);
        setCostPerStorage(costPerStorage);
        setCostPerBw(costPerBw);
    }

    void setDatacenter(@NonNull final Datacenter dc) {
        if(!Datacenter.NULL.equals(this.datacenter) && !this.datacenter.equals(dc))
            throw new IllegalStateException("This characteristics object is already attached to another Datacenter.");

        this.datacenter = dc;
    }

    @Override
    public double getMips() {
        return datacenter.getHostList().stream().mapToDouble(Host::getTotalMipsCapacity).sum();
    }

    @Override
    public int getPesNumber() {
        return (int)datacenter.getHostList().stream().mapToLong(Host::getPesNumber).sum();
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
