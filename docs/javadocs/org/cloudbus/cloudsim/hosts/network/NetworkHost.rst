.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.hosts HostSimple

.. java:import:: org.cloudbus.cloudsim.network HostPacket

.. java:import:: org.cloudbus.cloudsim.network VmPacket

.. java:import:: org.cloudbus.cloudsim.network.switches EdgeSwitch

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet.network CloudletTaskScheduler

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet.network CloudletTaskSchedulerSimple

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmSchedulerSpaceShared

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util ArrayList

.. java:import:: java.util List

NetworkHost
===========

.. java:package:: org.cloudbus.cloudsim.hosts.network
   :noindex:

.. java:type:: public class NetworkHost extends HostSimple

   NetworkHost class extends \ :java:ref:`HostSimple`\  to support simulation of networked datacenters. It executes actions related to management of packets (sent and received) other than that of virtual machines (e.g., creation and destruction). A host has a defined policy for provisioning memory and bw, as well as an allocation policy for PE's to virtual machines.

   Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg

Constructors
------------
NetworkHost
^^^^^^^^^^^

.. java:constructor:: public NetworkHost(long ram, long bw, long storage, List<Pe> peList)
   :outertype: NetworkHost

   Creates and powers on a NetworkHost using a \ :java:ref:`VmSchedulerSpaceShared`\  as default.

   :param ram: the RAM capacity in Megabytes
   :param bw: the Bandwidth (BW) capacity in Megabits/s
   :param storage: the storage capacity in Megabytes
   :param peList: the host's \ :java:ref:`Pe`\  list

Methods
-------
addReceivedNetworkPacket
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addReceivedNetworkPacket(HostPacket hostPacket)
   :outertype: NetworkHost

   Adds a packet to the list of received packets in order to further submit them to the respective target VMs and Cloudlets.

   :param hostPacket: received network packet

createVm
^^^^^^^^

.. java:method:: @Override public boolean createVm(Vm vm)
   :outertype: NetworkHost

   {@inheritDoc}

   It also creates and sets a  for each
   Vm that doesn't have one already.

   :param vm: {@inheritDoc}
   :return: {@inheritDoc}

getEdgeSwitch
^^^^^^^^^^^^^

.. java:method:: public EdgeSwitch getEdgeSwitch()
   :outertype: NetworkHost

   Gets the Switch the Host is directly connected to.

getTotalDataTransferBytes
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getTotalDataTransferBytes()
   :outertype: NetworkHost

setEdgeSwitch
^^^^^^^^^^^^^

.. java:method:: public void setEdgeSwitch(EdgeSwitch edgeSwitch)
   :outertype: NetworkHost

   Sets the Switch the Host is directly connected to. This method is to be called only by the \ :java:ref:`EdgeSwitch.connectHost(NetworkHost)`\  method.

   :param edgeSwitch: the Switch to set

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime)
   :outertype: NetworkHost

