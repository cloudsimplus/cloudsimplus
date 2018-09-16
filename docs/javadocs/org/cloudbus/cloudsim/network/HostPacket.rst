.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts.network NetworkHost

HostPacket
==========

.. java:package:: org.cloudbus.cloudsim.network
   :noindex:

.. java:type:: public class HostPacket implements NetworkPacket<NetworkHost>

   Represents a packet which travels from one \ :java:ref:`Host`\  to another. Each packet contains: IDs of the sender VM into the source Host and receiver VM into the destination Host which are communicating; the time at which it is sent and received; type and virtual IDs of tasks.

   Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
HostPacket
^^^^^^^^^^

.. java:constructor:: public HostPacket(NetworkHost senderHost, VmPacket vmPacket)
   :outertype: HostPacket

   Creates a new packet to be sent through the network between two hosts.

   :param senderHost: The id of the host sending the packet
   :param vmPacket: The vm packet containing information of sender and receiver Cloudlets and their VMs.

Methods
-------
getDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public NetworkHost getDestination()
   :outertype: HostPacket

   Gets the ID of the \ :java:ref:`Host`\  that the packet is going to.

getReceiveTime
^^^^^^^^^^^^^^

.. java:method:: @Override public double getReceiveTime()
   :outertype: HostPacket

getSendTime
^^^^^^^^^^^

.. java:method:: @Override public double getSendTime()
   :outertype: HostPacket

getSize
^^^^^^^

.. java:method:: @Override public long getSize()
   :outertype: HostPacket

getSource
^^^^^^^^^

.. java:method:: @Override public NetworkHost getSource()
   :outertype: HostPacket

   Gets the ID of the \ :java:ref:`Host`\  that this packet is coming from (the sender).

getVmPacket
^^^^^^^^^^^

.. java:method:: public VmPacket getVmPacket()
   :outertype: HostPacket

setDestination
^^^^^^^^^^^^^^

.. java:method:: @Override public void setDestination(NetworkHost receiverHost)
   :outertype: HostPacket

   Sets the ID of the \ :java:ref:`Host`\  that the packet is going to.

   :param receiverHost: the receiver Host id to set

setReceiveTime
^^^^^^^^^^^^^^

.. java:method:: @Override public void setReceiveTime(double receiveTime)
   :outertype: HostPacket

setSendTime
^^^^^^^^^^^

.. java:method:: @Override public void setSendTime(double sendTime)
   :outertype: HostPacket

setSource
^^^^^^^^^

.. java:method:: @Override public void setSource(NetworkHost senderHost)
   :outertype: HostPacket

   Sets the ID of the \ :java:ref:`Host`\  that this packet is coming from (the sender).

   :param senderHost: the source Host id to set

