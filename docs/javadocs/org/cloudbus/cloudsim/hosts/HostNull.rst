.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.power.models PowerModel

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Resource

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostUpdatesVmsProcessingEventInfo

HostNull
========

.. java:package:: org.cloudbus.cloudsim.hosts
   :noindex:

.. java:type:: final class HostNull implements Host

   A class that implements the Null Object Design Pattern for \ :java:ref:`Host`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Host.NULL`

Methods
-------
addMigratingInVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addMigratingInVm(Vm vm)
   :outertype: HostNull

addOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener)
   :outertype: HostNull

addVmMigratingOut
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addVmMigratingOut(Vm vm)
   :outertype: HostNull

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(Host host)
   :outertype: HostNull

createTemporaryVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean createTemporaryVm(Vm vm)
   :outertype: HostNull

createVm
^^^^^^^^

.. java:method:: @Override public boolean createVm(Vm vm)
   :outertype: HostNull

deallocatePesForVm
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForVm(Vm vm)
   :outertype: HostNull

destroyAllVms
^^^^^^^^^^^^^

.. java:method:: @Override public void destroyAllVms()
   :outertype: HostNull

destroyTemporaryVm
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void destroyTemporaryVm(Vm vm)
   :outertype: HostNull

destroyVm
^^^^^^^^^

.. java:method:: @Override public void destroyVm(Vm vm)
   :outertype: HostNull

disableStateHistory
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void disableStateHistory()
   :outertype: HostNull

enableStateHistory
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void enableStateHistory()
   :outertype: HostNull

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: HostNull

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAvailableMips()
   :outertype: HostNull

getAvailableStorage
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableStorage()
   :outertype: HostNull

getBuzyPeList
^^^^^^^^^^^^^

.. java:method:: @Override public List<Pe> getBuzyPeList()
   :outertype: HostNull

getBw
^^^^^

.. java:method:: @Override public Resource getBw()
   :outertype: HostNull

getBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getBwProvisioner()
   :outertype: HostNull

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: HostNull

getFinishedVms
^^^^^^^^^^^^^^

.. java:method:: @Override public List<Vm> getFinishedVms()
   :outertype: HostNull

getFreePeList
^^^^^^^^^^^^^

.. java:method:: @Override public List<Pe> getFreePeList()
   :outertype: HostNull

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: HostNull

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxAvailableMips()
   :outertype: HostNull

getMips
^^^^^^^

.. java:method:: @Override public double getMips()
   :outertype: HostNull

getNumberOfFailedPes
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfFailedPes()
   :outertype: HostNull

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfFreePes()
   :outertype: HostNull

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfPes()
   :outertype: HostNull

getNumberOfWorkingPes
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfWorkingPes()
   :outertype: HostNull

getPeList
^^^^^^^^^

.. java:method:: @Override public List<Pe> getPeList()
   :outertype: HostNull

getPowerModel
^^^^^^^^^^^^^

.. java:method:: @Override public PowerModel getPowerModel()
   :outertype: HostNull

getPreviousUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousUtilizationOfCpu()
   :outertype: HostNull

getProvisioner
^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> clazz)
   :outertype: HostNull

getRam
^^^^^^

.. java:method:: @Override public Resource getRam()
   :outertype: HostNull

getRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getRamProvisioner()
   :outertype: HostNull

getResources
^^^^^^^^^^^^

.. java:method:: @Override public List<ResourceManageable> getResources()
   :outertype: HostNull

getShutdownTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getShutdownTime()
   :outertype: HostNull

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: HostNull

getStartTime
^^^^^^^^^^^^

.. java:method:: @Override public double getStartTime()
   :outertype: HostNull

getStateHistory
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<HostStateHistoryEntry> getStateHistory()
   :outertype: HostNull

getStorage
^^^^^^^^^^

.. java:method:: @Override public Resource getStorage()
   :outertype: HostNull

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: HostNull

getTotalMipsCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalMipsCapacity()
   :outertype: HostNull

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public SortedMap<Double, DoubleSummaryStatistics> getUtilizationHistory()
   :outertype: HostNull

getUtilizationHistorySum
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public SortedMap<Double, Double> getUtilizationHistorySum()
   :outertype: HostNull

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getUtilizationOfBw()
   :outertype: HostNull

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpu()
   :outertype: HostNull

getUtilizationOfCpuMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpuMips()
   :outertype: HostNull

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getUtilizationOfRam()
   :outertype: HostNull

getVm
^^^^^

.. java:method:: @Override public Vm getVm(int vmId, int brokerId)
   :outertype: HostNull

getVmCreatedList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmCreatedList()
   :outertype: HostNull

getVmList
^^^^^^^^^

.. java:method:: @Override public List<Vm> getVmList()
   :outertype: HostNull

getVmScheduler
^^^^^^^^^^^^^^

.. java:method:: @Override public VmScheduler getVmScheduler()
   :outertype: HostNull

getVmsMigratingIn
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> Set<T> getVmsMigratingIn()
   :outertype: HostNull

getVmsMigratingOut
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Vm> getVmsMigratingOut()
   :outertype: HostNull

getWorkingPeList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Pe> getWorkingPeList()
   :outertype: HostNull

isActive
^^^^^^^^

.. java:method:: @Override public boolean isActive()
   :outertype: HostNull

isFailed
^^^^^^^^

.. java:method:: @Override public boolean isFailed()
   :outertype: HostNull

isStateHistoryEnabled
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isStateHistoryEnabled()
   :outertype: HostNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm)
   :outertype: HostNull

reallocateMigratingInVms
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void reallocateMigratingInVms()
   :outertype: HostNull

removeMigratingInVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void removeMigratingInVm(Vm vm)
   :outertype: HostNull

removeOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener)
   :outertype: HostNull

removeVmMigratingIn
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeVmMigratingIn(Vm vm)
   :outertype: HostNull

removeVmMigratingOut
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeVmMigratingOut(Vm vm)
   :outertype: HostNull

setActive
^^^^^^^^^

.. java:method:: @Override public Host setActive(boolean active)
   :outertype: HostNull

setBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host setBwProvisioner(ResourceProvisioner bwProvisioner)
   :outertype: HostNull

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenter(Datacenter datacenter)
   :outertype: HostNull

setFailed
^^^^^^^^^

.. java:method:: @Override public boolean setFailed(boolean failed)
   :outertype: HostNull

setId
^^^^^

.. java:method:: @Override public void setId(long id)
   :outertype: HostNull

setPowerModel
^^^^^^^^^^^^^

.. java:method:: @Override public Host setPowerModel(PowerModel powerModel)
   :outertype: HostNull

setRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host setRamProvisioner(ResourceProvisioner ramProvisioner)
   :outertype: HostNull

setShutdownTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public void setShutdownTime(double shutdownTime)
   :outertype: HostNull

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Host setSimulation(Simulation simulation)
   :outertype: HostNull

setStartTime
^^^^^^^^^^^^

.. java:method:: @Override public void setStartTime(double startTime)
   :outertype: HostNull

setVmScheduler
^^^^^^^^^^^^^^

.. java:method:: @Override public Host setVmScheduler(VmScheduler vmScheduler)
   :outertype: HostNull

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: HostNull

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime)
   :outertype: HostNull

