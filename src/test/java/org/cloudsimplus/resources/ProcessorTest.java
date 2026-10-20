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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ProcessorTest {
    private static final double PE_MIPS = 1000;
    private static final int NUMBER_OF_PES = 2;

    @Test
    public void testGetTotalMipsCapacity() {
        final Processor instance = createDefaultProcessor();
        final double expResult = PE_MIPS * NUMBER_OF_PES;
        assertEquals(expResult, instance.getTotalMips());
    }

    /**
     * Creates a processing using the default values.
     * @return the created processor
     */
    private static Processor createDefaultProcessor() {
        return new Processor(Vm.NULL, NUMBER_OF_PES, PE_MIPS);
    }

    @Test
    public void testGetCapacity() {
        final Processor instance = createDefaultProcessor();
        final double expResult = PE_MIPS;
        assertEquals(expResult, instance.getMips());
    }

    @Test
    public void testGetPesNumberFromDefaultConstructor() {
        final Processor instance = createDefaultProcessor();
        final int expResult = NUMBER_OF_PES;
        assertEquals(expResult, instance.getCapacity());
    }

    @Test
    public void testSetPesNumber() {
        final int expResult = NUMBER_OF_PES*2;
        final Processor instance = createDefaultProcessor();
        instance.setCapacity(expResult);
        assertEquals(expResult, instance.getCapacity());
    }

    @Test
    public void testSetCapacity() {
        final long expResult = (long)PE_MIPS*2;
        final Processor instance = createDefaultProcessor();
        instance.setMips(expResult);
        assertEquals(expResult, instance.getMips());
    }

}
