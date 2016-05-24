/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.resources.Pe;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.HostList;
import org.cloudbus.cloudsim.lists.PeList;

/**
 * Represents static properties of a Datacenter such as architecture, Operating
 * System (OS), management policy (time- or space-shared), cost and time zone at
 * which the resource is located along resource configuration. Each
 * {@link Datacenter} has to have its own instance of this class, since it
 * stores the Datacenter host list.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterCharacteristicsSimple implements DatacenterCharacteristics {

    /**
     * The datacenter id -- setup when datacenter is created.
     */
    private int id;

    /**
     * The architecture of the resource.
     */
    private String architecture;

    /**
     * The Operating System (OS) of the resource.
     */
    private String os;

    /**
     * The hosts owned by the datacenter.
     */
    private List<? extends Host> hostList;

    /**
     * The time zone, defined as the difference from GMT.
     */
    private double timeZone;

    /**
     * Price/CPU-unit. If unit = sec., then the price is defined as G$/CPU-sec.
     */
    private double costPerSecond;

    /**
     * The CPU allocation policy for all PMs of the datacenter, according to
     * constants such as {@link #TIME_SHARED} and {@link #SPACE_SHARED}.
     *
     * @todo The use of int constants difficult to know the valid values for the
     * property. It may be used a enum instead.
     */
    private int allocationPolicy;


    /**
     * The Virtual Machine Monitor (VMM), also called hypervisor, used in the
     * datacenter..
     */
    private String vmm;

    /**
     * The cost per each unity of RAM memory.
     */
    private double costPerMem;

    /**
     * The cost per each unit of storage.
     */
    private double costPerStorage;

    /**
     * The cost of each byte of bandwidth (bw) consumed.
     */
    private double costPerBw;

    /** @see #getDatacenter() */
    private Datacenter datacenter;

    /**
     * Creates a new DatacenterCharacteristics object. If the time zone is
     * invalid, then by default, it will be GMT+0.
     *
     * @param architecture the architecture of the datacenter
     * @param os the operating system used on the datacenter's PMs
     * @param vmm the virtual machine monitor used
     * @param hostList list of machines in the datacenter
     * @param timeZone local time zone of a user that owns this reservation.
     * Time zone should be of range [GMT-12 ... GMT+13]
     * @param costPerSec the cost per sec of CPU use in the datacenter
     * @param costPerMem the cost to use memory in the datacenter
     * @param costPerStorage the cost to use storage in the datacenter
     * @param costPerBw the cost of each byte of bandwidth (bw) consumed
     *
     * @pre architecture != null
     * @pre OS != null
     * @pre VMM != null
     * @pre machineList != null
     * @pre timeZone >= -12 && timeZone <= 13
	 * @
     * pre costPerSec >= 0.0
     * @pre costPerMem >= 0
     * @pre costPerStorage >= 0
     * @post $none
     */
    public DatacenterCharacteristicsSimple(
            String architecture,
            String os,
            String vmm,
            List<? extends Host> hostList,
            double timeZone,
            double costPerSec,
            double costPerMem,
            double costPerStorage,
            double costPerBw) {
        setId(-1);
        setArchitecture(architecture);
        setOs(os);
        setHostList(hostList);
        /*@todo allocationPolicy is not a parameter. It is setting
         the attribute to itself, what has not effect. */
        setAllocationPolicy(allocationPolicy);
        setCostPerSecond(costPerSec);

        setTimeZone(timeZone);

        setVmm(vmm);
        setCostPerMem(costPerMem);
        setCostPerStorage(costPerStorage);
        setCostPerBw(costPerBw);
    }

    @Override
    public String getResourceName() {
        return CloudSim.getEntityName(getId());
    }

    @Override
    public Host getHostWithFreePe() {
        return HostList.getHostWithFreePe(getHostList());
    }

    @Override
    public Host getHostWithFreePe(int peNumber) {
        return HostList.getHostWithFreePe(getHostList(), peNumber);
    }

    @Override
    public int getMipsOfOnePe() {
        if (getHostList().isEmpty()) {
            return -1;
        }

        /*@todo Why is it always get the MIPS of the first host in the datacenter?
         The note in the method states that it is considered that all PEs into
         a PM have the same MIPS capacity, but different PM can have different
         PEs' MIPS.*/
        return PeList.getMips(getHostList().get(0).getPeList(), 0);
    }

    @Override
    public int getMipsOfOnePe(int hostId, int peId) {
        if (getHostList().isEmpty()) {
            return -1;
        }

        return PeList.getMips(HostList.getById(getHostList(), hostId).getPeList(), peId);
    }

    @Override
    public int getMips() {
        int mips = 0;
        /*@todo It assumes that the heterogeinety of PE's capacity of PMs
         is dependent of the CPU allocation policy of the Datacenter.
         However, I don't see any relation between PMs heterogeinety and
         allocation policy.
         I can have a time shared policy in a datacenter of
         PMs with the same or different processing capacity.
         The same is true for a space shared or even any other policy. 
         */

        /*@todo the method doesn't use polymorphism to ensure that it will
         automatically behave according to the instance of the allocationPolicy used.
         The use of a switch here breaks the Open/Close Principle (OCP).
         Thus, it doesn't allow the class to be closed for changes
         and opened for extension.
         If a new scheduler is created, the class has to be changed
         to include the new scheduler in switches like that below.
         */
        switch (getAllocationPolicy()) {
                        // Assuming all PEs in all PMs have same rating.
                        /*@todo But it is possible to add PMs of different configurations
             in a hostlist attached to a DatacenterCharacteristic attribute
             of a Datacenter*/
            case DatacenterCharacteristics.TIME_SHARED:
            case DatacenterCharacteristics.OTHER_POLICY_SAME_RATING:
                mips = getMipsOfOnePe() * HostList.getNumberOfPes(getHostList());
                break;

			// Assuming all PEs in a given PM have the same rating.
            // But different PMs in a Cluster can have different rating
            case DatacenterCharacteristics.SPACE_SHARED:
            case DatacenterCharacteristics.OTHER_POLICY_DIFFERENT_RATING:
                for (Host host : getHostList()) {
                    mips += host.getTotalMips();
                }
                break;

            default:
                break;
        }

        return mips;
    }

    @Override
    public double getCpuTime(double cloudletLength, double load) {
        double cpuTime = 0.0;

        switch (getAllocationPolicy()) {
            case DatacenterCharacteristics.TIME_SHARED:
                /*@todo It is not exacly clear what this method does.
                 I guess it computes how many time the cloudlet will
                 spend using the CPU to finish its job, considering 
                 the CPU allocation policy. By this way,
                 the load parameter may be cloudlet's the percentage of load (from 0 to 1).
                 Then, (getMipsOfOnePe() * (1.0 - load)) computes the amount
                 MIPS that is currently being used by the cloudlet.
                 Dividing the total cloudlet length in MI by that result
                 returns the number of seconds that the cloudlet will spend
                 to execute its total MI.
                                
                 This method has to be reviewed and documentation
                 checked.
                            
                 If load is equals to 1, this calculation will 
                 raise and division by zero exception, what makes invalid
                 the pre condition defined in the method documention*/
                cpuTime = cloudletLength / (getMipsOfOnePe() * (1.0 - load));
                break;

            default:
                break;
        }

        return cpuTime;
    }

    @Override
    public int getNumberOfPes() {
        return HostList.getNumberOfPes(getHostList());
    }

    @Override
    public int getNumberOfFreePes() {
        return HostList.getNumberOfFreePes(getHostList());
    }

    @Override
    public int getNumberOfBusyPes() {
        return HostList.getNumberOfBusyPes(getHostList());
    }

    @Override
    public boolean setPeStatus(Pe.Status status, int hostId, int peId) {
        return HostList.setPeStatus(getHostList(), status, hostId, peId);
    }

    @Override
    public double getCostPerMi() {
        return getCostPerSecond() / getMipsOfOnePe();
    }

    @Override
    public int getNumberOfHosts() {
        return getHostList().size();
    }

    @Override
    public int getNumberOfFailedHosts() {
        int numberOfFailedHosts = 0;
        for (Host host : getHostList()) {
            if (host.isFailed()) {
                numberOfFailedHosts++;
            }
        }
        return numberOfFailedHosts;
    }

    @Override
    public boolean isWorking() {
        boolean result = false;
        if (getNumberOfFailedHosts() == 0) {
            result = true;
        }

        return result;
    }

    @Override
    public double getCostPerMem() {
        return costPerMem;
    }

    @Override
    public final void setCostPerMem(double costPerMem) {
        this.costPerMem = costPerMem;
    }

    @Override
    public double getCostPerStorage() {
        return costPerStorage;
    }

    @Override
    public final void setCostPerStorage(double costPerStorage) {
        this.costPerStorage = costPerStorage;
    }

    @Override
    public double getCostPerBw() {
        return costPerBw;
    }

    @Override
    public final void setCostPerBw(double costPerBw) {
        this.costPerBw = costPerBw;
    }

    @Override
    public String getVmm() {
        return vmm;
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * Sets the datacenter id.
     *
     * @param id the new id
     */
    protected final void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the architecture.
     *
     * @return the architecture
     */
    protected String getArchitecture() {
        return architecture;
    }

    /**
     * Sets the architecture.
     *
     * @param architecture the new architecture
     */
    protected final void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    /**
     * Gets the Operating System (OS).
     *
     * @return the Operating System (OS)
     */
    protected String getOs() {
        return os;
    }

    /**
     * Sets the Operating System (OS).
     *
     * @param os the new Operating System (OS)
     */
    protected final void setOs(String os) {
        this.os = os;
    }

    @Override
    public <T extends Host> List<T> getHostList() {
        return (List<T>) hostList;
    }

    /**
     * Sets the host list.
     *
     * @param hostList the new host list
     */
    protected final void setHostList(List<? extends Host> hostList) {
        this.hostList = hostList;
    }

    /**
     * Gets the time zone.
     *
     * @return the time zone
     */
    protected double getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the time zone.
     *
     * @param timeZone the new time zone
     */
    protected final void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public double getCostPerSecond() {
        return costPerSecond;
    }

    /**
     * Sets the cost per second of CPU.
     *
     * @param costPerSecond the new cost per second
     */
    protected final void setCostPerSecond(double costPerSecond) {
        this.costPerSecond = costPerSecond;
    }

    /**
     * Gets the allocation policy.
     *
     * @return the allocation policy
     */
    protected int getAllocationPolicy() {
        return allocationPolicy;
    }

    /**
     * Sets the allocation policy.
     *
     * @param allocationPolicy the new allocation policy
     */
    protected final void setAllocationPolicy(int allocationPolicy) {
        this.allocationPolicy = allocationPolicy;
    }

    /**
     * Sets the vmm.
     *
     * @param vmm the new vmm
     */
    protected final void setVmm(String vmm) {
        this.vmm = vmm;
    }

    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }

    @Override
    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
        this.setId(datacenter.getId());
    }
}
