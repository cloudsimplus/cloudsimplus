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

import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;

/// An utility class used by Cloudlet tests.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
public final class CloudletTestUtil {
    /* default */ static final long CLOUDLET_LENGTH = 1000;
    /* default */ static final long CLOUDLET_FILE_SIZE = 1000;
    /* default */ static final int  CLOUDLET_OUTPUT_SIZE = 1000;

    /// A private constructor to avoid class instantiation.
    private CloudletTestUtil(){/**/}

    /* default */ static CloudletSimple createCloudlet() {
        return createCloudlet(0);
    }

    /* default */ static CloudletSimple createCloudlet(final int id) {
        return createCloudlet(id, new UtilizationModelFull());
    }

    /* default */ static CloudletSimple createCloudlet(final int id, UtilizationModel cpuRamAndBwUtilizationModel)
    {
        return createCloudlet(id, cpuRamAndBwUtilizationModel,
                cpuRamAndBwUtilizationModel,
                cpuRamAndBwUtilizationModel);
    }

    public static CloudletSimple createCloudlet(final int id, long length, int pesNumber)
    {
        final UtilizationModel model = new UtilizationModelFull();
        return createCloudlet(id, model, model, model, length, pesNumber);
    }

    private static CloudletSimple createCloudlet(
         final int id,
         final UtilizationModel utilizationModelCPU,
         final UtilizationModel utilizationModelRAM,
         final UtilizationModel utilizationModelBW)
    {
        return createCloudlet(
                id, utilizationModelCPU, utilizationModelRAM, utilizationModelBW,
                CLOUDLET_LENGTH, 1);
    }

    public static CloudletSimple createCloudlet(final int id, final int pesNumber) {
        return createCloudlet(id, CLOUDLET_LENGTH, pesNumber);
    }

    private static CloudletSimple createCloudlet(
        final int id,
        final UtilizationModel utilizationModelCPU,
        final UtilizationModel utilizationModelRAM,
        final UtilizationModel utilizationModelBW,
        final long length, final int pesNumber)
    {
        final CloudletSimple cloudlet = new CloudletSimple(id, length, pesNumber);

        cloudlet
            .setFileSize(CLOUDLET_FILE_SIZE)
            .setOutputSize(CLOUDLET_OUTPUT_SIZE);
        cloudlet.setUtilizationModelCpu(utilizationModelCPU);
        cloudlet.setUtilizationModelRam(utilizationModelRAM);
        cloudlet.setUtilizationModelBw(utilizationModelBW);
        return cloudlet;
    }

    /// Creates a Cloudlet with id equals to 0.
    ///
    /// @param length the length of the Cloudlet to create
    /// @param pesNumber the number of PEs of the Cloudlet to create
    /// @return the created Cloudlet
    public static CloudletSimple createCloudlet0(final long length, final int pesNumber) {
        return createCloudlet(0, length, pesNumber);
    }

    public static CloudletSimple createCloudletWithOnePe(final int id) {
        return createCloudlet(id, CLOUDLET_LENGTH, 1);
    }
}
