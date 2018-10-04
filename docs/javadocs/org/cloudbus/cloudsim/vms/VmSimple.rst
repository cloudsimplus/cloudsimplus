.. java:import:: org.apache.commons.lang3 StringUtils

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core UniquelyIdentifiable

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudsimplus.autoscaling HorizontalVmScaling

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

.. java:import:: org.cloudsimplus.autoscaling VmScaling

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners VmDatacenterEventInfo

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: java.util.stream LongStream

VmSimple
========

.. java:package:: org.cloudbus.cloudsim.vms
   :noindex:

.. java:type:: public class VmSimple implements Vm

   Implements the basic features of a Virtual Machine (VM) that runs inside a \ :java:ref:`Host`\  that may be shared among other VMs. It processes \ :java:ref:`cloudlets <Cloudlet>`\ . This processing happens according to a policy, defined by the \ :java:ref:`CloudletScheduler`\ . Each VM has a owner (user), which can submit cloudlets to the VM to execute them.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

Constructors
------------
VmSimple
^^^^^^^^

.. java:constructor:: public VmSimple(int id, long mipsCapacity, long numberOfPes)
   :outertype: VmSimple

   Creates a Vm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size. To change these values, use the respective setters. While the Vm \ :java:ref:`is being instantiated <isCreated()>`\ , such values can be changed freely.

   :param id: unique ID of the VM
   :param mipsCapacity: the mips capacity of each Vm \ :java:ref:`Pe`\
   :param numberOfPes: amount of \ :java:ref:`Pe`\  (CPU cores)

VmSimple
^^^^^^^^

.. java:constructor:: public VmSimple(long mipsCapacity, long numberOfPes)
   :outertype: VmSimple

   Creates a Vm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size. To change these values, use the respective setters. While the Vm \ :java:ref:`is being instantiated <isCreated()>`\ , such values can be changed freely.

   It is not defined an id for the Vm. The id is defined when the Vm is submitted to a \ :java:ref:`DatacenterBroker`\ .

   :param mipsCapacity: the mips capacity of each Vm \ :java:ref:`Pe`\
   :param numberOfPes: amount of \ :java:ref:`Pe`\  (CPU cores)

VmSimple
^^^^^^^^

.. java:constructor:: public VmSimple(int id, double mipsCapacity, long numberOfPes)
   :outertype: VmSimple

   Creates a Vm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size. To change these values, use the respective setters. While the Vm \ :java:ref:`is being instantiated <isCreated()>`\ , such values can be changed freely.

   It receives the amount of MIPS as a double value but converts it internally to a long. The method is just provided as a handy-way to create a Vm using a double value for MIPS that usually is generated from some computations.

   :param id: unique ID of the VM
   :param mipsCapacity: the mips capacity of each Vm \ :java:ref:`Pe`\
   :param numberOfPes: amount of \ :java:ref:`Pe`\  (CPU cores)

Methods
-------
addOnCreationFailureListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm addOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener)
   :outertype: VmSimple

addOnHostAllocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm addOnHostAllocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmSimple

addOnHostDeallocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm addOnHostDeallocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmSimple

addOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm addOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmSimple

addStateHistoryEntry
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addStateHistoryEntry(VmStateHistoryEntry entry)
   :outertype: VmSimple

allocateResource
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void allocateResource(Class<? extends ResourceManageable> resourceClass, long newTotalResourceAmount)
   :outertype: VmSimple

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(Vm o)
   :outertype: VmSimple

   Compare this Vm with another one based on \ :java:ref:`getTotalMipsCapacity()`\ .

   :param o: the Vm to compare to
   :return: {@inheritDoc}

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateResource(Class<? extends ResourceManageable> resourceClass)
   :outertype: VmSimple

equals
^^^^^^

.. java:method:: @Override public boolean equals(Object o)
   :outertype: VmSimple

getBroker
^^^^^^^^^

.. java:method:: @Override public DatacenterBroker getBroker()
   :outertype: VmSimple

getBw
^^^^^

.. java:method:: @Override public Resource getBw()
   :outertype: VmSimple

getBwVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling getBwVerticalScaling()
   :outertype: VmSimple

getCloudletScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public CloudletScheduler getCloudletScheduler()
   :outertype: VmSimple

getCpuPercentUsage
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCpuPercentUsage()
   :outertype: VmSimple

getCpuPercentUsage
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCpuPercentUsage(double time)
   :outertype: VmSimple

getCurrentRequestedBw
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getCurrentRequestedBw()
   :outertype: VmSimple

getCurrentRequestedMaxMips
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCurrentRequestedMaxMips()
   :outertype: VmSimple

getCurrentRequestedMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getCurrentRequestedMips()
   :outertype: VmSimple

getCurrentRequestedRam
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getCurrentRequestedRam()
   :outertype: VmSimple

getCurrentRequestedTotalMips
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCurrentRequestedTotalMips()
   :outertype: VmSimple

getDescription
^^^^^^^^^^^^^^

.. java:method:: @Override public String getDescription()
   :outertype: VmSimple

getHorizontalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public HorizontalVmScaling getHorizontalScaling()
   :outertype: VmSimple

getHost
^^^^^^^

.. java:method:: @Override public Host getHost()
   :outertype: VmSimple

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: VmSimple

getIdleInterval
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getIdleInterval()
   :outertype: VmSimple

getLastBusyTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getLastBusyTime()
   :outertype: VmSimple

getMips
^^^^^^^

.. java:method:: @Override public double getMips()
   :outertype: VmSimple

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfPes()
   :outertype: VmSimple

getPeVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling getPeVerticalScaling()
   :outertype: VmSimple

getProcessor
^^^^^^^^^^^^

.. java:method:: @Override public Processor getProcessor()
   :outertype: VmSimple

getRam
^^^^^^

.. java:method:: @Override public Resource getRam()
   :outertype: VmSimple

getRamVerticalScaling
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public VerticalVmScaling getRamVerticalScaling()
   :outertype: VmSimple

getResources
^^^^^^^^^^^^

.. java:method:: @Override public List<ResourceManageable> getResources()
   :outertype: VmSimple

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: VmSimple

getStartTime
^^^^^^^^^^^^

.. java:method:: @Override public double getStartTime()
   :outertype: VmSimple

getStateHistory
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<VmStateHistoryEntry> getStateHistory()
   :outertype: VmSimple

getStopTime
^^^^^^^^^^^

.. java:method:: @Override public double getStopTime()
   :outertype: VmSimple

getStorage
^^^^^^^^^^

.. java:method:: @Override public Resource getStorage()
   :outertype: VmSimple

getSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSubmissionDelay()
   :outertype: VmSimple

getTotalCpuMipsUsage
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCpuMipsUsage()
   :outertype: VmSimple

getTotalCpuMipsUsage
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCpuMipsUsage(double time)
   :outertype: VmSimple

getTotalExecutionTime
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalExecutionTime()
   :outertype: VmSimple

getTotalMipsCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalMipsCapacity()
   :outertype: VmSimple

getUid
^^^^^^

.. java:method:: @Override public String getUid()
   :outertype: VmSimple

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationHistory getUtilizationHistory()
   :outertype: VmSimple

getVmm
^^^^^^

.. java:method:: @Override public String getVmm()
   :outertype: VmSimple

hashCode
^^^^^^^^

.. java:method:: @Override public int hashCode()
   :outertype: VmSimple

isCreated
^^^^^^^^^

.. java:method:: @Override public final boolean isCreated()
   :outertype: VmSimple

isFailed
^^^^^^^^

.. java:method:: @Override public boolean isFailed()
   :outertype: VmSimple

isIdle
^^^^^^

.. java:method:: @Override public boolean isIdle()
   :outertype: VmSimple

isIdleEnough
^^^^^^^^^^^^

.. java:method:: @Override public boolean isIdleEnough(double time)
   :outertype: VmSimple

isInMigration
^^^^^^^^^^^^^

.. java:method:: @Override public boolean isInMigration()
   :outertype: VmSimple

isSuitableForCloudlet
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isSuitableForCloudlet(Cloudlet cloudlet)
   :outertype: VmSimple

