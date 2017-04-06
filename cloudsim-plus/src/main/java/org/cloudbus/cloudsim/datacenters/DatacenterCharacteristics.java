/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.core.Identificable;
import org.cloudbus.cloudsim.resources.Pe;

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
public interface DatacenterCharacteristics extends Identificable {

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
     * The default Datacenter's Time Zone to be used if not one is set.
     */
    double DEFAULT_TIMEZONE = 0;

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Datacenter}
     * objects.
     */
    DatacenterCharacteristics NULL = new DatacenterCharacteristicsNull();

    /**
     * Gets the time zone, a value between  [-12 and 13].
     *
     * @return the time zone
     */
    double getTimeZone();

    /**
     * Sets the time zone. If an invalid value is given, the timezone is set to 0.
     *
     * @param timeZone the new time zone value, between  [-12 and 13].
     */
    DatacenterCharacteristics setTimeZone(double timeZone);

    /**
     * Sets the vmm.
     *
     * @param vmm the new vmm
     */
    DatacenterCharacteristics setVmm(String vmm);

    /**
     * Gets the {@link Datacenter} that owns these characteristics
     * @return the Datacenter
     */
    Datacenter getDatacenter();

    /**
     * Gets the architecture.
     *
     * @return the architecture
     */
    String getArchitecture();

    /**
     * Sets the architecture.
     *
     * @param architecture the new architecture
     */
    DatacenterCharacteristics setArchitecture(String architecture);

    /**
     * Gets the Operating System (OS).
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
     * Gets the host list.
     *
     * @param <T> The generic type
     * @return the host list
     */
    <T extends Host> List<T> getHostList();

    /**
     * Gets the first PM with at least one empty Pe.
     *
     * @return a Machine object or if not found
     * @pre $none
     * @post $none
     */
    Host getHostWithFreePe();

    /**
     * Gets a Machine with at least a given number of free Pe.
     *
     * @param peNumber the pe number
     * @return a Machine object or if not found
     * @pre $none
     * @post $none
     */
    Host getHostWithFreePe(int peNumber);

    /**
     * Gets the Datacenter id.
     *
     * @return the id
     */
    @Override int getId();

    /**
     * Gets the total MIPS rating, which is the sum of MIPS rating of all Hosts in
     * the Datacenter.
     *
     * @return the sum of MIPS ratings
     *
     * @pre $none
     * @post $result >= 0
     */
    double getMips();

    /**
     * Gets Millions Instructions Per Second (MIPS) Rating of a Processing
     * Element (Pe). It is essential to use this method when a Datacenter is
     * made up of heterogenous PEs per PMs.
     *
     * @param hostId the machine ID
     * @param peId the Pe ID
     * @return the MIPS Rating or -1 if no PEs are exists.
     *
     * @pre id >= 0
     * @pre peID >= 0
     * @post $result >= -1
     */
    long getMipsOfOnePe(int hostId, int peId);

    /**
     * Gets the total number of <tt>BUSY</tt> PEs for all PMs.
     *
     * @return number of PEs
     * @pre $none
     * @post $result >= 0
     */
    int getNumberOfBusyPes();

    /**
     * Gets the current number of failed PMs.
     *
     * @return current number of failed PMs the Datacenter has.
     */
    long getNumberOfFailedHosts();

    /**
     * Gets the total number of <tt>FREE</tt> or non-busy PEs for all PMs.
     *
     * @return number of PEs
     * @pre $none
     * @post $result >= 0
     */
    int getNumberOfFreePes();

    /**
     * Gets the total number of PMs.
     *
     * @return total number of machines the Datacenter has.
     */
    int getNumberOfHosts();

    /**
     * Gets the total number of PEs for all PMs.
     *
     * @return number of PEs
     * @pre $none
     * @post $result >= 0
     */
    int getNumberOfPes();

    /**
     * Gets the name of a resource.
     *
     * @return the resource name
     * @pre $none
     * @post $result != null
     */
    String getResourceName();

    /**
     * Gets the VMM in use in the Datacenter.
     *
     * @return the VMM name
     */
    String getVmm();

    /**
     * Checks whether all PMs of the Datacenter are working properly or not.
     *
     * @return if all PMs are working, otherwise
     */
    boolean isWorking();

    /**
     * Get the cost to use each each Megabit of bandwidth in the Datacenter.
     *
     * @return the cost to use bw
     */
    double getCostPerBw();

    /**
     * Get the cost to use each Megabyte of RAM in the Datacenter.
     *
     * @return the cost to use RAM
     */
    double getCostPerMem();

    /**
     * Gets the cost per second of CPU.
     *
     * @return the cost per second
     */
    double getCostPerSecond();

    /**
     * Get the cost to use each Megabyte of storage in the Datacenter.
     *
     * @return the cost to use storage
     */
    double getCostPerStorage();

    /**
     * Sets the cost per second of CPU.
     *
     * @param costPerSecond the new cost per second
     */
    DatacenterCharacteristics setCostPerSecond(double costPerSecond);

    /**
     * Sets cost to use each Megabit of bandwidth.
     *
     * @param costPerBw the cost to set
     * @pre costPerBw >= 0
     * @post $none
     */
    DatacenterCharacteristics setCostPerBw(double costPerBw);

    /**
     * Sets the cost to use each Megabyte of RAM in the Datacenter.
     *
     * @param costPerMem cost to use RAM
     * @pre costPerMem >= 0
     * @post $none
     */
    DatacenterCharacteristics setCostPerMem(double costPerMem);

    /**
     * Sets cost to use each Megabyte of storage.
     *
     * @param costPerStorage cost to use storage
     * @pre costPerStorage >= 0
     * @post $none
     */
    DatacenterCharacteristics setCostPerStorage(double costPerStorage);

    /**
     * Sets the {@link Datacenter} that owns these characteristics
     * @param datacenter the Datacenter to set
     */
    DatacenterCharacteristics setDatacenter(Datacenter datacenter);

    /**
     * Sets the particular Pe status on a PM.
     *
     * @param status the new Pe status
     * @param hostId Machine ID
     * @param peId Pe id
     * @return otherwise (Machine id or Pe id might not be exist)
     * @pre machineID >= 0
     * @pre peID >= 0
     * @post $none
     */
    boolean setPeStatus(Pe.Status status, int hostId, int peId);
}
