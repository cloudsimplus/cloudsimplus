.. java:import:: java.nio.file Files

.. java:import:: java.nio.file Paths

.. java:import:: java.util Arrays

.. java:import:: java.util.function Function

.. java:import:: java.util.zip GZIPInputStream

.. java:import:: java.util.zip ZipInputStream

TraceReaderAbstract
===================

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public abstract class TraceReaderAbstract implements TraceReader

   An abstract class providing features for subclasses implementing trace file readers for specific file formats.

   :author: Manoel Campos da Silva Filho

Constructors
------------
TraceReaderAbstract
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TraceReaderAbstract(String filePath) throws IOException
   :outertype: TraceReaderAbstract

   Create a new SwfWorkloadFileReader object.

   :param filePath: the workload trace file path in one of the following formats: \ *ASCII text, zip, gz.*\
   :throws IllegalArgumentException: when the workload trace file name is null or empty
   :throws FileNotFoundException: when the trace file is not found

TraceReaderAbstract
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected TraceReaderAbstract(String filePath, InputStream reader)
   :outertype: TraceReaderAbstract

   Create a new SwfWorkloadFileReader object.

   :param filePath: the workload trace file path in one of the following formats: \ *ASCII text, zip, gz.*\
   :param reader: a \ :java:ref:`InputStreamReader`\  object to read the file
   :throws IllegalArgumentException: when the workload trace file name is null or empty; or the resource PE mips <= 0

Methods
-------
getCommentString
^^^^^^^^^^^^^^^^

.. java:method:: @Override public String[] getCommentString()
   :outertype: TraceReaderAbstract

   {@inheritDoc}

   It's returned a defensive copy of the array.

   :return: {@inheritDoc}

getFieldDelimiterRegex
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public String getFieldDelimiterRegex()
   :outertype: TraceReaderAbstract

getFilePath
^^^^^^^^^^^

.. java:method:: @Override public String getFilePath()
   :outertype: TraceReaderAbstract

getMaxLinesToRead
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getMaxLinesToRead()
   :outertype: TraceReaderAbstract

getReader
^^^^^^^^^

.. java:method:: protected InputStream getReader()
   :outertype: TraceReaderAbstract

parseTraceLine
^^^^^^^^^^^^^^

.. java:method:: protected String[] parseTraceLine(String line)
   :outertype: TraceReaderAbstract

readFile
^^^^^^^^

.. java:method:: protected void readFile(Function<String[], Boolean> processParsedLineFunction)
   :outertype: TraceReaderAbstract

   Reads traces from the file indicated by the \ :java:ref:`getFilePath()`\ , then creates a Cloudlet for each line read.

   :param processParsedLineFunction: a \ :java:ref:`Function`\  that receives each parsed line as an array and performs an operation over it, returning true if the operation was executed
   :throws UncheckedIOException: if the there was any error reading the reader
   :return: \ ``true``\  if successful, \ ``false``\  otherwise.

readGZIPFile
^^^^^^^^^^^^

.. java:method:: protected void readGZIPFile(InputStream inputStream, Function<String[], Boolean> processParsedLineFunction) throws IOException
   :outertype: TraceReaderAbstract

   Reads traces from a gzip reader, then creates a Cloudlet for each line read.

   :param inputStream: a \ :java:ref:`InputStream`\  to read the file
   :param processParsedLineFunction: a \ :java:ref:`Function`\  that receives each parsed line as an array and performs an operation over it, returning true if the operation was executed
   :throws IOException: if the there was any error reading the reader
   :return: \ ``true``\  if successful; \ ``false``\  otherwise.

readTextFile
^^^^^^^^^^^^

.. java:method:: protected void readTextFile(InputStream inputStream, Function<String[], Boolean> processParsedLineFunction) throws IOException
   :outertype: TraceReaderAbstract

   Reads traces from a text reader, then creates a Cloudlet for each line read.

   :param inputStream: a \ :java:ref:`InputStream`\  to read the file
   :param processParsedLineFunction: a \ :java:ref:`Function`\  that receives each parsed line as an array and performs an operation over it, returning true if the operation was executed
   :throws IOException: if the there was any error reading the reader
   :return: \ ``true``\  if successful, \ ``false``\  otherwise.

readZipFile
^^^^^^^^^^^

.. java:method:: protected boolean readZipFile(InputStream inputStream, Function<String[], Boolean> processParsedLineFunction) throws IOException
   :outertype: TraceReaderAbstract

   Reads a set of trace files inside a Zip reader, then creates a Cloudlet for each line read.

   :param inputStream: a \ :java:ref:`InputStream`\  to read the file
   :param processParsedLineFunction: a \ :java:ref:`Function`\  that receives each parsed line as an array and performs an operation over it, returning true if the operation was executed
   :throws IOException: if the there was any error reading the reader
   :return: \ ``true``\  if reading a reader is successful; \ ``false``\  otherwise.

setCommentString
^^^^^^^^^^^^^^^^

.. java:method:: @Override public TraceReader setCommentString(String... commentString)
   :outertype: TraceReaderAbstract

setFieldDelimiterRegex
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final TraceReader setFieldDelimiterRegex(String fieldDelimiterRegex)
   :outertype: TraceReaderAbstract

setMaxLinesToRead
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public TraceReader setMaxLinesToRead(int maxLinesToRead)
   :outertype: TraceReaderAbstract

