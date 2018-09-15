.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.network VmPacket

CloudletSendTask
================

.. java:package:: org.cloudbus.cloudsim.cloudlets.network
   :noindex:

.. java:type:: public class CloudletSendTask extends CloudletTask

   Represents a task executed by a \ :java:ref:`NetworkCloudlet`\  that sends data to a \ :java:ref:`CloudletReceiveTask`\ .

   Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
CloudletSendTask
^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletSendTask(int id)
   :outertype: CloudletSendTask

   Creates a new task.

   :param id: task id

Methods
-------
addPacket
^^^^^^^^^

.. java:method:: public VmPacket addPacket(Cloudlet destinationCloudlet, long dataLength)
   :outertype: CloudletSendTask

   Creates and add a packet to the list of packets to be sent to a \ :java:ref:`Cloudlet`\  that is inside a specific VM.

   :param destinationCloudlet: destination cloudlet to send packets to
   :param dataLength: the number of data bytes of the packet to create
   :throws IllegalArgumentException: when the source or destination Cloudlet doesn't have an assigned VM
   :throws RuntimeException: when a NetworkCloudlet was not assigned to the Task
   :return: the created packet

getPacketsToSend
^^^^^^^^^^^^^^^^

.. java:method:: public List<VmPacket> getPacketsToSend()
   :outertype: CloudletSendTask

   :return: a read-only list of packets to send

getPacketsToSend
^^^^^^^^^^^^^^^^

.. java:method:: public List<VmPacket> getPacketsToSend(double sendTime)
   :outertype: CloudletSendTask

   Gets the list of packets to send, updating the send time to the given time and clearing the list of packets, marking the task as finished.

   :param sendTime: the send time to update all packets in the list
   :return: the packet list with the send time updated to the given time

