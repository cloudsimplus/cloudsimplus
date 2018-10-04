.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CustomerEntity

.. java:import:: org.cloudbus.cloudsim.core Machine

.. java:import:: org.cloudbus.cloudsim.core UniquelyIdentifiable

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudsimplus.autoscaling HorizontalVmScaling

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners VmDatacenterEventInfo

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: java.util List

.. java:import:: java.util.function Predicate

Vm
==

.. java:package:: org.cloudbus.cloudsim.vms
   :noindex:

.. java:type:: public interface Vm extends Machine, UniquelyIdentifiable, Comparable<Vm>, CustomerEntity

   An interface to be implemented by each class that provides basic features of Virtual Machines (VMs). The interface implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`Vm.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`Vm`\  variables.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Vm NULL
   :outertype: Vm

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Vm`\  objects.

Methods
-------
addOnCreationFailureListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm addOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener)
   :outertype: Vm

   Adds a listener object that will be notified when the Vm fail in being placed for lack of a \ :java:ref:`Host`\  with enough resources in a specific \ :java:ref:`Datacenter`\ .

   The \ :java:ref:`DatacenterBroker`\  is accountable for receiving the notification from the Datacenter and notifying the Listeners.

   :param listener: the listener to add

   **See also:** :java:ref:`.updateProcessing(double,List)`

addOnHostAllocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm addOnHostAllocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: Vm

   Adds a listener object that will be notified when a \ :java:ref:`Host`\  is allocated to the Vm, that is, when the Vm is placed into a given Host.

   :param listener: the listener to add

addOnHostDeallocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm addOnHostDeallocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: Vm

   Adds a listener object that will be notified when the Vm is moved/removed from a \ :java:ref:`Host`\ .

   :param listener: the listener to add

addOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm addOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener)
   :outertype: Vm

   Adds a listener object that will be notified every time when the processing of the Vm is updated in its \ :java:ref:`Host`\ .

   :param listener: the listener to seaddt

   **See also:** :java:ref:`.updateProcessing(double,List)`

addStateHistoryEntry
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void addStateHistoryEntry(VmStateHistoryEntry entry)
   :outertype: Vm

   Adds a VM state history entry.

   :param entry: the data about the state of the VM at given time

allocateResource
^^^^^^^^^^^^^^^^

.. java:method::  void allocateResource(Class<? extends ResourceManageable> resourceClass, long newTotalResourceAmount)
   :outertype: Vm

   Changes the allocation of a given resource for a VM. The old allocated amount will be changed to the new given amount.

   :param resourceClass: the class of the resource to change the allocation
   :param newTotalResourceAmount: the new amount to change the current allocation to

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method::  void deallocateResource(Class<? extends ResourceManageable> resourceClass)
   :outertype: Vm

   Removes the entire amount of a given resource allocated to VM.

   :param resourceClass: the class of the resource to deallocate from the VM

getBroker
^^^^^^^^^

.. java:method:: @Override  DatacenterBroker getBroker()
   :outertype: Vm

   Gets the \ :java:ref:`DatacenterBroker`\  that represents the owner of this Vm.

   :return: the broker or  if a broker has not been set yet

getBw
^^^^^

.. java:method:: @Override  Resource getBw()
   :outertype: Vm

   Gets bandwidth resource (in Megabits/s) assigned to the Vm, allowing to check its capacity and usage.

   :return: bandwidth resource.

getBwVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method::  VerticalVmScaling getBwVerticalScaling()
   :outertype: Vm

   Gets a \ :java:ref:`VerticalVmScaling`\  that will check if the Vm's Bandwidth is overloaded, based on some conditions defined by a \ :java:ref:`Predicate`\  given to the VerticalVmScaling, and then request the BW up scaling.

getCloudletScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method::  CloudletScheduler getCloudletScheduler()
   :outertype: Vm

   Gets the the Cloudlet scheduler the VM uses to schedule cloudlets execution.

   :return: the cloudlet scheduler

getCpuPercentUsage
^^^^^^^^^^^^^^^^^^

.. java:method::  double getCpuPercentUsage(double time)
   :outertype: Vm

   Gets the CPU utilization percentage of all Clouddlets running on this VM at the given time.

   :param time: the time
   :return: total utilization percentage

