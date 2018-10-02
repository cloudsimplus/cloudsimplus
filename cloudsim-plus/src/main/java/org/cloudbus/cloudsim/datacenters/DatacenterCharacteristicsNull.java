package org.cloudbus.cloudsim.datacenters;

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
    @Override public DatacenterCharacteristics setCostPerSecond(double cost) { return DatacenterCharacteristics.NULL; }
    @Override public DatacenterCharacteristics setVmm(String vmm) {
        return DatacenterCharacteristics.NULL;
    }
    @Override public Datacenter getDatacenter() {
        return Datacenter.NULL;
    }
    @Override public String getArchitecture() {
        return "";
    }
    @Override public DatacenterCharacteristics setArchitecture(String arch) { return DatacenterCharacteristics.NULL; }
    @Override public String getOs() {
        return "";
    }
    @Override public DatacenterCharacteristics setOs(String os) {
        return DatacenterCharacteristics.NULL;
    }
    @Override public long getId() {
        return 0;
    }
    @Override public double getMips() { return 0; }
    @Override public long getNumberOfFailedHosts() {
        return 0;
    }
    @Override public int getNumberOfFreePes() {
        return 0;
    }
    @Override public int getNumberOfPes() {
        return 0;
    }
    @Override public String getVmm() {
        return "";
    }
    @Override public boolean isWorking() {
        return false;
    }
    @Override public DatacenterCharacteristics setCostPerBw(double cost) {
        return DatacenterCharacteristics.NULL;
    }
    @Override public DatacenterCharacteristics setCostPerMem(double cost) { return DatacenterCharacteristics.NULL; }
    @Override public DatacenterCharacteristics setCostPerStorage(double cost) { return DatacenterCharacteristics.NULL; }
}
