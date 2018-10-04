.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: org.cloudsimplus.listeners DatacenterBrokerEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.traces.google GoogleTaskEventsTraceReader

.. java:import:: java.util.function Function

.. java:import:: java.util.function Supplier

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

   Creates a DatacenterBroker.

   :param simulation: the CloudSim instance that represents the simulation the Entity is related to

DatacenterBrokerAbstract
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterBrokerAbstract(CloudSim simulation, String name)
   :outertype: DatacenterBrokerAbstract

   Creates a DatacenterBroker giving a specific name.

   :param simulation: the CloudSim instance that represents the simulation the Entity is related to
   :param name: the DatacenterBroker name

Methods
-------
addOnVmsCreatedListener
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener)
   :outertype: DatacenterBrokerAbstract

addOneTimeOnCreationOfWaitingVmsFinishListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public DatacenterBroker addOneTimeOnCreationOfWaitingVmsFinishListener(EventListener<DatacenterBrokerEventInfo> listener, Boolean oneTimeListener)
   :outertype: DatacenterBrokerAbstract

addOneTimeOnVmsCreatedListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker addOneTimeOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener)
   :outertype: DatacenterBrokerAbstract

bindCloudletToVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm)
   :outertype: DatacenterBrokerAbstract

getCloudletCreatedList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Cloudlet> getCloudletCreatedList()
   :outertype: DatacenterBrokerAbstract

getCloudletFinishedList
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Cloudlet> List<T> getCloudletFinishedList()
   :outertype: DatacenterBrokerAbstract

getCloudletSubmittedList
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Cloudlet> getCloudletSubmittedList()
   :outertype: DatacenterBrokerAbstract

getCloudletWaitingList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Cloudlet> List<T> getCloudletWaitingList()
   :outertype: DatacenterBrokerAbstract

getDatacenter
^^^^^^^^^^^^^

.. java:method:: protected Datacenter getDatacenter(Vm vm)
   :outertype: DatacenterBrokerAbstract

   Gets the Datacenter where a VM is placed.

   :param vm: the VM to get its Datacenter

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

getVmCreatedList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmCreatedList()
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

getVmDestructionDelayFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function<Vm, Double> getVmDestructionDelayFunction()
   :outertype: DatacenterBrokerAbstract

getVmExecList
^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmExecList()
   :outertype: DatacenterBrokerAbstract

getVmFromCreatedList
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Vm getVmFromCreatedList(int vmIndex)
   :outertype: DatacenterBrokerAbstract

   Gets a Vm at a given index from the \ :java:ref:`list of created VMs <getVmExecList()>`\ .

   :param vmIndex: the index where a VM has to be got from the created VM list
   :return: the VM at the given index or \ :java:ref:`Vm.NULL`\  if the index is invalid

getVmWaitingList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmWaitingList()
   :outertype: DatacenterBrokerAbstract

getWaitingVm
^^^^^^^^^^^^

.. java:method:: @Override public Vm getWaitingVm(int index)
   :outertype: DatacenterBrokerAbstract

isThereWaitingCloudlets
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isThereWaitingCloudlets()
   :outertype: DatacenterBrokerAbstract

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent evt)
   :outertype: DatacenterBrokerAbstract

requestDatacenterToCreateWaitingVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void requestDatacenterToCreateWaitingVms()
   :outertype: DatacenterBrokerAbstract

   Request the creation of VMs in the \ :java:ref:`VM waiting list <getVmWaitingList()>`\  inside some Datacenter.

   **See also:** :java:ref:`.submitVmList(java.util.List)`

requestDatacenterToCreateWaitingVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void requestDatacenterToCreateWaitingVms(Datacenter datacenter)
   :outertype: DatacenterBrokerAbstract

   Request a specific Datacenter to create the VM in the \ :java:ref:`VM waiting list <getVmWaitingList()>`\ .

   :param datacenter: id of the Datacenter to request the VMs creation

   **See also:** :java:ref:`.submitVmList(java.util.List)`

requestDatacenterToCreateWaitingVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void requestDatacenterToCreateWaitingVms(Datacenter datacenter, boolean isFallbackDatacenter)
   :outertype: DatacenterBrokerAbstract

   Request a specific Datacenter to create the VM in the \ :java:ref:`VM waiting list <getVmWaitingList()>`\ .

   :param datacenter: id of the Datacenter to request the VMs creation
   :param isFallbackDatacenter: true to indicate that the given Datacenter is a fallback one, i.e., it's a next Datacenter where the creation of VMs is being tried (after some VMs could not be created into the previous Datacenter); false to indicate that this is a regular Datacenter where VM creation has to be tried.

   **See also:** :java:ref:`.submitVmList(java.util.List)`

requestDatacentersToCreateWaitingCloudlets
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void requestDatacentersToCreateWaitingCloudlets()
   :outertype: DatacenterBrokerAbstract

   Request Datacenters to create the Cloudlets in the \ :java:ref:`Cloudlets waiting list <getCloudletWaitingList()>`\ . If there aren't available VMs to host all cloudlets, the creation of some ones will be postponed.

   This method is called after all submitted VMs are created in some Datacenter.

   **See also:** :java:ref:`.submitCloudletList(java.util.List)`

setCloudletComparator
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setCloudletComparator(Comparator<Cloudlet> comparator)
   :outertype: DatacenterBrokerAbstract

setDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setDatacenterList(Set<Datacenter> datacenterList)
   :outertype: DatacenterBrokerAbstract

   Sets the list of available datacenters.

   :param datacenterList: the new dc list

setDatacenterSupplier
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setDatacenterSupplier(Supplier<Datacenter> datacenterSupplier)
   :outertype: DatacenterBrokerAbstract

setFallbackDatacenterSupplier
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setFallbackDatacenterSupplier(Supplier<Datacenter> fallbackDatacenterSupplier)
   :outertype: DatacenterBrokerAbstract

setVmComparator
^^^^^^^^^^^^^^^

.. java:method:: @Override public void setVmComparator(Comparator<Vm> comparator)
   :outertype: DatacenterBrokerAbstract

setVmDestructionDelayFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker setVmDestructionDelayFunction(Function<Vm, Double> function)
   :outertype: DatacenterBrokerAbstract

setVmMapper
^^^^^^^^^^^

.. java:method:: @Override public final void setVmMapper(Function<Cloudlet, Vm> vmMapper)
   :outertype: DatacenterBrokerAbstract

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

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay)
   :outertype: DatacenterBrokerAbstract

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list, Vm vm)
   :outertype: DatacenterBrokerAbstract

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay)
   :outertype: DatacenterBrokerAbstract

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list)
   :outertype: DatacenterBrokerAbstract

   {@inheritDoc}

   If the entity already started (the simulation is running), the creation of previously submitted Cloudlets already was requested by the \ :java:ref:`start()`\  method that is called just once. By this way, this method will immediately request the creation of these just submitted Cloudlets if all submitted VMs were already created, in order to allow Cloudlet creation after the simulation has started. This avoid the developer to dynamically create brokers just to create VMs or Cloudlets during simulation execution.

   :param list: {@inheritDoc}

   **See also:** :java:ref:`.submitCloudletList(List,double)`

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

   :param list: {@inheritDoc}

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: DatacenterBrokerAbstract

