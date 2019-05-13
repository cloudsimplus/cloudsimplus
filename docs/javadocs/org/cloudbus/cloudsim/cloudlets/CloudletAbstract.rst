.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core CustomerEntityAbstract

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelFull

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners CloudletVmEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

CloudletAbstract
================

.. java:package:: org.cloudbus.cloudsim.cloudlets
   :noindex:

.. java:type:: public abstract class CloudletAbstract extends CustomerEntityAbstract implements Cloudlet

   A base class for \ :java:ref:`Cloudlet`\  implementations.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
CloudletAbstract
^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletAbstract(long length, int pesNumber, UtilizationModel utilizationModel)
   :outertype: CloudletAbstract

   Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to a \ :java:ref:`DatacenterBroker`\ . The file size and output size is defined as 1.

   :param length: the length or size (in MI) of this cloudlet to be executed in a VM (check out \ :java:ref:`setLength(long)`\ )
   :param pesNumber: number of PEs that Cloudlet will require
   :param utilizationModel: a \ :java:ref:`UtilizationModel`\  to define how the Cloudlet uses CPU, RAM and BW. To define an independent utilization model for each resource, call the respective setters.

   **See also:** :java:ref:`.setUtilizationModelCpu(UtilizationModel)`, :java:ref:`.setUtilizationModelRam(UtilizationModel)`, :java:ref:`.setUtilizationModelBw(UtilizationModel)`

CloudletAbstract
^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletAbstract(long length, int pesNumber)
   :outertype: CloudletAbstract

   Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to a \ :java:ref:`DatacenterBroker`\ . The file size and output size is defined as 1.

   \ **NOTE:**\  By default, the Cloudlet will use a \ :java:ref:`UtilizationModelFull`\  to define CPU utilization and a \ :java:ref:`UtilizationModel.NULL`\  for RAM and BW. To change the default values, use the respective setters.

   :param length: the length or size (in MI) of this cloudlet to be executed in a VM (check out \ :java:ref:`setLength(long)`\ )
   :param pesNumber: number of PEs that Cloudlet will require

CloudletAbstract
^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletAbstract(long length, long pesNumber)
   :outertype: CloudletAbstract

   Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to a \ :java:ref:`DatacenterBroker`\ . The file size and output size is defined as 1.

   \ **NOTE:**\  By default, the Cloudlet will use a \ :java:ref:`UtilizationModelFull`\  to define CPU utilization and a \ :java:ref:`UtilizationModel.NULL`\  for RAM and BW. To change the default values, use the respective setters.

   :param length: the length or size (in MI) of this cloudlet to be executed in a VM (check out \ :java:ref:`setLength(long)`\ )
   :param pesNumber: number of PEs that Cloudlet will require

CloudletAbstract
^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletAbstract(long id, long length, long pesNumber)
   :outertype: CloudletAbstract

   Creates a Cloudlet with no priority, file size and output size equal to 1.

   \ **NOTE:**\  By default, the Cloudlet will use a \ :java:ref:`UtilizationModelFull`\  to define CPU utilization and a \ :java:ref:`UtilizationModel.NULL`\  for RAM and BW. To change the default values, use the respective setters.

   :param id: id of the Cloudlet
   :param length: the length or size (in MI) of this cloudlet to be executed in a VM (check out \ :java:ref:`setLength(long)`\ )
   :param pesNumber: number of PEs that Cloudlet will require

Methods
-------
absLength
^^^^^^^^^

.. java:method:: protected long absLength()
   :outertype: CloudletAbstract

   Gets the absolute value of the length (without the signal). Check out \ :java:ref:`getLength()`\  for details.

addFinishedLengthSoFar
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addFinishedLengthSoFar(long partialFinishedMI)
   :outertype: CloudletAbstract

addOnFinishListener
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet addOnFinishListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletAbstract

addOnStartListener
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet addOnStartListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletAbstract

addOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet addOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletAbstract

addRequiredFile
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addRequiredFile(String fileName)
   :outertype: CloudletAbstract

addRequiredFiles
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addRequiredFiles(List<String> fileNames)
   :outertype: CloudletAbstract

assignToDatacenter
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void assignToDatacenter(Datacenter datacenter)
   :outertype: CloudletAbstract

deleteRequiredFile
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deleteRequiredFile(String filename)
   :outertype: CloudletAbstract

equals
^^^^^^

.. java:method:: @Override public boolean equals(Object other)
   :outertype: CloudletAbstract

getAccumulatedBwCost
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAccumulatedBwCost()
   :outertype: CloudletAbstract

