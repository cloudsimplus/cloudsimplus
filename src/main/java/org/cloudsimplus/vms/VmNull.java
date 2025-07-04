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
package org.cloudsimplus.vms;

import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.*;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.resources.Processor;
import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.utilizationmodels.BootModel;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link Vm} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @see Vm#NULL
 */
final class VmNull implements Vm {
    @Override public Vm setId(long id) { return this; }
    @Override public long getId() {
        return -1;
    }
    @Override public double getSubmissionDelay() {
        return 0;
    }
    @Override public boolean isSubmissionDelayed() { return false; }
    @Override public void setSubmissionDelay(double submissionDelay) {/**/}
    @Override public void addStateHistoryEntry(VmStateHistoryEntry entry) {/**/}
    @Override public Resource getBw() {
        return Resource.NULL;
    }
    @Override public CloudletScheduler getCloudletScheduler() { return CloudletScheduler.NULL; }
    @Override public long getFreePesNumber() { return 0; }
    @Override public long getExpectedFreePesNumber() { return 0; }
    @Override public long getCurrentRequestedBw() {
        return 0;
    }
    @Override public MipsShare getCurrentRequestedMips() {
        return MipsShare.NULL;
    }
    @Override public long getCurrentRequestedRam() {
        return 0;
    }
    @Override public double getTotalCpuMipsRequested() {
        return 0.0;
    }
    @Override public Host getHost() {
        return Host.NULL;
    }
    @Override public double getMips() {
        return 0;
    }
    @Override public long getPesNumber() {
        return 0;
    }
    @Override public Vm addOnHostAllocationListener(EventListener<VmHostEventInfo> listener) {
        return this;
    }
    @Override public Vm addOnMigrationStartListener(EventListener<VmHostEventInfo> listener) { return this; }
    @Override public Vm addOnMigrationFinishListener(EventListener<VmHostEventInfo> listener) { return this; }
    @Override public Vm addOnHostDeallocationListener(EventListener<VmHostEventInfo> listener) { return this; }
    @Override public Vm addOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener) {
        return this;
    }
    @Override public Vm addOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener) {
        return this;
    }
    @Override public void notifyOnHostAllocationListeners() {/**/}
    @Override public void notifyOnHostDeallocationListeners(Host deallocatedHost) {/**/}
    @Override public void notifyOnCreationFailureListeners(Datacenter failedDatacenter) {/**/}
    @Override public boolean removeOnMigrationStartListener(EventListener<VmHostEventInfo> listener) { return false; }
    @Override public boolean removeOnMigrationFinishListener(EventListener<VmHostEventInfo> listener) { return false; }
    @Override public boolean removeOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener) {
        return false;
    }
    @Override public boolean removeOnHostAllocationListener(EventListener<VmHostEventInfo> listener) {
        return false;
    }
    @Override public boolean removeOnHostDeallocationListener(EventListener<VmHostEventInfo> listener) {
        return false;
    }
    @Override public boolean removeOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener) { return false; }
    @Override public Resource getRam() {
        return Resource.NULL;
    }
    @Override public Resource getStorage() {
        return Resource.NULL;
    }
    @Override public List<VmStateHistoryEntry> getStateHistory() {
        return Collections.emptyList();
    }
    @Override public double getCpuPercentUtilization() { return 0; }
    @Override public double getCpuPercentUtilization(double time) {
        return 0.0;
    }
    @Override public double getCpuPercentRequested() { return 0; }
    @Override public double getCpuPercentRequested(double time) { return 0; }
    @Override public double getHostCpuUtilization(double time) { return 0; }
    @Override public double getExpectedHostCpuUtilization(double vmCpuUtilizationPercent) { return 0; }
    @Override public double getHostRamUtilization() { return 0; }
    @Override public double getHostBwUtilization() { return 0; }
    @Override public double getTotalCpuMipsUtilization() { return 0; }
    @Override public double getTotalCpuMipsUtilization(double time) {
        return 0.0;
    }
    @Override public String getUid() {
        return "";
    }
    @Override public DatacenterBroker getBroker() {
        return DatacenterBroker.NULL;
    }
    @Override public Vm setBroker(DatacenterBroker broker) { return this; }
    @Override public double getStartTime() { return 0; }
    @Override public boolean isFinished() { return true; }
    @Override public Vm setStartTime(double startTime) { return this; }
    @Override public double getFinishTime() { return 0; }
    @Override public double getCreationWaitTime() { return 0; }
    @Override public double getTotalExecutionTime() { return 0; }
    @Override public Vm setFinishTime(double stopTime) { return this; }
    @Override public double getLastBusyTime() { return 0; }
    @Override public Startable setLastBusyTime(double time) { return this; }
    @Override public double getIdleInterval() { return 0; }
    @Override public boolean isIdle() { return false; }
    @Override public boolean isIdleEnough(double time) { return false; }
    @Override public VmResourceStats getCpuUtilizationStats() { return new VmResourceStats(NULL, vm -> 0.0); }
    @Override public void enableUtilizationStats() {/**/}
    @Override public String getVmm() {
        return "";
    }
    @Override public boolean isCreated() {
        return false;
    }
    @Override public boolean isSuitableForCloudlet(Cloudlet cloudlet) { return false; }
    @Override public boolean isInMigration() {
        return false;
    }
    @Override public Vm setBw(long bwCapacity) {
        return this;
    }
    @Override public Vm setInMigration(boolean migrating) {return this;}
    @Override public Vm setRam(long ramCapacity) {
        return this;
    }
    @Override public Vm setSize(long size) {
        return this;
    }
    @Override public double updateProcessing(double currentTime, MipsShare mipsShare) { return 0.0; }
    @Override public double updateProcessing(MipsShare mipsShare) { return 0; }
    @Override public Vm setCloudletScheduler(CloudletScheduler cloudletScheduler) {
        return this;
    }
    @Override public int compareTo(Vm vm) { return 0; }
    @Override public double getTotalMipsCapacity() {
        return 0.0;
    }
    @Override public void setFailed(boolean failed) {/**/}
    @Override public boolean isFailed() {
        return true;
    }
    @Override public boolean isWorking() { return false; }
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public Vm setLastTriedDatacenter(Datacenter lastTriedDatacenter) { return this; }
    @Override public Datacenter getLastTriedDatacenter() { return Datacenter.NULL; }
    @Override public double getBrokerArrivalTime() { return 0; }
    @Override public CustomerEntity setBrokerArrivalTime(double time) { return this; }
    @Override public double getCreationTime() { return 0; }
    @Override public String toString() { return "Vm.NULL"; }
    @Override public HorizontalVmScaling getHorizontalScaling() { return HorizontalVmScaling.NULL; }
    @Override public Vm setHorizontalScaling(HorizontalVmScaling scaling) throws IllegalArgumentException { return this; }
    @Override public Vm setRamVerticalScaling(VerticalVmScaling scaling) throws IllegalArgumentException { return this; }
    @Override public Vm setBwVerticalScaling(VerticalVmScaling scaling) throws IllegalArgumentException { return this; }
    @Override public Vm setPeVerticalScaling(VerticalVmScaling scaling) throws IllegalArgumentException { return this; }
    @Override public VerticalVmScaling getRamVerticalScaling() { return VerticalVmScaling.NULL; }
    @Override public VerticalVmScaling getBwVerticalScaling() { return VerticalVmScaling.NULL; }
    @Override public VerticalVmScaling getPeVerticalScaling() { return VerticalVmScaling.NULL; }
    @Override public Processor getProcessor() { return Processor.NULL; }
    @Override public String getDescription() { return ""; }
    @Override public Vm setDescription(String description) { return this; }
    @Override public VmGroup getGroup() { return null; }
    @Override public double getTimeZone() { return Integer.MAX_VALUE; }
    @Override public Vm setTimeZone(double timeZone) { return this; }
    @Override public List<ResourceManageable> getResources() { return Collections.emptyList(); }
    @Override public double getLifeTime() { return 0; }
    @Override public Lifetimed setLifeTime(double lifeTime) { return this; }
    @Override public boolean isLifeTimeReached() { return false; }
    @Override public BootModel getBootModel() { return BootModel.NULL; }
    @Override public Vm setBootModel(BootModel model) { return this; }
    @Override public double getStartupDelay() { return 0; }
    @Override public boolean isShuttingDown() { return false; }
    @Override  public double getShutdownBeginTime() { return -1; }
    @Override public ExecDelayable setShutdownBeginTime(double shutdownBeginTime) { return this; }
    @Override public ExecDelayable setStartupDelay(double delay) { return this; }
    @Override public void shutdown() {/**/}
    @Override public double getShutDownDelay() { return 0; }
    @Override public ExecDelayable setShutDownDelay(double delay) { return this; }
}
