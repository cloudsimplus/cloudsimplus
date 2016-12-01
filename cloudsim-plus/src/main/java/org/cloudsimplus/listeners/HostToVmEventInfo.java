package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An {@link EventInfo} class that stores data to be passed
 * to Vm's {@link EventListener} objects that are registered to be notified
 * about events of a Vm that happened inside a given Host.
 * So it represents the data of the notification passed from a Host to a Vm.
 *
 * This class can be used
 * to notify observers when a Host is {@link Vm#getOnHostAllocationListener() allocated} or
 * {@link Vm#getOnHostDeallocationListener() deallocated} to a given Vm,
 * when a Vm has its {@link Vm#getOnUpdateVmProcessingListener() processing updated by its Host},
 * etc.
 *
 * @see Vm#getOnHostAllocationListener()
 * @see Vm#getOnHostDeallocationListener()
 * @see Vm#getOnUpdateVmProcessingListener()
 * @author Manoel Campos da Silva Filho
 */
public class HostToVmEventInfo extends VmEventInfoSimple implements HostEventInfo {
    private Host host;

    /**
     * Creates a EventInfo with the given parameters.
     *
     * @param time the time the event was generated
     * @param host host where the Vm is placed
     * @param vm Vm that fired the event
     * @todo probably the Host is redundant, since now there is a host
     * attribute inside the Vm
     */
    public HostToVmEventInfo(double time, Host host, Vm vm) {
        super(time, vm);
        setHost(host);
    }

    @Override
    public Host getHost() {
        return host;
    }

    @Override
    public final void setHost(Host host) {
        this.host = host;
    }
}
