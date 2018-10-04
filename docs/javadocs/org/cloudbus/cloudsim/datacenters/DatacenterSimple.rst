.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicyAbstract

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecution

.. java:import:: org.cloudbus.cloudsim.core CloudSimEntity

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core.events PredicateType

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.network IcmpPacket

.. java:import:: org.cloudbus.cloudsim.resources DatacenterStorage

.. java:import:: org.cloudbus.cloudsim.resources FileStorage

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.util MathUtil

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: org.cloudsimplus.faultinjection HostFaultInjection

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostEventInfo

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

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

.. java:constructor:: public DatacenterSimple(Simulation simulation, VmAllocationPolicy vmAllocationPolicy)
   :outertype: DatacenterSimple

   Creates a Datacenter with an empty \ :java:ref:`storage <getDatacenterStorage()>`\  and no Hosts.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param vmAllocationPolicy: the policy to be used to allocate VMs into hosts

   **See also:** :java:ref:`.DatacenterSimple(Simulation,List,VmAllocationPolicy)`, :java:ref:`.DatacenterSimple(Simulation,List,VmAllocationPolicy,DatacenterStorage)`, :java:ref:`.addHostList(List)`

DatacenterSimple
^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterSimple(Simulation simulation, List<? extends Host> hostList, VmAllocationPolicy vmAllocationPolicy)
   :outertype: DatacenterSimple

   Creates a Datacenter with an empty \ :java:ref:`storage <getDatacenterStorage()>`\ .

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param hostList: list of \ :java:ref:`Host`\ s that will compound the Datacenter
   :param vmAllocationPolicy: the policy to be used to allocate VMs into hosts

   **See also:** :java:ref:`.DatacenterSimple(Simulation,List,VmAllocationPolicy,DatacenterStorage)`

DatacenterSimple
^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterSimple(Simulation simulation, List<? extends Host> hostList, VmAllocationPolicy vmAllocationPolicy, List<FileStorage> storageList)
   :outertype: DatacenterSimple

   Creates a Datacenter attaching a given storage list to its \ :java:ref:`storage <getDatacenterStorage()>`\ .

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param hostList: list of \ :java:ref:`Host`\ s that will compound the Datacenter
   :param vmAllocationPolicy: the policy to be used to allocate VMs into hosts
   :param storageList: the storage list to attach to the \ :java:ref:`datacenter storage <getDatacenterStorage()>`\

DatacenterSimple
^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterSimple(Simulation simulation, List<? extends Host> hostList, VmAllocationPolicy vmAllocationPolicy, DatacenterStorage storage)
   :outertype: DatacenterSimple

   Creates a Datacenter with a given \ :java:ref:`storage <getDatacenterStorage()>`\ .

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param hostList: list of \ :java:ref:`Host`\ s that will compound the Datacenter
   :param vmAllocationPolicy: the policy to be used to allocate VMs into hosts
   :param storage: the \ :java:ref:`storage <getDatacenterStorage()>`\  for this Datacenter

   **See also:** :java:ref:`DatacenterStorage.getStorageList()`

Methods
-------
addHost
^^^^^^^

.. java:method:: @Override public <T extends Host> Datacenter addHost(T host)
   :outertype: DatacenterSimple

addHostList
^^^^^^^^^^^

.. java:method:: @Override public <T extends Host> Datacenter addHostList(List<T> hostList)
   :outertype: DatacenterSimple

addOnHostAvailableListener
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter addOnHostAvailableListener(EventListener<HostEventInfo> listener)
   :outertype: DatacenterSimple

checkCloudletsCompletionForAllHosts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void checkCloudletsCompletionForAllHosts()
   :outertype: DatacenterSimple

   Verifies if some cloudlet inside the hosts of this Datacenter have already finished. If yes, send them to the User/Broker

disableMigrations
^^^^^^^^^^^^^^^^^

.. java:method:: public final Datacenter disableMigrations()
   :outertype: DatacenterSimple

   Disable VM migrations.

