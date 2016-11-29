package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An {@link EventInfo} class that stores data to be passed
 * to Vm's {@link EventListener} objects that are registered to be notified
 * about events of a Vm that happened inside a given Datacenter.
 * So it represents the data of the notification passed from a Datacenter to a Vm.
 *
 * @see Vm#getOnVmCreationFailureListener()
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterToVmEventInfo extends VmEventInfoSimple implements DatacenterEventInfo {
    private Datacenter datacenter;

    /**
     * Creates an EventInfo with the given parameters.
     *
     * @param time time when the event was fired
     * @param datacenter Datacenter where the Vm is placed
     * @param vm Vm that fired the event
     */
    public DatacenterToVmEventInfo(double time, Datacenter datacenter, Vm vm) {
        super(time, vm);
        setDatacenter(datacenter);
    }

    /**
     *
     * @return the Datacenter that caused the Vm event
     */
    @Override
    public Datacenter getDatacenter() {
        return datacenter;
    }

    /**
     * Sets the Datacenter that caused the Vm event
     * @param datacenter
     */
    @Override
    public final void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
