package org.cloudsimplus.provisioners;

import lombok.NonNull;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;

/// A base class for implementing [Pe] [ResourceProvisioner]s.
/// @author Manoel Campos
public abstract non-sealed class PeProvisionerAbstract extends ResourceProvisionerSimple implements PeProvisioner {
    public PeProvisionerAbstract(ResourceManageable resource, Function<Vm, ResourceManageable> vmResourceFunction) {
        super(resource, vmResourceFunction);
    }

    public void setPe(final Pe pe) {
        final var msg = "Pe already has a PeProvisioner assigned to it. Each Pe must have its own PeProvisioner instance.";
        if (isOtherProvisionerAssignedToPe(pe)) {
            throw new IllegalArgumentException(msg);
        }
        setResources(pe, Vm::getProcessor);
    }

    /**
     * Checks if the {@link Pe} has a {@link PeProvisioner} assigned that is
     * different from the current one.
     *
     * @param pe the Pe to check
     * @return {@code true} if the Pe has a PeProvisioner different then the current one,
     *         {@code false} otherwise.
     */
    private boolean isOtherProvisionerAssignedToPe(@NonNull final Pe pe) {
        return pe.getPeProvisioner() != null &&
            pe.getPeProvisioner() != PeProvisioner.NULL &&
            !pe.getPeProvisioner().equals(this);
    }
}
