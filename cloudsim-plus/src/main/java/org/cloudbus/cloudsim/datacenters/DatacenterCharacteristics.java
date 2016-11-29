package org.cloudbus.cloudsim.datacenters;

import java.util.Collections;
import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.core.Identificable;
import org.cloudbus.cloudsim.resources.Pe;

/**
 * An interface to be implemented by each class that represents
 * the physical characteristics of a Datacenter.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface DatacenterCharacteristics extends Identificable {

    /**
     * A resource that supports Advanced Reservation mechanisms.
     */
    int ADVANCE_RESERVATION = 4;

    /**
     * Assuming all PEs in a PM have the same rating. However, each PM has
     * different rating to each other.
     */
    int OTHER_POLICY_DIFFERENT_RATING = 3;

    /**
     * Assuming all PEs in all PMs have the same rating.
     */
    int OTHER_POLICY_SAME_RATING = 2;

    /**
     * Spaced-shared CPU allocation policy using First Come First Serve (FCFS)
     * algorithm.
     */
    int SPACE_SHARED = 1;

    /**
     * Time-shared CPU allocation policy using Round-Robin algorithm.
     */
    int TIME_SHARED = 0;
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
     * @return the switches
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
     * Gets the switches id.
     *
     * @return the id
     */
    @Override int getId();

    /**
     * Gets the total MIPS rating, which is the sum of MIPS rating of all Hosts in
     * the switches.
     *
     * @return the sum of MIPS ratings
     *
     * @pre $none
     * @post $result >= 0
     */
    int getMips();

    /**
     * Gets Millions Instructions Per Second (MIPS) Rating of a Processing
     * Element (Pe). It is essential to use this method when a switches is
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
    int getMipsOfOnePe(int hostId, int peId);

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
    int getNumberOfFailedHosts();

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
     * Gets the VMM in use in the switches.
     *
     * @return the VMM name
     */
    String getVmm();

    /**
     * Checks whether all PMs of the switches are working properly or not.
     *
     * @return if all PMs are working, otherwise
     */
    boolean isWorking();

    /**
     * Get the cost to use each each Megabit of bandwidth in the switches.
     *
     * @return the cost to use bw
     */
    double getCostPerBw();

    /**
     * Get the cost to use each Megabyte of RAM in the switches.
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
     * Get the cost to use each Megabyte of storage in the switches.
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
     * Sets the cost to use each Megabyte of RAM in the switches.
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
     * @param datacenter
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

    /**
     * A property that implements the Null Object Design Pattern for {@link Datacenter}
     * objects.
     */
    DatacenterCharacteristics NULL = new DatacenterCharacteristics() {
        @Override public double getCostPerBw() { return 0; }
        @Override public double getCostPerMem() { return 0; }
        @Override public double getTimeZone() { return 0; }
        @Override public DatacenterCharacteristics setTimeZone(double timeZone) { return DatacenterCharacteristics.NULL; }
        @Override public double getCostPerSecond() { return 0; }
        @Override public double getCostPerStorage() { return 0; }
        @Override public DatacenterCharacteristics setCostPerSecond(double costPerSecond) { return DatacenterCharacteristics.NULL; }
        @Override public DatacenterCharacteristics setVmm(String vmm) { return DatacenterCharacteristics.NULL; }
        @Override public Datacenter getDatacenter() { return Datacenter.NULL; }
        @Override public String getArchitecture() { return ""; }@Override public DatacenterCharacteristics setArchitecture(String architecture) { return DatacenterCharacteristics.NULL; }
        @Override public String getOs() { return ""; }
        @Override public DatacenterCharacteristics setOs(String os) { return DatacenterCharacteristics.NULL; }
        @Override public <T extends Host> List<T> getHostList() { return Collections.EMPTY_LIST; }
        @Override public Host getHostWithFreePe() { return Host.NULL; }
        @Override public Host getHostWithFreePe(int peNumber) { return Host.NULL; }
        @Override public int getId() { return 0; }
        @Override public int getMips() { return 0; }
        @Override public int getMipsOfOnePe(int hostId, int peId) { return 0; }
        @Override public int getNumberOfBusyPes() { return 0; }
        @Override public int getNumberOfFailedHosts() { return 0; }
        @Override public int getNumberOfFreePes() { return 0; }
        @Override public int getNumberOfHosts() { return 0; }
        @Override public int getNumberOfPes() { return 0; }
        @Override public String getResourceName() { return ""; }
        @Override public String getVmm() { return ""; }
        @Override public boolean isWorking() { return false; }
        @Override public DatacenterCharacteristics setCostPerBw(double costPerBw) { return DatacenterCharacteristics.NULL; }
        @Override public DatacenterCharacteristics setCostPerMem(double costPerMem) { return DatacenterCharacteristics.NULL; }
        @Override public DatacenterCharacteristics setCostPerStorage(double costPerStorage) { return DatacenterCharacteristics.NULL; }
        @Override public DatacenterCharacteristics setDatacenter(Datacenter datacenter) { return DatacenterCharacteristics.NULL; }
        @Override public boolean setPeStatus(Pe.Status status, int hostId, int peId) { return false; }
    };

}
