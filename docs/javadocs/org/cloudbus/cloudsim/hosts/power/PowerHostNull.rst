.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts HostStateHistoryEntry

.. java:import:: org.cloudbus.cloudsim.power.models PowerModel

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Resource

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostUpdatesVmsProcessingEventInfo

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Set

PowerHostNull
=============

.. java:package:: org.cloudbus.cloudsim.hosts.power
   :noindex:

.. java:type:: final class PowerHostNull implements PowerHost

   A class that implements the Null Object Design Pattern for \ :java:ref:`PowerHost`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`PowerHost.NULL`

Methods
-------
addMigratingInVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addMigratingInVm(Vm vm)
   :outertype: PowerHostNull

addOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> l)
   :outertype: PowerHostNull

addStateHistoryEntry
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addStateHistoryEntry(double time, double amips, double rmips, boolean active)
   :outertype: PowerHostNull

addVmMigratingOut
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addVmMigratingOut(Vm vm)
   :outertype: PowerHostNull

allocatePesForVm
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShare)
   :outertype: PowerHostNull

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(Host o)
   :outertype: PowerHostNull

createTemporaryVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean createTemporaryVm(Vm vm)
   :outertype: PowerHostNull

createVm
^^^^^^^^

.. java:method:: @Override public boolean createVm(Vm vm)
   :outertype: PowerHostNull

deallocatePesForVm
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesForVm(Vm vm)
   :outertype: PowerHostNull

destroyAllVms
^^^^^^^^^^^^^

.. java:method:: @Override public void destroyAllVms()
   :outertype: PowerHostNull

destroyTemporaryVm
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void destroyTemporaryVm(Vm vm)
   :outertype: PowerHostNull

destroyVm
^^^^^^^^^

.. java:method:: @Override public void destroyVm(Vm vm)
   :outertype: PowerHostNull

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: PowerHostNull

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAvailableMips()
   :outertype: PowerHostNull

getAvailableStorage
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableStorage()
   :outertype: PowerHostNull

getBw
^^^^^

.. java:method:: @Override public Resource getBw()
   :outertype: PowerHostNull

getBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getBwProvisioner()
   :outertype: PowerHostNull

getDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getDatacenter()
   :outertype: PowerHostNull

getEnergyLinearInterpolation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getEnergyLinearInterpolation(double from, double to, double time)
   :outertype: PowerHostNull

getFinishedVms
^^^^^^^^^^^^^^

.. java:method:: @Override public List<Vm> getFinishedVms()
   :outertype: PowerHostNull

getId
^^^^^

.. java:method:: @Override public int getId()
   :outertype: PowerHostNull

getMaxAvailableMips
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxAvailableMips()
   :outertype: PowerHostNull

getMaxPower
^^^^^^^^^^^

.. java:method:: @Override public double getMaxPower()
   :outertype: PowerHostNull

getMaxUtilization
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxUtilization()
   :outertype: PowerHostNull

getMaxUtilizationAmongVmsPes
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMaxUtilizationAmongVmsPes(Vm vm)
   :outertype: PowerHostNull

getMips
^^^^^^^

.. java:method:: @Override public double getMips()
   :outertype: PowerHostNull

getNumberOfFailedPes
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfFailedPes()
   :outertype: PowerHostNull

getNumberOfFreePes
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumberOfFreePes()
   :outertype: PowerHostNull

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfPes()
   :outertype: PowerHostNull

getNumberOfWorkingPes
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfWorkingPes()
   :outertype: PowerHostNull

getPeList
^^^^^^^^^

.. java:method:: @Override public List<Pe> getPeList()
   :outertype: PowerHostNull

getPower
^^^^^^^^

.. java:method:: @Override public double getPower()
   :outertype: PowerHostNull

getPowerModel
^^^^^^^^^^^^^

.. java:method:: @Override public PowerModel getPowerModel()
   :outertype: PowerHostNull

getPreviousUtilizationMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousUtilizationMips()
   :outertype: PowerHostNull

getPreviousUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousUtilizationOfCpu()
   :outertype: PowerHostNull

