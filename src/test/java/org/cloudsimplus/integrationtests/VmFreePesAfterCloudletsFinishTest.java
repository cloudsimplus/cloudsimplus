package org.cloudsimplus.integrationtests;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.vms.VmSimple;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that after all Cloudlets finish execution on a VM,
 * both {@code freePesNumber} and {@code expectedFreePesNumber}
 * are correctly updated to the total number of VM PEs.
 *
 * @author Theodoros Aslanidis
 * @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/544">Issue #544</a>
 */
public final class VmFreePesAfterCloudletsFinishTest {
    private static final int HOST_MIPS = 1000;
    private static final int VM_PES = 2;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 10_000;
    private static final int HRAM = 2048;
    private static final int HBW = 10_000;
    private static final int HSTORAGE = 1_000_000;
    private static final int VM_RAM = 512;
    private static final int VM_BW = 1000;

    private CloudSimPlus simulation;
    private VmSimple vm;

    @BeforeEach
    public void setUp() {
        simulation = new CloudSimPlus();
        final var host = new HostSimple(HRAM, HBW, HSTORAGE, List.of(newPe(), newPe()));
        new DatacenterSimple(simulation, List.of(host));

        final var broker = new DatacenterBrokerSimple(simulation);
        vm = new VmSimple(HOST_MIPS, VM_PES);
        vm.setRam(VM_RAM).setBw(VM_BW).setSize(HBW);
        broker.submitVmList(List.of(vm));
        final var cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES);
        broker.submitCloudletList(List.of(cloudlet));
    }

    private static @NotNull PeSimple newPe() {
        return new PeSimple(HOST_MIPS);
    }

    /**
     * After all Cloudlets finish, both freePesNumber and expectedFreePesNumber
     * must be equal to the total VM PEs.
     */
    @Test
    public void freePesEqualsVmPesAfterAllCloudletsFinish() {
        simulation.start();
        assertEquals(VM_PES, vm.getFreePesNumber(),
            "freePesNumber should equal total VM PEs after all Cloudlets finish");
        assertEquals(VM_PES, vm.getExpectedFreePesNumber(),
            "expectedFreePesNumber should equal total VM PEs after all Cloudlets finish");
    }
}
