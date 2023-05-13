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
package org.cloudsimplus.brokers;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.core.SimEntityNullBase;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.listeners.DatacenterBrokerEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.vms.Vm;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A class that implements the Null Object Design Pattern for {@link DatacenterBroker}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see DatacenterBroker#NULL
 */
final class DatacenterBrokerNull implements DatacenterBroker, SimEntityNullBase {
    @Override public int compareTo(SimEntity entity) { return 0; }
    @Override public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm) {
        return false;
    }
    @Override public <T extends Cloudlet> List<T> getCloudletWaitingList() {
        return Collections.emptyList();
    }
    @Override public <T extends Cloudlet> List<T> getCloudletFinishedList() {
        return Collections.emptyList();
    }
    @Override public Vm getWaitingVm(int index) {
        return Vm.NULL;
    }
    @Override public <T extends Vm> List<T> getVmWaitingList() {
        return Collections.emptyList();
    }
    @Override public <T extends Vm> List<T> getVmExecList() {
        return Collections.emptyList();
    }
    @Override public int getVmsNumber() { return 0; }
    @Override public DatacenterBroker requestIdleVmDestruction(Vm vm) { return this; }
    @Override public void requestShutdownWhenIdle() {/**/}
    @Override public <T extends Vm> List<T> getVmCreatedList() { return Collections.emptyList(); }
    @Override public DatacenterBroker setDatacenterMapper(BiFunction<Datacenter, Vm, Datacenter> datacenterMapper) { return this; }
    @Override public DatacenterBroker setVmMapper(Function<Cloudlet, Vm> vmMapper) { return this; }
    @Override public DatacenterBroker setSelectClosestDatacenter(boolean select) { return this; }
    @Override public boolean isSelectClosestDatacenter() { return false; }
    @Override public List<Cloudlet> getCloudletCreatedList() { return Collections.emptyList(); }
    @Override public DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener) { return this; }
    @Override public DatacenterBroker removeOnVmsCreatedListener(EventListener<? extends EventInfo> listener) { return this; }
    @Override public DatacenterBroker setVmDestructionDelayFunction(Function<Vm, Double> function) { return this; }
    @Override public DatacenterBroker setVmDestructionDelay(double delay) { return this; }
    @Override public List<Cloudlet> getCloudletSubmittedList() { return Collections.emptyList(); }
    @Override public <T extends Vm> List<T> getVmFailedList() { return Collections.emptyList(); }
    @Override public VmCreation getVmCreation() { return VmCreation.ofZero(); }
    @Override public DatacenterBroker setLastSelectedDc(Datacenter lastSelectedDc) { return this; }
    @Override public Datacenter getLastSelectedDc() { return Datacenter.NULL; }
    @Override public boolean isBatchVmCreation() { return false; }
    @Override public DatacenterBroker setBatchVmCreation(boolean enable) { return this; }
    @Override public boolean isShutdownWhenIdle() { return false; }
    @Override public DatacenterBroker setShutdownWhenIdle(boolean shutdownWhenIdle) { return this; }
    @Override public DatacenterBroker setVmComparator(Comparator<Vm> comparator) { return this; }
    @Override public DatacenterBroker setCloudletComparator(Comparator<Cloudlet> comparator) { return this; }
    @Override public DatacenterBroker submitCloudlet(Cloudlet cloudlet) { return this; }
    @Override public DatacenterBroker submitCloudletList(List<? extends Cloudlet> list) { return this; }
    @Override public DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, double submissionDelay) { return this; }
    @Override public DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, Vm vm) { return this; }
    @Override public DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay) { return this; }
    @Override public DatacenterBroker submitVm(Vm vm) { return this; }
    @Override public DatacenterBroker submitVmList(List<? extends Vm> list) { return this; }
    @Override public DatacenterBroker submitVmList(List<? extends Vm> list, double submissionDelay) { return this; }
    @Override public double getStartTime() { return -1; }
}
