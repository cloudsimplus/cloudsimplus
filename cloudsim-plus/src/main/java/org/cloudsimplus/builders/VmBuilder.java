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
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A Builder class to create {@link Vm} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class VmBuilder implements Builder {
    private Supplier<CloudletScheduler> cloudletSchedulerSupplier;
    private long size = 10000;
    private long  ram = 512;
    private double mips = 1000;
    private long bandwidth = 1000;
    private int  pes = 1;
    private int numberOfCreatedVms;
    private final DatacenterBrokerSimple broker;
    private EventListener<VmHostEventInfo> onHostAllocationListener;
    private EventListener<VmHostEventInfo> onHostDeallocationListener;
    private EventListener<VmDatacenterEventInfo> onVmCreationFailureListener;
    private EventListener<VmHostEventInfo> onUpdateVmProcessingListener;

    public VmBuilder(final DatacenterBrokerSimple broker) {
        this.broker = Objects.requireNonNull(broker);
        this.numberOfCreatedVms = 0;
        this.onHostAllocationListener = EventListener.NULL;
        this.onHostDeallocationListener = EventListener.NULL;
        this.onVmCreationFailureListener = EventListener.NULL;
        this.onUpdateVmProcessingListener = EventListener.NULL;
        this.cloudletSchedulerSupplier = () -> CloudletScheduler.NULL;
    }

    public VmBuilder setOnHostDeallocationListener(final EventListener<VmHostEventInfo> onHostDeallocationListener) {
        this.onHostDeallocationListener = onHostDeallocationListener;
        return this;
    }

    public VmBuilder setMips(double defaultMIPS) {
        this.mips = defaultMIPS;
        return this;
    }

    public VmBuilder setBandwidth(long defaultBW) {
        this.bandwidth = defaultBW;
        return this;
    }

    public VmBuilder setRam(int defaultRAM) {
        this.ram = defaultRAM;
        return this;
    }

    public VmBuilder setOnHostAllocationListener(final EventListener<VmHostEventInfo> onHostAllocationListener) {
        this.onHostAllocationListener  = onHostAllocationListener;
        return this;
    }

    public VmBuilder setSize(long defaultSize) {
        this.size = defaultSize;
        return this;
    }

    public VmBuilder setOnVmCreationFilatureListenerForAllVms(final EventListener<VmDatacenterEventInfo> onVmCreationFailureListener) {
        this.onVmCreationFailureListener = onVmCreationFailureListener;
        return this;
    }

    public VmBuilder createAndSubmitOneVm() {
        return createAndSubmitVms(1);
    }

    public VmBuilder createAndSubmitVms(final int amount) {
        final List<Vm> vms = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            final Vm vm = new VmSimple(numberOfCreatedVms++, mips, pes)
                    .setRam(ram).setBw(bandwidth).setSize(size)
                    .setCloudletScheduler(cloudletSchedulerSupplier.get())
                    .addOnHostAllocationListener(onHostAllocationListener)
                    .addOnHostDeallocationListener(onHostDeallocationListener)
                    .addOnCreationFailureListener(onVmCreationFailureListener)
                    .addOnUpdateProcessingListener(onUpdateVmProcessingListener);
            vm.setBroker(broker);
            vms.add(vm);
        }
        broker.submitVmList(vms);
        return this;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public VmBuilder setPes(int defaultPEs) {
        this.pes = defaultPEs;
        return this;
    }

    public Vm getVmById(final int id) {
        return broker.getVmWaitingList().stream()
            .filter(vm -> vm.getId() == id)
            .findFirst().orElse(Vm.NULL);
    }

    public long getSize() {
        return size;
    }

    public List<Vm> getVms() {
        return broker.getVmWaitingList();
    }

    public long getRam() {
        return ram;
    }

    public double getMips() {
        return mips;
    }

    public int getPes() {
        return pes;
    }

    /**
     * Sets a {@link Supplier} that is accountable to create CloudletScheduler
     * for requested VMs.
     *
     * @param cloudletSchedulerSupplier the CloudletScheduler Supplier to set
     * @return
     */
    public VmBuilder setCloudletSchedulerSupplier(Supplier<CloudletScheduler> cloudletSchedulerSupplier) {
        this.cloudletSchedulerSupplier = cloudletSchedulerSupplier;
        return this;
    }

    public EventListener<VmHostEventInfo> getOnUpdateVmProcessingListener() {
        return onUpdateVmProcessingListener;
    }

    public VmBuilder setOnUpdateVmProcessingListener(final EventListener<VmHostEventInfo> onUpdateVmProcessing) {
        this.onUpdateVmProcessingListener = Objects.requireNonNull(onUpdateVmProcessing);
        return this;
    }
}
