.. java:import:: org.cloudbus.cloudsim.core Machine

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.power.models PowerModel

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Bandwidth

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Pe.Status

.. java:import:: org.cloudbus.cloudsim.resources Ram

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.vms VmUtilizationHistory

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostUpdatesVmsProcessingEventInfo

.. java:import:: java.util DoubleSummaryStatistics

.. java:import:: java.util List

.. java:import:: java.util Set

.. java:import:: java.util SortedMap

Host
====

.. java:package:: org.cloudbus.cloudsim.hosts
   :noindex:

.. java:type:: public interface Host extends Machine, Comparable<Host>

   An interface to be implemented by each class that provides Physical Machines (Hosts) features. The interface implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`Host.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`Host`\  variables.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Host NULL
   :outertype: Host

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Host`\  objects.

Methods
-------
addMigratingInVm
^^^^^^^^^^^^^^^^

.. java:method::  boolean addMigratingInVm(Vm vm)
   :outertype: Host

   Try to add a VM migrating into the current host if there is enough resources for it. In this case, the resources are allocated and the VM added to the \ :java:ref:`getVmsMigratingIn()`\  List. Otherwise, the VM is not added.

   :param vm: the vm
   :return: true if the Vm was migrated in, false if the Host doesn't have enough resources to place the Vm

addOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener)
   :outertype: Host

   Adds a listener object that will be notified every time when the host updates the processing of all its \ :java:ref:`VMs <Vm>`\ .

   :param listener: the OnUpdateProcessingListener to add

   **See also:** :java:ref:`.updateProcessing(double)`

addVmMigratingOut
^^^^^^^^^^^^^^^^^

.. java:method::  boolean addVmMigratingOut(Vm vm)
   :outertype: Host

   Adds a \ :java:ref:`Vm`\  to the list of VMs migrating out from the Host.

   :param vm: the vm to be added
   :return: true if the VM wasn't into the list and was added, false otherwise

createTemporaryVm
^^^^^^^^^^^^^^^^^

.. java:method::  boolean createTemporaryVm(Vm vm)
   :outertype: Host

   Try to allocate resources to a new temporary VM in the Host. The method is used only to book resources for a given VM. For instance, if is being chosen Hosts to migrate a set of VMs, when a Host is selected for a given VM, using this method, the resources are reserved and then, when the next VM is selected for the same Host, the reserved resources already were reduced from the available amount. This way, it it was possible to place just one Vm into that Host, with the booking, no other VM will be selected to that Host.

   :param vm: Vm being started
   :return: $true if the VM could be started in the host; $false otherwise

createVm
^^^^^^^^

.. java:method::  boolean createVm(Vm vm)
   :outertype: Host

   Try to allocate resources to a new VM in the Host.

   :param vm: Vm being started
   :return: $true if the VM could be started in the host; $false otherwise

deallocatePesForVm
^^^^^^^^^^^^^^^^^^

.. java:method::  void deallocatePesForVm(Vm vm)
   :outertype: Host

   Releases PEs allocated to a VM.

   :param vm: the vm

destroyAllVms
^^^^^^^^^^^^^

.. java:method::  void destroyAllVms()
   :outertype: Host

   Destroys all VMs running in the host and remove them from the \ :java:ref:`getVmList()`\ .

destroyTemporaryVm
^^^^^^^^^^^^^^^^^^

.. java:method::  void destroyTemporaryVm(Vm vm)
   :outertype: Host

   Destroys a temporary VM created into the Host to book resources.

   :param vm: the VM

   **See also:** :java:ref:`.createTemporaryVm(Vm)`

destroyVm
^^^^^^^^^

.. java:method::  void destroyVm(Vm vm)
   :outertype: Host

   Destroys a VM running in the host and removes it from the \ :java:ref:`getVmList()`\ .

   :param vm: the VM

disableStateHistory
^^^^^^^^^^^^^^^^^^^

.. java:method::  void disableStateHistory()
   :outertype: Host

   Disable storing Host state history.

   **See also:** :java:ref:`.getStateHistory()`

enableStateHistory
^^^^^^^^^^^^^^^^^^

.. java:method::  void enableStateHistory()
   :outertype: Host

   Enables storing Host state history.

   **See also:** :java:ref:`.getStateHistory()`

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: Host

   Gets the MIPS share of each Pe that is allocated to a given VM.

   :param vm: the vm
   :return: an array containing the amount of MIPS of each pe that is available to the VM

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method::  double getAvailableMips()
   :outertype: Host

   Gets the current amount of available MIPS at the host.

   :return: the available amount of MIPS

getAvailableStorage
^^^^^^^^^^^^^^^^^^^

