/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.core.Identifiable;

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
     * The default Virtual Machine Monitor to be used if not one is set.
     */
    String DEFAULT_VMM = "Xen";

    /**
     * The default architecture of Datacenter Hosts to be used if not one is set.
     */
    String DEFAULT_ARCH = "x86";

    /**
     * The default Operating System of Datacenter Hosts to be used if not one is set.
     */
    String DEFAULT_OS = "Linux";

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Datacenter}
     * objects.
     */
    DatacenterCharacteristics NULL = new DatacenterCharacteristicsNull();

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
     * Gets the Virtual Machine Monitor (VMM), also called hypervisor, used in the Datacenter.
     *
     * @return the VMM name
     */
    String getVmm();

    /**
     * Sets the VMM.
     * @param vmm the new VMM
     */
    DatacenterCharacteristics setVmm(String vmm);

    /**
     * Gets the architecture of the Datacenter.
     *
     * @return the architecture
     */
    String getArchitecture();

    /**
     * Sets the architecture of the Datacenter.
     *
     * @param architecture the new architecture
     */
    DatacenterCharacteristics setArchitecture(String architecture);

    /**
     * Gets the Operating System (OS) used by the Hosts in the Datacenter.
     *
     * @return the Operating System (OS)
     */
    String getOs();

    /**
     * Sets the Operating System (OS).
     *
     * @param os the new Operating System (OS)
     */
    DatacenterCharacteristics setOs(String os);

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
    int getNumberOfPes();

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
