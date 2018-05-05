.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecution

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet.network PacketScheduler

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Set

CloudletSchedulerNull
=====================

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet
   :noindex:

.. java:type:: final class CloudletSchedulerNull implements CloudletScheduler

   A class that implements the Null Object Design Pattern for \ :java:ref:`CloudletScheduler`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`CloudletScheduler.NULL`

Methods
-------
addCloudletToReturnedList
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addCloudletToReturnedList(Cloudlet cloudlet)
   :outertype: CloudletSchedulerNull

canAddCloudletToExecutionList
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean canAddCloudletToExecutionList(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerNull

cloudletCancel
^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet cloudletCancel(int cloudletId)
   :outertype: CloudletSchedulerNull

cloudletFinish
^^^^^^^^^^^^^^

.. java:method:: @Override public void cloudletFinish(CloudletExecution ce)
   :outertype: CloudletSchedulerNull

cloudletPause
^^^^^^^^^^^^^

.. java:method:: @Override public boolean cloudletPause(int cloudletId)
   :outertype: CloudletSchedulerNull

cloudletResume
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletResume(int cloudletId)
   :outertype: CloudletSchedulerNull

cloudletSubmit
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletSubmit(Cloudlet cl, double fileTransferTime)
   :outertype: CloudletSchedulerNull

cloudletSubmit
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletSubmit(Cloudlet cl)
   :outertype: CloudletSchedulerNull

deallocatePesFromVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesFromVm(int pesToRemove)
   :outertype: CloudletSchedulerNull

getAllocatedMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAllocatedMipsForCloudlet(CloudletExecution ce, double time)
   :outertype: CloudletSchedulerNull

getCloudletExecList
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<CloudletExecution> getCloudletExecList()
   :outertype: CloudletSchedulerNull

getCloudletFinishedList
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<CloudletExecution> getCloudletFinishedList()
   :outertype: CloudletSchedulerNull

getCloudletList
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Cloudlet> getCloudletList()
   :outertype: CloudletSchedulerNull

getCloudletReturnedList
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Cloudlet> getCloudletReturnedList()
   :outertype: CloudletSchedulerNull

getCloudletStatus
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getCloudletStatus(int cloudletId)
   :outertype: CloudletSchedulerNull

getCloudletToMigrate
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet getCloudletToMigrate()
   :outertype: CloudletSchedulerNull

getCloudletWaitingList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<CloudletExecution> getCloudletWaitingList()
   :outertype: CloudletSchedulerNull

getCurrentMipsShare
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getCurrentMipsShare()
   :outertype: CloudletSchedulerNull

getCurrentRequestedBwPercentUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCurrentRequestedBwPercentUtilization()
   :outertype: CloudletSchedulerNull

getCurrentRequestedRamPercentUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCurrentRequestedRamPercentUtilization()
   :outertype: CloudletSchedulerNull

getFreePes
^^^^^^^^^^

.. java:method:: @Override public long getFreePes()
   :outertype: CloudletSchedulerNull

getPacketScheduler
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public PacketScheduler getPacketScheduler()
   :outertype: CloudletSchedulerNull

getPreviousTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousTime()
   :outertype: CloudletSchedulerNull

getRequestedCpuPercentUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getRequestedCpuPercentUtilization(double time)
   :outertype: CloudletSchedulerNull

getRequestedMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getRequestedMipsForCloudlet(CloudletExecution ce, double time)
   :outertype: CloudletSchedulerNull

getUsedPes
^^^^^^^^^^

.. java:method:: @Override public long getUsedPes()
   :outertype: CloudletSchedulerNull

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: CloudletSchedulerNull

hasFinishedCloudlets
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean hasFinishedCloudlets()
   :outertype: CloudletSchedulerNull

isCloudletReturned
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isCloudletReturned(Cloudlet cloudlet)
   :outertype: CloudletSchedulerNull

isEmpty
^^^^^^^

.. java:method:: @Override public boolean isEmpty()
   :outertype: CloudletSchedulerNull

isTherePacketScheduler
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isTherePacketScheduler()
   :outertype: CloudletSchedulerNull

runningCloudletsNumber
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int runningCloudletsNumber()
   :outertype: CloudletSchedulerNull

setPacketScheduler
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setPacketScheduler(PacketScheduler packetScheduler)
   :outertype: CloudletSchedulerNull

setVm
^^^^^

.. java:method:: @Override public void setVm(Vm vm)
   :outertype: CloudletSchedulerNull

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime, List<Double> mipsShare)
   :outertype: CloudletSchedulerNull