isWorking
^^^^^^^^^

.. java:method:: @Override public boolean isWorking()
   :outertype: VmSimple

notifyOnCreationFailureListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void notifyOnCreationFailureListeners(Datacenter failedDatacenter)
   :outertype: VmSimple

notifyOnHostAllocationListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void notifyOnHostAllocationListeners()
   :outertype: VmSimple

notifyOnHostDeallocationListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void notifyOnHostDeallocationListeners(Host deallocatedHost)
   :outertype: VmSimple

notifyOnUpdateProcessingListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void notifyOnUpdateProcessingListeners()
   :outertype: VmSimple

   Notifies all registered listeners when the processing of the Vm is updated in its \ :java:ref:`Host`\ .

removeOnCreationFailureListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener)
   :outertype: VmSimple

removeOnHostAllocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnHostAllocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmSimple

removeOnHostDeallocationListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnHostDeallocationListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmSimple

removeOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener)
   :outertype: VmSimple

setBroker
^^^^^^^^^

.. java:method:: @Override public final Vm setBroker(DatacenterBroker broker)
   :outertype: VmSimple

setBw
^^^^^

.. java:method:: @Override public final Vm setBw(long bwCapacity)
   :outertype: VmSimple

setBwVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Vm setBwVerticalScaling(VerticalVmScaling bwVerticalScaling) throws IllegalArgumentException
   :outertype: VmSimple

setCloudletScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Vm setCloudletScheduler(CloudletScheduler cloudletScheduler)
   :outertype: VmSimple

setCreated
^^^^^^^^^^

.. java:method:: @Override public final void setCreated(boolean created)
   :outertype: VmSimple

setDescription
^^^^^^^^^^^^^^

.. java:method:: @Override public Vm setDescription(String description)
   :outertype: VmSimple

setFailed
^^^^^^^^^

.. java:method:: @Override public void setFailed(boolean failed)
   :outertype: VmSimple

setHorizontalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Vm setHorizontalScaling(HorizontalVmScaling horizontalScaling) throws IllegalArgumentException
   :outertype: VmSimple

setHost
^^^^^^^

.. java:method:: @Override public final void setHost(Host host)
   :outertype: VmSimple

setId
^^^^^

.. java:method:: @Override public final void setId(long id)
   :outertype: VmSimple

   Sets the VM id.

   :param id: the new VM id, that has to be unique for the current \ :java:ref:`broker <getBroker()>`\

setInMigration
^^^^^^^^^^^^^^

.. java:method:: @Override public final void setInMigration(boolean migrating)
   :outertype: VmSimple

setMips
^^^^^^^

.. java:method:: protected final void setMips(double mips)
   :outertype: VmSimple

   Sets the individual MIPS capacity of any VM's PE, considering that all PEs have the same capacity.

   :param mips: the new mips for every VM's PE

setPeVerticalScaling
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Vm setPeVerticalScaling(VerticalVmScaling peVerticalScaling) throws IllegalArgumentException
   :outertype: VmSimple

setRam
^^^^^^

.. java:method:: @Override public final Vm setRam(long ramCapacity)
   :outertype: VmSimple

setRamVerticalScaling
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Vm setRamVerticalScaling(VerticalVmScaling ramVerticalScaling) throws IllegalArgumentException
   :outertype: VmSimple

setSize
^^^^^^^

.. java:method:: @Override public final Vm setSize(long size)
   :outertype: VmSimple

setStartTime
^^^^^^^^^^^^

.. java:method:: @Override public Vm setStartTime(double startTime)
   :outertype: VmSimple

setStopTime
^^^^^^^^^^^

.. java:method:: @Override public Vm setStopTime(double stopTime)
   :outertype: VmSimple

setSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setSubmissionDelay(double submissionDelay)
   :outertype: VmSimple

setVmm
^^^^^^

.. java:method:: protected final void setVmm(String vmm)
   :outertype: VmSimple

   Sets the Virtual Machine Monitor (VMM) that manages the VM.

   :param vmm: the new VMM

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: VmSimple

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime, List<Double> mipsShare)
   :outertype: VmSimple