getCpuPercentUsage
^^^^^^^^^^^^^^^^^^

.. java:method::  double getCpuPercentUsage()
   :outertype: Vm

   Gets the current CPU utilization percentage (in scale from 0 to 1) of all Cloudlets running on this VM.

   :return: total utilization percentage for the current time, in scale from 0 to 1

getCurrentRequestedBw
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getCurrentRequestedBw()
   :outertype: Vm

   Gets the current requested bw.

   :return: the current requested bw

getCurrentRequestedMaxMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getCurrentRequestedMaxMips()
   :outertype: Vm

   Gets the current requested max MIPS among all virtual \ :java:ref:`PEs <Pe>`\ .

   :return: the current requested max MIPS

getCurrentRequestedMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Double> getCurrentRequestedMips()
   :outertype: Vm

   Gets a \ **copy**\  list of current requested MIPS of each virtual \ :java:ref:`Pe`\ , avoiding the original list to be changed.

   :return: the current requested MIPS of each Pe

getCurrentRequestedRam
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getCurrentRequestedRam()
   :outertype: Vm

   Gets the current requested ram.

   :return: the current requested ram

getCurrentRequestedTotalMips
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getCurrentRequestedTotalMips()
   :outertype: Vm

   Gets the current requested total MIPS. It is the sum of MIPS capacity requested for every virtual \ :java:ref:`Pe`\ .

   :return: the current requested total MIPS

   **See also:** :java:ref:`.getCurrentRequestedMips()`

getDescription
^^^^^^^^^^^^^^

.. java:method::  String getDescription()
   :outertype: Vm

   Gets the Vm description, which is an optional text which one can use to provide details about this of this VM.

getHorizontalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method::  HorizontalVmScaling getHorizontalScaling()
   :outertype: Vm

   Gets a \ :java:ref:`HorizontalVmScaling`\  that will check if the Vm is overloaded, based on some conditions defined by a \ :java:ref:`Predicate`\  given to the HorizontalVmScaling, and then request the creation of new VMs to horizontally scale the Vm.

   If no HorizontalVmScaling is set, the Broker will not dynamically
   create VMs to balance arrived Cloudlets.

getHost
^^^^^^^

.. java:method::  Host getHost()
   :outertype: Vm

   Gets the \ :java:ref:`Host`\  where the Vm is or will be placed. To know if the Vm was already created inside this Host, call the \ :java:ref:`isCreated()`\  method.

   :return: the Host

   **See also:** :java:ref:`.isCreated()`

getIdleInterval
^^^^^^^^^^^^^^^

.. java:method::  double getIdleInterval()
   :outertype: Vm

   Gets the last interval the VM was idle (without running any Cloudlet).

   :return: the last idle time interval (in seconds)

getLastBusyTime
^^^^^^^^^^^^^^^

.. java:method::  double getLastBusyTime()
   :outertype: Vm

   Gets the last time the VM was running some Cloudlet.

   :return: the last buzy time (in seconds)

getPeVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method::  VerticalVmScaling getPeVerticalScaling()
   :outertype: Vm

   Gets a \ :java:ref:`VerticalVmScaling`\  that will check if the Vm's \ :java:ref:`Pe`\  is overloaded, based on some conditions defined by a \ :java:ref:`Predicate`\  given to the VerticalVmScaling, and then request the RAM up scaling.

getProcessor
^^^^^^^^^^^^

.. java:method::  Processor getProcessor()
   :outertype: Vm

   Gets the \ :java:ref:`Processor`\  of this VM. It is its Virtual CPU which may be compounded of multiple \ :java:ref:`Pe`\ s.

getRam
^^^^^^

.. java:method:: @Override  Resource getRam()
   :outertype: Vm

   Gets the RAM resource assigned to the Vm, allowing to check its capacity (in Megabytes) and usage.

   :return: the RAM resource

getRamVerticalScaling
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  VerticalVmScaling getRamVerticalScaling()
   :outertype: Vm

   Gets a \ :java:ref:`VerticalVmScaling`\  that will check if the Vm's RAM is overloaded, based on some conditions defined by a \ :java:ref:`Predicate`\  given to the VerticalVmScaling, and then request the RAM up scaling.

getResources
^^^^^^^^^^^^

