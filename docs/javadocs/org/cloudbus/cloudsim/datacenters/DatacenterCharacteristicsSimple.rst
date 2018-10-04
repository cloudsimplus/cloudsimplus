.. java:import:: org.cloudbus.cloudsim.hosts Host

DatacenterCharacteristicsSimple
===============================

.. java:package:: org.cloudbus.cloudsim.datacenters
   :noindex:

.. java:type:: public class DatacenterCharacteristicsSimple implements DatacenterCharacteristics

   Represents static properties of a Datacenter such as architecture, Operating System (OS), management policy (time- or space-shared), cost and time zone at which the resource is located along resource configuration. Each \ :java:ref:`Datacenter`\  has to have its own instance of this class, since it stores the Datacenter host list.

   :author: Manzur Murshed, Rajkumar Buyya, Rodrigo N. Calheiros, Anton Beloglazov

Constructors
------------
DatacenterCharacteristicsSimple
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterCharacteristicsSimple(Datacenter datacenter)
   :outertype: DatacenterCharacteristicsSimple

   Creates a DatacenterCharacteristics with default values for \ :java:ref:`architecture <getArchitecture()>`\ , \ :java:ref:`OS <getOs()>`\ , \ :java:ref:`Time Zone <getTimeZone()>`\  and \ :java:ref:`VMM <getVmm()>`\ . The costs for \ :java:ref:`BW <getCostPerBw()>`\ , \ :java:ref:`getCostPerMem()`\  () RAM} and \ :java:ref:`getCostPerStorage()`\  () Storage} are set to zero.

Methods
-------
getArchitecture
^^^^^^^^^^^^^^^

.. java:method:: @Override public String getArchitecture()
   :outertype: DatacenterCharacteristicsSimple

getCostPerBw
^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerBw()
   :outertype: DatacenterCharacteristicsSimple

getCostPerMem
^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerMem()
   :outertype: DatacenterCharacteristicsSimple

getCostPerSecond
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerSecond()
   :outertype: DatacenterCharacteristicsSimple

getCostPerStorage
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerStorage()
   :outertype: DatacenterCharacteristicsSimple

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: DatacenterCharacteristicsSimple

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: DatacenterCharacteristicsSimple

   Gets the Datacenter id, setup when Datacenter is created.

getMips
^^^^^^^

.. java:method:: @Override public double getMips()
   :outertype: DatacenterCharacteristicsSimple

getNumberOfFailedHosts
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfFailedHosts()
   :outertype: DatacenterCharacteristicsSimple

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfFreePes()
   :outertype: DatacenterCharacteristicsSimple

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfPes()
   :outertype: DatacenterCharacteristicsSimple

getOs
^^^^^

.. java:method:: @Override public String getOs()
   :outertype: DatacenterCharacteristicsSimple

getTimeZone
^^^^^^^^^^^

.. java:method:: @Override public double getTimeZone()
   :outertype: DatacenterCharacteristicsSimple

getVmm
^^^^^^

.. java:method:: @Override public String getVmm()
   :outertype: DatacenterCharacteristicsSimple

isWorking
^^^^^^^^^

.. java:method:: @Override public boolean isWorking()
   :outertype: DatacenterCharacteristicsSimple

setArchitecture
^^^^^^^^^^^^^^^

.. java:method:: @Override public final DatacenterCharacteristics setArchitecture(String architecture)
   :outertype: DatacenterCharacteristicsSimple

setCostPerBw
^^^^^^^^^^^^

.. java:method:: @Override public final DatacenterCharacteristics setCostPerBw(double costPerBw)
   :outertype: DatacenterCharacteristicsSimple

setCostPerMem
^^^^^^^^^^^^^

.. java:method:: @Override public final DatacenterCharacteristics setCostPerMem(double costPerMem)
   :outertype: DatacenterCharacteristicsSimple

setCostPerSecond
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final DatacenterCharacteristics setCostPerSecond(double costPerSecond)
   :outertype: DatacenterCharacteristicsSimple

setCostPerStorage
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final DatacenterCharacteristics setCostPerStorage(double costPerStorage)
   :outertype: DatacenterCharacteristicsSimple

setOs
^^^^^

.. java:method:: @Override public final DatacenterCharacteristics setOs(String os)
   :outertype: DatacenterCharacteristicsSimple

setTimeZone
^^^^^^^^^^^

.. java:method:: @Override public final DatacenterCharacteristics setTimeZone(double timeZone)
   :outertype: DatacenterCharacteristicsSimple

setVmm
^^^^^^

.. java:method:: @Override public final DatacenterCharacteristics setVmm(String vmm)
   :outertype: DatacenterCharacteristicsSimple

