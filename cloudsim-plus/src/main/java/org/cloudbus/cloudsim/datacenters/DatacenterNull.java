package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEntityNullBase;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.models.PowerModelDatacenter;
import org.cloudbus.cloudsim.resources.DatacenterStorage;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.DatacenterVmMigrationEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * A class that implements the Null Object Design Pattern for
 * {@link Datacenter} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Datacenter#NULL
 */
final class DatacenterNull implements Datacenter, SimEntityNullBase {
    private static final DatacenterStorage STORAGE = new DatacenterStorage();

    @Override public int compareTo(SimEntity entity) { return 0; }
    @Override public List<Host> getHostList() {
        return Collections.emptyList();
    }
    @Override public VmAllocationPolicy getVmAllocationPolicy() {
        return VmAllocationPolicy.NULL;
    }
    @Override public void requestVmMigration(Vm sourceVm, Host targetHost) {/**/}
    @Override public void requestVmMigration(Vm sourceVm) {/**/}
    @Override public Stream<? extends Host> getActiveHostStream() { return Stream.empty(); }
    @Override public Host getHost(final int index) { return Host.NULL; }
    @Override public long getActiveHostsNumber() { return 0; }
    @Override public long size() { return 0; }
    @Override public Host getHostById(long id) { return Host.NULL; }
    @Override public <T extends Host> Datacenter addHostList(List<T> hostList) { return this; }
    @Override public <T extends Host> Datacenter removeHost(T host) { return this; }
    @Override public Datacenter addHost(Host host) { return this; }
    @Override public double getSchedulingInterval() { return 0; }
    @Override public Datacenter setSchedulingInterval(double schedulingInterval) { return this; }
    @Override public DatacenterCharacteristics getCharacteristics() { return DatacenterCharacteristics.NULL; }
    @Override public DatacenterStorage getDatacenterStorage() { return STORAGE; }
    @Override public void setDatacenterStorage(DatacenterStorage datacenterStorage) {/**/}
    @Override public double getBandwidthPercentForMigration() { return 0; }
    @Override public void setBandwidthPercentForMigration(double bandwidthPercentForMigration) {/**/}
    @Override public Datacenter addOnHostAvailableListener(EventListener<HostEventInfo> listener) { return this; }
    @Override public Datacenter addOnVmMigrationFinishListener(EventListener<DatacenterVmMigrationEventInfo> listener) { return this; }
    @Override public boolean isMigrationsEnabled() { return false; }
    @Override public Datacenter enableMigrations() { return this; }
    @Override public Datacenter disableMigrations() { return this; }
    @Override public double getHostSearchRetryDelay() { return 0; }
    @Override public Datacenter setHostSearchRetryDelay(double delay) { return this; }
    @Override public String toString() { return "Datacenter.NULL"; }
    @Override public double getTimeZone() { return Integer.MAX_VALUE; }
    @Override public TimeZoned setTimeZone(double timeZone) { return this; }
    @Override public PowerModelDatacenter getPowerModel() { return PowerModelDatacenter.NULL; }
    @Override public void setPowerModel(PowerModelDatacenter powerModel) {/**/}
}
