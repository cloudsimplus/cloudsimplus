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
package org.cloudsimplus.integrationtests;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.placementpolicies.VmPlacementPolicy;
import org.cloudsimplus.placementpolicies.migration.VmPlacementPolicyMigrationAbstract;
import org.cloudsimplus.placementpolicies.migration.VmPlacementPolicyMigrationStaticThreshold;
import org.cloudsimplus.power.models.PowerModelHostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/// A small simulation for VM migration tests, ready to start.
/// Hosts and VMs are created from specs. Each VM runs one Cloudlet with a fixed CPU utilization.
/// VMs are placed into the Hosts given by their specs, then the migration policy takes over.
///
/// Every Host has [#MIPS] MIPS per core and plenty of RAM, BW and storage, unless its spec limits the RAM.
///
/// @param simulation the simulation, not started yet
/// @param datacenter the Datacenter with the Hosts, in the order of their specs
/// @param broker the broker that submitted the VMs and Cloudlets
/// @param vms the VMs, in the order of their specs
record VmMigrationScenario(CloudSimPlus simulation, Datacenter datacenter, DatacenterBroker broker, List<Vm> vms) {
    static final double SCHEDULING_INTERVAL = 5;
    static final double OVER_UTILIZATION_THRESHOLD = 0.8;
    static final double UNDER_UTILIZATION_THRESHOLD = 0.2;

    /// MIPS of each Host and VM core.
    static final int MIPS = 1000;
    static final int HOST_CORES = 8;
    static final long HOST_RAM = 100_000;
    static final long VM_RAM = 1000;

    private static final long HOST_BW = 100_000;
    private static final long HOST_STORAGE = 1_000_000;
    private static final double HOST_MAX_POWER = 200;
    private static final double HOST_STATIC_POWER = 100;
    private static final long VM_BW = 1000;
    private static final long VM_SIZE = 1000;
    private static final long CLOUDLET_LENGTH = 40_000;

    /// A Host with a given number of cores and RAM capacity.
    record HostSpec(int cores, long ram) {
        HostSpec(final int cores) {
            this(cores, HOST_RAM);
        }

        HostSpec() {
            this(HOST_CORES);
        }
    }

    /// A VM with a given number of cores, the CPU utilization of its Cloudlet and its initial Host index.
    record VmSpec(int cores, double cpuUtilization, int hostIndex) { }

    /// Creates a scenario where the given policy places and migrates the VMs.
    static VmMigrationScenario of(
        final List<HostSpec> hostSpecs, final List<VmSpec> vmSpecs, final VmPlacementPolicyMigrationAbstract policy)
    {
        final var simulation = new CloudSimPlus();
        policy.setUnderUtilizationThreshold(UNDER_UTILIZATION_THRESHOLD);
        policy.setFindHostForVmFunction(initialPlacement(vmSpecs));

        final var hosts = hostSpecs.stream().map(VmMigrationScenario::createHost).toList();
        final var datacenter = DatacenterSimple.of(simulation, hosts, policy).setSchedulingInterval(SCHEDULING_INTERVAL);

        final var broker = new DatacenterBrokerSimple(simulation);
        final var vms = vmSpecs.stream().map(VmMigrationScenario::createVm).toList();
        broker.submitVmList(vms);
        submitCloudlets(broker, vms, vmSpecs);

        // After the initial placement, the policy's default Host selection is used to migrate VMs.
        broker.listeners().addOnVmsCreated(info -> policy.setFindHostForVmFunction(null));
        return new VmMigrationScenario(simulation, datacenter, broker, vms);
    }

    static VmPlacementPolicyMigrationAbstract staticThreshold(final VmSelectionPolicy selectionPolicy) {
        return new VmPlacementPolicyMigrationStaticThreshold(selectionPolicy, OVER_UTILIZATION_THRESHOLD);
    }

    /// {@return a function that places each VM into the Host index given by its [VmSpec]}
    /// The broker gives VMs sequential ids from 0, in the order they are submitted (the order of the given List).
    private static BiFunction<VmPlacementPolicy, Vm, Optional<Host>> initialPlacement(final List<VmSpec> vmSpecs) {
        return (policy, vm) -> Optional.of(policy.getHostList().get(vmSpecs.get((int) vm.getId()).hostIndex()));
    }

    private static Host createHost(final HostSpec spec) {
        final var host = HostSimple.of(spec.ram(), HOST_BW, HOST_STORAGE, new CoreCapacity(spec.cores(), MIPS));
        host.setVmScheduler(new VmSchedulerTimeShared()).setStateHistoryEnabled(true);
        host.setPowerModel(new PowerModelHostSimple(HOST_MAX_POWER, HOST_STATIC_POWER));
        return host;
    }

    private static Vm createVm(final VmSpec spec) {
        final var vm = VmSimple.of(new CoreCapacity(spec.cores(), MIPS));
        vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE).setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    private static void submitCloudlets(final DatacenterBroker broker, final List<Vm> vms, final List<VmSpec> vmSpecs) {
        final var cloudlets = new ArrayList<Cloudlet>(vms.size());
        for (int i = 0; i < vms.size(); i++) {
            cloudlets.add(createCloudlet(broker, vms.get(i), vmSpecs.get(i)));
        }

        broker.submitCloudletList(cloudlets);
    }

    private static Cloudlet createCloudlet(final DatacenterBroker broker, final Vm vm, final VmSpec spec) {
        final var cloudlet = CloudletSimple.of(CLOUDLET_LENGTH, spec.cores())
            .setUtilizationModelCpu(new UtilizationModelDynamic(spec.cpuUtilization()))
            .setUtilizationModelRam(new UtilizationModelFull())
            .setUtilizationModelBw(new UtilizationModelFull());
        broker.bindCloudletToVm(cloudlet, vm);
        return cloudlet;
    }
}
