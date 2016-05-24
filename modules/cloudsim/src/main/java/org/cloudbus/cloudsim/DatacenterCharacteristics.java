package org.cloudbus.cloudsim;

import java.util.Collections;
import java.util.List;
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
     * Get the cost to use bandwidth in the datacenter.
     *
     * @return the cost to use bw
     */
    double getCostPerBw();

    /**
     * Get the cost to use memory in the datacenter.
     *
     * @return the cost to use memory
     */
    double getCostPerMem();

    /**
     * Gets the cost per Million Instruction (MI) associated with a Datacenter.
     *
     * @return the cost using CPU of PM in the Datacenter
     * @pre $none
     * @post $result >= 0.0
     * @todo Again, it considers that all PEs of all PM have the same MIPS
     * capacity, what is not ensured because it is possible to add PMs of
     * different configurations to a datacenter
     */
    double getCostPerMi();

    /**
     * Gets the cost per second of CPU.
     *
     * @return the cost per second
     */
    double getCostPerSecond();

    /**
     * Get the cost to use storage in the datacenter.
     *
     * @return the cost to use storage
     */
    double getCostPerStorage();

    /**
     * Gets the amount of CPU time (in seconds) that the cloudlet will spend to
     * finish processing, considering the current CPU allocation policy
     * (currently only for TIME_SHARED) and cloudlet load.
     *
     * @todo <tt>NOTE:</tt> The CPU time for SPACE_SHARED and
     * ADVANCE_RESERVATION are not yet implemented.
     *
     * @param cloudletLength the length of a Cloudlet
     * @param load the current load of a Cloudlet (percentage of load from 0 to
     * 1)
     * @return the CPU time (in seconds)
     *
     * @pre cloudletLength >= 0.0
     * @pre load >= 0.0
     * @post $result >= 0.0
     */
    double getCpuTime(double cloudletLength, double load);

    /**
     * Gets the {@link Datacenter} that owns these characteristics
     * @return the datacenter
     */
    Datacenter getDatacenter();

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
     * Gets the datacenter id.
     *
     * @return the id
     */
    @Override int getId();

    /**
     * Gets the total MIPS rating, which is the sum of MIPS rating of all PMs in
     * a datacenter.
     * <p>
     * Total MIPS rating for:
     * <ul>
     * <li>TimeShared = 1 Rating of a Pe * Total number of PEs
     * <li>Other policy same rating = same as TimeShared
     * <li>SpaceShared = Sum of all PEs in all Machines
     * <li>Other policy different rating = same as SpaceShared
     * <li>Advance Reservation = 0 or unknown. You need to calculate this
     * manually.
     * </ul>
     *
     * @return the sum of MIPS ratings
     *
     * @pre $none
     * @post $result >= 0
     */
    int getMips();

    /**
     * Gets the Million Instructions Per Second (MIPS) Rating of the first
     * Processing Element (Pe) of the first PM.
     * <tt>NOTE:</tt>It is assumed all PEs' rating is same in a given machine.
     *
     *
     * @return the MIPS Rating or -1 if no PEs exists
     *
     * @pre $none
     * @post $result >= -1
     * @todo It considers that all PEs of all PMs have the same MIPS capacity,
     * what is not ensured because it is possible to add PMs of different
     * configurations to a datacenter. Even for the {@link Host} it is possible
     * to add Pe's of different capacities through the {@link Host#peList}
     * attribute.
     */
    int getMipsOfOnePe();

    /**
     * Gets Millions Instructions Per Second (MIPS) Rating of a Processing
     * Element (Pe). It is essential to use this method when a datacenter is
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
     * Gets the VMM in use in the datacenter.
     *
     * @return the VMM name
     */
    String getVmm();

    /**
     * Checks whether all PMs of the datacenter are working properly or not.
     *
     * @return if all PMs are working, otherwise
     */
    boolean isWorking();

    /**
     * Sets cost to use bw.
     *
     * @param costPerBw the cost per bw
     * @pre costPerBw >= 0
     * @post $none
     */
    void setCostPerBw(double costPerBw);

    /**
     * Sets cost to use memory.
     *
     * @param costPerMem cost to use memory
     * @pre costPerMem >= 0
     * @post $none
     */
    void setCostPerMem(double costPerMem);

    /**
     * Sets cost to use storage.
     *
     * @param costPerStorage cost to use storage
     * @pre costPerStorage >= 0
     * @post $none
     */
    void setCostPerStorage(double costPerStorage);

    /**
     * Sets the {@link Datacenter} that owns these characteristics
     * @param datacenter
     */
    void setDatacenter(Datacenter datacenter);

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
        @Override public double getCostPerMi() { return 0; }
        @Override public double getCostPerSecond() { return 0; }
        @Override public double getCostPerStorage() { return 0; }
        @Override public double getCpuTime(double cloudletLength, double load) { return 0; }
        @Override public Datacenter getDatacenter() { return Datacenter.NULL; }
        @Override public <T extends Host> List<T> getHostList() { return Collections.EMPTY_LIST; }
        @Override public Host getHostWithFreePe() { return Host.NULL; }
        @Override public Host getHostWithFreePe(int peNumber) { return Host.NULL; }
        @Override public int getId() { return 0; }
        @Override public int getMips() { return 0; }
        @Override public int getMipsOfOnePe() { return 0; }
        @Override public int getMipsOfOnePe(int hostId, int peId) { return 0; }
        @Override public int getNumberOfBusyPes() { return 0; }
        @Override public int getNumberOfFailedHosts() { return 0; }
        @Override public int getNumberOfFreePes() { return 0; }
        @Override public int getNumberOfHosts() { return 0; }
        @Override public int getNumberOfPes() { return 0; }
        @Override public String getResourceName() { return ""; }
        @Override public String getVmm() { return ""; }
        @Override public boolean isWorking() { return false; }
        @Override public void setCostPerBw(double costPerBw) {}
        @Override public void setCostPerMem(double costPerMem) {}
        @Override public void setCostPerStorage(double costPerStorage) {}
        @Override public void setDatacenter(Datacenter datacenter) {}
        @Override public boolean setPeStatus(Pe.Status status, int hostId, int peId) { return false; }
    };
    
}
