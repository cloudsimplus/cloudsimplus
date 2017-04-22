.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: java.util Collections

.. java:import:: java.util List

DatacenterCharacteristicsNull
=============================

.. java:package:: org.cloudbus.cloudsim.datacenters
   :noindex:

.. java:type:: final class DatacenterCharacteristicsNull implements DatacenterCharacteristics

   A class that implements the Null Object Design Pattern for \ :java:ref:`Datacenter`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`DatacenterCharacteristics.NULL`

Methods
-------
getArchitecture
^^^^^^^^^^^^^^^

.. java:method:: @Override public String getArchitecture()
   :outertype: DatacenterCharacteristicsNull

getCostPerBw
^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerBw()
   :outertype: DatacenterCharacteristicsNull

getCostPerMem
^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerMem()
   :outertype: DatacenterCharacteristicsNull

getCostPerSecond
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerSecond()
   :outertype: DatacenterCharacteristicsNull

getCostPerStorage
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerStorage()
   :outertype: DatacenterCharacteristicsNull

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: DatacenterCharacteristicsNull

getHostList
^^^^^^^^^^^

.. java:method:: @Override public <T extends Host> List<T> getHostList()
   :outertype: DatacenterCharacteristicsNull

getHostWithFreePe
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host getHostWithFreePe()
   :outertype: DatacenterCharacteristicsNull

getHostWithFreePe
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host getHostWithFreePe(int peNumber)
   :outertype: DatacenterCharacteristicsNull

getId
^^^^^

.. java:method:: @Override public int getId()
   :outertype: DatacenterCharacteristicsNull

getMips
^^^^^^^

.. java:method:: @Override public double getMips()
   :outertype: DatacenterCharacteristicsNull

getMipsOfOnePe
^^^^^^^^^^^^^^

.. java:method:: @Override public long getMipsOfOnePe(int hostId, int peId)
   :outertype: DatacenterCharacteristicsNull

getNumberOfBusyPes
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfBusyPes()
   :outertype: DatacenterCharacteristicsNull

getNumberOfFailedHosts
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfFailedHosts()
   :outertype: DatacenterCharacteristicsNull

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfFreePes()
   :outertype: DatacenterCharacteristicsNull

getNumberOfHosts
^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfHosts()
   :outertype: DatacenterCharacteristicsNull

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfPes()
   :outertype: DatacenterCharacteristicsNull

getOs
^^^^^

.. java:method:: @Override public String getOs()
   :outertype: DatacenterCharacteristicsNull

getResourceName
^^^^^^^^^^^^^^^

.. java:method:: @Override public String getResourceName()
   :outertype: DatacenterCharacteristicsNull

getTimeZone
^^^^^^^^^^^

.. java:method:: @Override public double getTimeZone()
   :outertype: DatacenterCharacteristicsNull

getVmm
^^^^^^

.. java:method:: @Override public String getVmm()
   :outertype: DatacenterCharacteristicsNull

isWorking
^^^^^^^^^

.. java:method:: @Override public boolean isWorking()
   :outertype: DatacenterCharacteristicsNull

setArchitecture
^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics setArchitecture(String a)
   :outertype: DatacenterCharacteristicsNull

setCostPerBw
^^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics setCostPerBw(double c)
   :outertype: DatacenterCharacteristicsNull

setCostPerMem
^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics setCostPerMem(double c)
   :outertype: DatacenterCharacteristicsNull

setCostPerSecond
^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics setCostPerSecond(double c)
   :outertype: DatacenterCharacteristicsNull

setCostPerStorage
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics setCostPerStorage(double c)
   :outertype: DatacenterCharacteristicsNull

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics setDatacenter(Datacenter dc)
   :outertype: DatacenterCharacteristicsNull

setOs
^^^^^

.. java:method:: @Override public DatacenterCharacteristics setOs(String os)
   :outertype: DatacenterCharacteristicsNull

setPeStatus
^^^^^^^^^^^

.. java:method:: @Override public boolean setPeStatus(Pe.Status status, int hostId, int peId)
   :outertype: DatacenterCharacteristicsNull

setTimeZone
^^^^^^^^^^^

.. java:method:: @Override public DatacenterCharacteristics setTimeZone(double timeZone)
   :outertype: DatacenterCharacteristicsNull

setVmm
^^^^^^

.. java:method:: @Override public DatacenterCharacteristics setVmm(String vmm)
   :outertype: DatacenterCharacteristicsNull

