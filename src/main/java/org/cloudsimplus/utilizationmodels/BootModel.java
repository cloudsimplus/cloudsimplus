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
