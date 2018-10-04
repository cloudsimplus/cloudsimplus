.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners CloudletVmEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: java.util Collections

.. java:import:: java.util List

CloudletNull
============

.. java:package:: org.cloudbus.cloudsim.cloudlets
   :noindex:

.. java:type:: final class CloudletNull implements Cloudlet

   A class that implements the Null Object Design Pattern for \ :java:ref:`Cloudlet`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Cloudlet.NULL`

Methods
-------
addFinishedLengthSoFar
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addFinishedLengthSoFar(long partialFinishedMI)
   :outertype: CloudletNull

addOnFinishListener
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet addOnFinishListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletNull

addOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet addOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletNull

addRequiredFile
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addRequiredFile(String fileName)
   :outertype: CloudletNull

addRequiredFiles
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addRequiredFiles(List<String> fileNames)
   :outertype: CloudletNull

assignToDatacenter
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void assignToDatacenter(Datacenter datacenter)
   :outertype: CloudletNull

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(Cloudlet cloudlet)
   :outertype: CloudletNull

deleteRequiredFile
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deleteRequiredFile(String filename)
   :outertype: CloudletNull

getAccumulatedBwCost
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAccumulatedBwCost()
   :outertype: CloudletNull

getActualCpuTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getActualCpuTime(Datacenter datacenter)
   :outertype: CloudletNull

getActualCpuTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getActualCpuTime()
   :outertype: CloudletNull

getArrivalTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double getArrivalTime(Datacenter datacenter)
   :outertype: CloudletNull

getBroker
^^^^^^^^^

.. java:method:: @Override public DatacenterBroker getBroker()
   :outertype: CloudletNull

getCostPerBw
^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerBw()
   :outertype: CloudletNull

getCostPerSec
^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerSec()
   :outertype: CloudletNull

getCostPerSec
^^^^^^^^^^^^^

.. java:method:: @Override public double getCostPerSec(Datacenter datacenter)
   :outertype: CloudletNull

getExecStartTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getExecStartTime()
   :outertype: CloudletNull

getFileSize
^^^^^^^^^^^

.. java:method:: @Override public long getFileSize()
   :outertype: CloudletNull

getFinishTime
^^^^^^^^^^^^^

.. java:method:: @Override public double getFinishTime()
   :outertype: CloudletNull

getFinishedLengthSoFar
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getFinishedLengthSoFar()
   :outertype: CloudletNull

getFinishedLengthSoFar
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getFinishedLengthSoFar(Datacenter datacenter)
   :outertype: CloudletNull

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: CloudletNull

getJobId
^^^^^^^^

.. java:method:: @Override public long getJobId()
   :outertype: CloudletNull

getLastDatacenter
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter getLastDatacenter()
   :outertype: CloudletNull

getLastDatacenterArrivalTime
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getLastDatacenterArrivalTime()
   :outertype: CloudletNull

getLength
^^^^^^^^^

.. java:method:: @Override public long getLength()
   :outertype: CloudletNull

getNetServiceLevel
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNetServiceLevel()
   :outertype: CloudletNull

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfPes()
   :outertype: CloudletNull

getOutputSize
^^^^^^^^^^^^^

.. java:method:: @Override public long getOutputSize()
   :outertype: CloudletNull

getPriority
^^^^^^^^^^^

.. java:method:: @Override public int getPriority()
   :outertype: CloudletNull

getRequiredFiles
^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<String> getRequiredFiles()
   :outertype: CloudletNull

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: CloudletNull

getStatus
^^^^^^^^^

.. java:method:: @Override public Status getStatus()
   :outertype: CloudletNull

getSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getSubmissionDelay()
   :outertype: CloudletNull

getTotalCost
^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCost()
   :outertype: CloudletNull

getTotalLength
^^^^^^^^^^^^^^

.. java:method:: @Override public long getTotalLength()
   :outertype: CloudletNull

getUid
^^^^^^

.. java:method:: @Override public String getUid()
   :outertype: CloudletNull

getUtilizationModelBw
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationModel getUtilizationModelBw()
   :outertype: CloudletNull

getUtilizationModelCpu
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationModel getUtilizationModelCpu()
   :outertype: CloudletNull

getUtilizationModelRam
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public UtilizationModel getUtilizationModelRam()
   :outertype: CloudletNull

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfBw()
   :outertype: CloudletNull

getUtilizationOfBw
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfBw(double time)
   :outertype: CloudletNull

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpu()
   :outertype: CloudletNull

getUtilizationOfCpu
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfCpu(double time)
   :outertype: CloudletNull

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfRam()
   :outertype: CloudletNull

getUtilizationOfRam
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationOfRam(double time)
   :outertype: CloudletNull

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: CloudletNull

getWaitingTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double getWaitingTime()
   :outertype: CloudletNull

getWallClockTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getWallClockTime(Datacenter datacenter)
   :outertype: CloudletNull

getWallClockTimeInLastExecutedDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getWallClockTimeInLastExecutedDatacenter()
   :outertype: CloudletNull

isAssignedToDatacenter
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isAssignedToDatacenter()
   :outertype: CloudletNull

isBindToVm
^^^^^^^^^^

.. java:method:: @Override public boolean isBindToVm()
   :outertype: CloudletNull

isFinished
^^^^^^^^^^

.. java:method:: @Override public boolean isFinished()
   :outertype: CloudletNull

notifyOnUpdateProcessingListeners
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void notifyOnUpdateProcessingListeners(double time)
   :outertype: CloudletNull

registerArrivalInDatacenter
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double registerArrivalInDatacenter()
   :outertype: CloudletNull

removeOnFinishListener
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnFinishListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletNull

removeOnUpdateProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener)
   :outertype: CloudletNull

requiresFiles
^^^^^^^^^^^^^

.. java:method:: @Override public boolean requiresFiles()
   :outertype: CloudletNull

setBroker
^^^^^^^^^

.. java:method:: @Override public Cloudlet setBroker(DatacenterBroker broker)
   :outertype: CloudletNull

setExecStartTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setExecStartTime(double clockTime)
   :outertype: CloudletNull

setFileSize
^^^^^^^^^^^

.. java:method:: @Override public Cloudlet setFileSize(long fileSize)
   :outertype: CloudletNull

setId
^^^^^

.. java:method:: @Override public void setId(long id)
   :outertype: CloudletNull

setJobId
^^^^^^^^

.. java:method:: @Override public void setJobId(long jobId)
   :outertype: CloudletNull

setLength
^^^^^^^^^

.. java:method:: @Override public Cloudlet setLength(long length)
   :outertype: CloudletNull

setNetServiceLevel
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean setNetServiceLevel(int netServiceLevel)
   :outertype: CloudletNull

setNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet setNumberOfPes(long numberOfPes)
   :outertype: CloudletNull

setOutputSize
^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet setOutputSize(long outputSize)
   :outertype: CloudletNull

setPriority
^^^^^^^^^^^

.. java:method:: @Override public void setPriority(int priority)
   :outertype: CloudletNull

setStatus
^^^^^^^^^

.. java:method:: @Override public boolean setStatus(Status newStatus)
   :outertype: CloudletNull

setSubmissionDelay
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setSubmissionDelay(double submissionDelay)
   :outertype: CloudletNull

setUtilizationModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet setUtilizationModel(UtilizationModel utilizationModel)
   :outertype: CloudletNull

setUtilizationModelBw
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet setUtilizationModelBw(UtilizationModel utilizationModelBw)
   :outertype: CloudletNull

setUtilizationModelCpu
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet setUtilizationModelCpu(UtilizationModel utilizationModelCpu)
   :outertype: CloudletNull

setUtilizationModelRam
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet setUtilizationModelRam(UtilizationModel utilizationModelRam)
   :outertype: CloudletNull

setVm
^^^^^

.. java:method:: @Override public Cloudlet setVm(Vm vm)
   :outertype: CloudletNull

setWallClockTime
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean setWallClockTime(double wallTime, double actualCpuTime)
   :outertype: CloudletNull

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: CloudletNull

