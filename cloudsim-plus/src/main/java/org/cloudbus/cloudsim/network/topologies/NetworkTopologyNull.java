package org.cloudbus.cloudsim.network.topologies;

/**
 * A class that implements the Null Object Design Pattern for {@link NetworkTopology}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see NetworkTopology#NULL
 */
final class NetworkTopologyNull implements NetworkTopology {
    private final TopologicalGraph graph = new TopologicalGraph();

    @Override public void addLink(int srcId, int destId, double bw, double lat) {/**/}
    @Override public void mapNode(int cloudSimEntityID, int briteID) {/**/}
    @Override public void unmapNode(int cloudSimEntityID) {/**/}
    @Override public double getDelay(int srcID, int destID) {
        return 0;
    }
    @Override public boolean isNetworkEnabled() {
        return false;
    }
    @Override public TopologicalGraph getTopologycalGraph() {
        return graph;
    }
}
