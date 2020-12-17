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
    private static final TopologicalGraph GRAPH = new TopologicalGraph();

    @Override public void addLink(SimEntity src, SimEntity dest, double bandwidth, double lat) {/**/}
    @Override public void removeLink(SimEntity src, SimEntity dest) {/**/}
    @Override public double getDelay(SimEntity src, SimEntity dest) {
        return 0;
    }
}
