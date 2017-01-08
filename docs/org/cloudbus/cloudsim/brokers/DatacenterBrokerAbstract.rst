.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

DatacenterBrokerAbstract
========================

.. java:package:: org.cloudbus.cloudsim.brokers
   :noindex:

.. java:type:: public abstract class DatacenterBrokerAbstract extends CloudSimEntity implements DatacenterBroker

   An abstract class to be used as base for implementing a \ :java:ref:`DatacenterBroker`\ .

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
DatacenterBrokerAbstract
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterBrokerAbstract(CloudSim simulation)
   :outertype: DatacenterBrokerAbstract

   Creates a new DatacenterBroker object.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to

Methods
-------
bindCloudletToVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm)
   :outertype: DatacenterBrokerAbstract

destroyVms
^^^^^^^^^^

.. java:method:: protected void destroyVms()
   :outertype: DatacenterBrokerAbstract

   Destroy all created broker's VMs.

finishExecution
^^^^^^^^^^^^^^^

.. java:method:: protected void finishExecution()
   :outertype: DatacenterBrokerAbstract

   Send an internal event communicating the end of the simulation.

getCloudletsCreated
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected int getCloudletsCreated()
   :outertype: DatacenterBrokerAbstract

   Gets the total number of cloudlets created inside some Vm.

getCloudletsFinishedList
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Cloudlet> List<T> getCloudletsFinishedList()
   :outertype: DatacenterBrokerAbstract

getCloudletsWaitingList
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Cloudlet> List<T> getCloudletsWaitingList()
   :outertype: DatacenterBrokerAbstract

getDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Datacenter> getDatacenterList()
   :outertype: DatacenterBrokerAbstract

   Gets the list of available datacenters.

   :return: the dc list

getDatacenterRequestedList
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Set<Datacenter> getDatacenterRequestedList()
   :outertype: DatacenterBrokerAbstract

   Gets the list of datacenters where was requested to place VMs.

getLastSelectedVm
^^^^^^^^^^^^^^^^^

.. java:method:: protected Vm getLastSelectedVm()
   :outertype: DatacenterBrokerAbstract

   :return: latest VM selected to run a cloudlet.

getNumberOfCloudletCreationRequests
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfCloudletCreationRequests()
   :outertype: DatacenterBrokerAbstract

getVmCreationAcks
^^^^^^^^^^^^^^^^^

.. java:method:: protected int getVmCreationAcks()
   :outertype: DatacenterBrokerAbstract

   Gets the number of acknowledges (ACKs) received from Datacenters in response to requests to create VMs. The number of acks doesn't mean the number of created VMs, once Datacenters can respond informing that a Vm could not be created.

   :return: the number vm creation acks

getVmCreationRequests
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected int getVmCreationRequests()
   :outertype: DatacenterBrokerAbstract

   Gets the number of VM creation requests.

   :return: the number of VM creation requests

getVmDatacenter
^^^^^^^^^^^^^^^

.. java:method:: protected Datacenter getVmDatacenter(Vm vm)
   :outertype: DatacenterBrokerAbstract

   Gets the Datacenter where a VM is placed.

   :param vm: the VM to get its Datacenter

getVmFromCreatedList
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Vm getVmFromCreatedList(int vmIndex)
   :outertype: DatacenterBrokerAbstract

   Gets a Vm at a given index from the \ :java:ref:`list of created VMs <getVmsCreatedList()>`\ .

   :param vmIndex: the index where a VM has to be got from the created VM list
   :return: the VM at the given index or \ :java:ref:`Vm.NULL`\  if the index is invalid

getVmsCreatedList
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmsCreatedList()
   :outertype: DatacenterBrokerAbstract

getVmsToDatacentersMap
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Map<Vm, Datacenter> getVmsToDatacentersMap()
   :outertype: DatacenterBrokerAbstract

   Gets the VM to Datacenter map, where each key is a VM and each value is the Datacenter where the VM is placed.

   :return: the VM to Datacenter map

getVmsWaitingList
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmsWaitingList()
   :outertype: DatacenterBrokerAbstract

getWaitingVm
^^^^^^^^^^^^

.. java:method:: @Override public Vm getWaitingVm(int index)
   :outertype: DatacenterBrokerAbstract

hasMoreCloudletsToBeExecuted
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean hasMoreCloudletsToBeExecuted()
   :outertype: DatacenterBrokerAbstract

processCloudletReturn
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processCloudletReturn(SimEvent ev)
   :outertype: DatacenterBrokerAbstract

   Processes the end of execution of a given cloudlet inside a Vm.

   :param ev: The cloudlet that has just finished to execute

processDatacenterListRequest
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processDatacenterListRequest(SimEvent ev)
   :outertype: DatacenterBrokerAbstract

   Process a request for the list of all Datacenters registered in the Cloud Information Service (CIS) of the \ :java:ref:`simulation <getSimulation()>`\ .

   :param ev: a CloudSimEvent object

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent ev)
   :outertype: DatacenterBrokerAbstract

processFailedVmCreationInDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processFailedVmCreationInDatacenter(Vm vm, Datacenter datacenter)
   :outertype: DatacenterBrokerAbstract

   Process a response from a Datacenter informing that it was NOT able to create the VM requested by the broker.

   :param vm: id of the Vm that failed to be created inside the Datacenter
   :param datacenter: id of the Datacenter where the request to create

processOtherEvent
^^^^^^^^^^^^^^^^^

.. java:method:: protected void processOtherEvent(SimEvent ev)
   :outertype: DatacenterBrokerAbstract

   Process non-default received events that aren't processed by the \ :java:ref:`processEvent(SimEvent)`\  method. This method should be overridden by subclasses if they really want to process new defined events.

   :param ev: a CloudSimEvent object

processSuccessVmCreationInDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void processSuccessVmCreationInDatacenter(Vm vm, Datacenter datacenter)
   :outertype: DatacenterBrokerAbstract

   Process a response from a Datacenter informing that it was able to create the VM requested by the broker.

   :param vm: id of the Vm that succeeded to be created inside the Datacenter
   :param datacenter: id of the Datacenter where the request to create the Vm succeeded

processVmCreateResponseFromDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean processVmCreateResponseFromDatacenter(SimEvent ev)
   :outertype: DatacenterBrokerAbstract

   Process the ack received from a Datacenter to a broker's request for creation of a Vm in that Datacenter.

   :param ev: a CloudSimEvent object
   :return: true if the VM was created successfully, false otherwise

requestCreationOfWaitingVmsToNextDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void requestCreationOfWaitingVmsToNextDatacenter()
   :outertype: DatacenterBrokerAbstract

   After the response (ack) of all VM creation request were received but not all VMs could be created (what means some acks informed about Vm creation failures), try to find another Datacenter to request the creation of the VMs in the waiting list.

requestDatacenterToCreateWaitingVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void requestDatacenterToCreateWaitingVms()
   :outertype: DatacenterBrokerAbstract

   Request the \ :java:ref:`next Datacenter in the list <selectDatacenterForWaitingVms()>`\  to create the VM in the \ :java:ref:`VM waiting list <getVmsWaitingList()>`\ .

   **See also:** :java:ref:`.submitVmList(java.util.List)`

requestDatacenterToCreateWaitingVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void requestDatacenterToCreateWaitingVms(Datacenter datacenter)
   :outertype: DatacenterBrokerAbstract

   Request a Datacenter to create the VM in the \ :java:ref:`VM waiting list <getVmsWaitingList()>`\ .

   :param datacenter: id of the Datacenter to request the VMs creation

   **See also:** :java:ref:`.submitVmList(java.util.List)`

requestDatacentersToCreateWaitingCloudlets
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void requestDatacentersToCreateWaitingCloudlets()
   :outertype: DatacenterBrokerAbstract

   Request Datacenters to create the Cloudlets in the \ :java:ref:`Cloudlets waiting list <getCloudletsWaitingList()>`\ . If there aren't available VMs to host all cloudlets, the creation of some ones will be postponed.

   This method is called after all submitted VMs are created in some Datacenter.

   **See also:** :java:ref:`.submitCloudletList(java.util.List)`

setDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setDatacenterList(Set<Datacenter> datacenterList)
   :outertype: DatacenterBrokerAbstract

   Sets the list of available datacenters.

   :param datacenterList: the new dc list

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override public void shutdownEntity()
   :outertype: DatacenterBrokerAbstract

startEntity
^^^^^^^^^^^

.. java:method:: @Override public void startEntity()
   :outertype: DatacenterBrokerAbstract

submitCloudlet
^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudlet(Cloudlet cloudlet)
   :outertype: DatacenterBrokerAbstract

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list)
   :outertype: DatacenterBrokerAbstract

   {@inheritDoc}

   If the entity already started (the simulation is running), the creation of previously submitted Cloudlets already was requested by the \ :java:ref:`start()`\  method that is called just once. By this way, this method will immediately request the creation of these just submitted Cloudlets if all submitted VMs were already created, in order to allow Cloudlet creation after the simulation has started. This avoid the developer to dynamically create brokers just to create VMs or Cloudlets during simulation execution.

   :param {@inheritDoc}:

   **See also:** :java:ref:`.submitCloudletList(List,double)`

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay)
   :outertype: DatacenterBrokerAbstract

submitVm
^^^^^^^^

.. java:method:: @Override public void submitVm(Vm vm)
   :outertype: DatacenterBrokerAbstract

submitVmList
^^^^^^^^^^^^

.. java:method:: @Override public void submitVmList(List<? extends Vm> list, double submissionDelay)
   :outertype: DatacenterBrokerAbstract

submitVmList
^^^^^^^^^^^^

.. java:method:: @Override public void submitVmList(List<? extends Vm> list)
   :outertype: DatacenterBrokerAbstract

   {@inheritDoc}

   If the entity already started (the simulation is running), the creation of previously submitted VMs already was requested by the \ :java:ref:`start()`\  method that is called just once. By this way, this method will immediately request the creation of these just submitted VMs in order to allow VM creation after the simulation has started. This avoid the developer to dynamically create brokers just to create VMs or Cloudlets during simulation execution.

   :param {@inheritDoc}:

