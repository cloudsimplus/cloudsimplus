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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/// @author Manoel Campos da Silva Filho
public class HarddriveStorageTest {
    private static final int CAPACITY = 1000;

    @Test()
    public void testNewHarddriveStorageWhenOnlyWhiteSpacesName() {
        assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage("   ", CAPACITY));
    }

    @Test()
    public void testNewHarddriveStorageWhenEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage("", CAPACITY));
    }

    @Test()
    public void testNewHarddriveStorageWheNullName() {
        assertThrows(NullPointerException.class, () -> new HarddriveStorage(null, CAPACITY));
    }

    @Test()
    public void testNewHarddriveStorageWhenNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage(-1));
    }

    @Test
    public void testNewHarddriveStorageWhenZeroSize() {
        final int expResult = 0;
        final var hd = new HarddriveStorage(expResult);
        assertEquals(expResult, hd.getCapacity());
    }

    @Test
    public void testGetCapacity() {
        final HarddriveStorage instance = createHardDrive(CAPACITY);
        assertEquals(CAPACITY, instance.getCapacity());
    }

    @Test
    public void testGetTransferTime() {
        final HarddriveStorage instance = createHardDrive(1);
        final int fileSizeInMB = 100;
        final int maxTransferRateInMbitsSec = 10;
        final int latencyInSec = 1;
        final int expectedSecs = 81;
        instance.setLatency(latencyInSec);
        instance.setMaxTransferRate(maxTransferRateInMbitsSec);

        assertEquals(expectedSecs, instance.getTransferTime(fileSizeInMB));
    }

    @Test
    public void testGetName() {
        final String expResult = "hd1";
        final HarddriveStorage instance = createHardDrive(CAPACITY, expResult);
        assertEquals(expResult, instance.getName());
    }

    @Test()
    public void testSetLatencyNegative() {
        final HarddriveStorage instance = createHardDrive();
        assertThrows(IllegalArgumentException.class, () -> instance.setLatency(-1));
    }

    @Test
    public void testSetLatency0() {
        final HarddriveStorage instance = createHardDrive();
        final int expected = 0;
        instance.setLatency(expected);
        assertEquals(expected, instance.getLatency());
    }

    @Test
    public void testSetLatency1() {
        final HarddriveStorage instance = createHardDrive();
        final double latency = 1;
        instance.setLatency(latency);
        assertEquals(latency, instance.getLatency());
    }

    @Test
    public void testSetMaxTransferRate1() {
        final HarddriveStorage instance = createHardDrive();
        final int rate = 1;
        instance.setMaxTransferRate(rate);
        assertEquals(rate, instance.getMaxTransferRate());
    }

    @Test()
    public void testSetMaxTransferRateNegative() {
        final HarddriveStorage instance = createHardDrive();
        assertThrows(IllegalArgumentException.class, () -> instance.setMaxTransferRate(-1));
    }

    @Test()
    public void testSetMaxTransferRate0() {
        final HarddriveStorage instance = createHardDrive();
        assertThrows(IllegalArgumentException.class, () -> instance.setMaxTransferRate(0));
    }

    /// Creates a hard drive with the [#CAPACITY] capacity.
    /// @return
    private HarddriveStorage createHardDrive() {
        return createHardDrive(CAPACITY);
    }

    private HarddriveStorage createHardDrive(final long capacity) {
        return createHardDrive(capacity, "");
    }

    private HarddriveStorage createHardDrive(final long capacity, final String name) {
        if(StringUtils.isBlank(name)) {
            return new HarddriveStorage(capacity);
        }

        return new HarddriveStorage(name, capacity);
    }
}
