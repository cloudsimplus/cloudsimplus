.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util.function Predicate

.. java:import:: java.util.zip GZIPInputStream

.. java:import:: java.util.zip ZipInputStream

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletSimple

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelFull

WorkloadFileReader
==================

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public class WorkloadFileReader implements WorkloadReader

   Reads resource traces from a reader and creates a list of (\ :java:ref:`Cloudlets <Cloudlet>`\ ) (jobs). By default, it follows the \ `Standard Workload Format (*.swf files) <http://www.cs.huji.ac.il/labs/parallel/workload/>`_\  from \ `The Hebrew University of Jerusalem <new.huji.ac.il/en>`_\ . However, you can use other formats by calling the methods below before running the simulation:

   ..

   * \ :java:ref:`setComment(String)`\
   * \ :java:ref:`setField(int,int,int,int,int)`\

   \ **NOTES:**\

   ..

   * This class can only take \ ``one``\  trace reader of the following format: \ *ASCII text, zip, gz.*\
   * If you need to load multiple trace files, then you need to create multiple instances of this class \ ``each with a unique entity name``\ .
   * If size of the trace reader is huge or contains lots of traces, please increase the JVM heap size accordingly by using \ ``java -Xmx``\  option when running the simulation.
   * The default Cloudlet reader size for sending to and receiving from a Datacenter is \ :java:ref:`DataCloudTags.DEFAULT_MTU`\ . However, you can specify the reader size by using \ :java:ref:`Cloudlet.setFileSize(long)`\ .
   * A job run time is only for 1 PE \ ``not``\  the total number of allocated PEs. Therefore, a Cloudlet length is also calculated for 1 PE. For example, job #1 in the trace has a run time of 100 seconds for 2 processors. This means each processor runs job #1 for 100 seconds, if the processors have the same specification.

   :author: Anthony Sulistio, Marcos Dias de Assuncao

   **See also:** :java:ref:`WorkloadReader`

Constructors
------------
WorkloadFileReader
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public WorkloadFileReader(String filePath, int mips) throws FileNotFoundException
   :outertype: WorkloadFileReader

   Create a new WorkloadFileReader object.

   :param filePath: the workload trace file path in one of the following formats: \ *ASCII text, zip, gz.*\
   :param mips: the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run. Considering the workload reader provides the run time for each application registered inside the reader, the MIPS value will be used to compute the \ :java:ref:`length of the Cloudlet (in MI) <Cloudlet.getLength()>`\  so that it's expected to execute, inside the VM with the given MIPS capacity, for the same time as specified into the workload reader.
   :throws IllegalArgumentException: when the workload trace file name is null or empty; or the resource PE mips <= 0
   :throws FileNotFoundException:

   **See also:** :java:ref:`.getInstance(String,int)`

Methods
-------
generateWorkload
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Cloudlet> generateWorkload() throws IOException
   :outertype: WorkloadFileReader

getInstance
^^^^^^^^^^^

