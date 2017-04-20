package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link Datacenter}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see DatacenterCharacteristics#NULL
 */
final class DatacenterCharacteristicsNull implements DatacenterCharacteristics {
    @Override public double getCostPerBw() {
        return 0;
    }
    @Override public double getCostPerMem() {
        return 0;
    }
    @Override public double getTimeZone() {
        return 0;
    }
    @Override public DatacenterCharacteristics setTimeZone(double timeZone) {
        return DatacenterCharacteristics.NULL;
    }
    @Override public double getCostPerSecond() {
        return 0;
    }
    @Override public double getCostPerStorage() {
        return 0;
    }
    @Override public DatacenterCharacteristics setCostPerSecond(double c) { return DatacenterCharacteristics.NULL; }
    @Override public DatacenterCharacteristics setVmm(String vmm) {
        return DatacenterCharacteristics.NULL;
    }
    @Override public Datacenter getDatacenter() {
        return Datacenter.NULL;
    }
    @Override public String getArchitecture() {
        return "";
    }
    @Override public DatacenterCharacteristics setArchitecture(String a) { return DatacenterCharacteristics.NULL; }
    @Override public String getOs() {
        return "";
    }
    @Override public DatacenterCharacteristics setOs(String os) {
        return DatacenterCharacteristics.NULL;
    }
    @Override public <T extends Host> List<T> getHostList() {
        return Collections.EMPTY_LIST;
    }
    @Override public Host getHostWithFreePe() {
        return Host.NULL;
    }
    @Override public Host getHostWithFreePe(int peNumber) {
        return Host.NULL;
    }
    @Override public int getId() {
        return 0;
    }
    @Override public double getMips() { return 0; }
    @Override public long getMipsOfOnePe(int hostId, int peId) {
        return 0;
    }
    @Override public int getNumberOfBusyPes() {
        return 0;
    }
    @Override public long getNumberOfFailedHosts() {
        return 0;
    }
    @Override public int getNumberOfFreePes() {
        return 0;
    }
    @Override public int getNumberOfHosts() {
        return 0;
    }
    @Override public int getNumberOfPes() {
        return 0;
    }
    @Override public String getResourceName() {
        return "";
    }
    @Override public String getVmm() {
        return "";
    }
    @Override public boolean isWorking() {
        return false;
    }
    @Override public DatacenterCharacteristics setCostPerBw(double c) {
        return DatacenterCharacteristics.NULL;
    }
    @Override public DatacenterCharacteristics setCostPerMem(double c) { return DatacenterCharacteristics.NULL; }
    @Override public DatacenterCharacteristics setCostPerStorage(double c) { return DatacenterCharacteristics.NULL; }
    @Override public DatacenterCharacteristics setDatacenter(Datacenter dc) { return DatacenterCharacteristics.NULL; }
    @Override public boolean setPeStatus(Pe.Status status, int hostId, int peId) {
        return false;
    }
}
