.. java:import:: org.apache.commons.lang3 BooleanUtils

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecutionInfo

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.network IcmpPacket

.. java:import:: org.cloudbus.cloudsim.util DataCloudTags

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.resources File

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.resources FileStorage

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

DatacenterSimple
================

.. java:package:: org.cloudbus.cloudsim.datacenters
   :noindex:

.. java:type:: public class DatacenterSimple extends CloudSimEntity implements Datacenter

   Implements the basic features of a Virtualized Cloud Datacenter. It deals with processing of VM queries (i.e., handling of VMs) instead of processing Cloudlet-related queries.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

Constructors
------------
DatacenterSimple
^^^^^^^^^^^^^^^^

.. java:constructor:: @Deprecated public DatacenterSimple(Simulation simulation, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<FileStorage> storageList, double schedulingInterval)
   :outertype: DatacenterSimple

   Creates a Datacenter with the given parameters.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param characteristics: the characteristics of the Datacenter to be created
   :param storageList: a List of storage elements, for data simulation
   :param vmAllocationPolicy: the policy to be used to allocate VMs into hosts
   :param schedulingInterval: the scheduling interval to process each Datacenter received event (in seconds)
   :throws IllegalArgumentException: when this entity has \ ``zero``\  number of PEs (Processing Elements). No PEs mean the Cloudlets can't be processed. A CloudResource must contain one or more Machines. A Machine must contain one or more PEs.

DatacenterSimple
^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterSimple(Simulation simulation, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy)
   :outertype: DatacenterSimple

   Creates a Datacenter.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param characteristics: the characteristics of the Datacenter to be created
   :param vmAllocationPolicy: the policy to be used to allocate VMs into hosts
   :throws IllegalArgumentException: when this entity has \ ``zero``\  number of PEs (Processing Elements).  No PEs mean the Cloudlets can't be processed. A CloudResource must contain one or more Machines. A Machine must contain one or more PEs.

Methods
-------
addFile
^^^^^^^

.. java:method:: @Override public int addFile(File file)
   :outertype: DatacenterSimple

checkCloudletsCompletionForAllHosts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void checkCloudletsCompletionForAllHosts()
   :outertype: DatacenterSimple

   Verifies if some cloudlet inside the hosts of this Datacenter have already finished. If yes, send them to the User/Broker

checkCloudletsCompletionForGivenHost
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void checkCloudletsCompletionForGivenHost(Host host)
   :outertype: DatacenterSimple

checkCloudletsCompletionForGivenVm
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void checkCloudletsCompletionForGivenVm(Vm vm)
   :outertype: DatacenterSimple

contains
^^^^^^^^

.. java:method:: protected boolean contains(File file)
   :outertype: DatacenterSimple

   Checks whether the Datacenter has the given file.

   :param file: a file to be searched
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

contains
^^^^^^^^

.. java:method:: protected boolean contains(String fileName)
   :outertype: DatacenterSimple

   Checks whether the Datacenter has the given file.

   :param fileName: a file name to be searched
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

equals
^^^^^^

.. java:method:: @Override public boolean equals(Object o)
   :outertype: DatacenterSimple

getCharacteristics
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics getCharacteristics()
   :outertype: DatacenterSimple

