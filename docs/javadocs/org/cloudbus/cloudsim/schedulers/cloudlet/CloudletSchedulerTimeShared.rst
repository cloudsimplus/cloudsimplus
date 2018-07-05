.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecution

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: java.util List

CloudletSchedulerTimeShared
===========================

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet
   :noindex:

.. java:type:: public class CloudletSchedulerTimeShared extends CloudletSchedulerAbstract

   CloudletSchedulerTimeShared implements a policy of scheduling performed by a virtual machine to run its \ :java:ref:`Cloudlets <Cloudlet>`\ . Cloudlets execute in time-shared manner in VM. Each VM has to have its own instance of a CloudletScheduler. This scheduler does not consider Cloudlets priorities
   to define execution order. If actual priorities are defined for Cloudlets,
   they are just ignored by the scheduler.

   It also does not perform a preemption process in order to move running Cloudlets to the waiting list in order to make room for other already waiting Cloudlets to run. It just imposes there is not waiting Cloudlet, \ **oversimplifying**\  the problem considering that for a given simulation second \ ``t``\ , the total processing capacity of the processor cores (in MIPS) is equally divided by the applications that are using them.

   In processors enabled with \ `Hyper-threading technology (HT) <https://en.wikipedia.org/wiki/Hyper-threading>`_\ , it is possible to run up to 2 processes at the same physical CPU core. However, usually just the Host operating system scheduler (a \ :java:ref:`VmScheduler`\  assigned to a Host) has direct knowledge of HT to accordingly schedule up to 2 processes to the same physical CPU core. Further, this scheduler implementation oversimplifies a possible HT for the virtual PEs, allowing that more than 2 processes to run at the same core.

   Since this CloudletScheduler implementation does not account for the \ `context switch <https://en.wikipedia.org/wiki/Context_switch>`_\  overhead, this oversimplification impacts tasks completion by penalizing equally all the Cloudlets that are running on the same CPU core. Other impact is that, if there are Cloudlets of the same length running in the same PEs, they will finish exactly at the same time. On the other hand, on a real time-shared scheduler these Cloudlets will finish almost in the same time.

   As an example, consider a scheduler that has 1 PE that is able to execute 1000 MI/S (MIPS) and is running Cloudlet 0 and Cloudlet 1, each of having 5000 MI of length. These 2 Cloudlets will spend 5 seconds to finish. Now consider that the time slice allocated to each Cloudlet to execute is 1 second. As at every 1 second a different Cloudlet is allowed to run, the execution path will be as follows: Time (second): 00 01 02 03 04 05 Cloudlet (id): C0 C1 C0 C1 C0 C1 As one can see, in a real time-shared scheduler that does not define priorities for applications, the 2 Cloudlets will in fact finish in different times. In this example, one Cloudlet will finish 1 second after the other.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

   **See also:** :java:ref:`CloudletSchedulerCompletelyFair`

Methods
-------
canExecuteCloudletInternal
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean canExecuteCloudletInternal(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerTimeShared

   This time-shared scheduler shares the CPU time between all executing cloudlets, giving the same CPU timeslice for each Cloudlet to execute. It always allow any submitted Cloudlets to be immediately added to the execution list. By this way, it doesn't matter what Cloudlet is being submitted, since it will always include it in the execution list.

   :param cloudlet: the Cloudlet that will be added to the execution list.
   :return: always \ **true**\  to indicate that any submitted Cloudlet can be immediately added to the execution list

cloudletResume
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletResume(Cloudlet cloudlet)
   :outertype: CloudletSchedulerTimeShared

getCloudletWaitingList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<CloudletExecution> getCloudletWaitingList()
   :outertype: CloudletSchedulerTimeShared

   {@inheritDoc}

   For this scheduler, this list is always empty, once the VM PEs
   are shared across all Cloudlets running inside a VM. Each Cloudlet has
   the opportunity to use the PEs for a given timeslice.

   :return: {@inheritDoc}

