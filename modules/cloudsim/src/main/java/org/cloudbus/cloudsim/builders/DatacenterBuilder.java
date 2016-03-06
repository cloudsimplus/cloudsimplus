package org.cloudbus.cloudsim.builders;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.resources.FileStorage;

/**
 * A Builder class to createDatacenter {@link Datacenter} objects.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterBuilder extends Builder {
    public static final String DATACENTER_NAME_FORMAT = "Datacenter%d";

    public static final String VMM = "Xen"; 
    private String defaultArchitecture = "x86";
    private String defaultOperatingSystem = "Linux";
    private double defaultCostPerBwByte = 0.0;
    private double defaultCostPerCpuSecond = 3.0;
    private double defaultCostPerStorage = 0.001;
    private double defaultCostPerMem = 0.05;
    private double defaultTimezone = 10;
    
    private final List<Datacenter> datacenters;
    private int numberOfCreatedDatacenters;

    public DatacenterBuilder() {
        this.datacenters = new ArrayList<>();
        this.numberOfCreatedDatacenters = 0;
    }    

    public List<Datacenter> getDatacenters() {
        return datacenters;
    }

    public DatacenterBuilder createDatacenter(final List<Host> hosts) {
        if (hosts == null || hosts.isEmpty()) {
            throw new RuntimeException("The hosts parameter has to have at least 1 host.");
        }
        
        LinkedList<FileStorage> storageList = new LinkedList<>();
        DatacenterCharacteristics characteristics = 
                new DatacenterCharacteristics(
                        defaultArchitecture, defaultOperatingSystem, VMM, hosts, 
                        defaultTimezone, defaultCostPerCpuSecond, 
                        defaultCostPerMem, defaultCostPerStorage, 
                        defaultCostPerBwByte);
        String name = String.format(DATACENTER_NAME_FORMAT, numberOfCreatedDatacenters++);
        Datacenter datacenter = 
                new Datacenter(name, characteristics, 
                        new VmAllocationPolicySimple(hosts), 
                        storageList, 0);
        this.datacenters.add(datacenter);
        return this;
    }

    public String getDefaultArchitecture() {
        return defaultArchitecture;
    }

    public DatacenterBuilder setDefaultArchitecture(String defaultArchitecture) {
        this.defaultArchitecture = defaultArchitecture;
        return this;
    }

    public String getDefaultOperatingSystem() {
        return defaultOperatingSystem;
    }

    public DatacenterBuilder setDefaultOperatingSystem(String defaultOperatingSystem) {
        this.defaultOperatingSystem = defaultOperatingSystem;
        return this;
    }

    public double getDefaultCostPerBwByte() {
        return defaultCostPerBwByte;
    }

    public DatacenterBuilder setDefaultCostPerBwByte(double defaultCostPerBwByte) {
        this.defaultCostPerBwByte = defaultCostPerBwByte;
        return this;
    }

    public double getDefaultCostPerCpuSecond() {
        return defaultCostPerCpuSecond;
    }

    public DatacenterBuilder setDefaultCostPerCpuSecond(double defaultCostPerCpuSecond) {
        this.defaultCostPerCpuSecond = defaultCostPerCpuSecond;
        return this;
    }

    public double getDefaultCostPerStorage() {
        return defaultCostPerStorage;
    }

    public DatacenterBuilder setDefaultCostPerStorage(double defaultCostPerStorage) {
        this.defaultCostPerStorage = defaultCostPerStorage;
        return this;
    }

    public double getDefaultCostPerMem() {
        return defaultCostPerMem;
    }

    public DatacenterBuilder setDefaultCostPerMem(double defaultCostPerMem) {
        this.defaultCostPerMem = defaultCostPerMem;
        return this;
    }

    public double getDefaultTimezone() {
        return defaultTimezone;
    }

    public DatacenterBuilder setDefaultTimezone(double defaultTimezone) {
        this.defaultTimezone = defaultTimezone;
        return this;
    }
}