.. java:method:: @Override  List<ResourceManageable> getResources()
   :outertype: Vm

   {@inheritDoc} Such resources represent virtual resources corresponding to physical resources from the Host where the VM is placed.

   :return: {@inheritDoc}

getStartTime
^^^^^^^^^^^^

.. java:method::  double getStartTime()
   :outertype: Vm

   Gets the time the VM was created into some Host for the first time (in seconds). The value -1 means the VM was not created yet.

getStateHistory
^^^^^^^^^^^^^^^

.. java:method::  List<VmStateHistoryEntry> getStateHistory()
   :outertype: Vm

   Gets a \ **read-only**\  list with the history of requests and allocation of MIPS for this VM.

   :return: the state history

getStopTime
^^^^^^^^^^^

.. java:method::  double getStopTime()
   :outertype: Vm

   Gets the time the VM was destroyed into the last Host it executed (in seconds). The value -1 means the VM has not stopped or has not even started yet.

   **See also:** :java:ref:`.isCreated()`

getStorage
^^^^^^^^^^

.. java:method:: @Override  Resource getStorage()
   :outertype: Vm

   Gets the storage device of the VM, which represents the VM image, allowing to check its capacity (in Megabytes) and usage.

   :return: the storage resource

getTotalCpuMipsUsage
^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalCpuMipsUsage()
   :outertype: Vm

   Gets the current total CPU MIPS utilization of all PEs from all cloudlets running on this VM.

   :return: total CPU utilization in MIPS

   **See also:** :java:ref:`.getCpuPercentUsage(double)`

getTotalCpuMipsUsage
^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalCpuMipsUsage(double time)
   :outertype: Vm

   Gets the total CPU MIPS utilization of all PEs from all cloudlets running on this VM at the given time.

   :param time: the time to get the utilization
   :return: total CPU utilization in MIPS

   **See also:** :java:ref:`.getCpuPercentUsage(double)`

getTotalExecutionTime
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalExecutionTime()
   :outertype: Vm

   Gets the total time (in seconds) the Vm spent executing. It considers the entire VM execution even if in different Hosts it has possibly migrated.

   :return: the VM total execution time if the VM has stopped, the time executed so far if the VM is running yet, or 0 if it hasn't started.

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  UtilizationHistory getUtilizationHistory()
   :outertype: Vm

   Gets the object containing CPU utilization percentage history (between [0 and 1], where 1 is 100%). The history can be obtained by calling \ :java:ref:`VmUtilizationHistory.getHistory()`\ . Initially, the data collection is disabled. To enable it call \ :java:ref:`VmUtilizationHistory.enable()`\ .

   Utilization history for Hosts, obtained by calling \ :java:ref:`Host.getUtilizationHistory()`\  is just available if the utilization history for its VM is enabled.

   The time interval in which utilization is collected is defined by the \ :java:ref:`Datacenter.getSchedulingInterval()`\ .

   **See also:** :java:ref:`UtilizationHistory.enable()`

getVmm
^^^^^^

.. java:method::  String getVmm()
   :outertype: Vm

   Gets the Virtual Machine Monitor (VMM) that manages the VM.

   :return: VMM

isCreated
^^^^^^^^^

.. java:method::  boolean isCreated()
   :outertype: Vm

   Checks if the VM was created and placed inside the \ :java:ref:`Host <getHost()>`\ . If so, resources required by the Vm already were provisioned.

   :return: true, if it was created inside the Host, false otherwise

isFailed
^^^^^^^^

.. java:method::  boolean isFailed()
   :outertype: Vm

   Checks if the Vm is failed or not.

   **See also:** :java:ref:`.isWorking()`

isIdle
^^^^^^

.. java:method::  boolean isIdle()
   :outertype: Vm

   Checks if the VM is currently idle.

   :return: true if the VM currently idle, false otherwise

isIdleEnough
^^^^^^^^^^^^

.. java:method::  boolean isIdleEnough(double time)
   :outertype: Vm

   Checks if the VM has been idle for a given amount of time (in seconds).

   :param time: the time interval to check if the VM has been idle (in seconds). If time is zero, it will be checked if the VM is currently idle.
   :return: true if the VM has been idle as long as the given time, false if it's active of isn't idle as long enough

