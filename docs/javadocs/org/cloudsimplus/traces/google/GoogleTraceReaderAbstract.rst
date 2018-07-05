.. java:import:: org.cloudsimplus.traces TraceReaderBase

.. java:import:: java.io FileInputStream

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io InputStream

.. java:import:: java.util HashSet

.. java:import:: java.util Set

.. java:import:: java.util.function Consumer

GoogleTraceReaderAbstract
=========================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: abstract class GoogleTraceReaderAbstract<T> extends TraceReaderBase

   An abstract class for creating \ `Google Cluster Trace <https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md>`_\  readers.

   :author: Manoel Campos da Silva Filho
   :param <T>: the type of objects that will be created for each line read from the trace file

Fields
------
availableObjects
^^^^^^^^^^^^^^^^

.. java:field:: protected final Set<T> availableObjects
   :outertype: GoogleTraceReaderAbstract

   A Set of objects immediately created from the trace file. The type  of the objects depends on each concrete class. For instance, the \ :java:ref:`GoogleMachineEventsTraceReader`\  creates \ :java:ref:`org.cloudbus.cloudsim.hosts.Host`\ s. The \ :java:ref:`GoogleTaskEventsTraceReader`\  creates \ :java:ref:`org.cloudbus.cloudsim.cloudlets.Cloudlet`\ s.

Constructors
------------
GoogleTraceReaderAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected GoogleTraceReaderAbstract(String filePath) throws FileNotFoundException
   :outertype: GoogleTraceReaderAbstract

GoogleTraceReaderAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected GoogleTraceReaderAbstract(String filePath, InputStream reader)
   :outertype: GoogleTraceReaderAbstract

Methods
-------
formatPercentValue
^^^^^^^^^^^^^^^^^^

.. java:method:: protected String formatPercentValue(double percent)
   :outertype: GoogleTraceReaderAbstract

postProcess
^^^^^^^^^^^

.. java:method:: protected abstract void postProcess()
   :outertype: GoogleTraceReaderAbstract

   Executes some post-process after the trace file was totally parsed.

preProcess
^^^^^^^^^^

.. java:method:: protected abstract void preProcess()
   :outertype: GoogleTraceReaderAbstract

   Executes the pre-process before starting to read the trace file, such as checking if required attributes were set.

process
^^^^^^^

.. java:method:: public Set<T> process()
   :outertype: GoogleTraceReaderAbstract

   Process the \ :java:ref:`trace file <getFilePath()>`\  creating a Set of objects described in the file.

   It returns the Set of created objects that were available at timestamp 0 inside the trace file.

   :return: the Set of created objects that were available at timestamp 0 inside the trace file.

processParsedLine
^^^^^^^^^^^^^^^^^

.. java:method:: protected final boolean processParsedLine(String[] parsedLineArray)
   :outertype: GoogleTraceReaderAbstract

   Process the parsed line according to the event type.

   :param parsedLineArray: an array containing the field values from the last parsed trace line.
   :return: true if the parsed line was processed, false otherwise

processParsedLineInternal
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract boolean processParsedLineInternal()
   :outertype: GoogleTraceReaderAbstract

   Process the last parsed trace line.

   :return: true if the parsed line was processed, false otherwise

   **See also:** :java:ref:`.processParsedLine(String[])`, :java:ref:`.getLastParsedLineArray()`

setPreProcess
^^^^^^^^^^^^^

.. java:method:: public void setPreProcess(Consumer<? extends GoogleTraceReaderAbstract> preProcess)
   :outertype: GoogleTraceReaderAbstract

