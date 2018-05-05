.. java:import:: org.cloudbus.cloudsim.network.topologies TopologicalGraph

.. java:import:: java.io IOException

.. java:import:: java.io InputStreamReader

TopologyReader
==============

.. java:package:: org.cloudbus.cloudsim.network.topologies.readers
   :noindex:

.. java:type:: public interface TopologyReader

   An interface to be implemented by classes that read a network graph (topology) from a file name with a specific format.

   :author: Thomas Hohnstein

Methods
-------
readGraphFile
^^^^^^^^^^^^^

.. java:method::  TopologicalGraph readGraphFile(String filename)
   :outertype: TopologyReader

   Reads a file and creates an \ :java:ref:`TopologicalGraph`\  object.

   :param filename: Name of the file to read
   :throws IOException: when the file cannot be accessed
   :return: The created TopologicalGraph

readGraphFile
^^^^^^^^^^^^^

.. java:method::  TopologicalGraph readGraphFile(InputStreamReader streamReader)
   :outertype: TopologyReader

   Reads a file and creates an \ :java:ref:`TopologicalGraph`\  object.

   :param streamReader: the \ :java:ref:`InputStreamReader`\  to read the file
   :throws IOException: when the file cannot be accessed
   :return: The created TopologicalGraph

