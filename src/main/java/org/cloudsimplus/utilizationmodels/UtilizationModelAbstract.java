/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.utilizationmodels;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CustomerEntity;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.vms.Vm;
import org.jspecify.annotations.Nullable;

/// An abstract class to implement [UtilizationModel]s.
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.2
@Accessors @Getter
public abstract non-sealed class UtilizationModelAbstract implements UtilizationModel {
    /// Indicates that values lower or equal to this will be considered as zero.
    /// This constant is used to compare utilization values to avoid floating point precision issues.
    public static final double ALMOST_ZERO = 0.0000000001;

    @Setter @NonNull
    private Simulation simulation;

    private Unit unit;

    @Setter
    private boolean overCapacityRequestAllowed;

    /// The first [Vm] or [Cloudlet] using this model,
    /// or null if none has registered yet.
    @Getter(AccessLevel.NONE)
    private @Nullable CustomerEntity owner;

    public UtilizationModelAbstract(){
        this(Unit.PERCENTAGE);
    }

    public UtilizationModelAbstract(@NonNull final Unit unit){
        this.simulation = Simulation.NULL;
        this.setUnit(unit);
    }

    /// Sets the [Unit] in which the resource utilization is defined.
    /// @param unit [Unit] to set
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

    /// {@inheritDoc}
    /// It returns false by default. A subclass that keeps a state which changes along the simulation
    /// must override it, so that the model can't be shared by many VMs or Cloudlets.
    @Override
    public boolean isStateful() {
        return false;
    }

    @Override
    public void validateOwner(@NonNull final CustomerEntity owner) {
        if (this.owner != null && this.owner != owner && isStateful()) {
            throw new IllegalStateException(
                "%s is stateful and already used by %s, so it can't be shared with %s. Create one instance for each Vm or Cloudlet, or use a stateless model."
                    .formatted(getClass().getSimpleName(), describe(this.owner), describe(owner)));
        }
    }

    @Override
    public UtilizationModel registerOwner(@NonNull final CustomerEntity owner) {
        validateOwner(owner);
        if (this.owner == null) {
            this.owner = owner;
        }

        return this;
    }

    /// Ensures a model already used by a VM or Cloudlet doesn't become [stateful][#isStateful()],
    /// since it may be shared by many of them. Must be called by setters that make the model stateful.
    /// @param change the change that makes the model stateful, to show in the error message
    /// @throws IllegalStateException if a VM or Cloudlet already uses the model
    protected final void requireNotInUse(final String change) {
        if (owner != null) {
            throw new IllegalStateException(
                "%s is already used by %s, so %s isn't allowed, since it makes the model stateful. Do it before giving the model to a Vm or Cloudlet."
                    .formatted(getClass().getSimpleName(), describe(owner), change));
        }
    }

    /// {@return the type and id of a VM or Cloudlet, or just the type with an article if the id isn't set yet}
    private static String describe(final CustomerEntity entity) {
        final String type = entity instanceof Vm ? "Vm" : "Cloudlet";
        return entity.getId() < 0 ? "a " + type : type + " " + entity.getId();
    }

    /// Checks if a given field has a valid value, considering that the minimum value is zero.
    /// @param fieldName the name of the field to display at the Exception when the value is invalid
    /// @param fieldValue the current value of the field
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
