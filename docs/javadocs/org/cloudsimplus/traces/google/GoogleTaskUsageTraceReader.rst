.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core.events CloudSimEvent

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelDynamic

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelFull

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: java.io IOException

.. java:import:: java.io InputStream

.. java:import:: java.io UncheckedIOException

.. java:import:: java.nio.file Files

.. java:import:: java.nio.file Paths

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Set

GoogleTaskUsageTraceReader
==========================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public final class GoogleTaskUsageTraceReader extends GoogleTraceReaderAbstract<Cloudlet>

   Process "task usage" trace files from \ `Google Cluster Data <https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md>`_\  to change the resource utilization of \ :java:ref:`Cloudlet`\ s. The trace files are the ones inside the task_usage sub-directory of downloaded Google traces. The instructions to download the traces are provided in the link above.

   A spreadsheet that makes it easier to understand the structure of trace files is provided in docs/google-cluster-data-samples.xlsx

   The documentation for fields and values were obtained from the Google Cluster trace documentation in the link above. It's strongly recommended to read such a documentation before trying to use this class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`.process()`

Constructors
------------
GoogleTaskUsageTraceReader
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public GoogleTaskUsageTraceReader(List<DatacenterBroker> brokers, String filePath) throws IOException
   :outertype: GoogleTaskUsageTraceReader

   Instantiates a \ :java:ref:`GoogleTaskUsageTraceReader`\  to read a "task usage" trace file.

   :param brokers: a list of \ :java:ref:`DatacenterBroker`\ s that own running Cloudlets for which resource usage will be read from the trace.
   :param filePath: the workload trace \ **relative file name**\  in one of the following formats: \ *ASCII text, zip, gz.*\
   :throws IllegalArgumentException: when the trace file name is null or empty
   :throws UncheckedIOException: when the file cannot be accessed (such as when it doesn't exist)

   **See also:** :java:ref:`.process()`

Methods
-------
getInstance
^^^^^^^^^^^

.. java:method:: public static GoogleTaskUsageTraceReader getInstance(List<DatacenterBroker> brokers, String filePath)
   :outertype: GoogleTaskUsageTraceReader

   Gets a \ :java:ref:`GoogleTaskUsageTraceReader`\  instance to read a "task usage" trace file inside the \ **application's resource directory**\ .

   :param brokers: a list of \ :java:ref:`DatacenterBroker`\ s that own running Cloudlets for which resource usage will be read from the trace.
   :param filePath: the workload trace \ **relative file name**\  in one of the following formats: \ *ASCII text, zip, gz.*\
   :throws IllegalArgumentException: when the trace file name is null or empty
   :throws UncheckedIOException: when the file cannot be accessed (such as when it doesn't exist)

   **See also:** :java:ref:`.process()`

postProcess
^^^^^^^^^^^

.. java:method:: @Override protected void postProcess()
   :outertype: GoogleTaskUsageTraceReader

preProcess
^^^^^^^^^^

.. java:method:: @Override protected void preProcess()
   :outertype: GoogleTaskUsageTraceReader

   There is not pre-process for this implementation.

process
^^^^^^^

.. java:method:: @Override public Set<Cloudlet> process()
   :outertype: GoogleTaskUsageTraceReader

   Process the \ :java:ref:`trace file <getFilePath()>`\  request to change the resource usage of \ :java:ref:`Cloudlet`\ s as described in the file. It returns the List of all processed \ :java:ref:`Cloudlet`\ s.

   If the Cloudlets created by a \ :java:ref:`GoogleTaskEventsTraceReader`\  use a \ :java:ref:`UtilizationModelFull`\  to define that the CPUs required by the Cloudlets will be used 100%, when the "task usage" file is read, a different CPU usage can be set. In regular simulations, if this value is smaller, a Cloudlet will spend more time to finish. However, since the "task events" file defines the exact time to finish each Cloudlet, using less than 100% won't make the Cloudlet to finish earlier (as in simulations not using the Google Cluster Data). Each Cloudlet will just have a smaller length at the end of the simulation.

   These trace files don't define the length of the Cloudlet (task). This way, the Cloudlets are created with an indefinite length (see \ :java:ref:`Cloudlet.setLength(long)`\ ) and the length is increased as the Cloudlet is executed. Therefore, if the Cloudlet is using a higher percentage of the CPU capacity, it will execute more instructions in a given time interval. If it's using a lower percentage of the CPU capacity, it will execute less instructions in that interval.

   In conclusion, the exec and finish time of Cloudlets created from Google Cluster trace files won't change according to the percentage of CPU the Cloudlets are using.

   :return: the Set of all \ :java:ref:`Cloudlet`\ s processed according to a line in the trace file

processParsedLineInternal
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean processParsedLineInternal()
   :outertype: GoogleTaskUsageTraceReader

