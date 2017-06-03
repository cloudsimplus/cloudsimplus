.. java:import:: java.io BufferedReader

.. java:import:: java.io File

.. java:import:: java.io FileInputStream

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io IOException

.. java:import:: java.io InputStream

.. java:import:: java.io InputStreamReader

.. java:import:: java.util ArrayList

.. java:import:: java.util Enumeration

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.zip GZIPInputStream

.. java:import:: java.util.zip ZipEntry

.. java:import:: java.util.zip ZipFile

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletSimple

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelFull

WorkloadFileReader
==================

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public class WorkloadFileReader implements WorkloadReader

   Reads resource traces from a file and creates a list of (\ :java:ref:`Cloudlets <Cloudlet>`\ ) (jobs). By default, it follows the \ `Standard Workload Format (*.swf files) <http://www.cs.huji.ac.il/labs/parallel/workload/>`_\  from \ `The Hebrew University of Jerusalem <new.huji.ac.il/en>`_\ . However, you can use other formats by calling the methods below before running the simulation:

   ..

   * \ :java:ref:`setComment(String)`\
   * \ :java:ref:`setField(int,int,int,int,int)`\

   \ **NOTES:**\

   ..

   * This class can only take \ ``one``\  trace file of the following format: \ *ASCII text, zip, gz.*\
   * If you need to load multiple trace files, then you need to create multiple instances of this class \ ``each with a unique entity name``\ .
   * If size of the trace file is huge or contains lots of traces, please increase the JVM heap size accordingly by using \ ``java -Xmx``\  option when running the simulation.
   * The default Cloudlet file size for sending to and receiving from a Datacenter is \ :java:ref:`DataCloudTags.DEFAULT_MTU`\ . However, you can specify the file size by using \ :java:ref:`Cloudlet.setFileSize(long)`\ .
   * A job run time is only for 1 PE \ ``not``\  the total number of allocated PEs. Therefore, a Cloudlet length is also calculated for 1 PE. For example, job #1 in the trace has a run time of 100 seconds for 2 processors. This means each processor runs job #1 for 100 seconds, if the processors have the same specification.

   :author: Anthony Sulistio, Marcos Dias de Assuncao

   **See also:** :java:ref:`WorkloadReader`

Constructors
------------
WorkloadFileReader
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public WorkloadFileReader(String fileName, int rating) throws FileNotFoundException
   :outertype: WorkloadFileReader

   Create a new WorkloadFileReader object.

   :param fileName: the workload trace full filename in one of the following formats: \ *ASCII text, zip, gz.*\
   :param rating: the cloudlet's PE rating (in MIPS), considering that all PEs of a cloudlet have the same rate
   :throws IllegalArgumentException: This happens for the following conditions:

   ..

   * the workload trace file name is null or empty
   * the resource PE rating <= 0

   @pre fileName != null
   :throws FileNotFoundException:

Methods
-------
generateWorkload
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Cloudlet> generateWorkload() throws IOException
   :outertype: WorkloadFileReader

getInstanceFromResourcesDir
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static WorkloadFileReader getInstanceFromResourcesDir(String fileName, int rating) throws FileNotFoundException
   :outertype: WorkloadFileReader

   Gets a \ :java:ref:`WorkloadFileReader`\  object from a workload file inside the application's resource directory.

   :param fileName: the workload trace relative filename in one of the following formats: \ *ASCII text, zip, gz.*\
   :param rating: the cloudlet's PE rating (in MIPS), considering that all PEs of a cloudlet have the same rate
   :throws IllegalArgumentException: This happens for the following conditions:

   ..

   * the workload trace file name is null or empty
   * the resource PE rating <= 0

   @pre fileName != null
   :throws FileNotFoundException:

getMaxLinesToRead
^^^^^^^^^^^^^^^^^

.. java:method:: public int getMaxLinesToRead()
   :outertype: WorkloadFileReader

   Gets the maximum number of lines of the workload file that will be read. The value -1 indicates that all lines will be read, creating a cloudlet from every one.

readGZIPFile
^^^^^^^^^^^^

.. java:method:: protected void readGZIPFile(File fl) throws IOException
   :outertype: WorkloadFileReader

   Reads traces from a gzip file, one line at a time.

   :param fl: a gzip file name
   :throws IOException: if the there was any error reading the file
   :return: \ ``true``\  if successful; \ ``false``\  otherwise.

readTextFile
^^^^^^^^^^^^

.. java:method:: protected void readTextFile(File fl) throws IOException
   :outertype: WorkloadFileReader

   Reads traces from a text file, usually with the swf extension, one line at a time.

   :param fl: a file name
   :throws IOException: if the there was any error reading the file
   :return: \ ``true``\  if successful, \ ``false``\  otherwise.

readZipFile
^^^^^^^^^^^

.. java:method:: protected boolean readZipFile(File fl) throws IOException
   :outertype: WorkloadFileReader

   Reads a set of trace files inside a Zip file.

   :param fl: a zip file name
   :throws IOException: if the there was any error reading the file
   :return: \ ``true``\  if reading a file is successful; \ ``false``\  otherwise.

setComment
^^^^^^^^^^

.. java:method:: public boolean setComment(String cmt)
   :outertype: WorkloadFileReader

   Sets the string that identifies the start of a comment line.

   :param cmt: a character that denotes the start of a comment, e.g. ";" or "#"
   :return: \ ``true``\  if it is successful, \ ``false``\  otherwise

setField
^^^^^^^^

.. java:method:: public void setField(int maxField, int jobNum, int submitTime, int runTime, int numProc)
   :outertype: WorkloadFileReader

   Tells this class what to look in the trace file. This method should be called before the start of the simulation.

   By default, this class follows the standard workload format as specified in \ `http://www.cs.huji.ac.il/labs/parallel/workload/ <http://www.cs.huji.ac.il/labs/parallel/workload/>`_\   However, you can use other format by calling this method.

   The parameters must be a positive integer number starting from 1. A special case is where \ ``jobNum ==``\ , meaning the job or cloudlet ID will be generate by the Workload class, instead of reading from the trace file.

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

   Sets the maximum number of lines of the workload file that will be read. The value -1 indicates that all lines will be read, creating a cloudlet from every one.

   :param maxLinesToRead: the maximum number of lines to set

