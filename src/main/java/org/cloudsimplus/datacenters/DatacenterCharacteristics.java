/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.datacenters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.core.Identifiable;
import org.cloudsimplus.hosts.Host;

/**
 * An interface to be implemented by each class that represents
 * the physical characteristics of a {@link Datacenter}.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public sealed interface DatacenterCharacteristics
    extends Identifiable
    permits DatacenterCharacteristicsSimple, DatacenterCharacteristicsNull
{
    /**
     * Identifies different datacenter distribution models, just for classification purposes.
     * For instance, a {@link VmAllocationPolicy} may prioritize placing VMs into
     * a private Datacenter first, instead of trying a public one.
     */
    @Getter @Accessors(fluent = true) @AllArgsConstructor
    enum Distribution {
        /** Indicates a datacenter in a public Cloud. */
        PUBLIC('+'),

        /** Indicates a datacenter in a private (sometimes local) Cloud. */
        PRIVATE('#');

        /**
         * A character used as a representation of the distribution name.
         */
        private final char symbol;
    }

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Datacenter} objects.
     */
    DatacenterCharacteristics NULL = new DatacenterCharacteristicsNull();

    /**
     * {@return the distribution type of the datacenter} That is used for classification.
     * @see Distribution
     */
    Distribution getDistribution();

    /**
     * Sets the distribution type of the datacenter, which is used for classification.
     * @param distribution the distribution type to set
     * @return this instance
     */
    DatacenterCharacteristics setDistribution(Distribution distribution);

    /**
     * @return the {@link Datacenter} id.
     */
    @Override
    long getId();

    /**
     * @return the {@link Datacenter} that owns these characteristics
     */
    Datacenter getDatacenter();

    /**
     * @return the total MIPS rating, which is the sum of the MIPS rating across all {@link Host}s in the Datacenter.
     */
    double getMips();

    /**
     * @return the current number of failed PMs the Datacenter has.
     */
    long getNumberOfFailedHosts();

    /**
     * @return the total number of PEs for all PMs.
     */
    int getPesNumber();

    /**
     * @return true if all PMs of the Datacenter are working properly, false otherwise
     */
    boolean isWorking();

    /**
     * Get the monetary cost to use each Megabit of bandwidth in the Datacenter.
     *
     * @return the cost ($) to use bw
     */
    double getCostPerBw();

    /**
     * Sets the monetary cost to use each Megabit of bandwidth.
     *
     * @param costPerBw the cost ($) to set
     */
    DatacenterCharacteristics setCostPerBw(double costPerBw);

    /**
     * Get the monetary cost to use each Megabyte of RAM in the Datacenter.
     *
     * @return the cost ($) to use RAM
     */
    double getCostPerMem();

    /**
     * Sets the monetary cost to use each Megabyte of RAM in the Datacenter.
     *
     * @param costPerMem cost ($) to use RAM
     */
    DatacenterCharacteristics setCostPerMem(double costPerMem);

    /**
     * Gets the monetary cost per second of CPU for using the {@link Host}s in the Datacenter.
     *
     * @return the cost ($) per second
     */
    double getCostPerSecond();

    /**
     * Sets the monetary cost per second of CPU for using the {@link Host}s in the Datacenter.
     *
     * @param costPerSecond the new cost ($) per second
     */
    DatacenterCharacteristics setCostPerSecond(double costPerSecond);

    /**
     * Get the monetary cost to use each Megabyte of storage in the Datacenter.
     *
     * @return the cost ($) to use storage
     */
    double getCostPerStorage();

    /**
     * Sets the monetary cost to use each Megabyte of storage.
     *
     * @param costPerStorage cost ($) to use storage
     */
    DatacenterCharacteristics setCostPerStorage(double costPerStorage);
}
