package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.ResourceManageable;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link Machine}
 * objects.
 *
 * @author Manoel Campos da Silva Filho
 * @see Machine#NULL
 * @since CloudSim 1.2.0
 */
final class MachineNull implements Machine {
    @Override public Resource getBw() {
        return Resource.NULL;
    }
    @Override public Resource getRam() {
        return Resource.NULL;
    }
    @Override public Resource getStorage() {
        return Resource.NULL;
    }
    @Override public long getNumberOfPes() {
        return 0;
    }
    @Override public double getMips() {
        return 0;
    }
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public void setId(long id) {/**/}
    @Override public long getId() {
        return 0;
    }
    @Override public List<ResourceManageable> getResources() {
        return Collections.emptyList();
    }

    @Override
    public double getTotalMipsCapacity() { return 0.0; }
}
