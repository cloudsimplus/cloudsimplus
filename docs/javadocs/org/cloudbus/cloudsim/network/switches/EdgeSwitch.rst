.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts.network NetworkHost

.. java:import:: org.cloudbus.cloudsim.network HostPacket

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Objects

EdgeSwitch
==========

.. java:package:: org.cloudbus.cloudsim.network.switches
   :noindex:

.. java:type:: public class EdgeSwitch extends AbstractSwitch

   Represents an Edge Switch in a Datacenter network, which can be connected to \ :java:ref:`NetworkHost`\ s. It interacts with other Datacenter in order to exchange packets. Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Fields
------
LEVEL
^^^^^

.. java:field:: public static final int LEVEL
   :outertype: EdgeSwitch

   The level (layer) of the switch in the network topology.

PORTS
^^^^^

.. java:field:: public static final int PORTS
   :outertype: EdgeSwitch

   Default number of ports that defines the number of \ :java:ref:`Host`\  that can be connected to the switch.

Constructors
------------
EdgeSwitch
^^^^^^^^^^

.. java:constructor:: public EdgeSwitch(CloudSim simulation, NetworkDatacenter dc)
   :outertype: EdgeSwitch

   Instantiates a EdgeSwitch specifying Datacenter that are connected to its downlink and uplink ports, and corresponding bandwidths. In this switch, downlink ports aren't connected to other switch but to hosts.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param dc: The Datacenter where the switch is connected to

Methods
-------
connectHost
^^^^^^^^^^^

.. java:method:: public void connectHost(NetworkHost host)
   :outertype: EdgeSwitch

   Connects a \ :java:ref:`NetworkHost`\  to the switch, by adding it to the \ :java:ref:`getHostList()`\ .

   :param host: the host to be connected to the switch

disconnectHost
^^^^^^^^^^^^^^

.. java:method:: public boolean disconnectHost(NetworkHost host)
   :outertype: EdgeSwitch

   Disconnects a \ :java:ref:`NetworkHost`\  from the switch, by removing it from the \ :java:ref:`getHostList()`\ .

   :param host: the host to be disconnected from the switch
   :return: true if the Host was connected to the switch, false otherwise

getHostList
^^^^^^^^^^^

.. java:method:: public List<NetworkHost> getHostList()
   :outertype: EdgeSwitch

   Gets a \ **read-only**\  list of Hosts connected to the switch.

getLevel
^^^^^^^^

.. java:method:: @Override public int getLevel()
   :outertype: EdgeSwitch

processPacketDown
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void processPacketDown(SimEvent evt)
   :outertype: EdgeSwitch

processPacketUp
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void processPacketUp(SimEvent evt)
   :outertype: EdgeSwitch

