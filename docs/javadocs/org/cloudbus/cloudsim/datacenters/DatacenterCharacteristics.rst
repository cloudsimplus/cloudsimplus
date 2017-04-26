.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.core Identificable

.. java:import:: org.cloudbus.cloudsim.resources Pe

DatacenterCharacteristics
=========================

.. java:package:: org.cloudbus.cloudsim.datacenters
   :noindex:

.. java:type:: public interface DatacenterCharacteristics extends Identificable

   An interface to be implemented by each class that represents the physical characteristics of a Datacenter.

   :author: Manzur Murshed, Rajkumar Buyya, Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
DEFAULT_ARCH
^^^^^^^^^^^^

.. java:field::  String DEFAULT_ARCH
   :outertype: DatacenterCharacteristics

   The default architecture of Datacenter Hosts to be used if not one is set.

DEFAULT_OS
^^^^^^^^^^

.. java:field::  String DEFAULT_OS
   :outertype: DatacenterCharacteristics

   The default Operating System of Datacenter Hosts to be used if not one is set.

DEFAULT_TIMEZONE
^^^^^^^^^^^^^^^^

.. java:field::  double DEFAULT_TIMEZONE
   :outertype: DatacenterCharacteristics

   The default Datacenter's Time Zone to be used if not one is set.

DEFAULT_VMM
^^^^^^^^^^^

.. java:field::  String DEFAULT_VMM
   :outertype: DatacenterCharacteristics

   The default Virtual Machine Monitor to be used if not one is set.

NULL
^^^^

