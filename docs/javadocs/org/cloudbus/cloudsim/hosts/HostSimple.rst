.. java:import:: org.cloudbus.cloudsim.core ChangeableId

.. java:import:: org.cloudbus.cloudsim.core Machine

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.power.models PowerModel

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisionerSimple

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmSchedulerSpaceShared

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.vms UtilizationHistory

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.vms VmStateHistoryEntry

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostUpdatesVmsProcessingEventInfo

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util.function BinaryOperator

.. java:import:: java.util.function Function

.. java:import:: java.util.function Predicate

.. java:import:: java.util.function Supplier

.. java:import:: java.util.stream Collectors

.. java:import:: java.util.stream Stream

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

.. java:constructor:: public HostSimple(List<Pe> peList)
   :outertype: HostSimple

   Creates and powers on a Host without a pre-defined ID, 10GB of RAM, 1000Mbps of Bandwidth and 500GB of Storage. It creates a \ :java:ref:`ResourceProvisionerSimple`\  for RAM and Bandwidth. Finally, it sets a \ :java:ref:`VmSchedulerSpaceShared`\  as default. The ID is automatically set when a List of Hosts is attached to a \ :java:ref:`Datacenter`\ .

   :param peList: the host's \ :java:ref:`Pe`\  list

   **See also:** :java:ref:`ChangeableId.setId(long)`, :java:ref:`.setRamProvisioner(ResourceProvisioner)`, :java:ref:`.setBwProvisioner(ResourceProvisioner)`, :java:ref:`.setVmScheduler(VmScheduler)`, :java:ref:`.setDefaultRamCapacity(long)`, :java:ref:`.setDefaultBwCapacity(long)`, :java:ref:`.setDefaultStorageCapacity(long)`

HostSimple
^^^^^^^^^^

.. java:constructor:: public HostSimple(List<Pe> peList, boolean activate)
   :outertype: HostSimple

   Creates a Host without a pre-defined ID, 10GB of RAM, 1000Mbps of Bandwidth and 500GB of Storage and enabling the host to be powered on or not.

   It creates a \ :java:ref:`ResourceProvisionerSimple`\  for RAM and Bandwidth. Finally, it sets a \ :java:ref:`VmSchedulerSpaceShared`\  as default. The ID is automatically set when a List of Hosts is attached to a \ :java:ref:`Datacenter`\ .

   :param peList: the host's \ :java:ref:`Pe`\  list
   :param activate: define the Host activation status: true to power on, false to power off

   **See also:** :java:ref:`ChangeableId.setId(long)`, :java:ref:`.setRamProvisioner(ResourceProvisioner)`, :java:ref:`.setBwProvisioner(ResourceProvisioner)`, :java:ref:`.setVmScheduler(VmScheduler)`, :java:ref:`.setDefaultRamCapacity(long)`, :java:ref:`.setDefaultBwCapacity(long)`, :java:ref:`.setDefaultStorageCapacity(long)`

HostSimple
^^^^^^^^^^

.. java:constructor:: public HostSimple(ResourceProvisioner ramProvisioner, ResourceProvisioner bwProvisioner, long storage, List<Pe> peList)
   :outertype: HostSimple

   Creates and powers on a Host with the given parameters and a \ :java:ref:`VmSchedulerSpaceShared`\  as default.

   :param ramProvisioner: the ram provisioner with capacity in Megabytes
   :param bwProvisioner: the bw provisioner with capacity in Megabits/s
   :param storage: the storage capacity in Megabytes
   :param peList: the host's PEs list

   **See also:** :java:ref:`.setVmScheduler(VmScheduler)`

HostSimple
^^^^^^^^^^

