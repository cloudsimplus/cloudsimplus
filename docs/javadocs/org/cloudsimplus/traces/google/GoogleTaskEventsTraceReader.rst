.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core.events CloudSimEvent

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelDynamic

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: java.io IOException

.. java:import:: java.io InputStream

.. java:import:: java.io UncheckedIOException

.. java:import:: java.nio.file Files

.. java:import:: java.nio.file Paths

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

GoogleTaskEventsTraceReader
===========================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public final class GoogleTaskEventsTraceReader extends GoogleTraceReaderAbstract<Cloudlet>

   Process "task events" trace files from \ `Google Cluster Data <https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md>`_\  to create \ :java:ref:`Cloudlet`\ s belonging to cloud customers (users). Customers are represented as \ :java:ref:`DatacenterBroker`\  instances created from the trace file. The trace files are the ones inside the task_events sub-directory of downloaded Google traces. The instructions to download the traces are provided in the link above.

   The class also creates the required brokers to represent the customers (users) defined by the username field inside the trace file.

   A spreadsheet that makes it easier to understand the structure of trace files is provided in docs/google-cluster-data-samples.xlsx

   The documentation for fields and values were obtained from the Google Cluster trace documentation in the link above. It's strongly recommended to read such a documentation before trying to use this class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`.process()`

