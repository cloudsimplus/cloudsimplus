package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A class that implements the Null Object Design Pattern for {@link DatacenterBroker}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see DatacenterBroker#NULL
 */
final class DatacenterBrokerNull implements DatacenterBroker {
    @Override public int compareTo(SimEntity o) {
        return 0;
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
    @Override public int getId() {
        return -1;
    }
    @Override public String getName() {
        return "";
    }
    @Override public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm) {
        return false;
    }
    @Override public <T extends Cloudlet> List<T> getCloudletsWaitingList() {
        return Collections.emptyList();
    }
    @Override public <T extends Cloudlet> List<T> getCloudletsFinishedList() {
        return Collections.emptyList();
    }
    @Override public Vm getWaitingVm(int index) {
        return Vm.NULL;
    }
    @Override public <T extends Vm> List<T> getVmsWaitingList() {
        return Collections.emptyList();
    }
    @Override public <T extends Vm> List<T> getVmsCreatedList() {
        return Collections.emptyList();
    }
    @Override public void submitVm(Vm vm) {/**/}
    @Override public void submitCloudlet(Cloudlet cloudlet) {/**/}
    @Override public void submitCloudletList(List<? extends Cloudlet> list) {/**/}
    @Override public void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay) {/**/}
    @Override public void submitVmList(List<? extends Vm> list) {/**/}
    @Override public void submitVmList(List<? extends Vm> list, double submissionDelay) {/**/}
    @Override public boolean hasMoreCloudletsToBeExecuted() {
        return false;
    }
    @Override public void shutdownEntity() {/**/}
    @Override public SimEntity setName(String newName) throws IllegalArgumentException { return this; }
    @Override public void setDatacenterSupplier(Supplier<Datacenter> datacenterSupplier) {/**/}
    @Override public void setFallbackDatacenterSupplier(Supplier<Datacenter> fallbackDatacenterSupplier) {/**/}
    @Override public void setVmMapper(Function<Cloudlet, Vm> vmMapper) {/**/}
    @Override public Set<Cloudlet> getCloudletsCreatedList() { return Collections.EMPTY_SET; }
    @Override public void setVmComparator(Comparator<Vm> comparator) {/**/}
    @Override public void setCloudletComparator(Comparator<Cloudlet> comparator) {/**/}
    @Override public void setLog(boolean log) {}
    @Override public void println(String msg) {}
    @Override public void submitCloudletList(List<? extends Cloudlet> list, Vm vm) {}

    @Override
    public void submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
