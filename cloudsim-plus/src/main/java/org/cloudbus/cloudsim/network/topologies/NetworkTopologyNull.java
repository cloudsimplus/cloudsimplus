package org.cloudbus.cloudsim.network.topologies;

import org.cloudbus.cloudsim.core.SimEntity;

/**
 * A class that implements the Null Object Design Pattern for {@link NetworkTopology}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see NetworkTopology#NULL
 */
final class NetworkTopologyNull implements NetworkTopology {
    @Override public void addLink(SimEntity src, SimEntity dest, double bandwidth, double lat) {/**/}
    @Override public double getDelay(SimEntity src, SimEntity dest) {
        return 0;
    }
}
