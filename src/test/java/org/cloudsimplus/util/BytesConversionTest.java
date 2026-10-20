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
package org.cloudsimplus.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// @author Manoel Campos da Silva Filho
public class BytesConversionTest {
    private static final double ONE_MEGABYTE_IN_BYTES = 1048576;

    @Test
    public void kilo(){
        final double expectedBytes = 1024;
        assertEquals(expectedBytes, BytesConversion.KILO);
    }

    @Test
    public void mega(){
        final double expectedBytes = ONE_MEGABYTE_IN_BYTES;
        assertEquals(expectedBytes, BytesConversion.MEGA);
    }

    @Test
    public void giga(){
        final double expectedBytes = 1073741824;
        assertEquals(expectedBytes, BytesConversion.GIGA);
    }

    @Test
    public void tera(){
        final double expectedBytes = 1099511627776.0;
        assertEquals(expectedBytes, BytesConversion.TERA);
    }

    @Test
    public void bytesToMegaBytes(){
        final float expectedMegaBytes = 1;
        assertEquals(expectedMegaBytes, BytesConversion.bytesToMegaBytes(ONE_MEGABYTE_IN_BYTES));
    }

    @Test
    public void bytesToMegaBites(){
        final double expectedMegaBits = 8;
        assertEquals(expectedMegaBits, BytesConversion.bytesToMegaBits(ONE_MEGABYTE_IN_BYTES));
    }

    @Test
    public void bytesToBites(){
        final double expectedBits = 8192;
        assertEquals(expectedBits, BytesConversion.bytesToBits(1024));
    }

    @Test
    public void bitesToBytes(){
        final double expectedBytes = 128;
        assertEquals(expectedBytes, BytesConversion.bitsToBytes(1024));
    }

    @Test
    public void gigaToMega(){
        final double giga = 1;
        final double expectedMB = 1024;
        assertEquals(expectedMB, BytesConversion.gigaToMega(giga));
    }

    @Test
    public void teraToGiga(){
        final double tera = 1;
        final double expectedGB = 1024;
        assertEquals(expectedGB, BytesConversion.teraToGiga(tera));
    }

    @Test
    public void teraToMega(){
        final double tera = 1;
        final double expectedMB = 1048576;
        assertEquals(expectedMB, BytesConversion.teraToMega(tera));
    }
}