getActualCpuTime
^^^^^^^^^^^^^^^^

.. java:method:: protected double getActualCpuTime(Datacenter datacenter)
   :outertype: CloudletAbstract

   Gets the total execution time of this Cloudlet in a given Datacenter ID.

   :param datacenter: the Datacenter entity
   :return: the total execution time of this Cloudlet in the given Datacenter or 0 if the Cloudlet was not executed there

getActualCpuTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getActualCpuTime()
   :outertype: CloudletAbstract

getArrivalTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double getArrivalTime(Datacenter datacenter)
   :outertype: CloudletAbstract

getCostPerBw
^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerBw()
   :outertype: CloudletAbstract

getCostPerSec
^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerSec()
   :outertype: CloudletAbstract

getCostPerSec
^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerSec(Datacenter datacenter)
   :outertype: CloudletAbstract

getExecStartTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getExecStartTime()
   :outertype: CloudletAbstract

getFileSize
^^^^^^^^^^^

.. java:method:: @Override public long getFileSize()
   :outertype: CloudletAbstract

getFinishTime
^^^^^^^^^^^^^

.. java:method:: @Override public double getFinishTime()
   :outertype: CloudletAbstract

getFinishedLengthSoFar
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getFinishedLengthSoFar(Datacenter datacenter)
   :outertype: CloudletAbstract

getFinishedLengthSoFar
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getFinishedLengthSoFar()
   :outertype: CloudletAbstract

getJobId
^^^^^^^^

.. java:method:: @Override public long getJobId()
   :outertype: CloudletAbstract

getLastDatacenterArrivalTime
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getLastDatacenterArrivalTime()
   :outertype: CloudletAbstract

getLastExecutedDatacenterIdx
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected int getLastExecutedDatacenterIdx()
   :outertype: CloudletAbstract

getLength
^^^^^^^^^

.. java:method:: @Override public long getLength()
   :outertype: CloudletAbstract

getNetServiceLevel
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNetServiceLevel()
   :outertype: CloudletAbstract

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfPes()
   :outertype: CloudletAbstract

getOutputSize
^^^^^^^^^^^^^

.. java:method:: @Override public long getOutputSize()
   :outertype: CloudletAbstract

getPriority
^^^^^^^^^^^

.. java:method:: @Override public int getPriority()
   :outertype: CloudletAbstract

getRequiredFiles
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<String> getRequiredFiles()
   :outertype: CloudletAbstract

getStatus
^^^^^^^^^

.. java:method:: @Override public Status getStatus()
   :outertype: CloudletAbstract

getSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSubmissionDelay()
   :outertype: CloudletAbstract

getTotalCost
^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCost()
   :outertype: CloudletAbstract

getTotalLength
^^^^^^^^^^^^^^

.. java:method:: @Override public long getTotalLength()
   :outertype: CloudletAbstract

getUtilizationModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationModel getUtilizationModel(Class<? extends ResourceManageable> resourceClass)
   :outertype: CloudletAbstract

getUtilizationModelBw
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationModel getUtilizationModelBw()
   :outertype: CloudletAbstract

getUtilizationModelCpu
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationModel getUtilizationModelCpu()
   :outertype: CloudletAbstract

getUtilizationModelRam
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationModel getUtilizationModelRam()
   :outertype: CloudletAbstract

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfBw()
   :outertype: CloudletAbstract

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfBw(double time)
   :outertype: CloudletAbstract

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpu()
   :outertype: CloudletAbstract

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpu(double time)
   :outertype: CloudletAbstract

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfRam()
   :outertype: CloudletAbstract

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfRam(double time)
   :outertype: CloudletAbstract

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: CloudletAbstract

getWaitingTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double getWaitingTime()
   :outertype: CloudletAbstract

getWallClockTime
^^^^^^^^^^^^^^^^

.. java:method:: protected double getWallClockTime(Datacenter datacenter)
   :outertype: CloudletAbstract

   Gets the time of this Cloudlet resides in a given Datacenter (from arrival time until departure time).

   :param datacenter: a Datacenter entity
   :return: the wall-clock time or 0 if the Cloudlet has never been executed there

   **See also:** \ `Elapsed real time (wall-clock time) <https://en.wikipedia.org/wiki/Elapsed_real_time>`_\

isBoundToCreatedVm
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isBoundToCreatedVm()
   :outertype: CloudletAbstract

isBoundToVm
^^^^^^^^^^^

