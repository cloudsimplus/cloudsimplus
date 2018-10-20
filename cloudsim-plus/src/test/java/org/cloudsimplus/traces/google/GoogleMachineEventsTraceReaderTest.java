/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.traces.google;

import org.cloudsimplus.traces.google.GoogleMachineEventsTraceReader.FieldIndex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public class GoogleMachineEventsTraceReaderTest {

    /**
     * Ensures the order or enums is not changed,
     * because that will cause the enums ordinal values to change.
     * These values are defined in the Google Cluster Data documentation.
     */
    @Test
    public void testEventType(){
        assertEquals(0, MachineEventType.ADD.ordinal());
        assertEquals(1, MachineEventType.REMOVE.ordinal());
        assertEquals(2, MachineEventType.UPDATE.ordinal());
    }

    /**
     * Ensures the order or enums is not changed,
     * because that will cause the enums ordinal values to change.
     * These values are defined in the Google Cluster Data documentation.
     */
    @Test
    public void testFieldIndex(){
        assertEquals(0, FieldIndex.TIMESTAMP.ordinal());
        assertEquals(1, FieldIndex.MACHINE_ID.ordinal());
        assertEquals(2, FieldIndex.EVENT_TYPE.ordinal());
        assertEquals(3, FieldIndex.PLATFORM_ID.ordinal());
        assertEquals(4, FieldIndex.CPU_CAPACITY.ordinal());
        assertEquals(5, FieldIndex.RAM_CAPACITY.ordinal());
    }

}