Constructors
------------
GoogleTaskEventsTraceReader
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public GoogleTaskEventsTraceReader(CloudSim simulation, String filePath, Function<TaskEvent, Cloudlet> cloudletCreationFunction) throws IOException
   :outertype: GoogleTaskEventsTraceReader

   Instantiates a \ :java:ref:`GoogleTaskEventsTraceReader`\  to read a "task events" file.

   :param simulation: the simulation instance that the created tasks and brokers will belong to.
   :param filePath: the workload trace \ **relative file name**\  in one of the following formats: \ *ASCII text, zip, gz.*\
   :param cloudletCreationFunction: A \ :java:ref:`Function`\  that will be called for every \ :java:ref:`Cloudlet`\  to be created from a line inside the trace file. The \ :java:ref:`Function`\  will receive a \ :java:ref:`TaskEvent`\  object containing the task data read from the trace and must return a new Cloudlet according to such data.
   :throws IllegalArgumentException: when the trace file name is null or empty
   :throws UncheckedIOException: when the file cannot be accessed (such as when it doesn't exist)

   **See also:** :java:ref:`.process()`

Methods
-------
createBrokerIfAbsent
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected DatacenterBroker createBrokerIfAbsent(String username)
   :outertype: GoogleTaskEventsTraceReader

   Creates a new broker if a previous one with the specified username was not created

   :param username: the username of the broker
   :return: an already existing broker with the given username or a new one if there was no broker with such an username

createCloudlet
^^^^^^^^^^^^^^

.. java:method:: protected Cloudlet createCloudlet(TaskEvent taskEvent)
   :outertype: GoogleTaskEventsTraceReader

createTaskEventFromTraceLine
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected TaskEvent createTaskEventFromTraceLine()
   :outertype: GoogleTaskEventsTraceReader

getBroker
^^^^^^^^^

.. java:method:: protected DatacenterBroker getBroker()
   :outertype: GoogleTaskEventsTraceReader

   Gets an \ :java:ref:`DatacenterBroker`\  instance representing the username from the last trace line read.

   :return: the \ :java:ref:`DatacenterBroker`\  instance

getBrokers
^^^^^^^^^^

.. java:method:: public List<DatacenterBroker> getBrokers()
   :outertype: GoogleTaskEventsTraceReader

   Gets the List of brokers created according to the username from the trace file, representing a customer.

getCloudletCreationFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Function<TaskEvent, Cloudlet> getCloudletCreationFunction()
   :outertype: GoogleTaskEventsTraceReader

   Gets a \ :java:ref:`Function`\  that will be called for every \ :java:ref:`Cloudlet`\  to be created from a line inside the trace file.

   **See also:** :java:ref:`.setCloudletCreationFunction(Function)`

getInstance
^^^^^^^^^^^

.. java:method:: public static GoogleTaskEventsTraceReader getInstance(CloudSim simulation, String filePath, Function<TaskEvent, Cloudlet> cloudletCreationFunction)
   :outertype: GoogleTaskEventsTraceReader

   Gets a \ :java:ref:`GoogleTaskEventsTraceReader`\  instance to read a "task events" trace file inside the \ **application's resource directory**\ .

   :param simulation: the simulation instance that the created tasks and brokers will belong to.
   :param filePath: the workload trace \ **relative file name**\  in one of the following formats: \ *ASCII text, zip, gz.*\
   :param cloudletCreationFunction: A \ :java:ref:`Function`\  that will be called for every \ :java:ref:`Cloudlet`\  to be created from a line inside the trace file. The \ :java:ref:`Function`\  will receive a \ :java:ref:`TaskEvent`\  object containing the task data read from the trace and must return a new Cloudlet according to such data.
   :throws IllegalArgumentException: when the trace file name is null or empty
   :throws UncheckedIOException: when the file cannot be accessed (such as when it doesn't exist)

   **See also:** :java:ref:`.process()`

getSimulation
^^^^^^^^^^^^^

.. java:method:: public Simulation getSimulation()
   :outertype: GoogleTaskEventsTraceReader

postProcess
^^^^^^^^^^^

.. java:method:: @Override protected void postProcess()
   :outertype: GoogleTaskEventsTraceReader

preProcess
^^^^^^^^^^

.. java:method:: @Override protected void preProcess()
   :outertype: GoogleTaskEventsTraceReader

   There is no pre-process requirements for this implementation.

process
^^^^^^^

.. java:method:: @Override public Set<Cloudlet> process()
   :outertype: GoogleTaskEventsTraceReader

   Process the \ :java:ref:`trace file <getFilePath()>`\  creating a Set of \ :java:ref:`Cloudlet`\ s described in the file. Each created Cloudlet is automatically submitted to its respective
   broker.

   It returns the Set of all submitted \ :java:ref:`Cloudlet`\ s at any timestamp inside the trace file (the timestamp is used to delay the Cloudlet submission).

   :return: the Set of all submitted \ :java:ref:`Cloudlet`\ s for any timestamp inside the trace file.

   **See also:** :java:ref:`.getBrokers()`

processParsedLineInternal
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean processParsedLineInternal()
   :outertype: GoogleTaskEventsTraceReader

requestCloudletStatusChange
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean requestCloudletStatusChange(BiFunction<DatacenterBroker, Long, Optional<Cloudlet>> cloudletLookupFunction, int tag)
   :outertype: GoogleTaskEventsTraceReader

   Send a message to the broker to request change in a Cloudlet status, using some tags from \ :java:ref:`CloudSimTags`\  such as \ :java:ref:`CloudSimTags.CLOUDLET_READY`\ .

   :param cloudletLookupFunction: a \ :java:ref:`BiFunction`\  that receives the broker to find the Cloudlet into and the unique ID of the Cloudlet (task), so that the Cloudlet status can be changed
   :param tag: a tag from the \ :java:ref:`CloudSimTags`\  used to send a message to request the Cloudlet status change, such as \ :java:ref:`CloudSimTags.CLOUDLET_FINISH`\
   :return: true if the request was created, false otherwise

setCloudletCreationFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setCloudletCreationFunction(Function<TaskEvent, Cloudlet> cloudletCreationFunction)
   :outertype: GoogleTaskEventsTraceReader

   Sets a \ :java:ref:`Function`\  that will be called for every \ :java:ref:`Cloudlet`\  to be created from a line inside the trace file. The \ :java:ref:`Function`\  will receive a \ :java:ref:`TaskEvent`\  object containing the task data read from the trace and should the created Cloudlet. The provided function must instantiate the Host and defines Host's CPU cores and RAM capacity according the the received parameters. For other Hosts configurations (such as storage capacity), the provided function must define the value as desired, since the trace file doesn't have any other information for such resources.

   :param cloudletCreationFunction: the \ :java:ref:`Function`\  to set

