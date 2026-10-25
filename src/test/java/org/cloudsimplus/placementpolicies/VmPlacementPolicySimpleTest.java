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
package org.cloudsimplus.placementpolicies;

import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimpleTest;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmGroup;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
public class VmPlacementPolicySimpleTest {
    public static final int HOST_MIPS = 1000;
    public static final int HOST_RAM = 10000;
    public static final int HOST_BW = 100000;
    public static final int HOST_BASE_STORAGE = 1000;
    private VmPlacementPolicySimple policy;

    @BeforeEach
    public void setUp(){
        policy = createVmPlacementPolicy(4, 2, 6, 5);
    }

    /// Creates a VmPlacementPolicy.
    ///
    /// @param freeCoresByHost an array containing the number of free cores for each host of the
    /// placement policy, that will be assigned to the freeCoresList.
    /// This array will define the number of Hosts and its cores.
    /// @return
    private VmPlacementPolicySimple createVmPlacementPolicy(int... freeCoresByHost) {
        final Map<Host, Long> hostFreeCoresMap = new HashMap<>(freeCoresByHost.length);
        final List<Host> hosts = new ArrayList<>(freeCoresByHost.length);
        for(int i = 1; i <= freeCoresByHost.length; i++) {
            final Host host = HostSimpleTest.createHostSimple(
                i-1, freeCoresByHost[i-1], HOST_MIPS, HOST_RAM, HOST_BW, i*HOST_BASE_STORAGE);
            hostFreeCoresMap.put(host, host.getProcessor().getCores());
            hosts.add(host);
        }

        final var policy = new VmPlacementPolicySimple();
        policy.setDatacenter(DatacenterSimple.of(Simulation.NULL, hosts));
        return policy;
    }

    @Test
    public void allocateHostForVmWhenOneVmIsGivenAllocateHostWithLessUsedCoresToIt() {
        final Vm vm = VmTestUtil.createVm(0, 1000, 2);
        assertTrue(policy.placeVm(vm).fully());

        final Host allocatedHostForVm = vm.getHost();
        final Host hostWithLessCores = policy.getDatacenter().getHostList().get(2);
        assertEquals(hostWithLessCores, allocatedHostForVm);
    }

    @Test
    public void allocateHostForVmWhenOneVmIsGivenAndSelectedHostDoesntHaveStorageAllocateOtherHost() {
        final Host hostWithMoreFreeCores = policy.getDatacenter().getHostList().get(2);
        final Vm vm = VmTestUtil.createVm(
            0, 1000, 2, 1, 1,
            HOST_BASE_STORAGE, CloudletScheduler.NULL);
        assertTrue(policy.placeVm(vm).fully(), vm + " couldn't be allocated to " + hostWithMoreFreeCores);

        final Host allocatedHostForVm = vm.getHost();
        assertEquals(hostWithMoreFreeCores, allocatedHostForVm);
    }

    @Test
    public void allocateHostForVmWhenOneVmIsGivenAndNoHostHasResourcesToRunIt() {
        final Vm vm = VmTestUtil.createVm(0, 1000, 10);
        assertFalse(policy.placeVm(vm).fully());
    }

    @Test
    public void placeVmGroupIsFullySuitableWhenAllVmsAreCreated() {
        final Host host = policy.getDatacenter().getHostList().get(2);
        final var vmGroup = VmGroup.of(List.of(VmTestUtil.createVm(0, 1000, 2), VmTestUtil.createVm(1, 1000, 2)));

        assertTrue(policy.placeVm(vmGroup, host).fully());
        assertTrue(vmGroup.isCreated());
        vmGroup.getVmList().forEach(vm -> assertEquals(host, vm.getHost()));
    }

    @Test
    public void placeVmGroupIsNotFullySuitableWhenSomeVmIsNotCreated() {
        final Host host = policy.getDatacenter().getHostList().get(2);
        final var vmGroup = VmGroup.of(List.of(VmTestUtil.createVm(0, 1000, 4), VmTestUtil.createVm(1, 1000, 4)));

        final var suitability = policy.placeVm(vmGroup, host);
        assertFalse(suitability.fully());
        assertFalse(suitability.forCores());
        assertTrue(vmGroup.isCreated(), "The group is created since one of its VMs was");
    }
}
