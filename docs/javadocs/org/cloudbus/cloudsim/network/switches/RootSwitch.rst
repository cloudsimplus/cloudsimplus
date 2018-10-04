.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters.network NetworkDatacenter

.. java:import:: org.cloudbus.cloudsim.network HostPacket

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

RootSwitch
==========

.. java:package:: org.cloudbus.cloudsim.network.switches
   :noindex:

.. java:type:: public class RootSwitch extends AbstractSwitch

   This class allows to simulate Root switch which connects Datacenters to external network. It interacts with other Datacenter in order to exchange packets.

   Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Fields
------
DOWNLINK_BW
^^^^^^^^^^^

.. java:field:: public static final long DOWNLINK_BW
   :outertype: RootSwitch

   The downlink bandwidth of RootSwitch in Megabits/s. It also represents the uplink bandwidth of connected aggregation Datacenter.

LEVEL
^^^^^

.. java:field:: public static final int LEVEL
   :outertype: RootSwitch

   The level (layer) of the switch in the network topology.

PORTS
^^^^^

.. java:field:: public static final int PORTS
   :outertype: RootSwitch

   Default number of root switch ports that defines the number of \ :java:ref:`AggregateSwitch`\  that can be connected to it.

SWITCHING_DELAY
^^^^^^^^^^^^^^^

.. java:field:: public static final double SWITCHING_DELAY
   :outertype: RootSwitch

   Default switching delay in milliseconds.

Constructors
------------
RootSwitch
^^^^^^^^^^

.. java:constructor:: public RootSwitch(CloudSim simulation, NetworkDatacenter dc)
   :outertype: RootSwitch

   Instantiates a Root AbstractSwitch specifying what other Datacenter are connected to its downlink ports, and corresponding bandwidths.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to
   :param dc: The Datacenter where the switch is connected to

Methods
-------
getLevel
^^^^^^^^

.. java:method:: @Override public int getLevel()
   :outertype: RootSwitch

processPacketUp
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void processPacketUp(SimEvent evt)
   :outertype: RootSwitch

