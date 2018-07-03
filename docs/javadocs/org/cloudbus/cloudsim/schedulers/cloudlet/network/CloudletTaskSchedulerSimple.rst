.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.network VmPacket

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util.stream Collectors

CloudletTaskSchedulerSimple
===========================

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet.network
   :noindex:

.. java:type:: public class CloudletTaskSchedulerSimple implements CloudletTaskScheduler

   Implements a policy of scheduling performed by a virtual machine to process \ :java:ref:`CloudletTask`\ s of a \ :java:ref:`NetworkCloudlet`\ .

   It also schedules the network communication among the cloudlets, managing the time a cloudlet stays blocked waiting the response of a network package sent to another cloudlet.

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
CloudletTaskSchedulerSimple
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletTaskSchedulerSimple()
   :outertype: CloudletTaskSchedulerSimple

   Creates a CloudletTaskSchedulerSimple object.

Methods
-------
addPacketToListOfPacketsSentFromVm
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addPacketToListOfPacketsSentFromVm(VmPacket pkt)
   :outertype: CloudletTaskSchedulerSimple

clearVmPacketsToSend
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void clearVmPacketsToSend()
   :outertype: CloudletTaskSchedulerSimple

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: CloudletTaskSchedulerSimple

getVmPacketsToSend
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<VmPacket> getVmPacketsToSend()
   :outertype: CloudletTaskSchedulerSimple

isTimeToUpdateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isTimeToUpdateCloudletProcessing(Cloudlet cloudlet)
   :outertype: CloudletTaskSchedulerSimple

processCloudletTasks
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void processCloudletTasks(Cloudlet cloudlet, long partialFinishedMI)
   :outertype: CloudletTaskSchedulerSimple

setVm
^^^^^

.. java:method:: @Override public void setVm(Vm vm)
   :outertype: CloudletTaskSchedulerSimple

