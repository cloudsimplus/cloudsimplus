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

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelAbstract;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
public class CloudletTest {

    @Test
    public void testIsBoundedToVm() {
        final Cloudlet cloudlet = createCloudlet(0);
        assertFalse(cloudlet.isBoundToVm());
        cloudlet.setVm(Vm.NULL);
        assertFalse(cloudlet.isBoundToVm());

        cloudlet.setVm(VmTestUtil.createVm(0, 1));
        assertFalse(cloudlet.isBoundToVm());

        cloudlet.setVm(VmTestUtil.createVm(1, 1, cloudlet.getBroker()));
        assertTrue(cloudlet.isBoundToVm());

        cloudlet.setVm(VmTestUtil.createVm(2, 1, cloudlet.getBroker()));
        assertTrue(cloudlet.isBoundToVm());
    }

    private static CloudletSimple createCloudlet(int id) {
        return new CloudletSimple(id, 1, 1);
    }

    @Test
    public void testNullObject(){
        assertFalse(Cloudlet.NULL.addRequiredFile(""));
        assertFalse(Cloudlet.NULL.deleteRequiredFile(""));
        assertEquals(0, Cloudlet.NULL.getPriority());
        assertEquals(0, Cloudlet.NULL.getFileSize());
        assertEquals(-1, Cloudlet.NULL.getId());
        assertEquals(0, Cloudlet.NULL.getLength());
        assertEquals(0, Cloudlet.NULL.getOutputSize());
        assertEquals(0, Cloudlet.NULL.getTotalLength());

        assertEquals(0, Cloudlet.NULL.getNetServiceLevel());
        assertEquals(0, Cloudlet.NULL.getRequiredCores());

        assertSame(DatacenterBroker.NULL, Cloudlet.NULL.getBroker());

        assertEquals(Vm.NULL, Cloudlet.NULL.getVm());
        assertFalse(Cloudlet.NULL.hasRequiresFiles());
        assertFalse(Cloudlet.NULL.listeners().removeOnFinish(null));
        final EventListener listener = Mockito.mock(EventListener.class);
        Cloudlet.NULL.listeners().addOnFinish(listener);

        assertFalse(Cloudlet.NULL.isBoundToVm());
    }

    @Test
    public void testNullObjectStatus(){
        assertEquals(Cloudlet.Status.FAILED, Cloudlet.NULL.getStatus());
        assertTrue(Cloudlet.NULL.getRequiredFiles().isEmpty());
        assertEquals(0, Cloudlet.NULL.getFinishedLengthSoFar());
        assertFalse(Cloudlet.NULL.isFinished());

    }

    @Test
    public void testNullObjectTimes(){
        assertAll(
            () -> assertEquals(-1, Cloudlet.NULL.getDcArrivalTime()),
            () -> assertEquals(0, Cloudlet.NULL.getTotalExecutionTime()),
            () -> assertEquals(0, Cloudlet.NULL.getStartTime()),
            () -> assertEquals(0, Cloudlet.NULL.getStartWaitTime()),
            () -> Cloudlet.NULL.setSubmissionDelay(10),
            () -> assertEquals(0, Cloudlet.NULL.getSubmissionDelay())
        );
    }

    @Test
    public void testNullObjectUtilization1() {
        assertAll(
            () -> assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelBw()),
            () -> assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelCpu()),
            () -> assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelRam()),
            () -> assertEquals(0, Cloudlet.NULL.getUtilizationOfBw(0)),
            () -> assertEquals(0, Cloudlet.NULL.getUtilizationOfCpu(0)),
            () -> assertEquals(0, Cloudlet.NULL.getUtilizationOfRam(0))
        );
    }

    @Test
    public void testNullObjectUtilization2(){
        final UtilizationModel model = Mockito.mock(UtilizationModelAbstract.class);
        Cloudlet.NULL.setUtilizationModelBw(model);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelBw());

        Cloudlet.NULL.setUtilizationModelCpu(model);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelCpu());

        Cloudlet.NULL.setUtilizationModelRam(model);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelRam());
    }
}
