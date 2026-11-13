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
package org.cloudsimplus.utilizationmodels;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// Checks that [stateful][UtilizationModel#isStateful()] models can't be shared by many VMs or Cloudlets.
/// @author Manoel Campos da Silva Filho
class StatefulUtilizationModelTest {
    private static final double INITIAL_UTILIZATION = 0.1;

    private static UtilizationModelDynamic dynamicWithUpdateFunction() {
        return new UtilizationModelDynamic(INITIAL_UTILIZATION).setUtilizationUpdateFunction(um -> um.getUtilization() + 0.1);
    }

    private static Cloudlet cloudlet(final int id) {
        return CloudletSimple.of(id, 1000, 1);
    }

    private static Vm vm(final int id) {
        return VmSimple.of(id, new CoreCapacity(1, 1000));
    }

    @Test
    void stateless() {
        assertAll(
            () -> assertFalse(UtilizationModel.NULL.isStateful()),
            () -> assertFalse(UtilizationModel.FULL.isStateful()),
            () -> assertFalse(new UtilizationModelFull().isStateful()),
            () -> assertFalse(new UtilizationModelDynamic(INITIAL_UTILIZATION).isStateful()),
            () -> assertFalse(new UtilizationModelStochastic().setAlwaysGenNewRandUtilization(true).isStateful())
        );
    }

    @Test
    void stateful() {
        assertAll(
            () -> assertTrue(dynamicWithUpdateFunction().isStateful()),
            () -> assertTrue(new UtilizationModelStochastic().isStateful())
        );
    }

    @Test
    void statelessModelSharedByCloudletsAndVms() {
        final var model = new UtilizationModelDynamic(INITIAL_UTILIZATION);
        cloudlet(0).setUtilizationModel(model);
        cloudlet(1).setUtilizationModel(model);
        assertDoesNotThrow(() -> vm(0).setBootModel(new BootModel(model)));
    }

    @Test
    void statefulModelUsedByOneCloudletForAllResources() {
        assertDoesNotThrow(() -> cloudlet(0).setUtilizationModel(dynamicWithUpdateFunction()));
    }

    @Test
    void statefulModelSharedByCloudlets() {
        final var model = new UtilizationModelStochastic();
        cloudlet(0).setUtilizationModelCpu(model);
        final var other = cloudlet(1);
        assertThrows(IllegalStateException.class, () -> other.setUtilizationModelRam(model));
    }

    @Test
    void statefulModelSharedByVmsThroughOneBootModel() {
        final var bootModel = new BootModel(dynamicWithUpdateFunction());
        vm(0).setBootModel(bootModel);
        final var other = vm(1);
        assertThrows(IllegalStateException.class, () -> other.setBootModel(bootModel));
    }

    @Test
    void statefulModelSharedByVmsThroughDistinctBootModels() {
        final var model = dynamicWithUpdateFunction();
        vm(0).setBootModel(new BootModel(model));
        final var other = vm(1);
        assertThrows(IllegalStateException.class, () -> other.setBootModel(new BootModel(model, UtilizationModel.NULL)));
    }

    @Test
    void copyOfStatefulModelUsedByAnotherCloudlet() {
        final var model = dynamicWithUpdateFunction();
        cloudlet(0).setUtilizationModelCpu(model);
        final var copy = new UtilizationModelDynamic(model, INITIAL_UTILIZATION);
        assertTrue(copy.isStateful());
        assertDoesNotThrow(() -> cloudlet(1).setUtilizationModelCpu(copy));
    }

    @Test
    void setUpdateFunctionBeforeModelIsInUse() {
        final var model = new UtilizationModelDynamic(INITIAL_UTILIZATION);
        assertDoesNotThrow(() -> model.setUtilizationUpdateFunction(um -> um.getUtilization() + 0.1));
    }

    @Test
    void setUpdateFunctionAfterSharedModelIsInUse() {
        final var model = new UtilizationModelDynamic(INITIAL_UTILIZATION);
        cloudlet(0).setUtilizationModelCpu(model);
        cloudlet(1).setUtilizationModelCpu(model);
        assertThrows(IllegalStateException.class, () -> model.setUtilizationUpdateFunction(um -> um.getUtilization() + 0.1));
    }

    @Test
    void setUpdateFunctionAfterBootModelIsInUse() {
        final var model = new UtilizationModelDynamic(INITIAL_UTILIZATION);
        vm(0).setBootModel(new BootModel(model));
        assertThrows(IllegalStateException.class, () -> model.setUtilizationUpdateFunction(um -> um.getUtilization() + 0.1));
    }

    @Test
    void disableAlwaysGenNewRandUtilizationAfterModelIsInUse() {
        final var model = new UtilizationModelStochastic().setAlwaysGenNewRandUtilization(true);
        cloudlet(0).setUtilizationModelCpu(model);
        assertAll(
            () -> assertDoesNotThrow(() -> model.setAlwaysGenNewRandUtilization(true)),
            () -> assertThrows(IllegalStateException.class, () -> model.setAlwaysGenNewRandUtilization(false))
        );
    }

    @Test
    void disableAlwaysGenNewRandUtilizationWhenAlreadyDisabled() {
        final var model = new UtilizationModelStochastic();
        cloudlet(0).setUtilizationModelCpu(model);
        assertDoesNotThrow(() -> model.setAlwaysGenNewRandUtilization(false));
    }

    @Test
    void failedBootModelAssignmentDoesNotRegisterAnyModel() {
        final var cpuModel = dynamicWithUpdateFunction();
        final var ramModel = dynamicWithUpdateFunction();
        vm(0).setBootModel(new BootModel(UtilizationModel.NULL, ramModel));

        final var bootModel = new BootModel(cpuModel, ramModel);
        assertThrows(IllegalStateException.class, () -> vm(1).setBootModel(bootModel));
        assertDoesNotThrow(() -> vm(2).setBootModel(new BootModel(cpuModel, UtilizationModel.NULL)));
    }
}
