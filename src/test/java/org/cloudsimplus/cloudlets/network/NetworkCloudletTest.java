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
package org.cloudsimplus.cloudlets.network;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
class NetworkCloudletTest {
    private static final double FINISH_TIME = 1;

    /// A Cloudlet with a task that didn't finish.
    private NetworkCloudlet cloudlet;

    @BeforeEach
    void setUp() {
        cloudlet = NetworkCloudlet.of(1);
        cloudlet.addTask(new CloudletExecutionTask(0, 100));
        cloudlet.setFinishTime(FINISH_TIME);
    }

    @Test
    void cloudletWithUnfinishedTaskIsNotFinished() {
        assertFalse(cloudlet.isFinished());
    }

    /// A stopped Cloudlet won't run anymore, so its unfinished tasks don't matter.
    @ParameterizedTest
    @EnumSource(value = Cloudlet.Status.class, names = {"FAILED", "CANCELED"})
    void stoppedCloudletWithUnfinishedTaskIsFinished(final Cloudlet.Status status) {
        cloudlet.setStatus(status);
        assertTrue(cloudlet.isFinished());
    }
}
