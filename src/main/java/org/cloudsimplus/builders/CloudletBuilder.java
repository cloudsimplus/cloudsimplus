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
package org.cloudsimplus.builders;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;

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
@Accessors(chain = true)
public class CloudletBuilder implements Builder {
    @Getter @Setter private long length = 10000;
    @Getter @Setter private long outputSize = 300;
    @Getter @Setter private long fileSize = 300;
    @Getter @Setter private int  pes = 1;
    @Setter private double lifeTime = Double.MAX_VALUE;

    /**
     * The VM to be bind to created cloudlets.
     */
    @Setter private Vm vm = Vm.NULL;
    @Setter @NonNull private UtilizationModel utilizationModelRam;
    @Setter @NonNull private UtilizationModel utilizationModelCpu;
    @Setter @NonNull private UtilizationModel utilizationModelBw;

    @Getter
    private final List<Cloudlet> cloudlets;
    private int createdCloudlets;

    @Getter
    private final BrokerBuilderDecorator brokerBuilder;
    private final DatacenterBrokerSimple broker;

    /**
     * A {@link BiFunction} used to create Cloudlets.
     * It must receive the length of the Cloudlet (in MI) and the number of PEs it will require.
     */
    @Setter private BiFunction<Long,Integer, Cloudlet> cloudletCreationFunction;

    @Setter private EventListener<CloudletVmEventInfo> onCloudletFinishListener = EventListener.NULL;
    @Setter @NonNull private List<String> requiredFiles;

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
        if(vm != Vm.NULL){
            localList.forEach(cloudlet -> broker.bindCloudletToVm(cloudlet, vm));
        }
        broker.submitCloudletList(localList);
        return this;
    }

    private List<Cloudlet> createCloudletsInternal(final int amount, final int initialId) {
        final List<Cloudlet> localList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            final int cloudletId = initialId + createdCloudlets++;
            final Cloudlet cloudlet = cloudletCreationFunction.apply(length, pes)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize);
            cloudlet.setUtilizationModelCpu(utilizationModelCpu);
            cloudlet.setUtilizationModelRam(utilizationModelRam);
            cloudlet.setUtilizationModelBw(utilizationModelBw);
            cloudlet.addOnFinishListener(onCloudletFinishListener);
            cloudlet.setId(cloudletId);
            cloudlet.setBroker(broker);
            cloudlet.addRequiredFiles(requiredFiles);
            cloudlet.setLifeTime(lifeTime);
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
     * @return this builder
     */
    public final CloudletBuilder setUtilizationModelCpuRamAndBw(final UtilizationModel utilizationModel) {
        Objects.requireNonNull(utilizationModel);
        this.utilizationModelCpu = utilizationModel;
        this.utilizationModelRam = utilizationModel;
        this.utilizationModelBw = utilizationModel;
        return this;
    }
}
