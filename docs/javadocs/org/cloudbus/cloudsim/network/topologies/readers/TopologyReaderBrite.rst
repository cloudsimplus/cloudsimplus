.. java:import:: org.cloudbus.cloudsim.network.topologies Point2D

.. java:import:: org.cloudbus.cloudsim.network.topologies TopologicalGraph

.. java:import:: org.cloudbus.cloudsim.network.topologies TopologicalLink

.. java:import:: org.cloudbus.cloudsim.network.topologies TopologicalNode

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: java.io BufferedReader

.. java:import:: java.io IOException

.. java:import:: java.io InputStreamReader

.. java:import:: java.io UncheckedIOException

.. java:import:: java.util StringTokenizer

.. java:import:: java.util.function Function

TopologyReaderBrite
===================

.. java:package:: org.cloudbus.cloudsim.network.topologies.readers
   :noindex:

.. java:type:: public class TopologyReaderBrite implements TopologyReader

   A network graph (topology) readers that creates a network topology from a file in the \ `BRITE format <http://www.cs.bu.edu/brite/user_manual/node29.html>`_\ . A BRITE file is structured as follows:

   ..

   * Node-section: NodeID, xpos, ypos, indegree, outdegree, ASid, type(router/AS)
   * Edge-section: EdgeID, fromNode, toNode, euclideanLength, linkDelay, linkBandwith, AS_from, AS_to, type

   :author: Thomas Hohnstein

Methods
-------
readGraphFile
^^^^^^^^^^^^^

.. java:method:: @Override public TopologicalGraph readGraphFile(String filename)
   :outertype: TopologyReaderBrite

readGraphFile
^^^^^^^^^^^^^

.. java:method:: @Override public TopologicalGraph readGraphFile(InputStreamReader streamReader)
   :outertype: TopologyReaderBrite