.. java:constructor:: public HostSimple(long ram, long bw, long storage, List<Pe> peList)
   :outertype: HostSimple

   Creates and powers on a Host without a pre-defined ID. It uses a \ :java:ref:`ResourceProvisionerSimple`\  for RAM and Bandwidth and also sets a \ :java:ref:`VmSchedulerSpaceShared`\  as default. The ID is automatically set when a List of Hosts is attached to a \ :java:ref:`Datacenter`\ .

   :param ram: the RAM capacity in Megabytes
   :param bw: the Bandwidth (BW) capacity in Megabits/s
   :param storage: the storage capacity in Megabytes
   :param peList: the host's \ :java:ref:`Pe`\  list

   **See also:** :java:ref:`ChangeableId.setId(long)`, :java:ref:`.setRamProvisioner(ResourceProvisioner)`, :java:ref:`.setBwProvisioner(ResourceProvisioner)`, :java:ref:`.setVmScheduler(VmScheduler)`

HostSimple
^^^^^^^^^^

.. java:constructor:: public HostSimple(long ram, long bw, long storage, List<Pe> peList, boolean activate)
   :outertype: HostSimple

   Creates a Host without a pre-defined ID. It uses a \ :java:ref:`ResourceProvisionerSimple`\  for RAM and Bandwidth and also sets a \ :java:ref:`VmSchedulerSpaceShared`\  as default. The ID is automatically set when a List of Hosts is attached to a \ :java:ref:`Datacenter`\ .

   :param ram: the RAM capacity in Megabytes
   :param bw: the Bandwidth (BW) capacity in Megabits/s
   :param storage: the storage capacity in Megabytes
   :param peList: the host's \ :java:ref:`Pe`\  list
   :param activate: define the Host activation status: true to power on, false to power off

   **See also:** :java:ref:`ChangeableId.setId(long)`, :java:ref:`.setRamProvisioner(ResourceProvisioner)`, :java:ref:`.setBwProvisioner(ResourceProvisioner)`, :java:ref:`.setVmScheduler(VmScheduler)`

Methods
-------
addMigratingInVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addMigratingInVm(Vm vm)
   :outertype: HostSimple

addOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener)
   :outertype: HostSimple

addVmMigratingOut
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addVmMigratingOut(Vm vm)
   :outertype: HostSimple

addVmToCreatedList
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addVmToCreatedList(Vm vm)
   :outertype: HostSimple

addVmToList
^^^^^^^^^^^

.. java:method:: protected void addVmToList(Vm vm)
   :outertype: HostSimple

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(Host o)
   :outertype: HostSimple

   Compare this Host with another one based on \ :java:ref:`getTotalMipsCapacity()`\ .

   :param o: the Host to compare to
   :return: {@inheritDoc}

createTemporaryVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean createTemporaryVm(Vm vm)
   :outertype: HostSimple

createVm
^^^^^^^^

.. java:method:: @Override public boolean createVm(Vm vm)
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

destroyTemporaryVm
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void destroyTemporaryVm(Vm vm)
   :outertype: HostSimple

destroyVm
^^^^^^^^^

.. java:method:: @Override public void destroyVm(Vm vm)
   :outertype: HostSimple

disableStateHistory
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void disableStateHistory()
   :outertype: HostSimple

enableStateHistory
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void enableStateHistory()
   :outertype: HostSimple

equals
^^^^^^

.. java:method:: @Override public boolean equals(Object o)
   :outertype: HostSimple

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: HostSimple

   Gets the MIPS share of each Pe that is allocated to a given VM.

   :param vm: the vm
   :return: an array containing the amount of MIPS of each pe that is available to the VM

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAvailableMips()
   :outertype: HostSimple

getAvailableStorage
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableStorage()
   :outertype: HostSimple

getBusyPeList
^^^^^^^^^^^^^

.. java:method:: @Override public List<Pe> getBusyPeList()
   :outertype: HostSimple

getBw
^^^^^

.. java:method:: @Override public Resource getBw()
   :outertype: HostSimple

getBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getBwProvisioner()
   :outertype: HostSimple

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: HostSimple

getDefaultBwCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static long getDefaultBwCapacity()
   :outertype: HostSimple

   Gets the Default Bandwidth capacity (in Mbps) for creating Hosts. This value is used when the BW capacity is not given in a Host constructor.

getDefaultRamCapacity
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static long getDefaultRamCapacity()
   :outertype: HostSimple

   Gets the Default RAM capacity (in MB) for creating Hosts. This value is used when the RAM capacity is not given in a Host constructor.

getDefaultStorageCapacity
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static long getDefaultStorageCapacity()
   :outertype: HostSimple

   Gets the Default Storage capacity (in MB) for creating Hosts. This value is used when the Storage capacity is not given in a Host constructor.

getFailedPesNumber
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getFailedPesNumber()
   :outertype: HostSimple

getFinishedVms
^^^^^^^^^^^^^^

.. java:method:: @Override public List<Vm> getFinishedVms()
   :outertype: HostSimple

getFreePeList
^^^^^^^^^^^^^

.. java:method:: @Override public List<Pe> getFreePeList()
   :outertype: HostSimple

getFreePesNumber
^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getFreePesNumber()
   :outertype: HostSimple

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: HostSimple

getIdleShutdownDeadline
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getIdleShutdownDeadline()
   :outertype: HostSimple

getLastBusyTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getLastBusyTime()
   :outertype: HostSimple

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getMaxAvailableMips()
   :outertype: HostSimple

   Returns the maximum available MIPS among all the PEs of the host.

   :return: max mips

getMigratableVms
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Vm> getMigratableVms()
   :outertype: HostSimple

getMips
^^^^^^^

.. java:method:: @Override public double getMips()
   :outertype: HostSimple

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfPes()
   :outertype: HostSimple

   {@inheritDoc}

   :return: {@inheritDoc}

   **See also:** :java:ref:`.getWorkingPesNumber()`, :java:ref:`.getFreePesNumber()`, :java:ref:`.getFailedPesNumber()`

getPeList
^^^^^^^^^

.. java:method:: @Override public List<Pe> getPeList()
   :outertype: HostSimple

getPowerModel
^^^^^^^^^^^^^

.. java:method:: @Override public PowerModel getPowerModel()
   :outertype: HostSimple

getPreviousUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousUtilizationOfCpu()
   :outertype: HostSimple

getProvisioner
^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> resourceClass)
   :outertype: HostSimple

getRam
^^^^^^

.. java:method:: @Override public Resource getRam()
   :outertype: HostSimple

getRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getRamProvisioner()
   :outertype: HostSimple

getResources
^^^^^^^^^^^^

.. java:method:: @Override public List<ResourceManageable> getResources()
   :outertype: HostSimple

getShutdownTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getShutdownTime()
   :outertype: HostSimple

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: HostSimple

getStartTime
^^^^^^^^^^^^

.. java:method:: @Override public double getStartTime()
   :outertype: HostSimple

getStateHistory
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<HostStateHistoryEntry> getStateHistory()
   :outertype: HostSimple

getStorage
^^^^^^^^^^

.. java:method:: @Override public Resource getStorage()
   :outertype: HostSimple

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: HostSimple

getTotalMipsCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalMipsCapacity()
   :outertype: HostSimple

getTotalUpTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalUpTime()
   :outertype: HostSimple

getUpTime
^^^^^^^^^

.. java:method:: @Override public double getUpTime()
   :outertype: HostSimple

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public SortedMap<Double, DoubleSummaryStatistics> getUtilizationHistory()
   :outertype: HostSimple

getUtilizationHistorySum
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public SortedMap<Double, Double> getUtilizationHistorySum()
   :outertype: HostSimple

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getUtilizationOfBw()
   :outertype: HostSimple

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpu()
   :outertype: HostSimple

getUtilizationOfCpuMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpuMips()
   :outertype: HostSimple

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getUtilizationOfRam()
   :outertype: HostSimple

getVmCreatedList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmCreatedList()
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

