package org.cloudbus.cloudsim.network;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.Identifiable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.network.switches.Switch;

/**
 * An interface to identify Network Assets on the simulation.
 * Those assets can be either
 * (i) physical, such as {@link Datacenter}s, {@link Switch}s and {@link Host}s;
 * (ii) or logical, such as {@link DatacenterBroker}s.
 * This physical and logical assets pave the way for providing
 * Network Function Virtualization (NFV).
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.3.5
 */
public interface NetworkAsset extends Identifiable {
}
