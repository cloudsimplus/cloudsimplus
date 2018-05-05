.. java:import:: java.util.stream Collectors

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.network VmPacket

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: org.cloudbus.cloudsim.vms Vm

PacketSchedulerSimple
=====================

.. java:package:: org.cloudbus.cloudsim.schedulers.cloudlet.network
   :noindex:

.. java:type:: public class PacketSchedulerSimple implements PacketScheduler

   Implements a policy of scheduling performed by a virtual machine to process network packets to be sent or received by its \ :java:ref:`NetworkCloudlet`\ 's. It also schedules the network communication among the cloudlets, managing the time a cloudlet stays blocked waiting the response of a network package sent to another cloudlet.

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
PacketSchedulerSimple
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PacketSchedulerSimple()
   :outertype: PacketSchedulerSimple

   Creates a PacketSchedulerSimple object.

Methods
-------
addPacketToListOfPacketsSentFromVm
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean addPacketToListOfPacketsSentFromVm(VmPacket pkt)
   :outertype: PacketSchedulerSimple

clearVmPacketsToSend
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void clearVmPacketsToSend()
   :outertype: PacketSchedulerSimple

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: PacketSchedulerSimple

getVmPacketsToSend
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<VmPacket> getVmPacketsToSend()
   :outertype: PacketSchedulerSimple

isTimeToUpdateCloudletProcessing
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isTimeToUpdateCloudletProcessing(Cloudlet cloudlet)
   :outertype: PacketSchedulerSimple

processCloudletPackets
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void processCloudletPackets(Cloudlet cloudlet, double currentTime)
   :outertype: PacketSchedulerSimple

setVm
^^^^^

.. java:method:: @Override public void setVm(Vm vm)
   :outertype: PacketSchedulerSimple

