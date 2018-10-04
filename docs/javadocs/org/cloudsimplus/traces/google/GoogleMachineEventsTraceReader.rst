.. java:import:: org.cloudbus.cloudsim.core CloudInformationService

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io IOException

.. java:import:: java.io InputStream

.. java:import:: java.io UncheckedIOException

.. java:import:: java.nio.file Files

.. java:import:: java.nio.file Paths

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Set

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

GoogleMachineEventsTraceReader
==============================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public final class GoogleMachineEventsTraceReader extends GoogleTraceReaderAbstract<Host>

   Process "machine events" trace files from \ `Google Cluster Data <https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md>`_\ . When a trace file is \ :java:ref:`processed <process()>`\ , it creates a list of available \ :java:ref:`Host`\ s for every line with a zero timestamp and the \ :java:ref:`event type <getEventType()>`\  equals to \ :java:ref:`MachineEventType.ADD`\ , meaning that such Hosts will be immediately available at the simulation start. Hosts addition events with timestamp greater than zero will be scheduled to be added just at the specified type. In the same way, Hosts removal are accordingly scheduled.

   Such trace files are the ones inside the machine_events sub-directory of downloaded Google traces. The instructions to download the traces are provided in the link above. A spreadsheet that makes it easier to understand the structure of trace files is provided in docs/google-cluster-data-samples.xlsx

   The documentation for fields and values were obtained from the Google Cluster trace documentation in the link above. It's strongly recommended to read such a documentation before trying to use this class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`.getInstance(String,Function)`, :java:ref:`.process()`

Constructors
------------
GoogleMachineEventsTraceReader
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public GoogleMachineEventsTraceReader(String filePath, Function<MachineEvent, Host> hostCreationFunction) throws IOException
   :outertype: GoogleMachineEventsTraceReader

   Instantiates a GoogleMachineEventsTraceReader to read a "machine events" trace file. Created Hosts will have 16GB of maximum RAM and the maximum of 8 \ :java:ref:`Pe`\ s.

   :param filePath: the path to the trace file
   :param hostCreationFunction: A \ :java:ref:`Function`\  that will be called for every \ :java:ref:`Host`\  to be created from a line inside the trace file. The \ :java:ref:`Function`\  will receive a \ :java:ref:`MachineEvent`\  object containing the Host data read from the trace and must return the created Host according to such data.
   :throws IllegalArgumentException: when the trace file name is null or empty
   :throws FileNotFoundException: when the trace file is not found

   **See also:** :java:ref:`.setMaxRamCapacity(long)`, :java:ref:`.setMaxCpuCores(int)`, :java:ref:`.process()`

Methods
-------
addHostToRemovalList
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean addHostToRemovalList(Host host)
   :outertype: GoogleMachineEventsTraceReader

   Adds a Host to the List of Hosts to be removed from the Datacenter.

   :param host:

addLaterAvailableHost
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean addLaterAvailableHost(Host host)
   :outertype: GoogleMachineEventsTraceReader

   Adds a Host that will become available for the Datacenter just at the time specified by the timestamp in the trace line, which is set as the host \ :java:ref:`startup time <Host.getStartTime()>`\ .

   :param host: the Host to be added

createHostFromTraceLine
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Host createHostFromTraceLine()
   :outertype: GoogleMachineEventsTraceReader

   Creates a Host instance from the \ :java:ref:`last parsed line <getLastParsedLineArray()>`\ , using the given \ :java:ref:`host create function <setHostCreationFunction(Function)>`\ .

   :return: the Host instance

getDatacenterForLaterHosts
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Datacenter getDatacenterForLaterHosts()
   :outertype: GoogleMachineEventsTraceReader

   Gets the Datacenter where the Hosts with timestamp greater than 0 will be created.

getInstance
^^^^^^^^^^^

