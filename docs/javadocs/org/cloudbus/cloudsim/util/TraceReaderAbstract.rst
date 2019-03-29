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

   \ **NOTES:**\

   ..

   * This class can only read trace files in the following format: \ **ASCII text, zip, gz.**\
   * If you need to load multiple trace files, create multiple instances of this class.
   * If size of the trace reader is huge or contains lots of traces, please increase the JVM heap size accordingly by using \ **java -Xmx**\  option when running the simulation. For instance, you can use \ **java -Xmx200M**\  to define the JVM heap size will be 200MB.

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

.. java:constructor:: protected TraceReaderAbstract(String filePath, InputStream inputStream)
   :outertype: TraceReaderAbstract

   Create a new SwfWorkloadFileReader object.

   :param filePath: the workload trace file path in one of the following formats: \ *ASCII text, zip, gz.*\
   :param inputStream: a \ :java:ref:`InputStreamReader`\  object to read the file
   :throws IllegalArgumentException: when the workload trace file name is null or empty; or the resource PE mips is less or equal to 0

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

getInputStream
^^^^^^^^^^^^^^

.. java:method:: protected InputStream getInputStream()
   :outertype: TraceReaderAbstract

getLastLineNumber
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getLastLineNumber()
   :outertype: TraceReaderAbstract

getMaxLinesToRead
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getMaxLinesToRead()
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
   :throws UncheckedIOException: if the there was any error reading the file

readGZIPFile
^^^^^^^^^^^^

.. java:method:: protected void readGZIPFile(InputStream inputStream, Function<String[], Boolean> processParsedLineFunction) throws IOException
   :outertype: TraceReaderAbstract

   Reads traces from a gzip file, then creates a Cloudlet for each line read.

   :param inputStream: a \ :java:ref:`InputStream`\  to read the file
   :param processParsedLineFunction: a \ :java:ref:`Function`\  that receives each parsed line as an array and performs an operation over it, returning true if the operation was executed
   :throws IOException: if the there was any error reading the file

readTextFile
^^^^^^^^^^^^

.. java:method:: protected void readTextFile(InputStream inputStream, Function<String[], Boolean> processParsedLineFunction) throws IOException
   :outertype: TraceReaderAbstract

   Reads traces from a text file, then creates a Cloudlet for each line read.

   :param inputStream: a \ :java:ref:`InputStream`\  to read the file
   :param processParsedLineFunction: a \ :java:ref:`Function`\  that receives each parsed line as an array and performs an operation over it, returning true if the operation was executed
   :throws IOException: if the there was any error reading the file

readZipFile
^^^^^^^^^^^

.. java:method:: protected boolean readZipFile(InputStream inputStream, Function<String[], Boolean> processParsedLineFunction) throws IOException
   :outertype: TraceReaderAbstract

   Reads a set of trace files inside a Zip file, then creates a Cloudlet for each line read.

   :param inputStream: a \ :java:ref:`InputStream`\  to read the file
   :param processParsedLineFunction: a \ :java:ref:`Function`\  that receives each parsed line as an array and performs an operation over it, returning true if the operation was executed
   :throws IOException: if the there was any error reading the file
   :return: \ ``true``\  if reading a file is successful; \ ``false``\  otherwise.

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

