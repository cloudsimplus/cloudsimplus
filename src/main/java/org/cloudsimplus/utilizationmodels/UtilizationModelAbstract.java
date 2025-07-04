/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.utilizationmodels;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.core.Simulation;

/**
 * An abstract class to implement {@link UtilizationModel}s.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2
 */
@Accessors @Getter
public abstract non-sealed class UtilizationModelAbstract implements UtilizationModel {
    /**
     * Indicates that values lower or equal to this will be considered as zero.
     * This constant is used to compare utilization values to avoid floating point precision issues.
     */
    public static final double ALMOST_ZERO = 0.0000000001;

    @Setter @NonNull
    private Simulation simulation;

    private Unit unit;

    @Setter
    private boolean overCapacityRequestAllowed;

    public UtilizationModelAbstract(){
        this(Unit.PERCENTAGE);
    }

    public UtilizationModelAbstract(@NonNull final Unit unit){
        this.simulation = Simulation.NULL;
        this.setUnit(unit);
    }

    /**
     * Sets the {@link Unit} in which the resource utilization is defined.
     * @param unit {@link Unit} to set
     */
    protected final void setUnit(@NonNull final Unit unit) {
        this.unit = unit;
    }

    @Override
    public final double getUtilization() {
        return getUtilization(simulation.clock());
    }

    @Override
    public final double getUtilization(final double time) {
        if (time < 0) {
            throw new IllegalArgumentException("Time cannot be negative.");
        }

        final double requested = getUtilizationInternal(time);
        return unit == Unit.ABSOLUTE || overCapacityRequestAllowed ? requested : Math.min(requested, 1);
    }

    protected abstract double getUtilizationInternal(double time);

    /**
     * Checks if a given field has a valid value, considering that the minimum value is zero.
     * @param fieldName the name of the field to display at the Exception when the value is invalid
     * @param fieldValue the current value of the field
     */
    protected void validateUtilizationField(final String fieldName, final double fieldValue) {
        validateUtilizationField(fieldName, fieldValue, 0);
    }

    protected void validateUtilizationField(final String fieldName, final double fieldValue, double minValue) {
        minValue = minValue <= ALMOST_ZERO ? 0 : minValue;
        if(fieldValue < minValue) {
            throw new IllegalArgumentException(fieldName + " cannot be lower than " + minValue);
        }
    }
}
