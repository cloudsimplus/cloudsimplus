.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostUpdatesVmsProcessingEventInfo

.. java:import:: org.cloudbus.cloudsim.lists PeList

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.resources RawStorage

HostSimple
==========

.. java:package:: org.cloudbus.cloudsim.hosts
   :noindex:

.. java:type:: public class HostSimple implements Host

   A Host class that implements the most basic features of a Physical Machine (PM) inside a \ :java:ref:`Datacenter`\ . It executes actions related to management of virtual machines (e.g., creation and destruction). A host has a defined policy for provisioning memory and bw, as well as an allocation policy for PEs to \ :java:ref:`virtual machines <Vm>`\ . A host is associated to a Datacenter and can host virtual machines.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

Constructors
------------
HostSimple
^^^^^^^^^^

.. java:constructor:: public HostSimple(int id, long storageCapacity, List<Pe> peList)
   :outertype: HostSimple

   Creates a Host.

   :param id: the host id
   :param storageCapacity: the storage capacity in Megabytes
   :param peList: the host's PEs list

HostSimple
^^^^^^^^^^

.. java:constructor:: @Deprecated public HostSimple(int id, ResourceProvisioner ramProvisioner, ResourceProvisioner bwProvisioner, long storageCapacity, List<Pe> peList, VmScheduler vmScheduler)
   :outertype: HostSimple

   Creates a Host with the given parameters.

   :param id: the host id
   :param ramProvisioner: the ram provisioner with capacity in Megabytes
   :param bwProvisioner: the bw provisioner with capacity in Megabits/s
   :param storageCapacity: the storage capacity in Megabytes
   :param peList: the host's PEs list
   :param vmScheduler: the vm scheduler

Methods
-------
addMigratingInVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addMigratingInVm(Vm vm)
   :outertype: HostSimple

allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShare)
   :outertype: HostSimple

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(Host o)
   :outertype: HostSimple

   Compare this Host with another one based on \ :java:ref:`getTotalMips()`\ .

   :param o: the Host to compare to
   :return: {@inheritDoc}

deallocatePesForVm
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForVm(Vm vm)
   :outertype: HostSimple

deallocateResourcesOfAllVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void deallocateResourcesOfAllVms()
   :outertype: HostSimple

   Deallocate all resources that all VMs were using.

deallocateResourcesOfVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void deallocateResourcesOfVm(Vm vm)
   :outertype: HostSimple

   Deallocate all resources that a VM was using.

   :param vm: the VM

destroyAllVms
^^^^^^^^^^^^^

.. java:method:: @Override public void destroyAllVms()
   :outertype: HostSimple

destroyVm
^^^^^^^^^

.. java:method:: @Override public void destroyVm(Vm vm)
   :outertype: HostSimple

equals
^^^^^^

.. java:method:: @Override public boolean equals(Object o)
   :outertype: HostSimple

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: HostSimple

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAvailableMips()
   :outertype: HostSimple

getAvailableStorage
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableStorage()
   :outertype: HostSimple

getBwCapacity
^^^^^^^^^^^^^

.. java:method:: @Override public long getBwCapacity()
   :outertype: HostSimple

getBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getBwProvisioner()
   :outertype: HostSimple

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: HostSimple

getId
^^^^^

.. java:method:: @Override public int getId()
   :outertype: HostSimple

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxAvailableMips()
   :outertype: HostSimple

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfFreePes()
   :outertype: HostSimple

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfPes()
   :outertype: HostSimple

getNumberOfWorkingPes
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfWorkingPes()
   :outertype: HostSimple

getOnUpdateVmsProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public EventListener<HostUpdatesVmsProcessingEventInfo> getOnUpdateVmsProcessingListener()
   :outertype: HostSimple

getPeList
^^^^^^^^^

.. java:method:: @Override public List<Pe> getPeList()
   :outertype: HostSimple

getRamCapacity
^^^^^^^^^^^^^^

.. java:method:: @Override public long getRamCapacity()
   :outertype: HostSimple

getRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getRamProvisioner()
   :outertype: HostSimple

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: HostSimple

getStorage
^^^^^^^^^^

.. java:method:: protected RawStorage getStorage()
   :outertype: HostSimple

   Gets the storage device of the host with capacity in Megabytes.

   :return: the storage device

getStorageCapacity
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getStorageCapacity()
   :outertype: HostSimple

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: HostSimple

getTotalMips
^^^^^^^^^^^^

.. java:method:: @Override public long getTotalMips()
   :outertype: HostSimple

getVm
^^^^^

.. java:method:: @Override public Vm getVm(int vmId, int brokerId)
   :outertype: HostSimple

getVmList
^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmList()
   :outertype: HostSimple

getVmScheduler
^^^^^^^^^^^^^^

.. java:method:: @Override public VmScheduler getVmScheduler()
   :outertype: HostSimple

getVmsMigratingIn
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmsMigratingIn()
   :outertype: HostSimple

hashCode
^^^^^^^^

.. java:method:: @Override public int hashCode()
   :outertype: HostSimple

isFailed
^^^^^^^^

.. java:method:: @Override public boolean isFailed()
   :outertype: HostSimple

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm)
   :outertype: HostSimple

reallocateMigratingInVms
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void reallocateMigratingInVms()
   :outertype: HostSimple

removeMigratingInVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void removeMigratingInVm(Vm vm)
   :outertype: HostSimple

setBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Host setBwProvisioner(ResourceProvisioner bwProvisioner)
   :outertype: HostSimple

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenter(Datacenter datacenter)
   :outertype: HostSimple

setFailed
^^^^^^^^^

.. java:method:: @Override public final boolean setFailed(boolean failed)
   :outertype: HostSimple

setId
^^^^^

.. java:method:: protected final void setId(int id)
   :outertype: HostSimple

   Sets the host id.

   :param id: the new host id

setOnUpdateVmsProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener)
   :outertype: HostSimple

setPeList
^^^^^^^^^

.. java:method:: protected final Host setPeList(List<Pe> peList)
   :outertype: HostSimple

   Sets the pe list.

   :param peList: the new pe list

setPeStatus
^^^^^^^^^^^

.. java:method:: @Override public boolean setPeStatus(int peId, Pe.Status status)
   :outertype: HostSimple

setRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Host setRamProvisioner(ResourceProvisioner ramProvisioner)
   :outertype: HostSimple

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Host setSimulation(Simulation simulation)
   :outertype: HostSimple

setVmScheduler
^^^^^^^^^^^^^^

.. java:method:: @Override public final Host setVmScheduler(VmScheduler vmScheduler)
   :outertype: HostSimple

setVmsToFailedWhenHostIsFailed
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setVmsToFailedWhenHostIsFailed()
   :outertype: HostSimple

   Checks if the the host is failed and sets all its Vm' to failed.

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: HostSimple

updateVmsProcessing
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateVmsProcessing(double currentTime)
   :outertype: HostSimple

vmCreate
^^^^^^^^

.. java:method:: @Override public boolean vmCreate(Vm vm)
   :outertype: HostSimple

