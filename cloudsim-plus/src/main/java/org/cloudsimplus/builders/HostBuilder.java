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

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerAbstract;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Builder class to create {@link Host} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class HostBuilder implements Builder {
    private double mips = 2000;
    private int    pes = 1;
    private long   bandwidth = 10000;
    private long   storage = Conversion.MILLION;
    private long   ram = 1024;
    private Class<? extends VmSchedulerAbstract> vmSchedulerClass = VmSchedulerTimeShared.class;
    private EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener = EventListener.NULL;

    private final List<Host> hosts;

    public HostBuilder() {
        super();
        this.hosts = new ArrayList<>();
    }

    public List<Host> getHosts() {
        return hosts;
    }

    private Host createHost() {
        try {
            final List<Pe> peList = new PeBuilder().create(pes, mips);
            final Constructor cons = vmSchedulerClass.getConstructor();

            final Host host =
                     new HostSimple(ram, bandwidth, storage, peList)
                        .setRamProvisioner(new ResourceProvisionerSimple())
                        .setBwProvisioner(new ResourceProvisionerSimple())
                        .setVmScheduler((VmScheduler) cons.newInstance())
                        .addOnUpdateProcessingListener(onUpdateVmsProcessingListener);
            hosts.add(host);
            return host;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("It wasn't possible to instantiate VmScheduler", ex);
        }
    }

    public HostBuilder createOneHost() {
        return createHosts(1);
    }

    public HostBuilder createHosts(final int amount) {
        validateAmount(amount);

        for (int i = 0; i < amount; i++) {
            hosts.add(createHost());
        }
        return this;
    }

    public double getMips() {
        return mips;
    }

    public HostBuilder setMips(double defaultMIPS) {
        this.mips = defaultMIPS;
        return this;
    }

    public int getPes() {
        return pes;
    }

    public HostBuilder setPes(int defaultPEs) {
        this.pes = defaultPEs;
        return this;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public HostBuilder setBandwidth(long defaultBw) {
        this.bandwidth = defaultBw;
        return this;
    }

    public long getStorage() {
        return storage;
    }

    public HostBuilder setStorage(long defaultStorage) {
        this.storage = defaultStorage;
        return this;
    }

    public long getRam() {
        return ram;
    }

    public HostBuilder setRam(int defaultRam) {
        this.ram = defaultRam;
        return this;
    }

    public Class<? extends VmSchedulerAbstract> getVmSchedulerClass() {
        return vmSchedulerClass;
    }

    public HostBuilder setVmSchedulerClass(Class<? extends VmSchedulerAbstract> defaultVmSchedulerClass) {
        this.vmSchedulerClass = defaultVmSchedulerClass;
        return this;
    }

    public EventListener<HostUpdatesVmsProcessingEventInfo> getOnUpdateVmsProcessingListener() {
        return onUpdateVmsProcessingListener;
    }

    public HostBuilder setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener) {
        this.onUpdateVmsProcessingListener = onUpdateVmsProcessingListener;
        return this;
    }

}
