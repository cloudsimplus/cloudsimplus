.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet.Status

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecution

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core.events CloudSimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Ram

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet.network CloudletTaskScheduler

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util.function Consumer

.. java:import:: java.util.function Function

.. java:import:: java.util.stream IntStream

.. java:import:: java.util.stream Stream

CloudletSchedulerAbstract
=========================

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet
   :noindex:

.. java:type:: public abstract class CloudletSchedulerAbstract implements CloudletScheduler

   Implements the basic features of a \ :java:ref:`CloudletScheduler`\ , representing the policy of scheduling performed by a virtual machine to run its \ :java:ref:`Cloudlets <Cloudlet>`\ . So, classes extending this must execute Cloudlets. The interface for cloudlet management is also implemented in this class. Each VM has to have its own instance of a CloudletScheduler.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
CloudletSchedulerAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected CloudletSchedulerAbstract()
   :outertype: CloudletSchedulerAbstract

   Creates a new CloudletScheduler object.

Methods
-------
addCloudletToExecList
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addCloudletToExecList(CloudletExecution cle)
   :outertype: CloudletSchedulerAbstract

   Adds a Cloudlet to the list of cloudlets in execution.

   :param cle: the Cloudlet to be added

addCloudletToReturnedList
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addCloudletToReturnedList(Cloudlet cloudlet)
   :outertype: CloudletSchedulerAbstract

addCloudletToWaitingList
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addCloudletToWaitingList(CloudletExecution cle)
   :outertype: CloudletSchedulerAbstract

addWaitingCloudletToExecList
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected CloudletExecution addWaitingCloudletToExecList(CloudletExecution cle)
   :outertype: CloudletSchedulerAbstract

   Removes a Cloudlet from waiting list and adds it to the exec list.

   :param cle: the cloudlet to add to to exec list
   :return: the given cloudlet

canExecuteCloudletInternal
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract boolean canExecuteCloudletInternal(CloudletExecution cle)
   :outertype: CloudletSchedulerAbstract

   **See also:** :java:ref:`.canExecuteCloudlet(CloudletExecution)`

cloudletCancel
^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet cloudletCancel(Cloudlet cloudlet)
   :outertype: CloudletSchedulerAbstract

cloudletFail
^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet cloudletFail(Cloudlet cloudlet)
   :outertype: CloudletSchedulerAbstract

cloudletFinish
^^^^^^^^^^^^^^

.. java:method:: @Override public void cloudletFinish(CloudletExecution cle)
   :outertype: CloudletSchedulerAbstract

cloudletPause
^^^^^^^^^^^^^

.. java:method:: @Override public boolean cloudletPause(Cloudlet cloudlet)
   :outertype: CloudletSchedulerAbstract

cloudletReady
^^^^^^^^^^^^^

.. java:method:: @Override public boolean cloudletReady(Cloudlet cloudlet)
   :outertype: CloudletSchedulerAbstract

cloudletSubmit
^^^^^^^^^^^^^^

.. java:method:: @Override public final double cloudletSubmit(Cloudlet cloudlet)
   :outertype: CloudletSchedulerAbstract

cloudletSubmit
^^^^^^^^^^^^^^

.. java:method:: @Override public final double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime)
   :outertype: CloudletSchedulerAbstract

cloudletSubmitInternal
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double cloudletSubmitInternal(CloudletExecution cle, double fileTransferTime)
   :outertype: CloudletSchedulerAbstract

   Receives the execution information of a Cloudlet to be executed in the VM managed by this scheduler.

   :param cle: the submitted cloudlet
   :param fileTransferTime: time required to move the required files from the SAN to the VM
   :return: expected finish time of this cloudlet (considering the time to transfer required files from the Datacenter to the Vm), or 0 if it is in a waiting queue

   **See also:** :java:ref:`.cloudletSubmit(Cloudlet,double)`

deallocatePesFromVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocatePesFromVm(int pesToRemove)
   :outertype: CloudletSchedulerAbstract

findCloudletInAllLists
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Optional<CloudletExecution> findCloudletInAllLists(double cloudletId)
   :outertype: CloudletSchedulerAbstract

   Search for a Cloudlet into all Cloudlet lists.

   :param cloudletId: the id of the Cloudlet to search for
   :return: an \ :java:ref:`Optional`\  value that is able to indicate if the Cloudlet was found or not

findCloudletInList
^^^^^^^^^^^^^^^^^^

.. java:method:: protected Optional<CloudletExecution> findCloudletInList(Cloudlet cloudlet, List<CloudletExecution> list)
   :outertype: CloudletSchedulerAbstract

   Search for a Cloudlet into a given list.

   :param cloudlet: the Cloudlet to search for
   :param list: the list to search the Cloudlet into
   :return: an \ :java:ref:`Optional`\  value that is able to indicate if the Cloudlet was found or not

findSuitableWaitingCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Optional<CloudletExecution> findSuitableWaitingCloudlet()
   :outertype: CloudletSchedulerAbstract

   Try to find the first Cloudlet in the waiting list that the number of required PEs is not higher than the number of free PEs.

   :return: an \ :java:ref:`Optional`\  containing the found Cloudlet or an empty Optional otherwise

getAllocatedMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getAllocatedMipsForCloudlet(CloudletExecution cle, double time)
   :outertype: CloudletSchedulerAbstract

   Gets the current allocated MIPS for cloudlet.

   :param cle: the ce
   :param time: the time
   :return: the current allocated mips for cloudlet

getAvailableMipsByPe
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getAvailableMipsByPe()
   :outertype: CloudletSchedulerAbstract

   Gets the amount of MIPS available (free) for each Processor PE, considering the currently executing cloudlets in this processor and the number of PEs these cloudlets require. This is the amount of MIPS that each Cloudlet is allowed to used, considering that the processor is shared among all executing cloudlets.

   In the case of space shared schedulers, there is no concurrency for PEs because some cloudlets may wait in a queue until there is available PEs to be used exclusively by them.

   :return: the amount of available MIPS for each Processor PE.

getCloudletExecList
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<CloudletExecution> getCloudletExecList()
   :outertype: CloudletSchedulerAbstract

getCloudletFailedList
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<CloudletExecution> getCloudletFailedList()
   :outertype: CloudletSchedulerAbstract

   Gets the list of failed cloudlets.

   :return: the cloudlet failed list.

getCloudletFinishedList
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<CloudletExecution> getCloudletFinishedList()
   :outertype: CloudletSchedulerAbstract

getCloudletList
^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Cloudlet> getCloudletList()
   :outertype: CloudletSchedulerAbstract

getCloudletPausedList
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<CloudletExecution> getCloudletPausedList()
   :outertype: CloudletSchedulerAbstract

   Gets the list of paused cloudlets.

   :return: the cloudlet paused list

getCloudletReturnedList
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Cloudlet> getCloudletReturnedList()
   :outertype: CloudletSchedulerAbstract

getCloudletStatus
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getCloudletStatus(int cloudletId)
   :outertype: CloudletSchedulerAbstract

getCloudletToMigrate
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet getCloudletToMigrate()
   :outertype: CloudletSchedulerAbstract

   Returns the first cloudlet in the execution list to migrate to another VM, removing it from the list.

   :return: the first executing cloudlet or \ :java:ref:`Cloudlet.NULL`\  if the executing list is empty

getCloudletWaitingList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<CloudletExecution> getCloudletWaitingList()
   :outertype: CloudletSchedulerAbstract

getCurrentMipsShare
^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Double> getCurrentMipsShare()
   :outertype: CloudletSchedulerAbstract

   Gets a \ **read-only**\  list of current mips capacity from the VM that will be made available to the scheduler. This mips share will be allocated to Cloudlets as requested.

   :return: the current mips share list, where each item represents the MIPS capacity of a \ :java:ref:`Pe`\ . that is available to the scheduler.

getCurrentRequestedBwPercentUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCurrentRequestedBwPercentUtilization()
   :outertype: CloudletSchedulerAbstract

getCurrentRequestedRamPercentUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getCurrentRequestedRamPercentUtilization()
   :outertype: CloudletSchedulerAbstract

getEstimatedFinishTimeOfCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getEstimatedFinishTimeOfCloudlet(CloudletExecution cle, double currentTime)
   :outertype: CloudletSchedulerAbstract

   Gets the estimated time when a given cloudlet is supposed to finish executing. It considers the amount of Vm PES and the sum of PEs required by all VMs running inside the VM.

   :param cle: cloudlet to get the estimated finish time
   :param currentTime: current simulation time
   :return: the estimated finish time of the given cloudlet (which is a relative delay from the current simulation time)

getEstimatedFinishTimeOfSoonerFinishingCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getEstimatedFinishTimeOfSoonerFinishingCloudlet(double currentTime)
   :outertype: CloudletSchedulerAbstract

   Gets the estimated time, considering the current time, that a next Cloudlet is expected to finish.

   :param currentTime: current simulation time
   :return: the estimated finish time of sooner finishing cloudlet (which is a relative delay from the current simulation time)

getFreePes
^^^^^^^^^^

.. java:method:: @Override public long getFreePes()
   :outertype: CloudletSchedulerAbstract

   Gets the number of PEs currently not being used.

getPreviousTime
^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPreviousTime()
   :outertype: CloudletSchedulerAbstract

getRequestedCpuPercentUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getRequestedCpuPercentUtilization(double time)
   :outertype: CloudletSchedulerAbstract

getRequestedMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getRequestedMipsForCloudlet(CloudletExecution cle, double time)
   :outertype: CloudletSchedulerAbstract

getTaskScheduler
^^^^^^^^^^^^^^^^

.. java:method:: @Override public CloudletTaskScheduler getTaskScheduler()
   :outertype: CloudletSchedulerAbstract

getUsedPes
^^^^^^^^^^

.. java:method:: @Override public long getUsedPes()
   :outertype: CloudletSchedulerAbstract

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: CloudletSchedulerAbstract

hasFinishedCloudlets
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean hasFinishedCloudlets()
   :outertype: CloudletSchedulerAbstract

isCloudletReturned
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isCloudletReturned(Cloudlet cloudlet)
   :outertype: CloudletSchedulerAbstract

isEmpty
^^^^^^^

.. java:method:: @Override public boolean isEmpty()
   :outertype: CloudletSchedulerAbstract

isThereEnoughFreePesForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isThereEnoughFreePesForCloudlet(CloudletExecution cle)
   :outertype: CloudletSchedulerAbstract

   Checks if the amount of PEs required by a given Cloudlet is free to use.

   :param cle: the Cloudlet to get the number of required PEs
   :return: true if there is the amount of free PEs, false otherwise

isThereTaskScheduler
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isThereTaskScheduler()
   :outertype: CloudletSchedulerAbstract

moveNextCloudletsFromWaitingToExecList
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void moveNextCloudletsFromWaitingToExecList()
   :outertype: CloudletSchedulerAbstract

   Selects the next Cloudlets in the waiting list to move to the execution list in order to start executing them. While there is enough free PEs, the method try to find a suitable Cloudlet in the list, until it reaches the end of such a list.

   The method might also exchange some cloudlets in the execution list with some in the waiting list. Thus, some running cloudlets may be preempted to give opportunity to previously waiting cloudlets to run. This is a process called \ `context switch <https://en.wikipedia.org/wiki/Context_switch>`_\ . However, each CloudletScheduler implementation decides how such a process is implemented. For instance, Space-Shared schedulers may perform context switch just after the currently running Cloudlets completely finish executing.

   This method is called internally by the \ :java:ref:`CloudletScheduler.updateProcessing(double,List)`\ .

removeCloudletFromExecList
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected CloudletExecution removeCloudletFromExecList(CloudletExecution cle)
   :outertype: CloudletSchedulerAbstract

   Removes a Cloudlet from the list of cloudlets in execution.

   :param cle: the Cloudlet to be removed
   :return: the removed Cloudlet or \ :java:ref:`CloudletExecution.NULL`\  if not found

runningCloudletsNumber
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int runningCloudletsNumber()
   :outertype: CloudletSchedulerAbstract

setCurrentMipsShare
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void setCurrentMipsShare(List<Double> currentMipsShare)
   :outertype: CloudletSchedulerAbstract

   Sets the list of current mips share available for the VM using the scheduler.

   :param currentMipsShare: the new current mips share

   **See also:** :java:ref:`.getCurrentMipsShare()`

setPreviousTime
^^^^^^^^^^^^^^^

.. java:method:: protected final void setPreviousTime(double previousTime)
   :outertype: CloudletSchedulerAbstract

   Sets the previous time when the scheduler updated the processing of cloudlets it is managing.

   :param previousTime: the new previous time

setTaskScheduler
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setTaskScheduler(CloudletTaskScheduler taskScheduler)
   :outertype: CloudletSchedulerAbstract

setVm
^^^^^

.. java:method:: @Override public void setVm(Vm vm)
   :outertype: CloudletSchedulerAbstract

sortCloudletWaitingList
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void sortCloudletWaitingList(Comparator<CloudletExecution> comparator)
   :outertype: CloudletSchedulerAbstract

   Sorts the \ :java:ref:`cloudletWaitingList`\  using a given \ :java:ref:`Comparator`\ .

   :param comparator: the \ :java:ref:`Comparator`\  to sort the Waiting Cloudlets List

timeSpan
^^^^^^^^

.. java:method:: protected double timeSpan(CloudletExecution cle, double currentTime)
   :outertype: CloudletSchedulerAbstract

   Computes the time span between the current simulation time and the last time the processing of a cloudlet was updated.

   :param cle: the cloudlet to compute the execution time span
   :param currentTime: the current simulation time

updateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected long updateCloudletProcessing(CloudletExecution cle, double currentTime)
   :outertype: CloudletSchedulerAbstract

   Updates the processing of a specific cloudlet of the Vm using this scheduler.

   :param cle: The cloudlet to be its processing updated
   :param currentTime: current simulation time
   :return: the executed length, in \ **Million Instructions (MI)**\ , since the last time cloudlet was processed.

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime, List<Double> mipsShare)
   :outertype: CloudletSchedulerAbstract