getCloudletProcessingUpdateInterval
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getCloudletProcessingUpdateInterval(double nextFinishingCloudletTime)
   :outertype: DatacenterSimple

   Gets the time when the next update of cloudlets has to be performed. This is the minimum value between the \ :java:ref:`getSchedulingInterval()`\  and the given time (if the scheduling interval is enable, i.e. if it's greater than 0), which represents when the next update of Cloudlets processing has to be performed.

   :param nextFinishingCloudletTime: the predicted completion time of the earliest finishing cloudlet (which is a relative delay from the current simulation time), or \ :java:ref:`Double.MAX_VALUE`\  if there is no next Cloudlet to execute
   :return: next time cloudlets processing will be updated

   **See also:** :java:ref:`.updateCloudletProcessing()`

getHost
^^^^^^^

.. java:method:: @Override public Host getHost(int index)
   :outertype: DatacenterSimple

getHostList
^^^^^^^^^^^

.. java:method:: @Override public <T extends Host> List<T> getHostList()
   :outertype: DatacenterSimple

getLastProcessTime
^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getLastProcessTime()
   :outertype: DatacenterSimple

   Gets the last time some cloudlet was processed in the Datacenter.

   :return: the last process time

getRegionalCisName
^^^^^^^^^^^^^^^^^^

.. java:method:: protected String getRegionalCisName()
   :outertype: DatacenterSimple

   Gets the regional Cloud Information Service (CIS) name.

   :return: the regional CIS name

   **See also:** :java:ref:`org.cloudbus.cloudsim.core.CloudInformationService`

getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSchedulingInterval()
   :outertype: DatacenterSimple

getStorageList
^^^^^^^^^^^^^^

.. java:method:: @Override public List<FileStorage> getStorageList()
   :outertype: DatacenterSimple

getVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VmAllocationPolicy getVmAllocationPolicy()
   :outertype: DatacenterSimple

getVmList
^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmList()
   :outertype: DatacenterSimple

hashCode
^^^^^^^^

.. java:method:: @Override public int hashCode()
   :outertype: DatacenterSimple

isTimeToUpdateCloudletsProcessing
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isTimeToUpdateCloudletsProcessing()
   :outertype: DatacenterSimple

predictFileTransferTime
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double predictFileTransferTime(List<String> requiredFiles)
   :outertype: DatacenterSimple

   Predict the total time to transfer a list of files.

   :param requiredFiles: the files to be transferred
   :return: the predicted time

processCloudlet
^^^^^^^^^^^^^^^

.. java:method:: protected void processCloudlet(SimEvent ev, int type)
   :outertype: DatacenterSimple

   Processes a Cloudlet based on the event type.

   :param ev: information about the event just happened
   :param type: event type

processCloudletCancel
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processCloudletCancel(Cloudlet cloudlet)
   :outertype: DatacenterSimple

   Processes a Cloudlet cancel request.

   :param cloudlet: cloudlet to be canceled

processCloudletMove
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processCloudletMove(Object[] receivedData, int type)
   :outertype: DatacenterSimple

   Process the event for an User/Broker who wants to move a Cloudlet.

   :param receivedData: an Object array containing data about the migration, where the index 0 will be a Cloudlet and the index 1 will be the id of the destination VM
   :param type: event type

processCloudletPause
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processCloudletPause(Cloudlet cloudlet, boolean ack)
   :outertype: DatacenterSimple

   Processes a Cloudlet pause request.

   :param cloudlet: cloudlet to be paused
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

processCloudletResume
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processCloudletResume(Cloudlet cloudlet, boolean ack)
   :outertype: DatacenterSimple

   Processes a Cloudlet resume request.

   :param cloudlet: cloudlet to be resumed
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

processCloudletSubmit
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processCloudletSubmit(SimEvent ev, boolean ack)
   :outertype: DatacenterSimple

   Processes the submission of a Cloudlet by a DatacenterBroker.

   :param ev: information about the event just happened
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

processDataAdd
^^^^^^^^^^^^^^

.. java:method:: protected void processDataAdd(SimEvent ev, boolean ack)
   :outertype: DatacenterSimple

   Process a file inclusion request.

   :param ev: information about the event just happened
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

processDataDelete
^^^^^^^^^^^^^^^^^

.. java:method:: protected void processDataDelete(SimEvent ev, boolean ack)
   :outertype: DatacenterSimple

   Process a file deletion request.

   :param ev: information about the event just happened
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent ev)
   :outertype: DatacenterSimple

processOtherEvent
^^^^^^^^^^^^^^^^^

