.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecution

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.util MathUtil

.. java:import:: java.util List

.. java:import:: java.util Optional

.. java:import:: java.util.function Predicate

CloudletSchedulerCompletelyFair
===============================

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet
   :noindex:

.. java:type:: public final class CloudletSchedulerCompletelyFair extends CloudletSchedulerTimeShared

   A simplified implementation of the \ `Completely Fair Scheduler (CFS) <https://en.wikipedia.org/wiki/Completely_Fair_Scheduler>`_\  that is the default scheduler used for most tasks on recent Linux Kernel. It is a time-shared scheduler that shares CPU cores between running applications by preempting them after a time period (timeslice) to allow other ones to start executing during their timeslices.

   This scheduler supposes that Cloudlets priorities are in the range from [-20 to 19],
   as used in Linux Kernel. Despite setting Cloudlets priorities with values outside this interval will work as well, one has to realize that lower priorities are defined by negative values.

   It is a basic implementation that covers the following features:

   ..

   * Defines a general runqueue (the waiting list which defines which Cloudlets to run next) for all CPU cores (\ :java:ref:`Pe`\ ) instead of one for each core. More details in the listing below.
   * Computes process (\ :java:ref:`Cloudlet`\ ) niceness based on its priority: \ ``niceness = -priority``\ . The nice value (niceness) defines how nice a process is to the other ones. Lower niceness (negative values) represents higher priority and consequently higher weight, while higher niceness (positive values) represent lower priority and lower weight.
   * Computes process timeslice based on its weight, that in turn is computed based on its niceness. The timeslice is the amount of time that a process is allowed to use the CPU before be preempted to make room for other process to run. The CFS scheduler uses a dynamic defined timeslice.

   And it currently \ **DOES NOT**\  implement the following features:

   ..

   * Additional overhead for CPU context switch: the context switch is the process of removing an application that is using a CPU core to allow another one to start executing. This is the task preemption process that allows a core to be shared between several applications.
   * Since this scheduler does not consider \ `context switch <https://en.wikipedia.org/wiki/Context_switch>`_\  overhead, there is only one runqueue (waiting list) for all CPU cores because each application is not in fact assigned to a specific CPU core. The scheduler just computes how much computing power (in MIPS) and number of cores each application can use and that MIPS capacity is multiplied by the number of cores the application requires. Such an approach then enables the application to execute that number of instructions per second. Once the \ :java:ref:`PEs <Pe>`\  do not in fact run the application, (application execution is simulated just computing the amount of instructions that can be run), it doesn't matter which PEs are "running" the application.
   * It doesn't use a Red-Black tree (such as the TreeSet), as in real implementations of CFS, to sort waiting Cloudlets (runqueue list) increasingly, based on their virtual runtime (vruntime or VRT) (placing the Cloudlets that have run the least at the top of the tree). Furthermore, the use of such a data structure added some complexity to the implementation. Since different Cloudlets may have the same virtual runtime, this introduced some issues when adding or removing elements in a structure such as the TreeSet, that requires each value (the virtual runtime in this case) used to sort the Set to be unique.

   \ **NOTES:**\

   ..

   * The time interval for updating cloudlets execution in this scheduler is not primarily defined by the \ :java:ref:`Datacenter.getSchedulingInterval()`\ , but by the \ :java:ref:`timeslice <computeCloudletTimeSlice(CloudletExecution)>`\  computed based on the defined \ :java:ref:`getLatency()`\ . Each time the computed timeslice is greater than the Datacenter scheduling interval, then the next update of Cloudlets processing will follow the \ :java:ref:`Datacenter.getSchedulingInterval()`\ .
   * The implementation was based on the book of Robert Love: Linux Kernel Development, 3rd ed. Addison-Wesley, 2010 and some other references listed below.

   :author: Manoel Campos da Silva Filho

   **See also:** \ `Inside the Linux 2.6 Completely Fair Scheduler <http://www.ibm.com/developerworks/library/l-completely-fair-scheduler/>`_\, \ `Learn Linux, 101: Process execution priorities <http://www.ibm.com/developerworks/library/l-lpic1-103-6/index.html>`_\, \ `Towards achieving fairness in the Linux scheduler <https://doi.org/10.1145/1400097.1400102>`_\, \ `The Linux scheduler <https://doi.org/10.1145/10.1145/2901318.2901326>`_\, \ `kernel.org: CFS Scheduler Design <https://www.kernel.org/doc/Documentation/scheduler/sched-design-CFS.txt>`_\, \ `Linux Scheduler FAQ <https://oakbytes.wordpress.com/linux-scheduler/>`_\