.. java:method:: public static GoogleMachineEventsTraceReader getInstance(String filePath, Function<MachineEvent, Host> hostCreationFunction)
   :outertype: GoogleMachineEventsTraceReader

   Gets a \ :java:ref:`GoogleMachineEventsTraceReader`\  instance to read a "machine events" trace file inside the \ **application's resource directory**\ . Created Hosts will have 16GB of maximum RAM and the maximum of 8 \ :java:ref:`Pe`\ s. Use the available constructors if you want to load a file outside the resource directory.

   :param filePath: the workload trace \ **relative file name**\  in one of the following formats: \ *ASCII text, zip, gz.*\
   :param hostCreationFunction: A \ :java:ref:`Function`\  that will be called for every \ :java:ref:`Host`\  to be created from a line inside the trace file. The \ :java:ref:`Function`\  will receive a \ :java:ref:`MachineEvent`\  object containing the Host data read from the trace and must return the created Host according to such data.
   :throws IllegalArgumentException: when the trace file name is null or empty
   :throws UncheckedIOException: when the file cannot be accessed (such as when it doesn't exist)

   **See also:** :java:ref:`.setMaxRamCapacity(long)`, :java:ref:`.setMaxCpuCores(int)`, :java:ref:`.process()`

getMaxCpuCores
^^^^^^^^^^^^^^

.. java:method:: public int getMaxCpuCores()
   :outertype: GoogleMachineEventsTraceReader

   Gets the maximum number of \ :java:ref:`Pe`\ s (CPU cores) for created Hosts.

getMaxRamCapacity
^^^^^^^^^^^^^^^^^

.. java:method:: public long getMaxRamCapacity()
   :outertype: GoogleMachineEventsTraceReader

   Gets the maximum RAM capacity (in MB) for created Hosts.

getNumberOfHostsForRemoval
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getNumberOfHostsForRemoval()
   :outertype: GoogleMachineEventsTraceReader

   Gets the number of Hosts to be removed from some Datacenter.

getNumberOfLaterAvailableHosts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getNumberOfLaterAvailableHosts()
   :outertype: GoogleMachineEventsTraceReader

   Gets the number of Hosts that are going to be created later, according to the timestamp in the trace file.

postProcess
^^^^^^^^^^^

.. java:method:: @Override protected void postProcess()
   :outertype: GoogleMachineEventsTraceReader

   Process hosts events occurring for a timestamp greater than zero.

preProcess
^^^^^^^^^^

.. java:method:: @Override protected void preProcess()
   :outertype: GoogleMachineEventsTraceReader

process
^^^^^^^

.. java:method:: @Override public Set<Host> process()
   :outertype: GoogleMachineEventsTraceReader

   Process the \ :java:ref:`trace file <getFilePath()>`\  creating a Set of \ :java:ref:`Host`\ s described in the file.

   It returns the Set of \ :java:ref:`Host`\ s that were available at timestamp 0 inside the trace file. Hosts available just after this initial timestamp (that represents the beginning of the simulation) will be dynamically requested to be created by sending a message to the given Datacenter.

   The Set of returned Hosts is not added to any Datacenter. The developer creating the simulation must add such Hosts to any Datacenter desired.

   :return: the Set of \ :java:ref:`Host`\ s that were available at timestamp 0 inside the trace file.

processParsedLineInternal
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean processParsedLineInternal()
   :outertype: GoogleMachineEventsTraceReader

setDatacenterForLaterHosts
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDatacenterForLaterHosts(Datacenter datacenterForLaterHosts)
   :outertype: GoogleMachineEventsTraceReader

setHostCreationFunction
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setHostCreationFunction(Function<MachineEvent, Host> hostCreationFunction)
   :outertype: GoogleMachineEventsTraceReader

   Sets a \ :java:ref:`BiFunction`\  that will be called for every \ :java:ref:`Host`\  to be created from a line inside the trace file. The \ :java:ref:`BiFunction`\  will receive the number of \ :java:ref:`Pe`\ s (CPU cores) and RAM capacity for the Host to be created, returning the created Host. The provided function must instantiate the Host and defines Host's CPU cores and RAM capacity according the the received parameters. For other Hosts configurations (such as storage capacity), the provided function must define the value as desired, since the trace file doesn't have any other information for such resources.

   :param hostCreationFunction: the Host creation \ :java:ref:`BiFunction`\  to set

setMaxCpuCores
^^^^^^^^^^^^^^

.. java:method:: public void setMaxCpuCores(int maxCpuCores)
   :outertype: GoogleMachineEventsTraceReader

   Sets the maximum number of \ :java:ref:`Pe`\ s (CPU cores) for created Hosts.

   :param maxCpuCores: the maximum number of \ :java:ref:`Pe`\ s (CPU cores) to set

setMaxRamCapacity
^^^^^^^^^^^^^^^^^

.. java:method:: public void setMaxRamCapacity(long maxRamCapacity)
   :outertype: GoogleMachineEventsTraceReader

   Sets the maximum RAM capacity (in MB) for created Hosts.

   :param maxRamCapacity: the maximum RAM capacity (in MB) to set

