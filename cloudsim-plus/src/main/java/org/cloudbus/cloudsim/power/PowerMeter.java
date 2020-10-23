package org.cloudbus.cloudsim.power;

import org.cloudbus.cloudsim.core.CloudSimEntity;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.power.models.PowerModel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static org.cloudbus.cloudsim.core.CloudSimTags.POWER_MEASUREMENT;

/**
 * Periodically measures the current power usage of one or more {@link PowerAware} entities,
 * according to a defined {@link #getMeasurementInterval() interval}, storing the results.
 *
 * @see #getPowerMeasurements()
 * @since CloudSim Plus 6.0.0
 */
public class PowerMeter extends CloudSimEntity {

    private final Supplier<List<? extends PowerAware<? extends PowerModel>>> powerAwareEntitiesSupplier;

    /** @see #getMeasurementInterval() */
    private double measurementInterval;

    /** @see #getPowerMeasurements() () */
    private final List<PowerMeasurement> powerMeasurements = new LinkedList<>();

    /**
     * Initializes a {@link PowerMeter} to periodically measure power consumption of a single {@link PowerAware} entity.
     * @param simulation The simulation instance the Entity is related to
     * @param powerAwareEntity an entity to have its power consumption measured
     */
    public PowerMeter(final Simulation simulation, final PowerAware<? extends PowerModel> powerAwareEntity) {
        this(simulation, Arrays.asList(powerAwareEntity));
    }

    /**
     * Initializes a {@link PowerMeter} to periodically measure the
     * combined power consumption of a list of {@link PowerAware} entities.
     *
     * <p>If you want to compute power consumption individually for each entity,
     * check {@link #PowerMeter(Simulation, PowerAware)}.</p>
     * @param simulation The simulation instance the Entity is related to
     * @param powerAwareEntities a list of entities to have their combined power consumption measured
     */
    public PowerMeter(final Simulation simulation, final List<? extends PowerAware<? extends PowerModel>> powerAwareEntities) {
        this(simulation, () -> powerAwareEntities);
    }

    /**
     * Initializes a {@link PowerMeter} with a function that supplies a list of {@link PowerAware} entities
     * to have their combined power consumption periodically measured.
     * This is useful if the list of entities varies during the simulation run.
     *
     * <p>If you want to compute power consumption individually for each entity,
     * check {@link #PowerMeter(Simulation, PowerAware)}.</p>
     * @param simulation The simulation instance the Entity is related to
     * @param powerAwareEntitiesSupplier a {@link Supplier} that provides a list of entities
     *                                   to have their combined power consumption measured
     */
    public PowerMeter(final Simulation simulation, final Supplier<List<? extends PowerAware<? extends PowerModel>>> powerAwareEntitiesSupplier) {
        super(simulation);
        this.powerAwareEntitiesSupplier = Objects.requireNonNull(powerAwareEntitiesSupplier, "powerAwareEntitiesSupplier cannot be null");
    }

    @Override
    protected void startEntity() {
        schedule(POWER_MEASUREMENT);
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

    /**
     * Measures the power consumption of entities.
     * If the entities list contains a single element,
     * the measurement is related to that entity.
     * If the list has multiples entities,
     * it's returned the combined power consumption of such entities.
     */
    private void measurePowerConsumption() {
        final List<? extends PowerAware<? extends PowerModel>> powerAwareEntities = powerAwareEntitiesSupplier.get();
        final PowerMeasurement measurement = powerAwareEntities.stream()
            .map(PowerAware::getPowerModel)
            .map(PowerModel::getPowerMeasurement)
            .reduce(PowerMeasurement::add)
            .orElse(new PowerMeasurement());
        powerMeasurements.add(measurement);
        schedule(this, measurementInterval, POWER_MEASUREMENT);
    }

    @Override
    public PowerMeter setName(final String name) {
        super.setName(name);
        return this;
    }

    /**
     * Gets the list of all measurements collected up to now.
     * Each entry is a measurement collected in the defined {@link #measurementInterval}.
     * If you provided a list of entities on the class constructor,
     * a entry is the combined measurement for those entities.
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
}
