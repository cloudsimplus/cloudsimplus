.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners DatacenterBrokerEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: java.util Collections

.. java:import:: java.util Comparator

.. java:import:: java.util List

.. java:import:: java.util.function Function

.. java:import:: java.util.function Supplier

DatacenterBrokerNull
====================

.. java:package:: org.cloudbus.cloudsim.brokers
   :noindex:

.. java:type:: final class DatacenterBrokerNull implements DatacenterBroker

   A class that implements the Null Object Design Pattern for \ :java:ref:`DatacenterBroker`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`DatacenterBroker.NULL`

Methods
-------
addOnVmsCreatedListener
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener)
   :outertype: DatacenterBrokerNull

addOneTimeOnVmsCreatedListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker addOneTimeOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener)
   :outertype: DatacenterBrokerNull

bindCloudletToVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm)
   :outertype: DatacenterBrokerNull

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEntity entity)
   :outertype: DatacenterBrokerNull

defaultVmMapper
^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm defaultVmMapper(Cloudlet cloudlet)
   :outertype: DatacenterBrokerNull

getCloudletCreatedList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Cloudlet> getCloudletCreatedList()
   :outertype: DatacenterBrokerNull

getCloudletFinishedList
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Cloudlet> List<T> getCloudletFinishedList()
   :outertype: DatacenterBrokerNull

getCloudletSubmittedList
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Cloudlet> getCloudletSubmittedList()
   :outertype: DatacenterBrokerNull

getCloudletWaitingList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Cloudlet> List<T> getCloudletWaitingList()
   :outertype: DatacenterBrokerNull

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: DatacenterBrokerNull

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: DatacenterBrokerNull

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: DatacenterBrokerNull

getState
^^^^^^^^

.. java:method:: @Override public State getState()
   :outertype: DatacenterBrokerNull

getVmCreatedList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmCreatedList()
   :outertype: DatacenterBrokerNull

getVmDestructionDelayFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function<Vm, Double> getVmDestructionDelayFunction()
   :outertype: DatacenterBrokerNull

getVmExecList
^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmExecList()
   :outertype: DatacenterBrokerNull

getVmWaitingList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmWaitingList()
   :outertype: DatacenterBrokerNull

getWaitingVm
^^^^^^^^^^^^

.. java:method:: @Override public Vm getWaitingVm(int index)
   :outertype: DatacenterBrokerNull

isAlive
^^^^^^^

.. java:method:: @Override public boolean isAlive()
   :outertype: DatacenterBrokerNull

isFinished
^^^^^^^^^^

.. java:method:: @Override public boolean isFinished()
   :outertype: DatacenterBrokerNull

isStarted
^^^^^^^^^

.. java:method:: @Override public boolean isStarted()
   :outertype: DatacenterBrokerNull

isThereWaitingCloudlets
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isThereWaitingCloudlets()
   :outertype: DatacenterBrokerNull

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent evt)
   :outertype: DatacenterBrokerNull

run
^^^

.. java:method:: @Override public void run()
   :outertype: DatacenterBrokerNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEvent evt)
   :outertype: DatacenterBrokerNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(double delay, int tag, Object data)
   :outertype: DatacenterBrokerNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEntity dest, double delay, int tag, Object data)
   :outertype: DatacenterBrokerNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEntity dest, double delay, int tag)
   :outertype: DatacenterBrokerNull

setCloudletComparator
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setCloudletComparator(Comparator<Cloudlet> comparator)
   :outertype: DatacenterBrokerNull

setDatacenterSupplier
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenterSupplier(Supplier<Datacenter> datacenterSupplier)
   :outertype: DatacenterBrokerNull

setFallbackDatacenterSupplier
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setFallbackDatacenterSupplier(Supplier<Datacenter> fallbackDatacenterSupplier)
   :outertype: DatacenterBrokerNull

setName
^^^^^^^

.. java:method:: @Override public SimEntity setName(String newName) throws IllegalArgumentException
   :outertype: DatacenterBrokerNull

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public SimEntity setSimulation(Simulation simulation)
   :outertype: DatacenterBrokerNull

setState
^^^^^^^^

.. java:method:: @Override public SimEntity setState(State state)
   :outertype: DatacenterBrokerNull

setVmComparator
^^^^^^^^^^^^^^^

.. java:method:: @Override public void setVmComparator(Comparator<Vm> comparator)
   :outertype: DatacenterBrokerNull

setVmDestructionDelayFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker setVmDestructionDelayFunction(Function<Vm, Double> function)
   :outertype: DatacenterBrokerNull

setVmMapper
^^^^^^^^^^^

.. java:method:: @Override public void setVmMapper(Function<Cloudlet, Vm> vmMapper)
   :outertype: DatacenterBrokerNull

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override public void shutdownEntity()
   :outertype: DatacenterBrokerNull

start
^^^^^

.. java:method:: @Override public void start()
   :outertype: DatacenterBrokerNull

submitCloudlet
^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudlet(Cloudlet cloudlet)
   :outertype: DatacenterBrokerNull

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list)
   :outertype: DatacenterBrokerNull

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay)
   :outertype: DatacenterBrokerNull

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list, Vm vm)
   :outertype: DatacenterBrokerNull

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay)
   :outertype: DatacenterBrokerNull

submitVm
^^^^^^^^

.. java:method:: @Override public void submitVm(Vm vm)
   :outertype: DatacenterBrokerNull

submitVmList
^^^^^^^^^^^^

.. java:method:: @Override public void submitVmList(List<? extends Vm> list)
   :outertype: DatacenterBrokerNull

submitVmList
^^^^^^^^^^^^

.. java:method:: @Override public void submitVmList(List<? extends Vm> list, double submissionDelay)
   :outertype: DatacenterBrokerNull

