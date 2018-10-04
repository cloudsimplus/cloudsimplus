.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: java.util Objects

CloudletExecution
=================

.. java:package:: org.cloudbus.cloudsim.cloudlets
   :noindex:

.. java:type:: public class CloudletExecution

   Stores execution information about a \ :java:ref:`Cloudlet`\  submitted to a specific \ :java:ref:`Datacenter`\  for processing. This class keeps track of the time for all activities in the Datacenter for a specific Cloudlet. Before a Cloudlet exits the Datacenter, it is RECOMMENDED to call this method \ :java:ref:`finalizeCloudlet()`\ .

   It acts as a placeholder for maintaining the amount of resource share allocated at various times for simulating any scheduling using internal events.

   As the VM where the Cloudlet is running might migrate to another Datacenter, each CloudletExecutionInfo object represents the data about execution of the cloudlet when the Vm was in a given Datacenter.

   :author: Manzur Murshed, Rajkumar Buyya

Fields
------
NULL
^^^^

.. java:field:: public static final CloudletExecution NULL
   :outertype: CloudletExecution

   A property that implements the Null Object Design Pattern for \ :java:ref:`CloudletExecution`\  objects.

Constructors
------------
CloudletExecution
^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletExecution(Cloudlet cloudlet)
   :outertype: CloudletExecution

   Instantiates a CloudletExecutionInfo object upon the arrival of a Cloudlet inside a Datacenter. The arriving time is determined by \ :java:ref:`CloudSim.clock()`\ .

   :param cloudlet: the Cloudlet to store execution information from

Methods
-------
addVirtualRuntime
^^^^^^^^^^^^^^^^^

.. java:method:: public double addVirtualRuntime(double timeToAdd)
   :outertype: CloudletExecution

   Adds a given time to the \ :java:ref:`virtual runtime <getVirtualRuntime()>`\ .

   :param timeToAdd: time to add to the virtual runtime (in seconds)
   :return: the new virtual runtime (in seconds)

equals
^^^^^^

.. java:method:: @Override public boolean equals(Object obj)
   :outertype: CloudletExecution

finalizeCloudlet
^^^^^^^^^^^^^^^^

.. java:method:: public void finalizeCloudlet()
   :outertype: CloudletExecution

   Finalizes all relevant information before \ ``exiting``\  the Datacenter entity. This method sets the final data of:

   ..

   * wall clock time, i.e. the time of this Cloudlet resides in a Datacenter (from arrival time until departure time).
   * actual CPU time, i.e. the total execution time of this Cloudlet in a Datacenter.
   * Cloudlet's finished time so far

getCloudlet
^^^^^^^^^^^

.. java:method:: public Cloudlet getCloudlet()
   :outertype: CloudletExecution

   Gets the Cloudlet for which the execution information is related to.

   :return: cloudlet for this execution information object

getCloudletArrivalTime
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getCloudletArrivalTime()
   :outertype: CloudletExecution

   Gets the time the cloudlet arrived for execution inside the Datacenter.

   :return: arrival time

getCloudletId
^^^^^^^^^^^^^

.. java:method:: public long getCloudletId()
   :outertype: CloudletExecution

   Gets the ID of the Cloudlet this execution info is related to.

getCloudletLength
^^^^^^^^^^^^^^^^^

.. java:method:: public long getCloudletLength()
   :outertype: CloudletExecution

   Gets the \ :java:ref:`Cloudlet's length <Cloudlet.getLength()>`\ .

   :return: Cloudlet's length

getFileTransferTime
^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getFileTransferTime()
   :outertype: CloudletExecution

   Gets the time to transfer the list of files required by the Cloudlet from the Datacenter storage (such as a Storage Area Network) to the Vm of the Cloudlet.

getFinishTime
^^^^^^^^^^^^^

.. java:method:: public double getFinishTime()
   :outertype: CloudletExecution

   Gets the time when the Cloudlet has finished completely (not just in a given Datacenter, but finished at all). If the cloudlet wasn't finished completely yet, the value is equals to \ :java:ref:`Cloudlet.NOT_ASSIGNED`\ .

   :return: finish time of a cloudlet or \ ``-1.0``\  if it cannot finish in this hourly slot