isInMigration
^^^^^^^^^^^^^

.. java:method::  boolean isInMigration()
   :outertype: Vm

   Checks if the VM is in migration process or not, that is, if it is migrating in or out of a Host.

isSuitableForCloudlet
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForCloudlet(Cloudlet cloudlet)
   :outertype: Vm

   Checks if the VM has enough capacity to run a Cloudlet.

   :param cloudlet: the candidate Cloudlet to run inside the VM
   :return: true if the VM can run the Cloudlet, false otherwise

isWorking
^^^^^^^^^

.. java:method::  boolean isWorking()
   :outertype: Vm

   Checks if the Vm is working or failed.

   **See also:** :java:ref:`.isFailed()`

notifyOnCreationFailureListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void notifyOnCreationFailureListeners(Datacenter failedDatacenter)
   :outertype: Vm

   Notifies all registered listeners when the Vm fail in being placed for lack of a \ :java:ref:`Host`\  with enough resources in a specific \ :java:ref:`Datacenter`\ .

   \ **This method is used just internally and must not be called directly.**\

   :param failedDatacenter: the Datacenter where the VM creation failed

notifyOnHostAllocationListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void notifyOnHostAllocationListeners()
   :outertype: Vm

   Notifies all registered listeners when a \ :java:ref:`Host`\  is allocated to the \ :java:ref:`Vm`\ .

   \ **This method is used just internally and must not be called directly.**\

notifyOnHostDeallocationListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void notifyOnHostDeallocationListeners(Host deallocatedHost)
   :outertype: Vm

   Notifies all registered listeners when the \ :java:ref:`Vm`\  is moved/removed from a \ :java:ref:`Host`\ .

   \ **This method is used just internally and must not be called directly.**\

   :param deallocatedHost: the \ :java:ref:`Host`\  the \ :java:ref:`Vm`\  was moved/removed from

removeOnCreationFailureListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener)
   :outertype: Vm

   Removes a listener from the onVmCreationFailureListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

removeOnHostAllocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnHostAllocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: Vm

   Removes a listener from the onHostAllocationListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

removeOnHostDeallocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnHostDeallocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: Vm

   Removes a listener from the onHostDeallocationListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

removeOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener)
   :outertype: Vm

   Removes a listener from the onUpdateVmProcessingListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

setBroker
^^^^^^^^^

.. java:method:: @Override  Vm setBroker(DatacenterBroker broker)
   :outertype: Vm

   Sets a \ :java:ref:`DatacenterBroker`\  that represents the owner of this Vm.

   :param broker: the \ :java:ref:`DatacenterBroker`\  to set

setBw
^^^^^

.. java:method::  Vm setBw(long bwCapacity)
   :outertype: Vm

   Sets the bandwidth capacity (in Megabits/s)

   :param bwCapacity: new BW capacity (in Megabits/s)

setBwVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm setBwVerticalScaling(VerticalVmScaling bwVerticalScaling) throws IllegalArgumentException
   :outertype: Vm

   Sets a \ :java:ref:`VerticalVmScaling`\  that will check if the Vm's \ :java:ref:`Bandwidth`\  is under or overloaded, based on some conditions defined by \ :java:ref:`Predicate`\ s given to the VerticalVmScaling, and then request the Bandwidth up or down scaling.

   :param bwVerticalScaling: the VerticalVmScaling to set
   :throws IllegalArgumentException: if the given VmScaling is already linked to a Vm. Each VM must have its own VerticalVmScaling objects or none at all.

setCloudletScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm setCloudletScheduler(CloudletScheduler cloudletScheduler)
   :outertype: Vm

   Sets the Cloudlet scheduler the Vm uses to schedule cloudlets execution. It also sets the Vm itself to the given scheduler.

   :param cloudletScheduler: the cloudlet scheduler to set

setCreated
^^^^^^^^^^

.. java:method::  void setCreated(boolean created)
   :outertype: Vm

   Changes the created status of the Vm inside the Host.

   :param created: true to indicate the VM was created inside the Host; false otherwise

   **See also:** :java:ref:`.isCreated()`

setDescription
^^^^^^^^^^^^^^

.. java:method::  Vm setDescription(String description)
   :outertype: Vm

   Sets the VM description, which is an optional text which one can use to provide details about this of this VM.

   :param description: the Vm description to set

