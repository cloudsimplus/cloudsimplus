.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core SimEntityNullBase

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners DatacenterBrokerEventInfo

.. java:import:: org.cloudsimplus.listeners EventInfo

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

.. java:type:: final class DatacenterBrokerNull implements DatacenterBroker, SimEntityNullBase

   A class that implements the Null Object Design Pattern for \ :java:ref:`DatacenterBroker`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`DatacenterBroker.NULL`

Methods
-------
addOnVmsCreatedListener
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener)
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

getVmMapper
^^^^^^^^^^^

.. java:method:: @Override public Function<Cloudlet, Vm> getVmMapper()
   :outertype: DatacenterBrokerNull

getVmWaitingList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmWaitingList()
   :outertype: DatacenterBrokerNull

getWaitingVm
^^^^^^^^^^^^

.. java:method:: @Override public Vm getWaitingVm(int index)
   :outertype: DatacenterBrokerNull

removeOnVmsCreatedListener
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker removeOnVmsCreatedListener(EventListener<? extends EventInfo> listener)
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

setVmComparator
^^^^^^^^^^^^^^^

.. java:method:: @Override public void setVmComparator(Comparator<Vm> comparator)
   :outertype: DatacenterBrokerNull

setVmDestructionDelay
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker setVmDestructionDelay(double delay)
   :outertype: DatacenterBrokerNull

setVmDestructionDelayFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterBroker setVmDestructionDelayFunction(Function<Vm, Double> function)
   :outertype: DatacenterBrokerNull

setVmMapper
^^^^^^^^^^^

.. java:method:: @Override public void setVmMapper(Function<Cloudlet, Vm> vmMapper)
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

