/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.datacenters;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.core.SimEntityNullBase;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.listeners.DatacenterVmMigrationEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.power.models.PowerModelDatacenter;
import org.cloudsimplus.resources.DatacenterStorage;
import org.cloudsimplus.vms.Vm;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * A class that implements the Null Object Design Pattern for {@link Datacenter} class.
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
    @Override public Datacenter setCharacteristics(DatacenterCharacteristics characteristics) { return this; }
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
    @Override public Datacenter setPowerModel(PowerModelDatacenter powerModel) { return this; }
}
