.. java:import:: org.cloudbus.cloudsim.network.topologies TopologicalGraph

.. java:import:: org.cloudbus.cloudsim.network.topologies TopologicalLink

DelayMatrix
===========

.. java:package:: org.cloudbus.cloudsim.network
   :noindex:

.. java:type:: public class DelayMatrix

   This class represents a delay matrix between every pair or nodes inside a network topology, storing every distance between connected nodes.

   :author: Thomas Hohnstein

Constructors
------------
DelayMatrix
^^^^^^^^^^^

.. java:constructor:: public DelayMatrix()
   :outertype: DelayMatrix

DelayMatrix
^^^^^^^^^^^

.. java:constructor:: public DelayMatrix(TopologicalGraph graph, boolean directed)
   :outertype: DelayMatrix

   Creates an correctly initialized double-Delay-Matrix.

   :param graph: the network topological graph
   :param directed: indicates if an directed matrix should be computed (true) or not (false)

Methods
-------
getDelay
^^^^^^^^

.. java:method:: public double getDelay(int srcID, int destID)
   :outertype: DelayMatrix

   Gets the delay between two nodes.

   :param srcID: the id of the source node
   :param destID: the id of the destination node
   :return: the delay between the given two nodes

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: DelayMatrix

