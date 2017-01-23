.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.core Identificable

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostUpdatesVmsProcessingEventInfo

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

Host
====

.. java:package:: org.cloudbus.cloudsim.hosts
   :noindex:

.. java:type:: public interface Host extends Identificable, Resourceful, Comparable<Host>

   An interface to be implemented by each class that provides Physical Machines (Hosts) features. The interface implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`Host.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`Host`\  variables.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Host NULL
   :outertype: Host

   A property that implements the Null Object Design Pattern for \ :java:ref:`Host`\  objects.

Methods
-------
addMigratingInVm
^^^^^^^^^^^^^^^^

.. java:method::  void addMigratingInVm(Vm vm)
   :outertype: Host

   Adds a VM migrating into the current host.

   :param vm: the vm

allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method::  boolean allocatePesForVm(Vm vm, List<Double> mipsShare)
   :outertype: Host

   Allocates PEs for a VM.

   :param vm: the vm
   :param mipsShare: the list of MIPS share to be allocated to the VM
   :return: $true if this policy allows a new VM in the host, $false otherwise

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

destroyVm
^^^^^^^^^

.. java:method::  void destroyVm(Vm vm)
   :outertype: Host

   Destroys a VM running in the host and removes it from the \ :java:ref:`getVmList()`\ .

   :param vm: the VM

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

   Gets the total free MIPS available at the host.

   :return: the free mips

getAvailableStorage
^^^^^^^^^^^^^^^^^^^

.. java:method::  long getAvailableStorage()
   :outertype: Host

   Gets the total free storage available at the host in Megabytes.

   :return: the free storage

getBw
^^^^^

.. java:method::  Resource getBw()
   :outertype: Host

   Gets the host bw capacity in Megabits/s.

   :return: the host bw capacity

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

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method::  double getMaxAvailableMips()
   :outertype: Host

   Returns the maximum available MIPS among all the PEs of the host.

   :return: max mips

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method::  int getNumberOfFreePes()
   :outertype: Host

   Gets the free pes number.

   :return: the free pes number

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method::  int getNumberOfPes()
   :outertype: Host

   Gets the PEs number.

   :return: the pes number

getNumberOfWorkingPes
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getNumberOfWorkingPes()
   :outertype: Host

   Gets the number of PEs that are working. That is, the number of PEs that aren't FAIL.

   :return: the number of working pes

getOnUpdateVmsProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  EventListener<HostUpdatesVmsProcessingEventInfo> getOnUpdateVmsProcessingListener()
   :outertype: Host

   Gets the listener object that will be notified every time when the host updates the processing of all its \ :java:ref:`VMs <Vm>`\ .

   :return: the onUpdateVmsProcessingListener

   **See also:** :java:ref:`.updateVmsProcessing(double)`

getPeList
^^^^^^^^^

.. java:method::  List<Pe> getPeList()
   :outertype: Host

   Gets the Processing Elements (PEs) of the host, that represent its CPU cores and thus, its processing capacity.

   :return: the pe list

getProvisioner
^^^^^^^^^^^^^^

.. java:method::  ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> resourceClass)
   :outertype: Host

   Gets the \ :java:ref:`ResourceProvisioner`\ s that manages a Host resource such as \ :java:ref:`Ram`\ , \ :java:ref:`Bandwidth`\  and \ :java:ref:`Pe`\ .

   :param resourceClass: the class of the resource to get its provisioner
   :return: the \ :java:ref:`ResourceProvisioner`\  for the given resource class

getRam
^^^^^^

.. java:method::  Resource getRam()
   :outertype: Host

   Gets the host memory resource in Megabytes.

   :return: the host memory

getRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method::  ResourceProvisioner getRamProvisioner()
   :outertype: Host

   Gets the ram provisioner with capacity in Megabytes.

   :return: the ram provisioner

getSimulation
^^^^^^^^^^^^^

.. java:method::  Simulation getSimulation()
   :outertype: Host

   Gets the CloudSim instance that represents the simulation the Entity is related to.

   **See also:** :java:ref:`.setSimulation(Simulation)`

getStorage
^^^^^^^^^^

.. java:method::  Resource getStorage()
   :outertype: Host

   Gets the storage device of the host with capacity in Megabytes.

   :return: the host storage device

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: Host

   Gets the total allocated MIPS for a VM along all its PEs.

   :param vm: the vm
   :return: the allocated mips for vm

getTotalMips
^^^^^^^^^^^^

.. java:method::  long getTotalMips()
   :outertype: Host

   Gets the total mips.

   :return: the total mips

getVm
^^^^^

.. java:method::  Vm getVm(int vmId, int brokerId)
   :outertype: Host

   Gets a VM by its id and user.

   :param vmId: the vm id
   :param brokerId: ID of VM's owner
   :return: the virtual machine object, $null if not found

getVmList
^^^^^^^^^

.. java:method::  <T extends Vm> List<T> getVmList()
   :outertype: Host

   Gets the list of VMs assigned to the host.

   :param <T>: The generic type
   :return: the vm list

getVmScheduler
^^^^^^^^^^^^^^

.. java:method::  VmScheduler getVmScheduler()
   :outertype: Host

   Gets the policy for allocation of host PEs to VMs in order to schedule VM execution.

   :return: the \ :java:ref:`VmScheduler`\

getVmsMigratingIn
^^^^^^^^^^^^^^^^^

.. java:method::  <T extends Vm> List<T> getVmsMigratingIn()
   :outertype: Host

   Gets the list of VMs migrating into this host.

   :param <T>: the generic type
   :return: the vms migrating in

isFailed
^^^^^^^^

.. java:method::  boolean isFailed()
   :outertype: Host

   Checks if the host is working properly or has failed.

   :return: true, if the host PEs have failed; false otherwise

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method::  boolean isSuitableForVm(Vm vm)
   :outertype: Host

   Checks if the host is suitable for vm. If it has enough resources to attend the VM.

   :param vm: the vm
   :return: true, if is suitable for vm

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

setOnUpdateVmsProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Host setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener)
   :outertype: Host

   Sets the listener object that will be notified every time when the host updates the processing of all its \ :java:ref:`VMs <Vm>`\ .

   :param onUpdateVmsProcessingListener: the onUpdateVmsProcessingListener to set

   **See also:** :java:ref:`.updateVmsProcessing(double)`

setPeStatus
^^^^^^^^^^^

.. java:method::  boolean setPeStatus(int peId, Pe.Status status)
   :outertype: Host

   Sets the particular Pe status on the host.

   :param peId: the pe id
   :param status: the new Pe status
   :return: \ ``true``\  if the Pe status has set, \ ``false``\  otherwise (Pe id might not be exist)

setRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method::  Host setRamProvisioner(ResourceProvisioner ramProvisioner)
   :outertype: Host

   Sets the ram provisioner with capacity in Megabytes.

   :param ramProvisioner: the new ram provisioner

setSimulation
^^^^^^^^^^^^^

.. java:method::  Host setSimulation(Simulation simulation)
   :outertype: Host

   Sets the CloudSim instance that represents the simulation the Entity is related to. Such attribute has to be set by the \ :java:ref:`Datacenter`\  that the host belongs to.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to

setVmScheduler
^^^^^^^^^^^^^^

.. java:method::  Host setVmScheduler(VmScheduler vmScheduler)
   :outertype: Host

   Sets the policy for allocation of host PEs to VMs in order to schedule VM execution. The host also sets itself to the given scheduler. It also sets the Host itself to the given scheduler.

   :param vmScheduler: the vm scheduler to set

updateVmsProcessing
^^^^^^^^^^^^^^^^^^^

.. java:method::  double updateVmsProcessing(double currentTime)
   :outertype: Host

   Updates the processing of VMs running on this Host, that makes the processing of cloudlets inside such VMs to be updated.

   :param currentTime: the current time
   :return: the predicted completion time of the earliest finishing cloudlet (that is a future simulation time), or \ :java:ref:`Double.MAX_VALUE`\  if there is no next Cloudlet to execute

vmCreate
^^^^^^^^

.. java:method::  boolean vmCreate(Vm vm)
   :outertype: Host

   Try to allocate resources to a new VM in the Host.

   :param vm: Vm being started
   :return: $true if the VM could be started in the host; $false otherwise

