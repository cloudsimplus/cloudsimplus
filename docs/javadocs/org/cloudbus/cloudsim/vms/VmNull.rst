.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Processor

.. java:import:: org.cloudbus.cloudsim.resources Resource

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudsimplus.autoscaling HorizontalVmScaling

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners VmDatacenterEventInfo

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: java.util Collections

.. java:import:: java.util List

VmNull
======

.. java:package:: org.cloudbus.cloudsim.vms
   :noindex:

.. java:type:: final class VmNull implements Vm

   A class that implements the Null Object Design Pattern for \ :java:ref:`Vm`\  objects.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Vm.NULL`

Methods
-------
addOnCreationFailureListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm addOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener)
   :outertype: VmNull

addOnHostAllocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm addOnHostAllocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmNull

addOnHostDeallocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm addOnHostDeallocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmNull

addOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm addOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmNull

addStateHistoryEntry
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addStateHistoryEntry(VmStateHistoryEntry entry)
   :outertype: VmNull

allocateResource
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void allocateResource(Class<? extends ResourceManageable> clazz, long amount)
   :outertype: VmNull

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(Vm vm)
   :outertype: VmNull

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateResource(Class<? extends ResourceManageable> clazz)
   :outertype: VmNull

getBroker
^^^^^^^^^

.. java:method:: @Override public DatacenterBroker getBroker()
   :outertype: VmNull

getBw
^^^^^

.. java:method:: @Override public Resource getBw()
   :outertype: VmNull

getBwVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling getBwVerticalScaling()
   :outertype: VmNull

getCloudletScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public CloudletScheduler getCloudletScheduler()
   :outertype: VmNull

getCpuPercentUsage
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCpuPercentUsage(double time)
   :outertype: VmNull

getCpuPercentUsage
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCpuPercentUsage()
   :outertype: VmNull

getCurrentRequestedBw
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getCurrentRequestedBw()
   :outertype: VmNull

getCurrentRequestedMaxMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCurrentRequestedMaxMips()
   :outertype: VmNull

getCurrentRequestedMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getCurrentRequestedMips()
   :outertype: VmNull

getCurrentRequestedRam
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getCurrentRequestedRam()
   :outertype: VmNull

getCurrentRequestedTotalMips
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCurrentRequestedTotalMips()
   :outertype: VmNull

getDescription
^^^^^^^^^^^^^^

.. java:method:: @Override public String getDescription()
   :outertype: VmNull

getHorizontalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public HorizontalVmScaling getHorizontalScaling()
   :outertype: VmNull

getHost
^^^^^^^

.. java:method:: @Override public Host getHost()
   :outertype: VmNull

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: VmNull

getIdleInterval
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getIdleInterval()
   :outertype: VmNull

getLastBusyTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getLastBusyTime()
   :outertype: VmNull

getMips
^^^^^^^

.. java:method:: @Override public double getMips()
   :outertype: VmNull

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfPes()
   :outertype: VmNull

getPeVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling getPeVerticalScaling()
   :outertype: VmNull

getProcessor
^^^^^^^^^^^^

.. java:method:: @Override public Processor getProcessor()
   :outertype: VmNull

getRam
^^^^^^

.. java:method:: @Override public Resource getRam()
   :outertype: VmNull

getRamVerticalScaling
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling getRamVerticalScaling()
   :outertype: VmNull

getResources
^^^^^^^^^^^^

.. java:method:: @Override public List<ResourceManageable> getResources()
   :outertype: VmNull

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: VmNull

getStartTime
^^^^^^^^^^^^

.. java:method:: @Override public double getStartTime()
   :outertype: VmNull

getStateHistory
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<VmStateHistoryEntry> getStateHistory()
   :outertype: VmNull

getStopTime
^^^^^^^^^^^

.. java:method:: @Override public double getStopTime()
   :outertype: VmNull

getStorage
^^^^^^^^^^

.. java:method:: @Override public Resource getStorage()
   :outertype: VmNull

getSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSubmissionDelay()
   :outertype: VmNull

getTotalCpuMipsUsage
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCpuMipsUsage()
   :outertype: VmNull

getTotalCpuMipsUsage
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCpuMipsUsage(double time)
   :outertype: VmNull

getTotalExecutionTime
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalExecutionTime()
   :outertype: VmNull

getTotalMipsCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalMipsCapacity()
   :outertype: VmNull

getUid
^^^^^^

.. java:method:: @Override public String getUid()
   :outertype: VmNull

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationHistory getUtilizationHistory()
   :outertype: VmNull

getVmm
^^^^^^

.. java:method:: @Override public String getVmm()
   :outertype: VmNull

isCreated
^^^^^^^^^

.. java:method:: @Override public boolean isCreated()
   :outertype: VmNull

isFailed
^^^^^^^^

.. java:method:: @Override public boolean isFailed()
   :outertype: VmNull

isIdle
^^^^^^

.. java:method:: @Override public boolean isIdle()
   :outertype: VmNull

isIdleEnough
^^^^^^^^^^^^

.. java:method:: @Override public boolean isIdleEnough(double time)
   :outertype: VmNull

isInMigration
^^^^^^^^^^^^^

.. java:method:: @Override public boolean isInMigration()
   :outertype: VmNull

isSuitableForCloudlet
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForCloudlet(Cloudlet cloudlet)
   :outertype: VmNull

isWorking
^^^^^^^^^

.. java:method:: @Override public boolean isWorking()
   :outertype: VmNull

notifyOnCreationFailureListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void notifyOnCreationFailureListeners(Datacenter failedDatacenter)
   :outertype: VmNull

notifyOnHostAllocationListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void notifyOnHostAllocationListeners()
   :outertype: VmNull

notifyOnHostDeallocationListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void notifyOnHostDeallocationListeners(Host deallocatedHost)
   :outertype: VmNull

removeOnCreationFailureListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener)
   :outertype: VmNull

removeOnHostAllocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnHostAllocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmNull

removeOnHostDeallocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnHostDeallocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmNull

removeOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmNull

setBroker
^^^^^^^^^

.. java:method:: @Override public Vm setBroker(DatacenterBroker broker)
   :outertype: VmNull

setBw
^^^^^

.. java:method:: @Override public Vm setBw(long bwCapacity)
   :outertype: VmNull

setBwVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm setBwVerticalScaling(VerticalVmScaling scaling) throws IllegalArgumentException
   :outertype: VmNull

setCloudletScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm setCloudletScheduler(CloudletScheduler cloudletScheduler)
   :outertype: VmNull

setCreated
^^^^^^^^^^

.. java:method:: @Override public void setCreated(boolean created)
   :outertype: VmNull

setDescription
^^^^^^^^^^^^^^

.. java:method:: @Override public Vm setDescription(String description)
   :outertype: VmNull

setFailed
^^^^^^^^^

.. java:method:: @Override public void setFailed(boolean failed)
   :outertype: VmNull

setHorizontalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm setHorizontalScaling(HorizontalVmScaling scaling) throws IllegalArgumentException
   :outertype: VmNull

setHost
^^^^^^^

.. java:method:: @Override public void setHost(Host host)
   :outertype: VmNull

setId
^^^^^

.. java:method:: @Override public void setId(long id)
   :outertype: VmNull

setInMigration
^^^^^^^^^^^^^^

.. java:method:: @Override public void setInMigration(boolean migrating)
   :outertype: VmNull

setPeVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm setPeVerticalScaling(VerticalVmScaling scaling) throws IllegalArgumentException
   :outertype: VmNull

setRam
^^^^^^

.. java:method:: @Override public Vm setRam(long ramCapacity)
   :outertype: VmNull

setRamVerticalScaling
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm setRamVerticalScaling(VerticalVmScaling scaling) throws IllegalArgumentException
   :outertype: VmNull

setSize
^^^^^^^

.. java:method:: @Override public Vm setSize(long size)
   :outertype: VmNull

setStartTime
^^^^^^^^^^^^

.. java:method:: @Override public Vm setStartTime(double startTime)
   :outertype: VmNull

setStopTime
^^^^^^^^^^^

.. java:method:: @Override public Vm setStopTime(double stopTime)
   :outertype: VmNull

setSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setSubmissionDelay(double submissionDelay)
   :outertype: VmNull

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: VmNull

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime, List<Double> mipsShare)
   :outertype: VmNull

