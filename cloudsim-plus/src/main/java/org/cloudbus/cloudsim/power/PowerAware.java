package org.cloudbus.cloudsim.power;


import org.cloudbus.cloudsim.power.models.PowerModel;

/**
 * Interface for power-aware entities such as hosts or data centers
 */
public interface PowerAware<T extends PowerModel> {

    T getPowerModel();

    void setPowerModel(T powerModel);

}
