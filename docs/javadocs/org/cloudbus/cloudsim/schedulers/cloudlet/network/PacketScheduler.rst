.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets.network CloudletTask

.. java:import:: org.cloudbus.cloudsim.cloudlets.network NetworkCloudlet

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.hosts.network NetworkHost

.. java:import:: org.cloudbus.cloudsim.network VmPacket

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.vms.network NetworkVm

.. java:import:: java.util List

PacketScheduler
===============

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet.network
   :noindex:

.. java:type:: public interface PacketScheduler

   Provides the functionalities to enable a \ :java:ref:`CloudletScheduler`\  to send \ :java:ref:`VmPacket`\ s from the \ :java:ref:`Vm`\  of the scheduler to other ones or to receive \ :java:ref:`VmPacket`\ s sent from other VMs to that \ :java:ref:`Vm`\ . The packet dispatching is performed by processing \ :java:ref:`CloudletTask`\ s inside a \ :java:ref:`NetworkCloudlet`\ .

   A researcher creating its own simulations using CloudSim Plus usually doesn't have to care about this class, since even creating network-enabled simulations using objects such as \ :java:ref:`NetworkDatacenter`\ , \ :java:ref:`NetworkHost`\ , \ :java:ref:`NetworkVm`\  and \ :java:ref:`NetworkCloudlet`\ , the \ :java:ref:`NetworkHost`\  will automatically create and instance of the current interface and attach them to the \ :java:ref:`CloudletScheduler`\  that every Vm is using, doesn't matter what kind of scheduler it is.

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  PacketScheduler NULL
   :outertype: PacketScheduler

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`PacketScheduler`\  objects.

Methods
-------
addPacketToListOfPacketsSentFromVm
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean addPacketToListOfPacketsSentFromVm(VmPacket pkt)
   :outertype: PacketScheduler

   Adds a packet to the list of packets sent by a given VM, targeting the VM of this scheduler. The source VM is got from the packet.

   :param pkt: packet to be added to the list
   :return: true if the packet was added, false otherwise

clearVmPacketsToSend
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void clearVmPacketsToSend()
   :outertype: PacketScheduler

   Clears the list of \ :java:ref:`VmPacket`\ 's to send from the Vm of this scheduler to other VMs.

getVm
^^^^^

.. java:method::  Vm getVm()
   :outertype: PacketScheduler

   Gets the Vm that the PacketScheduler will sent packets from or receive packets to.

getVmPacketsToSend
^^^^^^^^^^^^^^^^^^

.. java:method::  List<VmPacket> getVmPacketsToSend()
   :outertype: PacketScheduler

   Gets a \ **read-only**\  list of \ :java:ref:`VmPacket`\ 's to send from the Vm of this scheduler to other VMs.

   :return: a \ **read-only**\  \ :java:ref:`VmPacket`\  list

isTimeToUpdateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isTimeToUpdateCloudletProcessing(Cloudlet cloudlet)
   :outertype: PacketScheduler

   Checks if is time to update the execution of a given Cloudlet. If the Cloudlet is waiting for packets to be sent or received, then it isn't time to update its processing.

   :param cloudlet: the Cloudlet to check if it is time to update its execution
   :return: true if its timie to update Cloudlet execution, false otherwise.

processCloudletPackets
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void processCloudletPackets(Cloudlet cloudlet, double currentTime)
   :outertype: PacketScheduler

   Process the packets to be sent from or received by a Cloudlet inside the vm.

   :param cloudlet: the Cloudlet to process packets
   :param currentTime: current simulation time

setVm
^^^^^

.. java:method::  void setVm(Vm vm)
   :outertype: PacketScheduler

   Sets the Vm that the PacketScheduler will sent packets from or receive packets to. It is not required to manually set a Vm for the PacketScheduler, since the \ :java:ref:`NetworkHost`\  does it when it creates a Vm.

   :param vm: the Vm to set

