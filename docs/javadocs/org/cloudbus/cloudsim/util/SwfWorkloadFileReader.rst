.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletSimple

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelFull

.. java:import:: java.nio.file Files

.. java:import:: java.nio.file Paths

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util.function Predicate

SwfWorkloadFileReader
=====================

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public final class SwfWorkloadFileReader extends TraceReaderAbstract

   Reads resource traces and creates a list of (\ :java:ref:`Cloudlets <Cloudlet>`\ ) (jobs). It follows the \ `Standard Workload Format (*.swf files) <http://www.cs.huji.ac.il/labs/parallel/workload/>`_\  from \ `The Hebrew University of Jerusalem <new.huji.ac.il/en>`_\ .

   \ **NOTES:**\

   ..

   * This class can only read trace files in the following format: \ **ASCII text, zip, gz.**\
   * If you need to load multiple trace files, then you need to create multiple instances of this class \ ``each with a unique entity name``\ .
   * If size of the trace reader is huge or contains lots of traces, please increase the JVM heap size accordingly by using \ ``java -Xmx``\  option when running the simulation.
   * The default Cloudlet reader size for sending to and receiving from a Datacenter is \ :java:ref:`DataCloudTags.DEFAULT_MTU`\ . However, you can specify the reader size by using \ :java:ref:`Cloudlet.setFileSize(long)`\ .
   * A job run time considers the time spent for a single PE (since all PEs will be used for the same amount of time)\ ``not``\  not the total execution time across all PEs. For example, job #1 in the trace has a run time of 100 seconds for 2 processors. This means each processor runs job #1 for 100 seconds, if the processors have the same specification.

   :author: Anthony Sulistio, Marcos Dias de Assuncao, Manoel Campos da Silva Filho

   **See also:** :java:ref:`.getInstance(String,int)`, :java:ref:`.generateWorkload()`

Constructors
------------
SwfWorkloadFileReader
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SwfWorkloadFileReader(String filePath, int mips) throws IOException
   :outertype: SwfWorkloadFileReader

   Create a new SwfWorkloadFileReader object.

   :param filePath: the workload trace file path in one of the following formats: \ *ASCII text, zip, gz.*\
   :param mips: the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run. Considering the workload reader provides the run time for each application registered inside the reader, the MIPS value will be used to compute the \ :java:ref:`length of the Cloudlet (in MI) <Cloudlet.getLength()>`\  so that it's expected to execute, inside the VM with the given MIPS capacity, for the same time as specified into the workload reader.
   :throws IllegalArgumentException: when the workload trace file name is null or empty; or the resource PE mips <= 0
   :throws FileNotFoundException: when the file is not found

   **See also:** :java:ref:`.getInstance(String,int)`

Methods
-------
generateWorkload
^^^^^^^^^^^^^^^^

.. java:method:: public List<Cloudlet> generateWorkload()
   :outertype: SwfWorkloadFileReader

   Generates a list of jobs (\ :java:ref:`Cloudlets <Cloudlet>`\ ) to be executed, if it wasn't generated yet.

   :return: a generated Cloudlet list

getInstance
^^^^^^^^^^^

.. java:method:: public static SwfWorkloadFileReader getInstance(String fileName, int mips)
   :outertype: SwfWorkloadFileReader

   Gets a \ :java:ref:`SwfWorkloadFileReader`\  instance from a workload file inside the \ **application's resource directory**\ . Use the available constructors if you want to load a file outside the resource directory.

   :param fileName: the workload trace \ **relative file name**\  in one of the following formats: \ *ASCII text, zip, gz.*\
   :param mips: the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run. Considering the workload reader provides the run time for each application registered inside the reader, the MIPS value will be used to compute the \ :java:ref:`length of the Cloudlet (in MI) <Cloudlet.getLength()>`\  so that it's expected to execute, inside the VM with the given MIPS capacity, for the same time as specified into the workload reader.
   :throws IllegalArgumentException: when the workload trace file name is null or empty; or the resource PE mips <= 0
   :throws UncheckedIOException: when the file cannot be accessed (such as when it doesn't exist)

getMips
^^^^^^^

.. java:method:: public int getMips()
   :outertype: SwfWorkloadFileReader

   Gets the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run. Considering the workload reader provides the run time for each application registered inside the reader, the MIPS value will be used to compute the \ :java:ref:`length of the Cloudlet (in MI) <Cloudlet.getLength()>`\  so that it's expected to execute, inside the VM with the given MIPS capacity, for the same time as specified into the workload reader.

setMips
^^^^^^^

.. java:method:: public SwfWorkloadFileReader setMips(int mips)
   :outertype: SwfWorkloadFileReader

   Sets the MIPS capacity of the PEs from the VM where each created Cloudlet is supposed to run. Considering the workload reader provides the run time for each application registered inside the reader, the MIPS value will be used to compute the \ :java:ref:`length of the Cloudlet (in MI) <Cloudlet.getLength()>`\  so that it's expected to execute, inside the VM with the given MIPS capacity, for the same time as specified into the workload reader.

   :param mips: the MIPS value to set

setPredicate
^^^^^^^^^^^^

.. java:method:: public SwfWorkloadFileReader setPredicate(Predicate<Cloudlet> predicate)
   :outertype: SwfWorkloadFileReader

   Defines a \ :java:ref:`Predicate`\  which indicates when a \ :java:ref:`Cloudlet`\  must be created from a trace line read from the workload file. If a Predicate is not set, a Cloudlet will be created for any line read.

   :param predicate: the predicate to define when a Cloudlet must be created from a line read from the workload file

