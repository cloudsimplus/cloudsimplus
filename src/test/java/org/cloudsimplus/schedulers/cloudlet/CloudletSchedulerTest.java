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
package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletSchedulerTest {

    @Test
    public void testNullObject() {
        final CloudletScheduler instance = CloudletScheduler.NULL;
        assertAll(
            () -> assertEquals(Cloudlet.NULL, instance.cloudletCancel(null)),
            () -> assertEquals(0, instance.cloudletResume(null)),
            () -> assertEquals(0, instance.cloudletSubmit(null, 0)),
            () -> assertEquals(0, instance.cloudletSubmit(null)),
            () -> assertFalse(instance.cloudletPause(null)),
            () -> assertTrue(instance.getCloudletFinishedList().isEmpty()),
            () -> assertEquals(0, instance.getCurrentRequestedBwPercentUtilization()),
            () -> assertEquals(0, instance.getCurrentRequestedRamPercentUtilization()),
            () -> assertEquals(0, instance.getPreviousTime()),
            () -> assertEquals(0, instance.getRequestedCpuPercent(0)),
            () -> assertFalse(instance.hasFinishedCloudlets()),
            () -> assertEquals(0, instance.updateProcessing(0, null))
        );
    }

}
