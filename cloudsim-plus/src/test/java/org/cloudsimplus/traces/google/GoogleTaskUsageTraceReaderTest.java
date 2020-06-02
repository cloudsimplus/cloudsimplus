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

import org.cloudsimplus.traces.google.GoogleTaskUsageTraceReader.FieldIndex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
public class GoogleTaskUsageTraceReaderTest {

    /**
     * Ensures the order or enums is not changed,
     * because that will cause the enums ordinal values to change.
     * These values are defined in the Google Cluster Data documentation
     * and indicate the order of the fields inside the trace file.
     */
    @Test
    public void testFieldIndex(){
        assertEquals(0, FieldIndex.START_TIME.ordinal());
        assertEquals(1, FieldIndex.END_TIME.ordinal());
        assertEquals(2, FieldIndex.JOB_ID.ordinal());
        assertEquals(3, FieldIndex.TASK_INDEX.ordinal());
        assertEquals(4, FieldIndex.MACHINE_ID.ordinal());
        assertEquals(5, FieldIndex.MEAN_CPU_USAGE_RATE.ordinal());
        assertEquals(6, FieldIndex.CANONICAL_MEMORY_USAGE.ordinal());
        assertEquals(7, FieldIndex.ASSIGNED_MEMORY_USAGE.ordinal());
        assertEquals(8, FieldIndex.UNMAPPED_PAGE_CACHE_MEMORY_USAGE.ordinal());
        assertEquals(9, FieldIndex.TOTAL_PAGE_CACHE_MEMORY_USAGE.ordinal());
        assertEquals(10, FieldIndex.MAXIMUM_MEMORY_USAGE.ordinal());
        assertEquals(11, FieldIndex.MEAN_DISK_IO_TIME.ordinal());
        assertEquals(12, FieldIndex.MEAN_LOCAL_DISK_SPACE_USED.ordinal());
        assertEquals(13, FieldIndex.MAXIMUM_CPU_USAGE.ordinal());
        assertEquals(14, FieldIndex.MAXIMUM_DISK_IO_TIME.ordinal());
    }
}
