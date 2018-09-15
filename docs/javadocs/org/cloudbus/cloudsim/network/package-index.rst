org.cloudbus.cloudsim.network
=============================

Provides classes to define network assets, such as different kinds of \ :java:ref:`org.cloudbus.cloudsim.network.switches.AbstractSwitch`\  and also the \ :java:ref:`org.cloudbus.cloudsim.network.topologies.NetworkTopology`\  that can be specified in some standard file format and read using a implementation of \ :java:ref:`org.cloudbus.cloudsim.network.topologies.readers.TopologyReader`\ .

It also provides class to enable simulation of network package transmission. For more information about the network moduule, please refer to following publication:

..

* \ ` Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

:author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

.. java:package:: org.cloudbus.cloudsim.network

.. toctree::
   :maxdepth: 1

   DelayMatrix
   FloydWarshall
   HostPacket
   IcmpPacket
   NetworkPacket
   VmPacket

