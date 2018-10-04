NetworkTopology
===============

.. java:package:: org.cloudbus.cloudsim.network.topologies
   :noindex:

.. java:type:: public interface NetworkTopology

   * Implements a network layer by reading the topology from a file in a specific format that is defined by each implementing class.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

   **See also:** :java:ref:`BriteNetworkTopology`

Fields
------
NULL
^^^^

.. java:field::  NetworkTopology NULL
   :outertype: NetworkTopology

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`NetworkTopology`\  objects.

Methods
-------
addLink
^^^^^^^

.. java:method::  void addLink(long srcId, long destId, double bw, double lat)
   :outertype: NetworkTopology

   Adds a new link in the network topology. The CloudSim entities that represent the source and destination of the link will be mapped to BRITE entities.

   :param srcId: ID of the CloudSim entity that represents the link's source node
   :param destId: ID of the CloudSim entity that represents the link's destination node
   :param bw: Link's bandwidth
   :param lat: link's latency

getDelay
^^^^^^^^

.. java:method::  double getDelay(long srcID, long destID)
   :outertype: NetworkTopology

   Calculates the delay between two nodes.

   :param srcID: ID of the CloudSim entity that represents the link's source node
   :param destID: ID of the CloudSim entity that represents the link's destination node
   :return: communication delay between the two nodes

getTopologycalGraph
^^^^^^^^^^^^^^^^^^^

.. java:method::  TopologicalGraph getTopologycalGraph()
   :outertype: NetworkTopology

   :return: the graph

isNetworkEnabled
^^^^^^^^^^^^^^^^

.. java:method::  boolean isNetworkEnabled()
   :outertype: NetworkTopology

   Checks if the network simulation is working. If there were some problem during creation of network (e.g., during parsing of BRITE file) that does not allow a proper simulation of the network, this method returns false.

   :return: $true if network simulation is working, $false otherwise

mapNode
^^^^^^^

.. java:method::  void mapNode(long cloudSimEntityID, int briteID)
   :outertype: NetworkTopology

   Maps a CloudSim entity to a BRITE node in the network topology.

   :param cloudSimEntityID: ID of the entity being mapped
   :param briteID: ID of the BRITE node that corresponds to the CloudSim

unmapNode
^^^^^^^^^

.. java:method::  void unmapNode(long cloudSimEntityID)
   :outertype: NetworkTopology

   Unmaps a previously mapped CloudSim entity to a BRITE node in the network topology.

   :param cloudSimEntityID: ID of the entity being unmapped

