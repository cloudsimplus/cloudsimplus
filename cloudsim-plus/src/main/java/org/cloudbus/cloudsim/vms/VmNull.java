package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Processor;
import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link Vm}
 * objects.
 *
 * @author Manoel Campos da Silva Filho
 * @see Vm#NULL
 */
final class VmNull implements Vm {
    @Override public void setId(int id) {/**/}
    @Override public int getId() {
        return -1;
    }
    @Override public double getSubmissionDelay() {
        return 0;
    }
    @Override public void setSubmissionDelay(double submissionDelay) {/**/}
    @Override public void addStateHistoryEntry(VmStateHistoryEntry entry) {/**/}
    @Override public Resource getBw() {
        return Resource.NULL;
    }
    @Override public CloudletScheduler getCloudletScheduler() { return CloudletScheduler.NULL; }
    @Override public long getCurrentAllocatedBw() {
        return 0;
    }
    @Override public long getCurrentAllocatedRam() {
        return 0;
    }
    @Override public long getCurrentAllocatedSize() {
        return 0;
    }
    @Override public long getCurrentRequestedBw() {
        return 0;
    }
    @Override public double getCurrentRequestedMaxMips() {
        return 0.0;
    }
    @Override public List<Double> getCurrentRequestedMips() {
        return Collections.emptyList();
    }
    @Override public long getCurrentRequestedRam() {
        return 0;
    }
    @Override public double getCurrentRequestedTotalMips() {
        return 0.0;
    }
    @Override public Host getHost() {
        return Host.NULL;
    }
    @Override public double getMips() {
        return 0;
    }
    @Override public long getNumberOfPes() {
        return 0;
    }
    @Override public Vm addOnHostAllocationListener(EventListener<VmHostEventInfo> listener) {
        return this;
    }
    @Override public Vm addOnHostDeallocationListener(EventListener<VmHostEventInfo> listener) {
        return this;
    }
    @Override public Vm addOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener) {
        return this;
    }
    @Override public Vm addOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener) {
        return this;
    }
    @Override public void notifyOnHostAllocationListeners() {/**/}
    @Override public void notifyOnHostDeallocationListeners(Host deallocatedHost) {/**/}
    @Override public void notifyOnCreationFailureListeners(Datacenter failedDatacenter) {/**/}
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
    @Override public double getCpuPercentUse(double time) {
        return 0.0;
    }
    @Override public double getCurrentCpuPercentUse() {
        return 0;
    }
    @Override public double getTotalUtilizationOfCpuMips(double time) {
        return 0.0;
    }
    @Override public String getUid() {
        return "";
    }
    @Override public DatacenterBroker getBroker() {
        return DatacenterBroker.NULL;
    }
    @Override public Vm setBroker(DatacenterBroker broker) {
        return this;
    }
    @Override public String getVmm() {
        return "";
    }
    @Override public boolean isCreated() {
        return false;
    }
    @Override public boolean isInMigration() {
        return false;
    }
    @Override public void setCreated(boolean created) {/**/}
    @Override public Vm setBw(long bwCapacity) {
        return this;
    }
    @Override public void setHost(Host host) {/**/}
    @Override public void setInMigration(boolean inMigration) {/**/}
    @Override public Vm setRam(long ramCapacity) {
        return this;
    }
    @Override public Vm setSize(long size) {
        return this;
    }
    @Override public double updateProcessing(double currentTime, List<Double> mipsShare) {
        return 0.0;
    }
    @Override public Vm setCloudletScheduler(CloudletScheduler cloudletScheduler) {
        return this;
    }
    @Override public int compareTo(Vm o) {
        return 0;
    }
    @Override public double getTotalMipsCapacity() {
        return 0.0;
    }
    @Override public void allocateResource(Class<? extends ResourceManageable> c, long amount) {/**/}
    @Override public void deallocateResource(Class<? extends ResourceManageable> c) {/**/}
    @Override public void setFailed(boolean failed) {/**/}
    @Override public boolean isFailed() {
        return false;
    }
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public List<ResourceManageable> getResources() {
        return Collections.emptyList();
    }
    @Override public String toString() {
        return "Vm.NULL";
    }
    @Override public HorizontalVmScaling getHorizontalScaling() {
        return HorizontalVmScaling.NULL;
    }
    @Override public Vm setHorizontalScaling(HorizontalVmScaling h) throws IllegalArgumentException { return this; }
    @Override public Vm setRamVerticalScaling(VerticalVmScaling v) throws IllegalArgumentException { return this; }
    @Override public Vm setBwVerticalScaling(VerticalVmScaling v) throws IllegalArgumentException { return this; }
    @Override public Vm setPeVerticalScaling(VerticalVmScaling peVerticalScaling) throws IllegalArgumentException { return this; }
    @Override public VerticalVmScaling getRamVerticalScaling() {
        return VerticalVmScaling.NULL;
    }
    @Override public VerticalVmScaling getBwVerticalScaling() {
        return VerticalVmScaling.NULL;
    }
    @Override public VerticalVmScaling getPeVerticalScaling() { return VerticalVmScaling.NULL; }
    @Override public Processor getProcessor() { return Processor.NULL; }

    @Override public String getDescription() { return ""; }
    @Override public Vm setDescription(String description) { return this; }
}
