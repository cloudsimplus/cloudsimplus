package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;

/**
 * An interface that represents data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * when a {@link CloudletScheduler} <b>is not able to allocated the amount of resource a {@link Cloudlet}
 * is requesting due to lack of available capacity</b>.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 5.4.3
 */
public interface CloudletResourceAllocationFailEventInfo extends CloudletEventInfo {
    /**
     * The the class of the resource the Cloudlet is requesting.
     * @return
     */
    Class<? extends ResourceManageable> getResourceClass();

    /**
     * The amount of resources which is being requested
     * and which is not currently available.
     * The unit depends on the type of the {@link #getResourceClass()} resource}.
     * @return
     */
    long getRequestedAmount();

    /**
     * The amount of resource amount that was available before allocating for the Cloudlet.
     * The unit depends on the type of the {@link #getResourceClass()} resource}.
     * @return
     */
    long getAvailableAmount();

    @Override
    EventListener<CloudletResourceAllocationFailEventInfo> getListener();

    /**
     * Gets a EventInfo instance from the given parameters.
     *
     * @param listener the listener to be notified about the event
     * @param cloudlet the Cloudlet requesting the resource
     * @param resourceClass the class of the resource the Cloudlet is requesting
     * @param requestedAmount the requested resource amount (the unit depends on the resource requested)
     * @param availableAmount the amount of resource amount that was available before allocating
     *                        for the Cloudlet (the unit depends on the resource requested)
     * @param time the time the event happened
     * @return
     */
    static CloudletResourceAllocationFailEventInfo of(
        final EventListener<CloudletResourceAllocationFailEventInfo> listener,
        final Cloudlet cloudlet,
        final Class<? extends ResourceManageable> resourceClass,
        final long requestedAmount,
        final long availableAmount,
        final double time)
    {
        return new CloudletResourceAllocationFailEventInfo() {
            @Override public EventListener<CloudletResourceAllocationFailEventInfo> getListener() { return listener; }
            @Override public Cloudlet getCloudlet() { return cloudlet; }
            @Override public Class<? extends ResourceManageable> getResourceClass() { return resourceClass; }
            @Override public long getRequestedAmount() { return requestedAmount; }
            @Override public long getAvailableAmount() { return availableAmount; }
            @Override public double getTime() { return time; }
        };
    }
}
