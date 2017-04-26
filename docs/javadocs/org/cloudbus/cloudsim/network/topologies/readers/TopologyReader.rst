.. java:import:: org.cloudbus.cloudsim.network.topologies TopologicalGraph

.. java:import:: java.io IOException

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

.. java:method::  TopologicalGraph readGraphFile(String filename) throws IOException
   :outertype: TopologyReader

   Reads a file and creates an \ :java:ref:`TopologicalGraph`\  object.

   :param filename: Name of the file to read
   :throws IOException: when the file cannot be accessed
   :return: The created TopologicalGraph