.. java:method:: @Override public boolean isBoundToVm()
   :outertype: CloudletAbstract

isFinished
^^^^^^^^^^

.. java:method:: @Override public boolean isFinished()
   :outertype: CloudletAbstract

isReturnedToBroker
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isReturnedToBroker()
   :outertype: CloudletAbstract

notifyOnUpdateProcessingListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void notifyOnUpdateProcessingListeners(double time)
   :outertype: CloudletAbstract

registerArrivalInDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double registerArrivalInDatacenter()
   :outertype: CloudletAbstract

removeOnFinishListener
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnFinishListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletAbstract

removeOnStartListener
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnStartListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletAbstract

removeOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletAbstract

requiresFiles
^^^^^^^^^^^^^

.. java:method:: @Override public boolean requiresFiles()
   :outertype: CloudletAbstract

setAccumulatedBwCost
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setAccumulatedBwCost(double accumulatedBwCost)
   :outertype: CloudletAbstract

   Sets the \ :java:ref:`accumulated bw cost <getAccumulatedBwCost()>`\ .

   :param accumulatedBwCost: the accumulated bw cost to set

setCostPerBw
^^^^^^^^^^^^

.. java:method:: protected final void setCostPerBw(double costPerBw)
   :outertype: CloudletAbstract

   Sets \ :java:ref:`the cost of each byte of bandwidth (bw) <getCostPerBw()>`\  consumed.

   :param costPerBw: the new cost per bw to set

setExecStartTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setExecStartTime(double clockTime)
   :outertype: CloudletAbstract

setFileSize
^^^^^^^^^^^

.. java:method:: @Override public final Cloudlet setFileSize(long fileSize)
   :outertype: CloudletAbstract

setFinishTime
^^^^^^^^^^^^^

.. java:method:: protected final void setFinishTime(double finishTime)
   :outertype: CloudletAbstract

   Sets the \ :java:ref:`finish time <getFinishTime()>`\  of this cloudlet in the latest Datacenter.

   :param finishTime: the finish time

setJobId
^^^^^^^^

.. java:method:: @Override public final void setJobId(long jobId)
   :outertype: CloudletAbstract

setLastExecutedDatacenterIdx
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void setLastExecutedDatacenterIdx(int lastExecutedDatacenterIdx)
   :outertype: CloudletAbstract

setLength
^^^^^^^^^

.. java:method:: @Override public final Cloudlet setLength(long length)
   :outertype: CloudletAbstract

setNetServiceLevel
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean setNetServiceLevel(int netServiceLevel)
   :outertype: CloudletAbstract

setNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public final Cloudlet setNumberOfPes(long numberOfPes)
   :outertype: CloudletAbstract

setOutputSize
^^^^^^^^^^^^^

.. java:method:: @Override public final Cloudlet setOutputSize(long outputSize)
   :outertype: CloudletAbstract

setPriority
^^^^^^^^^^^

.. java:method:: @Override public void setPriority(int priority)
   :outertype: CloudletAbstract

setRequiredFiles
^^^^^^^^^^^^^^^^

.. java:method:: public final void setRequiredFiles(List<String> requiredFiles)
   :outertype: CloudletAbstract

   Sets the list of \ :java:ref:`required files <getRequiredFiles()>`\ .

   :param requiredFiles: the new list of required files

setSizes
^^^^^^^^

.. java:method:: @Override public Cloudlet setSizes(long size)
   :outertype: CloudletAbstract

setStatus
^^^^^^^^^

.. java:method:: @Override public boolean setStatus(Status newStatus)
   :outertype: CloudletAbstract

setSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final void setSubmissionDelay(double submissionDelay)
   :outertype: CloudletAbstract

setUtilizationModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet setUtilizationModel(UtilizationModel utilizationModel)
   :outertype: CloudletAbstract

setUtilizationModelBw
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Cloudlet setUtilizationModelBw(UtilizationModel utilizationModelBw)
   :outertype: CloudletAbstract

setUtilizationModelCpu
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Cloudlet setUtilizationModelCpu(UtilizationModel utilizationModelCpu)
   :outertype: CloudletAbstract

setUtilizationModelRam
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Cloudlet setUtilizationModelRam(UtilizationModel utilizationModelRam)
   :outertype: CloudletAbstract

setVm
^^^^^

.. java:method:: @Override public final Cloudlet setVm(Vm vm)
   :outertype: CloudletAbstract

setWallClockTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean setWallClockTime(double wallTime, double actualCpuTime)
   :outertype: CloudletAbstract

