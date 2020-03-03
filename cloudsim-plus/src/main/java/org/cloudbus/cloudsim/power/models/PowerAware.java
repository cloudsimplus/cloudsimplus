package org.cloudbus.cloudsim.power.models;


/**
 * Interface for power-aware entities such as hosts or data centers
 */
public interface PowerAware<T extends PowerModel> {

    T getPowerModel();

    void setPowerModel(T powerModel);

}
