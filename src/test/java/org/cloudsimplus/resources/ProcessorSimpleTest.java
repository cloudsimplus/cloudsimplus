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
package org.cloudsimplus.resources;

import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
public class ProcessorSimpleTest {
    private static final double CORE_MIPS = 1000;
    private static final int NUMBER_OF_CORES = 4;

    /// Creates a processor using the default values.
    /// @return the created processor
    private static Processor createDefaultProcessor() {
        return new ProcessorSimple(Vm.NULL, new CoreCapacity(NUMBER_OF_CORES, CORE_MIPS));
    }

    @Test
    public void testGetTotalMipsCapacity() {
        final Processor instance = createDefaultProcessor();
        final double expResult = CORE_MIPS * NUMBER_OF_CORES;
        assertEquals(expResult, instance.getTotalMips());
    }

    @Test
    public void testGetTotalMipsCapacityWithFailedCores() {
        final Processor instance = createDefaultProcessor();
        instance.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
        assertEquals(CORE_MIPS * (NUMBER_OF_CORES - 1), instance.getTotalMips());
    }

    @Test
    public void testGetMips() {
        final Processor instance = createDefaultProcessor();
        assertEquals(CORE_MIPS, instance.getMips());
    }

    @Test
    public void testGetCoresNumberFromDefaultConstructor() {
        final Processor instance = createDefaultProcessor();
        assertAll(
            () -> assertEquals(NUMBER_OF_CORES, instance.getCapacity()),
            () -> assertEquals(NUMBER_OF_CORES, instance.getCores(CoreStatus.FREE)),
            () -> assertEquals(0, instance.getCores(CoreStatus.BUSY)),
            () -> assertEquals(0, instance.getCores(CoreStatus.FAILED))
        );
    }

    @Test
    public void testSetCoresNumber() {
        final int expResult = NUMBER_OF_CORES*2;
        final Processor instance = createDefaultProcessor();
        instance.setCapacity(expResult);
        assertEquals(expResult, instance.getCapacity());
    }

