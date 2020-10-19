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
 * Periodically measures the current power usage of one or more {@link PowerAware} entities,
 * according to a defined {@link #getMeasurementInterval() interval}, storing the results.
 *
 * @see #getPowerMeasurements()
 * @since CloudSim Plus 6.0.0
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
    public PowerMeter(final Simulation simulation, final Callable<List<? extends PowerAware<? extends PowerModel>>> powerAwareEntitiesFn) {
        super(simulation);
        this.powerAwareEntitiesFn = powerAwareEntitiesFn;
        this.startTime = 0;
    }

    /**
     * Initializes a {@link PowerMeter} with a list of {@link PowerAware} entities.
     */
    public PowerMeter(final Simulation simulation, final List<? extends PowerAware<? extends PowerModel>> powerAwareEntities) {
        this(simulation, () -> powerAwareEntities);
    }

    /**
     * Initializes a {@link PowerMeter} with a single {@link PowerAware} entity.
     */
    public PowerMeter(final Simulation simulation, final PowerAware<? extends PowerModel> powerAwareEntity) {
        this(simulation, new ArrayList<>(Arrays.asList(powerAwareEntity)));
    }

    @Override
    protected void startEntity() {
        schedule(this, startTime, POWER_MEASUREMENT);
    }

    @Override
    public void processEvent(final SimEvent evt) {
        switch (evt.getTag()) {
            case POWER_MEASUREMENT:
                measurePowerConsumption();
                break;
            case CloudSimTags.END_OF_SIMULATION:
                this.shutdownEntity();
                break;
            default:
                throw new RuntimeException("Unknown Event: " + evt);
        }
    }

    private void measurePowerConsumption() {
        try {
            final List<? extends PowerAware<? extends PowerModel>> powerAwareEntities = powerAwareEntitiesFn.call();
            final PowerMeasurement measurement =
                powerAwareEntities.stream()
                                  .map(PowerAware::getPowerModel)
                                  .map(PowerModel::getPowerMeasurement)
                                  .reduce(PowerMeasurement::add)
                                  .orElse(new PowerMeasurement());

            powerMeasurements.add(measurement);
        } catch (Exception e) {
            // call() may raise, in this case we append an empty measurement
            e.printStackTrace();
            powerMeasurements.add(new PowerMeasurement());
        }

        schedule(this, measurementInterval, POWER_MEASUREMENT);
    }

    @Override
    public PowerMeter setName(final String name) {
        super.setName(name);
        return this;
    }

    /**
     * Gets the list of all measurements collected up to now.
     * @return
     */
    public List<PowerMeasurement> getPowerMeasurements() {
        return powerMeasurements;
    }

    /**
     * Gets the time interval to collect power measurements.
     * @return
     */
    public double getMeasurementInterval() {
        return measurementInterval;
    }

    /**
     * Sets the time interval to collect power measurements.
     * @param measurementInterval the value to set
     * @return
     */
    public void setMeasurementInterval(final double measurementInterval) {
        if(measurementInterval <= 0){
            throw new IllegalArgumentException("measurementInterval must be a positive number.");
        }

        this.measurementInterval = measurementInterval;
    }

    /**
     * Gets the time the power meter started to collect measurements.
     * @return
     */
    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(final double startTime) {
        this.startTime = startTime;
    }
}
