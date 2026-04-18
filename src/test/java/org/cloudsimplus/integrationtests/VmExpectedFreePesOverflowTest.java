package org.cloudsimplus.integrationtests;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.vms.VmSimple;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Verifies that {@code expectedFreePesNumber} does not overflow
 * beyond the total VM PEs when a Cloudlet requires more PEs
 * than the VM has available.
 *
 * @author Theodoros Aslanidis
 * @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/546">Issue #546</a>
 */
public final class VmExpectedFreePesOverflowTest {
    // Constants that matter to the test.
    private static final int VM_PES = 2;
    private static final int CLOUDLET_PES_HIGHER_THAN_VM_CAPACITY = 3;

    // Attributes used across the entire test class
    private static final int HOST_MIPS = 1000;
    private CloudSimPlus simulation;
    private VmSimple vm;

    @BeforeEach
    public void setUp() {
        // Values for attributes that don't matter to the test
        final int cloudletLen = 10_000;
        final int hRAM = 2048;
        final int hBW = 10_000;
        final int hStorage = 1_000_000;
        final int vmRAM = 512;
        final int vmBW = 1000;
        final int vmStorage = 10_000;

        simulation = new CloudSimPlus();
        final List<Pe> peList = List.of(newPe(), newPe(), newPe(), newPe());
        final var host = new HostSimple(hRAM, hBW, hStorage, peList);
        new DatacenterSimple(simulation, List.of(host));

        final var broker = new DatacenterBrokerSimple(simulation);
        vm = new VmSimple(HOST_MIPS, VM_PES);
        vm.setRam(vmRAM).setBw(vmBW).setSize(vmStorage);
        broker.submitVmList(List.of(vm));

        final var cloudlet = new CloudletSimple(cloudletLen, CLOUDLET_PES_HIGHER_THAN_VM_CAPACITY);
        broker.submitCloudletList(List.of(cloudlet));
    }

    private static @NotNull PeSimple newPe() {
        return new PeSimple(HOST_MIPS);
    }

    /**
     * When a Cloudlet requires more PEs than the VM has,
     * {@code expectedFreePesNumber} must never exceed the total VM PEs
     * after the Cloudlet finishes.
     */
    @Test
    public void expectedFreePesDoesNotExceedVmPesAfterCloudletFinish() {
        assumeTrue(CLOUDLET_PES_HIGHER_THAN_VM_CAPACITY > VM_PES,
            "This test is only valid if the Cloudlet requires more PEs than the VM has");

        simulation.start();
        assertTrue(vm.getExpectedFreePesNumber() <= vm.getPesNumber(),
            "expectedFreePesNumber (%d) should not exceed total VM PEs (%d)"
                .formatted(vm.getExpectedFreePesNumber(), vm.getPesNumber()));

        assertEquals(VM_PES, vm.getExpectedFreePesNumber(),
            "expectedFreePesNumber should equal total VM PEs after all Cloudlets finish");

        assertEquals(VM_PES, vm.getFreePesNumber(),
            "freePesNumber should equal total VM PEs after all Cloudlets finish");
    }
}
