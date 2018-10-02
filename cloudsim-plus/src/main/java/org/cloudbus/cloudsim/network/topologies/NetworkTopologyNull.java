package org.cloudbus.cloudsim.network.topologies;

/**
 * A class that implements the Null Object Design Pattern for {@link NetworkTopology}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see NetworkTopology#NULL
 */
final class NetworkTopologyNull implements NetworkTopology {
    private static final TopologicalGraph GRAPH = new TopologicalGraph();

    @Override public void addLink(long srcId, long destId, double bandwidth, double lat) {/**/}
    @Override public void mapNode(long cloudSimEntityID, int briteID) {/**/}
    @Override public void unmapNode(long cloudSimEntityID) {/**/}
    @Override public double getDelay(long srcID, long destID) {
        return 0;
    }
    @Override public boolean isNetworkEnabled() {
        return false;
    }
    @Override public TopologicalGraph getTopologycalGraph() {
        return GRAPH;
    }
}
