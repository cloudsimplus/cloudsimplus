.. java:import:: java.util Arrays

.. java:import:: java.util List

.. java:import:: java.util.function BiConsumer

.. java:import:: java.util.stream IntStream

FloydWarshall
=============

.. java:package:: org.cloudbus.cloudsim.network
   :noindex:

.. java:type:: public class FloydWarshall

   \ `Floyd-Warshall algorithm <https://en.wikipedia.org/wiki/Floyd–Warshall_algorithm>`_\  to calculate the predecessor matrix and the delay between all pairs of nodes. The delay represents the distance between the two vertices and it works as the weight for the Floyd-Warshall algorithm.

   :author: Rahul Simha, Weishuai Yang

Constructors
------------
FloydWarshall
^^^^^^^^^^^^^

.. java:constructor:: public FloydWarshall(int numVertices)
   :outertype: FloydWarshall

   Creates a matrix of network nodes.

   :param numVertices: number of network nodes

Methods
-------
computeShortestPaths
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double[][] computeShortestPaths(double[][] originalDelayMatrix)
   :outertype: FloydWarshall

   Computes the shortest path between a vertex to all the other ones, for all existing vertices. This is represented by the delay between all pairs vertices.

   :param originalDelayMatrix: original delay matrix
   :return: the new delay matrix (dk)

getNumVertices
^^^^^^^^^^^^^^

.. java:method:: public int getNumVertices()
   :outertype: FloydWarshall

getPk
^^^^^

.. java:method:: public int[][] getPk()
   :outertype: FloydWarshall

   Gets a \ **copy**\  of the predecessor matrix.

   :return: the predecessor matrix copy

