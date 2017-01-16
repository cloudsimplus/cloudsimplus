.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudsimplus.autoscaling HorizontalVmScaling

.. java:import:: org.cloudbus.cloudsim.core Delayable

.. java:import:: org.cloudbus.cloudsim.core UniquelyIdentificable

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util.function Predicate

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudsimplus.autoscaling VmScaling

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: org.cloudsimplus.listeners VmDatacenterEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

Vm
==

.. java:package:: org.cloudbus.cloudsim.vms
   :noindex:

.. java:type:: public interface Vm extends UniquelyIdentificable, Delayable, Comparable<Vm>

   An interface to be implemented by each class that provides basic features of Virtual Machines (VMs). The interface implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`Vm.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`Vm`\  variables.

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Vm NULL
   :outertype: Vm

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Vm`\  objects.

Methods
-------
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

addOnUpdateVmProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm addOnUpdateVmProcessingListener(EventListener<VmHostEventInfo> listener)
   :outertype: Vm

   Adds a listener object that will be notified every time when the processing of the Vm is updated in its \ :java:ref:`Host`\ .

   :param listener: the listener to seaddt

   **See also:** :java:ref:`.updateVmProcessing(double,java.util.List)`

addOnVmCreationFailureListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm addOnVmCreationFailureListener(EventListener<VmDatacenterEventInfo> listener)
   :outertype: Vm

   Adds a listener object that will be notified when the Vm fail in being placed for lack of a \ :java:ref:`Host`\  with enough resources in a specific \ :java:ref:`Datacenter`\ .

   :param listener: the listener to add

   **See also:** :java:ref:`.updateVmProcessing(double,java.util.List)`

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

.. java:method::  DatacenterBroker getBroker()
   :outertype: Vm

   Gets the \ :java:ref:`DatacenterBroker`\  that represents the owner of the VM.

   :return: the broker or  if a broker has not been set yet

getBw
^^^^^

.. java:method::  long getBw()
   :outertype: Vm

   Gets bandwidth capacity.

   :return: bandwidth capacity.

getCloudletScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method::  CloudletScheduler getCloudletScheduler()
   :outertype: Vm

   Gets the the Cloudlet scheduler the VM uses to schedule cloudlets execution.

   :return: the cloudlet scheduler

getCurrentAllocatedBw
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getCurrentAllocatedBw()
   :outertype: Vm

   Gets the current allocated bw.

   :return: the current allocated bw

getCurrentAllocatedRam
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getCurrentAllocatedRam()
   :outertype: Vm

   Gets the current allocated ram.

   :return: the current allocated ram

getCurrentAllocatedSize
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getCurrentAllocatedSize()
   :outertype: Vm

   Gets the current allocated storage size.

   :return: the current allocated size

   **See also:** :java:ref:`.getSize()`

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

   Gets the current requested max mips among all virtual PEs.

   :return: the current requested max mips

getCurrentRequestedMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Double> getCurrentRequestedMips()
   :outertype: Vm

   Gets the current requested mips.

   :return: the current requested mips

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

   Gets the current requested total mips. It is the sum of MIPS capacity requested for every VM's Pe.

   :return: the current requested total mips

   **See also:** :java:ref:`.getCurrentRequestedMips()`

getHorizontalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method::  VmScaling getHorizontalScaling()
   :outertype: Vm

   Gets the \ :java:ref:`HorizontalVmScaling`\  that will check if the Vm is overloaded, based on some conditions defined by a \ :java:ref:`Predicate`\  given to the HorizontalVmScaling.

   If no HorizontalVmScaling is set, the Broker will not dynamically
   create VMs to balance arrived Cloudlets.

getHost
^^^^^^^

.. java:method::  Host getHost()
   :outertype: Vm

   Gets the Host where the Vm is or will be placed. To know if the Vm was already created inside this Host, call the \ :java:ref:`isCreated()`\  method.

   :return: the host

   **See also:** :java:ref:`.isCreated()`

getMips
^^^^^^^

.. java:method::  double getMips()
   :outertype: Vm

   Gets the individual MIPS capacity of any VM's PE, considering that all PEs have the same capacity.

   :return: the mips

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method::  int getNumberOfPes()
   :outertype: Vm

   Gets the number of PEs required by the VM. Each PE has the capacity defined in \ :java:ref:`getMips()`\

   :return: the number of PEs

   **See also:** :java:ref:`.getMips()`

getRam
^^^^^^

.. java:method::  long getRam()
   :outertype: Vm

   Gets the RAM capacity in Megabytes.

   :return: the RAM capacity

getSimulation
^^^^^^^^^^^^^

.. java:method::  Simulation getSimulation()
   :outertype: Vm

   Gets the CloudSim instance that represents the simulation the Entity is related to.

getSize
^^^^^^^

.. java:method::  long getSize()
   :outertype: Vm

   Gets the storage size (capacity) of the VM image in Megabytes (the amount of storage it will use, at least initially).

   :return: amount of storage

getStateHistory
^^^^^^^^^^^^^^^

