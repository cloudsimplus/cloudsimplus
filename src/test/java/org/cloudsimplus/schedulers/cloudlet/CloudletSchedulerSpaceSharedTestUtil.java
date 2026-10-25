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
package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletTestUtil;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

/// An utility class used by [CloudletSchedulerSpaceShared] tests.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
final class CloudletSchedulerSpaceSharedTestUtil {

    static final long SCHEDULER_MIPS = 1000;

    /// A private constructor to avoid class instantiation.
    private CloudletSchedulerSpaceSharedTestUtil(){/**/}

    private static CloudletSchedulerSpaceShared createCloudletSchedulerWithMipsList(int cores, long mips) {
        final CloudletSchedulerSpaceShared instance = createScheduler(mips, cores);
        instance.setCurrentMipsShare(new CoreCapacity(cores, mips));
        return instance;
    }

    /// Creates a scheduler with a list of running cloudlets, where each Cloudlet has just one core.
    ///
    /// @param mips the MIPS capacity of each core from the VM's scheduler
    /// @param numberOfVmCores number of CPU cores of the VM's scheduler
    /// @param numberOfCloudlets number of Cloudlets to create
    /// @return the new scheduler
    /* default */ static CloudletSchedulerSpaceShared newSchedulerWithSingleCoreRunningCloudlets(long mips, int numberOfVmCores, int numberOfCloudlets) {
        return newSchedulerWithRunningCloudlets(mips, numberOfVmCores, numberOfCloudlets, 1);
    }

    /// Creates a scheduler with a list of running cloudlets.
    /// @param mips the MIPS capacity of each core from the VM's scheduler
    /// @param numberOfVmCores number of CPU cores of the VM's scheduler
    /// @param numberOfCloudlets number of Cloudlets to create
    /// @param numberOfCloudletCores the number of CPU cores for each Cloudlet
    /// @return the new scheduler
    /* default */ static CloudletSchedulerSpaceShared newSchedulerWithRunningCloudlets(long mips, int numberOfVmCores, int numberOfCloudlets, int numberOfCloudletCores) {
        final CloudletSchedulerSpaceShared instance = createCloudletSchedulerWithMipsList(numberOfVmCores, mips);

        for(int i = 0; i < numberOfCloudlets; i++) {
            final var cloudlet = CloudletTestUtil.createCloudlet(i, mips, numberOfCloudletCores);
            cloudlet.registerArrivalInDatacenter();
            instance.cloudletSubmit(cloudlet);
        }

        return instance;
    }

    /* default */ static CloudletSchedulerSpaceShared createScheduler() {
        return createScheduler(2);
    }

    /* default */ static CloudletSchedulerSpaceShared createScheduler(final int cores) {
        return createScheduler(SCHEDULER_MIPS, cores);
    }

    private static CloudletSchedulerSpaceShared createScheduler(final long mips, final int cores) {
        return createScheduler(VmSimple.of(new CoreCapacity(cores, mips)));
    }

    /* default */ static CloudletSchedulerSpaceShared createScheduler(final Vm vm) {
        final var scheduler = new CloudletSchedulerSpaceShared();
        scheduler.setVm(vm);
        scheduler.setCurrentMipsShare(vm.getProcessor().toCoreCapacity());
        return scheduler;
    }
}
