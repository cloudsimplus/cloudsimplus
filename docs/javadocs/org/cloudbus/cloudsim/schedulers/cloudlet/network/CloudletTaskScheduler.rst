.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets.network CloudletExecutionTask

.. java:import:: org.cloudbus.cloudsim.cloudlets.network CloudletTask

.. java:import:: org.cloudbus.cloudsim.cloudlets.network NetworkCloudlet

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.hosts.network NetworkHost

.. java:import:: org.cloudbus.cloudsim.network VmPacket

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.vms.network NetworkVm

.. java:import:: java.util List

CloudletTaskScheduler
=====================

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet.network
   :noindex:

.. java:type:: public interface CloudletTaskScheduler

   Provides the features to enable a \ :java:ref:`CloudletScheduler`\  to process internal \ :java:ref:`CloudletTask`\ s such as:

   ..

   * processing of \ :java:ref:`CloudletExecutionTask`\ s;
   * sending \ :java:ref:`VmPacket`\ s from the \ :java:ref:`Vm`\  of the scheduler to other ones;
   * or receiving \ :java:ref:`VmPacket`\ s sent from other VMs to that \ :java:ref:`Vm`\ .

   The packet dispatching is performed by processing \ :java:ref:`CloudletTask`\ s inside a \ :java:ref:`NetworkCloudlet`\ .

   A researcher creating its own simulations using CloudSim Plus usually doesn't have to care about this class, since even creating network-enabled simulations using objects such as \ :java:ref:`NetworkDatacenter`\ , \ :java:ref:`NetworkHost`\ , \ :java:ref:`NetworkVm`\  and \ :java:ref:`NetworkCloudlet`\ , the \ :java:ref:`NetworkHost`\  will automatically create instances of the current interface and attach each one to the \ :java:ref:`CloudletScheduler`\  that every Vm is using, doesn't matter what kind of scheduler it is.

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  CloudletTaskScheduler NULL
   :outertype: CloudletTaskScheduler

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`CloudletTaskScheduler`\  objects.

Methods
-------
addPacketToListOfPacketsSentFromVm
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean addPacketToListOfPacketsSentFromVm(VmPacket pkt)
   :outertype: CloudletTaskScheduler

   Adds a packet to the list of packets sent by a given VM, targeting the VM of this scheduler. The source VM is got from the packet.

   :param pkt: packet to be added to the list
   :return: true if the packet was added, false otherwise

clearVmPacketsToSend
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void clearVmPacketsToSend()
   :outertype: CloudletTaskScheduler

   Clears the list of \ :java:ref:`VmPacket`\ 's to send from the Vm of this scheduler to other VMs.

getVm
^^^^^

.. java:method::  Vm getVm()
   :outertype: CloudletTaskScheduler

   Gets the Vm that the CloudletTaskScheduler will sent packets from or receive packets to.

getVmPacketsToSend
^^^^^^^^^^^^^^^^^^

.. java:method::  List<VmPacket> getVmPacketsToSend()
   :outertype: CloudletTaskScheduler

   Gets a \ **read-only**\  list of \ :java:ref:`VmPacket`\ 's to send from the Vm of this scheduler to other VMs.

   :return: a \ **read-only**\  \ :java:ref:`VmPacket`\  list

isTimeToUpdateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isTimeToUpdateCloudletProcessing(Cloudlet cloudlet)
   :outertype: CloudletTaskScheduler

   Checks if it's time to update the execution of a given Cloudlet. If the Cloudlet is waiting for packets to be sent or received, then it isn't time to update its processing.

   :param cloudlet: the Cloudlet to check if it is time to update its execution
   :return: true if its timie to update Cloudlet execution, false otherwise.

processCloudletTasks
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void processCloudletTasks(Cloudlet cloudlet, long partialFinishedMI)
   :outertype: CloudletTaskScheduler

   Process Cloudlet's tasks, such as tasks to send packets from or received by a Cloudlet inside a VM.

   :param cloudlet: the Cloudlet to process packets
   :param partialFinishedMI: the partial executed length of this Cloudlet (in MI)

setVm
^^^^^

.. java:method::  void setVm(Vm vm)
   :outertype: CloudletTaskScheduler

   Sets the Vm that the CloudletTaskScheduler will sent packets from or receive packets to. It is not required to manually set a Vm for the CloudletTaskScheduler, since the \ :java:ref:`NetworkHost`\  does it when it creates a Vm.

   :param vm: the Vm to set

