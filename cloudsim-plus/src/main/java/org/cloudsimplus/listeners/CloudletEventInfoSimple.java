package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

/**
 * A basic implementation of the {@link CloudletEventInfo} interface.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class CloudletEventInfoSimple extends EventInfoAbstract implements CloudletEventInfo {

    private Cloudlet cloudlet;

    /**
     * Create an EventInfo with the given parameters.
     *
     * @param time the time the event was fired
     * @param cloudlet the Cloudlet that fired the event
     */
    public CloudletEventInfoSimple(double time, Cloudlet cloudlet) {
        super(time);
        setCloudlet(cloudlet);
    }

    @Override
    public Cloudlet getCloudlet() {
        return cloudlet;
    }

    @Override
    public final void setCloudlet(Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
    }

}
