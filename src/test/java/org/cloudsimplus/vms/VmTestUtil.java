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

import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.mocks.MocksHelper;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;

import java.util.function.Consumer;

/// An utility class used by Vm tests.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
public final class VmTestUtil {
    /* default */ static final long BANDWIDTH = 10000;
    /* default */ static final long SIZE = 1000;
    /* default */ static final String VMM = "Xen";
    /* default */ static final double MIPS = 1000;
    /* default */ static final int RAM = 1024;
    /* default */ static final int ID = 1;
    /* default */ static final int CORES_NUMBER = 2;

    /// A private constructor to avoid class instantiation.
    private VmTestUtil(){/**/}

    /// Creates a VM with the given cores and default configuration for
    /// HOST_MIPS, HOST_RAM, HOST_BW and Storage.
    ///
    /// @param vmId
    /// @param cores
    /// @return
    public static VmSimple createVm(final int vmId, final int cores) {
        return createVm(vmId, MIPS, cores);
    }

    public static VmSimple createVm(final int vmId, final int cores, final DatacenterBroker broker) {
        final VmSimple vm = createVm(vmId, cores);
        vm.setBroker(broker);
        return vm;
    }

    /// Creates a VM with the default configuration defined in the Test Class'
    /// constants.
    ///
    /// @param cloudletScheduler
    /// @return
    public static VmSimple createVm(final CloudletScheduler cloudletScheduler, final Consumer<DatacenterBroker> brokerMockerConsumer) {
        return createVm(ID, MIPS, CORES_NUMBER, RAM, BANDWIDTH, SIZE, cloudletScheduler, brokerMockerConsumer);
    }

    public static VmSimple createVm(final CloudletScheduler cloudletScheduler) {
        return createVm(ID, MIPS, CORES_NUMBER, RAM, BANDWIDTH, SIZE, cloudletScheduler);
    }

    /// Creates a VM with the given mips and cores and default
    /// configuration for HOST_RAM, HOST_BW and Storage.
    ///
    /// @param vmId
    /// @param mips
    /// @param cores
    /// @param brokerMockerConsumer a consumer to configure internally created broker mock
    /// @return
    public static VmSimple createVm(
        final int vmId, final double mips, final int cores,
        final Consumer<DatacenterBroker> brokerMockerConsumer)
    {
        return createVm(vmId, mips, cores, RAM, BANDWIDTH, SIZE, CloudletScheduler.NULL, brokerMockerConsumer);
    }

    public static VmSimple createVm(final int vmId, final double mips, final int cores) {
        return createVm(vmId, mips, cores, RAM, BANDWIDTH, SIZE, CloudletScheduler.NULL);
    }

    /// Creates a VM with 1 core.
    ///
    /// @param vmId id of the VM
    /// @param capacity a capacity that will be set to all resources, such as CPU, HOST_RAM, HOST_BW, etc.
    /// @return
    public static VmSimple createVm(final int vmId, final long capacity) {
        return createVm(vmId, capacity, 1, capacity, capacity, capacity, CloudletScheduler.NULL);
    }

    public static VmSimple createVm(
        final int vmId,
        final double mips, final int cores,
        final long ram, final long bw, final long storage, CloudSimPlus cloudsim)
    {
        final DatacenterBroker broker = new DatacenterBrokerSimple(cloudsim);
        final var vm = new VmSimple(vmId, new CoreCapacity(cores, mips));
        vm.setRam(ram).setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(CloudletScheduler.NULL)
                .setBroker(broker);
        return vm;
    }

    public static VmSimple createVm(
        final int vmId,
        final double mips, final int cores,
        final long ram, final long bw, final long storage,
        final CloudletScheduler scheduler)
    {
        return createVm(vmId, mips, cores, ram, bw, storage, scheduler, broker -> {});
    }

    /// @param vmId
    /// @param mips
    /// @param cores
    /// @param ram
    /// @param bw
    /// @param storage
    /// @param scheduler
    /// @param brokerMockerConsumer a consumer to configure internally created broker mock
    /// @return
    public static VmSimple createVm(
        final int vmId,
        final double mips, final int cores,
        final long ram, final long bw, final long storage,
        final CloudletScheduler scheduler,
        final Consumer<DatacenterBroker> brokerMockerConsumer)
    {
        final CloudSimPlus cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(0);
            mocker.clockStr();
        });

        final DatacenterBroker broker = MocksHelper.createMockBroker(cloudsim, brokerMockerConsumer);
        final var vm = new VmSimple(vmId, new CoreCapacity(cores, mips));
        vm.setRam(ram).setBw(bw)
           .setSize(storage)
           .setCloudletScheduler(scheduler)
           .setBroker(broker);
        return vm;
    }
}
