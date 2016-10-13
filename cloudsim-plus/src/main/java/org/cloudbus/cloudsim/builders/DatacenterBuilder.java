package org.cloudbus.cloudsim.builders;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.resources.FileStorage;

/**
 * A Builder class to createDatacenter {@link DatacenterSimple} objects.
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterBuilder extends Builder {
    public static final String DATACENTER_NAME_FORMAT = "Datacenter%d";

    public static final String VMM = "Xen";
    private String architecture = "x86";
    private String operatingSystem = "Linux";
    private double costPerBwByte = 0.0;
    private double costPerCpuSecond = 3.0;
    private double costPerStorage = 0.001;
    private double costPerMem = 0.05;
    private double schedulingInterval = 1;
    private double timezone = 10;

    private final List<Datacenter> datacenters;
    private int numberOfCreatedDatacenters;
	private List<FileStorage> storageList;

	public DatacenterBuilder() {
        this.datacenters = new ArrayList<>();
		this.storageList = new ArrayList<>();
        this.numberOfCreatedDatacenters = 0;
    }

    public List<Datacenter> getDatacenters() {
        return datacenters;
    }

    public Datacenter get(final int index) {
        if(index >= 0 && index < datacenters.size())
            return datacenters.get(index);

        return Datacenter.NULL;
    }

    public Host getHostOfDatacenter(final int hostIndex, final int datacenterIndex){
        return get(datacenterIndex).getHost(hostIndex);
    }

    public Host getFirstHostFromFirstDatacenter(){
        return getHostOfDatacenter(0,0);
    }

    public DatacenterBuilder createDatacenter(final List<Host> hosts) {
        if (hosts == null || hosts.isEmpty()) {
            throw new RuntimeException("The hosts parameter has to have at least 1 host.");
        }

        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristicsSimple (
                        architecture, operatingSystem, VMM, hosts,
                        timezone, costPerCpuSecond,
                        costPerMem, costPerStorage,
                        costPerBwByte);
        String name = String.format(DATACENTER_NAME_FORMAT, numberOfCreatedDatacenters++);
        Datacenter datacenter =
                new DatacenterSimple(name, characteristics,
                        new VmAllocationPolicySimple(hosts),
                        storageList, schedulingInterval);
        this.datacenters.add(datacenter);
        return this;
    }

    public String getArchitecture() {
        return architecture;
    }

    public DatacenterBuilder setArchitecture(String defaultArchitecture) {
        this.architecture = defaultArchitecture;
        return this;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public DatacenterBuilder setOperatingSystem(String defaultOperatingSystem) {
        this.operatingSystem = defaultOperatingSystem;
        return this;
    }

    public double getCostPerBwByte() {
        return costPerBwByte;
    }

    public DatacenterBuilder setCostPerBwByte(double defaultCostPerBwByte) {
        this.costPerBwByte = defaultCostPerBwByte;
        return this;
    }

    public double getCostPerCpuSecond() {
        return costPerCpuSecond;
    }

    public DatacenterBuilder setCostPerCpuSecond(double defaultCostPerCpuSecond) {
        this.costPerCpuSecond = defaultCostPerCpuSecond;
        return this;
    }

    public double getCostPerStorage() {
        return costPerStorage;
    }

    public DatacenterBuilder setCostPerStorage(double defaultCostPerStorage) {
        this.costPerStorage = defaultCostPerStorage;
        return this;
    }

    public double getCostPerMem() {
        return costPerMem;
    }

    public DatacenterBuilder setCostPerMem(double defaultCostPerMem) {
        this.costPerMem = defaultCostPerMem;
        return this;
    }

    public double getTimezone() {
        return timezone;
    }

    public DatacenterBuilder setTimezone(double defaultTimezone) {
        this.timezone = defaultTimezone;
        return this;
    }

    public double getSchedulingInterval() {
        return schedulingInterval;
    }

    public DatacenterBuilder setSchedulingInterval(double schedulingInterval) {
        this.schedulingInterval = schedulingInterval;
        return this;
    }

	public DatacenterBuilder setStorageList(List<FileStorage> storageList) {
		this.storageList = storageList;
		return this;
	}

	public DatacenterBuilder addStorageToList(FileStorage storage) {
		this.storageList.add(storage);
		return this;
	}

}
