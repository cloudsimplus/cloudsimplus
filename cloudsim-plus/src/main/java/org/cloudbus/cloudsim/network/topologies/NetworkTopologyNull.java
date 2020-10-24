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
    @Override public void mapNode(SimEntity entity, int briteID) {/**/}
    @Override public void unmapNode(SimEntity entity) {/**/}
    @Override public double getDelay(SimEntity src, SimEntity dest) {
        return 0;
    }
    @Override public boolean isNetworkEnabled() {
        return false;
    }
    @Override public TopologicalGraph getTopologicalGraph() { return GRAPH; }
}
