.. java:import:: org.cloudbus.cloudsim.network DelayMatrix

.. java:import:: org.cloudbus.cloudsim.network.topologies.readers TopologyReaderBrite

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.io InputStreamReader

.. java:import:: java.util Arrays

.. java:import:: java.util HashMap

.. java:import:: java.util Map

BriteNetworkTopology
====================

.. java:package:: org.cloudbus.cloudsim.network.topologies
   :noindex:

.. java:type:: public final class BriteNetworkTopology implements NetworkTopology

   Implements a network layer by reading the topology from a file in the \ `BRITE format <http://www.cs.bu.edu/brite/user_manual/node29.html>`_\ , the \ `Boston university Representative Topology gEnerator <http://www.cs.bu.edu/brite/>`_\ , and generates a topological network from it. Information of this network is used to simulate latency in network traffic of CloudSim.

   The topology file may contain more nodes than the number of entities in the simulation. It allows users to increase the scale of the simulation without changing the topology file. Nevertheless, each CloudSim entity must be mapped to one (and only one) BRITE node to allow proper work of the network simulation. Each BRITE node can be mapped to only one entity at a time.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

   **See also:** :java:ref:`.getInstance(String)`

Constructors
------------
BriteNetworkTopology
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public BriteNetworkTopology()
   :outertype: BriteNetworkTopology

   Instantiates a Network Topology.

   **See also:** :java:ref:`.BriteNetworkTopology(String)`, :java:ref:`.BriteNetworkTopology(InputStreamReader)`, :java:ref:`.getInstance(String)`

BriteNetworkTopology
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public BriteNetworkTopology(String filePath)
   :outertype: BriteNetworkTopology

   Instantiates a Network Topology if a given file exists and can be successfully parsed. File is written in the BRITE format and contains topological information on simulation entities.

   :param filePath: the path of the BRITE file

   **See also:** :java:ref:`.BriteNetworkTopology()`, :java:ref:`.BriteNetworkTopology(InputStreamReader)`, :java:ref:`.getInstance(String)`

Methods
-------
addLink
^^^^^^^

.. java:method:: @Override public void addLink(long srcId, long destId, double bandwidth, double latency)
   :outertype: BriteNetworkTopology

getBwMatrix
^^^^^^^^^^^

.. java:method:: public double[][] getBwMatrix()
   :outertype: BriteNetworkTopology

   Gets a\ **copy**\  of the matrix containing the bandwidth between every pair of nodes in the network.

getDelay
^^^^^^^^

.. java:method:: @Override public double getDelay(long srcID, long destID)
   :outertype: BriteNetworkTopology

getInstance
^^^^^^^^^^^

.. java:method:: public static BriteNetworkTopology getInstance(String fileName)
   :outertype: BriteNetworkTopology

   Instantiates a Network Topology from a file inside the \ **application's resource directory**\ .

   :param fileName: the \ **relative name**\  of the BRITE file
   :return: the BriteNetworkTopology instance.

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

.. java:method:: @Override public void mapNode(long cloudSimEntityID, int briteID)
   :outertype: BriteNetworkTopology

unmapNode
^^^^^^^^^

.. java:method:: @Override public void unmapNode(long cloudSimEntityID)
   :outertype: BriteNetworkTopology

