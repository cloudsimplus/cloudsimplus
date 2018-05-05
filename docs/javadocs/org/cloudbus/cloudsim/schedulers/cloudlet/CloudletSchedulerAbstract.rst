.. java:import:: java.util.function Consumer

.. java:import:: java.util.function Function

.. java:import:: java.util.stream IntStream

.. java:import:: java.util.stream Stream

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet.Status

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecution

.. java:import:: org.cloudbus.cloudsim.resources Ram

.. java:import:: org.cloudbus.cloudsim.resources ResourceManageable

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet.network PacketScheduler

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.vms Vm

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

.. java:method:: protected void addCloudletToExecList(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerAbstract

   Adds a Cloudlet to the list of cloudlets in execution.

   :param cloudlet: the Cloudlet to be added

addCloudletToFinishedList
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addCloudletToFinishedList(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerAbstract

addCloudletToReturnedList
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addCloudletToReturnedList(Cloudlet cloudlet)
   :outertype: CloudletSchedulerAbstract

addCloudletToWaitingList
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addCloudletToWaitingList(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerAbstract

addWaitingCloudletToExecList
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected CloudletExecution addWaitingCloudletToExecList(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerAbstract

   Removes a Cloudlet from waiting list and adds it to the exec list.

   :param cloudlet: the cloudlet to add to to exec list
   :return: the given cloudlet

cloudletCancel
^^^^^^^^^^^^^^

.. java:method:: @Override public Cloudlet cloudletCancel(int cloudletId)
   :outertype: CloudletSchedulerAbstract

cloudletFinish
^^^^^^^^^^^^^^

.. java:method:: @Override public void cloudletFinish(CloudletExecution ce)
   :outertype: CloudletSchedulerAbstract

cloudletPause
^^^^^^^^^^^^^

.. java:method:: @Override public boolean cloudletPause(int cloudletId)
   :outertype: CloudletSchedulerAbstract

cloudletSubmit
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletSubmit(Cloudlet cloudlet)
   :outertype: CloudletSchedulerAbstract

cloudletSubmit
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletSubmit(Cloudlet cl, double fileTransferTime)
   :outertype: CloudletSchedulerAbstract

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

.. java:method:: protected Optional<CloudletExecution> findCloudletInList(double cloudletId, List<CloudletExecution> list)
   :outertype: CloudletSchedulerAbstract

   Search for a Cloudlet into a given list.

   :param cloudletId: the id of the Cloudlet to search for
   :param list: the list to search the Cloudlet into
   :return: an \ :java:ref:`Optional`\  value that is able to indicate if the Cloudlet was found or not

findSuitableWaitingCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Optional<CloudletExecution> findSuitableWaitingCloudlet()
   :outertype: CloudletSchedulerAbstract

   Try to find the first Cloudlet in the waiting list which the number of required PEs is not higher than the number of free PEs.

   :return: an \ :java:ref:`Optional`\  containing the found Cloudlet or an empty Optional otherwise

getAllocatedMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAllocatedMipsForCloudlet(CloudletExecution ce, double time)
   :outertype: CloudletSchedulerAbstract

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

.. java:method:: @Override public List<Double> getCurrentMipsShare()
   :outertype: CloudletSchedulerAbstract

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

.. java:method:: protected double getEstimatedFinishTimeOfCloudlet(CloudletExecution ce, double currentTime)
   :outertype: CloudletSchedulerAbstract

   Gets the estimated time when a given cloudlet is supposed to finish executing. It considers the amount of Vm PES and the sum of PEs required by all VMs running inside the VM.

   :param ce: cloudlet to get the estimated finish time
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

getPacketScheduler
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public PacketScheduler getPacketScheduler()
   :outertype: CloudletSchedulerAbstract

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

.. java:method:: @Override public double getRequestedMipsForCloudlet(CloudletExecution ce, double time)
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

.. java:method:: protected boolean isThereEnoughFreePesForCloudlet(CloudletExecution c)
   :outertype: CloudletSchedulerAbstract

   Checks if the amount of PEs required by a given Cloudlet is free to use.

   :param c: the Cloudlet to get the number of required PEs
   :return: true if there is the amount of free PEs, false otherwise

isTherePacketScheduler
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isTherePacketScheduler()
   :outertype: CloudletSchedulerAbstract

moveNextCloudletsFromWaitingToExecList
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void moveNextCloudletsFromWaitingToExecList()
   :outertype: CloudletSchedulerAbstract

   Selects the next Cloudlets in the waiting list to move to the execution list in order to start executing them. While there is enough free PEs, the method try to find a suitable Cloudlet in the list, until it reaches the end of such a list.

   The method might also exchange some cloudlets in the execution list with some in the waiting list. Thus, some running cloudlets may be preempted to give opportunity to previously waiting cloudlets to run. This is a process called \ `context switch <https://en.wikipedia.org/wiki/Context_switch>`_\ . However, each CloudletScheduler implementation decides how such a process is implemented. For instance, Space-Shared schedulers may just perform context switch just after currently running Cloudlets completely finish executing.

   This method is called internally by the \ :java:ref:`CloudletScheduler.updateProcessing(double,List)`\  one.

processCloudletSubmit
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double processCloudletSubmit(CloudletExecution ce, double fileTransferTime)
   :outertype: CloudletSchedulerAbstract

   Process a Cloudlet after it is received by the \ :java:ref:`cloudletSubmit(Cloudlet,double)`\  method, that creates a \ :java:ref:`CloudletExecution`\  object to encapsulate the submitted Cloudlet and record execution data.

   :param ce: the CloudletExecutionInfo that encapsulates the Cloudlet object
   :param fileTransferTime: time required to move the required files from the SAN to the VM
   :return: expected finish time of this cloudlet (considering the time to transfer required files from the Datacenter to the Vm), or 0 if it is in a waiting queue

removeCloudletFromExecList
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected CloudletExecution removeCloudletFromExecList(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerAbstract

   Removes a Cloudlet from the list of cloudlets in execution.

   :param cloudlet: the Cloudlet to be removed
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

setPacketScheduler
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setPacketScheduler(PacketScheduler packetScheduler)
   :outertype: CloudletSchedulerAbstract

setPreviousTime
^^^^^^^^^^^^^^^

.. java:method:: protected final void setPreviousTime(double previousTime)
   :outertype: CloudletSchedulerAbstract

   Sets the previous time when the scheduler updated the processing of cloudlets it is managing.

   :param previousTime: the new previous time

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

.. java:method:: protected double timeSpan(CloudletExecution cl, double currentTime)
   :outertype: CloudletSchedulerAbstract

   Computes the time span between the current simulation time and the last time the processing of a cloudlet was updated.

   :param cl: the cloudlet to compute the execution time span
   :param currentTime: the current simulation time

updateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void updateCloudletProcessing(CloudletExecution ce, double currentTime)
   :outertype: CloudletSchedulerAbstract

   Updates the processing of a specific cloudlet of the Vm using this scheduler.

   :param ce: The cloudlet to be its processing updated
   :param currentTime: current simulation time

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime, List<Double> mipsShare)
   :outertype: CloudletSchedulerAbstract