setFailed
^^^^^^^^^

.. java:method::  void setFailed(boolean failed)
   :outertype: Vm

   Sets the status of VM to FAILED.

   :param failed: true to indicate that the VM is failed, false to indicate it is working

setHorizontalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm setHorizontalScaling(HorizontalVmScaling horizontalScaling) throws IllegalArgumentException
   :outertype: Vm

   Sets a \ :java:ref:`HorizontalVmScaling`\  that will check if the Vm is overloaded, based on some conditions defined by a \ :java:ref:`Predicate`\  given to the HorizontalVmScaling, and then request the creation of new VMs to horizontally scale the Vm.

   :param horizontalScaling: the HorizontalVmScaling to set
   :throws IllegalArgumentException: if the given VmScaling is already linked to a Vm. Each VM must have its own HorizontalVmScaling object or none at all.

setHost
^^^^^^^

.. java:method::  void setHost(Host host)
   :outertype: Vm

   Sets the PM that hosts the VM.

   :param host: Host to run the VM

setInMigration
^^^^^^^^^^^^^^

.. java:method::  void setInMigration(boolean migrating)
   :outertype: Vm

   Defines if the VM is in migration process or not.

   :param migrating: true to indicate the VM is migrating into a Host, false otherwise

setPeVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm setPeVerticalScaling(VerticalVmScaling peVerticalScaling) throws IllegalArgumentException
   :outertype: Vm

   Sets a \ :java:ref:`VerticalVmScaling`\  that will check if the Vm's \ :java:ref:`Pe`\  is under or overloaded, based on some conditions defined by \ :java:ref:`Predicate`\ s given to the VerticalVmScaling, and then request the Pe up or down scaling.

   The Pe scaling is performed by adding or removing PEs to/from the VM. Added PEs will have the same MIPS than the already existing ones.

   :param peVerticalScaling: the VerticalVmScaling to set
   :throws IllegalArgumentException: if the given VmScaling is already linked to a Vm. Each VM must have its own VerticalVmScaling objects or none at all.

setRam
^^^^^^

.. java:method::  Vm setRam(long ramCapacity)
   :outertype: Vm

   Sets RAM capacity in Megabytes.

   :param ramCapacity: new RAM capacity

setRamVerticalScaling
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm setRamVerticalScaling(VerticalVmScaling ramVerticalScaling) throws IllegalArgumentException
   :outertype: Vm

   Sets a \ :java:ref:`VerticalVmScaling`\  that will check if the Vm's \ :java:ref:`Ram`\  is under or overloaded, based on some conditions defined by \ :java:ref:`Predicate`\ s given to the VerticalVmScaling, and then request the RAM up or down scaling.

   :param ramVerticalScaling: the VerticalVmScaling to set
   :throws IllegalArgumentException: if the given VmScaling is already linked to a Vm. Each VM must have its own VerticalVmScaling objects or none at all.

setSize
^^^^^^^

.. java:method::  Vm setSize(long size)
   :outertype: Vm

   Sets the storage size (capacity) of the VM image in Megabytes.

   :param size: new storage size

setStartTime
^^^^^^^^^^^^

.. java:method::  Vm setStartTime(double startTime)
   :outertype: Vm

   Sets the time the VM was created into some Host for the first time. The value -1 means the VM was not created yet.

   :param startTime: the start time to set (in seconds)

setStopTime
^^^^^^^^^^^

.. java:method::  Vm setStopTime(double stopTime)
   :outertype: Vm

   Sets the time the VM was destroyed into the last Host it executed (in seconds). The value -1 means the VM has not stopped or has not even started yet.

   :param stopTime: the stop time to set (in seconds)

   **See also:** :java:ref:`.isCreated()`

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method::  double updateProcessing(double currentTime, List<Double> mipsShare)
   :outertype: Vm

   Updates the processing of cloudlets running on this VM.

   :param currentTime: current simulation time
   :param mipsShare: list with MIPS share of each Pe available to the scheduler
   :return: the predicted completion time of the earliest finishing cloudlet (which is a relative delay from the current simulation time), or \ :java:ref:`Double.MAX_VALUE`\  if there is no next Cloudlet to execute

