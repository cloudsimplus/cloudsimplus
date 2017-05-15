.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.resources File

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.resources FileStorage

.. java:import:: java.util List

Datacenter
==========

.. java:package:: org.cloudbus.cloudsim.datacenters
   :noindex:

.. java:type:: public interface Datacenter extends SimEntity

   An interface to be implemented by each class that provides Datacenter features. The interface implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`Datacenter.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`Datacenter`\  variables.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Datacenter NULL
   :outertype: Datacenter

   A property that implements the Null Object Design Pattern for \ :java:ref:`Datacenter`\  objects.

Methods
-------
addFile
^^^^^^^

.. java:method::  int addFile(File file)
   :outertype: Datacenter

   Adds a file into the resource's storage before the experiment starts. If the file is a master file, then it will be registered to the RC when the experiment begins.

   :param file: a DataCloud file
   :return: a tag number denoting whether this operation is a success or not

getCharacteristics
^^^^^^^^^^^^^^^^^^

.. java:method::  DatacenterCharacteristics getCharacteristics()
   :outertype: Datacenter

   Gets the Datacenter characteristics.

   :return: the Datacenter characteristics

getHost
^^^^^^^

.. java:method::  Host getHost(int index)
   :outertype: Datacenter

getHostList
^^^^^^^^^^^

.. java:method::  <T extends Host> List<T> getHostList()
   :outertype: Datacenter

   Gets the host list.

   :param <T>: The generic type
   :return: the host list

getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getSchedulingInterval()
   :outertype: Datacenter

   Gets the scheduling interval to process each event received by the Datacenter (in seconds). This value defines the interval in which processing of Cloudlets will be updated. The interval doesn't affect the processing of such cloudlets, it only defines in which interval the processing will be updated. For instance, if it is set a interval of 10 seconds, the processing of cloudlets will be updated at every 10 seconds. By this way, trying to get the amount of instructions the cloudlet has executed after 5 seconds, by means of \ :java:ref:`Cloudlet.getFinishedLengthSoFar(Datacenter)`\ , it will not return an updated value. By this way, one should set the scheduling interval to 5 to get an updated result. As longer is the interval, faster will be the simulation execution.

   :return: the scheduling interval

getStorageList
^^^^^^^^^^^^^^

.. java:method::  List<FileStorage> getStorageList()
   :outertype: Datacenter

   Gets a \ **read-only**\  list of storage devices of the Datacenter.

   :return: the storage list

getVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  VmAllocationPolicy getVmAllocationPolicy()
   :outertype: Datacenter

   Gets the policy to be used by the Datacenter to allocate VMs into hosts.

   :return: the VM allocation policy

   **See also:** :java:ref:`VmAllocationPolicy`

getVmList
^^^^^^^^^

.. java:method::  <T extends Vm> List<T> getVmList()
   :outertype: Datacenter

   Gets a \ **read-only**\  list all VMs from all Hosts of this Datacenter.

   :param <T>: the class of VMs inside the list
   :return: the list all VMs from all Hosts

setSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Datacenter setSchedulingInterval(double schedulingInterval)
   :outertype: Datacenter

   Sets the scheduling delay to process each event received by the Datacenter (in seconds).

   :param schedulingInterval: the new scheduling interval

   **See also:** :java:ref:`.getSchedulingInterval()`

setStorageList
^^^^^^^^^^^^^^

.. java:method::  Datacenter setStorageList(List<FileStorage> storageList)
   :outertype: Datacenter

   Sets the list of storage devices of the Datacenter.

   :param storageList: the new storage list

