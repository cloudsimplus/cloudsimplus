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
package org.cloudsimplus.power;

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.core.CloudSimEntity;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.models.PowerModel;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Periodically measures the current power usage of one or more {@link PowerAware} entities,
 * according to a defined {@link #getMeasurementInterval() interval}, storing the results.
 * PowerAware entities include {@link Host}s and {@link Datacenter}s.
 *
 * @see #getPowerMeasurements()
 * @since CloudSim Plus 6.0.0
 */
public class PowerMeter extends CloudSimEntity {

    /**
     * A {@link Supplier} that provides a list of entities to have their combined power consumption measured.
     */
    private final Supplier<List<? extends PowerAware<? extends PowerModel>>> powerAwareEntitiesSupplier;

    /**
     * The time interval to collect power measurements (in seconds).
     */
    @Getter
    private double measurementInterval = 1;

    /**
     * The list of all measurements collected up to now.
     * Each entry is a measurement collected in the defined {@link #measurementInterval}.
     * If you have provided a list of entities on the class constructor,
     * an entry is the combined measurement for those entities.
     */
    @Getter
    private final List<PowerMeasurement> powerMeasurements = new LinkedList<>();

    /**
     * Initializes a {@link PowerMeter} to periodically measure power consumption of a single {@link PowerAware} entity.
     * @param simulation the simulation instance the Entity is related to
     * @param powerAwareEntity an entity to have its power consumption measured
     */
    public PowerMeter(final Simulation simulation, final PowerAware<? extends PowerModel> powerAwareEntity) {
        this(simulation, List.of(powerAwareEntity));
    }

    /**
     * Initializes a {@link PowerMeter} to periodically measure the
     * combined power consumption of a list of {@link PowerAware} entities.
     *
     * <p>If you want to compute power consumption individually for each entity,
     * check {@link #PowerMeter(Simulation, PowerAware)}.</p>
     * @param simulation the simulation instance the Entity is related to
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
     * @param simulation the simulation instance the Entity is related to
     * @param powerAwareEntitiesSupplier a {@link Supplier} that provides a list of entities
     *                                   to have their combined power consumption measured
     */
    public PowerMeter(final Simulation simulation, @NonNull final Supplier<List<? extends PowerAware<? extends PowerModel>>> powerAwareEntitiesSupplier) {
        super(simulation);
        this.powerAwareEntitiesSupplier = powerAwareEntitiesSupplier;
    }

    @Override
    protected void startInternal() {
        schedule(CloudSimTag.POWER_MEASUREMENT);
    }

    @Override
    public void processEvent(final SimEvent evt) {
        switch (evt.getTag()) {
            case CloudSimTag.POWER_MEASUREMENT -> measurePowerConsumption();
            case CloudSimTag.SIMULATION_END -> shutdown();
            default -> throw new IllegalStateException("Unknown Event: " + evt);
        }
    }

    /**
     * Measures the power consumption of entities and schedules next measurement.
     * If the entity list contains a single element, the measurement is related to that entity.
     * If the list has multiple entities,
     * it's returned the combined power consumption of such entities.
     */
    private void measurePowerConsumption() {
        final var powerAwareEntitiesList = powerAwareEntitiesSupplier.get();
        final PowerMeasurement measurement = powerAwareEntitiesList.stream()
            .map(PowerAware::getPowerModel)
            .map(PowerModel::getPowerMeasurement)
            .reduce(PowerMeasurement::add)
            .orElse(new PowerMeasurement());
        powerMeasurements.add(measurement);
        scheduleMeasurement();
    }

    /**
     * Just re-schedule measurements if there are other events to be processed.
     * Otherwise, the simulation has finished and no more measurements should be scheduled.
     */
    private void scheduleMeasurement() {
        if (getSimulation().isThereAnyFutureEvt(evt -> evt.getTag() != CloudSimTag.POWER_MEASUREMENT)) {
            schedule(measurementInterval, CloudSimTag.POWER_MEASUREMENT);
        }
    }

    /**
     * Sets the time interval to collect power measurements.
     * @param measurementInterval the value to set (in seconds)
     * @return this object
     */
    public PowerMeter setMeasurementInterval(final double measurementInterval) {
        if(measurementInterval <= 0){
            throw new IllegalArgumentException("measurementInterval must be a positive number.");
        }

        this.measurementInterval = measurementInterval;
        return this;
    }
}