Methods
-------
canExecuteCloudletInternal
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean canExecuteCloudletInternal(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerCompletelyFair

   Checks if a Cloudlet can be submitted to the execution list. This scheduler, different from its time-shared parent, only adds submitted Cloudlets to the execution list if there is enough free PEs. Otherwise, such Cloudlets are added to the waiting list, really enabling time-sharing between running Cloudlets. By this way, some Cloudlets have to be preempted to allow other ones to be executed.

   :param cloudlet: {@inheritDoc}
   :return: {@inheritDoc}

cloudletSubmitInternal
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected double cloudletSubmitInternal(CloudletExecution cle, double fileTransferTime)
   :outertype: CloudletSchedulerCompletelyFair

   {@inheritDoc}

   It also sets the initial virtual runtime for the given Cloudlet in order to define how long the Cloudlet has executed yet. See \ :java:ref:`computeCloudletInitialVirtualRuntime(CloudletExecution)`\  for more details.

   :param cle: {@inheritDoc}
   :param fileTransferTime: {@inheritDoc}
   :return: {@inheritDoc}

computeCloudletTimeSlice
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double computeCloudletTimeSlice(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerCompletelyFair

   Computes the timeslice for a Cloudlet, which is the amount of time (in seconds) that it will have to use the PEs, considering all Cloudlets in the \ :java:ref:`executing list <getCloudletExecList()>`\ .

   The timeslice is computed considering the \ :java:ref:`Cloudlet weight <getCloudletWeight(CloudletExecution)>`\  and what it represents in percentage of the \ :java:ref:`weight sum <getWeightSumOfRunningCloudlets()>`\  of all cloudlets in the execution list.

   :param cloudlet: Cloudlet to get the timeslice
   :return: Cloudlet timeslice (in seconds)

   **See also:** :java:ref:`.getCloudletWeight(CloudletExecution)`, :java:ref:`.getWeightSumOfRunningCloudlets()`

findSuitableWaitingCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected Optional<CloudletExecution> findSuitableWaitingCloudlet()
   :outertype: CloudletSchedulerCompletelyFair

   {@inheritDoc} The cloudlet waiting list (runqueue) is sorted according to the virtual runtime (vruntime or VRT), which indicates the amount of time the Cloudlet has run. This runtime increases as the Cloudlet executes.

   :return: {@inheritDoc}

getCloudletExecList
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<CloudletExecution> getCloudletExecList()
   :outertype: CloudletSchedulerCompletelyFair

   {@inheritDoc}

   Prior to start executing, a Cloudlet is added to this list. When the Cloudlet vruntime reaches its timeslice (the amount of time it can use the CPU), it is removed from this list and added back to the \ :java:ref:`getCloudletWaitingList()`\ .

   The sum of the PEs of Cloudlets into this list cannot exceeds the number of PEs available for the scheduler. If the sum of PEs of such Cloudlets is less than the number of existing PEs, there are idle PEs. Since the CPU context switch overhead is not regarded in this implementation and as result, it doesn't matter which PEs are running which Cloudlets, there is not such information in anywhere. As an example, if the first Cloudlet requires 2 PEs, then one can say that it is using the first 2 PEs. But if at the next simulation time the same Cloudlet can be at the 3º position in this Collection, indicating that now it is using the 3º and 4º Pe, which doesn't change anything. In real schedulers, usually a process is pinned to a specific set of cores until it finishes executing, to avoid the overhead of changing processes from a run queue to another unnecessarily.

getCloudletNiceness
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getCloudletNiceness(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerCompletelyFair

   Gets the nice value from a Cloudlet based on its priority. The nice value is the opposite of the priority.

   As "niceness" is a terminology defined by specific schedulers (such as Linux Schedulers), it is not defined inside the Cloudlet.

   :param cloudlet: Cloudlet to get the nice value
   :return: the cloudlet niceness

   **See also:** \ `Man Pages: Nice values for Linux processes <http://man7.org/linux/man-pages/man1/nice.1.html>`_\

getCloudletWaitingList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<CloudletExecution> getCloudletWaitingList()
   :outertype: CloudletSchedulerCompletelyFair

   Gets a \ **read-only**\  list of Cloudlets which are waiting to run, the so called \ `run queue <https://en.wikipedia.org/wiki/Run_queue>`_\ .

   \ **NOTE:**\  Different from real implementations, this scheduler uses just one run queue for all processor cores (PEs). Since CPU context switch is not concerned, there is no point in using different run queues.

getCloudletWeight
^^^^^^^^^^^^^^^^^

.. java:method:: protected double getCloudletWeight(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerCompletelyFair

   Gets the weight of the Cloudlet to use the CPU, that is defined based on its niceness. As greater is the weight, more time the Cloudlet will have to use the PEs.

   As the \ :java:ref:`timelice <computeCloudletTimeSlice(CloudletExecution)>`\  assigned to a Cloudlet to use the CPU is defined exponentially instead of linearly according to its niceness, this method is used as the base to correctly compute the timeslice.

   \ **NOTICE**\ : The formula used is based on the book referenced at the class documentation.

   :param cloudlet: Cloudlet to get the weight to use PEs
   :return: the cloudlet weight to use PEs

   **See also:** :java:ref:`.getCloudletNiceness(CloudletExecution)`

getLatency
^^^^^^^^^^

.. java:method:: public int getLatency()
   :outertype: CloudletSchedulerCompletelyFair

   Gets the latency, which is the amount of time (in seconds) the scheduler will allow the execution of running Cloudlets in the available PEs, before checking which are the next Cloudlets to execute. The latency time is divided by the number of the number of Cloudlets that can be executed at the current time. If there are 4 Cloudlets by just 2 PEs, the latency is divided by 2, because only 2 Cloudlets can be concurrently executed at the moment. However, the minimum amount of time allocated to each Cloudlet is defined by the \ :java:ref:`getMinimumGranularity()`\ .

   As lower is the latency, more responsive a real operating system will be perceived by users, at the cost or more frequent CPU context Datacenter (that reduces CPU throughput). \ **However, CPU context switch overhead is not being considered.**\

   NOTE: The default value for linux scheduler is 0.02s.

getMinimumGranularity
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getMinimumGranularity()
   :outertype: CloudletSchedulerCompletelyFair

   Gets the minimum granularity that is the minimum amount of time (in seconds) that is assigned to each Cloudlet to execute.

   This minimum value is used to reduce the frequency of CPU context Datacenter, that degrade CPU throughput. \ **However, CPU context switch overhead is not being considered.**\  By this way, it just ensures that each Cloudlet will not use the CPU for less than the minimum granularity.

   The default value for linux scheduler is 0.001s

   **See also:** :java:ref:`.getLatency()`

moveNextCloudletsFromWaitingToExecList
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void moveNextCloudletsFromWaitingToExecList()
   :outertype: CloudletSchedulerCompletelyFair

   Checks which Cloudlets in the execution list have the virtual runtime equals to their allocated time slice and preempt them, getting the most priority Cloudlets in the waiting list (i.e., those ones in the beginning of the list).

   **See also:** :java:ref:`.preemptExecCloudletsWithExpiredVRuntimeAndMoveToWaitingList()`

setLatency
^^^^^^^^^^

.. java:method:: public void setLatency(int latency)
   :outertype: CloudletSchedulerCompletelyFair

   Sets the latency time (in seconds).

   :param latency: the latency to set
   :throws IllegalArgumentException: when latency is lower than minimum granularity

   **See also:** :java:ref:`.getLatency()`

setMinimumGranularity
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setMinimumGranularity(int minimumGranularity)
   :outertype: CloudletSchedulerCompletelyFair

   Sets the minimum granularity that is the minimum amount of time (in seconds) that is assigned to each Cloudlet to execute.

   :param minimumGranularity: the minimum granularity to set
   :throws IllegalArgumentException: when minimum granularity is greater than latency

updateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long updateCloudletProcessing(CloudletExecution cle, double currentTime)
   :outertype: CloudletSchedulerCompletelyFair

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime, List<Double> mipsShare)
   :outertype: CloudletSchedulerCompletelyFair

   {@inheritDoc}

   :param currentTime: {@inheritDoc}
   :param mipsShare: {@inheritDoc}
   :return: the shorter timeslice assigned to the running cloudlets (which defines the time of the next expiring Cloudlet, enabling the preemption process), or Double.MAX_VALUE if there is no next events

