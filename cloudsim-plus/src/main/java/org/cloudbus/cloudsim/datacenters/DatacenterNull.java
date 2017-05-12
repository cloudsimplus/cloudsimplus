package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for
 * {@link Datacenter} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Datacenter#NULL
 */
final class DatacenterNull implements Datacenter {
    @Override public int getId() {
        return -1;
    }
    @Override public int compareTo(SimEntity o) {
        return 0;
    }
    @Override public String getName() {
        return "";
    }
    @Override public int addFile(File file) {
        return 0;
    }
    @Override public List<Host> getHostList() {
        return Collections.emptyList();
    }
    @Override public VmAllocationPolicy getVmAllocationPolicy() {
        return VmAllocationPolicy.NULL;
    }
    @Override public List<Vm> getVmList() {
        return Collections.emptyList();
    }
    @Override public Host getHost(final int index) {
        return Host.NULL;
    }
    @Override public double getSchedulingInterval() {
        return 0;
    }
    @Override public Datacenter setSchedulingInterval(double schedulingInterval) {
        return Datacenter.NULL;
    }
    @Override public DatacenterCharacteristics getCharacteristics() {
        return DatacenterCharacteristics.NULL;
    }
    @Override public List<FileStorage> getStorageList() {
        return Collections.emptyList();
    }
    @Override public Datacenter setStorageList(List<FileStorage> storageList) {
        return Datacenter.NULL;
    }
    @Override public boolean isStarted() {
        return false;
    }
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public SimEntity setSimulation(Simulation simulation) {
        return this;
    }
    @Override public void processEvent(SimEvent ev) {/**/}
    @Override public void schedule(int dest, double delay, int tag) {/**/}
    @Override public void run() {/**/}
    @Override public void start() {/**/}
    @Override public void shutdownEntity() {/**/}
    @Override public SimEntity setName(String newName) throws IllegalArgumentException { return this; }
    @Override public String toString() {
        return "Datacenter.NULL";
    }

    @Override public void setLog(boolean log) {}
    @Override public void println(String msg) {}
}
