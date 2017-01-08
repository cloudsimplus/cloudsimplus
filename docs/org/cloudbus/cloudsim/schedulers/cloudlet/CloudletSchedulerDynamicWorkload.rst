.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecutionInfo

.. java:import:: org.cloudbus.cloudsim.resources Processor

CloudletSchedulerDynamicWorkload
================================

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet
   :noindex:

.. java:type:: @Deprecated public class CloudletSchedulerDynamicWorkload extends CloudletSchedulerTimeShared

   CloudletSchedulerDynamicWorkload implements a policy of scheduling performed by a virtual machine to run its \ :java:ref:`Cloudlets <Cloudlet>`\ , assuming there is just one cloudlet which is working as an online service.

   It extends a TimeShared policy, but in fact, considering that there is just one cloudlet for the VM using this scheduler. By this way, such a cloudlet will not compete for CPU with other ones. Each VM must have its own instance of a CloudletScheduler.

   :author: Anton Beloglazov

Constructors
------------
CloudletSchedulerDynamicWorkload
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletSchedulerDynamicWorkload(double mips, int numberOfPes)
   :outertype: CloudletSchedulerDynamicWorkload

   Instantiates a new VM scheduler

   :param mips: The individual MIPS capacity of each PE allocated to the VM using the scheduler, considering that all PEs have the same capacity.
   :param numberOfPes: The number of PEs allocated to the VM using the scheduler.

Methods
-------
cloudletSubmit
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletSubmit(Cloudlet cl, double fileTransferTime)
   :outertype: CloudletSchedulerDynamicWorkload

getCacheCurrentRequestedMips
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Double> getCacheCurrentRequestedMips()
   :outertype: CloudletSchedulerDynamicWorkload

   Gets the cache of current requested mips.

   :return: the cache current requested mips

getCachePreviousTime
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double getCachePreviousTime()
   :outertype: CloudletSchedulerDynamicWorkload

   Gets the cache of previous time.

   :return: the cache previous time

getCurrentRequestedMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getCurrentRequestedMips()
   :outertype: CloudletSchedulerDynamicWorkload

getMips
^^^^^^^

.. java:method:: public final double getMips()
   :outertype: CloudletSchedulerDynamicWorkload

   Gets the mips.

   :return: the mips

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: public final int getNumberOfPes()
   :outertype: CloudletSchedulerDynamicWorkload

   Gets the pes number.

   :return: the pes number

getTotalCurrentAllocatedMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCurrentAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double time)
   :outertype: CloudletSchedulerDynamicWorkload

getTotalCurrentAvailableMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCurrentAvailableMipsForCloudlet(CloudletExecutionInfo rcl, List<Double> mipsShare)
   :outertype: CloudletSchedulerDynamicWorkload

getTotalCurrentMips
^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getTotalCurrentMips()
   :outertype: CloudletSchedulerDynamicWorkload

   Gets the total current mips available for the VM using the scheduler. The total is computed from the \ :java:ref:`getCurrentMipsShare()`\

   :return: the total current mips

getTotalCurrentRequestedMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCurrentRequestedMipsForCloudlet(CloudletExecutionInfo rcl, double time)
   :outertype: CloudletSchedulerDynamicWorkload

getTotalMips
^^^^^^^^^^^^

.. java:method:: public double getTotalMips()
   :outertype: CloudletSchedulerDynamicWorkload

   Gets the total mips considering all PEs.

   :return: the total mips capacity

getUnderAllocatedMips
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Map<Cloudlet, Double> getUnderAllocatedMips()
   :outertype: CloudletSchedulerDynamicWorkload

   Gets the under allocated mips.

   :return: the under allocated mips

setCacheCurrentRequestedMips
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void setCacheCurrentRequestedMips(List<Double> cacheCurrentRequestedMips)
   :outertype: CloudletSchedulerDynamicWorkload

   Sets the cache of current requested mips.

   :param cacheCurrentRequestedMips: the new cache current requested mips

setCachePreviousTime
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final void setCachePreviousTime(double cachePreviousTime)
   :outertype: CloudletSchedulerDynamicWorkload

   Sets the cache of previous time.

   :param cachePreviousTime: the new cache previous time

setMips
^^^^^^^

.. java:method:: public final void setMips(double mips)
   :outertype: CloudletSchedulerDynamicWorkload

   Sets the mips.

   :param mips: the new mips

setNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: public final void setNumberOfPes(int pesNumber)
   :outertype: CloudletSchedulerDynamicWorkload

   Sets the pes number.

   :param pesNumber: the new pes number

setUnderAllocatedMips
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final void setUnderAllocatedMips(Map<Cloudlet, Double> underAllocatedMips)
   :outertype: CloudletSchedulerDynamicWorkload

   Sets the under allocated mips.

   :param underAllocatedMips: the under allocated mips

updateUnderAllocatedMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void updateUnderAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double mips)
   :outertype: CloudletSchedulerDynamicWorkload

   Update under allocated mips for cloudlet.

   :param rcl: the rgl
   :param mips: the mips

updateVmProcessing
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateVmProcessing(double currentTime, List<Double> mipsShare)
   :outertype: CloudletSchedulerDynamicWorkload

