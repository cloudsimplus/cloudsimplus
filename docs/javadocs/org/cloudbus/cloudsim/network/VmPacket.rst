.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

VmPacket
========

.. java:package:: org.cloudbus.cloudsim.network
   :noindex:

.. java:type:: public class VmPacket implements NetworkPacket<Vm>

   Represents a packet that travels from a \ :java:ref:`Vm`\  to another, through the virtual network within a \ :java:ref:`Host`\ . It contains information about Cloudlets which are communicating.

   Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
VmPacket
^^^^^^^^

.. java:constructor:: public VmPacket(Vm sourceVm, Vm destinationVm, long size, Cloudlet senderCloudlet, Cloudlet receiverCloudlet)
   :outertype: VmPacket

   Creates a packet to be sent to to a VM inside the Host of the sender VM.

   :param sourceVm: id of the VM sending the packet
   :param destinationVm: id of the VM that has to receive the packet
   :param size: data length of the packet in bytes
   :param senderCloudlet: cloudlet sending the packet
   :param receiverCloudlet: cloudlet that has to receive the packet

Methods
-------
getDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public Vm getDestination()
   :outertype: VmPacket

   Gets the id of the VM that has to receive the packet. This is the VM where th \ :java:ref:`receiver cloudlet <getReceiverCloudlet()>`\  is running.

getReceiveTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double getReceiveTime()
   :outertype: VmPacket

getReceiverCloudlet
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Cloudlet getReceiverCloudlet()
   :outertype: VmPacket

   Gets the cloudlet that has to receive the packet.

getSendTime
^^^^^^^^^^^

.. java:method:: @Override public double getSendTime()
   :outertype: VmPacket

getSenderCloudlet
^^^^^^^^^^^^^^^^^

.. java:method:: public Cloudlet getSenderCloudlet()
   :outertype: VmPacket

   Gets the cloudlet sending the packet.

getSize
^^^^^^^

.. java:method:: @Override public long getSize()
   :outertype: VmPacket

getSource
^^^^^^^^^

.. java:method:: @Override public Vm getSource()
   :outertype: VmPacket

   Gets the VM sending the packet. This is the VM where the \ :java:ref:`sending cloudlet <getSenderCloudlet()>`\  is running.

setDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public void setDestination(Vm destinationVmId)
   :outertype: VmPacket

   Sets the id of the VM that has to receive the packet. This is the VM where th \ :java:ref:`receiver cloudlet <getReceiverCloudlet()>`\  is running.

   :param destinationVmId: the destination VM id to set

setReceiveTime
^^^^^^^^^^^^^^

.. java:method:: @Override public void setReceiveTime(double receiveTime)
   :outertype: VmPacket

setSendTime
^^^^^^^^^^^

.. java:method:: @Override public void setSendTime(double sendTime)
   :outertype: VmPacket

setSource
^^^^^^^^^

.. java:method:: @Override public void setSource(Vm sourceVmId)
   :outertype: VmPacket

   Sets the id of the VM sending the packet. This is the VM where the \ :java:ref:`sending cloudlet <getSenderCloudlet()>`\  is running.

   :param sourceVmId: the source VM id to set

