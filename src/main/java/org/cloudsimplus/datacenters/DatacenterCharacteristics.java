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

/**
 * An interface to be implemented by each class that represents
 * the physical characteristics of a Datacenter.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface DatacenterCharacteristics extends Identifiable {
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
     * An attribute that implements the Null Object Design Pattern for {@link Datacenter}
     * objects.
     */
    DatacenterCharacteristics NULL = new DatacenterCharacteristicsNull();

    /**
     * {@return the distribution type of the datacenter} That is used for classification purposes.
     * @see Distribution
     */
    Distribution getDistribution();

    /**
     * Sets the distribution type of the datacenter, which is used for classification purposes.
     * @param distribution the distribution type to set
     * @return
     */
    DatacenterCharacteristics setDistribution(Distribution distribution);

    /**
     * Gets the Datacenter id.
     * @return
     */
    @Override
    long getId();

    /**
     * Gets the {@link Datacenter} that owns these characteristics
     * @return
     */
    Datacenter getDatacenter();

    /**
     * Gets the total MIPS rating, which is the sum of MIPS rating of all Hosts in
     * the Datacenter.
     *
     * @return the sum of MIPS ratings
     */
    double getMips();

    /**
     * Gets the current number of failed PMs.
     *
     * @return current number of failed PMs the Datacenter has.
     */
    long getNumberOfFailedHosts();

    /**
     * Gets the total number of PEs for all PMs.
     *
     * @return number of PEs
     */
    int getPesNumber();

    /**
     * Checks whether all PMs of the Datacenter are working properly or not.
     *
     * @return if all PMs are working, otherwise
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
     * Gets the monetary cost per second of CPU for using the Hosts in the Datacenter.
     *
     * @return the cost ($) per second
     */
    double getCostPerSecond();

    /**
     * Sets the monetary cost per second of CPU.
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
