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
package org.cloudsimplus.faultinjection;

import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.distributions.UniformDistr;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreStatus;
import org.cloudsimplus.resources.Processor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
class HostFaultInjectionTest {
    private static final int HOST_CORES = 8;
    private static final double MIPS = 1000;

    @Test
    void testFailuresOnlyBusyCores() {
        final Host host = createHost();
        final Processor processor = host.getProcessor();
        processor.moveCores(CoreStatus.FREE, CoreStatus.BUSY, HOST_CORES);

        createFaultInjection(host, 1).generateHostFault(host, 3);
        assertCoreCounts(processor, 0, HOST_CORES - 3, 3);
    }

    @Test
    void testFailuresOnlyFreeCores() {
        final Host host = createHost();
        final Processor processor = host.getProcessor();

        createFaultInjection(host, 1).generateHostFault(host, 3);
        assertCoreCounts(processor, HOST_CORES - 3, 0, 3);
    }

    @Test
    void testFailuresLimitedToWorkingCores() {
        final Host host = createHost();
        final Processor processor = host.getProcessor();
        processor.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 2);

        createFaultInjection(host, 1).generateHostFault(host, HOST_CORES * 2);
        assertCoreCounts(processor, 0, 0, HOST_CORES);
    }

    /// A busy core that fails is replaced by a free one, so the number of busy cores
    /// doesn't change while there are free cores.
    @Test
    void testFreeCoresFailFirst() {
        final Host host = createHost();
        final Processor processor = host.getProcessor();
        processor.moveCores(CoreStatus.FREE, CoreStatus.BUSY, HOST_CORES / 2);

        createFaultInjection(host, 1).generateHostFault(host, HOST_CORES / 2 + 1);
        assertCoreCounts(processor, 0, HOST_CORES / 2 - 1, HOST_CORES / 2 + 1);
        assertEquals(HOST_CORES, sumCores(processor));
    }

    private static void assertCoreCounts(final Processor processor, final long free, final long busy, final long failed) {
        assertAll(
            () -> assertEquals(free, processor.getCores(CoreStatus.FREE), "free"),
            () -> assertEquals(busy, processor.getCores(CoreStatus.BUSY), "busy"),
            () -> assertEquals(failed, processor.getCores(CoreStatus.FAILED), "failed")
        );
    }

    private static long sumCores(final Processor processor) {
        long sum = 0;
        for (final CoreStatus status : CoreStatus.values()) {
            sum += processor.getCores(status);
        }

        return sum;
    }

    private static Host createHost() {
        return new HostSimple(new CoreCapacity(HOST_CORES, MIPS));
    }

    private static HostFaultInjection createFaultInjection(final Host host, final long seed) {
        final var simulation = new CloudSimPlus();
        final var datacenter = new DatacenterSimple(simulation, List.of(host));
        return new HostFaultInjection(datacenter, new UniformDistr(seed));
    }
}
