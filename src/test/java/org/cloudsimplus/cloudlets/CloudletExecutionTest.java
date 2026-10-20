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
package org.cloudsimplus.cloudlets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
class CloudletExecutionTest {
    private final CloudletExecution instance = new CloudletExecution(Cloudlet.NULL);

    @Test
    public void testTimeSpanIntegerTime(){
        instance.setLastProcessingTime(0);
        final double expected = 10;
        assertEquals(expected, instance.processingTimeSpan(expected), 0.01);
    }

    @Test
    public void testTimeSpanLessThan1(){
        instance.setLastProcessingTime(0);
        final double expected = 0.6;
        assertEquals(expected, instance.processingTimeSpan(expected), 0.01);
    }

    @Test
    public void testTimeSpanGreaterThan1AndLessThan2(){
        instance.setLastProcessingTime(0);
        final double expected = 1.7;
        assertEquals(expected, instance.processingTimeSpan(expected), 0.01);
    }
}
