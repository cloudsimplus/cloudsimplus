package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

/**
 * An interface to be implemented by a machine such as a {@link Host} or {@link Vm},
 * that provides a polymorphic way to access a given resource
 * like {@link Ram}, {@link Bandwidth}, {@link Storage}
 * or {@link Pe} from a List containing such different resources.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Resourceful {
    /**
     * Gets a given machine {@link Resource}, such as {@link Ram} or {@link Bandwidth},
     * from the List of machine resources.
     *
     * @param resourceClass the class of resource to get
     * @return the {@link Resource} corresponding to the given class
     */
    default ResourceManageable getResource(Class<? extends ResourceManageable> resourceClass){
        return getResources().stream()
            .filter(r -> r.isObjectSubClassOf(resourceClass))
            .findFirst()
            .orElse(ResourceManageable.NULL);
    }

    /**
     * Gets a <b>read-only</b> list of resources the machine has.
     *
     * @see #getResource(Class)
     * @return a read-only list of resources
     */
    List<ResourceManageable> getResources();
}
