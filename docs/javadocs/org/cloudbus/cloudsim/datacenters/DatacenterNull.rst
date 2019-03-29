.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.core SimEntityNullBase

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources DatacenterStorage

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostEventInfo

.. java:import:: java.util Collections

.. java:import:: java.util List

DatacenterNull
==============

.. java:package:: org.cloudbus.cloudsim.datacenters
   :noindex:

.. java:type:: final class DatacenterNull implements Datacenter, SimEntityNullBase

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

getVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VmAllocationPolicy getVmAllocationPolicy()
   :outertype: DatacenterNull

removeHost
^^^^^^^^^^

.. java:method:: @Override public <T extends Host> Datacenter removeHost(T host)
   :outertype: DatacenterNull

setBandwidthPercentForMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setBandwidthPercentForMigration(double bandwidthPercentForMigration)
   :outertype: DatacenterNull

setDatacenterStorage
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenterStorage(DatacenterStorage datacenterStorage)
   :outertype: DatacenterNull

setSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter setSchedulingInterval(double schedulingInterval)
   :outertype: DatacenterNull

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: DatacenterNull

