.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources DatacenterStorage

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostEventInfo

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
addHost
^^^^^^^

.. java:method:: @Override public Datacenter addHost(Host host)
   :outertype: DatacenterNull

addHostList
^^^^^^^^^^^

.. java:method:: @Override public <T extends Host> Datacenter addHostList(List<T> hostList)
   :outertype: DatacenterNull

addOnHostAvailableListener
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter addOnHostAvailableListener(EventListener<HostEventInfo> listener)
   :outertype: DatacenterNull

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(SimEntity entity)
   :outertype: DatacenterNull

getBandwidthPercentForMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getBandwidthPercentForMigration()
   :outertype: DatacenterNull

getCharacteristics
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics getCharacteristics()
   :outertype: DatacenterNull

getDatacenterStorage
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterStorage getDatacenterStorage()
   :outertype: DatacenterNull

getHost
^^^^^^^

.. java:method:: @Override public Host getHost(int index)
   :outertype: DatacenterNull

getHostById
^^^^^^^^^^^

.. java:method:: @Override public Host getHostById(long id)
   :outertype: DatacenterNull

getHostList
^^^^^^^^^^^

.. java:method:: @Override public List<Host> getHostList()
   :outertype: DatacenterNull

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: DatacenterNull

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: DatacenterNull

getPower
^^^^^^^^

.. java:method:: @Override public double getPower()
   :outertype: DatacenterNull

getPowerInKWattsHour
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPowerInKWattsHour()
   :outertype: DatacenterNull

getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSchedulingInterval()
   :outertype: DatacenterNull

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: DatacenterNull

getState
^^^^^^^^

.. java:method:: @Override public State getState()
   :outertype: DatacenterNull

getVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VmAllocationPolicy getVmAllocationPolicy()
   :outertype: DatacenterNull

getVmList
^^^^^^^^^

.. java:method:: @Override public List<Vm> getVmList()
   :outertype: DatacenterNull

isAlive
^^^^^^^

.. java:method:: @Override public boolean isAlive()
   :outertype: DatacenterNull

isFinished
^^^^^^^^^^

.. java:method:: @Override public boolean isFinished()
   :outertype: DatacenterNull

isStarted
^^^^^^^^^

.. java:method:: @Override public boolean isStarted()
   :outertype: DatacenterNull

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent evt)
   :outertype: DatacenterNull

removeHost
^^^^^^^^^^

.. java:method:: @Override public <T extends Host> Datacenter removeHost(T host)
   :outertype: DatacenterNull

run
^^^

.. java:method:: @Override public void run()
   :outertype: DatacenterNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEvent evt)
   :outertype: DatacenterNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEntity dest, double delay, int tag, Object data)
   :outertype: DatacenterNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(double delay, int tag, Object data)
   :outertype: DatacenterNull

schedule
^^^^^^^^

.. java:method:: @Override public boolean schedule(SimEntity dest, double delay, int tag)
   :outertype: DatacenterNull

setBandwidthPercentForMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setBandwidthPercentForMigration(double bandwidthPercentForMigration)
   :outertype: DatacenterNull

setDatacenterStorage
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenterStorage(DatacenterStorage datacenterStorage)
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

setState
^^^^^^^^

.. java:method:: @Override public SimEntity setState(State state)
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