getLastProcessingTime
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getLastProcessingTime()
   :outertype: CloudletExecution

   Gets the last time the Cloudlet was processed at the Datacenter where this execution information is related to.

   :return: the last time the Cloudlet was processed or zero when it has never been processed yet

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: public long getNumberOfPes()
   :outertype: CloudletExecution

getRemainingCloudletLength
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public long getRemainingCloudletLength()
   :outertype: CloudletExecution

   Gets the remaining cloudlet length (in MI) that has to be execute yet, considering the \ :java:ref:`Cloudlet.getLength()`\ .

   :return: remaining cloudlet length in MI

getTimeSlice
^^^^^^^^^^^^

.. java:method:: public double getTimeSlice()
   :outertype: CloudletExecution

   Gets the timeslice assigned by a \ :java:ref:`CloudletScheduler`\  for a Cloudlet, which is the amount of time (in seconds) that such a Cloudlet will have to use the PEs of a Vm. Each CloudletScheduler implementation can make use of this attribute or not. CloudletSchedulers that use it, are in charge to compute the timeslice to assign to each Cloudlet.

   :return: Cloudlet timeslice (in seconds)

getVirtualRuntime
^^^^^^^^^^^^^^^^^

.. java:method:: public double getVirtualRuntime()
   :outertype: CloudletExecution

   Gets the virtual runtime (vruntime) that indicates how long the Cloudlet has been executing by a \ :java:ref:`CloudletScheduler`\  (in seconds). The default value of this attribute is zero. Each scheduler implementation might set a value to such attribute to use it for context switch, preempting running Cloudlets to enable other ones to start executing. This way, the attribute is just used internally by specific CloudletSchedulers.

hashCode
^^^^^^^^

.. java:method:: @Override public int hashCode()
   :outertype: CloudletExecution

setFileTransferTime
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setFileTransferTime(double fileTransferTime)
   :outertype: CloudletExecution

   Sets the time to transfer the list of files required by the Cloudlet from the Datacenter storage (such as a Storage Area Network) to the Vm of the Cloudlet.

   :param fileTransferTime: the file transfer time to set

setFinishTime
^^^^^^^^^^^^^

.. java:method:: public void setFinishTime(double time)
   :outertype: CloudletExecution

   Sets the finish time for this Cloudlet. If time is negative, then it will be ignored.

   :param time: finish time

setLastProcessingTime
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setLastProcessingTime(double lastProcessingTime)
   :outertype: CloudletExecution

   Sets the last time this Cloudlet was processed at a Datacenter.

   :param lastProcessingTime: the last processing time to set

setStatus
^^^^^^^^^

.. java:method:: public boolean setStatus(Cloudlet.Status newStatus)
   :outertype: CloudletExecution

   Sets the Cloudlet status.

   :param newStatus: the Cloudlet status
   :return: \ ``true``\  if the new status has been set, \ ``false``\  otherwise

setTimeSlice
^^^^^^^^^^^^

.. java:method:: public void setTimeSlice(double timeSlice)
   :outertype: CloudletExecution

   Sets the timeslice assigned by a \ :java:ref:`CloudletScheduler`\  for a Cloudlet, which is the amount of time (in seconds) that such a Cloudlet will have to use the PEs of a Vm. Each CloudletScheduler implementation can make use of this attribute or not. CloudletSchedulers that use it, are in charge to compute the timeslice to assign to each Cloudlet.

   :param timeSlice: the Cloudlet timeslice to set (in seconds)

setVirtualRuntime
^^^^^^^^^^^^^^^^^

.. java:method:: public void setVirtualRuntime(double virtualRuntime)
   :outertype: CloudletExecution

   Sets the virtual runtime (vruntime) that indicates how long the Cloudlet has been executing by a \ :java:ref:`CloudletScheduler`\  (in seconds). This attribute is used just internally by specific CloudletSchedulers.

   :param virtualRuntime: the value to set (in seconds)

   **See also:** :java:ref:`.getVirtualRuntime()`

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: CloudletExecution

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: public void updateProcessing(long partialFinishedInstructions)
   :outertype: CloudletExecution

   Updates the length of cloudlet that has already been completed.

   :param partialFinishedInstructions: the partial amount of instructions just executed, to be added to the \ :java:ref:`instructionsFinishedSoFar`\ , in \ **Number of Instructions (instead of Million Instructions)**\

