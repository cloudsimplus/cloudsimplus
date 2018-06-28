package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.DatacenterStorage;
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
    private static final DatacenterStorage storage = new DatacenterStorage();

    @Override public int getId() {
        return -1;
    }
    @Override public int compareTo(SimEntity o) {
        return 0;
    }
    @Override public String getName() {
        return "";
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
    @Override public <T extends Host> Datacenter addHostList(List<T> hostList) { return this; }
    @Override public Datacenter addHost(Host host) { return this; }
    @Override public double getSchedulingInterval() {
        return 0;
    }
    @Override public Datacenter setSchedulingInterval(double schedulingInterval) {
        return Datacenter.NULL;
    }
    @Override public DatacenterCharacteristics getCharacteristics() {
        return DatacenterCharacteristics.NULL;
    }
    @Override public DatacenterStorage getDatacenterStorage() { return storage; }
    @Override public void setDatacenterStorage(DatacenterStorage datacenterStorage) {/**/}
    @Override public double getBandwidthPercentForMigration() { return 0; }
    @Override public void setBandwidthPercentForMigration(double bandwidthPercentForMigration) {/**/}
    @Override public double getPower() { return 0; }
    @Override public double getPowerInKWattsHour() { return 0; }
    @Override public SimEntity setState(State state) { return SimEntity.NULL; }
    @Override public boolean isStarted() { return false; }
    @Override public boolean isAlive() { return false; }
    @Override public boolean isFinished() { return false; }
    @Override public Simulation getSimulation() { return Simulation.NULL; }
    @Override public SimEntity setSimulation(Simulation simulation) { return this; }
    @Override public void processEvent(SimEvent ev) {/**/}
    @Override public void schedule(SimEntity dest, double delay, int tag) {/**/}
    @Override public void run() {/**/}
    @Override public void start() {/**/}
    @Override public void shutdownEntity() {/**/}
    @Override public SimEntity setName(String newName) throws IllegalArgumentException { return this; }
    @Override public String toString() {
        return "Datacenter.NULL";
    }
    @Override public void setLog(boolean log) {/**/}
}
