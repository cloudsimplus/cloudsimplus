.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources File

.. java:import:: org.cloudbus.cloudsim.resources FileStorage

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Collections

.. java:import:: java.util List

DatacenterNull
==============

.. java:package:: org.cloudbus.cloudsim.datacenters
   :noindex:

.. java:type:: final class DatacenterNull implements Datacenter

   A class that implements the Null Object Design Pattern for \ :java:ref:`Datacenter`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Datacenter.NULL`

Methods
-------
addFile
^^^^^^^

.. java:method:: @Override public int addFile(File file)
   :outertype: DatacenterNull

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEntity o)
   :outertype: DatacenterNull

getCharacteristics
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics getCharacteristics()
   :outertype: DatacenterNull

getHost
^^^^^^^

.. java:method:: @Override public Host getHost(int index)
   :outertype: DatacenterNull

getHostList
^^^^^^^^^^^

.. java:method:: @Override public List<Host> getHostList()
   :outertype: DatacenterNull

getId
^^^^^

.. java:method:: @Override public int getId()
   :outertype: DatacenterNull

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: DatacenterNull

getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSchedulingInterval()
   :outertype: DatacenterNull

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: DatacenterNull

getStorageList
^^^^^^^^^^^^^^

.. java:method:: @Override public List<FileStorage> getStorageList()
   :outertype: DatacenterNull

getVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VmAllocationPolicy getVmAllocationPolicy()
   :outertype: DatacenterNull

getVmList
^^^^^^^^^

.. java:method:: @Override public List<Vm> getVmList()
   :outertype: DatacenterNull

isStarted
^^^^^^^^^

.. java:method:: @Override public boolean isStarted()
   :outertype: DatacenterNull

println
^^^^^^^

.. java:method:: @Override public void println(String msg)
   :outertype: DatacenterNull

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent ev)
   :outertype: DatacenterNull

run
^^^

.. java:method:: @Override public void run()
   :outertype: DatacenterNull

schedule
^^^^^^^^

.. java:method:: @Override public void schedule(int dest, double delay, int tag)
   :outertype: DatacenterNull

setLog
^^^^^^

.. java:method:: @Override public void setLog(boolean log)
   :outertype: DatacenterNull

setName
^^^^^^^

.. java:method:: @Override public SimEntity setName(String newName) throws IllegalArgumentException
   :outertype: DatacenterNull

setSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter setSchedulingInterval(double schedulingInterval)
   :outertype: DatacenterNull

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public SimEntity setSimulation(Simulation simulation)
   :outertype: DatacenterNull

setStorageList
^^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter setStorageList(List<FileStorage> storageList)
   :outertype: DatacenterNull

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override public void shutdownEntity()
   :outertype: DatacenterNull

start
^^^^^

.. java:method:: @Override public void start()
   :outertype: DatacenterNull

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: DatacenterNull