.. java:method::  long getAvailableStorage()
   :outertype: Host

   Gets the total free storage available at the host in Megabytes.

   :return: the free storage

getBuzyPeList
^^^^^^^^^^^^^

.. java:method::  List<Pe> getBuzyPeList()
   :outertype: Host

   Gets the list of working Processing Elements (PEs) of the host, \ **which excludes failed PEs**\ .

   :return: the list working (non-failed) Host PEs

getBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method::  ResourceProvisioner getBwProvisioner()
   :outertype: Host

   Gets the bandwidth (BW) provisioner with capacity in Megabits/s.

   :return: the bw provisioner

getDatacenter
^^^^^^^^^^^^^

.. java:method::  Datacenter getDatacenter()
   :outertype: Host

   Gets the Datacenter where the host is placed.

   :return: the data center of the host

getFinishedVms
^^^^^^^^^^^^^^

.. java:method::  List<Vm> getFinishedVms()
   :outertype: Host

   Gets the List of VMs that have finished executing.

getFreePeList
^^^^^^^^^^^^^

.. java:method::  List<Pe> getFreePeList()
   :outertype: Host

   Gets the list of Free Processing Elements (PEs) of the host, \ **which excludes failed PEs**\ .

   :return: the list free (non-failed) Host PEs

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method::  double getMaxAvailableMips()
   :outertype: Host

   Returns the maximum available MIPS among all the PEs of the host.

   :return: max mips

getNumberOfFailedPes
^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getNumberOfFailedPes()
   :outertype: Host

   Gets the number of PEs that have failed.

   :return: the number of failed pes

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method::  int getNumberOfFreePes()
   :outertype: Host

   Gets the free pes number.

   :return: the free pes number

getNumberOfWorkingPes
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getNumberOfWorkingPes()
   :outertype: Host

   Gets the number of PEs that are working. That is, the number of PEs that aren't FAIL.

   :return: the number of working pes

getPeList
^^^^^^^^^

.. java:method::  List<Pe> getPeList()
   :outertype: Host

   Gets the list of all Processing Elements (PEs) of the host, including failed PEs.

   :return: the list of all Host PEs

   **See also:** :java:ref:`.getWorkingPeList()`

getPowerModel
^^^^^^^^^^^^^

.. java:method::  PowerModel getPowerModel()
   :outertype: Host

   Gets the \ :java:ref:`PowerModel`\  used by the host to define how it consumes power. A Host just provides power usage data if a PowerModel is set.

   :return: the Host's \ :java:ref:`PowerModel`\

getPreviousUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getPreviousUtilizationOfCpu()
   :outertype: Host

getProvisioner
^^^^^^^^^^^^^^

.. java:method::  ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> resourceClass)
   :outertype: Host

   Gets the \ :java:ref:`ResourceProvisioner`\ s that manages a Host resource such as \ :java:ref:`Ram`\ , \ :java:ref:`Bandwidth`\  and \ :java:ref:`Pe`\ .

   :param resourceClass: the class of the resource to get its provisioner
   :return: the \ :java:ref:`ResourceProvisioner`\  for the given resource class

getRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method::  ResourceProvisioner getRamProvisioner()
   :outertype: Host

   Gets the ram provisioner with capacity in Megabytes.

   :return: the ram provisioner

getShutdownTime
^^^^^^^^^^^^^^^

.. java:method::  double getShutdownTime()
   :outertype: Host

   Gets the time the Host shut down.

getStartTime
^^^^^^^^^^^^

.. java:method::  double getStartTime()
   :outertype: Host

   Gets the time the Host was powered-on (in seconds).

getStateHistory
^^^^^^^^^^^^^^^

.. java:method::  List<HostStateHistoryEntry> getStateHistory()
   :outertype: Host

   Gets a \ **read-only**\  host state history. This List is just populated if \ :java:ref:`isStateHistoryEnabled()`\

   :return: the state history

   **See also:** :java:ref:`.enableStateHistory()`

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: Host

   Gets the total allocated MIPS for a VM along all its PEs.

   :param vm: the vm
   :return: the allocated mips for vm

getTotalMipsCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override  double getTotalMipsCapacity()
   :outertype: Host

   Gets total MIPS capacity of PEs which are not \ :java:ref:`Status.FAILED`\ .

   :return: the total MIPS of working PEs

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  SortedMap<Double, DoubleSummaryStatistics> getUtilizationHistory()
   :outertype: Host

   Gets a map containing the host CPU utilization percentage history (between [0 and 1]), based on its VM utilization history. Each key is a time when the data collection was performed and each value is a \ :java:ref:`DoubleSummaryStatistics`\  from where some operations over the CPU utilization entries for every VM inside the Host can be performed, such as counting, summing, averaging, etc. For instance, if you call the \ :java:ref:`DoubleSummaryStatistics.getSum()`\ , you'll get the total Host's CPU utilization for the time specified by the map key.

   There is an entry for each time multiple of the \ :java:ref:`Datacenter.getSchedulingInterval()`\ . \ **This way, it's required to set a Datacenter scheduling interval with the desired value.**\

   In order to enable the Host to get utilization history,
   its VMs' utilization history must be enabled
   by calling enable() from
   the .

   :return: a Map where keys are the data collection time and each value is a \ :java:ref:`DoubleSummaryStatistics`\  objects that provides lots of useful methods to get max, min, average, count and sum of utilization values.

   **See also:** :java:ref:`.getUtilizationHistorySum()`

getUtilizationHistorySum
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  SortedMap<Double, Double> getUtilizationHistorySum()
   :outertype: Host

   Gets a map containing the host CPU utilization percentage history (between [0 and 1]), based on its VM utilization history. Each key is a time when the data collection was performed and each value is the sum of all CPU utilization of the VMs running inside this Host for that time. This way, the value represents the total Host's CPU utilization for each time that data was collected.

   There is an entry for each time multiple of the \ :java:ref:`Datacenter.getSchedulingInterval()`\ . \ **This way, it's required to set a Datacenter scheduling interval with the desired value.**\

   In order to enable the Host to get utilization history,
   its VMs' utilization history must be enabled
   by calling enable() from
   the .

   :return: a Map where keys are the data collection time and each value is a \ :java:ref:`DoubleSummaryStatistics`\  objects that provides lots of useful methods to get max, min, average, count and sum of utilization values.

   **See also:** :java:ref:`.getUtilizationHistory()`

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method::  long getUtilizationOfBw()
   :outertype: Host

   Gets the current utilization of bw (in absolute values).

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method::  double getUtilizationOfCpu()
   :outertype: Host

   Gets current utilization of CPU in percentage (between [0 and 1]), considering the usage of all its PEs..

getUtilizationOfCpuMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getUtilizationOfCpuMips()
   :outertype: Host

   Gets the current total utilization of CPU in MIPS, considering the usage of all its PEs.

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method::  long getUtilizationOfRam()
   :outertype: Host

   Gets the current utilization of memory (in absolute values).

getVm
^^^^^

.. java:method::  Vm getVm(int vmId, int brokerId)
   :outertype: Host

   Gets a VM by its id and user.

   :param vmId: the vm id
   :param brokerId: ID of VM's owner
   :return: the virtual machine object, $null if not found

getVmCreatedList
^^^^^^^^^^^^^^^^

.. java:method::  <T extends Vm> List<T> getVmCreatedList()
   :outertype: Host

   Gets a \ **read-only**\  list of all VMs which have been created into the host during the entire simulation. This way, this method returns a historic list of created VMs, including those ones already destroyed.

   :param <T>: The generic type
   :return: the read-only vm created list

getVmList
^^^^^^^^^

.. java:method::  <T extends Vm> List<T> getVmList()
   :outertype: Host

   Gets a \ **read-only**\  list of VMs currently assigned to the host.

   :param <T>: The generic type
   :return: the read-only vm list

getVmScheduler
^^^^^^^^^^^^^^

.. java:method::  VmScheduler getVmScheduler()
   :outertype: Host

   Gets the policy for allocation of host PEs to VMs in order to schedule VM execution.

   :return: the \ :java:ref:`VmScheduler`\

getVmsMigratingIn
^^^^^^^^^^^^^^^^^

.. java:method::  <T extends Vm> Set<T> getVmsMigratingIn()
   :outertype: Host

   Gets the list of VMs migrating into this host.

   :param <T>: the generic type
   :return: the vms migrating in

getVmsMigratingOut
^^^^^^^^^^^^^^^^^^

.. java:method::  Set<Vm> getVmsMigratingOut()
   :outertype: Host

   Gets a \ **read-only**\  list of VMs migrating out from the Host.

getWorkingPeList
^^^^^^^^^^^^^^^^

.. java:method::  List<Pe> getWorkingPeList()
   :outertype: Host

   Gets the list of working Processing Elements (PEs) of the host. It's the list of all PEs which are not \ **FAILEd**\ .

   :return: the list working (non-failed) Host PEs

isActive
^^^^^^^^

.. java:method::  boolean isActive()
   :outertype: Host

   Checks if the Host is powered-on or not.

   :return: true if the Host is powered-on, false otherwise.

isFailed
^^^^^^^^

.. java:method::  boolean isFailed()
   :outertype: Host

   Checks if the host is working properly or has failed.

   :return: true, if the host PEs have failed; false otherwise