enableMigrations
^^^^^^^^^^^^^^^^

.. java:method:: public final Datacenter enableMigrations()
   :outertype: DatacenterSimple

   Enable VM migrations.

equals
^^^^^^

.. java:method:: @Override public boolean equals(Object object)
   :outertype: DatacenterSimple

finishVmMigration
^^^^^^^^^^^^^^^^^

.. java:method:: protected void finishVmMigration(SimEvent evt, boolean ack)
   :outertype: DatacenterSimple

   Finishes the process of migrating a VM.

   :param evt: information about the event just happened
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

getBandwidthPercentForMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getBandwidthPercentForMigration()
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

getDatacenterStorage
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterStorage getDatacenterStorage()
   :outertype: DatacenterSimple

getHost
^^^^^^^

.. java:method:: @Override public Host getHost(int index)
   :outertype: DatacenterSimple

getHostById
^^^^^^^^^^^

.. java:method:: @Override public Host getHostById(long id)
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

getPower
^^^^^^^^

.. java:method:: @Override public double getPower()
   :outertype: DatacenterSimple

getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSchedulingInterval()
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

isMigrationsEnabled
^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isMigrationsEnabled()
   :outertype: DatacenterSimple

   Checks if migrations are enabled.

   :return: true, if migrations are enable; false otherwise

processCloudlet
^^^^^^^^^^^^^^^

.. java:method:: protected void processCloudlet(SimEvent evt, int type)
   :outertype: DatacenterSimple

   Processes a Cloudlet based on the event type.

   :param evt: information about the event just happened
   :param type: event type

processCloudletCancel
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processCloudletCancel(Cloudlet cloudlet)
   :outertype: DatacenterSimple

   Processes a Cloudlet cancel request.

   :param cloudlet: cloudlet to be canceled

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

.. java:method:: protected void processCloudletSubmit(SimEvent evt, boolean ack)
   :outertype: DatacenterSimple

   Processes the submission of a Cloudlet by a DatacenterBroker.

   :param evt: information about the event just happened
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent evt)
   :outertype: DatacenterSimple

processPingRequest
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processPingRequest(SimEvent evt)
   :outertype: DatacenterSimple

   Processes a ping request.

   :param evt: information about the event just happened

processVmCreate
^^^^^^^^^^^^^^^

.. java:method:: protected boolean processVmCreate(SimEvent evt, boolean ackRequested)
   :outertype: DatacenterSimple

   Process the event for a Broker which wants to create a VM in this Datacenter. This Datacenter will then send the status back to the Broker.

   :param evt: information about the event just happened
   :param ackRequested: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed
   :return: true if a host was allocated to the VM; false otherwise

processVmDestroy
^^^^^^^^^^^^^^^^

.. java:method:: protected void processVmDestroy(SimEvent evt, boolean ack)
   :outertype: DatacenterSimple

   Process the event sent by a Broker, requesting the destruction of a given VM created in this Datacenter. This Datacenter may send, upon request, the status back to the Broker.

   :param evt: information about the event just happened
   :param ack: indicates if the event's sender expects to receive an acknowledge message when the event finishes to be processed

removeHost
^^^^^^^^^^

.. java:method:: @Override public <T extends Host> Datacenter removeHost(T host)
   :outertype: DatacenterSimple

setBandwidthPercentForMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setBandwidthPercentForMigration(double bandwidthPercentForMigration)
   :outertype: DatacenterSimple

setDatacenterStorage
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setDatacenterStorage(DatacenterStorage datacenterStorage)
   :outertype: DatacenterSimple

setLastProcessTime
^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setLastProcessTime(double lastProcessTime)
   :outertype: DatacenterSimple

   Sets the last time some cloudlet was processed in the Datacenter.

   :param lastProcessTime: the new last process time

setSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Datacenter setSchedulingInterval(double schedulingInterval)
   :outertype: DatacenterSimple

setVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final Datacenter setVmAllocationPolicy(VmAllocationPolicy vmAllocationPolicy)
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

