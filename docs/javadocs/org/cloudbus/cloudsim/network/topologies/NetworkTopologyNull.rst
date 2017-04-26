NetworkTopologyNull
===================

.. java:package:: org.cloudbus.cloudsim.network.topologies
   :noindex:

.. java:type:: final class NetworkTopologyNull implements NetworkTopology

   A class that implements the Null Object Design Pattern for \ :java:ref:`NetworkTopology`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`NetworkTopology.NULL`

Methods
-------
addLink
^^^^^^^

.. java:method:: @Override public void addLink(int srcId, int destId, double bw, double lat)
   :outertype: NetworkTopologyNull

getDelay
^^^^^^^^

.. java:method:: @Override public double getDelay(int srcID, int destID)
   :outertype: NetworkTopologyNull

getTopologycalGraph
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public TopologicalGraph getTopologycalGraph()
   :outertype: NetworkTopologyNull

isNetworkEnabled
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isNetworkEnabled()
   :outertype: NetworkTopologyNull

mapNode
^^^^^^^

.. java:method:: @Override public void mapNode(int cloudSimEntityID, int briteID)
   :outertype: NetworkTopologyNull

unmapNode
^^^^^^^^^

.. java:method:: @Override public void unmapNode(int cloudSimEntityID)
   :outertype: NetworkTopologyNull

