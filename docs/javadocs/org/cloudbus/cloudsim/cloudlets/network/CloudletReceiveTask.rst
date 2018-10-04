.. java:import:: org.cloudbus.cloudsim.network VmPacket

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

CloudletReceiveTask
===================

.. java:package:: org.cloudbus.cloudsim.cloudlets.network
   :noindex:

.. java:type:: public class CloudletReceiveTask extends CloudletTask

   A task executed by a \ :java:ref:`NetworkCloudlet`\  that receives data from a \ :java:ref:`CloudletSendTask`\ . Each receiver task expects to receive packets from just one VM.

   Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
CloudletReceiveTask
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletReceiveTask(int id, Vm sourceVm)
   :outertype: CloudletReceiveTask

   Creates a new task.

   :param id: task id
   :param sourceVm: the Vm where it is expected to receive packets from

Methods
-------
getExpectedPacketsToReceive
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public long getExpectedPacketsToReceive()
   :outertype: CloudletReceiveTask

   Gets the number of packets that are expected to be received. After this number of packets is received, the task is marked as finished.

getPacketsReceived
^^^^^^^^^^^^^^^^^^

.. java:method:: public List<VmPacket> getPacketsReceived()
   :outertype: CloudletReceiveTask

   Gets the list of packets received.

   :return: a read-only received packet list

getSourceVm
^^^^^^^^^^^

.. java:method:: public Vm getSourceVm()
   :outertype: CloudletReceiveTask

   Gets the Vm where it is expected to receive packets from.

receivePacket
^^^^^^^^^^^^^

.. java:method:: public void receivePacket(VmPacket packet)
   :outertype: CloudletReceiveTask

   Receives a packet sent from a \ :java:ref:`CloudletSendTask`\  and add it the the received packet list.

   :param packet: the packet received

setExpectedPacketsToReceive
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setExpectedPacketsToReceive(long expectedPacketsToReceive)
   :outertype: CloudletReceiveTask

   Sets the number of packets that are expected to be received. After this number of packets is received, the task is marked as finished.

   :param expectedPacketsToReceive: the number of expected packets to set

