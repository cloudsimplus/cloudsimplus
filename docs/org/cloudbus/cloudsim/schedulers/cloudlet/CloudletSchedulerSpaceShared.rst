.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecutionInfo

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Processor

CloudletSchedulerSpaceShared
============================

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet
   :noindex:

.. java:type:: public class CloudletSchedulerSpaceShared extends CloudletSchedulerAbstract

   CloudletSchedulerSpaceShared implements a policy of scheduling performed by a virtual machine to run its \ :java:ref:`Cloudlets <Cloudlet>`\ . It considers there will be only one Cloudlet per VM. Other Cloudlets will be in a waiting list. It also considers that the time to transfer Cloudlets to the Vm happens before Cloudlet starts executing. I.e., even though Cloudlets must wait for CPU, data transfer happens as soon as Cloudlets are submitted.

   This scheduler does not consider Cloudlets priorities to define execution
   order. If actual priorities are defined for Cloudlets, they are just ignored
   by the scheduler.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
CloudletSchedulerSpaceShared
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletSchedulerSpaceShared()
   :outertype: CloudletSchedulerSpaceShared

   Creates a new CloudletSchedulerSpaceShared object. This method must be invoked before starting the actual simulation.

Methods
-------
canAddCloudletToExecutionList
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean canAddCloudletToExecutionList(CloudletExecutionInfo cloudlet)
   :outertype: CloudletSchedulerSpaceShared

   The space-shared scheduler \ **does not**\  share the CPU time between executing cloudlets. Each CPU (\ :java:ref:`Pe`\ ) is used by another Cloudlet just when the previous Cloudlet using it has finished executing completely. By this way, if there are more Cloudlets than PEs, some Cloudlet will not be allowed to start executing immediately.

   :param cloudlet: {@inheritDoc}
   :return: {@inheritDoc}

cloudletResume
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletResume(int cloudletId)
   :outertype: CloudletSchedulerSpaceShared

getCurrentRequestedMips
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getCurrentRequestedMips()
   :outertype: CloudletSchedulerSpaceShared

getTotalCurrentAvailableMipsForCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalCurrentAvailableMipsForCloudlet(CloudletExecutionInfo rcl, List<Double> mipsShare)
   :outertype: CloudletSchedulerSpaceShared

   {@inheritDoc}

   It doesn't consider the given Cloudlet because the scheduler ensures that the Cloudlet will use all required PEs until it finishes executing.

   :param rcl: {@inheritDoc}
   :param mipsShare: {@inheritDoc}
   :return: {@inheritDoc}

