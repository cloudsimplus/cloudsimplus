.. java:import:: java.util Objects

TopologicalNode
===============

.. java:package:: org.cloudbus.cloudsim.network.topologies
   :noindex:

.. java:type:: public class TopologicalNode

   Represents an topological network node that retrieves its information from a topological-generated file (eg. topology-generator)

   :author: Thomas Hohnstein

Constructors
------------
TopologicalNode
^^^^^^^^^^^^^^^

.. java:constructor:: public TopologicalNode()
   :outertype: TopologicalNode

   Creates a network topology node with ID equals to zero.

TopologicalNode
^^^^^^^^^^^^^^^

.. java:constructor:: public TopologicalNode(int nodeId)
   :outertype: TopologicalNode

   Creates a network topology node with a specific ID.

   :param nodeId: The BRITE id of the node inside the network

TopologicalNode
^^^^^^^^^^^^^^^

.. java:constructor:: public TopologicalNode(int nodeId, Point2D worldCoordinates)
   :outertype: TopologicalNode

   Creates a network topology node including world-coordinates.

   :param nodeId: The BRITE id of the node inside the network
   :param worldCoordinates: the x,y world-coordinates of the Node

TopologicalNode
^^^^^^^^^^^^^^^

.. java:constructor:: public TopologicalNode(int nodeId, String nodeName, Point2D worldCoordinates)
   :outertype: TopologicalNode

   Creates a network topology node including world-coordinates and the nodeName.

   :param nodeId: The BRITE id of the node inside the network
   :param nodeName: The name of the node inside the network
   :param worldCoordinates: the x,y world-coordinates of the Node

Methods
-------
getNodeId
^^^^^^^^^

.. java:method:: public int getNodeId()
   :outertype: TopologicalNode

   Gets the BRITE id of the node inside the network.

   :return: the nodeId

getNodeName
^^^^^^^^^^^

.. java:method:: public String getNodeName()
   :outertype: TopologicalNode

   Gets the name of the node

   :return: name of the node

getWorldCoordinates
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Point2D getWorldCoordinates()
   :outertype: TopologicalNode

   Gets the x,y world coordinates of this network-node.

   :return: the x,y world coordinates

setNodeId
^^^^^^^^^

.. java:method:: public void setNodeId(int nodeId)
   :outertype: TopologicalNode

setNodeName
^^^^^^^^^^^

.. java:method:: public void setNodeName(String nodeName)
   :outertype: TopologicalNode

setWorldCoordinates
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setWorldCoordinates(Point2D worldCoordinates)
   :outertype: TopologicalNode

