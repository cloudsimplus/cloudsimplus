TopologicalLink
===============

.. java:package:: org.cloudbus.cloudsim.network.topologies
   :noindex:

.. java:type:: public class TopologicalLink

   Represents a link (edge) of a network graph where the network topology was defined from a file in \ `BRITE format <http://www.cs.bu.edu/brite/user_manual/node29.html>`_\ .

   :author: Thomas Hohnstein

Constructors
------------
TopologicalLink
^^^^^^^^^^^^^^^

.. java:constructor:: public TopologicalLink(int srcNode, int destNode, double delay, double bandwidth)
   :outertype: TopologicalLink

   Creates a new Topological Link.

   :param srcNode: the BRITE id of the source node of the link.
   :param destNode: the BRITE id of the destination node of the link.
   :param delay: the link delay of the connection.
   :param bandwidth: the link bandwidth (bw)

Methods
-------
getDestNodeID
^^^^^^^^^^^^^

.. java:method:: public int getDestNodeID()
   :outertype: TopologicalLink

   Gets the BRITE id of the destination node of the link.

   :return: nodeID

getLinkBw
^^^^^^^^^

.. java:method:: public double getLinkBw()
   :outertype: TopologicalLink

   Gets the bandwidth of the link.

   :return: the bw

getLinkDelay
^^^^^^^^^^^^

.. java:method:: public double getLinkDelay()
   :outertype: TopologicalLink

   Gets the delay of the link.

   :return: the link delay

getSrcNodeID
^^^^^^^^^^^^

.. java:method:: public int getSrcNodeID()
   :outertype: TopologicalLink

   Gets the BRITE id of the source node of the link.

   :return: nodeID

