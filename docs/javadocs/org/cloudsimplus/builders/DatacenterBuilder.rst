.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicySimple

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.datacenters DatacenterSimple

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources FileStorage

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Objects

DatacenterBuilder
=================

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class DatacenterBuilder extends Builder

   A Builder class to createDatacenter \ :java:ref:`DatacenterSimple`\  objects.

   :author: Manoel Campos da Silva Filho

Constructors
------------
DatacenterBuilder
^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterBuilder(SimulationScenarioBuilder scenario)
   :outertype: DatacenterBuilder

Methods
-------
addStorageToList
^^^^^^^^^^^^^^^^

.. java:method:: public DatacenterBuilder addStorageToList(FileStorage storage)
   :outertype: DatacenterBuilder

createDatacenter
^^^^^^^^^^^^^^^^

.. java:method:: public DatacenterBuilder createDatacenter(List<Host> hosts)
   :outertype: DatacenterBuilder

get
^^^

.. java:method:: public Datacenter get(int index)
   :outertype: DatacenterBuilder

getCostPerBwMegabit
^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getCostPerBwMegabit()
   :outertype: DatacenterBuilder

getCostPerCpuSecond
^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getCostPerCpuSecond()
   :outertype: DatacenterBuilder

getCostPerMem
^^^^^^^^^^^^^

.. java:method:: public double getCostPerMem()
   :outertype: DatacenterBuilder

getCostPerStorage
^^^^^^^^^^^^^^^^^

.. java:method:: public double getCostPerStorage()
   :outertype: DatacenterBuilder

getDatacenters
^^^^^^^^^^^^^^

.. java:method:: public List<Datacenter> getDatacenters()
   :outertype: DatacenterBuilder

getFirstHostFromFirstDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Host getFirstHostFromFirstDatacenter()
   :outertype: DatacenterBuilder

getHostOfDatacenter
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Host getHostOfDatacenter(int hostIndex, int datacenterIndex)
   :outertype: DatacenterBuilder

getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getSchedulingInterval()
   :outertype: DatacenterBuilder

getTimezone
^^^^^^^^^^^

.. java:method:: public double getTimezone()
   :outertype: DatacenterBuilder

setCostPerBwMegabit
^^^^^^^^^^^^^^^^^^^

.. java:method:: public DatacenterBuilder setCostPerBwMegabit(double defaultCostPerBwByte)
   :outertype: DatacenterBuilder

setCostPerCpuSecond
^^^^^^^^^^^^^^^^^^^

.. java:method:: public DatacenterBuilder setCostPerCpuSecond(double defaultCostPerCpuSecond)
   :outertype: DatacenterBuilder

setCostPerMem
^^^^^^^^^^^^^

.. java:method:: public DatacenterBuilder setCostPerMem(double defaultCostPerMem)
   :outertype: DatacenterBuilder

setCostPerStorage
^^^^^^^^^^^^^^^^^

.. java:method:: public DatacenterBuilder setCostPerStorage(double defaultCostPerStorage)
   :outertype: DatacenterBuilder

setSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public DatacenterBuilder setSchedulingInterval(double schedulingInterval)
   :outertype: DatacenterBuilder

setStorageList
^^^^^^^^^^^^^^

.. java:method:: public DatacenterBuilder setStorageList(List<FileStorage> storageList)
   :outertype: DatacenterBuilder

setTimezone
^^^^^^^^^^^

.. java:method:: public DatacenterBuilder setTimezone(double defaultTimezone)
   :outertype: DatacenterBuilder

