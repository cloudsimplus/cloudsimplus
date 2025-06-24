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
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudsimplus.provisioners.ResourceProvisionerSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.schedulers.vm.VmScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A Builder class to create {@link Host} objects
 * using the default configurations defined in the {@link Host} class.
 *
 * @see HostSimple#setDefaultRamCapacity(long)
 * @see HostSimple#setDefaultBwCapacity(long)
 * @see HostSimple#setDefaultStorageCapacity(long)
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
@Accessors(chain = true)
public class HostBuilder implements Builder {
    @Getter @Setter private double mips = 2000;
    @Getter @Setter private int    pes = 1;

    /**
     * The list of all created Hosts
     */
    @Getter private final List<Host> hosts;

    /**
     * A {@link Function} used to create Hosts.
     * It must receive a list of {@link Pe} for the Host it will create.
     */
    @Setter @NonNull private Function<List<Pe>, Host> hostCreationFunction;
    @Setter @NonNull private EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener = EventListener.NULL;
    @Setter private Supplier<VmScheduler> vmSchedulerSupplier;

    public HostBuilder() {
        super();
        this.hosts = new ArrayList<>();
        this.hostCreationFunction = this::defaultHostCreationFunction;
    }

    /**
     * Creates a single Host and stores it internally.
     * @return this builder
     * @see #getHosts()
     */
    public HostBuilder create() {
        return create(1);
    }

    /**
     * Creates a list of Hosts and stores it internally.
     * @return this builder
     * @see #getHosts()
     */
    public HostBuilder create(final int amount) {
        validateAmount(amount);

        for (int i = 0; i < amount; i++) {
            final var peList = new PeBuilder().create(pes, mips);
            final var host = hostCreationFunction.apply(peList);
            if(vmSchedulerSupplier != null) {
                host.setVmScheduler(vmSchedulerSupplier.get());
            }

            hosts.add(host);
        }

        return this;
    }

    private Host defaultHostCreationFunction(final List<Pe> peList) {
        return new HostSimple(peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .addOnUpdateProcessingListener(onUpdateVmsProcessingListener);
    }
}
