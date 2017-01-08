FloydWarshall
=============

.. java:package:: org.cloudbus.cloudsim.network
   :noindex:

.. java:type:: public class FloydWarshall

   FloydWarshall algorithm to calculate the predecessor matrix and the delay between all pairs of nodes.

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
allPairsShortestPaths
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double[][] allPairsShortestPaths(double[][] adjMatrix)
   :outertype: FloydWarshall

   Calculates the delay between all pairs of nodes.

   :param adjMatrix: original delay matrix
   :return: the delay matrix

getPk
^^^^^

.. java:method:: public int[][] getPk()
   :outertype: FloydWarshall

   Gets predecessor matrix.

   :return: predecessor matrix