isStateHistoryEnabled
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isStateHistoryEnabled()
   :outertype: Host

   Checks if Host state history is being collected and stored.

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForVm(Vm vm)
   :outertype: Host

   Checks if the host is active and is suitable for vm (if it has enough resources to attend the VM).

   :param vm: the vm to check
   :return: true if is suitable for vm, false otherwise

reallocateMigratingInVms
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void reallocateMigratingInVms()
   :outertype: Host

   Reallocate VMs migrating into the host. Gets the VM in the migrating in queue and allocate them on the host.

removeMigratingInVm
^^^^^^^^^^^^^^^^^^^

.. java:method::  void removeMigratingInVm(Vm vm)
   :outertype: Host

   Removes a migrating in vm.

   :param vm: the vm

removeOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener)
   :outertype: Host

   Removes a listener object from the OnUpdateProcessingListener List.

   :param listener: the listener to remove
   :return: true if the listener was found and removed, false otherwise

   **See also:** :java:ref:`.updateProcessing(double)`

removeVmMigratingIn
^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeVmMigratingIn(Vm vm)
   :outertype: Host

   Adds a \ :java:ref:`Vm`\  to the list of VMs migrating into the Host.

   :param vm: the vm to be added

removeVmMigratingOut
^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean removeVmMigratingOut(Vm vm)
   :outertype: Host

   Adds a \ :java:ref:`Vm`\  to the list of VMs migrating out from the Host.

   :param vm: the vm to be added

setActive
^^^^^^^^^

.. java:method::  Host setActive(boolean active)
   :outertype: Host

   Sets the powered state of the Host, to indicate if it's powered on or off. When a Host is powered off, no VMs will be submitted to it.

   If it is set to powered off while VMs are running inside it, it is simulated a scheduled shutdown, so that, all running VMs will finish, but not more VMs will be submitted to this Host.

   :param active: true to set the Host as powered on, false as powered off

setBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method::  Host setBwProvisioner(ResourceProvisioner bwProvisioner)
   :outertype: Host

   Sets the bandwidth (BW) provisioner with capacity in Megabits/s.

   :param bwProvisioner: the new bw provisioner

setDatacenter
^^^^^^^^^^^^^

.. java:method::  void setDatacenter(Datacenter datacenter)
   :outertype: Host

   Sets the Datacenter where the host is placed.

   :param datacenter: the new data center to move the host

setFailed
^^^^^^^^^

.. java:method::  boolean setFailed(boolean failed)
   :outertype: Host

   Sets the Host state to "failed" or "working".

   :param failed: true to set the Host to "failed", false to set to "working"
   :return: true if the Host status was changed, false otherwise

setPowerModel
^^^^^^^^^^^^^

.. java:method::  Host setPowerModel(PowerModel powerModel)
   :outertype: Host

   Sets the \ :java:ref:`PowerModel`\  used by the host to define how it consumes power. A Host just provides power usage data if a PowerModel is set.

   :param powerModel: the \ :java:ref:`PowerModel`\  to set

setRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method::  Host setRamProvisioner(ResourceProvisioner ramProvisioner)
   :outertype: Host

   Sets the ram provisioner with capacity in Megabytes.

   :param ramProvisioner: the new ram provisioner

setShutdownTime
^^^^^^^^^^^^^^^

.. java:method::  void setShutdownTime(double shutdownTime)
   :outertype: Host

   Sets the time the Host shut down.

   :param shutdownTime: the time to set

setSimulation
^^^^^^^^^^^^^

.. java:method::  Host setSimulation(Simulation simulation)
   :outertype: Host

   Sets the CloudSim instance that represents the simulation the Entity is related to. Such attribute has to be set by the \ :java:ref:`Datacenter`\  that the host belongs to.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to

setStartTime
^^^^^^^^^^^^

.. java:method::  void setStartTime(double startTime)
   :outertype: Host

   Sets the time the Host was powered-on.

   :param startTime: the time to set (in seconds)

setVmScheduler
^^^^^^^^^^^^^^

.. java:method::  Host setVmScheduler(VmScheduler vmScheduler)
   :outertype: Host

   Sets the policy for allocation of host PEs to VMs in order to schedule VM execution. The host also sets itself to the given scheduler. It also sets the Host itself to the given scheduler.

   :param vmScheduler: the vm scheduler to set

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method::  double updateProcessing(double currentTime)
   :outertype: Host

   Updates the processing of VMs running on this Host, that makes the processing of cloudlets inside such VMs to be updated.

   :param currentTime: the current time
   :return: the predicted completion time of the earliest finishing cloudlet (which is a relative delay from the current simulation time), or \ :java:ref:`Double.MAX_VALUE`\  if there is no next Cloudlet to execute

