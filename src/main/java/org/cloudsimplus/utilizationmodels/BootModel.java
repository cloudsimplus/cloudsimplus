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

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.core.Machine;

/**
 * Defines how a {@link Machine} uses resources
 * such as CPU and RAM during the boot process.
 * The default constructor doesn't set any utilization model.
 * You need to set a model for the resources you wish by calling the respective setter.
 * If you don't call any setter, the boot process won't use any resources,
 * indicating there is no overhead during the machine boot (which is not accurate).
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 8.3.0
 */
@Setter @NoArgsConstructor
public class BootModel {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link BootModel} objects.
     */
    public static final BootModel NULL = new BootModel();

    /**
     * The utilization model which defines how the machine uses
     * CPU during the boot process.
     */
    @NonNull
    private UtilizationModel utilizationModelCpu = UtilizationModel.NULL;

    /**
     * The utilization model which defines how the machine uses
     * RAM during the boot process.
     */
    @NonNull
    private UtilizationModel utilizationModelRam = UtilizationModel.NULL;

    /**
     * Creates a BootModel object for a Vm, setting the same {@link UtilizationModel} for CPU and RAM.
     * @param utilizationModel the utilization model to set for VM CPU and RAM resources
     * @see BootModel#BootModel(UtilizationModel, UtilizationModel)
     */
    public BootModel(final @NonNull UtilizationModel utilizationModel) {
        this(utilizationModel, utilizationModel);
    }

    /**
     * Creates a BootModel object for a Vm, setting specific {@link UtilizationModel} for CPU and RAM.
     * @param cpuModel the utilization model to set for CPU
     * @param ramModel the utilization model to set for RAM
     * @see BootModel#BootModel(UtilizationModel)
     */
    public BootModel(@NonNull UtilizationModel cpuModel, @NonNull UtilizationModel ramModel) {
        this.utilizationModelCpu = cpuModel;
        this.utilizationModelRam = ramModel;
    }

    /**
     * @return [0..1] to indicate the VM boot CPU utilization percentage for the current time.
     */
    public double getCpuPercentUtilization(){
        return utilizationModelCpu.getUtilization();
    }

    /**
     * @return [0..1] to indicate the VM boot RAM utilization percentage for the current time.
     */
    public double getRamPercentUtilization(){
        return utilizationModelRam.getUtilization();
    }
}
