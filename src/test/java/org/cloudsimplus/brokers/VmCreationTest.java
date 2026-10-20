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
package org.cloudsimplus.brokers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VmCreationTest {
    private static final double RETRY_DELAY = 5;
    private static final int MAX_RETRIES = 2;

    @Test
    void retriesFailedVmsUntilMaxRetriesIsReached() {
        final var vmCreation = new VmCreation(RETRY_DELAY, MAX_RETRIES);
        incRetries(vmCreation, MAX_RETRIES - 1);
        assertTrue(vmCreation.isRetryFailedVms());

        vmCreation.incCurrentRetries();
        assertFalse(vmCreation.isRetryFailedVms());
    }

    /// After resetting, failed VMs must be retried again.
    /// @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/579">Issue #579</a>
    @Test
    void resetCurrentRetriesEnablesRetryingFailedVmsAgain() {
        final var vmCreation = new VmCreation(RETRY_DELAY, MAX_RETRIES);
        incRetries(vmCreation, MAX_RETRIES);

        vmCreation.resetCurrentRetries();
        assertEquals(0, vmCreation.getRetries());
        assertTrue(vmCreation.isRetryFailedVms());
    }

    @Test
    void resetCurrentRetriesWithDefaultValuesEnablesRetryingFailedVms() {
        final var vmCreation = new VmCreation();
        vmCreation.resetCurrentRetries();
        assertTrue(vmCreation.isRetryFailedVms());
    }

    private static void incRetries(final VmCreation vmCreation, final int times) {
        for (int i = 0; i < times; i++) {
            vmCreation.incCurrentRetries();
        }
    }
}
