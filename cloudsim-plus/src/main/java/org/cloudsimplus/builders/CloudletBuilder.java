/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * A Builder class to create {@link Cloudlet} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CloudletBuilder implements Builder {
    private long length = 10000;
    private long outputSize = 300;
    private long fileSize = 300;
    private int  pes = 1;
    /**
     * The VM to be bind to created cloudlets.
     */
    private Vm vm = Vm.NULL;
    private UtilizationModel utilizationModelRam;
    private UtilizationModel utilizationModelCpu;
    private UtilizationModel utilizationModelBw;

    private final List<Cloudlet> cloudlets;
    private int createdCloudlets;

    private final BrokerBuilderDecorator brokerBuilder;
    private final DatacenterBrokerSimple broker;
    private BiFunction<Long,Integer, Cloudlet> cloudletCreationFunction;

    private EventListener<CloudletVmEventInfo> onCloudletFinishEventListener = EventListener.NULL;
	private List<String> requiredFiles;

	public CloudletBuilder(final BrokerBuilderDecorator brokerBuilder, final DatacenterBrokerSimple broker) {
	    super();
        this.brokerBuilder = Objects.requireNonNull(brokerBuilder);
        this.broker = Objects.requireNonNull(broker);
        setUtilizationModelCpuRamAndBw(new UtilizationModelFull());
        this.cloudlets = new ArrayList<>();
		this.requiredFiles = new ArrayList<>();
        this.createdCloudlets = 0;
        this.cloudletCreationFunction = this::defaultCloudletCreationFunction;
    }

    public CloudletBuilder create(final int amount, final int initialId) {
        createCloudletsInternal(amount, initialId);
        return this;

    }

    public CloudletBuilder create(final int amount) {
        createCloudletsInternal(amount, 0);
        return this;
    }

    public CloudletBuilder createAndSubmit(final int amount) {
        return createAndSubmit(amount, 0);
    }

    public CloudletBuilder createAndSubmit(final int amount, final int initialId) {
        final List<Cloudlet> localList = createCloudletsInternal(amount, initialId);
        broker.submitCloudletList(localList);
        if(vm != Vm.NULL){
            localList.forEach(cloudlet -> broker.bindCloudletToVm(cloudlet, vm));
        }
        return this;
    }

    private List<Cloudlet> createCloudletsInternal(final int amount, final int initialId) {
        final List<Cloudlet> localList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            final int cloudletId = initialId + createdCloudlets++;
            final Cloudlet cloudlet = cloudletCreationFunction.apply(length, pes)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModelCpu(utilizationModelCpu)
                    .setUtilizationModelRam(utilizationModelRam)
                    .setUtilizationModelBw(utilizationModelBw)
                    .addOnFinishListener(onCloudletFinishEventListener);
            cloudlet.setId(cloudletId);
            cloudlet.setBroker(broker);
            cloudlet.addRequiredFiles(requiredFiles);
            localList.add(cloudlet);
        }
        cloudlets.addAll(localList);
        return localList;
    }

    private Cloudlet defaultCloudletCreationFunction(final long length, final int pes){
	    return new CloudletSimple(length, pes);
    }

    /**
     * Submits the list of created cloudlets to the latest created broker.
     * @return the CloudletBuilder instance
     */
    public CloudletBuilder submitCloudlets(){
        broker.submitCloudletList(cloudlets);
        return this;
    }

    /**
     * Sets the same utilization model for CPU, RAM and BW.
     * By this way, at a time t, every one of the 3 resources will use the same percentage
     * of its capacity.
     *
     * @param utilizationModel the utilization model to set
     * @return
     */
    public final CloudletBuilder setUtilizationModelCpuRamAndBw(final UtilizationModel utilizationModel) {
        Objects.requireNonNull(utilizationModel);
        this.utilizationModelCpu = utilizationModel;
        this.utilizationModelRam = utilizationModel;
        this.utilizationModelBw = utilizationModel;
        return this;
    }

    public CloudletBuilder setVm(final Vm defaultVm) {
        this.vm = defaultVm;
        return this;
    }

    public CloudletBuilder setFileSize(final long defaultFileSize) {
        this.fileSize = defaultFileSize;
        return this;
    }

    public CloudletBuilder setRequiredFiles(final List<String> requiredFiles){
	    this.requiredFiles = requiredFiles;
	    return this;
    }

    public List<Cloudlet> getCloudlets() {
        return cloudlets;
    }

    public CloudletBuilder setPEs(final int defaultPEs) {
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
    public BrokerBuilderDecorator getBrokerBuilder() {
        return brokerBuilder;
    }

    public CloudletBuilder setOnCloudletFinishEventListener(final EventListener<CloudletVmEventInfo> defaultOnCloudletFinishEventListener) {
        this.onCloudletFinishEventListener = defaultOnCloudletFinishEventListener;
        return this;
    }

    public CloudletBuilder setUtilizationModelRam(final UtilizationModel utilizationModelRam) {
        this.utilizationModelRam = Objects.requireNonNull(utilizationModelRam);
        return this;
    }

    public CloudletBuilder setUtilizationModelCpu(final UtilizationModel utilizationModelCpu) {
        this.utilizationModelCpu = Objects.requireNonNull(utilizationModelCpu);
        return this;
    }

    public CloudletBuilder setUtilizationModelBw(final UtilizationModel utilizationModelBw) {
        this.utilizationModelBw = Objects.requireNonNull(utilizationModelBw);
        return this;
    }

    /**
     * Sets a {@link BiFunction} used to create Cloudlets.
     * It must length of the Cloudlet (in MI) and the number of PEs it will require.
     * @param cloudletCreationFunction
     */
    public void setCloudletCreationFunction(final BiFunction<Long, Integer, Cloudlet> cloudletCreationFunction) {
        this.cloudletCreationFunction = Objects.requireNonNull(cloudletCreationFunction);
    }
}