.. java:field::  DatacenterCharacteristics NULL
   :outertype: DatacenterCharacteristics

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Datacenter`\  objects.

Methods
-------
getArchitecture
^^^^^^^^^^^^^^^

.. java:method::  String getArchitecture()
   :outertype: DatacenterCharacteristics

   Gets the architecture.

   :return: the architecture

getCostPerBw
^^^^^^^^^^^^

.. java:method::  double getCostPerBw()
   :outertype: DatacenterCharacteristics

   Get the cost to use each each Megabit of bandwidth in the Datacenter.

   :return: the cost to use bw

getCostPerMem
^^^^^^^^^^^^^

.. java:method::  double getCostPerMem()
   :outertype: DatacenterCharacteristics

   Get the cost to use each Megabyte of RAM in the Datacenter.

   :return: the cost to use RAM

getCostPerSecond
^^^^^^^^^^^^^^^^

.. java:method::  double getCostPerSecond()
   :outertype: DatacenterCharacteristics

   Gets the cost per second of CPU.

   :return: the cost per second

getCostPerStorage
^^^^^^^^^^^^^^^^^

.. java:method::  double getCostPerStorage()
   :outertype: DatacenterCharacteristics

   Get the cost to use each Megabyte of storage in the Datacenter.

   :return: the cost to use storage

getDatacenter
^^^^^^^^^^^^^

.. java:method::  Datacenter getDatacenter()
   :outertype: DatacenterCharacteristics

   Gets the \ :java:ref:`Datacenter`\  that owns these characteristics

   :return: the Datacenter

getHostList
^^^^^^^^^^^

.. java:method::  <T extends Host> List<T> getHostList()
   :outertype: DatacenterCharacteristics

   Gets the host list.

   :param <T>: The generic type
   :return: the host list

getHostWithFreePe
^^^^^^^^^^^^^^^^^

.. java:method::  Host getHostWithFreePe()
   :outertype: DatacenterCharacteristics

   Gets the first PM with at least one empty Pe.

   :return: a Machine object or if not found

getHostWithFreePe
^^^^^^^^^^^^^^^^^

.. java:method::  Host getHostWithFreePe(int peNumber)
   :outertype: DatacenterCharacteristics

   Gets a Machine with at least a given number of free Pe.

   :param peNumber: the pe number
   :return: a Machine object or if not found

getId
^^^^^

.. java:method:: @Override  int getId()
   :outertype: DatacenterCharacteristics

   Gets the Datacenter id.

   :return: the id

getMips
^^^^^^^

.. java:method::  double getMips()
   :outertype: DatacenterCharacteristics

   Gets the total MIPS rating, which is the sum of MIPS rating of all Hosts in the Datacenter.

   :return: the sum of MIPS ratings

getMipsOfOnePe
^^^^^^^^^^^^^^

.. java:method::  long getMipsOfOnePe(int hostId, int peId)
   :outertype: DatacenterCharacteristics

   Gets Millions Instructions Per Second (MIPS) Rating of a Processing Element (Pe). It is essential to use this method when a Datacenter is made up of heterogenous PEs per PMs.

   :param hostId: the machine ID
   :param peId: the Pe ID
   :return: the MIPS Rating or -1 if no PEs are exists.

getNumberOfBusyPes
^^^^^^^^^^^^^^^^^^

.. java:method::  int getNumberOfBusyPes()
   :outertype: DatacenterCharacteristics

   Gets the total number of \ ``BUSY``\  PEs for all PMs.

   :return: number of PEs

getNumberOfFailedHosts
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getNumberOfFailedHosts()
   :outertype: DatacenterCharacteristics

   Gets the current number of failed PMs.

   :return: current number of failed PMs the Datacenter has.

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method::  int getNumberOfFreePes()
   :outertype: DatacenterCharacteristics

   Gets the total number of \ ``FREE``\  or non-busy PEs for all PMs.

   :return: number of PEs

getNumberOfHosts
^^^^^^^^^^^^^^^^

.. java:method::  int getNumberOfHosts()
   :outertype: DatacenterCharacteristics

   Gets the total number of PMs.

   :return: total number of machines the Datacenter has.

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method::  int getNumberOfPes()
   :outertype: DatacenterCharacteristics

   Gets the total number of PEs for all PMs.

   :return: number of PEs

getOs
^^^^^

.. java:method::  String getOs()
   :outertype: DatacenterCharacteristics

   Gets the Operating System (OS).

   :return: the Operating System (OS)

getResourceName
^^^^^^^^^^^^^^^

.. java:method::  String getResourceName()
   :outertype: DatacenterCharacteristics

   Gets the name of a resource.

   :return: the resource name

getTimeZone
^^^^^^^^^^^

.. java:method::  double getTimeZone()
   :outertype: DatacenterCharacteristics

   Gets the time zone, a value between [-12 and 13].

   :return: the time zone

getVmm
^^^^^^

.. java:method::  String getVmm()
   :outertype: DatacenterCharacteristics

   Gets the VMM in use in the Datacenter.

   :return: the VMM name

isWorking
^^^^^^^^^

.. java:method::  boolean isWorking()
   :outertype: DatacenterCharacteristics

   Checks whether all PMs of the Datacenter are working properly or not.

   :return: if all PMs are working, otherwise

setArchitecture
^^^^^^^^^^^^^^^

.. java:method::  DatacenterCharacteristics setArchitecture(String architecture)
   :outertype: DatacenterCharacteristics

   Sets the architecture.

   :param architecture: the new architecture

setCostPerBw
^^^^^^^^^^^^

.. java:method::  DatacenterCharacteristics setCostPerBw(double costPerBw)
   :outertype: DatacenterCharacteristics

   Sets cost to use each Megabit of bandwidth.

   :param costPerBw: the cost to set

setCostPerMem
^^^^^^^^^^^^^

.. java:method::  DatacenterCharacteristics setCostPerMem(double costPerMem)
   :outertype: DatacenterCharacteristics

   Sets the cost to use each Megabyte of RAM in the Datacenter.

   :param costPerMem: cost to use RAM

setCostPerSecond
^^^^^^^^^^^^^^^^

.. java:method::  DatacenterCharacteristics setCostPerSecond(double costPerSecond)
   :outertype: DatacenterCharacteristics

   Sets the cost per second of CPU.

   :param costPerSecond: the new cost per second

setCostPerStorage
^^^^^^^^^^^^^^^^^

.. java:method::  DatacenterCharacteristics setCostPerStorage(double costPerStorage)
   :outertype: DatacenterCharacteristics

   Sets cost to use each Megabyte of storage.

   :param costPerStorage: cost to use storage

setDatacenter
^^^^^^^^^^^^^

.. java:method::  DatacenterCharacteristics setDatacenter(Datacenter datacenter)
   :outertype: DatacenterCharacteristics

   Sets the \ :java:ref:`Datacenter`\  that owns these characteristics

   :param datacenter: the Datacenter to set

setOs
^^^^^

.. java:method::  DatacenterCharacteristics setOs(String os)
   :outertype: DatacenterCharacteristics

   Sets the Operating System (OS).

   :param os: the new Operating System (OS)

setPeStatus
^^^^^^^^^^^

.. java:method::  boolean setPeStatus(Pe.Status status, int hostId, int peId)
   :outertype: DatacenterCharacteristics

   Sets the particular Pe status on a PM.

   :param status: the new Pe status
   :param hostId: Machine ID
   :param peId: Pe id
   :return: otherwise (Machine id or Pe id might not be exist)

setTimeZone
^^^^^^^^^^^

.. java:method::  DatacenterCharacteristics setTimeZone(double timeZone)
   :outertype: DatacenterCharacteristics

   Sets the time zone. If an invalid value is given, the timezone is set to 0.

   :param timeZone: the new time zone value, between [-12 and 13].

setVmm
^^^^^^

.. java:method::  DatacenterCharacteristics setVmm(String vmm)
   :outertype: DatacenterCharacteristics

   Sets the vmm.

   :param vmm: the new vmm

