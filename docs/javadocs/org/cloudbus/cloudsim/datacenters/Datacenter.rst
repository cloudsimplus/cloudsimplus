.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.power.models PowerAware

.. java:import:: org.cloudbus.cloudsim.power.models PowerModel

.. java:import:: org.cloudbus.cloudsim.resources DatacenterStorage

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostEventInfo

.. java:import:: java.util List

Datacenter
==========

.. java:package:: org.cloudbus.cloudsim.datacenters
   :noindex:

.. java:type:: public interface Datacenter extends SimEntity, PowerAware

   An interface to be implemented by each class that provides Datacenter features. The interface implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`Datacenter.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`Datacenter`\  variables.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
DEF_BW_PERCENT_FOR_MIGRATION
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field::  double DEF_BW_PERCENT_FOR_MIGRATION
   :outertype: Datacenter

   The default percentage of bandwidth allocated for VM migration, is a value is not set.

   **See also:** :java:ref:`.setBandwidthPercentForMigration(double)`

NULL
^^^^

.. java:field::  Datacenter NULL
   :outertype: Datacenter

   A property that implements the Null Object Design Pattern for \ :java:ref:`Datacenter`\  objects.

Methods
-------
addHost
^^^^^^^

.. java:method::  <T extends Host> Datacenter addHost(T host)
   :outertype: Datacenter

   Physically expands the Datacenter by adding a new Host (physical machine) to it. Hosts can be added before or after the simulation has started. If a Host is added during simulation execution, in case VMs are added dynamically too, they may be allocated to this new Host, depending on the \ :java:ref:`VmAllocationPolicy`\ .

   If an ID is not assigned to the given Host, the method assigns one.

   :param host: the new host to be added

   **See also:** :java:ref:`.getVmAllocationPolicy()`

addHostList
^^^^^^^^^^^

.. java:method::  <T extends Host> Datacenter addHostList(List<T> hostList)
   :outertype: Datacenter

   Physically expands the Datacenter by adding a List of new Hosts (physical machines) to it. Hosts can be added before or after the simulation has started. If a Host is added during simulation execution, in case VMs are added dynamically too, they may be allocated to this new Host, depending on the \ :java:ref:`VmAllocationPolicy`\ .

   If an ID is not assigned to a Host, the method assigns one.

   :param hostList: the List of new hosts to be added

   **See also:** :java:ref:`.getVmAllocationPolicy()`

addOnHostAvailableListener
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Datacenter addOnHostAvailableListener(EventListener<HostEventInfo> listener)
   :outertype: Datacenter

   Adds a \ :java:ref:`EventListener`\  object that will be notified every time when the a new Hosts is available for the Datacenter during simulation runtime. If the \ :java:ref:`addHost(Host)`\  or \ :java:ref:`addHostList(List)`\  is called before the simulation starts, the listeners will not be notified.

   :param listener: the event listener to add

getBandwidthPercentForMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getBandwidthPercentForMigration()
   :outertype: Datacenter

   Gets the percentage of the bandwidth allocated to a Host to migrate VMs. It's a value between [0 and 1] (where 1 is 100%). The default value is 0.5, meaning only 50% of the bandwidth will be allowed for migration, while the remaining will be used for VM services.

   **See also:** :java:ref:`.DEF_BW_PERCENT_FOR_MIGRATION`

getCharacteristics
^^^^^^^^^^^^^^^^^^

.. java:method::  DatacenterCharacteristics getCharacteristics()
   :outertype: Datacenter

   Gets the Datacenter characteristics.

   :return: the Datacenter characteristics

getDatacenterStorage
^^^^^^^^^^^^^^^^^^^^

.. java:method::  DatacenterStorage getDatacenterStorage()
   :outertype: Datacenter

   Gets the storage of the Datacenter.

   :return: the storage

getHost
^^^^^^^

.. java:method::  Host getHost(int index)
   :outertype: Datacenter

   Gets a Host in a given position inside the Host List.

   :param index: the position of the List to get the Host

getHostById
^^^^^^^^^^^

.. java:method::  Host getHostById(long id)
   :outertype: Datacenter

   Gets a Host from its id.

   :param id: the ID of the Host to get from the List.
   :return: the Host if found or \ :java:ref:`Host.NULL`\  otherwise

getHostList
^^^^^^^^^^^

.. java:method::  <T extends Host> List<T> getHostList()
   :outertype: Datacenter

   Gets an \ **unmodifiable**\  host list.

   :param <T>: The generic type
   :return: the host list

getPower
^^^^^^^^

.. java:method:: @Override  double getPower()
   :outertype: Datacenter

   Gets an \ **estimation**\  of Datacenter power consumption in Watt-Second (Ws).

   To get actual power consumption, it's required to enable
   Host's StateHistory
   by calling
   and use each Host  to compute power usage
   based on the CPU utilization got form the StateHistory.

   :return: th \ **estimated**\  power consumption in Watt-Second (Ws)

getSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getSchedulingInterval()
   :outertype: Datacenter

   Gets the scheduling interval to process each event received by the Datacenter (in seconds). This value defines the interval in which processing of Cloudlets will be updated. The interval doesn't affect the processing of such cloudlets, it only defines in which interval the processing will be updated. For instance, if it is set a interval of 10 seconds, the processing of cloudlets will be updated at every 10 seconds. By this way, trying to get the amount of instructions the cloudlet has executed after 5 seconds, by means of \ :java:ref:`Cloudlet.getFinishedLengthSoFar(Datacenter)`\ , it will not return an updated value. By this way, one should set the scheduling interval to 5 to get an updated result. As longer is the interval, faster will be the simulation execution.

   :return: the scheduling interval (in seconds)

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

removeHost
^^^^^^^^^^

.. java:method::  <T extends Host> Datacenter removeHost(T host)
   :outertype: Datacenter

   Removes a Host from its Datacenter.

   :param host: the new host to be removed from its assigned Datacenter

setBandwidthPercentForMigration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setBandwidthPercentForMigration(double bandwidthPercentForMigration)
   :outertype: Datacenter

   Sets the percentage of the bandwidth allocated to a Host to migrate VMs. It's a value between [0 and 1] (where 1 is 100%). The default value is 0.5, meaning only 50% of the bandwidth will be allowed for migration, while the remaining will be used for VM services.

   :param bandwidthPercentForMigration: the bandwidth migration percentage to set

setDatacenterStorage
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setDatacenterStorage(DatacenterStorage datacenterStorage)
   :outertype: Datacenter

   Sets the storage of the Datacenter.

   :param datacenterStorage: the new storage

setSchedulingInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Datacenter setSchedulingInterval(double schedulingInterval)
   :outertype: Datacenter

   Sets the scheduling delay to process each event received by the Datacenter (in seconds).

   :param schedulingInterval: the new scheduling interval (in seconds)

   **See also:** :java:ref:`.getSchedulingInterval()`

