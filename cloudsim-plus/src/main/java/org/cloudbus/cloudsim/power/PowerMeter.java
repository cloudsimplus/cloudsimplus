package org.cloudbus.cloudsim.power;

import org.cloudbus.cloudsim.core.CloudSimEntity;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.power.models.PowerModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.cloudbus.cloudsim.core.CloudSimTags.POWER_MEASUREMENT;

/**
 * Periodically measures the current power usage of one or more {@link PowerAware} entities and stores the results.
 */
public class PowerMeter extends CloudSimEntity {

    private final Callable<List<? extends PowerAware<? extends PowerModel>>> powerAwareEntitiesFn;

    private double measurementInterval;
    private double startTime;

    private final List<PowerMeasurement> powerMeasurements = new LinkedList<>();

    /**
     * Initializes a {@link PowerMeter} with a function that returns a list of {@link PowerAware} entities on every measurement.
     *
     * This is useful if the list of entities varies during a simulation run.
     */
    public PowerMeter(Simulation simulation, Callable<List<? extends PowerAware<? extends PowerModel>>> powerAwareEntitiesFn) {
        super(simulation);
        this.powerAwareEntitiesFn = powerAwareEntitiesFn;
        this.startTime = 0;
    }

    /**
     * Initializes a {@link PowerMeter} with a list of {@link PowerAware} entities.
     */
    public PowerMeter(Simulation simulation, List<? extends PowerAware<? extends PowerModel>> powerAwareEntities) {
        this(simulation, () -> powerAwareEntities);
    }

    /**
     * Initializes a {@link PowerMeter} with a single {@link PowerAware} entity.
     */
    public PowerMeter(Simulation simulation, PowerAware<? extends PowerModel> powerAwareEntity) {
        this(simulation, new ArrayList<>(Arrays.asList(powerAwareEntity)));
    }

    @Override
    protected void startEntity() {
        schedule(this, startTime, POWER_MEASUREMENT);
    }

    @Override
    public void processEvent(SimEvent evt) {
        switch (evt.getTag()) {
            case POWER_MEASUREMENT:
                List<? extends PowerAware<? extends PowerModel>> powerAwareEntities;
                try {
                    powerAwareEntities = powerAwareEntitiesFn.call();
                    powerMeasurements.add(powerAwareEntities.stream()
                        .map(PowerAware::getPowerModel)
                        .map(PowerModel::getPowerMeasurement)
                        .reduce(PowerMeasurement::add)
                        .orElse(new PowerMeasurement()));
                } catch (Exception e) {
                    // call() may raise, in this case we append an empty measurement
                    e.printStackTrace();
                    powerMeasurements.add(new PowerMeasurement());
                }

                schedule(this, measurementInterval, POWER_MEASUREMENT);
                break;
            case CloudSimTags.END_OF_SIMULATION:
                this.shutdownEntity();
                break;
            default:
                throw new RuntimeException("Unknown Event: " + evt);
        }
    }

    @Override
    public PowerMeter setName(String name) {
        super.setName(name);
        return this;
    }

    public List<PowerMeasurement> getPowerMeasurements() {
        return powerMeasurements;
    }

    public double getMeasurementInterval() {
        return measurementInterval;
    }

    public void setMeasurementInterval(double measurementInterval) {
        this.measurementInterval = measurementInterval;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }
}
