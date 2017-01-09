TopologicalNode
===============

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public class TopologicalNode

   Represents an topological network node that retrieves its information from a topological-generated file (eg. topology-generator)

   :author: Thomas Hohnstein

Constructors
------------
TopologicalNode
^^^^^^^^^^^^^^^

.. java:constructor:: public TopologicalNode(int nodeID)
   :outertype: TopologicalNode

   Constructs an new node.

   :param nodeID: The BRITE id of the node inside the network

TopologicalNode
^^^^^^^^^^^^^^^

.. java:constructor:: public TopologicalNode(int nodeID, int x, int y)
   :outertype: TopologicalNode

   Constructs an new node including world-coordinates.

   :param nodeID: The BRITE id of the node inside the network
   :param x: x world-coordinate
   :param y: y world-coordinate

TopologicalNode
^^^^^^^^^^^^^^^

.. java:constructor:: public TopologicalNode(int nodeID, String nodeName, int x, int y)
   :outertype: TopologicalNode

   Constructs an new node including world-coordinates and the nodeName.

   :param nodeID: The BRITE id of the node inside the network
   :param nodeName: The name of the node inside the network
   :param x: x world-coordinate
   :param y: y world-coordinate

Methods
-------
getCoordinateX
^^^^^^^^^^^^^^

.. java:method:: public int getCoordinateX()
   :outertype: TopologicalNode

   Gets the x world coordinate of this network-node.

   :return: the x world coordinate

getCoordinateY
^^^^^^^^^^^^^^

.. java:method:: public int getCoordinateY()
   :outertype: TopologicalNode

   Gets the y world coordinate of this network-node

   :return: the y world coordinate

getNodeID
^^^^^^^^^

.. java:method:: public int getNodeID()
   :outertype: TopologicalNode

   Gets the node BRITE id.

   :return: the nodeID

getNodeLabel
^^^^^^^^^^^^

.. java:method:: public String getNodeLabel()
   :outertype: TopologicalNode

   Gets the name of the node

   :return: name of the node

