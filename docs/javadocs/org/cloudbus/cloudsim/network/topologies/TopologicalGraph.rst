.. java:import:: java.util Collections

.. java:import:: java.util LinkedList

.. java:import:: java.util List

TopologicalGraph
================

.. java:package:: org.cloudbus.cloudsim.network.topologies
   :noindex:

.. java:type:: public class TopologicalGraph

   This class represents a graph containing vertices (nodes) and edges (links), used for input with a network-layer.

   Graphical-Output Restricions:

   ..

   * EdgeColors: GraphicalProperties.getColorEdge
   * NodeColors: GraphicalProperties.getColorNode

   :author: Thomas Hohnstein

Constructors
------------
TopologicalGraph
^^^^^^^^^^^^^^^^

.. java:constructor:: public TopologicalGraph()
   :outertype: TopologicalGraph

   Creates an empty graph-object.

Methods
-------
addLink
^^^^^^^

.. java:method:: public void addLink(TopologicalLink edge)
   :outertype: TopologicalGraph

   Adds an link between two topological nodes.

   :param edge: the topological link

addNode
^^^^^^^

.. java:method:: public void addNode(TopologicalNode node)
   :outertype: TopologicalGraph

   Adds an Topological Node to this graph.

   :param node: the topological node to add

getLinksList
^^^^^^^^^^^^

.. java:method:: public List<TopologicalLink> getLinksList()
   :outertype: TopologicalGraph

   Gets a \ **read-only**\  List of all network-graph links.

   :return: the List of network-graph links

getNodeList
^^^^^^^^^^^

.. java:method:: public List<TopologicalNode> getNodeList()
   :outertype: TopologicalGraph

   Gets a \ **read-only**\  list of nodes of the network graph.

getNumberOfLinks
^^^^^^^^^^^^^^^^

.. java:method:: public int getNumberOfLinks()
   :outertype: TopologicalGraph

   Gets the number of links contained inside the topological-graph.

   :return: number of links

getNumberOfNodes
^^^^^^^^^^^^^^^^

.. java:method:: public int getNumberOfNodes()
   :outertype: TopologicalGraph

   Gets the number of nodes contained inside the topological-graph.

   :return: number of nodes

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: TopologicalGraph