.. java:method:: @Override public <T extends Vm> Set<T> getVmsMigratingIn()
   :outertype: HostSimple

getVmsMigratingOut
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Vm> getVmsMigratingOut()
   :outertype: HostSimple

getWorkingPeList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Pe> getWorkingPeList()
   :outertype: HostSimple

getWorkingPesNumber
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getWorkingPesNumber()
   :outertype: HostSimple

hasEverStarted
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean hasEverStarted()
   :outertype: HostSimple

hashCode
^^^^^^^^

.. java:method:: @Override public int hashCode()
   :outertype: HostSimple

isActive
^^^^^^^^

.. java:method:: @Override public boolean isActive()
   :outertype: HostSimple

isFailed
^^^^^^^^

.. java:method:: @Override public boolean isFailed()
   :outertype: HostSimple

isStateHistoryEnabled
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isStateHistoryEnabled()
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

removeOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener)
   :outertype: HostSimple

removeVmMigratingOut
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeVmMigratingOut(Vm vm)
   :outertype: HostSimple

setActive
^^^^^^^^^

.. java:method:: @Override public final Host setActive(boolean activate)
   :outertype: HostSimple

setBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Host setBwProvisioner(ResourceProvisioner bwProvisioner)
   :outertype: HostSimple

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public final void setDatacenter(Datacenter datacenter)
   :outertype: HostSimple

setDefaultBwCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void setDefaultBwCapacity(long defaultCapacity)
   :outertype: HostSimple

   Sets the Default Bandwidth capacity (in Mbps) for creating Hosts. This value is used when the BW capacity is not given in a Host constructor.

setDefaultRamCapacity
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void setDefaultRamCapacity(long defaultCapacity)
   :outertype: HostSimple

   Sets the Default RAM capacity (in MB) for creating Hosts. This value is used when the RAM capacity is not given in a Host constructor.

setDefaultStorageCapacity
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void setDefaultStorageCapacity(long defaultCapacity)
   :outertype: HostSimple

   Sets the Default Storage capacity (in MB) for creating Hosts. This value is used when the Storage capacity is not given in a Host constructor.

setFailed
^^^^^^^^^

.. java:method:: @Override public final boolean setFailed(boolean failed)
   :outertype: HostSimple

setId
^^^^^

.. java:method:: @Override public final void setId(long id)
   :outertype: HostSimple

setIdleShutdownDeadline
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host setIdleShutdownDeadline(double deadline)
   :outertype: HostSimple

setPeList
^^^^^^^^^

.. java:method:: protected final Host setPeList(List<Pe> peList)
   :outertype: HostSimple

   Sets the pe list.

   :param peList: the new pe list

setPeStatus
^^^^^^^^^^^

.. java:method:: public final void setPeStatus(List<Pe> peList, Pe.Status newStatus)
   :outertype: HostSimple

   Sets the status of a given (sub)list of \ :java:ref:`Pe`\  to a new status.

   :param peList: the (sub)list of \ :java:ref:`Pe`\  to change the status
   :param newStatus: the new status

setPowerModel
^^^^^^^^^^^^^

.. java:method:: @Override public Host setPowerModel(PowerModel powerModel)
   :outertype: HostSimple

setRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Host setRamProvisioner(ResourceProvisioner ramProvisioner)
   :outertype: HostSimple

setShutdownTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public void setShutdownTime(double shutdownTime)
   :outertype: HostSimple

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public final Host setSimulation(Simulation simulation)
   :outertype: HostSimple

setStartTime
^^^^^^^^^^^^

.. java:method:: @Override public void setStartTime(double startTime)
   :outertype: HostSimple

setVmScheduler
^^^^^^^^^^^^^^

.. java:method:: @Override public final Host setVmScheduler(VmScheduler vmScheduler)
   :outertype: HostSimple

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: HostSimple

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @SuppressWarnings @Override public double updateProcessing(double currentTime)
   :outertype: HostSimple

