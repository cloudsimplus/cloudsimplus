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
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CustomerEntity;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link Cloudlet}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Cloudlet#NULL
 */
final class CloudletNull implements Cloudlet {
    @Override public void setId(long id) {/**/}
    @Override public long getId() {
        return -1;
    }
    @Override public String getUid() {
        return "";
    }
    @Override public boolean addRequiredFile(String fileName) {
        return false;
    }
    @Override public boolean addRequiredFiles(List<String> fileNames) {
        return false;
    }
    @Override public boolean deleteRequiredFile(String filename) {
        return false;
    }
    @Override public double getArrivalTime() { return -1; }
    @Override public double getActualCpuTime() {
        return 0.0;
    }
    @Override public int getPriority() {
        return 0;
    }
    @Override public long getFileSize() {
        return 0L;
    }
    @Override public long getFinishedLengthSoFar() {
        return 0L;
    }
    @Override public long getLength() {
        return 0L;
    }
    @Override public long getOutputSize() {
        return 0L;
    }
    @Override public long getTotalLength() {
        return 0L;
    }
    @Override public double getExecStartTime() {
        return 0.0;
    }
    @Override public double getFinishTime() {
        return 0.0;
    }
    @Override public int getNetServiceLevel() {
        return 0;
    }
    @Override public long getNumberOfPes() {
        return 0;
    }
    @Override public List<String> getRequiredFiles() {
        return Collections.emptyList();
    }
    @Override public Status getStatus() {
        return Status.FAILED;
    }
    @Override public boolean isReturnedToBroker() { return false; }
    @Override public long getJobId() { return 0; }
    @Override public void setJobId(long jobId) {/**/}
    @Override public UtilizationModel getUtilizationModelBw() {
        return UtilizationModel.NULL;
    }
    @Override public UtilizationModel getUtilizationModelCpu() {
        return UtilizationModel.NULL;
    }
    @Override public UtilizationModel getUtilizationModelRam() {
        return UtilizationModel.NULL;
    }
    @Override public UtilizationModel getUtilizationModel(Class<? extends ResourceManageable> resourceClass) { return UtilizationModel.NULL; }
    @Override public double getUtilizationOfBw() {
        return 0;
    }
    @Override public double getUtilizationOfBw(double time) {
        return 0.0;
    }
    @Override public double getUtilizationOfCpu() {
        return 0;
    }
    @Override public double getUtilizationOfCpu(double time) {
        return 0.0;
    }
    @Override public double getUtilizationOfRam() {
        return 0;
    }
    @Override public double getUtilizationOfRam(double time) {
        return 0.0;
    }
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public double getWaitingTime() {
        return 0.0;
    }
    @Override public boolean isFinished() {
        return false;
    }
    @Override public boolean hasRequiresFiles() {
        return false;
    }
    @Override public Cloudlet setPriority(int priority) { return this; }
    @Override public Cloudlet setLength(long length) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setFileSize(long fileSize) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setOutputSize(long outputSize) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setSizes(long size) { return this; }
    @Override public boolean setStatus(Status newStatus) {
        return false;
    }
    @Override public void setNetServiceLevel(int netServiceLevel) {/**/}
    @Override public Cloudlet setNumberOfPes(long numberOfPes) {
        return Cloudlet.NULL;
    }
    @Override public void setBroker(DatacenterBroker broker) {/**/}
    @Override public DatacenterBroker getBroker() {
        return DatacenterBroker.NULL;
    }
    @Override public Cloudlet setUtilizationModel(UtilizationModel utilizationModel) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setUtilizationModelBw(UtilizationModel utilizationModelBw) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setUtilizationModelCpu(UtilizationModel utilizationModelCpu) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setUtilizationModelRam(UtilizationModel utilizationModelRam) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setVm(Vm vm) {
        return Cloudlet.NULL;
    }
    @Override public boolean removeOnFinishListener(EventListener<CloudletVmEventInfo> listener) { return false; }
    @Override public Cloudlet addOnFinishListener(EventListener<CloudletVmEventInfo> listener) { return Cloudlet.NULL; }
    @Override public void notifyOnUpdateProcessingListeners(double time) {/**/}
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public void setLastTriedDatacenter(Datacenter lastTriedDatacenter) {/**/}
    @Override public Datacenter getLastTriedDatacenter() { return Datacenter.NULL; }
    @Override public double getArrivedTime() { return 0; }
    @Override public CustomerEntity setArrivedTime(double time) { return this; }
    @Override public double getCreationTime() { return 0; }
    @Override public double getWaitTime() { return 0; }
    @Override public boolean removeOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener) { return false; }
    @Override public Cloudlet addOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener) { return Cloudlet.NULL; }
    @Override public double getSubmissionDelay() { return 0; }
    @Override public boolean isDelayed() { return false; }
    @Override public void setSubmissionDelay(double submissionDelay) {/**/}
    @Override public boolean isBoundToVm() { return false; }
    @Override public int compareTo(Cloudlet cloudlet) {
        return 0;
    }
    @Override public String toString() {
        return "Cloudlet.NULL";
    }
    @Override public boolean addFinishedLengthSoFar(long partialFinishedMI) {
        return false;
    }
    @Override public void setExecStartTime(double clockTime) {/**/}
    @Override public Cloudlet addOnStartListener(EventListener<CloudletVmEventInfo> listener) { return this; }
    @Override public boolean removeOnStartListener(EventListener<CloudletVmEventInfo> listener) { return false; }
    @Override public double registerArrivalInDatacenter() {
        return -1;
    }
    @Override public Cloudlet reset() { return this; }
    @Override public Cloudlet setLifeTime(final double lifeTime) { return this; }
    @Override public double getLifeTime() { return -1; }
}
