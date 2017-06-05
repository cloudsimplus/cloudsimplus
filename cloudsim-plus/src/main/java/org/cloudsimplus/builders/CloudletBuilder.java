/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

/**
 * A Builder class to create {@link Cloudlet} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CloudletBuilder extends Builder {
    private long length = 10000;
    private long outputSize = 300;
    private long fileSize = 300;
    private int  pes = 1;
    /**
     * The id of the VM to be bind to created cloudlets.
     */
    private Vm vm = Vm.NULL;
    private UtilizationModel utilizationModelRam;
    private UtilizationModel utilizationModelCpu;
    private UtilizationModel utilizationModelBw;

    private final List<Cloudlet> cloudlets;
    private int numberOfCreatedCloudlets;

    private final BrokerBuilderDecorator brokerBuilder;
    private final DatacenterBrokerSimple broker;

    private EventListener<CloudletVmEventInfo> onCloudletFinishEventListener = EventListener.NULL;
	private List<String> requiredFiles;

	public CloudletBuilder(final BrokerBuilderDecorator brokerBuilder, final DatacenterBrokerSimple broker) {
        if(Objects.isNull(brokerBuilder)) {
            throw new RuntimeException("The brokerBuilder parameter cannot be null.");
        }
        if(Objects.isNull(broker)) {
            throw new RuntimeException("The broker parameter cannot be null.");
        }

        this.brokerBuilder = brokerBuilder;
        setUtilizationModelCpuRamAndBw(new UtilizationModelFull());
        this.broker = broker;
        this.cloudlets = new ArrayList<>();
		this.requiredFiles = new ArrayList<>();
        this.numberOfCreatedCloudlets = 0;
    }

    /**
     * Sets the same utilization model for CPU, RAM and BW.
     * By this way, at a time t, every one of the 3 resources will use the same percentage
     * of its capacity.
     *
     * @param utilizationModel the utilization model to set
     * @return
     */
    public final CloudletBuilder setUtilizationModelCpuRamAndBw(UtilizationModel utilizationModel) {
        if(!Objects.isNull(utilizationModel)){
            this.utilizationModelCpu = utilizationModel;
            this.utilizationModelRam = utilizationModel;
            this.utilizationModelBw = utilizationModel;
        }
        return this;
    }

    public CloudletBuilder setVm(Vm defaultVm) {
        this.vm = defaultVm;
        return this;
    }

    public CloudletBuilder setFileSize(long defaultFileSize) {
        this.fileSize = defaultFileSize;
        return this;
    }

    public CloudletBuilder setRequiredFiles(List<String> requiredFiles){
	    this.requiredFiles = requiredFiles;
	    return this;
    }

    public List<Cloudlet> getCloudlets() {
        return cloudlets;
    }

    public CloudletBuilder setPEs(int defaultPEs) {
        this.pes = defaultPEs;
        return this;
    }

    public long getLength() {
        return length;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getOutputSize() {
        return outputSize;
    }

    public Cloudlet getCloudletById(final int id) {
        return broker.getCloudletWaitingList().stream()
            .filter(cloudlet -> cloudlet.getId() == id)
            .findFirst()
            .orElse(Cloudlet.NULL);
    }

    public CloudletBuilder setOutputSize(long defaultOutputSize) {
        this.outputSize = defaultOutputSize;
        return this;
    }

    public int getPes() {
        return pes;
    }

    public CloudletBuilder setLength(long defaultLength) {
        this.length = defaultLength;
        return this;
    }

    public CloudletBuilder createAndSubmitOneCloudlet() {
        return createAndSubmitCloudlets(1);
    }

    public CloudletBuilder createCloudlets(final int amount) {
        createCloudletsInternal(amount);
        return this;
    }

    private List<Cloudlet> createCloudletsInternal(final int amount) {
        List<Cloudlet> localList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            final int cloudletId = numberOfCreatedCloudlets++;
            Cloudlet cloudlet =
                    new CloudletSimple(cloudletId, length, pes)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModelCpu(utilizationModelCpu)
                    .setUtilizationModelRam(utilizationModelRam)
                    .setUtilizationModelBw(utilizationModelBw)
                    .setBroker(broker)
                    .addOnFinishListener(onCloudletFinishEventListener);
            cloudlet.addRequiredFiles(requiredFiles);
            localList.add(cloudlet);
        }
        cloudlets.addAll(localList);
        return localList;
    }

    public CloudletBuilder createAndSubmitCloudlets(final int amount) {
        List<Cloudlet> localList = createCloudletsInternal(amount);
        broker.submitCloudletList(localList);
        if(vm != Vm.NULL){
            localList.forEach(c -> broker.bindCloudletToVm(c, vm));
        }
        return this;
    }

    /**
     * Submits the list of created cloudlets to the latest created broker.
     * @return the CloudletBuilder instance
     */
    public CloudletBuilder submitCloudlets(){
        broker.submitCloudletList(cloudlets);
        return this;
    }

    public BrokerBuilderDecorator getBrokerBuilder() {
        return brokerBuilder;
    }

    public EventListener<CloudletVmEventInfo> getOnCloudletFinishEventListener() {
        return onCloudletFinishEventListener;
    }

    public CloudletBuilder setOnCloudletFinishEventListener(EventListener<CloudletVmEventInfo> defaultOnCloudletFinishEventListener) {
        this.onCloudletFinishEventListener = defaultOnCloudletFinishEventListener;
        return this;
    }

    public UtilizationModel getUtilizationModelRam() {
        return utilizationModelRam;
    }

    public CloudletBuilder setUtilizationModelRam(UtilizationModel utilizationModelRam) {
        if(!Objects.isNull(utilizationModelRam)){
            this.utilizationModelRam = utilizationModelRam;
        }

        return this;
    }

    public UtilizationModel getUtilizationModelCpu() {
        return utilizationModelCpu;
    }

    public CloudletBuilder setUtilizationModelCpu(UtilizationModel utilizationModelCpu) {
        if(!Objects.isNull(utilizationModelCpu)){
            this.utilizationModelCpu = utilizationModelCpu;
        }

        return this;
    }

    public UtilizationModel getUtilizationModelBw() {
        return utilizationModelBw;
    }

    public CloudletBuilder setUtilizationModelBw(UtilizationModel utilizationModelBw) {
        if(!Objects.isNull(utilizationModelBw)){
            this.utilizationModelBw = utilizationModelBw;
        }
        return this;
    }
}