.. java:method::  List<VmStateHistoryEntry> getStateHistory()
   :outertype: Vm

   Gets the history of MIPS capacity allocated to the VM.

   :return: the state history

getTotalMipsCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalMipsCapacity()
   :outertype: Vm

   Gets the total MIPS capacity (across all PEs) of this VM.

   :return: MIPS capacity sum of all PEs

   **See also:** :java:ref:`.getMips()`, :java:ref:`.getNumberOfPes()`

getTotalUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalUtilizationOfCpu(double time)
   :outertype: Vm

   Gets total CPU utilization percentage of all Clouddlets running on this VM at the given time.

   :param time: the time
   :return: total utilization percentage

getTotalUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalUtilizationOfCpu()
   :outertype: Vm

   Gets total CPU utilization percentage (in scale from 0 to 1) of all Clouddlets running on this VM at the current simulation time.

   :return: total utilization percentage for the current time, in scale from 0 to 1

getTotalUtilizationOfCpuMips
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalUtilizationOfCpuMips(double time)
   :outertype: Vm

   Gets the total CPU utilization (in scale from 0 to 1) of all cloudlets running on this VM at the given time (in MIPS).

   :param time: the time
   :return: total cpu utilization in MIPS, in scale from 0 to 1

   **See also:** :java:ref:`.getTotalUtilizationOfCpu(double)`

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

isInMigration
^^^^^^^^^^^^^

.. java:method::  boolean isInMigration()
   :outertype: Vm

   Checks if the VM is in migration process or not.

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

notifyOnVmCreationFailureListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void notifyOnVmCreationFailureListeners(Datacenter failedDatacenter)
   :outertype: Vm

   Notifies all registered listeners when the Vm fail in being placed for lack of a \ :java:ref:`Host`\  with enough resources in a specific \ :java:ref:`Datacenter`\ .

   \ **This method is used just internally and must not be called directly.**\

   :param failedDatacenter: the Datacenter where the VM creation failed

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

removeOnUpdateVmProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnUpdateVmProcessingListener(EventListener<VmHostEventInfo> listener)
   :outertype: Vm

   Removes a listener from the onUpdateVmProcessingListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

removeOnVmCreationFailureListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnVmCreationFailureListener(EventListener<VmDatacenterEventInfo> listener)
   :outertype: Vm

   Removes a listener from the onVmCreationFailureListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

setBroker
^^^^^^^^^

.. java:method::  Vm setBroker(DatacenterBroker broker)
   :outertype: Vm

   Sets a \ :java:ref:`DatacenterBroker`\  that represents the owner of the VM.

   :param broker: the \ :java:ref:`DatacenterBroker`\  to set

setBw
^^^^^

.. java:method::  Vm setBw(long bwCapacity)
   :outertype: Vm

   Sets the BW capacity

   :param bwCapacity: new BW capacity

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

setFailed
^^^^^^^^^

.. java:method::  void setFailed(boolean failed)
   :outertype: Vm

   Sets the status of VM to FAILED.

   :param failed: the failed

setHorizontalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method::  Vm setHorizontalScaling(VmScaling horizontalScaling) throws IllegalArgumentException
   :outertype: Vm

   Sets the \ :java:ref:`HorizontalVmScaling`\  that will check if the Vm is overloaded, based on some conditions defined by a \ :java:ref:`Predicate`\  given to the HorizontalVmScaling.

   If no HorizontalVmScaling is set, the Broker will not dynamically
   create VMs to balance arrived Cloudlets.

   :param horizontalScaling: the HorizontalVmScaling to set
   :throws IllegalArgumentException: if the given Vm Scaling already is linked to a Vm. Each VM must have its own scaling object.

setHost
^^^^^^^

.. java:method::  void setHost(Host host)
   :outertype: Vm

   Sets the PM that hosts the VM.

   :param host: Host to run the VM

setInMigration
^^^^^^^^^^^^^^

.. java:method::  void setInMigration(boolean inMigration)
   :outertype: Vm

   Defines if the VM is in migration process or not.

   :param inMigration: true to indicate the VM is migrating into a Host, false otherwise

setRam
^^^^^^

.. java:method::  Vm setRam(long ramCapacity)
   :outertype: Vm

   Sets RAM capacity in Megabytes.

   :param ramCapacity: new RAM capacity

setSize
^^^^^^^

.. java:method::  Vm setSize(long size)
   :outertype: Vm

   Sets the storage size (capacity) of the VM image in Megabytes.

   :param size: new storage size

updateVmProcessing
^^^^^^^^^^^^^^^^^^

.. java:method::  double updateVmProcessing(double currentTime, List<Double> mipsShare)
   :outertype: Vm

   Updates the processing of cloudlets running on this VM.

   :param currentTime: current simulation time
   :param mipsShare: list with MIPS share of each Pe available to the scheduler
   :return: the predicted completion time of the earliest finishing cloudlet (that is a future simulation time), or \ :java:ref:`Double.MAX_VALUE`\  if there is no next Cloudlet to execute

