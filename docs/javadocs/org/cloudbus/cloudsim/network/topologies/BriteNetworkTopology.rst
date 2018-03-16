.. java:import:: java.io IOException

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.network DelayMatrix

.. java:import:: org.cloudbus.cloudsim.network.topologies.readers TopologyReaderBrite

BriteNetworkTopology
====================

.. java:package:: org.cloudbus.cloudsim.network.topologies
   :noindex:

.. java:type:: public class BriteNetworkTopology implements NetworkTopology

   Implements a network layer by reading the topology from a file in the \ `BRITE format <http://www.cs.bu.edu/brite/user_manual/node29.html>`_\ , the \ `Boston university Representative Topology gEnerator <http://www.cs.bu.edu/brite/>`_\ , and generates a topological network from it. Information of this network is used to simulate latency in network traffic of CloudSim.

   The topology file may contain more nodes than the number of entities in the simulation. It allows users to increase the scale of the simulation without changing the topology file. Nevertheless, each CloudSim entity must be mapped to one (and only one) BRITE node to allow proper work of the network simulation. Each BRITE node can be mapped to only one entity at a time.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

Constructors
------------
BriteNetworkTopology
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public BriteNetworkTopology()
   :outertype: BriteNetworkTopology

   Creates a network topology

BriteNetworkTopology
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public BriteNetworkTopology(String fileName)
   :outertype: BriteNetworkTopology

   Creates a network topology if the file exists and can be successfully parsed. File is written in the BRITE format and contains topological information on simulation entities.

   :param fileName: name of the BRITE file

Methods
-------
addLink
^^^^^^^

.. java:method:: @Override public void addLink(int srcId, int destId, double bw, double lat)
   :outertype: BriteNetworkTopology

getBwMatrix
^^^^^^^^^^^

.. java:method:: public double[][] getBwMatrix()
   :outertype: BriteNetworkTopology

   Gets a\ **copy**\  of the matrix containing the bandwidth between every pair of nodes in the network.

getDelay
^^^^^^^^

.. java:method:: @Override public double getDelay(int srcID, int destID)
   :outertype: BriteNetworkTopology

getTopologycalGraph
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public TopologicalGraph getTopologycalGraph()
   :outertype: BriteNetworkTopology

isNetworkEnabled
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isNetworkEnabled()
   :outertype: BriteNetworkTopology

mapNode
^^^^^^^

.. java:method:: @Override public void mapNode(int cloudSimEntityID, int briteID)
   :outertype: BriteNetworkTopology

unmapNode
^^^^^^^^^

.. java:method:: @Override public void unmapNode(int cloudSimEntityID)
   :outertype: BriteNetworkTopology