    @Test
    public void testSetCoresNumberLowerThanBusyAndFailedCores() {
        final Processor instance = createDefaultProcessor();
        instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 2);
        instance.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
        assertAll(
            () -> assertFalse(instance.setCapacity(2)),
            () -> assertTrue(instance.setCapacity(3)),
            () -> assertEquals(0, instance.getCores(CoreStatus.FREE))
        );
    }

    @Test
    public void testSetCoresNumberToZero() {
        final Processor instance = createDefaultProcessor();
        assertAll(
            () -> assertFalse(instance.setCapacity(-1)),
            () -> assertTrue(instance.setCapacity(0)),
            () -> assertEquals(0, instance.getCores())
        );
    }

    @Test
    public void testConstructorRejectsZeroCores() {
        assertThrows(IllegalArgumentException.class, () -> new ProcessorSimple(Vm.NULL, new CoreCapacity(0, CORE_MIPS)));
    }

    @Test
    public void testSetMipsAfterMachineStarted() {
        final var vm = new VmSimple(new CoreCapacity(NUMBER_OF_CORES, CORE_MIPS));
        vm.setCreated(true);
        assertThrows(IllegalStateException.class, () -> vm.getProcessor().setMips(CORE_MIPS * 2));
    }

    @Test
    public void testCannotMoveFailedCoresToBusy() {
        final Processor instance = createDefaultProcessor();
        instance.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
        assertFalse(instance.moveCores(CoreStatus.FAILED, CoreStatus.BUSY, 1));
    }

    @Test
    public void testWorkingCoresAreTheOnesInWorkingStatuses() {
        final Processor instance = createDefaultProcessor();
        instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 1);
        instance.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
        final long coresInWorkingStatuses =
            Arrays.stream(CoreStatus.values()).filter(CoreStatus::working).mapToLong(instance::getCores).sum();
        assertEquals(instance.getWorkingCores(), coresInWorkingStatuses);
    }

    @Test
    public void testDeallocateAndRemoveFreesBusyCoresFirst() {
        final Processor instance = createDefaultProcessor();
        instance.setOccupied(NUMBER_OF_CORES);
        assertTrue(instance.freeAndRemove(1));
        assertAll(
            () -> assertEquals(NUMBER_OF_CORES - 1, instance.getCores()),
            () -> assertEquals(NUMBER_OF_CORES - 1, instance.getBusyCores()),
            () -> assertEquals(0, instance.getFreeCores())
        );
    }

    @Test
    public void testSetMips() {
        final long expResult = (long)CORE_MIPS*2;
        final Processor instance = createDefaultProcessor();
        instance.setMips(expResult);
        assertEquals(expResult, instance.getMips());
    }

    @Test
    public void testAllocateAndDeallocateCores() {
        final Processor instance = createDefaultProcessor();
        assertTrue(instance.occupy(3));
        assertAll(
            () -> assertEquals(3, instance.getOccupied()),
            () -> assertEquals(3, instance.getCores(CoreStatus.BUSY)),
            () -> assertEquals(1, instance.getFree())
        );

        assertTrue(instance.free(2));
        assertEquals(1, instance.getCores(CoreStatus.BUSY));
    }

    @Test
    public void testFailedCoresAreNotAllocatable() {
        final Processor instance = createDefaultProcessor();
        instance.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 3);
        assertAll(
            () -> assertFalse(instance.occupy(2)),
            () -> assertTrue(instance.occupy(1)),
            () -> assertEquals(1, instance.getOccupied()),
            () -> assertEquals(1, instance.getWorkingCores())
        );
    }

    @Test
    public void testDeallocateAllResourcesKeepsFailedCores() {
        final Processor instance = createDefaultProcessor();
        instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 2);
        instance.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);

        assertEquals(2, instance.freeAll());
        assertAll(
            () -> assertEquals(0, instance.getCores(CoreStatus.BUSY)),
            () -> assertEquals(3, instance.getCores(CoreStatus.FREE)),
            () -> assertEquals(1, instance.getCores(CoreStatus.FAILED))
        );
    }

    @Test
    public void testMoveCoresInvalidArguments() {
        final Processor instance = createDefaultProcessor();
        assertAll(
            () -> assertFalse(instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 0)),
            () -> assertFalse(instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, -1)),
            () -> assertFalse(instance.moveCores(CoreStatus.FREE, CoreStatus.FREE, 1)),
            () -> assertFalse(instance.moveCores(CoreStatus.BUSY, CoreStatus.FREE, 1)),
            () -> assertFalse(instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, NUMBER_OF_CORES + 1)),
            () -> assertEquals(NUMBER_OF_CORES, instance.getCores(CoreStatus.FREE))
        );
    }

    /// Moves cores into and out of every status (from and to every other one that allows such moves),
    /// checking that the sum of cores in all statuses is always equal to the processor capacity.
    /// A new [CoreStatus] that isn't handled by the processor makes this test fail.
    @ParameterizedTest
    @EnumSource(CoreStatus.class)
    public void testMoveCoresKeepsTotalForEveryStatus(final CoreStatus status) {
        final Processor instance = createDefaultProcessor();
        for (final CoreStatus other : CoreStatus.values()) {
            if (other != status && isMoveAllowed(other, status) && isMoveAllowed(status, other)) {
                moveCoreInAndOut(instance, status, other);
            }
        }
    }

    /// @return true if cores can be moved between the given statuses:
    /// cores can only become busy if they are in an [allocatable][CoreStatus#allocatable()] status.
    private static boolean isMoveAllowed(final CoreStatus from, final CoreStatus to) {
        return to != CoreStatus.BUSY || from.allocatable();
    }

    /// Moves 1 core from another status into a given status and then back,
    /// checking the number of cores in the given status and the total number of cores.
    /// @param instance the processor to move cores
    /// @param status the status to move a core into and out
    /// @param other the status to move a core from and back to
    private static void moveCoreInAndOut(final Processor instance, final CoreStatus status, final CoreStatus other) {
        if (other != CoreStatus.FREE) {
            moveAndCheck(instance, CoreStatus.FREE, other);
        }

        final long cores = instance.getCores(status);
        moveAndCheck(instance, other, status);
        assertEquals(cores + 1, instance.getCores(status));

        moveAndCheck(instance, status, other);
        assertEquals(cores, instance.getCores(status));
    }

    private static void moveAndCheck(final Processor instance, final CoreStatus from, final CoreStatus to) {
        assertTrue(instance.moveCores(from, to, 1), "Unable to move a core from %s to %s".formatted(from, to));
        final long total = Arrays.stream(CoreStatus.values()).mapToLong(instance::getCores).sum();
        assertEquals(instance.getCapacity(), total);
    }

    @Test
    public void testGetPercentUtilization() {
        final Processor instance = createDefaultProcessor();
        instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 1);
        assertEquals(1.0 / NUMBER_OF_CORES, instance.getPercentOccupied());
    }

    @Test
    public void testFailAllCores() {
        final Processor instance = createDefaultProcessor();
        instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 1);
        instance.failAllCores();
        assertEquals(NUMBER_OF_CORES, instance.getFailedCores());
        assertEquals(0, instance.getWorkingCores());
    }

    @Test
    public void testRecoverAllCores() {
        final Processor instance = createDefaultProcessor();
        instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 1);
        instance.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
        instance.recoverAllCores();
        assertAll(
            () -> assertEquals(0, instance.getFailedCores()),
            () -> assertEquals(1, instance.getBusyCores()),
            () -> assertEquals(NUMBER_OF_CORES - 1, instance.getFreeCores())
        );
    }

    @Test
    public void testNullProcessor() {
        final Processor instance = Processor.NULL;
        assertAll(
            () -> assertEquals(0, instance.getCapacity()),
            () -> assertEquals(0, instance.getTotalMips()),
            () -> assertEquals(0, instance.getPercentOccupied()),
            () -> assertFalse(instance.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 1))
        );
    }
}