.. java:method:: public static WorkloadFileReader getInstance(String fileName, int mips)
   :outertype: WorkloadFileReader

   Gets a \ :java:ref:`WorkloadFileReader`\  instance from a workload file inside the \ **application's resource directory**\ . Use the available constructors if you want to load a file outside the resource directory.

   :param fileName: the workload trace \ **relative file name**\  in one of the following formats: \ *ASCII text, zip, gz.*\
   :param mips: the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run. Considering the workload reader provides the run time for each application registered inside the reader, the MIPS value will be used to compute the \ :java:ref:`length of the Cloudlet (in MI) <Cloudlet.getLength()>`\  so that it's expected to execute, inside the VM with the given MIPS capacity, for the same time as specified into the workload reader.
   :throws IllegalArgumentException: when the workload trace file name is null or empty; or the resource PE mips <= 0
   :throws UncheckedIOException: when the file cannot be accessed (such as when it doesn't exist)

getMaxLinesToRead
^^^^^^^^^^^^^^^^^

.. java:method:: public int getMaxLinesToRead()
   :outertype: WorkloadFileReader

   Gets the maximum number of lines of the workload reader that will be read. The value -1 indicates that all lines will be read, creating a cloudlet from every one.

getMips
^^^^^^^

.. java:method:: public int getMips()
   :outertype: WorkloadFileReader

   Gets the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run. Considering the workload reader provides the run time for each application registered inside the reader, the MIPS value will be used to compute the \ :java:ref:`length of the Cloudlet (in MI) <Cloudlet.getLength()>`\  so that it's expected to execute, inside the VM with the given MIPS capacity, for the same time as specified into the workload reader.

readGZIPFile
^^^^^^^^^^^^

.. java:method:: protected void readGZIPFile(InputStream inputStream) throws IOException
   :outertype: WorkloadFileReader

   Reads traces from a gzip reader, one line at a time.

   :param inputStream: a \ :java:ref:`InputStream`\  to read the file
   :throws IOException: if the there was any error reading the reader
   :return: \ ``true``\  if successful; \ ``false``\  otherwise.

readTextFile
^^^^^^^^^^^^

.. java:method:: protected void readTextFile(InputStream inputStream) throws IOException
   :outertype: WorkloadFileReader

   Reads traces from a text reader, usually with the swf extension, one line at a time.

   :param inputStream: a reader name
   :throws IOException: if the there was any error reading the reader
   :return: \ ``true``\  if successful, \ ``false``\  otherwise.

readZipFile
^^^^^^^^^^^

.. java:method:: protected boolean readZipFile(InputStream inputStream) throws IOException
   :outertype: WorkloadFileReader

   Reads a set of trace files inside a Zip reader.

   :param inputStream: a \ :java:ref:`InputStream`\  to read the file
   :throws IOException: if the there was any error reading the reader
   :return: \ ``true``\  if reading a reader is successful; \ ``false``\  otherwise.

setComment
^^^^^^^^^^

.. java:method:: public boolean setComment(String comment)
   :outertype: WorkloadFileReader

   Sets the string that identifies the start of a comment line.

   :param comment: a character that denotes the start of a comment, e.g. ";" or "#"
   :return: \ ``true``\  if it is successful, \ ``false``\  otherwise

setField
^^^^^^^^

.. java:method:: public void setField(int maxField, int jobNum, int submitTime, int runTime, int numProc)
   :outertype: WorkloadFileReader

   Tells this class what to look in the trace reader. This method should be called before the start of the simulation.

   By default, this class follows the standard workload format as specified in \ `http://www.cs.huji.ac.il/labs/parallel/workload/ <http://www.cs.huji.ac.il/labs/parallel/workload/>`_\   However, you can use other format by calling this method.

   The parameters must be a positive integer number starting from 1. A special case is where \ ``jobNum ==``\ , meaning the job or cloudlet ID will be generate by the Workload class, instead of reading from the trace reader.

   :param maxField: max. number of field/column in one row
   :param jobNum: field/column number for locating the job ID
   :param submitTime: field/column number for locating the job submit time
   :param runTime: field/column number for locating the job run time
   :param numProc: field/column number for locating the number of PEs required to run a job
   :throws IllegalArgumentException: if any of the arguments are not within the acceptable ranges

setMaxLinesToRead
^^^^^^^^^^^^^^^^^

.. java:method:: public void setMaxLinesToRead(int maxLinesToRead)
   :outertype: WorkloadFileReader

   Sets the maximum number of lines of the workload reader that will be read. The value -1 indicates that all lines will be read, creating a cloudlet from every one.

   :param maxLinesToRead: the maximum number of lines to set

setMips
^^^^^^^

.. java:method:: public final WorkloadFileReader setMips(int mips)
   :outertype: WorkloadFileReader

   Sets the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run. Considering the workload reader provides the run time for each application registered inside the reader, the MIPS value will be used to compute the \ :java:ref:`length of the Cloudlet (in MI) <Cloudlet.getLength()>`\  so that it's expected to execute, inside the VM with the given MIPS capacity, for the same time as specified into the workload reader.

   :param mips: the MIPS value to set

setPredicate
^^^^^^^^^^^^

.. java:method:: @Override public WorkloadReader setPredicate(Predicate<Cloudlet> predicate)
   :outertype: WorkloadFileReader