getProvisioner
^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> c)
   :outertype: PowerHostNull

getRam
^^^^^^

.. java:method:: @Override public Resource getRam()
   :outertype: PowerHostNull

getRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public ResourceProvisioner getRamProvisioner()
   :outertype: PowerHostNull

getResources
^^^^^^^^^^^^

.. java:method:: @Override public List<ResourceManageable> getResources()
   :outertype: PowerHostNull

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: PowerHostNull

getStateHistory
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<HostStateHistoryEntry> getStateHistory()
   :outertype: PowerHostNull

getStorage
^^^^^^^^^^

.. java:method:: @Override public Resource getStorage()
   :outertype: PowerHostNull

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: PowerHostNull

getTotalMipsCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalMipsCapacity()
   :outertype: PowerHostNull

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getUtilizationOfBw()
   :outertype: PowerHostNull

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpu()
   :outertype: PowerHostNull

getUtilizationOfCpuMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpuMips()
   :outertype: PowerHostNull

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getUtilizationOfRam()
   :outertype: PowerHostNull

getVm
^^^^^

.. java:method:: @Override public Vm getVm(int vmId, int brokerId)
   :outertype: PowerHostNull

getVmList
^^^^^^^^^

.. java:method:: @Override public <T extends Vm> List<T> getVmList()
   :outertype: PowerHostNull

getVmScheduler
^^^^^^^^^^^^^^

.. java:method:: @Override public VmScheduler getVmScheduler()
   :outertype: PowerHostNull

getVmsMigratingIn
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public <T extends Vm> Set<T> getVmsMigratingIn()
   :outertype: PowerHostNull

getVmsMigratingOut
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Vm> getVmsMigratingOut()
   :outertype: PowerHostNull

getWorkingPeList
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Pe> getWorkingPeList()
   :outertype: PowerHostNull

isActive
^^^^^^^^

.. java:method:: @Override public boolean isActive()
   :outertype: PowerHostNull

isFailed
^^^^^^^^

.. java:method:: @Override public boolean isFailed()
   :outertype: PowerHostNull

isSuitableForVm
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForVm(Vm vm)
   :outertype: PowerHostNull

reallocateMigratingInVms
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void reallocateMigratingInVms()
   :outertype: PowerHostNull

removeMigratingInVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void removeMigratingInVm(Vm vm)
   :outertype: PowerHostNull

removeOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> l)
   :outertype: PowerHostNull

removeVmMigratingIn
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeVmMigratingIn(Vm vm)
   :outertype: PowerHostNull

removeVmMigratingOut
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeVmMigratingOut(Vm vm)
   :outertype: PowerHostNull

setActive
^^^^^^^^^

.. java:method:: @Override public Host setActive(boolean active)
   :outertype: PowerHostNull

setBwProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host setBwProvisioner(ResourceProvisioner bwProvisioner)
   :outertype: PowerHostNull

setDatacenter
^^^^^^^^^^^^^

.. java:method:: @Override public void setDatacenter(Datacenter datacenter)
   :outertype: PowerHostNull

setFailed
^^^^^^^^^

.. java:method:: @Override public boolean setFailed(boolean failed)
   :outertype: PowerHostNull

setId
^^^^^

.. java:method:: @Override public void setId(int id)
   :outertype: PowerHostNull

setPeStatus
^^^^^^^^^^^

.. java:method:: @Override public boolean setPeStatus(int peId, Pe.Status status)
   :outertype: PowerHostNull

setPowerModel
^^^^^^^^^^^^^

.. java:method:: @Override public PowerHost setPowerModel(PowerModel powerModel)
   :outertype: PowerHostNull

setRamProvisioner
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Host setRamProvisioner(ResourceProvisioner ramProvisioner)
   :outertype: PowerHostNull

setSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Host setSimulation(Simulation simulation)
   :outertype: PowerHostNull

setVmScheduler
^^^^^^^^^^^^^^

.. java:method:: @Override public Host setVmScheduler(VmScheduler vmScheduler)
   :outertype: PowerHostNull

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime)
   :outertype: PowerHostNull

