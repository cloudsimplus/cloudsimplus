.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts.network NetworkHost

.. java:import:: org.cloudbus.cloudsim.network HostPacket

.. java:import:: org.cloudbus.cloudsim.vms Vm

EdgeSwitch
==========

.. java:package:: org.cloudbus.cloudsim.network.switches
   :noindex:

.. java:type:: public class EdgeSwitch extends AbstractSwitch

   This class represents an Edge AbstractSwitch in a Datacenter network. It interacts with other Datacenter in order to exchange packets. Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Fields
------
DOWNLINK_BW
^^^^^^^^^^^

.. java:field:: public static final long DOWNLINK_BW
   :outertype: EdgeSwitch

   Default downlink bandwidth of EdgeSwitch in Megabits/s. It also represents the uplink bandwidth of connected hosts.

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

SWITCHING_DELAY
^^^^^^^^^^^^^^^

.. java:field:: public static final double SWITCHING_DELAY
   :outertype: EdgeSwitch

   Default switching delay in milliseconds.

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

