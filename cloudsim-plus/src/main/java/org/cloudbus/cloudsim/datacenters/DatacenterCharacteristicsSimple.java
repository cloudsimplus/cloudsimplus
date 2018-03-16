/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;

import java.util.List;
import java.util.Objects;

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
     * The Datacenter id -- setup when Datacenter is created.
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
     * The hosts owned by the Datacenter.
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
     * The Virtual Machine Monitor (VMM), also called hypervisor, used in the
     * Datacenter..
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
     * Creates a DatacenterCharacteristics with default values
     * for {@link #getArchitecture() architecture}, {@link #getOs() OS}, {@link #getTimeZone() Time Zone} and
     * {@link #getVmm() VMM}. The costs for {@link #getCostPerBw() BW}, {@link #getCostPerMem()} () RAM}
     * and {@link #getCostPerStorage()} () Storage} are set to zero.
     *
     * @param hostList list of {@link Host} in the Datacenter
     *
     * @pre machineList != null
     * @post $none
     */
    public DatacenterCharacteristicsSimple(List<? extends Host> hostList){
        setId(-1);
        setHostList(hostList);
        setArchitecture(DEFAULT_ARCH);
        setOs(DEFAULT_OS);
        setHostList(hostList);
        setTimeZone(DEFAULT_TIMEZONE);
        setVmm(DEFAULT_VMM);
        setCostPerSecond(0);
        setCostPerMem(0);
        setCostPerStorage(0);
        setCostPerBw(0);
        this.datacenter = Datacenter.NULL;
    }

    /**
     * Creates a DatacenterCharacteristics. If the time zone is
     * invalid, then by default, it will be GMT+0.
     *
     * @param architecture the architecture of the Datacenter
     * @param os the operating system used on the Datacenter's PMs
     * @param vmm the virtual machine monitor used
     * @param hostList list of machines in the Datacenter
     * @param timeZone local time zone of a user that owns this reservation.
     * Time zone should be of range [GMT-12 ... GMT+13]
     * @param costPerSec the cost per sec of CPU use in the Datacenter
     * @param costPerMem the cost to use memory in the Datacenter
     * @param costPerStorage the cost to use storage in the Datacenter
     * @param costPerBw the cost of each byte of bandwidth (bw) consumed
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     *
     * @pre architecture != null
     * @pre OS != null
     * @pre VMM != null
     * @pre machineList != null
     * @pre timeZone >= -12 && timeZone <= 13
     * @pre costPerSec >= 0.0
     * @pre costPerMem >= 0
     * @pre costPerStorage >= 0
     * @post $none
     */
    @Deprecated
    public DatacenterCharacteristicsSimple(
        String architecture,
        String os,
        String vmm,
        List<? extends Host> hostList,
        double timeZone,
        double costPerSec,
        double costPerMem,
        double costPerStorage,
        double costPerBw)
    {
            this(hostList);
            this.setTimeZone(timeZone)
                .setCostPerSecond(costPerSec)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);
    }

    @Override
    public String getResourceName() {
        return datacenter.getSimulation().getEntityName(getId());
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
    public long getMipsOfOnePe(int hostId, int peId) {
        if (getHostList().isEmpty()) {
            return -1;
        }

        return PeList.getMips(HostList.getById(getHostList(), hostId).getPeList(), peId);
    }

    @Override
    public double getMips() {
        return getHostList().stream().mapToDouble(Host::getTotalMipsCapacity).sum();
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
    public int getNumberOfHosts() {
        return getHostList().size();
    }

    @Override
    public long getNumberOfFailedHosts() {
        return getHostList().stream().filter(Host::isFailed).count();
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
    public final DatacenterCharacteristics setCostPerMem(double costPerMem) {
        this.costPerMem = costPerMem;
        return this;
    }

    @Override
    public double getCostPerStorage() {
        return costPerStorage;
    }

    @Override
    public final DatacenterCharacteristics setCostPerStorage(double costPerStorage) {
        this.costPerStorage = costPerStorage;
        return this;
    }

    @Override
    public double getCostPerBw() {
        return costPerBw;
    }

    @Override
    public final DatacenterCharacteristics setCostPerBw(double costPerBw) {
        this.costPerBw = costPerBw;
        return this;
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
     * Sets the Datacenter id.
     *
     * @param id the new id
     */
    protected final void setId(int id) {
        this.id = id;
    }

    @Override
    public String getArchitecture() {
        return architecture;
    }

    @Override
    public final DatacenterCharacteristics setArchitecture(String architecture) {
        this.architecture = architecture;
        return this;
    }

    @Override
    public String getOs() {
        return os;
    }

    @Override
    public final DatacenterCharacteristics setOs(String os) {
        this.os = os;
        return this;
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
        Objects.requireNonNull(hostList);
        this.hostList = hostList;
    }

    @Override
    public double getTimeZone() {
        return timeZone;
    }

    @Override
    public final DatacenterCharacteristics setTimeZone(double timeZone) {
        if(timeZone < -12 || timeZone > 13)
            timeZone = 0;

        this.timeZone = timeZone;
        return this;
    }

    @Override
    public double getCostPerSecond() {
        return costPerSecond;
    }

    @Override
    public final DatacenterCharacteristics setCostPerSecond(double costPerSecond) {
        this.costPerSecond = costPerSecond;
        return this;
    }

    @Override
    public final DatacenterCharacteristics setVmm(String vmm) {
        this.vmm = vmm;
        return this;
    }

    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }

    @Override
    public DatacenterCharacteristics setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
        this.setId(datacenter.getId());
        return this;
    }

}
