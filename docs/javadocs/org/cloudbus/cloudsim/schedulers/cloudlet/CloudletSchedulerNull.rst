.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecution

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet.network CloudletTaskScheduler

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

cloudletCancel
^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet cloudletCancel(Cloudlet cloudlet)
   :outertype: CloudletSchedulerNull

cloudletFail
^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet cloudletFail(Cloudlet cloudlet)
   :outertype: CloudletSchedulerNull

cloudletFinish
^^^^^^^^^^^^^^

.. java:method:: @Override public void cloudletFinish(CloudletExecution cle)
   :outertype: CloudletSchedulerNull

cloudletPause
^^^^^^^^^^^^^

.. java:method:: @Override public boolean cloudletPause(Cloudlet cloudlet)
   :outertype: CloudletSchedulerNull

cloudletReady
^^^^^^^^^^^^^

.. java:method:: @Override public boolean cloudletReady(Cloudlet cloudlet)
   :outertype: CloudletSchedulerNull

cloudletResume
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletResume(Cloudlet cloudlet)
   :outertype: CloudletSchedulerNull

cloudletSubmit
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime)
   :outertype: CloudletSchedulerNull

cloudletSubmit
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletSubmit(Cloudlet cloudlet)
   :outertype: CloudletSchedulerNull

deallocatePesFromVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesFromVm(int pesToRemove)
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

.. java:method:: @Override public double getRequestedMipsForCloudlet(CloudletExecution cle, double time)
   :outertype: CloudletSchedulerNull

getTaskScheduler
^^^^^^^^^^^^^^^^

.. java:method:: @Override public CloudletTaskScheduler getTaskScheduler()
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

isThereTaskScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isThereTaskScheduler()
   :outertype: CloudletSchedulerNull

runningCloudletsNumber
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int runningCloudletsNumber()
   :outertype: CloudletSchedulerNull

setTaskScheduler
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setTaskScheduler(CloudletTaskScheduler taskScheduler)
   :outertype: CloudletSchedulerNull

setVm
^^^^^

.. java:method:: @Override public void setVm(Vm vm)
   :outertype: CloudletSchedulerNull

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime, List<Double> mipsShare)
   :outertype: CloudletSchedulerNull

