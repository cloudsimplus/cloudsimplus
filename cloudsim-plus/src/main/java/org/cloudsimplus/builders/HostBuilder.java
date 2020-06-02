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
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A Builder class to create {@link Host} objects
 * using the default configurations defined in {@link Host} class.
 *
 * @see HostSimple#setDefaultRamCapacity(long)
 * @see HostSimple#setDefaultBwCapacity(long)
 * @see HostSimple#setDefaultStorageCapacity(long)
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class HostBuilder implements Builder {
    private double mips = 2000;
    private int    pes = 1;

    private final List<Host> hosts;
    private Function<List<Pe>, Host> hostCreationFunction;
    private EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener = EventListener.NULL;
    private Supplier<VmScheduler> vmSchedulerSupplier;

    public HostBuilder() {
        super();
        this.hosts = new ArrayList<>();
        this.hostCreationFunction = this::defaultHostCreationFunction;
    }

    /**
     * Creates a single Host and stores it internally.
     * @return
     * @see #getHosts()
     */
    public HostBuilder create() {
        return create(1);
    }

    /**
     * Creates a list of Hosts and stores it internally.
     * @return
     * @see #getHosts()
     */
    public HostBuilder create(final int amount) {
        validateAmount(amount);

        for (int i = 0; i < amount; i++) {
            final List<Pe> peList = new PeBuilder().create(pes, mips);
            final Host host = hostCreationFunction.apply(peList);
            if(vmSchedulerSupplier != null) {
                host.setVmScheduler(vmSchedulerSupplier.get());
            }
            hosts.add(host);
        }
        return this;
    }

    /**
     * Gets the list of all created Hosts.
     * @return
     * @see #create()
     * @see #create(int)
     */
    public List<Host> getHosts() {
        return hosts;
    }

    private Host defaultHostCreationFunction(final List<Pe> peList) {
        return new HostSimple(peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .addOnUpdateProcessingListener(onUpdateVmsProcessingListener);
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

    /**
     * Sets a {@link Function} used to create Hosts.
     * It must receive a list of {@link Pe} for the Host it will create.
     * @param hostCreationFunction
     */
    public void setHostCreationFunction(final Function<List<Pe>, Host> hostCreationFunction) {
        this.hostCreationFunction = Objects.requireNonNull(hostCreationFunction);
    }

    public HostBuilder setOnUpdateVmsProcessingListener(final EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        this.onUpdateVmsProcessingListener = Objects.requireNonNull(listener);
        return this;
    }

    public HostBuilder setVmSchedulerSupplier(final Supplier<VmScheduler> vmSchedulerSupplier) {
        this.vmSchedulerSupplier = Objects.requireNonNull(vmSchedulerSupplier);
        return this;
    }
}