.. java:method:: protected void processOtherEvent(SimEvent ev)
   :outertype: DatacenterSimple

   Process non-default received events that aren't processed by the \ :java:ref:`processEvent(SimEvent)`\  method. This method should be overridden by subclasses in other to process new defined events.

   :param ev: information about the event just happened

processPingRequest
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processPingRequest(SimEvent ev)
   :outertype: DatacenterSimple

   Processes a ping request.

   :param ev: information about the event just happened

processVmCreate
^^^^^^^^^^^^^^^

.. java:method:: protected boolean processVmCreate(SimEvent ev, boolean ackRequested)
   :outertype: DatacenterSimple

   Process the event for a Broker which wants to create a VM in this Datacenter. This Datacenter will then send the status back to the Broker.

   :param ev: information about the event just happened
   :param ackRequested: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed
   :return: true if a host was allocated to the VM; false otherwise

processVmDestroy
^^^^^^^^^^^^^^^^

.. java:method:: protected void processVmDestroy(SimEvent ev, boolean ack)
   :outertype: DatacenterSimple

   Process the event sent by a Broker, requesting the destruction of a given VM created in this Datacenter. This Datacenter may send, upon request, the status back to the Broker.

   :param ev: information about the event just happened
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

processVmMigrate
^^^^^^^^^^^^^^^^

.. java:method:: protected void processVmMigrate(SimEvent ev, boolean ack)
   :outertype: DatacenterSimple

   Process the event from the Datacenter to migrate a VM.

   :param ev: information about the event just happened
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

setCharacteristics
^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setCharacteristics(DatacenterCharacteristics characteristics)
   :outertype: DatacenterSimple

   Sets the Datacenter characteristics.

   :param characteristics: the new Datacenter characteristics

setLastProcessTime
^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setLastProcessTime(double lastProcessTime)
   :outertype: DatacenterSimple

   Sets the last time some cloudlet was processed in the Datacenter.

   :param lastProcessTime: the new last process time

setRegionalCisName
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void setRegionalCisName(String regionalCisName)
   :outertype: DatacenterSimple

   Sets the regional Cloud Information Service (CIS) name.

   :param regionalCisName: the new regional CIS name

setSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Datacenter setSchedulingInterval(double schedulingInterval)
   :outertype: DatacenterSimple

setStorageList
^^^^^^^^^^^^^^

.. java:method:: @Override public final Datacenter setStorageList(List<FileStorage> storageList)
   :outertype: DatacenterSimple

   Sets the list of storage devices of the Datacenter.

   :param storageList: the new storage list

setVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final Datacenter setVmAllocationPolicy(VmAllocationPolicy vmAllocationPolicy)
   :outertype: DatacenterSimple

   Sets the policy to be used by the Datacenter to allocate VMs into hosts.

   :param vmAllocationPolicy: the new vm allocation policy

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override public void shutdownEntity()
   :outertype: DatacenterSimple

startEntity
^^^^^^^^^^^

.. java:method:: @Override protected void startEntity()
   :outertype: DatacenterSimple

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: DatacenterSimple

updateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double updateCloudletProcessing()
   :outertype: DatacenterSimple

   Updates processing of each Host, that fires the update of VMs, which in turn updates cloudlets running in this Datacenter. After that, the method schedules the next processing update. It is necessary because Hosts and VMs are simple objects, not entities. So, they don't receive events and updating cloudlets inside them must be called from the outside.

   :return: the predicted completion time of the earliest finishing cloudlet (which is a relative delay from the current simulation time), or \ :java:ref:`Double.MAX_VALUE`\  if there is no next Cloudlet to execute or it isn't time to update the cloudlets

updateHostsProcessing
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double updateHostsProcessing()
   :outertype: DatacenterSimple

   Updates the processing of all Hosts, that means that makes the processing of VMs running inside such hosts to be updated. Finally, the processing of Cloudlets running inside such VMs is updated.

   :return: the predicted completion time of the earliest finishing cloudlet (which is a relative delay from the current simulation time), or \ :java:ref:`Double.MAX_VALUE`\  if there is no next Cloudlet to execute

