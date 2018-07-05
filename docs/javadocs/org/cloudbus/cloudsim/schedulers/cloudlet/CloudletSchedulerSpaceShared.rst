.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecution

.. java:import:: org.cloudbus.cloudsim.resources Pe

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

Methods
-------
canExecuteCloudletInternal
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean canExecuteCloudletInternal(CloudletExecution cloudlet)
   :outertype: CloudletSchedulerSpaceShared

   The space-shared scheduler \ **does not**\  share the CPU time between executing cloudlets. Each CPU (\ :java:ref:`Pe`\ ) is used by another Cloudlet just when the previous Cloudlet using it has finished executing completely. By this way, if there are more Cloudlets than PEs, some Cloudlet will not be allowed to start executing immediately.

   :param cloudlet: {@inheritDoc}
   :return: {@inheritDoc}

cloudletResume
^^^^^^^^^^^^^^

.. java:method:: @Override public double cloudletResume(Cloudlet cloudlet)
   :outertype: CloudletSchedulerSpaceShared

