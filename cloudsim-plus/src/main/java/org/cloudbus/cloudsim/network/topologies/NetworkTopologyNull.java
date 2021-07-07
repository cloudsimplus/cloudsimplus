package org.cloudbus.cloudsim.network.topologies;

import org.cloudbus.cloudsim.network.NetworkAsset;

/**
 * A class that implements the Null Object Design Pattern for {@link NetworkTopology}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see NetworkTopology#NULL
 */
final class NetworkTopologyNull implements NetworkTopology {
    private static final TopologicalGraph GRAPH = new TopologicalGraph();

    @Override public void addLink(NetworkAsset src, NetworkAsset dest, double bandwidth, double lat) {/**/}
    @Override public void removeLink(NetworkAsset src, NetworkAsset dest) {/**/}
    @Override public double getDelay(NetworkAsset src, NetworkAsset dest) {
        return 0;
    }
}
